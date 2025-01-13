package com.sbb2.common.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.jwt.TokenType;
import com.sbb2.common.jwt.exception.JwtTokenBusinessLogicException;
import com.sbb2.common.jwt.exception.JwtTokenErrorCode;
import com.sbb2.member.domain.Member;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	private final int ACCESS_MAX_AGE;
	private final int REFRESH_MAX_AGE;
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final AntPathMatcher antPathMatcher;
	private final UserDetailsService userDetailsService;
	private HashMap<HttpMethod, List<String>> uriPattern = new HashMap<>();

	/**
	 * @param jwtUtil
	 * @param uriPattern 인증이 필요한 URI
	 */
	public JwtFilter(int accessMaxAge, int refreshMaxAge, JwtUtil jwtUtil, ObjectMapper objectMapper,
		UserDetailsService userDetailsService,
		AntPathMatcher antPathMatcher) {
		this.ACCESS_MAX_AGE = accessMaxAge;
		this.REFRESH_MAX_AGE = refreshMaxAge;
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
		this.userDetailsService = userDetailsService;
		this.antPathMatcher = antPathMatcher;
		Arrays.stream(HttpMethod.values())
			.forEach(httpMethod -> uriPattern.put(httpMethod, new ArrayList<>()));
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		//JWT 인증이 필요한 URI, 토큰이 AccessToken이면 인증 로직 실행
		if (matchers(request.getMethod(), request.getRequestURI())) {
			try {
				log.info("JWTFilter Start!");
				authenticateWithToken(request, response);
				filterChain.doFilter(request, response);
			} catch (JwtTokenBusinessLogicException | JwtException e) {
				log.error(e.toString());
				createErrorInfo(request, response, e);
			}
		} else {
			//인증이 필요하지 않다면 다음 필터로 진행
			filterChain.doFilter(request, response);
		}
	}

	/**
	 * 토큰 인증 로직
	 */
	private void authenticateWithToken(HttpServletRequest request, HttpServletResponse response) {
		String token = jwtUtil.getAccessToken(request);

		//토큰 만료시간 검증
		if (!StringUtils.hasText(token)) {
			String refreshToken = jwtUtil.refreshTokenValid(request);

			//refresh 토큰이 블랙리스트에 있는지 체크
			jwtUtil.blackListCheck(refreshToken);

			String email = jwtUtil.getEmail(refreshToken);

			UserDetails userDetails = userDetailsService.loadUserByUsername(email);

			MemberUserDetails memberUserDetails = (MemberUserDetails)userDetails;

			Member loginMember = memberUserDetails.getMember();

			MemberLoginResponse memberLoginResponse = MemberLoginResponse.builder()
				.email(loginMember.email())
				.username(loginMember.username())
				.memberRole(loginMember.memberRole())
				.build();

			String createAccessToken = jwtUtil.createAccessToken(memberLoginResponse);
			String createRefreshToken = jwtUtil.createRefreshToken(memberLoginResponse);

			ResponseCookie responseAccessCookie = createTokenCookie(createAccessToken, ACCESS_MAX_AGE, TokenType.ACCESS);
			ResponseCookie responseRefreshCookie = createTokenCookie(createRefreshToken, REFRESH_MAX_AGE, TokenType.REFRESH);

			response.addHeader(HttpHeaders.SET_COOKIE, responseAccessCookie.toString());
			response.addHeader(HttpHeaders.SET_COOKIE, responseRefreshCookie.toString());

			//인증 토큰 생성
			Authentication memberLoginToken =
				new MemberLoginToken(userDetails.getAuthorities(), userDetails, null);

			//세션에 사용자 저장
			SecurityContextHolder.getContext().setAuthentication(memberLoginToken);

		} else if (!jwtUtil.isAccessToken(token)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.INVALID_ACCESS_TOKEN);
		} else {
			jwtUtil.blackListCheck(token);

			UserDetails memberUserDetails = userDetailsService.loadUserByUsername(jwtUtil.getEmail(token));

			//인증 토큰 생성
			Authentication memberLoginToken =
				new MemberLoginToken(memberUserDetails.getAuthorities(), memberUserDetails, null);

			//세션에 사용자 저장
			SecurityContextHolder.getContext().setAuthentication(memberLoginToken);
		}
	}

	private void createErrorInfo(
		HttpServletRequest request, HttpServletResponse response, RuntimeException e)
		throws IOException, ServletException {
		String message = e.getMessage();

		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

		if (e instanceof JwtTokenBusinessLogicException) {
			JwtTokenBusinessLogicException exception = (JwtTokenBusinessLogicException)e;
			httpStatus = exception.getStatus();
		}

		//커스텀한 예외가 아닌 JWT 예외가 터졌을 때
		if (e instanceof JwtException) {
			log.error("처리되지 않은 예외 발생: ", e);
		}

		HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(
			HttpStatus.UNAUTHORIZED.value(),
			request.getRequestURI(),
			message,
			ErrorDetail.of(Collections.emptyList()));

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(httpErrorInfo));
		response.setStatus(httpErrorInfo.code());
	}

	private boolean matchers(String requestHttpMethod, String requestUri) {
		HttpMethod httpMethod = HttpMethod.valueOf(requestHttpMethod);
		List<String> uriList = uriPattern.get(httpMethod);

		return uriList.stream().anyMatch(
			(uri) -> antPathMatcher.match(uri, requestUri)
		);
	}

	/**
	 * JwtFilter 로직을 수행할 URI를 추가하는 메소드 입니다.
	 *
	 * @param httpMethod
	 * @param uri
	 */
	public JwtFilter addUriPattern(HttpMethod httpMethod, String... uri) {
		List<String> uriPattern = this.uriPattern.get(httpMethod);
		uriPattern.addAll(Arrays.asList(uri));
		List<String> strings = this.uriPattern.get(httpMethod);
		for (String string : strings) {
			log.info("jwtFilter test={}", string);
		}
		return this;
	}

	private ResponseCookie createTokenCookie(String tokenValue, int maxAge, TokenType tokenType) {
		ResponseCookie responseCookie = ResponseCookie
			.from(tokenType.toString(), tokenValue)
			.domain("localhost") //로컬에서 사용할 때 사용
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(maxAge) //1일
			.sameSite("Strict")
			.build();

		return responseCookie;
	}
}

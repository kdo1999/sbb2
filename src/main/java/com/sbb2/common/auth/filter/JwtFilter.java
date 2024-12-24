package com.sbb2.common.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.jwt.exception.JwtTokenBusinessLogicException;
import com.sbb2.common.jwt.exception.JwtTokenErrorCode;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final AntPathMatcher antPathMatcher;
	private final UserDetailsService userDetailsService;
	private HashMap<HttpMethod, List<String>> uriPattern = new HashMap<>();

	/**
	 * @param jwtUtil
	 * @param uriPattern 인증이 필요한 URI
	 */
	public JwtFilter(JwtUtil jwtUtil, ObjectMapper objectMapper, UserDetailsService userDetailsService,
		AntPathMatcher antPathMatcher) {
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
		if (!jwtUtil.isExpired(token)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.EXPIRED);
		}

		//Access토큰이 아닐 때
		if (!jwtUtil.isAccessToken(token)) {
			throw new JwtTokenBusinessLogicException(JwtTokenErrorCode.INVALID_ACCESS_TOKEN);
		}

		jwtUtil.blackListCheck(token);

		UserDetails memberUserDetails = userDetailsService.loadUserByUsername(jwtUtil.getEmail(token));

		//인증 토큰 생성
		Authentication memberLoginToken =
			new MemberLoginToken(memberUserDetails.getAuthorities(), memberUserDetails, null);

		//세션에 사용자 저장
		SecurityContextHolder.getContext().setAuthentication(memberLoginToken);
	}

	private void createErrorInfo(
		HttpServletRequest request, HttpServletResponse response, RuntimeException e)
		throws IOException, ServletException {
		String message = "JWT 처리중 오류가 발생했습니다.";

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
	 * @param httpMethod
	 * @param uri
	 */
	public void addUriPattern(HttpMethod httpMethod, String... uri) {
		List<String> uriPattern = this.uriPattern.get(httpMethod);
		uriPattern.addAll(Arrays.asList(uri));
		List<String> strings = this.uriPattern.get(httpMethod);
		for (String string : strings) {
			log.info("jwtFilter test={}", string);
		}
	}
}

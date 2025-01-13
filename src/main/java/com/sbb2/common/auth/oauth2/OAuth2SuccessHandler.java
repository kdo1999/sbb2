package com.sbb2.common.auth.oauth2;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.jwt.TokenType;
import com.sbb2.member.domain.MemberRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final int ACCESS_MAX_AGE;
	private final int REFRESH_MAX_AGE;
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	@Autowired
	public OAuth2SuccessHandler(JwtUtil jwtUtil, ObjectMapper objectMapper,
		@Value("${jwt.refresh-max-age}") int refreshMaxAge, @Value("${jwt.access-max-age}") int accessMaxAge) {
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
		this.REFRESH_MAX_AGE = refreshMaxAge;
		this.ACCESS_MAX_AGE = accessMaxAge;
	}

	public OAuth2SuccessHandler(String defaultTargetUrl, JwtUtil jwtUtil, ObjectMapper objectMapper, int refreshMaxAge,
		int accessMaxAge) {
		super(defaultTargetUrl);
		this.jwtUtil = jwtUtil;
		this.objectMapper = objectMapper;
		this.REFRESH_MAX_AGE = refreshMaxAge;
		this.ACCESS_MAX_AGE = accessMaxAge;
	}

	/**
	 * 인증 성공 후 호출되며, 리다이렉트를 처리하고 인증 속성을 제거합니다.
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		System.out.println("authentication = " + authentication);
		CustomMemberDetails principal = (CustomMemberDetails)authentication.getPrincipal();
		System.out.println("principal = " + principal);
		MemberLoginResponse memberLoginResponse = MemberLoginResponse.builder()
			.email(principal.email())
			.username(principal.username())
			.memberRole(MemberRole.USER)
			.build();

		String accessToken = jwtUtil.createAccessToken(memberLoginResponse);
		String refreshToken = jwtUtil.createRefreshToken(memberLoginResponse);
		ResponseCookie accessCookie = createTokenCookie(accessToken, ACCESS_MAX_AGE, TokenType.ACCESS);
		ResponseCookie refreshCookie = createTokenCookie(refreshToken, REFRESH_MAX_AGE, TokenType.REFRESH);

		response.addHeader("Set-Cookie", refreshCookie.toString());
		response.addHeader("Set-Cookie", accessCookie.toString());

		handle(request, response, authentication);
		super.clearAuthenticationAttributes(request);
	}

	/**
	 * 지정된 URL로 리다이렉트합니다.
	 *
	 * @throws IOException 입출력 예외 발생 시
	 */

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000/oauth/callback");
	}

	private ResponseCookie createTokenCookie(String tokenValue, int maxAge, TokenType tokenType) {

		return ResponseCookie
			.from(tokenType.toString(), tokenValue)
			.domain("localhost") //로컬에서 사용할 때 사용
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(maxAge) //1일
			.sameSite("Strict")
			.build();
	}
}

package com.sbb2.auth.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.auth.controller.request.MemberEmailSignupRequest;
import com.sbb2.auth.controller.request.MemberLoginRequest;
import com.sbb2.auth.service.AuthService;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.validation.ValidationSequence;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
	private static final int REFRESH_MAX_AGE = 24 * 60 * 60; //1일
	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<GenericResponse<MemberEmailSignupResponse>> signup(@Validated(ValidationSequence.class) @RequestBody MemberEmailSignupRequest memberEmailSignupRequest) {
		MemberEmailSignupResponse signup = authService.signup(memberEmailSignupRequest.email(),
			memberEmailSignupRequest.username(), memberEmailSignupRequest.password());

		return ResponseEntity.created(URI.create("/login")).body(GenericResponse.of(signup));
	}

	@PostMapping("/login")
	public ResponseEntity<GenericResponse<MemberLoginResponse>> login(@Validated(ValidationSequence.class) @RequestBody MemberLoginRequest memberLoginRequest) {
		MemberLoginResponse memberLoginResponse = authService.memberLogin(memberLoginRequest.email(),
			memberLoginRequest.password());

		String accessToken = jwtUtil.createAccessToken(memberLoginResponse);
		String refreshToken = jwtUtil.createRefreshToken(memberLoginResponse);

		ResponseCookie refreshCookie = createRefreshCookie(refreshToken, REFRESH_MAX_AGE);

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			.body(GenericResponse.of(memberLoginResponse));
	}

	@PostMapping("/logout")
	public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request) {
		String accessToken = jwtUtil.getAccessToken(request);
		String refreshToken = jwtUtil.getRefreshToken(request);

		jwtUtil.logout(accessToken, refreshToken);

		ResponseCookie refreshCookie = createRefreshCookie(null, 0);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			.body(GenericResponse.of());
	}

	/**
	 * RefreshToken를 파라미터로 보내면 쿠키를 생성후 반환
	 * @param refreshToken
	 * @return {@link ResponseCookie}
	 */
	private ResponseCookie createRefreshCookie(String refreshToken, int maxAge) {
		return ResponseCookie
			.from("refresh", refreshToken)
			.domain("localhost") //로컬에서 사용할 때 사용
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(maxAge) //1일
			.sameSite("Strict")
			.build();
	}
}

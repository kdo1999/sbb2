package com.sbb2.auth.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.auth.controller.request.EmailSendRequestDto;
import com.sbb2.auth.controller.request.MemberEmailSignupRequest;
import com.sbb2.auth.controller.request.MemberLoginRequest;
import com.sbb2.auth.controller.request.PasswordChangeRequest;
import com.sbb2.auth.controller.request.PasswordResetRequest;
import com.sbb2.auth.service.AuthService;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.jwt.TokenType;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.validation.ValidationSequence;
import com.sbb2.member.domain.LoginType;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final int ACCESS_MAX_AGE; //20분
	private final int REFRESH_MAX_AGE; //1일
	private final AuthService authService;
	private final JwtUtil jwtUtil;

	public AuthController(AuthService authService, JwtUtil jwtUtil,
		@Value("${jwt.refresh-max-age}") int refreshMaxAge, @Value("${jwt.access-max-age}") int ACCESS_MAX_AGE) {
		this.authService = authService;
		this.jwtUtil = jwtUtil;
		this.REFRESH_MAX_AGE = refreshMaxAge;
		this.ACCESS_MAX_AGE = ACCESS_MAX_AGE;
	}

	@PostMapping("/signup")
	public ResponseEntity<GenericResponse<MemberEmailSignupResponse>> signup(
		@Validated(ValidationSequence.class) @RequestBody MemberEmailSignupRequest memberEmailSignupRequest) {
		MemberEmailSignupResponse signup = authService.signup(memberEmailSignupRequest.email(),
			memberEmailSignupRequest.username(), memberEmailSignupRequest.password(), LoginType.EMAIL);

		return ResponseEntity.created(URI.create("/login")).body(GenericResponse.of(signup));
	}

	@PostMapping("/login")
	public ResponseEntity<GenericResponse<MemberLoginResponse>> login(
		@Validated(ValidationSequence.class) @RequestBody MemberLoginRequest memberLoginRequest) {
		MemberLoginResponse memberLoginResponse = authService.memberLogin(memberLoginRequest.email(),
			memberLoginRequest.password());

		String accessToken = jwtUtil.createAccessToken(memberLoginResponse);
		String refreshToken = jwtUtil.createRefreshToken(memberLoginResponse);

		ResponseCookie accessCookie = createTokenCookie(refreshToken, ACCESS_MAX_AGE, TokenType.ACCESS);
		ResponseCookie refreshCookie = createTokenCookie(refreshToken, REFRESH_MAX_AGE, TokenType.REFRESH);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
			.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			.body(GenericResponse.of(memberLoginResponse));
	}

	@PostMapping("/logout")
	public ResponseEntity<GenericResponse<Void>> logout(HttpServletRequest request) {
		String accessToken = jwtUtil.getAccessToken(request);
		String refreshToken = jwtUtil.getRefreshToken(request);

		jwtUtil.logout(accessToken, refreshToken);

		ResponseCookie refreshCookie = createTokenCookie(null, 0, TokenType.REFRESH);
		ResponseCookie accessCookie = createTokenCookie(null, 0, TokenType.ACCESS);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
			.body(GenericResponse.of());
	}

	@PatchMapping("/password/change")
	public ResponseEntity<GenericResponse<Void>> changePassword(@Validated(ValidationSequence.class) @RequestBody
	PasswordChangeRequest passwordChangeRequest, @AuthenticationPrincipal MemberUserDetails memberUserDetails) {

		authService.passwordChange(passwordChangeRequest.originalPassword(), passwordChangeRequest.getPassword(),
			memberUserDetails.getMember());

		return ResponseEntity.ok().body(GenericResponse.of());
	}

	@PostMapping("/code")
	public ResponseEntity<GenericResponse<Void>> sendCode(
		@Validated(ValidationSequence.class) @RequestBody EmailSendRequestDto emailSendRequestDto) {
		authService.sendCode(emailSendRequestDto.email(), emailSendRequestDto.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}

	@PostMapping("/password/reset")
	public ResponseEntity<GenericResponse<Void>> passwordReset(
		@Validated(ValidationSequence.class) @RequestBody PasswordResetRequest passwordResetRequest) {
		authService.passwordReset(passwordResetRequest.email(), passwordResetRequest.certificationCode(),
			passwordResetRequest.verifyType());

		return ResponseEntity.ok().body(GenericResponse.of());
	}

	/**
	 * RefreshToken를 파라미터로 보내면 쿠키를 생성후 반환
	 *
	 * @param tokenValue
	 * @return {@link ResponseCookie}
	 */
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

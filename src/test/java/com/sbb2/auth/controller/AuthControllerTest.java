package com.sbb2.auth.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.sbb2.auth.controller.request.MemberEmailSignupRequest;
import com.sbb2.auth.controller.request.MemberLoginRequest;
import com.sbb2.auth.service.AuthService;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.MemberRole;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

	@DisplayName("회원가입 성공 테스트")
    @Test
    void signup_success() {
        // Given
		String givenEmail = "testEmail@naver.com";
		String givenUsername = "testUsername";
		String givenPassword = "!testPassword1234";
		String givenPasswordCheck = "!testPassword1234";
		MemberRole memberRole = MemberRole.USER;

		MemberEmailSignupRequest memberEmailSignupRequest = new MemberEmailSignupRequest(givenEmail, givenUsername, givenPassword, givenPasswordCheck);
        MemberEmailSignupResponse memberEmailSignupResponse = new MemberEmailSignupResponse(givenEmail, givenUsername, memberRole);

        given(authService.signup(memberEmailSignupRequest.email(), memberEmailSignupRequest.username(), memberEmailSignupRequest.password())).willReturn(memberEmailSignupResponse);

        // When
        ResponseEntity<GenericResponse<MemberEmailSignupResponse>> result = authController.signup(memberEmailSignupRequest);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().getData()).isEqualTo(memberEmailSignupResponse);
        assertThat(result.getHeaders().getLocation().toString()).isEqualTo("/login");
    }
	@DisplayName("회원 로그인 성공 테스트")
    @Test
    void login_success() {
        // Given
		String givenEmail = "testEmail@naver.com";
		String givenUsername = "testUsername";
		String givenPassword = "!testPassword1234";
		MemberRole memberRole = MemberRole.USER;

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest(givenEmail, givenPassword);
        MemberLoginResponse memberLoginResponse = new MemberLoginResponse(givenEmail, givenUsername, memberRole);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(authService.memberLogin(memberLoginRequest.email(), memberLoginRequest.password())).thenReturn(memberLoginResponse);
        when(jwtUtil.createAccessToken(memberLoginResponse)).thenReturn(accessToken);
        when(jwtUtil.createRefreshToken(memberLoginResponse)).thenReturn(refreshToken);

        // When
        ResponseEntity<GenericResponse<MemberLoginResponse>> result = authController.login(memberLoginRequest);

        // Then
		ResponseCookie cookie = ResponseCookie.from("refresh", refreshToken)
                .domain("localhost")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData()).isEqualTo(memberLoginResponse);
        assertThat(result.getHeaders().getFirst("Authorization")).isEqualTo("Bearer " + accessToken);
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE)).containsExactly(cookie.toString());
    }
}

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
import com.sbb2.auth.controller.request.PasswordChangeRequest;
import com.sbb2.auth.service.AuthService;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.jwt.JwtUtil;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthController authController;

	@Mock
	private HttpServletRequest request;

	@DisplayName("회원가입 성공 테스트")
	@Test
	void signup_success() {
		// Given
		String givenEmail = "testEmail@naver.com";
		String givenUsername = "testUsername";
		String givenPassword = "!testPassword1234";
		String givenPasswordCheck = "!testPassword1234";
		MemberRole memberRole = MemberRole.USER;

		MemberEmailSignupRequest memberEmailSignupRequest = new MemberEmailSignupRequest(givenEmail, givenUsername,
			givenPassword, givenPasswordCheck);
		MemberEmailSignupResponse memberEmailSignupResponse = new MemberEmailSignupResponse(givenEmail, givenUsername,
			memberRole);

		given(authService.signup(memberEmailSignupRequest.email(), memberEmailSignupRequest.username(),
			memberEmailSignupRequest.password())).willReturn(memberEmailSignupResponse);

		// When
		ResponseEntity<GenericResponse<MemberEmailSignupResponse>> result = authController.signup(
			memberEmailSignupRequest);

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

		when(authService.memberLogin(memberLoginRequest.email(), memberLoginRequest.password())).thenReturn(
			memberLoginResponse);
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

	@DisplayName("로그아웃 성공 테스트")
	@Test
	void logout_success() {
		//given
		String accessToken = "mockAccessToken";
		String refreshToken = "mockRefreshToken";

		given(jwtUtil.getAccessToken(request)).willReturn(accessToken);
		given(jwtUtil.getRefreshToken(request)).willReturn(refreshToken);

		ResponseCookie refreshCookie = ResponseCookie
			.from("refresh", null)
			.domain("localhost")
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(0)
			.sameSite("Strict")
			.build();

		doNothing().when(jwtUtil).logout(accessToken, refreshToken);

		//when
		ResponseEntity<GenericResponse<Void>> response = authController.logout(request);

		//then
		verify(jwtUtil).getAccessToken(request);
		verify(jwtUtil).getRefreshToken(request);
		verify(jwtUtil).logout(accessToken, refreshToken);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).isEqualTo(refreshCookie.toString());
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getData()).isNull();
	}

	@DisplayName("비밀번호 변경 성공 테스트")
	@Test
	void change_password_success() {
		//given
		PasswordChangeRequest givenPasswordChangeRequest = PasswordChangeRequest.builder()
			.originalPassword("!testPassword1234")
			.password("!changePassword1234")
			.passwordCheck("!changePassword1234")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.password("!testPassword1234")
			.username("testUsername")
			.memberRole(MemberRole.USER)
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		doNothing().when(authService).passwordChange(givenPasswordChangeRequest.originalPassword(),
				givenPasswordChangeRequest.getPassword(), givenMemberUserDetails.getMember());
		//when
		ResponseEntity<GenericResponse<Void>> result = authController.changePassword(
			givenPasswordChangeRequest, givenMemberUserDetails);

		//then
		verify(authService).passwordChange(givenPasswordChangeRequest.originalPassword(),
				givenPasswordChangeRequest.getPassword(), givenMemberUserDetails.getMember());

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getData()).isNull();
	}
}

package com.sbb2.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbb2.auth.domain.EmailCertification;
import com.sbb2.auth.domain.VerifyType;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.exception.AuthBusinessLogicException;
import com.sbb2.common.auth.exception.AuthErrorCode;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.mail.service.MailService;
import com.sbb2.common.mail.util.TemplateName;
import com.sbb2.common.redis.service.RedisService;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.LoginType;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;
import com.sbb2.member.exception.MemberBusinessLoginException;
import com.sbb2.member.exception.MemberErrorCode;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private MailService mailService;
	@Mock
	private RedisService redisService;
	@Mock
	private ObjectMapper objectMapper;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(passwordEncoder, memberRepository, authenticationManager, mailService,
			redisService, objectMapper);
	}

	@DisplayName("회원가입 성공 테스트")
	@Test
	void signup_success() {
		//given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";
		LoginType loginType = LoginType.EMAIL;

		Member givenMember = Member.builder()
			.email(email)
			.username(username)
			.password(password)
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.build();

		given(passwordEncoder.encode(password)).willReturn(password);
		given(memberRepository.existsByEmail(email)).willReturn(false);
		given(memberRepository.existsByUsername(username)).willReturn(false);
		given(memberRepository.save(givenMember)).willReturn(givenMember);

		//when
		MemberEmailSignupResponse memberEmailSignupResponse = authService.signup(email, username, password, loginType);

		//then
		assertThat(memberEmailSignupResponse.email()).isEqualTo(email);
		assertThat(memberEmailSignupResponse.username()).isEqualTo(username);
		assertThat(memberEmailSignupResponse.memberRole()).isEqualTo(MemberRole.USER);
	}

	@DisplayName("회원가입시 LoginType가 null일때 실패 테스트")
	@Test
	void signup_login_type_isNull_fail() {
		//given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";
		LoginType loginType = null;

		Member givenMember = Member.builder()
			.email(email)
			.username(username)
			.password(password)
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.build();

		given(memberRepository.existsByEmail(email)).willReturn(false);
		given(memberRepository.existsByUsername(username)).willReturn(false);

		//when & then
		assertThatThrownBy(() -> authService.signup(email, username, password, loginType))
			.isInstanceOf(AuthBusinessLogicException.class)
			.hasMessage(AuthErrorCode.LOGIN_TYPE_NOT_SUPPORT.getMessage());
	}

	@DisplayName("회원가입시 이메일 중복 실패 테스트")
	@Test
	void signup_exists_email_fail() {
		//given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";
		LoginType loginType = LoginType.EMAIL;


		given(memberRepository.existsByEmail(email)).willReturn(true);
		given(memberRepository.existsByUsername(username)).willReturn(false);

		//when & then
		assertThatThrownBy(() -> authService.signup(email, username, password, loginType)).isInstanceOf(
			MemberBusinessLoginException.class).hasMessage(MemberErrorCode.EXISTS_EMAIL.getMessage());
	}

	@DisplayName("회원가입시 author 중복 실패 테스트")
	@Test
	void signup_exists_username_fail() {
		//given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";
		LoginType loginType = LoginType.EMAIL;

		given(memberRepository.existsByEmail(email)).willReturn(false);
		given(memberRepository.existsByUsername(username)).willReturn(true);

		//when & then
		assertThatThrownBy(() -> authService.signup(email, username, password, loginType)).isInstanceOf(
			MemberBusinessLoginException.class).hasMessage(MemberErrorCode.EXISTS_USERNAME.getMessage());
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void memberLogin_Success() {
		// Given
		String username = "testUsername";
		String email = "testEmail@naver.com";
		String password = "!Password1234";

		Member member = Member.builder()
			.email(email)
			.username(username)
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.build();

		MemberUserDetails userDetails = new MemberUserDetails(member);
		Authentication successAuth = new MemberLoginToken(userDetails.getAuthorities(), userDetails, null);

		given(authenticationManager.authenticate(any(MemberLoginToken.class))).willReturn(successAuth);

		// When
		MemberLoginResponse memberLoginResponse = authService.memberLogin(email, password);

		// Then
		assertAll(() -> assertThat(memberLoginResponse.email()).isEqualTo(email),
			() -> assertThat(memberLoginResponse.username()).isEqualTo(username),
			() -> assertThat(memberLoginResponse.memberRole()).isEqualTo(MemberRole.USER));

		verify(authenticationManager).authenticate(any(MemberLoginToken.class));
	}

	@DisplayName("비밀번호 변경 성공 테스트")
	@Test
	void update_password_success() {
		//given
		Member loginMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("!testPassword1234")
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.build();

		String originalPassword = "!testPassword1234";
		String changePassword = "!testPassword4321";

		given(passwordEncoder.matches(originalPassword, loginMember.password())).willReturn(
			originalPassword.equals(loginMember.password()));

		Member changedPasswordMember = loginMember.changePassword(changePassword);

		given(memberRepository.save(changedPasswordMember)).willReturn(changedPasswordMember);

		given(passwordEncoder.encode(changePassword)).willReturn(changePassword);

		//when
		authService.passwordChange(originalPassword, changePassword, loginMember);

		//then
		verify(passwordEncoder, times(1)).matches(originalPassword, loginMember.password());
		verify(memberRepository, times(1)).save(changedPasswordMember);
	}

	@DisplayName("비밀번호 변경시 기존 비밀번호가 일치하지 않을 떄 실패 테스트")
	@Test
	void update_password_not_match_fail() {
		//given
		Member loginMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("!testPassword1234")
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.build();

		String originalPassword = "!testPassword12345";
		String changePassword = "!testPassword4321";

		given(passwordEncoder.matches(originalPassword, loginMember.password())).willReturn(
			originalPassword.equals(loginMember.password()));

		//when & then
		assertThatThrownBy(
			() -> authService.passwordChange(originalPassword, changePassword, loginMember)).isInstanceOf(
			AuthBusinessLogicException.class).hasMessage(AuthErrorCode.PASSWORD_NOT_MATCH.getMessage());

	}

	@DisplayName("인증 코드 전송 성공 테스트")
	@Test
	void sendCode_Success() {
		// given
		String email = "testEmail@naver.com";
		VerifyType verifyType = VerifyType.PASSWORD_RESET_VERIFY;

		Member member = Member.builder()
			.email(email)
			.username("testUsername")
			.loginType(LoginType.EMAIL)
			.build();

		given(memberRepository.findByEmail(email))
			.willReturn(Optional.of(member));

		given(redisService.getHashDataAll("certification_email:" + email))
			.willReturn(new HashMap<>());

		// when
		authService.sendCode(email, verifyType);

		// then
		verify(redisService, times(1)).setHashDataAll(eq("certification_email:" + email), isNull());
		verify(redisService, times(1)).setTimeout(eq("certification_email:" + email), eq(600000L));
		verify(mailService, times(1)).sendEmail(eq(email), anyMap(), eq(TemplateName.PASSWORD_RESET_VERIFY));
	}

	@DisplayName("비밀번호 초기화 성공 테스트")
	@Test
	void passwordReset_Success() {
		// given
		String email = "testEmail@naver.com";
		String certificationCode = "123456";
		VerifyType verifyType = VerifyType.PASSWORD_RESET_VERIFY;

		Map<Object, Object> redisData = Map.of("certificationCode", certificationCode, "verifyType",
			verifyType.toString(), "sendCount", "1");

		Member member = Member.builder()
			.email(email)
			.username("testUsername")
			.password("oldPassword")
			.loginType(LoginType.EMAIL)
			.build();

		given(redisService.getHashDataAll("certification_email:" + email)).willReturn(redisData);

		given(objectMapper.convertValue(redisData, EmailCertification.class)).willReturn(
			new EmailCertification(certificationCode, verifyType.toString(), "1"));

		given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));

		// when
		authService.passwordReset(email, certificationCode, verifyType);

		// then
		verify(redisService, times(1)).getHashDataAll("certification_email:" + email);
		verify(mailService, times(1)).sendEmail(eq(email), anyMap(), eq(TemplateName.PASSWORD_RESET));
	}

	@DisplayName("인증 코드 불일치 실패 테스트")
	@Test
	void passwordReset_CertificationCodeNotMatch_Fail() {
		// given
		String email = "testEmail@naver.com";
		String certificationCode = "123456";
		VerifyType verifyType = VerifyType.PASSWORD_RESET_VERIFY;

		Map<Object, Object> redisData = Map.of("certification_email", "wrongCode", "verifyType", verifyType.toString(),
			"sendCount", "1");

		given(redisService.getHashDataAll("certification_email:" + email)).willReturn(redisData);

		given(objectMapper.convertValue(redisData, EmailCertification.class)).willReturn(
			new EmailCertification("wrongCode", verifyType.toString(), "1"));

		// when & then
		assertThatThrownBy(() -> authService.passwordReset(email, certificationCode, verifyType)).isInstanceOf(
			AuthBusinessLogicException.class).hasMessage(AuthErrorCode.CERTIFICATION_CODE_NOT_MATCH.getMessage());
	}

	@DisplayName("이메일로 멤버 찾기 실패 테스트")
	@Test
	void passwordReset_MemberNotFound_Fail() {
		// given
		String email = "testEmail@naver.com";
		String certificationCode = "123456";
		VerifyType verifyType = VerifyType.PASSWORD_RESET_VERIFY;

		Map<Object, Object> redisData = Map.of("certificationCode", certificationCode, "verifyType",
			verifyType.toString(), "sendCount", "1");

		given(redisService.getHashDataAll("certification_email:" + email)).willReturn(redisData);

		given(objectMapper.convertValue(redisData, EmailCertification.class)).willReturn(
			new EmailCertification(certificationCode, verifyType.toString(), "1"));

		given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> authService.passwordReset(email, certificationCode, verifyType)).isInstanceOf(
			MemberBusinessLoginException.class).hasMessage(MemberErrorCode.NOT_FOUND.getMessage());
	}
}

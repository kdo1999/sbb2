package com.sbb2.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.infrastructer.member.repository.MemberRepository;
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
	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(passwordEncoder, memberRepository, authenticationManager);
	}

	@DisplayName("회원가입 성공 테스트")
	@Test
	void signup_success() {
	    //given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";

		Member givenMember = Member.builder()
			.email(email)
			.username(username)
			.password(password)
			.memberRole(MemberRole.USER)
			.build();

		given(passwordEncoder.encode(password)).willReturn(password);
		given(memberRepository.existsByEmail(email)).willReturn(false);
		given(memberRepository.existsByUsername(username)).willReturn(false);
		given(memberRepository.save(givenMember)).willReturn(givenMember);

	    //when
		MemberEmailSignupResponse memberEmailSignupResponse = authService.signup(email, username, password);

		//then
		assertThat(memberEmailSignupResponse.email()).isEqualTo(email);
		assertThat(memberEmailSignupResponse.username()).isEqualTo(username);
		assertThat(memberEmailSignupResponse.memberRole()).isEqualTo(MemberRole.USER);
	}

	@DisplayName("회원가입시 이메일 중복 실패 테스트")
	@Test
	void signup_exists_email_fail() {
	    //given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";

		given(memberRepository.existsByEmail(email)).willReturn(true);
		given(memberRepository.existsByUsername(username)).willReturn(false);

	    //when & then
		assertThatThrownBy(() -> authService.signup(email, username, password))
			.isInstanceOf(MemberBusinessLoginException.class)
			.hasMessage(MemberErrorCode.EXISTS_EMAIL.getMessage());
	}

	@DisplayName("회원가입시 author 중복 실패 테스트")
	@Test
	void signup_exists_username_fail() {
	    //given
		String email = "testEmail@naver.com";
		String username = "testUsername";
		String password = "testPassword";

		given(memberRepository.existsByEmail(email)).willReturn(false);
		given(memberRepository.existsByUsername(username)).willReturn(true);

	    //when & then
		assertThatThrownBy(() -> authService.signup(email, username, password))
			.isInstanceOf(MemberBusinessLoginException.class)
			.hasMessage(MemberErrorCode.EXISTS_USERNAME.getMessage());
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
            .build();

        MemberUserDetails userDetails = new MemberUserDetails(member);
        Authentication successAuth = new MemberLoginToken(
            userDetails.getAuthorities(),
            userDetails,
            null
        );

        given(authenticationManager.authenticate(any(MemberLoginToken.class)))
            .willReturn(successAuth);

        // When
        MemberLoginResponse memberLoginResponse = authService.memberLogin(email, password);

        // Then
        assertAll(
            () -> assertThat(memberLoginResponse.email()).isEqualTo(email),
            () -> assertThat(memberLoginResponse.username()).isEqualTo(username),
            () -> assertThat(memberLoginResponse.memberRole()).isEqualTo(MemberRole.USER)
        );

        verify(authenticationManager).authenticate(any(MemberLoginToken.class));
    }
}

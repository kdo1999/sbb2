package com.sbb2.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(passwordEncoder, memberRepository);
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
}

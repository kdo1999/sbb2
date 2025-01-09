package com.sbb2.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.auth.exception.AuthBusinessLogicException;
import com.sbb2.common.auth.exception.AuthErrorCode;
import com.sbb2.common.auth.token.MemberLoginToken;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;
import com.sbb2.member.exception.MemberBusinessLoginException;
import com.sbb2.member.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final AuthenticationManager authenticationManager;

	@Override
	public MemberEmailSignupResponse signup(String email, String username, String password) {
		existsMember(email, username);

		Member createMember = Member.builder()
			.email(email)
			.username(username)
			.password(passwordEncoder.encode(password))
			.memberRole(MemberRole.USER)
			.build();

		Member savedMember = memberRepository.save(createMember);

		return MemberEmailSignupResponse.builder()
			.email(savedMember.email())
			.username(savedMember.username())
			.memberRole(savedMember.memberRole())
			.build();
	}

	@Override
	public MemberLoginResponse memberLogin(String email, String password) {
		Authentication authentication = authenticationManager.authenticate(
			new MemberLoginToken(email, password)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		MemberUserDetails memberUserDetails = (MemberUserDetails)authentication.getPrincipal();

		Member member = memberUserDetails.getMember();
		return new MemberLoginResponse(
			member.email(), member.username(),
			member.memberRole()
		);
	}

	@Override
	public void passwordChange(String originalPassword, String changePassword, Member loginMember) {
		if (!passwordEncoder.matches(originalPassword, loginMember.password())) {
			throw new AuthBusinessLogicException(AuthErrorCode.PASSWORD_NOT_MATCH);
		}

		Member changedPasswordMember = loginMember.changePassword(passwordEncoder.encode(changePassword));

		memberRepository.save(changedPasswordMember);
	}

	private void existsMember(String email, String username) {
		Boolean emailExists = memberRepository.existsByEmail(email);
		Boolean usernameExists = memberRepository.existsByUsername(username);
		if (emailExists) {
			throw new MemberBusinessLoginException(MemberErrorCode.EXISTS_EMAIL);
		}

		if (usernameExists) {
			throw new MemberBusinessLoginException(MemberErrorCode.EXISTS_USERNAME);
		}
	}
}

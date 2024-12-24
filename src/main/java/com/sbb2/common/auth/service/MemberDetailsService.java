package com.sbb2.common.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.common.auth.exception.AuthBusinessLogicException;
import com.sbb2.common.auth.exception.AuthErrorCode;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.Member;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class MemberDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String email) throws AuthBusinessLogicException {
		Member findMember = memberRepository.findByEmail(email)
			.orElseThrow(() -> new AuthBusinessLogicException(AuthErrorCode.USERNAME_NOT_FOUND));

		return new MemberUserDetails(findMember);
	}
}

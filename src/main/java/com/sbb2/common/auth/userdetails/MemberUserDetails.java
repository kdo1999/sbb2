package com.sbb2.common.auth.userdetails;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sbb2.member.domain.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MemberUserDetails implements UserDetails {
	private final Member member;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add((GrantedAuthority)() -> member.memberRole().toString());

		return collection;
	}

	@Override
	public String getPassword() {
		return member.password();
	}

	@Override
	public String getUsername() {
		return member.email();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 회원 탈퇴 및 차단 체크
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	public Member getMember() {
		return member;
	}
}

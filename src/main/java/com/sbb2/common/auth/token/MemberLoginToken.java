package com.sbb2.common.auth.token;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberLoginToken extends AbstractAuthenticationToken {
	private Object principal;
	private Object credentials;

	/**
	 * 인증 받지 않은 토큰 생성
	 * @param principal
	 * @param credentials
	 */
	public MemberLoginToken(Object principal, Object credentials) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(false);
	}

	/**
	 * 인증 받은 토큰 생성
	 * @param authorities
	 * @param principal
	 * @param credentials
	 */
	public MemberLoginToken(
		Collection<? extends GrantedAuthority> authorities,
		Object principal, Object credentials) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}

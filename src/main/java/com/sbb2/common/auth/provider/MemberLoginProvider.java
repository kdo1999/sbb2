package com.sbb2.common.auth.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.sbb2.common.auth.exception.AuthBusinessLogicException;
import com.sbb2.common.auth.exception.AuthErrorCode;
import com.sbb2.common.auth.token.MemberLoginToken;

import lombok.RequiredArgsConstructor;

/**
 * 회원 로그인 인증 Provider
 *
 * @author : Kim Dong O
 * @fileName : MemberLoginProvider
 * @since : 5/14/24
 */
@RequiredArgsConstructor
@Component
public class MemberLoginProvider implements AuthenticationProvider {
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication)
		throws AuthBusinessLogicException {
		String email = authentication.getName();
		String password = (String)authentication.getCredentials();

		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		//BCryptPasswordEncoder 사용
		if (!bCryptPasswordEncoder.matches(password, userDetails.getPassword())) {
			throw new AuthBusinessLogicException(AuthErrorCode.BAD_CREDENTIALS);
		}

		if (!userDetails.isAccountNonLocked()) {
			throw new AuthBusinessLogicException(AuthErrorCode.NOT_VERIFIED);
		}

		return new MemberLoginToken(userDetails.getAuthorities(), userDetails, null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return MemberLoginToken.class.isAssignableFrom(authentication);
	}
}

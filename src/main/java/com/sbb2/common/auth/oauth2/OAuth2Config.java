package com.sbb2.common.auth.oauth2;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import com.sbb2.common.auth.oauth2.kakao.KakaoOAuth2Constants;
import com.sbb2.common.auth.oauth2.kakao.KakaoOAuth2Properties;
import com.sbb2.common.auth.oauth2.naver.NaverOAuth2Constants;
import com.sbb2.common.auth.oauth2.naver.NaverOAuth2Properties;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class OAuth2Config {
	@Value("${oauth2.naver.client-id}")
	private String naverClientId;
	@Value("${oauth2.naver.client-secret}")
	private String naverClientSecret;
	@Value("${oauth2.naver.redirect-uri}")
	private String naverRedirectUri;

	@Value("${oauth2.kakao.client-id}")
	private String kakaoClientId;
	@Value("${oauth2.kakao.client-secret}")
	private String kakaoClientSecret;
	@Value("${oauth2.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {

		return new InMemoryClientRegistrationRepository(
			List.of(kakaoRegistration(kakaoOAuth2Properties()), naverRegistration(naverOAuth2Properties())));
	}

	private ClientRegistration kakaoRegistration(KakaoOAuth2Properties kakaoOauth2Properties) {
		return ClientRegistration
			.withRegistrationId(KakaoOAuth2Constants.KAKAO_REGISTRATION_ID)
			.clientId(kakaoOauth2Properties.getClientId())
			.clientSecret(kakaoOauth2Properties.getClientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.scope(KakaoOAuth2Constants.KAKAO_SCOPE)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri(kakaoOauth2Properties.getRedirectUri())
			.clientName(KakaoOAuth2Constants.KAKAO_CLIENT_NAME)
			.authorizationUri(KakaoOAuth2Constants.KAKAO_AUTHORIZATION_URI)
			.tokenUri(KakaoOAuth2Constants.KAKAO_TOKEN_URI)
			.userInfoUri(KakaoOAuth2Constants.KAKAO_USER_INFO_URI)
			.userNameAttributeName(KakaoOAuth2Constants.KAKAO_USER_NAME_ATTRIBUTE)
			.build();
	}

	private ClientRegistration naverRegistration(NaverOAuth2Properties naverOAuth2Properties) {
		return ClientRegistration
			.withRegistrationId(NaverOAuth2Constants.NAVER_REGISTRATION_ID)
			.clientId(naverOAuth2Properties.getClientId())
			.clientSecret(naverOAuth2Properties.getClientSecret())
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			.scope(NaverOAuth2Constants.NAVER_SCOPE)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri(naverOAuth2Properties.getRedirectUri())
			.clientName(NaverOAuth2Constants.NAVER_CLIENT_NAME)
			.authorizationUri(NaverOAuth2Constants.NAVER_AUTHORIZATION_URI)
			.tokenUri(NaverOAuth2Constants.NAVER_TOKEN_URI)
			.userInfoUri(NaverOAuth2Constants.NAVER_USER_INFO_URI)
			.userNameAttributeName(NaverOAuth2Constants.NAVER_USER_NAME_ATTRIBUTE)
			.build();
	}

	@Bean
	public OAuth2AuthorizedClientService authorizedClientService(
		JdbcOperations jdbcOperations,
		ClientRegistrationRepository clientRegistrationRepository // google, facebook, kakao 등의 client 정보를 가지고 있다.
	) {
		return new JdbcOAuth2AuthorizedClientService(jdbcOperations, clientRegistrationRepository);
	}

	@Bean
	public KakaoOAuth2Properties kakaoOAuth2Properties() {
		return new KakaoOAuth2Properties(kakaoClientSecret, kakaoClientId, kakaoRedirectUri);
	}


	@Bean
	public NaverOAuth2Properties naverOAuth2Properties() {
		return new NaverOAuth2Properties(naverClientId, naverClientSecret, naverRedirectUri);
	}
}

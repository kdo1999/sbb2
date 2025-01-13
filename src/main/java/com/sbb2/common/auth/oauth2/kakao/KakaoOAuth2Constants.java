package com.sbb2.common.auth.oauth2.kakao;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoOAuth2Constants {
	//카카오 OAuth2 등록 ID
	public static final String KAKAO_REGISTRATION_ID = "kakao";

	//카카오 클라이언트 이름
	public static final String KAKAO_CLIENT_NAME = "kakao";

	//카카오 인증 URI
	public static final String KAKAO_AUTHORIZATION_URI = "https://kauth.kakao.com/oauth/authorize";

	//카카오 토큰 URI
	public static final String KAKAO_TOKEN_URI = "https://kauth.kakao.com/oauth/token";

	//카카오 사용자 정보 URI
	public static final String KAKAO_USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

	//카카오 사용자 이름 속성
	public static final String KAKAO_USER_NAME_ATTRIBUTE = "id";

	//카카오 OAuth2 요청
	public static final List<String> KAKAO_SCOPE = List.of("profile_nickname", "account_email", "profile_image");
}
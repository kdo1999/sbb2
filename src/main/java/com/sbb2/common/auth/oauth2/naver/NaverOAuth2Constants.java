package com.sbb2.common.auth.oauth2.naver;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Naver 설정 상수를 정의한 클래스 입니다.
 *
 * @author : KimDongO
 * @fileName : NaverOAuth2Properties
 * @since : 2024/06/10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NaverOAuth2Constants {
	//NAVER OAuth2 등록 ID
	public static final String NAVER_REGISTRATION_ID = "naver";
	//NAVER Client Name
	public static final String NAVER_CLIENT_NAME = "naver";
	//NAVER 인증 URI
	public static final String NAVER_AUTHORIZATION_URI = "https://nid.naver.com/oauth2.0/authorize";
	//NAVER 토큰 URI
	public static final String NAVER_TOKEN_URI = "https://nid.naver.com/oauth2.0/token";
	//NAVER 사용자 정보 URI
	public static final String NAVER_USER_INFO_URI = "https://openapi.naver.com/v1/nid/me";
	//NAVER 사용자 정보 속성
	public static final String NAVER_USER_NAME_ATTRIBUTE = "response";
	//NAVER OAuth2 요청 범위
	public static final List<String> NAVER_SCOPE = List.of("nickname", "email", "profile_image");
}

package com.sbb2.common.auth.oauth2.kakao;

import lombok.Getter;
import lombok.ToString;

/**
 * Kakao OAuth2를 위한 설정 속성 클래스.
 * 이 클래스는 Kakao와의 OAuth2 인증에 필요한 속성을 저장합니다.
 * 애플리케이션 속성 파일에서 "oauth2.kakao" 접두사로 구성됩니다.
 *
 * @author : sebin
 * @fileName : KakaoOauth2Properties
 * @since : 2024/06/07
 *
 * <p>애플리케이션 속성 파일에서의 사용 예시:
 * <pre>
 * oauth2.kakao.client-id=your-client-id
 * oauth2.kakao.client-secret=your-client-secret
 * oauth2.kakao.redirect-uri=your-redirect-uri
 * </pre>
 * </p>
 */


@Getter
@ToString
public class KakaoOAuth2Properties {
	private final String clientSecret;
	private final String clientId;
	private final String redirectUri;

	public KakaoOAuth2Properties(String clientSecret, String clientId, String redirectUri) {
		this.clientSecret = clientSecret;
		this.clientId = clientId;
		this.redirectUri = redirectUri;
	}
}


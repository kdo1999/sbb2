package com.sbb2.common.auth.oauth2.naver;

import lombok.Getter;
import lombok.ToString;

/**
 * Naver OAuth2를 위한 설정 속성 클래스.
 * 이 클래스는 Naver OAuth2 인증에 필요한 속성을 저장합니다.
 * 애플리케이션 속성 파일에서 "oauth2.naver" 접두사로 구성됩니다.
 *
 * @author : KimDongO
 * @fileName : NaverOAuth2Properties
 * @since : 2024/06/10
 *
 * <p>애플리케이션 속성 파일에서의 사용 예시:
 * <pre>
 * oauth2.naver.client-id=your-client-id
 * oauth2.naver.client-secret=your-client-secret
 * oauth2.naver.redirect-uri=your-redirect-uri
 * </pre>
 * </p>
 */

@Getter
@ToString
public class NaverOAuth2Properties {
	private final String clientId;
	private final String clientSecret;
	private final String redirectUri;

	public NaverOAuth2Properties(String clientId, String clientSecret, String redirectUri) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirectUri = redirectUri;
	}
}

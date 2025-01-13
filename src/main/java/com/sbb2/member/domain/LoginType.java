package com.sbb2.member.domain;

import java.util.stream.Stream;

public enum LoginType {
	EMAIL, KAKAO, GOOGLE, NAVER;

	public static LoginType from(String param) {
		return Stream.of(LoginType.values())
			.filter(loginType -> loginType.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

package com.sbb2.auth.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VerifyType {
	PASSWORD_RESET_VERIFY;

	@JsonCreator
	public static VerifyType from(String param) {
		return Stream.of(VerifyType.values())
			.filter(verify -> verify.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

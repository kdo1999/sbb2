package com.sbb2.voter.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VoterType {
	QUESTION, ANSWER;

	@JsonCreator
	public static VoterType from(String param) {
		return Stream.of(VoterType.values())
			.filter(verify -> verify.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}
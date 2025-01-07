package com.sbb2.comment.domain;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sbb2.voter.domain.VoterType;

public enum ParentType {
	QUESTION, ANSWER;

	@JsonCreator
	public static ParentType from(String param) {
		return Stream.of(ParentType.values())
			.filter(verify -> verify.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

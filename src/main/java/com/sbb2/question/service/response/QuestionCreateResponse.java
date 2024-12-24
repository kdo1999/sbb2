package com.sbb2.question.service.response;

import lombok.Builder;

public record QuestionCreateResponse(Long id) {

	@Builder
	public QuestionCreateResponse(Long id) {
		this.id = id;
	}
}

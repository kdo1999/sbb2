package com.sbb2.answer.service.response;

import lombok.Builder;

public record AnswerCreateResponse(Long questionId, Long answerId, String content, String author) {

	@Builder
	public AnswerCreateResponse(Long questionId, Long answerId, String content, String author) {
		this.questionId = questionId;
		this.answerId = answerId;
		this.content = content;
		this.author = author;
	}
}

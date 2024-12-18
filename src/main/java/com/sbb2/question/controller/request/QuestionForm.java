package com.sbb2.question.controller.request;

import lombok.Builder;

public record QuestionForm(String content, String subject) {

	@Builder
	public QuestionForm(String content, String subject) {
		this.content = content;
		this.subject = subject;
	}
}

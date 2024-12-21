package com.sbb2.question.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record QuestionForm(
	@NotEmpty(message = "제목은 필수 항목입니다.")
	@Size(max = 200)
	String subject,
	@NotEmpty(message = "내용은 필수 항목입니다.")
	String content) {

	@Builder
	public QuestionForm(String subject, String content) {
		this.content = content;
		this.subject = subject;
	}
}

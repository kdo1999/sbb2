package com.sbb2.answer.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record AnswerForm(
	@NotNull(message = "질문 ID는 필수 항목입니다.") Long questionId,
	@NotEmpty(message = "답변 내용은 필수 항목입니다.") String content) {

	@Builder
	public AnswerForm(@NotEmpty(message = "질문 ID는 필수 항목입니다.") Long questionId,
		@NotEmpty(message = "답변 내용은 필수 항목입니다.") String content) {
		this.questionId = questionId;
		this.content = content;
	}
}

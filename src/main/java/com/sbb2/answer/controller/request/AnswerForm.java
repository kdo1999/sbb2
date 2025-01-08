package com.sbb2.answer.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record AnswerForm(
	@NotNull(message = "질문 ID는 필수 항목입니다.", groups = NotNullGroup.class) Long questionId,
	@NotBlank(message = "답변 내용은 필수 항목입니다.", groups = NotBlankGroup.class) String content) {

	@Builder
	public AnswerForm(Long questionId, String content) {
		this.questionId = questionId;
		this.content = content;
	}
}

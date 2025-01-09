package com.sbb2.question.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record QuestionForm(
	@NotBlank(message = "제목은 필수 항목입니다.", groups = NotBlankGroup.class)
	@Size(max = 200, message = "최대 200자까지만 입력 가능합니다.", groups = SizeGroup.class)
	String subject,
	@NotBlank(message = "내용은 필수 항목입니다.", groups = NotBlankGroup.class)
	String content,
	@NotNull(message = "카테고리는 필수 항목입니다.",groups = NotNullGroup.class)
	Long categoryId) {

	@Builder
	public QuestionForm(String subject, String content, Long categoryId) {
		this.content = content;
		this.subject = subject;
		this.categoryId = categoryId;
	}
}

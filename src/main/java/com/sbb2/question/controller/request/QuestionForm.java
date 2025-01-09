package com.sbb2.question.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.common.validation.annotation.ValidEnum;
import com.sbb2.common.validation.annotation.ValidStringEnum;
import com.sbb2.infrastructer.category.entity.CategoryName;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record QuestionForm(
	@NotBlank(message = "제목은 필수 항목입니다.", groups = NotBlankGroup.class)
	@Size(max = 200, message = "최대 200자까지만 입력 가능합니다.", groups = SizeGroup.class)
	String subject,
	@NotBlank(message = "내용은 필수 항목입니다.", groups = NotBlankGroup.class)
	String content,
	@ValidStringEnum(enumClass = CategoryName.class, message = "카테고리는 필수 항목입니다.", groups = ValidEnum.class)
	String categoryName) {

	@Builder
	public QuestionForm(String subject, String content, String categoryName) {
		this.content = content;
		this.subject = subject;
		this.categoryName = categoryName;
	}
}

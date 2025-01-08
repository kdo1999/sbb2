package com.sbb2.comment.controller.request;

import com.sbb2.common.validation.ValidationGroups;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record CommentForm(
	@NotBlank(message = "댓글 내용은 필수 항목입니다.", groups = ValidationGroups.NotBlankGroup.class)
	@Size(max = 200, message = "최대 200자까지만 입력 가능합니다.")
	String content) {

	@Builder
	public CommentForm(String content) {
		this.content = content;
	}
}

package com.sbb2.comment.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.common.validation.annotation.ValidStringEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record CommentCreateForm(
	@NotNull(message = "부모 ID는 필수 항목입니다.", groups = NotNullGroup.class)
	Long parentId,
	@ValidStringEnum(enumClass = ParentType.class, groups = ValidEnumGroup.class)
	String parentType,
	@NotBlank(message = "댓글 내용은 필수 항목입니다.", groups = NotBlankGroup.class)
	@Size(max = 200, message = "최대 200자까지만 입력 가능합니다.", groups = SizeGroup.class)
	String content) {

	@Builder
	public CommentCreateForm(Long parentId, String parentType, String content) {
		this.parentId = parentId;
		this.parentType = parentType;
		this.content = content;
	}
}

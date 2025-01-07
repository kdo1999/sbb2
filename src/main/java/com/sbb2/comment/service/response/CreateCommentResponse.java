package com.sbb2.comment.service.response;

import java.time.LocalDateTime;

import com.sbb2.comment.domain.ParentType;

import lombok.Builder;

public record CreateCommentResponse(Long commentId, Long parentId, String content, String author,
									ParentType parentType, LocalDateTime createdAt, LocalDateTime modifiedAt) {

	@Builder
	public CreateCommentResponse(Long commentId, Long parentId, String content, String author, ParentType parentType,
		LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.commentId = commentId;
		this.parentId = parentId;
		this.content = content;
		this.author = author;
		this.parentType = parentType;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}

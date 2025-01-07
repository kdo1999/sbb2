package com.sbb2.comment.service.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.sbb2.comment.domain.ParentType;

import lombok.Builder;

public record CommentResponse(Long commentId, Long parentId, String content, String author, boolean isAuthor,
							  ParentType parentType, LocalDateTime createdAt, LocalDateTime modifiedAt) {

	@QueryProjection
	@Builder
	public CommentResponse(Long commentId, Long parentId, String content, String author, boolean isAuthor,
		ParentType parentType,
		LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.commentId = commentId;
		this.parentId = parentId;
		this.content = content;
		this.author = author;
		this.isAuthor = isAuthor;
		this.parentType = parentType;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}

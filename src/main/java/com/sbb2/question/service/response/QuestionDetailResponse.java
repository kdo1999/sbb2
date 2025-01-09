package com.sbb2.question.service.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.sbb2.category.service.response.CategoryResponse;

import lombok.Builder;

public record QuestionDetailResponse(Long id, String subject, String content, String author, CategoryResponse categoryResponse,
									 LocalDateTime createdAt,
									 LocalDateTime modifiedAt, Long voterCount, Long commentCount,
									 boolean isAuthor, boolean isVoter) {

	@QueryProjection
	@Builder
	public QuestionDetailResponse(Long id, String subject, String content, String author, CategoryResponse categoryResponse,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long voterCount, Long commentCount, boolean isAuthor, boolean isVoter) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.categoryResponse = categoryResponse;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterCount = voterCount;
		this.commentCount = commentCount;
		this.isAuthor = isAuthor;
		this.isVoter = isVoter;
	}
}

package com.sbb2.question.service.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;

import lombok.Builder;

public record QuestionDetailResponse(Long id, String subject, String content, String author, String categoryDisplayName,
									 LocalDateTime createdAt,
									 LocalDateTime modifiedAt, Long voterCount, Long commentCount,
									 boolean isAuthor, boolean isVoter) {

	@QueryProjection
	@Builder
	public QuestionDetailResponse(Long id, String subject, String content, String author, String categoryDisplayName,
		LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long voterCount, Long commentCount, boolean isAuthor, boolean isVoter) {
		//QuestionQueryRepository에서 혹시나 빈값이 들어올 경우 예외 발생
		if (categoryDisplayName.isBlank()) {
			throw new CategoryBusinessLogicException(CategoryErrorCode.UNKNOWN_SERVER);
		}
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.categoryDisplayName = categoryDisplayName;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterCount = voterCount;
		this.commentCount = commentCount;
		this.isAuthor = isAuthor;
		this.isVoter = isVoter;
	}
}

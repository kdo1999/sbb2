package com.sbb2.answer.service.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public record AnswerDetailResponse(Long id, String content, String author, Long questionId, LocalDateTime createdAt,
								   LocalDateTime modifiedAt, Long voterCount, boolean isAuthor, boolean isVoter) {
	@Builder
	@QueryProjection
	public AnswerDetailResponse(Long id, String content, String author, Long questionId, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long voterCount, boolean isAuthor, boolean isVoter) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.questionId = questionId;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterCount = voterCount;
		this.isAuthor = isAuthor;
		this.isVoter = isVoter;
	}
}

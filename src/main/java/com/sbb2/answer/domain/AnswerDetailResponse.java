package com.sbb2.answer.domain;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public record AnswerDetailResponse(Long id, String content, String username, Long questionId, LocalDateTime createdAt,
								   LocalDateTime modifiedAt, Long voterCount, boolean isVoter) {
	@Builder
	@QueryProjection
	public AnswerDetailResponse(Long id, String content, String username, Long questionId, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long voterCount, boolean isVoter) {
		this.id = id;
		this.content = content;
		this.username = username;
		this.questionId = questionId;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterCount = voterCount;
		this.isVoter = isVoter;
	}
}

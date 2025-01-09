package com.sbb2.question.service.response;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

public record QuestionPageResponse(Long id, String subject, String content, String author, Long viewCount, LocalDateTime createdAt, LocalDateTime modifiedAt, Long answerCount) {

	@QueryProjection
	public QuestionPageResponse(Long id, String subject, String content, String author, Long viewCount, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long answerCount) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.viewCount = viewCount;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerCount = answerCount;
	}
}

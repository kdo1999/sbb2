package com.sbb2.question.domain;

import java.time.LocalDateTime;

import com.querydsl.core.annotations.QueryProjection;

public record QuestionPageResponse(Long id, String subject, String content, String author, LocalDateTime createdAt, LocalDateTime modifiedAt, Long answerCount) {

	@QueryProjection
	public QuestionPageResponse(Long id, String subject, String content, String author, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Long answerCount) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerCount = answerCount;
	}

	@Override
	public String toString() {
		return "QuestionPageResponse{" +
			"id=" + id +
			", subject='" + subject + '\'' +
			", content='" + content + '\'' +
			", author='" + author + '\'' +
			", createdAt=" + createdAt +
			", modifiedAt=" + modifiedAt +
			", answerCount=" + answerCount +
			'}';
	}
}

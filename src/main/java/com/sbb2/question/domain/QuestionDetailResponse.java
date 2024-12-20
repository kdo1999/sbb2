package com.sbb2.question.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.querydsl.core.annotations.QueryProjection;
import com.sbb2.answer.domain.AnswerDetailResponse;

import lombok.Builder;

public record QuestionDetailResponse(Long id, String subject, String content, String author, LocalDateTime createdAt,
									 LocalDateTime modifiedAt, List<AnswerDetailResponse> answerList, Long voterCount, boolean isVoter) {

	@QueryProjection
	@Builder
	public QuestionDetailResponse(Long id, String subject, String content, String author, LocalDateTime createdAt,
		LocalDateTime modifiedAt, List<AnswerDetailResponse> answerList, Long voterCount, boolean isVoter) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerList = answerList == null ? new ArrayList<>() : answerList;
		this.voterCount = voterCount;
		this.isVoter = isVoter;
	}

	@Override
	public String toString() {
		return "QuestionDetailResponse{" +
			"id=" + id +
			", subject='" + subject + '\'' +
			", content='" + content + '\'' +
			", author='" + author + '\'' +
			", createdAt=" + createdAt +
			", modifiedAt=" + modifiedAt +
			", answerList=" + answerList +
			", voterCount=" + voterCount +
			", isVoter=" + isVoter +
			'}';
	}
}

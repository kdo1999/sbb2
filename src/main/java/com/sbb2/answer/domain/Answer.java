package com.sbb2.answer.domain;

import java.time.LocalDateTime;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.Builder;

public record Answer(Long id, String content, Member author, Question question, LocalDateTime createdAt, LocalDateTime modifiedAt) {

	@Builder
	public Answer(Long id, String content, Member author, Question question, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.question = question;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}

package com.sbb2.comment;

import java.time.LocalDateTime;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.Builder;

public record Comment(Long id, String content, Member author, Question question, Answer answer, LocalDateTime createdAt,
					  LocalDateTime modifiedAt) {

	@Builder
	public Comment(Long id, String content, Member author, Question question, Answer answer, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.question = question;
		this.answer = answer;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}

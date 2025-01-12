package com.sbb2.comment.domain;

import java.time.LocalDateTime;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.Builder;

public record Comment(Long id, String content, Member author, Question question, Question rootQuestion, Answer answer, LocalDateTime createdAt,
					  LocalDateTime modifiedAt) {

	@Builder
	public Comment(Long id, String content, Member author, Question question, Question rootQuestion, Answer answer, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.question = question;
		this.rootQuestion = rootQuestion;
		this.answer = answer;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public Comment fetch(String updateContent) {
		if (this.content != updateContent) {
			return Comment.builder()
				.id(this.id)
				.content(updateContent)
				.author(this.author)
				.question(this.question)
				.rootQuestion(this.rootQuestion)
				.answer(this.answer)
				.createdAt(this.createdAt)
				.modifiedAt(this.modifiedAt)
				.build();
		} else {
			return this;
		}
	}
}

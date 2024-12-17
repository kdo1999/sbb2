package com.sbb2.question.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;

import lombok.Builder;

public record Question (Long id, String subject, String content, Member author, LocalDateTime createdAt, LocalDateTime modifiedAt, List<Answer> answerList) {

	@Builder
	public Question(Long id, String subject, String content, Member author, LocalDateTime createdAt, LocalDateTime modifiedAt, List<Answer> answerList) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerList = answerList == null ? new ArrayList<>() : answerList;
	}

	public Question fetch(Question updateQuestion) {
		return Question.builder()
			.id(this.id)
			.subject(updateQuestion.subject)
			.content(updateQuestion.content())
			.author(this.author)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.answerList(this.answerList)
			.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Question question = (Question)o;
		return Objects.equals(id, question.id) && Objects.equals(author, question.author)
			&& Objects.equals(subject, question.subject) && Objects.equals(content, question.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, subject, content, author, createdAt, modifiedAt);
	}
}

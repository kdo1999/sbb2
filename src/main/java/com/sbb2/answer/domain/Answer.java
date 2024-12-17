package com.sbb2.answer.domain;

import java.time.LocalDateTime;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Answer answer = (Answer)o;
		return Objects.equals(id, answer.id) && Objects.equals(author, answer.author)
			&& Objects.equals(content, answer.content) && Objects.equals(question, answer.question)
			&& Objects.equals(createdAt, answer.createdAt) && Objects.equals(modifiedAt,
			answer.modifiedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, content, author, question, createdAt, modifiedAt);
	}
}

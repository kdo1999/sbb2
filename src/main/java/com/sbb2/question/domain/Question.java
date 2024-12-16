package com.sbb2.question.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import com.sbb2.member.domain.Member;

import lombok.Builder;

public record Question (Long id, String subject, String content, Member author, LocalDateTime createdAt, LocalDateTime modifiedAt) {

	@Builder
	public Question(Long id, String subject, String content, Member author, LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
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

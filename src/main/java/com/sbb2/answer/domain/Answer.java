package com.sbb2.answer.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.Voter;

import lombok.Builder;

public record Answer(Long id, String content, Member author, Question question, LocalDateTime createdAt,
					 LocalDateTime modifiedAt, Set<Voter> voterSet) {

	@Builder
	public Answer(Long id, String content, Member author, Question question, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Set<Voter> voterSet) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.question = question;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterSet = voterSet == null ? new HashSet<>() : voterSet;
	}

	public Answer fetch(Answer updateAnswer) {
		return Answer.builder()
			.id(this.id)
			.content(updateAnswer.content())
			.author(this.author)
			.question(this.question)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
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

	@Override
	public String toString() {
		return "Answer{" +
			"id=" + id +
			", content='" + content + '\'' +
			", author=" + author +
			", question=" + question +
			", createdAt=" + createdAt +
			", modifiedAt=" + modifiedAt +
			'}';
	}

	public void addVoter(Voter voter) {
		this.voterSet.add(voter);
	}
}

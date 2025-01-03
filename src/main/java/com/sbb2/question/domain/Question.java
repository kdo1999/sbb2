package com.sbb2.question.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;
import com.sbb2.voter.domain.Voter;

import lombok.Builder;

public record Question(Long id, String subject, String content, Member author, LocalDateTime createdAt,
					   LocalDateTime modifiedAt, List<Answer> answerList, Set<Voter> voterSet) {

	@Builder
	public Question(Long id, String subject, String content, Member author, LocalDateTime createdAt,
		LocalDateTime modifiedAt, List<Answer> answerList, Set<Voter> voterSet) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerList = answerList == null ? new ArrayList<>() : answerList;
		this.voterSet = voterSet == null ? new HashSet<>() : voterSet;
	}

	public Question fetch(String updateSubject, String updateContent) {
		return Question.builder()
			.id(this.id)
			.subject(updateSubject)
			.content(updateContent)
			.author(this.author)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.answerList(this.answerList)
			.voterSet(this.voterSet)
			.build();
	}

	public void addVoter(Voter voter) {
		this.voterSet.add(voter);
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

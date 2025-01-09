package com.sbb2.question.domain;

import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.sbb2.category.domain.Category;
import com.sbb2.member.domain.Member;
import com.sbb2.voter.domain.Voter;

import lombok.Builder;

public record Question(Long id, String subject, String content, Member author, Category category, LocalDateTime createdAt,
					   LocalDateTime modifiedAt, Set<Voter> voterSet) {

	@Builder
	public Question(Long id, String subject, String content, Member author, Category category, LocalDateTime createdAt,
		LocalDateTime modifiedAt, Set<Voter> voterSet) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.category = category;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterSet = voterSet == null ? new HashSet<>() : voterSet;
	}

	public Question fetch(String updateSubject, String updateContent, Category updateCategory) {
		return Question.builder()
			.id(this.id)
			.subject(updateSubject)
			.content(updateContent)
			.author(this.author)
			.category(updateCategory)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.voterSet(this.voterSet)
			.build();
	}

	public void addVoter(Voter voter) {
		this.voterSet.add(voter);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, subject, content, author, createdAt, modifiedAt);
	}
}

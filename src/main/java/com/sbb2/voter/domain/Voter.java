package com.sbb2.voter.domain;

import java.util.Objects;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.Builder;

public record Voter(Long id, Question question, Member member, Answer answer) {

	@Builder
	public Voter(Long id, Question question, Member member, Answer answer) {
		this.id = id;
		this.question = question;
		this.member = member;
		this.answer = answer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Voter voter = (Voter)o;
		return Objects.equals(id, voter.id) && Objects.equals(member, voter.member)
			&& Objects.equals(answer, voter.answer) && Objects.equals(question, voter.question);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, question, member, answer);
	}
}

package com.sbb2.voter;

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
}
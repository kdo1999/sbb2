package com.sbb2.voter;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.Builder;

public record Voter(Long id, Question question, Member member) {

	@Builder
	public Voter(Long id, Question question, Member member) {
		this.id = id;
		this.question = question;
		this.member = member;
	}
}

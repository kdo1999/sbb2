package com.sbb2.voter.service;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.service.response.VoterCreateResponse;

public interface VoterService {
	VoterCreateResponse save(Long id, VoterType voterType, Member member);

	void delete(Question question, Member member);

	void delete(Answer answer, Member member);
}

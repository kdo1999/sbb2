package com.sbb2.voter.service;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;

public interface VoterService {
	Voter save(Question question, Member member);
}
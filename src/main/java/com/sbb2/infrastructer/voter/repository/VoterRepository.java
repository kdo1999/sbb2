package com.sbb2.infrastructer.voter.repository;

import java.util.List;

import com.sbb2.voter.domain.Voter;

public interface VoterRepository {
	Voter save(Voter voter);

	List<Voter> findByQuestionId(Long questionId);

	List<Voter> findByAnswerId(Long answerId);

	Voter findByQuestionIdAndMemberId(Long questionId, Long memberId);

	void deleteById(Long voterId);
}

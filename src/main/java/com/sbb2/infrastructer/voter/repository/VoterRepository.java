package com.sbb2.infrastructer.voter.repository;

import java.util.List;

import com.sbb2.voter.Voter;

public interface VoterRepository {
	Voter save(Voter voter);

	List<Voter> findByQuestionId(Long questionId);
}

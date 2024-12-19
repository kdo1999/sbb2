package com.sbb2.infrastructer.voter.repository;

import java.util.List;
import java.util.Optional;

import com.sbb2.voter.domain.Voter;

public interface VoterRepository {
	Voter save(Voter voter);

	List<Voter> findByQuestionId(Long questionId);

	List<Voter> findByAnswerId(Long answerId);

	Optional<Voter> findByQuestionIdAndMemberId(Long questionId, Long memberId);

	void deleteById(Long voterId);

	Optional<Voter> findById(Long voterId);

	Boolean existsByAnswerIdAndMemberId(Long answerId, Long memberId);

	Boolean existsByQuestionIdAndMemberId(Long questionId, Long memberId);
}

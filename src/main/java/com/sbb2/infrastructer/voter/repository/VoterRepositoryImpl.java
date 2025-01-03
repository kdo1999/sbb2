package com.sbb2.infrastructer.voter.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.voter.entity.VoterEntity;
import com.sbb2.voter.domain.Voter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VoterRepositoryImpl implements VoterRepository {
	private final VoterJpaRepository voterJpaRepository;

	@Override
	public Voter save(Voter voter) {

		VoterEntity saved = voterJpaRepository.save(VoterEntity.from(voter));
		return saved.toModel();
	}

	@Override
	public List<Voter> findByQuestionId(Long questionId) {
		return voterJpaRepository.findByQuestionId(questionId).stream().map(VoterEntity::toModel).toList();
	}

	@Override
	public List<Voter> findByAnswerId(Long answerId) {
		return voterJpaRepository.findByAnswerId(answerId).stream().map(VoterEntity::toModel).toList();
	}

	@Override
	public Optional<Voter> findByQuestionIdAndMemberId(Long questionId, Long memberId) {
		return voterJpaRepository.findByQuestionIdAndMemberId(questionId, memberId).map(VoterEntity::toModel);
	}

	@Override
	public Optional<Voter> findByAnswerIdAndMemberId(Long answerId, Long memberId) {
		return voterJpaRepository.findByAnswerIdAndMemberId(answerId, memberId).map(VoterEntity::toModel);
	}

	@Override
	public void deleteById(Long voterId) {
		voterJpaRepository.deleteById(voterId);
	}

	@Override
	public Optional<Voter> findById(Long voterId) {
		return voterJpaRepository.findById(voterId).map(VoterEntity::toModel);
	}

	@Override
	public Boolean existsByAnswerIdAndMemberId(Long answerId, Long memberId) {
		return voterJpaRepository.existsByAnswerIdAndMemberId(answerId, memberId);
	}

	@Override
	public Boolean existsByQuestionIdAndMemberId(Long questionId, Long memberId) {
		return voterJpaRepository.existsByQuestionIdAndMemberId(questionId, memberId);
	}
}

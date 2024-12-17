package com.sbb2.infrastructer.voter.repository;

import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.voter.entity.VoterEntity;
import com.sbb2.voter.Voter;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VoterRepositoryImpl implements VoterRepository {
	private final VoterJpaRepository voterJpaRepository;

	@Override
	public Voter save(Voter voter) {
		return voterJpaRepository.save(VoterEntity.from(voter)).toModel();
	}
}

package com.sbb2.infrastructer.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{
	private final MemberJpaRepository memberJpaRepository;

	@Override
	public Member save(Member member) {
		return memberJpaRepository.save(MemberEntity.from(member)).toModel();
	}

	@Override
	public Optional<Member> findById(Long id) {
		return memberJpaRepository.findById(id).map(MemberEntity::toModel);
	}

	@Override
	public Optional<Member> findByUsername(String username) {
		return memberJpaRepository.findByUsername(username).map(MemberEntity::toModel);
	}

	@Override
	public Boolean existsByUsername(String username) {
		return memberJpaRepository.existsByUsername(username);
	}

	@Override
	public Boolean existsByEmail(String email) {
		return memberJpaRepository.existsByEmail(email);
	}

	@Override
	public Optional<Member> findByEmail(String email) {
		return memberJpaRepository.findByEmail(email).map(MemberEntity::toModel);
	}
}

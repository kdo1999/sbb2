package com.sbb2.infrastructer.member.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository{
	private final MemberJpaRepository memberJpaRepository;

	@Override
	public Optional<Member> save(Member member) {
		return memberJpaRepository.save(member).toModel();
	}
}

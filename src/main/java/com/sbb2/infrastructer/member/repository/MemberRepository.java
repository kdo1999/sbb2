package com.sbb2.infrastructer.member.repository;

import java.util.Optional;

import com.sbb2.member.domain.Member;

public interface MemberRepository {
	Member save(Member member);
	Optional<Member> findById(Long id);
	Optional<Member> findByUsername(String username);
}

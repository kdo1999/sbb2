package com.sbb2.infrastructer.member.repository;

import java.util.Optional;

public interface MemberRepository {
	Optional<Member> save(Member member);
}

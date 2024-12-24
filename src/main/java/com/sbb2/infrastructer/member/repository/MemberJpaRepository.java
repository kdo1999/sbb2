package com.sbb2.infrastructer.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.member.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
	Optional<MemberEntity> findByUsername(String username);
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
}

package com.sbb2.infrastructer.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.member.entity.MemberEntity;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {
}

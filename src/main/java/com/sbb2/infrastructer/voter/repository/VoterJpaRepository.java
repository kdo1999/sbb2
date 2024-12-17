package com.sbb2.infrastructer.voter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.voter.entity.VoterEntity;

public interface VoterJpaRepository extends JpaRepository<VoterEntity, Long> {

}

package com.sbb2.infrastructer.voter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbb2.infrastructer.voter.entity.VoterEntity;

public interface VoterJpaRepository extends JpaRepository<VoterEntity, Long> {
	@Query("select v from VoterEntity v left join fetch v.questionEntity left join fetch v.memberEntity where v.questionEntity.id = :questionId")
	List<VoterEntity> findByQuestionId(@Param("questionId") Long questionId);
}

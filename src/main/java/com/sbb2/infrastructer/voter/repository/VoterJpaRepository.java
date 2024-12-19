package com.sbb2.infrastructer.voter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbb2.infrastructer.voter.entity.VoterEntity;

public interface VoterJpaRepository extends JpaRepository<VoterEntity, Long> {
	@Query("select v from VoterEntity v left join fetch v.questionEntity left join fetch v.memberEntity where v.questionEntity.id = :questionId")
	List<VoterEntity> findByQuestionId(@Param("questionId") Long questionId);

	@Query("select v from VoterEntity v left join fetch v.answerEntity left join fetch v.memberEntity where v.answerEntity.id = :answerId")
	List<VoterEntity> findByAnswerId(@Param("answerId") Long answerId);

	@Query("select v from VoterEntity v where v.questionEntity.id = :questionId and v.memberEntity.id = :memberId")
	VoterEntity findByQuestionIdAndMemberId(@Param("questionId") Long questionId, @Param("memberId") Long memberId);

	@Query("select count(v) > 0 from VoterEntity v where v.answerEntity.id = :answerId and v.memberEntity.id = :memberId")
	Boolean existsByAnswerIdAndMemberId(@Param("answerId") Long answerId, @Param("memberId") Long memberId);
}

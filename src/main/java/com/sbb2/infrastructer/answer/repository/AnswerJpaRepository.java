package com.sbb2.infrastructer.answer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbb2.infrastructer.answer.entity.AnswerEntity;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, Long> {
	@Query("select a from AnswerEntity a left join fetch a.voterEntitySet left join fetch a.author left join fetch a.questionEntity where a.questionEntity.id = :questionId")
	List<AnswerEntity> findByQuestionId(@Param("questionId") Long questionId);
}

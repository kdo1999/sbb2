package com.sbb2.infrastructer.question.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbb2.infrastructer.question.entity.QuestionEntity;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {
	@Query("select q from QuestionEntity q left join fetch q.author left join fetch q.answerEntityList left join fetch q.voterEntitySet left join fetch q.category where q.id = :questionId")
	Optional<QuestionEntity> findById(@Param("questionId") Long questionId);

	@Modifying
	@Query("UPDATE QuestionEntity q SET q.viewCount = q.viewCount + 1 WHERE q.id = :questionId")
	void incrementViewCount(@Param("questionId") Long questionId);
}

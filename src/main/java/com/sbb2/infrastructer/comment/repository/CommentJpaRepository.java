package com.sbb2.infrastructer.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sbb2.infrastructer.comment.entity.CommentEntity;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
	@Query("select c from CommentEntity c where c.questionEntity.id = :questionId")
	List<CommentEntity> findByQuestionId(@Param("questionId") Long questionId);

	@Query("select c from CommentEntity c where c.answerEntity.id = :answerId")
	List<CommentEntity> findByAnswerId(@Param("answerId") Long answerId);
}

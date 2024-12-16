package com.sbb2.infrastructer.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.question.entity.QuestionEntity;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {
}

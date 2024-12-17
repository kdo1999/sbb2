package com.sbb2.infrastructer.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.answer.entity.AnswerEntity;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, Long> {

}

package com.sbb2.infrastructer.question.repository;

import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.question.domain.Question;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {
	private final QuestionJpaRepository questionJpaRepository;

	@Override
	public Question save(Question question) {
		return questionJpaRepository.save(QuestionEntity.from(question)).toModel();
	}
}

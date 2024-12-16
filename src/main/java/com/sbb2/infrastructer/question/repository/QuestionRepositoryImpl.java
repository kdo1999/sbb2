package com.sbb2.infrastructer.question.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.domain.Question;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {
	private final QuestionJpaRepository questionJpaRepository;
	private final QuestionQueryRepository questionQueryRepository;

	@Override
	public Question save(Question question) {
		return questionJpaRepository.save(QuestionEntity.from(question)).toModel();
	}

	@Override
	public Page<QuestionPageResponse> findAll(String keyword, Pageable pageable) {
		return questionQueryRepository.findAll(keyword, pageable);
	}
}

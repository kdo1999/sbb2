package com.sbb2.infrastructer.answer.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sbb2.answer.domain.Answer;
import com.sbb2.infrastructer.answer.entity.AnswerEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository{
	private final AnswerJpaRepository answerJpaRepository;

	@Override
	public Answer save(Answer answer) {
		return answerJpaRepository.save(AnswerEntity.from(answer)).toModel();
	}

	@Override
	public Optional<Answer> findById(Long id) {
		return answerJpaRepository.findById(id).map(AnswerEntity::toModel);
	}
}

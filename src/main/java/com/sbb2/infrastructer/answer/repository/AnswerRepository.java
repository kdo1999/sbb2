package com.sbb2.infrastructer.answer.repository;

import java.util.List;
import java.util.Optional;

import com.sbb2.answer.domain.Answer;

public interface AnswerRepository {
	Answer save(Answer answer);
	Optional<Answer> findById(Long id);

	List<Answer> findByQuestionId(Long questionId);

	void deleteById(Long id);
}

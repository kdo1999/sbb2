package com.sbb2.infrastructer.question.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.domain.Question;

public interface QuestionRepository {
	Question save(Question question);

	Page<QuestionPageResponse> findAll(String keyword, Pageable pageable);
}

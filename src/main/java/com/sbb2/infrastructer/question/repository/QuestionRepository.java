package com.sbb2.infrastructer.question.repository;

import com.sbb2.question.domain.Question;

public interface QuestionRepository {
	Question save(Question question);
}

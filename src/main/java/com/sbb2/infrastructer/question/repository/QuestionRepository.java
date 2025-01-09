package com.sbb2.infrastructer.question.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sbb2.common.util.SearchCondition;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;

public interface QuestionRepository {
	Question save(Question question);

	Optional<Question> findById(Long questionId);

	Page<QuestionPageResponse> findAll(SearchCondition searchCondition, Pageable pageable);

	void deleteById(Long targetId);

	QuestionDetailResponse findDetailById(Long questionId, Long memberId);

	void incrementViewCount(Long questionId);
}

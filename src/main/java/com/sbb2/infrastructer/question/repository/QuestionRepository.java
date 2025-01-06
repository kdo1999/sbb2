package com.sbb2.infrastructer.question.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;
import com.sbb2.question.domain.Question;
import com.sbb2.common.util.SearchCondition;

public interface QuestionRepository {
	Question save(Question question);
	Optional<Question> findById(@Param("id") Long id);
	Page<QuestionPageResponse> findAll(SearchCondition searchCondition, Pageable pageable);

	void deleteById(Long targetId);
	QuestionDetailResponse findDetailById(Long questionId, Long memberId);
}

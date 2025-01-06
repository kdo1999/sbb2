package com.sbb2.infrastructer.answer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.common.util.SearchCondition;

public interface AnswerRepository {
	Answer save(Answer answer);

	Optional<Answer> findById(Long id);

	List<Answer> findByQuestionId(Long questionId);

	void deleteById(Long id);

	Optional<AnswerDetailResponse> findAnswerDetailByIdAndMemberId(Long id, Long memberId);

	Page<AnswerDetailResponse> findAnswerDetailPageByQuestionId(SearchCondition searchCondition, Long questionId,
		Long loginMemberId, Pageable pageable);
}

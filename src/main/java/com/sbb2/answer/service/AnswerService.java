package com.sbb2.answer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.member.domain.Member;

public interface AnswerService {

	AnswerDetailResponse save(Long questionId, String content, Member author);

	Answer findById(Long answerId);

	Answer update(Long answerId, String content, Member author);

	void deleteById(Long answerId, Member author);

	List<Answer> findByQuestionId(Long questionId);

	AnswerDetailResponse findAnswerDetailByIdAndMemberId(Long answerId, Long memberId);

	Page<AnswerDetailResponse> findAnswerDetailPageByQuestionId(SearchCondition searchCondition, Long questionId, Long memberId);
}

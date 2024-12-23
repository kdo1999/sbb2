package com.sbb2.answer.service;

import java.util.List;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.member.domain.Member;

public interface AnswerService {

	AnswerCreateResponse save(Long questionId, String content, Member author);

	Answer findById(Long answerId);

	Answer update(Long answerId, String content, Member author);

	void deleteById(Long answerId, Member author);

	List<Answer> findByQuestionId(Long questionId);
}

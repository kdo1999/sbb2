package com.sbb2.answer.service;

import com.sbb2.answer.domain.Answer;
import com.sbb2.member.domain.Member;

public interface AnswerService {

	Answer save(Long questionId, String content, Member author);

	Answer findById(Long answerId);

	Answer update(Long answerId, String content, Member author);

	void deleteById(Long answerId, Member author);
}

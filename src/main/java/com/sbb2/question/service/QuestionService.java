package com.sbb2.question.service;

import org.springframework.data.domain.Page;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionPageResponse;

public interface QuestionService {
	Question save(String subject, String content, Member author);

	Question findById(Long id);

	Question update(Long id, String subject, String content, Member author);

	void deleteById(Long id, Member author);

	Page<QuestionPageResponse> findAll(int pageNum, String keyword);
}

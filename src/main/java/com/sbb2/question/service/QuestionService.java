package com.sbb2.question.service;

import org.springframework.data.domain.Page;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.response.QuestionCreateResponse;

public interface QuestionService {
	QuestionCreateResponse save(String subject, String content, Member author);

	Question findById(Long id);

	//TODO 반환 타입 void로 바꿀 것
	Question update(Long id, String subject, String content, Member author);

	void deleteById(Long id, Member author);

	Page<QuestionPageResponse> findAll(int pageNum, String keyword);

	QuestionDetailResponse findDetailById(Long id, Member loginMember);
}

package com.sbb2.question.service;

import org.springframework.data.domain.Page;

import com.sbb2.common.util.SearchCondition;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionCreateResponse;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;

public interface QuestionService {
	QuestionCreateResponse save(String subject, String content, Member author, String categoryName);

	Question findById(Long id);

	Question update(Long id, String subject, String content, Member author, String categoryName);

	void deleteById(Long id, Member author);

	Page<QuestionPageResponse> findAll(SearchCondition searchCondition);

	QuestionDetailResponse findDetailById(Long id, Member loginMember);
}

package com.sbb2.question.service;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

public interface QuestionService {
	Question save(String subject, String content, Member author);
}

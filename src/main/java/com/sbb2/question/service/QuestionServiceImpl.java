package com.sbb2.question.service;

import org.springframework.stereotype.Service;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
	private final QuestionRepository questionRepository;

	@Override
	public Question save(String subject, String content, Member author) {
		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		return questionRepository.save(question);
	}
}

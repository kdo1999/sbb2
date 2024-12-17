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

	@Override
	public Question findById(Long id) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("데이터가 존재하지 않습니다."));

		return question;
	}
}

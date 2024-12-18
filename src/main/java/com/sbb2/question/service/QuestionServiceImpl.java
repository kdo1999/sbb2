package com.sbb2.question.service;

import org.springframework.stereotype.Service;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

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
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		return question;
	}

	@Override
	public Question update(Long id, String subject, String content, Member author) {
		Question target = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		Question updateQuestion = Question.builder()
			.subject(subject)
			.content(content)
			.build();

		target = target.fetch(updateQuestion);

		return questionRepository.save(target);
	}

	@Override
	public void deleteById(Long id) {
		Question target = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		questionRepository.deleteById(target.id());
	}
}

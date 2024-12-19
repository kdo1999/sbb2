package com.sbb2.answer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	@Override
	public Answer save(Long questionId, String content, Member author) {
		Question findQuestion = questionRepository.findById(questionId)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		Answer answer = Answer.builder()
			.question(findQuestion)
			.content(content)
			.author(author)
			.build();

		return answerRepository.save(answer);
	}

	@Override
	@Transactional(readOnly = true)
	public Answer findById(Long answerId) {
		return answerRepository.findById(answerId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));
	}

	@Override
	public Answer update(Long answerId, String content, Member author) {
		Answer findAnswer = answerRepository.findById(answerId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

		if (!findAnswer.author().equals(author)) {
			throw new AnswerBusinessLogicException(AnswerErrorCode.UNAUTHORIZED);
		}

		Answer updateAnswer = findAnswer.fetch(content);
		Answer savedAnswer = answerRepository.save(updateAnswer);

		return savedAnswer;
	}
}

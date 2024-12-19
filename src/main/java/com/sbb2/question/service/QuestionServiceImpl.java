package com.sbb2.question.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
	private final QuestionRepository questionRepository;
	private static final int PAGE_SIZE = 10;

	@Override
	public Question save(String subject, String content, Member author) {
		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		return questionRepository.save(question);
	}

	@Transactional(readOnly = true)
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

		if (!target.author().equals(author)) {
			throw new QuestionBusinessLogicException(QuestionErrorCode.UNAUTHORIZED);
		}

		Question updateQuestion = target.fetch(subject, content);

		return questionRepository.save(updateQuestion);
	}

	@Override
	public void deleteById(Long id, Member author) {
		Question target = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		if (!target.author().equals(author)) {
			throw new QuestionBusinessLogicException(QuestionErrorCode.UNAUTHORIZED);
		}

		questionRepository.deleteById(target.id());
	}

	@Transactional(readOnly = true)
	@Override
	public Page<QuestionPageResponse> findAll(int pageNum, String keyword) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGE_SIZE);
		return questionRepository.findAll(keyword, pageRequest);
	}
}

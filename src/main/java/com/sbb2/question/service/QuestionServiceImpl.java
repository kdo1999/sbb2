package com.sbb2.question.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.question.service.response.QuestionCreateResponse;
import com.sbb2.common.util.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
	private final QuestionRepository questionRepository;
	private static final int PAGE_SIZE = 10;

	@Override
	public QuestionCreateResponse save(String subject, String content, Member author) {
		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(question);

		return QuestionCreateResponse.builder()
			.id(savedQuestion.id())
			.build();
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
	public Page<QuestionPageResponse> findAll(SearchCondition searchCondition) {
		//TODO 정렬 기능 추가할 것 (Sort.Order 사용하면 메모리에서 정렬하기 때문에 Enum으로 정렬 값 받고 QuestionQueeryRepository에 넘겨줄 것)
		PageRequest pageRequest = PageRequest.of(searchCondition.pageNum() == null ? 0 : searchCondition.pageNum(),
			PAGE_SIZE);
		return questionRepository.findAll(searchCondition, pageRequest);
	}

	@Transactional(readOnly = true)
	@Override
	public QuestionDetailResponse findDetailById(Long id, Member loginMember) {

		return questionRepository.findDetailById(id, loginMember.id());
	}
}

package com.sbb2.answer.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.service.response.AnswerDetailResponse;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.common.util.SearchCondition;
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

	private static final int PAGE_SIZE = 10;

	@Override
	public AnswerDetailResponse save(Long questionId, String content, Member author) {
		Question findQuestion = questionRepository.findById(questionId)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		Answer answer = Answer.builder()
			.question(findQuestion)
			.content(content)
			.author(author)
			.build();

		Answer savedAnswer = answerRepository.save(answer);

		return AnswerDetailResponse.builder()
			.id(savedAnswer.id())
			.questionId(savedAnswer.question().id())
			.content(savedAnswer.content())
			.author(savedAnswer.author().username())
			.isAuthor(savedAnswer.author().equals(author))
			.isVoter(false)
			.voterCount(0L)
			.createdAt(savedAnswer.createdAt())
			.modifiedAt(savedAnswer.modifiedAt())
			.build();
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

		loginMemberEqualsAuthor(author, findAnswer);

		Answer updateAnswer = findAnswer.fetch(content);
		Answer savedAnswer = answerRepository.save(updateAnswer);

		return savedAnswer;
	}

	@Override
	public void deleteById(Long answerId, Member author) {
		Answer findAnswer = answerRepository.findById(answerId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

		loginMemberEqualsAuthor(author, findAnswer);

		answerRepository.deleteById(findAnswer.id());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Answer> findByQuestionId(Long questionId) {
		return answerRepository.findByQuestionId(questionId);
	}

	@Override
	public AnswerDetailResponse findAnswerDetailByIdAndMemberId(Long answerId, Long memberId) {
		return answerRepository.findAnswerDetailByIdAndMemberId(answerId, memberId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));
	}

	@Override
	public Page<AnswerDetailResponse> findAnswerDetailPageByQuestionId(SearchCondition searchCondition, Long questionId,
		Long memberId) {

		Pageable pageable = PageRequest.of(
			searchCondition.pageNum() == null ? 0 : searchCondition.pageNum(), PAGE_SIZE
		);

		return answerRepository.findAnswerDetailPageByQuestionId(searchCondition, questionId, memberId, pageable);
	}

	private void loginMemberEqualsAuthor(Member author, Answer findAnswer) {
		if (!findAnswer.author().equals(author)) {
			throw new AnswerBusinessLogicException(AnswerErrorCode.UNAUTHORIZED);
		}
	}
}

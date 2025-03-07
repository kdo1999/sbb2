package com.sbb2.infrastructer.answer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.service.response.AnswerDetailResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.answer.entity.AnswerEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository{
	private final AnswerJpaRepository answerJpaRepository;
	private final AnswerQueryRepository answerQueryRepository;

	@Override
	public Answer save(Answer answer) {
		return answerJpaRepository.save(AnswerEntity.from(answer)).toModel();
	}

	@Override
	public Optional<Answer> findById(Long id) {
		return answerJpaRepository.findById(id).map(AnswerEntity::toModel);
	}

	@Override
	public List<Answer> findByQuestionId(Long questionId) {

		return answerJpaRepository.findByQuestionId(questionId).stream()
			.map(AnswerEntity::toModel)
			.toList();
	}

	@Override
	public void deleteById(Long id) {
		answerJpaRepository.deleteById(id);
	}

	@Override
	public Optional<AnswerDetailResponse> findAnswerDetailByIdAndMemberId(Long id, Long memberId) {
		return answerQueryRepository.findByAnswerId(id, memberId);
	}

	@Override
	public Page<AnswerDetailResponse> findAnswerDetailPageByQuestionId(SearchCondition searchCondition, Long questionId,
		Long loginMemberId, Pageable pageable) {
		return answerQueryRepository.findAnswerDetailPageByQuestionId(searchCondition, questionId, loginMemberId, pageable);
	}
}

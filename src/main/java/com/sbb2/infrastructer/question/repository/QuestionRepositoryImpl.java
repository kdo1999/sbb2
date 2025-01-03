package com.sbb2.infrastructer.question.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.domain.Question;
import com.sbb2.question.util.SearchCondition;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepository {
	private final QuestionJpaRepository questionJpaRepository;
	private final QuestionQueryRepository questionQueryRepository;

	@Override
	public Question save(Question question) {
		return questionJpaRepository.save(QuestionEntity.from(question)).toModel();
	}

	@Override
	public Optional<Question> findById(Long id) {
		return questionJpaRepository.findById(id).map(QuestionEntity::toModel);
	}

	@Override
	public Page<QuestionPageResponse> findAll(SearchCondition searchCondition, Pageable pageable) {
		return questionQueryRepository.findAll(searchCondition, pageable);
	}

	@Override
	public void deleteById(Long targetId) {
		questionJpaRepository.deleteById(targetId);
	}

	@Override
	public QuestionDetailResponse findDetailById(Long questionId, Long memberId) {
		return questionQueryRepository.findById(questionId, memberId);
	}
}

package com.sbb2.infrastructer.answer.repository;

import static com.sbb2.infrastructer.answer.entity.QAnswerEntity.*;
import static com.sbb2.infrastructer.question.entity.QQuestionEntity.*;
import static com.sbb2.infrastructer.voter.entity.QVoterEntity.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.answer.domain.QAnswerDetailResponse;
import com.sbb2.infrastructer.answer.entity.QAnswerEntity;
import com.sbb2.question.domain.QQuestionPageResponse;
import com.sbb2.question.domain.QuestionPageResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnswerQueryRepository {
	private final JPAQueryFactory queryFactory;

	public Optional<AnswerDetailResponse> findByAnswerId(Long answerId, Long memberId) {
		AnswerDetailResponse answerDetailResponses = queryFactory.select(new QAnswerDetailResponse(
				answerEntity.id,
				answerEntity.content,
				answerEntity.author.username,
				answerEntity.questionEntity.id,
				answerEntity.createdAt,
				answerEntity.modifiedAt,
				voterEntity.countDistinct(),
				answerEntity.author.id.eq(memberId),
				voterEntity.memberEntity.id.eq(memberId)
			))
			.from(answerEntity)
			.leftJoin(answerEntity.voterEntitySet, voterEntity)
			.leftJoin(answerEntity.author)
			.where(answerEntity.id.eq(answerId))
			.groupBy(
				answerEntity.id,
				answerEntity.content,
				answerEntity.author.username,
				answerEntity.questionEntity.id,
				answerEntity.createdAt,
				answerEntity.modifiedAt,
				voterEntity.memberEntity.id
			)
			.fetchOne();

		return Optional.ofNullable(answerDetailResponses);
	}
}

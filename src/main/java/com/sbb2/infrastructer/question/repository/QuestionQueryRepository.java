package com.sbb2.infrastructer.question.repository;

import static com.sbb2.infrastructer.answer.entity.QAnswerEntity.*;
import static com.sbb2.infrastructer.question.entity.QQuestionEntity.*;
import static com.sbb2.infrastructer.voter.entity.QVoterEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.answer.domain.QAnswerDetailResponse;
import com.sbb2.infrastructer.answer.entity.QAnswerEntity;
import com.sbb2.question.domain.QQuestionDetailResponse;
import com.sbb2.question.domain.QQuestionPageResponse;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<QuestionPageResponse> findAll(String kw, Pageable pageable) {
		List<QuestionPageResponse> content = queryFactory
			.select(new QQuestionPageResponse(
				questionEntity.id,
				questionEntity.subject,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.createdAt,
				questionEntity.modifiedAt,
				queryFactory.select(QAnswerEntity.answerEntity.count())
					.from(QAnswerEntity.answerEntity)
					.where(QAnswerEntity.answerEntity.questionEntity.id.eq(questionEntity.id))))
			.from(questionEntity)
			.leftJoin(questionEntity.author)
			.where(subjectAndContentContains(kw))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(questionEntity.count())
			.from(questionEntity)
			.where(subjectAndContentContains(kw));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public QuestionDetailResponse findById(Long questionId, Long memberId) {
		QAnswerEntity subAnswerEntity = new QAnswerEntity("subAnswerEntity");
		List<AnswerDetailResponse> answerDetailResponses = queryFactory.select(new QAnswerDetailResponse(
				answerEntity.id,
				answerEntity.content,
				answerEntity.author.username,
				answerEntity.questionEntity.id,
				answerEntity.createdAt,
				answerEntity.modifiedAt,
				voterEntity.countDistinct(),
				voterEntity.memberEntity.id.eq(memberId),
				voterEntity.memberEntity.id.eq(memberId)
			))
			.from(answerEntity)
			.leftJoin(answerEntity.voterEntitySet, voterEntity)
			.leftJoin(answerEntity.author)
			.where(answerEntity.questionEntity.id.eq(questionId))
			.groupBy(
				answerEntity.id,
				answerEntity.content,
				answerEntity.author.username,
				answerEntity.questionEntity.id,
				answerEntity.createdAt,
				answerEntity.modifiedAt
			)
			.fetch();

		QuestionDetailResponse questionDetailResponse = queryFactory.select(new QQuestionDetailResponse(
				questionEntity.id,
				questionEntity.subject,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.createdAt,
				questionEntity.modifiedAt,
				Expressions.asSimple(answerDetailResponses),
				voterEntity.countDistinct(),
				questionEntity.author.id.eq(memberId),
				voterEntity.memberEntity.id.eq(memberId)
			))
			.from(questionEntity)
			.leftJoin(questionEntity.author)
			.leftJoin(questionEntity.voterEntitySet, voterEntity)
			.where(questionEntity.id.eq(questionId))
			.groupBy(
				questionEntity.id,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.createdAt,
				questionEntity.modifiedAt
			)
			.fetchOne();

		return questionDetailResponse;
	}

	public BooleanExpression subjectAndContentContains(String kw) {
		return StringUtils.hasText(kw) ? questionEntity.subject.contains(kw).or(questionEntity.content.contains(kw)) :
			null;
	}
}

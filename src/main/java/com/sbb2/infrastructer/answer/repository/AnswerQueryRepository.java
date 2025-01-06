package com.sbb2.infrastructer.answer.repository;

import static com.sbb2.infrastructer.answer.entity.QAnswerEntity.*;
import static com.sbb2.infrastructer.question.entity.QQuestionEntity.*;
import static com.sbb2.infrastructer.voter.entity.QVoterEntity.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.answer.domain.QAnswerDetailResponse;
import com.sbb2.common.util.SearchCondition;
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

	public Page<AnswerDetailResponse> findAnswerDetailPageByQuestionId(SearchCondition searchCondition, Long questionId, Long memberId, Pageable pageable) {
		List<AnswerDetailResponse> content = queryFactory
			.select(new QAnswerDetailResponse(
				answerEntity.id,
				answerEntity.content,
				answerEntity.author.username,
				answerEntity.questionEntity.id,
				answerEntity.createdAt,
				answerEntity.modifiedAt,
				answerEntity.voterEntitySet.size().longValue(),
				answerEntity.author.id.eq(memberId),
				voterEntity.memberEntity.id.eq(memberId)
			))
			.from(answerEntity)
			.leftJoin(answerEntity.author)
			.leftJoin(answerEntity.questionEntity)
			.on(answerEntity.questionEntity.id.eq(questionId))
			.leftJoin(answerEntity.voterEntitySet, voterEntity)
			.where(contentContains(searchCondition))
			.orderBy(getOrderBy(searchCondition))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(answerEntity.count())
			.from(answerEntity)
			.where(contentContains(searchCondition));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public OrderSpecifier<?> getOrderBy(SearchCondition searchCondition) {
		// 기본 정렬 방식 설정
		Order queryOrder = Order.ASC.toString().equalsIgnoreCase(searchCondition.order()) ? Order.ASC : Order.DESC;

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"modifiedAt", answerEntity.modifiedAt,
			"voter", answerEntity.voterEntitySet.size()
		);

		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(searchCondition.sort()) && fieldMap.containsKey(searchCondition.sort())
				? fieldMap.get(searchCondition.sort())
				: answerEntity.createdAt;

		return new OrderSpecifier<>(queryOrder, sortField);
	}

	public BooleanExpression contentContains(SearchCondition searchCondition) {
		return StringUtils.hasText(searchCondition.kw()) ?
			answerEntity.content.contains(searchCondition.kw()) :
			null;
	}
}

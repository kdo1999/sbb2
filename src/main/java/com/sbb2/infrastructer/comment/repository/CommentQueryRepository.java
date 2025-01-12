package com.sbb2.infrastructer.comment.repository;

import static com.sbb2.infrastructer.comment.entity.QCommentEntity.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.comment.service.response.QCommentResponse;
import com.sbb2.common.util.SearchCondition;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {
	private final JPAQueryFactory queryFactory;

	public Page<CommentResponse> findAll(Long parentId, Long memberId, ParentType parentType, Pageable pageable,
		SearchCondition searchCondition) {
		List<CommentResponse> content = queryFactory
			.select(new QCommentResponse(
				commentEntity.id,
				commentEntity.rootQuestion.id,
				getParentIdPath(parentType),
				commentEntity.content,
				commentEntity.memberEntity.username,
				commentEntity.memberEntity.id.eq(memberId),
				getParentType(parentType),
				commentEntity.createdAt,
				commentEntity.modifiedAt
			))
			.from(commentEntity)
			.leftJoin(commentEntity.memberEntity)
			.where(parentIdEquals(parentId, parentType), usernameEquals(searchCondition))
			.orderBy(getOrderBy(searchCondition))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(commentEntity.count())
			.from(commentEntity)
			.where(parentIdEquals(parentId, parentType), usernameEquals(searchCondition));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private Expression<Long> getParentIdPath(ParentType parentType) {
		return parentType == ParentType.QUESTION
			? commentEntity.questionEntity.id
			: commentEntity.answerEntity.id;
	}

	private Expression<ParentType> getParentType(ParentType parentType) {
		return parentType == null ? Expressions.nullExpression(ParentType.class) : Expressions.asSimple(parentType);
	}

	private BooleanExpression parentIdEquals(Long parentId, ParentType parentType) {
		if (parentId == null || parentType == null) {
			return null;
		}

		return parentType == ParentType.QUESTION
			? commentEntity.questionEntity.id.eq(parentId)
			: commentEntity.answerEntity.id.eq(parentId);
	}

	private BooleanExpression usernameEquals(SearchCondition searchCondition) {
		return StringUtils.hasText(searchCondition.username()) ?
			commentEntity.memberEntity.username.eq(searchCondition.username()) : null;
	}

	private OrderSpecifier<?> getOrderBy(SearchCondition searchCondition) {
		// 기본 정렬 방식 설정
		Order queryOrder = Order.ASC.toString().equalsIgnoreCase(searchCondition.order()) ? Order.ASC : Order.DESC;

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"modifiedAt", commentEntity.modifiedAt
		);

		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(searchCondition.sort()) && fieldMap.containsKey(searchCondition.sort())
				? fieldMap.get(searchCondition.sort())
				: commentEntity.createdAt;

		return new OrderSpecifier<>(queryOrder, sortField);
	}
}
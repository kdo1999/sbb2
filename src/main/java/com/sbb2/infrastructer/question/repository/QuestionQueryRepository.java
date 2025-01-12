package com.sbb2.infrastructer.question.repository;

import static com.sbb2.infrastructer.answer.entity.QAnswerEntity.*;
import static com.sbb2.infrastructer.comment.entity.QCommentEntity.*;
import static com.sbb2.infrastructer.question.entity.QQuestionEntity.*;
import static com.sbb2.infrastructer.voter.entity.QVoterEntity.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.category.service.response.QCategoryResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.question.service.response.QQuestionDetailResponse;
import com.sbb2.question.service.response.QQuestionPageResponse;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<QuestionPageResponse> findAll(SearchCondition searchCondition, Pageable pageable) {
		List<QuestionPageResponse> content = queryFactory
			.selectDistinct(new QQuestionPageResponse(
				questionEntity.id,
				questionEntity.subject,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.viewCount,
				questionEntity.createdAt,
				questionEntity.modifiedAt,
				answerEntity.id.countDistinct()))
			.from(questionEntity)
			.leftJoin(questionEntity.author)
			.leftJoin(questionEntity.answerEntityList, answerEntity)
			.where(subjectAndContentContains(searchCondition), categoryIdEquals(searchCondition),
				usernameEquals(searchCondition))
			.groupBy(questionEntity.id,
				questionEntity.subject,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.viewCount,
				questionEntity.createdAt,
				questionEntity.modifiedAt)
			.orderBy(getOrderBy(searchCondition))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(questionEntity.count())
			.from(questionEntity)
			.where(subjectAndContentContains(searchCondition), categoryIdEquals(searchCondition),
				usernameEquals(searchCondition));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	public QuestionDetailResponse findById(Long questionId, Long memberId) {
		QuestionDetailResponse questionDetailResponse = queryFactory.selectDistinct(new QQuestionDetailResponse(
				questionEntity.id,
				questionEntity.subject,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.viewCount,
				new QCategoryResponse(questionEntity.category.id,
					getCategoryDisplayName()),
				questionEntity.createdAt,
				questionEntity.modifiedAt,
				voterEntity.countDistinct(),
				commentEntity.countDistinct(),
				questionEntity.author.id.eq(memberId),
				voterEntity.memberEntity.id.eq(memberId)
			))
			.from(questionEntity)
			.leftJoin(questionEntity.author)
			.leftJoin(questionEntity.category)
			.leftJoin(questionEntity.voterEntitySet, voterEntity)
			.leftJoin(questionEntity.commentEntityList, commentEntity)
			.where(questionEntity.id.eq(questionId))
			.groupBy(
				questionEntity.id,
				questionEntity.content,
				questionEntity.author.username,
				questionEntity.createdAt,
				questionEntity.modifiedAt,
				voterEntity.memberEntity.id
			)
			.fetchOne();

		return questionDetailResponse;
	}

	private StringExpression getCategoryDisplayName() {
		return new CaseBuilder()
			.when(questionEntity.category.categoryName.eq(CategoryName.QUESTION_BOARD))
			.then(CategoryName.QUESTION_BOARD.getCategoryDisplayName())
			.when(questionEntity.category.categoryName.eq(CategoryName.LECTURE_BOARD))
			.then(CategoryName.LECTURE_BOARD.getCategoryDisplayName())
			.when(questionEntity.category.categoryName.eq(CategoryName.FREE_BOARD))
			.then(CategoryName.FREE_BOARD.getCategoryDisplayName())
			.otherwise(new String());
	}

	public OrderSpecifier<?> getOrderBy(SearchCondition searchCondition) {
		// 기본 정렬 방식 설정
		Order queryOrder = Order.ASC.toString().equalsIgnoreCase(searchCondition.order()) ? Order.ASC : Order.DESC;

		// 정렬 필드를 매핑
		Map<String, ComparableExpressionBase<?>> fieldMap = Map.of(
			"modifiedAt", questionEntity.modifiedAt,
			"voter", questionEntity.voterEntitySet.size()
		);

		ComparableExpressionBase<?> sortField =
			StringUtils.hasText(searchCondition.sort()) && fieldMap.containsKey(searchCondition.sort())
				? fieldMap.get(searchCondition.sort())
				: questionEntity.createdAt;

		return new OrderSpecifier<>(queryOrder, sortField);
	}

	public BooleanExpression subjectAndContentContains(SearchCondition searchCondition) {
		return StringUtils.hasText(searchCondition.kw()) ?
			questionEntity.subject.contains(searchCondition.kw())
				.or(questionEntity.content.contains(searchCondition.kw())) :
			null;
	}

	private BooleanExpression categoryIdEquals(SearchCondition searchCondition) {
		return searchCondition.categoryId() != null ? questionEntity.category.id.eq(searchCondition.categoryId()) :
			null;
	}

	private BooleanExpression usernameEquals(SearchCondition searchCondition) {
		return StringUtils.hasText(searchCondition.username()) ?
			questionEntity.author.username.eq(searchCondition.username()) : null;
	}

}

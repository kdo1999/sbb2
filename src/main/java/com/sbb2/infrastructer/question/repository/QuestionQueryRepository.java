package com.sbb2.infrastructer.question.repository;


import static com.sbb2.infrastructer.question.entity.QQuestionEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sbb2.question.domain.QQuestionPageResponse;
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
				questionEntity.modifiedAt))
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

	public BooleanExpression subjectAndContentContains(String kw) {
		return StringUtils.hasText(kw) ? questionEntity.subject.contains(kw).or(questionEntity.content.contains(kw)) : null;
	}
}

package com.sbb2.infrastructer.comment.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.common.config.QuerydslConfig;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import(QuerydslConfig.class)
@DirtiesContext
public class CommentRepositoryTest {
	private final CommentRepository commentRepository;

	@Autowired
	public CommentRepositoryTest(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}
}

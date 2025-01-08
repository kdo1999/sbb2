package com.sbb2.infrastructer.category.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.category.domain.Category;
import com.sbb2.common.config.QuerydslConfig;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Slf4j
public class CategoryRepositoryTest {
	private final CategoryRepository categoryRepository;

	@Autowired
	public CategoryRepositoryTest(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@BeforeAll
	void setUp() {
		Category category1 = Category.builder()
			.categoryName("question_board")
			.build();

		Category category2 = Category.builder()
			.categoryName("lecture_board")
			.build();

		Category category3 = Category.builder()
			.categoryName("free_board")
			.build();

		categoryRepository.save(category1);
		categoryRepository.save(category2);
		categoryRepository.save(category3);
	}

	@DisplayName("카테고리 저장 성공 테스트")
	@Test
	void save_category_success() {
		//given
		String categoryName = "test_board";

		Category givenCategory = Category.builder()
			.categoryName(categoryName)
			.build();

		//when
		Category savedCategory = categoryRepository.save(givenCategory);

		//then
		Assertions.assertThat(savedCategory.id()).isNotNull();
		Assertions.assertThat(savedCategory.categoryName()).isEqualTo(categoryName);
	}

	@DisplayName("카테고리 ID로 조회 성공 테스트")
	@Test
	void find_category_id_success() {
		//given
		Long givenId = 1L;
		String categoryName = "question_board";

		Category givenCategory = Category.builder()
			.id(givenId)
			.categoryName(categoryName)
			.build();

		//when
		Category savedCategory = categoryRepository.findById(givenId).get();

		//then
		Assertions.assertThat(savedCategory).isEqualTo(givenCategory);
	}

	@DisplayName("카테고리 이름으로 조회 성공 테스트")
	@Test
	void find_category_categoryName_success() {
		//given
		Long givenId = 1L;
		String categoryName = "question_board";

		Category givenCategory = Category.builder()
			.id(givenId)
			.categoryName(categoryName)
			.build();

		//when
		Category savedCategory = categoryRepository.findByCategoryName(categoryName).get();

		//then
		Assertions.assertThat(savedCategory).isEqualTo(givenCategory);
	}
}

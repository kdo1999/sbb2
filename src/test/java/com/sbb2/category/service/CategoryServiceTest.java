package com.sbb2.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.category.domain.Category;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
	@Mock
	private CategoryRepository categoryRepository;
	private CategoryService categoryService;

	@BeforeEach
	void setUp() {
		categoryService = new CategoryServiceImpl(categoryRepository);
	}

	@DisplayName("카테고리 ID로 조회 성공 테스트")
	@Test
	void save_category_success() {
	    //given
		Long givenCategoryId = 1L;
		CategoryName givenCategoryName = CategoryName.QUESTION_BOARD;

		Category givenCategory = Category.builder()
			.id(givenCategoryId)
			.categoryName(givenCategoryName)
			.build();

		given(categoryRepository.findById(givenCategoryId))
			.willReturn(Optional.of(givenCategory));

		//when
		Category findCategory = categoryService.findById(givenCategoryId);

		//then
		assertThat(findCategory).isEqualTo(givenCategory);
	}
}

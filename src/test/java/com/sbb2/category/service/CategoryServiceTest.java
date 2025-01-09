package com.sbb2.category.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.category.domain.Category;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;
import com.sbb2.category.service.response.CategoryResponse;
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
	void find_category_id_success() {
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

	@DisplayName("카테고리 ID로 조회 실패 테스트")
	@Test
	void find_category_id_not_found_fail() {
		//given
		Long givenCategoryId = 1L;

		given(categoryRepository.findById(givenCategoryId))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> categoryService.findById(givenCategoryId))
			.isInstanceOf(CategoryBusinessLogicException.class)
			.hasMessage(CategoryErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("카테고리 전체 조회 성공 테스트")
	@Test
	void findAll_category_success() {
		//given
		Long givenCategoryId1 = 1L;
		CategoryName givenCategoryName1 = CategoryName.QUESTION_BOARD;

		Long givenCategoryId2 = 2L;
		CategoryName givenCategoryName2 = CategoryName.LECTURE_BOARD;

		Category givenCategory1 = Category.builder()
			.id(givenCategoryId1)
			.categoryName(givenCategoryName1)
			.build();

		Category givenCategory2 = Category.builder()
			.id(givenCategoryId2)
			.categoryName(givenCategoryName2)
			.build();

		List<Category> givenCategoryList = List.of(givenCategory1, givenCategory2);

		List<CategoryResponse> categoryResponseList = givenCategoryList.stream()
			.map(category -> CategoryResponse.builder()
				.categoryName(category.categoryName().toString())
				.categoryDisplayName(category.categoryName().getCategoryDisplayName())
				.build())
			.toList();

		given(categoryRepository.findAll())
			.willReturn(givenCategoryList);

		//when
		List<CategoryResponse> findCategoryList = categoryService.findAll();

		//then
		assertThat(findCategoryList).isEqualTo(categoryResponseList);
	}
}

package com.sbb2.category.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sbb2.category.domain.Category;
import com.sbb2.category.service.CategoryService;
import com.sbb2.category.service.response.CategoryResponse;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.infrastructer.category.entity.CategoryName;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
	@Mock
	private CategoryService categoryService;
	private CategoryController categoryController;

	@BeforeEach
	void setUp() {
		categoryController = new CategoryController(categoryService);
	}

	@DisplayName("카테고리 전체 조회 성공 테스트")
	@Test
	void findAll_success() {
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
				.categoryId(category.id())
				.categoryDisplayName(category.categoryName().getCategoryDisplayName())
				.build())
			.toList();

		given(categoryService.findAll()).willReturn(categoryResponseList);

		//when
		ResponseEntity<GenericResponse<List<CategoryResponse>>> result = categoryController.findAll();

		//then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getData()).isEqualTo(categoryResponseList);
		verify(categoryService, times(1)).findAll();
	}
}

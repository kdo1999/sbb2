package com.sbb2.category.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.category.domain.Category;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;
import com.sbb2.category.service.response.CategoryResponse;
import com.sbb2.infrastructer.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	public Category findById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryBusinessLogicException(CategoryErrorCode.NOT_FOUND));
	}

	@Override
	public List<CategoryResponse> findAll() {
		List<Category> findCategoryList = categoryRepository.findAll();

		List<CategoryResponse> categoryResponseList = findCategoryList.stream().map((category) -> CategoryResponse.builder()
				.categoryId(category.id())
				.categoryDisplayName(category.categoryName().getCategoryDisplayName())
				.build())
			.toList();

		return categoryResponseList;
	}
}

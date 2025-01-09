package com.sbb2.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sbb2.category.domain.Category;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;
import com.sbb2.infrastructer.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	public Category findById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new CategoryBusinessLogicException(CategoryErrorCode.NOT_FOUND));
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}
}

package com.sbb2.infrastructer.category.repository;

import java.util.Optional;

import com.sbb2.category.domain.Category;

public interface CategoryRepository {
	Category save(Category category);

	Optional<Category> findById(Long categoryId);

	Optional<Category> findByCategoryName(String categoryName);

	void deleteById(Long categoryId);
}

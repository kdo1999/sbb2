package com.sbb2.infrastructer.category.repository;

import java.util.Optional;

import com.sbb2.category.domain.Category;
import com.sbb2.infrastructer.category.entity.CategoryName;

public interface CategoryRepository {
	Category save(Category category);

	Optional<Category> findById(Long categoryId);

	Optional<Category> findByCategoryName(CategoryName categoryName);

	void deleteById(Long categoryId);
}

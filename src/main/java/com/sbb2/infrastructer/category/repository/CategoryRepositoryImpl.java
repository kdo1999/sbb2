package com.sbb2.infrastructer.category.repository;

import org.springframework.stereotype.Repository;

import com.sbb2.category.domain.Category;
import com.sbb2.infrastructer.category.entity.CategoryEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public Category save(Category category) {
		return categoryJpaRepository.save(CategoryEntity.from(category)).toModel();
	}
}


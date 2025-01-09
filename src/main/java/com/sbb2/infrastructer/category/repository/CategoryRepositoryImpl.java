package com.sbb2.infrastructer.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sbb2.category.domain.Category;
import com.sbb2.infrastructer.category.entity.CategoryEntity;
import com.sbb2.infrastructer.category.entity.CategoryName;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
	private final CategoryJpaRepository categoryJpaRepository;

	@Override
	public Category save(Category category) {
		return categoryJpaRepository.save(CategoryEntity.from(category)).toModel();
	}

	@Override
	public Optional<Category> findById(Long categoryId) {
		return categoryJpaRepository.findById(categoryId).map(CategoryEntity::toModel);
	}

	@Override
	public Optional<Category> findByCategoryName(CategoryName categoryName) {
		return categoryJpaRepository.findByCategoryName(categoryName).map(CategoryEntity::toModel);
	}

	@Override
	public void deleteById(Long categoryId) {
		categoryJpaRepository.deleteById(categoryId);
	}

	@Override
	public List<Category> findAll() {
		return categoryJpaRepository.findAll().stream()
			.map(CategoryEntity::toModel)
			.toList();
	}
}


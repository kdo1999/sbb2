package com.sbb2.infrastructer.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.category.entity.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
	Optional<CategoryEntity> findByCategoryName(String categoryName);
}

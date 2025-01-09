package com.sbb2.infrastructer.category.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.category.entity.CategoryEntity;
import com.sbb2.infrastructer.category.entity.CategoryName;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
	Optional<CategoryEntity> findByCategoryName(CategoryName categoryName);
}

package com.sbb2.infrastructer.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.category.entity.CategoryEntity;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {
}

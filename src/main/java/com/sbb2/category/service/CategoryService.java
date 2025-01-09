package com.sbb2.category.service;

import java.util.List;

import com.sbb2.category.domain.Category;

public interface CategoryService {
	Category findById(Long id);

	List<Category> findAll();
}

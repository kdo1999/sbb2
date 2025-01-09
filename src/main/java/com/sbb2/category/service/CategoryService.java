package com.sbb2.category.service;

import java.util.List;

import com.sbb2.category.domain.Category;
import com.sbb2.category.service.response.CategoryResponse;

public interface CategoryService {
	Category findById(Long id);

	List<CategoryResponse> findAll();
}

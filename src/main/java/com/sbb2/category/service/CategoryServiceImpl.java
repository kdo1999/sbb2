package com.sbb2.category.service;

import org.springframework.stereotype.Service;

import com.sbb2.category.domain.Category;
import com.sbb2.infrastructer.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	public Category findById(Long id) {
		return categoryRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("해당 질문이 존재하지 않습니다."));
	}
}

package com.sbb2.category.service;

import org.springframework.stereotype.Service;

import com.sbb2.infrastructer.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;
}

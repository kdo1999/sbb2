package com.sbb2.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.category.service.CategoryService;

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {
	@Mock
	private CategoryService categoryService;
	private CategoryController categoryController;

	@BeforeEach
	void setUp() {
		categoryController = new CategoryController(categoryService);
	}
}

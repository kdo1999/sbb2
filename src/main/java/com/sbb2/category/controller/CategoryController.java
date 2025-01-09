package com.sbb2.category.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.category.domain.Category;
import com.sbb2.category.service.CategoryService;
import com.sbb2.common.response.GenericResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping
	public ResponseEntity<GenericResponse<List<Category>>> findAll() {
		List<Category> categoryList = categoryService.findAll();

		return ResponseEntity.ok()
			.body(GenericResponse.of(categoryList));
	}
}

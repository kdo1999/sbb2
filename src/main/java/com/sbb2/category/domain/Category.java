package com.sbb2.category.domain;

import com.sbb2.infrastructer.category.entity.CategoryName;

import lombok.Builder;

public record Category(Long id, CategoryName categoryName) {

	@Builder
	public Category(Long id, CategoryName categoryName) {
		this.id = id;
		this.categoryName = categoryName;
	}
}

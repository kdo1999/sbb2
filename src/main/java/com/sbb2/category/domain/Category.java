package com.sbb2.category.domain;

import lombok.Builder;

public record Category(Long id, String categoryName) {

	@Builder
	public Category(Long id, String categoryName) {
		this.id = id;
		this.categoryName = categoryName;
	}
}

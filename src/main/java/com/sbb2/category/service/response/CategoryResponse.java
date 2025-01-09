package com.sbb2.category.service.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public record CategoryResponse(String categoryName, String categoryDisplayName) {

	@QueryProjection
	@Builder
	public CategoryResponse(String categoryName, String categoryDisplayName) {
		this.categoryName = categoryName;
		this.categoryDisplayName = categoryDisplayName;
	}
}

package com.sbb2.category.service.response;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;

public record CategoryResponse(Long categoryId, String categoryDisplayName) {

	@QueryProjection
	@Builder
	public CategoryResponse(Long categoryId, String categoryDisplayName) {
		this.categoryId = categoryId;
		this.categoryDisplayName = categoryDisplayName;
	}
}

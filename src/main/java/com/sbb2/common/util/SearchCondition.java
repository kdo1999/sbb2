package com.sbb2.common.util;

import lombok.Builder;

public record SearchCondition(String kw, String order, String categoryName, String sort, Integer pageNum) {

	@Builder
	public SearchCondition(String kw, String order, String categoryName, String sort, Integer pageNum) {
		this.kw = kw;
		this.order = order;
		this.categoryName = categoryName;
		this.sort = sort;
		this.pageNum = pageNum;
	}
}

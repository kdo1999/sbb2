package com.sbb2.common.util;

import lombok.Builder;

public record SearchCondition(String kw, String order, Long categoryId, String sort, Integer pageNum, String username) {

	@Builder
	public SearchCondition(String kw, String order, Long categoryId, String sort, Integer pageNum, String username) {
		this.kw = kw;
		this.order = order;
		this.categoryId = categoryId;
		this.sort = sort;
		this.pageNum = pageNum;
		this.username = username;
	}
}

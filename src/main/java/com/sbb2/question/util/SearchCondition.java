package com.sbb2.question.util;

import lombok.Builder;

public record SearchCondition(String kw, String order, String sort, Integer pageNum) {

	@Builder
	public SearchCondition(String kw, String order, String sort, Integer pageNum) {
		this.kw = kw;
		this.order = order;
		this.sort = sort;
		this.pageNum = pageNum;
	}
}

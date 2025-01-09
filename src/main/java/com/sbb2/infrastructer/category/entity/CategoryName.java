package com.sbb2.infrastructer.category.entity;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum CategoryName {
	QUESTION_BOARD("질문 게시판"),
	LECTURE_BOARD("강의 게시판"),
	FREE_BOARD("자유 게시판");

	String categoryDisplayName;

	CategoryName(String categoryDisplayName) {
		this.categoryDisplayName = categoryDisplayName;
	}

	@JsonCreator
	public static CategoryName from(String param) {
		return Stream.of(CategoryName.values())
			.filter(categoryName -> categoryName.toString().equalsIgnoreCase(param))
			.findFirst()
			.orElse(null);
	}
}

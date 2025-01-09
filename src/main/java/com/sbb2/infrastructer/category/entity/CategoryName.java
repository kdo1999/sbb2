package com.sbb2.infrastructer.category.entity;

public enum CategoryName {
	QUESTION_BOARD("질문 게시판"),
	LECTURE_BOARD("강의 게시판"),
	FREE_BOARD("자유 게시판");

	String categoryDisplayName;

	CategoryName(String categoryDisplayName) {
		this.categoryDisplayName = categoryDisplayName;
	}
}

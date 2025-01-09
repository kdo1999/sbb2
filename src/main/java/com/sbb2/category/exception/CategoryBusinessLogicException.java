package com.sbb2.category.exception;

import org.springframework.http.HttpStatus;

public class CategoryBusinessLogicException extends RuntimeException {
	private final CategoryErrorCode categoryErrorCode;

	public CategoryBusinessLogicException(CategoryErrorCode categoryErrorCode) {
		super(categoryErrorCode.getMessage());
		this.categoryErrorCode = categoryErrorCode;
	}

	public HttpStatus getStatus() {
		return categoryErrorCode.getHttpStatus();
	}
}

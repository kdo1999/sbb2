package com.sbb2.category.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CategoryErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 ID의 카테고리가 존재하지 않습니다."),
	UNKNOWN_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류입니다.");

	HttpStatus httpStatus;
	String message;

	CategoryErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

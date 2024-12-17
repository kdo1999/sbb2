package com.sbb2.question.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum QuestionErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 질문이 존재하지 않습니다.");

	HttpStatus httpStatus;
	String message;

	QuestionErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

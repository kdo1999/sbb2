package com.sbb2.answer.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum AnswerErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 답변이 존재하지 않습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "답변 작성자만 사용할 수 있습니다.");

	HttpStatus httpstatus;
	String message;

	AnswerErrorCode(HttpStatus httpstatus, String message) {
		this.httpstatus = httpstatus;
		this.message = message;
	}
}

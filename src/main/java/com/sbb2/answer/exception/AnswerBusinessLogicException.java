package com.sbb2.answer.exception;

import org.springframework.http.HttpStatus;

public class AnswerBusinessLogicException extends RuntimeException {
	private AnswerErrorCode answerErrorCode;

	public AnswerBusinessLogicException(AnswerErrorCode answerErrorCode) {
		super(answerErrorCode.getMessage());
		this.answerErrorCode = answerErrorCode;
	}

	public HttpStatus getStatus() {
		return answerErrorCode.getHttpstatus();
	}
}

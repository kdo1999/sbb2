package com.sbb2.question.exception;

import org.springframework.http.HttpStatus;

public class QuestionBusinessLogicException extends RuntimeException {

	private QuestionErrorCode questionErrorCode;

	public QuestionBusinessLogicException(QuestionErrorCode questionErrorCode) {
		super(questionErrorCode.getMessage());
		this.questionErrorCode = questionErrorCode;
	}

	public HttpStatus getStatus() {
		return questionErrorCode.getHttpStatus();
	}
}

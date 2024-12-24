package com.sbb2.common.jwt.exception;

import org.springframework.http.HttpStatus;


public class JwtTokenBusinessLogicException extends RuntimeException {
	private final JwtTokenErrorCode errorCode;

	public JwtTokenBusinessLogicException(Throwable cause, JwtTokenErrorCode errorCode) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public JwtTokenBusinessLogicException(JwtTokenErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public HttpStatus getStatus() {
		return errorCode.getHttpStatus();
	}
}

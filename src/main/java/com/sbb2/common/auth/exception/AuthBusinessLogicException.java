package com.sbb2.common.auth.exception;

import org.springframework.http.HttpStatus;

public class AuthBusinessLogicException extends RuntimeException {
	private final AuthErrorCode authErrorCode;

	public AuthBusinessLogicException(AuthErrorCode authErrorCode) {
		super(authErrorCode.getMessage());
		this.authErrorCode = authErrorCode;
	}

	public HttpStatus getStatus() {
		return authErrorCode.getHttpStatus();
	}
}

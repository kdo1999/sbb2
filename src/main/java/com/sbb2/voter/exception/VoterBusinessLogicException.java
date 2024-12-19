package com.sbb2.voter.exception;

import org.springframework.http.HttpStatus;

public class VoterBusinessLogicException extends RuntimeException {
	private final VoterErrorCode voterErrorCode;

	public VoterBusinessLogicException(VoterErrorCode voterErrorCode) {
		super(voterErrorCode.getMessage());
		this.voterErrorCode = voterErrorCode;
	}

	public HttpStatus getStatus() {
		return voterErrorCode.getHttpStatus();
	}
}

package com.sbb2.voter.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum VoterErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 추천이 존재하지 않습니다."),
	DUPLICATE_VOTER(HttpStatus.BAD_REQUEST, "중복 추천은 허용하지 않습니다.");

	HttpStatus httpStatus;
	String message;

	VoterErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

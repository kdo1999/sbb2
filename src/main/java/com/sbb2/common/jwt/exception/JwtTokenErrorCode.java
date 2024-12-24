package com.sbb2.common.jwt.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum JwtTokenErrorCode {

	EXPIRED(HttpStatus.UNAUTHORIZED, "JWT 만료 시간이 지났습니다."),
	SIGNATURE(HttpStatus.UNAUTHORIZED, "JWT 서명이 올바르지 않습니다."),
	PREMATURE(HttpStatus.FORBIDDEN, "유효시간이 아직 되지 않았습니다."),
	MALFORMED(HttpStatus.BAD_REQUEST, "JWT 형식이 올바르지 않습니다"),
	NOT_VALID(HttpStatus.BAD_REQUEST, "저장된 RefreshToken이랑 일치하지 않습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token이 아닙니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 아닙니다."),
	NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "접근이 금지된 토큰입니다."),
	BAD_REQUEST(HttpStatus.UNAUTHORIZED, "JWT 처리중 예외가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	JwtTokenErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

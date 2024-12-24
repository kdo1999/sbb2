package com.sbb2.common.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
	USERNAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 아이디가 존재하지 않습니다."),
	BAD_CREDENTIALS(HttpStatus.BAD_REQUEST, "비밀번호를 잘못 입력했습니다."),
	NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "탈퇴되었거나 계정이 잠겼습니다."),
	SOCIAL_NOT_SIGNUP(HttpStatus.UNAUTHORIZED, "해당 소셜 계정이 없습니다."),
	NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "닉네임이 중복입니다."),
	EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "해당 이메일은 이미 가입이 돼있습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

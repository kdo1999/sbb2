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
	PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
	EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "해당 이메일은 이미 가입이 돼있습니다."),
	EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST, "계정 이메일과 일치하지 않습니다."),
	UNKNOWN_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류입니다."),
	CERTIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증번호가 존재하지 않습니다."),
	CERTIFICATION_CODE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
	VERIFY_TYPE_NOT_MATCH(HttpStatus.UNAUTHORIZED, "인증 타입이 일치하지 않습니다."),
	TOOMANY_RESEND_ATTEMPTS(HttpStatus.BAD_REQUEST, "5회 이상 시도하셨습니다. 잠시후 다시 시도해주세요.");

	private final HttpStatus httpStatus;
	private final String message;

	AuthErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

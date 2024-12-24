package com.sbb2.member.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum MemberErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 회원이 존재하지 않습니다."),
	EXISTS_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일 입니다."),
	EXISTS_USERNAME(HttpStatus.BAD_REQUEST, "중복된 회원 아이디 입니다.");

	HttpStatus httpstatus;
	String message;

	MemberErrorCode(HttpStatus httpstatus, String message) {
		this.httpstatus = httpstatus;
		this.message = message;
	}
}

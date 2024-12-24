package com.sbb2.member.exception;

import org.springframework.http.HttpStatus;

public class MemberBusinessLoginException extends RuntimeException {
	private MemberErrorCode memberErrorCode;

	public MemberBusinessLoginException(MemberErrorCode memberErrorCode) {
		super(memberErrorCode.getMessage());
		this.memberErrorCode = memberErrorCode;
	}

	public HttpStatus getStatus() {
		return memberErrorCode.getHttpstatus();
	}
}

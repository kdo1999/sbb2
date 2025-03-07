package com.sbb2.comment.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum CommentErrorCode {
	NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 댓글이 존재하지 않습니다."),
	NOT_SUPPORT(HttpStatus.BAD_REQUEST, "지원하지 않는 댓글 부모 타입입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "댓글 작성자만 사용할 수 있습니다."),
	UNKNOWN_SERVER(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 서버 오류입니다.");

	HttpStatus httpStatus;
	String message;

	CommentErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}

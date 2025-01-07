package com.sbb2.comment.exception;

import org.springframework.http.HttpStatus;

public class CommentBusinessLogicException extends RuntimeException {
	private final CommentErrorCode commentErrorCode;

	public CommentBusinessLogicException(CommentErrorCode commentErrorCode) {
		super(commentErrorCode.getMessage());
		this.commentErrorCode = commentErrorCode;
	}

	public HttpStatus getStatus() {
		return commentErrorCode.getHttpStatus();
	}
}

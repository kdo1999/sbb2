package com.sbb2.comment.advice;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sbb2.comment.exception.CommentBusinessLogicException;
import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.sbb2.comment.controller")
public class CommentControllerAdvice {

	@ExceptionHandler(CommentBusinessLogicException.class)
	public ResponseEntity<HttpErrorInfo> handlerCommentBusinessLogicException(CommentBusinessLogicException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(ex.getStatus().value())
			.body(createHttpErrorInfo(ex.getStatus().value(), request.getRequestURI(), ex.getMessage(), ErrorDetail.of(
				Collections.emptyList())));
	}

	protected HttpErrorInfo createHttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail) {
		HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(code, path, message, errorDetail);

		log.error("CommentControllerAdvice = {}", httpErrorInfo);

		return httpErrorInfo;
	}
}

package com.sbb2.question.advice;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;
import com.sbb2.question.exception.QuestionBusinessLogicException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.sbb2.question.controller")
public class QuestionControllerAdvice {

	@ExceptionHandler(QuestionBusinessLogicException.class)
	public ResponseEntity<HttpErrorInfo> handlerQuestionBizLogicException(QuestionBusinessLogicException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(ex.getStatus().value())
			.body(createHttpErrorInfo(ex.getStatus().value(), request.getRequestURI(), ex.getMessage(), ErrorDetail.of(
				Collections.emptyList())));
	}

	protected HttpErrorInfo createHttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail) {
		HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(code, path, message, errorDetail);

		log.error("QuestionControllerAdvice = {}", httpErrorInfo);

		return httpErrorInfo;
	}
}

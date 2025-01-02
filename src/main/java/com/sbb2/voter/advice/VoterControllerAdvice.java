package com.sbb2.voter.advice;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.voter.exception.VoterBusinessLogicException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.sbb2.voter.controller")
public class VoterControllerAdvice {
	@ExceptionHandler(QuestionBusinessLogicException.class)
	public ResponseEntity<HttpErrorInfo> handlerQuestionBizLogicException(QuestionBusinessLogicException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(ex.getStatus().value())
			.body(createHttpErrorInfo(ex.getStatus().value(), request.getRequestURI(), ex.getMessage(), ErrorDetail.of(
				Collections.emptyList())));
	}

	@ExceptionHandler(AnswerBusinessLogicException.class)
	public ResponseEntity<HttpErrorInfo> handlerAnswerBizLogicException(AnswerBusinessLogicException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(ex.getStatus().value())
			.body(createHttpErrorInfo(ex.getStatus().value(), request.getRequestURI(), ex.getMessage(), ErrorDetail.of(
				Collections.emptyList())));
	}

	@ExceptionHandler(VoterBusinessLogicException.class)
	public ResponseEntity<HttpErrorInfo> handlerVoterBizLogicException(VoterBusinessLogicException ex,
		HttpServletRequest request) {
		return ResponseEntity.status(ex.getStatus().value())
			.body(createHttpErrorInfo(ex.getStatus().value(), request.getRequestURI(), ex.getMessage(), ErrorDetail.of(
				Collections.emptyList())));
	}



	protected HttpErrorInfo createHttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail) {
		HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(code, path, message, errorDetail);

		log.error("VoterControllerAdvice = {}", httpErrorInfo);

		return httpErrorInfo;
	}
}

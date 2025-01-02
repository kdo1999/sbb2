package com.sbb2.common.advice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sbb2.common.httpError.Error;
import com.sbb2.common.httpError.ErrorDetail;
import com.sbb2.common.httpError.HttpErrorInfo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class BaseControllerAdvice {
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<HttpErrorInfo> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex,
		HttpServletRequest request) {
		BindingResult bindingResult = ex.getBindingResult();
		List<Error> errors = new ArrayList<>();

		for (FieldError error : bindingResult.getFieldErrors()) {
			Error customError = Error.of("field", error.getField(), error.getDefaultMessage(), "");

			errors.add(customError);
		}

    for (ObjectError globalError : bindingResult.getGlobalErrors()) {
        Error customError = Error.of(
                "global",
                globalError.getObjectName(),
                globalError.getDefaultMessage(),
                ""
        );
        errors.add(customError);
    }

		return ResponseEntity.status(ex.getStatusCode().value())
			.body(createHttpErrorInfo(ex.getStatusCode().value(), request.getRequestURI(), "유효하지 않은 값 입니다.",
				ErrorDetail.of(errors)));
	}

	@ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpErrorInfo> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
		List<Error> errors = new ArrayList<>();

		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			errors.add(Error.of(
                "global",
                constraintViolation.getPropertyPath().toString(),
                "Invalid value",
                constraintViolation.getMessage()
            ));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(createHttpErrorInfo(HttpStatus.BAD_REQUEST.value(), request.getRequestURI(), ex.getMessage(),
				ErrorDetail.of(errors))
			);
    }

	protected HttpErrorInfo createHttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail) {
		HttpErrorInfo httpErrorInfo = HttpErrorInfo.of(code, path, message, errorDetail);

		log.error("BaseControllerAdvice = {}", httpErrorInfo);

		return httpErrorInfo;
	}
}

package com.sbb2.common.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sbb2.common.validation.validator.ValidUsernameValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ValidUsernameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
	String message() default "유효하지 않은 회원 이름 입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

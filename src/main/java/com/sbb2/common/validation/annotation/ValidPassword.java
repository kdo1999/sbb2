package com.sbb2.common.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sbb2.common.validation.validator.ValidPasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
	String message() default "공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

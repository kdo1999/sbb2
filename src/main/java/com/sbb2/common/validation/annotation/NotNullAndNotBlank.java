package com.sbb2.common.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sbb2.common.validation.validator.NotNullAndNotBlankValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * String 타입에 유효성 검사 <br>
 * Null일 경우 통과 <br>
 * Null이 아닐경우 공백이 없다면 통과 <br>
 *
 * */
@Constraint(validatedBy = NotNullAndNotBlankValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullAndNotBlank {
	String message() default "공백은 허용하지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}

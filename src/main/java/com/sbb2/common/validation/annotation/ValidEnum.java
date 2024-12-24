package com.sbb2.common.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sbb2.common.validation.validator.EnumValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Enum 해당 값이 있는지 유효성 검사 <br>
 * nullable true로 설정할 시 null 허용
 *
 * @author : Kim Dong O
 * @fileName : ValidEnum
 * */
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEnum {
	String message() default "요청 값이 유효하지 않습니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	Class<? extends Enum<?>> enumClass();

	boolean nullable() default false;
}

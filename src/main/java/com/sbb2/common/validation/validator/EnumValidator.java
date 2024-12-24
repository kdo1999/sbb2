package com.sbb2.common.validation.validator;

import java.util.Arrays;

import com.sbb2.common.validation.annotation.ValidEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum> {
	private ValidEnum validEnum;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		this.validEnum = constraintAnnotation;
	}

	@Override
	public boolean isValid(Enum value, ConstraintValidatorContext context) {
		boolean result = false;
		// nullable이 true, value가 null일 때
		if (this.validEnum.nullable() && value == null) {
			return result = true;
		}

		Object[] enumValues = this.validEnum.enumClass().getEnumConstants();

		if (enumValues != null) {
			result = Arrays.stream(enumValues)
				.anyMatch(enumValue -> value == enumValue);
		}

		return result;
	}
}

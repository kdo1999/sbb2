package com.sbb2.common.validation.validator;

import java.util.Arrays;

import com.sbb2.common.validation.annotation.ValidStringEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidStringEnumValidator implements ConstraintValidator<ValidStringEnum, String> {
	private ValidStringEnum validStringEnum;

	@Override
	public void initialize(ValidStringEnum constraintAnnotation) {
		this.validStringEnum = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		boolean result = false;
		// nullable이 true, value가 null일 때
		if (this.validStringEnum.nullable() && value == null) {
			return result = true;
		}

		Object[] enumValues = this.validStringEnum.enumClass().getEnumConstants();

		if (enumValues != null) {
			result = Arrays.stream(enumValues)
				.anyMatch(enumValue -> enumValue.toString().equalsIgnoreCase(value));
		}

		return result;
	}
}

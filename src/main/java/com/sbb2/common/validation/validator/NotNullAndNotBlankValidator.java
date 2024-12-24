package com.sbb2.common.validation.validator;

import com.sbb2.common.validation.annotation.NotNullAndNotBlank;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotNullAndNotBlankValidator implements ConstraintValidator<NotNullAndNotBlank, String> {

	private NotNullAndNotBlank notNullAndNotBlank;

	@Override
	public void initialize(NotNullAndNotBlank constraintAnnotation) {
		this.notNullAndNotBlank = constraintAnnotation;
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		//value가 null이 아니면서 공백이 아닐 때
		return value != null && !value.isBlank();
	}
}

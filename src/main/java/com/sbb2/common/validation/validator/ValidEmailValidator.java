package com.sbb2.common.validation.validator;

import java.util.regex.Pattern;

import com.sbb2.common.validation.annotation.ValidEmail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {
	private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
	private ValidEmail validEmail;

	@Override
	public void initialize(ValidEmail constraintAnnotation) {
		this.validEmail = constraintAnnotation;
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
		return Pattern.matches(EMAIL_REGEX, email);
	}
}

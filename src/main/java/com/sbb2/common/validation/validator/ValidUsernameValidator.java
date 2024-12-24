package com.sbb2.common.validation.validator;

import java.util.regex.Pattern;

import com.sbb2.common.validation.annotation.ValidUsername;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
	private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,}$";
	private ValidUsername validUsername;

	@Override
	public void initialize(ValidUsername constraintAnnotation) {
		this.validUsername = constraintAnnotation;
	}

	@Override
	public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
		return Pattern.matches(NICKNAME_REGEX, nickname);
	}
}

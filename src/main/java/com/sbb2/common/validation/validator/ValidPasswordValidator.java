package com.sbb2.common.validation.validator;

import java.util.regex.Pattern;

import com.sbb2.common.validation.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
	private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9])[^\s]{8,20}$";
	private ValidPassword validPassword;

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		this.validPassword = constraintAnnotation;
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		return (password != null) && Pattern.matches(PASSWORD_REGEX, password);
	}
}

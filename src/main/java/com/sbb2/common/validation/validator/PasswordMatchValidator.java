package com.sbb2.common.validation.validator;


import com.sbb2.common.validation.annotation.PasswordMatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordMatchable> {
	private PasswordMatch passwordMatch;

	@Override
	public boolean isValid(PasswordMatchable passwordMatchable, ConstraintValidatorContext constraintValidatorContext) {
		return (passwordMatchable.getPasswordCheck() != null) && (passwordMatchable.getPassword()
			.equals(passwordMatchable.getPasswordCheck()));
	}

	@Override
	public void initialize(PasswordMatch constraintAnnotation) {
		this.passwordMatch = constraintAnnotation;
	}
}

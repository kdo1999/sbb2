package com.sbb2.auth.controller.request;

import com.sbb2.common.validation.ValidationGroups;
import com.sbb2.common.validation.annotation.PasswordMatch;
import com.sbb2.common.validation.annotation.ValidPassword;
import com.sbb2.common.validation.validator.PasswordMatchable;

import lombok.Builder;

@PasswordMatch(groups = ValidationGroups.PatternGroup.class)
public record PasswordChangeRequest(
	@ValidPassword(groups = ValidationGroups.PatternGroup.class) String originalPassword,

	@ValidPassword(groups = ValidationGroups.PatternGroup.class) String password,

	@ValidPassword(groups = ValidationGroups.PatternGroup.class) String passwordCheck) implements PasswordMatchable {

	@Builder
	public PasswordChangeRequest(String originalPassword, String password, String passwordCheck) {
		this.originalPassword = originalPassword;
		this.password = password;
		this.passwordCheck = passwordCheck;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
}

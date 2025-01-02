package com.sbb2.auth.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.common.validation.annotation.PasswordMatch;
import com.sbb2.common.validation.annotation.ValidEmail;
import com.sbb2.common.validation.annotation.ValidPassword;
import com.sbb2.common.validation.annotation.ValidUsername;
import com.sbb2.common.validation.validator.PasswordMatchable;

import lombok.Builder;

@PasswordMatch(groups = PatternGroup.class)
public record MemberEmailSignupRequest(
	@ValidEmail(groups = PatternGroup.class)
	String email,

	@ValidUsername(groups = PatternGroup.class)
	String username,

	@ValidPassword(groups = PatternGroup.class)
	String password,

	@ValidPassword(groups = PatternGroup.class)
	String passwordCheck
) implements PasswordMatchable {

	@Builder
	public MemberEmailSignupRequest(String email, String username, String password, String passwordCheck) {
		this.email = email;
		this.username = username;
		this.password = password;
		this.passwordCheck = passwordCheck;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPasswordCheck() {
		return this.passwordCheck();
	}
}

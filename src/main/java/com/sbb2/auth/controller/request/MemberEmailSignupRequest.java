package com.sbb2.auth.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.common.validation.annotation.ValidEmail;
import com.sbb2.common.validation.annotation.ValidPassword;
import com.sbb2.common.validation.annotation.ValidUsername;

import lombok.Builder;

public record MemberEmailSignupRequest(
	@ValidEmail(groups = PatternGroup.class)
	String email,

	@ValidUsername(groups = PatternGroup.class)
	String username,

	@ValidPassword(groups = PatternGroup.class)
	String password
) {

	@Builder
	public MemberEmailSignupRequest(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}
}

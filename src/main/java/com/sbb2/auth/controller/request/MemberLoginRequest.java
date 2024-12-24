package com.sbb2.auth.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.common.validation.annotation.ValidEmail;
import com.sbb2.common.validation.annotation.ValidPassword;

public record MemberLoginRequest(
	@ValidEmail(groups = PatternGroup.class)
	String email,

	@ValidPassword(groups = PatternGroup.class)
	String password) {
}

package com.sbb2.auth.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.auth.domain.VerifyType;
import com.sbb2.common.validation.annotation.ValidEnum;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
	@NotBlank(message = "이메일은 필수 항목입니다.", groups = NotBlankGroup.class)
	String email,
	@NotBlank(message = "인증 코드는 필수 항목입니다.", groups = NotBlankGroup.class)
	String certificationCode,
	@ValidEnum(message = "지원하지 않는 인증 유형입니다.", groups = ValidEnumGroup.class, enumClass = VerifyType.class)
	VerifyType verifyType) {
}

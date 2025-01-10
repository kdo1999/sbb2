package com.sbb2.auth.controller.request;

import static com.sbb2.common.validation.ValidationGroups.*;

import com.sbb2.auth.domain.VerifyType;
import com.sbb2.common.validation.annotation.ValidEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailSendRequestDto(
	@NotBlank(message = "이메일은 필수 항목입니다.", groups = NotBlankGroup.class)
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
		message = "유효하지 않은 이메일 입니다.", groups = PatternGroup.class)
	String email,
	@ValidEnum(message = "지원하지 않는 인증 유형입니다.", groups = ValidEnumGroup.class, enumClass = VerifyType.class)
	VerifyType verifyType) {
}

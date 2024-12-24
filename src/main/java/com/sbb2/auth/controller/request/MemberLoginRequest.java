package com.sbb2.auth.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberLoginRequest(
	@NotBlank(message = "이메일은 필수 항목입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
		message = "유효하지 않은 이메일 입니다.")
	String email,

	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9])[^\s]{8,20}$",
		message = "공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다.")
	String password) {
}

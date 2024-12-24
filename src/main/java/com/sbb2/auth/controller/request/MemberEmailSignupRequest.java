package com.sbb2.auth.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

public record MemberEmailSignupRequest(
	@NotBlank(message = "이메일은 필수 항목입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
		message = "유효하지 않은 이메일 입니다.")
	String email,

	@Pattern(
		regexp = "^[가-힣a-zA-Z0-9]{2,}$",
		message = "닉네임은 최소 2자 이상이어야 하며, 한글, 숫자, 영문만 포함할 수 있습니다."
	)
	@NotBlank(message = "닉네임은 필수 항목입니다.")
	String username,

	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9])[^\s]{8,20}$",
		message = "공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다.")
	String password
) {

	@Builder
	public MemberEmailSignupRequest(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}
}

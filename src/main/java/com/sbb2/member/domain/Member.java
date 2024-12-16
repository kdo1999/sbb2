package com.sbb2.member.domain;

import java.util.Objects;

import lombok.Builder;

public record Member(String email, String username, String password) {

	@Builder
	public Member(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}
}

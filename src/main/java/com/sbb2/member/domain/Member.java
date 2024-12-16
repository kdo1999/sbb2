package com.sbb2.member.domain;

import java.util.Objects;

import lombok.Builder;

public record Member(Long id, String email, String username, String password) {

	@Builder
	public Member(Long id, String email, String username, String password) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Member member = (Member)o;
		return Objects.equals(id, member.id) && Objects.equals(email, member.email)
			&& Objects.equals(username, member.username) && Objects.equals(password, member.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, email, username, password);
	}
}

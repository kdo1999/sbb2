package com.sbb2.member.domain;

import lombok.Builder;

public record Member(Long id, String email, String username, String password, MemberRole memberRole) {

	@Builder
	public Member(Long id, String email, String username, String password, MemberRole memberRole) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
		this.memberRole = memberRole;
	}

	public Member changePassword(String changePassword) {
		return Member.builder()
			.id(this.id)
			.email(this.email)
			.username(this.username)
			.password(changePassword)
			.memberRole(this.memberRole)
			.build();
	}
}

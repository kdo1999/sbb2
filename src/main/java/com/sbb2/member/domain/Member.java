package com.sbb2.member.domain;

import java.time.LocalDateTime;

import lombok.Builder;

public record Member(Long id, String email, String username, String password, MemberRole memberRole,
					 LoginType loginType, LocalDateTime createdAt, LocalDateTime modifiedAt) {

	@Builder
	public Member(Long id, String email, String username, String password, MemberRole memberRole, LoginType loginType,
		LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.email = email;
		this.username = username;
		this.password = password;
		this.memberRole = memberRole;
		this.loginType = loginType;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public Member changePassword(String changePassword) {
		return Member.builder()
			.id(this.id)
			.email(this.email)
			.username(this.username)
			.password(changePassword)
			.memberRole(this.memberRole)
			.loginType(this.loginType)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
	}
}

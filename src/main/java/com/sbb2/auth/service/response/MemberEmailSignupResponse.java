package com.sbb2.auth.service.response;

import com.sbb2.member.domain.MemberRole;

import lombok.Builder;

public record MemberEmailSignupResponse(String email, String username, MemberRole memberRole) {

	@Builder
	public MemberEmailSignupResponse(String email, String username, MemberRole memberRole) {
		this.email = email;
		this.username = username;
		this.memberRole = memberRole;
	}
}

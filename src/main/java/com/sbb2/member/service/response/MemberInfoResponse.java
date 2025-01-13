package com.sbb2.member.service.response;

import com.sbb2.member.domain.MemberRole;

import lombok.Builder;

public record MemberInfoResponse(String email, String username, MemberRole memberRole) {
	@Builder
	public MemberInfoResponse(String email, String username, MemberRole memberRole) {
		this.email = email;
		this.username = username;
		this.memberRole = memberRole;
	}
}

package com.sbb2.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.member.service.response.MemberInfoResponse;

@RestController
@RequestMapping("/api/v1/member")
public class MemberController {

	@GetMapping("/info")
	public ResponseEntity<GenericResponse<MemberInfoResponse>> info(@AuthenticationPrincipal MemberUserDetails memberUserDetails) {
		Member loginMember = memberUserDetails.getMember();

		MemberInfoResponse memberInfoResponse = MemberInfoResponse.builder()
			.email(loginMember.email())
			.username(loginMember.username())
			.memberRole(loginMember.memberRole())
			.build();

		return ResponseEntity.ok().body(GenericResponse.of(memberInfoResponse));
	}
}

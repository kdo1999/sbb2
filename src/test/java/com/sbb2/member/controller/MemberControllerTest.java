package com.sbb2.member.controller;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.LoginType;
import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;
import com.sbb2.member.service.response.MemberInfoResponse;

class MemberControllerTest {
	private MemberController memberController = new MemberController();

	@DisplayName("회원 정보 조회 성공 테스트")
	@Test
	void member_info_success() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.memberRole(MemberRole.USER)
			.loginType(LoginType.EMAIL)
			.password("testPassword")
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		MemberInfoResponse givenMemberInfoResponse = MemberInfoResponse.builder()
			.email(givenMember.email())
			.username(givenMember.username())
			.memberRole(givenMember.memberRole())
			.build();

		//when
		ResponseEntity<GenericResponse<MemberInfoResponse>> result = memberController.info(givenMemberUserDetails);

		//then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getData()).isEqualTo(givenMemberInfoResponse);

	}
}
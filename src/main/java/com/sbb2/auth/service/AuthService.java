package com.sbb2.auth.service;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.member.domain.Member;

public interface AuthService {
	MemberEmailSignupResponse signup(String email, String username, String password);
	MemberLoginResponse memberLogin(String email, String password);
	void passwordChange(String originalPassword, String changePassword, Member loginMember);
}

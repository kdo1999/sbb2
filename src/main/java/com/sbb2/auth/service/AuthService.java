package com.sbb2.auth.service;

import com.sbb2.auth.domain.VerifyType;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.member.domain.LoginType;
import com.sbb2.member.domain.Member;

public interface AuthService {
	MemberEmailSignupResponse signup(String email, String username, String password, LoginType loginType);
	MemberLoginResponse memberLogin(String email, String password);
	void passwordChange(String originalPassword, String changePassword, Member loginMember);
	void passwordReset(String email, String certificationCode, VerifyType verifyType);

	void sendCode(String email, VerifyType verifyType);
}

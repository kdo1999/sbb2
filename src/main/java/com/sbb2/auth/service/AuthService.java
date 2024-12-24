package com.sbb2.auth.service;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;

public interface AuthService {
	MemberEmailSignupResponse signup(String email, String username, String password);
	MemberLoginResponse memberLogin(String email, String password);
}

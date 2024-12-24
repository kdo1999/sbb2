package com.sbb2.auth.service;

import com.sbb2.auth.service.response.MemberEmailSignupResponse;

public interface AuthService {
	MemberEmailSignupResponse signup(String email, String username, String password);
}

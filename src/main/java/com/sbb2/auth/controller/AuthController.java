package com.sbb2.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.auth.controller.request.MemberEmailSignupRequest;
import com.sbb2.auth.controller.request.MemberLoginRequest;
import com.sbb2.auth.service.AuthService;
import com.sbb2.auth.service.response.MemberEmailSignupResponse;
import com.sbb2.auth.service.response.MemberLoginResponse;
import com.sbb2.common.response.GenericResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
	private final AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<GenericResponse<MemberEmailSignupResponse>> signup(@Valid @RequestBody MemberEmailSignupRequest memberEmailSignupRequest) {
		MemberEmailSignupResponse signup = authService.signup(memberEmailSignupRequest.email(),
			memberEmailSignupRequest.username(), memberEmailSignupRequest.password());

		return ResponseEntity.ok().body(GenericResponse.of(signup));
	}

	@PostMapping("/login")
	public ResponseEntity<GenericResponse<MemberLoginResponse>> login(@Valid @RequestBody MemberLoginRequest memberLoginRequest) {
		MemberLoginResponse memberLoginResponse = authService.memberLogin(memberLoginRequest.email(),
			memberLoginRequest.password());

		return ResponseEntity.ok().body(GenericResponse.of(memberLoginResponse));
	}
}

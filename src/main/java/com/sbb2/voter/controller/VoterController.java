package com.sbb2.voter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.service.VoterService;
import com.sbb2.voter.service.response.VoterCreateResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voter")
public class VoterController {
	private final VoterService voterService;

	@PostMapping("/{id}")
	public ResponseEntity<GenericResponse<VoterCreateResponse>> save(@PathVariable("id") Long id,
		@RequestParam VoterType voterType, @AuthenticationPrincipal MemberUserDetails loginMember) {
		VoterCreateResponse voterCreateResponse = voterService.save(id, voterType, loginMember.getMember());

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GenericResponse.of(voterCreateResponse));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> delete(@PathVariable("id") Long id, @RequestParam VoterType voterType,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		voterService.delete(id, voterType, loginMember.getMember());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
}

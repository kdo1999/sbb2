package com.sbb2.voter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
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
	public ResponseEntity<GenericResponse<VoterCreateResponse>> save(@PathVariable("id") Long id, @RequestParam VoterType voterType,
		Member loginMember) {
		VoterCreateResponse voterCreateResponse = voterService.save(id, voterType, loginMember);

		return ResponseEntity.ok().body(GenericResponse.of(voterCreateResponse));
	}
}

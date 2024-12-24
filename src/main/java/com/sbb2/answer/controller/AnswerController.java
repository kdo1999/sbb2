package com.sbb2.answer.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.answer.controller.request.AnswerForm;
import com.sbb2.answer.service.AnswerService;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.validation.ValidationSequence;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/answer")
public class AnswerController {
	private final AnswerService answerService;

	@PostMapping
	public ResponseEntity<GenericResponse<AnswerCreateResponse>> save(
		@Validated(ValidationSequence.class) @RequestBody AnswerForm answerForm,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		AnswerCreateResponse answerCreateResponse = answerService.save(answerForm.questionId(), answerForm.content(),
			loginMember.getMember());

		return ResponseEntity.created(URI.create("/question/" + answerCreateResponse.questionId()))
			.body(GenericResponse.of(answerCreateResponse));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> update(@PathVariable("id") Long answerId,
		@Validated(ValidationSequence.class) @RequestBody AnswerForm answerForm,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		answerService.update(answerId, answerForm.content(), loginMember.getMember());

		return ResponseEntity.ok(GenericResponse.of());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> delete(@PathVariable("id") Long answerId,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		answerService.deleteById(answerId, loginMember.getMember());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
}

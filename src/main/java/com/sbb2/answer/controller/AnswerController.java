package com.sbb2.answer.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.answer.controller.request.AnswerForm;
import com.sbb2.answer.service.AnswerService;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/answer")
public class AnswerController {
	private final AnswerService answerService;

	@PostMapping
	public ResponseEntity<GenericResponse<AnswerCreateResponse>> save(@Valid @RequestBody AnswerForm answerForm, Member loginMember) {
		AnswerCreateResponse answerCreateResponse = answerService.save(answerForm.questionId(), answerForm.content(), loginMember);

		return ResponseEntity.created(URI.create("/question/" + answerCreateResponse.questionId()))
			.body(GenericResponse.of(answerCreateResponse));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> update(@PathVariable("id") Long answerId, @Valid @RequestBody AnswerForm answerForm, Member loginMember) {
		answerService.update(answerId, answerForm.content(), loginMember);

		return ResponseEntity.ok(GenericResponse.of());
	}
}

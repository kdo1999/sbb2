package com.sbb2.answer.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.answer.controller.request.AnswerForm;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.answer.service.AnswerService;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.common.validation.ValidationSequence;
import com.sbb2.question.domain.QuestionPageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/answer")
public class AnswerController {
	private final AnswerService answerService;

	@GetMapping
	public ResponseEntity<GenericResponse<Page<AnswerDetailResponse>>> findAll(
		@RequestParam("questionId") Long questionId,
		@AuthenticationPrincipal MemberUserDetails loginMember,
		SearchCondition searchCondition) {

		Page<AnswerDetailResponse> findAllPage = answerService.findAnswerDetailPageByQuestionId(
			searchCondition, questionId, loginMember.getMember().id()
		);

		return ResponseEntity.ok()
			.body(GenericResponse.of(findAllPage));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GenericResponse<AnswerDetailResponse>> findByDetail(@PathVariable("id") Long answerId,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		AnswerDetailResponse answerDetailResponse = answerService.findAnswerDetailByIdAndMemberId(answerId,
			loginMember.getMember().id());

		return ResponseEntity.ok(GenericResponse.of(answerDetailResponse));
	}

	@PostMapping
	public ResponseEntity<GenericResponse<AnswerDetailResponse>> save(
		@Validated(ValidationSequence.class) @RequestBody AnswerForm answerForm,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		AnswerDetailResponse answerDetailResponse = answerService.save(answerForm.questionId(), answerForm.content(),
			loginMember.getMember());

		return ResponseEntity.created(URI.create("/question/" + answerDetailResponse.questionId()))
			.body(GenericResponse.of(answerDetailResponse));
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

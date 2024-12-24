package com.sbb2.question.controller;

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

import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.validation.ValidationSequence;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;
import com.sbb2.question.service.response.QuestionCreateResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question")
public class QuestionController {
	private final QuestionService questionService;

	@GetMapping
	public ResponseEntity<GenericResponse<Page<QuestionPageResponse>>> findAll(
		@RequestParam(value = "page", defaultValue = "0") int page
		, @RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<QuestionPageResponse> findAllPage = questionService.findAll(page, kw);

		return ResponseEntity.ok()
			.body(GenericResponse.of(findAllPage));
	}

	@GetMapping("/{questionId}")
	public ResponseEntity<GenericResponse<QuestionDetailResponse>> findDetailById(
		@PathVariable(value = "questionId") Long questionId, @AuthenticationPrincipal MemberUserDetails loginMember) {
		QuestionDetailResponse questionDetailResponse = questionService.findDetailById(questionId,
			loginMember.getMember());

		return ResponseEntity.ok()
			.body(GenericResponse.of(questionDetailResponse));
	}

	@PostMapping
	public ResponseEntity<GenericResponse<QuestionCreateResponse>> save(@Validated(ValidationSequence.class) @RequestBody QuestionForm questionForm,
		@AuthenticationPrincipal MemberUserDetails loginMember) {

		QuestionCreateResponse savedQuestion = questionService.save(questionForm.subject(), questionForm.content(),
			loginMember.getMember());

		return ResponseEntity.created(URI.create("/question/" + savedQuestion.id()))
			.body(GenericResponse.of(savedQuestion));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> update(@PathVariable("id") Long id,
		@Validated(ValidationSequence.class) @RequestBody QuestionForm questionForm, @AuthenticationPrincipal MemberUserDetails loginMember) {
		questionService.update(id, questionForm.subject(), questionForm.content(), loginMember.getMember());

		return ResponseEntity.ok(GenericResponse.of());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> delete(@PathVariable("id") Long id,
		@AuthenticationPrincipal MemberUserDetails loginMember) {
		questionService.deleteById(id, loginMember.getMember());

		return ResponseEntity.ok().body(GenericResponse.of());
	}
}

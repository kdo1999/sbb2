package com.sbb2.question.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.question.service.QuestionService;
import com.sbb2.question.service.response.QuestionCreateResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question")
public class QuestionController {
	private final QuestionService questionService;

	@GetMapping
	public ResponseEntity<GenericResponse<Page<QuestionPageResponse>>> findAll(@RequestParam(value = "page", defaultValue = "0") int page
		, @RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<QuestionPageResponse> findAllPage = questionService.findAll(page, kw);

		return ResponseEntity.ok()
			.body(GenericResponse.of(findAllPage));
	}

	@GetMapping("/{questionId}")
	public ResponseEntity<GenericResponse<QuestionDetailResponse>> findDetailById(@PathVariable(value = "questionId") Long questionId, Member loginMember) {
		QuestionDetailResponse questionDetailResponse = questionService.findDetailById(questionId, loginMember);

		return ResponseEntity.ok()
			.body(GenericResponse.of(questionDetailResponse));
	}

	@PostMapping
	public ResponseEntity<GenericResponse<QuestionCreateResponse>> save(@Valid @RequestBody QuestionForm questionForm, Member loginMember) {

		QuestionCreateResponse savedQuestion = questionService.save(questionForm.subject(), questionForm.content(), loginMember);

		return ResponseEntity.created(URI.create("/question/" + savedQuestion.id()))
			.body(GenericResponse.of(savedQuestion));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> update(@PathVariable("id") Long id, @Valid @RequestBody QuestionForm questionForm, Member loginMember) {
		Question question = questionService.findById(id);

		questionService.update(question.id(), questionForm.subject(), questionForm.content(), loginMember);

		return ResponseEntity.ok(GenericResponse.of());
	}

	private void authorEqualsLoginMember(Member loginMember, Question question) {
		if (!question.author().equals(loginMember)) {
			throw new QuestionBusinessLogicException(QuestionErrorCode.UNAUTHORIZED);
		}
	}
}

package com.sbb2.question.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
	private final QuestionService questionService;

	@GetMapping
	public String findAll(Model model, @RequestParam(value = "page", defaultValue = "0") int page
		, @RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<QuestionPageResponse> findAllPage = questionService.findAll(page, kw);
		model.addAttribute("paging", findAllPage);
		model.addAttribute("kw", kw);

		return "question_list";
	}

	@GetMapping("/detail/{questionId}")
	public String findDetailById(Model model, @PathVariable(value = "questionId") Long questionId, Member loginMember) {
		QuestionDetailResponse questionDetailResponse = questionService.findDetailById(questionId, loginMember);

		model.addAttribute("questionDetailResponse", questionDetailResponse);

		return "question_detail";
	}

	@PostMapping("/create")
	public String save(@Valid QuestionForm questionForm, BindingResult bindingResult, Member loginMember) {
		if (bindingResult.hasErrors()) {
			return "question_form";
		}

		questionService.save(questionForm.subject(), questionForm.content(), loginMember);

		return "redirect:/question";
	}
}

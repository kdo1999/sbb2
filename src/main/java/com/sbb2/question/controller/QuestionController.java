package com.sbb2.question.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;

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
}

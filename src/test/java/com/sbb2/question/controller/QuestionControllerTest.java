package com.sbb2.question.controller;

import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {
	@Mock
	private QuestionService questionService;
	private QuestionController questionController;
	private Model model;

	@BeforeEach
	void setUp() {
		questionController = new QuestionController(questionService);
		model = new ConcurrentModel();
	}

	@DisplayName("질문 전체 조회 성공 테스트")
	@Test
	void findAll_success() {
		//given
		int page = 0;
		String kw = "";
		List<QuestionPageResponse> questionPageResponseList = new ArrayList<>();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		for (int i = 0; i < 15; i++) {
			questionPageResponseList.add(
				new QuestionPageResponse(
					(long)i,
					"subject" + (i+1),
					"content" + (i+1),
					givenMember.username(),
					LocalDateTime.now(),
					LocalDateTime.now(),
					(long)i)
			);
		}
		PageImpl<QuestionPageResponse> questionPageResponses = new PageImpl<>(
			questionPageResponseList.subList(0, Math.min(10, questionPageResponseList.size())),
			PageRequest.of(page, 10),
			questionPageResponseList.size());

		given(questionService.findAll(page, kw)).willReturn(questionPageResponses);

		//when
		String viewName = questionController.findAll(model, page, kw);

		//then
		Page<QuestionPageResponse> getAttributePaging = (Page<QuestionPageResponse>)model.getAttribute("paging");
		Assertions.assertThat(viewName).isEqualTo("question_list");
		Assertions.assertThat(questionPageResponses).isEqualTo(getAttributePaging);
	}
}

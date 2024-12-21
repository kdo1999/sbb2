package com.sbb2.question.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;
import com.sbb2.voter.domain.Voter;

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
					"subject" + (i + 1),
					"content" + (i + 1),
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
		assertThat(viewName).isEqualTo("question_list");
		assertThat(questionPageResponses).isEqualTo(getAttributePaging);
	}

	@DisplayName("질문 단건 조회 성공 테스트")
	@Test
	void findQuestionDetail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(givenMember)
			.build();

		Voter voter = Voter.builder()
			.member(givenMember)
			.question(Question.builder().id(1L).build())
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.answerList(List.of(answer))
			.voterSet(Set.of(voter))
			.build();

		AnswerDetailResponse answerDetailResponse = AnswerDetailResponse.builder()
			.id(1L)
			.questionId(question.id())
			.voterCount((long)question.answerList().get(0).voterSet().size())
			.content(question.answerList().get(0).content())
			.username(givenMember.username())
			.isVoter(false)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		QuestionDetailResponse questionDetailResponse = QuestionDetailResponse.builder()
			.id(question.id())
			.subject(question.subject())
			.content(question.content())
			.voterCount((long)question.voterSet().size())
			.isVoter(question.voterSet().stream()
				.anyMatch(v -> v.member().id().equals(givenMember.id())))
			.answerList(List.of(answerDetailResponse))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(questionService.findDetailById(question.id(), givenMember))
			.willReturn(questionDetailResponse);

		//when
		String viewName = questionController.findDetailById(model, question.id(), givenMember);

		//then
		QuestionDetailResponse getQuestionDetailResponse = (QuestionDetailResponse)model.getAttribute(
			"questionDetailResponse");

		assertThat(viewName).isEqualTo("question_detail");
		assertThat(getQuestionDetailResponse).isEqualTo(questionDetailResponse);
		assertThat(getQuestionDetailResponse.isVoter()).isTrue();
	}
	@DisplayName("질문 저장 성공 테스트")
	@Test
	void save_success() {
	    //given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("testSubject")
			.content("testContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionService.save(questionForm.subject(), questionForm.content(), givenMember))
			.willReturn(Question.builder()
				.id(1L)
				.subject(questionForm.subject())
				.content(questionForm.content())
				.author(givenMember)
				.build());

		//when
		String viewName = questionController.save(questionForm, givenMember);

	    //then
		assertThat(viewName).isEqualTo("redirect:/question");
	}
	//TODO 질문 삭제

	//TODO 질문 추천
}
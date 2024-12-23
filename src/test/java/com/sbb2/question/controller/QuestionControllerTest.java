package com.sbb2.question.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.HibernateValidator;
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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.question.service.QuestionService;
import com.sbb2.voter.domain.Voter;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class QuestionControllerTest {
	@Mock
	private QuestionService questionService;
	private QuestionController questionController;
	private Model model;
	private Validator validator;

	@BeforeEach
	void setUp() {
		questionController = new QuestionController(questionService);
		model = new ConcurrentModel();
		ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
			.configure()
			.buildValidatorFactory();
		validator = factory.getValidator();
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

		BindingResult bindingResult = new BeanPropertyBindingResult(questionForm, "questionForm");
		validator.validate(questionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		given(questionService.save(questionForm.subject(), questionForm.content(), givenMember))
			.willReturn(Question.builder()
				.id(1L)
				.subject(questionForm.subject())
				.content(questionForm.content())
				.author(givenMember)
				.build());

		//when
		String viewName = questionController.save(questionForm, bindingResult, givenMember);

	    //then
		assertThat(viewName).isEqualTo("redirect:/question");
	}

	@DisplayName("질문 저장시 제목 빈 값 들어갔을 때 실패 테스트")
	@Test
	void save_emptySubject_fail() {
	    //given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("")
			.content("testContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(questionForm, "questionForm");

		validator.validate(questionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//when
		String viewName = questionController.save(questionForm, bindingResult, givenMember);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("subject").getDefaultMessage())
			.isEqualTo("제목은 필수 항목입니다.");
		assertThat(viewName).isEqualTo("question_form");
	}

	@DisplayName("질문 저장시 내용 빈 값 들어갔을 때 실패 테스트")
	@Test
	void save_emptyContent_fail() {
	    //given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("testSubject")
			.content("")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(questionForm, "questionForm");

		validator.validate(questionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//when
		String viewName = questionController.save(questionForm, bindingResult, givenMember);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage())
			.isEqualTo("내용은 필수 항목입니다.");
		assertThat(viewName).isEqualTo("question_form");
	}

	@DisplayName("질문 생성 GET 요청 성공 테스트")
	@Test
	void save_getCreate_success() {
	    //given
		QuestionForm questionForm = QuestionForm.builder().build();

		//when
		String viewName = questionController.save(questionForm);

		//then
		assertThat(viewName).isEqualTo("question_form");
	}

	@DisplayName("질문 수정 GET 요청 성공 테스트")
	@Test
	void update_getUpdate_success() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();

		QuestionForm givenQuestionForm = QuestionForm.builder()
			.build();

		given(questionService.findById(givenQuestion.id())).willReturn(givenQuestion);

		//when
		String viewName = questionController.update(givenQuestion.id(), givenQuestionForm, givenMember);

		//then
		assertThat(viewName).isEqualTo("question_form");
		verify(questionService, times(1)).findById(givenQuestion.id());
	}

	@DisplayName("질문 생성 GET 요청시 작성자와 요청 사용자가 다를 때 실패 테스트")
	@Test
	void update_getUpdate_fail() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Member loginMember = Member.builder()
			.id(2L)
			.username("loginMember")
			.password("loginPassword")
			.email("loginEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();

		QuestionForm givenQuestionForm = QuestionForm.builder()
			.build();

		given(questionService.findById(givenQuestion.id())).willReturn(givenQuestion);

		//then
		assertThatThrownBy(() -> questionController.update(givenQuestion.id(), givenQuestionForm, loginMember))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.UNAUTHORIZED.getMessage())
			.extracting("message", "status")
       	 	.containsExactly(QuestionErrorCode.UNAUTHORIZED.getMessage(), QuestionErrorCode.UNAUTHORIZED.getHttpStatus());
	}

	@DisplayName("질문 수정 성공 테스트")
	@Test
	void update_question_success() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();

		QuestionForm givenQuestionForm = QuestionForm.builder()
			.content("updateContent")
			.subject("updateSubject")
			.build();

		given(questionService.findById(givenQuestion.id())).willReturn(givenQuestion);
		given(questionService.update(givenQuestion.id(), givenQuestionForm.subject(), givenQuestionForm.content(), givenMember)).willReturn(givenQuestion);

		BindingResult bindingResult = new BeanPropertyBindingResult(givenQuestionForm, "questionForm");

		validator.validate(givenQuestionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//when
		String viewName = questionController.update(givenQuestion.id(), givenQuestionForm, bindingResult, givenMember);

		//then
		assertThat(viewName).isEqualTo("redirect:/question/detail/" + givenQuestion.id());
		verify(questionService, times(1)).findById(givenQuestion.id());
		verify(questionService, times(1)).update(givenQuestion.id(), givenQuestionForm.subject(), givenQuestionForm.content(), givenMember);
	}

	@DisplayName("질문 수정시 제목이 비었을 때 실패 테스트")
	@Test
	void update_subject_empty_fail() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();

		QuestionForm givenQuestionForm = QuestionForm.builder()
			.content("updateContent")
			.subject("")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenQuestionForm, "questionForm");

		validator.validate(givenQuestionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//when
		String viewName = questionController.update(givenQuestion.id(), givenQuestionForm, bindingResult, givenMember);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("subject").getDefaultMessage())
			.isEqualTo("제목은 필수 항목입니다.");
		assertThat(viewName).isEqualTo("question_form");
	}

	@DisplayName("질문 수정시 내용이 비었을 때 실패 테스트")
	@Test
	void update_content_empty_fail() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();

		QuestionForm givenQuestionForm = QuestionForm.builder()
			.content("")
			.subject("updateSubject")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenQuestionForm, "questionForm");

		validator.validate(givenQuestionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//when
		String viewName = questionController.update(givenQuestion.id(), givenQuestionForm, bindingResult, givenMember);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage())
			.isEqualTo("내용은 필수 항목입니다.");
		assertThat(viewName).isEqualTo("question_form");
	}

	@DisplayName("질문 삭제 성공 테스트")
	@Test
	void delete_question_success() {
	    //given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question givenQuestion = Question.builder()
			.id(1L)
			.content("testContent")
			.subject("testSubject")
			.author(givenMember)
			.build();
		doNothing().when(questionService).deleteById(givenQuestion.id(), givenMember);

	    //when
		String viewName = questionController.delete(givenQuestion.id(), givenMember);

		//then
		assertThat(viewName).isEqualTo("redirect:/");
		verify(questionService, times(1)).deleteById(givenQuestion.id(), givenMember);
	}

	//TODO 질문 추천

	//TODO 질문 추천 삭제
}

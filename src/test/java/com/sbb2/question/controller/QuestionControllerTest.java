package com.sbb2.question.controller;

import static com.sbb2.common.validation.ValidationGroups.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.domain.AnswerDetailResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionDetailResponse;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.service.QuestionService;
import com.sbb2.question.service.response.QuestionCreateResponse;
import com.sbb2.common.util.SearchCondition;
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

		SearchCondition searchCondition = SearchCondition.builder()
			.pageNum(page)
			.kw(kw)
			.build();

		PageImpl<QuestionPageResponse> questionPageResponses = new PageImpl<>(
			questionPageResponseList.subList(0, Math.min(10, questionPageResponseList.size())),
			PageRequest.of(searchCondition.pageNum(), 10),
			questionPageResponseList.size());

		given(questionService.findAll(searchCondition)).willReturn(questionPageResponses);

		//when
		ResponseEntity<GenericResponse<Page<QuestionPageResponse>>> result = questionController.findAll(searchCondition);

		//then
		Page<QuestionPageResponse> data = result.getBody().getData();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(questionPageResponses).isEqualTo(data);
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

		QuestionDetailResponse questionDetailResponse = QuestionDetailResponse.builder()
			.id(question.id())
			.subject(question.subject())
			.content(question.content())
			.voterCount((long)question.voterSet().size())
			.isVoter(question.voterSet().stream()
				.anyMatch(v -> v.member().id().equals(givenMember.id())))
			.isAuthor(question.author().id().equals(givenMember.id()))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		MemberUserDetails member = new MemberUserDetails(givenMember);

		given(questionService.findDetailById(question.id(), givenMember))
			.willReturn(questionDetailResponse);

		//when
		ResponseEntity<GenericResponse<QuestionDetailResponse>> result = questionController.findDetailById(
			question.id(), member);

		//then
		QuestionDetailResponse data = result.getBody().getData();

		assertThat(data).isEqualTo(questionDetailResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
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

		MemberUserDetails member = new MemberUserDetails(givenMember);

		BindingResult bindingResult = new BeanPropertyBindingResult(questionForm, "questionForm");
		validator.validate(questionForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		QuestionCreateResponse questionCreateResponse = QuestionCreateResponse.builder().id(1L).build();
		given(questionService.save(questionForm.subject(), questionForm.content(), givenMember))
			.willReturn(questionCreateResponse);

		//when
		ResponseEntity<GenericResponse<QuestionCreateResponse>> result = questionController.save(questionForm,
			member);

		//then
		QuestionCreateResponse data = result.getBody().getData();
		assertThat(questionCreateResponse).isEqualTo(data);
		assertThat(result.getHeaders().getLocation().getPath()).isEqualTo("/question/" + data.id());
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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

		validator.validate(questionForm, NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("subject").getDefaultMessage())
			.isEqualTo("제목은 필수 항목입니다.");
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

		validator.validate(questionForm, NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage())
			.isEqualTo("내용은 필수 항목입니다.");
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

		MemberUserDetails member = new MemberUserDetails(givenMember);

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
		ResponseEntity<GenericResponse<Void>> result = questionController.update(givenQuestion.id(), givenQuestionForm,
			member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
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

		validator.validate(givenQuestionForm, NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("subject").getDefaultMessage())
			.isEqualTo("제목은 필수 항목입니다.");
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

		validator.validate(givenQuestionForm, NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage())
			.isEqualTo("내용은 필수 항목입니다.");
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

		MemberUserDetails member = new MemberUserDetails(givenMember);

		doNothing().when(questionService).deleteById(givenQuestion.id(), givenMember);

	    //when
		ResponseEntity<GenericResponse<Void>> result = questionController.delete(givenQuestion.id(), member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(questionService, times(1)).deleteById(givenQuestion.id(), givenMember);
	}
}

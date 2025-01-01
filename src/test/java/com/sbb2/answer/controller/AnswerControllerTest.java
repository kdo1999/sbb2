package com.sbb2.answer.controller;

import static com.sbb2.common.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.sbb2.answer.controller.request.AnswerForm;
import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.service.AnswerService;
import com.sbb2.answer.service.response.AnswerCreateResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.validation.ValidationGroups;
import com.sbb2.member.domain.Member;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class AnswerControllerTest {
	@Mock
	private AnswerService answerService;
	private AnswerController answerController;
	private Model model;
	private Validator validator;

	@BeforeEach
	void setUp() {
		answerController = new AnswerController(answerService);
		model = new ConcurrentModel();
		ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
			.configure()
			.buildValidatorFactory();
		validator = factory.getValidator();
	}

	@DisplayName("답변 저장 성공 테스트")
	@Test
	void save_answer_success() {
	    //given
		AnswerForm answerForm = AnswerForm.builder()
			.questionId(1L)
			.content("testContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		MemberUserDetails member = new MemberUserDetails(givenMember);

		BindingResult bindingResult = new BeanPropertyBindingResult(answerForm, "answerForm");
		validator.validate(answerForm).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		AnswerCreateResponse answerCreateResponse = AnswerCreateResponse.builder()
			.questionId(answerForm.questionId())
			.answerId(1L)
			.content(answerForm.content())
			.author(givenMember.username())
			.build();

		given(answerService.save(answerForm.questionId(), answerForm.content(), givenMember))
			.willReturn(answerCreateResponse);

		//when
		ResponseEntity<GenericResponse<AnswerCreateResponse>> result = answerController.save(answerForm,
			member);

		//then
		AnswerCreateResponse data = result.getBody().getData();
		assertThat(answerCreateResponse).isEqualTo(data);
		assertThat(result.getHeaders().getLocation().getPath()).isEqualTo("/question/" + data.questionId());
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@DisplayName("답변 저장시 내용이 비었을 때 실패 테스트")
	@Test
	void save_answer_content_empty_fail() {
	    //given
		AnswerForm answerForm = AnswerForm.builder()
			.questionId(1L)
			.content("")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(answerForm, "answerForm");
		validator.validate(answerForm, NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage()).isEqualTo("답변 내용은 필수 항목입니다.");
	}

	@DisplayName("답변 수정 성공 테스트")
	@Test
	void update_answer_success() {
		//given
		AnswerForm answerForm = AnswerForm.builder()
			.questionId(1L)
			.content("updateContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		MemberUserDetails member = new MemberUserDetails(givenMember);

		given(answerService.update(1L, answerForm.content(), givenMember))
			.willReturn(Answer.builder()
				.id(1L)
				.content(answerForm.content())
				.build());

		//when
		ResponseEntity<GenericResponse<Void>> result = answerController.update(1L, answerForm, member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(answerService, times(1)).update(1L, answerForm.content(), givenMember);
	}

	@DisplayName("답변 삭제 성공 테스트")
	@Test
	void delete_answer_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		MemberUserDetails member = new MemberUserDetails(givenMember);

		doNothing().when(answerService).deleteById(1L, givenMember);

		//when
		ResponseEntity<GenericResponse<Void>> result = answerController.delete(1L, member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(answerService, times(1)).deleteById(1L, givenMember);
	}
}

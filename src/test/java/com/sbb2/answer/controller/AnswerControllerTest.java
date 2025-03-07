package com.sbb2.answer.controller;

import static com.sbb2.common.validation.ValidationGroups.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

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
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.sbb2.answer.controller.request.AnswerForm;
import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.service.response.AnswerDetailResponse;
import com.sbb2.answer.service.AnswerService;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

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

		AnswerDetailResponse answerDetailResponse = AnswerDetailResponse.builder()
			.id(1L)
			.questionId(answerForm.questionId())
			.content(answerForm.content())
			.author(givenMember.username())
			.isAuthor(true)
			.isVoter(false)
			.voterCount(0L)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(answerService.save(answerForm.questionId(), answerForm.content(), givenMember))
			.willReturn(answerDetailResponse);

		//when
		ResponseEntity<GenericResponse<AnswerDetailResponse>> result = answerController.save(answerForm,
			member);

		//then
		AnswerDetailResponse data = result.getBody().getData();
		assertThat(answerDetailResponse).isEqualTo(data);
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

	@DisplayName("답변 상세 조회 성공 테스트")
	@Test
	void find_detail_success() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content(content)
			.author(member)
			.question(question)
			.voterSet(Set.of())
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		AnswerDetailResponse answerDetailResponse = AnswerDetailResponse.builder()
			.id(answer.id())
			.content(answer.content())
			.author(answer.author().username())
			.voterCount((long)answer.voterSet().size())
			.createdAt(answer.createdAt())
			.modifiedAt(answer.modifiedAt())
			.isAuthor(answer.author().id().equals(member.id()))
			.isVoter(answer.voterSet().stream().anyMatch(voter -> voter.member().id().equals(member.id())))
			.questionId(answer.question().id())
			.build();

		MemberUserDetails loginMember = new MemberUserDetails(member);

		given(answerService.findAnswerDetailByIdAndMemberId(answer.id(), member.id()))
			.willReturn(answerDetailResponse);

		//when
		ResponseEntity<GenericResponse<AnswerDetailResponse>> result = answerController.findByDetail(answer.id(),
			loginMember);

		//then
		assertThat(result.getBody().getData()).isEqualTo(answerDetailResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(answerService, times(1)).findAnswerDetailByIdAndMemberId(answer.id(), member.id());
	}

	@DisplayName("답변 페이징 조회 성공 테스트")
	@Test
	void findAll_success() {
		//given
		Long questionId = 1L;

		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		MemberUserDetails loginMember = new MemberUserDetails(member);

		List<AnswerDetailResponse> answerDetailResponseList = new ArrayList<>();
		LongStream.range(0, 15)
			.forEach((index) -> answerDetailResponseList.add(
				AnswerDetailResponse.builder()
					.id(index)
					.content("testContent" + index)
					.author(member.username())
					.isAuthor(true)
					.isVoter(false)
					.voterCount(0L)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.questionId(1L)
					.build()
			));

		SearchCondition searchCondition = SearchCondition.builder()
			.pageNum(0)
			.build();

		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);

		Page<AnswerDetailResponse> answerDetailResponsePage = new PageImpl<>(
			answerDetailResponseList.subList(0, Math.min(10, answerDetailResponseList.size())), pageable,
			answerDetailResponseList.size());

		given(answerService.findAnswerDetailPageByQuestionId(searchCondition, questionId, member.id()))
			.willReturn(answerDetailResponsePage);

	    //when
		ResponseEntity<GenericResponse<Page<AnswerDetailResponse>>> result = answerController.findAll(questionId,
			loginMember, searchCondition);

		//then
	    assertThat(result.getBody().getData()).isEqualTo(answerDetailResponsePage);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(answerService, times(1)).findAnswerDetailPageByQuestionId(searchCondition, questionId, member.id());
	}
}

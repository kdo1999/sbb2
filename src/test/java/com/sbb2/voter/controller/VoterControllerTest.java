package com.sbb2.voter.controller;

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

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.service.VoterService;
import com.sbb2.voter.service.response.VoterCreateResponse;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class VoterControllerTest {
	@Mock
	private VoterService voterService;
	private VoterController voterController;
	private Validator validator;

	@BeforeEach
	void setUp() {
		voterController = new VoterController(voterService);
		ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
			.configure()
			.buildValidatorFactory();
		validator = factory.getValidator();
	}

	@DisplayName("질문 추천 성공 테스트")
	@Test
	void save_question_voter_success() {
	    //given
	    Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.build();

		VoterType voterType = VoterType.QUESTION;

		VoterCreateResponse voterCreateResponse = VoterCreateResponse.builder()
			.voterId(1L)
			.voterUsername(givenMember.username())
			.targetId(question.id())
			.voterType(voterType)
			.isVoter(true)
			.build();

		MemberUserDetails member = new MemberUserDetails(givenMember);

		given(voterService.save(question.id(), voterType, givenMember))
			.willReturn(voterCreateResponse);

	    //when
		ResponseEntity<GenericResponse<VoterCreateResponse>> result = voterController.save(question.id(), voterType,
			member);

		//then
		assertThat(result.getBody().getData()).isEqualTo(voterCreateResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@DisplayName("질문 추천 삭제 성공 테스트")
	@Test
	void delete_question_success() {
	     //given
	    Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.build();

		Voter voter = Voter.builder()
			.id(1L)
			.question(question)
			.member(givenMember)
			.build();

		question.voterSet().add(voter);

		MemberUserDetails member = new MemberUserDetails(givenMember);

		VoterType voterType = VoterType.QUESTION;

		doNothing().when(voterService).delete(question.id(), voterType, givenMember);

	    //when
		ResponseEntity<GenericResponse<Void>> result = voterController.delete(question.id(), voterType,
			member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(voterService, times(1)).delete(question.id(), voterType, givenMember);

	}

	@DisplayName("답변 추천 성공 테스트")
	@Test
	void save_answer_voter_success() {
	    //given
	    Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(givenMember)
			.build();

		VoterType voterType = VoterType.QUESTION;

		MemberUserDetails member = new MemberUserDetails(givenMember);

		VoterCreateResponse voterCreateResponse = VoterCreateResponse.builder()
			.voterId(1L)
			.voterUsername(givenMember.username())
			.targetId(answer.id())
			.voterType(voterType)
			.isVoter(true)
			.build();

		given(voterService.save(answer.id(), voterType, givenMember))
			.willReturn(voterCreateResponse);

	    //when
		ResponseEntity<GenericResponse<VoterCreateResponse>> result = voterController.save(answer.id(), voterType,
			member);

		//then
		assertThat(result.getBody().getData()).isEqualTo(voterCreateResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@DisplayName("답변 추천 삭제 성공 테스트")
	@Test
	void delete_answer_success() {
	     //given
	    Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(givenMember)
			.build();

		Voter voter = Voter.builder()
			.id(1L)
			.answer(answer)
			.member(givenMember)
			.build();

		answer.voterSet().add(voter);

		MemberUserDetails member = new MemberUserDetails(givenMember);

		VoterType voterType = VoterType.ANSWER;

		doNothing().when(voterService).delete(answer.id(), voterType, givenMember);

	    //when
		ResponseEntity<GenericResponse<Void>> result = voterController.delete(answer.id(), voterType,
			member);

		//then
		assertThat(result.getBody().getData()).isNull();
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(voterService, times(1)).delete(answer.id(), voterType, givenMember);

	}
}

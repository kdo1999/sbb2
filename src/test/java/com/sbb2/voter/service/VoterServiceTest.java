package com.sbb2.voter.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.answer.domain.Answer;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.exception.VoterBusinessLogicException;
import com.sbb2.voter.exception.VoterErrorCode;
import com.sbb2.voter.service.response.VoterCreateResponse;

@ExtendWith(MockitoExtension.class)
public class VoterServiceTest {
	@Mock
	private VoterRepository voterRepository;
	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private AnswerRepository answerRepository;
	private VoterService voterService;

	@BeforeEach
	void setUp() {
		voterService = new VoterServiceImpl(voterRepository, questionRepository, answerRepository);
	}

	@DisplayName("질문 추천 성공 테스트")
	@Test
	void save_question_voter_success() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.question(question)
			.member(member)
			.build();

		VoterType voterType = VoterType.QUESTION;
		given(voterRepository.save(voter)).willReturn(voter);
		given(questionRepository.findById(question.id())).willReturn(Optional.of(question));
		given(voterRepository.existsByQuestionIdAndMemberId(question.id(), member.id())).willReturn(false);

		//when
		VoterCreateResponse voterCreateResponse = voterService.save(question.id(), voterType, member);

		//then
		assertThat(voterCreateResponse.targetId()).isEqualTo(question.id());
		assertThat(voterCreateResponse.voterType()).isEqualTo(voterType);
		assertThat(voterCreateResponse.voterUsername()).isEqualTo(member.username());
		assertThat(voterCreateResponse.isVoter()).isTrue();
	}

	@DisplayName("질문 추천 삭제 성공 테스트")
	@Test
	void delete_question_voter_success() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.id(1L)
			.question(question)
			.member(member)
			.build();

		question.voterSet().add(voter);

		doNothing().when(voterRepository).deleteById(voter.id());

		//when
		voterService.delete(question, member);

		//then
		verify(voterRepository, times(1)).deleteById(voter.id());
	}
	@DisplayName("질문 중복 추천 방지 성공 테스트")
	@Test
	void prevent_duplicate_voter_success() {
	    //given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.question(question)
			.member(member)
			.build();

		VoterType voterType = VoterType.QUESTION;

		given(voterRepository.existsByQuestionIdAndMemberId(question.id(), member.id())).willReturn(true);

	    //then
		assertThatThrownBy(() -> voterService.save(question.id(), voterType, member))
			.isInstanceOf(VoterBusinessLogicException.class)
			.hasMessage(VoterErrorCode.DUPLICATE_VOTER.getMessage());
	}

	@DisplayName("댓글 추천 성공 테스트")
	@Test
	void save_answer_voter_success() {
	    //given
	    Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.answer(answer)
			.member(member)
			.build();

		VoterType voterType = VoterType.ANSWER;

		given(voterRepository.save(voter)).willReturn(voter);
		given(answerRepository.findById(question.id())).willReturn(Optional.of(answer));
		given(voterRepository.existsByAnswerIdAndMemberId(answer.id(), member.id())).willReturn(false);

		//when
		VoterCreateResponse voterCreateResponse = voterService.save(answer.id(), voterType, member);

		//then
		assertThat(voterCreateResponse.targetId()).isEqualTo(answer.id());
		assertThat(voterCreateResponse.voterType()).isEqualTo(voterType);
		assertThat(voterCreateResponse.voterUsername()).isEqualTo(member.username());
		assertThat(voterCreateResponse.isVoter()).isTrue();
	}

	@DisplayName("답변 추천 삭제 성공 테스트")
	@Test
	void delete_answer_voter_success() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.id(1L)
			.answer(answer)
			.member(member)
			.build();

		answer.voterSet().add(voter);

		doNothing().when(voterRepository).deleteById(voter.id());

		//when
		voterService.delete(answer, member);

		//then
		verify(voterRepository, times(1)).deleteById(voter.id());
	}
}

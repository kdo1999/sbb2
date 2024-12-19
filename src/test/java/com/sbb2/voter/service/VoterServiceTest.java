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
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;
import com.sbb2.voter.exception.VoterBusinessLogicException;
import com.sbb2.voter.exception.VoterErrorCode;

@ExtendWith(MockitoExtension.class)
public class VoterServiceTest {
	@Mock
	private VoterRepository voterRepository;
	private VoterService voterService;

	@BeforeEach
	void setUp() {
		voterService = new VoterServiceImpl(voterRepository);
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

		given(voterRepository.save(voter)).willReturn(voter);

		//when
		Voter savedVoter = voterService.save(question, member);

		//then
		assertThat(savedVoter).isEqualTo(voter);
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

		given(voterRepository.findByQuestionIdAndMemberId(question.id(), member.id())).willReturn(Optional.of(voter));
		doNothing().when(voterRepository).deleteById(voter.id());

		//when
		voterService.deleteQuestionVoter(question.id(), member.id());

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

		given(voterRepository.existsByQuestionIdAndMemberId(question.id(), member.id())).willReturn(true);

	    //then
		assertThatThrownBy(() -> voterService.save(question, member))
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

		given(voterRepository.save(voter)).willReturn(voter);

	    //when
		Voter savedVoter = voterService.save(answer, member);

	    //then
	    assertThat(savedVoter).isEqualTo(voter);
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

		given(voterRepository.findByQuestionIdAndMemberId(answer.id(), member.id())).willReturn(Optional.of(voter));
		doNothing().when(voterRepository).deleteById(voter.id());

		//when
		voterService.deleteAnswerVoter(question.id(), member.id());

		//then
		verify(voterRepository, times(1)).deleteById(voter.id());
	}
	//TODO 댓글 중복 추천 방지 테스트
	//TODO 질문 추천 삭제 테스트
}

package com.sbb2.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {
	@Mock
	private AnswerRepository answerRepository;
	@Mock
	private QuestionRepository questionRepository;
	private AnswerService answerService;

	@BeforeEach
	void setUp() {
		answerService = new AnswerServiceImpl(questionRepository, answerRepository);
	}

	@DisplayName("답변 저장 성공 테스트")
	@Test
	void save_answer_success() {
	    //given
		Member member = Member.builder()
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
		Long questionId = question.id();

		given(questionRepository.findById(any(Long.class))).willReturn(Optional.of(question));

		given(answerRepository.save(any(Answer.class)))
			.willReturn(Answer.builder()
				.id(1L)
				.content(content)
				.author(member)
				.question(question)
				.build());

	    //when
		Answer savedAnswer = answerService.save(questionId, content, member);

	    //then
		assertThat(savedAnswer.id()).isEqualTo(questionId);
		assertThat(savedAnswer.content()).isEqualTo(content);
		assertThat(savedAnswer.author()).isEqualTo(member);
		assertThat(savedAnswer.question()).isEqualTo(question);
	}

	@DisplayName("답변 저장 질문 조회 실패 테스트")
	@Test
	void save_answer_find_fail() {
	    //given
		Member member = Member.builder()
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
		Long questionId = question.id();

		given(questionRepository.findById(any(Long.class)))
			.willThrow(new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

	    //then
		assertThatThrownBy(() -> answerService.save(questionId, content, member));
	}

	@DisplayName("답변 조회 성공 테스트")
	@Test
	void find_answer_success() {
	    //given
		Member member = Member.builder()
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
			.build();

		Long answerId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));

		//when
	    Answer findAnswer = answerService.findById(answerId);

	    //then
	    assertThat(findAnswer).isEqualTo(answer);
	}

	@DisplayName("답변 조회 실패 테스트")
	@Test
	void find_answer_fail() {
	    //given
		Long answerId = 1L;

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.empty());

	    //then
		assertThatThrownBy(() -> answerService.findById(answerId));
	}

	@DisplayName("답변 수정 성공 테스트")
	@Test
	void update_answer_success() {
	    //given
		Member member = Member.builder()
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

		String updateContent = "updateAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(member)
			.question(question)
			.build();

		Answer updateAnswer = answer.fetch(updateContent);
		Long answerId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));
		given(answerRepository.save(any(Answer.class))).willReturn(updateAnswer);

	    //when
		Answer result = answerService.update(answerId, updateContent, member);

	    //then
	    assertThat(result).isEqualTo(updateAnswer);
	}

	@DisplayName("답변 수정시 답변 조회 실패 테스트")
	@Test
	void update_answer_find_fail() {
	    //given
		Long answerId = 1L;
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.empty());

	    //then
		assertThatThrownBy(() -> answerService.update(answerId, "updateContent", member))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("작성자가 아닌 회원이 질문 수정 시도시 실패")
	@Test
	void update_answer_unauthorized_fail() {
	    //given
		Member author = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(author)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(author)
			.question(question)
			.build();

		Member loginMember = Member.builder()
			.username("testUsername2")
			.password("testPassword2")
			.email("testEmail2@naver.com")
			.build();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));

	    //then
		assertThatThrownBy(() -> answerService.update(answer.id(), "updateContent", loginMember))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.UNAUTHORIZED.getMessage());
	}

	@DisplayName("답변 삭제 성공 테스트")
	@Test
	void delete_answer_success() {
	    //given
		Member author = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(author)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(author)
			.question(question)
			.build();

		Long targetId = answer.id();

		doNothing().when(answerRepository).deleteById(any(Long.class));

	    //when
		answerService.deleteById(targetId, author);

	    //then
		verify(answerRepository, times(1)).deleteById(any(Long.class));
	}
}

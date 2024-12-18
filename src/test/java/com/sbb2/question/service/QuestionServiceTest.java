package com.sbb2.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
	@Mock
	private QuestionRepository questionRepository;

	@InjectMocks
	private QuestionServiceImpl questionService;

	@DisplayName("질문 저장 테스트")
	@Test
	void save_question() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.save(any(Question.class)))
			.willReturn(
				Question.builder()
					.id(1L)
					.subject(questionSubject)
					.content(questionContent)
					.author(givenMember)
					.build()
			);

		//when
		Question savedQuestion = questionService.save("questionSubject", "questionContent", givenMember);

		//then
		assertThat(savedQuestion.author()).isEqualTo(givenMember);
		assertThat(savedQuestion.subject()).isEqualTo(questionSubject);
		assertThat(savedQuestion.content()).isEqualTo(questionContent);
	}

	@DisplayName("질문 ID로 조회 성공 테스트")
	@Test
	void find_id_question() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(
				Optional.of(Question.builder()
					.id(1L)
					.subject(questionSubject)
					.content(questionContent)
					.author(givenMember)
					.build()
				)
			);

		//when
		Question findQuestion = questionService.findById(1L);

		//then
		assertThat(findQuestion.author()).isEqualTo(givenMember);
		assertThat(findQuestion.subject()).isEqualTo(questionSubject);
		assertThat(findQuestion.content()).isEqualTo(questionContent);
	}

	@DisplayName("질문 ID로 조회 실패 테스트")
	@Test
	void find_id_question_fail() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willThrow(new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		//then
		assertThatThrownBy(() -> questionService.findById(2L)).isInstanceOf(QuestionBusinessLogicException.class);
	}

	@DisplayName("질문 수정 성공 테스트")
	@Test
	void update_question_succes() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.author(givenMember)
				.build()));

		given(questionRepository.save(any(Question.class)))
			.willReturn(Question.builder()
				.id(1L)
				.subject(questionForm.subject())
				.content(questionForm.content())
				.author(givenMember)
				.build());
		//when
		Question updateQuestion = questionService.update(1L, questionForm.subject(), questionForm.content(),
			givenMember);

		//then
		assertThat(updateQuestion.author()).isEqualTo(givenMember);
		assertThat(updateQuestion.subject()).isEqualTo(questionForm.subject());
		assertThat(updateQuestion.content()).isEqualTo(questionForm.content());
	}

	@DisplayName("질문 수정시 조회 실패 테스트")
	@Test
	void update_find_question_fail() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.empty());

		//then
		assertThatThrownBy(
			() -> questionService.update(1L, questionForm.subject(), questionForm.content(), givenMember))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
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

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.author(givenMember)
				.build()));

		doNothing().when(questionRepository).deleteById(any(Long.class));

		//when
		questionService.deleteById(1L);

		//then
		verify(questionRepository, times(1)).deleteById(any(Long.class));
	}

	@DisplayName("질문 삭제시 질문 조회 실패 테스트")
	@Test
	void delete_question_find_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.author(givenMember)
				.build()));

		given(questionRepository.findById(any(Long.class))).willReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> questionService.deleteById(1L))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("질문 페이징 조회 성공 테스트")
	@Test
	void findAll_question_success() {
		//given
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

		given(questionRepository.findAll(any(String.class), any(Pageable.class))).willReturn(new PageImpl<>(
			questionPageResponseList,
			PageRequest.of(0, 10),
			questionPageResponseList.size()
			)
		);

		//when
		Page<QuestionPageResponse> findAll = questionService.findAll(1, "");

		//then
		assertThat(findAll.getContent().size()).isEqualTo(10);
		assertThat(findAll.getTotalElements()).isEqualTo(15);
		assertThat(findAll.getTotalPages()).isEqualTo(1);
	}
}

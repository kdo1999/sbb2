package com.sbb2.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

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
}

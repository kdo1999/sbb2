package com.sbb2.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

public class QuestionServiceTest {
	private final QuestionService questionService = new QuestionService();
	@Mock
	private QuestionRepository questionRepository;

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
}

package com.sbb2.infrastructer.answer.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnswerRepositoryTest {
	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;
	private final MemberRepository memberRepository;

	@Autowired
	public AnswerRepositoryTest(AnswerRepository answerRepository, QuestionRepository questionRepository,
		MemberRepository memberRepository) {
		this.answerRepository = answerRepository;
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
	}

	@BeforeAll
	void initMember() {
		String username = "testUsername";
		String password = "testPassword";
		String email = "testEmail";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		String subject = "testSubject";
		String content = "testContent";
		Member author = memberRepository.findById(1L).get();

		for (int i = 0; i < 4; i++) {
			Question question = Question.builder()
				.subject(subject + (i + 1))
				.content(content + (i + 1))
				.author(author)
				.build();

			questionRepository.save(question);
		}
	}

	@DisplayName("답변 저장 테스트")
	@Test
	void save_answer() {
		//given
		String content = "testAnswerContent";
		Member member = memberRepository.findById(1L).get();
		Question question = questionRepository.findById(1L).get();

		Answer givenAnswer = Answer.builder()
			.content(content)
			.author(member)
			.question(question)
			.build();

		//when
		Answer savedAnswer = answerRepository.save(givenAnswer);

		//then
		assertThat(savedAnswer.content()).isEqualTo(content);
		assertThat(savedAnswer.author()).isEqualTo(member);
		assertThat(savedAnswer.question()).isEqualTo(question);
		assertThat(savedAnswer.createdAt()).isNotNull();
		assertThat(savedAnswer.modifiedAt()).isNotNull();
	}

	@DisplayName("답변 조회 테스트")
	@Test
	void find_id_answer() {
	    //given
	    String content = "testAnswerContent";
		Member member = memberRepository.findById(1L).get();
		Question question = questionRepository.findById(1L).get();

		Answer givenAnswer = Answer.builder()
			.content(content)
			.author(member)
			.question(question)
			.build();

		Answer savedAnswer = answerRepository.save(givenAnswer);
	    //when
		Answer findAnswer = answerRepository.findById(savedAnswer.id()).get();

	    //then
		assertThat(findAnswer).isEqualTo(savedAnswer);
	}
}

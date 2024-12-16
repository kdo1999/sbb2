package com.sbb2.infrastructer.question.repository;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.member.repository.MemberRepositoryImpl;
import com.sbb2.member.domain.Member;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import(JpaAudtingConfig.class)
public class QuestionRepositoryTest {
	private static MemberRepository memberRepository;
	private final QuestionRepository questionRepository;

	@Autowired
	public QuestionRepositoryTest(QuestionRepository questionRepository, MemberRepository memberRepository) {
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
	}

	@BeforeAll
	static void initMember() {
		String username = "testUsername";
		String password = "testPassword";
		String email = "testEmail";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);
	}

	@DisplayName("질문 저장 테스트")
	@Test
	void save_question() {
	    //given
		String subject = "testSubject1";
		String content = "testContent1";
		Member author = memberRepository.findById(1L).get();

	    //when
		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(question);

	    //then
		assertThat(savedQuestion.subject()).isEqualTo(savedQuestion.subject());
		assertThat(savedQuestion.content()).isEqualTo(question.content());
		assertThat(savedQuestion.author()).isEqualTo(question.author());
		assertThat(savedQuestion.createdAt()).isNotNull();
		assertThat(savedQuestion.modifieddAt()).isNotNull();
	}
}

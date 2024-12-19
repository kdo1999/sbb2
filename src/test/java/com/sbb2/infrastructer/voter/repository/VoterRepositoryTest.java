package com.sbb2.infrastructer.voter.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

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
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;

import jakarta.persistence.EntityManager;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class VoterRepositoryTest {
	private final MemberRepository memberRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final VoterRepository voterRepository;
	private final EntityManager em;

	@Autowired
	public VoterRepositoryTest(MemberRepository memberRepository, QuestionRepository questionRepository,
		AnswerRepository answerRepository, VoterRepository voterRepository, EntityManager em) {
		this.memberRepository = memberRepository;
		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
		this.voterRepository = voterRepository;
		this.em = em;
	}
	@BeforeAll
	void dataInit() {
		Member member = Member.builder()
			.email("testEmail")
			.username("testUsername")
			.password("testPassword")
			.build();
		Member savedMember = memberRepository.save(member);

		Question question = Question.builder()
			.subject("testSubject")
			.content("testContent")
			.author(savedMember)
			.build();
		questionRepository.save(question);
	}

	@DisplayName("질문 추천 성공 테스트")
	@Test
	void save_question_voter_success() {
	    //given
		Member member = memberRepository.findById(1L).get();
		Question findQuestion = questionRepository.findById(1L).get();

		Voter voter = Voter.builder()
			.question(findQuestion)
			.member(member)
			.build();

		//when
		Voter savedVoter = voterRepository.save(voter);

		//then
		assertThat(savedVoter.id()).isNotNull();
		assertThat(savedVoter.question().id()).isEqualTo(findQuestion.id());
		assertThat(savedVoter.member()).isEqualTo(member);
	}

	@DisplayName("질문 추천 삭제 테스트")
	@Test
	void delete_question_voter_success() {
		//given
		Member member = memberRepository.findById(1L).get();
		Question findQuestion = questionRepository.findById(1L).get();

		Voter voter = Voter.builder()
			.question(findQuestion)
			.member(member)
			.build();

		Voter savedVoter = voterRepository.save(voter);

		//when
		voterRepository.deleteById(savedVoter.id());

		//then
		Optional<Voter> findVoter = voterRepository.findById(savedVoter.id());
		assertThat(findVoter.isEmpty()).isTrue();
	}
}

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
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.Voter;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
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
		String username = "testUsername41241231";
		String password = "testPassword";
		String email = "testEmail3124124";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		String questionSubject = "testSubject";
		String questionContent = "testContent";
		Member author = memberRepository.findById(1L).get();

		for (int i = 0; i < 4; i++) {
			Question question = Question.builder()
				.subject(questionSubject + (i + 1))
				.content(questionContent + (i + 1))
				.author(author)
				.build();

			questionRepository.save(question);
		}

		Question question = questionRepository.findById(1L).get();
		String answerContent = "answerContent";

		for (int i = 0; i < 4; i++) {
			Answer givenAnswer = Answer.builder()
			.content(answerContent + (i + 1))
			.author(author)
			.question(question)
			.build();

			answerRepository.save(givenAnswer);
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

	@DisplayName("답변 수정 테스트")
	@Test
	void update_answer() {
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
		String updateContent = "updateTestContent";

		savedAnswer = savedAnswer.fetch(
			Answer.builder()
				.content(updateContent)
				.build()
		);
		
		Answer updatedAnswer = answerRepository.save(savedAnswer);

		//then
		assertThat(updatedAnswer.content()).isEqualTo(updateContent);
		assertThat(updatedAnswer.id()).isEqualTo(savedAnswer.id());
	}

	@DisplayName("답변 추천 테스트")
	@Test
	void save_voter_answer() {
	    //given
		Member member = memberRepository.findById(1L).get();
		Answer answer = answerRepository.findById(1L).get();
		Voter voter = Voter.builder()
			.member(member)
			.build();

		//when
		answer.addVoter(voter);
		Answer savedAnswer = answerRepository.save(answer);

		//then
		Voter savedVoter = savedAnswer.voterSet().iterator().next();
		assertThat(savedVoter.id()).isNotNull();
		assertThat(savedVoter.member()).isEqualTo(member);
		assertThat(savedVoter.answer()).isNotNull();
		assertThat(savedVoter.question()).isNull();
	}
}
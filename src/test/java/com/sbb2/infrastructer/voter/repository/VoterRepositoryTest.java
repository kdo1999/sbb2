package com.sbb2.infrastructer.voter.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
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

import com.sbb2.answer.domain.Answer;
import com.sbb2.category.domain.Category;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
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
	private final CategoryRepository categoryRepository;
	private final EntityManager em;

	@Autowired
	public VoterRepositoryTest(MemberRepository memberRepository, QuestionRepository questionRepository,
		AnswerRepository answerRepository, VoterRepository voterRepository, CategoryRepository categoryRepository, EntityManager em) {
		this.memberRepository = memberRepository;
		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
		this.voterRepository = voterRepository;
		this.categoryRepository = categoryRepository;
		this.em = em;
	}
	@BeforeAll
	void dataInit() {
		Member member1 = Member.builder()
			.email("testEmail")
			.username("testUsername")
			.password("testPassword")
			.build();
		Member savedMember1 = memberRepository.save(member1);

		Member member2 = Member.builder()
			.email("testEmail2")
			.username("testUsername2")
			.password("testPassword2")
			.build();
		Member savedMember2 = memberRepository.save(member2);

		Category category1 = Category.builder()
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		Category category2 = Category.builder()
			.categoryName(CategoryName.LECTURE_BOARD)
			.build();

		Category savedCategory = categoryRepository.save(category1);
		categoryRepository.save(category2);


		Question question1 = Question.builder()
			.subject("testSubject")
			.content("testContent")
			.category(savedCategory)
			.author(savedMember1)
			.build();
		Question savedQuestion1 = questionRepository.save(question1);

		Question question2 = Question.builder()
			.subject("testSubject")
			.content("testContent")
			.category(savedCategory)
			.author(savedMember1)
			.build();
		Question savedQuestion2 = questionRepository.save(question2);

		Answer answer1 = Answer.builder()
			.content("testAnswer")
			.question(savedQuestion1)
			.author(savedMember1)
			.build();
		answerRepository.save(answer1);

		Answer answer2 = Answer.builder()
			.content("testAnswer")
			.question(savedQuestion2)
			.author(savedMember1)
			.build();
		answerRepository.save(answer2);
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

	@DisplayName("질문ID로 추천 조회 테스트")
	@Test
	void find_questionId_voter_success() {
	    //given
	    Member member = memberRepository.findById(1L).get();
		Question findQuestion1 = questionRepository.findById(1L).get();
		Question findQuestion2 = questionRepository.findById(2L).get();

		Voter voter1 = Voter.builder()
			.question(findQuestion1)
			.member(member)
			.build();

		Voter voter2 = Voter.builder()
			.question(findQuestion2)
			.member(member)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);

	    //when
		List<Voter> findVoterList = voterRepository.findByQuestionId(findQuestion1.id());

		//then
	    assertThat(findVoterList.size()).isEqualTo(1);
		assertThat(findVoterList.get(0)).isEqualTo(savedVoter1);
		assertThat(findVoterList.get(0)).isNotEqualTo(savedVoter2);
	}

	@DisplayName("질문ID와 회원ID로 추천 조회 테스트")
	@Test
	void find_questionId_and_memberId_voter_success() {
	    //given
	    Member member = memberRepository.findById(1L).get();
		Question findQuestion1 = questionRepository.findById(1L).get();

		Voter voter1 = Voter.builder()
			.question(findQuestion1)
			.member(member)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);

	    //when
		Voter findVoter = voterRepository.findByQuestionIdAndMemberId(findQuestion1.id(), member.id()).get();

		//then
		assertThat(findVoter).isEqualTo(savedVoter1);
	}

	@DisplayName("추천 ID로 조회 성공 테스트")
	@Test
	void find_voterId_voter_success() {
	    //given
		Member member = memberRepository.findById(1L).get();
		Question findQuestion = questionRepository.findById(1L).get();

		Voter voter = Voter.builder()
			.question(findQuestion)
			.member(member)
			.build();

		Voter savedVoter = voterRepository.save(voter);

	    //when
		Voter findVoter = voterRepository.findById(savedVoter.id()).get();

		//then
	    assertThat(findVoter).isEqualTo(savedVoter);
	}

	@DisplayName("답변 추천 성공 테스트")
	@Test
	void save_answer_voter_success() {
	    //given
		Answer findAnswer = answerRepository.findById(1L).get();
		Member findMember = memberRepository.findById(1L).get();

		Voter voter = Voter.builder()
			.answer(findAnswer)
			.member(findMember)
			.build();

		//when
		Voter savedVoter = voterRepository.save(voter);

		//then
		assertThat(savedVoter.answer().id()).isEqualTo(voter.answer().id());
		assertThat(savedVoter.member()).isEqualTo(findMember);
	}

	@DisplayName("답변 ID로 조회 성공 테스트")
	@Test
	void find_answerId_voter_success() {
	    //given
		Answer findAnswer1 = answerRepository.findById(1L).get();
		Answer findAnswer2 = answerRepository.findById(2L).get();
		Member findMember = memberRepository.findById(1L).get();

		Voter voter1 = Voter.builder()
			.answer(findAnswer1)
			.member(findMember)
			.build();

		Voter voter2 = Voter.builder()
			.answer(findAnswer2)
			.member(findMember)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);

		//when
		List<Voter> findVoterList = voterRepository.findByAnswerId(findAnswer1.id());

		//then
	    assertThat(findVoterList.size()).isEqualTo(1);
		assertThat(findVoterList.get(0)).isEqualTo(savedVoter1);
		assertThat(findVoterList.get(0)).isNotEqualTo(savedVoter2);
	}

	@DisplayName("질문ID와 회원ID로 추천 조회 성공 테스트")
	@Test
	void find_answerId_and_memberId_voter_success() {
	    //given
		Answer findAnswer1 = answerRepository.findById(1L).get();
		Answer findAnswer2 = answerRepository.findById(2L).get();
		Member findMember1 = memberRepository.findById(1L).get();
		Member findMember2 = memberRepository.findById(2L).get();

		Voter voter1 = Voter.builder()
			.answer(findAnswer1)
			.member(findMember1)
			.build();

		Voter voter2 = Voter.builder()
			.answer(findAnswer2)
			.member(findMember1)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);

		//when
		Voter findVoter = voterRepository.findByAnswerIdAndMemberId(findAnswer1.id(), findMember1.id()).get();

		//then
	    assertThat(findVoter).isEqualTo(savedVoter1);
	}

	@DisplayName("답변 ID와 멤버 ID가 일치하는 추천 데이터가 있을 때 테스트")
	@Test
	void exists_answerId_memberId_voter_success() {
	    //given
		Answer findAnswer1 = answerRepository.findById(1L).get();
		Answer findAnswer2 = answerRepository.findById(2L).get();
		Member findMember = memberRepository.findById(1L).get();

		Voter voter1 = Voter.builder()
			.answer(findAnswer1)
			.member(findMember)
			.build();

		Voter voter2 = Voter.builder()
			.answer(findAnswer2)
			.member(findMember)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);

		//when
		Boolean result = voterRepository.existsByAnswerIdAndMemberId(findAnswer1.id(), findMember.id());

		//then
	    assertThat(result).isTrue();
	}

	@DisplayName("답변 ID와 멤버 ID가 일치하는 추천이 없을 때 테스트")
	@Test
	void exists_answerId_memberId_voter_false_success() {
	    //given
		Answer findAnswer1 = answerRepository.findById(1L).get();
		Answer findAnswer2 = answerRepository.findById(2L).get();
		Member findMember1 = memberRepository.findById(1L).get();
		Member findMember2 = memberRepository.findById(2L).get();

		Voter voter1 = Voter.builder()
			.answer(findAnswer1)
			.member(findMember1)
			.build();

		Voter voter2 = Voter.builder()
			.answer(findAnswer2)
			.member(findMember1)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);

		//when
		Boolean result = voterRepository.existsByQuestionIdAndMemberId(findAnswer1.id(), findMember2.id());

		//then
	    assertThat(result).isFalse();
	}

	@DisplayName("질문 ID와 멤버 ID가 일치하는 추천 조회가 됐을 때 테스트")
	@Test
	void exists_questionId_memberId_voter_success() {
	    //given
		Member member1 = memberRepository.findById(1L).get();
		Member member2 = memberRepository.findById(2L).get();
		Question findQuestion1 = questionRepository.findById(1L).get();

		Voter voter1 = Voter.builder()
			.question(findQuestion1)
			.member(member1)
			.build();

		Voter voter2 = Voter.builder()
			.question(findQuestion1)
			.member(member2)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);


		//when
		Boolean result = voterRepository.existsByQuestionIdAndMemberId(findQuestion1.id(), member1.id());

		//then
	    assertThat(result).isTrue();
	}

	@DisplayName("질문 ID와 멤버 ID가 일치하는 추천이 없을 때 테스트")
	@Test
	void exists_questionId_memberId_voter_false_success() {
	    //given
		Member member1 = memberRepository.findById(1L).get();
		Member member2 = memberRepository.findById(2L).get();
		Question findQuestion1 = questionRepository.findById(1L).get();
		Question findQuestion2 = questionRepository.findById(2L).get();

		Voter voter1 = Voter.builder()
			.question(findQuestion1)
			.member(member1)
			.build();

		Voter voter2 = Voter.builder()
			.question(findQuestion1)
			.member(member2)
			.build();

		Voter savedVoter1 = voterRepository.save(voter1);
		Voter savedVoter2 = voterRepository.save(voter2);


		//when
		Boolean result = voterRepository.existsByQuestionIdAndMemberId(findQuestion2.id(), member1.id());

		//then
	    assertThat(result).isFalse();
	}
}

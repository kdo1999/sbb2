package com.sbb2.infrastructer.question.repository;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.voter.domain.Voter;

import jakarta.persistence.EntityManager;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class QuestionRepositoryTest {
	private final MemberRepository memberRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final VoterRepository voterRepository;
	private final EntityManager em;

	@Autowired
	public QuestionRepositoryTest(QuestionRepository questionRepository, MemberRepository memberRepository,
		AnswerRepository answerRepository, VoterRepository voterRepository, EntityManager em) {
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.answerRepository = answerRepository;
		this.voterRepository = voterRepository;
		this.em = em;
	}

	@BeforeAll
	void initMember() {
		String username = "testUsername123";
		String password = "testPassword123";
		String email = "testEmail123";

		String username2 = "testUsername1234";
		String password2 = "testPassword1234";
		String email2 = "testEmail1234";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member member2 = Member.builder()
			.username(username2)
			.password(password2)
			.email(email2)
			.build();

		Member savedMember = memberRepository.save(member);
		Member savedMember2 = memberRepository.save(member2);

		String subject = "testSubject";
		String content = "testContent";
		Member author = memberRepository.findById(1L).get();

		for (int i = 0; i < 20; i++) {
			Question question = Question.builder()
				.subject(subject + i + 1)
				.content(content + i + 1)
				.author(author)
				.build();

			questionRepository.save(question);
		}

		for (int i = 0; i < 5; i++) {
			Question question = Question.builder()
				.subject("searchSubject" + i + 1)
				.content("searchContent" + i + 1)
				.author(author)
				.build();

			questionRepository.save(question);
		}

		Question question = questionRepository.findById(21L).get();
		String answerContent = "answerContent";

		for (int i = 0; i < 5; i++) {
			answerRepository.save(Answer.builder()
				.content(answerContent)
				.question(question)
				.author(author)
				.build());
		}

		Voter voter = Voter.builder()
			.question(question)
			.member(savedMember2)
			.build();

		voterRepository.save(voter);
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
		assertThat(savedQuestion.subject()).isEqualTo(subject);
		assertThat(savedQuestion.content()).isEqualTo(content);
		assertThat(savedQuestion.author()).isEqualTo(author);
		assertThat(savedQuestion.createdAt()).isNotNull();
		assertThat(savedQuestion.modifiedAt()).isNotNull();
	}

	@DisplayName("질문 키워드 조회 테스트")
	@Test
	void find_keyword_question() {
		//given
		String keyword = "search";
		int page = 0;

		SearchCondition searchCondition = SearchCondition.builder()
			.kw(keyword)
			.pageNum(page)
			.sort("createdAt")
			.order("desc")
			.build();

		//when
		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);
		Page<QuestionPageResponse> questionPage = questionRepository.findAll(searchCondition, pageable);
		questionPage.getContent().iterator().forEachRemaining(System.out::println);

		//then
		assertThat(questionPage.getTotalPages()).isEqualTo(1);
		assertThat(questionPage.getContent().size()).isEqualTo(5);
	}

	@DisplayName("질문 전체 조회 테스트")
	@Test
	void find_question() {
		//given
		String keyword = "";
		int page = 0;

		SearchCondition searchCondition = SearchCondition.builder()
			.kw(keyword)
			.pageNum(page)
			.sort("createdAt")
			.order("desc")
			.build();
		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);

		//when
		Page<QuestionPageResponse> questionPage = questionRepository.findAll(searchCondition, pageable);

		//then
		assertThat(questionPage.getTotalPages()).isEqualTo(3);
		assertThat(questionPage.getContent().size()).isEqualTo(10);
	}


	@DisplayName("질문 ID 조회 테스트")
	@Test
	void find_id_question() {
		//given
		String subject = "testSubject1";
		String content = "testContent1";
		Member author = memberRepository.findById(1L).get();

		Question givenQuestion = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(givenQuestion);

		//when
		Question findQuestion = questionRepository.findById(savedQuestion.id()).get();

		//then
		assertThat(findQuestion).isEqualTo(savedQuestion);
	}

	@DisplayName("질문 수정 테스트")
	@Test
	void update_question() {
		//given
		String subject = "testSubject1";
		String content = "testContent1";
		String updateSubject = "updateSubject";
		String updateContent = "updateContent";
		Member author = memberRepository.findById(1L).get();

		Question givenQuestion = Question.builder()
			.subject(subject)
			.content(content)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(givenQuestion);
		//when
		savedQuestion = savedQuestion.fetch(updateSubject, updateContent);

		Question updateQuestion = questionRepository.save(savedQuestion);

		//then
		assertThat(updateQuestion.subject()).isEqualTo(updateSubject);
		assertThat(updateQuestion.content()).isEqualTo(updateContent);
		assertThat(updateQuestion.id()).isEqualTo(savedQuestion.id());
	}

	@DisplayName("질문 추천 조회")
	@Test
	void find_voter_question() {
		//given
		Member member = memberRepository.findById(1L).get();
		Question question = questionRepository.findById(1L).get();
		Voter voter = Voter.builder()
			.question(question)
			.member(member)
			.build();

		//when
		Voter savedVoter = voterRepository.save(voter);
		em.flush();
		em.clear();

		Question savedQuestion = questionRepository.findById(question.id()).get();

		//then
		Voter findVoter = savedQuestion.voterSet().iterator().next();
		assertThat(findVoter).isEqualTo(savedVoter);
	}

	@DisplayName("질문 삭제 테스트")
	@Test
	void delete_question() {
		//given
		Question question = questionRepository.findById(1L).get();

		//when
		questionRepository.deleteById(question.id());

		//then
		Optional<Question> findQuestion = questionRepository.findById(question.id());
		assertThat(findQuestion.isEmpty()).isTrue();
	}

	@DisplayName("질문 삭제시 댓글 삭제 성공 테스트")
	@Test
	void delete_question_answer_success() {
		//given
		Member member = memberRepository.findById(1L).get();
		Question question = Question.builder()
			.subject("givenSubject")
			.content("givenContent")
			.author(member)
			.build();

		Question savedQuestion = questionRepository.save(question);
		String answerContent = "answerContent";

		for (int i = 0; i < 5; i++) {
			answerRepository.save(Answer.builder()
				.content(answerContent)
				.question(savedQuestion)
				.author(member)
				.build());
		}

		em.flush();
		em.clear();
		//when
		questionRepository.deleteById(savedQuestion.id());

		//then
		Optional<Question> findQuestionOptional = questionRepository.findById(savedQuestion.id());
		List<Answer> findAnswerList = answerRepository.findByQuestionId(savedQuestion.id());
		assertThat(findQuestionOptional.isEmpty()).isTrue();
		assertThat(findAnswerList.isEmpty()).isTrue();
	}

	@DisplayName("질문 삭제시 추천 삭제 성공 테스트")
	@Test
	void delete_question_voter_success() {
		//given
		Member member = memberRepository.findById(1L).get();
		Question question = Question.builder()
			.subject("givenSubject")
			.content("givenContent")
			.author(member)
			.build();

		Question savedQuestion = questionRepository.save(question);
		String answerContent = "answerContent";

		Voter voter = Voter.builder()
			.member(member)
			.question(savedQuestion)
			.build();
		Voter savedVoter = voterRepository.save(voter);

		em.flush();
		em.clear();

		//when
		questionRepository.deleteById(savedQuestion.id());

		//then
		List<Voter> findVoterList = voterRepository.findByQuestionId(savedQuestion.id());
		Optional<Question> findQuestion = questionRepository.findById(savedQuestion.id());

		assertThat(findVoterList.isEmpty()).isTrue();
		assertThat(findQuestion.isEmpty()).isTrue();
	}

	@DisplayName("추천한 사용자가 질문을 조회하면 isVoter가 true인 테스트")
	@Test
	void find_detail_success() {
		//given
		Member member = memberRepository.findById(2L).get();
		Question question = questionRepository.findById(21L).get();

		//when
		QuestionDetailResponse detailById = questionRepository.findDetailById(question.id(), member.id());

		//then
		assertThat(detailById.isVoter()).isTrue();
	}
}

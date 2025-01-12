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
import com.sbb2.category.domain.Category;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;
import com.sbb2.voter.domain.Voter;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Slf4j
public class QuestionRepositoryTest {
	private final MemberRepository memberRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final VoterRepository voterRepository;
	private final CategoryRepository categoryRepository;
	private final EntityManager em;

	@Autowired
	public QuestionRepositoryTest(QuestionRepository questionRepository, MemberRepository memberRepository,
		AnswerRepository answerRepository, VoterRepository voterRepository, CategoryRepository categoryRepository,
		EntityManager em) {
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.answerRepository = answerRepository;
		this.voterRepository = voterRepository;
		this.categoryRepository = categoryRepository;
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

		Category category1 = Category.builder()
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		Category category2 = Category.builder()
			.categoryName(CategoryName.LECTURE_BOARD)
			.build();

		Category category3 = Category.builder()
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		Category savedCategory1 = categoryRepository.save(category1);
		Category savedCategory2 = categoryRepository.save(category2);
		categoryRepository.save(category3);

		for (int i = 0; i < 20; i++) {
			Question question = Question.builder()
				.subject(subject + i + 1)
				.content(content + i + 1)
				.category(savedCategory1)
				.author(savedMember)
				.build();

			questionRepository.save(question);
		}

		for (int i = 0; i < 5; i++) {
			Question question = Question.builder()
				.subject("searchSubject" + i + 1)
				.content("searchContent" + i + 1)
				.category(savedCategory2)
				.author(savedMember2)
				.build();

			questionRepository.save(question);
		}

		Question question = questionRepository.findById(21L).get();
		String answerContent = "answerContent";

		for (int i = 0; i < 5; i++) {
			answerRepository.save(Answer.builder()
				.content(answerContent)
				.question(question)
				.author(savedMember)
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
		Member givenAuthor = memberRepository.findById(1L).get();
		Category givenCategory = categoryRepository.findById(1L).get();

		//when
		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.category(givenCategory)
			.author(givenAuthor)
			.build();

		Question savedQuestion = questionRepository.save(question);

		//then
		assertThat(savedQuestion.subject()).isEqualTo(subject);
		assertThat(savedQuestion.content()).isEqualTo(content);
		assertThat(savedQuestion.author()).isEqualTo(givenAuthor);
		assertThat(savedQuestion.category()).isEqualTo(givenCategory);
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
			.categoryId(2L)
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

	@DisplayName("질문 카테고리 조회 테스트")
	@Test
	void find_categoryName_sort_order_question() {
		//given
		int page = 0;

		SearchCondition searchCondition = SearchCondition.builder()
			.pageNum(page)
			.categoryId(2L)
			.sort("modifiedAt")
			.order("asc")
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
	void findAll_question() {
		//given
		String keyword = "";
		int page = 0;

		SearchCondition searchCondition = SearchCondition.builder()
			.kw(keyword)
			.pageNum(page)
			.build();
		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);

		//when
		Page<QuestionPageResponse> questionPage = questionRepository.findAll(searchCondition, pageable);

		//then
		assertThat(questionPage.getTotalPages()).isEqualTo(3);
		assertThat(questionPage.getContent().size()).isEqualTo(10);
	}

	@DisplayName("질문 회원 이름으로 전체 조회 성공 테스트")
	@Test
	void findAll_question_username_success() {
		//given
		String keyword = "";
		int page = 0;
		Member findMember = memberRepository.findById(2L).get();

		SearchCondition searchCondition = SearchCondition.builder()
			.username(findMember.username())
			.pageNum(0)
			.build();
		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);

		//when
		Page<QuestionPageResponse> questionPage = questionRepository.findAll(searchCondition, pageable);

		//then
		assertThat(questionPage.getTotalPages()).isEqualTo(1);
		assertThat(questionPage.getContent().size()).isEqualTo(5);
	}

	@DisplayName("질문 ID 조회 테스트")
	@Test
	void find_id_question() {
		//given
		String givenSubject = "testSubject1";
		String givenContent = "testContent1";
		Member givenAuthor = memberRepository.findById(1L).get();
		Category givenCategory = categoryRepository.findById(1L).get();

		Question givenQuestion = Question.builder()
			.subject(givenSubject)
			.content(givenContent)
			.author(givenAuthor)
			.category(givenCategory)
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
		Category givenCategory = categoryRepository.findById(1L).get();
		Category updateCategory = categoryRepository.findById(2L).get();
		Member author = memberRepository.findById(1L).get();

		Question givenQuestion = Question.builder()
			.subject(subject)
			.content(content)
			.category(givenCategory)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(givenQuestion);
		//when
		savedQuestion = savedQuestion.fetch(updateSubject, updateContent, updateCategory);

		Question updateQuestion = questionRepository.save(savedQuestion);

		//then
		assertThat(updateQuestion.subject()).isEqualTo(updateSubject);
		assertThat(updateQuestion.content()).isEqualTo(updateContent);
		assertThat(updateQuestion.category()).isEqualTo(updateCategory);
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
		assertThat(findVoter.question().id()).isEqualTo(savedVoter.question().id());
		assertThat(findVoter.question().author()).isEqualTo(savedVoter.question().author());
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
		Category givenCategory = categoryRepository.findById(1L).get();
		Question question = Question.builder()
			.subject("givenSubject")
			.content("givenContent")
			.category(givenCategory)
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
		Category givenCategory = categoryRepository.findById(1L).get();
		Question question = Question.builder()
			.subject("givenSubject")
			.content("givenContent")
			.category(givenCategory)
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

	@DisplayName("질문 조회수 증가 성공 테스트")
	@Test
	void increment_viewCount_success () {
	    //given
		Question findQuestion = questionRepository.findById(1L).get();

		//when
		questionRepository.incrementViewCount(findQuestion.id());
		em.flush();
		em.clear();

		//then
		Question incrementQuestion = questionRepository.findById(1L).get();
		assertThat(incrementQuestion.viewCount()).isEqualTo(1L);
	}
}

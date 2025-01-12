package com.sbb2.infrastructer.answer.repository;

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
import com.sbb2.answer.service.response.AnswerDetailResponse;
import com.sbb2.category.domain.Category;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Slf4j
public class AnswerRepositoryTest {
	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;
	private final MemberRepository memberRepository;
	private final VoterRepository voterRepository;
	private final CategoryRepository categoryRepository;

	@Autowired
	public AnswerRepositoryTest(AnswerRepository answerRepository, QuestionRepository questionRepository,
		MemberRepository memberRepository, VoterRepository voterRepository, CategoryRepository categoryRepository) {
		this.answerRepository = answerRepository;
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.voterRepository = voterRepository;
		this.categoryRepository = categoryRepository;
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

		Member member2 = Member.builder()
			.username("testUsername2")
			.password("testPassword")
			.email("testEmail2@naver.com")
			.build();

		Member savedMember = memberRepository.save(member);
		Member savedMember2 = memberRepository.save(member2);

		Category category1 = Category.builder()
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		Category category2 = Category.builder()
			.categoryName(CategoryName.LECTURE_BOARD)
			.build();

		Category savedCategory = categoryRepository.save(category1);
		categoryRepository.save(category2);

		String questionSubject = "testSubject";
		String questionContent = "testContent";

		for (int i = 0; i < 4; i++) {
			Question question = Question.builder()
				.subject(questionSubject + (i + 1))
				.content(questionContent + (i + 1))
				.category(savedCategory)
				.author(savedMember)
				.build();

			questionRepository.save(question);
		}

		Question question = questionRepository.findById(1L).get();
		String answerContent = "answerContent";

		for (int i = 0; i < 30; i++) {
			Answer givenAnswer = Answer.builder()
				.content(answerContent + (i + 1))
				.author(savedMember)
				.question(question)
				.build();

			Voter givenVoter = Voter.builder()
				.member(savedMember)
				.answer(givenAnswer)
				.build();

			givenAnswer.voterSet().add(givenVoter);

			answerRepository.save(givenAnswer);
		}

		Answer givenAnswer = Answer.builder()
			.content(answerContent)
			.author(savedMember2)
			.question(question)
			.build();

		Voter givenVoter = Voter.builder()
			.member(savedMember2)
			.answer(givenAnswer)
			.build();

		givenAnswer.voterSet().add(givenVoter);

		answerRepository.save(givenAnswer);

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

		savedAnswer = savedAnswer.fetch(updateContent);

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

	@DisplayName("답변 삭제시 추천 삭제 성공 테스트")
	@Test
	void delete_answer_voter_success() {
		//given
		Member member = memberRepository.findById(1L).get();
		Question question = questionRepository.findById(1L).get();
		Answer givenAnswer = Answer.builder()
			.content("givenAnswer")
			.author(member)
			.question(question)
			.build();

		Answer savedAnswer = answerRepository.save(givenAnswer);

		Voter voter = Voter.builder()
			.member(member)
			.build();
		savedAnswer.addVoter(voter);

		savedAnswer = answerRepository.save(savedAnswer);

		//when
		answerRepository.deleteById(savedAnswer.id());

		//then
		Optional<Answer> findAnswerOptional = answerRepository.findById(savedAnswer.id());
		List<Voter> findVoterList = voterRepository.findByAnswerId(savedAnswer.id());

		assertThat(findAnswerOptional.isEmpty()).isTrue();
		assertThat(findVoterList.isEmpty()).isTrue();
	}

	@DisplayName("응답용 답변 조회 성공 테스트")
	@Test
	void find_answerDetailResponse_success() {
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
		AnswerDetailResponse answerDetailResponse = answerRepository.findAnswerDetailByIdAndMemberId(savedAnswer.id(),
			savedAnswer.author().id()).get();

		//then
		assertThat(answerDetailResponse.id()).isEqualTo(savedAnswer.id());
		assertThat(answerDetailResponse.content()).isEqualTo(savedAnswer.content());
		assertThat(answerDetailResponse.createdAt()).isEqualTo(savedAnswer.createdAt());
		assertThat(answerDetailResponse.modifiedAt()).isEqualTo(savedAnswer.modifiedAt());
		assertThat(answerDetailResponse.questionId()).isEqualTo(savedAnswer.question().id());
		assertThat(answerDetailResponse.isAuthor()).isEqualTo(savedAnswer.author().id().equals(member.id()));
		assertThat(answerDetailResponse.isVoter()).isEqualTo(savedAnswer.voterSet().stream()
			.anyMatch(voter -> voter.member().id().equals(member.id())));
		assertThat(answerDetailResponse.voterCount()).isEqualTo(savedAnswer.voterSet().size());
	}

	@DisplayName("답변 회원 이름으로 페이징 조회 성공 테스트")
	@Test
	void find_answerDetailPage_username_success() {
		//given
		Member findMember = memberRepository.findById(2L).get();

		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(0)
			.username(findMember.username())
			.build();
		Long questionId = 1L;
		Long loginMemberId = 1L;

		Pageable pageable = PageRequest.of(givenSearchCondition.pageNum(), 10);

		//when
		Page<AnswerDetailResponse> answerDetailResponsePage = answerRepository.findAnswerDetailPageByQuestionId(
			givenSearchCondition, questionId, loginMemberId, pageable);

		//then
		assertThat(answerDetailResponsePage.getTotalPages()).isEqualTo(1);
		assertThat(answerDetailResponsePage.getContent().size()).isEqualTo(1);
	}

	@DisplayName("답변 페이징 조회 성공 테스트")
	@Test
	void find_answerDetailPage_success() {
		//given
		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(0)
			.build();
		Long questionId = 1L;
		Long loginMemberId = 1L;

		Pageable pageable = PageRequest.of(givenSearchCondition.pageNum(), 10);

		//when
		Page<AnswerDetailResponse> answerDetailResponsePage = answerRepository.findAnswerDetailPageByQuestionId(
			givenSearchCondition, questionId, loginMemberId, pageable);

		//then
		assertThat(answerDetailResponsePage.getTotalPages()).isEqualTo(4);
		assertThat(answerDetailResponsePage.getContent().size()).isEqualTo(10);
	}

	@DisplayName("답변 페이징 키워드 조회 성공 테스트")
	@Test
	void find_answerDetailPage_kw_success() {
		//given
		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(0)
			.kw("1")
			.build();
		Long questionId = 1L;
		Long loginMemberId = 1L;

		Pageable pageable = PageRequest.of(givenSearchCondition.pageNum(), 10);

		//when
		Page<AnswerDetailResponse> answerDetailResponsePage = answerRepository.findAnswerDetailPageByQuestionId(
			givenSearchCondition, questionId, loginMemberId, pageable);

		//then
		assertThat(answerDetailResponsePage.getTotalPages()).isEqualTo(2);
		assertThat(answerDetailResponsePage.getContent().size()).isEqualTo(10);
		assertThat(answerDetailResponsePage.getTotalElements()).isEqualTo(12);
	}
}

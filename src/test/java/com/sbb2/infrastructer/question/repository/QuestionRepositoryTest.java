package com.sbb2.infrastructer.question.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.domain.QuestionPageResponse;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class QuestionRepositoryTest {
	private final MemberRepository memberRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	@Autowired
	public QuestionRepositoryTest(QuestionRepository questionRepository, MemberRepository memberRepository,
		AnswerRepository answerRepository) {
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.answerRepository = answerRepository;
	}

	@BeforeAll
	void initMember() {
		String username = "testUsername123";
		String password = "testPassword123";
		String email = "testEmail123";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

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

		//when
		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createdAt"));
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		Page<QuestionPageResponse> questionPage = questionRepository.findAll(keyword, pageable);
		questionPage.getContent().iterator().forEachRemaining(System.out::println);
		//then
		assertThat(questionPage.getTotalPages()).isEqualTo(1);
		assertThat(questionPage.getContent().size()).isEqualTo(5);
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
		savedQuestion = savedQuestion.fetch(
			Question.builder()
				.subject(updateSubject)
				.content(updateContent)
				.build()
		);

		Question updateQuestion = questionRepository.save(savedQuestion);

		//then
		assertThat(updateQuestion.subject()).isEqualTo(updateSubject);
		assertThat(updateQuestion.content()).isEqualTo(updateContent);
		assertThat(updateQuestion.id()).isEqualTo(savedQuestion.id());
	}
}

package com.sbb2.infrastructer.comment.repository;

import static org.assertj.core.api.Assertions.*;

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
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.answer.domain.Answer;
import com.sbb2.comment.Comment;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import({JpaAudtingConfig.class, QuerydslConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Slf4j
public class CommentRepositoryTest {
	private final CommentRepository commentRepository;
	private final QuestionRepository questionRepository;
	private final MemberRepository memberRepository;
	private final AnswerRepository answerRepository;

	@Autowired
	public CommentRepositoryTest(CommentRepository commentRepository, QuestionRepository questionRepository,
		MemberRepository memberRepository, AnswerRepository answerRepository) {
		this.commentRepository = commentRepository;
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.answerRepository = answerRepository;
	}

	@BeforeAll
	void initData() {
		String username = "testUsername123";
		String password = "testPassword123";
		String email = "testEmail123";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		String questionSubject = "testSubject";
		String questionContent = "testContent";

		Question question1 = Question.builder()
			.subject(questionSubject)
			.content(questionContent)
			.author(savedMember)
			.build();

		Question question2 = Question.builder()
			.subject(questionSubject)
			.content(questionContent)
			.author(savedMember)
			.build();

		Question savedQuestion1 = questionRepository.save(question1);
		Question savedQuestion2 = questionRepository.save(question2);

		String answerContent = "testAnswerContent";

		Answer answer = Answer.builder()
			.content(answerContent)
			.question(savedQuestion1)
			.author(savedMember)
			.build();

		Answer savedAnswer = answerRepository.save(answer);
	}

	@DisplayName("질문 댓글 저장 성공 테스트")
	@Test
	void save_question_comment_success() {
	    //given
		Question question = questionRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.content(commentContent)
			.question(question)
			.author(member)
			.build();

		//when
		Comment savedComment = commentRepository.save(givenComment);

	    //then
		assertThat(savedComment.id()).isNotNull();
		assertThat(savedComment.content()).isEqualTo(givenComment.content());
		assertThat(savedComment.question().id()).isEqualTo(givenComment.question().id());
		assertThat(savedComment.author()).isEqualTo(givenComment.author());
		assertThat(savedComment.answer()).isNull();
		assertThat(savedComment.createdAt()).isNotNull();
		assertThat(savedComment.modifiedAt()).isNotNull();
	}

	@DisplayName("답변 댓글 저장 성공 테스트")
	@Test
	void save_answer_comment_success() {
	    //given
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.content(commentContent)
			.answer(answer)
			.author(member)
			.build();

		//when
		Comment savedComment = commentRepository.save(givenComment);

	    //then
		assertThat(savedComment.id()).isNotNull();
		assertThat(savedComment.content()).isEqualTo(givenComment.content());
		assertThat(savedComment.answer().id()).isEqualTo(givenComment.answer().id());
		assertThat(savedComment.author()).isEqualTo(givenComment.author());
		assertThat(savedComment.question()).isNull();
		assertThat(savedComment.createdAt()).isNotNull();
		assertThat(savedComment.modifiedAt()).isNotNull();
	}

	@DisplayName("댓글 수정 성공 테스트")
	@Test
	void update_comment_success() {
		//given
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();

		String commentContent = "testCommentContent";
		String updateContent = "updateCommentContent";

		Comment givenComment = Comment.builder()
			.content(commentContent)
			.answer(answer)
			.author(member)
			.build();

		Comment savedComment = commentRepository.save(givenComment);

		//when
		Comment fetchComment = savedComment.fetch(updateContent);
		Comment updateComment = commentRepository.save(fetchComment);

		//then
		assertThat(updateComment.id()).isEqualTo(savedComment.id());
		assertThat(updateComment.author().id()).isEqualTo(savedComment.author().id());
		assertThat(updateComment.answer().id()).isEqualTo(savedComment.answer().id());
		assertThat(updateComment.content()).isEqualTo(updateContent);
	}

	@DisplayName("질문 ID로 댓글 조회 성공 테스트")
	@Test
	void find_comment_success() {
		//given
		Question question1 = questionRepository.findById(1L).get();
		Question question2 = questionRepository.findById(2L).get();
		Member member = memberRepository.findById(1L).get();

		String commentContent = "testCommentContent";

		Comment givenComment1 = Comment.builder()
			.content(commentContent)
			.question(question1)
			.author(member)
			.build();

		Comment givenComment2 = Comment.builder()
			.content(commentContent)
			.question(question2)
			.author(member)
			.build();

		Comment savedComment1 = commentRepository.save(givenComment1);
		Comment savedComment2 = commentRepository.save(givenComment2);

		List<Comment> savedCommentList = List.of(savedComment2);

		//when
		List<Comment> findCommentList = commentRepository.findByQuestionId(savedComment2.id()).get();

		//then
		assertThat(findCommentList).isEqualTo(savedCommentList);
	}
}

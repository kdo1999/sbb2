package com.sbb2.infrastructer.comment.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

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
import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.config.JpaAudtingConfig;
import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
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
	private final CategoryRepository categoryRepository;

	@Autowired
	public CommentRepositoryTest(CommentRepository commentRepository, QuestionRepository questionRepository,
		MemberRepository memberRepository, AnswerRepository answerRepository, CategoryRepository categoryRepository) {
		this.commentRepository = commentRepository;
		this.questionRepository = questionRepository;
		this.memberRepository = memberRepository;
		this.answerRepository = answerRepository;
		this.categoryRepository = categoryRepository;
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

		Question question1 = Question.builder()
			.subject(questionSubject)
			.content(questionContent)
			.category(savedCategory)
			.author(savedMember)
			.build();

		Question question2 = Question.builder()
			.subject(questionSubject)
			.content(questionContent)
			.category(savedCategory)
			.author(savedMember)
			.build();

		Question savedQuestion1 = questionRepository.save(question1);
		Question savedQuestion2 = questionRepository.save(question2);

		String answerContent = "testAnswerContent";

		Answer answer1 = Answer.builder()
			.content(answerContent + 1)
			.question(savedQuestion1)
			.author(savedMember)
			.build();

		Answer answer2 = Answer.builder()
			.content(answerContent + 2)
			.question(savedQuestion1)
			.author(savedMember)
			.build();

		Answer savedAnswer1 = answerRepository.save(answer1);
		Answer savedAnswer2 = answerRepository.save(answer2);

		LongStream.range(0, 26).forEach(i -> {
			Comment comment = Comment.builder()
				.rootQuestion(savedQuestion1)
				.content("testCommentContent" + i)
				.question(savedQuestion1)
				.author(savedMember)
				.build();

			commentRepository.save(comment);
		});

		LongStream.range(0, 26).forEach(i -> {
			Comment comment = Comment.builder()
				.rootQuestion(savedQuestion2)
				.content("testCommentContent" + i)
				.answer(savedAnswer1)
				.author(savedMember)
				.build();

			commentRepository.save(comment);
		});
	}

	@DisplayName("질문 댓글 저장 성공 테스트")
	@Test
	void save_question_comment_success() {
		//given
		Question question = questionRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.question(question)
			.author(member)
			.build();

		//when
		Comment savedComment = commentRepository.save(givenComment);

		//then
		assertThat(savedComment.id()).isNotNull();
		assertThat(savedComment.content()).isEqualTo(givenComment.content());
		assertThat(savedComment.rootQuestion().id()).isEqualTo(givenComment.rootQuestion().id());
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
		Question question = questionRepository.findById(1L).get();
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.answer(answer)
			.author(member)
			.build();

		//when
		Comment savedComment = commentRepository.save(givenComment);

		//then
		assertThat(savedComment.id()).isNotNull();
		assertThat(savedComment.rootQuestion().id()).isEqualTo(givenComment.rootQuestion().id());
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
		Question question = questionRepository.findById(1L).get();
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();

		String commentContent = "testCommentContent";
		String updateContent = "updateCommentContent";

		Comment givenComment = Comment.builder()
			.rootQuestion(question)
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
		assertThat(updateComment.rootQuestion().id()).isEqualTo(savedComment.rootQuestion().id());
		assertThat(updateComment.author().id()).isEqualTo(savedComment.author().id());
		assertThat(updateComment.answer().id()).isEqualTo(savedComment.answer().id());
		assertThat(updateComment.content()).isEqualTo(updateContent);
	}

	@DisplayName("질문 ID로 댓글 조회 성공 테스트")
	@Test
	void find_comment_questionId_success() {
		//given
		Question question1 = questionRepository.findById(1L).get();
		Question question2 = questionRepository.findById(2L).get();
		Member member = memberRepository.findById(1L).get();

		String commentContent = "testCommentContent";

		Comment givenComment1 = Comment.builder()
			.rootQuestion(question1)
			.content(commentContent)
			.question(question1)
			.author(member)
			.build();

		Comment givenComment2 = Comment.builder()
			.rootQuestion(question2)
			.content(commentContent)
			.question(question2)
			.author(member)
			.build();

		Comment savedComment1 = commentRepository.save(givenComment1);
		Comment savedComment2 = commentRepository.save(givenComment2);

		List<Comment> savedCommentList = List.of(savedComment2);

		//when
		List<Comment> findCommentList = commentRepository.findByQuestionId(question2.id());

		//then
		assertThat(findCommentList).isEqualTo(savedCommentList);
	}

	@DisplayName("답변 ID로 댓글 조회 성공 테스트")
	@Test
	void find_comment_answerId_success() {
		//given
		Question question = questionRepository.findById(1L).get();
		Answer answer1 = answerRepository.findById(1L).get();
		Answer answer2 = answerRepository.findById(2L).get();
		Member member = memberRepository.findById(1L).get();

		String commentContent = "testCommentContent";

		Comment givenComment1 = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.answer(answer1)
			.author(member)
			.build();

		Comment givenComment2 = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.answer(answer2)
			.author(member)
			.build();

		Comment savedComment1 = commentRepository.save(givenComment1);
		Comment savedComment2 = commentRepository.save(givenComment2);

		List<Comment> savedCommentList = List.of(savedComment2);

		//when
		List<Comment> findCommentList = commentRepository.findByAnswerId(answer2.id());

		//then
		assertThat(findCommentList).isEqualTo(savedCommentList);
	}

	@DisplayName("댓글 조회 성공 테스트")
	@Test
	void find_comment_success() {
		//given
		Question question = questionRepository.findById(1L).get();
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.answer(answer)
			.author(member)
			.build();

		Comment savedComment = commentRepository.save(givenComment);

		//when
		Comment findComment = commentRepository.findById(savedComment.id()).get();

		//then
		assertThat(findComment).isEqualTo(savedComment);
	}

	@DisplayName("댓글 삭제 성공 테스트")
	@Test
	void delete_comment_success() {
		//given
		Question question = questionRepository.findById(1L).get();
		Answer answer = answerRepository.findById(1L).get();
		Member member = memberRepository.findById(1L).get();
		String commentContent = "testCommentContent";

		Comment givenComment = Comment.builder()
			.rootQuestion(question)
			.content(commentContent)
			.answer(answer)
			.author(member)
			.build();

		Comment savedComment = commentRepository.save(givenComment);

		//when
		commentRepository.deleteById(savedComment.id());

		//then
		Optional<Comment> findComment = commentRepository.findById(savedComment.id());
		assertThat(findComment).isEmpty();
	}

	@DisplayName("질문 ID로 댓글 전체 조회 테스트")
	@Test
	void findAll_questionId() {
		//given
		Member findMember = memberRepository.findById(1L).get();
		Question findQuestion = questionRepository.findById(1L).get();

		int page = 0;

		ParentType givenParentType = ParentType.QUESTION;

		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(page)
			.sort("createdAt")
			.order("desc")
			.build();

		Pageable givenPageable = PageRequest.of(givenSearchCondition.pageNum(), 10);

		//when
		Page<CommentResponse> commentResponsePage = commentRepository.findAll(findQuestion.id(), findMember.id(),
			givenParentType, givenPageable, givenSearchCondition);

		//then
		assertThat(commentResponsePage.getTotalPages()).isEqualTo(3);
		assertThat(commentResponsePage.getContent().size()).isEqualTo(10);
		assertThat(commentResponsePage.getTotalElements()).isEqualTo(26);
	}

	@DisplayName("답변 ID로 댓글 전체 조회 테스트")
	@Test
	void findAll_answerId() {
		//given
		Member findMember = memberRepository.findById(1L).get();
		Answer findAnswer = answerRepository.findById(1L).get();

		int page = 0;

		ParentType givenParentType = ParentType.ANSWER;

		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(page)
			.sort("createdAt")
			.order("desc")
			.build();

		Pageable givenPageable = PageRequest.of(givenSearchCondition.pageNum(), 10);

		//when
		Page<CommentResponse> commentResponsePage = commentRepository.findAll(findAnswer.id(), findMember.id(),
			givenParentType, givenPageable, givenSearchCondition);

		//then
		assertThat(commentResponsePage.getTotalPages()).isEqualTo(3);
		assertThat(commentResponsePage.getContent().size()).isEqualTo(10);
		assertThat(commentResponsePage.getTotalElements()).isEqualTo(26);
	}
}

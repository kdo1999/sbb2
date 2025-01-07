package com.sbb2.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.exception.CommentBusinessLogicException;
import com.sbb2.comment.exception.CommentErrorCode;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.comment.repository.CommentRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	private CommentService commentService;

	@BeforeEach
	void setUp() {
		commentService = new CommentServiceImpl(commentRepository, questionRepository, answerRepository);
	}

	@DisplayName("질문 댓글 저장 성공 테스트")
	@Test
	void save_comment_question_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Question givenQuestion = Question.builder().id(1L).build();

		String givenContent = "testContent";


		ParentType givenParentType = ParentType.QUESTION;

		Comment givenComment = Comment.builder()
			.id(1L)
			.content(givenContent)
			.author(givenMember)
			.question(givenQuestion)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(questionRepository.findById(1L))
			.willReturn(Optional.of(givenQuestion));

		given(commentRepository.save(any(Comment.class)))
			.willReturn(givenComment);

		//when
		CommentResponse commentResponse = commentService.save(
			givenQuestion.id(), givenContent, givenParentType, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(1L);
		assertThat(commentResponse.parentId()).isEqualTo(1L);
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenContent);
		assertThat(commentResponse.createdAt()).isNotNull();
		assertThat(commentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(any(Comment.class));
		verify(questionRepository, times(1)).findById(givenQuestion.id());
	}

	@DisplayName("답변 댓글 저장 성공 테스트")
	@Test
	void save_comment_answer_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenComment = Comment.builder()
			.id(1L)
			.content(givenContent)
			.author(givenMember)
			.answer(givenAnswer)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(answerRepository.findById(1L))
			.willReturn(Optional.of(givenAnswer));

		given(commentRepository.save(any(Comment.class)))
			.willReturn(givenComment);

		//when
		CommentResponse commentResponse = commentService.save(
			givenAnswer.id(), givenContent, givenParentType, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(1L);
		assertThat(commentResponse.parentId()).isEqualTo(1L);
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenContent);
		assertThat(commentResponse.createdAt()).isNotNull();
		assertThat(commentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(any(Comment.class));
		verify(answerRepository, times(1)).findById(givenAnswer.id());
	}

	@DisplayName("댓글 저장시 질문이 존재하지 않는 경우 실패 테스트")
	@Test
	void save_comment_question_not_found_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Long givenQuestionId = 1L;

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.QUESTION;

		given(questionRepository.findById(givenQuestionId))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> commentService.save(givenQuestionId, givenContent, givenParentType, givenMember))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("댓글 저장시 답변이 존재하지 않는 경우 실패 테스트")
	@Test
	void save_comment_answer_not_found_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Long givenAnswerId = 1L;

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		given(answerRepository.findById(givenAnswerId))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> commentService.save(givenAnswerId, givenContent, givenParentType, givenMember))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("댓글 저장시 ParentType이 null인 경우 실패 테스트")
	@Test
	void save_comment_parentType_not_support_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Long givenAnswerId = 1L;

		String givenContent = "testContent";

		ParentType givenParentType = null;

		//when & then
		assertThatThrownBy(() -> commentService.save(givenAnswerId, givenContent, givenParentType, givenMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.NOT_SUPPORT.getMessage());
	}

	@DisplayName("답변 댓글 수정 성공 테스트")
	@Test
	void update_comment_answer_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		String givenContent = "testContent";
		String givenUpdateContent = "updateContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenFindComment = Comment.builder()
			.id(1L)
			.content(givenContent)
			.author(givenMember)
			.answer(givenAnswer)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		Comment givenUpdateComment = givenFindComment.fetch(givenUpdateContent);

		given(commentRepository.findById(givenFindComment.id()))
			.willReturn(Optional.of(givenFindComment));

		given(commentRepository.save(givenUpdateComment))
			.willReturn(givenUpdateComment);

		//when
		CommentResponse commentResponse = commentService.update(
			givenAnswer.id(), givenUpdateContent, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(1L);
		assertThat(commentResponse.parentId()).isEqualTo(1L);
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenUpdateContent);
		assertThat(commentResponse.createdAt()).isNotNull();
		assertThat(commentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(givenUpdateComment);
		verify(commentRepository, times(1)).findById(givenFindComment.id());
	}

	@DisplayName("질문 댓글 수정 성공 테스트")
	@Test
	void update_comment_question_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Question givenQuestion = Question.builder().id(1L).build();

		String givenContent = "testContent";
		String givenUpdateContent = "updateContent";

		ParentType givenParentType = ParentType.QUESTION;

		Comment givenFindComment = Comment.builder()
			.id(1L)
			.content(givenContent)
			.author(givenMember)
			.question(givenQuestion)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		Comment givenUpdateComment = givenFindComment.fetch(givenUpdateContent);

		given(commentRepository.findById(givenFindComment.id()))
			.willReturn(Optional.of(givenFindComment));

		given(commentRepository.save(givenUpdateComment))
			.willReturn(givenUpdateComment);

		//when
		CommentResponse commentResponse = commentService.update(
			givenQuestion.id(), givenUpdateContent, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(1L);
		assertThat(commentResponse.parentId()).isEqualTo(1L);
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenUpdateContent);
		assertThat(commentResponse.createdAt()).isNotNull();
		assertThat(commentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(givenUpdateComment);
		verify(commentRepository, times(1)).findById(givenFindComment.id());
	}
}

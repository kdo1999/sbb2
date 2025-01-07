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

import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CreateCommentResponse;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.comment.repository.CommentRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

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
		commentService = new CommentServiceImpl(commentRepository, questionRepository);
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
		CreateCommentResponse createCommentResponse = commentService.save(
			givenQuestion.id(), givenContent, givenParentType, givenMember
		);

		//then
		assertThat(createCommentResponse.commentId()).isEqualTo(1L);
		assertThat(createCommentResponse.parentId()).isEqualTo(1L);
		assertThat(createCommentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(createCommentResponse.content()).isEqualTo(givenContent);
		assertThat(createCommentResponse.createdAt()).isNotNull();
		assertThat(createCommentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(any(Comment.class));
		verify(questionRepository, times(1)).findById(givenQuestion.id());
	}
}

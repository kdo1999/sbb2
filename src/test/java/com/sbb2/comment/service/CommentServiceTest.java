package com.sbb2.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.exception.CommentBusinessLogicException;
import com.sbb2.comment.exception.CommentErrorCode;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.util.SearchCondition;
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
			.rootQuestion(givenQuestion)
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
			givenQuestion.id(), givenQuestion.id(), givenContent, givenParentType, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(givenComment.id());
		assertThat(commentResponse.rootQuestionId()).isEqualTo(givenComment.rootQuestion().id());
		assertThat(commentResponse.parentId()).isEqualTo(givenComment.question().id());
		assertThat(commentResponse.author()).isEqualTo(givenComment.author().username());
		assertThat(commentResponse.isAuthor()).isTrue();
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenComment.content());
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

		Question givenQuestion = Question.builder().id(1L).build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenComment = Comment.builder()
			.id(1L)
			.rootQuestion(givenQuestion)
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

		given(questionRepository.findById(givenComment.rootQuestion().id()))
			.willReturn(Optional.of(givenQuestion));

		//when
		CommentResponse commentResponse = commentService.save(
			givenQuestion.id(), givenAnswer.id(), givenContent, givenParentType, givenMember
		);

		//then
		assertThat(commentResponse.commentId()).isEqualTo(givenComment.id());
		assertThat(commentResponse.rootQuestionId()).isEqualTo(givenComment.rootQuestion().id());
		assertThat(commentResponse.parentId()).isEqualTo(givenComment.answer().id());
		assertThat(commentResponse.author()).isEqualTo(givenComment.author().username());
		assertThat(commentResponse.isAuthor()).isTrue();
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenComment.content());
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
		assertThatThrownBy(
			() -> commentService.save(givenQuestionId, givenQuestionId, givenContent, givenParentType, givenMember))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("댓글 저장시 답변이 존재하지 않는 경우 실패 테스트")
	@Test
	void save_comment_answer_not_found_fail() {
		//given
		Question givenQuestion = Question.builder().id(1L).build();

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
		assertThatThrownBy(() ->
			commentService.save(givenQuestion.id(), givenAnswerId, givenContent, givenParentType, givenMember))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("댓글 저장시 ParentType이 null인 경우 실패 테스트")
	@Test
	void save_comment_parentType_not_support_fail() {
		//given
		Question givenQuestion = Question.builder().id(1L).build();

		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Long givenAnswerId = 1L;

		String givenContent = "testContent";

		ParentType givenParentType = null;

		//when & then
		assertThatThrownBy(
			() -> commentService.save(givenQuestion.id(), givenAnswerId, givenContent, givenParentType, givenMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.NOT_SUPPORT.getMessage());
	}

	@DisplayName("답변 댓글 수정 성공 테스트")
	@Test
	void update_comment_answer_success() {
		//given
		Question givenQuestion = Question.builder().id(1L).build();

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
			.rootQuestion(givenQuestion)
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
		assertThat(commentResponse.rootQuestionId()).isEqualTo(givenFindComment.rootQuestion().id());
		assertThat(commentResponse.parentId()).isEqualTo(1L);
		assertThat(commentResponse.author()).isEqualTo(givenMember.username());
		assertThat(commentResponse.isAuthor()).isTrue();
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
			.rootQuestion(givenQuestion)
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
		assertThat(commentResponse.rootQuestionId()).isEqualTo(givenFindComment.rootQuestion().id());
		assertThat(commentResponse.parentId()).isEqualTo(givenFindComment.question().id());
		assertThat(commentResponse.author()).isEqualTo(givenFindComment.author().username());
		assertThat(commentResponse.isAuthor()).isTrue();
		assertThat(commentResponse.parentType()).isEqualTo(givenParentType);
		assertThat(commentResponse.content()).isEqualTo(givenUpdateContent);
		assertThat(commentResponse.createdAt()).isNotNull();
		assertThat(commentResponse.modifiedAt()).isNotNull();

		verify(commentRepository, times(1)).save(givenUpdateComment);
		verify(commentRepository, times(1)).findById(givenFindComment.id());
	}

	@DisplayName("댓글 수정시 작성자가 아닐 경우 실패 테스트")
	@Test
	void update_comment_author_not_match_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Member givenLoginMember = Member.builder()
			.id(2L)
			.email("testEmail2@naver.com")
			.username("testUsername2")
			.build();

		Question givenQuestion = Question.builder().id(1L).build();

		String givenContent = "testContent";
		String givenUpdateContent = "updateContent";

		ParentType givenParentType = ParentType.QUESTION;

		Comment givenFindComment = Comment.builder()
			.id(1L)
			.rootQuestion(givenQuestion)
			.content(givenContent)
			.author(givenMember)
			.question(givenQuestion)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(commentRepository.findById(givenFindComment.id()))
			.willReturn(Optional.of(givenFindComment));

		//when & then
		assertThatThrownBy(() -> commentService.update(givenFindComment.id(), givenUpdateContent, givenLoginMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.UNAUTHORIZED.getMessage());

		verify(commentRepository, times(1)).findById(givenFindComment.id());
	}

	@DisplayName("댓글 수정시 댓글이 존재하지 않을 때 실패 테스트")
	@Test
	void update_comment_not_found_fail() {
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

		Comment givenComment = Comment.builder()
			.id(1L)
			.content(givenContent)
			.rootQuestion(givenQuestion)
			.author(givenMember)
			.question(givenQuestion)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(commentRepository.findById(givenComment.id()))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> commentService.update(givenComment.id(), givenUpdateContent, givenMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.NOT_FOUND.getMessage());

		verify(commentRepository, times(1)).findById(givenComment.id());
	}

	@DisplayName("댓글 수정시 question, answer가 null일 때 실패 테스트")
	@Test
	void update_comment_question_and_answer_isNull_fail() {
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
			.rootQuestion(givenQuestion)
			.author(givenMember)
			.question(givenQuestion)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		Comment givenFetchComment = givenFindComment.fetch(givenUpdateContent);

		Comment givenResponsehComment = Comment.builder()
			.id(1L)
			.content(givenUpdateContent)
			.author(givenMember)
			.createdAt(givenFindComment.createdAt())
			.modifiedAt(givenFindComment.modifiedAt())
			.build();

		given(commentRepository.findById(givenFindComment.id()))
			.willReturn(Optional.of(givenFindComment));

		given(commentRepository.save(givenFetchComment))
			.willReturn(givenResponsehComment);

		//when & then
		assertThatThrownBy(() -> commentService.update(givenFindComment.id(), givenUpdateContent, givenMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.UNKNOWN_SERVER.getMessage());

		verify(commentRepository, times(1)).findById(givenFindComment.id());
		verify(commentRepository, times(1)).save(givenFetchComment);
	}

	@DisplayName("댓글 삭제 성공 테스트")
	@Test
	void delete_comment_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Question givenQuestion = Question.builder().id(1L).build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenComment = Comment.builder()
			.id(1L)
			.rootQuestion(givenQuestion)
			.content(givenContent)
			.author(givenMember)
			.answer(givenAnswer)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(commentRepository.findById(1L))
			.willReturn(Optional.of(givenComment));

		doNothing().when(commentRepository).deleteById(givenComment.id());
		//when
		commentService.deleteById(givenComment.id(), givenMember);

		//then
		verify(commentRepository, times(1)).findById(givenComment.id());
		verify(commentRepository, times(1)).deleteById(givenComment.id());
	}

	@DisplayName("댓글 삭제시 댓글이 존재하지 않을 때 실패 테스트")
	@Test
	void delete_find_comment_isNull_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		Question givenQuestion = Question.builder().id(1L).build();

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenComment = Comment.builder()
			.id(1L)
			.rootQuestion(givenQuestion)
			.content(givenContent)
			.author(givenMember)
			.answer(givenAnswer)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(commentRepository.findById(1L))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() -> commentService.deleteById(givenComment.id(), givenMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("댓글 삭제시 작성자가 아닐 때 실패 테스트")
	@Test
	void delete_comment_author_not_match_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.build();

		Question givenQuestion = Question.builder().id(1L).build();

		Member givenLoginMember = Member.builder()
			.id(2L)
			.email("testEmail2@naver.com")
			.username("testUsername2")
			.build();

		Answer givenAnswer = Answer.builder().id(1L).build();

		String givenContent = "testContent";

		ParentType givenParentType = ParentType.ANSWER;

		Comment givenComment = Comment.builder()
			.id(1L)
			.rootQuestion(givenQuestion)
			.content(givenContent)
			.author(givenMember)
			.answer(givenAnswer)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(commentRepository.findById(1L))
			.willReturn(Optional.of(givenComment));

		//when & then
		assertThatThrownBy(() -> commentService.deleteById(givenComment.id(), givenLoginMember))
			.isInstanceOf(CommentBusinessLogicException.class)
			.hasMessage(CommentErrorCode.UNAUTHORIZED.getMessage());
	}

	@DisplayName("질문 ID로 댓글 페이징 조회 성공 테스트")
	@Test
	void findAll_questionId_success() {
		//given
		Long givenQuestionId = 1L;

		Long givenMemberId = 1L;

		ParentType givenParentType = ParentType.QUESTION;

		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(0)
			.build();

		List<CommentResponse> commentResponseList = new ArrayList<>();

		LongStream.range(0, 26)
			.forEach((index) -> {
				commentResponseList.add(CommentResponse.builder()
					.commentId(index + 1)
					.rootQuestionId(givenQuestionId)
					.parentId(givenQuestionId)
					.parentType(givenParentType)
					.author("testUsername")
					.isAuthor(true)
					.content("testContent" + index)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.build());
			});

		Pageable givenPageable = PageRequest.of(0, 10);

		int startIndex = givenPageable.getPageSize() * givenPageable.getPageNumber();

		List<CommentResponse> commentResponseSubList = commentResponseList.subList(startIndex,
			Math.min(startIndex + givenPageable.getPageSize(), commentResponseList.size()));

		Page<CommentResponse> commentResponsePage = new PageImpl<>(
			commentResponseList.subList(startIndex,
				Math.min(startIndex + givenPageable.getPageSize(), commentResponseList.size())), givenPageable,
			commentResponseList.size());

		given(commentRepository.findAll(givenQuestionId, givenMemberId, givenParentType, givenPageable,
			givenSearchCondition))
			.willReturn(commentResponsePage);

		//when
		Page<CommentResponse> result = commentService.findAll(givenQuestionId, givenMemberId, givenParentType,
			givenSearchCondition);

		//then
		assertThat(result.getTotalElements()).isEqualTo(commentResponseList.size());
		assertThat(result.getTotalPages()).isEqualTo((int)Math.ceil(commentResponseList.size() / 10.0));
		assertThat(result.getContent()).isEqualTo(commentResponseSubList);
	}

	@DisplayName("답변 ID로 댓글 페이징 조회 성공 테스트")
	@Test
	void findAll_answerId_success() {
		//given
		Long givenAnswerId = 1L;

		Long givenMemberId = 1L;

		ParentType givenParentType = ParentType.ANSWER;

		Long givenQuestionId = 1L;

		SearchCondition givenSearchCondition = SearchCondition.builder()
			.pageNum(0)
			.build();

		List<CommentResponse> commentResponseList = new ArrayList<>();

		LongStream.range(0, 26)
			.forEach((index) -> {
				commentResponseList.add(CommentResponse.builder()
					.commentId(index + 1)
					.rootQuestionId(givenQuestionId)
					.parentId(givenAnswerId)
					.parentType(givenParentType)
					.author("testUsername")
					.isAuthor(true)
					.content("testContent" + index)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.build());
			});

		Pageable givenPageable = PageRequest.of(0, 10);

		int startIndex = givenPageable.getPageSize() * givenPageable.getPageNumber();

		List<CommentResponse> commentResponseSubList = commentResponseList.subList(startIndex,
			Math.min(startIndex + givenPageable.getPageSize(), commentResponseList.size()));

		Page<CommentResponse> commentResponsePage = new PageImpl<>(
			commentResponseList.subList(startIndex,
				Math.min(startIndex + givenPageable.getPageSize(), commentResponseList.size())), givenPageable,
			commentResponseList.size());

		given(commentRepository.findAll(givenAnswerId, givenMemberId, givenParentType, givenPageable,
			givenSearchCondition))
			.willReturn(commentResponsePage);

		//when
		Page<CommentResponse> result = commentService.findAll(givenAnswerId, givenMemberId, givenParentType,
			givenSearchCondition);

		//then
		assertThat(result.getTotalElements()).isEqualTo(commentResponseList.size());
		assertThat(result.getTotalPages()).isEqualTo((int)Math.ceil(commentResponseList.size() / 10.0));
		assertThat(result.getContent()).isEqualTo(commentResponseSubList);
	}
}

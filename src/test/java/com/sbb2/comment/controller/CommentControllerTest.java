package com.sbb2.comment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import org.hibernate.validator.HibernateValidator;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import com.sbb2.comment.controller.request.CommentCreateForm;
import com.sbb2.comment.controller.request.CommentUpdateForm;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.CommentService;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.common.validation.ValidationGroups;
import com.sbb2.common.validation.annotation.ValidStringEnum;
import com.sbb2.member.domain.Member;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
	@Mock
	private CommentService commentService;
	private CommentController commentController;
	private Validator validator;

	@BeforeEach
	void setUp() {
		commentController = new CommentController(commentService);
		ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
			.configure()
			.buildValidatorFactory();
		validator = factory.getValidator();
	}

	@DisplayName("질문 댓글 조회 성공 테스트")
	@Test
	void findAll_questionId() {
		//given
		Long givenQuestionId = 1L;

		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("testPassword1234!")
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

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

		given(commentService.findAll(givenQuestionId, givenMemberUserDetails.getMember().id(), givenParentType,
			givenSearchCondition))
			.willReturn(commentResponsePage);

		//when
		ResponseEntity<GenericResponse<Page<CommentResponse>>> result =
			commentController.findAll(givenQuestionId, givenParentType.toString(), givenSearchCondition, givenMemberUserDetails);

		//then
		assertThat(result.getBody().getData()).isEqualTo(commentResponsePage);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(commentService, times(1))
			.findAll(givenQuestionId, givenMemberUserDetails.getMember().id(), givenParentType, givenSearchCondition);
	}

	@DisplayName("답변 댓글 조회 성공 테스트")
	@Test
	void findAll_answerId() {
		//given
		Long givenAnswerId = 1L;

		Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("testPassword1234!")
			.build();

		Long givenQuestionId = 1L;

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		ParentType givenParentType = ParentType.ANSWER;

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

		given(commentService.findAll(givenAnswerId, givenMemberUserDetails.getMember().id(), givenParentType,
			givenSearchCondition))
			.willReturn(commentResponsePage);

		//when
		ResponseEntity<GenericResponse<Page<CommentResponse>>> result =
			commentController.findAll(givenAnswerId, givenParentType.toString(), givenSearchCondition, givenMemberUserDetails);

		//then
		assertThat(result.getBody().getData()).isEqualTo(commentResponsePage);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(commentService, times(1))
			.findAll(givenAnswerId, givenMemberUserDetails.getMember().id(), givenParentType, givenSearchCondition);
	}

	@DisplayName("댓글 저장 성공 테스트")
	@Test
	void save_comment_success() {
	    //given
	    Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("testPassword1234!")
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		String givenContent = "createContent";

		Long givenParentId = 1L;
		Long commentId = 1L;

		ParentType givenParentType = ParentType.QUESTION;

		CommentResponse givenCommentResponse = CommentResponse.builder()
			.commentId(commentId)
			.rootQuestionId(givenParentId)
			.parentId(givenParentId)
			.parentType(ParentType.QUESTION)
			.author(givenMemberUserDetails.getMember().username())
			.isAuthor(true)
			.content(givenContent)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		CommentCreateForm givenCommentCreateForm = CommentCreateForm.builder()
			.rootQuestionId(givenParentId)
			.parentId(givenParentId)
			.parentType(givenParentType.toString())
			.content(givenContent)
			.build();


		given(commentService.save(givenCommentResponse.rootQuestionId(), givenParentId, givenContent,
			givenParentType, givenMemberUserDetails.getMember()))
			.willReturn(givenCommentResponse);

	    //when
		ResponseEntity<GenericResponse<CommentResponse>> result = commentController
			.save(givenCommentCreateForm, givenMemberUserDetails);

	    //then
	    assertThat(result.getBody().getData()).isEqualTo(givenCommentResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		verify(commentService, times(1))
			.save(givenParentId, givenParentId, givenContent, givenParentType, givenMemberUserDetails.getMember());
	}

	@DisplayName("댓글 수정 성공 테스트")
	@Test
	void update_comment_success() {
	    //given
	    Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("testPassword1234!")
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		String updateContent = "updateContent";

		Long commentId = 1L;

		CommentResponse givenCommentResponse = CommentResponse.builder()
			.commentId(commentId)
			.rootQuestionId(1L)
			.parentId(1L)
			.parentType(ParentType.QUESTION)
			.author(givenMemberUserDetails.getMember().username())
			.isAuthor(true)
			.content(updateContent)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		CommentUpdateForm givenCommentUpdateForm = CommentUpdateForm.builder()
			.content(updateContent)
			.build();

		given(commentService.update(commentId, updateContent, givenMemberUserDetails.getMember()))
			.willReturn(givenCommentResponse);

	    //when
		ResponseEntity<GenericResponse<CommentResponse>> result = commentController
			.update(commentId, givenCommentUpdateForm, givenMemberUserDetails);

	    //then
	    assertThat(result.getBody().getData()).isEqualTo(givenCommentResponse);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(commentService, times(1))
			.update(commentId, updateContent, givenMemberUserDetails.getMember());
	}

	@DisplayName("댓글 삭제 성공 테스트")
	@Test
	void delete_comment_success() {
	    //given
	    Member givenMember = Member.builder()
			.id(1L)
			.email("testEmail@naver.com")
			.username("testUsername")
			.password("testPassword1234!")
			.build();

		MemberUserDetails givenMemberUserDetails = new MemberUserDetails(givenMember);

		Long commentId = 1L;

		doNothing().when(commentService).deleteById(commentId, givenMemberUserDetails.getMember());

	    //when
		ResponseEntity<GenericResponse<Void>> result = commentController
			.delete(commentId, givenMemberUserDetails);

	    //then
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody().getData()).isNull();
		verify(commentService, times(1))
			.deleteById(commentId, givenMemberUserDetails.getMember());

	}

	@DisplayName("댓글 페이징 조회시 ParentType가 일치하는게 없을 때 실패 테스트")
	@Test
	void findAll_parentType_not_match_fail() {
		//given
		ParentTypeValid parentTypeValid = new ParentTypeValid("test");

		BindingResult bindingResult = new BeanPropertyBindingResult(parentTypeValid, "parentTypeValid");
		validator.validate(parentTypeValid, ValidationGroups.ValidEnumGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("parentType").getDefaultMessage()).isEqualTo("지원하지 않는 타입입니다.");
	}

	@DisplayName("댓글 저장시 rootQuestionId가 없을 때 실패 테스트")
	@Test
	void save_comment_rootQuestionId_parentId_is_null_fail() {
		//given
		CommentCreateForm givenCommentCreateForm = CommentCreateForm.builder()
			.parentId(1L)
			.parentType("answer")
			.content("givenContent")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenCommentCreateForm, "givenCommentCreateForm");
		validator.validate(givenCommentCreateForm, ValidationGroups.NotNullGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("rootQuestionId").getDefaultMessage()).isEqualTo("질문 ID는 필수 항목입니다.");
	}

	@DisplayName("댓글 저장시 parentId가 없을 때 실패 테스트")
	@Test
	void save_comment_parentId_is_null_fail() {
		//given
		CommentCreateForm givenCommentCreateForm = CommentCreateForm.builder()
			.rootQuestionId(1L)
			.parentType("answer")
			.content("givenContent")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenCommentCreateForm, "givenCommentCreateForm");
		validator.validate(givenCommentCreateForm, ValidationGroups.NotNullGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("parentId").getDefaultMessage()).isEqualTo("부모 ID는 필수 항목입니다.");
	}

	@DisplayName("댓글 저장시 content가 비었을 때 실패 테스트")
	@Test
	void save_comment_content_is_null_fail() {
		//given
		CommentCreateForm givenCommentCreateForm = CommentCreateForm.builder()
			.parentId(1L)
			.parentType("answer")
			.content("")
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenCommentCreateForm, "givenCommentCreateForm");
		validator.validate(givenCommentCreateForm, ValidationGroups.NotBlankGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage()).isEqualTo("댓글 내용은 필수 항목입니다.");
	}

	@DisplayName("댓글 저장시 content가 200자 이상일 때 실패 테스트")
	@Test
	void save_comment_content_size_over_fail() {
		//given
		StringBuilder sb = new StringBuilder();
		sb.repeat('a', 210);

		CommentCreateForm givenCommentCreateForm = CommentCreateForm.builder()
			.parentId(1L)
			.parentType("answer")
			.content(sb.toString())
			.build();

		BindingResult bindingResult = new BeanPropertyBindingResult(givenCommentCreateForm, "givenCommentCreateForm");
		validator.validate(givenCommentCreateForm, ValidationGroups.SizeGroup.class).forEach(violation ->
			bindingResult.rejectValue(
				violation.getPropertyPath().toString(),
				"error",
				violation.getMessage()
			)
		);

		//then
		assertThat(bindingResult.hasErrors()).isTrue();
		assertThat(bindingResult.getErrorCount()).isEqualTo(1);
		assertThat(bindingResult.getFieldError("content").getDefaultMessage()).isEqualTo("최대 200자까지만 입력 가능합니다.");
	}

	private static class ParentTypeValid {
        @ValidStringEnum(enumClass = ParentType.class, groups = ValidationGroups.ValidEnumGroup.class)
        private String parentType;

		public ParentTypeValid(String parentType) {
			this.parentType = parentType;
		}

		public String getParentType() {
			return parentType;
		}
	}

}

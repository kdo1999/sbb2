package com.sbb2.comment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.CommentService;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.member.domain.Member;

//TODO 01/08 컨트롤러 테스트부터 작성하고 로직 구현할 것
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
	@Mock
	private CommentService commentService;

	private CommentController commentController;

	@BeforeEach
	void setUp() {
		commentController = new CommentController(commentService);
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
			commentController.findAll(givenQuestionId, givenParentType, givenSearchCondition, givenMemberUserDetails);

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
			commentController.findAll(givenAnswerId, givenParentType, givenSearchCondition, givenMemberUserDetails);

		//then
		assertThat(result.getBody().getData()).isEqualTo(commentResponsePage);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		verify(commentService, times(1))
			.findAll(givenAnswerId, givenMemberUserDetails.getMember().id(), givenParentType, givenSearchCondition);
	}
}

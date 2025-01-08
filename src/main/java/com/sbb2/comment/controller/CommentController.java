package com.sbb2.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.comment.controller.request.CommentCreateForm;
import com.sbb2.comment.controller.request.CommentUpdateForm;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.CommentService;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.common.validation.ValidationGroups;
import com.sbb2.common.validation.ValidationSequence;
import com.sbb2.common.validation.annotation.ValidStringEnum;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
	private final CommentService commentService;

	@GetMapping
	public ResponseEntity<GenericResponse<Page<CommentResponse>>> findAll(@RequestParam("parentId") Long parentId,
		@ValidStringEnum(enumClass = ParentType.class, groups = ValidationGroups.ValidEnumGroup.class)
		ParentType parentType,
		SearchCondition searchCondition, @AuthenticationPrincipal MemberUserDetails memberUserDetails) {

		Page<CommentResponse> commentResponsePage = commentService
			.findAll(parentId, memberUserDetails.getMember().id(), parentType, searchCondition);

		return ResponseEntity.ok()
			.body(GenericResponse.of(commentResponsePage));
	}

	@PostMapping
	public ResponseEntity<GenericResponse<CommentResponse>> save(
		@Validated(ValidationSequence.class) @RequestBody CommentCreateForm commentCreateForm,
		@AuthenticationPrincipal MemberUserDetails memberUserDetails) {

		ParentType parentType = ParentType.valueOf(commentCreateForm.parentType());

		CommentResponse savedCommentResponse = commentService.save(
			commentCreateForm.parentId(), commentCreateForm.content(), parentType, memberUserDetails.getMember()
		);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(GenericResponse.of(savedCommentResponse));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GenericResponse<CommentResponse>> update(
		@PathVariable("id") Long id,
		@Validated(ValidationSequence.class) @RequestBody CommentUpdateForm commentUpdateForm,
		@AuthenticationPrincipal MemberUserDetails memberUserDetails) {

		CommentResponse updatedCommentResponse = commentService
			.update(id, commentUpdateForm.content(), memberUserDetails.getMember());

		return ResponseEntity.ok()
			.body(GenericResponse.of(updatedCommentResponse));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<GenericResponse<Void>> delete(@PathVariable("id") Long commentId,
		@AuthenticationPrincipal MemberUserDetails givenMemberUserDetails) {

		commentService.deleteById(commentId, givenMemberUserDetails.getMember());

		return ResponseEntity.ok()
			.body(GenericResponse.of());
	}
}

package com.sbb2.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.CommentService;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.auth.userdetails.MemberUserDetails;
import com.sbb2.common.response.GenericResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.common.validation.ValidationGroups;
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
}

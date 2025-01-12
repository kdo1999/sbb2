package com.sbb2.comment.service;

import org.springframework.data.domain.Page;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.member.domain.Member;

public interface CommentService {
	Page<CommentResponse> findAll(Long parentId, Long memberId, ParentType parentType, SearchCondition searchCondition);
	CommentResponse save(Long rootQuestionId, Long parentId, String content, ParentType parentType, Member author);
	CommentResponse update(Long commentId, String updateContent, Member author);
	void deleteById(Long commentId, Member author);
}

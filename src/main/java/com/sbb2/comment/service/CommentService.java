package com.sbb2.comment.service;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.member.domain.Member;

public interface CommentService {
	CommentResponse save(Long parentId, String content, ParentType parentType, Member author);
	CommentResponse update(Long commentId, String updateContent, Member author);
}

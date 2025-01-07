package com.sbb2.comment.service;

import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CreateCommentResponse;
import com.sbb2.member.domain.Member;

public interface CommentService {
	CreateCommentResponse save(Long parentId, String content, ParentType parentType, Member author);
}

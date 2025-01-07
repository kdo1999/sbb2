package com.sbb2.infrastructer.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.util.SearchCondition;

public interface CommentRepository {
	Page<CommentResponse> findAll(Long parentId, Long memberId, ParentType parentType, Pageable pageable,
		SearchCondition searchCondition);

	Comment save(Comment comment);

	List<Comment> findByQuestionId(Long questionId);

	List<Comment> findByAnswerId(Long answerId);

	Optional<Comment> findById(Long commentId);

	void deleteById(Long commentId);
}

package com.sbb2.infrastructer.comment.repository;

import java.util.List;

import com.sbb2.comment.Comment;

public interface CommentRepository {
	Comment save(Comment comment);

	List<Comment> findByQuestionId(Long questionId);
}

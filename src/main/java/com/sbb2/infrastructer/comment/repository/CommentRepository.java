package com.sbb2.infrastructer.comment.repository;

import java.util.List;
import java.util.Optional;

import com.sbb2.comment.Comment;

public interface CommentRepository {
	Comment save(Comment comment);

	List<Comment> findByQuestionId(Long questionId);

	List<Comment> findByAnswerId(Long answerId);

	Optional<Comment> findById(Long commentId);

	void deleteById(Long commentId);
}

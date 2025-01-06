package com.sbb2.infrastructer.comment.repository;

import com.sbb2.comment.Comment;

public interface CommentRepository {
	Comment save(Comment comment);
}

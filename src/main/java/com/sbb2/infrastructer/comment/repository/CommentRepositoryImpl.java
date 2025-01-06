package com.sbb2.infrastructer.comment.repository;

import org.springframework.stereotype.Repository;

import com.sbb2.comment.Comment;
import com.sbb2.infrastructer.comment.entity.CommentEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository{
	private final CommentJpaRepository commentJpaRepository;

	@Override
	public Comment save(Comment comment) {
		return commentJpaRepository.save(CommentEntity.from(comment)).toModel();
	}
}

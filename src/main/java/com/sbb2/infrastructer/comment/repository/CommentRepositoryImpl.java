package com.sbb2.infrastructer.comment.repository;

import java.util.List;

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

	@Override
	public List<Comment> findByQuestionId(Long questionId) {
		return commentJpaRepository.findByQuestionId(questionId)
			.stream()
			.map(CommentEntity::toModel).toList();
	}

	@Override
	public List<Comment> findByAnswerId(Long answerId) {
		return commentJpaRepository.findByAnswerId(answerId)
			.stream()
			.map(CommentEntity::toModel).toList();
	}
}

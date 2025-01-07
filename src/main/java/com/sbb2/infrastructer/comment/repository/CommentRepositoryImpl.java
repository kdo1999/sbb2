package com.sbb2.infrastructer.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.comment.entity.CommentEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository{
	private final CommentJpaRepository commentJpaRepository;
	private final CommentQueryRepository commentQueryRepository;

	@Override
	public Page<CommentResponse> findAll(Long parentId, Long memberId, ParentType parentType, Pageable pageable,
		SearchCondition searchCondition) {
		return commentQueryRepository.findAll(parentId, memberId, parentType, pageable, searchCondition);
	}

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

	@Override
	public Optional<Comment> findById(Long commentId) {
		return commentJpaRepository.findById(commentId).map(CommentEntity::toModel);
	}

	@Override
	public void deleteById(Long commentId) {
		commentJpaRepository.deleteById(commentId);
	}
}

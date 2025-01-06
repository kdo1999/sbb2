package com.sbb2.infrastructer.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sbb2.infrastructer.comment.entity.CommentEntity;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {
}

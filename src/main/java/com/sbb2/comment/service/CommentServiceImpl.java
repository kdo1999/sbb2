package com.sbb2.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.service.response.CreateCommentResponse;
import com.sbb2.infrastructer.comment.repository.CommentRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
	private final CommentRepository commentRepository;
	private final QuestionRepository questionRepository;

	@Override
	public CreateCommentResponse save(Long parentId, String content, ParentType parentType, Member author) {
		if (parentType == null) {
			//TODO 추후 예외 처리
			throw new RuntimeException();
		}
		Comment comment = null;

		switch (parentType) {
			case QUESTION:
				Question findQuestion = questionRepository.findById(parentId)
					.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

				comment = Comment.builder()
					.question(findQuestion)
					.author(author)
					.content(content).build();

				break;
		}

		Comment savedComment = commentRepository.save(comment);

		return CreateCommentResponse.builder()
			.commentId(savedComment.id())
			.parentId(parentId)
			.parentType(parentType)
			.content(savedComment.content())
			.author(savedComment.author().username())
			.createdAt(savedComment.createdAt())
			.modifiedAt(savedComment.modifiedAt())
			.build();
	}
}

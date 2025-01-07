package com.sbb2.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.comment.domain.Comment;
import com.sbb2.comment.domain.ParentType;
import com.sbb2.comment.exception.CommentBusinessLogicException;
import com.sbb2.comment.exception.CommentErrorCode;
import com.sbb2.comment.service.response.CommentResponse;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
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
	private final AnswerRepository answerRepository;

	@Override
	public CommentResponse save(Long parentId, String content, ParentType parentType, Member author) {
		if (parentType == null) {
			throw new CommentBusinessLogicException(CommentErrorCode.NOT_SUPPORT);
		}
		Comment.CommentBuilder commentBuilder = Comment.builder()
			.content(content)
			.author(author);

		switch (parentType) {
			case QUESTION:
				Question findQuestion = questionRepository.findById(parentId)
					.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

				commentBuilder.question(findQuestion);

				break;
			case ANSWER:
				Answer findAnswer = answerRepository.findById(parentId)
					.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

				commentBuilder.answer(findAnswer);
		}

		Comment savedComment = commentRepository.save(commentBuilder.build());

		return CommentResponse.builder()
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

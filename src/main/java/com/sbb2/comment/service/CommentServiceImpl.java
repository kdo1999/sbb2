package com.sbb2.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.sbb2.common.util.SearchCondition;
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
	private static final int PAGE_SIZE = 10;

	private final CommentRepository commentRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<CommentResponse> findAll(Long parentId, Long memberId, ParentType parentType,
		SearchCondition searchCondition) {
		Pageable pageable = PageRequest.of(
			searchCondition.pageNum() == null ? 0 : searchCondition.pageNum(),
			PAGE_SIZE
		);

		return commentRepository.findAll(parentId, memberId, parentType, pageable, searchCondition);
	}

	@Override
	public CommentResponse save(Long rootQuestionId, Long parentId, String content, ParentType parentType, Member author) {
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

				commentBuilder
					.question(findQuestion)
					.rootQuestion(findQuestion);

				break;
			case ANSWER:
				Answer findAnswer = answerRepository.findById(parentId)
					.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

				Question findRootQuestion = questionRepository.findById(rootQuestionId)
					.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

				commentBuilder
					.answer(findAnswer)
					.rootQuestion(findRootQuestion);

				break;
		}

		Comment savedComment = commentRepository.save(commentBuilder.build());

		return CommentResponse.builder()
			.commentId(savedComment.id())
			.rootQuestionId(savedComment.rootQuestion().id())
			.parentId(savedComment.id())
			.parentType(parentType)
			.content(savedComment.content())
			.author(savedComment.author().username())
			.isAuthor(true)
			.createdAt(savedComment.createdAt())
			.modifiedAt(savedComment.modifiedAt())
			.build();
	}

	@Override
	public CommentResponse update(Long commentId, String updateContent, Member author) {
		Comment findComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentBusinessLogicException(CommentErrorCode.NOT_FOUND));

		if (!findComment.author().equals(author)) {
			throw new CommentBusinessLogicException(CommentErrorCode.UNAUTHORIZED);
		}

		Comment fetchComment = findComment.fetch(updateContent);
		Comment updatedComment = commentRepository.save(fetchComment);

		ParentType parentType;
		Long parentId;

		if (updatedComment.question() != null) {
			parentType = ParentType.QUESTION;
			parentId = updatedComment.question().id();
		} else if (updatedComment.answer() != null) {
			parentType = ParentType.ANSWER;
			parentId = updatedComment.answer().id();
		} else {
			throw new CommentBusinessLogicException(CommentErrorCode.UNKNOWN_SERVER);
		}

		return CommentResponse.builder()
			.commentId(updatedComment.id())
			.rootQuestionId(updatedComment.rootQuestion().id())
			.parentId(parentId)
			.parentType(parentType)
			.content(updatedComment.content())
			.author(updatedComment.author().username())
			.isAuthor(true)
			.createdAt(updatedComment.createdAt())
			.modifiedAt(updatedComment.modifiedAt())
			.build();
	}

	@Override
	public void deleteById(Long commentId, Member author) {
		Comment findComment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentBusinessLogicException(CommentErrorCode.NOT_FOUND));

		if (!findComment.author().equals(author)) {
			throw new CommentBusinessLogicException(CommentErrorCode.UNAUTHORIZED);
		}

		commentRepository.deleteById(findComment.id());
	}
}

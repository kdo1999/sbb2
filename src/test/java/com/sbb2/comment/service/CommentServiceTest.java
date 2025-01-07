package com.sbb2.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.comment.repository.CommentRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.question.service.QuestionService;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
	@Mock
	private CommentRepository commentRepository;

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private AnswerRepository answerRepository;

	private QuestionService questionService;

	@BeforeEach
	void setUp() {
		questionService = new QuestionService(commentRepository);
	}
}

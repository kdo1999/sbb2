package com.sbb2.comment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.comment.service.CommentService;

//TODO 01/08 컨트롤러 테스트부터 작성하고 로직 구현할 것
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
	@Mock
	private CommentService commentService;

	private CommentController commentController;

	@BeforeEach
	void setUp() {
		commentController = new CommentController(commentService);
	}
}

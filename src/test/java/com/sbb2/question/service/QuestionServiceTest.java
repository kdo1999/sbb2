package com.sbb2.question.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.sbb2.answer.domain.Answer;
import com.sbb2.category.domain.Category;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.controller.request.QuestionForm;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.question.service.response.QuestionCreateResponse;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.voter.domain.Voter;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceTest {
	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private CategoryRepository categoryRepository;

	private QuestionService questionService;

	@BeforeEach
	void setUp() {
		questionService = new QuestionServiceImpl(questionRepository, categoryRepository);
	}

	@DisplayName("질문 저장 테스트")
	@Test
	void save_question() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		given(categoryRepository.findByCategoryName(givenCategory.categoryName()))
			.willReturn(Optional.of(givenCategory));

		given(questionRepository.save(any(Question.class)))
			.willReturn(
				Question.builder()
					.id(1L)
					.subject(questionSubject)
					.content(questionContent)
					.category(givenCategory)
					.author(givenMember)
					.build()
			);

		//when
		QuestionCreateResponse questionCreateResponse = questionService.save("questionSubject", "questionContent",
			givenMember, givenCategory.categoryName().toString());

		//then
		assertThat(questionCreateResponse.id()).isEqualTo(1L);
	}

	@DisplayName("질문 저장시 카테고리가 존재하지 않을때 실패 테스트")
	@Test
	void save_question_category_not_found() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		CategoryName givenCategoryName = CategoryName.QUESTION_BOARD;

		given(categoryRepository.findByCategoryName(givenCategoryName))
			.willReturn(Optional.empty());

		//when & then
		assertThatThrownBy(() ->
			questionService.save("questionSubject", "questionContent", givenMember, givenCategoryName.toString()))
			.isInstanceOf(CategoryBusinessLogicException.class)
			.hasMessage(CategoryErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("질문 ID로 조회 성공 테스트")
	@Test
	void find_id_question() {
		//given
		String questionSubject = "subject";
		String questionContent = "content";
		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(
				Optional.of(Question.builder()
					.id(1L)
					.subject(questionSubject)
					.content(questionContent)
					.category(givenCategory)
					.author(givenMember)
					.build()
				)
			);

		//when
		Question findQuestion = questionService.findById(1L);

		//then
		assertThat(findQuestion.author()).isEqualTo(givenMember);
		assertThat(findQuestion.subject()).isEqualTo(questionSubject);
		assertThat(findQuestion.content()).isEqualTo(questionContent);
	}

	@DisplayName("질문 ID로 조회 실패 테스트")
	@Test
	void find_id_question_fail() {
		//given
		given(questionRepository.findById(any(Long.class)))
			.willThrow(new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		//then
		assertThatThrownBy(() -> questionService.findById(2L)).isInstanceOf(QuestionBusinessLogicException.class);
	}

	@DisplayName("질문 수정 성공 테스트")
	@Test
	void update_question_succes() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.categoryName("question_board")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.category(givenCategory)
				.author(givenMember)
				.build()));

		Category updateCategory = Category.builder()
			.id(2L)
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		given(questionRepository.save(any(Question.class)))
			.willReturn(Question.builder()
				.id(1L)
				.subject(questionForm.subject())
				.content(questionForm.content())
				.category(updateCategory)
				.author(givenMember)
				.build());

		given(categoryRepository.findByCategoryName(updateCategory.categoryName()))
			.willReturn(Optional.of(updateCategory));

		//when
		Question updateQuestion = questionService.update(1L, questionForm.subject(), questionForm.content(),
			givenMember, questionForm.categoryName());

		//then
		assertThat(updateQuestion.author()).isEqualTo(givenMember);
		assertThat(updateQuestion.subject()).isEqualTo(questionForm.subject());
		assertThat(updateQuestion.content()).isEqualTo(questionForm.content());
		assertThat(updateQuestion.category()).isEqualTo(updateCategory);
	}

	@DisplayName("질문 수정시 작성자가 아닐 때 실패 테스트")
	@Test
	void update_author_loginMember_not_match_fail() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.categoryName("free_board")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Member givenMember2 = Member.builder()
			.id(2L)
			.username("testMember2")
			.password("testPassword2")
			.email("testEmail2")
			.build();

		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.author(givenMember)
				.category(givenCategory)
				.build()));

		//when & then
		assertThatThrownBy(
			() -> questionService.update(1L, questionForm.subject(), questionForm.content(), givenMember2,
				questionForm.categoryName()))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.UNAUTHORIZED.getMessage());
	}

	@DisplayName("질문 수정시 조회 실패 테스트")
	@Test
	void update_find_question_fail() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.categoryName("free_board")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.empty());

		//then
		assertThatThrownBy(
			() -> questionService.update(1L, questionForm.subject(), questionForm.content(), givenMember,
				questionForm.categoryName()))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("질문 수정시 카테고리 조회 실패 테스트")
	@Test
	void update_find_category_fail() {
		//given
		QuestionForm questionForm = QuestionForm.builder()
			.subject("updateSubject")
			.content("updateContent")
			.categoryName("question_board")
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		Category updateCategory = Category.builder()
			.id(2L)
			.categoryName(CategoryName.QUESTION_BOARD)
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.author(givenMember)
				.category(givenCategory)
				.build()));

		given(categoryRepository.findByCategoryName(updateCategory.categoryName()))
			.willReturn(Optional.empty());

		//then
		assertThatThrownBy(
			() -> questionService.update(1L, questionForm.subject(), questionForm.content(), givenMember,
				questionForm.categoryName()))
			.isInstanceOf(CategoryBusinessLogicException.class)
			.hasMessage(CategoryErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("질문 삭제 성공 테스트")
	@Test
	void delete_question_success() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.category(givenCategory)
				.author(givenMember)
				.build()));

		doNothing().when(questionRepository).deleteById(any(Long.class));

		//when
		questionService.deleteById(1L, givenMember);

		//then
		verify(questionRepository, times(1)).deleteById(any(Long.class));
	}

	@DisplayName("질문 삭제시 작성자가 아닐 때 실패 테스트")
	@Test
	void delete_author_loginMember_not_match_fail() {
		//given
		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Member givenMember2 = Member.builder()
			.id(2L)
			.username("testMember2")
			.password("testPassword2")
			.email("testEmail2")
			.build();

		given(questionRepository.findById(any(Long.class)))
			.willReturn(Optional.of(Question.builder()
				.id(1L)
				.subject("subject")
				.content("content")
				.category(givenCategory)
				.author(givenMember)
				.build()));

		//when & then
		assertThatThrownBy(() -> questionService.deleteById(1L, givenMember2))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.UNAUTHORIZED.getMessage());
	}

	@DisplayName("질문 삭제시 질문 조회 실패 테스트")
	@Test
	void delete_question_find_fail() {
		//given
		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		given(questionRepository.findById(any(Long.class))).willReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> questionService.deleteById(1L, givenMember))
			.isInstanceOf(QuestionBusinessLogicException.class)
			.hasMessage(QuestionErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("질문 페이징 조회 성공 테스트")
	@Test
	void findAll_question_success() {
		//given
		List<QuestionPageResponse> questionPageResponseList = new ArrayList<>();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		for (int i = 0; i < 15; i++) {
			questionPageResponseList.add(
				new QuestionPageResponse(
					(long)i,
					"subject" + (i + 1),
					"content" + (i + 1),
					givenMember.username(),
					LocalDateTime.now(),
					LocalDateTime.now(),
					(long)i)
			);
		}

		SearchCondition searchCondition = SearchCondition.builder()
			.pageNum(1)
			.kw("")
			.build();

		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);
		int startIndex = pageable.getPageSize() * pageable.getPageNumber();

		List<QuestionPageResponse> questionPageResponseSubList = questionPageResponseList.subList(
			startIndex, Math.min(startIndex + pageable.getPageSize(), questionPageResponseList.size())
		);

		given(questionRepository.findAll(searchCondition, pageable)).willReturn(new PageImpl<>(
				questionPageResponseList.subList(
					startIndex, Math.min(startIndex + pageable.getPageSize(), questionPageResponseList.size())
				),
				pageable,
				questionPageResponseList.size()
			)
		);

		//when
		Page<QuestionPageResponse> findAll = questionService.findAll(searchCondition);

		//then
		assertThat(findAll.getContent()).isEqualTo(questionPageResponseSubList);
		assertThat(findAll.getTotalElements()).isEqualTo(15);
		assertThat(findAll.getTotalPages()).isEqualTo(2);
	}

	@DisplayName("추천한 사용자가 질문을 조회하면 isVoter가 true인 테스트")
	@Test
	void find_QuestionDetail_isVoter_true() {
		//given
		Category givenCategory = Category.builder()
			.id(1L)
			.categoryName(CategoryName.FREE_BOARD)
			.build();

		Member givenMember = Member.builder()
			.id(1L)
			.username("testMember")
			.password("testPassword")
			.email("testEmail")
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("testContent")
			.author(givenMember)
			.build();

		Voter voter = Voter.builder()
			.member(givenMember)
			.question(Question.builder().id(1L).build())
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("subject")
			.content("content")
			.author(givenMember)
			.category(givenCategory)
			.voterSet(Set.of(voter))
			.build();

		QuestionDetailResponse questionDetailResponse = QuestionDetailResponse.builder()
			.id(question.id())
			.subject(question.subject())
			.content(question.content())
			.categoryDisplayName(givenCategory.categoryName().getCategoryDisplayName())
			.voterCount((long)question.voterSet().size())
			.isVoter(question.voterSet().stream()
				.anyMatch(v -> v.member().id().equals(givenMember.id())))
			.isAuthor(question.author().id().equals(givenMember.id()))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(questionRepository.findDetailById(question.id(), givenMember.id()))
			.willReturn(questionDetailResponse);

		//when
		QuestionDetailResponse findDetailResponse = questionService.findDetailById(question.id(), givenMember);

		//then
		assertThat(findDetailResponse).isEqualTo(questionDetailResponse);
		assertThat(findDetailResponse.isVoter()).isTrue();
	}
}

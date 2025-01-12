package com.sbb2.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

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
import com.sbb2.answer.service.response.AnswerDetailResponse;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.common.util.SearchCondition;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;

@ExtendWith(MockitoExtension.class)
public class AnswerServiceTest {
	@Mock
	private AnswerRepository answerRepository;
	@Mock
	private QuestionRepository questionRepository;
	private AnswerService answerService;

	@BeforeEach
	void setUp() {
		answerService = new AnswerServiceImpl(questionRepository, answerRepository);
	}

	@DisplayName("답변 저장 성공 테스트")
	@Test
	void save_answer_success() {
		//given
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";
		Long questionId = question.id();

		given(questionRepository.findById(any(Long.class))).willReturn(Optional.of(question));

		given(answerRepository.save(any(Answer.class)))
			.willReturn(Answer.builder()
				.id(1L)
				.content(content)
				.author(member)
				.question(question)
				.build());

		//when
		AnswerDetailResponse answerDetailResponse = answerService.save(questionId, content, member);

		//then
		assertThat(answerDetailResponse.questionId()).isEqualTo(questionId);
		assertThat(answerDetailResponse.content()).isEqualTo(content);
		assertThat(answerDetailResponse.author()).isEqualTo(member.username());
	}

	@DisplayName("답변 저장 질문 조회 실패 테스트")
	@Test
	void save_answer_find_fail() {
		//given
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";
		Long questionId = question.id();

		given(questionRepository.findById(any(Long.class)))
			.willThrow(new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		//then
		assertThatThrownBy(() -> answerService.save(questionId, content, member));
	}

	@DisplayName("답변 조회 성공 테스트")
	@Test
	void find_answer_success() {
		//given
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content(content)
			.author(member)
			.question(question)
			.build();

		Long answerId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));

		//when
		Answer findAnswer = answerService.findById(answerId);

		//then
		assertThat(findAnswer).isEqualTo(answer);
	}

	@DisplayName("답변 조회 실패 테스트")
	@Test
	void find_answer_fail() {
		//given
		Long answerId = 1L;

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> answerService.findById(answerId));
	}

	@DisplayName("답변 수정 성공 테스트")
	@Test
	void update_answer_success() {
		//given
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String updateContent = "updateAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(member)
			.question(question)
			.build();

		Answer updateAnswer = answer.fetch(updateContent);
		Long answerId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));
		given(answerRepository.save(any(Answer.class))).willReturn(updateAnswer);

		//when
		Answer result = answerService.update(answerId, updateContent, member);

		//then
		assertThat(result).isEqualTo(updateAnswer);
	}

	@DisplayName("답변 수정시 답변 조회 실패 테스트")
	@Test
	void update_answer_find_fail() {
		//given
		Long answerId = 1L;
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.empty());

		//then
		assertThatThrownBy(() -> answerService.update(answerId, "updateContent", member))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("작성자가 아닌 회원이 답변 수정 시도시 실패")
	@Test
	void update_answer_unauthorized_fail() {
		//given
		Member author = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(author)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(author)
			.question(question)
			.build();

		Member loginMember = Member.builder()
			.username("testUsername2")
			.password("testPassword2")
			.email("testEmail2@naver.com")
			.build();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));

		//then
		assertThatThrownBy(() -> answerService.update(answer.id(), "updateContent", loginMember))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.UNAUTHORIZED.getMessage());
	}

	@DisplayName("답변 삭제 성공 테스트")
	@Test
	void delete_answer_success() {
		//given
		Member author = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(author)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(author)
			.question(question)
			.build();

		Long targetId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));
		doNothing().when(answerRepository).deleteById(any(Long.class));

		//when
		answerService.deleteById(targetId, author);

		//then
		verify(answerRepository, times(1)).deleteById(any(Long.class));
	}

	@DisplayName("작성자가 아닌 회원이 답변 삭제 시도시 실패")
	@Test
	void delete_answer_unauthorized_fail() {
		//given
		Member author = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();
		Member loginMember = Member.builder()
			.username("testUsername2")
			.password("testPassword2")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(author)
			.build();

		Answer answer = Answer.builder()
			.id(1L)
			.content("content")
			.author(author)
			.question(question)
			.build();

		Long targetId = answer.id();

		given(answerRepository.findById(any(Long.class))).willReturn(Optional.of(answer));

		//then
		assertThatThrownBy(() -> answerService.deleteById(targetId, loginMember))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.UNAUTHORIZED.getMessage());

	}

	@DisplayName("질문 ID로 답변 조회 성공 테스트")
	@Test
	void find_answer_questionId_success() {
		//given
		Member member = Member.builder()
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";
		List<Answer> answerList =
			IntStream.range(1, 5)
				.mapToObj(index -> Answer.builder()
					.id((long)index)
					.content(content)
					.author(member)
					.question(question)
					.build())
				.toList();

		given(answerRepository.findByQuestionId(any(Long.class))).willReturn(answerList);

		//when
		List<Answer> findAnswerList = answerService.findByQuestionId(question.id());

		//then
		assertThat(findAnswerList).isEqualTo(answerList);
		assertThat(findAnswerList.size()).isEqualTo(4);
	}

	@DisplayName("응답용 답변 조회 성공 테스트")
	@Test
	void find_answerDetailResponse_success() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content(content)
			.author(member)
			.question(question)
			.voterSet(Set.of())
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		AnswerDetailResponse answerDetailResponse = AnswerDetailResponse.builder()
			.id(answer.id())
			.content(answer.content())
			.author(answer.author().username())
			.voterCount((long)answer.voterSet().size())
			.createdAt(answer.createdAt())
			.modifiedAt(answer.modifiedAt())
			.isAuthor(answer.author().id().equals(member.id()))
			.isVoter(answer.voterSet().stream().anyMatch(voter -> voter.member().id().equals(member.id())))
			.questionId(answer.question().id())
			.build();

		given(answerRepository.findAnswerDetailByIdAndMemberId(answer.id(), member.id())).willReturn(
			Optional.of(answerDetailResponse));

		//when
		AnswerDetailResponse findAnswerDetailResponse = answerService.findAnswerDetailByIdAndMemberId(answer.id(),
			member.id());

		//then
		assertThat(findAnswerDetailResponse).isEqualTo(answerDetailResponse);
	}

	@DisplayName("응답용 답변 조회 실패 테스트")
	@Test
	void find_answerDetailResponse_fail() {
		//given
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail@naver.com")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		String content = "saveAnswer";

		Answer answer = Answer.builder()
			.id(1L)
			.content(content)
			.author(member)
			.question(question)
			.voterSet(Set.of())
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();

		given(answerRepository.findAnswerDetailByIdAndMemberId(answer.id(), member.id())).willReturn(
			Optional.empty());

		//when & then
		assertThatThrownBy(() -> answerService.findAnswerDetailByIdAndMemberId(answer.id(), member.id()))
			.isInstanceOf(AnswerBusinessLogicException.class)
			.hasMessage(AnswerErrorCode.NOT_FOUND.getMessage());
	}

	@DisplayName("답변 페이징 조회 성공 테스트")
	@Test
	void find_answerDetailPage_success() {
		//given
		List<AnswerDetailResponse> answerDetailResponseList = new ArrayList<>();
		LongStream.range(0, 15)
			.forEach((index) -> answerDetailResponseList.add(
				AnswerDetailResponse.builder()
					.id(index)
					.content("testContent" + index)
					.author("testUser")
					.isAuthor(true)
					.isVoter(false)
					.voterCount(0L)
					.createdAt(LocalDateTime.now())
					.modifiedAt(LocalDateTime.now())
					.questionId(1L)
					.build()
			));

		SearchCondition searchCondition = SearchCondition.builder()
			.pageNum(0)
			.build();

		Pageable pageable = PageRequest.of(searchCondition.pageNum(), 10);
		int startIndex = pageable.getPageSize() * pageable.getPageNumber();

		List<AnswerDetailResponse> answerDetailResponseSubList = answerDetailResponseList.subList(
			startIndex, Math.min(startIndex + pageable.getPageSize(), answerDetailResponseList.size())
		);

		Page<AnswerDetailResponse> answerDetailResponsePage = new PageImpl<>(
			answerDetailResponseList.subList(
				startIndex, Math.min(startIndex + pageable.getPageSize(), answerDetailResponseList.size())
			),
			pageable,
			answerDetailResponseList.size());

		given(answerRepository.findAnswerDetailPageByQuestionId(searchCondition, 1L, 1L, pageable))
			.willReturn(answerDetailResponsePage);

		//when
		Page<AnswerDetailResponse> result = answerService.findAnswerDetailPageByQuestionId(
			searchCondition, 1L, 1L);

		//then
		assertThat(result.getTotalElements()).isEqualTo(answerDetailResponseList.size());
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.getContent()).isEqualTo(answerDetailResponseSubList);
	}
}

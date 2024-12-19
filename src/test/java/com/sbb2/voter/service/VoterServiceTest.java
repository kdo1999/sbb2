package com.sbb2.voter.service;

import static org.mockito.BDDMockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.Voter;

@ExtendWith(MockitoExtension.class)
public class VoterServiceTest {
	@Mock
	private VoterRepository voterRepository;
	private VoterService voterService;

	@BeforeEach
	void setUp() {
		voterService = new VoterServiceImpl(voterRepository);
	}

	//TODO 질문 추천 성공 테스트
	@DisplayName("질문 추천 성공 테스트")
	@Test
	void save_question_voter_success() {
	    //given
		// 컨트롤러에서 id, member -> 질문 서비스로
		// 질문 서비스로 해당 id 질문 조회 후 추천 서비스로 질문, 멤버 전달
		// 추천 서비스에서 질문과 멤버로 voter 만든 후 저장
		Member member = Member.builder()
			.id(1L)
			.username("testUsername")
			.password("testPassword")
			.email("testEmail")
			.build();

		Question question = Question.builder()
			.id(1L)
			.subject("testSubject")
			.content("testContent")
			.author(member)
			.build();

		Voter voter = Voter.builder()
			.id(1L)
			.question(question)
			.member(member)
			.build();

		given(voterRepository.save(voter)).willReturn(voter);

		//when
		Voter savedVoter = voterService.save(question, member);

	    //then
		Assertions.assertThat(savedVoter).isEqualTo(voter);
	}
	//TODO 질문 추천 삭제 테스트
	//TODO 질문 중복 추천 방지 테스트
	//TODO 질문 추천 조회 테스트

	//TODO 댓글 추천 성공 테스트
	//TODO 댓글 추천 삭제 테스트
	//TODO 댓글 중복 추천 방지 테스트
	//TODO 질문 추천 삭제 테스트
}

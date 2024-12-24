package com.sbb2.voter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.answer.domain.Answer;
import com.sbb2.answer.exception.AnswerBusinessLogicException;
import com.sbb2.answer.exception.AnswerErrorCode;
import com.sbb2.infrastructer.answer.repository.AnswerRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.voter.domain.Voter;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.exception.VoterBusinessLogicException;
import com.sbb2.voter.exception.VoterErrorCode;
import com.sbb2.voter.service.response.VoterCreateResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VoterServiceImpl implements VoterService {
	private final VoterRepository voterRepository;
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;

	@Override
	public VoterCreateResponse save(Long targetId, VoterType voterType, Member member) {
		boolean exists;
		VoterCreateResponse voterCreateResponse = null;
		
		if (voterType == null) {
				throw new VoterBusinessLogicException(VoterErrorCode.NOT_VOTER_TYPE);
		}
		
		switch (voterType) {
			case QUESTION:
				exists = voterRepository.existsByQuestionIdAndMemberId(targetId, member.id());

				if (exists) {
					throw new VoterBusinessLogicException(VoterErrorCode.DUPLICATE_VOTER);
				}

				voterCreateResponse = saveQuestionVoter(targetId, member);

				break;
			case ANSWER:
				exists = voterRepository.existsByAnswerIdAndMemberId(targetId, member.id());

				if (exists) {
					throw new VoterBusinessLogicException(VoterErrorCode.DUPLICATE_VOTER);
				}

				voterCreateResponse = saveAnswerVoter(targetId, member);

				break;
		}

		return voterCreateResponse;
	}

	@Override
	public void delete(Long targetId, VoterType voterType, Member member) {
		if (voterType == null) {
			throw new VoterBusinessLogicException(VoterErrorCode.NOT_VOTER_TYPE);
		}

		switch (voterType) {
			case QUESTION -> deleteQuestion(targetId, member);
			case ANSWER -> deleteAnswer(targetId, member);
		}
	}

	private VoterCreateResponse saveQuestionVoter(Long questionId, Member member) {
		Question findQuestion = questionRepository.findById(questionId)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		Voter voter = Voter.builder()
			.question(findQuestion)
			.member(member)
			.build();

		Voter questionVoter = voterRepository.save(voter);

		return VoterCreateResponse.builder()
					.voterId(questionVoter.id())
					.voterUsername(questionVoter.member().username())
					.targetId(questionVoter.question().id())
					.voterType(VoterType.QUESTION)
					.isVoter(true)
					.build();
	}

	private void deleteQuestion(Long questionId, Member member) {
		Question findQuestion = questionRepository.findById(questionId)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		Voter findVoter = findQuestion.voterSet().stream()
			.filter(voter -> member.id().equals(voter.member().id()))
			.findFirst()
			.orElseThrow(() -> new VoterBusinessLogicException(VoterErrorCode.NOT_FOUND));

		voterRepository.deleteById(findVoter.id());
	}

	private void deleteAnswer(Long answerId, Member member) {
		Answer findAnswer = answerRepository.findById(answerId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

		Voter findVoter = findAnswer.voterSet().stream()
			.filter(voter -> member.id().equals(voter.member().id()))
			.findFirst()
			.orElseThrow(() -> new VoterBusinessLogicException(VoterErrorCode.NOT_FOUND));

		voterRepository.deleteById(findVoter.id());
	}

	private VoterCreateResponse saveAnswerVoter(Long answerId, Member member) {
		Answer findAnswer = answerRepository.findById(answerId)
			.orElseThrow(() -> new AnswerBusinessLogicException(AnswerErrorCode.NOT_FOUND));

		Voter voter = Voter.builder()
			.answer(findAnswer)
			.member(member)
			.build();

		Voter answerVoter = voterRepository.save(voter);

		return VoterCreateResponse.builder()
					.voterId(answerVoter.id())
					.voterUsername(answerVoter.member().username())
					.targetId(answerVoter.answer().id())
					.voterType(VoterType.ANSWER)
					.isVoter(true)
					.build();
	}
}

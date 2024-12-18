package com.sbb2.voter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.answer.domain.Answer;
import com.sbb2.infrastructer.voter.repository.VoterRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;
import com.sbb2.voter.exception.VoterBusinessLogicException;
import com.sbb2.voter.exception.VoterErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VoterServiceImpl implements VoterService {
	private final VoterRepository voterRepository;

	@Override
	public Voter save(Question question, Member member) {
		Boolean exists = voterRepository.existsByQuestionIdAndMemberId(question.id(), member.id());

		if (exists) {
			throw new VoterBusinessLogicException(VoterErrorCode.DUPLICATE_VOTER);
		}

		Voter voter = Voter.builder()
			.question(question)
			.member(member)
			.build();

		return voterRepository.save(voter);
	}

	@Override
	public Voter save(Answer answer, Member member) {
		Boolean exists = voterRepository.existsByAnswerIdAndMemberId(answer.id(), member.id());

		if (exists) {
			throw new VoterBusinessLogicException(VoterErrorCode.DUPLICATE_VOTER);
		}

		Voter voter = Voter.builder()
			.answer(answer)
			.member(member)
			.build();

		return voterRepository.save(voter);
	}

	@Override
	public void delete(Question question, Member member) {
		Voter findVoter = question.voterSet().stream()
			.filter(voter -> member.id().equals(voter.member().id())).findFirst()
			.orElseThrow(() -> new VoterBusinessLogicException(VoterErrorCode.NOT_FOUND));

		voterRepository.deleteById(findVoter.id());
	}

	@Override
	public void delete(Answer answer, Member member) {
		Voter findVoter = answer.voterSet().stream()
			.filter(voter -> member.id().equals(voter.member().id()))
			.findFirst()
			.orElseThrow(() -> new VoterBusinessLogicException(VoterErrorCode.NOT_FOUND));

		voterRepository.deleteById(findVoter.id());
	}
}

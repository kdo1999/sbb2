package com.sbb2.voter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public void deleteQuestionVoter(Long questionId, Long memberId) {
		Voter findVoter = voterRepository.findByQuestionIdAndMemberId(questionId, memberId)
			.orElseThrow(() -> new VoterBusinessLogicException(VoterErrorCode.NOT_FOUND));

		voterRepository.deleteById(findVoter.id());
	}
}

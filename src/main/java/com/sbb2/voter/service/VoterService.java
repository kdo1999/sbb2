package com.sbb2.voter.service;

import com.sbb2.member.domain.Member;
import com.sbb2.voter.domain.VoterType;
import com.sbb2.voter.service.response.VoterCreateResponse;

public interface VoterService {
	VoterCreateResponse save(Long targetId, VoterType voterType, Member member);

	void delete(Long targetId, VoterType voterType, Member member);
}

package com.sbb2.voter.service.response;

import com.sbb2.voter.domain.VoterType;

import lombok.Builder;

public record VoterCreateResponse (Long voterId, String voterUsername, boolean isVoter, Long targetId, VoterType voterType){

	@Builder
	public VoterCreateResponse(Long voterId, String voterUsername, boolean isVoter, Long targetId,
		VoterType voterType) {
		this.voterId = voterId;
		this.voterUsername = voterUsername;
		this.isVoter = isVoter;
		this.targetId = targetId;
		this.voterType = voterType;
	}
}

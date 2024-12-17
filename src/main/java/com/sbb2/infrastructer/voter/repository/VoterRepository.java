package com.sbb2.infrastructer.voter.repository;

import com.sbb2.voter.Voter;

public interface VoterRepository {
	Voter save(Voter voter);
}

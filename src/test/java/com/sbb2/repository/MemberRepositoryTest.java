package com.sbb2.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import com.sbb2.infrastructer.member.repository.MemberRepository;
import com.sbb2.member.domain.Member;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class MemberRepositoryTest {
	private final MemberRepository memberRepository;

	@Autowired
	public MemberRepositoryTest(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Test
	@DisplayName("회원 저장 테스트")
	void save_member() {
		String username = "testUsername";
		String password = "testPassword";
		String email = "testEmail";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		assertThat(username).isEqualTo(member.username());
		assertThat(password).isEqualTo(member.password());
		assertThat(email).isEqualTo(member.email());
	}

	@Test
	@DisplayName("회원 조회 테스트")
	void find_member() {
		//given
		String username = "testUsername";
		String password = "testPassword";
		String email = "testEmail";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		//when
		Member findMember = memberRepository.findById(savedMember.id());

		//then
		assertThat(findMember).isEqualTo(savedMember);
	}
}

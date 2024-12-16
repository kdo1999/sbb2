package com.sbb2.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class MemberRepositoryTest {
	@Autowired
	private final MemberRepository memberRepository;

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

		Optional<Member> savedMember = memberRepository.save(member);

		Assertions.assertThat(username).isEqualTo(member.username());
		Assertions.assertThat(password).isEqualTo(member.password());
		Assertions.assertThat(email).isEqualTo(member.email());
	}
}

package com.sbb2.infrastructer.member.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;
import org.springframework.test.annotation.DirtiesContext;

import com.sbb2.common.config.QuerydslConfig;
import com.sbb2.member.domain.Member;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@Import(QuerydslConfig.class)
@DirtiesContext
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
	@DisplayName("회원 ID 조회 테스트")
	void find_id_member() {
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
		Member findMember = memberRepository.findById(savedMember.id()).get();

		//then
		assertThat(findMember).isEqualTo(savedMember);
	}

	@DisplayName("회원 Username 조회 테스트")
	@Test
	void find_username_member() {
		//given
		String username = "findUsername";
		String password = "testPassword";
		String email = "testEmail";

		Member member = Member.builder()
			.username(username)
			.password(password)
			.email(email)
			.build();

		Member savedMember = memberRepository.save(member);

		//when
		Member findMember = memberRepository.findByUsername(savedMember.username()).get();

		//then
		assertThat(findMember).isEqualTo(savedMember);
	}

	@Test
	@DisplayName("동일한 이메일의 데이터가 존재하는 성공 테스트")
	void exists_email_success() {
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
		Boolean exists = memberRepository.existsByEmail(savedMember.email());

		//then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("동일한 username 데이터가 존재하는 성공 테스트")
	void exists_username_success() {
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
		Boolean exists = memberRepository.existsByUsername(savedMember.username());

		//then
		assertThat(exists).isTrue();
	}
}

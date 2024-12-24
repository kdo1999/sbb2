package com.sbb2.infrastructer.member.entity;

import com.sbb2.member.domain.Member;
import com.sbb2.member.domain.MemberRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private MemberRole memberRole;

	@Builder(access = AccessLevel.PROTECTED)
	private MemberEntity(Long id, String username, String email, String password, MemberRole memberRole) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.memberRole = memberRole;
	}

	public static MemberEntity from(Member member) {
		return MemberEntity.builder()
			.id(member.id())
			.username(member.username())
			.email(member.email())
			.password(member.password())
			.memberRole(member.memberRole())
			.build();
	}

	public Member toModel() {
		return Member.builder()
			.id(this.id)
			.username(this.username)
			.email(this.email)
			.password(this.password)
			.memberRole(this.memberRole)
			.build();
	}
}

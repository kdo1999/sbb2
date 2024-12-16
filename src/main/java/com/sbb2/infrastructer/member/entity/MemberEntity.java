package com.sbb2.infrastructer.member.entity;

import com.sbb2.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private Long id;

	@Column
	private String usernmae;

	@Column
	private String email;

	@Column
	private String password;

	@Builder(access = AccessLevel.PROTECTED)
	private MemberEntity(Long id, String usernmae, String email, String password) {
		this.id = id;
		this.usernmae = usernmae;
		this.email = email;
		this.password = password;
	}

	public static MemberEntity from(Member member) {
		return MemberEntity.builder()
			.id(member.id())
			.usernmae(member.username())
			.email(member.email())
			.password(member.password())
			.build();
	}

	public Member toModel() {
		return Member.builder()
			.id(this.id)
			.username(this.usernmae)
			.email(this.email)
			.password(this.password)
			.build();
	}
}

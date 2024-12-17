package com.sbb2.infrastructer.question.entity;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.sbb2.common.util.BaseEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.question.domain.Question;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class QuestionEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String subject;

	@Column(columnDefinition = "text")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private MemberEntity author;

	@Builder
	public QuestionEntity(Long id, String subject, String content, MemberEntity author, LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public static QuestionEntity from(Question question) {
		return QuestionEntity.builder()
			.id(question.id())
			.subject(question.subject())
			.content(question.content())
			.author(MemberEntity.from(question.author()))
			.build();
	}

	public Question toModel() {
		return Question.builder()
			.id(this.id)
			.subject(this.subject)
			.content(this.content)
			.author(this.author.toModel())
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
	}
}

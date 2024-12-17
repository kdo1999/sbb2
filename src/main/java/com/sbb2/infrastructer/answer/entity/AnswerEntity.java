package com.sbb2.infrastructer.answer.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.stream.Collectors;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.util.BaseEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.infrastructer.question.entity.QuestionEntity;
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
@Table(name = "answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	private MemberEntity author;

	@ManyToOne(fetch = FetchType.LAZY)
	private QuestionEntity questionEntity;

	@Builder(access = AccessLevel.PROTECTED)
	private AnswerEntity(Long id, String content, MemberEntity author, QuestionEntity questionEntity, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.questionEntity = questionEntity;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public static AnswerEntity from(Answer answer) {
		return AnswerEntity.builder()
			.id(answer.id())
			.content(answer.content())
			.author(MemberEntity.from(answer.author()))
			.questionEntity(QuestionEntity.builder()
				.id(answer.question().id())
				.subject(answer.question().subject())
				.content(answer.question().content())
				.createdAt(answer.question().createdAt())
				.modifiedAt(answer.question().modifiedAt())
				.author(MemberEntity.from(answer.author()))
				.build())
			.build();
	}

	public Answer toModel() {
		return Answer.builder()
			.id(id)
			.content(content)
			.author(author.toModel())
			.question(Question.builder()
				.id(questionEntity.getId())
				.subject(questionEntity.getSubject())
				.content(questionEntity.getContent())
				.createdAt(questionEntity.getCreatedAt())
				.modifiedAt(questionEntity.getModifiedAt())
				.author(questionEntity.getAuthor().toModel())
				.build()
			)
			.modifiedAt(modifiedAt)
			.createdAt(createdAt)
			.build();
	}
}

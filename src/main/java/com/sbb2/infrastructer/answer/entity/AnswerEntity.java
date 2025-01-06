package com.sbb2.infrastructer.answer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sbb2.answer.domain.Answer;
import com.sbb2.common.util.BaseEntity;
import com.sbb2.infrastructer.comment.entity.CommentEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.infrastructer.voter.entity.VoterEntity;
import com.sbb2.question.domain.Question;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@Column(name = "answer_id")
	private Long id;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private MemberEntity author;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id")
	private QuestionEntity questionEntity;

	@OneToMany(mappedBy = "answerEntity", cascade = CascadeType.ALL)
	private Set<VoterEntity> voterEntitySet = new HashSet<>();

	@OneToMany(mappedBy = "answerEntity", cascade = CascadeType.ALL)
	private List<CommentEntity> commentEntityList = new ArrayList<>();

	@Builder
	private AnswerEntity(Long id, String content, MemberEntity author, QuestionEntity questionEntity,
		LocalDateTime createdAt, LocalDateTime modifiedAt,
		Set<VoterEntity> voterEntitySet, List<CommentEntity> commentEntityList) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.questionEntity = questionEntity;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.voterEntitySet = voterEntitySet;
		this.commentEntityList = commentEntityList;
	}

	public void setVoterEntitySet(Set<VoterEntity> voterEntitySet) {
		this.voterEntitySet = voterEntitySet;
		voterEntitySet.forEach(v -> v.setAnswerEntity(this));
	}

	public static AnswerEntity from(Answer answer) {
		AnswerEntity buildAnswerEntity = AnswerEntity.builder()
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
		buildAnswerEntity.setVoterEntitySet(answer.voterSet().stream().map(voter -> VoterEntity.from(voter)).collect(
			Collectors.toSet()));
		return buildAnswerEntity;
	}

	public Answer toModel() {
		Answer buildAnswer = Answer.builder()
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

		if (voterEntitySet.size() > 0) {
			this.voterEntitySet.stream()
				.forEach(voter -> buildAnswer.addVoter(voter.toModel()));
		}

		return buildAnswer;
	}
}

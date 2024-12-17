package com.sbb2.infrastructer.question.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sbb2.common.util.BaseEntity;
import com.sbb2.infrastructer.answer.entity.AnswerEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
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
@Table(name = "question")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class QuestionEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long id;

	@Column(nullable = false)
	private String subject;

	@Column(nullable = false, columnDefinition = "text")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private MemberEntity author;

	@OneToMany(mappedBy = "questionEntity")
	private List<AnswerEntity> answerEntityList = new ArrayList<>();

	@OneToMany(mappedBy = "questionEntity", cascade = CascadeType.ALL)
	private Set<VoterEntity> voterEntitySet = new HashSet<>();

	@Builder
	public QuestionEntity(Long id, String subject, String content, MemberEntity author, LocalDateTime createdAt,
		LocalDateTime modifiedAt, List<AnswerEntity> answerEntityList) {
		this.id = id;
		this.subject = subject;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
		this.answerEntityList = answerEntityList;
	}

	public void setVoterEntitySet(Set<VoterEntity> voterEntitySet) {
		this.voterEntitySet = voterEntitySet;
		voterEntitySet.forEach(v -> v.setQuestionEntity(this));
	}

	public static QuestionEntity from(Question question) {
		QuestionEntity buildQuestion = QuestionEntity.builder()
			.id(question.id())
			.subject(question.subject())
			.content(question.content())
			.author(MemberEntity.from(question.author()))
			.answerEntityList(question.answerList().isEmpty() ? new ArrayList<>() :
				question.answerList().stream().map(AnswerEntity::from).toList())
			.build();
		buildQuestion.setVoterEntitySet(question.voterSet().stream().map(voter -> VoterEntity.from(voter)).collect(
			Collectors.toSet()));
		return buildQuestion;
	}

	public Question toModel() {
		Question buildQuestion = Question.builder()
			.id(this.id)
			.subject(this.subject)
			.content(this.content)
			.author(this.author.toModel())
			.answerList(answerEntityList.isEmpty() ? new ArrayList<>() : answerEntityList.stream()
				.map(AnswerEntity::toModel)
				.toList()
			)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();

		this.voterEntitySet.stream()
			.forEach(voter -> buildQuestion.addVoter(voter.toModel()));

		return buildQuestion;
	}
}

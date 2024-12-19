package com.sbb2.infrastructer.voter.entity;

import java.util.ArrayList;

import com.sbb2.answer.domain.Answer;
import com.sbb2.infrastructer.answer.entity.AnswerEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.question.domain.Question;
import com.sbb2.voter.domain.Voter;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voter")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoterEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "qustion_id")
	private QuestionEntity questionEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private MemberEntity memberEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "answer_id")
	private AnswerEntity answerEntity;

	@Builder
	private VoterEntity(Long id, QuestionEntity questionEntity, MemberEntity memberEntity, AnswerEntity answerEntity) {
		this.id = id;
		this.questionEntity = questionEntity;
		this.memberEntity = memberEntity;
		this.answerEntity = answerEntity;
	}

	public static VoterEntity from(Voter voter) {
		return VoterEntity.builder()
			.id(voter.id())
			.memberEntity(MemberEntity.from(voter.member()))
			.build();
	}

	public void setQuestionEntity(QuestionEntity questionEntity) {
		this.questionEntity = questionEntity;
	}

	public void setAnswerEntity(AnswerEntity answerEntity) {
		this.answerEntity = answerEntity;
	}

	public Voter toModel() {
		return Voter.builder()
			.id(this.id)
			.member(this.memberEntity.toModel())
			.question(this.questionEntity == null ? null : Question.builder()
				.id(this.questionEntity.getId())
				.subject(this.questionEntity.getSubject())
				.content(this.questionEntity.getContent())
				.author(this.questionEntity.getAuthor().toModel())
				.answerList(questionEntity.getAnswerEntityList().isEmpty() ? new ArrayList<>() :
					this.questionEntity.getAnswerEntityList().stream()
						.map(AnswerEntity::toModel)
						.toList()
				)
				.createdAt(this.questionEntity.getCreatedAt())
				.modifiedAt(this.questionEntity.getModifiedAt())
				.build())
			.answer(this.answerEntity == null ? null : Answer.builder()
				.id(this.answerEntity.getId())
				.content(this.answerEntity.getContent())
				.author(this.answerEntity.getAuthor().toModel())
				.build())
			.build();
	}
}

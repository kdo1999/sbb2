package com.sbb2.infrastructer.comment.entity;

import com.sbb2.answer.domain.Answer;
import com.sbb2.comment.Comment;
import com.sbb2.common.util.BaseEntity;
import com.sbb2.infrastructer.answer.entity.AnswerEntity;
import com.sbb2.infrastructer.member.entity.MemberEntity;
import com.sbb2.infrastructer.question.entity.QuestionEntity;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;

import jakarta.persistence.Column;
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
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "content", nullable = false)
	private String content;

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
	public CommentEntity(Long id, String content, QuestionEntity questionEntity, MemberEntity memberEntity,
		AnswerEntity answerEntity) {
		this.id = id;
		this.content = content;
		this.questionEntity = questionEntity;
		this.memberEntity = memberEntity;
		this.answerEntity = answerEntity;
	}

	public static CommentEntity from(Comment comment) {
		return CommentEntity.builder()
				.id(comment.id())
				.content(comment.content())
				.questionEntity(comment.question() == null ? null : QuestionEntity.builder()
					.id(comment.question().id())
					.build())
				.memberEntity(MemberEntity.from(comment.author()))
				.answerEntity(comment.answer() == null ? null : AnswerEntity.builder()
					.id(comment.answer().id())
					.build())
				.build();
	}

	public Comment toModel() {
		return Comment.builder()
				.id(id)
				.content(content)
				.question(questionEntity == null ? null : Question.builder()
					.id(questionEntity.getId())
					.build())
				.author(memberEntity.toModel())
				.answer(answerEntity == null ? null : Answer.builder()
					.id(answerEntity.getId())
					.build())
			.createdAt(createdAt)
			.modifiedAt(modifiedAt)
				.build();
	}
}

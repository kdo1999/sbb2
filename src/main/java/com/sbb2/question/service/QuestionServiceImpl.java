package com.sbb2.question.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sbb2.category.domain.Category;
import com.sbb2.category.exception.CategoryBusinessLogicException;
import com.sbb2.category.exception.CategoryErrorCode;
import com.sbb2.infrastructer.category.entity.CategoryName;
import com.sbb2.infrastructer.category.repository.CategoryRepository;
import com.sbb2.infrastructer.question.repository.QuestionRepository;
import com.sbb2.member.domain.Member;
import com.sbb2.question.domain.Question;
import com.sbb2.question.service.response.QuestionDetailResponse;
import com.sbb2.question.service.response.QuestionPageResponse;
import com.sbb2.question.exception.QuestionBusinessLogicException;
import com.sbb2.question.exception.QuestionErrorCode;
import com.sbb2.question.service.response.QuestionCreateResponse;
import com.sbb2.common.util.SearchCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
	private final QuestionRepository questionRepository;
	private final CategoryRepository categoryRepository;
	private static final int PAGE_SIZE = 10;

	@Override
	public QuestionCreateResponse save(String subject, String content, Member author, String categoryName) {
		CategoryName fromCategoryName = CategoryName.from(categoryName);

		Category findCategory = categoryRepository.findByCategoryName(fromCategoryName)
			.orElseThrow(() -> new CategoryBusinessLogicException(
				CategoryErrorCode.NOT_FOUND));

		Question question = Question.builder()
			.subject(subject)
			.content(content)
			.category(findCategory)
			.author(author)
			.build();

		Question savedQuestion = questionRepository.save(question);

		return QuestionCreateResponse.builder()
			.id(savedQuestion.id())
			.build();
	}

	@Transactional(readOnly = true)
	@Override
	public Question findById(Long id) {
		Question question = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		return question;
	}

	@Override
	public Question update(Long id, String subject, String content, Member author, String categoryName) {
		Question target = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		if (!target.author().equals(author)) {
			throw new QuestionBusinessLogicException(QuestionErrorCode.UNAUTHORIZED);
		}

		CategoryName fromCategoryName = CategoryName.from(categoryName);
		Category category = null;

		if (!target.category().categoryName().equals(fromCategoryName)) {
			category = categoryRepository.findByCategoryName(fromCategoryName)
				.orElseThrow(() -> new CategoryBusinessLogicException(CategoryErrorCode.NOT_FOUND));
		} else {
			category = target.category();
		}

		Question updateQuestion = target.fetch(subject, content, category);

		return questionRepository.save(updateQuestion);
	}

	@Override
	public void deleteById(Long id, Member author) {
		Question target = questionRepository.findById(id)
			.orElseThrow(() -> new QuestionBusinessLogicException(QuestionErrorCode.NOT_FOUND));

		if (!target.author().equals(author)) {
			throw new QuestionBusinessLogicException(QuestionErrorCode.UNAUTHORIZED);
		}

		questionRepository.deleteById(target.id());
	}

	@Transactional(readOnly = true)
	@Override
	public Page<QuestionPageResponse> findAll(SearchCondition searchCondition) {

		PageRequest pageRequest = PageRequest.of(searchCondition.pageNum() == null ? 0 : searchCondition.pageNum(),
			PAGE_SIZE);
		return questionRepository.findAll(searchCondition, pageRequest);
	}

	@Transactional(readOnly = true)
	@Override
	public QuestionDetailResponse findDetailById(Long id, Member loginMember) {

		return questionRepository.findDetailById(id, loginMember.id());
	}
}

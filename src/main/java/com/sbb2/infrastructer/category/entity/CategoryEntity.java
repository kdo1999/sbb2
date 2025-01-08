package com.sbb2.infrastructer.category.entity;

import com.sbb2.category.domain.Category;

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
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@Column(name = "category_name", nullable = false, unique = true)
	private String categoryName;

	@Builder
	public CategoryEntity(Long id, String categoryName) {
		this.id = id;
		this.categoryName = categoryName;
	}

	public static CategoryEntity from(Category category) {
		return CategoryEntity.builder()
			.id(category.id())
			.categoryName(category.categoryName())
			.build();
	}

	public Category toModel() {
		return Category.builder()
			.id(id)
			.categoryName(categoryName)
			.build();
	}
}

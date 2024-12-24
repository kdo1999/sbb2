package com.sbb2.common.validation;


import static com.sbb2.common.validation.ValidationGroups.*;

import jakarta.validation.GroupSequence;

/**
 * 검증 어노테이션의 순서를 지정하는 인터페이스
 * NotNullGroup(@NotNull) → NotBlankGroup(@NotBlank) → ValidEnumGroup(@ValidEnum) → AssertTrueGroup(@AssertTrue) <br>
 * → PatternGroup(@Pattern) → SizeGroup(@Size)
 * → MinGroup(@Min) → MaxGroup(@Max)
 * @author : Kim Dong O
 * @fileName : ValidationSequence
 */

@GroupSequence({NotNullGroup.class, NotBlankGroup.class, ValidEnumGroup.class, AssertTrueGroup.class,
	PatternGroup.class})
public interface ValidationSequence {
}

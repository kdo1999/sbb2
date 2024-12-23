package com.sbb2.common.httpError;

import java.util.List;

public record ErrorDetail(List<java.lang.Error> errors) {
	public static ErrorDetail of(List<java.lang.Error> errors) {
		return new ErrorDetail(errors);
	}
}

package com.sbb2.common.httpError;

import java.util.List;

public record ErrorDetail(List<Error> errors) {
	public static ErrorDetail of(List<Error> errors) {
		return new ErrorDetail(errors);
	}
}

package com.sbb2.common.httpError;

import java.time.ZonedDateTime;

public record HttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail, ZonedDateTime timeStamp) {
	// of 메서드를 통한 팩토리 메서드 구현
	public static HttpErrorInfo of(int code, String path, String message, ErrorDetail errorDetail) {
		return new HttpErrorInfo(code, path, message, errorDetail, ZonedDateTime.now());
	}

}

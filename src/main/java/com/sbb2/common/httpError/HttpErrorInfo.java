package com.sbb2.common.httpError;

import java.time.ZonedDateTime;

/**
 * Exception을 핸들링 할 때, 해당 응답을 보냅니다.
 *
 * @author : sebin
 * @since : 2024-05-14
 */
public record HttpErrorInfo(int code, String path, String message, ErrorDetail errorDetail, ZonedDateTime timeStamp) {
	// of 메서드를 통한 팩토리 메서드 구현
	public static HttpErrorInfo of(int code, String path, String message, ErrorDetail errorDetail) {
		return new HttpErrorInfo(code, path, message, errorDetail, ZonedDateTime.now());
	}

}

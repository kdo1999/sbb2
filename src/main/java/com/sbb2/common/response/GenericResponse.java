package com.sbb2.common.response;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GenericResponse<T> {
	private final ZonedDateTime timestamp;
	private boolean isSuccess;
	private String message;
	private final T data;

	@Builder
	public GenericResponse(boolean isSuccess, String message, T data) {
		this.timestamp = ZonedDateTime.now();
		this.isSuccess = isSuccess;
		this.message = message;
		this.data = data;
	}

	public static <T> GenericResponse<T> of(T data) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.message("Success")
			.data(data)
			.build();
	}

	public static <T> GenericResponse<T> of(T data, String customMessage) {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.message(customMessage)
			.data(data)
			.build();
	}

	public static <T> GenericResponse<T> of() {
		return GenericResponse.<T>builder()
			.isSuccess(true)
			.message("Success")
			.build();
	}


	public static <T> GenericResponse<T> fail(T data) {
		return GenericResponse.<T>builder()
			.isSuccess(false)
			.message("Fail")
			.data(data)
			.build();
	}
}

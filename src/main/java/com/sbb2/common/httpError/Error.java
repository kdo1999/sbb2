package com.sbb2.common.httpError;

public record Error(String domain, String field, String reason, String message) {
	public static Error of(String domain, String field, String reason, String message) {
		return new Error(domain, field, reason, message);
	}
}

package com.sbb2.common.mail.util;

import java.util.Map;

import jakarta.mail.internet.MimeMessage;

/**
 * TemplateMaker 인터페이스 입니다.
 *
 * <p>이메일 템플릿을 만들어 반환합니다.</p>
 */
public interface TemplateMaker {

	public MimeMessage create(MimeMessage newMimeMessage, String email, String title,
		Map<String, String> htmlParameterMap, TemplateName templateName);
}

package com.sbb2.common.mail.util;

import jakarta.mail.internet.MimeMessage;

/**
 * MailSender
 * 메일 전송 기능입니다.
 */
public interface MailSender {
	public void send(MimeMessage mimeMessage);

	public MimeMessage createMimeMessage();
}

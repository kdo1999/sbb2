package com.sbb2.common.mail.util;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailSenderImpl implements MailSender {
	private final JavaMailSender javaMailSender;

	@Override
	public void send(MimeMessage mimeMessage) {
		javaMailSender.send(mimeMessage);
	}

	@Override
	public MimeMessage createMimeMessage() {
		return javaMailSender.createMimeMessage();
	}
}

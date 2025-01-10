package com.sbb2.common.mail.service;

import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sbb2.common.mail.util.MailSender;
import com.sbb2.common.mail.util.TemplateMaker;
import com.sbb2.common.mail.util.TemplateName;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService{
	private final TemplateMaker templateMaker;
	private final MailSender mailSender;

	@Async("threadPoolTaskExecutor")
	@Override
	public void sendEmail(String to, Map<String, String> htmlParameterMap, TemplateName templateName) {
		StringBuilder sb = new StringBuilder();
		switch (templateName) {
			case TemplateName.PASSWORD_RESET -> {
				sb.append("[SBB] 임시 비밀번호 입니다.");
			}
			case TemplateName.PASSWORD_RESET_VERIFY -> {
				sb.append("[SBB] 비밀번호 초기화 인증번호 입니다.");
			}
		}
		String title = sb.toString();

		MimeMessage mimeMessage = templateMaker.create(mailSender.createMimeMessage(), to, title, htmlParameterMap,
			templateName);

		mailSender.send(mimeMessage);
	}
}

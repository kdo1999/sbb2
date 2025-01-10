package com.sbb2.common.mail.service;

import java.util.Map;

import com.sbb2.common.mail.util.TemplateName;

public interface MailService {
	void sendEmail(String to, Map<String, String> htmlParameterMap, TemplateName templateName);
}

/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oh.common.service;

import org.oh.common.annotation.ResultLogging;
import org.oh.common.config.CommonConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.Mail;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.WebUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.constraints.NotBlank;

import java.util.Map;

/**
 * 메일 서비스
 * <pre>
 * application.yml
 *
 * ##### 메일 관리
 * mail:
 *   ### 보낸 사람 이메일
 *   from: blockchain@gmail.com
 * </pre>
 */
@Slf4j
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Service
@Validated
@ConfigurationProperties(MailService.PROPERTY_PREFIX)
@ConditionalOnProperty(value = "from", prefix = MailService.PROPERTY_PREFIX)
public class MailService {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".mail";

	@NotBlank
	private String from;

	protected final ITemplateEngine templateEngine;
	protected final JavaMailSender sender;

	/**
	 * 해당 메일 정보로 메일을 발송
	 *
	 * @param mail 메일 정보
	 */
	@Retryable
	@ResultLogging
	public void send(Mail mail) {
		if (mail.getToList().isEmpty() && mail.getCcList().isEmpty()) {
			throw new CommonException(CommonError.COM_EMAIL_ERROR, "No recipient addresses. mail: " + mail);
		}

		Map<String, Object> params = JsonUtil.convertValueMap(mail);
		String text = templateEngine.process(WebUtil.getTemplatesName() + "mail",
				new Context(null, params));
		log.debug("text:\n{}", text);

		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		try {
			helper.setFrom(from);
			helper.setTo(mail.getToList().toArray(new String[0]));
			helper.setCc(mail.getCcList().toArray(new String[0]));
			helper.setSubject(mail.getSubject());
			helper.setText(text, true);
		} catch (MessagingException e) {
			throw new CommonException(CommonError.COM_EMAIL_ERROR, "mail: " + mail, e);
		}
		sender.send(message);
	}
}

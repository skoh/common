package org.oh.common.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.model.Mail;
import org.springframework.beans.factory.annotation.Autowired;

//@Disabled
@Slf4j
@ServiceTest
public class MailServiceTest {
	private static final Mail.Content content = Mail.Content.builder()
			.title("title01")
			.link("https://www.naver.com")
			.body("body01\nbody02")
			.build();
	private static final Mail mail = Mail.builder()
			.subject("subject01")
			.content(content)
			.build();

	@Autowired
	private MailService service;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	//	@Test
	void t01send() {
		mail.getToList().add("dsbct1@gmail.com");
		mail.getCcList().add("dsbct1@gmail.com");
		service.send(mail);
	}
}

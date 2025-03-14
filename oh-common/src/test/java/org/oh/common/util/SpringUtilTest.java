package org.oh.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.oh.common.config.ServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//@Disabled
@Slf4j
@ServiceTest
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SpringUtilTest {
	@Autowired
	private SpringUtil springUtil;

	@Test
	void t01test() {
		String token = SecurityUtil.createToken(SecurityUtilTest.USER_NAME, SecurityUtilTest.ROLE_NAME,
				SecurityUtilTest.KEY, SecurityUtilTest.EXPIRE_TIME);
		Jws<Claims> jwt = SecurityUtil.getJws(SecurityUtilTest.KEY, token);
		Authentication auth = SecurityUtil.getAuthentication(jwt, token);
		SecurityContextHolder.getContext().setAuthentication(auth);

		SpringUtil.getLoginUserOrEmpty()
				.ifPresent(a -> {
					String userName = a.getId();
					log.debug("userName: {}", userName);
					Assertions.assertEquals(SecurityUtilTest.USER_NAME, userName);
				});

	}

	@Test
	void t02test() {
		ServerProperties serverProperties = springUtil.getBean(ServerProperties.class);
		log.debug("serverProperties: {}", serverProperties);
		Optional<ServerProperties.Tomcat> tomcat = springUtil.getBeanOrEmpty(ServerProperties.Tomcat.class);
		log.debug("tomcat: {}", tomcat);
	}

	@Test
	void t03test() {
		int port = springUtil.getServerPort();
		log.debug("port: {}", port);
	}

	@Test
	void t04test() {
		springUtil.registerBean(new EventHandler());
	}
}

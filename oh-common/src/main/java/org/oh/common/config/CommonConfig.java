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

package org.oh.common.config;

import org.oh.common.util.AESEncryptUtil;
import org.oh.common.util.SecurityUtil;
import org.oh.common.util.SpringUtil;
import org.oh.common.util.ThreadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;

import java.time.Duration;
import java.util.Optional;

/**
 * 공통 초기화
 * <pre>
 * application.yml
 *
 * ##### 서버 관리
 * server:
 *   ### 내장 WAS 관리
 *   servlet:
 *     ### 세션 타임 아웃 (기본값: 30m)
 *     session.timeout: 30m
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Configuration
public class CommonConfig {
	protected final SpringUtil springUtil; // dependency
	protected final ThreadUtil threadUtil; // dependency
	public static final String SPRING_PREFIX = "spring";
	public static final String COMMON_PREFIX = "common";
	public static final String COMMON_API_PREFIX = COMMON_PREFIX + ".api";
	public static final String APP_PREFIX = "app";
	public static final String APP_API_PREFIX = APP_PREFIX + ".api";

	protected final ApplicationContext context;
	protected final ServerProperties properties;

	/**
	 * 세션 초기화
	 */
	@EventListener
	public void initSession(ContextRefreshedEvent event) {
		SpringUtil springUtil = context.getBean(SpringUtil.class);
		Optional<Hazelcast4IndexedSessionRepository> sessionRepository
				= springUtil.getBeanOrEmpty(Hazelcast4IndexedSessionRepository.class);
		sessionRepository.ifPresent(a -> {
			Duration timeout = properties.getServlet().getSession().getTimeout();
			a.setDefaultMaxInactiveInterval((int) timeout.getSeconds());
			log.info("session timeout(seconds): {}", timeout.getSeconds());
		});
	}

//	@Bean
//	public CookieSerializer cookieSerializer() {
//		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//		cookieSerializer.setSameSite("None");
//		return cookieSerializer;
//	}

	/**
	 * 바밀번호 암호기 생성 (복호화 불가)
	 *
	 * @return 바밀번호 암호기
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return SecurityUtil.PASSWORD_ENCODER;
	}

	/**
	 * 문자열 암호기 생성 (복호화 가능)
	 *
	 * @return 문자열 암호기
	 */
	@Bean
	public TextEncryptor textEncryptor() {
		return Encryptors.text(AESEncryptUtil.PASSWORD, AESEncryptUtil.SALT);
	}
}

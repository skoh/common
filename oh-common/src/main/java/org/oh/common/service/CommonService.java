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
import org.oh.common.config.SecurityConfig;
import org.oh.common.filter.SecurityFilter;
import org.oh.common.model.user.Login;
import org.oh.common.security.PropertyUserDetailsService;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.SecurityUtil;
import org.oh.common.util.SpringUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

/**
 * 공통 서비스
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Service
@ConditionalOnProperty(value = "enabled", prefix = SecurityConfig.SECURITY_API_PREFIX, havingValue = "true")
public class CommonService {
	protected final SecurityFilter securityFilter;
	protected final SpringUtil springUtil;
	protected final PropertyUserDetailsService userDetailsService;
	protected final TextEncryptor textEncryptor;

	private AuthenticationManagerBuilder authBuilder;

	/**
	 * 로그인 인증을 통해 토큰을 발급
	 *
	 * @param login 로그인 정보
	 * @return 토큰 (JWT 또는 AES256 토큰)
	 * @see SecurityConfig
	 */
	@ResultLogging(result = true)
	public String login(Login login) {
		String token;
		if (userDetailsService.isEnabled()) {
			authBuilder = springUtil.getBean(authBuilder, AuthenticationManagerBuilder.class);
			AuthenticationManager authManager = authBuilder.getObject();
			UsernamePasswordAuthenticationToken authToken =
					new UsernamePasswordAuthenticationToken(login.getId(), login.getPassword());
			Authentication auth = authManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(auth);
			login.setRoles(SecurityUtil.getRoleNames(auth.getAuthorities()));
			token = securityFilter.createToken(login);
		} else {
			UserDetails userDetails = userDetailsService.loadUserByUsername(login.getId());
			login.checkEncPassword(userDetails.getPassword());

			Login loginUser = SecurityUtil.convertUser(userDetails);
			Login user = JsonUtil.merge(loginUser,
					securityFilter.setExpireDate(login.getExpireTimeMin()));
			String userJson = JsonUtil.toString(user);
			log.debug("user: {}", userJson);
//			token = AESEncryptUtil.encrypt(userJson);
			token = textEncryptor.encrypt(userJson);
		}
		return token;
	}
}

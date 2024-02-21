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

package org.oh.common.controller;

import org.oh.common.annotation.ResultLogging;
import org.oh.common.config.SecurityConfig;
import org.oh.common.model.http.HttpResponse;
import org.oh.common.model.user.Login;
import org.oh.common.service.CommonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import java.util.Optional;

/**
 * 공통 API
 */
@Slf4j
@Tag(name = "Common", description = "로그인 등의 공통 API")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RestController
@RequestMapping(CommonRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SecurityConfig.SECURITY_API_PREFIX, havingValue = "true")
public class CommonRestController {
	//		implements DefaultController {
	public static final String PATH = "/v1/common";

	protected final CommonService service;

	@Operation(summary = "1. 로그인(토큰 발급)")
	@ResultLogging(result = true)
	@PostMapping(DefaultController.LOGIN_PAGE)
	public ResponseEntity<String> login(@RequestBody Login login) {
		String token = service.login(login);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
		return new ResponseEntity<>(token, httpHeaders, HttpStatus.OK);
	}

	@Tag(name = "공통 테스트", description = "로그인 권한 등의 공통 테스트 API")
	@RestController
	@RequestMapping(CommonRestController.PATH)
	@ConditionalOnProperty(value = "enabled", prefix = SecurityConfig.SECURITY_API_PREFIX + ".test",
			havingValue = "true")
	protected static class TestRestController {
		//		@PreAuthorize("hasAnyRole('" + Role.ROLE_USER.getValue() + "')")
//		@Secured({"ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER"})
		@Operation(summary = "2. USER 권한 테스트")
		@ResultLogging(result = true)
		@GetMapping("user")
		public ResponseEntity<String> user(HttpSession session) {
			int count = Optional.ofNullable(session.getAttribute("count"))
					.map(Integer.class::cast)
					.orElse(1);
			log.debug("count: {}", count);
			session.setAttribute("count", ++count);
			return ResponseEntity.ok(HttpResponse.MESSAGE_SUCCESS);
		}

		@PreAuthorize("hasAnyRole('MANAGER')")
//		@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
		@Operation(summary = "3. MANAGER 권한 테스트")
		@ResultLogging(result = true)
		@GetMapping("manager")
		public ResponseEntity<String> manager() {
			return ResponseEntity.ok(HttpResponse.MESSAGE_SUCCESS);
		}

		//		@PreAuthorize("hasAnyRole('ADMIN')")
//		@Secured({"ROLE_ADMIN"})
		@Operation(summary = "4. ADMIN 권한 테스트")
		@ResultLogging(result = true)
		@GetMapping("admin")
		public ResponseEntity<String> admin() {
			return ResponseEntity.ok(HttpResponse.MESSAGE_SUCCESS);
		}
	}
}

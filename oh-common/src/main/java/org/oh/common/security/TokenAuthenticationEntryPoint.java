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

package org.oh.common.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.model.http.FilterResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 인증 예외(AuthenticationException) 핸들링
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class TokenAuthenticationEntryPoint
		implements AuthenticationEntryPoint {
	protected final FilterResponse filterResponse;

	/**
	 * 인증 예외 발생시 응답 메세지 출력
	 *
	 * @param request       HTTP 요청 정보
	 * @param response      HTTP 응답 정보
	 * @param authException 인증 예외
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws IOException {
		filterResponse.handleException(authException, request, response);
	}
}

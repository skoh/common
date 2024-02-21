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

import org.oh.common.model.http.FilterResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 권한 예외(AccessDeniedException)  핸들링
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class TokenAccessDeniedHandler
		implements AccessDeniedHandler {
	protected final FilterResponse filterResponse;

	/**
	 * 권한 예외 발생시 응답 메세지 출력
	 *
	 * @param request               HTTP 요청 정보
	 * @param response              HTTP 응답 정보
	 * @param accessDeniedException 권한 예외
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					   AccessDeniedException accessDeniedException) throws IOException {
		filterResponse.handleException(accessDeniedException, request, response);
	}
}

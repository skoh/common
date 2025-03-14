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

package org.oh.common.filter;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.oh.common.model.http.FilterResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 에외 핸들링 필더
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
public class ExceptionHandlerFilter
		extends OncePerRequestFilter {
	protected final FilterResponse filterResponse;

	/**
	 * 필터에 대한 예외 핸들링
	 *
	 * @param request     HTTP 요청 정보
	 * @param response    HTTP 응답 정보
	 * @param filterChain 필터 체인
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
		try {
			filterChain.doFilter(cachingRequest, response);
		} catch (Exception e) {
			filterResponse.handleException(e, cachingRequest, response);
		}
	}
}

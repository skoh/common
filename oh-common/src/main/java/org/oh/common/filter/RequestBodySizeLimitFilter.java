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

import lombok.Setter;
import org.oh.common.config.CommonConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * HTTP 요청의 바디 크기 제한 필터
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### 요쳥 관리
 *   request:
 *     ### 요청문 최대 크기
 *     limit-size-bytes: 1_000_000
 * </pre>
 */
@Setter
@Component
@Validated
@ConfigurationProperties(RequestBodySizeLimitFilter.PROPERTY_PREFIX)
@ConditionalOnProperty(value = "limit-size-bytes", prefix = RequestBodySizeLimitFilter.PROPERTY_PREFIX)
public class RequestBodySizeLimitFilter
		extends OncePerRequestFilter {
	protected static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".request";

	private int limitSizeBytes;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		long size = request.getContentLengthLong();
		if (size > limitSizeBytes) {
			throw new CommonException(CommonError.COM_INVALID_ARGUMENT,
					String.format("Request content exceeded limit of %d bytes(size: %d)", limitSizeBytes, size));
		}

		filterChain.doFilter(request, response);
	}
}

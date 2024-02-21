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

package org.oh.common.config.httpclient;

import org.oh.common.util.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

/**
 * 기본 HTTP 클라이언트 초기화
 */
@Configuration
@Validated
@ConditionalOnProperty(value = "connect-timeout-sec", prefix = HttpClientConfig.PROPERTY_FULL_PREFIX)
public class HttpClientConfig
		extends AbstractHttpClientConfig {
	private static final String PROPERTY_NAME = "default";
	protected static final String PROPERTY_FULL_PREFIX = PROPERTY_PREFIX + '.' + PROPERTY_NAME;

	@Primary
	@Bean
	public HttpClient httpClient(
			RestTemplate restTemplate,
			@Value("${" + PROPERTY_FULL_PREFIX + ".max-conn-total}") int threadPoolSize) {
		return new HttpClient(restTemplate, DEFAULT_CORE_THREAD_POOL_SIZE, threadPoolSize,
				DEFAULT_THREAD_POOL_NAME_PREFIX + PROPERTY_NAME);
	}

	@Primary
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return super.restTemplate(builder);
	}
}

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

package org.oh.sample.config;

import org.oh.common.config.httpclient.AbstractHttpClientConfig;
import org.oh.common.util.HttpClient;
import org.oh.sample.model.Sample;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

/**
 * 샘플 HTTP 클라이언트 초기화
 */
@Configuration
@Validated
@ConfigurationProperties(HttpClientSampleConfig.PROPERTY_FULL_PREFIX)
@ConditionalOnProperty(value = "connect-timeout-sec", prefix = HttpClientSampleConfig.PROPERTY_FULL_PREFIX)
public class HttpClientSampleConfig
		extends AbstractHttpClientConfig {
	protected static final String PROPERTY_FULL_PREFIX = PROPERTY_PREFIX + '.' + Sample.NAME_SPACE;

	@Bean
	public HttpClient httpClientSample(
			@Qualifier("restTemplateSample") RestTemplate restTemplate,
			@Value("${" + PROPERTY_FULL_PREFIX + ".max-conn-total}") int threadPoolSize) {
		return new HttpClient(restTemplate, DEFAULT_CORE_THREAD_POOL_SIZE, threadPoolSize,
				DEFAULT_THREAD_POOL_NAME_PREFIX + Sample.NAME_SPACE);
	}

	@Bean
	public RestTemplate restTemplateSample(RestTemplateBuilder builder) {
		return restTemplate(builder, new RestResponseExceptionHandlerSample());
	}

	@Override
	protected RestTemplateRequestCustomizer<ClientHttpRequest> restTemplateRequestCustomizers() {
		super.restTemplateRequestCustomizers();
		return request -> {
			HttpHeaders httpHeaders = request.getHeaders();
			httpHeaders.set("key", "value");
		};
	}

	private static final class RestResponseExceptionHandlerSample
			extends RestResponseExceptionHandler {
//		@Override
//		public void handleError(ClientHttpResponse response) throws IOException {
//			super.handleError(response);
//		}
	}
}

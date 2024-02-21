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

package org.oh.common.util;

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * HTTP 클라이언트
 */
@Slf4j
public class HttpClient
		implements Closeable {
	protected final RestTemplate restTemplate;
	protected final String namePrefix;
	protected final ThreadPoolExecutor threadPool;

	/**
	 * 조건에 맞는 HTTP 클라이언트를 샌성
	 *
	 * @param restTemplate       REST 템플릿
	 * @param coreThreadPoolSize 기본 쓰레드 풀 크기
	 * @param maxThreadPoolSize  최대 쓰레드 풀 크기
	 * @param threadNamePrefix   시작 쓰레드명
	 */
	public HttpClient(RestTemplate restTemplate, int coreThreadPoolSize,
					  int maxThreadPoolSize, String threadNamePrefix) {
		this.restTemplate = restTemplate;
		this.namePrefix = threadNamePrefix;
		threadPool = ThreadUtil.createThreadPool(coreThreadPoolSize, maxThreadPoolSize, 60, threadNamePrefix);
	}

	@Override
	public void close() {
		threadPool.shutdown();
		log.info("Shutdown threadPool: {}", namePrefix);
	}

	/**
	 * HTTP 통신으로 요청문을 전송
	 *
	 * @param request HTTP 요청문
	 * @param type    클래스 타입
	 * @return HTTP 응답 정보
	 */
	public <T> ResponseEntity<T> request(Request request, Class<T> type) {
		return request(request, ParameterizedTypeReference.forType(type));
	}

	/**
	 * HTTP 통신으로 요청문을 전송
	 *
	 * @param request HTTP 요청문
	 * @param type    파라미터 타입 레퍼런스
	 * @return HTTP 응답 정보
	 */
//	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> request(Request request, ParameterizedTypeReference<T> type) {
		String uri = request.url;
		if (request.getMethod() == HttpMethod.GET && request.getBody() != null) {
			uri = request.getUri();
			request.setBody(null);
		}
		log.debug("request uri: {} {}", uri, request);

		ResponseEntity<T> result;
//		ResponseEntity<Object> result;
		try {
//			if (true) throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "test");
			result = restTemplate.exchange(uri, request.getMethod(),
					new HttpEntity<>(request.getBody(), request.getHeaders()), type);
//					new HttpEntity<>(request.getBody(), request.getHeaders()), Object.class);
			log.debug("response: {}", StringUtil.toCodeString(result.getStatusCode(), result.getBody()));
		} catch (HttpStatusCodeException e) {
			throw new CommonException(CommonError.COM_HTTP_CLIENT_ERROR,
					String.format("request: %s response: %s",
							request, StringUtil.toCodeString(e.getStatusCode(), e.getResponseBodyAsString())), e);
		} catch (Exception e) {
			throw new CommonException(CommonError.COM_HTTP_CLIENT_ERROR,
					String.format("request: %s message: %s", request, e.getMessage()), e);
		}
		return result;
//		return new ResponseEntity<>(JsonUtil.convertValue(result.getBody(), (Class<T>) type.getType()),
//				result.getHeaders(), result.getStatusCode());
	}

	/**
	 * HTTP 통신으로 요청문을 비동기(쓰레드 생성) 전송
	 *
	 * @param request HTTP 요청문
	 * @param type    클래스 타입
	 * @param success 성공시 실행할 메소드
	 * @param fail    실패시 실행할 메소드
	 * @return 미래 HTTP 응답 정보
	 */
	public <T> CompletableFuture<ResponseEntity<T>> requestAsync(Request request,
																 Class<T> type,
																 Consumer<? super ResponseEntity<T>> success,
																 BiConsumer<Throwable, String> fail) {
		return requestAsync(request, ParameterizedTypeReference.forType(type), success, fail);
//		CompletableFuture<ResponseEntity<T>> result = CompletableFuture
//				.supplyAsync(() -> request(request, type), threadPool);
//		result.thenAccept(success);
//		return result.exceptionally(CommonUtil.toFunction(fail, request.toString(), null));
	}

	/**
	 * HTTP 통신으로 요청문을 비동기(쓰레드 생성) 전송
	 *
	 * @param request HTTP 요청문
	 * @param type    파라미터 타입 레퍼런스
	 * @param success 성공시 실행할 메소드
	 * @param fail    실패시 실행할 메소드
	 * @return 미래 HTTP 응답 정보
	 */
	public <T> CompletableFuture<ResponseEntity<T>> requestAsync(Request request,
																 ParameterizedTypeReference<T> type,
																 Consumer<? super ResponseEntity<T>> success,
																 BiConsumer<Throwable, String> fail) {
		CompletableFuture<ResponseEntity<T>> result = CompletableFuture
				.supplyAsync(() -> request(request, type), threadPool);
		result.thenAccept(success);
		return result.exceptionally(CommonUtil.toFunction(fail, request.toString(), null));
	}

	/**
	 * HTTP 요청문
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Request {
		/**
		 * 요총 URL
		 */
		protected String url;
		/**
		 * 요청 메소드
		 */
		protected HttpMethod method;
		/**
		 * 요청 헤더
		 */
		protected HttpHeaders headers;
		/**
		 * 요청 바디
		 */
		protected Object body;

		/**
		 * URI를 반환
		 *
		 * @return URI
		 */
		public String getUri() {
			return url + WebUtil.getUri(body);
		}
	}
}

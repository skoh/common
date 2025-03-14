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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import okhttp3.ConnectionPool;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.oh.common.config.CommonConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.validation.constraints.Min;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 기본 HTTP 클라이언트 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### (비)동기 HTTP Client
 *   http-client:
 *     default: &http-client-default
 *       ### httpComponents(defalut)/okHttp/simple
 *       vendor: httpComponents
 *       connect-timeout-sec: 2
 *       read-timeout-sec: 10
 *       max-conn-total: ${server.tomcat.threads.max}
 *       max-conn-per-route: ${common.http-client.default.max-conn-total}
 *     sample:
 *       <<: *http-client-default
 *       max-conn-total: ${server.tomcat.threads.max}
 *       max-conn-per-route: ${common.http-client.sample.max-conn-total}
 * </pre>
 */
@Setter
@Validated
@ConfigurationProperties(HttpClientConfig.PROPERTY_FULL_PREFIX)
public abstract class AbstractHttpClientConfig {
	protected static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".http-client";
	protected static final int DEFAULT_CONNECTION_TIME_TO_LIVE_SEC = 60;
	protected static final int DEFAULT_CORE_THREAD_POOL_SIZE = 5;
	protected static final String DEFAULT_THREAD_POOL_NAME_PREFIX = "hc-";

	protected String vendor = Vendor.HTTP_COMPONENTS.value;
	@Min(1)
	protected int connectTimeoutSec;
	@Min(1)
	protected int readTimeoutSec;
	@Min(1)
	protected int maxConnTotal;
	@Min(1)
	protected int maxConnPerRoute;

	protected RestTemplate restTemplate(RestTemplateBuilder builder) {
		return restTemplate(builder, new RestResponseExceptionHandler());
	}

	protected RestTemplate restTemplate(RestTemplateBuilder builder,
										ResponseErrorHandler responseErrorHandler) {
		return builder.setConnectTimeout(Duration.ofSeconds(connectTimeoutSec))
				.setReadTimeout(Duration.ofSeconds(readTimeoutSec))
				.additionalCustomizers(restTemplateCustomizer())
				.additionalRequestCustomizers(restTemplateRequestCustomizers())
				.errorHandler(responseErrorHandler)
				.build();
	}

	protected RestTemplateCustomizer restTemplateCustomizer() {
		return restTemplate -> {
			X509TrustManager x509TrustManager = new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) {
				} //NOSONAR HTTP 클라이언트 용으로만 사용

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) {
				} //NOSONAR HTTP 클라이언트 용으로만 사용

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0]; //NOSONAR HTTP 클라이언트 용으로만 사용
				}
			};

			SSLContext sslContext;
			try {
				sslContext = SSLContexts.custom()
						.loadTrustMaterial(null, new TrustSelfSignedStrategy())
						.build();
			} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
				throw new CommonException(CommonError.COM_HTTP_CLIENT_ERROR, e);
			}

			ClientHttpRequestFactory factory;
			if (Vendor.HTTP_COMPONENTS.value.equalsIgnoreCase(vendor)) {
				SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sslContext,
						NoopHostnameVerifier.INSTANCE);
				org.apache.http.client.HttpClient client = HttpClientBuilder.create()
						.setDefaultRequestConfig(RequestConfig.custom()
								.setConnectTimeout((int) Duration.ofSeconds(connectTimeoutSec).toMillis())
								.setSocketTimeout((int) Duration.ofSeconds(readTimeoutSec).toMillis())
								.build())
						.setMaxConnPerRoute(maxConnPerRoute)
						.setConnectionTimeToLive(DEFAULT_CONNECTION_TIME_TO_LIVE_SEC, TimeUnit.SECONDS)
						.setSSLSocketFactory(scsf)
						.build();
				factory = new HttpComponentsClientHttpRequestFactory(client);
			} else if (Vendor.OK_HTTP.value.equalsIgnoreCase(vendor)) {
				ConnectionPool okHttpConnectionPool = new ConnectionPool(maxConnTotal,
						DEFAULT_CONNECTION_TIME_TO_LIVE_SEC, TimeUnit.SECONDS);
				OkHttpClient client = new OkHttpClient.Builder()
						.connectTimeout(Duration.ofSeconds(connectTimeoutSec))
						.readTimeout(Duration.ofSeconds(readTimeoutSec))
						.cookieJar(new JavaNetCookieJar(new CookieManager()))
						.connectionPool(okHttpConnectionPool)
						.sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
						.hostnameVerifier((hostname, session) -> true)
						.retryOnConnectionFailure(false)
						.build();
				factory = new OkHttp3ClientHttpRequestFactory(client);
			} else {
				CookieHandler.setDefault(new CookieManager());
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
				factory = new SimpleClientHttpRequestFactory();
			}
			restTemplate.setRequestFactory(factory);
		};
	}

	protected RestTemplateRequestCustomizer<ClientHttpRequest> restTemplateRequestCustomizers() {
		return request -> {
			HttpHeaders httpHeaders = request.getHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		};
	}

	protected static class RestResponseExceptionHandler
			extends DefaultResponseErrorHandler {
//		@Override
//		public void handleError(ClientHttpResponse response) throws IOException {
////			log.debug("Response status: {}", response.getStatusCode());
//			super.handleError(response);
//		}
	}

	/**
	 * HTTP 클라이언트 벤더
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private enum Vendor {
		HTTP_COMPONENTS("httpComponents"),
		OK_HTTP("okHttp"),
		SIMPLE("simple");

		private final String value;
	}
}

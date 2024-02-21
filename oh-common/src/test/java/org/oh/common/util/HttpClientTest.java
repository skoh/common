package org.oh.common.util;

import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.exception.CommonException;
import org.oh.sample.controller.SampleDbRestController;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;

@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
@ServiceTest
public class HttpClientTest {
	private static final int count = 100;
	private static final HttpClient.Request request = HttpClient.Request.builder()
			.url("http://10.10.40.91:8010/v1/did/challenge")
			.method(HttpMethod.GET)
			.build();
	private static final Sample entity = Sample.builder()
			.id(SampleDbServiceTest.TEST_ID)
			.build();

	@Autowired
	private HttpClient httpClient;

	@Autowired
	@Qualifier("httpClientSample")
	private HttpClient httpClientSample;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01UriComp() throws Exception {
		URI uri = new URI("scheme", "userInfo", "host", 1234,
				"/path1", "id=1", "fragment1");
		Sample entity = Sample.builder()
				.id(4L)
				.name("name")
				.build();
		MultiValueMap<String, String> params = WebUtil.getParams(entity);

		UriComponents UriComp = UriComponentsBuilder.newInstance()
				.uri(uri)
				.path("/path2")
				.query("id=2")
				.queryParam("id", 3)
				.queryParams(params)
				.fragment("fragment2")
				.build();

		log.debug("uri: {}", UriComp);
	}

	@Test
	void t02httpClient() {
		List<ResponseEntity<String>> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			ResponseEntity<String> result = httpClient.request(request, String.class);
			results.add(result);
			log.debug(LoggingConfig.ONE_LINE_100);
		}
		log.debug("results: {} {}", results.size(), results);
	}

	@Test
	void t03httpClientAsync() {
		List<Future<ResponseEntity<String>>> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			CompletableFuture<ResponseEntity<String>> result = httpClient.requestAsync(request, String.class,
					this::success, this::fail);
			results.add(result);
			log.debug(LoggingConfig.ONE_LINE_100);
		}
		log.debug("results: {} {}", results.size(), results);
	}

	///////////////////////////////////////////////////////////////////////////

	//	@Test
	void t11sync() {
		Assertions.assertThrows(CommonException.class, this::sync);
	}

	//	@Test
	void t12async() {
		Assertions.assertThrows(CompletionException.class, () -> callAsync(httpClient));
	}

	//	@Test
	void t13async() {
		Assertions.assertThrows(CompletionException.class, () -> callAsync(httpClientSample));
	}

	///////////////////////////////////////////////////////////////////////////

	private void sync() {
//		List<ResponseEntity<List<Sample>>> results = new ArrayList<>();
		List<ResponseEntity<Sample>> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (i == count - 1)
				entity.setId(2L);
//			ResponseEntity<List<Sample>> result = null;
			ResponseEntity<Sample> result = null;
			try {
//				HttpClient.Request request = HttpClient.Request.builder()
//						.url(HttpClientCrudTest.URL + SampleRestController.PATH)
//						.method(HttpMethod.GET)
//						.body(entity)
//						.build();
//				result = httpClient.request(request, new ParameterizedTypeReference<List<Sample>>() {
//				});
				HttpClient.Request request = HttpClient.Request.builder()
						.url(HttpClientCrudTest.URL + SampleDbRestController.PATH + "/" + entity.getId())
						.method(HttpMethod.GET)
						.build();
				result = httpClient.request(request, new ParameterizedTypeReference<Sample>() {
				});
				successSample(result);
			} catch (Exception e) {
				fail(e, HttpMethod.GET + " " + HttpClientCrudTest.URL + " body: " + entity);
			}
			results.add(result);
			log.debug(LoggingConfig.ONE_LINE_100);
		}
		log.debug("results: {} {}", results.size(), JsonUtil.toPrettyString(results));
	}

	private void callAsync(HttpClient httpClient) {
//		List<Future<ResponseEntity<List<Sample>>>> results = new ArrayList<>();
		List<Future<ResponseEntity<Sample>>> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Sample entity = Sample.builder()
					.id(SampleDbServiceTest.TEST_ID)
					.build();
			if (i == count - 1)
				entity.setId(2L);

//			CompletableFuture<ResponseEntity<List<Sample>>> result =
//					httpClient.requestAsync(
//							HttpClient.Request.builder()
//									.url(HttpClientCrudTest.URL + SampleRestController.PATH)
//									.method(HttpMethod.GET)
//									.body(entity)
//									.build(), new ParameterizedTypeReference<List<Sample>>() {
			CompletableFuture<ResponseEntity<Sample>> result =
					httpClient.requestAsync(
							HttpClient.Request.builder()
									.url(HttpClientCrudTest.URL + SampleDbRestController.PATH + "/" + entity.getId())
									.method(HttpMethod.GET)
									.build(), new ParameterizedTypeReference<Sample>() {
							}, this::successSample, this::fail);
			results.add(result);
			log.debug(LoggingConfig.ONE_LINE_100);
		}
		log.debug("results: {}", ThreadUtil.allOf(results));
	}

	private void success(ResponseEntity<String> r) {
		log.debug("response: {}", JsonUtil.toPrettyString(r));
	}

	//	private void success(ResponseEntity<List<Sample>> r) {
	private void successSample(ResponseEntity<Sample> r) {
		log.debug("response: {}", JsonUtil.toPrettyString(r));
	}

	private void fail(Throwable e, String request) {
		Throwable root = ExceptionUtil.getRootCause(e);
		if (root instanceof HttpClientErrorException) {
			HttpClientErrorException hcee = (HttpClientErrorException) root;
			log.debug("response: {}", StringUtil.toCodeString(hcee.getStatusCode(),
					JsonUtil.toPrettyString(hcee.getResponseBodyAsString())));
		}
		String message = "request: " + request;
		log.error(ExceptionUtil.getMessageAndType(e) + " " + message, e);
		throw new CommonException(message, e);
	}
}

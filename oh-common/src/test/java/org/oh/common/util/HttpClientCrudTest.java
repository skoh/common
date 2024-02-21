package org.oh.common.util;

import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.model.enume.State;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.oh.sample.controller.SampleDbRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Disabled
@Slf4j
@ServiceTest
public class HttpClientCrudTest {
	public static final String URL = "http://localhost:8080";

	private static final Sample entity = JsonUtil.copy(SampleDbServiceTest.ENTITY, Sample.class);
	private static final Sample entityResult = Sample.builder()
			.id(SampleDbServiceTest.TEST_ID)
			.build();

	@Autowired
	private HttpClient httpClient;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() {
		HttpClient.Request request = HttpClient.Request.builder()
				.url(URL + SampleDbRestController.PATH)
				.method(HttpMethod.POST)
				.body(entity)
				.build();
		ResponseEntity<Sample> result = httpClient.request(request, Sample.class);
		Sample body = result.getBody();
		Assertions.assertTrue(body.getId() > 0);
		entityResult.setId(body.getId());
	}

	@Test
	void t02find() {
		HttpClient.Request request = HttpClient.Request.builder()
				.url(URL + SampleDbRestController.PATH)
				.method(HttpMethod.GET)
				.body(entityResult)
				.build();
		ResponseEntity<List<Sample>> result = httpClient.request(request, new ParameterizedTypeReference<List<Sample>>() {
		});
		Assertions.assertFalse(result.getBody().isEmpty());
	}

	@Test
	void t03update() {
		State state = State.DELETED;
		entityResult.setState(state);
		HttpClient.Request request = HttpClient.Request.builder()
				.url(URL + SampleDbRestController.PATH)
				.method(HttpMethod.PUT)
				.body(entityResult)
				.build();
		ResponseEntity<Sample> result = httpClient.request(request, Sample.class);
		Assertions.assertEquals(state, result.getBody().getState());
	}

	@Test
	void t04delete() {
		HttpClient.Request request = HttpClient.Request.builder()
				.url(URL + SampleDbRestController.PATH)
				.method(HttpMethod.DELETE)
				.body(entityResult)
				.build();
		httpClient.request(request, Object.class);
	}
}

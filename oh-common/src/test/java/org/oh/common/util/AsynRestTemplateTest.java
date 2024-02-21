package org.oh.common.util;

import org.oh.common.config.ServiceTest;
import org.oh.sample.controller.SampleDbRestController;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.ArrayList;
import java.util.List;

@Disabled
@Slf4j
@ServiceTest
public class AsynRestTemplateTest {
	private static final Sample entityResult = Sample.builder()
			.id(SampleDbServiceTest.TEST_ID)
			.build();
	private static final int count = 4;

	@Autowired
	private AsyncRestTemplate asyncRestTemplate;

	@Test
	void test() throws Exception {
		List<ListenableFuture<ResponseEntity<List<Sample>>>> results = new ArrayList<>();
		String query = WebUtil.getUri(entityResult).toString();
		for (int i = 0; i < count; i++) {
			if (i == count - 1) entityResult.setId(2L);
			ListenableFuture<ResponseEntity<List<Sample>>> result =
					asyncRestTemplate.exchange(HttpClientCrudTest.URL + SampleDbRestController.PATH +
									query, HttpMethod.GET, new HttpEntity<>(null, null),
							new ParameterizedTypeReference<List<Sample>>() {
							});
			results.add(result);
		}
		log.debug("results: {} {}", results.size(), results);

		for (ListenableFuture<ResponseEntity<List<Sample>>> future : results) {
			future.addCallback(
					r -> {
						// TODO
					},
					e -> {
						// TODO
					});
		}

		Thread.sleep(1_000_000);
	}
}

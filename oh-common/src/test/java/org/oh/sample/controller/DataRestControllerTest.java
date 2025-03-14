package org.oh.sample.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oh.common.config.ControllerTest;
import org.oh.common.config.LoggingConfig;
import org.oh.common.model.data.Data;
import org.oh.common.model.enume.State;
import org.oh.common.service.data.DataService;
import org.oh.common.util.DateUtil;
import org.oh.common.util.JsonUtil;
import org.oh.sample.service.SampleDbServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.oh.common.controller.data.DataRestController.PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled
@Slf4j
@ControllerTest
public class DataRestControllerTest {
	public static final String TEST_DATA_ID = "1";

	private static final Data entity = JsonUtil.copy(Data.builder()
			.id(DateUtil.nanoTime())
			.attributes(JsonUtil.readTree(SampleDbServiceTest.ENTITY))
			.build(), Data.class);
	private static final Data entityResult = Data.builder()
			.id(TEST_DATA_ID)
			.build();
	private static final Data entityResult2 = Data.builder()
			.build();

	@Autowired
	public MockMvc mvc;
	@Autowired
	private DataService service;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		mvc.perform(post(PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entity)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					Data result = JsonUtil.readValue(r.getResponse().getContentAsString(), Data.class);
					AssertionErrors.assertNotNull("", result.getId());
					entityResult.setId(result.getId());
				});
	}

	@Test
	void t02findById() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(get(PATH + "/" + entityResult.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					Data result = JsonUtil.readValue(r.getResponse().getContentAsString(), Data.class);
					log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
					AssertionErrors.assertEquals("", entityResult.getId(), result.getId());
				});
	}

	@Test
	void t05findPage() throws Exception {
		mvc.perform(get(PATH + "/page")
						.param("psize", "10")
						.param("page", "1")
						.param("sort", "state")
						.param("sort", "id,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numberOfElements", Matchers.greaterThan(0)));
	}

	@Test
	void t06findLimit() throws Exception {
		mvc.perform(get(PATH + "/limit")
						.param("lsize", "10")
						.param("sort", "state")
						.param("sort", "id,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					List<Data> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Data>>() {
							});
					AssertionErrors.assertTrue("", result.size() > 0);
				});
	}

	@Test
	void t07count() throws Exception {
		mvc.perform(get(PATH + "/count"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					int result = Integer.parseInt(r.getResponse().getContentAsString());
					AssertionErrors.assertTrue("", result > 0);
				});
	}

	@Test
	void t08exists() throws Exception {
		mvc.perform(get(PATH + "/exists/" + entityResult.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					boolean result = Boolean.parseBoolean(r.getResponse().getContentAsString());
					AssertionErrors.assertTrue("", result);
				});
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11update() throws Exception {
		State state = State.END;
		entityResult.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(put(PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityResult)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					Data result = JsonUtil.readValue(r.getResponse().getContentAsString(), Data.class);
					AssertionErrors.assertEquals("", state, result.getState());
				});
	}

	@Test
	void t12merge() throws Exception {
		Data entityTemp = JsonUtil.copy(Data.builder()
				.id(DateUtil.nanoTime())
				.attributes(JsonUtil.readTree(SampleDbServiceTest.ENTITY))
				.build(), Data.class);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		mvc.perform(post(PATH + "/merge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityTemp)))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(r -> {
					Data result = JsonUtil.readValue(r.getResponse().getContentAsString(), Data.class);
					entityTemp.setId(result.getId());
					AssertionErrors.assertNotNull("", result.getId());
					entityResult2.setId(result.getId());
				});

		Thread.sleep(1_000);
		log.debug(LoggingConfig.ONE_LINE_100);
		State state = State.END;
		entityTemp.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		mvc.perform(post(PATH + "/merge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityTemp)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					Data result = JsonUtil.readValue(r.getResponse().getContentAsString(), Data.class);
					AssertionErrors.assertEquals("", state, result.getState());
				});
	}

	@Test
	void t13updateAll() throws Exception {
		State state = State.START;
		Data entity1 = Data.builder()
				.id(entityResult.getId())
				.state(state)
				.build();
		Data entity2 = Data.builder()
				.id(entityResult2.getId())
				.state(state)
				.build();
		List<Data> entities = ImmutableList.of(entity1, entity2);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(put(PATH + "/all")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entities)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					List<Data> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Data>>() {
							});
					AssertionErrors.assertEquals("", state, result.get(0).getState());
					AssertionErrors.assertEquals("", state, result.get(1).getState());
				});

		log.debug(LoggingConfig.ONE_LINE_100);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult2.toString()));
		mvc.perform(delete(PATH + "/" + entityResult2.getId()))
				.andDo(print())
				.andExpect(status().isOk());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t99delete() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(delete(PATH + "/" + entityResult.getId()))
				.andDo(print())
				.andExpect(status().isOk());
	}
}

package org.oh.sample.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oh.common.config.ControllerTest;
import org.oh.common.config.LoggingConfig;
import org.oh.common.model.enume.State;
import org.oh.common.util.JsonUtil;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import org.oh.sample.service.SampleMapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

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
public class SampleMapperControllerTest {
	private static final Sample entity = JsonUtil.copy(SampleDbServiceTest.ENTITY, Sample.class);
	private static final Sample entityResult = Sample.builder()
			.id(SampleDbServiceTest.TEST_ID)
			.build();
	private static final Sample entityResult2 = Sample.builder()
			.build();

	@Autowired
	public MockMvc mvc;
	@Autowired
	private SampleMapperService service;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		mvc.perform(MockMvcRequestBuilders.post(SampleMapperRestController.PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entity)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertNotNull("", result.getId());
					entityResult.setId(result.getId());
				});
	}

	@Test
	void t02findById() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(get(SampleMapperRestController.PATH + "/" + entityResult.getId()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertEquals("", entityResult.getId(), result.getId());
				});
	}

	@Test
	void t04findAll() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get(SampleMapperRestController.PATH)
						.param("sort", "state")
						.param("sort", "id,DESC")
						.param("state", "ACTIVE"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					List<Sample> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Sample>>() {
							});
					AssertionErrors.assertTrue("", result.size() > 0);
				});
	}

	@Test
	void t05findPage() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/page")
						.param("psize", "10")
						.param("page", "1")
						.param("sort", "state")
						.param("sort", "id,DESC")
						.param("state", "ACTIVE"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numberOfElements", Matchers.greaterThan(0)));
	}

	@Test
	void t06findLimit() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/limit")
						.param("lsize", "10")
						.param("sort", "state")
						.param("sort", "id,DESC")
						.param("state", "ACTIVE"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					List<Sample> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Sample>>() {
							});
					AssertionErrors.assertTrue("", result.size() > 0);
				});
	}

	@Test
	void t07count() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/count")
						.param("state", "ACTIVE"))
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
		mvc.perform(get(SampleMapperRestController.PATH + "/exists")
						.param("state", "ACTIVE"))
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
		State state = State.DELETED;
		entityResult.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(MockMvcRequestBuilders.put(SampleMapperRestController.PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityResult)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertEquals("", state, result.getState());
				});
	}

	@Test
	void t12merge() throws Exception {
		Sample entityTemp = JsonUtil.copy(SampleDbServiceTest.ENTITY, Sample.class);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		mvc.perform(post(SampleMapperRestController.PATH + "/merge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityTemp)))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertNotNull("", result.getId());
					entityTemp.setId(result.getId());
					entityResult2.setId(result.getId());
				});

		Thread.sleep(1_000);
		log.debug(LoggingConfig.ONE_LINE_100);
		State state = State.DELETED;
		entityTemp.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		mvc.perform(post(SampleMapperRestController.PATH + "/merge")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entityTemp)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertEquals("", state, result.getState());
				});
	}

	@Test
	void t13updateAll() throws Exception {
		State state = State.ACTIVE;
		Sample entity1 = Sample.builder()
				.id(entityResult.getId())
				.state(state)
				.build();
		Sample entity2 = Sample.builder()
				.id(entityResult2.getId())
				.state(state)
				.build();
		List<Sample> entities = ImmutableList.of(entity1, entity2);
		log.debug("entities: {}", JsonUtil.toPrettyString(entities));
		mvc.perform(put(SampleMapperRestController.PATH + "/all")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entities)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(r -> {
					List<Sample> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Sample>>() {
							});
					AssertionErrors.assertEquals("", state, result.get(0).getState());
					AssertionErrors.assertEquals("", state, result.get(1).getState());
				});

		log.debug(LoggingConfig.ONE_LINE_100);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult2.toString()));
		mvc.perform(delete(SampleMapperRestController.PATH + "/" + entityResult2.getId()))
				.andDo(print())
				.andExpect(status().isOk());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t21search() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/search")
						.param("regDateStart", "2000-01-01")
						.param("regDateEnd", "2099-12-31")
						.param("sort", "state")
						.param("sort", "id,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(r -> {
					List<Sample> result = JsonUtil.readValue(r.getResponse().getContentAsString(),
							new TypeReference<List<Sample>>() {
							});
					AssertionErrors.assertTrue("", result.size() > 0);
				});
	}

	@Test
	void t22searchPage() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/search/page")
						.param("regDateStart", "2000-01-01")
						.param("regDateEnd", "2099-12-31")
						.param("psize", "10")
						.param("page", "1")
						.param("sort", "state")
						.param("sort", "id,DESC"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.numberOfElements", Matchers.greaterThan(0)));
	}

	@Test
	void t23stats() throws Exception {
		mvc.perform(get(SampleMapperRestController.PATH + "/stats")
						.param("regDateStart", "2000-01-01")
						.param("regDateEnd", "2099-12-31")
						.param("sort", "state"))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(r -> {
					List<Map<String, Object>> result = JsonUtil.readValueListMap(r.getResponse().getContentAsString());
					AssertionErrors.assertTrue("", result.size() > 0);
				});
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t91validate() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post(SampleMapperRestController.PATH)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(new Sample())))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void t99delete() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(delete(SampleMapperRestController.PATH + "/" + entityResult.getId()))
				.andDo(print())
				.andExpect(status().isOk());
	}
}

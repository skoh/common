package org.oh.sample.controller;

import org.oh.common.config.ControllerTest;
import org.oh.common.config.LoggingConfig;
import org.oh.common.util.JsonUtil;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;

import static org.oh.sample.controller.TestController.PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled
@Slf4j
@ControllerTest
public class TestRestControllerTest {
	private static final MockHttpSession SESSION = new MockHttpSession();
	private static final Sample entity = JsonUtil.copy(SampleDbServiceTest.ENTITY, Sample.class);
	private static final Sample entityResult = Sample.builder()
			.id(SampleDbServiceTest.TEST_ID)
			.build();

	@Autowired
	public MockMvc mvc;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		mvc.perform(post(PATH)
						.session(SESSION)
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
		mvc.perform(get(PATH + "/" + entityResult.getId())
						.session(SESSION))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					Sample result = JsonUtil.readValue(r.getResponse().getContentAsString(), Sample.class);
					AssertionErrors.assertEquals("", entityResult.getId(), result.getId());
				});
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11setSession() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		mvc.perform(post(PATH + "/session/test")
						.session(SESSION)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entity)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void t12getSession() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(get(PATH + "/session/test")
						.session(SESSION))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t98delete() throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		mvc.perform(delete(PATH + "/" + entityResult.getId())
						.session(SESSION))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	void t99delSession() throws Exception {
		mvc.perform(delete(PATH + "/session/test")
						.session(SESSION))
				.andDo(print())
				.andExpect(status().isOk());
	}
}

package org.oh.common.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oh.common.config.ControllerTest;
import org.oh.common.config.LoggingConfig;
import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Login;
import org.oh.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.oh.common.controller.CommonRestController.PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@Disabled
@Slf4j
@ControllerTest
public class CommonRestControllerTest {
	@Autowired
	public MockMvc mvc;

	private static final Login entity = Login.builder()
			.id("user")
			.password("1234567890") // NOSONAR 테스트 코드이므로 제외
			.build();
	private static String token;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01login() throws Exception {
		login(entity);
	}

	@Test
	void t12user() throws Exception {
		user(status().isOk());
	}

	@Test
	void t13manager() throws Exception {
		manager(status().isForbidden());
	}

	@Test
	void t14admin() throws Exception {
		admin(status().isForbidden());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t21login() throws Exception {
		entity.setId("manager");
		login(entity);
	}

	@Test
	void t22user() throws Exception {
		user(status().isOk());
	}

	@Test
	void t23manager() throws Exception {
		manager(status().isOk());
	}

	@Test
	void t24admin() throws Exception {
		admin(status().isForbidden());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t31login() throws Exception {
		entity.setId(AbstractUser.ADMIN_ID);
		login(entity);
	}

	@Test
	void t32user() throws Exception {
		user(status().isOk());
	}

	@Test
	void t33manager() throws Exception {
		manager(status().isOk());
	}

	@Test
	void t34admin() throws Exception {
		admin(status().isOk());
	}

	///////////////////////////////////////////////////////////////////////////

	void login(Login entity) throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity));
		mvc.perform(post(PATH + "/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JsonUtil.toString(entity)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isNotEmpty())
				.andDo(r -> {
					token = r.getResponse().getContentAsString();
					AssertionErrors.assertNotNull("", token);
				});
	}

	private void user(ResultMatcher resultMatcher) throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity));
		mvc.perform(get(PATH + "/user")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andDo(print())
				.andExpect(resultMatcher)
				.andDo(r -> {
					String result = r.getResponse().getContentAsString();
					AssertionErrors.assertNotNull("", result);
				});
	}

	private void manager(ResultMatcher resultMatcher) throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity));
		mvc.perform(get(PATH + "/manager")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andDo(print())
				.andExpect(resultMatcher)
				.andDo(r -> {
					String result = r.getResponse().getContentAsString();
					AssertionErrors.assertNotNull("", result);
				});
	}

	private void admin(ResultMatcher resultMatcher) throws Exception {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity));
		mvc.perform(get(PATH + "/admin")
						.header(HttpHeaders.AUTHORIZATION, token))
				.andDo(print())
				.andExpect(resultMatcher)
				.andDo(r -> {
					String result = r.getResponse().getContentAsString();
					AssertionErrors.assertNotNull("", result);
				});
	}
}

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

package org.oh.sample.controller;

import org.oh.common.annotation.ResultLogging;
import org.oh.common.config.DataGridConfig;
import org.oh.common.config.LoggingConfig;
import org.oh.common.controller.DefaultController;
import org.oh.common.exception.CommonException;
import org.oh.common.util.DataGridUtil;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.WebUtil;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 테스트 컨트롤러
 */
//@CrossOrigin(origins = "http://localhost:8080")
@Tag(name = "테스트", description = "테스트 API")
@Slf4j
@Controller
@RequestMapping(TestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class TestController
		implements DefaultController {
	public static final String PATH = VERSION_1 + "/test";

	protected final HazelcastInstance hazelcast;
	protected final DataGridUtil hazelcastUtil;
	protected final SampleService service;
	protected final SessionRepository<Session> sessionRepository;

	protected TestController(HazelcastInstance hazelcast,
							 DataGridUtil hazelcastUtil,
							 SampleService service,
							 @Lazy SessionRepository<Session> sessionRepository) {
		this.hazelcast = hazelcast;
		this.hazelcastUtil = hazelcastUtil;
		this.service = service;
		this.sessionRepository = sessionRepository;
	}

//	@ResultLogging
	@GetMapping("jsp")
	public String jsp() {
		return "test";
	}

//	@ResultLogging
	@GetMapping("templete")
	public String templete(Model model) {
		model.addAttribute("test", "테스트");
		return WebUtil.getTemplatesName() + "test";
	}

//	@ResultLogging
	@GetMapping("error")
	public String error() {
		throw new CommonException("test", new Exception("test2"));
	}

	///////////////////////////////////////////////////////////////////////////

//	@ResultLogging(result = true)
	@Operation(summary = "단일 항목 저장")
	@PostMapping
	@ResponseBody
	public Sample save(@Valid @RequestBody Sample entity) {
		return service.insert(entity);
	}

//	@ResultLogging(result = true)
	@Operation(summary = "단일 항목 조회")
	@GetMapping("{id}")
	@ResponseBody
	public Sample find(@Parameter(description = "아이디")
					   @PathVariable Long id) {
		return service.findById(id);
	}

//	@ResultLogging
	@Operation(summary = "단일 항목 삭제")
	@DeleteMapping("{id}")
	@ResponseBody
	public void delete(@Parameter(description = "아이디")
					   @PathVariable Long id) {
		service.deleteById(id);
	}

	///////////////////////////////////////////////////////////////////////////

//	@ResultLogging(indexesOfArgs = {0, 1})
	@Operation(summary = "세션 저장")
	@PostMapping("session/{name}")
	@ResponseBody
	public void setSession(@Parameter(description = "세션키", example = "test")
						   @PathVariable String name,
						   @Valid @RequestBody Sample entity,
						   HttpSession session) {
		session.setAttribute(name, entity); //NOSONAR 테스트 코드로 사용 안함
	}

//	@ResultLogging(result = true, indexesOfArgs = {0})
	@Operation(summary = "세션 조회")
	@GetMapping("session/{name}")
	@ResponseBody
	public Object getSession(@Parameter(description = "세션키", example = "test")
							 @PathVariable String name,
							 HttpSession session) {
		Object entity = session.getAttribute(name);

//		log.debug(LoggingConfig.ONE_LINE_100);
//		Session s = sessionRepository.findById(session.getId());
//		if (s != null) {
//			log.debug("session1: {}", JsonUtil.toPrettyString(s));
//			log.debug(name + ": {}", (Object) s.getAttribute(name));
//		}

		log.debug(LoggingConfig.ONE_LINE_100);
		IMap<String, MapSession> iMap = hazelcast.getMap(DataGridConfig.MAP_NAME_SESSION);
		MapSession ms = iMap.get(session.getId());
		if (ms != null) {
			log.debug("session2: {}", JsonUtil.toPrettyString(ms));
			log.debug(name + ": {}", (Object) ms.getAttribute(name));
		}

		log.debug(LoggingConfig.ONE_LINE_100);
		hazelcastUtil.logMap(DataGridConfig.MAP_NAME_SESSION, true, true);

		log.debug(LoggingConfig.ONE_LINE_100);
		hazelcastUtil.logAllMap(true, true);

		return entity;
	}

//	@ResultLogging(result = true, indexesOfArgs = {0})
	@Operation(summary = "세션 삭제")
	@DeleteMapping("session/{name}")
	@ResponseBody
	public void delSession(@Parameter(description = "세션키", example = "test")
						   @PathVariable String name,
						   HttpSession session) {
		session.removeAttribute(name);
	}
}

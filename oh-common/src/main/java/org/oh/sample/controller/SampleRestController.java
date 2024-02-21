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

import org.oh.common.controller.AbstractCrudKvController;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 샘플 컨트롤러
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### API 사용 여부
 *   api:
 *     ### 샘플 CRUD API(FilesDbRestController, SampleDbRestController, SampleMapperRestController, SampleRestController, TestController) 사용 여부 (기본값: false)
 *     sample.enabled: true
 * </pre>
 */
@Tag(name = "샘플", description = "샘플을 메모리에서 관리하는 API")
@RestController
@RequestMapping(SampleRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleRestController
		extends AbstractCrudKvController<Sample, Long> {
	public static final String PATH = "/v1/" + Sample.NAME_SPACE + "/dg";

	protected SampleRestController(SampleService service) {
		super(service);
	}
}

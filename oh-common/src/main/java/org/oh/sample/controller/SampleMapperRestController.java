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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.oh.common.controller.AbstractCrudMapperController;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.service.AbstractCrudMapperService;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.oh.sample.service.SampleMapperService;
import org.oh.sample.service.SampleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 샢플 매퍼 컨트롤러
 */
@Tag(name = "샘플 매퍼", description = "샘플을 관리하는 매퍼 API")
@RestController
@RequestMapping(SampleMapperRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleMapperRestController
		extends AbstractCrudMapperController<Sample, Long> {
	public static final String PATH = SampleDbRestController.PATH + "/" + AbstractCrudMapperService.MAPPER;

	protected final SampleMapperService service;

	protected SampleMapperRestController(SampleMapperService service) {
		super(service);
		this.service = service;
	}

	@Operation(summary = "검색")
//	@ResultLogging
	@GetMapping("search")
	public List<Sample> search(Sample entity, StatsParams params, Sorting sort) {
		return service.search(entity, params, sort);
	}

	@Operation(summary = "페이지별 검색")
//	@ResultLogging
	@GetMapping("search/page")
	public Page<Sample> search(Sample entity, StatsParams params, Paging page) {
		return service.search(entity, params, page);
	}

	@Operation(summary = "통계")
//	@ResultLogging
	@GetMapping("stats")
	public List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort) {
		return service.stats(entity, params, sort);
	}
}

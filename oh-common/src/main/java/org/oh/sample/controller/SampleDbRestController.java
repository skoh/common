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
import org.oh.common.controller.AbstractCrudDbController;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.oh.sample.service.SampleDbService;
import org.oh.sample.service.SampleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 샘플 DB 컨트롤러
 */
@Tag(name = "샘플 DB", description = "샘플을 DB에서 관리하는 API")
@RestController
@RequestMapping(SampleDbRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleDbRestController
		extends AbstractCrudDbController<Sample, Long> {
	public static final String PATH = "/v1/" + Sample.NAME_SPACE;

	protected final SampleDbService service;

	protected SampleDbRestController(SampleDbService service) {
		super(service);
		this.service = service;
	}

//	@Override
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
//	@JsonView(SampleModel.Sample.Many.class)
//	public Sample findById(@Parameter(description = "아이디")
//						   @PathVariable Long id) {
//		return super.findById(id);
//	}
//
//	@Override
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
//	@JsonView(SampleModel.Sample.One.class)
//	public List<Sample> findAll(Sample entity, Sorting sort) {
//		return super.findAll(entity, sort);
//	}
//
//	@Override
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
//	@JsonView(SampleModel.Sample.One.class)
//	public Page<Sample> findPage(Sample entity, Paging page) {
//		return super.findPage(entity, page);
//	}
//
//	@Override
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
//	@JsonView(SampleModel.Sample.One.class)
//	public List<Sample> findAll(Sample entity,
//								@Parameter(description = "페이지 갯수", example = "10")
//								@RequestParam int lsize,
//								Sorting sort) {
//		return super.findAll(entity, lsize, sort);
//	}

	@Operation(summary = "검색")
	@ResultLogging
	@GetMapping("search")
	public List<Sample> search(Sample entity, StatsParams params, Sorting sort) {
		return service.search(entity, params, sort);
	}

	@Operation(summary = "페이지별 검색")
	@ResultLogging
	@GetMapping("search/page")
	public Page<Sample> search(Sample entity, StatsParams params, Paging page) {
		return service.search(entity, params, page.pageable());
	}

	@Operation(summary = "통계")
	@ResultLogging
	@GetMapping("stats")
	public List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort) {
		return service.stats(entity, params, sort);
	}

	///////////////////////////////////////////////////////////////////////////

	@Operation(summary = "모든 캐시 삭제")
	@GetMapping("cacheEvictAll")
	public void cacheEvictAll() {
		service.cacheEvictAll();
	}

	@Operation(summary = "단일 캐시 삭제")
	@GetMapping("cacheEvictKey")
	public void cacheEvictKey(Sample entity, Sorting sort) {
		service.cacheEvictKey(entity, sort.sortable());
	}
}

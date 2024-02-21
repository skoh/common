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

package org.oh.sample.service;

import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.service.AbstractCrudMapperService;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.QueryUtil;
import org.oh.sample.mapper.SampleMapper;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.oh.sample.repository.impl.SampleDbRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 샘플 매퍼 서비스
 */
@Service
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleMapperService
		extends AbstractCrudMapperService<Sample, Long> {
	private static Map<String, Object> createParams(Sample entity, StatsParams params, Sorting sort) {
		Map<String, Object> parameters = Optional.ofNullable(entity)
				.map(JsonUtil::convertValueMap)
				.orElseGet(HashMap::new);
		params.setParameters(parameters);
		Optional.ofNullable(sort)
				.filter(Sorting::isNotEmpty)
				.ifPresent(a -> parameters.put(QueryUtil.KEY_ORDER_BY, a.toString()));
		return parameters;
	}

	protected final SampleMapperService self;
	protected final SampleMapper mapper;

	protected SampleMapperService(@Lazy SampleMapperService self,
								  SampleMapper mapper) {
		super(self, mapper);
		this.self = self;
		this.mapper = mapper;
	}

	public List<Sample> search(Sample entity, StatsParams params, Sorting sort) {
		Map<String, Object> map = createParams(entity, params, sort);
		return mapper.find(map);
	}

	public Page<Sample> search(Sample entity, StatsParams params, Paging page) {
		Map<String, Object> map = createParams(null, params, null);
		return self.findPageOrEmpty(entity, map, page);
	}

	public List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort) {
		Map<String, Object> map = createParams(entity, params, sort);
		List<Map<String, Object>> result = mapper.stats(map);
		return result.stream()
				.map(a -> a.values().toArray())
				.map(SampleDbRepositoryImpl::createStatsResult)
				.collect(Collectors.toList());
	}
}

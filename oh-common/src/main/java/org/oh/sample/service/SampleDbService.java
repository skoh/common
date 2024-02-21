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

import org.oh.common.annotation.CacheEvictAll;
import org.oh.common.annotation.CacheEvictKey;
import org.oh.common.annotation.CacheGet;
import org.oh.common.annotation.SqlTransaction;
import org.oh.common.config.DataGridConfig;
import org.oh.common.model.data.Sorting;
import org.oh.common.service.AbstractCrudDbService;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.oh.sample.repository.SampleDbRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 샘플 DB 서비스
 */
@Service
@CacheConfig(cacheNames = DataGridConfig.MAP_NAME_CACHE + '.' + Sample.NAME_SPACE)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleDbService
		extends AbstractCrudDbService<Sample, Long> {
	protected final SampleDbRepository repository;

	protected SampleDbService(@Lazy SampleDbService self,
							  SampleDbRepository repository) {
		super(self, repository);
		this.repository = repository;
	}

	public List<Sample> search(Sample entity, StatsParams params, Sorting sort) {
		return repository.search(entity, params, sort);
	}

	public Page<Sample> search(Sample entity, StatsParams params, Pageable pageable) {
		return repository.search(entity, params, pageable);
	}

	public List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort) {
		return repository.stats(entity, params, sort);
	}

	@SqlTransaction
	public void init() {
		repository.init();
	}

	@Override
	@CacheGet
	public List<Sample> findAllOrEmpty(Sample entity, Sorting sort) {
		return super.findAllOrEmpty(entity, sort);
	}

	@CacheEvictKey
	public void cacheEvictKey(Sample entity, Sort sort) {
	}

	@CacheEvictAll
	public void cacheEvictAll() {
	}
}

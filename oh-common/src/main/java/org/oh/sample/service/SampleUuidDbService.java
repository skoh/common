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

import org.oh.common.config.DataGridConfig;
import org.oh.common.service.AbstractCrudDbService;
import org.oh.sample.model.Sample;
import org.oh.sample.model.SampleUuid;
import org.oh.sample.repository.SampleUuidDbRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 샘플 UUID DB 서비스
 */
@Service
@CacheConfig(cacheNames = DataGridConfig.MAP_NAME_CACHE + '.' + Sample.NAME_SPACE)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class SampleUuidDbService
		extends AbstractCrudDbService<SampleUuid, UUID> {
	protected SampleUuidDbService(@Lazy SampleUuidDbService self,
								  SampleUuidDbRepository repository) {
		super(self, repository);
	}
}

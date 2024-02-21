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

package org.oh.common.service.data;

import org.oh.common.config.CommonConfig;
import org.oh.common.model.data.Data;
import org.oh.common.repository.data.DataRepository;
import org.oh.common.service.AbstractCrudService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 데이터 서비스
 */
@Service
@ConditionalOnProperty(value = "enabled", prefix = DataService.PROPERTY_PREFIX, havingValue = "true")
public class DataService
		extends AbstractCrudService<Data, String> {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_API_PREFIX + '.' + Data.NAME_SPACE;

	protected DataService(@Lazy DataService self,
						  DataRepository repository) {
		super(self, repository);
	}
}

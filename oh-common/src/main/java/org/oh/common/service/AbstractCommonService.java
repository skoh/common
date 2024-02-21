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

package org.oh.common.service;

import org.oh.common.config.DataGridConfig;
import org.oh.common.model.AbstractCommon;
import org.oh.common.repository.CrudDbRepository;
import org.springframework.cache.annotation.CacheConfig;

/**
 * 공통 CRUD 서비스
 */
@CacheConfig(cacheNames = DataGridConfig.MAP_NAME_CACHE + ".common")
public abstract class AbstractCommonService<T extends AbstractCommon>
		extends AbstractCrudDbService<T, String> {
	protected AbstractCommonService(AbstractCommonService<T> self,
									CrudDbRepository<T, String> repository) {
		super(self, repository);
	}
}

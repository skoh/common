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

package org.oh.common.repository.data;

import org.oh.common.model.data.DataDb;
import org.oh.common.repository.CrudDbRepository;
import org.oh.common.service.data.DataDbService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 데이터 DB 레파지토리
 */
@ConditionalOnProperty(value = "enabled", prefix = DataDbService.PROPERTY_PREFIX, havingValue = "true")
public interface DataDbRepository
		extends CrudDbRepository<DataDb, String> {
}

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

package org.oh.sample.repository;

import org.oh.common.repository.CrudRepository;
import org.oh.sample.model.SampleUuid;
import org.oh.sample.service.SampleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.UUID;

/**
 * 샘플 UUID 레파지토리
 */
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public interface SampleUuidRepository
		extends CrudRepository<SampleUuid, UUID> {
}

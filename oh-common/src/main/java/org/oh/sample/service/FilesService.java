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

import com.hazelcast.core.HazelcastInstance;
import org.oh.common.service.file.AbstractFilesService;
import org.oh.sample.model.Files;
import org.oh.sample.repository.FilesRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 파일 서비스
 */
@Service
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class FilesService
		extends AbstractFilesService<Files> {
	protected FilesService(@Lazy FilesService self,
						   FilesRepository repository,
						   HazelcastInstance hazelcast) {
		super(self, repository, hazelcast);
	}
}

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

package org.oh.common.service.schedule;

import org.oh.common.config.EntityConfig;
import org.oh.common.model.schedule.ScheduleDb;
import org.oh.common.repository.schedule.ScheduleDbRepository;
import org.oh.common.service.AbstractCrudDbService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * 스케쥴 DB 서비스
 */
@Service
@ConditionalOnProperty(value = "save-to", prefix = ScheduleService.PROPERTY_PREFIX,
		havingValue = EntityConfig.DATA_BASE)
public class ScheduleDbService
		extends AbstractCrudDbService<ScheduleDb, String>
		implements IScheduleService<ScheduleDb> {
	protected final ScheduleDbService self;

	protected ScheduleDbService(@Lazy ScheduleDbService self,
								ScheduleDbRepository repository) {
		super(self, repository);
		this.self = self;
	}

	@Override
	public ScheduleDb insertSchedule(@Valid ScheduleDb entity) {
		return self.insert(entity);
	}

	@Override
	public ScheduleDb updateSchedule(@Valid ScheduleDb entity) {
		return self.update(entity);
	}
}

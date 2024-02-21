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

import org.oh.common.config.CommonConfig;
import org.oh.common.config.EntityConfig;
import org.oh.common.model.schedule.Schedule;
import org.oh.common.repository.schedule.ScheduleRepository;
import org.oh.common.service.AbstractCrudService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 스케쥴 서비스
 */
@Service
@ConditionalOnProperty(value = "save-to", prefix = ScheduleService.PROPERTY_PREFIX,
		havingValue = EntityConfig.DATA_GRID)
public class ScheduleService
		extends AbstractCrudService<Schedule, String>
		implements IScheduleService<Schedule> {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + '.' + Schedule.NAME_SPACE;

	protected final ScheduleService self;

	protected ScheduleService(@Lazy ScheduleService self,
							  ScheduleRepository repository) {
		super(self, repository);
		this.self = self;
	}

	@Override
	public List<Schedule> findAllOrEmpty(Schedule entity) {
		return self.findAllOrEmpty().stream()
				.filter(e -> entity.getType().equals(e.getType()))
				.collect(Collectors.toList());
	}

	@Override
	public Schedule insertSchedule(@Valid Schedule entity) {
		return self.insert(entity);
	}

	@Override
	public Schedule updateSchedule(@Valid Schedule entity) {
		return self.update(entity);
	}
}

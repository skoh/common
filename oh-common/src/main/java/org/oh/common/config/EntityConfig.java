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

package org.oh.common.config;

import org.oh.common.model.data.DataDb;
import org.oh.common.model.schedule.ScheduleDb;
import org.oh.common.service.data.DataDbService;
import org.oh.common.service.schedule.ScheduleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

/**
 * 엔터티 초기화 (설정으로 DB 테이블을 자동 생성)
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### API 사용 여부
 *   api:
 *     ### 데이터(데이터 베이스 사용) CRUD API(DataDbRestController) 사용 여부 (기본값: false)
 *     dataDb.enabled: true
 *
 *   ### 스케쥴 관리
 *   schedule:
 *     ### 스케쥴 이중화 저장소 (DG: 데이터 그리드, DB: 데이터 베이스, NONE: 사용안함, 기본값: NONE)
 *     save-to: DG
 * </pre>
 */
@Configuration
public class EntityConfig {
	/**
	 * 데이터 그리드
	 */
	public static final String DATA_GRID = "DG";

	/**
	 * 데이터 베이스
	 */
	public static final String DATA_BASE = "DB";

	@EntityScan(basePackageClasses = DataDb.class)
	@ConditionalOnProperty(value = "enabled", prefix = DataDbService.PROPERTY_PREFIX, havingValue = "true")
	public static class DataEntityConfig {
	}

	@EntityScan(basePackageClasses = ScheduleDb.class)
	@ConditionalOnProperty(value = "save-to", prefix = ScheduleService.PROPERTY_PREFIX, havingValue = DATA_BASE)
	public static class ScheduleEntityConfig {
	}
}

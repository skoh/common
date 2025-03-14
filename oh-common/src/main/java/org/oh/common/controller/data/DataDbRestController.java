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

package org.oh.common.controller.data;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oh.common.controller.AbstractCrudDbController;
import org.oh.common.model.data.DataDb;
import org.oh.common.service.data.DataDbService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 데이터 DB 컨트롤러
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### API 사용 여부
 *   api:
 *     ### 데이터(데이터 베이스 사용) CRUD API(DataDbRestController) 사용 여부 (기본값: false)
 *     dataDb.enabled: true
 * </pre>
 */
@Tag(name = "데이터 DB", description = "DB 데이터를 메모리에서 관리하는 API")
@RestController
@RequestMapping(DataDbRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = DataDbService.PROPERTY_PREFIX, havingValue = "true")
public class DataDbRestController
		extends AbstractCrudDbController<DataDb, String> {
	public static final String PATH = VERSION_1 + "/" + DataDb.NAME_SPACE;

	protected DataDbRestController(@Qualifier("dataDbService") DataDbService service) {
		super(service);
	}
}

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
import org.oh.common.controller.AbstractCrudKvController;
import org.oh.common.model.data.Data;
import org.oh.common.service.data.DataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 데이터 컨트롤러
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### API 사용 여부
 *   api:
 *     ### 데이터(데이터 그리드 사용) CRUD API(DataRestController) 사용 여부 (기본값: false)
 *     data.enabled: true
 * </pre>
 */
@Tag(name = "데이터", description = "데이터를 메모리에서 관리하는 API")
@RestController
@RequestMapping(DataRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = DataService.PROPERTY_PREFIX, havingValue = "true")
public class DataRestController
		extends AbstractCrudKvController<Data, String> {
	public static final String PATH = VERSION_1 + "/" + Data.NAME_SPACE;

	protected DataRestController(@Qualifier("dataService") DataService service) {
		super(service);
	}
}

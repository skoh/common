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

package org.oh.common.controller;

import org.oh.common.model.Model;
import org.oh.common.service.AbstractCrudMapperService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 기본 CRUD 매퍼 컨트롤러
 */
public abstract class AbstractCrudMapperController<T extends Model<ID>, ID>
		extends AbstractCrudDbController<T, ID> {
	protected final AbstractCrudMapperService<T, ID> service;

	protected AbstractCrudMapperController(AbstractCrudMapperService<T, ID> service) {
		super(service);
		this.service = service;
	}

	/**
	 * 해당 모델 클래스로 CRUD 매퍼 쿼리 파일을 다운로드
	 *
	 * @return HTTP 응답 정보
	 * @throws Exception
	 */
	@Operation(summary = "매퍼 다운로드")
	@GetMapping("down")
	public ResponseEntity<Resource> down() throws Exception {
		byte[] bytes = service.mapper().getBytes();
		String fileName = service.getTypeName() + "Mapper.xml";
		return DefaultController.getResponseEntity(new ByteArrayResource(bytes),
				MediaType.APPLICATION_OCTET_STREAM, fileName, bytes.length);
	}
}

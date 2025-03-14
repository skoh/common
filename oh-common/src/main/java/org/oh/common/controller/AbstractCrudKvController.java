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

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.oh.common.model.CommonModel;
import org.oh.common.model.Model;
import org.oh.common.model.data.Cvs;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.service.CrudService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.List;

/**
 * 기본 CRUD 키값 컨트롤러 (데이터 그리드 사용)
 */
public abstract class AbstractCrudKvController<T extends Model<ID>, ID extends Serializable>
		extends AbstractCrudController<T, ID> {
	protected final CrudService<T, ID> service;

	protected AbstractCrudKvController(CrudService<T, ID> service) {
		super(service);
		this.service = service;
	}

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param page 페이징 조건
	 * @return 페이징 목록
	 */
	@Operation(summary = "페이지별 항목 조회")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping("page")
	public Page<T> findPage(Paging page) {
		return service.findPageOrEmpty(page);
	}

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param lsize 조회 건수
	 * @param sort  정렬 조건
	 * @return 객체 목록
	 */
	@Operation(summary = "TopN 항목 조회")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping("limit")
	public List<T> findAll(@Parameter(description = "페이지 갯수", example = "10")
						   @RequestParam int lsize,
						   Sorting sort) {
		return service.findAllOrEmpty(lsize, sort);
	}

	/**
	 * 전체 건수를 조회
	 *
	 * @return 객체 건수
	 */
	@Operation(summary = "항목 건수 조회")
//	@ResultLogging(result = true)
	@GetMapping("count")
	public long count() {
		return service.count();
	}

	/**
	 * 해당 조건으로 검색 목록을 CVS 파일로 다운로드
	 *
	 * @param cvs         CVS 조건
	 * @param sort        정렬 조건
	 * @param charsetName 캐릿터셋명
	 * @return CVS 파일
	 */
	@ResponseBody
	@Operation(summary = "CVS 다운로드")
//	@ResultLogging
	@GetMapping("cvs")
	public ResponseEntity<Resource> cvs(Cvs cvs, Sorting sort,
										@Parameter(description = "캐릿터셋명", example = "UTF-8")
										@RequestParam(required = false) String charsetName) {
		List<T> entities = service.findAllOrEmpty(sort);
		return cvs(cvs, entities, charsetName);
	}
}

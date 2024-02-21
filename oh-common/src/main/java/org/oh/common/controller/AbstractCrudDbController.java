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

import org.oh.common.annotation.ResultLogging;
import org.oh.common.model.CommonModel;
import org.oh.common.model.Model;
import org.oh.common.model.data.Cvs;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.service.CrudDbService;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 기본 CRUD DB 컨트롤러 (데이터 베이스 사용)
 */
public abstract class AbstractCrudDbController<T extends Model<ID>, ID>
		extends AbstractCrudController<T, ID> {
	protected final CrudDbService<T, ID> service;

	protected AbstractCrudDbController(CrudDbService<T, ID> service) {
		super(service);
		this.service = service;
	}

	/**
	 * 해당 객체 목록을 삭제
	 *
	 * @param entities 객체 목록
	 */
	@Override
	@ResultLogging(json = true)
	public void deleteAll(@RequestBody List<T> entities) {
		service.deleteAll(entities);
	}

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	@Operation(summary = "다중 항목 조회")
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping
	public List<T> findAll(T entity, Sorting sort) {
		entity.convert();
		entity.encrypt();
		List<T> entities = service.findAllOrEmpty(entity, sort);
		entities.forEach(e -> {
			e.sum();
			e.decrypt();
		});
		return entities;
	}

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param page   페이징 조건
	 * @return 페이징 목록
	 */
	@Operation(summary = "페이지별 항목 조회")
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping("page")
	public Page<T> findPage(T entity, Paging page) {
		entity.convert();
		entity.encrypt();
		Page<T> entities = service.findPageOrEmpty(entity, page);
		entities.getContent()
				.forEach(e -> {
					e.sum();
					e.decrypt();
				});
		return entities;
	}

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param lsize  조회 건수
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	@Operation(summary = "TopN 항목 조회")
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping("limit")
	public List<T> findAll(T entity,
						   @Parameter(description = "페이지 갯수", example = "10")
						   @RequestParam int lsize,
						   Sorting sort) {
		entity.convert();
		entity.encrypt();
		List<T> entities = service.findAllOrEmpty(entity, lsize, sort);
		entities.forEach(e -> {
			e.sum();
			e.decrypt();
		});
		return entities;
	}

	/**
	 * 검색 건수를 조회
	 *
	 * @param entity 검색 조건
	 * @return 검색 건수
	 */
	@Operation(summary = "항목 건수 조회")
	@ResultLogging(result = true, json = true)
	@GetMapping("count")
	public long count(T entity) {
		entity.convert();
		entity.encrypt();
		return service.count(entity);
	}

	/**
	 * 해당 조건으로 검색 목록을 CVS 파일로 다운로드
	 *
	 * @param cvs         CVS 조건
	 * @param entity      검색 조건
	 * @param sort        정렬 조건
	 * @param charsetName 캐릿터셋명
	 * @return CVS 파일
	 */
	@ResponseBody
	@Operation(summary = "CVS 다운로드")
	@ResultLogging
	@GetMapping("cvs")
	public ResponseEntity<Resource> cvs(Cvs cvs, T entity, Sorting sort,
										@Parameter(description = "캐릿터셋명", example = "UTF-8")
										@RequestParam(required = false) String charsetName) {
		List<T> entities = service.findAllOrEmpty(entity, sort);
		return cvs(cvs, entities, charsetName);
	}

	/**
	 * 해당 조건으로 데이터의 존재 여부
	 *
	 * @param entity 검색 조건
	 * @return 존재 여부
	 */
	@Operation(summary = "항목 존재 여부 조회")
	@ResultLogging(result = true, json = true)
	@GetMapping("exists")
	public boolean exists(T entity) {
		entity.convert();
		entity.encrypt();
		return service.exists(entity);
	}

	/**
	 * 검색 목록을 삭제
	 *
	 * @param entity 검색 조건
	 */
	@Operation(summary = "다중 항목 삭제")
	@ResultLogging(json = true)
	@DeleteMapping
	public void delete(@RequestBody T entity) {
		service.delete(entity);
	}
}

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.model.CommonModel;
import org.oh.common.model.Model;
import org.oh.common.model.data.Cvs;
import org.oh.common.model.data.Sorting;
import org.oh.common.model.validate.ValidList;
import org.oh.common.model.validate.ValidationGroup;
import org.oh.common.service.CrudService;
import org.oh.common.util.JsonUtil;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * 기본 CRUD 컨트롤러
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Validated(ValidationGroup.Api.class)
public abstract class AbstractCrudController<T extends Model<ID>, ID>
		implements DefaultController {
	protected final CrudService<T, ID> service;

	/**
	 * 정렬 정보를 바인딩하기 위해 필요
	 *
	 * @param binder 웹 데이터 바인더
	 * @see Sorting
	 */
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		// 이게 없으면 Sorting 의 desc가 동작하지 않음
		binder.registerCustomEditor(String[].class, new StringArrayPropertyEditor(null)); //NOSONAR
	}

	/**
	 * 아이디에 해당하는 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param id 아이디
	 * @return 객체
	 */
	@Operation(summary = "단일 항목 조회")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@GetMapping("{id}")
	public T findById(@Parameter(description = "아이디")
					  @PathVariable ID id) {
		T result = service.findById(id);
		result.sum();
		result.decrypt();
		return result;
	}

	/**
	 * 아이디에 해당하는 데이터의 존재 여부를 확인
	 *
	 * @param id 아이디
	 * @return 존재 여부
	 */
	@Operation(summary = "항목 존재 여부 조회")
//	@ResultLogging(result = true)
	@GetMapping("exists/{id}")
	public boolean exists(@Parameter(description = "아이디")
						  @PathVariable ID id) {
		return service.exists(id);
	}

	/**
	 * 해당 객체를 추가
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	@Operation(summary = "단일 항목 추가")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@PostMapping
	public T insert(@Valid @RequestBody T entity) {
		return service.insert(entity);
	}

	/**
	 * 해당 객체 목록을 추가
	 *
	 * @param entities 객체 목록
	 * @return 저장 목록
	 */
	@Operation(summary = "다중 항목들 추가")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@PostMapping("all")
	public Collection<T> insert(@Valid @RequestBody ValidList<T> entities) {
		return service.insert(entities.getList());
	}

	/**
	 * 아이디에 해당하는 객체를 병합 (없으면 생성, 있으면 수정)
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	@Operation(summary = "단일 항목 병합")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@PostMapping("merge")
	public T merge(@RequestBody T entity) {
		return service.merge(entity);
	}

	/**
	 * 아이디에 해당하는 객체를 수정
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	@Operation(summary = "단일 항목 수정")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@PutMapping
	public T update(@RequestBody T entity) {
		return service.update(entity);
	}

	/**
	 * 해당 객체 목록을 수정
	 *
	 * @param entities 객체 목록
	 * @return 저장 목록
	 */
	@Operation(summary = "다중 항목들 수정")
//	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	@JsonView(CommonModel.Default.class)
	@PutMapping("all")
	public List<T> update(@RequestBody List<T> entities) {
		return service.update(entities);
	}

	/**
	 * 아이디에 해당하는 1건을 삭제 (없으면 예외 발생)
	 *
	 * @param id 아이디
	 */
	@Operation(summary = "단일 항목 삭제")
//	@ResultLogging
	@DeleteMapping("{id}")
	public void deleteById(@Parameter(description = "아이디")
						   @PathVariable ID id) {
		service.deleteById(id);
	}

	/**
	 * 해당 객체 목록을 삭제
	 *
	 * @param entities 객체 목록
	 */
	@Operation(summary = "다중 항목들 삭제")
//	@ResultLogging(json = true)
	@DeleteMapping("all")
	public void deleteAll(@RequestBody List<T> entities) {
		service.deleteAll(entities);
	}

	/**
	 * 해당 조건과 검색 목록으로 CVS 파일을 다운로드
	 *
	 * @param cvs         CVS 조건
	 * @param entities    객체 목록
	 * @param charsetName 문자 인코딩
	 * @return HTTP 응답 정보
	 */
	protected ResponseEntity<Resource> cvs(Cvs cvs, List<T> entities, String charsetName) {
		String json = JsonUtil.toString(entities, CommonModel.Csv.class);
		json = JsonUtil.flatten(json);

		String csv = StringUtils.isEmpty(cvs.getFieldNames()) ? JsonUtil.toCsv(json, ",") :
				JsonUtil.toCsv(json, ",", cvs.getFieldNames(), cvs.getHeader());
		byte[] bytes = charsetName == null ? csv.getBytes() : csv.getBytes(Charset.forName(charsetName));
		String fileName = service.getTypeName().toLowerCase() + ".csv";
		return DefaultController.getResponseEntity(new ByteArrayResource(bytes),
				MediaType.APPLICATION_OCTET_STREAM, fileName, bytes.length);
	}
}

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
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.CommonModel;
import org.oh.common.model.data.Cvs;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Role;
import org.oh.common.service.CrudDbService;
import org.oh.common.util.SpringUtil;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

/**
 * 기본 사용자 컨트롤러
 */
public abstract class AbstractUserController<T extends AbstractUser>
		extends AbstractCrudDbController<T, String> {
	public static final String PATH = "/v1/" + AbstractUser.NAME_SPACE;

	protected AbstractUserController(CrudDbService<T, String> service) {
		super(service);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@Override
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	public Page<T> findPage(T entity, Paging page) {
		return super.findPage(entity, page);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@Override
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	public List<T> findAll(T entity,
						   @Parameter(description = "페이지 갯수", example = "10")
						   @RequestParam int lsize,
						   Sorting sort) {
		return super.findAll(entity, lsize, sort);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@Override
	@ResultLogging(result = true, json = true)
	public long count(T entity) {
		return super.count(entity);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@Override
	@ResultLogging
	public ResponseEntity<Resource> cvs(Cvs cvs, T entity, Sorting sort,
										@Parameter(description = "캐릿터셋명", example = "UTF-8")
										@RequestParam(required = false) String charsetName) {
		return super.cvs(cvs, entity, sort, charsetName);
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@Override
	@ResultLogging(result = true, json = true)
	public boolean exists(T entity) {
		return super.exists(entity);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	public T findById(@Parameter(description = "아이디")
					  @PathVariable String id) {
		SpringUtil.getLoginUserOrEmpty()
				.ifPresent(a -> {
					if (Role.ROLE_USER.name().equals(a.getRoles())) {
						if (!a.getId().equals(id)) {
							throw new CommonException(CommonError.COM_FORBIDDEN,
									String.format("(일반 사용자는 다른 사용자의 정보를 조회할 수 없습니다." +
											" loginUser: %s userId: %s)", a, id));
						}
					}
				});
		return super.findById(id);
	}

	@Override
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	public T update(@RequestBody T entity) {
		throwForbidden(Collections.singletonList(entity));
		return super.update(entity);
	}

	@Override
	@ResultLogging(result = true, jsonView = CommonModel.Default.class)
	public List<T> update(@RequestBody List<T> entities) {
		throwForbidden(entities);
		return super.update(entities);
	}

	/**
	 * 접근 권한 예외를 체크
	 *
	 * @param entities 객체 목록
	 */
	protected void throwForbidden(@RequestBody List<T> entities) {
		SpringUtil.getLoginUserOrEmpty()
				.ifPresent(a -> {
					for (T entity : entities) {
						if (Role.ROLE_USER.name().equals(a.getRoles())) {
							if (!a.getId().equals(entity.getId())
									|| (a.getId().equals(entity.getId())
									&& StringUtils.isNotEmpty(entity.getRoles()))) {
								throw new CommonException(CommonError.COM_FORBIDDEN,
										String.format("(일반 사용자는 다른 사용자의 정보나 자신의 권한을 수정할 수 없습니다." +
												" loginUser: %s user: %s)", a, entity));
							}
						}
					}
				});
	}
}

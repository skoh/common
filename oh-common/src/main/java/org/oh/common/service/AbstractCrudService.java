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

package org.oh.common.service;

import org.oh.common.config.LoggingConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.AbstractUserCommon;
import org.oh.common.model.Model;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.model.validate.ValidationGroup;
import org.oh.common.repository.CommonCrudRepository;
import org.oh.common.util.CommonUtil;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.SpringUtil;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Transient;
import javax.validation.Valid;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 기본 CRUD 서비스
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Validated({ValidationGroup.Data.class})
public abstract class AbstractCrudService<T extends Model<ID>, ID>
		implements CrudService<T, ID> {
	/**
	 * 페이지 수를 반환
	 *
	 * @param totalSize 전체 항목 수
	 * @param rowsSize  페이지 당 항목 수
	 * @return 페이지 수
	 */
	public static int getPageSize(long totalSize, int rowsSize) {
		double size = (double) totalSize / rowsSize;
		return (int) Math.ceil(size);
	}

	/**
	 * 해당 객체 목록을 페이징 조건으로 페이징 목록으로 변환
	 *
	 * @param content 객체 목록
	 * @param page    페이징 조건
	 * @return 페이징 목록
	 */
	public static <T> Page<T> getPage(List<T> content, Paging page) {
		List<T> list = Lists.partition(content, page.getPsize())
				.get(page.getPage() - 1);
		return getPage(list, page, content.size());
	}

	/**
	 * 해당 객체 목록을 페이징 조건으로 페이징 목록으로 변환
	 *
	 * @param content 객체 목록
	 * @param page    페이징 조건
	 * @param total   전체 건수
	 * @return 페이징 목록
	 */
	public static <T> Page<T> getPage(List<T> content, Paging page, long total) {
		return PageableExecutionUtils.getPage(content, page.pageable(), () -> total);
	}

	/**
	 * 해당 객체 목록을 키 목록을 기준으로 값 목록을 그룹핑
	 *
	 * @param target         대상 목록
	 * @param keys           키 목록
	 * @param fieldPrefix    값 필드명
	 * @param fieldDelimiter 값 구분자
	 * @param values         값 목록
	 * @return 그룹핑된 객체 목록
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<Map<String, Object>> groupingBy(List<Map<String, Object>> target,
													   Function<Map<String, Object>, List<Object>> keys,
													   String fieldPrefix, String fieldDelimiter, String... values) {
		Map<List<Object>, List<Object>> group = target.stream()
//		Map<Object, Long> result = list.stream()
				.collect(Collectors.groupingBy(keys,
						Collectors.toList()));
//						Collectors.mapping(keys, Collectors.toList())));
//						Collectors.counting()));
		log.debug("group: {}", JsonUtil.toPrettyString(group));
		log.debug(LoggingConfig.ONE_LINE_100);

//		List<Object> result = group.values().stream()
//				.flatMap(List::stream)
//				.collect(Collectors.toList());
		List<Map<String, Object>> result = new ArrayList<>();
		for (Map.Entry<List<Object>, List<Object>> me : group.entrySet()) {
			List<Map<String, Object>> list = (List) me.getValue();
			Map<String, Object> map = new LinkedHashMap<>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> listMap = list.get(i);
				map.putAll(listMap);
				Arrays.stream(values)
						.forEach(map::remove);
				map.put(fieldPrefix + (i + 1),
						Arrays.stream(values)
								.map(e -> listMap.get(e).toString())
								.collect(Collectors.joining(fieldDelimiter)));
			}
			result.add(map);
		}
		return result;
	}

	/**
	 * 해당 객체 목록에 아이디 목록만 추출
	 *
	 * @param entities 객체 목록
	 * @param <T>
	 * @param <ID>
	 * @return 아이디 목록
	 */
	public static <T extends Model<ID>, ID> List<ID> toIds(Collection<T> entities) {
		return entities.stream()
				.map(T::id)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	/**
	 * 해당 객체를 대상 서비스로 저장
	 *
	 * @param entity  대상 객체
	 * @param service 대상 서비스
	 * @return 저장된 객체
	 */
	public static <T extends Model<ID>, ID> T insert(T entity, CrudService<T, ID> service) {
		return Optional.ofNullable(entity)
				.map(service::insert)
				.orElse(entity);
	}

	/**
	 * 해당 객체 목록을 대상 서비스로 저장
	 *
	 * @param entities 대상 객체 목록
	 * @param service  대상 서비스
	 * @return 저장된 객체 목록
	 */
	public static <T extends Model<ID>, ID> Collection<T> insert(Collection<T> entities, CrudService<T, ID> service) {
		return Optional.ofNullable(entities)
				.map(service::insert)
				.orElse(entities);
	}

	/**
	 * 해당 객체를 대상 서비스로 삭제
	 *
	 * @param entity  대상 객체
	 * @param service 대상 서비스
	 */
	public static <T extends Model<ID>, ID> void delete(T entity, CrudService<T, ID> service) {
		Optional.ofNullable(entity)
				.map(T::id)
				.ifPresent(service::deleteById);
	}

	/**
	 * 해당 객체 목록을 대상 서비스로 삭제
	 *
	 * @param entities 대상 객체 목록
	 * @param service  대상 서비스
	 */
	public static <T extends Model<ID>, ID> void delete(Collection<T> entities, CrudService<T, ID> service) {
		Optional.ofNullable(entities)
				.ifPresent(service::deleteAll);
	}

	///////////////////////////////////////////////////////////////////////////

	protected final AbstractCrudService<T, ID> self;
	protected final CommonCrudRepository<T, ID> repository;

	@Override
	public T findById(ID id) {
		return self.findByIdOrEmpty(id)
				.orElseThrow(throwNotFound(id));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findByIdOrEmpty(ID id) {
		return repository.findById(id)
				.map(a -> (T) a.sort());
	}

	@Override
	public List<T> findAll() {
		List<T> result = self.findAllOrEmpty();
		return throwNotFound(result, null);
	}

	@Override
	public List<T> findAllOrEmpty() {
		return self.findAllOrEmpty(new Sorting());
	}

	@Override
	public List<T> findAll(Sorting sort) {
		List<T> result = self.findAllOrEmpty(sort);
		return throwNotFound(result, null);
	}

	@Override
	public List<T> findAllOrEmpty(Sorting sort) {
		List<T> result = (List<T>) repository.findAll(sort.sortable());
		result.forEach(Model::sort);
		return result;
	}

	@Override
	public List<T> findAll(int lsize, Sorting sort) {
		List<T> result = self.findAllOrEmpty(lsize, sort);
		return throwNotFound(result, null);
	}

	@Override
	public List<T> findAllOrEmpty(int lsize, Sorting sort) {
//		return findOrEmpty(PageRequest.of(0, lsize, sort)).getContent();
		Paging page = Paging.builder()
				.page(1)
				.psize(lsize)
				.sort(sort.getSort())
				.build();
		return self.findPageOrEmpty(page).getContent();
	}

	@Override
	public Page<T> findPage(Paging page) {
		Page<T> result = self.findPageOrEmpty(page);
		return throwNotFound(result, null);
	}

	@Override
	public Page<T> findPageOrEmpty(Paging page) {
		Page<T> result = repository.findAll(page.pageable());
		result.getContent()
				.forEach(Model::sort);
		return result;
	}

	@Override
	public long count() {
		return repository.count();
	}

	@Override
	public boolean exists(ID id) {
		return repository.existsById(id);
	}

	@Override
	public T insert(@Valid T entity) {
		throwExists(entity);

		return self.insertInternal(entity);
	}

	@Override
	public Collection<T> insert(@Valid Collection<T> entities) {
		throwExists(entities);

		return self.insertInternal(entities);
	}

	@Override
	public Optional<T> insertOrIgnore(@Valid T entity) {
		try {
			throwExists(entity);
		} catch (CommonException e) {
			if (e.getError() == CommonError.COM_ALREADY_EXISTS) {
				return Optional.empty();
			} else {
				throw e;
			}
		}
		return Optional.of(self.insertInternal(entity));
	}

	@Override
	public Optional<Collection<T>> insertOrIgnore(@Valid Collection<T> entities) {
		try {
			throwExists(entities);
		} catch (CommonException e) {
			if (e.getError() == CommonError.COM_ALREADY_EXISTS) {
				return Optional.empty();
			} else {
				throw e;
			}
		}
		return Optional.of(self.insertInternal(entities));
	}

	@Override
	public T merge(T entity) {
		if (entity.id() != null) {
//				&& entity.getValue("regDate").isPresent()) {
			try {
				return self.update(entity);
			} catch (CommonException e) {
				if (e.getError() == CommonError.COM_NOT_FOUND) {
					return self.saveRep(entity);
				} else {
					throw e;
				}
			}
		} else {
			return self.insertInternal(entity);
		}
	}

	@Override
	public List<T> update(Collection<T> entities) {
		return entities.stream()
				.map(self::update)
				.collect(Collectors.toList());
	}

	@Override
	public T update(T entity) {
		throwIdNull(entity);

		preSave(entity);
		T des = self.findById(entity.id());
//		T des = repository.findById(entity.id())
//				.orElseThrow(throwNotFound(entity.id()));

		return updateInternal(entity, des);
	}

	@Override
	public void deleteById(ID id) {
		repository.deleteById(id);
	}

	@Override
	public void deleteByIdOrIgnore(ID id) {
		try {
			self.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			log.debug(ExceptionUtil.getMessageAndType(e));
		}
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		repository.deleteAll(entities);
	}

	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	@Override
	public String getTypeName() {
		return getType().map(Class::getSimpleName)
				.orElse("None");
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 객체를 추가
	 *
	 * @param entity 대상 객체
	 * @return 저장된 객체
	 */
	protected T insertInternal(@Valid T entity) {
		preInsert(entity);
		preSave(entity);

		return self.saveRep(entity);
	}

	/**
	 * 해당 객체 목록을 추가
	 *
	 * @param entities 객체 목록
	 * @return 저장 목록
	 */
	protected Collection<T> insertInternal(@Valid Collection<T> entities) {
		preInsert(entities);
		preSave(entities);

		return (List<T>) repository.saveAll(entities);
	}

	/**
	 * 대상 객체의 수정 항목을 수정
	 *
	 * @param entity 수정 항목
	 * @param des    대상 객체
	 * @return 저장 객체
	 */
	@SuppressWarnings("unchecked")
	protected T updateInternal(T entity, T des) {
		FieldUtils.getAllFieldsList(entity.getClass()).stream()
				.filter(e -> !Modifier.isStatic(e.getModifiers()))
				.filter(e -> !CommonUtil.existAnnotation(e, Transient.class))
				.forEach(e -> {
					e.setAccessible(true);
					try {
//						Optional.ofNullable(PropertyUtils.getProperty(entity, e.getName()))
						Optional.ofNullable(e.get(entity))
								.ifPresent(a -> {
									try {
//										PropertyUtils.setProperty(des, e.getName(), a);
										e.set(des, a);
									} catch (ReflectiveOperationException ex) {
										throw new CommonException(ex);
									}
								});
					} catch (ReflectiveOperationException ex) {
						throw new CommonException(ex);
					}
				});

		try {
			Optional.ofNullable(PropertyUtils.getProperty(entity, "nulls"))
					.ifPresent(a -> ((List<String>) a).forEach(e -> {
						try {
							PropertyUtils.setProperty(des, e, null);
						} catch (ReflectiveOperationException ex) {
							throw new CommonException(ex);
						}
					}));
		} catch (ReflectiveOperationException e) {
			throw new CommonException(e);
		}

		return self.saveRep(des);
	}

	/**
	 * 수퍼(제네릭) 타입을 반환
	 *
	 * @return 수퍼 타입
	 */
	protected Optional<Class<?>> getType() {
		return CommonUtil.getSuperClassParameterizedType(getClass())
				.map(a -> (Class<?>) a.getActualTypeArguments()[0]);
	}

	/**
	 * 해당 객체 목록을 추가 전 처리 (필요시 재정의)
	 *
	 * @param entities 객체 목록
	 */
	protected void preInsert(Collection<T> entities) {
		entities.forEach(this::preInsert);
	}

	/**
	 * 해당 객체를 추가 전 처리 (필요시 재정의)
	 *
	 * @param entity 대상 객체
	 */
	protected void preInsert(T entity) {
		entity.setInsertValue();
	}

	/**
	 * 해당 객체 목록을 추가나 수정 전 처리 (필요시 재정의)
	 *
	 * @param entities 객체 목록
	 */
	protected void preSave(Collection<T> entities) {
		entities.forEach(this::preSave);
	}

	/**
	 * 해당 객체를 추가나 수정 전 처리 (필요시 재정의)
	 *
	 * @param entity 대상 객체
	 */
	protected void preSave(T entity) {
		entity.setSaveValue();
		entity.encrypt();
		setUser(Collections.singletonList(entity));
	}

	/**
	 * 해당 객체를 최종 저장 (필요시 재정의)
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	protected T saveRep(T entity) {
		return repository.save(entity);
	}

	private void setUser(Collection<T> entities) {
		SpringUtil.getLoginUserOrEmpty()
				.ifPresent(u ->
						entities.stream()
								.filter(AbstractUserCommon.class::isInstance)
								.map(AbstractUserCommon.class::cast)
								.forEach(e -> e.setUser(u))
				);
	}

	///////////////////////////////////////////////////////////////////////////

	protected T throwNotFound(T result, T entity) {
		return Optional.ofNullable(result)
				.orElseThrow(throwNotFound(entity));
	}

	protected List<T> throwNotFound(List<T> result, T entity) {
		return (List<T>) throwNotFoundInternal(result, entity);
	}

	protected Page<T> throwNotFound(Page<T> result, T entity) {
		return (Page<T>) throwNotFoundInternal(result, entity);
	}

	protected Iterable<T> throwNotFoundInternal(Iterable<T> result, T entity) {
		return Optional.ofNullable(result)
				.filter(r -> r.iterator().hasNext())
				.orElseThrow(throwNotFound(entity));
	}

	protected Supplier<CommonException> throwNotFound(T entity) {
		String params = Optional.ofNullable(entity)
				.map(e -> "[\"" + getTypeName() + "\"," + JsonUtil.GSON.toJson(entity) + ']')
				.orElse(null);
		return () -> new CommonException(CommonError.COM_NOT_FOUND, params);
	}

	protected Supplier<CommonException> throwNotFound(ID id) {
		return () -> new CommonException(CommonError.COM_NOT_FOUND,
				"[\"" + getTypeName() + "\",{\"id\":" + id + "}]");
	}

	protected void throwEmpty(T entity) {
		if (JsonUtil.OBJECT_MAPPER.createObjectNode().equals(JsonUtil.readTree(entity))) {
			throw new CommonException(CommonError.COM_EMPTY_INPUT_DATA,
					"[\"" + getTypeName() + "\"," + JsonUtil.GSON.toJson(entity) + ']');
		}
	}

	protected void throwIdNull(T entity) {
		if (entity.id() == null) {
			throw new CommonException(CommonError.COM_ID_IS_NULL, "[\"" + getTypeName() + "\"," + JsonUtil.GSON.toJson(entity));
		}
	}

	protected void throwExists(T entity) {
		throwExists(Collections.singletonList(entity));
	}

	protected void throwExists(Collection<T> entities) {
		List<ID> ids = toIds(entities);
		if (ids.size() == 0) {
			return;
		}

		Iterable<T> entitiesTemp = repository.findAllById(ids);
		int size = Iterators.size(entitiesTemp.iterator());
		log.debug("entities.size: {} size: {}", entities.size(), size);
		if (entities.size() == size) {
			throw new CommonException(CommonError.COM_ALREADY_EXISTS,
					"[\"" + getTypeName() + "\"," + JsonUtil.GSON.toJson(ids) + ']');
		}
	}
}

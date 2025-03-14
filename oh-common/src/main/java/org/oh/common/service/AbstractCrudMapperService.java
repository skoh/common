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

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.mapper.CrudMapper;
import org.oh.common.model.Model;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.util.FileUtil;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.QueryUtil;
import org.oh.common.util.SpringUtil;
import org.oh.common.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.MustacheResourceTemplateLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 공통 CRUD 매퍼 서비스
 */
@Slf4j
public abstract class AbstractCrudMapperService<T extends Model<ID>, ID>
		extends AbstractCrudDbService<T, ID> {
	public static final String MAPPER = "mapper";

	@Autowired
	private MustacheResourceTemplateLoader templateLoader;
	@Autowired
	private Mustache.Compiler compiler;

	/**
	 * 해당 조건과 파라미터를 병합
	 *
	 * @param entity 검색 조건
	 * @param params 검색 파라미터
	 * @param page   페이징 조건
	 * @return 검색 파라미터
	 */
	public static Map<String, Object> createParams(Object entity, Map<String, Object> params, Paging page) {
		Objects.requireNonNull(page, "Page must not null");

		Map<String, Object> map = Optional.ofNullable(entity)
				.map(JsonUtil::convertValueMap)
				.orElseGet(HashMap::new);

		Optional.ofNullable(params)
				.ifPresent(map::putAll);

		Map<String, Object> pagingMap = JsonUtil.convertValueMap(page);
		map.putAll(pagingMap);
		map.put("offset", page.getPsize() * (page.getPage() - 1));

		String orderBy = Sorting.toString(page.pageable().getSort());
		if (StringUtils.isNotEmpty(orderBy)) {
			map.put(QueryUtil.KEY_ORDER_BY, orderBy);
		}

		return map;
	}

	/**
	 * 템플릿 파일을 읽는다.
	 *
	 * @param name 파일명
	 * @return 스트림 리더
	 */
	public static Reader getTemplate(String name) {
		return getTemplate(null, name);
	}

	/**
	 * 해당 경로의 템플릿 파일을 읽는다.
	 *
	 * @param path 디렉토리 경로
	 * @param name 파일명
	 * @return 스트림 리더
	 */
	public static Reader getTemplate(String path, String name) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		path = Optional.ofNullable(path)
				.map(a -> a + "/")
				.orElse("");
		String filePath = "classpath:" + WebUtil.getTemplatesName() + path + name + ".mustache";
		try {
			return new InputStreamReader(resourceLoader.getResource(filePath).getInputStream(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_READ_TEMPLETE_ERROR + " file: " + filePath, e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	protected final AbstractCrudMapperService<T, ID> self;
	protected final CrudMapper<T, ID> mapper;

	protected AbstractCrudMapperService(AbstractCrudMapperService<T, ID> self,
										CrudMapper<T, ID> mapper) {
		super(self, null);
		this.self = self;
		this.mapper = mapper;
	}

	@Override
	public Optional<T> findByIdOrEmpty(ID id) {
		Map<String, Object> map = ImmutableMap.of("id", id);
		return Optional.ofNullable(mapper.find(map))
				.filter(a -> !a.isEmpty())
				.map(a -> a.get(0));
	}

	@Override
	public List<T> findAllOrEmpty(Sorting sort) {
		return self.findAllOrEmpty(null, sort);
	}

	@Override
	public Page<T> findPageOrEmpty(Paging page) {
		return self.findPageOrEmpty(null, page);
	}

	@Override
	public long count() {
		return self.count(null);
	}

	@Override
	public boolean exists(ID id) {
		return self.findByIdOrEmpty(id).isPresent();
	}

	@Override
	public T insert(T entity) {
		preInsert(entity);
		preSave(entity);

		mapper.insert(entity);
		return entity;
	}

	@Override
	public Collection<T> insert(@Valid Collection<T> entities) {
		return entities.stream()
				.map(self::insert)
				.collect(Collectors.toList());
	}

	@Override
	public T merge(T entity) {
		if (entity.id() == null) {
			return self.insert(entity);
		} else {
			return self.update(entity);
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
		preSave(entity);

		Map<String, Object> map = JsonUtil.convertValueMap(entity);
		mapper.update(map);
		return entity;
	}

	@Override
	public void deleteById(ID id) {
		Map<String, Object> map = ImmutableMap.of("id", id);
		mapper.delete(map);
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		entities.forEach(self::delete);
	}

	@Override
	public void deleteAll() {
		self.delete((T) null);
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public Optional<T> findOrEmpty(T entity) {
		return Optional.ofNullable(self.findAllOrEmpty(entity, null))
				.filter(a -> !a.isEmpty())
				.map(a -> a.get(0));
	}

	@Override
	public List<T> findAllOrEmpty(T entity, Sorting sort) {
		Map<String, Object> map = Optional.ofNullable(entity)
				.map(JsonUtil::convertValueMap)
				.orElseGet(HashMap::new);

		Optional.ofNullable(sort)
				.filter(Sorting::isNotEmpty)
				.ifPresent(a -> map.put(QueryUtil.KEY_ORDER_BY, a.toString()));

		return mapper.find(map);
	}

	@Override
	public Page<T> findPageOrEmpty(T entity, Paging page) {
		return self.findPageOrEmpty(entity, null, page);
	}

	@Override
	public long count(T entity) {
		Map<String, Object> map = Optional.ofNullable(entity)
				.map(JsonUtil::convertValueMap)
				.orElseGet(HashMap::new);
		return mapper.count(map);
	}

	@Override
	public boolean exists(T entity) {
		return Optional.ofNullable(self.findAllOrEmpty(entity))
				.filter(a -> !a.isEmpty())
				.isPresent();
	}

	@Override
	public void delete(T entity) {
		Map<String, Object> map = Optional.ofNullable(entity)
				.map(JsonUtil::convertValueMap)
				.orElseGet(HashMap::new);
		mapper.delete(map);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 모델 클래스로 CRUD 매퍼 쿼리를 자동 생성
	 *
	 * @return 매퍼 쿼리 문자열
	 * @throws Exception
	 */
	public String mapper() throws Exception {
		Class<?> clazz = getType().get();
		String mapperName = clazz.getSimpleName() + "Mapper";
		String namespace = StringUtils.substringBeforeLast(clazz.getPackage().getName(), ".") +
				".mapper." + mapperName;
//		String filePath = "src/main/resources/mapper/" + vendor + "/" + mapperName + ".xml";
		return mapper(getType().get(), namespace, null);
	}

	/**
	 * 해당 모델 클래스로 CRUD 매퍼 쿼리를 자동 생성하여 필요시 저장
	 *
	 * @param modelClazz 모델 클래스
	 * @param namespace  네임 스페이스
	 * @param filePath   저장 파일(xml) 경로 (null 인경우 저장 안함)
	 * @return 매퍼 쿼리 문자열
	 * @throws Exception
	 */
	public String mapper(Class<?> modelClazz, String namespace, String filePath) throws Exception {
		String tableName = (String) SpringUtil.getValue(modelClazz, Table.class, "name");
		if (StringUtils.isEmpty(tableName)) {
			tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, modelClazz.getSimpleName());
		}

		Map<String, String> columns = FieldUtils.getFieldsListWithAnnotation(modelClazz, Column.class).stream()
				.collect(Collectors.toMap(a -> {
					String columnName = (String) SpringUtil.getValue(modelClazz, Column.class, "name");
					if (StringUtils.isEmpty(columnName)) {
						columnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, a.getName());
					}
					return columnName;
				}, Field::getName, (u, v) -> u, LinkedHashMap::new));

		Field idField = FieldUtils.getFieldsListWithAnnotation(modelClazz, Id.class).get(0);
		Map<String, String> idColumn = ImmutableMap.of(
				CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, idField.getName()),
				idField.getName());

		Map<String, String> allColumns = new LinkedHashMap<>(idColumn);
		allColumns.putAll(columns);

		Mapper mapper = Mapper.builder()
				.namespace(namespace)
				.className(modelClazz.getName())
				.tableName(tableName)
				.idColumn(idColumn.entrySet())
				.columns(columns.entrySet())
				.allColumns(allColumns.entrySet())
				.build();

		Mustache.Compiler compiler;
		Reader reader;
		if (StringUtils.isEmpty(filePath)) {
			compiler = this.compiler;
			reader = templateLoader.getTemplate(MAPPER);
		} else {
			compiler = Mustache.compiler();
			reader = getTemplate(MAPPER);
		}
		String result = compiler.withDelims("{= }")
				.compile(reader)
				.execute(mapper);
		log.debug("result:\n{}", result);

		if (StringUtils.isNotEmpty(filePath)) {
			File file = FileUtil.createFile(filePath);
			log.debug("file: {}", file.getAbsolutePath());
			FileUtil.write(file, result);
			return file.getAbsolutePath();
		}
		return result;
	}

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param params 검색 파라미터
	 * @param page   페이징 조건
	 * @return 페이징 목록
	 */
	protected Page<T> findPageOrEmpty(T entity, Map<String, Object> params, Paging page) {
		Map<String, Object> map = createParams(entity, params, page);
		List<T> content = mapper.find(map);
		long total = mapper.count(map);
		return getPage(content, page, total);
	}

	/**
	 * 매퍼 파일 파라미터
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	protected static class Mapper {
		/**
		 * 네임 스페이스
		 */
		private String namespace;
		/**
		 * 클래스명
		 */
		private String className;
		/**
		 * 테이블명
		 */
		private String tableName;
		/**
		 * 아이디 컬럼
		 */
		private Set<Map.Entry<String, String>> idColumn;
		/**
		 * 아이디를 제외한 컬럼들
		 */
		private Set<Map.Entry<String, String>> columns;
		/**
		 * 전체 컬럼들
		 */
		private Set<Map.Entry<String, String>> allColumns;
	}
}

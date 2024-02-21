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

package org.oh.common.util;

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.exception.DefaultException;
import org.oh.common.model.AbstractModel;
import org.oh.common.model.Model;
import org.oh.common.model.data.Condition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.ExampleMatcher;

import javax.persistence.Query;
import javax.sql.DataSource;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 쿼리 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class QueryUtil {
	public static final String KEY_ORDER_BY = "order_by";

	/**
	 * 커넥션의 유효성을 체크
	 *
	 * @param ds 데이터 소스
	 */
	public static void validateConnection(DataSource ds) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new CommonException(e);
		} finally {
			Optional.ofNullable(conn)
					.ifPresent(c -> {
						try {
							c.close();
						} catch (SQLException e) {
							log.error(ExceptionUtil.getMessageAndType(e), e);
						}
					});
		}
	}

	/**
	 * 해당 객체에서 원하는 필드를 제외하고 맵 형태로 변환
	 *
	 * @param obj           객체
	 * @param excludeFields 제외할 필드 리스트
	 * @return 맵
	 */
	public static Map<String, Object> convertObjectToMap(Object obj, String... excludeFields) {
		return convertObjectToMapWithDefault(obj, null, excludeFields);
	}

	/**
	 * 해당 객체에서 원하는 필드를 제외하고 맵 형태로 변환
	 *
	 * @param obj           객체
	 * @param defaultValue  null 값을 대체할 값
	 * @param excludeFields 제외할 필드 리스트
	 * @return 맵
	 */
	public static Map<String, Object> convertObjectToMapWithDefault(Object obj, Object defaultValue,
																	String... excludeFields) {
		DefaultException.assertTrue(obj != null, CommonError.COM_INVALID_ARGUMENT,
				"Obj must not be null", null);

		Map<String, Object> map = new HashMap<>();
		Class<?> clazz = obj.getClass();
		while (clazz != null) {
			for (Field field : clazz.getDeclaredFields()) {
				String key = field.getName();
				if (Modifier.isStatic(field.getModifiers())
						|| ArrayUtils.contains(excludeFields, key)) {
					continue;
				}
				field.setAccessible(true);
				Object value;
				try {
					value = field.get(obj);
				} catch (IllegalAccessException e) {
					throw new CommonException(e);
				}
				if (defaultValue != null && value == null) {
					map.put(key, defaultValue);
				} else {
					if (value != null) {
						map.put(key, value);
					}
				}
			}
			clazz = clazz.getSuperclass();
		}
		return map;
	}

	/**
	 * 해당 파라미터와 객체롤 Spring JPA WHERE절을 만들어 반환
	 *
	 * @param params        파라미터
	 * @param obj           객체
	 * @param alias         테이블 OWNER
	 * @param excludeFields 제외할 필드 리스트
	 * @return Spring JPA WHERE절
	 */
	public static String createWhere(Map<String, Object> params, Object obj, String alias, String... excludeFields) {
		if (params == null || obj == null || alias == null) {
			return "";
		}
		Map<String, Object> map = convertObjectToMap(obj, excludeFields);
		params.putAll(map);
		return map.keySet().stream()
				.map(e -> String.format("AND %s.%s = :%s ", alias, e, e))
				.collect(Collectors.joining());
	}

	/**
	 * JPA 쿼리에 파라미터를 설정
	 *
	 * @param query      JPA 쿼리
	 * @param parameters 파라미터
	 * @return 쿼리
	 */
	public static Query setParameters(Query query, Map<String, Object> parameters) {
		DefaultException.assertTrue(query != null, CommonError.COM_INVALID_ARGUMENT,
				"Query must not be null", null);
		DefaultException.assertTrue(parameters != null, CommonError.COM_INVALID_ARGUMENT,
				"Parameters must not be null", null);

		parameters.forEach(query::setParameter);
		return query;
	}

	/**
	 * 검색 조건을 반환
	 *
	 * @param entity 대상 객체
	 * @return 검색 조건
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Model<ID>, ID> ExampleMatcher getMatcher(T entity) {
		ExampleMatcher matcher = ExampleMatcher.matchingAll();
		if (entity instanceof AbstractModel) {
			AbstractModel<ID> model = (AbstractModel<ID>) entity;
			if (model.getOperation() == Condition.Operarion.OR) {
				matcher = ExampleMatcher.matchingAny();
			}

			List<Condition> conditions = model.getConditions();
			if (conditions != null) {
				for (Condition condition : conditions) {
					ExampleMatcher.GenericPropertyMatcher propertyMatcher = ExampleMatcher.GenericPropertyMatchers
							.storeDefaultMatching();
					for (Condition.Property property : condition.getProperties()) {
						switch (property) {
							case STARTS_WITH:
								propertyMatcher.startsWith();
								break;
							case ENDS_WITH:
								propertyMatcher.endsWith();
								break;
							case CONTAINS:
								propertyMatcher.contains();
								break;
							case IGNORE_CASE:
								propertyMatcher.ignoreCase();
						}
					}
					matcher = matcher.withMatcher(condition.getFieldName(), propertyMatcher);
				}
			}
		}
		return matcher;
	}

	/**
	 * 스키마 명과  테이블 명을 조합하여 반환
	 *
	 * @param schemaName 스키마명
	 * @param tableName  테이블명
	 * @return [스키마명.]테이블명
	 */
	public static String getTableName(String schemaName, String tableName) {
		return Optional.ofNullable(schemaName)
				.filter(StringUtils::isNotEmpty)
				.map(a -> a + '.')
				.orElse("") + tableName;
	}
}

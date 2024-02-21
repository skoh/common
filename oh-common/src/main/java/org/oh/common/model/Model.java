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

package org.oh.common.model;

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.persistence.Id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 모델 인터페이스
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("hibernateLazyInitializer")
public interface Model<ID>
		extends Serializable {
	String DATE_TIME = "";
	/**
	 * MySql 기본 일자 타입
	 */
//	String DATE_TIME = "DATETIME(6)";
	/**
	 * PostgreSql 기본 일자 타입
	 */
//	String DATE_TIME = "TIMESTAMP";

	/**
	 * 기본 일자 (현재 시간)
	 */
	String DEFAULT_DATE = "CURRENT_TIMESTAMP";

	String STR_Y = "Y";
	String STR_N = "N";

	/**
	 * id 필드가 Long 타입인 모델들을 id 기준으로 정렬
	 *
	 * @param entities 객체 목록
	 * @return 정렬된 객체들
	 */
	static <T extends Model<Long>> List<T> sortByLongId(List<T> entities) {
		return (List<T>) sort(entities, Comparator.comparing(T::id));
	}

	/**
	 * id 필드가 String 타입인 모델들을 id 기준으로 정렬
	 *
	 * @param entities 객체 목록
	 * @return 정렬된 객체들
	 */
	static <T extends Model<String>> List<T> sortByStringId(List<T> entities) {
		return (List<T>) sort(entities, Comparator.comparing(T::id));
	}

	/**
	 * id 필드가 Long 타입인 모델들을 id 기준으로 정렬
	 *
	 * @param entities 객체 목록
	 * @return 정렬된 객체들
	 */
	static <T extends Model<Long>> Set<T> sortByLongId(Set<T> entities) {
		return (Set<T>) sort(entities, Comparator.comparing(T::id));
	}

	/**
	 * id 필드가 String 타입인 모델들을 id 기준으로 정렬
	 *
	 * @param entities 객체 목록
	 * @return 정렬된 객체들
	 */
	static <T extends Model<String>> Set<T> sortByStringId(Set<T> entities) {
		return (Set<T>) sort(entities, Comparator.comparing(T::id));
	}

	/**
	 * 모델들을 해당 조건으로 정렬
	 *
	 * @param entities   객체 목록
	 * @param comparator 정렬 조건
	 * @return 정렬된 객체들
	 */
	static <T extends Model<ID>, ID> Collection<T> sort(Collection<T> entities, Comparator<T> comparator) {
		if (entities == null || entities.isEmpty()) {
			return entities;
		}

		Stream<T> result = entities.stream()
				.sorted(comparator);
		return entities instanceof List ? result.collect(Collectors.toList()) :
				result.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * 모델들을 복호화
	 *
	 * @param entities 객체 목록
	 */
	static <T extends Model<ID>, ID> void decrypt(Iterable<T> entities) {
		Optional.ofNullable(entities)
				.ifPresent(a -> a.forEach(Model::decrypt));
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 아이디 값을 반환 (javax.persistence.Id 또는 org.springframework.data.annotation.Id 로 설정된 필드)
	 */
	@SuppressWarnings("unchecked")
	default ID id() {
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(getClass(), Id.class);
		if (fields.isEmpty()) {
			fields = FieldUtils.getFieldsListWithAnnotation(getClass(),
					org.springframework.data.annotation.Id.class);
			if (fields.isEmpty()) {
				throw new CommonException(CommonError.COM_NO_ID_FIELD);
			}
		}

		try {
			return (ID) FieldUtils.readField(fields.get(0), this, true);
		} catch (IllegalAccessException e) {
			throw new CommonException(e);
		}
	}

	/**
	 * 필드 값을 반환 (필드 명이 fieldName 으로 설정된 필드값)
	 */
	default Optional<Object> getValue(String fieldName) {
		try {
			Object obj = FieldUtils.readField(this, fieldName, true);
			if (obj == null) {
				throw new RuntimeException();
			} else {
				return Optional.of(obj);
			}
		} catch (IllegalAccessException | RuntimeException e) {
			return Optional.empty();
		}
	}

	/**
	 * 추가 전 기본값 설정 (필요시 재정의)
	 */
	default void setInsertValue() {
	}

	/**
	 * 추가나 수정 전 기본값 설정 (필요시 재정의)
	 */
	default void setSaveValue() {
	}

	/**
	 * 조회 후 정렬 (필요시 재정의)
	 *
	 * @return 정렬된 모델
	 */
	default Model<ID> sort() {
		return this;
	}

	/**
	 * 조회 후 합계 (필요시 재정의)
	 *
	 * @return 합산된 모델
	 */
	default Model<ID> sum() {
		return this;
	}

	/**
	 * 추가나 수정 전 암호화 (필요시 재정의)
	 *
	 * @return 암호화된 모델
	 */
	default Model<ID> encrypt() {
		return this;
	}

	/**
	 * 조회 후 복호화 (필요시 재정의)
	 *
	 * @return 복호화된 모델
	 */
	default Model<ID> decrypt() {
		return this;
	}

	/**
	 * 검색 파라미터 타입 변환
	 */
	default Model<ID> convert() {
		return this;
	}
}

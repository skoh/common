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

package org.oh.common.mapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.oh.common.model.Model;

import java.util.List;
import java.util.Map;

/**
 * 기본 CRUD 매퍼 인터페이스
 */
public interface CrudMapper<T extends Model<ID>, ID> {
	/**
	 * 해당 객체를 추가
	 *
	 * @param entity 대상 객체
	 * @return 성공 여부
	 */
//	@ResultLogging
	boolean insert(T entity);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param params 파라미터
	 * @return 객체 목록
	 */
//	@ResultLogging
	List<T> find(Map<String, Object> params);

	/**
	 * 검색 건수를 조회
	 *
	 * @param params 파라미터
	 * @return 검색 건수
	 */
//	@ResultLogging
	long count(Map<String, Object> params);

	/**
	 * 아이디에 해당하는 객체를 수정
	 *
	 * @param params 파라미터
	 * @return 성공 여부
	 */
//	@ResultLogging
	boolean update(Map<String, Object> params);

	/**
	 * 해당 조건으로 검색 목록을 삭제
	 *
	 * @param params 파라미터
	 * @return 객체 목록
	 */
//	@ResultLogging
	boolean delete(Map<String, Object> params);

	/**
	 * DB 벤더
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	enum Vendor {
		H2("h2"),
		MYSQL("mysql"),
		POSTGRESQL("postgresql"),
		ORACLE("oracle"),
		TIBERO("tibero"),
		MSSQL("mssql");

		private final String value;
	}
}

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

package org.oh.common.repository;

import org.oh.common.model.data.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * DB 레파지토리 - 동적(수동) 쿼리(JPQL)시 사용
 */
public interface DbRepository {
	/**
	 * 해당 조건으로 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param query      쿼리
	 * @param parameters 파라미터
	 * @param resultType 결과 타입
	 * @return 객체 1건
	 */
	<T> T find(String query, Map<String, Object> parameters, Class<T> resultType);

	/**
	 * 해당 조건으로 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param query      쿼리
	 * @param resultType 결과 타입
	 * @return 객체 1건
	 */
	<T> T find(Query query, Class<T> resultType);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param query      쿼리
	 * @param parameters 파라미터
	 * @param lsize      조회 건수
	 * @param resultType 결과 타입
	 * @return 객체 목록
	 */
	<T> List<T> findAll(String query, Map<String, Object> parameters, int lsize, Class<T> resultType);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param query      쿼리
	 * @param resultType 결과 타입
	 * @return 객체 목록
	 */
	<T> List<T> findAll(Query query, Class<T> resultType);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param query      쿼리
	 * @param lsize      조회 건수
	 * @param resultType 결과 타입
	 * @return 객체 목록
	 */
	<T> List<T> findAll(Query query, int lsize, Class<T> resultType);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param query      쿼리
	 * @param parameters 파라미터
	 * @param pageable   페이징 조건
	 * @param resultType 결과 타입
	 * @return 페이징 목록
	 */
	<T> Page<T> findPage(String query, Map<String, Object> parameters, Pageable pageable, Class<T> resultType);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param query           쿼리
	 * @param parameters      파라미터
	 * @param countProjection 건수 조회용 쿼리
	 * @param pageable        페이징 조건
	 * @param resultType      결과 타입
	 * @return 페이징 목록
	 */
	<T> Page<T> findPage(String query, Map<String, Object> parameters, String countProjection,
						 Pageable pageable, Class<T> resultType);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param query      쿼리
	 * @param pageable   페이징 조건
	 * @param resultType 결과 타입
	 * @return 페이징 목록
	 */
	<T> Page<T> findPage(Query query, Pageable pageable, Class<T> resultType);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param query           쿼리
	 * @param countProjection 건수 조회용 쿼리
	 * @param pageable        페이징 조건
	 * @param resultType      결과 타입
	 * @return 페이징 목록
	 */
	<T> Page<T> findPage(Query query, String countProjection, Pageable pageable, Class<T> resultType);

	/**
	 * 해당 조건으로 데이터를 수정
	 *
	 * @param query      쿼리
	 * @param parameters 파라미터
	 * @return 수정 건수
	 */
	int update(String query, Map<String, Object> parameters);
}

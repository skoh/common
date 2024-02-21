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

import org.oh.common.model.Model;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * 기본 CRUD DB 서비스 인터페이스 (데이터 베이스 사용)
 */
public interface CrudDbService<T extends Model<ID>, ID>
		extends CrudService<T, ID> {
	void clear();

	void flush();

	/**
	 * 해당 조건으로 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @return 객체
	 */
	T find(T entity);

	/**
	 * 해당 조건으로 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @param sort   정렬 조건
	 * @return 객체
	 */
	T find(T entity, Sorting sort);

	/**
	 * 해당 조건으로 1건을 조회 (2건 이상 조회시 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @return 객체
	 */
	Optional<T> findOrEmpty(T entity);

	/**
	 * 해당 조건으로 1건을 조회 (2건 이상 조회시 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @return 객체
	 */
	Optional<T> findOrEmpty(T entity, Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @return 객체 목록
	 */
	List<T> findAll(T entity);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty(T entity);

	/**
	 * 해당 조건으로 검색 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAll(T entity, Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty(T entity, Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @param lsize  조회 건수
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAll(T entity, int lsize, Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param lsize  조회 건수
	 * @param sort   정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty(T entity, int lsize, Sorting sort);

	/**
	 * 해당 조건으로 페이징 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param entity 검색 조건
	 * @param page   페이징 조건
	 * @return 페이징 목록
	 */
	Page<T> findPage(T entity, Paging page);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param page   페이징 조건
	 * @return 페이징 목록
	 */
	Page<T> findPageOrEmpty(T entity, Paging page);

	/**
	 * 검색 건수를 조회
	 *
	 * @param entity 검색 조건
	 * @return 검색 건수
	 */
	long count(T entity);

	/**
	 * 해당 조건으로 데이터의 존재 여부
	 *
	 * @param entity 검색 조건
	 * @return 존재 여부
	 */
	boolean exists(T entity);

	/**
	 * 검색 조건에 만족하는 객체를 수정
	 *
	 * @param entity 수정 항목
	 * @param search 검색 조건
	 * @return 저장 객체
	 */
	T update(T entity, T search);

	/**
	 * 검색 목록을 삭제
	 *
	 * @param entity 검색 조건
	 */
	void delete(T entity);
}

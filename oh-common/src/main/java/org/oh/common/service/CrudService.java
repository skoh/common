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

import javax.validation.Valid;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 기본 CRUD 서비스 인터페이스 (데이터 그리드 사용)
 */
public interface CrudService<T extends Model<ID>, ID> {
	/**
	 * 아이디에 해당하는 1건을 조회 (없거나 2건 이상 조회시 예외 발생)
	 *
	 * @param id 아이디
	 * @return 객체
	 */
	T findById(ID id);

	/**
	 * 아이디에 해당하는 1건을 조회 (2건 이상 조회시 예외 발생)
	 *
	 * @param id 아이디
	 * @return 객체
	 */
	Optional<T> findByIdOrEmpty(ID id);

	/**
	 * 전체 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @return 객체 목록
	 */
	List<T> findAll();

	/**
	 * 전체 목록을 조회
	 *
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty();

	/**
	 * 해당 조건으로 전체 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param sort 정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAll(Sorting sort);

	/**
	 * 해당 조건으로 전체 목록을 조회
	 *
	 * @param sort 정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty(Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param lsize 조회 건수
	 * @param sort  정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAll(int lsize, Sorting sort);

	/**
	 * 해당 조건으로 검색 목록을 조회
	 *
	 * @param lsize 조회 건수
	 * @param sort  정렬 조건
	 * @return 객체 목록
	 */
	List<T> findAllOrEmpty(int lsize, Sorting sort);

	/**
	 * 해당 조건으로 페이징 목록을 조회 (1건도 없으면 예외 발생)
	 *
	 * @param page 페이징 조건
	 * @return 페이징 목록
	 */
	Page<T> findPage(Paging page);

	/**
	 * 해당 조건으로 페이징 목록을 조회
	 *
	 * @param page 페이징 조건
	 * @return 페이징 목록
	 */
	Page<T> findPageOrEmpty(Paging page);

	/**
	 * 전체 건수를 조회
	 *
	 * @return 객체 건수
	 */
	long count();

	/**
	 * 아이디에 해당하는 데이터의 존재 여부를 확인
	 *
	 * @param id 아이디
	 * @return 존재 여부
	 */
	boolean exists(ID id);

	/**
	 * 해당 객체를 추가
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	T insert(@Valid T entity);

	/**
	 * 해당 객체 목록을 추가
	 *
	 * @param entities 객체 목록
	 * @return 저장 목록
	 */
	Collection<T> insert(@Valid Collection<T> entities);

	/**
	 * 해당 객체가 있으면 무시, 없으면 추가
	 *
	 * @param entity 대상 객체
	 * @return 있으면 빈값, 없으면 추가한 저장 객체
	 */
	Optional<T> insertOrIgnore(@Valid T entity);

	/**
	 * 해당 객체 목록이 있으면 무시, 없으면 추가
	 *
	 * @param entities 객체 목록
	 * @return 있으면 빈값, 없으면 추가한 저장 목록
	 */
	Optional<Collection<T>> insertOrIgnore(@Valid Collection<T> entities);

	/**
	 * 아이디에 해당하는 객체를 병합 (없으면 생성, 있으면 수정)
	 *
	 * @param entity 대상 객체
	 * @return 저장 객체
	 */
	T merge(T entity);

	/**
	 * 해당 객체 목록을 수정
	 *
	 * @param entities 객체 목록
	 * @return 저장 목록
	 */
	List<T> update(Collection<T> entities);

	/**
	 * 아이디에 해당하는 객체를 수정
	 *
	 * @param entity 수정 항목 (아이디 필수)
	 * @return 저장 객체
	 */
	T update(T entity);

	/**
	 * 아이디에 해당하는 1건을 삭제 (없으면 예외 발생)
	 *
	 * @param id 아이디
	 */
	void deleteById(ID id);

	/**
	 * 아이디에 해당하는 1건을 삭제
	 *
	 * @param id 아이디
	 */
	void deleteByIdOrIgnore(ID id);

	/**
	 * 해당 객체 목록을 삭제
	 *
	 * @param entities 객체 목록
	 */
	void deleteAll(Collection<T> entities);

	/**
	 * 전체 목록을 삭제
	 */
	void deleteAll();

	/**
	 * 수퍼(제네릭) 타입명을 반환 (없으면 None)
	 *
	 * @return 수퍼 타입명
	 */
	String getTypeName();
}

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

package org.oh.sample.repository;

import org.oh.common.model.data.Sorting;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 샘플 JPQL 인터페이스
 */
public interface SampleJpqlRepository {
	/**
	 * 해당 조건으로 샘플 목록을 조회
	 *
	 * @param entity 검색 조건
	 * @param params 일자 조건
	 * @param sort   정렬 조건
	 * @return 샘플 목록
	 */
	List<Sample> search(Sample entity, StatsParams params, Sorting sort);

	/**
	 * 해당 조건으로 샘플 페이징 목록을 조회
	 *
	 * @param entity   검색 조건
	 * @param params   일자 조건
	 * @param pageable 페이징 조건
	 * @return 샘플 페이징 목록
	 */
	Page<Sample> search(Sample entity, StatsParams params, Pageable pageable);

	/**
	 * 해당 조건으로 샘플 현황을 조회
	 *
	 * @param entity 검색 조건
	 * @param params 일자 조건
	 * @param sort   정렬 조건
	 * @return 샘플 현황
	 */
	List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort);

	/**
	 * DB 초기화
	 */
	void init();
}

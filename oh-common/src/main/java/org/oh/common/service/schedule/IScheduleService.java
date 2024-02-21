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

package org.oh.common.service.schedule;

import org.oh.common.model.schedule.Schedule;

import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * 스케쥴 인터페이스
 */
public interface IScheduleService<T extends Schedule> {
	/**
	 * 해당 스케쥴을 조회
	 *
	 * @param id 아이디
	 * @return 스케쥴 정보
	 */
	Optional<T> findByIdOrEmpty(String id);

	/**
	 * 조건에 만족하는 스케쥴을 조회
	 *
	 * @param entity 스케쥴 조건
	 * @return 스케쥴 목록
	 */
	List<T> findAllOrEmpty(T entity);

	/**
	 * 해당 스케쥴을 추가
	 *
	 * @param entity 스케쥴 정보
	 * @return 스케쥴 정보
	 */
	T insertSchedule(@Valid T entity);

	/**
	 * 해당 스케쥴을 수정
	 *
	 * @param entity 스케쥴 정보
	 * @return 스케쥴 정보
	 */
	T updateSchedule(@Valid T entity);

	/**
	 * 해당 스케쥴을 삭제
	 *
	 * @param id 아이디
	 */
	void deleteByIdOrIgnore(String id);
}

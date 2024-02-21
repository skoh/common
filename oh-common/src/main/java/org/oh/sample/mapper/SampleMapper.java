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

package org.oh.sample.mapper;

import org.oh.common.annotation.ResultLogging;
import org.oh.common.mapper.CrudMapper;
import org.oh.sample.model.Sample;

import java.util.List;
import java.util.Map;

/**
 * 샘플 CRUD 매퍼 인터페이스
 */
public interface SampleMapper
		extends CrudMapper<Sample, Long> {
	/**
	 * 해당 조건으로 현황 목록을 조회
	 *
	 * @param params 파라미터
	 * @return 현황 목록
	 */
	@ResultLogging
	List<Map<String, Object>> stats(Map<String, Object> params);
}

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

package org.oh.sample.model;

import org.oh.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 현황 파라미터
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StatsParams {
	/**
	 * 시작 등록일자(yyyy-mm-dd)
	 */
	@JsonProperty(index = 10)
	@Schema(description = "시작 등록일자(yyyy-mm-dd)")
	protected String regDateStart;

	/**
	 * 종료 등록일자(yyyy-mm-dd)
	 */
	@JsonProperty(index = 20)
	@Schema(description = "종료 등록일자(yyyy-mm-dd)")
	protected String regDateEnd;


	/**
	 * 필드 조건으루 WHERE 절과 파라미터를 설정
	 *
	 * @param parameters 파라미터
	 * @return WHERE 절
	 */
	public String setParameters(Map<String, Object> parameters) {
		String where = "";
		if (StringUtils.isNotEmpty(regDateStart)) {
			where += " AND a.regDate >= :regDateStart";
			parameters.put("regDateStart", DateUtil.parseStartDate(regDateStart));
		}
		if (StringUtils.isNotEmpty(regDateEnd)) {
			where += " AND a.regDate <= :regDateEnd";
			parameters.put("regDateEnd", DateUtil.parseEndDate(regDateEnd));
		}
		return where;
	}
}

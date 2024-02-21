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

package org.oh.common.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.validation.constraints.NotEmpty;

import java.util.Map;

/**
 * 동적(수동) 쿼리(JPQL) 정보
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Query {
	public static final String GROUP_BY = " GROUP BY ";
	public static final String ORDER_BY = " ORDER BY ";

	/**
	 * SELECT 절
	 */
	@NotEmpty
	protected String select;

	/**
	 * FROM 절
	 */
	@NotEmpty
	protected String from;
	/**
	 * WHERE 절
	 */
	protected String where;
	/**
	 * GROUP BY 절
	 */
	protected String groupBy;
	/**
	 * ORDER BY 절
	 */
	protected String orderBy;

	/**
	 * 파라이터
	 */
	protected Map<String, Object> parameters;

	@Override
	public String toString() {
		Assert.hasText(select, "Select must not be empty");
		Assert.hasText(from, "From must not be empty");

		return "SELECT " + select +
				"  FROM " + from +
				(StringUtils.isEmpty(where) ? "" : " WHERE 1 = 1 " + where) +
				(StringUtils.isEmpty(groupBy) ? "" : GROUP_BY + groupBy) +
				(StringUtils.isEmpty(orderBy) ? "" : ORDER_BY + orderBy);
	}
}

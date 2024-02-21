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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 검색 조건
 */
@Schema(description = "검색 조건")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
	@Schema(description = "필드명")
	protected String fieldName;

	@Schema(description = "검색 속성들",
			example = "STARTS_WITH, ENDS_WITH, CONTAINS, IGNORE_CASE")
	protected Set<Property> properties;

	/**
	 * 검색 연산자
	 */
	public enum Operarion {
		AND,
		OR;
	}

	/**
	 * 검색 속성
	 */
	public enum Property {
		/**
		 * 시작 문자열
		 */
		STARTS_WITH,
		/**
		 * 종료 문자열
		 */
		ENDS_WITH,
		/**
		 * 포함 문자열
		 */
		CONTAINS,
		/**
		 * 대소문자 무시
		 */
		IGNORE_CASE;

		public static Set<Property> of(List<String> properties) {
			return properties.stream()
					.map(Property::valueOf)
					.collect(Collectors.toSet());
		}
	}
}

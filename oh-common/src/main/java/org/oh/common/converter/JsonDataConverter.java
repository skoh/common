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

package org.oh.common.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.oh.common.util.JsonUtil;

import javax.persistence.AttributeConverter;

import java.util.Optional;

/**
 * JsonNode <-> String 데이터 변환
 */
public class JsonDataConverter
		implements AttributeConverter<JsonNode, String> {
	/**
	 * JsonNode <-> String 데이터 변환
	 *
	 * @param value JsonNode 데이터
	 * @return String 데이터
	 */
	@Override
	public String convertToDatabaseColumn(JsonNode value) {
		return Optional.ofNullable(value)
				.map(JsonUtil::writeValueAsString)
				.orElse(null);
	}

	/**
	 * String <-> JsonNode 데이터 변환
	 *
	 * @param value String 데이터
	 * @return JsonNode 데이터
	 */
	@Override
	public JsonNode convertToEntityAttribute(String value) {
		return Optional.ofNullable(value)
				.filter(a -> !a.isEmpty())
				.map(JsonUtil::readTree)
				.orElse(null);
	}
}

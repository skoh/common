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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.oh.common.config.DataGridConfig;
import org.oh.common.converter.JsonDataConverter;
import org.oh.common.model.AbstractCommon;
import org.oh.common.util.JsonUtil;
import org.springframework.data.keyvalue.annotation.KeySpace;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

import java.util.Optional;

/**
 * 메모리 데이터 (다양한 용도로 활용 가능)
 */
@Schema(description = Data.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@KeySpace(DataGridConfig.MAP_NAME_DEFAULT)
public class Data
		extends AbstractCommon {
	public static final String NAME_SPACE = "data";
	public static final String TABLE_DESC = "데이터";

	/**
	 * 종류
	 */
	@JsonProperty(index = 10)
	@Schema(description = "종류")
	@Column(length = 100)
	@Comment("종류")
	protected String type;

	/**
	 * 속성들
	 */
	@JsonProperty(index = 20)
	@Schema(description = "속성들")
	@Convert(converter = JsonDataConverter.class)
	@Column(length = 1_000) // MySql: columnDefinition = "JSON")
	@Comment("속성들")
	protected JsonNode attributes;

	@Override
	public void setInsertValue() {
		super.setInsertValue();
	}

	/**
	 * 속성에서 이름에 해당하는 값을 반환
	 *
	 * @param name      속성명
	 * @param valueType 결과 타입
	 * @return 속성 값
	 */
	public <T> Optional<T> getAttribute(String name, Class<T> valueType) {
		return Optional.ofNullable(attributes)
				.map(a -> JsonUtil.treeToValue(a.get(name), valueType));
	}

	/**
	 * 속성에 이름과 값을 설정
	 *
	 * @param name  속성명
	 * @param value 속성 값
	 */
	public void setAttribute(String name, Object value) {
		ObjectNode attributesTemp = Optional.ofNullable(attributes)
				.map(ObjectNode.class::cast)
				.orElseGet(JsonUtil.OBJECT_MAPPER::createObjectNode);
		attributesTemp.putPOJO(name, value);
		attributes = attributesTemp;
	}

	/**
	 * 속성에서 이름에 해당하는 값을 삭제
	 *
	 * @param name 속성명
	 */
	public void clearAttribute(String name) {
		Optional.ofNullable(attributes)
				.map(ObjectNode.class::cast)
				.ifPresent(a -> a.remove(name));
	}

	/**
	 * 속성에서 모든 이름과 값을 삭제
	 */
	public void clearAllAttribute() {
		Optional.ofNullable(attributes)
				.map(ObjectNode.class::cast)
				.ifPresent(ObjectNode::removeAll);
	}
}

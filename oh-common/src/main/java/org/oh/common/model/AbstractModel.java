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

package org.oh.common.model;

import org.oh.common.converter.DateTimeFormatConverter;
import org.oh.common.model.data.Condition;
import org.oh.common.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 최상위 모델
 */
@Data
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractModel<ID>
		implements Model<ID>, Comparable<AbstractModel<ID>> {
//	public static <ID> boolean isNew(AbstractModel<ID> entity) {
//		return !(entity instanceof HibernateProxy) &&
//				entity.getRegDate() == null;
//	}

	public static List<Condition> toCondition(String conditions) {
		if (conditions == null) {
			return null;
		}

		return Arrays.stream(conditions.split(":"))
				.map(e -> {
					List<String> conds = Arrays.asList(e.split(","));
					return Condition.builder()
							.fieldName(conds.get(0))
							.properties(Condition.Property.of(conds.subList(1, conds.size())))
							.build();
				})
				.collect(Collectors.toList());
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 등록일시(형식: yyyy-MM-dd HH:mm:ss)
	 */
	@JsonProperty(index = 910)
	@Schema(description = "등록일시(형식: yyyy-MM-dd HH:mm:ss)", hidden = true)
	@JsonView(CommonModel.Default.class)
//	@DateTimeFormat(pattern = DateUtil.DEFAULT_DATE_TIME_PATTERN)
	@JsonSerialize(converter = DateTimeFormatConverter.DateToStringConverter.class)
	@JsonDeserialize(converter = DateTimeFormatConverter.StringToDateConverter.class)
	@Column(nullable = false, columnDefinition = DATE_TIME)
	@ColumnDefault(DEFAULT_DATE)
	@Comment("등록일시")
//	@Temporal(TemporalType.TIMESTAMP)
	protected Date regDate;

	/**
	 * 수정일시(형식: yyyy-MM-dd HH:mm:ss)
	 */
	@JsonProperty(index = 920)
	@Schema(description = "수정일시(형식: yyyy-MM-dd HH:mm:ss)", hidden = true)
	@JsonView(CommonModel.Default.class)
	@JsonSerialize(converter = DateTimeFormatConverter.DateToStringConverter.class)
	@JsonDeserialize(converter = DateTimeFormatConverter.StringToDateConverter.class)
	@Column(nullable = false, columnDefinition = DATE_TIME)
	@ColumnDefault(DEFAULT_DATE)
	@Comment("수정일시")
	protected Date modDate;

	/**
	 * 검색 연산자 (기본값: AND)
	 * <pre>
	 * - Parameter: operation=OR
	 * - SQL: name = ? OR desc = ? OR ...
	 * </pre>
	 */
	@JsonProperty(index = 970)
	@Schema(description = "검색 연산자 (AND/OR 기본값: AND)", example = "OR")
	@Transient
	protected Condition.Operarion operation;

	/**
	 * 검색 조건 (POJO)
	 */
	@JsonView(CommonModel.Ignore.class)
	@Schema(hidden = true)
	@Transient
	protected List<Condition> conditions;

	/**
	 * 검색 조건 (LIKE 등)
	 * <pre>
	 * - Parameter: condition=name,STARTS_WITH:desc,CONTAINS,IGNORE_CASE
	 * - SQL: name LIKE '?%' AND LOWER(desc) LIKE '%?%'
	 * </pre>
	 */
	@JsonProperty(index = 980)
	@Schema(description = "검색 조건 (필드명1,[STARTS_WITH/ENDS_WITH/CONTAINS/IGNORE_CASE],...:필드명2,...)",
			example = "name,STARTS_WITH:desc,CONTAINS,IGNORE_CASE")
	@Transient
	protected String condition;

	/**
	 * null 필드들 (null 값으로 수정시 사용)
	 * <pre>
	 * 예) { "nulls" : [ "state" ] }
	 * </pre>
	 */
	@JsonProperty(index = 990)
	@Schema(description = "null 필드들 (null 값으로 수정시 사용)")
	@JsonView(CommonModel.Ignore.class)
	@Transient
	protected List<String> nulls;

	///////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(AbstractModel<ID> entity) {
		return getRegDate().compareTo(entity.getRegDate());
	}

	@Override
	public void setInsertValue() {
		if (regDate == null) {
			regDate = new Date();
		}
	}

	@Override
	public void setSaveValue() {
//		if (regDate == null) {
		modDate = new Date();
//		} else {
//			modDate = regDate;
//		}
	}

	@Override
	public AbstractModel<ID> convert() {
		conditions = toCondition(condition);
		return this;
	}

	@Override
	public String toString() {
		return toString(JsonUtil.toString(this, CommonModel.Default.class));
	}

	/**
	 * JSON 뷰 모델에 맞게 선택한 필드만 출력
	 *
	 * @param jsonView JSON 뷰 모델
	 * @return JSON 문자열
	 */
	public String toString(Class<? extends CommonModel.None> jsonView) {
		return toString(JsonUtil.toString(this, jsonView));
	}

	/**
	 * 클래스명과 같이 출력
	 *
	 * @param value JSON 문자열
	 * @return ex) ["Document", JSON 문자열]
	 */
	private String toString(String value) {
		return "[\"" + getClass().getSimpleName() + "\"," + value + ']';
	}
}

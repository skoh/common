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

package org.oh.common.model.schedule;

import org.oh.common.config.DataGridConfig;
import org.oh.common.model.AbstractCommon;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.springframework.data.keyvalue.annotation.KeySpace;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

/**
 * 메모리 스케쥴 (스케쥴 이중화에 메모리를 사용)
 */
@Schema(description = Schedule.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@KeySpace(DataGridConfig.MAP_NAME_SCHEDULE)
public class Schedule
		extends AbstractCommon {
	public static final String NAME_SPACE = "schedule";
	public static final String TABLE_DESC = "스케쥴";

	/**
	 * 종류
	 */
	@JsonProperty(index = 10)
	@Schema(description = "종류")
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("종류")
	protected String type;

	/**
	 * 프로세스 아이디
	 */
	@JsonProperty(index = 20)
	@Schema(description = "프로세스 아이디")
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("프로세스 아이디")
	protected String pid;
}

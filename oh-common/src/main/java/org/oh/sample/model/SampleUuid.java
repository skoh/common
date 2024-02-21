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

import org.oh.common.config.DataGridConfig;
import org.oh.common.model.AbstractDefaultUuid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.keyvalue.annotation.KeySpace;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * 샘플 UUID
 */
@Schema(description = SampleUuid.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(indexes = {
		@Index(name = "idx_sample_uuid_name", columnList = "name"),
		@Index(name = "idx_sample_uuid_state", columnList = "state"),
		@Index(name = "idx_sample_uuid_reg_date", columnList = "regDate"),
		@Index(name = "idx_sample_uuid_mod_date", columnList = "modDate")})
@org.hibernate.annotations.Table(appliesTo = SampleUuid.TABLE_NAME, comment = SampleUuid.TABLE_DESC)
@KeySpace(DataGridConfig.MAP_NAME_DEFAULT)
public class SampleUuid
		extends AbstractDefaultUuid {
	public static final String NAME_SPACE = "sampleUuid";
	public static final String TABLE_NAME = "sample_uuid";
	public static final String TABLE_DESC = "샘플 UUID";

	@JsonProperty(index = 10)
	@Schema(description = "이름")
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("이름")
	protected String name;

	@JsonProperty(index = 20)
	@Schema(description = "설명")
	@Column(length = 1000)
	@Comment("설명")
	protected String descp;
}

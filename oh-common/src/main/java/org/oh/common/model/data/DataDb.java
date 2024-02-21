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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * 데이터 DB (다양한 용도로 활용 가능)
 */
@Schema(description = Data.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = Data.NAME_SPACE, indexes = {
		@Index(name = "idx_data_state", columnList = "state"),
		@Index(name = "idx_data_reg_date", columnList = "regDate")})
@org.hibernate.annotations.Table(appliesTo = Data.NAME_SPACE, comment = Data.TABLE_DESC)
public class DataDb
		extends Data {
	public static final String NAME_SPACE = "dataDb";
}

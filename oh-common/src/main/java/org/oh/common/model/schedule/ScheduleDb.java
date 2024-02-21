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
 * DB 스케쥴 (스케쥴 이중화에 DB를 사용)
 */
@Schema(description = Schedule.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = Schedule.NAME_SPACE, indexes = {
		@Index(name = "idx_schedule_type", columnList = "type", unique = true),
		@Index(name = "idx_schedule_state", columnList = "state"),
		@Index(name = "idx_schedule_reg_date", columnList = "regDate"),
		@Index(name = "idx_schedule_mod_date", columnList = "modDate")})
@org.hibernate.annotations.Table(appliesTo = Schedule.NAME_SPACE, comment = Schedule.TABLE_DESC)
public class ScheduleDb
		extends Schedule {
}

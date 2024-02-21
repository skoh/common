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

import org.oh.common.model.enume.State;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import java.util.UUID;

/**
 * 기본 UUID 모델
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractDefaultUuid
		extends AbstractModel<UUID> {
	/**
	 * 아이디
	 */
	@JsonProperty(index = 1)
	@Schema(description = "아이디", hidden = true)
	@org.springframework.data.annotation.Id
	@Id
	@GeneratedValue(generator = "uuid2")
//	@GenericGenerator(name = "uuid2", strategy = "uuid2")
//	@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
	@Type(type = "org.hibernate.type.UUIDCharType")
//	@Column(columnDefinition = "BINARY(16)")
	@Column(columnDefinition = "CHAR(36)")
	@ColumnDefault("uuid()")
	@Comment("아이디(UUID)")
	protected UUID id;

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 상태(ACTIVE:사용중, START: 시작, END: 종료, DELETED:삭제)
	 */
	@JsonProperty(index = 810)
	@Schema(description = State.DESC)
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false, columnDefinition = State.ENUM)
	@ColumnDefault(State.DEFAULT)
	@Comment(State.DESC)
	protected State state;

	@Override
	public void setInsertValue() {
		super.setInsertValue();
		if (state == null) {
			state = State.ACTIVE;
		}
	}

	/**
	 * 상태가 "사용중"인지 여부
	 *
	 * @return 사용 여부
	 */
	public boolean activated() {
		return state == State.ACTIVE;
	}
}

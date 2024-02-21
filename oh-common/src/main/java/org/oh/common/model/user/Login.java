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

package org.oh.common.model.user;

import org.oh.common.converter.DateTimeFormatConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * 로그인 정보
 */
@Schema(description = "로그인 정보")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Login
		extends AbstractUser {
	/**
	 * 관리자 정보
	 */
	public static final Login ADMIN = Login.builder()
			.id(ADMIN_ID)
			.build();

	/**
	 * 토큰 만료 시간(분)
	 */
	@JsonProperty(index = 10)
	@Schema(description = "토큰 만료 시간(분)", hidden = true)
	protected Integer expireTimeMin;

	/**
	 * 토큰 만료 일시
	 */
	@JsonProperty(index = 20)
	@Schema(description = "토큰 만료 일시", hidden = true)
	@JsonSerialize(converter = DateTimeFormatConverter.DateToStringConverter.class)
	@JsonDeserialize(converter = DateTimeFormatConverter.StringToDateConverter.class)
	protected Date expireDate;
}

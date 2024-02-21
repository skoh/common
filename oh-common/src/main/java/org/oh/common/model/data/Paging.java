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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 페이징 정보
 */
@Schema(description = "페이지")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Paging
		extends Sorting {
	/**
	 * 페이지 번호(1~N)
	 */
//	@Schema(description = "Results page you want to retrieve (1~N)", example = "1")
	@Schema(description = "페이지 번호(1~N)", example = "1")
	@Builder.Default
	protected int page = 1;

	/**
	 * 페이지 당 항목 수
	 */
//	@Schema(description = "Number of records per page.", example = "20")
	@Schema(description = "페이지 당 항목 수", example = "20")
	@Builder.Default
	protected int psize = 20;

	/**
	 * Spring 페이징 정보로 변환
	 *
	 * @return Spring 페이징 정보
	 */
	public Pageable pageable() {
		return PageRequest.of(page - 1, psize, parseParameterIntoSort(sort, ","));
	}
}

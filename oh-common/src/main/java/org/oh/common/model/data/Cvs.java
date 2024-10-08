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
import lombok.Data;

/**
 * CVS 조건
 */
@Schema(description = "CVS")
@Data
public class Cvs {
	@Schema(description = "필드명 리스트", example = "id,state,regDate,modDate")
	protected String fieldNames;

	@Schema(description = "헤더", example = "아이디,상태,등록일시,수정일시")
	protected String header;
}

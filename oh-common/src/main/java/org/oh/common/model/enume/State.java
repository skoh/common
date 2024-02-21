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

package org.oh.common.model.enume;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상태
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum State {
	/**
	 * 사용중
	 */
	ACTIVE("사용중"),
	/**
	 * 시작
	 */
	START("시작"),
	/**
	 * 종료
	 */
	END("종료"),
	/**
	 * 삭제
	 */
	DELETED("삭제");

	public static final String ENUM = "";
	//	public static final String ENUM = "ENUM ('ACTIVE', 'START', 'END', 'DELETED')"; // mySql
	public static final String DESC = "상태 (ACTIVE:사용중, START: 시작, END: 종료, DELETED:삭제)";
	public static final String DEFAULT = "'ACTIVE'";

	private final String value;
}

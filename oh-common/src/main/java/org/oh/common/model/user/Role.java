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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 권한 목록
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {
	/**
	 * 사용자 권한
	 */
	ROLE_USER("USER"),
	/**
	 * 일반 관린자 권한
	 */
	ROLE_MANAGER("MANAGER"),
	/**
	 * 수퍼 관리자 권한
	 */
	ROLE_ADMIN("ADMIN");

	private final String value;

	public static final String ENUM = "";
	//		public static final String ENUM = "ENUM ('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')"; // mySql
	public static final String DEFAULT = "'ROLE_USER'";
}

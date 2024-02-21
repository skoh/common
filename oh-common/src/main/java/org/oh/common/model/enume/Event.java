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
 * 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Event {
	/**
	 * 생성
	 */
	CREATE("생성"),
	/**
	 * 조회
	 */
	READ("조회"),
	/**
	 * 수정
	 */
	UPDATE("수정"),
	/**
	 * 삭제
	 */
	DELETE("삭제"),
	/**
	 * 검증
	 */
	VERIFY("검증");

	public static final String ENUM = "";
	//	public static final String ENUM = "ENUM ('CREATE', 'READ', 'UPDATE', 'DELETE', 'VERIFY')"; // mySql
	public static final String DESC = "이벤트 (CREATE:생성, READ:조회, UPDATE:수정, DELETE:삭제, VERIFY:검증)";
	public static final String DEFAULT = "'CREATE'";

	private final String value;
}

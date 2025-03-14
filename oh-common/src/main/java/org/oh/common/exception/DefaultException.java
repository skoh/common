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

package org.oh.common.exception;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.StringUtil;

/**
 * 기본 예외
 */
@Getter
public abstract class DefaultException
		extends RuntimeException {
	/**
	 * 기본 에러 코드 접두어
	 */
	public static final String CODE_PREFIX = "DEF-";

	protected final Error error;

	/**
	 * 해당 예외 조건에 따른 예외 발생
	 *
	 * @param condition 예외 조건
	 * @param message   에러 메세지
	 */
	public static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new CommonException(message);
		}
	}

	/**
	 * 해당 예외 조건에 따른 예외 발생
	 *
	 * @param condition 예외 조건
	 * @param error     에러 유형
	 * @param message   에러 메세지
	 * @param cause     원인 예외
	 */
	public static void assertTrue(boolean condition, Error error, String message, Throwable cause) {
		if (!condition) {
			throw new CommonException(error, message, cause);
		}
	}

	protected DefaultException(Error error, String message, Throwable cause) {
		super(StringUtil.joiningBySpace(error.toCodeString(),
				StringUtils.isEmpty(message) && cause != null ?
						ExceptionUtil.getNotRootMessage(cause) : message), cause);
		this.error = error;
	}
}

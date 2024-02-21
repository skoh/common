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

package org.oh.adapter.exception;

import org.oh.common.exception.DefaultException;
import org.oh.common.exception.Error;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AdapterException
		extends DefaultException {
	public static final String CODE_PREFIX = "ADT-";

	public AdapterException(String message) {
		this(message, null);
	}

	public AdapterException(Throwable cause) {
		this((String) null, cause);
	}

	public AdapterException(String message, Throwable cause) {
		this(Error.DefaultError.NONE, message, cause);
	}

	public AdapterException(Error error) {
		this(error, (String) null);
	}

	public AdapterException(Error error, String message) {
		this(error, message, null);
	}

	public AdapterException(Error error, Throwable cause) {
		this(error, null, cause);
	}

	public AdapterException(Error error, String message, Throwable cause) {
		super(error, message, cause);
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum AdapterError
			implements Error {
		FAIL_EAI(CODE_PREFIX + "10-01", "EAI 작업을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

		private final String code;
		private final String message;
		private final HttpStatus httpStatus;

		@Override
		public String toString() {
			return toCodeString();
		}
	}
}

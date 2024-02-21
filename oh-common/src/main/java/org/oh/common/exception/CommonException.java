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

/**
 * 공통 예외
 */
public class CommonException
		extends DefaultException {
	public CommonException(String message) {
		this(message, null);
	}

	public CommonException(Throwable cause) {
		this((String) null, cause);
	}

	public CommonException(String message, Throwable cause) {
		this(Error.DefaultError.NONE, message, cause);
	}

	public CommonException(Error error) {
		this(error, (String) null);
	}

	public CommonException(Error error, String message) {
		this(error, message, null);
	}

	public CommonException(Error error, Throwable cause) {
		this(error, null, cause);
	}

	public CommonException(Error error, String message, Throwable cause) {
		super(error, message, cause);
	}
}

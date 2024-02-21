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

import org.oh.common.util.SpringUtil;
import org.oh.common.util.StringUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 최상위 에러 유형
 */
public interface Error
		extends Serializable {
	/**
	 * 에러 유형을 반환
	 */
	String name();

	/**
	 * 어레 코드를 반환
	 */
	String getCode();

	/**
	 * 어레 메세지를 반환
	 */
	String getMessage();

	/**
	 * HTTP 상태 코드를 반환
	 */
	HttpStatus getHttpStatus();

	default String ofString() {
		return String.format("name: %s code: %s message: %s httpStatus: %s",
				name(), getCode(), getMessage(), getHttpStatus());
	}

	default String toCodeString() {
//		return StringUtil.toCodeString(name(), getMessage());
		String name = "".equals(name()) || DefaultError.NONE.name().equals(name()) ? "" : "][" + name();
//		String serverInfo = SpringUtil.getServerInfo(); // ex) localhost:8010
		String serverInfo = SpringUtil.getEnvironment() // ex) didm
				.map(a -> a.getProperty(SpringUtil.SPRING_CONFIG_NAME))
				.orElse("");
		return StringUtil.toCodeString(serverInfo + name, getMessage()); // error
	}

	/**
	 * 기본 에러 유형
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	enum DefaultError
			implements Error {
		NONE(DefaultException.CODE_PREFIX + "10-01", "", HttpStatus.INTERNAL_SERVER_ERROR),
		UNKNOWN(DefaultException.CODE_PREFIX + "10-02", "알수 없음", HttpStatus.INTERNAL_SERVER_ERROR);

		private final String code;
		private final String message;
		private final HttpStatus httpStatus;

		@Override
		public String toString() {
			return toCodeString();
		}
	}

	/**
	 * 커스텀 에러 유형
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	class CustomError
			implements Error {
		public static CustomError createError(Error error, HttpStatus httpStatus) {
			return createError(error.name(), error.getCode(), error.getMessage(), httpStatus);
		}

		public static CustomError createError(String name, String code, String message, HttpStatus httpStatus) {
			return new CustomError(name, code, message, httpStatus);
		}

		protected final String name;
		protected final String code;
		protected final String message;
		protected final HttpStatus httpStatus;

		@Override
		public String name() {
			return name;
		}

		@Override
		public String toString() {
			return toCodeString();
		}
	}
}

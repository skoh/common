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

package org.oh.common.util;

import org.oh.common.exception.DefaultException;
import org.oh.common.exception.Error;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Optional;

/**
 * 예외 유틸리티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ExceptionUtil {
	public static final String CAUSED_BY = " Caused by: ";

	/**
	 * 원인 예외를 반환
	 *
	 * @param e 예외
	 * @return 원인 예외
	 */
	public static Throwable getCause(Throwable e) {
		return Optional.ofNullable(e.getCause())
				.orElse(e);
	}

	/**
	 * 최상위 원인 예외를 반환
	 *
	 * @param e 예외
	 * @return 최상위 원인 예외
	 */
	public static Throwable getRootCause(Throwable e) {
		return Optional.ofNullable(ExceptionUtils.getRootCause(e))
				.orElse(e);
	}

	/**
	 * 원인 예외의 메세지를 반환
	 *
	 * @param e 예외
	 * @return 원인 예외의 메세지
	 */
	public static String getRootMessage(Throwable e) {
		return getRootCause(e).getMessage();
	}

	/**
	 * 원인 예외가 아니면 메세지를, 원인 예외 이면 ""를 반환
	 *
	 * @param e 예외
	 * @return 원인 예외가 아닌 메세지
	 */
	public static String getNotRootMessage(Throwable e) {
		return e == getRootCause(e) ? "" : e.getMessage();
	}

	/**
	 * 예외와 원인 예외의 메세지를 동시에 반환
	 *
	 * @param e 예외
	 * @return 예외와 원인 예외 메세지
	 */
	public static String getMessage(Throwable e) {
		return getMessage(CAUSED_BY, e);
	}

	/**
	 * 예외와 원인 예외의 메세지를 구분자로 나누어 동시에 반환
	 *
	 * @param delimiter 구분자
	 * @param e         예외
	 * @return 예외와 원인 예외 메세지
	 */
	public static String getMessage(String delimiter, Throwable e) {
		Throwable cause = getRootCause(e);
		return e == cause ? e.getMessage() : StringUtil.joining(delimiter, e.getMessage(), cause.getMessage());
	}

	/**
	 * 예외와 원인 예외의 메세지를 예외명과 함께 동시에 반환
	 *
	 * @param e 예외
	 * @return 예외와 원인 예외 메세지
	 */
	public static String getMessageAndType(Throwable e) {
		Throwable cause = getRootCause(e);
		return e == cause ? ExceptionUtils.getMessage(e)
				: StringUtil.joining(CAUSED_BY, ExceptionUtils.getMessage(e), ExceptionUtils.getMessage(cause));
	}

	/**
	 * 예외 메세지에서 인자값을 제외한 메세지를 반환
	 *
	 * @param e 예외
	 * @return 인자값을 제외한 메세지
	 */
	public static String getMessageWithoutArgs(Throwable e) {
		String message = getMessage(e);
		return getMessageWithoutArgs(message);
	}

	/**
	 * 인자값을 제외한 메세지를 반환
	 *
	 * @param message 예외 메세지
	 * @return 인자값을 제외한 메세지
	 */
	public static String getMessageWithoutArgs(String message) {
		if (message == null) {
			return "";
		}
		String before = StringUtils.substringBefore(message, CommonUtil.SEPARATOR);
		String after = StringUtils.substringAfterLast(message, CAUSED_BY);
		return before + (StringUtils.isEmpty(after) ? "" : CAUSED_BY + after);
	}

	/**
	 * 해당 예외 클래스가 발견되는 첫번째 예외를 반환
	 *
	 * @param e    예외
	 * @param type 예외 클래스
	 * @return 첫번째 예외
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> Optional<T> getFirstExceptionOrNull(Throwable e, Class<T> type) {
		return (Optional<T>) ExceptionUtils.getThrowableList(e).stream()
				.filter(type::isInstance)
				.findFirst();
	}

	/**
	 * 해당 예외 클래스가 발견되는 마지막 예외를 반환
	 *
	 * @param e    예외
	 * @param type 예외 클래스
	 * @return 마지막 예외
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Throwable> Optional<T> getLastExceptionOrNull(Throwable e, Class<T> type) {
		return (Optional<T>) ExceptionUtils.getThrowableList(e).stream()
				.filter(type::isInstance)
				.reduce((first, second) -> second);
	}

	/**
	 * 기본 예외의 에러를 반환
	 *
	 * @param de 기본 예외
	 * @return 기본 예외의 에러
	 */
	public static Error getError(DefaultException de) {
		return Optional.ofNullable(de)
				.map(DefaultException::getError)
				.orElse(Error.DefaultError.NONE);
	}
}

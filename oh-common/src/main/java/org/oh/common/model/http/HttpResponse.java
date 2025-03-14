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

package org.oh.common.model.http;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.oh.common.exception.DefaultException;
import org.oh.common.exception.Error;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.StringUtil;

/**
 * HTTP 응답 메세지
 */
@Schema(description = "HTTP 응답")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpResponse
		implements Response {
	public static final String MESSAGE_SUCCESS = "Success";

	/**
	 * 성공 응답 메세지 반환
	 *
	 * @return 성공 응답 메세지
	 */
	public static HttpResponse ofSuccess() {
		return new HttpResponse(MESSAGE_SUCCESS, "");
	}

	/**
	 * 실패 응답 메세지 반환
	 *
	 * @param e 예외
	 * @return 실패 응답 메세지
	 */
	public static HttpResponse ofFail(Exception e) {
		return ExceptionUtil.getLastExceptionOrNull(e, DefaultException.class)
				.map(a -> {
					String message = ExceptionUtil.getMessage(a);
//					String message = ExceptionUtil.getMessageWithoutArgs(a); // error
					return new HttpResponse(ExceptionUtil.getError(a).getCode(), message);
//					return new HttpResponse(ExceptionUtil.getError(a).name(), message); // error
				})
				.orElseGet(() -> ofFail(Error.DefaultError.NONE, e));
	}

	/**
	 * 실패 응답 메세지 반환
	 *
	 * @param message 성공 메세지
	 * @return 실패 응답 메세지
	 */
	public static HttpResponse ofFail(String message) {
		return new HttpResponse(Error.DefaultError.NONE.getCode(), message);
//		return new HttpResponse(Error.DefaultError.NONE.name(), message); // error
	}

	/**
	 * 실패 응답 메세지 반환
	 *
	 * @param error 에러
	 * @param e     예외
	 * @return 실패 응답 메세지
	 */
	private static HttpResponse ofFail(Error error, Exception e) {
		return new HttpResponse(error.getCode(), ExceptionUtil.getMessage(e));
//		return new HttpResponse(error.name(), ExceptionUtil.getMessage(e)); // error
	}

	@Schema(description = "에러 코드")
	protected String error;

	@Schema(description = "에러 메세지")
	protected String message;

	@Override
	public String toString() {
		return StringUtil.toCodeString(error, message);
	}
}

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

import org.oh.common.controller.ControllerExceptionHandler;
import org.oh.common.exception.DefaultException;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 필터 응답 메세지
 */
@Slf4j
@Component
public class FilterResponse {
	/**
	 * 예외에 대한 응답 메세지 출력
	 *
	 * @param e        예외
	 * @param request  HTTP 요청 정보
	 * @param response HTTP 응답 정보
	 */
	public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
//		try {
//			ThreadLocalUtil.get(FILTER_EXCEPTION, CommonException.class)
//					.ifPresent(a -> HttpResponse.ofFail(e));
//		} finally {
//			ThreadLocalUtil.remove(FILTER_EXCEPTION);
//		}
		DefaultException de = ControllerExceptionHandler.ofDefaultException(e, null);
		HttpStatus status = de.getError().getHttpStatus();
		HttpResponse httpResponse = HttpResponse.ofFail(de);
		log.error("request {} body: {} message: {}, response: {} status: {}",
				WebUtil.getRequestInfo(request), WebUtil.getBody(request),
				ExceptionUtil.getMessageAndType(de), httpResponse, status, de);

		write(status.value(), JsonUtil.toString(httpResponse), response);
	}

	/**
	 * 예외에 대한 응답 메세지 출력
	 *
	 * @param status   HTTP 상태 정보
	 * @param body     응답 메세지
	 * @param response HTTP 응답 정보
	 */
	protected void write(int status, String body, HttpServletResponse response)
			throws IOException {
		response.setStatus(status);
		response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		PrintWriter writer = response.getWriter();
		writer.write(body); //NOSONAR REST API 응답 데이터로 html 코드가 아님
//		writer.flush();
	}
}

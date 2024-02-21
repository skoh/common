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

package org.oh.common.controller;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.exception.DefaultException;
import org.oh.common.exception.Error;
import org.oh.common.model.http.HttpResponse;
import org.oh.common.model.http.Response;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.Logging;
import org.oh.common.util.WebUtil;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import java.util.Optional;

/**
 * 기본 컨트롤러 예외 핸들러
 * <pre>
 * - @ControllerAdvice를 사용하지 않을 경우
 *   {@link DefaultHandlerExceptionResolver}가 기본으로 사용
 * </pre>
 */
@Slf4j
@Order(100)
@ControllerAdvice
public class ControllerExceptionHandler
		extends ResponseEntityExceptionHandler {
	/**
	 * 일반 예외와 HTTP 상태 정보에 맞는 기본 예외로 변환
	 *
	 * @param e      일반 예외
	 * @param status HTTP 상태 정보
	 * @return 기본 예외
	 */
	public static DefaultException ofDefaultException(Exception e, HttpStatus status) {
		// HTTP Status Code
		if (e instanceof DefaultException) {
			return (DefaultException) e;
			// 400
		} else if (e instanceof ConstraintViolationException
				|| e instanceof TypeMismatchException
				|| e instanceof HttpMessageConversionException
				|| e instanceof MethodArgumentNotValidException) {
			return new CommonException(CommonError.COM_INVALID_ARGUMENT, e);
			// 401
		} else if (e instanceof AuthenticationException) {
			if (e instanceof InsufficientAuthenticationException) {
				return new CommonException(CommonError.COM_NO_VALID_TOKEN, e);
			} else if (e instanceof UsernameNotFoundException
					|| e instanceof BadCredentialsException) {
				return new CommonException(CommonError.COM_FAILED_CREDENTIALS, e);
			} else {
				return new CommonException(CommonError.COM_UNAUTHORIZED, e);
			}
			// 403
		} else if (e instanceof AccessDeniedException) {
			return new CommonException(CommonError.COM_FORBIDDEN, e);
			// 404
		} else if (e instanceof EmptyResultDataAccessException) {
			return new CommonException(CommonError.COM_NOT_FOUND, e);
			// 500
		} else if (e instanceof DataAccessException) {
			return new CommonException(CommonError.COM_DB_ERROR, e);
		} else {
			return Optional.ofNullable(status)
					.map(a -> new CommonException(Error.CustomError.createError(Error.DefaultError.NONE, a), e))
					.orElseGet(() -> new CommonException(e));
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Spring 예외 핻들링
	 *
	 * @param ex      일반 예외
	 * @param body    HTTP 바디 정보
	 * @param headers HTTP 헤더 정보
	 * @param status  HTTP 상태 정보
	 * @param request 웹 요청 정보
	 * @return HTTP 응답 정보
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
															 HttpStatus status, WebRequest request) {
		HttpServletRequest req = ((ServletWebRequest) request).getRequest();
		DefaultException de = ofDefaultException(ex, status);
		return (ResponseEntity) ofResponseEntity(de, req);
	}

	/**
	 * 클라이언트가 요청하는 미디어 타입(HTML 또는 JSON)에 맞게 응답
	 *
	 * @param e       일반 예외
	 * @param request 웹 요청 정보
	 * @return HTTP 응답 정보
	 */
	@ResultLogging(result = true, indexesOfArgs = {0, 1})
	protected Object ofResponse(Exception e, WebRequest request) {
		HttpServletRequest req = ((ServletWebRequest) request).getRequest();
		DefaultException de = ofDefaultException(e, null);
		if (WebUtil.isCompatibleWith(req, HttpHeaders.ACCEPT, MediaType.TEXT_HTML)) {
			Optional<DefaultException> le = ExceptionUtil.getLastExceptionOrNull(de, DefaultException.class);
			Error error = ExceptionUtil.getError(le.orElse(null));
			return ImmutableMap.of(
					DefaultErrorController.ERROR_REQUEST_PREFIX_NAME + ".status", error.getHttpStatus(),
					DefaultErrorController.ERROR_REQUEST_PREFIX_NAME + ".error", error.getCode());
		} else {
			return ofResponseEntity(de, req);
		}
	}

	/**
	 * 기본 예외를 로그에 기록하고 에러 메세지를 응답
	 *
	 * @param e       기본 예외
	 * @param request HTTP 요청 정보
	 * @return HTTP 응답 정보
	 */
	protected ResponseEntity<Response> ofResponseEntity(DefaultException e, HttpServletRequest request) {
		String message = String.format("request %s body: %s message: %s error: %s",
				WebUtil.getRequestInfo(request), WebUtil.getBody(request),
				ExceptionUtil.getMessageAndType(e), e.getError().ofString());
		getLogging(e).log(message, e);

		Response response = HttpResponse.ofFail(e);
		return ResponseEntity.status(e.getError().getHttpStatus())
				.body(response);
	}

	/**
	 * 기본 예외에 맞는 로깅 레벨을 반환
	 *
	 * @param e 예외
	 * @return 로깅 레벨
	 */
	protected Logging getLogging(Exception e) {
		if (ExceptionUtils.indexOfType(e, ClientAbortException.class) > -1) {
			return log::warn;
		} else {
			return log::error;
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 일반 예외 핸들링
	 *
	 * @param e       일반 예외
	 * @param request 웹 요청 정보
	 * @return HTTP 응답 정보
	 */
	@ExceptionHandler
	private Object handleException2(Exception e, WebRequest request) {
		return ofResponse(e, request);
	}
}

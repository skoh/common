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

import org.oh.common.exception.CommonException;
import org.oh.common.filter.ExceptionHandlerFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 웹 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class WebUtil {
	@Getter
	@Setter
	private static String templatesName = "templates/";

	/**
	 * 객체를 MultiValueMap 형태의 데이터롤 변환
	 *
	 * @param obj 대상 객체
	 * @return MultiValueMap 형태의 데이터
	 */
	public static MultiValueMap<String, String> getParams(Object obj) {
		Map<String, String> map = JsonUtil.OBJECT_MAPPER.convertValue(obj, new TypeReference<Map<String, String>>() {
		});
		return getParams(map);
	}

	/**
	 * 맵 정보를 MultiValueMap 형태의 데이터롤 변환
	 *
	 * @param map 맵 정보
	 * @return MultiValueMap 형태의 데이터
	 */
	public static MultiValueMap<String, String> getParams(Map<String, String> map) {
		LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.setAll(map);
		return params;
	}

	/**
	 * URI 정보를 반환
	 *
	 * @param obj 파라미터 객체
	 * @return URI 정보
	 */
	public static UriComponents getUri(Object obj) {
		return getUri(null, obj);
	}

	/**
	 * URI 정보를 반환
	 *
	 * @param map 파라미터 맵
	 * @return URI 정보
	 */
	public static UriComponents getUri(Map<String, String> map) {
		return getUri(null, map);
	}

	/**
	 * URI 정보를 반환
	 *
	 * @param query 쿼리 문자열
	 * @param obj   파라미터 객체
	 * @return URI 정보
	 */
	public static UriComponents getUri(String query, Object obj) {
		MultiValueMap<String, String> params = getParams(obj);
		return getUri(query, params);
	}

	/**
	 * URI 정보를 반환
	 *
	 * @param query 쿼리 문자열
	 * @param map   파라미터 맵
	 * @return URI 정보
	 */
	public static UriComponents getUri(String query, Map<String, String> map) {
		MultiValueMap<String, String> params = getParams(map);
		return getUri(query, params);
	}

	/**
	 * URI 정보를 반환
	 *
	 * @param query  쿼리 문자열
	 * @param params 파라미터 멀티맵
	 * @return URI 정보
	 */
	private static UriComponents getUri(String query, MultiValueMap<String, String> params) {
		return UriComponentsBuilder.newInstance()
				.query(query)
				.queryParams(params)
				.build();
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 엔코드된 파일명을 반환
	 *
	 * @param fileName  파일명
	 * @param userAgent 사용자 에이전트
	 * @return 엔코드된 파일명
	 */
	public static String getEncodedFileName(String fileName, String userAgent) {
		String fileNameTemp;
		try {
			if (userAgent != null && userAgent.contains("MSIE")) {
				fileNameTemp = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
			} else {
				fileNameTemp = new String(fileName.getBytes(StandardCharsets.UTF_8.toString()),
						StandardCharsets.ISO_8859_1.toString());
			}
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(e);
		}
		return fileNameTemp;
	}

	/**
	 * HTTP 헤더에 있는 정보가 해당 미디어 타입에 호환이 되는지 여부
	 *
	 * @param request    HTTP 요청 정보
	 * @param httpHeader HTTP 헤더 정보
	 * @param mediaType  미디어 타입
	 * @return 호환 여부
	 */
	public static boolean isCompatibleWith(HttpServletRequest request, String httpHeader, MediaType mediaType) {
		List<MediaType> mediaTypes = MediaType.parseMediaTypes(request.getHeader(httpHeader));
		log.debug("httpHeader: {} mediaTypes: {}", httpHeader, mediaTypes);
		return mediaTypes.stream()
				.anyMatch(e -> !MediaType.ALL.equals(e) && mediaType.isCompatibleWith(e));
	}

	/**
	 * HTTP 요청자의 IP 정보를 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return IP 정보
	 */
	public static String getProxyRemoteAddr(HttpServletRequest request) {
		return StringUtils.firstNonBlank(request.getHeader("x-forwarded-for"), request.getRemoteAddr());
	}

	/**
	 * HTTP 요청자의 IP와 URI 정보을 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return IP와 URI 정보
	 */
	public static String getRequestInfo(HttpServletRequest request) {
		return String.format("ip: %s uri: %s %s%s", getProxyRemoteAddr(request),
				request.getMethod(), request.getRequestURI(),
				Optional.ofNullable(request.getQueryString())
						.map(a -> "?" + a)
						.orElse(""));
	}

	/**
	 * HTTP 요청 정보에서 요청 내용을 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return 요청 내용
	 * @see ExceptionHandlerFilter
	 */
	public static String getBody(HttpServletRequest request) {
		String body = null;
		if (request instanceof ServletRequestWrapper) {
			ServletRequestWrapper wrapperRequest = (ServletRequestWrapper) request;

			ContentCachingRequestWrapper cachingRequest = null;
			if (wrapperRequest instanceof ContentCachingRequestWrapper) {
				cachingRequest = (ContentCachingRequestWrapper) wrapperRequest;
			} else if (wrapperRequest.getRequest() instanceof ContentCachingRequestWrapper) {
				cachingRequest = (ContentCachingRequestWrapper) wrapperRequest.getRequest();
			}

			if (cachingRequest != null) {
				body = IOUtils.toString(cachingRequest.getContentAsByteArray(),
						StandardCharsets.UTF_8.toString());
			}
		}
		return body;
	}

	/**
	 * 서버의 접속 정보를 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return 서버의 접속 정보
	 */
	public static String getServerInfo(HttpServletRequest request) {
		return String.format("%s:%s", request.getServerName(), request.getServerPort());
	}
}

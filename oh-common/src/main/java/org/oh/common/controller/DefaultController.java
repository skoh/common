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

import org.oh.common.util.SpringUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import java.util.Optional;

//@CrossOrigin(maxAge = 3600)
public interface DefaultController {
	/**
	 * api version
	 */
	String VERSION_1 = "/v1";

	/**
	 * JSON 데이터의 파라미터명
	 */
	String JSON_NAME = "json";

	/**
	 * 팝업 페이지 경로
	 */
	String ALERT_PAGE = "alert";

	/**
	 * 로그인 페이지 경로
	 */
	String LOGIN_PAGE = "login";

	/**
	 * 로그인 사용자의 세션키
	 */
	String KEY_LOGIN_USER = "loginUser";

	/**
	 * 해당 HTTP 요청 정보에서 페이지 경로를 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return 페이지 경로
	 */
	static String getPage(HttpServletRequest request) {
		String[] paths = request.getRequestURI().split("/");
		return paths[paths.length - 1];
	}

	/**
	 * 해당 조건으로 파일을 다운로드
	 *
	 * @param resource  리소스 종류
	 * @param mediaType 미디어 타입
	 * @param fileName  파일명
	 * @param fileSize  파일 크기
	 * @return HTTP 응답 정보
	 */
	static ResponseEntity<Resource> getResponseEntity(Resource resource, MediaType mediaType,
													  String fileName, long fileSize) {
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileName)
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize))
				.header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
				.body(resource);
	}

	/**
	 * 로그인 성공 여부를 반환
	 */
	static boolean isLoginSuccess() {
		Optional<Object> user = SpringUtil.getAttributeOfSession(KEY_LOGIN_USER);
		return user.isPresent();
	}
}

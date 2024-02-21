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

import org.oh.common.annotation.ResultLogging;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.AbstractFiles;
import org.oh.common.service.file.AbstractFilesDbService;
import org.oh.common.util.WebUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.Optional;

/**
 * 기본 파일 컨트롤러
 */
public abstract class AbstractFilesRestController<T extends AbstractFiles>
		extends AbstractCrudDbController<T, Long> {
	public static final String PATH = "/v1/" + AbstractFiles.NAME_SPACE;

	protected AbstractFilesRestController(AbstractFilesDbService<T> service) {
		super(service);
	}

	/**
	 * 해당 파일을 미리보기
	 *
	 * @param id       아이디
	 * @param sizeType 이미지 크기 종류
	 * @param request  HTTP 요청 정보
	 * @return HTTP 응답 정보
	 */
	@ResponseBody
	@Operation(summary = "파일 미리보기")
	@ResultLogging
	@GetMapping({"view/{id}", "view/{id}/{sizeType}"})
	public ResponseEntity<Resource> view(@Parameter(description = "아이디")
										 @PathVariable Long id,
										 @Parameter(description = "크기 종류")
										 @PathVariable(required = false)
										 AbstractFiles.SizeType sizeType,
										 HttpServletRequest request) {
		return getResponseEntity(id, sizeType, false, request);
	}

	/**
	 * 해당 파일을 다운로드
	 *
	 * @param id       아이디
	 * @param sizeType 이미지 크기 종류
	 * @param request  HTTP 요청 정보
	 * @return HTTP 응답 정보
	 */
	@ResponseBody
	@Operation(summary = "파일 다운로드")
	@ResultLogging
	@GetMapping({"down/{id}", "down/{id}/{sizeType}"})
	public ResponseEntity<Resource> down(@Parameter(description = "아이디")
										 @PathVariable Long id,
										 @Parameter(description = "크기 종류")
										 @PathVariable(required = false)
										 AbstractFiles.SizeType sizeType,
										 HttpServletRequest request) {
		return getResponseEntity(id, sizeType, true, request);
	}

	/**
	 * 해당 크기의 파일을 미리보기 또는 다운로드
	 *
	 * @param id       아이디
	 * @param sizeType 이미지 크기 종류
	 * @param download 다운로드 여부 (true: 다운로드, false: 미리보기)
	 * @param request  HTTP 요청 정보
	 * @return HTTP 응답 정보
	 */
	protected ResponseEntity<Resource> getResponseEntity(Long id, AbstractFiles.SizeType sizeType,
														 boolean download, HttpServletRequest request) {
		T entity = findById(id);

		AbstractFiles.SizeType sizeTypeTemp = Optional.ofNullable(sizeType)
				.orElse(AbstractFiles.SizeType.ORIGIN);
		File file = sizeTypeTemp == AbstractFiles.SizeType.ORIGIN ? entity.getOriginAttach().getFile() :
				entity.getThumbAttach().getFile();
		if (!file.exists()) {
			throw new CommonException(CommonError.COM_NOT_FOUND, "file: " + file.getAbsolutePath());
		}

		String fileNameTemp = sizeTypeTemp == AbstractFiles.SizeType.ORIGIN ? entity.getName() :
				entity.getThumbAttach().getFile().getName();
		final String fileName = WebUtil.getEncodedFileName(fileNameTemp, request.getHeader("User-Agent"));
		MediaType mediaType = download ? MediaType.APPLICATION_OCTET_STREAM :
				MediaTypeFactory.getMediaType(fileName)
						.orElseThrow(() ->
								new CommonException(CommonError.COM_NOT_SUPPORTED, "fileName: " + fileName));

		return getResponseEntity(entity, file, fileName, mediaType);
	}

	/**
	 * 해당 조건의 파일을 미리보기 또는 다운로드
	 *
	 * @param entity    파일 객체
	 * @param file      실제 파일
	 * @param fileName  파일명
	 * @param mediaType 미디어 타입
	 * @return HTTP 응답 정보
	 */
	protected ResponseEntity<Resource> getResponseEntity(T entity, File file, String fileName, MediaType mediaType) {
		return DefaultController.getResponseEntity(new FileSystemResource(file), mediaType, fileName, file.length());
	}
}

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

package org.oh.sample.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.controller.AbstractFilesRestController;
import org.oh.sample.model.Files;
import org.oh.sample.service.FilesDbService;
import org.oh.sample.service.SampleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 DB 컨트롤러
 */
@Tag(name = "파일", description = "파일을 관리하는 매퍼 API")
@Slf4j
@Controller
@RequestMapping(FilesDbRestController.PATH)
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class FilesDbRestController
		extends AbstractFilesRestController<Files> {
	public static final String PATH = VERSION_1 + "/files";

	protected final FilesDbService filesDbService;

	protected FilesDbRestController(FilesDbService filesDbService) {
		super(filesDbService);
		this.filesDbService = filesDbService;
	}

	/**
	 * 업로드된 파일들 DB와 시스템에 저장
	 *
	 * @param file 업로드 파일들
	 */
	@ResponseBody
	@Operation(summary = "파일 업로드")
	@ResultLogging(args = false)
	@PostMapping("upload")
	public void upload(@Parameter(description = "파일") MultipartFile file) {
		LinkedMultiValueMap<String, MultipartFile> filesMap = new LinkedMultiValueMap<>();
//		for (MultipartFile file : files) {
		filesMap.add(file.getName(), file);
//		}
		filesDbService.create(filesMap);
	}
}

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

package org.oh.sample.service;

import org.oh.common.service.file.AbstractFilesDbService;
import org.oh.sample.model.Files;
import org.oh.sample.repository.FilesDbRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 파일 DB 서비스
 */
@Service
@ConditionalOnProperty(value = "enabled", prefix = SampleService.PROPERTY_PREFIX, havingValue = "true")
public class FilesDbService
		extends AbstractFilesDbService<Files> {
	protected final FilesDbService self;
	protected final FilesService filesService;

	protected FilesDbService(@Lazy FilesDbService self,
							 FilesDbRepository repository,
							 FilesService filesService) {
		super(self, repository);
		this.self = self;
		this.filesService = filesService;
	}

	/**
	 * 업로드된 파일들 DB와 시스템에 저장
	 *
	 * @param files 업로드 파일들
	 * @return 저장된 파일 정보들
	 */
	public List<Files> create(MultiValueMap<String, MultipartFile> files) {
		List<Files> result = new ArrayList<>();
		if (files == null) {
			return result;
		}

		File filePath = self.getPath();
		for (String type : files.keySet()) {
			for (MultipartFile file : files.get(type)) {
				if (file.isEmpty()) {
					continue;
				}

				Files filesTemp = Files.builder()
						.path(filePath.getPath())
						.name(file.getOriginalFilename())
						.size(file.getSize())
						.build();
				Files filesTemp2 = self.create(filePath, file, filesTemp);
				insert(filesTemp2);
				filesService.insert(filesTemp2);
			}
		}
		return result;
	}
}

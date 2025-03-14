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

package org.oh.common.service.file;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.config.CommonConfig;
import org.oh.common.config.DataGridConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.AbstractFiles;
import org.oh.common.repository.CrudRepository;
import org.oh.common.service.AbstractCrudService;
import org.oh.common.util.FileUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

/**
 * 공통 파일 서비스
 */
@Slf4j
@Setter
@Validated
@ConfigurationProperties(AbstractFilesService.PROPERTY_NAME)
public abstract class AbstractFilesService<T extends AbstractFiles>
		extends AbstractCrudService<T, Long>
		implements EntryAddedListener<Long, T> {
	public static final String PROPERTY_NAME = CommonConfig.COMMON_PREFIX + ".files";

	protected boolean copyToCluster;

	protected AbstractFilesService(AbstractFilesService<T> self,
								   CrudRepository<T, Long> repository,
								   HazelcastInstance hazelcast) {
		super(self, repository);
		IMap<Long, Files> iMap = hazelcast.getMap(DataGridConfig.MAP_NAME_FILES);
		iMap.addEntryListener(this, true);
	}

	@Override
	@ResultLogging
	public void entryAdded(EntryEvent<Long, T> event) {
		if (!copyToCluster) {
			return;
		}

		T files = event.getValue();
		File originFile = files.getOriginAttach().getFile();
		File thumbFile = Optional.ofNullable(files.getThumbAttach())
				.map(AbstractFiles.Attachment::getFile)
				.orElse(null);
		try {
			Files.createDirectories(FileUtil.getPaths(files.getPath()));
			if (!originFile.exists()) {
				log.debug("originFile: {}", originFile.getAbsolutePath());
				Files.write(FileUtil.getPaths(originFile.getAbsolutePath() + "!"), files.getOriginAttach().getBytes());
			}
			if (thumbFile != null && !thumbFile.exists()) {
				log.debug("thumbFile: {}", thumbFile.getAbsolutePath());
				Files.write(FileUtil.getPaths(thumbFile.getAbsolutePath()), files.getThumbAttach().getBytes());
			}
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "file: " + originFile.getAbsolutePath(), e);
		}
	}
}

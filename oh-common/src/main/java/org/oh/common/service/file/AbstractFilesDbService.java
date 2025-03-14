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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.config.DataGridConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.AbstractFiles;
import org.oh.common.repository.CrudDbRepository;
import org.oh.common.service.AbstractCrudDbService;
import org.oh.common.util.DateUtil;
import org.oh.common.util.FileUtil;
import org.oh.common.util.ImageUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 공통 파일 DB 서비스
 */
@Slf4j
@Setter
@CacheConfig(cacheNames = DataGridConfig.MAP_NAME_CACHE + '.' + AbstractFiles.NAME_SPACE)
@Validated
@ConfigurationProperties(AbstractFilesService.PROPERTY_NAME)
public abstract class AbstractFilesDbService<T extends AbstractFiles>
		extends AbstractCrudDbService<T, Long> {
	public static final String PROPERTY_NAME_ROOT_PATH = AbstractFilesService.PROPERTY_NAME + ".rootPath";

	protected String rootPath;
	protected boolean thumbnail;

	protected File rootFile;

	protected final AbstractFilesDbService<T> self;

	protected AbstractFilesDbService(AbstractFilesDbService<T> self,
									 CrudDbRepository<T, Long> repository) {
		super(self, repository);
		this.self = self;
	}

	/**
	 * 최상위 파일 경로를 생성
	 *
	 * @param rootPath 최상위 파일 경로
	 * @return 파일 경로
	 * @throws IOException
	 */
	public static File createPath(String rootPath) throws IOException {
		File rootFile = FileUtil.createFile(rootPath);
		if (!rootFile.exists()) {
			log.info(PROPERTY_NAME_ROOT_PATH + ": {}", rootFile.getAbsolutePath());
			Files.createDirectories(FileUtil.getPaths(rootFile.getAbsolutePath()));
		}
		return rootFile;
	}

	/**
	 * 해당 이미지 파일로 썸네일 이미지를 생성
	 *
	 * @param imageFile 이미지 파일
	 * @return 첨부 파일
	 * @throws IOException
	 */
	protected static AbstractFiles.Attachment createThumb(File imageFile) throws IOException {
		String filePathName = FilenameUtils.getBaseName(imageFile.getName()) + AbstractFiles.SizeType.SMALL.getValue()
				+ '.' + ImageUtil.THUMB_EXTENSION;
		File thumbFile = FileUtil.createFile(filePathName);
		AbstractFiles.Attachment thumbAttach = AbstractFiles.Attachment.builder()
				.file(thumbFile)
				.build();

		ImageUtil.createScalrThumb(imageFile, thumbFile, ImageUtil.THUMB_WIDTH, ImageUtil.RATIO);
		log.debug("thumbFile: {}", thumbFile.getAbsolutePath());
		thumbAttach.setBytes(java.nio.file.Files.readAllBytes(thumbFile.toPath()));

		return thumbAttach;
	}

	@PostConstruct
	private void init() throws IOException {
		if (rootPath != null) {
			rootFile = createPath(rootPath);
		}
	}

	@Override
	public T findById(Long id) {
		T files = super.findById(id);
		return setAttach(files);
	}

	@Override
	public List<T> findAll(T entity) {
		List<T> files = super.findAll(entity);
		return files.stream()
				.map(this::setAttach)
				.collect(Collectors.toList());
	}

	@Override
//	@ResultLogging
	@Transactional
	public void deleteAll(Collection<T> entities) {
		Collection<T> entitiesTemp = entities.stream()
				.map(e -> self.findById(e.getId()))
				.collect(Collectors.toList());
		super.deleteAll(entitiesTemp);
		deleteFile(entitiesTemp);
	}

	@Override
//	@ResultLogging
	@Transactional
	public void deleteById(Long id) {
		T entity = self.findById(id);
		super.deleteById(id);
		deleteFile(entity);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 현재 일자를 기준으로 디렉토리 구조를 반환
	 *
	 * @return 디렉토리 구조 (yyyy/MM)
	 */
	@ResultLogging(result = true)
	protected File getPath() {
		String yyyyMMdd = DateUtil.formatCurrentDate();
		File path = FileUtil.getPath(rootFile, yyyyMMdd.substring(0, 4)); // yyyy
		return FileUtil.getPath(path, yyyyMMdd.substring(4, 6)); // MM
	}

	/**
	 * 해당 디렉토리 하위에 파일과 썸네일 이미지(선택)를 저장
	 *
	 * @param path  대상 디렉토리
	 * @param file  업로드 파일
	 * @param files 파일 정보
	 * @return 저장된 파일 정보
	 */
	@ResultLogging(result = true)
	protected T create(File path, MultipartFile file, T files) {
		T filesTemp = self.insert(files);
		String filePath = path.getAbsolutePath() + "/" + filesTemp.getId();
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());

		File originFile = FileUtil.createFile(filePath + '.' + extension);
		AbstractFiles.Attachment originAttach = AbstractFiles.Attachment.builder()
				.file(originFile)
				.build();

		try {
			file.transferTo(originFile);
			log.debug("originFile: {}", originFile.getAbsolutePath());
			originAttach.setBytes(java.nio.file.Files.readAllBytes(originFile.toPath()));

			if (thumbnail && files.createThumbnail()) {
				AbstractFiles.Attachment thumbAttach = createThumb(originFile);
				filesTemp.setThumbAttach(thumbAttach);
			}
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "file: " + originFile.getAbsolutePath(), e);
		}
		filesTemp.setOriginAttach(originAttach);
		return filesTemp;
	}

	/**
	 * 해당 파일 정보에 원본과 썸네일 이미지 파일을 설정
	 *
	 * @param files 파일 정보
	 * @return 파일 정보
	 */
	private T setAttach(T files) {
		AbstractFiles.Attachment originAttach = getAttachment(files, AbstractFiles.SizeType.ORIGIN);
		files.setOriginAttach(originAttach);

		AbstractFiles.Attachment thumbAttach = getAttachment(files, AbstractFiles.SizeType.SMALL);
		files.setThumbAttach(thumbAttach);

		return files;
	}

	/**
	 * 해당 파일 정보에 크기 종류에 따른 첨부파일을 반환
	 *
	 * @param files 파일 정보
	 * @param type  크기 종류
	 * @return 첨부파일
	 */
	private AbstractFiles.Attachment getAttachment(T files, AbstractFiles.SizeType type) {
		String extension = type == AbstractFiles.SizeType.ORIGIN ? FilenameUtils.getExtension(files.getName()) :
				ImageUtil.THUMB_EXTENSION;
		File file = FileUtil.createFile(files.getPath() +
				"/" + files.getId() + type.getValue() + '.' + extension);
		return AbstractFiles.Attachment.builder()
				.file(file)
				.build();
	}

	/**
	 * 파일 정보 들로 원본과 썸네일 이미지 파일을 삭제
	 *
	 * @param entities 파일 정보들
	 */
	private void deleteFile(Collection<T> entities) {
		entities.forEach(this::deleteFile);
	}

	/**
	 * 파일 정보로 원본과 썸네일 이미지 파일을 삭제
	 *
	 * @param entity 파일 정보
	 */
	private void deleteFile(T entity) {
		Optional.ofNullable(entity.getOriginAttach())
				.map(AbstractFiles.Attachment::getFile)
				.ifPresent(File::delete);
		Optional.ofNullable(entity.getThumbAttach())
				.map(AbstractFiles.Attachment::getFile)
				.ifPresent(File::delete);
	}
}

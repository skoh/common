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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

/**
 * 파일 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FileUtil {
	// 정규화 예외 파일 경로 (로컬 테스트용)
	private static final String[] NORMALIZE_EXCLUDE_STARTS_WITH_PATHS = {
			"../files-test"
	};

	/**
	 * 부모 경로 하위에 해당 경로를 반환 (없으면 생성)
	 *
	 * @param parentPath 부모 경로
	 * @param path       경로
	 * @return 파일 정보
	 */
	public static File getPath(File parentPath, String path) {
		File pathTemp = createFile(parentPath.getAbsolutePath() + (StringUtils.isEmpty(path) ? "" : "/" + path));
		if (!pathTemp.exists() && !pathTemp.mkdir()) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "file: " + pathTemp.getAbsolutePath());
		}
		return pathTemp;
	}

	public static void write(File file, byte[] data) {
		write(file, data, false);
	}

	/**
	 * 해당 파일에 데이터를 추가하여 생성
	 *
	 * @param file   파일
	 * @param data   데이터
	 * @param append 추가 여부
	 */
	public static void write(File file, byte[] data, boolean append) {
		try {
			FileUtils.writeByteArrayToFile(file, data, append);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR,
					String.format("file: %s data size: %d", file.getAbsolutePath(), data.length), e);
		}
	}

	/**
	 * 해당 파일에 데이터를 덮어써서 생성
	 *
	 * @param file 파일
	 * @param data 데이터
	 */
	public static void write(File file, String data) {
		write(file, data, false);
	}

	/**
	 * 해당 파일에 문자열을 추가하여 생성
	 *
	 * @param file   파일
	 * @param data   문자열
	 * @param append 추가 여부
	 */
	public static void write(File file, String data, boolean append) {
		try {
			FileUtils.write(file, data, StandardCharsets.UTF_8, append);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR,
					String.format("file: %s data size: %d", file.getAbsolutePath(), data.length()), e);
		}
	}

	/**
	 * 파일을 읽어 데이터를 반환
	 *
	 * @param file 파일
	 * @return 데이터
	 */
	public static byte[] read(File file) {
		try {
			return FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * 파일을 읽어 문자열을 반환
	 *
	 * @param file 파일
	 * @return 문자열
	 */
	public static String readString(File file) {
		try {
			return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "file: " + file.getAbsolutePath(), e);
		}
	}

	/**
	 * 파일을 복사
	 */
	public static void copy(String sSrcPath, String sDestPath, String fileName) {
		File srcFile = createFile(sSrcPath + '/' + fileName);
		File destFile = createFile(sDestPath + '/' + fileName);
		try {
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR,
					String.format("srcFile: %s destFile: %s",
							srcFile.getAbsolutePath(), destFile.getAbsolutePath()), e);
		}
	}

	/**
	 * 해당 디렉토리의 하위 디렉토리까지 삭제
	 *
	 * @param dir 디렉토리
	 */
	public static void delete(File dir) {
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			throw new CommonException(CommonError.COM_FILES_ERROR, "dir: " + dir.getAbsolutePath());
		}
	}

	/**
	 * 와일드 카드에 해당하는 파일을 모두 예외 없이 삭제
	 *
	 * @param targetDir        대상 디렉토리
	 * @param wildCardFileName 와일드 카드를 사용한 파일명
	 */
	public static void deleteQuietly(File targetDir, String wildCardFileName) {
		File[] files = listFiles(targetDir, wildCardFileName);
		if (files != null) {
			for (File file : files) {
				FileUtils.deleteQuietly(file);
			}
		}
	}

	/**
	 * 와일드 카드에 해당하는 파일을 모두 삭제
	 *
	 * @param targetDir        대상 디렉토리
	 * @param wildCardFileName 와일드 카드를 사용한 파일명
	 */
	public static void delete(File targetDir, String wildCardFileName) {
		File[] files = listFiles(targetDir, wildCardFileName);
		if (files != null) {
			for (File file : files) {
				try {
					FileUtils.delete(file);
				} catch (IOException e) {
					throw new CommonException(CommonError.COM_FILES_ERROR,
							"Can't delete file: " + file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * 와일드 카드에 해당하는 파일을 조회
	 *
	 * @param targetDir        대상 디렉토리
	 * @param wildCardFileName 와일드 카드를 사용한 파일명
	 */
	public static File[] listFiles(File targetDir, String wildCardFileName) {
		FileFilter fileFilter = WildcardFileFilter.builder()
				.setWildcards(wildCardFileName)
				.get();
		return targetDir.listFiles(fileFilter);
	}

	/**
	 * 정규화 한 파일을 생성
	 *
	 * @param filePath 파일 경로
	 * @return 파일
	 */
	public static File createFile(String filePath) {
		return new File(getFilePath(filePath)); //NOSONAR 파일 경로 정규화
	}

	/**
	 * 정규화 한 경로를 생성
	 *
	 * @param filePath 파일 경로
	 * @return 경로
	 */
	public static Path getPaths(String filePath) {
		return Paths.get(getFilePath(filePath)); //NOSONAR 파일 경로 정규화
	}

	/**
	 * 보안을 위해 파일 경로를 정규화하여 반환
	 *
	 * @param filePath 파일 경로
	 * @return 정규화 한 파일 경로 (보안에 위배되면 null을 반환)
	 */
	private static String getFilePath(String filePath) {
		return Arrays.stream(NORMALIZE_EXCLUDE_STARTS_WITH_PATHS)
				.filter(filePath::startsWith)
				.findFirst()
				.map(a -> {
					try {
						return new File(filePath).getCanonicalPath(); //NOSONAR 파일 경로 정규화
					} catch (IOException e) {
						throw new CommonException(CommonError.COM_FILES_ERROR, "filePath: " + filePath);
					}
				})
				.orElseGet(() ->
						Optional.ofNullable(FilenameUtils.normalize(filePath))
								.orElseThrow(() ->
										new CommonException(CommonError.COM_SECURITY_ERROR, "filePath: " + filePath))
				);
	}
}

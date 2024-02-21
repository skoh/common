package org.oh.common.util;

import org.oh.common.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

//@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FileUtilTest {
	@Test
	void t01createFile() {
		createFile("../files-test");
		createFile(".");
		createFile("./files");
		createFile("./files/didDoc.json");
		createFile("C:/workspace/bct/common/files");
		createFile("C:/workspace/bct/common/files/didDoc.json");
	}

	@Test
	void t01createFileThrows() {
		Assertions.assertThrows(CommonException.class, () -> createFile("../files"));
	}

	private void createFile(String filePath) {
		File file = FileUtil.createFile(filePath);
		Assertions.assertNotNull(file);
		log.debug(file.getAbsolutePath());
	}
}

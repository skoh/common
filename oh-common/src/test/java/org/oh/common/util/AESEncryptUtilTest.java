package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;

@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AESEncryptUtilTest {
	public static final String TEXT = "test";
	public static final String PATH = "./files/";
	public static final String FILE_NAME = "테스트.png";
	//	public static final String FILE_NAME = "테스트.pdf";
	public static final String BASE_NAME = FilenameUtils.getBaseName(FILE_NAME);
	public static final String EXTENSION = FilenameUtils.getExtension(FILE_NAME);
	public static final File FILE = new File(PATH + FILE_NAME);

	public static String encryptText;
	public static File encryptFile = new File(PATH + BASE_NAME + "_encrypt." + EXTENSION);

	@Test
	void t01encrypt() {
		encryptText = AESEncryptUtil.encrypt(TEXT);
		log.debug("result: {}", encryptText);
		Assertions.assertNotEquals(TEXT, encryptText);
	}

	@Test
	void t02decrypt() {
		encryptText = AESEncryptUtil.decrypt(encryptText);
		log.debug("result: {}", encryptText);
		Assertions.assertEquals(TEXT, encryptText);
	}

//	@Test
	void t03encryptFile() {
		AESEncryptUtil.encrypt(FILE, encryptFile);
	}

//	@Test
	void t04decryptFile() {
		AESEncryptUtil.decrypt(encryptFile, new File(PATH + BASE_NAME + "_decrypt." + EXTENSION));
	}
}

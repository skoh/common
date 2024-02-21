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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 암/복호화 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AESEncryptUtil {
	// 32 byte = 256 bit                   12345678901234567890123456789012
	public static final String PASSWORD = "12345678901234567890123456789012";
	public static final String SALT = "1234567890123456";//KeyGenerators.string().generateKey();
	private static final String KEY_REVERSE = new StringBuilder(PASSWORD.substring(0, 16)).reverse().toString();
	private static final byte[] KEY_BYTES = KEY_REVERSE.getBytes();
	//	private static final IvParameterSpec IV_SPEC = new IvParameterSpec(KEY_BYTES);
	private static final GCMParameterSpec IV_SPEC = new GCMParameterSpec(128, KEY_BYTES);
	private static final SecretKeySpec SECRET_KEY_SPEC = new SecretKeySpec(KEY_BYTES, "AES");
	private static final Cipher CIPHER;

	static {
//		log.debug("salt: {}", SALT);
		try {
//			CIPHER = Cipher.getInstance("AES/CBC/PKCS5Padding");
			CIPHER = Cipher.getInstance("AES/GCM/NoPadding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new CommonException(CommonError.COM_ENCRYPTION_ERROR, "Failed cipher instantiation", e);
		}
	}

	/**
	 * 한번만 문자열 암호화
	 *
	 * @param value 문자열
	 * @return 암호화된 문자열
	 */
	public static String encryptPerOnce(String value) {
		if (StringUtils.isNotEmpty(value) && !CommonUtil.SEPARATOR.equals(String.valueOf(value.charAt(0)))) {
			return CommonUtil.SEPARATOR + encrypt(value);
		}
		return value;
	}

	/**
	 * 문자열 암호화
	 *
	 * @param value 문자열
	 * @return 암호화된 문자열
	 */
	public static String encrypt(String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		byte[] bytes = encrypt(value.getBytes());
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * 파일 암호화
	 *
	 * @param src  원본 파일
	 * @param desc 암호화된 대상 파일
	 */
	public static void encrypt(File src, File desc) {
		if (!src.exists()) {
			throw new CommonException(CommonError.COM_NOT_FOUND, "file: " + src.getAbsolutePath());
		}

		byte[] bytes = FileUtil.read(src);
		bytes = encrypt(bytes);
		FileUtil.write(desc, bytes);
	}

	/**
	 * byte 배열을 AES256 알고리즘으로 암호화
	 *
	 * @param value byte 배열 암호화
	 * @return 암호화된 byte 배열 암호화
	 */
	public static synchronized byte[] encrypt(byte[] value) {
		if (value.length == 0) {
			return value;
		}

		try {
			CIPHER.init(Cipher.ENCRYPT_MODE, SECRET_KEY_SPEC, IV_SPEC);
			return CIPHER.doFinal(value);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException
				 | BadPaddingException | IllegalBlockSizeException e) {
			throw new CommonException(CommonError.COM_ENCRYPTION_ERROR, "length: " + value.length, e);
		}
	}

	/**
	 * 한번만 문자열 복호화
	 *
	 * @param value 문자열
	 * @return 암호화된 문자열
	 */
	public static String decryptPerOnce(String value) {
		if (StringUtils.isNotEmpty(value) && CommonUtil.SEPARATOR.equals(String.valueOf(value.charAt(0)))) {
			return decrypt(value.substring(1));
		}
		return value;
	}

	/**
	 * 문자열 복호화
	 *
	 * @param value 문자열
	 * @return 암호화된 문자열
	 */
	public static String decrypt(String value) {
		if (StringUtils.isEmpty(value)) {
			return value;
		}

		byte[] bytes = decrypt(value.getBytes(), true);
		return new String(bytes);
	}

	/**
	 * 파일 복호화
	 *
	 * @param src  원본 파일
	 * @param desc 복호화 대상 파일
	 */
	public static void decrypt(File src, File desc) {
		byte[] bytes = decrypt(src);
		FileUtil.write(desc, bytes);
	}

	/**
	 * 파일 복호화
	 *
	 * @param src 원본 파일
	 * @return 복호화 대상 파일
	 */
	public static byte[] decrypt(File src) {
		if (!src.exists()) {
			throw new CommonException(CommonError.COM_NOT_FOUND, "file: " + src.getAbsolutePath());
		}

		byte[] bytes = FileUtil.read(src);
		return decrypt(bytes, false);
	}

	/**
	 * byte 배열을 AES256 알고리즘으로 복호화
	 */
	public static synchronized byte[] decrypt(byte[] value, boolean base64) {
		if (value.length == 0) {
			return value;
		}

		try {
			CIPHER.init(Cipher.DECRYPT_MODE, SECRET_KEY_SPEC, IV_SPEC);
			return CIPHER.doFinal(base64 ? Base64.decodeBase64(value) : value);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException
				 | BadPaddingException | IllegalBlockSizeException e) {
			throw new CommonException(CommonError.COM_ENCRYPTION_ERROR, "length: " + value.length, e);
		}
	}
}

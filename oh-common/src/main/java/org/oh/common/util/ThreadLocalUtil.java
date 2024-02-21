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

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 쓰레드 로컬 유틸리티 (쓰레드 내에서만 유효한 속성)
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ThreadLocalUtil {
	private static final ThreadLocal<Map<String, Object>> LOCAL = new ThreadLocal<>();

	/**
	 * 쓰레드 로컬 맵에서 키에 해당하는 값을 반환
	 *
	 * @param key           속성 키
	 * @param resultTypeRef 결과 타입 레퍼런스
	 * @param defaultValue  없을 경우 기본값
	 * @return 속성 값
	 */
	public static <T> T get(LocalKey key, TypeReference<T> resultTypeRef, T defaultValue) {
		return get(key, resultTypeRef)
				.orElse(defaultValue);
	}

	/**
	 * 쓰레드 로컬 맵에서 키에 해당하는 값을 반환
	 *
	 * @param key           속성 키
	 * @param resultTypeRef 결과 타입 레퍼런스
	 * @return 속성 값
	 */
	public static <T> Optional<T> get(LocalKey key, TypeReference<T> resultTypeRef) {
		return get(key, CommonUtil.getClass(resultTypeRef));
	}

	/**
	 * 쓰레드 로컬 맵에서 키에 해당하는 값을 반환
	 *
	 * @param key          속성 키
	 * @param defaultValue 없을 경우 기본값
	 * @return 속성 값
	 */
	public static <T> T get(LocalKey key, T defaultValue) {
		return getInternal(key.name(), defaultValue);
	}

	/**
	 * 쓰레드 로컬 맵에서 키에 해당하는 값을 반환
	 *
	 * @param key        속성 키
	 * @param resultType 결과 타입
	 * @return 속성 값
	 */
	public static <T> Optional<T> get(LocalKey key, Class<T> resultType) {
		return getInternal(key.name(), resultType);
	}

	/**
	 * 쓰레드 로컬 맵에 키와 값을 설정
	 *
	 * @param key   속성 키
	 * @param value 속성 값
	 */
	public static <T> void set(LocalKey key, T value) {
		setInternal(key.name(), value);
	}

	/**
	 * 쓰레드 로컬 맵에서 키와 값을 삭제
	 *
	 * @param key 속성 키
	 */
	public static void remove(LocalKey key) {
		Optional.ofNullable(getInternal())
				.filter(a -> a.containsKey(key.name()))
				.ifPresent(a -> {
					a.remove(key.name());
					log.debug("Removed local key: {}", key);
				});
	}

	/**
	 * 쓰레드 로컬 맵에서 모든 키와 값을 삭제
	 */
	public static void remove() {
		Optional.ofNullable(LOCAL.get())
				.ifPresent(a -> {
					LOCAL.remove();
					log.debug("Removed local");
				});
	}

	public static String getCaller(int depth) {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		if (stackTrace.length <= depth) {
			return "Invalid call stack depth value";
		}
		return String.format("%s(%s:%s)",
				stackTrace[depth].getMethodName(),
				stackTrace[depth].getFileName(),
				stackTrace[depth].getLineNumber());
	}

	@SuppressWarnings("unchecked")
	private static <T> T getInternal(String key, T defaultValue) {
		return getInternal(key, (Class<T>) defaultValue.getClass())
				.orElse(defaultValue);
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> getInternal(String key, Class<T> resultType) {
		Object value = getInternal().get(key);
//		log.debug("Local key: {}, value: {}", key, StringUtils.abbreviate(value.toString(), 128));
		return Optional.ofNullable(value)
				.filter(resultType::isInstance)
				.map(a -> (T) a);
	}

	@NotNull
	private static Map<String, Object> getInternal() {
		return Optional.ofNullable(LOCAL.get())
				.orElseGet(() -> {
					LOCAL.set(new HashMap<>());
					return LOCAL.get();
				});
	}

	private static <T> void setInternal(String key, T value) {
		getInternal().put(key, value);
		log.debug("Local key: {}, value: {}", key, StringUtils.abbreviate(value.toString(), 128));
	}

	/**
	 * 쓰레드 로컬 키 정보
	 */
	@FunctionalInterface
	public interface LocalKey {
		String name();
	}

	/**
	 * 쓰레드 로컬 키 리스트
	 */
	public enum ThreadLocalKey
			implements LocalKey {
	}
}

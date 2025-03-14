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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.model.CommonModel;
import org.oh.common.model.http.HttpResponse;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 문자열 유틸리티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class StringUtil {
	public static final String DEFAULT_ELLIPSIS = "...";
	public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat(
			String.format("#,###.%s", StringUtils.repeat('#', 20)));

	/**
	 * 해당 객체를 JSON 뷰 모델에 맞게 선택한 필드만 변환
	 *
	 * @param target 대상 객체
	 * @return 문자열
	 */
	public static String toString(Object target) {
		return toString(target, false);
	}

	/**
	 * 해당 객체를 JSON 뷰 모델에 맞게 선택한 필드만 변환
	 *
	 * @param target 대상 객체
	 * @param json   JSON 여부
	 * @return 문자열
	 */
	public static String toString(Object target, boolean json) {
		return Optional.ofNullable(target)
				.map(a -> json ? JsonUtil.toString(a) : ArrayUtils.toString(a))
				.orElse("");
	}

	/**
	 * 해당 객체를 JSON 뷰 모델에 맞게 선택한 필드만 변환
	 *
	 * @param target   대상 객체
	 * @param json     JSON 여부
	 * @param jsonView JSON 뷰 모델
	 * @return 문자열
	 */
	public static String toString(Object target, boolean json, Class<? extends CommonModel.None> jsonView) {
		if (jsonView == CommonModel.None.class) {
			return toString(target, json);
		} else {
			return JsonUtil.toString(target, jsonView);
		}
	}

	/**
	 * 밀리 초를 문자열로 변환
	 *
	 * @param ms 밀리 초
	 * @return 문자열 예) 0 ms(00:00:00.000)
	 */
	public static String toStringTime(long ms) {
		return String.format("%s ms(%s)", NUMBER_FORMAT.format(ms), DateUtil.formatHHmmssSSS(ms));
	}

	/**
	 * 초를 문자열로 변환
	 *
	 * @param sec 초
	 * @return 문자열 예) 0 sec(00:00:00)
	 */
	public static String toStringTime(int sec) {
		return String.format("%s sec(%s)", NUMBER_FORMAT.format(sec), DateUtil.formatHHmmss(sec));
	}

	/**
	 * HTTP 응답 메세지를 에러 메세지 형식에 맞게 변환
	 *
	 * @param resp HTTP 응답 메세지
	 * @return 에러 메세지 예) [code]message
	 */
	public static String toCodeString(HttpResponse resp) {
		return toCodeString(resp.getError(), resp.getMessage());
	}

	/**
	 * 에러 메세지 형식에 맞게 변환
	 *
	 * @param code    에러 코드
	 * @param message 에러 메세지
	 * @return 에러 메세지 예) [code]message
	 */
	public static String toCodeString(Object code, Object message) {
		return String.format("[%s]%s", code, message);
	}

	/**
	 * 문자열을 스페이스로 결합
	 *
	 * @param values 문자열들
	 * @return 결합된 문자열
	 */
	public static String joiningBySpace(String... values) {
		return joining(" ", values);
	}

	/**
	 * 문자열을 구분자로 결합
	 *
	 * @param delimiter 구분자
	 * @param values    문자열들
	 * @return 결합된 문자열
	 */
	public static String joining(String delimiter, String... values) {
		return Arrays.stream(values)
				.filter(StringUtils::isNotEmpty)
				.collect(Collectors.joining(delimiter));
	}

	/**
	 * 긴 문자열을 최대 길이에 맞게 자름
	 *
	 * @param str 대상 문자열
	 * @param max 초대 길이
	 * @return 잘린 문자열
	 */
	public static String ellipsis(String str, int max) {
		return ellipsis(str, max, DEFAULT_ELLIPSIS);
	}

	/**
	 * 긴 문자열을 최대 길이에 맞게 자름
	 * <pre>
	 * max : return
	 * 00 :
	 * 01 : 가
	 * 02 : 가나
	 * 03 : 가나다
	 * 04 : 가...
	 * 05 : 가나...
	 * 06 : 가나다...
	 * 07 : 가나다라...
	 * 08 : 가나다라마...
	 * 09 : 가나다라마바...
	 * 10 : 가나다라마바사...
	 * </pre>
	 *
	 * @param str      대상 문자열
	 * @param max      초대 길이
	 * @param ellipsis 잘린 대체 문자열
	 * @return 잘린 문자열
	 */
	public static String ellipsis(String str, int max, String ellipsis) {
		int length = str == null ? 0 : str.length();
		if (length < max) {
			return str;
		}

		int ellipsisLength = ellipsis == null ? 0 : ellipsis.length();
		if (ellipsisLength >= max) {
			return str.substring(0, max);
		}

		return str.substring(0, max - ellipsisLength) + ellipsis;
	}

	/**
	 * UUID 반환
	 *
	 * @return UUID 문자열
	 */
	public static String generateUUID() {
		return UUID.randomUUID().toString();
	}
}

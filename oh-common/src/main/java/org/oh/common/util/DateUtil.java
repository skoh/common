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

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DateUtil {
	/**
	 * yyyyMMdd
	 */
	public static final String PURE_DATE_PATTERN = "yyyyMMdd";
	/**
	 * HHmmss
	 */
	public static final String PURE_TIME_PATTERN = "HHmmss";
	/**
	 * PURE_MS_PATTERN
	 */
	public static final String PURE_MS_PATTERN = "SSS";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String PURE_DATE_TIME_PATTERN = PURE_DATE_PATTERN + PURE_TIME_PATTERN;
	/**
	 * yyyyMMddHHmmssSSS
	 */
	public static final String PURE_DATE_TIME_PATTERN_MS = PURE_DATE_TIME_PATTERN + PURE_MS_PATTERN;
	/**
	 * yyyyMMddHH
	 */
	public static final String PURE_DATE_HOUR_PATTERN = PURE_DATE_PATTERN + "HH";

	/**
	 * yyyy-MM-dd
	 */
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	/**
	 * HH:mm:ss
	 */
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	/**
	 * .SSS
	 */
	public static final String DEFAULT_MS_PATTERN = '.' + PURE_MS_PATTERN;
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DEFAULT_DATE_TIME_PATTERN = DEFAULT_DATE_PATTERN + " " + DEFAULT_TIME_PATTERN;
	/**
	 * yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static final String DEFAULT_DATE_TIME_PATTERN_MS = DEFAULT_DATE_TIME_PATTERN + DEFAULT_MS_PATTERN;

	/**
	 * 00:00:00
	 */
	public static final String DEFAULT_TIME_START = "00:00:00";
	/**
	 * .000
	 */
	public static final String DEFAULT_MS_START = ".000";
	/**
	 * 00:00:00.000
	 */
	public static final String DEFAULT_TIME_MS_START = DEFAULT_TIME_START + DEFAULT_MS_START;

	/**
	 * 23:59:59
	 */
	public static final String DEFAULT_TIME_END = "23:59:59";
	/**
	 * .999
	 */
	public static final String DEFAULT_MS_END = ".999";
	/**
	 * 23:59:59.999
	 */
	public static final String DEFAULT_TIME_MS_END = DEFAULT_TIME_END + DEFAULT_MS_END;

	/**
	 * 2020-01-01
	 */
	public static final String SAMPLE_DATE_START = "2020-01-01";
	/**
	 * 2020-01-01 00:00:00
	 */
	public static final String SAMPLE_DATE_TIME_START = SAMPLE_DATE_START + " " + DEFAULT_TIME_START;
	/**
	 * 2020-01-01 00:00:00.000
	 */
	public static final String SAMPLE_DATE_TIME_START_MS = SAMPLE_DATE_TIME_START + DEFAULT_MS_START;

	/**
	 * 2020-01-02
	 */
	public static final String SAMPLE_DATE_END = "2020-01-02";
	/**
	 * 2020-01-02 23:59:59
	 */
	public static final String SAMPLE_DATE_TIME_END = SAMPLE_DATE_END + " " + DEFAULT_TIME_END;
	/**
	 * 2020-01-02 23:59:59.999
	 */
	public static final String SAMPLE_DATE_TIME_END_MS = SAMPLE_DATE_TIME_END + DEFAULT_MS_END;

	public static final long SAMPLE_DATE_TIME_LONG = 1552359929342L;

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param day format: yyyy-MM-dd
	 * @return Date 객체
	 */
	public static Date parseDate(String day) {
		return parse(day, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param date format: yyyy-MM-dd HH:mm:ss
	 * @return Date 객체
	 */
	public static Date parseDateTime(String date) {
		return parse(date, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param date format: yyyy-MM-dd HH:mm:ss.SSS
	 * @return Date 객체
	 */
	public static Date parseDateTimeMs(String date) {
		return parse(date, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 시작 일로 반환
	 *
	 * @param day Date 객체
	 * @return yyyy-MM-dd 00:00:00.000
	 */
	public static Date parseStartDate(Date day) {
		return parseStartDate(formatDate(day));
	}

	/**
	 * 해당 시작 일로 반환
	 *
	 * @param day format: yyyy-MM-dd
	 * @return yyyy-MM-dd 00:00:00.000
	 */
	public static Date parseStartDate(String day) {
		return parse(day + " " + DEFAULT_TIME_MS_START, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 종료 일로 반환
	 *
	 * @param day Date 객체
	 * @return yyyy-MM-dd 23:59:59.999
	 */
	public static Date parseEndDate(Date day) {
		return parseEndDate(formatDate(day));
	}

	/**
	 * 해당 종료 일로 반환
	 *
	 * @param day format: yyyy-MM-dd
	 * @return yyyy-MM-dd 23:59:59.999
	 */
	public static Date parseEndDate(String day) {
		return parse(day + " " + DEFAULT_TIME_MS_END, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 시작 일시로 반환
	 *
	 * @param date format: yyyy-MM-dd HH:mm:ss
	 * @return yyyy-MM-dd HH:mm:ss.000
	 */
	public static Date parseStartDateTime(String date) {
		return parse(date + DEFAULT_MS_START, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 종료 일시로 반환
	 *
	 * @param date format: yyyy-MM-dd HH:mm:ss
	 * @return yyyy-MM-dd HH:mm:ss.999
	 */
	public static Date parseEndDateTime(String date) {
		return parse(date + DEFAULT_MS_END, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 일시를 해당 패턴으로 반환
	 *
	 * @param date    문자열 일시
	 * @param pattern 패턴
	 * @return Date 객체
	 */
	public static Date parse(String date, String pattern) {
		try {
			LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
			return DateUtils.parseDate(date, pattern);
		} catch (DateTimeException | ParseException e) {
			throw new CommonException(CommonError.COM_INVALID_ARGUMENT,
					String.format("date: %s, pattern: %s", date, pattern), e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Date 객체를 문자열로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyy-MM-dd
	 */
	public static String formatDate(Date date) {
		return format(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Date 객체를 문자열로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyy-MM-dd HH:mm:ss
	 */
	public static String formatDateTime(Date date) {
		return format(date, DEFAULT_DATE_TIME_PATTERN);
	}

	/**
	 * Date 객체를 문자열로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String formatDateTimeMs(Date date) {
		return format(date, DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 문자열 일자를 문자열 시작 일시로 반환
	 *
	 * @param day format: yyyy-MM-dd
	 * @return format: yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String formatStartDateTimeMs(String day) {
		return format(parseStartDate(day), DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 문자열 일자를 문자열 종료 일시로 반환
	 *
	 * @param day format: yyyy-MM-dd
	 * @return format: yyyy-MM-dd HH:mm:ss.SSS
	 */
	public static String formatEndDateTimeMs(String day) {
		return format(parseEndDate(day), DEFAULT_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 해당 일시를 해당 패턴으로 반환
	 *
	 * @param date    문자열 일시
	 * @param pattern 패턴
	 * @return 문자열 일시
	 */
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern);
	}

	/**
	 * Date 객체를 시작 월로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyy-MM-01
	 */
	public static String formatStartMonth(Date date) {
		return formatDate(date).substring(0, 8) + "01";
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param date format: yyyyMMdd
	 * @return Date 객체
	 */
	public static Date parsePureDate(String date) {
		return parse(date, PURE_DATE_PATTERN);
	}

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param date format: yyyyMMddHH
	 * @return Date 객체
	 */
	public static Date parsePureHour(String date) {
		return parse(date, PURE_DATE_HOUR_PATTERN);
	}

	/**
	 * 문자열을 Date 객체로 반환
	 *
	 * @param date format: yyyyMMddHHmmssSSS
	 * @return Date 객체
	 */
	public static Date parsePureDateTimeMs(String date) {
		return parse(date, PURE_DATE_TIME_PATTERN_MS);
	}

	/**
	 * 현재 일자를 문자열로 반환
	 *
	 * @return format: yyyyMMdd
	 */
	public static String formatCurrentDate() {
		return format(new Date(), PURE_DATE_PATTERN);
	}

	/**
	 * Date 객체를 문자열로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyyMMdd
	 */
	public static String formatPureDate(Date date) {
		return format(date, PURE_DATE_PATTERN);
	}

	/**
	 * Date 객체를 문자열로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyyMMddHH
	 */
	public static String formatPureHour(Date date) {
		return format(date, PURE_DATE_HOUR_PATTERN);
	}

	/**
	 * Date 객체를 시작 일시로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyyMMdd01
	 */
	public static Date startPureHour(Date date) {
		return parsePureHour(formatPureDate(date) + "01");
	}

	/**
	 * Date 객체를 종료 일시로 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyyMMdd23
	 */
	public static Date endPureHour(Date date) {
		return parsePureHour(formatPureDate(date) + "23");
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 밀리초를 문자열 시간으로 반환
	 *
	 * @param millis 밀리초
	 * @return format: HH:mm:ss.SSS
	 */
	public static String formatHHmmssSSS(long millis) {
		return String.format("%s.%03d", formatHHmmss(millis / 1000), millis % 1000);
	}

	/**
	 * 초를 문자열 시간으로 반환
	 *
	 * @param sec 밀리초
	 * @return format: HH:mm:ss
	 */
	public static String formatHHmmss(long sec) {
		return String.format("%02d:%02d:%02d", sec / 3600, sec % 3600 / 60, sec % 3600 % 60);
	}

	/**
	 * Date 객체에서 일자를 반환
	 *
	 * @param date Date 객체
	 * @return format: yyyy-MM-dd 00:00:00.000
	 */
	public static Date getDay(Date date) {
		return parseDate(formatDate(date));
	}

	/**
	 * 해당 일자가 포함된 주의 시작 일자를 반환
	 *
	 * @param date Date 객체
	 * @return 해당 주의 시작일
	 */
	public static Date getFirstDateOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, -(cal.get(Calendar.DAY_OF_WEEK) - 1));
		return cal.getTime();
	}

	/**
	 * 해당 일자가 포함된 월의 시작 일자를 반환
	 *
	 * @param date Date 객체
	 * @return 해당 월의 시작일
	 */
	public static Date getFirstDateOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal.getTime();
	}

	/**
	 * 현재 시간을 문자열 밀리초로 반환
	 *
	 * @return 밀리초
	 */
	public static String currentTimeMillis() {
		return Long.toString(System.currentTimeMillis());
	}

	/**
	 * 현재 시간을 문자열 나노초로 반환
	 *
	 * @return 나노초
	 */
	public static String nanoTime() {
		return Long.toString(System.nanoTime());
	}
}

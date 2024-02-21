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

package org.oh.common.converter;

import org.oh.common.util.DateUtil;
import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Date <-> String(yyyy-MM-dd) 형식 변환
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DateFormatConverter {
	/**
	 * Date -> String 형식 변환
	 */
	public static class DateToStringConverter
			extends StdConverter<Date, String> {
		@Override
		public String convert(Date value) {
			return DateUtil.formatDate(value);
		}
	}

	/**
	 * String -> Date 형식 변환
	 */
	public static class StringToDateConverter
			extends StdConverter<String, Date> {
		@Override
		public Date convert(String value) {
			if (StringUtils.isEmpty(value)) {
				return null;
			}
			return DateUtil.parseDate(value);
		}
	}
}

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

import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * BigDecimal <-> String(#.#") 형식 변환
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DecimalFormatConverter {
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
	private static final DecimalFormat DECIMAL_FORMAT2 = new DecimalFormat("#.##");
	private static final DecimalFormat DECIMAL_FORMAT3 = new DecimalFormat("#.###");
	private static final DecimalFormat DECIMAL_FORMAT4 = new DecimalFormat("#.####");

	/**
	 * BigDecimal <-> String(#.#") 형식 변환
	 */
	public static class DecimalToStringConverter
			extends StdConverter<BigDecimal, String> {
		@Override
		public String convert(BigDecimal value) {
			return DECIMAL_FORMAT.format(value);
		}
	}

	/**
	 * BigDecimal <-> String(#.##") 형식 변환
	 */
	public static class DecimalToStringConverter2
			extends StdConverter<BigDecimal, String> {
		@Override
		public String convert(BigDecimal value) {
			return DECIMAL_FORMAT2.format(value);
		}
	}

	/**
	 * BigDecimal <-> String(#.###") 형식 변환
	 */
	public static class DecimalToStringConverter3
			extends StdConverter<BigDecimal, String> {
		@Override
		public String convert(BigDecimal value) {
			return DECIMAL_FORMAT3.format(value);
		}
	}

	/**
	 * BigDecimal <-> String(#.####") 형식 변환
	 */
	public static class DecimalToStringConverter4
			extends StdConverter<BigDecimal, String> {
		@Override
		public String convert(BigDecimal value) {
			return DECIMAL_FORMAT4.format(value);
		}
	}

//	public static class StringToDecimalConverter
//			extends StdConverter<String, BigDecimal> {
//		@Override
//		public BigDecimal convert(String value) {
//			return new BigDecimal(value);
//		}
//	}
}

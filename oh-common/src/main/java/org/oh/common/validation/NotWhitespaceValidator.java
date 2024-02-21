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

package org.oh.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 공백 유효성 체크
 */
public class NotWhitespaceValidator implements ConstraintValidator<NotWhitespace, Character> {
	private boolean isNull;

	/**
	 * 초기화
	 *
	 * @param constraintAnnotation 유효성 체크 어노테이션
	 */
	@Override
	public void initialize(NotWhitespace constraintAnnotation) {
		isNull = constraintAnnotation.isNull();
	}

	/**
	 * 해당 값으로 유효성을 체크
	 *
	 * @param value   대상 값
	 * @param context
	 * @return 유효성 여부
	 */
	@Override
	public boolean isValid(Character value, ConstraintValidatorContext context) {
		if (value == null) {
			return isNull;
		}

		return !Character.isWhitespace(value);
	}
}

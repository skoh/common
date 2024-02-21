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

package org.oh.common.annotation;

import com.google.common.base.Defaults;
import org.oh.common.config.AopConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 대상 메소드 안에서 발생하는 예외를 처리할 방법 정의
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandling {
	/**
	 * 입력 인자값 중에 출력할 대상을 선택 (기본값: 없음)
	 * 예) 1, 2번째 값만 출력할 경우: indexesOfArgs = {0, 1}
	 */
	int[] indexesOfArgs() default {};

	/**
	 * 입출력 값을 JSON 형태로 출력할지 여부를 지정 (기본값: false)
	 */
	boolean json() default false;

	/**
	 * 예외 발생시 메세지안에 입력 인자값을 포함할지 여부를 지정 (기본값: false)
	 */
	boolean argsInException() default false;

	/**
	 * 메소드에서 발생하는 모든 예외 중에 잡아서 처리할 예외 유형을 지정 (기본값: Exception.class)
	 */
	Class<? extends Exception>[] catchTypes() default {Exception.class};

	/**
	 * 지정한 예외 유형을 찾을 경우 로그에 출력할 로그 레벨을 지정
	 * (THROW/SHORT/ERROR/WARN/INFO/DEBUG/TRACE,기본값: ERROR)
	 */
	AopConfig.LogLevel logLevel() default AopConfig.LogLevel.ERROR;

	/**
	 * 지정한 예외 유형을 찾을 경우 로그에 출력하고 다시 예외를 상위로 던짐 (기본값: false)
	 */
	boolean throwException() default false;

	/**
	 * 지정한 예외 유형을 찾을 경우 결과값을 SPEL 표현식으로 지정
	 * (기본값: {@link Defaults#defaultValue(Class)})
	 */
	String returnExpression() default "";
}
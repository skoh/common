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

import com.fasterxml.jackson.annotation.JsonView;
import org.oh.common.config.AopConfig;
import org.oh.common.model.CommonModel;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 대상 메소드의 입출력 값과 쇼요시간을 로그에 출력
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResultLogging {
	/**
	 * 클래스명 앞에 그룹핑할 타이틀명을 지정 (기본값: 없음)
	 */
	String value() default "";

	/**
	 * 입력 인자값을 로그에 출력할지 여부를 지정 (기본값: true)
	 * 주의) 데이터가 너무 크면 성능에 영향을 준다
	 */
	boolean args() default true;

	/**
	 * 입력 인자값 중에 출력할 대상을 선택 (기본값: 없음)
	 * 예) 1, 2번째 값만 출력할 경우: indexesOfArgs = {0, 1}
	 */
	int[] indexesOfArgs() default {};

	/**
	 * 출력 결과값을 로그에 출력할지 여부를 지정 (기본값: false)
	 * 주의) 데이터가 너무 크면 성능에 영향을 준다
	 */
	boolean result() default false;

	/**
	 * 입출력 값을 JSON 형태로 출력할지 여부를 지정 (기본값: false)
	 */
	boolean json() default false;

	/**
	 * {@link JsonView} 로 정의한 필드가 있는 모델의 경우 사용할 뷰 모델을 지정
	 */
	Class<? extends CommonModel.None> jsonView() default CommonModel.None.class;

	/**
	 * 출력할 로그 레벨을 지정
	 * (ERROR/WARN/INFO/DEBUG/TRACE,기본값: DEBUG)
	 */
	AopConfig.LogLevel logLevel() default AopConfig.LogLevel.DEBUG;
}

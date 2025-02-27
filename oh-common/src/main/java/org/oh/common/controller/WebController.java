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

package org.oh.common.controller;

import org.oh.common.config.CommonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 웹 관련 공통 컨트롤러
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### API 사용 여부
 *   api:
 *     ### SPA(Single Page Application) UI 내장 여부 (기본값: false)
 *     spa.enabled: true
 * </pre>
 */
public class WebController {
	/**
	 * SPA(Single Page Application) UI 내장 지원
	 *
	 * @see <a href="https://spring.io/blog/2015/05/13/modularizing-the-client-angular-js-and-spring-security-part-vii">Spring Blog</a>
	 */
	@Controller
	@ConditionalOnProperty(value = "enabled", prefix = CommonConfig.COMMON_API_PREFIX + ".spa", havingValue = "true")
	public static class SpaController
			implements DefaultController {
		@GetMapping("/{path:[^\\.]*}")
		public String redirect() {
			return "forward:/";
		}
	}
}

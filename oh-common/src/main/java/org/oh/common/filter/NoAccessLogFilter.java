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

package org.oh.common.filter;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * 접근 로그 제외 필터
 * <pre>
 * application.yml
 *
 * server:
 *   tomcat:
 *     accesslog:
 *       condition-unless: noAccessLog
 * </pre>
 */
@WebFilter({"/css/*", "/styles/*", // styles
		"/img/*", "/images/*", // images
		"/js/*", // js
		"/node_modules/*", // node
		"/undefined", "*.ico", "*.html", "*.json", // files
		"/static/*", // etc
		"/", "/csrf", "/v3/*", "/swagger-ui/*"}) // swagger
@NoArgsConstructor
public class NoAccessLogFilter
		implements Filter {
	private String conditionUnless;

	@Autowired
	public NoAccessLogFilter(ServerProperties serverProperties) {
		conditionUnless = serverProperties.getTomcat()
				.getAccesslog()
				.getConditionUnless();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		Optional.ofNullable(conditionUnless)
				.ifPresent(a -> request.setAttribute(a, "NO_LOG"));

		chain.doFilter(request, response);
	}
}

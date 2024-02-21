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

package org.oh.common.config;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * 로깅 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### 로깅 관리 확장
 *   logs:
 *     ### 모든 시스템 설정 출력 여부 (기본값: false)
 *     properties: trye/false
 *     ### 모든 빈 정보 출력 여부 (기본값: false)
 *     beans: trye/false
 * </pre>
 */
@Slf4j
@Configuration
public class LoggingConfig {
	public static final String ONE_LINE_10 = StringUtils.repeat('-', 10);
	public static final String ONE_LINE_30 = StringUtils.repeat('-', 30);
	public static final String ONE_LINE_50 = StringUtils.repeat('-', 50);
	public static final String ONE_LINE_100 = StringUtils.repeat('-', 100);
	public static final String TWO_LINE_10 = StringUtils.repeat('=', 10);
	public static final String TWO_LINE_30 = StringUtils.repeat('=', 30);
	public static final String TWO_LINE_50 = StringUtils.repeat('=', 50);
	public static final String TWO_LINE_100 = StringUtils.repeat('=', 100);

	/**
	 * 로깅 초기화
	 */
	@EventListener
	public void initLogging(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Environment environment = context.getEnvironment();

		logProperties(context, environment);
		logBeans(context, environment);
	}

	@SuppressWarnings("rawtypes")
	private void logProperties(ApplicationContext context, Environment environment) {
		boolean properties = environment.getProperty(CommonConfig.COMMON_PREFIX + ".logs.properties",
				Boolean.class, false);
		if (!properties) {
			return;
		}

		log.info(ONE_LINE_100);
		MutablePropertySources sources = ((AbstractEnvironment) environment).getPropertySources();
		StreamSupport.stream(sources.spliterator(), false)
				.filter(EnumerablePropertySource.class::isInstance)
				.map(e -> ((EnumerablePropertySource) e).getPropertyNames())
				.flatMap(Arrays::stream)
				.distinct()
				.filter(e -> !(e.equals("Path") || e.equals("gitlabMergeRequestDescription")
						|| e.endsWith("class.path") || e.endsWith("library.path")
						|| e.endsWith("datasource.password") || e.endsWith("secret")))
				.sorted()
				.forEach(e -> log.info("{}: {}", e, environment.getProperty(e)));
		log.info(ONE_LINE_100);
	}

	@SuppressWarnings("UnstableApiUsage")
	private void logBeans(ApplicationContext context, Environment environment) {
		boolean beans = environment.getProperty(CommonConfig.COMMON_PREFIX + ".logs.beans", Boolean.class, false);
		if (!beans) {
			return;
		}

		List<String> list = Arrays.asList(context.getBeanDefinitionNames());
		Collections.sort(list);
		log.info(ONE_LINE_100);
		String format = "%-4.4s %-100.100s %s";
		log.info(String.format(format, "no", "name", "class"));
		log.info(ONE_LINE_100);
		Streams.mapWithIndex(list.stream(), (n, i) ->
						String.format(format, i + 1, n, Optional.ofNullable(context.getBean(n))
								.map(a -> a.getClass().getName())
								.orElse("")))
				.forEach(log::info);
		log.info(ONE_LINE_100);
	}
}

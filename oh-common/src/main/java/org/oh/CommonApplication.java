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

package org.oh;

import org.oh.common.config.CommonConfig;
import org.oh.common.model.user.Login;
import org.oh.common.repository.CrudDbRepository;
import org.oh.common.repository.CrudRepository;
import org.oh.sample.mapper.SampleMapper;
import com.hazelcast.spi.properties.ClusterProperty;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.hazelcast.repository.config.EnableHazelcastRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 메인 어플리케이션
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### 메인 클래스 실행 여부 (기본값: false)
 *   enabled: true
 * </pre>
 */
@EnableCaching
@ServletComponentScan
@SpringBootApplication
@EnableJpaRepositories(
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CrudDbRepository.class))
@EnableHazelcastRepositories(
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CrudRepository.class))
@EntityScan(basePackageClasses = Login.class)
@MapperScan(basePackageClasses = SampleMapper.class)
@ConditionalOnProperty(value = "enabled", prefix = CommonConfig.COMMON_PREFIX, havingValue = "true")
public class CommonApplication
		extends SpringBootServletInitializer {
//	public static final String SPRING_CONFIG_NAME = SpringUtil.SPRING_CONFIG_NAME + ":" + CommonConfig.COMMON_PREFIX;

	public static void main(String[] args) { //NOSONAR 스프링 부트는 메인으로 실행
		System.setProperty(ClusterProperty.SOCKET_BIND_ANY.toString(), "false");

		SpringApplication app = new SpringApplicationBuilder(CommonApplication.class)
//				.properties(SPRING_CONFIG_NAME)
				.build();
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//		return builder.sources(CommonApplication.class);
//	}
}

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

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 기본 스웨거 초기화
 * <pre>
 * application.yml
 *
 * ### API 문서(스웨거) 관리
 * api-doc:
 *   title: Common(공통) API
 *   description: Apis provided by development team
 *   version: 0.0.1
 *   email: skoh38@gmail.com
 *   ### 실행 URL (프록시 등으로 자동 설정이 안될 경우 사용)
 *   url: http://localhost:8080
 * </pre>
 */
@Setter
@Configuration
@Validated
@ConfigurationProperties(CommonConfig.COMMON_PREFIX + ".api-doc")
public class SpringDocConfig {
	protected static final License LICENSE = new License()
			.name("Licensed under the Apache License, Version 2.0");
	protected static final String API_KEY_NAME = "Token";
	protected static final SecurityScheme SECURITY_SCHEME = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");
	protected static final SecurityRequirement SECURITY_REQUIREMENT = new SecurityRequirement()
			.addList(API_KEY_NAME);

	protected String title;
	protected String description;
	protected String version;
	protected String email;
	protected String url;

	@Bean
	protected OpenAPI openAPI(@Value("${" + SecurityConfig.SECURITY_API_PREFIX + ".enabled:false}")
							  boolean securityEnabled) {
		Contact contact = new Contact()
				.name("skoh")
				.url("https://github.com/skoh")
				.email(email);

		Info info = new Info()
				.title(title)
				.version(version)
				.description(description)
				.contact(contact)
				.license(LICENSE);

		OpenAPI openAPI = new OpenAPI()
				.info(info);

		Optional.ofNullable(url)
				.filter(StringUtils::isNotEmpty)
				.ifPresent(a -> {
					List<Server> servers = Collections.singletonList(new Server().url(a));
					openAPI.servers(servers);
				});

		if (securityEnabled) {
			openAPI.components(new Components()
							.addSecuritySchemes(API_KEY_NAME, SECURITY_SCHEME))
					.addSecurityItem(SECURITY_REQUIREMENT);
		}

		return openAPI;
	}
}

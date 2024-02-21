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

package org.oh.common.config.database;

import org.oh.common.config.CommonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * 데이터 소스 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### JPA 관리
 *   jpa:
 *     ### 사용 여부 (기본값: false)
 *     enabled: true
 * </pre>
 */
@Configuration
public class DataSourceConfig {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".jpa";

	@ConditionalOnProperty(value = "enabled", prefix = PROPERTY_PREFIX, havingValue = "false", matchIfMissing = true)
	public static class JdbcConfig {
		@Primary
		@Bean
		public TransactionManager transactionManager(@Lazy DataSource dataSource) {
			return new JdbcTransactionManager(dataSource);
		}
	}

	@ConditionalOnProperty(value = "enabled", prefix = PROPERTY_PREFIX, havingValue = "true")
	public static class JpaConfig {
		@Primary
		@Bean
		public TransactionManager transactionManager(EntityManagerFactory emf) {
			return new JpaTransactionManager(emf);
		}
	}
}

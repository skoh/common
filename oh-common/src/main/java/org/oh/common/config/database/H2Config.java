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

import org.h2.tools.Server;
import org.oh.common.config.CommonConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import java.sql.SQLException;

/**
 * H2 서버 초기화
 */
@Configuration
@ConditionalOnProperty(value = "enabled", prefix = CommonConfig.SPRING_PREFIX + ".h2.console", havingValue = "true")
public class H2Config {
	/**
	 * H2 서버 초기값 설정
	 */
	@Bean
	public Server h2TcpServer() throws SQLException {
		return Server.createTcpServer(
//				"-tcpAllowOthers",
//				"-tcpPort", "9092"
				"-ifNotExists"
		).start();
	}

	/**
	 * DataSource 생성 (의존성 주입)
	 */
	@Bean
	public DataSource dataSource(DataSourceProperties properties, Server /* dependency */ h2TcpServer) {
		return properties.initializeDataSourceBuilder().build();
	}
}

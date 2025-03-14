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

import com.hazelcast.config.Config;
import com.hazelcast.config.cp.CPSubsystemConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.properties.ClusterProperty;
import com.hazelcast.spring.transaction.HazelcastTransactionManager;
import com.hazelcast.spring.transaction.ManagedTransactionalTaskContext;
import org.oh.common.model.AbstractFiles;
import org.oh.common.model.schedule.Schedule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

/**
 * 데이터 그리드 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### 데이터 그리드 관리
 *   data-grid:
 *     ### 사용 여부 (기본값: false)
 *     enabled: true
 *     session:
 *       ### 세션 사용 여부 (기본값: false)
 *       enabled: true
 * </pre>
 */
@Configuration
@ConditionalOnProperty(value = "enabled", prefix = DataGridConfig.PROPERTY_PREFIX, havingValue = "true")
public class DataGridConfig {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".data-grid";

	public static final String MAP_NAME_SESSION = "session";
	public static final String MAP_NAME_DEFAULT = "default";
	public static final String MAP_NAME_CACHE = "cache";
	public static final String MAP_NAME_SCHEDULE = Schedule.NAME_SPACE;
	public static final String MAP_NAME_FILES = AbstractFiles.NAME_SPACE;

	@Bean
	public HazelcastTransactionManager hazelcastTransactionManager(HazelcastInstance hazelcast) {
		return new HazelcastTransactionManager(hazelcast);
	}

	@Bean
	public ManagedTransactionalTaskContext managedTransactionalTaskContext(HazelcastInstance hazelcast) {
		return new ManagedTransactionalTaskContext(hazelcastTransactionManager(hazelcast));
	}

	//	@Bean
	public HazelcastInstance hazelcastInstance() {
		Config config = new Config();
		config.getNetworkConfig().setPort(5701);
		config.getCPSubsystemConfig().setCPMemberCount(CPSubsystemConfig.MIN_GROUP_SIZE);
		config.setProperty(ClusterProperty.LOGGING_TYPE.getName(), "slf4j");
		return Hazelcast.newHazelcastInstance(config);
	}

	@EnableHazelcastHttpSession(sessionMapName = MAP_NAME_SESSION)
	@ConditionalOnProperty(value = "enabled", prefix = PROPERTY_PREFIX + ".session", havingValue = "true")
	public static class Session {
	}
}

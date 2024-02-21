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

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케쥴러 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### 쓰레드/스케쥴러 풀 관리
 *   thread-pool:
 *     async:
 *       min-size: 8
 *       max-size: 20
 *     scheduling:
 *       max-size: 10
 *
 * - 현재 Spring 설정으로 대체
 * spring:
 * ### 쓰레드/스케쥴러 풀 관리
 *   task:
 *     execution.pool:
 *       core-size: 20
 *       max-size: 40
 *       queue-capacity: 100
 *       keep-alive: 60s
 *     scheduling.pool.size: 10
 * </pre>
 */
//@Slf4j
//@Setter
@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableScheduling
//@Validated
//@ConfigurationProperties(SchedulingConfig.PROPERTY_PREFIX)
//@ConditionalOnProperty(value = "async.max-size", prefix = SchedulingConfig.PROPERTY_PREFIX)
public class SchedulingConfig {//implements AsyncConfigurer, SchedulingConfigurer {
//	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".thread-pool";
//
//	@NestedConfigurationProperty
//	private Thread async;
//	@NestedConfigurationProperty
//	private Thread scheduling;
//
//	@Override
//	public Executor getAsyncExecutor() {
//		return ThreadUtil.createThreadPool(async.minSize, async.maxSize,
//				60, AsyncExecutionAspectSupport.DEFAULT_TASK_EXECUTOR_BEAN_NAME);
//	}
//
//	@Override
//	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//		taskRegistrar.setTaskScheduler(taskScheduler());
//	}
//
//	@Bean
//	public ThreadPoolTaskScheduler taskScheduler() {
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setPoolSize(scheduling.maxSize);
//		scheduler.initialize();
//		return scheduler;
//	}
//
//	@Data
//	@SuperBuilder
//	@NoArgsConstructor
//	private static class Thread {
//		@Min(1)
//		private int minSize;
//		@Min(1)
//		private int maxSize;
//	}

//	@Async
//	@Scheduled(fixedRate = 10_000)
//	@Scheduled(fixedDelay = 10_000)
//	@Scheduled(cron = "*/10 * * * * *")
//	protected void schedule() throws Exception {
//		log.debug("test");
//		Thread.sleep(5_000);
//	}
}

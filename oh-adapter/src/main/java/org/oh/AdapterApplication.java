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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@ServletComponentScan
@SpringBootApplication
@ConditionalOnProperty(value = "enabled", prefix = AdapterApplication.APP_NAME, havingValue = "true")
public class AdapterApplication
		extends SpringBootServletInitializer {
	public static final String APP_NAME = "adapter";

	public static void main(String[] args) { //NOSONAR 스프링 부트는 메인으로 실행
		SpringApplication app = new SpringApplicationBuilder(AdapterApplication.class).build();
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}

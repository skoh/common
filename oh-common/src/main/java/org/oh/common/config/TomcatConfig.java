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

import lombok.Setter;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AjpNioProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 톰캣 초기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 *   ### Apache-Tomcat AJP 연계
 *   tomcat.ajp:
 *     port: 8009
 *     address: localhost
 * </pre>
 */
@Setter
@Configuration
@Validated
@ConfigurationProperties(TomcatConfig.PROPERTY_PREFIX)
@ConditionalOnProperty(value = "port", prefix = TomcatConfig.PROPERTY_PREFIX)
public class TomcatConfig {
	public static final String PROPERTY_PREFIX = CommonConfig.COMMON_PREFIX + ".tomcat.ajp";

	@NotNull
	private Integer port;
	private String address;

	@Bean
	public ServletWebServerFactory servletContainer() throws UnknownHostException {
		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
		tomcat.addAdditionalTomcatConnectors(createAjpConnector());
		return tomcat;
	}

	private Connector createAjpConnector() throws UnknownHostException {
		Connector connector = new Connector("AJP/1.3");
		connector.setPort(port);
		AjpNioProtocol ajpNioProtocol = (AjpNioProtocol) connector.getProtocolHandler();
		ajpNioProtocol.setSecretRequired(false);
		if (address != null) {
			ajpNioProtocol.setAddress(InetAddress.getByName(address));
		}
		return connector;
	}
}

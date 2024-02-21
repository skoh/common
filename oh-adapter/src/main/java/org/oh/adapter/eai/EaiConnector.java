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

package org.oh.adapter.eai;

import COM.activesw.api.client.BrokerClient;
import COM.activesw.api.client.BrokerEvent;
import COM.activesw.api.client.BrokerException;
import org.oh.AdapterApplication;
import org.oh.adapter.exception.AdapterException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * EAI 커넥터
 */
@Slf4j
@Setter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Service
@Validated
@ConfigurationProperties(EaiConnector.PROPERTY_PREFIX)
@ConditionalOnProperty(value = "host", prefix = EaiConnector.PROPERTY_PREFIX)
public class EaiConnector {
	protected static final String PROPERTY_PREFIX = AdapterApplication.APP_NAME + ".eai";

	private String host;
	private int port;
	private String name;
	private String group;
	private int waitingMs;
	private int repeatTimes;
	private int repeatSleepMs;

	public <T1, T2> T2 execute(String appName, String pubDoc, String subDoc,
							   T1 params, EaiMapper<T1, T2> mapper) {
		T2 response;
		do {
			BrokerClient client = null;
			try {
				client = createBrokerClient(appName);
				BrokerEvent event = createEvent(client, pubDoc);
				checkPermission(client, pubDoc, subDoc);
				mapper.mappingRequestParam(event, params);
				response = publish(client, event, subDoc, mapper);
			} catch (BrokerException e) {
				throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail execute", e);
			} finally {
				try {
					if (client != null && client.isConnected()) {
						client.destroy();
					}
				} catch (BrokerException e) {
					log.debug("Error on client destroy", e);
				}
			}

			if (response != null) {
				break;
			}
			repeatTimes--;
			try {
				Thread.sleep(repeatSleepMs);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		} while (repeatTimes > 0);
		return response;
	}

	private BrokerClient createBrokerClient(String appName) {
		BrokerClient client;
		try {
			client = new BrokerClient(host + ":" + port, name, null, group, appName, null);
		} catch (BrokerException e) {
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail create broker client", e);
		}
		log.debug("Create a client. [{}]", client.getApiVersionNumber());
		return client;
	}

	private BrokerEvent createEvent(BrokerClient client, String pubDoc) {
		try {
			return new BrokerEvent(client, pubDoc);
		} catch (BrokerException e) {
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail create event", e);
		}
	}

	private void checkPermission(BrokerClient client, String pubDoc, String subDoc) {
		boolean canPublish;
		try {
			canPublish = client.canPublish(pubDoc);
		} catch (BrokerException e) {
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail check publish permission", e);
		}

		boolean canSubscribe;
		try {
			canSubscribe = client.canSubscribe(subDoc);
		} catch (BrokerException e) {
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail check subscribe permission", e);
		}

		if (!canPublish || !canSubscribe) {
			log.debug("canPublish: {} canSubscribe: {}", canPublish, canSubscribe);
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI,
					"Cannot publish or subscribe event." +
							" Make sure it is loaded in the broker" +
							" and permission is given to publish or subscribe it in the ...");
		}
	}

	private <T1, T2> T2 publish(BrokerClient client, BrokerEvent event,
								String subdoc, EaiMapper<T1, T2> mapper) {
		T2 response = null;
		try {
			client.publish(event);
			log.debug("client info : [{}], pub message : [{}]", client, event);

			client.newSubscription(subdoc, null);
			BrokerEvent subEvent = client.getEvent(waitingMs);
			log.debug("client info : [{}], sub message : [{}]", client, subEvent);
			if (subEvent.isNullReply()) {
				log.debug("Null reply");
			} else if (subEvent.isErrorReply()) {
				log.debug("Error reply");
			} else {
				response = mapper.mappingResponseParam(subEvent);
			}
		} catch (BrokerException e) {
			throw new AdapterException(AdapterException.AdapterError.FAIL_EAI, "Fail publish event", e);
		}
		return response;
	}
}

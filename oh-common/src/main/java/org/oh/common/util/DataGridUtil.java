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

package org.oh.common.util;

import com.hazelcast.core.HazelcastInstance;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.config.DataGridConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
@ConditionalOnProperty(value = "enabled", prefix = DataGridConfig.PROPERTY_PREFIX, havingValue = "true")
public final class DataGridUtil {
	/**
	 * 해당 클래스를 포함하여 상위의 @KeySpace의 값을 반환
	 *
	 * @param clazz 클래스 타입
	 * @return @KeySpace의 값
	 */
	public static Optional<String> getKeySpaceValue(Class<?> clazz) {
		return Optional.ofNullable(AnnotationUtils.findAnnotation(clazz, KeySpace.class))
				.map(KeySpace::value);
	}

	///////////////////////////////////////////////////////////////////////////

	private final HazelcastInstance hazelcast;

	/**
	 * 데이터 그리드에서 전체 맵 이름과 크기를 출력
	 */
	public void logAllMap() {
		logAllMap(false, false);
	}

	/**
	 * 데이터 그리드에서 전체 맵 정보를 출력
	 *
	 * @param showData 데이터 출력 여부
	 * @param json     JSON 형태로 출력 여부
	 */
	public void logAllMap(boolean showData, boolean json) {
		hazelcast.getDistributedObjects()
				.forEach(m -> logMap(m.getName(), showData, json));
	}

	/**
	 * 데이터 그리드에서 해당 맵 정보를 출력
	 *
	 * @param mapName  맵명
	 * @param showData 데이터 출력 여부
	 */
	public void logMap(String mapName, boolean showData) {
		logMap(mapName, showData, false);
	}

	/**
	 * 데이터 그리드에서 해당 맵 정보를 출력
	 *
	 * @param mapName  맵명
	 * @param showData 데이터 출력 여부
	 * @param json     JSON 형태로 출력 여부
	 */
	public void logMap(String mapName, boolean showData, boolean json) {
		Set<Map.Entry<Object, Object>> data = hazelcast.getMap(mapName).entrySet();
		log.debug("map: {}, size: {}", mapName, data.size());
		if (showData) {
			data.forEach(e -> {
				log.debug("data: {}", json ? JsonUtil.toString(e) : e);
				if (e instanceof Map.Entry && e.getValue() instanceof Session) {
					SpringUtil.logAttributeOfSession((Session) e.getValue(), json);
				}
			});
		}
	}
}

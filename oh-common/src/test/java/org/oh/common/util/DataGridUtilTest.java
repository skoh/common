package org.oh.common.util;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Map;
import java.util.Set;

//@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class DataGridUtilTest {
	protected static final HazelcastInstance HAZELCAST;

	static {
		Config config = new Config();
		config.getNetworkConfig().setPort(15701);
		HAZELCAST = Hazelcast.newHazelcastInstance(config);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			// Shutdown this Hazelcast client
			log.info("Hazelcast is shutting down ...");
			HAZELCAST.shutdown();
		}));
	}

	@Test
	void test() throws Exception {
		// Get the Distributed Map from Cluster.
		IMap<String, Object> map = HAZELCAST.getMap("default");

		//Standard Put and Get.
		Object value = map.put("key", "value");
		log.debug("{}", value);
		Assertions.assertNull(value);

		value = map.get("key");
		log.debug("{}", value);
		Assertions.assertNotNull(value);
		logAllMap();

		//Concurrent Map methods, optimistic updating
		value = map.putIfAbsent("somekey", "somevalue");
		log.debug("{}", value);
		Assertions.assertNull(value);

		boolean replace = map.replace("key", "value", "newvalue");
		log.debug("{}", replace);
		Assertions.assertTrue(replace);
		logAllMap();
	}

	/**
	 * 데이터 그리드에서 전체 맵 정보를 출력
	 */
	public void logAllMap() {
		HAZELCAST.getDistributedObjects()
				.forEach(m -> logMap(m.getName()));
	}

	/**
	 * 데이터 그리드에서 해당 맵 정보를 출력
	 *
	 * @param mapName 맵명
	 */
	public void logMap(String mapName) {
		Set<Map.Entry<Object, Object>> data = HAZELCAST.getMap(mapName).entrySet();
		log.debug("map: {}, size: {}", mapName, data.size());
		data.forEach(e -> log.debug("data: {}", e));
	}
}

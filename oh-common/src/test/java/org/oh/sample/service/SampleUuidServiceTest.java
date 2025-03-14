package org.oh.sample.service;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.exception.CommonException;
import org.oh.common.model.enume.State;
import org.oh.common.util.JsonUtil;
import org.oh.sample.model.SampleUuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

//@Disabled
@Slf4j
@ServiceTest
public class SampleUuidServiceTest {
	public static final UUID TEST_ID = UUID.fromString("b3b30987-a60e-44a5-9a40-3fdf4175fdd3");
	public static final SampleUuid ENTITY = SampleUuid.builder()
			.name("테스트")
			.build();

	private static final SampleUuid entity = JsonUtil.copy(ENTITY, SampleUuid.class);
	private static final SampleUuid entityResult = SampleUuid.builder()
			.id(TEST_ID)
			.build();
	private static final SampleUuid entityResult2 = new SampleUuid();

	@Autowired
	private SampleUuidService service;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		SampleUuid result = service.insert(entity);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertNotNull(result.getId());
		entityResult.setId(result.getId());
	}

	@Test
	void t02findById() {
		SampleUuid result = service.findById(entityResult.getId());
		Assertions.assertNotNull(result);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
	}

	@Test
	void t04findAll() {
		List<SampleUuid> result = service.findAllOrEmpty();
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		result = service.findAll(SampleDbServiceTest.SORT);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void t05findPage() {
		Page<SampleUuid> result = service.findPageOrEmpty(SampleDbServiceTest.PAGE);
		log.debug("result: {}", JsonUtil.toPrettyString(result));
		Assertions.assertTrue(result.getNumberOfElements() > 0);
	}

	@Test
	void t06findLimit() {
		List<SampleUuid> result = service.findAllOrEmpty(1, SampleDbServiceTest.SORT);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void t07count() {
		long result = service.count();
		log.debug("result: {}", result);
		Assertions.assertTrue(result > 0);
	}

	@Test
	void t08exists() {
		boolean result = service.exists(entityResult.getId());
		log.debug("result: {}", result);
		Assertions.assertTrue(result);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11update() throws Exception {
		Thread.sleep(1_000);
		State state = State.DELETED;
		entityResult.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		SampleUuid result = service.update(entityResult);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertEquals(state, result.getState());

		log.debug(LoggingConfig.ONE_LINE_100);
		SampleUuid entityTemp = SampleUuid.builder()
				.id(entityResult.getId())
				.nulls(ImmutableList.of("descp"))
				.build();
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		result = service.update(entityTemp);
		log.debug("result: {}", result.getDescp());
		Assertions.assertNull(result.getDescp());
	}

	@Test
	void t12merge() {
		SampleUuid entityTemp = service.findById(entityResult.getId());
		entityTemp.setNulls(null);
		entityTemp.setId(null);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		SampleUuid result = service.merge(entityTemp);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertNotNull(result.id());
		entityResult2.setId(result.getId());

		log.debug(LoggingConfig.ONE_LINE_100);
		State state = State.DELETED;
		entityTemp.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		result = service.merge(entityTemp);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertEquals(state, result.getState());
	}

	@Test
	void t13updateAll() {
		State state = State.ACTIVE;
		SampleUuid entity1 = SampleUuid.builder()
				.id(entityResult.getId())
				.state(state)
				.build();
		SampleUuid entity2 = SampleUuid.builder()
				.id(entityResult2.getId())
				.state(state)
				.build();
		List<SampleUuid> entities = ImmutableList.of(entity1, entity2);
		log.debug("entities: {}", JsonUtil.toPrettyString(entities));
		List<SampleUuid> result = service.update(entities);
		log.debug("result: {} {}", result.get(0).getState(), result.get(1).getState());
		Assertions.assertEquals(state, result.get(0).getState());
		Assertions.assertEquals(state, result.get(1).getState());

		log.debug(LoggingConfig.ONE_LINE_100);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult2.toString()));
		service.deleteById(entityResult2.getId());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t99delete() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		service.deleteById(entityResult.getId());
		Assertions.assertThrows(CommonException.class, () -> service.findById(entityResult.getId()));
	}
}

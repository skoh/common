package org.oh.sample.service;

import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.exception.CommonException;
import org.oh.common.model.enume.State;
import org.oh.common.util.JsonUtil;
import org.oh.sample.model.SampleUuid;
import org.oh.sample.model.SampleUuidModel;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

//@Disabled
@Slf4j
@ServiceTest
public class SampleUuidDbServiceTest {
	private static final SampleUuid entity = JsonUtil.copy(SampleUuidServiceTest.ENTITY, SampleUuid.class);
	private static final SampleUuid entity2 = JsonUtil.copy(entity, SampleUuid.class);
	private static final SampleUuid entityResult = SampleUuid.builder()
			.id(SampleUuidServiceTest.TEST_ID)
			.build();
	private static final SampleUuid entityResult2 = new SampleUuid();

	@Autowired
	private SampleUuidDbService service;

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

	//	@Test
	void t01saveAll() {
		service.insert(ImmutableList.of(entity, entity2));
	}

	@Test
	void t02findById() {
		SampleUuid result = service.findById(entityResult.getId());
		Assertions.assertNotNull(result);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
	}

	@Test
	void t03findOne() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		SampleUuid result = service.find(entityResult);
		Assertions.assertNotNull(result);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
	}

	@Test
	void t04findAll() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity2.toString()));
		List<SampleUuid> result = service.findAll(entity2, SampleDbServiceTest.SORT);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void t05findPage() {
		Page<SampleUuid> result = service.findPageOrEmpty(new SampleUuid(), SampleDbServiceTest.PAGE);
		log.debug("result: {}", JsonUtil.toPrettyString(result, SampleUuidModel.SampleUuid.One.class));
		Assertions.assertTrue(result.getNumberOfElements() > 0);
	}

	@Test
	void t06findLimit() {
		List<SampleUuid> result = service.findAllOrEmpty(new SampleUuid(), 1, SampleDbServiceTest.SORT);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void t07count() {
		long result = service.count(entity2);
		log.debug("result: {}", result);
		Assertions.assertTrue(result > 0);
	}

	@Test
	void t08exists() {
		boolean result = service.exists(entity2);
		log.debug("result: {}", result);
		Assertions.assertTrue(result);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11update() {
		State state = State.DELETED;
		entityResult.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		SampleUuid result = service.update(entityResult);
		log.debug("result: {}", result.getState());
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
		entityTemp.setId(null);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		SampleUuid result = service.merge(entityTemp);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		entityResult2.setId(result.getId());
		Assertions.assertNotNull(result.getId());

		log.debug(LoggingConfig.ONE_LINE_100);
		State state = State.DELETED;
		entityTemp.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		result = service.merge(entityTemp);
		log.debug("result: {}", result.getState());
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
		service.delete(entityResult2);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t99delete() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		service.deleteById(entityResult.getId());
		Assertions.assertThrows(CommonException.class, () -> service.findById(entityResult.getId()));
	}
}

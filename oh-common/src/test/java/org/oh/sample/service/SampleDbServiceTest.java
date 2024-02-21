package org.oh.sample.service;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.data.Condition;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.model.enume.State;
import org.oh.common.util.JsonUtil;
import org.oh.sample.model.Sample;
import org.oh.sample.model.SampleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

//@Disabled
@Slf4j
@ServiceTest
public class SampleDbServiceTest {
	public static final Sorting SORT = Sorting.builder()
			.sort(new String[]{"state", "id," + Sort.Direction.DESC})
			.build();
	public static final Paging PAGE = Paging.builder()
			.page(1)
			.psize(10)
			.sort(SORT.getSort())
			.build();

	public static final Long TEST_ID = 1L;
	public static final Sample ENTITY = Sample.builder()
			.name("테스트")
			.descp("테스트2")
			.build();

	private static final Sample entity = JsonUtil.copy(ENTITY, Sample.class);
	private static final Sample entity2 = JsonUtil.merge(ENTITY, Sample.builder()
			.condition("name,CONTAINS,IGNORE_CASE")
			.operation(Condition.Operarion.OR)
			.build());
	private static final Sample entityResult = Sample.builder()
			.id(TEST_ID)
			.build();
	private static final Sample entityResult2 = new Sample();

	@Autowired
	private SampleDbService service;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01save() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity.toString()));
		Sample result = service.insert(entity);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertTrue(result.getId() > 0);
		entityResult.setId(result.getId());
	}

	//	@Test
	void t01saveAll() {
		service.insert(ImmutableList.of(entity, entity2));
	}

	@Test
	void t02findById() {
		Sample result = service.findById(entityResult.getId());
		Assertions.assertNotNull(result);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
	}

	@Test
	void t03findOne() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		Sample result = service.find(entityResult);
		Assertions.assertNotNull(result);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
	}

	@Test
	void t04findAll() {
		log.debug("entity: {}", JsonUtil.toPrettyString(entity2.toString()));
		List<Sample> result = service.findAll(entity2, SORT);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		Assertions.assertFalse(result.isEmpty());
	}

	@Test
	void t05findPage() {
		Page<Sample> result = service.findPageOrEmpty(new Sample(), PAGE);
		log.debug("result: {}", JsonUtil.toPrettyString(result, SampleModel.Sample.One.class));
		Assertions.assertTrue(result.getNumberOfElements() > 0);
	}

	@Test
	void t06findLimit() {
		List<Sample> result = service.findAllOrEmpty(new Sample(), 1, SORT);
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

	@Test
	void t09init() {
		CommonException e = Assertions.assertThrows(CommonException.class, () -> {
			service.init();
		});
		log.debug(e.getMessage(), e);
		Assertions.assertEquals(CommonError.COM_DB_ERROR, e.getError());
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11update() {
		State state = State.DELETED;
		entityResult.setState(state);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityResult.toString()));
		Sample result = service.update(entityResult);
		log.debug("result: {}", result.getState());
		Assertions.assertEquals(state, result.getState());

		log.debug(LoggingConfig.ONE_LINE_100);
		Sample entityTemp = Sample.builder()
				.nulls(ImmutableList.of("descp"))
				.build();
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		Sample search = Sample.builder()
				.id(entityResult.getId())
				.build();
		log.debug("search: {}", JsonUtil.toPrettyString(search.toString()));
		result = service.update(entityTemp, search);
		log.debug("result: {}", result.getDescp());
		Assertions.assertNull(result.getDescp());
	}

	@Test
	void t12merge() {
		Sample entityTemp = service.findById(entityResult.getId());
		entityTemp.setId(null);
		log.debug("entity: {}", JsonUtil.toPrettyString(entityTemp.toString()));
		Sample result = service.merge(entityTemp);
		log.debug("result: {}", JsonUtil.toPrettyString(result.toString()));
		entityResult2.setId(result.getId());
		Assertions.assertTrue(result.getId() > 0);

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
		Sample entity1 = Sample.builder()
				.id(entityResult.getId())
				.state(state)
				.build();
		Sample entity2 = Sample.builder()
				.id(entityResult2.getId())
				.state(state)
				.build();
		List<Sample> entities = ImmutableList.of(entity1, entity2);
		log.debug("entities: {}", JsonUtil.toPrettyString(entities));
		List<Sample> result = service.update(entities);
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

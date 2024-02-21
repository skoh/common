package org.oh.common.util;

import org.oh.common.model.enume.State;
import org.oh.sample.model.Sample;
import org.oh.sample.service.SampleDbServiceTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.Date;
import java.util.List;

//@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class JsonUtilTest {
	private static final String FIELD_NAMES = "id,name,descp,state,regDate,modDate";
	private static final String HEADER = "아이디,이름,설명,상태,등록일시,수정일시";

	private static final Sample entity = JsonUtil.copyAndMerge(SampleDbServiceTest.ENTITY, Sample.builder()
			.id(1L)
			.name("a")
			.descp("b")
			.state(State.ACTIVE)
			.regDate(new Date())
			.modDate(new Date())
			.build(), Sample.class);
	private static List<Sample> entities;
	private static String csv;

	@Test
	void t01toCsv() throws IOException {
		entities = ImmutableList.of(entity, JsonUtil.copyAndMerge(entity, Sample.builder()
				.id(2L)
				.state(State.DELETED)
				.build(), Sample.class));
		log.debug("entity:\n{}", JsonUtil.toPrettyString(entities));

		csv = JsonUtil.toCsv(entities, ",");
		log.debug("result:\n{}", csv);

		csv = JsonUtil.toCsv(entities, ",", FIELD_NAMES, HEADER);
		log.debug("result2:\n{}", csv);

//		FileUtils.write(new File("test.csv"), csv, Charset.forName("EUC-KR"));
	}

	@Test
	void t02toPojo() {
		CsvSchema csvSchema = JsonUtil.getCvsScheme(FIELD_NAMES, ",", false);
		csv = JsonUtil.toCvs(entities, csvSchema, null);

		List<Sample> result = JsonUtil.toPojo(csv, csvSchema, Sample.class);
		log.debug("result:\n{}", JsonUtil.toPrettyString(result));
	}

	@Test
	void t03test() throws JsonProcessingException {
		JsonizerItem item = JsonUtil.OBJECT_MAPPER.readValue("{\"partitionvalue\" : \"test\"}", JsonizerItem.class);
		log.debug("item: {}", item);
		Assertions.assertEquals("test", item.getPartitionvalue());
	}

	@Data
//	@SuperBuilder
//	@NoArgsConstructor
//	@Jacksonized
	protected static class JsonizerItem {
		private String partitionvalue;
	}
}

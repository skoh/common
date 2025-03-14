package org.oh.common.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.oh.common.config.LoggingConfig;
import org.oh.common.model.data.Paging;
import org.oh.common.util.JsonUtil;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
//@Disabled
@TestMethodOrder(MethodOrderer.MethodName.class)
class CrudServiceTest {
	@Test
	void t01groupingBy() {
		List<Map<String, Object>> list = ImmutableList.of(
				ImmutableMap.of("exam_no", "1234", "exam_nm", "홍길동", "doc_knd_nm", "주민등록표등본", "cnt", 1),
				ImmutableMap.of("exam_no", "1235", "exam_nm", "김철수", "doc_knd_nm", "주민등록표등본", "cnt", 1),
				ImmutableMap.of("exam_no", "1234", "exam_nm", "홍길동", "doc_knd_nm", "국가기술자격증", "cnt", 1),
				ImmutableMap.of("exam_no", "1236", "exam_nm", "김하늘", "doc_knd_nm", "국가기술자격증", "cnt", 1),
				ImmutableMap.of("exam_no", "1236", "exam_nm", "김하늘", "doc_knd_nm", "국가기술자격증", "cnt", 1)
		);
		log.debug("list: {}", JsonUtil.toPrettyString(list));
		log.debug(LoggingConfig.ONE_LINE_100);

		List<Map<String, Object>> result = AbstractCrudService.groupingBy(list,
				a -> Arrays.asList(a.get("exam_no"), a.get("exam_nm")),
				"doc_cnt", "|", "doc_knd_nm", "cnt");
		log.debug("result: {}", JsonUtil.toPrettyString(result));
		log.debug(LoggingConfig.ONE_LINE_100);

		Paging paging = Paging.builder()
				.page(1)
				.psize(2)
				.build();
		Page<Map<String, Object>> page = AbstractCrudService.getPage(result, paging);
		log.debug("page: {}", JsonUtil.toPrettyString(page));
	}
}

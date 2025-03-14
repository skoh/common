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

package org.oh.sample.repository.impl;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.oh.common.model.data.Query;
import org.oh.common.model.data.Sorting;
import org.oh.common.repository.AbstractDbRepository;
import org.oh.sample.model.Sample;
import org.oh.sample.model.StatsParams;
import org.oh.sample.model.StatsResult;
import org.oh.sample.repository.SampleJpqlRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 샘플 레파지토리와 동적 쿼리 구현체
 */
@Slf4j
@RequiredArgsConstructor
public class SampleDbRepositoryImpl
		extends AbstractDbRepository
		implements SampleJpqlRepository {
	public static final String VALUE_DELIMITER = "|";

//	@Value("${" + CommonConfig.APP_PREFIX + ".mapper.vendor:h2}")
//	private CrudMapper.Vendor vendor;

	/**
	 * 쿼리 결과를 현황 결과 객체에 담는다.
	 *
	 * @param cols 쿼리 결과
	 * @return 현황 결과
	 */
	public static StatsResult createStatsResult(Object... cols) {
		int optionsStartIndex = 2;
//		int optionIndex = 3;
		Assert.isTrue(cols.length >= optionsStartIndex,
				String.format("The number(%d) of a arguments must be greater than or equal to %d.",
						cols.length, optionsStartIndex));

		BasicNameValuePair[] options = null;
		if (cols.length >= optionsStartIndex + 1) {
			options = Arrays.stream(cols, optionsStartIndex, cols.length)
					.map(e -> {
						String[] values = StringUtils.split((String) e, VALUE_DELIMITER);
						return new BasicNameValuePair(values[0], values[1]);
					})
					.toArray(BasicNameValuePair[]::new);
		}

		return StatsResult.builder()
				.date((String) cols[0])
//				.state(((State) cols[1]).name())
				.total(convertLong(cols[1]))
				.options(options)
				.build();
	}

	/**
	 * 해당 객체를 long 형태로 변환
	 *
	 * @param obj 객체
	 * @return long 형태
	 */
	public static long convertLong(Object obj) {
		return obj instanceof BigDecimal ? ((BigDecimal) obj).longValue() : (long) obj;
	}

	private final ApplicationContext applicationContext;
	private final DataSource dataSource;

	@Override
	public List<Sample> search(Sample entity, StatsParams params, Sorting sort) {
		// Query
		Query query = createQuery(entity, params);
		query.setSelect("DISTINCT a");
		query.setOrderBy(sort.toString());
//		query.setSelect("new org.oh.didm.model.Sample(a.id)");
		return findAll(query, Sample.class);

		// String
//		Map<String, Object> parameters = new HashMap<>();
//		String jpql = "SELECT DISTINCT a" +
//				"  FROM Sample a" +
//				" WHERE 1 = 1";
//		jpql += setParameters(entity, params, parameters);
//		jpql += " ORDER BY " + sort;
//		return findAll(jpql, parameters, 0, Sample.class);
	}

	@Override
	public Page<Sample> search(Sample entity, StatsParams params, Pageable pageable) {
		// Query
		Query query = createQuery(entity, params);
		query.setSelect("DISTINCT a");
		return findPage(query, pageable, Sample.class);

		// String
//		Map<String, Object> parameters = new HashMap<>();
//		String jpql = "SELECT DISTINCT a" +
//				"  FROM Sample a" +
//				" WHERE 1 = 1";
//		jpql += setParameters(entity, params, parameters);
//		return findPage(jpql, parameters, pageable, Sample.class);
	}

	@Override
	public List<StatsResult> stats(Sample entity, StatsParams params, Sorting sort) {
		// Query
		String groupBy = "DATE_FORMAT(a.regDate, '%Y-%m-%d')";//, a.state";
//		String groupBy = "FUNCTION('DATE_FORMAT', a.regDate, '%Y-%m-%d')";
//		if (vendor == CrudMapper.Vendor.H2) {
//			groupBy = "FORMATDATETIME(a.regDate, '%yyyy-MM-dd')"; // error
//			groupBy = "FUNCTION('FORMATDATETIME', a.regDate, 'yyyy-MM-dd')"; // error
//		}
		StringBuilder select = new StringBuilder(groupBy + " AS date, COUNT(1) AS total");
		List<String> states = ImmutableList.of("ACTIVE", "DELETED");
		if (!states.isEmpty()) {
			select.append(", ");
			select.append(states.stream()
					.map(a -> "CONCAT('" + a + "', '" + VALUE_DELIMITER +
							"', SUM(CASE WHEN a.state = '" + a + "' THEN 1 ELSE 0 END))")
					.collect(Collectors.joining(", ")));
		}

		Query query = createQuery(entity, params);
		query.setSelect(select.toString());
		query.setGroupBy(groupBy);
		query.setOrderBy(sort.toString());
		List<Object[]> result = findAll(query, Object[].class);

		// String
//		Map<String, Object> parameters = new HashMap<>();
//		String jpql = "SELECT DATE_FORMAT(a.regDate, '%Y-%m-%d') AS date," +
//				" COUNT(1) AS total," +
//				" CONCAT('ACTIVE', '|', SUM(CASE WHEN a.state = 'ACTIVE' THEN 1 ELSE 0 END))," +
//				" CONCAT('DELETED', '|', SUM(CASE WHEN a.state = 'DELETED' THEN 1 ELSE 0 END))" +
//				"  FROM Sample a" +
//				" WHERE 1 = 1";
//		jpql += setParameters(entity, params, parameters);
//		jpql += " GROUP BY DATE_FORMAT(a.regDate, '%Y-%m-%d')" +
//				" ORDER BY state asc, id desc";
//		List<Object[]> result = findAll(jpql, parameters, 0, Object[].class);

		return result.stream()
				.map(SampleDbRepositoryImpl::createStatsResult)
				.collect(Collectors.toList());
	}

	@Override
	public void init() {
		Connection connection = DataSourceUtils.getConnection(dataSource);
		Resource resource = applicationContext.getResource("classpath:init.sql");
		ScriptUtils.executeSqlScript(connection, resource);
	}

	/**
	 * 해당 조건으로 쿼리 객체를 생성
	 *
	 * @param entity 샘플 조건
	 * @param params 일자 조건
	 * @return 쿼리 문자열
	 */
	private static Query createQuery(Sample entity, StatsParams params) {
		Map<String, Object> parameters = new HashMap<>();
		String where = setParameters(entity, params, parameters);
		return Query.builder()
				.from("Sample a")
				.where(where)
				.parameters(parameters)
				.build();
	}

	/**
	 * 해당 조건으로 WHERE 절과 파라미터를 설정
	 *
	 * @param entity     샘플 조건
	 * @param params     일자 조건
	 * @param parameters 파라미터
	 * @return WHERE 절
	 */
	private static String setParameters(Sample entity, StatsParams params, Map<String, Object> parameters) {
		String where = "";
		if (StringUtils.isNotEmpty(entity.getName())) {
			where += " AND a.name = :name";
			parameters.put("name", entity.getName());
		}
		where += params.setParameters(parameters);
		return where;
	}
}

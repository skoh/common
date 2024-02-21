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

package org.oh.common.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.web.SortHandlerMethodArgumentResolverSupport;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 정렬 정보
 */
@Schema(description = "정렬")
@Data
@SuperBuilder
@NoArgsConstructor
public class Sorting {
	/**
	 * Spring 정렬 정보를 쿼리 문자열로 변환
	 *
	 * @param sort Spring 정렬 정보
	 * @return 쿼리 문자열
	 */
	public static String toString(Sort sort) {
		return org.apache.commons.lang3.StringUtils.substringAfter(
				QueryUtils.applySorting("#", sort), " order by ").trim();
	}

	/**
	 * 정렬 기준
	 * <pre>
	 * 사용 방법은 컬럼명[,asc|desc] 형식입니다.
	 * 기본 정렬 순서는 오름차순이며, 여러 정렬 기준을 지원됩니다.
	 * 예) id,desc"
	 * </pre>
	 */
//	@Schema(description = "Sorting criteria in the format: parameter[,asc|desc]." +
//			" Default sort order is ascending. Multiple sort criteria are supported.")
	@Schema(description = "정렬 기준\n" +
			"사용 방법은 컬럼명[,asc|desc] 형식입니다.\n" +
			"기본 정렬 순서는 오름차순이며, 여러 정렬 기준을 지원됩니다.\n" +
			"예) id,desc")
	@Builder.Default
	protected String[] sort = new String[]{};

	/**
	 * Spring 정렬 정보로 변환
	 *
	 * @return Spring 정렬 정보
	 */
	public Sort sortable() {
		return PageRequest.of(1, 1, parseParameterIntoSort(sort, ",")).getSort();
	}

	/**
	 * 정렬 기준이 비어있는지 여부
	 *
	 * @return 정렬 공백 여부
	 */
	public boolean isEmpty() {
		return org.apache.commons.lang3.StringUtils.isEmpty(toString());
	}

	/**
	 * 정렬 기준이 안 비어있는지 여부
	 *
	 * @return 정렬 비공백 여부
	 */
	public boolean isNotEmpty() {
		return !isEmpty();
	}

	@Override
	public String toString() {
		return toString(sortable());
	}

	/**
	 * Copy from {@link SortHandlerMethodArgumentResolverSupport#parseParameterIntoSort(List, String)}
	 */
	public static Sort parseParameterIntoSort(String[] source, String delimiter) {
		List<Sort.Order> allOrders = new ArrayList<>();

		for (String part : source) {

			if (part == null) {
				continue;
			}

			SortOrderParser.parse(part, delimiter) //
					.parseIgnoreCase() //
					.parseDirection() //
					.forEachOrder(allOrders::add);
		}

		return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
	}

	/**
	 * Returns whether the given source {@link  String} consists of dots only.
	 *
	 * @param source must not be {@literal null}.
	 * @return
	 */
	static boolean notOnlyDots(String source) {
		return StringUtils.hasText(source.replace(".", ""));
	}

	/**
	 * Parser for sort {@link Sort.Order}.
	 *
	 * @author Mark Paluch
	 * @since 2.3
	 */
	static final class SortOrderParser {
		private static final String IGNORE_CASE = "ignorecase";

		private final String[] elements;
		private final int lastIndex;
		private final Optional<Sort.Direction> direction;
		private final Optional<Boolean> ignoreCase;

		private SortOrderParser(String[] elements) {
			this(elements, elements.length, Optional.empty(), Optional.empty());
		}

		private SortOrderParser(String[] elements, int lastIndex, Optional<Sort.Direction> direction,
								Optional<Boolean> ignoreCase) {
			this.elements = elements;
			this.lastIndex = Math.max(0, lastIndex);
			this.direction = direction;
			this.ignoreCase = ignoreCase;
		}

		/**
		 * Parse the raw sort string delimited by {@code delimiter}.
		 *
		 * @param part      sort part to parse.
		 * @param delimiter the delimiter to be used to split up the source elements, will never be {@literal null}.
		 * @return the parsing state object.
		 */
		public static SortOrderParser parse(String part, String delimiter) {
			String[] elements = Arrays.stream(part.split(delimiter)) //
					.filter(Paging::notOnlyDots) //
					.toArray(String[]::new);

			return new SortOrderParser(elements);
		}

		/**
		 * Parse the {@code ignoreCase} portion of the sort specification.
		 *
		 * @return a new parsing state object.
		 */
		public SortOrderParser parseIgnoreCase() {
			Optional<Boolean> ignoreCase = lastIndex > 0
					? fromOptionalString(elements[lastIndex - 1]) : Optional.empty();

			return new SortOrderParser(elements, lastIndex - (ignoreCase.isPresent() ? 1 : 0),
					direction, ignoreCase);
		}

		/**
		 * Parse the {@link Sort.Order} portion of the sort specification.
		 *
		 * @return a new parsing state object.
		 */
		public SortOrderParser parseDirection() {
			Optional<Sort.Direction> direction = lastIndex > 0
					? Sort.Direction.fromOptionalString(elements[lastIndex - 1]) : Optional.empty();

			return new SortOrderParser(elements, lastIndex - (direction.isPresent() ? 1 : 0),
					direction, ignoreCase);
		}

		/**
		 * Notify a {@link Consumer callback function} for each parsed {@link Sort.Order} object.
		 *
		 * @param callback block to be executed.
		 */
		public void forEachOrder(Consumer<? super Sort.Order> callback) {
			for (int i = 0; i < lastIndex; i++) {
				toOrder(elements[i]).ifPresent(callback);
			}
		}

		private Optional<Boolean> fromOptionalString(String value) {
			return IGNORE_CASE.equalsIgnoreCase(value) ? Optional.of(true) : Optional.empty();
		}

		private Optional<Sort.Order> toOrder(String property) {
			if (!StringUtils.hasText(property)) {
				return Optional.empty();
			}

			Sort.Order order = direction.map(it -> new Sort.Order(it, property))
					.orElseGet(() -> Sort.Order.by(property));

			if (ignoreCase.isPresent()) {
				return Optional.of(order.ignoreCase());
			}

			return Optional.of(order);
		}
	}
}

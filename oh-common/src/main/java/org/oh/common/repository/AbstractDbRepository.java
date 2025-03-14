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

package org.oh.common.repository;

import lombok.extern.slf4j.Slf4j;
import org.oh.common.model.data.Query;
import org.oh.common.util.QueryUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 기본 DB 레파지토리
 */
@Slf4j
public abstract class AbstractDbRepository
		implements DbRepository {
	@PersistenceContext
	protected EntityManager entityManager;

	@Override
	public <T> T find(String query, Map<String, Object> parameters, Class<T> resultType) {
		TypedQuery<T> typedQuery = getTypeQuery(query, resultType);
		return getSingleResult(typedQuery, parameters);
	}

	@Override
	public <T> T find(Query query, Class<T> resultType) {
		return find(query.toString(), query.getParameters(), resultType);
	}

	///////////////////////////////////

	@Override
	public <T> List<T> findAll(String query, Map<String, Object> parameters, int lsize, Class<T> resultType) {
		TypedQuery<T> typedQuery = getTypeQuery(query, resultType);
		return getResultList(typedQuery, parameters, lsize);
	}

	@Override
	public <T> List<T> findAll(Query query, Class<T> resultType) {
		return findAll(query, 0, resultType);
	}

	@Override
	public <T> List<T> findAll(Query query, int lsize, Class<T> resultType) {
		return findAll(query.toString(), query.getParameters(), lsize, resultType);
	}

	///////////////////////////////////

	@Override
	public <T> Page<T> findPage(String query, Map<String, Object> parameters, Pageable pageable, Class<T> resultType) {
		return findPage(query, parameters, null, pageable, resultType);
	}

	@Override
	public <T> Page<T> findPage(String query, Map<String, Object> parameters, String countProjection,
								Pageable pageable, Class<T> resultType) {
		String qlString = QueryUtils.applySorting(query, pageable.getSort());
		TypedQuery<T> typedQuery = getTypeQuery(qlString, resultType);

		String countQuery = QueryUtils.createCountQueryFor(query, countProjection);
		countQuery = countQuery.replace(" FETCH", "");
		TypedQuery<Long> typedCountQuery = getTypeQuery(countQuery, Long.class);

		return getResultPage(typedQuery, parameters, typedCountQuery, pageable);
	}

	@Override
	public <T> Page<T> findPage(Query query, Pageable pageable, Class<T> resultType) {
		return findPage(query.toString(), query.getParameters(), pageable, resultType);
	}

	@Override
	public <T> Page<T> findPage(Query query, String countProjection, Pageable pageable, Class<T> resultType) {
		return findPage(query.toString(), query.getParameters(), countProjection, pageable, resultType);
	}

	///////////////////////////////////

	@Override
	public int update(String query, Map<String, Object> parameters) {
		javax.persistence.Query ql = QueryUtil.setParameters(getQuery(query), parameters);
		return ql.executeUpdate();
	}

	///////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	protected <T> T getSingleResult(TypedQuery<T> query, Map<String, Object> parameters) {
		return (T) QueryUtil.setParameters(query, parameters).getSingleResult();
	}

	protected <T> List<T> getResultList(TypedQuery<T> query, Map<String, Object> parameters) {
		return getResultList(query, parameters, 0);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> getResultList(TypedQuery<T> query, Map<String, Object> parameters, int lsize) {
		if (lsize > 0) {
			query.setMaxResults(lsize);
		}
		javax.persistence.Query ql = QueryUtil.setParameters(query, parameters);
		return ql.getResultList();
	}

	protected <T> Page<T> getResultPage(TypedQuery<T> query, Map<String, Object> parameters,
										TypedQuery<Long> countQuery, Pageable pageable) {
		List<Long> result = getResultList(countQuery, parameters);
		long total = result.size() == 1 ? result.get(0) : result.size();

		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());
		List<T> content = total > pageable.getOffset() ? getResultList(query, parameters) :
				Collections.emptyList();

		return PageableExecutionUtils.getPage(content, pageable, () -> total);
	}

	protected <T> TypedQuery<T> getTypeQuery(String query, Class<T> resultType) {
		log.debug("JPQL: {}", query);
		return entityManager.createQuery(query, resultType); //NOSONAR setParameters()를 처리하는 메소드가 따로 있음
	}

	protected javax.persistence.Query getQuery(String query) {
		log.debug("JPQL: {}", query);
		return entityManager.createQuery(query); //NOSONAR setParameters()를 처리하는 메소드가 따로 있음
	}
}

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

package org.oh.common.service;

import org.oh.common.model.Model;
import org.oh.common.model.data.Paging;
import org.oh.common.model.data.Sorting;
import org.oh.common.repository.CrudDbRepository;
import org.oh.common.util.QueryUtil;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 기본 CRUD DB 서비스
 */
public abstract class AbstractCrudDbService<T extends Model<ID>, ID>
		extends AbstractCrudService<T, ID>
		implements CrudDbService<T, ID> {
	@PersistenceContext
	protected EntityManager entityManager;

	protected final AbstractCrudDbService<T, ID> self;
	protected final CrudDbRepository<T, ID> repository;

	protected AbstractCrudDbService(AbstractCrudDbService<T, ID> self,
									CrudDbRepository<T, ID> repository) {
		super(self, repository);
		this.self = self;
		this.repository = repository;
	}

	@Override
	public void clear() {
		entityManager.clear();
	}

	@Override
	public void flush() {
		entityManager.flush();
	}

	@Override
	public void deleteAll(Collection<T> entities) {
		repository.deleteAllInBatch(entities);
	}

	@Override
	public void deleteAll() {
		repository.deleteAllInBatch();
	}

	///////////////////////////////////////////////////////////////////////////

	@Override
	public T find(T entity) {
		Optional<T> result = self.findOrEmpty(entity);
		return throwNotFound(result.orElse(null), entity);
	}

	@Override
	public T find(T entity, Sorting sort) {
		Optional<T> result = self.findOrEmpty(entity, sort);
		return throwNotFound(result.orElse(null), entity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Optional<T> findOrEmpty(T entity) {
		ExampleMatcher matcher = QueryUtil.getMatcher(entity);
		return repository.findOne(Example.of(entity, matcher))
				.map(a -> (T) a.sort());
	}

	@Override
	public Optional<T> findOrEmpty(T entity, Sorting sort) {
		return Optional.of(self.findAllOrEmpty(entity, 1, sort))
				.filter(a -> !a.isEmpty())
				.map(a -> a.get(0));
	}

	@Override
	public List<T> findAll(T entity) {
		List<T> result = self.findAllOrEmpty(entity);
		return throwNotFound(result, entity);
	}

	@Override
	public List<T> findAllOrEmpty(T entity) {
		return self.findAllOrEmpty(entity, new Sorting());
	}

	@Override
	public List<T> findAll(T entity, Sorting sort) {
		List<T> result = self.findAllOrEmpty(entity, sort);
		return throwNotFound(result, entity);
	}

	@Override
	public List<T> findAllOrEmpty(T entity, Sorting sort) {
		ExampleMatcher matcher = QueryUtil.getMatcher(entity);
		List<T> result = repository.findAll(Example.of(entity, matcher), sort.sortable());
		result.forEach(Model::sort);
		return result;
	}

	@Override
	public List<T> findAll(T entity, int lsize, Sorting sort) {
		List<T> result = self.findAllOrEmpty(entity, lsize, sort);
		return throwNotFound(result, entity);
	}

	@Override
	public List<T> findAllOrEmpty(T entity, int lsize, Sorting sort) {
		Paging page = Paging.builder()
				.page(1)
				.psize(lsize)
				.sort(sort.getSort())
				.build();
		return self.findPageOrEmpty(entity, page).getContent();
	}

	@Override
	public Page<T> findPage(T entity, Paging page) {
		Page<T> result = self.findPageOrEmpty(entity, page);
		return throwNotFound(result, entity);
	}

	@Override
	public Page<T> findPageOrEmpty(T entity, Paging page) {
		ExampleMatcher matcher = QueryUtil.getMatcher(entity);
		Page<T> result = repository.findAll(Example.of(entity, matcher), page.pageable());
		result.getContent()
				.forEach(Model::sort);
		return result;
	}

	@Override
	public long count(T entity) {
		ExampleMatcher matcher = QueryUtil.getMatcher(entity);
		return repository.count(Example.of(entity, matcher));
	}

	@Override
	public boolean exists(T entity) {
		ExampleMatcher matcher = QueryUtil.getMatcher(entity);
		return repository.exists(Example.of(entity, matcher));
	}

	@Override
	public T update(T entity, T search) {
		preSave(entity);
		T des = self.find(search);

		return updateInternal(entity, des);
	}

	@Override
	public void delete(T entity) {
		throwEmpty(entity);

		self.deleteAll(self.findAll(entity));
	}
}

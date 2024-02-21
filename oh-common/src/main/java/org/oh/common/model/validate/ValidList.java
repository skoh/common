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

package org.oh.common.model.validate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * List의 유효성을 체크하기 위한 List
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ValidList<E>
		extends AbstractList<E> {
	@Valid
	protected List<E> list = new ArrayList<>();

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public E set(int index, E element) {
		return list.set(index, element);
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
	}

	@Override
	public E remove(int index) {
		return list.remove(index);
	}
}

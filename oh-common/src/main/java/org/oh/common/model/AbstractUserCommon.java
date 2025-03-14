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

package org.oh.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.oh.common.model.enume.State;
import org.oh.common.model.user.AbstractUser;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import java.util.Optional;

/**
 * 공통 사용자 모델 (등록자와 수정자를 포함한 모델)
 */
// Swagger2 성능 문제로 @Getter 제거
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractUserCommon<T extends AbstractUser, ID>
		extends AbstractModel<ID> {
	/**
	 * 등록자
	 */
	@JsonProperty(index = 810)
	@Schema(description = "등록자")
	@JsonView(CommonModel.Ignore.class)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	protected T register;

	/**
	 * 수정자
	 */
	@JsonProperty(index = 820)
	@Schema(description = "수정자")
	@JsonView(CommonModel.Ignore.class)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	protected T modifier;

	/**
	 * 등록자 반환
	 *
	 * @return 등록자
	 */
	public T register() {
		return register;
	}

	/**
	 * 수정자 반환
	 *
	 * @return 수정자
	 */
	public T modifier() {
		return modifier;
	}

	/**
	 * 등록자와 수정자를 해당 사용자로 설정
	 *
	 * @param user 사용자
	 */
	@SuppressWarnings("unchecked")
	public void setUser(AbstractUser user) {
		if (id() == null && register == null) {
			register = (T) user;
		}
		if (modifier == null) {
			modifier = (T) user;
		}
	}

	@Override
	public void setInsertValue() {
		super.setInsertValue();
		Optional.ofNullable(register)
				.filter(a -> a.getState() == null)
				.ifPresent(a -> a.setState(State.ACTIVE));
		Optional.ofNullable(modifier)
				.filter(a -> a.getState() == null)
				.ifPresent(m -> m.setState(State.ACTIVE));
	}
}

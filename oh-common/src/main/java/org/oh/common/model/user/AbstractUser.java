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

package org.oh.common.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.AbstractCommon;
import org.oh.common.model.CommonModel;
import org.oh.common.model.enume.State;
import org.oh.common.util.SecurityUtil;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

import java.util.Optional;

/**
 * 기본 사용자 모델
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractUser
		extends AbstractCommon {
	public static final String NAME_SPACE = "users";

	/**
	 * 권한 구분자
	 */
	public static final String ROLES_SEPARATOR = ",";

	/**
	 * 관리자 아이디
	 */
	public static final String ADMIN_ID = "admin";

	/**
	 * 암호
	 */
	@JsonProperty(index = 710)
	@Schema(description = "암호 - 필수값")
	@JsonView(CommonModel.Ignore.class)
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("암호")
	protected String password;

	/**
	 * 접근 권한들(ROLE_USER:사용자, ROLE_MANAGER:관리자, ROLE_ADMIN:슈퍼관리자)
	 */
	@JsonProperty(index = 720)
	@Schema(description = "접근 권한들(ROLE_USER:사용자, ROLE_MANAGER:관리자, ROLE_ADMIN:슈퍼관리자)")
	@Column(length = 100, nullable = false, columnDefinition = State.ENUM)
	@ColumnDefault(Role.DEFAULT)
	@Comment("접근 권한들(ROLE_USER:사용자, ROLE_MANAGER:관리자, ROLE_ADMIN:슈퍼관리자)")
	protected String roles;

	@Override
	public String toString() {
		return toString(CommonModel.User.One.class);
	}

	@Override
	public void setInsertValue() {
		super.setInsertValue();
		if (roles == null) {
			roles = Role.ROLE_USER.name();
		}
	}

	@Override
	public AbstractUser encrypt() {
		super.encrypt();
		// 1번만 암호화
		Optional.ofNullable(password)
				.filter(a -> a.length() != 60)
				.ifPresent(a -> password = SecurityUtil.PASSWORD_ENCODER.encode(a));
		return this;
	}

	/**
	 * 비밀번호가 일치하는지 확인
	 *
	 * @param encPassword 암호화된 비밀번호
	 */
	public void checkEncPassword(String encPassword) {
		if (!SecurityUtil.PASSWORD_ENCODER.matches(password, encPassword)) {
			throw new CommonException(CommonError.COM_FAILED_CREDENTIALS,
					String.format("encPassword: %s entity password: %s", encPassword, password));
		}
	}

	/**
	 * 비밀번호가 일치하는지 확인
	 *
	 * @param password 비밀번호
	 */
	public void checkPassword(String password) {
		if (!SecurityUtil.PASSWORD_ENCODER.matches(password, this.password)) {
			throw new CommonException(CommonError.COM_INVALID_PASSWORD,
					String.format("password: %s entity password: %s", password, this.password));
		}
	}
}

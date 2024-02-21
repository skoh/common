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

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;

/**
 * 비밀번호 변경 정보
 */
@Schema(description = "비밀번호 변경 정보")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChangePassword
		extends AbstractUser {
	/**
	 * 새 비밀번호
	 */
	@JsonProperty(index = 10)
	@Schema(description = "새 비밀번호 - 필수값")
	@NotBlank
	protected String newPassword;

	/**
	 * 확인 비밀번호
	 */
	@JsonProperty(index = 20)
	@Schema(description = "확인 비밀번호 - 필수값")
	@NotBlank
	protected String confirmPassword;

	/**
	 * 새 비밀번호와 확인 비밀번호가 일치하는지 확인
	 */
	public void confirmPassword() {
		if (!newPassword.equals(confirmPassword)) {
			throw new CommonException(CommonError.COM_INVALID_NEW_PASSWORD,
					String.format("newPassword: %s changePassword: %s", newPassword, confirmPassword));
		}
	}
}

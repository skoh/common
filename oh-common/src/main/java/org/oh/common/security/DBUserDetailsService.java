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

package org.oh.common.security;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.oh.common.config.CommonConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.user.AbstractUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Optional;

/**
 * DB 기반의 사용자 서비스
 */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
//@Service
@ConditionalOnProperty(value = "enabled", prefix = CommonConfig.APP_PREFIX + ".console", havingValue = "true")
public class DBUserDetailsService
		extends PropertyUserDetailsService {
//	protected final DidUserRepository repository;

	@SuppressWarnings({"unchecked"})
	@Override
	protected <T extends AbstractUser> Optional<T> findOrEmpty(String username) {
//		return (Optional<T>) repository.findById(username);
		throw new CommonException(CommonError.COM_NOT_SUPPORTED);
	}
}

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

import org.oh.common.config.SecurityConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Login;
import org.oh.common.util.SecurityUtil;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 설정 기반의 사용자 서비스
 */
@Data
@Service
@Validated
@ConfigurationProperties(SecurityConfig.SECURITY_PREFIX)
@ConditionalOnProperty(value = "default-users[0].id", prefix = SecurityConfig.SECURITY_PREFIX)
public class PropertyUserDetailsService
		implements UserDetailsService {
	private boolean enabled;
	private List<Login> defaultUsers;
	private List<Login> appendUsers;
	private List<Login> users;

	@NestedConfigurationProperty
	private SecurityConfig.Include include = new SecurityConfig.Include();
	@NestedConfigurationProperty
	private SecurityConfig.Exclude exclude = new SecurityConfig.Exclude();

	@PostConstruct
	private void init() {
		if (defaultUsers != null) {
			users = new ArrayList<>(defaultUsers);
		}
		if (appendUsers != null) {
			users = Stream.concat(users.stream(), appendUsers.stream())
					.collect(Collectors.toList());
		}
	}

	/**
	 * 사용자 아이디로 사용자 정보를 찾음
	 *
	 * @param username 사용자 아이디
	 * @return 사용자 정보
	 */
	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<? extends AbstractUser> user = findOrEmpty(username);
		return user.map(u -> getSecurityUser(username, u))
				.orElseThrow(() -> new UsernameNotFoundException(username + " 사용자를 찾을 수 없습니다."));
	}

	@SuppressWarnings({"unchecked"})
	protected <T extends AbstractUser> Optional<T> findOrEmpty(String username) {
		return (Optional<T>) Optional.ofNullable(users)
				.flatMap(a -> a.stream()
						.filter(e -> e.getId().equals(username))
						.findFirst());
	}

	private org.springframework.security.core.userdetails.User getSecurityUser(String username, AbstractUser user) {
		if (!user.activated()) {
			throw new CommonException(CommonError.COM_NOT_ACTIVATED, "username: " + username);
		}
		String[] roleNames = user.getRoles()
				.split(AbstractUser.ROLES_SEPARATOR);
		List<GrantedAuthority> grantedAuthorities = SecurityUtil.getGrantedAuthorities(roleNames);
		return new org.springframework.security.core.userdetails.User(
				user.getId(), user.getPassword(), grantedAuthorities);
	}
}

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

package org.oh.common.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.oh.common.config.SecurityConfig;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.exception.Error;
import org.oh.common.model.user.Login;
import org.oh.common.security.PropertyUserDetailsService;
import org.oh.common.util.JsonUtil;
import org.oh.common.util.SecurityUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 보안 필터
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Component
@Validated
@ConfigurationProperties(SecurityConfig.SECURITY_PREFIX + ".token")
@ConditionalOnProperty(value = "enabled", prefix = SecurityConfig.SECURITY_API_PREFIX, havingValue = "true")
public class SecurityFilter
		extends OncePerRequestFilter {
	public static final int DEFAULT_EXPIRE_TIME = 60 * 4;

	protected final PropertyUserDetailsService userDetailsService;
	protected final TextEncryptor textEncryptor;

	private String secret;
	private Integer expireTimeMin;
	private Key key;

	private static CommonException handleException(Error error, String token, Exception e) {
//		log.error(ExceptionUtil.getMessageAndType(e), e);
		return new CommonException(error, "token: " + token, e);
//		ThreadLocalUtil.set(FILTER_EXCEPTION, ce);
	}

	/**
	 * 보안에 대한 예외 핸들링
	 *
	 * @param request     HTTP 요청 정보
	 * @param response    HTTP 응답 정보
	 * @param filterChain 필터 체인
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (userDetailsService.isEnabled()) {
			SecurityUtil.getToken(request)
					.map(SecurityUtil::substringAfterBearer)
					.ifPresent(a -> {
						Jws<Claims> jwt = validateToken(a);
						Authentication auth = SecurityUtil.getAuthentication(jwt, a);
//						log.debug("'{}' save authentication info. {}", auth.getName(), WebUtil.getRequestInfo(request));
						SecurityContextHolder.getContext().setAuthentication(auth);
					});
		} else {
			validateToken(request);
		}

		filterChain.doFilter(request, response);
	}

	@PostConstruct
	private void init() {
		Optional.ofNullable(secret)
				.ifPresent(a -> key = SecurityUtil.createKey(a));
	}

	/**
	 * 해당 로그인 정보로 문자열 토큰을 반환
	 *
	 * @param login 로그인 정보
	 * @return 문자열 토큰
	 */
	public String createToken(Login login) {
		Date expireTime = setExpireDate(login.getExpireTimeMin()).getExpireDate();
		log.debug("login: {} expireTime: {}", login, expireTime);
		return SecurityUtil.createToken(login.getId(), login.getRoles(), key, expireTime);
	}

	/**
	 * 만료 시간을 설정한 로그인 정보를 반환
	 *
	 * @param expireTimeMin 만료 시간(분)
	 * @return 로그인 정보
	 */
	public Login setExpireDate(Integer expireTimeMin) {
		int expireTimeMinTemp = Optional.ofNullable(expireTimeMin)
				.orElseGet(() -> ObjectUtils.defaultIfNull(this.expireTimeMin, DEFAULT_EXPIRE_TIME));
		Date expireDate = DateUtils.addMinutes(new Date(), expireTimeMinTemp);
		return Login.builder()
				.expireTimeMin(expireTimeMinTemp)
				.expireDate(expireDate)
				.build();
	}

	/**
	 * 해당 문자열 토큰으로 토큰 정보를 반환
	 *
	 * @param token 문자열 토큰
	 * @return 토큰 정보
	 */
	protected Jws<Claims> validateToken(String token) {
		try {
			return SecurityUtil.getJws(key, token);

//			Claims claims = SecurityUtil.getJws(key, token).getBody();
//			String claimsTemp = String.format("expireTime: %s, %s: %s, %s: %s", claims.getExpiration(),
//					Claims.SUBJECT, claims.get(Claims.SUBJECT), AUTH_KEY, claims.get(AUTH_KEY));
//			log.debug("claims: {}", claimsTemp);
		} catch (IllegalArgumentException | MalformedJwtException | DecodingException e) {
			throw handleException(CommonError.COM_INVALID_TOKEN, token, e);
		} catch (SignatureException e) {
			throw handleException(CommonError.COM_INVALID_SIGNATURE, token, e);
		} catch (ExpiredJwtException e) {
			throw handleException(CommonError.COM_EXPIRED_TOKEN, token, e);
		} catch (UnsupportedJwtException e) {
			throw handleException(CommonError.COM_NOT_SUPPORTED_TOKEN, token, e);
		}
	}

	/**
	 * HTTP 요청에서 토큰의 유효성을 확인
	 *
	 * @param request HTTP 요청 정보
	 */
	private void validateToken(HttpServletRequest request) {
		String path = request.getServletPath();
		boolean include = Optional.ofNullable(userDetailsService.getInclude().getApiPaths())
				.map(a -> a.stream()
						.anyMatch(e -> path.startsWith(e.get(0))))
				.orElse(false);
		boolean exclude = Optional.ofNullable(userDetailsService.getExclude().getApiPaths())
				.map(a -> a.stream()
						.noneMatch(e -> path.startsWith(e.get(0))))
				.orElse(false);
		if (include && exclude) {
			String token = SecurityUtil.getToken(request)
					.orElseThrow(() -> new CommonException(CommonError.COM_NO_VALID_TOKEN));

			String userJson;
			try {
//				userJson = AESEncryptUtil.decrypt(token);
				userJson = textEncryptor.decrypt(token);
			} catch (IllegalArgumentException | IllegalStateException e) {
				throw handleException(CommonError.COM_INVALID_TOKEN, token, e);
			}
			Login login = JsonUtil.readValue(userJson, Login.class);
//			log.debug("user: {} {}", userJson, WebUtil.getRequestInfo(request));

			Date currentDate = new Date();
			if (login.getExpireDate() == null || login.getExpireDate().compareTo(currentDate) < 0) {
				throw new CommonException(CommonError.COM_EXPIRED_TOKEN,
						String.format("expireDate: %s currentDate: %s", login.getExpireDate(), currentDate));
			}

			List<Login> users = userDetailsService.getUsers();
			if (users != null && !users.isEmpty()) {
				Login user = users.get(0);
				if (!login.getPassword().equals(user.getPassword())) {
					throw new CommonException(CommonError.COM_FAILED_CREDENTIALS, "token: " + token);
				}
			}
		}
	}
}

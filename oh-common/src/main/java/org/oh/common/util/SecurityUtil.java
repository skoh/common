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

package org.oh.common.util;

import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Login;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 보안 유틸리티
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SecurityUtil {
	public static final String BEARER = "Bearer ";
	public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	public static final String ROLE = "role";
	public static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * 다수의 문자열을 기반으로 해시값(SHA256)을 반환
	 *
	 * @param keys 다수의 문자열
	 * @return 해시값
	 */
	public static String getHex(String... keys) {
		String key = getKeys(keys);
		return DigestUtils.sha256Hex(key);
	}

	/**
	 * 다수의 문자열을 기반으로 해시값(SHA512)을 반환
	 *
	 * @param keys 다수의 문자열
	 * @return 해시값
	 */
	public static String get512tHex(String... keys) {
		String key = getKeys(keys);
		return DigestUtils.sha512Hex(key);
	}

	/**
	 * 8 자리의 임시 암호를 반환
	 *
	 * @return 암호
	 */
	public static String createPassword() {
		return createPassword(8);
	}

	/**
	 * 길이에 맞는 임시 암호를 반환
	 *
	 * @param size 암호 길이
	 * @return 암호
	 */
	public static String createPassword(int size) {
		char[] charSet = new char[]{
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
				'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				'!', '@', '#', '$', '%', '^', '&'};
		RANDOM.setSeed(System.currentTimeMillis());
		StringBuilder sb = new StringBuilder();
		int len = charSet.length;
		int idx;
		for (int i = 0; i < size; i++) {
//			idx = (int) (len * Math.random());
			idx = RANDOM.nextInt(len);
			sb.append(charSet[idx]);
		}
		return sb.toString();
	}

	private static String getKeys(String[] keys) {
		return String.join("|", keys);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 암호로 서명 키를 반환
	 *
	 * @param secret 암호 (64 byte = 512 bit)
	 * @return 서명 키
	 */
	public static Key createKey(String secret) {
		byte[] keyBytes = secret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/**
	 * 해당 조건에 만족하는 토큰을 발행
	 *
	 * @param subject    사용자 아이디
	 * @param role       권한
	 * @param key        서명 키
	 * @param expiration 만료 일시
	 * @return 토큰
	 */
	public static String createToken(String subject, Object role, Key key, Date expiration) {
		return Jwts.builder()
				.setSubject(subject)
				.claim(ROLE, role)
				.signWith(key)
				.setExpiration(expiration)
				.compact();
	}

	/**
	 * HTTP 요청 정보에서 토큰 문자열을 반환
	 *
	 * @param request HTTP 요청 정보
	 * @return 문자열 토큰
	 */
	public static Optional<String> getToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
	}

	/**
	 * 토큰 문자열에서 "Bearer "을 제거하여 반환
	 *
	 * @param token 문자열 토큰
	 * @return "Bearer "을 제거한 문자열
	 */
	public static String substringAfterBearer(String token) {
		if (token.startsWith(BEARER)) {
			return StringUtils.substringAfter(token, BEARER);
		} else {
			return token;
		}
	}

	/**
	 * 토큰 정보로 인증 정보를 반환
	 *
	 * @param jwt   토큰 정보
	 * @param token 문자열 토큰
	 * @return 인증 정보
	 */
	public static Authentication getAuthentication(Jws<Claims> jwt, String token) {
		Claims claims = jwt.getBody();
		String[] roleNames = claims.get(ROLE).toString()
				.split(AbstractUser.ROLES_SEPARATOR);
		List<GrantedAuthority> gAuths = getGrantedAuthorities(roleNames);
		org.springframework.security.core.userdetails.User user =
				new org.springframework.security.core.userdetails.User(claims.getSubject(), "", gAuths);
		return new UsernamePasswordAuthenticationToken(user, token, gAuths);
	}

	/**
	 * 해당 권한 리스트로 권한 정보를 반환
	 *
	 * @param roleNames 권한 리스트
	 * @return 권한 정보
	 */
	public static List<GrantedAuthority> getGrantedAuthorities(String[] roleNames) {
		return Arrays.stream(roleNames)
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	/**
	 * 문자열 토큰에 서명 키를 설정한 토큰 정보를 반환
	 *
	 * @param key   서명 키
	 * @param token 문자열 토큰
	 * @return 토큰 정보
	 */
	public static Jws<Claims> getJws(Key key, String token) {
		JwtParser parser = Jwts.parserBuilder()
				.setSigningKey(key)
				.build();
		return parser.parseClaimsJws(token);
	}

	/**
	 * 인증 정보에서 사용자 정보를 반환
	 *
	 * @return 사용자 아이디
	 */
	public static Optional<AbstractUser> getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null) {
//			log.debug("Security Context에 인증 정보가 없습니다.");
			return Optional.empty();
		}

		AbstractUser user = null;
		if (auth.getPrincipal() instanceof UserDetails) {
			user = convertUser((UserDetails) auth.getPrincipal());
		} else if (auth.getPrincipal() instanceof String) {
			user = Login.builder()
					.id((String) auth.getPrincipal())
					.build();
		}

		return Optional.ofNullable(user);
	}

	/**
	 * 사용자 정보를 로그인 정보로 변환
	 *
	 * @param user 사용자 정보
	 * @return 로그인 정보
	 */
	public static Login convertUser(UserDetails user) {
		return Login.builder()
				.id(user.getUsername())
				.password(user.getPassword())
				.roles(getRoleNames(user.getAuthorities()))
				.build();
	}

	/**
	 * 해당 권한 정보로 권한 문자열을 반환
	 *
	 * @param auths 권한 정보
	 * @return 권한 문자열
	 */
	public static String getRoleNames(Collection<? extends GrantedAuthority> auths) {
		return auths.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(AbstractUser.ROLES_SEPARATOR));
	}
}

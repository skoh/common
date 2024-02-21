package org.oh.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.oh.common.filter.SecurityFilter;
import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.security.Key;
import java.util.Date;

import static org.oh.common.model.user.AbstractUser.ADMIN_ID;

//@Disabled
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SecurityUtilTest {
	public static final String USER_NAME = ADMIN_ID;
	public static final String ROLE_NAME = Role.ROLE_ADMIN.name();
	public static final Date EXPIRE_TIME = DateUtils.addMinutes(new Date(), 1_440);
	public static final Key KEY = SecurityUtil.createKey("1234567890123456789012345678901234567890123456789012345678901234");

	private static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwiZXhwIjo4ODAzOTE1NzU0OH0.pm7141sb_XGvDNkEUliI_dUW--eifrpdzy_2ZkOsDajfCxBmMfQTeWmeFHqBM2uUwmuMZlAVWwMRIYU6zrb8xw";

	@Test
	void t01jwt() {
		token = SecurityUtil.createToken(USER_NAME, ROLE_NAME, KEY, EXPIRE_TIME);
		log.debug("token: {}", token);

		Jws<Claims> jwt = SecurityUtil.getJws(KEY, token);
		Claims claims = jwt.getBody();

		String userName = claims.getSubject();
		log.debug("userName: {}", userName);
//		log.debug("userName: {}", body.get("sub"));
		Assertions.assertEquals(USER_NAME, userName);

		Object roleNames = claims.get(SecurityUtil.ROLE);
		log.debug("roleNames: {}", roleNames);
		Assertions.assertEquals(ROLE_NAME, roleNames);

		Date expTime = claims.getExpiration();
		log.debug("expTime: {}", expTime);
//		log.debug("expTime: {}", new Date((int) body.get("exp") * 1_000L));
		Assertions.assertEquals(EXPIRE_TIME.getTime() / 1_000, expTime.getTime() / 1_000);
	}

	@Test
	void t02auth() {
		Jws<Claims> jwt = SecurityUtil.getJws(KEY, token);
		Authentication auth = SecurityUtil.getAuthentication(jwt, token);

		String userName = auth.getName();
		log.debug("userName: {}", auth.getName());
		Assertions.assertEquals(USER_NAME, userName);

		String roleNames = SecurityUtil.getRoleNames(auth.getAuthorities());
		log.debug("roleNames: {}", roleNames);
		Assertions.assertEquals(ROLE_NAME, roleNames);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t11encode() {
		log.debug(SecurityUtil.PASSWORD_ENCODER.encode("1234567890"));
	}

	@Test
	void t12jwt() {
		String secret = "1234567890123456789012345678901234567890123456789012345678901234";
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwiZXhwIjoxNzA3OTcxNzI2fQ.xV28rb-v_r-pcRSlE2uSNNH76H41qpOqnbNZDwZwqYbfc9yMdcvxKlbBbY8l9zFx7BhSg4IfT73g42y_PPuCvA";
		String password = "1234567890";
		String encPassword = "$2a$10$xa6J5T.ijRg0IOkZGtgWIOfHzncyVPrJg/INZJ8RIjF30YyXnIsxu";

		Key key = SecurityUtil.createKey(secret);
		Date expireDate = DateUtils.addMinutes(new Date(), SecurityFilter.DEFAULT_EXPIRE_TIME);
		token = SecurityUtil.createToken(AbstractUser.ADMIN_ID, Role.ROLE_ADMIN.name(), key, expireDate);
		log.debug("token: {}", token);

		Key keys = SecurityUtil.createKey(secret);
		Jws<Claims> jwt = SecurityUtil.getJws(keys, token);
		Claims claims = jwt.getBody();

		String userName = claims.getSubject();
		log.debug("userName: {}", userName);

		encPassword = SecurityUtil.PASSWORD_ENCODER.encode(password);
		log.debug("encPassword: {}", encPassword);

		boolean match = SecurityUtil.PASSWORD_ENCODER.matches(password, encPassword);
		log.debug("match: {} password: {}", match, password);

		Object roleNames = claims.get(SecurityUtil.ROLE);
		log.debug("roleNames: {}", roleNames);

		Date expTime = claims.getExpiration();
		log.debug("expTime: {}", expTime);
	}

	@Test
	void t13encrypt() {
		String value = "{\"id\":\"12345678901234567890123456789012345678901234567890\"," +
				"\"password\":\"$2a$10$1XMCiVSCSZgWftU8HVyOQOSnnfnRPpFmtfhiKoY8d.iC1xh/IFYcq\"," +
				"\"expireTimeMin\":1234567890," +
				"\"expireDate\":\"2022-05-23 16:39:19\"," +
				"\"roles\":\"ROLE_MANAGER\"}";
		log.debug("length: {}", value.length());
		log.debug("value: {}", value);

		String encrypt = AESEncryptUtil.encrypt(value);
		log.debug("length: {}", encrypt.length());
		log.debug("value: {}", encrypt);

		String salt = "1234567890123456";//AESEncryptUtil.SALT;
		TextEncryptor encryptor = Encryptors.text(AESEncryptUtil.PASSWORD, salt);
		encrypt = encryptor.encrypt(value);
		log.debug("length: {}", encrypt.length());
		log.debug("value: {}", encrypt);
		// {"id":"12345678901234567890123456789012345678901234567890","password":"$2a$10$1XMCiVSCSZgWftU8HVyOQOSnnfnRPpFmtfhiKoY8d.iC1xh/IFYcq","expireTimeMin":1234567890,"expireDate":"2022-05-23 16:39:19","roles":"ROLE_MANAGER"}
//		encrypt = "0ee645b066a095e1c4576fb18725210abbccfe6311780be98a4ffcdc680dde066ee08d55878de37b29901292f33d8d15f2e89c89a466030d6421c6d58ff58380f9dce10dd525e753e2cd46bd4862eb87df4d4c3224ab7268950674813a6d0c47c7a9c1d38f2d75df07b9c4833ec62bf1f4b4269b643309e3bbbc3fe52f5d857763c26b6ab1e39dce95a3cad1936a1df0cf80c843fed084db40fa9379415965be5390550e92ed37e987a48a82fd7af8053589dd4582ad9e5a3e99a727fa7ca0a6dc414f62ca5ff1cdf94700d717ad5570bbae3aae3e5cac35e5414b148ba42e13b42e5ab52ef3b871a2cb0af0ee1b7ef7";
		String decrypt = encryptor.decrypt(encrypt);
		log.debug("length: {}", decrypt.length());
		log.debug("value: {}", decrypt);
	}
}

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

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.controller.DefaultController;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.model.user.AbstractUser;
import org.oh.common.model.user.Login;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.session.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * 스프링 유틸리티
 */
@Slf4j
@Component
public final class SpringUtil {
	public static final String SPRING_CONFIG_NAME = "spring.config.name";
	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
	private static Environment environment;

	public static Optional<Environment> getEnvironment() {
		if (environment == null) {
			return Optional.empty();
		}
		return Optional.of(environment);
	}

	/**
	 * 해당 SPEL 표현식에 해당하는 결과 값을 반환
	 *
	 * @param expression SPEL 표현식
	 * @return 결과 값
	 */
	public static Object getSpelValue(String expression) {
		return EXPRESSION_PARSER.parseExpression(expression) //NOSONAR 현재 결과값 로깅 용도로만 사용중
				.getValue();
	}

	/**
	 * 스프링의 ServletRequestAttributes 정보를 반환
	 *
	 * @return ServletRequestAttributes 정보
	 */
	public static Optional<ServletRequestAttributes> getServletRequestAttributes() {
		return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
	}

	/**
	 * 현재의 HTTP 요청 정보를 반환
	 *
	 * @return HTTP 요청 정보
	 */
	public static Optional<HttpServletRequest> getRequest() {
		return getServletRequestAttributes()
				.map(ServletRequestAttributes::getRequest);
	}

	/**
	 * 현재의 HTTP 응답 정보를 반환
	 *
	 * @return HTTP 응답 정보
	 */
	public static Optional<HttpServletResponse> getResponse() {
		return getServletRequestAttributes()
				.map(ServletRequestAttributes::getResponse);
	}

	/**
	 * 현재의 HTTP 세션 정보를 반환
	 *
	 * @return HTTP 세션 정보
	 */
	public static Optional<HttpSession> getSession() {
		return getRequest()
				.map(HttpServletRequest::getSession);
	}

	/**
	 * HTTP 요청 정보에서 속성 키에 해당하는 속성 값을 반환
	 *
	 * @param name          속성 키
	 * @param resultTypeRef 결과 타입 레퍼런스
	 * @param defaultValue  속성 값이 없을 경우 기본값
	 * @return 속성 값
	 */
	public static <T> T getAttributeOfRequest(String name, TypeReference<T> resultTypeRef, T defaultValue) {
		return getAttributeOfRequest(name, resultTypeRef)
				.orElse(defaultValue);
	}

	/**
	 * HTTP 요청 정보에서 속성 키에 해당하는 속성 값을 반환
	 *
	 * @param name          속성 키
	 * @param resultTypeRef 결과 타입 레퍼런스
	 * @return 속성 값
	 */
	public static <T> Optional<T> getAttributeOfRequest(String name, TypeReference<T> resultTypeRef) {
		return getAttributeOfRequest(name, CommonUtil.getClass(resultTypeRef));
	}


	/**
	 * HTTP 요청 정보에서 속성 키에 해당하는 속성 값을 반환
	 *
	 * @param name         속성 키
	 * @param defaultValue 속성 값이 없을 경우 기본값
	 * @return 속성 값
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttributeOfRequest(String name, T defaultValue) {
		return getAttributeOfRequest(name, (Class<T>) defaultValue.getClass())
				.orElse(defaultValue);
	}

	/**
	 * HTTP 요청 정보에서 속성 키에 해당하는 속성 값을 반환
	 *
	 * @param name       속성 키
	 * @param resultType 결과 타입
	 * @return 속성 값
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getAttributeOfRequest(String name, Class<T> resultType) {
		return getRequest()
				.map(a -> (T) a.getAttribute(name));
	}

	/**
	 * 세션에서 키에 해당하는 값을 반환
	 *
	 * @param key 키
	 * @return 값
	 */
	public static Optional<Object> getAttributeOfSession(String key) {
		return getSession()
				.map(a -> a.getAttribute(key));
	}

	/**
	 * 세션에 해당 키와 값을 등록
	 *
	 * @param key   키
	 * @param value 키
	 */
	public static void setAttributeToSession(String key, Object value) {
		getSession()
				.ifPresent(a -> a.setAttribute(key, value));
	}

	/**
	 * 세션의 모든 키와 값을 로그에 출력
	 *
	 * @param session 세션 정보
	 */
	public static void logAttributeOfSession(Session session) {
		logAttributeOfSession(session, false);
	}

	/**
	 * 세션의 모든 키와 값을 로그에 출력
	 *
	 * @param session 세션 정보
	 * @param json    JSON 여부
	 */
	public static void logAttributeOfSession(Session session, boolean json) {
		if (session == null) {
			return;
		}
		session.getAttributeNames()
				.forEach(e -> {
					Object value = session.getAttribute(e);
					log.debug("{}: {}", e, json ? JsonUtil.toString(value) : value);
				});
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 세션에서 로그인 정보를 반환
	 *
	 * @return 로그인 정보
	 */
	public static Optional<AbstractUser> getLoginUserOrEmptyFromSession() {
		return getAttributeOfSession(DefaultController.KEY_LOGIN_USER)
				.map(AbstractUser.class::cast);
	}

	/**
	 * 세션에서 로그인 정보를 반환 (없을 걍우 토큰에서 추출)
	 *
	 * @return 로그인 정보
	 */
	public static Optional<AbstractUser> getLoginUserOrEmpty() {
		Optional<AbstractUser> user = getLoginUserOrEmptyFromSession();
		Optional<AbstractUser> result = user.isPresent() ? user : SecurityUtil.getLoginUser();
		log.debug("loginUserFromSession: {} loginUser: {}", user, result);
		return result;
	}

	/**
	 * 세션과 토큰에서 로그인 정보를 반환 (없을 걍우 예외 발생)
	 *
	 * @return 로그인 정보
	 */
	public static AbstractUser getLoginUser() {
		return getLoginUserOrEmpty()
				.orElseThrow(() -> new CommonException(CommonError.COM_NOT_LOGGED_IN));
	}

	/**
	 * 세션과 토큰에서 로그인 정보를 반환 (없을 걍우 관리자 정보를 반환)
	 *
	 * @return 로그인 정보
	 */
	public static AbstractUser getLoginUserOrAdmin() {
		return getLoginUserOrEmpty()
				.orElse(Login.ADMIN);
	}

	/**
	 * 클래스에서 해당 어노테이션의 속성 깂을 반환
	 *
	 * @param type           클래스 타입
	 * @param annotationType 어노테이션 타입
	 * @param attributeName  속성명
	 * @return 속성 깂
	 */
	public static <T> Object getValue(Class<T> type,
									  Class<? extends Annotation> annotationType, String attributeName) {
		return AnnotationUtils.getValue(type.getAnnotation(annotationType), attributeName);
	}

	/**
	 * HTTP 요청자의 IP와 URI 정보을 반환
	 *
	 * @return IP와 URI 정보
	 */
	public static String getRequestInfo() {
		return getRequest().map(WebUtil::getRequestInfo)
				.orElse("");
	}

	/**
	 * 서버의 접속 정보를 반환
	 *
	 * @return 서버의 접속 정보
	 */
	public static String getServerInfo() {
		return getRequest().map(WebUtil::getServerInfo)
				.orElse("");
	}

	///////////////////////////////////////////////////////////////////////////

	private final DefaultListableBeanFactory beanFactory;
	private final GenericApplicationContext context;

	public SpringUtil(Environment environment,
					  DefaultListableBeanFactory beanFactory,
					  GenericApplicationContext context) {
		SpringUtil.environment = environment;
		this.beanFactory = beanFactory;
		this.context = context;
	}

	/**
	 * 객체의 클래스 풀 이름으로 빈 객체를 스프링에 등록
	 *
	 * @param obj 빈 객체
	 */
	public void registerBean(Object obj) {
		registerBean(obj.getClass().getName(), obj);
	}

	/**
	 * 해당 빈 이름으로 빈 객체를 스프링에 등록
	 *
	 * @param name 빈 이름
	 * @param obj  빈 객체
	 */
	public void registerBean(String name, Object obj) {
		beanFactory.registerSingleton(name, obj);
//		Supplier<Object> supplier = () -> obj;
//		context.registerBean(name, obj.getClass(), supplier);
	}

	/**
	 * 설정 키에 해당하는 설정 값을 반환 (배열 값은 안됨)
	 *
	 * @param value ${설정 키[:기본 값]}
	 * @return 설정 값
	 */
	public String getPlaceholderValue(String value) {
		ConfigurableListableBeanFactory bf = ((ConfigurableApplicationContext) context).getBeanFactory();
		return bf.resolveEmbeddedValue(value);
	}

	/**
	 * 빈 이름에 해당하는 빈 객체를 반환 (찾지 못하면 예외 발생)
	 *
	 * @param name 빈 이름
	 * @return 빈 객체
	 */
	public Object getBean(String name) {
		return context.getBean(name);
	}

	/**
	 * 해당 객체가 null이 아니면 객체를, null이면 빈 이름에 해당하는 빈 객체를 반환
	 *
	 * @param value 객체
	 * @param name  빈 이름
	 * @return 빈 객체
	 */
	public Object getBean(Object value, String name) {
		return Optional.ofNullable(value)
				.orElseGet(() -> getBean(name));
	}

	/**
	 * 빈 이름에 해당하는 빈 객체를 반환 (찾지 못해도 예외 발생 없음)
	 *
	 * @param name 빈 이름
	 * @return 빈 객체
	 */
	public Optional<Object> getBeanOrEmpty(String name) {
		try {
			return Optional.of(getBean(name));
		} catch (BeansException e) {
			log.debug(ExceptionUtil.getMessageAndType(e));
			return Optional.empty();
		}
	}

	/**
	 * 빈 타입에 해당하는 빈 객체를 반환 (찾지 못하면 예외 발생)
	 *
	 * @param requiredType 빈 타입
	 * @return 빈 객체
	 */
	public <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	/**
	 * 해당 객체가 null이 아니면 객체를, null이면 빈 타입에 해당하는 빈 객체를 반환
	 *
	 * @param value        객체
	 * @param requiredType 빈 타입
	 * @return 빈 객체
	 */
	public <T> T getBean(T value, Class<T> requiredType) {
		return Optional.ofNullable(value)
				.orElseGet(() -> getBean(requiredType));
	}

	/**
	 * 빈 타입에 해당하는 빈 객체를 반환 (찾지 못해도 예외 발생 없음)
	 *
	 * @param requiredType 빈 타입
	 * @return 빈 객체
	 */
	public <T> Optional<T> getBeanOrEmpty(Class<T> requiredType) {
		try {
			return Optional.of(getBean(requiredType));
		} catch (BeansException e) {
			log.debug(ExceptionUtil.getMessageAndType(e));
			return Optional.empty();
		}
	}

	/**
	 * 모든 캐시 내용을 삭제
	 */
	public void clearCache() {
		getBeanOrEmpty(CacheManager.class)
				.ifPresent(a -> a.getCacheNames()
						.forEach(cn -> Optional.ofNullable(a.getCache(cn))
								.ifPresent(Cache::clear)));
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 서버의 설정 정보를 반환
	 *
	 * @return 서버의 설정 정보
	 */
	public ServerProperties getServerProperties() {
		return getBean(ServerProperties.class);
	}

	/**
	 * 서버의 사용 포트를 반환
	 *
	 * @return 서버의 사용 포트
	 */
	public int getServerPort() {
		return Optional.ofNullable(getServerProperties().getPort())
				.orElse(8080);
	}
}

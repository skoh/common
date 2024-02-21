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

package org.oh.common.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.oh.common.filter.ExceptionHandlerFilter;
import org.oh.common.filter.SecurityFilter;
import org.oh.common.model.user.Role;
import org.oh.common.security.DBUserDetailsService;
import org.oh.common.security.TokenAccessDeniedHandler;
import org.oh.common.security.TokenAuthenticationEntryPoint;
import org.oh.common.util.ExceptionUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 보안 조기화
 * <pre>
 * application.yml
 *
 * ##### 공통 관리
 * common:
 * ### API 보안 관리
 *   security:
 *     ### 사용 여부 (기본값: false)
 *     ### true  : [표준] 성능이 느린 웹 표준 토큰 사용 (JWT)
 *     ### false : [비표준] 성능이 빠른 일반 암호화 토큰 사용 (AES256), 경로에 패턴 사용 불가
 *     enabled: true
 *     ### API 사용 여부
 *     api:
 *       ### 공통(로그인/토큰 발급) API(CommonRestController) 사용 여부 (기본값: false)
 *       enabled: true
 *       ### 공통(권한) 테스트 API(CommonRestController.TestRestController) 사용 여부 (기본값: false)
 *       test.enabled: true
 *     ### 토큰 관리
 *     token:
 *       ### 토큰 생성 암호 (64 byte = 512 bit)
 *       secret: 1234567890123456789012345678901234567890123456789012345678901234
 *       ### 기본 토큰 만료 시간 (분, 기본값: 4시간)
 * #      expire-time-min: 240
 *     ### 권한 상속 체계 (기본값: ROLE_ADMIN > ROLE_MANAGER > ROLE_USER)
 * #    role-hierarchy: ROLE_ADMIN > ROLE_MANAGER > ROLE_USER
 *
 *     ### 주의)
 *     ### - 지정시 범위가 작은 것부터 순서 대로 설정 (필터 개념)
 *     ### - 같은 성격을 같은 경로로 설정하면 마지막 설정이 적용
 *     ###   예) include.api-paths: - /v* /**, ...
 *     ###       exclude.api-paths: - /v* /**, ...
 *     ### 보안 포함(차단) 경로
 *     include:
 *       ### CORS 출처 (url: /**, 기본값: *)
 *       cors-origins:
 * #        - https://*.domain1.com:[8080,8081]
 *       ### 5. API 경로 (기본값: path, ALL, hasRole('USER'))
 *       api-paths:
 * #        - /v* /common/manager, ALL, hasRole('MANAGER')
 *         - /v* /common/admin, ALL, hasRole('ADMIN')
 *         - /v* /**
 *         # 비표준 보안 적용
 * #        - /v1/
 *     ### 보안 제외(허용) 경로
 *     exclude:
 *       ### 1. 웹 경로
 *       web-paths:
 * #      - /html/**
 *       ### 2. CSRF 경로 (기본값: /**)
 *       csrf-paths:
 * #      - /**
 *       ### 3. API 경로 (기본값: /**, ALL, permitAll)
 *       api-paths:
 *         # 보안 무시 (개발용)
 * #        - /**
 *         # 비표준 보안 적용
 * #        - /v1/common/login
 * #        - /v1/did/
 * #        - /v1/didDoc
 *
 *         - /v* /api-docs/**
 *         - /v* /common/login
 * #        - /v* /** /cvs, GET
 * #        - /v* /files/view/**, GET
 * #        - /v* /files/down/**, GET
 *
 * #        - /v* /admin/**, ALL, hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')
 *       ### 4. IP 경로
 *       ip-paths:
 * #        - path: /v* /admin/**
 * #          ips:
 * #            - 127.0.0.1
 * #            - 0:0:0:0:0:0:0:1
 *
 *     ### 기본 사용자 관리
 *     ### DB 사용자 테이블 연계 : {@link DBUserDetailsService}
 *     default-users:
 *       - &default-user
 *         id: user
 *         password: $2a$10$hmq/jplrU8JKkDfGXU4x1uDBgmq46D90vJ.MAOmiU3uidCxfPWmfa
 *         state: ACTIVE
 *         roles: ROLE_USER
 *       - <<: *default-user
 *         id: manager
 *         roles: ROLE_MANAGER
 *       - <<: *default-user
 *        id: admin
 *        roles: ROLE_ADMIN
 *     ### 추가 사용자 관리
 *     append-users:
 * </pre>
 */
@Slf4j
@Setter
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//@EnableMethodSecurity(securedEnabled = true)
@Validated
@ConfigurationProperties(SecurityConfig.SECURITY_PREFIX)
@ConditionalOnProperty(value = "enabled", prefix = SecurityConfig.SECURITY_PREFIX, havingValue = "true")
public class SecurityConfig {
	public static final String SECURITY_PREFIX = CommonConfig.COMMON_PREFIX + ".security";
	public static final String SECURITY_API_PREFIX = SECURITY_PREFIX + ".api";
	/**
	 * 전체 경로
	 */
	public static final String MATCH_ALL = "/**";
	/**
	 * 전체 메소드
	 */
	public static final String METHOD_ALL = "ALL";
	/**
	 * 문자열 권한
	 */
	public static final String DEFAULT_ROLE_HIERARCHY = "ROLE_ADMIN > ROLE_MANAGER > ROLE_USER";

	@NestedConfigurationProperty
	protected Include include = new Include();
	@NestedConfigurationProperty
	protected Exclude exclude = new Exclude();

	protected final TokenAuthenticationEntryPoint authenticationEntryPoint;
	protected final TokenAccessDeniedHandler accessDeniedHandler;
	protected final SecurityFilter securityFilter;
	protected final ExceptionHandlerFilter exceptionHandlerFilter;
	protected final H2ConsoleProperties h2Properties;

	protected SecurityConfig(TokenAuthenticationEntryPoint authenticationEntryPoint,
							 TokenAccessDeniedHandler accessDeniedHandler,
							 SecurityFilter securityFilter,
							 ExceptionHandlerFilter exceptionHandlerFilter,
							 @Lazy H2ConsoleProperties h2Properties) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
		this.securityFilter = securityFilter;
		this.exceptionHandlerFilter = exceptionHandlerFilter;
		this.h2Properties = h2Properties;
	}

	/**
	 * HTTP 보안 초기화
	 *
	 * @param http HTTP 보안
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf().and()
				.formLogin().disable()
				.headers().frameOptions().disable()

				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler)

				.and()
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(exceptionHandlerFilter, SecurityFilter.class);

		// excludes
		Optional.ofNullable(exclude.getCsrfPaths())
				.map(a -> a.isEmpty() ? null : a)
				.orElseGet(() -> Collections.singletonList(MATCH_ALL))
				.forEach(http.csrf()::ignoringAntMatchers);

		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry requests =
				http.authorizeRequests();

		requests.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();

		Optional.ofNullable(exclude.getApiPaths())
				.map(a -> a.isEmpty() ? null : a)
				.orElseGet(() -> Collections.singletonList(Collections.singletonList(MATCH_ALL)))
				.forEach(e -> {
					HttpMethod method = getMethod(e);
					String attribute = e.size() > 2 ? e.get(2) : "permitAll";
					requests.antMatchers(method, e.get(0)).access(attribute);
				});

		Optional.ofNullable(exclude.getIpPaths())
				.ifPresent(a -> a.forEach(e -> {
					if (StringUtils.isNotEmpty(e.getPath()) && !e.getIps().isEmpty()) {
						String ips = e.getIps().stream()
								.map(ip -> "hasIpAddress('" + ip + "')")
								.collect(Collectors.joining(" or "));
						requests.antMatchers(e.getPath()).access(ips);
					}
				}));

		// includes
		Optional.ofNullable(include.getCorsOrigins())
				.map(a -> a.isEmpty() ? null : createCorsConfigurationSource(a))
				.ifPresent(http.cors()::configurationSource);

		Optional.ofNullable(include.getApiPaths())
				.ifPresent(a -> a.forEach(e -> {
					HttpMethod method = getMethod(e);
					String attribute = e.size() > 2 ? e.get(2) : "hasRole('" + Role.ROLE_USER.getValue() + "')";
					requests.antMatchers(method, e.get(0)).access(attribute);
				}));

		requests.anyRequest().permitAll();

		return http.build();
	}

	/**
	 * 웹 보안 초기화
	 */
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> {
			WebSecurity.IgnoredRequestConfigurer ignoring = web.ignoring();

			try {
				ignoring.antMatchers(h2Properties.getPath() + MATCH_ALL);
			} catch (NoSuchBeanDefinitionException e) {
				log.debug(ExceptionUtil.getMessageAndType(e));
			}

			Optional.ofNullable(exclude.getWebPaths())
					.map(a -> a.isEmpty() ? null : a)
					.ifPresent(a -> ignoring.antMatchers(a.toArray(new String[0])));

			ignoring.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
		};
	}

	/**
	 * 해당 문자열 권한으로 권한 체계 정보를 반환
	 *
	 * @param roleHierarchy 문자열 권한
	 * @return 권한 체계 정보
	 */
	@Bean
	public RoleHierarchy roleHierarchy(@Value("${" + SECURITY_PREFIX + ".role-hierarchy:#{null}}")
									   String roleHierarchy) {
		RoleHierarchyImpl roleHierarchyTemp = new RoleHierarchyImpl();
		roleHierarchyTemp.setHierarchy(Objects.toString(roleHierarchy, DEFAULT_ROLE_HIERARCHY));
		return roleHierarchyTemp;
	}

//	@Bean
//	public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
//		DefaultMethodSecurityExpressionHandler meh = new DefaultMethodSecurityExpressionHandler();
//		meh.setRoleHierarchy(roleHierarchy);
//		return meh;
//	}
//
//	@Bean
//	public SecurityExpressionHandler<FilterInvocation> expressionHandler(RoleHierarchy roleHierarchy) {
//		DefaultWebSecurityExpressionHandler weh = new DefaultWebSecurityExpressionHandler();
//		weh.setRoleHierarchy(roleHierarchy);
//		return weh;
//	}

	private CorsConfigurationSource createCorsConfigurationSource(List<String> corsOrigins) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);

		log.debug("corsOrigins: {}", corsOrigins);
		config.setAllowedOriginPatterns(corsOrigins);

		config.addAllowedHeader(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		config.setMaxAge(Duration.ofMinutes(60));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(MATCH_ALL, config);
		return source;
	}

	private HttpMethod getMethod(List<String> path) {
		return path.size() == 1
				|| (path.size() > 1 && METHOD_ALL.equalsIgnoreCase(path.get(1))) ?
				null : HttpMethod.valueOf(path.get(1));
	}

	/**
	 * 포함(차단)할 경로들
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Include {
		/**
		 * CORS 출처들
		 */
		protected List<String> corsOrigins;
		/**
		 * API 경로들
		 */
		protected List<List<String>> apiPaths;
	}

	/**
	 * 제외(허용)할 경로들
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Exclude {
		/**
		 * 웹 경로들
		 */
		protected List<String> webPaths;
		/**
		 * CSRF 경로들
		 */
		protected List<String> csrfPaths;
		/**
		 * API 경로들
		 */
		protected List<List<String>> apiPaths;
		/**
		 * IP 기반 경로들
		 */
		protected List<IpPaths> ipPaths;
	}

	/**
	 * IP 기반 경로
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	protected static class IpPaths {
		/**
		 * 경로들
		 */
		protected String path;
		/**
		 * IP들
		 */
		protected List<String> ips;
	}

	// Test code
	//	@Bean
	@SuppressWarnings("unchecked")
	public SecurityFilterChain securityFilterChainTest(HttpSecurity http) throws Exception {
		http
//				.anonymous().disable()
				.csrf().disable() // NOSONAR 테스트 코드이므로 제외
				.cors().disable()
				.formLogin().disable();
//				.headers().disable()
//				.httpBasic().disable()
//				.jee().disable()
//				.logout().disable()
//				.oauth2Client().disable()
//				.oauth2Login().disable()
//				.oauth2ResourceServer().disable()
//				.portMapper().disable()
//				.rememberMe().disable()
//				.requestCache().disable()
//				.saml2Login().disable()
//				.securityContext().disable()
//				.servletApi().disable()
//				.x509().disable()

		http.authorizeRequests()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler)
				.and()
				.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(exceptionHandlerFilter, SecurityFilter.class);

		// exclude
		http.authorizeRequests()
//				.antMatchers("/**").permitAll()
				.antMatchers("/v*/api-docs/**").permitAll()
				.antMatchers("/v*/common/login").permitAll();

		// include
		http.authorizeRequests()
//				.antMatchers("/v*/**")
//				.hasRole("ANONYMOUS")

//				.antMatchers("/v*/**")
//				.access("hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1')")
//				.access("hasIpAddress('127.0.0.2')")

//				.antMatchers("/v*/admin/**")
//				.access("hasRole('" + Role.ROLE_ADMIN.getValue() + "')"
//						+ " and (hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1'))"
//						+ " and hasIpAddress('127.0.0.2')"
//				)

				.antMatchers("/v*/**")
				.access("hasRole('" + Role.ROLE_USER.getValue() + "')"
//						+ " and (hasIpAddress('127.0.0.1') or hasIpAddress('0:0:0:0:0:0:0:1'))"
//						+ " and hasIpAddress('127.0.0.2')"
				)

				.anyRequest()
				.permitAll();
//				.authenticated()
		ExpressionUrlAuthorizationConfigurer config = http.getConfigurer(ExpressionUrlAuthorizationConfigurer.class);
		log.debug("config: {}", config);

		return http.build();
	}
}

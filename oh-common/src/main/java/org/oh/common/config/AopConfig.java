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

import com.google.common.base.Defaults;
import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.oh.common.annotation.ExceptionHandling;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.annotation.SqlTransaction;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.util.CommonUtil;
import org.oh.common.util.ExceptionUtil;
import org.oh.common.util.Logging;
import org.oh.common.util.SpringUtil;
import org.oh.common.util.StringUtil;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AOP 초기화
 */
@Slf4j
@Configuration
public class AopConfig {
	/**
	 * 해당 인자 목록 중에 선택한 인자 목록만 반환
	 *
	 * @param args    인자 목록
	 * @param indexes 인자 인덱스들
	 * @return 선택한 인자 목록
	 */
	private static Object[] getArgs(Object[] args, int[] indexes) {
		if (indexes.length > 0) {
			return Arrays.stream(indexes)
					.filter(e -> args.length > e)
					.mapToObj(e -> args[e])
					.toArray();
		} else {
			return args;
		}
	}

	/**
	 * 로그 레벨에 맞는 로깅 객체를 반환
	 *
	 * @param logLevel       로그 레벨
	 * @param defaultLogging 기본 로깅 객체
	 * @return 로깅 객체
	 */
	private static Logging getLogging(AopConfig.LogLevel logLevel, Logging defaultLogging) {
		Logging logging;
		switch (logLevel) {
			case TRACE:
				logging = log::trace;
				break;
			case DEBUG:
				logging = log::debug;
				break;
			case INFO:
				logging = log::info;
				break;
			case WARN:
				logging = log::warn;
				break;
			case ERROR:
				logging = log::error;
				break;
			default:
				logging = defaultLogging;
		}
		return logging;
	}

	//	@Bean
	public Advisor transactionAdvisor(TransactionManager transactionManager) {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* org.oh.*.controller..*(..))");
		return new DefaultPointcutAdvisor(pointcut, transactionAdvice(transactionManager));
	}

	//	@Bean
	public TransactionInterceptor transactionAdvice(TransactionManager transactionManager) {
		MatchAlwaysTransactionAttributeSource source = new MatchAlwaysTransactionAttributeSource();
		RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();
		transactionAttribute.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
		transactionAttribute.setName("*");
		source.setTransactionAttribute(transactionAttribute);
		return new TransactionInterceptor(transactionManager, source);
	}

	/**
	 * 결과 로깅 초기화
	 */
	@Aspect
	@Configuration
	@Order(10) // 낮은 값이 더 높은 우선 순위
	protected static class ResultLoggingConfig {
		/**
		 * 해당 조건으로 로깅 제목을 반환
		 *
		 * @param joinPoint JoinPoint
		 * @param value     로깅 제목 접두어
		 * @return 로깅 제목
		 */
		private static String getTitle(JoinPoint joinPoint, String value) {
			return getTitle(joinPoint, value, ImmutableMap.of());
		}

		/**
		 * 해당 조건으로 로깅 제목을 반환
		 *
		 * @param joinPoint JoinPoint
		 * @param value     로깅 제목 접두어
		 * @param options   항목별 출력 여부
		 * @return 로깅 제목
		 */
		private static String getTitle(JoinPoint joinPoint, String value, Map<Option, Object> options) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			String title = Stream.of(value,
							(boolean) options.getOrDefault(Option.OPTION_KEY_TYPE,
									Option.OPTION_KEY_TYPE.isDefaultValue()) ?
									joinPoint.getTarget()
											.getClass()
											.getSimpleName()
									: "",
							(boolean) options.getOrDefault(Option.OPTION_KEY_METHOD,
									Option.OPTION_KEY_METHOD.isDefaultValue())
									? signature.getName()
									: "")
					.filter(StringUtils::isNotEmpty)
					.collect(Collectors.joining("."));

			if ((boolean) options.getOrDefault(Option.OPTION_KEY_PARAMS, Option.OPTION_KEY_PARAMS.isDefaultValue())) {
				title += '(' + Arrays.stream(signature.getParameterTypes())
						.map(Class::getSimpleName)
						.collect(Collectors.joining(","))
						+ ')';
			}
			return title;
		}

		/**
		 * 결과 로깅 어노테이션으로 메소드의 입/출력을 로깅
		 *
		 * @param joinPoint ProceedingJoinPoint
		 * @param anno      결과 로깅 어노테이션
		 * @return 메소드 결과
		 * @throws Throwable
		 */
		@Around("@annotation(anno)")
		public Object around(ProceedingJoinPoint joinPoint, ResultLogging anno) throws Throwable {
			log.debug(LoggingConfig.TWO_LINE_100);
			Object[] args = joinPoint.getArgs();

			String title = "";
			String time = "";
			String sResult = "";
			try {
				title = getTitle(joinPoint, anno.value());
			} catch (Exception e) {
				log.warn(ExceptionUtil.getMessageAndType(e), e);
			}

			String format = "{} is execution {} {} {}";
			String sArgs = anno.args()
					? "args: " + StringUtil.toString(getArgs(args, anno.indexesOfArgs()), anno.json())
					: "";
			long startTime = System.currentTimeMillis();
			Object result;
			try {
				result = joinPoint.proceed();
			} catch (Exception e) {
				log.debug(format, title, time, "message: " + ExceptionUtil.getMessageAndType(e), sArgs);
				throw e;
			}

			try {
				long processTime = System.currentTimeMillis() - startTime;
				time = "time: " + StringUtil.toString(StringUtil.toStringTime(processTime));
				sResult = anno.result() ? "result: " + StringUtil.toString(result, anno.json(), anno.jsonView()) : "";
			} catch (Exception e) {
				log.warn(ExceptionUtil.getMessageAndType(e), e);
			} finally {
				Logging logging = getLogging(anno.logLevel(), log::debug);
				logging.log(format, title, time, sArgs, sResult);
			}
			return result;
		}

		/**
		 * 로깅 제목에 항목별 출력 여부
		 */
		@Getter
		@AllArgsConstructor(access = AccessLevel.PRIVATE)
		private enum Option {
			/**
			 * 클래스명
			 */
			OPTION_KEY_TYPE(true),
			/**
			 * 메소드명
			 */
			OPTION_KEY_METHOD(true),
			/**
			 * 파라미터 목록
			 */
			OPTION_KEY_PARAMS(true);

			private final boolean defaultValue;

			@Override
			public String toString() {
				return StringUtil.toCodeString(name(), defaultValue);
			}
		}
	}

	/**
	 * 예외 핸들링 초기화
	 */
	@Aspect
	@Configuration
	@Order(20)
	protected static class ExceptionHandlingConfig {
		/**
		 * 메소드 결과 타입에 맞는 기본값을 반환
		 *
		 * @param signature  메소드 스펙
		 * @param expression 결과값
		 * @return 기본값
		 */
		@SuppressWarnings("unchecked")
		private static Object getDefaultValue(MethodSignature signature, String expression) {
			return Optional.of(expression)
					.filter(StringUtils::isNotEmpty)
					.map(SpringUtil::getSpelValue)
					.orElseGet(() -> Defaults.defaultValue(signature.getReturnType()));
		}

		/**
		 * 예외 핸들링 어노테이션으로 메소드의 예외 처리를 제어
		 *
		 * @param joinPoint ProceedingJoinPoint
		 * @param anno      예외 핸들링 어노테이션
		 * @return 메소드 결과
		 * @throws Throwable
		 */
		@Around("@annotation(anno)")
		public Object around(ProceedingJoinPoint joinPoint, ExceptionHandling anno) throws Throwable {
			Object[] args = joinPoint.getArgs();
			String sArgs = StringUtil.toString(getArgs(args, anno.indexesOfArgs()), anno.json());
			Object result;
			try {
				result = joinPoint.proceed();
			} catch (Exception e) {
				MethodSignature signature = (MethodSignature) joinPoint.getSignature();

				if (anno.argsInException()) {
					try {
						FieldUtils.writeField(e, "detailMessage",
								String.format("%s%s args: %s", ExceptionUtil.getMessageWithoutArgs(e.getMessage()),
										CommonUtil.SEPARATOR, sArgs),
								true);
					} catch (IllegalAccessException iae) {
						throw new CommonException(iae);
					}
				}

				Optional<? extends Exception> t = Arrays.stream(anno.catchTypes())
						.findFirst()
						.flatMap(a -> ExceptionUtil.getFirstExceptionOrNull(e, a));
				if (t.isPresent()) {
					String message = ExceptionUtil.getMessageAndType(e);
					switch (anno.logLevel()) {
						case TRACE:
						case DEBUG:
						case INFO:
						case WARN:
						case ERROR:
							Logging logging = getLogging(anno.logLevel(), log::error);
							logging.log(message, e);
							break;
						case SHORT:
							log.debug(message);
							break;
						case THROW:
						default:
							throw e;
					}
					if (anno.throwException()) {
						throw e;
					}
					result = getDefaultValue(signature, anno.returnExpression());
				} else {
					throw e;
				}
			}
			return result;
		}
	}

	/**
	 * 로그 레벨
	 */
	public enum LogLevel {
		TRACE, DEBUG, INFO, WARN, ERROR,
		/**
		 * stack trace 가 아닌 message 만 로그에 출력
		 */
		SHORT,
		/**
		 * 로그에 출력하지 않고 그대로 예외를 상위로 던짐
		 */
		THROW
	}

	/**
	 * SQL 트랜잭션 초기화
	 */
	@RequiredArgsConstructor
	@Aspect
	@Configuration
	@Order(30)
	protected static class TransactionConfig {
		private final DataSource dataSource;

		/**
		 * SQL 트랜잭션 어노테이션으로 메소드의 일반 SQL 트랜잭션을 처리
		 *
		 * @param joinPoint ProceedingJoinPoint
		 * @param anno      트랜잭션 어노테이션
		 * @return 메소드 결과
		 * @throws Throwable
		 */
		@Around("@annotation(anno)")
		public Object around(ProceedingJoinPoint joinPoint, SqlTransaction anno) throws Throwable {
			Object result;

			PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
			TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
			try {
				result = joinPoint.proceed();
				transactionManager.commit(status);
			} catch (Exception e) {
				transactionManager.rollback(status);
				throw new CommonException(CommonError.COM_DB_ERROR, e);
			}

//			TransactionSynchronizationManager.initSynchronization();
//			Connection connection = DataSourceUtils.getConnection(dataSource);
//			connection.setAutoCommit(false);
//			try {
//				result = joinPoint.proceed();
//				connection.commit();
//			} catch (Exception e) {
//				connection.rollback();
//				throw e;
//			} finally {
//				DataSourceUtils.releaseConnection(connection, dataSource);
//				TransactionSynchronizationManager.unbindResource(dataSource);
//				TransactionSynchronizationManager.clearSynchronization();
//			}

//			Connection connection = dataSource.getConnection();
//			connection.setAutoCommit(false);
//			try {
//				result = joinPoint.proceed();
//				connection.commit();
//			} catch (Exception e) {
//				connection.rollback();
//				throw e;
//			} finally {
//				connection.close();
//			}

			return result;
		}
	}
}

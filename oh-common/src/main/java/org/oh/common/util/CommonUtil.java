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

import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;
import org.oh.common.exception.DefaultException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 일반적인 공통 유틸리티
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class CommonUtil {
	/**
	 * 구분 기호 (안 보이는 제어 문자로 문자열 구분시 사용)
	 */
	public static final String SEPARATOR = "\f";

	private static final Executor executor = DefaultExecutor.builder().get();

	private static String hostName;
	private static String hostAddress;

	/**
	 * 호스트명 반환
	 *
	 * @return 호스트명
	 */
	public static synchronized String getHostName() {
		try {
			if (hostName == null) {
				hostName = InetAddress.getLocalHost().getHostName();
			}
			return hostName;
		} catch (UnknownHostException e) {
			throw new CommonException(e);
		}
	}

	/**
	 * IP 반환
	 *
	 * @return IP
	 */
	public static synchronized String getHostAddress() {
		try {
			if (hostAddress == null) {
				hostAddress = InetAddress.getLocalHost().getHostAddress();
			}
			return hostAddress;
		} catch (UnknownHostException e) {
			throw new CommonException(e);
		}
	}

	/**
	 * PID 반환
	 *
	 * @return PID
	 */
	public static String getPid() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * Consumer을 Function으로 인터페이스 변경
	 *
	 * @param consumer Consumer
	 * @param result   결과값
	 * @return Function
	 */
	public static <T, R> Function<T, R> toFunction(Consumer<T> consumer, R result) {
		return t -> {
			consumer.accept(t);
			return result;
		};
	}

	/**
	 * Consumer을 Function으로 인터페이스 변경
	 *
	 * @param consumer Consumer
	 * @param params   파라미터
	 * @param result   결과값
	 * @return Function
	 */
	public static <T, U, R> Function<T, R> toFunction(BiConsumer<T, U> consumer, U params, R result) {
		return t -> {
			consumer.accept(t, params);
			return result;
		};
	}

	/**
	 * Function을 Consumer으로 인터페이스 변경
	 *
	 * @param function Function
	 * @return Consumer
	 */
	public static <T, R> Consumer<T> toConsumer(Function<T, R> function) {
		return function::apply;
	}

	/**
	 * Function을 Consumer으로 인터페이스 변경
	 *
	 * @param function Function
	 * @return Supplier
	 */
	public static <T, R> Supplier<R> toSupplier(Function<T, R> function) {
		return () -> function.apply(null);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * TypeReference를 Class로 변경
	 * <a href = "http://gafter.blogspot.com/2006/12/super-type-tokens.html">FYI</a>
	 *
	 * @param resultTypeRef ex) {@code new TypeReference<Map<String, String>>() {}}
	 * @return ex) {@code Class<Map<String, String>>}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(TypeReference<T> resultTypeRef) {
		return (Class<T>) ((ParameterizedType) resultTypeRef.getType()).getRawType();
	}

	/**
	 * 상위 클래스의 파라미터 타잎을 반환
	 *
	 * @param clazz 대상 클래스
	 * @return 파라미터 타잎
	 */
	public static Optional<ParameterizedType> getSuperClassParameterizedType(Class<?> clazz) {
		Type type = clazz.getGenericSuperclass();
		if (type == null) {
			return Optional.empty();
		} else if (type instanceof ParameterizedType) {
			return Optional.of((ParameterizedType) type);
		} else {
			Class<?> superClass = clazz.getSuperclass();
			return getSuperClassParameterizedType(superClass);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 특정 Annotation이 있는 필드만을 복사
	 *
	 * @param src  원본 객체
	 * @param des  대상 객체
	 * @param anno 대상 Annotation
	 * @return 대상 객체
	 */
	public static <T> T copyFields(T src, T des, Class<? extends Annotation> anno) {
		DefaultException.assertTrue(src.getClass() == des.getClass(),
				CommonError.COM_INVALID_ARGUMENT,
				String.format("The source type(%s) and the destination type(%s) must be the same",
						src.getClass().getName(), des.getClass().getName()), null);
		FieldUtils.getFieldsListWithAnnotation(src.getClass(), anno)
				.forEach(e -> {
					e.setAccessible(true);
					ReflectionUtils.setField(e, des, ReflectionUtils.getField(e, src));
				});
		return des;
	}

	/**
	 * 필드에서 특정 Annotation의 존재 여부
	 *
	 * @param field 대상 필드
	 * @param anno  대상 Annotation
	 * @return 존재 여부
	 */
	public static boolean existAnnotation(Field field, Class<? extends Annotation> anno) {
		return Arrays.stream(field.getDeclaredAnnotations())
				.anyMatch(anno::isInstance);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 스톱워치 초기화
	 *
	 * @param id 메인 타이틀
	 * @return 스톱워치
	 */
	public static StopWatch initWatch(String id) {
		return new StopWatch(id);
	}

	/**
	 * 스톱워치 시작
	 *
	 * @param sw       스톱워치
	 * @param taskName 서브 타이틀
	 */
	public static void startWatch(StopWatch sw, String taskName) {
		Optional.ofNullable(sw)
				.ifPresent(a -> a.start((a.getTaskCount() + 1) + ". " + taskName));
	}

	/**
	 * 스톱워치 중지
	 *
	 * @param sw 스톱워치
	 */
	public static void stopWatch(StopWatch sw) {
		Optional.ofNullable(sw)
				.ifPresent(StopWatch::stop);
	}

	/**
	 * 스톱워치 결과를 반환
	 *
	 * @param sw StopWatch
	 */
	public static void printWatch(StopWatch sw) {
		log.debug("stopWatch: {}", Optional.ofNullable(sw)
				.map(a -> a.getTotalTimeSeconds() + " sec " + a.prettyPrint())
				.orElse(""));
	}

	/**
	 * 소요시간 측정
	 *
	 * @param title 메인 타이틀
	 * @param func  측정 대상
	 * @return 측정시간 정보
	 */
	public static org.apache.commons.lang3.time.StopWatch stopWatch(String title, CheckedFuncIf.Runnable func) {
		org.apache.commons.lang3.time.StopWatch stopWatch = new org.apache.commons.lang3.time.StopWatch(title);
		stopWatch.start();
		try {
			func.run();
		} catch (Exception e) {
			throw new CommonException(e);
		}
		stopWatch.stop();
		return stopWatch;
	}

	/**
	 * 전체 소요시간 출력
	 *
	 * @param stopWatchs 측정시간 정보들
	 */
	public static void printTotal(List<org.apache.commons.lang3.time.StopWatch> stopWatchs) {
		AtomicLong total = new AtomicLong();
		stopWatchs.forEach(a -> {
			total.set(total.get() + a.getTime());
			log.debug("{} time: {} secs", a.getMessage(), a.formatTime());
		});
		log.debug("total time: {} secs", DurationFormatUtils.formatDurationHMS(total.get()));
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 유일한 아이디 생성
	 *
	 * @return 유일한 아이디
	 */
	public static String getUniqueId() {
		String idx = String.format("%05d", SecurityUtil.RANDOM.nextInt(100_000));
		return DateUtil.currentTimeMillis() + idx;
	}

	/**
	 * 객체를 Long 형태으로 변환
	 *
	 * @param col 대상 객체
	 * @return Long
	 */
	public static long convertLong(Object col) {
		return col instanceof BigDecimal ? ((BigDecimal) col).longValue() : (long) col;
	}

	/**
	 * OS 명령어를 실행
	 */
	public static String exec(String cmd) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
			 ByteArrayOutputStream err = new ByteArrayOutputStream()) {
			PumpStreamHandler streamHandler = new PumpStreamHandler(out, err);
			executor.setStreamHandler(streamHandler);

//			String[] arrCmd = new String[]{"cmd", "/c", cmd};
//			CommandLine cmdLine = CommandLine.parse(arrCmd[0]);
//			for (int i = 1; i < arrCmd.length; i++) {
//				cmdLine.addArgument(arrCmd[i]);
//			}
			CommandLine cmdLine = CommandLine.parse(cmd);
			int result = executor.execute(cmdLine);

			String sErr = err.toString("EUC-KR");
			if (result != 0 || StringUtils.isNotEmpty(sErr)) {
				throw new CommonException(cmd + " 명령을 실패하였습니다. err: " + sErr);
			}

			String sOut = out.toString("EUC-KR");
			if (StringUtils.isNotEmpty(sOut)) {
				log.debug("out: {}", sOut);
			}
			return sOut;
		} catch (IOException e) {
			throw new CommonException(cmd + " 명령을 실행하지 못했습니다.", e);
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 멀티 쓰레드 환경에서 락킹 (@Synchronized 사용)
	 */
	public static class Locking {
		private boolean locked;

		@Synchronized
		public boolean lock() {
			if (locked) {
				return false;
			} else {
				locked = true;
				return true;
			}
		}
	}

	/**
	 * 멀티 쓰레드 환경에서 락킹2 (Atomic 변수 사용)
	 */
	public static class Locking2
			extends Locking {
		protected final AtomicBoolean locked = new AtomicBoolean();

		@Override
		public boolean lock() {
			return locked.compareAndSet(false, true);
		}
	}
}

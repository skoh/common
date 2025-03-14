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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.exception.CommonException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 쓰레드 유틸리티
 */
@Slf4j
@Component
public final class ThreadUtil {
	/**
	 * 조건에 해당하는 쓰레드 풀을 샌성
	 *
	 * @param corePoolSize    기본 크기
	 * @param maximumPoolSize 최대 크기
	 * @param keepAliveTime   쓰레드를 유지하는 시간 (초)
	 * @param namePrefix      쓰레드명
	 * @return 쓰레드 풀
	 */
	public static ThreadPoolExecutor createThreadPool(int corePoolSize, int maximumPoolSize,
													  long keepAliveTime, String namePrefix) {
		ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat(namePrefix + "-%d").build();
		return new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new SynchronousQueue<>(), factory, new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 모든 쓰레드의 결과값을 반환
	 *
	 * @param futures 미래 결과값들
	 * @return 결과값들
	 */
	public static <T> List<T> allOf(List<Future<T>> futures) {
		return allOf(futures, false);
	}

	/**
	 * 모든 쓰레드의 결과값을 반환
	 *
	 * @param futures         미래 결과값들
	 * @param ignoreException 예외 무시 여부
	 * @return 결과값들
	 */
	public static <T> List<T> allOf(List<Future<T>> futures, boolean ignoreException) {
		return CompletableFuture
				.supplyAsync(() -> futures.stream()
						.map(a -> of(a, ignoreException))
						.collect(Collectors.toList())
				).join();
	}

	public static <T> T of(Future<T> future) {
		return of(future, false);
	}

	/**
	 * 쓰레드의 결과값을 반환
	 *
	 * @param future          미래 결과값
	 * @param ignoreException 예외 무시 여부
	 * @return 결과값
	 */
	public static <T> T of(Future<T> future, boolean ignoreException) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			if (ignoreException) {
				log.debug(ExceptionUtil.getMessageAndType(e), e);
				return null;
			} else {
				throw new CommonException(e);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 해당 메소드를 비동기로 실행
	 *
	 * @param method  비동기로 실행할 메소드
	 * @param params  실행 파라미터
	 * @param success 성공시 실행할 메소드
	 * @param fail    실패시 실행할 메소드
	 * @return 미래 결과값
	 */
	public <T, R> CompletableFuture<R> async(Function<T, R> method, T params,
											 Consumer<R> success,
											 BiConsumer<Throwable, T> fail) {
		CompletableFuture<R> result = CompletableFuture
				.supplyAsync(() -> method.apply(params), executor);
		result.thenAccept(success);
		return result.exceptionally(CommonUtil.toFunction(fail, params, null));
	}

	///////////////////////////////////////////////////////////////////////////

	private final ThreadPoolExecutor executor;

	//	private ThreadUtil(SchedulingConfig config) {
//		executor = (ThreadPoolExecutor) config.getAsyncExecutor();
//	}
	private ThreadUtil(ThreadPoolTaskExecutor executor) {
		this.executor = createThreadPool(
				executor.getCorePoolSize(), executor.getMaxPoolSize(),
				executor.getKeepAliveSeconds(), executor.getThreadNamePrefix());
	}
}

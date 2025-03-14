package org.oh.common.util;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.config.LoggingConfig;
import org.oh.common.exception.CommonException;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AsyncTask2 {
	protected final AsyncTask2 self;
	protected final ThreadUtil threadUtil;

	protected AsyncTask2(@Lazy AsyncTask2 self,
						 ThreadUtil threadUtil) {
		this.self = self;
		this.threadUtil = threadUtil;
	}

	void async() {
		List<Future<List<Integer>>> results = new ArrayList<>();
		List<Future<List<Integer>>> results2 = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			// CompletableFuture
			Future<List<Integer>> result =
					threadUtil.async(this::sync, i,
							this::success,
							this::fail
					);
			results.add(result);

			// @Async
			Future<List<Integer>> result2 = self.async(i);
			results2.add(result2);

			log.debug(LoggingConfig.ONE_LINE_100);
		}
		log.debug("results: {}", ThreadUtil.allOf(results, true));
		log.debug("results2: {}", ThreadUtil.allOf(results2));
	}

	@Async
	public Future<List<Integer>> async(int params) {
		return AsyncResult.forValue(sync(params));
	}

	private List<Integer> sync(int params) {
		if (params == 2) {
			int i = 1 / 0;
		}
		log.debug("sync-" + params);
		return ImmutableList.of(params);
	}
	private void success(List<Integer> r) {
		log.debug("result: {}", JsonUtil.toPrettyString(r));
	}

	private void fail(Throwable e, int params) {
		String message = "params: " + params;
		log.error(ExceptionUtil.getMessageAndType(e) + " " + message, e);
		throw new CommonException(message, e);
	}
}

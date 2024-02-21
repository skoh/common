package org.oh.common.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AsyncTask
		implements Closeable {
	protected final ThreadPoolExecutor executor = ThreadUtil.createThreadPool(
			5, 10, 60, "async");
	protected final ThreadUtil threadUtil;

	@Override
	public void close() throws IOException {
		executor.shutdown();
		log.debug("Close task");
	}

	public CompletableFuture<String> async() {
		String params = "success";
		CompletableFuture<String> result = CompletableFuture

//				.supplyAsync(() -> {
//					int i = 1 / 0;
//					return params;
//				}, executor);

//		result.thenAccept(r -> log.debug("result: {}", r));
//		return result.exceptionally(e -> {
//			throw new RuntimeException("params: " + params, e);
////			log.error("params: {}", params, e);
////			return "fail";
//		});

//		return result.handle((r, e) -> {
//			if (r == null) {
//				throw new RuntimeException("params: " + params, e);
////				log.error("params: {}", params, e);
////				return "fail";
//			} else {
//				log.debug("result: {}", r);
//				return r;
//			}
//		});

				.supplyAsync(() -> sync(params), executor);
		result.thenAccept(this::success);
		return result.exceptionally(CommonUtil.toFunction(this::fail, params, null));
	}

	public CompletableFuture<String> async2() {
		return threadUtil.async(this::sync, "success",
				this::success,
				this::fail);
	}

	private String sync(String params) {
		int i = 1 / 0;
		log.debug("sync-" + params);
		return params;
	}

	private void success(String result) {
		log.debug("result: {}", result);
	}

	private void fail(Throwable e, String params) {
		log.error("params: {}", params, e);
		throw new RuntimeException("params: " + params, e);
	}
}

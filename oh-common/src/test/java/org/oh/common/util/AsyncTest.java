package org.oh.common.util;

import org.oh.common.config.LoggingConfig;
import org.oh.common.config.ServiceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletionException;

@Disabled
@Slf4j
@ServiceTest
public class AsyncTest {
	@Autowired
	private AsyncTask task;
	@Autowired
	private AsyncTask2 task2;

	@BeforeEach
	void before() {
		log.debug(LoggingConfig.TWO_LINE_100);
	}

	///////////////////////////////////////////////////////////////////////////

	@Test
	void t01async() {
		Assertions.assertThrows(CompletionException.class, () -> task.async2().join());
	}

	@Test
	void t02async() {
		Assertions.assertThrows(CompletionException.class, () -> task2.async());
	}

	///////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) throws Exception {
		log.debug("start");

//		Future<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
//			log.debug("future-1");
//			return "1";
//		});
//		Future<String> completableFuture2 = CompletableFuture.supplyAsync(() -> {
//			try {
//				Thread.sleep(1_000);
//			} catch (InterruptedException e) {
//				log.error(e.getMessage(), e);
//			}
//			int i = 1 / 0;
//			log.debug("future-2");
//			return "2";
//		});
//		log.debug("results: {}", ThreadUtil.allOf(Arrays.asList(completableFuture1, completableFuture2)));

		try (AsyncTask task = new AsyncTask(null)) {
			task.async().join();
		}

		Thread.sleep(2_000);
	}
}

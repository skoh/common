package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.concurrent.TimeUnit;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class DaemonThreadTest
		implements Runnable {
	private static boolean autoSave;

	@Test
	void test() throws Exception {
		Thread thread = new Thread(new DaemonThreadTest());
		// 데몬쓰레드 (메인쓰레드 종료시 종속쓰레드는 작업 다 안 끝내도 메인 쓰레드와 함께 종료된다.)
		thread.setDaemon(true); // 이 부분이 없으면 종료되지 않는다.
		thread.start();

		for (int i = 1; i <= 10; i++) {
			try {
				Thread.sleep(1_000);
//				TimeUnit.MILLISECONDS.sleep(1_000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
			log.debug(i + " secs");

			if (i == 3) {
				autoSave = true;
			}
			if (i == 7) {
				thread.interrupt();
			}
		}
		log.debug("프로그램을 종료합니다.");
	}

	@Override
	public void run() {
		while (true) {
			try {
				TimeUnit.MILLISECONDS.sleep(2_000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				break;
			}

			if (autoSave) {
				log.debug("작업 파일이 자동 저장 되었습니다.");
			}
		}
	}
}
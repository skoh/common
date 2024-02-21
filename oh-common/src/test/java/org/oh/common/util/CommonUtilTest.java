package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

//@Disabled기본 테스트
@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CommonUtilTest {
	@Test
	void t01lock() {
		loop(new CommonUtil.Locking());
		loop(new CommonUtil.Locking2());

		try {
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	void t02test() throws Exception {
		log.debug(CommonUtil.getPid());
	}

	private void loop(CommonUtil.Locking locking) {
		for (int i = 0; i < 100; i++) {
			new Thread(new LockRunnable(locking)).start();
		}
	}

	protected static class LockRunnable
			implements Runnable {
		protected final CommonUtil.Locking locking;

		public LockRunnable(CommonUtil.Locking locking) {
			this.locking = locking;
		}

		@Override
		public void run() {
			boolean lock = locking.lock();
			if (lock) {
				log.debug("{} - {}", Thread.currentThread().getName(), lock);
				// TODO
			}
		}
	}
}

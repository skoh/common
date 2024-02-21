package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@Slf4j
//@Disabled
@TestMethodOrder(MethodOrderer.MethodName.class)
class StringUtilTest {
	@Test
	void t01ellipsis() {
		String s = "가나다라마바사아자차";
		for (int i = 0; i <= 10; i++) {
			String max = String.format("%02d", i);
			try {
				log.debug("{} : {}", max, StringUtil.ellipsis(s, i));
			} catch (Exception e) {
				log.debug("{} : {}", max, e.getMessage());
			}
		}
	}
}

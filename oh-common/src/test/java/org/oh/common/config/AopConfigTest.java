package org.oh.common.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.oh.common.annotation.ExceptionHandling;
import org.oh.common.annotation.ResultLogging;
import org.oh.common.exception.CommonException;
import org.oh.common.util.CheckedFuncIf;
import org.oh.common.util.ExceptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;

//@Disabled
@Slf4j
@ServiceTest
@ComponentScan
@ContextConfiguration(classes = AopConfigTest.class)
public class AopConfigTest {
	public static <T, R> R apply(CheckedFuncIf.Function<T, R> func, T args) {
		try {
			return func.apply(args);
		} catch (Exception e) {
			log.error("{}", ExceptionUtil.getMessageAndType(e), e);
			return null;
		}
	}

	@Autowired
	private AopTest test;

	@Test
	public void t01Logging() {
		apply(a -> test.test01(a, "test"), 3);
		apply(a -> test.test02(a), 1);
	}

	@Component
	static class AopTest {
		@ResultLogging(value = "test", indexesOfArgs = 1, result = true)
		@ExceptionHandling(indexesOfArgs = 1, argsInException = true,
				logLevel = AopConfig.LogLevel.WARN, returnExpression = "-1")
		public int test01(double d, String s) {
			return 1 / 0;
		}

		@ResultLogging(value = "test", result = true)
		@ExceptionHandling(catchTypes = CommonException.class,
				logLevel = AopConfig.LogLevel.SHORT, throwException = true)
		public int test02(double d) {
			throw new RuntimeException("1", new CommonException("2",
					new CommonException("3", new ArithmeticException("4"))));
		}
	}
}
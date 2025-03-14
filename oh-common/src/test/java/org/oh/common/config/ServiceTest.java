package org.oh.common.config;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.oh.CommonApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@TestMethodOrder(MethodOrderer.MethodName.class)
@Rollback(false)
@SpringBootTest(classes = CommonApplication.class)//, properties = CommonApplication.SPRING_CONFIG_NAME)
@ActiveProfiles("test")
public @interface ServiceTest {
}

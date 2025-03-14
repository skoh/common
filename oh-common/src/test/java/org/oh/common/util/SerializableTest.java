package org.oh.common.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.oh.common.exception.CommonError;
import org.oh.common.exception.CommonException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

@Disabled
@TestMethodOrder(MethodOrderer.MethodName.class)
class SerializableTest {
	@Test
	void t01serializable() throws Exception {
		try (ObjectOutput oo = new ObjectOutputStream(new FileOutputStream("serializable.txt"));) {
			oo.writeObject("test");
			oo.writeObject(new Date());
			oo.writeObject(new CommonException(CommonError.COM_NOT_FOUND));
		}
	}

	@Test
	void t02deserializable() throws Exception {
		try (ObjectInput oi = new ObjectInputStream(new FileInputStream("serializable.txt"))) {
			System.out.println(oi.readObject()); // NOSONAR 테스트 코드이므로 제외
		}
	}
}
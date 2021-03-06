package com.nemustech.common.util;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.nemustech.common.exception.CommonException;

/**
 * Bean 유틸
 */
public abstract class BeanUtil extends BeanUtils {
	public static <T> T convertMapToObject(Map<String, ?> map, Class<T> resultType) throws RuntimeException {
		T obj = null;
		try {
			obj = resultType.newInstance();
			for (Map.Entry<String, ?> entry : map.entrySet()) {
				if (entry == null || entry.getValue() == null)
					continue;

				setProperty(obj, entry.getKey(), entry.getValue());
			}
		} catch (Exception e) {
			throw new CommonException(CommonException.ERROR,
					LogUtil.buildMessage("Read map to object \"" + map + "\" error", e.getMessage()), e);
		}

		return obj;
	}
}
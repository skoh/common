package org.oh.common.util;

/**
 * 쓰레드 로컬 키 리스트
 */
public enum ThreadLocalKey
		implements ThreadLocalUtil.LocalKey {
	//	FILTER_EXCEPTION, // 필터용 예외
	FILTER_START_TIME // 필터용 시작 시간
}

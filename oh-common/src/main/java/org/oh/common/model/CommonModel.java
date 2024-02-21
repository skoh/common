/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oh.common.model;

/**
 * 공통 JSON 뷰 모델
 */
public interface CommonModel {
	/**
	 * 최상위 뷰 모델
	 */
	interface None {
	}

	/**
	 * 무시 뷰 모델
	 */
	interface Ignore
			extends None {
	}

	/**
	 * 기본 뷰 모델
	 */
	interface Default
			extends None {
	}

	/**
	 * CSV 뷰 모델
	 */
	interface Csv
			extends Default {
	}

	/**
	 * 파일 뷰 모델
	 */
	interface Files {
		/**
		 * 단일 뷰 모델
		 */
		interface One
				extends Default {
		}

		/**
		 * 다중 뷰 모델
		 */
		interface Many
				extends One {
		}
	}

	/**
	 * 사용자 뷰 모델
	 */
	interface User {
		interface One
				extends Default {
		}

		interface Many
				extends One {
		}
	}
}

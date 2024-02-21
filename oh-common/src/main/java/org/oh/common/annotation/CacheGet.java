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

package org.oh.common.annotation;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 파라미터에 해당하는 캐시만 반환
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Cacheable(key = CacheGet.KEY_FORMAT_ARGS)
public @interface CacheGet {
	String KEY_FORMAT = "#root.caches[0].name + '_' + #root.targetClass + '_' + ";
	String KEY_FORMAT_ARGS = KEY_FORMAT + "T(org.oh.common.util.StringUtil).toString(#root.args)";
}

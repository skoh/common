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

package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.oh.common.model.data.Data;
import org.oh.common.service.AbstractCrudMapperService;

/**
 * CLI 메퍼 파일 생성 유틸리티
 * <pre>
 * gradlew -S mapper --args="modelName mapperName mapperFilePath"
 * ex) gradlew -S mapper --args="model.sample.org.oh.Sample mapper.sample.org.oh.SampleMapper src/main/resources/mapper/mysql/SampleMapper.xml"
 * </pre>
 */
@Slf4j
public class CliMapperUtil
		extends AbstractCrudMapperService<Data, String> {
	protected CliMapperUtil() {
		super(null, null);
	}

	@SuppressWarnings("UncommentedMain")
	public static void main(String[] args) throws Exception {
		log.debug("args.length: {}", args.length);
		if (args.length < 3) {
			log.debug("usage: gradlew -S mapper --args=\"modelName mapperName mapperFilePath\"\n" +
					"ex) gradlew -S mapper --args=\"model.sample.org.oh.Sample" +
					" mapper.sample.org.oh.SampleMapper" +
					" src/main/resources/mapper/mysql/SampleMapper.xml\"");
			System.exit(1);
		}
		new CliMapperUtil()
				.mapper(Class.forName(args[0]), args[1], args[2]);
	}
}

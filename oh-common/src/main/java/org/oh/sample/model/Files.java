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

package org.oh.sample.model;

import org.oh.common.config.DataGridConfig;
import org.oh.common.model.AbstractFiles;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.keyvalue.annotation.KeySpace;

import javax.persistence.Entity;

/**
 * 파일
 */
@Schema(description = Files.TABLE_DESC)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@org.hibernate.annotations.Table(appliesTo = AbstractFiles.NAME_SPACE, comment = Files.TABLE_DESC)
@KeySpace(DataGridConfig.MAP_NAME_FILES)
public class Files
		extends AbstractFiles {
	public static final String TABLE_DESC = "파일";
	public static final String NAME_SPACE = "files";
}

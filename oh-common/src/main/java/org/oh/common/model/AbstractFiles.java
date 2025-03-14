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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.oh.common.annotation.ExcludeLogging;
import org.oh.common.model.enume.State;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.io.File;
import java.io.Serializable;

/**
 * 기본 파일
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
public abstract class AbstractFiles
		extends AbstractDefault<Long> {
	public static final String NAME_SPACE = "files";
	public static final String NAME_DELIMITER = "-";

	/**
	 * 경로
	 */
	@JsonProperty(index = 710)
	@Schema(description = "경로")
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("경로")
	protected String path;

	/**
	 * 파일명
	 */
	@JsonProperty(index = 720)
	@Schema(description = "파일명")
	@NotBlank
	@Column(length = 100, nullable = false)
	@Comment("파일명")
	protected String name;

	/**
	 * 크기
	 */
	@JsonProperty(index = 730)
	@Schema(description = "크기")
	@Min(1)
	@Column(nullable = false)
	@Comment("크기")
	protected Long size;

	/**
	 * 원본 파일
	 */
	@JsonIgnore
	@ExcludeLogging
	@Transient
	protected Attachment originAttach;

	/**
	 * 썸네일 이미지
	 */
	@JsonIgnore
	@ExcludeLogging
	@Transient
	protected Attachment thumbAttach;

	/**
	 * 썸네일 이미지 생성 여부
	 */
	public boolean createThumbnail() {
		return true;
	}

	@Override
	public String toString() {
		return toString(CommonModel.Files.One.class);
	}

	@Override
	public void setInsertValue() {
		super.setInsertValue();
		if (state == null) {
			state = State.ACTIVE;
		}
	}

	/**
	 * 첨부 파일
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Attachment
			implements Serializable {
		/**
		 * 파일 정보
		 */
		protected File file;

		/**
		 * 파일 내용
		 */
		protected byte[] bytes;
	}

	/**
	 * 이미지 크기 종류
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public enum SizeType {
		/**
		 * 원본 크기
		 */
		ORIGIN(""),
		/**
		 * 소 크기
		 */
		SMALL("s");

		private final String value;
	}
}

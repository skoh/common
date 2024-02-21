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

import org.oh.common.util.DateUtil;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 메일 정보
 */
@Data
@SuperBuilder
@NoArgsConstructor
public class Mail {
	/**
	 * 공지
	 */
	public static final String TITLE_NOTICE = "Notice";

	/**
	 * 메일 구분
	 */
	@Builder.Default
	protected Mail.Type type = Type.IDENTIFICATION;

	/**
	 * 발송 일자
	 */
	@Builder.Default
	protected String date = DateUtil.format(new Date(), DateUtil.DEFAULT_DATE_PATTERN);

	/**
	 * 메일 종류
	 */
	@Builder.Default
	protected String title = TITLE_NOTICE;

	/**
	 * 수신자 목록
	 */
	@Builder.Default
	protected Set<String> toList = new LinkedHashSet<>();

	/**
	 * 참조자 목록
	 */
	@Builder.Default
	protected Set<String> ccList = new LinkedHashSet<>();

	/**
	 * 메일 제목
	 */
	@NotBlank
	protected String subject;

	/**
	 * 메일 본문
	 */
	@NotNull
	protected Mail.Content content;

	/**
	 * 메일 본문
	 */
	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Content {
		/**
		 * 본문 타이틀
		 */
		@NotBlank
		protected String title;

		/**
		 * 본문 내용
		 */
		protected String body;

		/**
		 * 본문 참조 링크
		 */
		protected String link;
	}

	/**
	 *
	 */
	public enum Type {
		IDENTIFICATION;
	}
}

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

package org.oh.common.controller;

import org.oh.common.util.DateUtil;
import org.oh.common.util.ExceptionUtil;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.Optional;

/**
 * 기본 에러 컨트롤러
 */
@Controller
public class DefaultErrorController
		extends BasicErrorController {
	public static final String ERROR_REQUEST_PREFIX_NAME = DefaultErrorController.class.getPackage().getName();

	protected final ErrorAttributes errorAttributes;

	public DefaultErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties,
								  ThymeleafProperties thymeleafProperties) {
		super(errorAttributes, serverProperties.getError());
		this.errorAttributes = errorAttributes;
//		Optional.ofNullable(thymeleafProperties.getViewNames())
//				.filter(ArrayUtils::isNotEmpty)
//				.map(a -> a[0].substring(0, a[0].length() - 1))
//				.ifPresent(WebUtil::setTemplatesName);
	}

	/**
	 * 에러 페이지(templates/error.html)에 에러 정보를 표시
	 *
	 * @param request  HTTP 요청 정보
	 * @param response HTTP 응답 정보
	 * @return 모델앤뷰 정보
	 */
	@Override
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = super.errorHtml(request, response);
		modelAndView.setViewName("error");
		modelAndView.addObject("timestamp", DateUtil.formatDateTime(new Date()));
		setAttribute(request, modelAndView, "status");
		setAttribute(request, modelAndView, "error");
		Optional.ofNullable(errorAttributes.getError(new ServletWebRequest(request)))
				.ifPresent(e -> modelAndView.addObject("message", ExceptionUtil.getMessageWithoutArgs(e)));
		return modelAndView;
	}

	/**
	 * HTTP 요청 정보에서 해당 속성 명에 대한 값을 가져와 모델앤뷰에 설정
	 *
	 * @param request       HTTP 요청 정보
	 * @param modelAndView  모델앤뷰 정보
	 * @param attributeName 속성명
	 */
	private void setAttribute(HttpServletRequest request, ModelAndView modelAndView, String attributeName) {
		Optional.ofNullable(request.getAttribute(ERROR_REQUEST_PREFIX_NAME + '.' + attributeName))
				.ifPresent(a -> modelAndView.addObject(attributeName, a));
	}
}

package org.oh.common.filter;

import lombok.Setter;
import lombok.SneakyThrows;
import org.oh.common.config.CommonConfig;
import org.oh.common.config.LoggingConfig;
import org.oh.common.controller.DefaultController;
import org.oh.common.util.SpringUtil;
import org.oh.common.util.StringUtil;
import org.oh.common.util.ThreadLocalKey;
import org.oh.common.util.ThreadLocalUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * HTTP 로깅 필터
 */
@Setter
@Component
@ConfigurationProperties(CommonConfig.COMMON_PREFIX + ".logs.api")
public class HttpLoggingFilter extends CommonsRequestLoggingFilter {
	private int maxSize;
	private boolean requestEnabled;
	private boolean responseEnabled;

	protected HttpLoggingFilter() {
		setIncludeClientInfo(true);
		setIncludeHeaders(true);
		setIncludeQueryString(true);
	}

	@PostConstruct
	private void init() {
		if (maxSize > 0) {
			setMaxPayloadLength(maxSize);
		}
		if (requestEnabled) {
			setIncludePayload(true);
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.startsWith(DefaultController.VERSION_1)) {
			boolean isFirstRequest = !isAsyncDispatch(request);
			HttpServletResponse responseToUse = response;

			if (responseEnabled && isFirstRequest && !(response instanceof ContentCachingResponseWrapper)) {
				responseToUse = new ContentCachingResponseWrapper(response);
				RequestContextHolder.setRequestAttributes(new ServletWebRequest(request, responseToUse));
			}

			ThreadLocalUtil.set(ThreadLocalKey.FILTER_START_TIME, System.currentTimeMillis());
			super.doFilterInternal(request, responseToUse, filterChain);
		} else {
			filterChain.doFilter(request, response);
		}
	}

	@SneakyThrows
	@Override
	protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
		StringBuilder msg = new StringBuilder();
		if (DEFAULT_AFTER_MESSAGE_PREFIX.equals(prefix)) {
			msg.append("[");
			msg.append(String.format("method={%s}", request.getMethod()));
			msg.append(super.createMessage(request, ";", ""));
			msg.append(String.format(", params={%s}", request.getParameterMap().entrySet().stream()
					.map(e -> e.getKey() + "=" + Arrays.toString(e.getValue()))
					.collect(Collectors.joining(","))));
			SpringUtil.getResponse()
					.map(this::getMessageResponse)
					.ifPresent(b -> msg.append(", response=").append(b));
			ThreadLocalUtil.get(ThreadLocalKey.FILTER_START_TIME, Long.class)
					.ifPresent(a -> msg.append(String.format(", time={%s}",
							StringUtil.toStringTime(System.currentTimeMillis() - a))));
			msg.append(DEFAULT_AFTER_MESSAGE_SUFFIX);
		}
		return msg.toString();
	}

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		super.beforeRequest(request, LoggingConfig.TWO_LINE_50);
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		super.afterRequest(request, message);
		logger.debug(LoggingConfig.TWO_LINE_50);
		ThreadLocalUtil.remove(ThreadLocalKey.FILTER_START_TIME);
	}

	@SneakyThrows
	@Nullable
	protected String getMessageResponse(HttpServletResponse response) {
		ContentCachingResponseWrapper wrapper =
				WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
		if (wrapper != null) {
			byte[] buf = wrapper.getContentAsByteArray();
			wrapper.copyBodyToResponse();
			if (buf.length > 0) {
				int length = Math.min(buf.length, getMaxPayloadLength());
				return new String(buf, 0, length);
			}
		}
		return null;
	}
}

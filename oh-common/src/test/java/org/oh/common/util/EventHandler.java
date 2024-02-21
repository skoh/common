package org.oh.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 서비스 시작 이벤트
 * 1. Context Loading 완료
 * 2. ContextRefreshedEvent
 * 3. ApplicationPreparedEvent
 * 4. ApplicationStartedEvent
 * 5. ApplicationReadyEvent
 * <p>
 * 서비스 중지 이벤트
 * 1. ContextClosedEvent
 */
@Slf4j
@Component
public class EventHandler {
	@EventListener(ContextRefreshedEvent.class)
	public void onContextRefreshedEvent(ContextRefreshedEvent event) {
		log.debug("@@@ onContextRefreshedEvent !!!");
	}

	@EventListener(ContextStartedEvent.class)
	public void onContextStartedEvent(ContextStartedEvent event) {
		log.debug("@@@ onContextStartedEvent !!!");
	}

	@EventListener(ContextStoppedEvent.class)
	public void onContextStoppedEvent(ContextStoppedEvent event) {
		log.debug("@@@ onContextStoppedEvent !!!");
	}

	@EventListener(ContextClosedEvent.class)
	public void onContextClosedEvent(ContextClosedEvent event) {
		log.debug("@@@ onContextClosedEvent !!!");
	}

	@EventListener(ApplicationContextInitializedEvent.class)
	public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
		log.debug("@@@ onApplicationContextInitializedEvent !!!");
	}

	@EventListener(ApplicationEnvironmentPreparedEvent.class)
	public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
		log.debug("@@@ onApplicationEnvironmentPreparedEvent !!!");
	}

	@EventListener(ApplicationPreparedEvent.class)
	public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
		log.debug("@@@ onApplicationPreparedEvent !!!");
	}

	@EventListener(ApplicationStartedEvent.class)
	public void onApplicationStartedEvent(ApplicationStartedEvent event) {
		log.debug("@@@ onApplicationStartedEvent !!!");
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReadyEvent(ApplicationReadyEvent event) {
		log.debug("@@@ onApplicationReadyEvent !!!");
	}

	@EventListener(ApplicationFailedEvent.class)
	public void onApplicationFailedEvent(ApplicationFailedEvent event) {
		log.debug("@@@ onApplicationFailedEvent !!!");
	}

	@EventListener(ApplicationStartingEvent.class)
	public void onApplicationStartingEvent(ApplicationStartingEvent event) {
		log.debug("@@@ onApplicationStartingEvent !!!");
	}
}

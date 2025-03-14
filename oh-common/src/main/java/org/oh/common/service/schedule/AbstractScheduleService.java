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

package org.oh.common.service.schedule;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.oh.common.config.CommonConfig;
import org.oh.common.config.LoggingConfig;
import org.oh.common.model.enume.State;
import org.oh.common.model.schedule.Schedule;
import org.oh.common.model.schedule.ScheduleDb;
import org.oh.common.util.CommonUtil;
import org.oh.common.util.DateUtil;
import org.oh.common.util.SpringUtil;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 스케쥴 별로 이 클래스를 상속 받아 하나씩 만든다.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractScheduleService<T extends Schedule> {
	public static final String SCHEDULE_PROPERTY_PREFIX = CommonConfig.APP_PREFIX + '.' + Schedule.NAME_SPACE;

	protected final SpringUtil springUtil;
	protected final IScheduleService<T> service;
	@SuppressWarnings("unchecked")
	protected final T type = (T) ScheduleDb.builder()
			.type(getType())
			.build();

	protected Properties prop;
	protected String activeId;
	protected boolean closed;

	/**
	 * 스케쥴 종료
	 *
	 * @param event 종료 이벤트
	 */
	@EventListener(ContextClosedEvent.class)
	public void close(ContextClosedEvent event) {
		closed = true;
		if (!prop.isEnabled()) {
			return;
		}

		deleteById(activeId);
		log.debug(LoggingConfig.TWO_LINE_50 + " Schedule Closed. " + LoggingConfig.TWO_LINE_50);
	}

	/**
	 * 스케쥴 초기화
	 */
	@PostConstruct
	private void init() {
		prop = getProperties();
		if (!prop.isEnabled()) {
			return;
		}

		log.info(LoggingConfig.TWO_LINE_50 + " Schedule Initializing ... " + LoggingConfig.TWO_LINE_50);
		activeId = CommonUtil.getHostName() + ":" +
				springUtil.getServerPort() + "/" + getType();
		log.info("Schedule activeId: {}", activeId);

		deleteById(activeId);
	}

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 스케쥴 속성
	 * <pre> - 샘플
	 * {@code @NestedConfigurationProperty}
	 * {@code protected Properties scheduleName = new Properties();}
	 *
	 * {@code @Override}
	 * {@code protected Properties getProperties() {
	 *     return scheduleName;
	 * }
	 * }
	 * </pre>
	 */
	protected abstract Properties getProperties();

	/**
	 * 스케쥴 메인
	 * <pre> - 샘플
	 * {@code @Override}
	 * {@code @Scheduled(cron = "${app.schedule.schedule-name.cron}")}
	 * {@code protected void schedule() {
	 *     super.schedule();
	 * }
	 * }
	 * - application.yml
	 * app:
	 *   schedule:
	 *     schedule-name:
	 *       # 예) 10초마다 실행, 15초마다 실행 여부 체크
	 *       cron: "0/10 * * * * * *"
	 *       health-check-time-sec: 15
	 * </pre>
	 */
	protected void schedule() {
		if (closed || !prop.isEnabled()) {
			return;
		}

		Optional<T> active = getActive();
		active.ifPresent(a -> {
			try {
				log.debug(LoggingConfig.TWO_LINE_10 + " Schedule Starting ... " + LoggingConfig.TWO_LINE_10);
				process(a);
			} finally {
				log.debug(LoggingConfig.TWO_LINE_10 + " Schedule Ending ... " + LoggingConfig.TWO_LINE_10);
				update(a, State.END);
				log.debug(LoggingConfig.TWO_LINE_30 + " Schedule Finished. " + LoggingConfig.TWO_LINE_30);
			}
		});
	}

	/**
	 * 스케쥴 로직
	 *
	 * @param active 실행 중인 스케쥴 정보
	 */
	public abstract void process(T active);

	///////////////////////////////////////////////////////////////////////////

	/**
	 * 실행 중인 스케줄 정보를 반환
	 *
	 * @return 스케줄 정보
	 */
	private Optional<T> getActive() {
		List<T> activeList = service.findAllOrEmpty(type);
		log.debug("Schedule activeList: {}", activeList);
		if (!activeList.isEmpty() && activeList.get(0).getState() == State.START) {
			return Optional.empty();
		}

		log.debug(LoggingConfig.TWO_LINE_30 + " Schedule Preparing ... " + LoggingConfig.TWO_LINE_30);
		activeList = delete(activeList);

		Optional<T> active;
		if (activeList.isEmpty()) {
			T entity = insert();
			active = service.findByIdOrEmpty(entity.getId());
		} else {
			active = activeList.stream()
					.filter(e -> activeId.equals(e.getId()))
					.findFirst();
			active.ifPresent(a -> update(a, State.START));
		}
		return active;
	}

	/**
	 * 1건의 스케쥴 정보를 등록
	 *
	 * @return 스케쥴 정보
	 */
	@SuppressWarnings("unchecked")
	private T insert() {
		T entity = (T) ScheduleDb.builder()
				.id(activeId)
				.state(State.START)
				.type(getType())
				.pid(CommonUtil.getPid())
				.build();
		T result = service.insertSchedule(entity);
		log.debug("Schedule inserted: {}", result);
		return result;
	}

	/**
	 * 1건의 스케쥴 정보를 수정
	 *
	 * @param active 스케쥴 정보
	 * @param state  스케쥴 상태 정보
	 */
	private void update(T active, State state) {
		active.setState(state);
		service.updateSchedule(active);
		log.debug("Schedule updated: {}", active);
	}

	/**
	 * 스케쥴 정보를 삭제
	 *
	 * @param list 삭제 대상 스케쥴 목록
	 * @return 실행 중인 스케줄 목록
	 */
	private List<T> delete(List<T> list) {
		AtomicBoolean deleted = new AtomicBoolean();
		if (prop.getHealthCheckTimeSec() > 0) {
			list.forEach(e -> {
				Date current = new Date();
				Date modify = DateUtils.addSeconds(e.getModDate(), prop.getHealthCheckTimeSec());
				log.debug("Schedule modify: {} current: {}", DateUtil.formatDateTimeMs(modify),
						DateUtil.formatDateTimeMs(current));
				if (modify.before(current)) {
					deleteById(e.getId());
					deleted.set(true);
				}
			});
		}
		if (deleted.get()) {
			list = service.findAllOrEmpty(type);
			log.debug("Schedule deletelist: {}", list);
		}
		return list;
	}

	/**
	 * 1건의 스케쥴 정보를 삭제
	 *
	 * @param id 삭제 대상 아이디
	 */
	private void deleteById(String id) {
		service.deleteByIdOrIgnore(id);
		log.debug("Schedule deleted: {}", id);
	}

	/**
	 * 스케쥴 종류를 반환
	 *
	 * @return 스케쥴 종류
	 */
	private String getType() {
		return getClass().getName();
	}

	/**
	 * 스케쥴 속성
	 */
	@lombok.Data
	@SuperBuilder
	@NoArgsConstructor
	public static class Properties {
		protected boolean enabled;
		protected int healthCheckTimeSec;
		@Builder.Default
		protected int pageSize = 1_000;
		@Builder.Default
		protected int threadSize = 1;
	}
}

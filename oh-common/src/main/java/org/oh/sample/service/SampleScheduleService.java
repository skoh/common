package org.oh.sample.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.oh.common.model.schedule.Schedule;
import org.oh.common.service.schedule.AbstractScheduleService;
import org.oh.common.service.schedule.IScheduleService;
import org.oh.common.util.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
@ConfigurationProperties(AbstractScheduleService.SCHEDULE_PROPERTY_PREFIX)
@ConditionalOnProperty(value = "enabled", prefix = AbstractScheduleService.SCHEDULE_PROPERTY_PREFIX,
		havingValue = "true")
public class SampleScheduleService<T extends Schedule>
		extends AbstractScheduleService<T> {
	@NestedConfigurationProperty
	private Properties sample = new Properties();

	public SampleScheduleService(SpringUtil springUtil,
								 IScheduleService<T> service) {
		super(springUtil, service);
	}

	@Scheduled(cron = "${app.schedule.sample.cron}")
	protected void schedule() {
		super.schedule();
	}

	@Override
	protected Properties getProperties() {
		return sample;
	}

	@Override
	public void process(T active) {
		try {
			log.debug("sample");
			Thread.sleep(5_000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
}

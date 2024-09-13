package com.maan.eway.thread;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.maan.eway.renewal.service.RenewalService;

@Component
public class ScheduledTasks {

	@Autowired
	private RenewalService service;
	@Autowired
	private RenewSchedular renewSchedular;
	@Autowired
	private NotificationSchedular notificationSchedular;

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	// Schedular runs everyday at 4 AM//
	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 4 * * ?")
	//@Scheduled(cron = "${cron.renew}")
	//@EventListener(ApplicationReadyEvent.class)
	public void startRenewSchedular() {
		log.info("The time is now {startRenewSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			renewSchedular.schedule();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}

	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 8 * * ?")
	//@Scheduled(cron = "0 30 1 * * ?")
	//@EventListener(ApplicationReadyEvent.class)
	public void startNotificationSMSSchedular() {
		log.info("The time is now {startNotificationSMSSchedular}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			notificationSchedular.schedule();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}


	// @Scheduled(fixedRate=86400000)
	// @Scheduled(cron = "0 0 6 * * ?")
	//@Scheduled(cron = "0 30 2 * * ?")
	//@EventListener(ApplicationReadyEvent.class)
	public void startExpiredPolicyUpdateData() {
		log.info("The time is now {startExpiredPolicyUpdateData}", dateFormat.format(new Date()));

		if (service.getDbStatus()) {
			service.startExpiredPolicyUpdateData();
		} else {
			System.out.println(
					"|************|  RENEWAL QUOTE TO POLICY API THREAD is Switched OFF from DB  |*************|");
		}
	}

}
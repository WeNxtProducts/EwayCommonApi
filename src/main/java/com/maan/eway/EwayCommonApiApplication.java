package com.maan.eway;

import javax.annotation.PostConstruct;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import com.maan.eway.notification.service.JobRunrService;
@EnableCaching
@SpringBootApplication
public class EwayCommonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwayCommonApiApplication.class, args);
	}
	
	
}

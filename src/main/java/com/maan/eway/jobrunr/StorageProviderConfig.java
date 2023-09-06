package com.maan.eway.jobrunr;

import javax.annotation.PostConstruct;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maan.eway.notification.service.JobRunrService;

@Configuration
public class StorageProviderConfig {

   @Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }
    @Autowired
	private JobScheduler jobScheduler;
	
	@Autowired
	private JobRunrService ourservice;
	
	@Job(name = "Pushing Tracking Details")
	@PostConstruct
	//@Recurring(cron = "@hourly"  )
	public void jobScheduleForTracking() {
	//log.info("Job Runner Started...");
//		scheduler.scheduleRecurrently(Cron.minutely() , () -> integService.pushClaimTrackings() );
		
		jobScheduler.scheduleRecurrently(Cron.minutely() ,() -> ourservice.jobProcess() );
	}
}
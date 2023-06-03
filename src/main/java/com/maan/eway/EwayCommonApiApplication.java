package com.maan.eway;

import javax.sql.DataSource;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.server.JobActivator;
import org.jobrunr.server.configuration.BackgroundJobServerWorkerPolicy;
import org.jobrunr.server.configuration.FixedSizeBackgroundJobServerWorkerPolicy;
import org.jobrunr.storage.sql.common.SqlStorageProviderFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableCaching
@SpringBootApplication
@EnableAsync
public class EwayCommonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwayCommonApiApplication.class, args);
	}
	
	  	/*@Bean(name = "NoticationThread-M")
	    public Executor threadPoolTaskExecutor() {
	  		ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
	  		t.setCorePoolSize(2);
	  		t.setMaxPoolSize(2);
	  		t.setQueueCapacity(2);
	  		t.setThreadNamePrefix("Mail(Async)-");
	  		t.initialize();	  		
	        return t;
	    }*/
	  	
//	    @Bean
//	    public JobScheduler initJobRunr(DataSource dataSource, JobActivator jobActivator) {
//	       /* return JobRunr.configure()
//	                .useJobActivator(jobActivator)
//	                .useStorageProvider(SqlStorageProviderFactory
//	                          .using(dataSource))
//	                .useBackgroundJobServer()
//	                .useDashboard(9879)	                
//	                .initialize();*/
//	    	return JobRunr.configure()
//	    			.useJobActivator(jobActivator)
//	    			.useStorageProvider(SqlStorageProviderFactory
//	    			.using(dataSource))
//	    			.useBackgroundJobServer()
//	    			.useDashboard(9879)
//	    			.initialize();
//	    }

	    @Bean
	    public JobScheduler initJobRunr(DataSource dataSource, JobActivator jobActivator) {
	        return JobRunr.configure()
	                .useJobActivator(jobActivator)
	                .useStorageProvider(SqlStorageProviderFactory
	                          .using(dataSource))
	                .useBackgroundJobServer()
	                .useDashboard(9879)	                
	                .initialize();
	    }


}

package com.maan.eway;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@EnableCaching
@SpringBootApplication
@EnableAsync
public class EwayCommonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwayCommonApiApplication.class, args);
	}
	
	  	@Bean(name = "NoticationThread-M")
	    public Executor threadPoolTaskExecutor() {
	  		ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
	  		t.setCorePoolSize(2);
	  		t.setMaxPoolSize(2);
	  		t.setQueueCapacity(2);
	  		t.setThreadNamePrefix("Mail(Async)-");
	  		t.initialize();	  		
	        return t;
	    }
}

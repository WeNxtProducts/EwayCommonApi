package com.maan.eway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
@EnableCaching
@SpringBootApplication
public class EwayCommonApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EwayCommonApiApplication.class, args);
	}
	
	 
}

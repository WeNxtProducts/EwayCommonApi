package com.maan.eway;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maan.eway.req.calcengine.CalcEngine;
@Configuration
@EnableCaching
public class CachingConfig   {
	
	  @Bean
	  public CacheManager  cacheManager() {
	    return new SpringCache2kCacheManager()
	      .defaultSetup(b->b.entryCapacity(2000))
	      .addCaches(
	        b->b.name("RatingType").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false)	        
	        );
		
	  }
	
	
	    @Bean
		public KeyGenerator ratingTypeKeyGen() {
			
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				CalcEngine e=(CalcEngine)params[0];
				String s=(String) params[1];
			//	String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;factorTypeId:"+factorTypeId+";";

				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				.append(s)
				.append(e.getBranchCode())
				.toString();
				return string;
			}
			
		};
	}
	    @Bean
		public KeyGenerator productTypeKeyGen() {
			
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				CalcEngine e=(CalcEngine)params[0];
				//String s=(String) params[1];
			//	String search="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;factorTypeId:"+factorTypeId+";";

				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				//.append(s)
				//.append(e.getBranchCode())
				.toString();
				return string;
			}
			
		};
	}
	    
	
	
}

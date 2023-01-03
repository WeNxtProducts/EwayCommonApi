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
	        b->b.name("RatingType").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("ProductType").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadTax").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadProRata").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("LoadConstant").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false)
	        );
		
	  }
	
	
	    @Bean
		public KeyGenerator ratingTypeKeyGen() {
			
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				CalcEngine e=(CalcEngine)params[0];
				String s=(String) params[1]; 
				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				.append(s)
				.append(e.getBranchCode())
				.append("rating")
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
				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				.append("producttype")
				.toString();
				return string;
			}
			
		};
	}
	    
	    
	    	@Bean
	    	public KeyGenerator loadTaxKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];	    				
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append("99999")
	    						.append("loadtax")
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    	
	    	@Bean
	    	public KeyGenerator loadProRataKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())	  
	    						.append("prorata")
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    	
	    	@Bean
	    	public KeyGenerator loadConstantKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append(e.getBranchCode())
	    						.append("99999")
	    						.append("constant")
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    	
	    
	
	
}


package com.maan.eway;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.RatingInfo;
@Configuration
@EnableCaching
public class CachingConfig   {
	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	  @Bean
	  public CacheManager  cacheManager() {
	    return new SpringCache2kCacheManager()
	      .defaultSetup(b->b.entryCapacity(2000))
	      .addCaches(
	        b->b.name("RatingType").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("ProductType").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadTax").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadProRata").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("LoadConstant").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(true),
	        b->b.name("ProductToRawtable").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("EndtMasterData").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("getCachedRatingFields").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadfactorOnlyquery").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("countfactorOnlyquery").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false)
	        
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
	    						.append(e.getBranchCode())
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
	    				String periodOfInsurance=(String) params[1];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append(periodOfInsurance)
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
	    	
	    
	
	    	 @Bean
	    	 public KeyGenerator getProductIdBasedRawTable() {
	    		 return new KeyGenerator() {
	    			 @Override
	    			 public Object generate(Object target, Method method, Object... params) {
	    				 CalcEngine e=(CalcEngine)params[0]; 
	    				 String string = new StringBuilder().append(e.getInsuranceId())
	    						 .append(e.getProductId())
	    						 .append("rawtable")
	    						 .toString();
	    				 return string;
	    			 }

	    		 };
	    	 }
	    	 
	    	 @Bean
	    	 public KeyGenerator getEndtMasterData() {
	    		 return new KeyGenerator() {
	    			 @Override
	    			 public Object generate(Object target, Method method, Object... params) {
	    				 //CalcEngine e=(CalcEngine)params[0]; 
	    				 String string = new StringBuilder().append(params[0])
	    						 .append(params[1])
	    						 .append(params[2])
	    						 .append("EndtTable")
	    						 .toString();
	    				 return string;
	    			 }

	    		 };
	    	 }
	    	 @Bean
		    	public KeyGenerator getCachedRatingFieldsKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				RatingInfo r=(RatingInfo)params[1];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(r.getRatingFieldId())
		    						.append("RatingFields")
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
		    	
	    	 @Bean
		    	public KeyGenerator loadfactorOnlyqueryKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				 

		    				String r=(String)params[1];
		    				String r1=(String)params[2];
		    				String r2=(String)params[3];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(e.getSectionId())
		    						.append(e.getAgencyCode())
		    						.append(r)
		    						.append(r1)
		    						.append(r2)
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
	    	 @Bean
		    	public KeyGenerator countfactorOnlyqueryKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				 

		    				String r=(String)params[1];
		    				String r1=(String)params[2];
		    				String r2=(String)params[3];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(e.getSectionId())
		    						.append(e.getAgencyCode())
		    						.append(r)
		    						.append(r1)
		    						.append(r2)
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
	    	 
}

package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.SeqCreditno;
import com.maan.eway.bean.SeqDebitnote;
import com.maan.eway.bean.SeqPolicyno;
import com.maan.eway.bean.SeqProductbenefit;
import com.maan.eway.bean.SeqRefno;
import com.maan.eway.bean.SeqTinyrefno;
import com.maan.eway.repository.SeqCreditnoRepository;
import com.maan.eway.repository.SeqDebitnoteRepository;
import com.maan.eway.repository.SeqPolicynoRepository;
import com.maan.eway.repository.SeqProductbenefitRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.SeqTinyrefnoRepository;

@Service
public class GenerateSeqNoServiceImpl {
	
	private Logger log = LogManager.getLogger(GenerateSeqNoServiceImpl.class);
	
	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private SeqPolicynoRepository polNoRepo;
	
	@Autowired
	private SeqDebitnoteRepository debitRepo;
	
	@Autowired
	private SeqCreditnoRepository creditRepo;
	
	@Autowired
	private SeqTinyrefnoRepository tinyRefRepo;
	
	@Autowired
	private SeqProductbenefitRepository benefitRepo ;

	 public synchronized String generateRefNo() {
	       try {
	    	   SeqRefno entity;
	            entity = refNoRepo.save(new SeqRefno());          
	            return String.format("%05d",entity.getRequestReferenceNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 

	 public synchronized String generatePolicyNo(String productCode,String branchcode) {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
	       try {
	    	    SeqPolicyno entity;
	            entity = polNoRepo.save(new SeqPolicyno());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	            //P11/2021/100/1002/10/020459
	            
	            return "P11/"+year+"/"+branchcode+"/"+productCode+"/10/"+String.format("%07d",entity.getPolicyno()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateDebitNo(String branchCode) {
	       try {
	    	    SeqDebitnote entity;
	            entity = debitRepo.save(new SeqDebitnote());          
	            return "DNP"+branchCode+"-"+String.format("%09d",entity.getDebitnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateCreditNo(String branchCode) {
	       try {
	    	    SeqCreditno entity;
	            entity = creditRepo.save(new SeqCreditno());          
	            return "CNP"+branchCode+"-"+String.format("%09d",entity.getCreditnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateTinyRefNo() {
	       try {
	    	    SeqTinyrefno entity;
	            entity = tinyRefRepo.save(new SeqTinyrefno());          
	            return String.format("%05d",entity.getTinyUrlRefNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateBenefitId() {
	       try {
	    	    SeqProductbenefit entity;
	            entity = benefitRepo.save(new SeqProductbenefit());          
	            return String.format("%05d",entity.getBenefitId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
}

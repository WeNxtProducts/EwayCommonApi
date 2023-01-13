package com.maan.eway.common.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.SeqCreditno;
import com.maan.eway.bean.SeqDebitnote;
import com.maan.eway.bean.SeqPolicyno;
import com.maan.eway.bean.SeqRefno;
import com.maan.eway.repository.SeqCreditnoRepository;
import com.maan.eway.repository.SeqDebitnoteRepository;
import com.maan.eway.repository.SeqPolicynoRepository;
import com.maan.eway.repository.SeqRefnoRepository;

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
	 

	 public synchronized String generatePolicyNo() {
	       try {
	    	    SeqPolicyno entity;
	            entity = polNoRepo.save(new SeqPolicyno());    
	            Date currentDate = Calendar.getInstance().getTime();
	            int year = currentDate.getYear();
	            //P11/2021/100/1002/10/020459
	            
	            return "P11/"+year+"/100/1002/10/"+String.format("%05d",entity.getPolicyno()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateDebitNo() {
	       try {
	    	    SeqDebitnote entity;
	            entity = debitRepo.save(new SeqDebitnote());          
	            return String.format("%05d",entity.getDebitnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
	 
	 public synchronized String generateCreditNo() {
	       try {
	    	    SeqCreditno entity;
	            entity = creditRepo.save(new SeqCreditno());          
	            return String.format("%05d",entity.getCreditnote()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 
	 }
}

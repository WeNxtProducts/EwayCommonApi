package com.maan.eway.common.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.SeqRefno;
import com.maan.eway.repository.SeqRefnoRepository;

@Service
public class GenerateSeqNoServiceImpl {
	
	private Logger log = LogManager.getLogger(GenerateSeqNoServiceImpl.class);
	
	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	

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
	 

	 
	 
	 
}

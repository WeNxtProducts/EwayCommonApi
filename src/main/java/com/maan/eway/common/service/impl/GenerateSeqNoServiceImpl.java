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
import com.maan.eway.bean.SeqErrorCode;
import com.maan.eway.bean.SeqPolicyno;
import com.maan.eway.bean.SeqPolicynoMadison;
import com.maan.eway.bean.SeqProductbenefit;
import com.maan.eway.bean.SeqRefno;
import com.maan.eway.bean.SeqTinyrefno;
import com.maan.eway.repository.SeqCreditnoRepository;
import com.maan.eway.repository.SeqDebitnoteRepository;
import com.maan.eway.repository.SeqErrorCodeRepository;
import com.maan.eway.repository.SeqPolicynoMadisonRepository;
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
	private SeqPolicynoMadisonRepository polNoMadisonRepo;
	
	@Autowired
	private SeqDebitnoteRepository debitRepo;
	
	@Autowired
	private SeqCreditnoRepository creditRepo;
	
	@Autowired
	private SeqTinyrefnoRepository tinyRefRepo;
	
	@Autowired
	private SeqProductbenefitRepository benefitRepo ;
	
	@Autowired
	private SeqErrorCodeRepository errorCodeRepo ;

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
	 
	
	 

	 public synchronized String generatePolicyNo(String productCode,String branchcode, String companyId, String vehUsageCoreappcode, String productId, String itemvalue ) { //madison 100004
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
		 String policyNo = "";
	       try {
	    	   SeqPolicynoMadison entity;
	            entity = polNoMadisonRepo.save(new SeqPolicynoMadison());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	       
	            	 //---> P/01/4013/001377/2023/B,  P/01/4013/001377/2023/E
		            //----> 'P'||'/'||LvBranchCd||'/'||LvVeh_Usage||'/'||Lpad(MOTOR_POLICY_NO.Nextval,'6','0')||'/'||TO_CHAR(SYSDATE,'YYYY') //others productid
	            	//end local, uat-->/T, live-->/B
	            	
	            	if(productId.equalsIgnoreCase("5"))
	            		policyNo = "P/" + branchcode + "/"  + vehUsageCoreappcode + "/" + String.format("%06d",entity.getPolicyno()) +  "/" + year + itemvalue;
	            	else
	            		policyNo = "P/" + branchcode + "/"  + productCode + "/" + String.format("%06d",entity.getPolicyno()) +  "/" + year + itemvalue;
	            	
	    
	            
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return policyNo;
	 
	 }
	 
	 public synchronized String generatePolicyNo(String productCode,String branchcode) {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
		 String policyNo = "";
	       try {
	    	    SeqPolicyno entity;
	            entity = polNoRepo.save(new SeqPolicyno());    
	            Date currentDate = Calendar.getInstance().getTime();
	            String year =  sdf.format(new Date()) ;
	        
	            	policyNo =  "P11/"+year+"/"+branchcode+"/"+productCode+"/10/"+String.format("%07d",entity.getPolicyno()) ;
	        
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return policyNo;
	 
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
	 
	 public synchronized String generateErrorCode() {
	       try {
	    	   SeqErrorCode entity;
	            entity = errorCodeRepo.save(new SeqErrorCode());          
	            return String.format("%04d",entity.getErrorCode()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
}

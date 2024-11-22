package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maan.eway.bean.CoInsuranceInfo;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CoInsuranceInfoRepository;
import com.maan.eway.req.CoInsuranceDetails;
import com.maan.eway.req.CoInsuranceInfoReq;
import com.maan.eway.service.CoInsuranceInfoService;

import net.sf.jsqlparser.util.validation.ValidationError;

import org.springframework.transaction.annotation.Transactional;

@Service
public   class CoInsuranceInfoServiceImpl  implements CoInsuranceInfoService  {
	

	private Logger log=LogManager.getLogger(CoInsuranceInfoServiceImpl.class);
	@Autowired
	private CoInsuranceInfoRepository GetdataReq;

	
	

	@Override
	@Transactional  
	public CommonRes CoInsuranceInfosaveupdate(CoInsuranceDetails req) {
	    CommonRes response = new CommonRes();
	    
	    try {
	         GetdataReq.deleteByQuoteno(req.getQuoteno()); 
	        
	         
             List<CoInsuranceInfo> saveList = new ArrayList<>();
	         for (CoInsuranceInfoReq r : req.getCoinsreq()) {
	         CoInsuranceInfo entity = new CoInsuranceInfo();
	         ModelMapper mapper = new ModelMapper();
	          mapper.map(r, entity);
	         entity.setQuoteno(req.getQuoteno());
	         entity.setRequestreferenceno(req.getRequestreferenceno());
	         entity.setProductid(req.getProductid());
	         entity.setStatus("Y");
	         entity.setEffectivedatestart(convertToDateViaSqlDate(LocalDate.now()));
	         entity.setEffectivedateend(convertToDateViaSqlDate(LocalDate.now().plusYears(15)));
	         saveList.add(entity);  
	        }

	        if (!saveList.isEmpty()) {
	           
	            GetdataReq.saveAll(saveList);  
	            response.setErrorMessage(Collections.EMPTY_LIST);
				response.setMessage("Success");
			}else {
				response.setCommonResponse(null);
				response.setErrorMessage(Collections.EMPTY_LIST);
				response.setMessage("Failed");
				response.setIsError(true);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return response;
	}


	

private Date convertToDateViaSqlDate(LocalDate localDate) {
    return java.sql.Date.valueOf(localDate);
}
	   
@Override
@Transactional
public CommonRes CoInsuranceInfodelete(String QUOTENO) {
	
	CommonRes response = new CommonRes();
    try {
    	GetdataReq.deleteByQuoteno(QUOTENO);
    	response.setCommonResponse(null);
		response.setErrorMessage(Collections.EMPTY_LIST);
		response.setMessage("Success");
	}catch (Exception e) {
		e.printStackTrace();
		log.error(e);
	}
	return response;
}
   
@Override
public CommonRes getAllByQuoteNo(String QUOTENO) {
    CommonRes response = new CommonRes();

    List<CoInsuranceInfo> resList = new ArrayList<>();
    

    List<CoInsuranceInfo> d = GetdataReq.findByQuoteno(QUOTENO);   
    
   
    if (d != null) {
    	resList.addAll(d);
        response.setCommonResponse(resList);
        response.setErrorMessage(Collections.EMPTY_LIST);
        response.setMessage("Success");
    } else {
        response.setCommonResponse(null);
        response.setErrorMessage(Collections.EMPTY_LIST);
        response.setMessage("Failed");
        response.setIsError(true);
    }
 
    return response;
}

 @Override

 public List<Error> validatecoinsurancedetails(CoInsuranceInfoReq req) {
     List<ValidationError> errors = new ArrayList<>();
     CoInsuranceDetails entity = new CoInsuranceDetails();
     List<Error> error = null;
	try {
         // Validation checks
         if (req.getSno() == 0) {
             errors.add(new ValidationError("Please Enter SNo"));
         }

         if (req.getInsurancecompanyid() == 0) {
             errors.add(new ValidationError("Please Enter Insurance Company ID"));
         }

         if (StringUtils.isBlank(req.getInsurancecompanyname())) {
             errors.add(new ValidationError("Please Enter Insurance Company Name"));
         }

         if (req.getSharedpercentage() == null || req.getSharedpercentage().compareTo(BigDecimal.ZERO) == 0) {
             errors.add(new ValidationError("Please Enter Shared Percentage"));
         }

         if (StringUtils.isBlank(req.getLeaderparticipant())) {
             errors.add(new ValidationError("Please Enter Leader Participant"));
         }

         // Assuming 'entity' needs to be populated with some fields as well
         if (entity.getProductid() == 0) {
             errors.add(new ValidationError("Please Enter Product ID"));
         }

         if (StringUtils.isBlank(entity.getRequestreferenceno())) {
             errors.add(new ValidationError("Please Enter Request Reference No"));
         }

     } catch (Exception e) {
         e.printStackTrace();
         log.info("Exception occurred: " + e.getMessage());
     
         errors.add(new ValidationError("An unexpected error occurred: " + e.getMessage()));
         return error;
     }
		
	return error;
 }




@Override
public List<String> validatecoinsurancedetails(CoInsuranceDetails req) {
	// TODO Auto-generated method stub
	return null;
}

}

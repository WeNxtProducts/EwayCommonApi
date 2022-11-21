package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.bean.UwQuestionsDetailsArch;
import com.maan.eway.bean.UwQuestionsDetailsId;
import com.maan.eway.common.req.UwQuestionsDetailsGetReq;
import com.maan.eway.common.req.UwQuestionsDetailsSaveReq;
import com.maan.eway.common.res.UwQuestionsDetailsRes;
import com.maan.eway.common.service.UwQuestionsDetailsService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.UwQuestionsDetailsArchRepository;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class UwQuesitonsDetailsServiceImpl implements UwQuestionsDetailsService {

	@Autowired
	private UwQuestionsDetailsRepository uwRepo;

	@Autowired
	private UwQuestionsDetailsArchRepository uwArchRepo;

	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(UwQuesitonsDetailsServiceImpl.class);
	
	
	@Override
	public List<Error> validateUwQuestions(List<UwQuestionsDetailsSaveReq> data) {
		List<Error> error = new ArrayList<Error>();

		try {
			Long row = 0L ;

			for(UwQuestionsDetailsSaveReq req : data) {
				row =row+1;
			if (StringUtils.isBlank(req.getCompanyId())) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId" +row));
			} else if (req.getCompanyId().length() > 20) {
				error.add(new Error("01", "CompanyId", "Please Enter CompanyId within 20 Characters"+row));
			}

			if (StringUtils.isBlank(req.getProductId())) {
				error.add(new Error("02", "ProductId", "Please Enter ProductId"+row));
			}
			if (StringUtils.isNotBlank(req.getUwQuestionDesc()) &&req.getUwQuestionDesc().length() > 100) {
				error.add(new Error("03", "UwQuestionDesc", "Please Enter UwQuestionDesc within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getQuestionType()) &&req.getQuestionType().length() > 100) {
				error.add(new Error("04", "QuestionType", "Please Enter QuestionType within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getRemarks()) && req.getRemarks().length() > 100) {
				error.add(new Error("05", "Remarks", "Please Enter Remarks within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo"+row));
			}
			else if (req.getQuestionType().length() > 20) {
				error.add(new Error("06", "RequestReferenceNo", "Please Enter RequestReferenceNo within 20 Characters"+row));
			}
			if (StringUtils.isBlank(req.getVehicleId())) {
				error.add(new Error("07", "VehicleId", "Please Enter VehicleId"+row));
			}
			if (StringUtils.isBlank(req.getUwQuestionId())) {
				error.add(new Error("08", "UwQuestionId", "Please Enter UwQuestionId"+row));
			}
			if (StringUtils.isNotBlank(req.getValue()) &&req.getValue().length() > 100) {
				error.add(new Error("09", "Value", "Please Enter Value within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getCreatedBy()) &&req.getCreatedBy().length() > 100) {
				error.add(new Error("10", "CreatedBy", "Please Enter CreatedBy within 100 Characters"+row));
			}
			if (StringUtils.isNotBlank(req.getUpdatedBy()) &&req.getUpdatedBy().length() > 100) {
				error.add(new Error("11", "UpdatedBy", "Please Enter UpdatedBy within 100 Characters"+row));
			}
			if (StringUtils.isBlank(req.getBranchCode())) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode"+row));
			}
			else if (req.getBranchCode().length() > 20) {
				error.add(new Error("12", "BranchCode", "Please Enter BranchCode within 20 Characters"+row));
			}
				
			}
		} catch (Exception e) {

			log.error(e);
			e.printStackTrace();
			error.add(new Error("15", "Common Error", e.getMessage()));
		}
		return error;
	}



	@Override
	public SuccessRes saveUwQuestions(List<UwQuestionsDetailsSaveReq> req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try {
			
			for(UwQuestionsDetailsSaveReq data : req) {
			UwQuestionsDetailsId id = new UwQuestionsDetailsId();
			id.setCompanyId(data.getCompanyId());
			id.setProductId(Integer.valueOf(data.getProductId()));
			id.setRequestReferenceNo(data.getRequestReferenceNo());
			id.setUwQuestionId(Integer.valueOf(data.getUwQuestionId()));
			id.setVehicleId(Integer.valueOf(data.getVehicleId()));
			UwQuestionsDetails saveData = new UwQuestionsDetails();
			UwQuestionsDetailsArch saveData1 = new UwQuestionsDetailsArch();
			
			Optional<UwQuestionsDetails> da = uwRepo.findById(id);
			if(da.isPresent()) {
			uwRepo.delete(saveData);	
			saveData = dozerMapper.map(data,UwQuestionsDetails.class);
			saveData.setEntryDate(da.get().getEntryDate());		
			saveData.setStatus(da.get().getStatus());
			saveData.setUpdatedDate(new Date());			
			res.setResponse("Updated Successfully");

			saveData1 = dozerMapper.map(data,UwQuestionsDetailsArch.class);
			Long count = uwArchRepo.count();
			Integer sno = Integer.valueOf(count.toString())+1;
			saveData1.setArchId(sno.toString());
			saveData1.setEntryDate(da.get().getEntryDate());		
			saveData1.setStatus(da.get().getStatus());		
			
			uwArchRepo.save(saveData1);	
			
			
			}
			else {
			saveData = dozerMapper.map(data,UwQuestionsDetails.class);
			saveData.setEntryDate(new Date());
			saveData.setStatus("Y");
			saveData.setUpdatedDate(data.getUpdatedDate());
			res.setResponse("Inserted Successfully");
			}
			uwRepo.save(saveData);				
			res.setSuccessId(data.getRequestReferenceNo());			
			}
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<UwQuestionsDetailsRes> getUwQuestionsDetails(UwQuestionsDetailsGetReq req) {
		List<UwQuestionsDetailsRes> resList = new ArrayList<UwQuestionsDetailsRes>();
		
		try {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<UwQuestionsDetails> datas = uwRepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(req.getCompanyId(),Integer.valueOf(req.getProductId()),req.getRequestReferenceNo(),Integer.valueOf(req.getVehicleId()));
		for(UwQuestionsDetails data : datas) {
			UwQuestionsDetailsRes res = new UwQuestionsDetailsRes();
			res=dozerMapper.map(data,UwQuestionsDetailsRes.class);
			resList.add(res);
		}
		}
		catch(Exception e)
		{
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return resList;
}

	
}

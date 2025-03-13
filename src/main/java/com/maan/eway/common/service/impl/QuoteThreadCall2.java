package com.maan.eway.common.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceInsuredDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.InsurerInfo;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EServiceDriverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceInsuredDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.InsurerInfoRepository;
import com.maan.eway.repository.MsDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;

import jakarta.persistence.EntityManager;
import lombok.Data;

@Data
public class QuoteThreadCall2 implements Callable<Object>  {
	
	private Logger log = LogManager.getLogger(getClass());
	
	Gson json = new Gson();
	private String type;
	private QuoteThreadReq request ;
	private EntityManager em;
	
	// customer
	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo ;
	@Autowired
	private PersonalInfoRepository perInfoRepo ;
	
	// Insurer
	@Autowired
	private EserviceInsuredDetailsRepository eserInsurerRepo ;
	@Autowired
	private InsurerInfoRepository insurerInfoRepo ;
		
	// motor
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo ;
	
	// Home
	private HomePositionMasterRepository homeRepo ;
	
	// Travel 
	private EserviceTravelDetailsRepository eserTraRepo ;
	
	// Building
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	//Common
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	// productId
	private String travelProductId;
	
	// Document
	private DocumentUniqueDetailsRepository docUniqueRepo ;
	private DocumentTransactionDetailsRepository docTranRepo ;
	private ProductEmployeesDetailsRepository empRepo;
	private EServiceDriverDetailsRepository eservicedriverRepo;
	private MsDriverDetailsRepository msDriverRepo;
	public QuoteThreadCall2(String type , QuoteThreadReq request , EntityManager em ,EserviceCustomerDetailsRepository eserCustRepo ,
			EServiceMotorDetailsRepository eserMotRepo  ,
			EserviceTravelDetailsRepository eserTraRepo ,EserviceCommonDetailsRepository eserCommonRepo,
			  EserviceBuildingDetailsRepository eserBuildRepo, EserviceInsuredDetailsRepository eserInsurerRepo,  InsurerInfoRepository insurerInfoRepo) {
		this.type = type;
		this.request = request;
		this.em=em;
		this.eserCustRepo = eserCustRepo ;
		this.eserMotRepo = eserMotRepo ;
		this.eserTraRepo = eserTraRepo ;
		this.eserBuildRepo = eserBuildRepo ;
		this.eserCommonRepo=eserCommonRepo;
		this.eserInsurerRepo=eserInsurerRepo;
		this.insurerInfoRepo=insurerInfoRepo;
	} 
	
	@Override
	public  Map<String, Object>  call() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_OneTime--> type: " + type);

			if (type.equalsIgnoreCase("InsurerSave")) {
				map.put("InsurerSave", call_InsurerSave(request));
			} 
						
		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
    @Transactional
	private synchronized Map<String,Object> call_InsurerSave(QuoteThreadReq request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			InsurerInfo findInfo =  insurerInfoRepo.findByCustomerId(request.getCustomerId());
	         String sectionid=request.getVehicleIdsList().get(0).getSectionId();
			if(findInfo!=null) {
				insurerInfoRepo.deleteByCustomerId(request.getCustomerId());
			}
			

			// FindData 
			String customerRefNo = "" ;
			if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId) ) {
				EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
				customerRefNo = travelData.getCustomerReferenceNo();
				
			} else if(request.getMotorYn().equalsIgnoreCase("M")) {
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
				customerRefNo = motorData.getCustomerReferenceNo();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A")) {
				EserviceBuildingDetails buldingData = eserBuildRepo.findByRequestReferenceNo(request.getRequestReferenceNo() ).get(0);
				customerRefNo = buldingData.getCustomerReferenceNo();
				
			}else {
				//***EserviceCommonDetails commonData = eserCommonRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
//				EserviceCommonDetails commonData = eserCommonRepo.findByRequestReferenceNoAndRiskIdAndSectionId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId(),sectionid);
//				EserviceCommonDetails commonData = eserCommonRepo.findByRequestReferenceNoAndOriginalRiskIdAndSectionId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId(),sectionid);
				EserviceCommonDetails commonData = eserCommonRepo.findByRequestReferenceNo(request.getRequestReferenceNo() ).get(0);
				customerRefNo = commonData.getCustomerReferenceNo();
				
			}
			
			// Find Customer
			EserviceInsuredDetails insData = eserInsurerRepo.findByCustomerReferenceNo(customerRefNo);
			
			if(insData!=null) {
			// Save Personal INfo
			InsurerInfo insurerInfo = new InsurerInfo();
			dozerMapper.map(insData, insurerInfo);
			insurerInfo.setCustomerId(request.getCustomerId());
			insurerInfo.setEntryDate(new Date());
			insurerInfo.setCreatedBy(request.getCreatedBy());
			//personalInfo.setVipFlag(custData.getVipFlag());
			//personalInfo.setRiskAssessmentDate(custData.getRiskAssessmentDate());
			//personalInfo.setPhoneNoCode(custData.getPhoneNoCode());
			//personalInfo.setFather_name(custData.getFather_name());		
			//personalInfo.setMother_name(custData.getMother_name());	

			insurerInfoRepo.save(insurerInfo);
			
			log.error("Save Personal Info is ---> " + json.toJson(insurerInfo));
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Insurer Details") ;
		}
	
		return res;
	}

}
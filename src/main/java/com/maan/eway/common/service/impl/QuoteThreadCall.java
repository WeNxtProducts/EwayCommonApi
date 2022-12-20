package com.maan.eway.common.service.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPassengerHistory;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;


public class QuoteThreadCall implements Callable<Object>  {
	
	private Logger log = LogManager.getLogger(getClass());
	
	Gson json = new Gson();
	
	private String type;
	private QuoteThreadReq request ;
	private EntityManager em;
	
	// customer
	private EserviceCustomerDetailsRepository eserCustRepo ;
	private PersonalInfoRepository perInfoRepo ;
	
	// motor
	private EServiceMotorDetailsRepository eserMotRepo ;
	private MotorDataDetailsRepository motorRepo ;
	
	// Cover 
	private FactorRateRequestDetailsRepository facRateRepo ;
	private CoverDetailsRepository coverRepo ;
	
	// Home
	private HomePositionMasterRepository homeRepo ;
	
	// Travel 
	private EserviceTravelDetailsRepository eserTraRepo ;
	private EserviceTravelGroupDetailsRepository eserGroupRepo ;
	private TravelPassengerDetailsRepository traPassRepo  ;
	private TravelPassengerHistoryRepository traPassHisRepo  ;
	
	// productId
	private String motorProductId;
	private String travelProductId;
	
	
	public QuoteThreadCall(String type , QuoteThreadReq request , EntityManager em ,EserviceCustomerDetailsRepository eserCustRepo ,
			EServiceMotorDetailsRepository eserMotRepo  ,FactorRateRequestDetailsRepository facRateRepo  ,PersonalInfoRepository perInfoRepo  , MotorDataDetailsRepository motorRepo , 
			 CoverDetailsRepository coverRepo  , HomePositionMasterRepository homeRepo  ,EserviceTravelDetailsRepository eserTraRepo ,EserviceTravelGroupDetailsRepository eserGroupRepo ,
			 TravelPassengerDetailsRepository    traPassRepo ,TravelPassengerHistoryRepository traPassHisRepo  , String motorProductId ,String travelProductId) {
		this.type = type;
		this.request = request;
		this.em=em;
		this.eserCustRepo = eserCustRepo ;
		this.eserMotRepo = eserMotRepo ;
		this.facRateRepo = facRateRepo ;
		this.perInfoRepo = perInfoRepo ;
		this.motorRepo = motorRepo ;
		this.coverRepo = coverRepo ;
		this.homeRepo = homeRepo ;
		this.eserTraRepo = eserTraRepo ;
		this.eserGroupRepo = eserGroupRepo ;
		this.traPassRepo = traPassRepo ;
		this.motorProductId = motorProductId ;
		this.travelProductId = travelProductId ;
		this.traPassHisRepo = traPassHisRepo ;
	} 
	
	@Override
	public  Map<String, Object>  call() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_OneTime--> type: " + type);

			if (type.equalsIgnoreCase("CustomerSave")) {

				map.put("CustomerSave", call_CustomerSave(request));

			} else if (type.equalsIgnoreCase("MotorSave")) {

				map.put("MotorSave", call_MotorSave(request));

			} else if (type.equalsIgnoreCase("TravelSave")) {

				map.put("TravelSave", call_TravelSave(request));

			} else if (type.equalsIgnoreCase("CoverSave")) {

				map.put("CoverSave", call_CoverSave(request));

			} else if (type.equalsIgnoreCase("QuoteSave")) {

				map.put("QuoteSave", call_QuoteSave(request));

			}

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
	
	private synchronized Map<String,Object> call_CustomerSave(QuoteThreadReq request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Long findInfo =  perInfoRepo.countByCustomerId(request.getCustomerId());
			if (findInfo > 0 && request.getRowCount().equals(1) ) {
				perInfoRepo.deleteByCustomerId(request.getCustomerId());

			}
			// FindData 
			String customerRefNo = "" ;
			if(request.getProductId().equalsIgnoreCase(motorProductId) ) {
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndVehicleId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
				customerRefNo = motorData.getCustomerReferenceNo();
			} else if(request.getProductId().equalsIgnoreCase(travelProductId) ) {
				EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
				customerRefNo = travelData.getCustomerReferenceNo();
			}
			
			// Find Customer
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(customerRefNo);
			
			// Save Personal INfo
			PersonalInfo personalInfo = new PersonalInfo();
			dozerMapper.map(custData, personalInfo);
			personalInfo.setCustomerId(request.getCustomerId());
			personalInfo.setEntryDate(new Date());
			personalInfo.setCreatedBy(request.getCreatedBy());
			perInfoRepo.save(personalInfo);
			
			log.error("Save Personal Info is ---> " + json.toJson(personalInfo));
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Customer Details") ;
		}
	
		return res;
	}
	
	
	private synchronized  Map<String,Object>  call_MotorSave(QuoteThreadReq  request  ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			 // Delete Old Record
			// // Delete Old Record
			Long motorInfo =  motorRepo.countByQuoteNo(request.getQuoteNo());
			if (motorInfo > 0 && request.getRowCount().equals(1) ) {
				//Delete data
				motorRepo.deleteByQuoteNo(request.getQuoteNo());
				
			}
			
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,request.getVehicleId());
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = defaultCovers.stream().filter( o -> o.getIsSelected()!=null &&   (! o.getIsSelected().equalsIgnoreCase("D")) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc()  ).sum();
			
			
			// Find Motor
			EserviceMotorDetails eserMotors = eserMotRepo.findByRequestReferenceNoAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
			
			// Update Eservice Motor
			eserMotors.setActualPremiumFc(premiumFc);
			eserMotors.setActualPremiumLc(premiumLc);
			eserMotors.setOverallPremiumFc(overAllPremiumFc);
			eserMotors.setOverallPremiumLc(overAllPremiumLc);
			eserMotors.setQuoteNo(request.getQuoteNo());
			eserMotors.setCustomerId(request.getCustomerId());
			eserMotRepo.saveAndFlush(eserMotors);
			
			// Save Motro Details
			MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserMotors, motorData);
			motorData.setEntryDate(new Date());	
			motorData.setCreatedBy(request.getCreatedBy());
			motorData.setQuoteNo(request.getQuoteNo());
			motorData.setCustomerId(request.getCustomerId());
			motorData.setStatus("Y");
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( eserMotors.getVehicleId())).collect(Collectors.toList());
			motorData.setVdRefno(filterCover.get(0).getVdRefno());	
			motorData.setMsRefno(filterCover.get(0).getMsRefno());		
			motorData.setCdRefno(filterCover.get(0).getCdRefno());	
			motorData.setActualPremiumFc(premiumFc);
			motorData.setActualPremiumLc(premiumLc);
			motorData.setOverallPremiumFc(overAllPremiumFc);
			motorData.setOverallPremiumLc(overAllPremiumLc);
			motorRepo.saveAndFlush(motorData);
			log.error("Save Motor Info is ---> " + json.toJson(motorData));
			
			// Update Eservice Motor
			
			
	
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Details" ) ;
		}
	
		return res;
	}
	

	private synchronized  Map<String,Object>  call_TravelSave(QuoteThreadReq  request  ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			//  // Delete Old Record
			Long travelInfo =  traPassRepo.countByQuoteNoAndPassengerId(request.getQuoteNo() ,request.getVehicleId());
			if (travelInfo > 0 /*&& request.getRowCount().equals(1) */) {
				//Delete data
				TravelPassengerDetails oldPassData = 	traPassRepo.findByQuoteNoAndPassengerId(request.getQuoteNo() ,request.getVehicleId());
				traPassRepo.deleteByQuoteNoAndPassengerId(request.getQuoteNo(),request.getVehicleId());
				
				// Find History
				Long travelHisInfo =  traPassHisRepo.countByQuoteNoAndPassengerId(request.getQuoteNo() ,request.getVehicleId());
				if (travelHisInfo > 0 ) {
					//Delete data
					traPassHisRepo.deleteByQuoteNoAndPassengerId(request.getQuoteNo(),request.getVehicleId());
					
				}
				// Save New 
				TravelPassengerHistory traHistorySave = new TravelPassengerHistory(); 
				dozerMapper.map(oldPassData, traHistorySave);
				traHistorySave.setEntryDate(new Date());
				traPassHisRepo.saveAndFlush(traHistorySave);
				
			}
				
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,request.getGroupId());
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = defaultCovers.stream().filter( o -> o.getIsSelected()!=null &&   (! o.getIsSelected().equalsIgnoreCase("D")) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc()  ).sum();

			// Update Eservice Travel
			EserviceTravelDetails eserTravel = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo() );
						
			
			
			eserTravel.setActualPremiumFc(premiumFc);
			eserTravel.setActualPremiumLc(premiumLc);
			eserTravel.setOverallPremiumFc(overAllPremiumFc);
			eserTravel.setOverallPremiumLc(overAllPremiumLc);
			eserTravel.setQuoteNo(request.getQuoteNo());
			eserTravel.setCustomerId(request.getCustomerId());
			eserTraRepo.saveAndFlush(eserTravel);
			
			
			EserviceTravelGroupDetails groupData = eserGroupRepo.findByRequestReferenceNoAndGroupId(request.getRequestReferenceNo() , request.getGroupId());
			Double groupPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();					
			Double groupOverAllPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc()  ).sum();
			Double groupPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc()  ).sum();					
			Double groupOverAllPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc()  ).sum();
			groupData.setActualPremiumFc(groupPremiumFc);
			groupData.setActualPremiumLc(groupPremiumLc);
			groupData.setOverallPremiumFc(groupOverAllPremiumFc);
			groupData.setOverallPremiumLc(groupOverAllPremiumLc);
			groupData.setQuoteNo(request.getQuoteNo());
			groupData.setCustomerId(request.getCustomerId());
			eserGroupRepo.saveAndFlush(groupData);
			
			// Save Motro Details
			TravelPassengerDetails travelData  = new TravelPassengerDetails();
			dozerMapper.map(eserTravel, travelData);
			travelData.setEntryDate(new Date());	
			travelData.setCreatedBy(request.getCreatedBy());
			travelData.setQuoteNo(request.getQuoteNo());
			travelData.setTravelId(request.getVehicleId());
			travelData.setCustomerId(request.getCustomerId());
			travelData.setPassengerId( request.getVehicleId());
			travelData.setGroupId(request.getGroupId());
			travelData.setGroupCount(request.getGroupCount());
			travelData.setStatus("Y");
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( request.getGroupId())).collect(Collectors.toList());
			travelData.setVdRefno(filterCover.get(0).getVdRefno());	
			travelData.setMsRefno(filterCover.get(0).getMsRefno());		
			travelData.setCdRefno(filterCover.get(0).getCdRefno());	
			travelData.setActualPremiumFc(premiumFc);
			travelData.setActualPremiumLc(premiumLc);
			travelData.setOverallPremiumFc(overAllPremiumFc);
			travelData.setOverallPremiumLc(overAllPremiumLc);
			traPassRepo.saveAndFlush(travelData);
			log.error("Save Motor Info is ---> " + json.toJson(travelData));
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Details" ) ;
		}
	
		return res;
	}
	
	
	
	
	private synchronized  Map<String,Object>  call_CoverSave(QuoteThreadReq  request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		try {
			 //  // Delete Old Record
			Long coverInfo =  coverRepo.countByQuoteNoAndVehicleId(request.getQuoteNo(),request.getVehicleId() );
 			if (coverInfo >0 &&  request.getRowCount().equals(1) ) {
 				//Delete data
 				coverRepo.deleteByQuoteNoAndVehicleId(request.getQuoteNo(),request.getVehicleId() );
 				
 			}
						
			// Find Motor
			
			if( request.getProductId().equalsIgnoreCase(motorProductId)) {
				List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
				
				res = CoverSavePoint(covers);
				
			} else if( request.getProductId().equalsIgnoreCase(travelProductId)) {
				List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getGroupId());
				
				List<FactorRateRequestDetails>  devidedCovers = new ArrayList<FactorRateRequestDetails>();
				
				// Cover Amounts Devide By Group Count
				for ( FactorRateRequestDetails cover  : covers) {
					cover.setActualRate(cover.getActualRate()==null?null : getDevidedValue(  cover.getActualRate() ,request.getGroupCount()));
					cover.setMaxLodingAmount(cover.getMaxLodingAmount()==null?null : getDevidedValue(  cover.getMaxLodingAmount() ,request.getGroupCount()));
					cover.setMinimumPremium(cover.getMinimumPremium()==null?null : getDevidedValue(  cover.getMinimumPremium() ,request.getGroupCount()));
					cover.setPremiumAfterDiscountFc(cover.getPremiumAfterDiscountFc()==null?null : getDevidedValue(  cover.getPremiumAfterDiscountFc() ,request.getGroupCount()));
					cover.setPremiumAfterDiscountLc(cover.getPremiumAfterDiscountLc()==null?null : getDevidedValue(  cover.getPremiumAfterDiscountLc() ,request.getGroupCount()));
					cover.setPremiumBeforeDiscountFc(cover.getPremiumBeforeDiscountFc()==null?null : getDevidedValue(  cover.getPremiumBeforeDiscountFc() ,request.getGroupCount()));
					cover.setPremiumBeforeDiscountLc(cover.getPremiumBeforeDiscountLc()==null?null : getDevidedValue(  cover.getPremiumBeforeDiscountLc() ,request.getGroupCount()));
					cover.setPremiumExcludedTaxFc(cover.getPremiumExcludedTaxFc()==null?null : getDevidedValue(  cover.getPremiumExcludedTaxFc() ,request.getGroupCount()));
					cover.setPremiumExcludedTaxLc(cover.getPremiumExcludedTaxLc()==null?null : getDevidedValue(  cover.getPremiumExcludedTaxLc() ,request.getGroupCount()));
					cover.setPremiumIncludedTaxFc(cover.getPremiumIncludedTaxFc()==null?null : getDevidedValue(  cover.getPremiumIncludedTaxFc() ,request.getGroupCount()));
					cover.setPremiumIncludedTaxLc(cover.getPremiumIncludedTaxLc()==null?null : getDevidedValue(  cover.getPremiumIncludedTaxLc() ,request.getGroupCount()));
					cover.setRate(cover.getRate()==null?null : getDevidedValue(  cover.getRate() ,request.getGroupCount()));
					cover.setRegulSumInsured(cover.getRegulSumInsured()==null?null : getDevidedValue(  cover.getRegulSumInsured() ,request.getGroupCount()));
					cover.setSumInsured(cover.getSumInsured()==null?null : getDevidedValue(  cover.getSumInsured() ,request.getGroupCount()));
					cover.setTaxAmount(cover.getTaxAmount()==null?null : getDevidedValue(  cover.getTaxAmount() ,request.getGroupCount()));
					cover.setTaxRate(cover.getTaxRate()==null?null : getDevidedValue(  cover.getTaxRate() ,request.getGroupCount()));  
					devidedCovers.add(cover);
				}
				
				res = CoverSavePoint(devidedCovers);
			}
				
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Cover Details" ) ;
		}
	
		return res;
	}
		
	
	private synchronized Map<String,Object>  CoverSavePoint(List<FactorRateRequestDetails>  covers) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		try {
			// FindData 
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") ).collect(Collectors.toList() );
			
			// Insert Default Covers
			res = InsertCoverDetails(defaultCovers);
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails> updateCovers = new ArrayList<FactorRateRequestDetails>(); 
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null && (! o.getIsSelected().equalsIgnoreCase("D")) && o.getCoverId().equals(covReq.getCoverId())).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						res = InsertCoverDetails(filterNonDefaultCovers);
						
						List<FactorRateRequestDetails> 	updateCovers1 = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (o.getIsSelected().equalsIgnoreCase("N")) ).collect(Collectors.toList());
						updateCovers.addAll(updateCovers1);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> ! o.getIsSelected().equalsIgnoreCase("D") && o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId())) ).collect(Collectors.toList());
						res = InsertCoverDetails(filterNonDefaultSubCovers);
						List<FactorRateRequestDetails> 	updateCovers2 = filterNonDefaultSubCovers.stream().filter( o -> o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("N") ).collect(Collectors.toList());
						updateCovers.addAll(updateCovers2);
					}
				}
				
				// Update Factor Rate Details
				for (FactorRateRequestDetails fac  : updateCovers) {
					fac.setUserOpt("Y");
					facRateRepo.saveAndFlush(fac);
				}
			}
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Cover Details" ) ;
		}
	
		return res;
	}
	
	
	
	
	private synchronized Double getDevidedValue(Double inputValue ,Integer groupCount ) {
		Double devidedValue = 0D ;
		try {
			devidedValue = inputValue / groupCount ;
	
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return devidedValue;
	}

	
		private synchronized Map<String,Object>  InsertCoverDetails(List<FactorRateRequestDetails> covers) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Save Cover Details
				for ( FactorRateRequestDetails cov : covers) {
					PolicyCoverData coverData  = new PolicyCoverData();
					dozerMapper.map(cov, coverData);
					coverData.setEntryDate(new Date());	
					coverData.setQuoteNo(request.getQuoteNo());
					coverData.setIsSelected(cov.getIsSelected().equalsIgnoreCase("N") ? "Y" :cov.getIsSelected());
					coverData.setCreatedBy(request.getCreatedBy());
					coverData.setVehicleId(request.getVehicleId());
					coverRepo.saveAndFlush(coverData);	
					log.error("Save Cover Info is ---> " + json.toJson(coverData));
					
				}
		
				res.put("Response", "Success") ;
				res.put("Errors", null) ;
			}catch (Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
				res.put("Response", "Failed") ;
				res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Cover Details" ) ;
			}
		
			return res;
		}
		
	
	
	private synchronized QuoteThreadRes call_QuoteSave(QuoteThreadReq  request) {
		QuoteThreadRes res= new QuoteThreadRes() ;
		String pattern = "#####0.00";
		DecimalFormat df = new DecimalFormat(pattern);
		try {
			// Home Positiom Master Thread Call
			Long homeInfo =  homeRepo.countByQuoteNo(request.getQuoteNo());
			if (homeInfo > 0 ) {
				//Delete data
				homeRepo.deleteByQuoteNo(request.getQuoteNo());
 				
			}
			
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0);
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals( request.getGroupId()==null?request.getVehicleId() : request.getGroupId() ) ).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for (VehicleIdsReq vehReq : request.getVehicleIdsList() ) {
				for ( CoverIdsReq covReq :  coverReqList) { 
					List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) &&  o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
					
					if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							premiumCovers.addAll(filterNonDefaultCovers);
							
						}else {
							List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) && o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
							premiumCovers.addAll(filterNonDefaultSubCovers);
						}
					}
				}
			}
			
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc()  ).sum();
			Double vatPremiumFc = overAllPremiumFc - premiumFc ;  
			Double vatPercent = vatPremiumFc<=0D ?0 : (vatPremiumFc*100) / premiumFc ;
			Double vatPremiumLc = overAllPremiumLc - premiumLc ;  
			
			Double tax1 =  premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(1) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();
			Double tax2 = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(2) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();
			Double tax3 = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(3) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc()  ).sum();
			
			
			
			
			List<Integer> vehicleIds = request.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId ).collect(Collectors.toList());
			HomePositionMaster home = new HomePositionMaster();
			
			if( request.getProductId().equalsIgnoreCase(motorProductId) ) {
				
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
				home.setCompanyId(motorData.getCompanyId());
				home.setBranchCode(motorData.getBranchCode());
				home.setProductId(Integer.valueOf(motorData.getProductId()));
				home.setSectionId(Integer.valueOf(motorData.getSectionId()));
				home.setBrokerBranchCode(motorData.getBrokerBranchCode());	
				home.setLoginId(motorData.getLoginId());
				home.setApplicationId(motorData.getApplicationId());
				home.setAgencyCode(Integer.valueOf(motorData.getAgencyCode()));
				home.setAcExecutiveId(motorData.getAcExecutiveId()==null?null : Long.valueOf(motorData.getAcExecutiveId()));
				home.setBrokerCode(motorData.getBrokerCode());
				home.setEffectiveDate(motorData.getPolicyStartDate());
				home.setExpiryDate(motorData.getPolicyEndDate());
				home.setAdminRemarks(motorData.getAdminRemarks());
				home.setAdminReferralStatus(motorData.getStatus());			
				home.setReferralDescription(motorData.getReferalRemarks());
				home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? motorData.getAdminLoginId() : request.getAdminLoginId() );
				home.setStatus(motorData.getStatus());
				home.setQuoteCreatedDate(new Date());
				home.setEntryDate(new Date());
				home.setInceptionDate(motorData.getPolicyStartDate());
				home.setExpiryDate(motorData.getPolicyEndDate());
				home.setCurrency(motorData.getCurrency());
				home.setExchangeRate(motorData.getExchangeRate());
				home.setNoOfVehicles( request.getVehicleIdsList().size());
				
			} else if(request.getProductId().equalsIgnoreCase(travelProductId) ) {
				
				EserviceTravelDetails  travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo()) ;
				home.setCompanyId(travelData.getCompanyId());
				home.setBranchCode(travelData.getBranchCode());
				home.setProductId(Integer.valueOf(travelData.getProductId()));
				home.setSectionId(Integer.valueOf(travelData.getSectionId()));
				home.setBrokerBranchCode(travelData.getBrokerBranchCode());	
				home.setLoginId(travelData.getLoginId());
				home.setApplicationId(travelData.getApplicationId());
				home.setAgencyCode(Integer.valueOf(travelData.getBrokerCode()));
				home.setAcExecutiveId(travelData.getAcExecutiveId()==null?null : Long.valueOf(travelData.getAcExecutiveId()));
				home.setBrokerCode(travelData.getBrokerCode());
				home.setEffectiveDate(travelData.getTravelStartDate());
				home.setExpiryDate(travelData.getTravelEndDate());
				home.setAdminRemarks(travelData.getAdminRemarks());
				home.setAdminReferralStatus(travelData.getStatus());			
				home.setReferralDescription(travelData.getReferalRemarks());
				home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? travelData.getAdminLoginId() : request.getAdminLoginId() );
				home.setStatus(travelData.getStatus());
				home.setQuoteCreatedDate(new Date());
				home.setEntryDate(new Date());
				home.setInceptionDate(travelData.getTravelStartDate());
				home.setExpiryDate(travelData.getTravelEndDate());
				home.setCurrency(travelData.getCurrency());
				home.setExchangeRate(travelData.getExchangeRate());
				home.setNoOfVehicles( travelData.getTotalPassengers());
			}
			
			// Save Home Position Master
			
			home.setQuoteNo(request.getQuoteNo());
			home.setRequestReferenceNo(request.getRequestReferenceNo());
			home.setCustomerId(request.getCustomerId());
			//	home.setProposalNo("");
			home.setAmendId(0);
			home.setApplicationNo(0L);
			
			//home.setLapsedDate(null);
			//home.setLapsedRemarks(null);
			//home.setLapsedUpdatedBy(null);
			
	//		home.setRemarks("");
			home.setVehicleNo(vehicleIds.size());
			
			
			// No OF Vehicles
			
			home.setPremiumFc(Double.valueOf(df.format(premiumFc)) );
			home.setOverallPremiumFc(Double.valueOf(df.format(overAllPremiumFc)));
			home.setVatPremiumFc(Double.valueOf(df.format(vatPremiumFc)));
			home.setVatPercent(Double.valueOf(df.format(vatPercent)));
			home.setPremiumLc(Double.valueOf(df.format(premiumLc)) );
			home.setOverallPremiumLc(Double.valueOf(df.format(overAllPremiumLc)));
			home.setVatPremiumLc(Double.valueOf(df.format(vatPremiumLc)));
			home.setFinalizeYn("N");
			home.setTax1(tax1);
			home.setTax2(tax2);
			home.setTax3(tax3);
			
			homeRepo.saveAndFlush(home);
			
	/*		home.setExcessSign(null);
			home.setExcessPremium(null);
			home.setDiscountPremium(null);
			home.setPolicyFee(null);
			home.setOtherFee(null);
			home.setCommission(null);
			home.setCommissionPercentage(null);
			home.setVatCommission(nll);
			home.setCalcPremium(null);
			home.setAdminReferralStatus(null);
			home.setAdminReferralStatus(null);
			home.setReferralDescription(null);
			home.setApprovedBy(null);
			home.setApprCanBy(null); */
			
			
			log.error("Save Motor Info is ---> " + json.toJson(home));
			
			// Response 
			res.setCustomerId(request.getCustomerId());
			res.setQuoteNo(request.getQuoteNo());
			res.setRequestReferenceNo(request.getRequestReferenceNo());
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return res;
	}

	

	

	

	
}

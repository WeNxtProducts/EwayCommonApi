package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;

import com.google.gson.Gson;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPassengerHistory;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
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
	private MotorDriverDetailsRepository driverRepo;
	//Common
	
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
	
	// Building
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	//Common
	private EserviceCommonDetailsRepository eserCommonRepo;
	private CommonDataDetailsRepository commonDataRepo;
	
	// productId
	private String motorProductId;
	private String travelProductId;
	private String buildingProductId;
	private String personalAccidentProductId;
	
	public QuoteThreadCall(String type , QuoteThreadReq request , EntityManager em ,EserviceCustomerDetailsRepository eserCustRepo ,
			EServiceMotorDetailsRepository eserMotRepo  ,FactorRateRequestDetailsRepository facRateRepo  ,PersonalInfoRepository perInfoRepo  , MotorDataDetailsRepository motorRepo , MotorDriverDetailsRepository driverRepo ,
			 CoverDetailsRepository coverRepo  , HomePositionMasterRepository homeRepo  ,EserviceTravelDetailsRepository eserTraRepo ,EserviceTravelGroupDetailsRepository eserGroupRepo ,
			 TravelPassengerDetailsRepository    traPassRepo ,TravelPassengerHistoryRepository traPassHisRepo  , String motorProductId ,String travelProductId, String buildingProductId 
			 , EserviceBuildingDetailsRepository eserBuildRepo , EServiceSectionDetailsRepository eserSecRepo,EserviceCommonDetailsRepository eserCommonRepo,CommonDataDetailsRepository commonDataRepo) {
		this.type = type;
		this.request = request;
		this.em=em;
		this.eserCustRepo = eserCustRepo ;
		this.eserMotRepo = eserMotRepo ;
		this.facRateRepo = facRateRepo ;
		this.perInfoRepo = perInfoRepo ;
		this.motorRepo = motorRepo ;
		this.driverRepo = driverRepo  ;
		this.coverRepo = coverRepo ;
		this.homeRepo = homeRepo ;
		this.eserTraRepo = eserTraRepo ;
		this.eserGroupRepo = eserGroupRepo ;
		this.traPassRepo = traPassRepo ;
		this.motorProductId = motorProductId ;
		this.travelProductId = travelProductId ;
		this.traPassHisRepo = traPassHisRepo ;
		this.buildingProductId = buildingProductId ;
		this.eserBuildRepo = eserBuildRepo ;
		this.eserSecRepo = eserSecRepo ;
		this.eserCommonRepo=eserCommonRepo;
		this.commonDataRepo=commonDataRepo;
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

			} else if (type.equalsIgnoreCase("BuildingSave")) {

				map.put("BuildingSave", call_BuildingSave(request));

			}else if (type.equalsIgnoreCase("CommonDataSave")) {

				map.put("CommonDataSave", call_CommonDataSave(request));

			}else if (type.equalsIgnoreCase("CoverSave")) {

				map.put("CoverSave", call_CoverSave(request));

			} else if (type.equalsIgnoreCase("QuoteSave")) {

				map.put("QuoteSave", call_QuoteSave(request));

			} else if (type.equalsIgnoreCase("DeleteOldRecords")) {

				map.put("DeleteOldRecords", deleteOldQuoteRecords(request));

			}
			
			

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
	
	private Map<String,Object> call_CommonDataSave(QuoteThreadReq request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
		
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getVehicleId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->   (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumFc --> "  + premiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumLc --> "  + premiumLc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumLc --> "  + overAllPremiumLc );
			
			
			// Find Motor
			EserviceCommonDetails  eserCommonData = eserCommonRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() ,request.getVehicleId());
			String decimalDigits = currencyDecimalFormat(eserCommonData.getCompanyId() , eserCommonData.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			// Update Eservice Motor
			eserCommonData.setActualPremiumFc(new BigDecimal(df.format(premiumFc)));
			eserCommonData.setActualPremiumLc(new BigDecimal(df.format(premiumLc)));
			eserCommonData.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			eserCommonData.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			eserCommonData.setQuoteNo(request.getQuoteNo());
			eserCommonData.setCustomerId(request.getCustomerId());
			eserCommonRepo.saveAndFlush(eserCommonData);
			
			// Save Details
			CommonDataDetails commonData= new CommonDataDetails();
			//MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserCommonData, commonData);
			commonData.setEntryDate(new Date());	
			commonData.setCreatedBy(request.getCreatedBy());
			commonData.setQuoteNo(request.getQuoteNo());
			commonData.setCustomerId(request.getCustomerId());
			commonData.setRiskId(eserCommonData.getRiskId());
			commonData.setStatus("Y");
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( eserCommonData.getRiskId())).collect(Collectors.toList());
		//	commonData.setVdRefno(filterCover.get(0).getVdRefno());	
		//	commonData.setMsRefno(filterCover.get(0).getMsRefno());		
		//	commonData.setCdRefno(filterCover.get(0).getCdRefno());	
			commonData.setActualPremiumFc(BigDecimal.valueOf(premiumFc));
			commonData.setActualPremiumLc(BigDecimal.valueOf(premiumLc));
			commonData.setOverallPremiumFc(BigDecimal.valueOf(overAllPremiumFc));
			commonData.setOverallPremiumLc(BigDecimal.valueOf(overAllPremiumLc));
			commonDataRepo.saveAndFlush(commonData);
			log.error("Save Common Info is ---> " + json.toJson(commonData));
			
			// Update Eservice Motor
			
			
	
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Common Id : " + request.getVehicleId() + " Details" ) ;
		}
	
		return res;
	}

	private synchronized Map<String,Object> call_CustomerSave(QuoteThreadReq request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Long findInfo =  perInfoRepo.countByCustomerId(request.getCustomerId());
	
			if(findInfo > 0 ) {
				perInfoRepo.deleteByCustomerId(request.getCustomerId());	
			}
			

			// FindData 
			String customerRefNo = "" ;
			if(request.getProductId().equalsIgnoreCase(motorProductId) ) {
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
				customerRefNo = motorData.getCustomerReferenceNo();
			} else if(request.getProductId().equalsIgnoreCase(travelProductId) ) {
				EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
				customerRefNo = travelData.getCustomerReferenceNo();
			}else if(request.getProductId().equalsIgnoreCase(buildingProductId) ) {
				EserviceBuildingDetails buldingData = eserBuildRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),1 );
				customerRefNo = buldingData.getCustomerReferenceNo();
			}else {
				EserviceCommonDetails commonData = eserCommonRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
				customerRefNo = commonData.getCustomerReferenceNo();
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
			Long motorInfo =  motorRepo.countByQuoteNoAndVehicleId(request.getQuoteNo(),String.valueOf(request.getVehicleId()));
			if (motorInfo > 0  ) {
				motorRepo.deleteByQuoteNoAndVehicleId(request.getQuoteNo(), String.valueOf(request.getVehicleId()));
			}
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getVehicleId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->   (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumFc --> "  + premiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumLc --> "  + premiumLc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumLc --> "  + overAllPremiumLc );
			
			
			// Find Motor
			EserviceMotorDetails eserMotors = eserMotRepo.findByRequestReferenceNoAndRiskIdOrderByRiskIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
			String decimalDigits = currencyDecimalFormat(eserMotors.getCompanyId() , eserMotors.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			// Update Eservice Motor
			eserMotors.setActualPremiumFc(new BigDecimal(df.format(premiumFc)));
			eserMotors.setActualPremiumLc(new BigDecimal(df.format(premiumLc)));
			eserMotors.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			eserMotors.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
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
			motorData.setVehicleId(eserMotors.getRiskId().toString());
			motorData.setStatus("Y");
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( eserMotors.getRiskId())).collect(Collectors.toList());
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
			// Save Driver Details
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(eserMotors.getCustomerReferenceNo() );

			Long driverInfo = driverRepo.countByQuoteNoAndRiskId(request.getQuoteNo() , request.getVehicleId());
			if (driverInfo <= 0  ) {
				MotorDriverDetails saveDri = new MotorDriverDetails(); 		
				Integer driId = 1 ;
				saveDri.setCompanyId(motorData.getCompanyId());
				saveDri.setCreatedBy(motorData.getUpdatedBy());
				saveDri.setDriverDob(custData.getDobOrRegDate());
				saveDri.setDriverId(driId);
				saveDri.setDriverName(custData.getClientName());
				saveDri.setPolicyHolderType(custData.getPolicyHolderType());
				saveDri.setPolicyHolderTypeDesc(custData.getPolicyHolderTypeDesc());
				saveDri.setIdType(pattern);
				saveDri.setIdTypeDesc(pattern);
				saveDri.setIdNumber(custData.getIdNumber());
				saveDri.setDriverType("1");
				List<ListItemValue> owerDesc = getListItem(motorData.getCompanyId() , motorData.getBranchCode() , "DRIVER_TYPES" , "1" );
				saveDri.setDriverTypedesc(owerDesc.size()> 0 ? owerDesc.get(0).getItemValue() : "Owner" );
				saveDri.setEntryDate(new Date());
				saveDri.setProductId(motorData.getProductId());
				saveDri.setQuoteNo(motorData.getQuoteNo() );
				saveDri.setRequestReferenceNo(motorData.getRequestReferenceNo());
				saveDri.setRiskId(Integer.valueOf(motorData.getVehicleId()));
				saveDri.setStatus("Y");
				driverRepo.saveAndFlush(saveDri);
			}
			
	
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
	
	public synchronized List<ListItemValue> getListItem(String companyId ,String branchCode , String itemType , String itemCode) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			Predicate n11 = cb.equal(c.get("itemCode"),itemCode);
			query.where(n1,n2,n3,n8,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}
	
	public synchronized Integer currencyDecimalFormat(String insuranceId  ,String currencyId ) {
		Integer decimalFormat = 0 ;
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CurrencyMaster> query = cb.createQuery(CurrencyMaster.class);
			List<CurrencyMaster> list = new ArrayList<CurrencyMaster>();
			
			// Find All
			Root<CurrencyMaster>    c = query.from(CurrencyMaster.class);		
			
			// Select
			query.select(c);
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("currencyName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CurrencyMaster> ocpm1 = effectiveDate.from(CurrencyMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a11 = cb.equal(c.get("currencyId"),ocpm1.get("currencyId") );
			Predicate a12 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a18 = cb.equal(c.get("status"),ocpm1.get("status") );
			Predicate a22 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			
			effectiveDate.where(a11,a12,a18,a22);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CurrencyMaster> ocpm2 = effectiveDate2.from(CurrencyMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a13 = cb.equal(c.get("currencyId"),ocpm2.get("currencyId") );
			Predicate a14 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a19 = cb.equal(c.get("status"),ocpm2.get("status") );
			Predicate a23 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			
			effectiveDate2.where(a13,a14,a19,a23);
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"),"99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(c.get("currencyId"),currencyId);
			query.where(n1,n2,n3,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<CurrencyMaster> result = em.createQuery(query);			
			list =  result.getResultList(); 
			
			decimalFormat = list.size() > 0 ? (list.get(0).getDecimalDigit()==null?0 :list.get(0).getDecimalDigit()) :0; 		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return decimalFormat;
	}
	
	private synchronized  Map<String,Object>  call_BuildingSave(QuoteThreadReq  request  ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
	//	 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
	//		String SectionId = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId() ) ).collect(Collectors.toList()).get(0).getSectionId();
			
			// Cover Calc
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getGroupId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));

			
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null &&   (! o.getIsSelected().equalsIgnoreCase("D")) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			
			
			// Find Building
			EserviceBuildingDetails eserBuild = eserBuildRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() ,request.getVehicleId());
			String decimalDigits = currencyDecimalFormat(eserBuild.getCompanyId() , eserBuild.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			// Update Eservice Building
			eserBuild.setActualPremiumFc(new BigDecimal(df.format(premiumFc)));
			eserBuild.setActualPremiumLc(new BigDecimal(df.format(premiumLc)));
			eserBuild.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			eserBuild.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			eserBuild.setQuoteNo(request.getQuoteNo());
			eserBuild.setCustomerId(request.getCustomerId());
			eserBuildRepo.saveAndFlush(eserBuild);
		
	
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
			
			List<FactorRateRequestDetails>  covers = new ArrayList<FactorRateRequestDetails>();
			
			covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getGroupId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));		
			
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			premiumCovers.addAll(defaultCovers);
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null &&   (! o.getIsSelected().equalsIgnoreCase("D")) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();

			// Update Eservice Travel
			EserviceTravelDetails eserTravel = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo() );
			String decimalDigits = currencyDecimalFormat(eserTravel.getCompanyId() , eserTravel.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			eserTravel.setActualPremiumFc(new BigDecimal(df.format(premiumFc)));
			eserTravel.setActualPremiumLc(new BigDecimal(df.format(premiumLc)));
			eserTravel.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			eserTravel.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			eserTravel.setQuoteNo(request.getQuoteNo());
			eserTravel.setCustomerId(request.getCustomerId());
			eserTraRepo.saveAndFlush(eserTravel);
			
			
			EserviceTravelGroupDetails groupData = eserGroupRepo.findByRequestReferenceNoAndGroupId(request.getRequestReferenceNo() , request.getGroupId());
			Double groupPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double groupOverAllPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			Double groupPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double groupOverAllPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			groupData.setActualPremiumFc(new BigDecimal(df.format(groupPremiumFc)));
			groupData.setActualPremiumLc(new BigDecimal(df.format(groupPremiumLc)));
			groupData.setOverallPremiumFc(new BigDecimal(df.format(groupOverAllPremiumFc)));
			groupData.setOverallPremiumLc(new BigDecimal(df.format(groupOverAllPremiumLc)));
			groupData.setQuoteNo(request.getQuoteNo());
			groupData.setCustomerId(request.getCustomerId());
			eserGroupRepo.saveAndFlush(groupData);
			
			// Save Motro Details
			TravelPassengerDetails travelData  = new TravelPassengerDetails();
			dozerMapper.map(eserTravel, travelData);
			travelData.setTravelId(eserTravel.getRiskId());
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
			if( request.getProductId().equalsIgnoreCase(travelProductId)) {
				List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()) , request.getGroupId());
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
				
			} else {
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()) , request.getVehicleId());
				res = CoverSavePoint(covers);
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
			
			List<VehicleIdsReq> VehicleList = new ArrayList<VehicleIdsReq>();
			List<CoverIdsReq> coverReqList =new ArrayList<CoverIdsReq>();
			
			// Insert Other Covers
			if ( request.getProductId().equalsIgnoreCase(motorProductId)) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if( request.getProductId().equalsIgnoreCase(buildingProductId)    ) {
				
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())   &&  o.getSectionId().equalsIgnoreCase(request.getSectionId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			
			} else if( request.getProductId().equalsIgnoreCase(travelProductId)) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			} else   {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			}
			
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
	
	
	
	
	private synchronized BigDecimal getDevidedValue(BigDecimal inputValue ,Integer groupCount ) {
		BigDecimal devidedValue = BigDecimal.ZERO ;
		try {
			devidedValue = inputValue.divide(new BigDecimal(groupCount)) ;
	
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
		
		public synchronized Map<String,Object>  deleteOldQuoteRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				if( req.getProductId().equalsIgnoreCase(motorProductId) ) {
					
					Long motorInfo =  motorRepo.countByQuoteNo(req.getQuoteNo());
					if (motorInfo > 0  ) {
						motorRepo.deleteByQuoteNo(req.getQuoteNo());
					}
					
					
				} else if( req.getProductId().equalsIgnoreCase(travelProductId) ) {
					// Delete Old Record
					Long travelInfo =  traPassRepo.countByQuoteNo(req.getQuoteNo());
					if (travelInfo > 0  ) {
						//Delete data
						List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getQuoteNo());
						traPassRepo.deleteByQuoteNo(req.getQuoteNo());
							
						// Find History
						for (TravelPassengerDetails passData :  oldPassDatas) {
							Long travelHisInfo =  traPassHisRepo.countByQuoteNoAndPassengerId(req.getQuoteNo() ,passData.getPassengerId());
							if (travelHisInfo > 0 ) {
								//Delete data
								traPassHisRepo.deleteByQuoteNoAndPassengerId(req.getQuoteNo(),passData.getPassengerId());
								
							}
							// Save New 
							TravelPassengerHistory traHistorySave = new TravelPassengerHistory(); 
							dozerMapper.map(passData, traHistorySave);
							traHistorySave.setEntryDate(new Date());
							traPassHisRepo.saveAndFlush(traHistorySave);
						}
						
					
					}	
				} else  {
					
					Long commonInfo =  commonDataRepo.countByQuoteNo(req.getQuoteNo());
					if (commonInfo > 0  ) {
						commonDataRepo.deleteByQuoteNo(req.getQuoteNo());
					}
					
				}
				
				// Remove Covers
				Long coverInfo =  coverRepo.countByQuoteNo(req.getQuoteNo());
	 			if (coverInfo >0 ) {
	 				//Delete data
	 				coverRepo.deleteByQuoteNo(req.getQuoteNo() );
	 				
	 			}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
				res.put("Response", "Failed") ;
				res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Cover Details" ) ;
			}
			return res;
		}
	
	private synchronized QuoteThreadRes call_QuoteSave(QuoteThreadReq  request) {
		QuoteThreadRes res= new QuoteThreadRes() ;
		try {
			// Home Positiom Master Thread Call
			Long homeInfo =  homeRepo.countByQuoteNo(request.getQuoteNo());
			if (homeInfo > 0 ) {
				//Delete data
				homeRepo.deleteByQuoteNo(request.getQuoteNo());
 				
			}
			
			// Cover Calc
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
			
			List<PolicyCoverData>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList() );
			
			List<PolicyCoverData>  premiumCovers = new  ArrayList<PolicyCoverData>();
			premiumCovers.addAll(defaultCovers);
			
			for (VehicleIdsReq vehReq : request.getVehicleIdsList() ) {
				List<CoverIdsReq> coverReqList = vehReq.getCoverIdList();
				for ( CoverIdsReq covReq :  coverReqList) { 
					List<PolicyCoverData> filterNonDefaultCovers  = new ArrayList<PolicyCoverData>();
					
					 if( request.getProductId().equalsIgnoreCase(buildingProductId)    ) {
						 
						 filterNonDefaultCovers = covers.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(vehReq.getSectionId())) && o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) &&  o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
					
					} else  {
						
						 filterNonDefaultCovers = covers.stream().filter( o ->  o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) &&  o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
						
					}
					
					if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							premiumCovers.addAll(filterNonDefaultCovers);
							
						}else {
							List<PolicyCoverData> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) && o.getIsSelected()!=null &&  (! o.getIsSelected().equalsIgnoreCase("D")) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0)  && o.getTaxId().equals(0) ).collect(Collectors.toList());
							premiumCovers.addAll(filterNonDefaultSubCovers);
						}
					}
				}
			}
			
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
			
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()   ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum();
			Double vatPremiumFc = overAllPremiumFc - premiumFc ;  
			Double vatPercent = vatPremiumFc<=0D ?0 : (vatPremiumFc*100) / premiumFc ;
			Double vatPremiumLc = overAllPremiumLc - premiumLc ;  
			System.out.println("Home Position PremiumFc --> "  + premiumFc );
			System.out.println("Home Position OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Home Position PremiumLc --> "  + premiumLc );
			System.out.println("Home Position OverAllPremiumLc --> "  + overAllPremiumLc );
			Double tax1 =  premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(1) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			Double tax2 = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(2) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			Double tax3 = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(3) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			
			List<Integer> vehicleIds = request.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId ).collect(Collectors.toList());
			HomePositionMaster home = new HomePositionMaster();
			
			if( request.getProductId().equalsIgnoreCase(motorProductId) ) {
				
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskIdOrderByRiskIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
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
				home.setHavepromoYn(motorData.getHavepromocode());
				home.setPromocode(motorData.getPromocode());
				home.setManualReferalYn(motorData.getManualReferalYn());
				
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
				home.setHavepromoYn(travelData.getHavepromocode());
				home.setPromocode(travelData.getPromocode());
				home.setManualReferalYn(travelData.getManualReferalYn());
				
			}  else if(request.getProductId().equalsIgnoreCase(buildingProductId) ) {
				
				EserviceBuildingDetails  buildingData = eserBuildRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() , request.getVehicleId()) ;
				Long builCount =  eserBuildRepo.countByRequestReferenceNo(request.getRequestReferenceNo() ) ;
				//List<EserviceSectionDetails> sections = eserSecRepo.findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(request.getRequestReferenceNo() , request.getVehicleId(),request.getProductId() );
				home.setCompanyId(buildingData.getCompanyId());
				home.setBranchCode(buildingData.getBranchCode());
				home.setProductId(Integer.valueOf(buildingData.getProductId()));
				home.setSectionId(Integer.valueOf(0));
				home.setBrokerBranchCode(buildingData.getBrokerBranchCode());	
				home.setLoginId(buildingData.getLoginId());
				home.setApplicationId(buildingData.getApplicationId());
				home.setAgencyCode(Integer.valueOf(buildingData.getBrokerCode()));
				home.setAcExecutiveId(buildingData.getAcExecutiveId()==null?null : Long.valueOf(buildingData.getAcExecutiveId()));
				home.setBrokerCode(buildingData.getBrokerCode());
				home.setEffectiveDate(buildingData.getPolicyStartDate());
				home.setExpiryDate(buildingData.getPolicyEndDate());
				home.setAdminRemarks(buildingData.getAdminRemarks());
				home.setAdminReferralStatus(buildingData.getStatus());			
				home.setReferralDescription(buildingData.getReferalRemarks());
				home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? buildingData.getAdminLoginId() : request.getAdminLoginId() );
				home.setStatus(buildingData.getStatus());
				home.setQuoteCreatedDate(new Date());
				home.setEntryDate(new Date());
				home.setInceptionDate(buildingData.getPolicyStartDate());
				home.setExpiryDate(buildingData.getPolicyEndDate());
				home.setCurrency(buildingData.getCurrency());
				home.setExchangeRate(buildingData.getExchangeRate());
				home.setNoOfVehicles(Integer.valueOf(builCount.toString()));
				home.setHavepromoYn(buildingData.getHavepromocode());
				home.setPromocode(buildingData.getPromocode());
				home.setManualReferalYn(buildingData.getManualReferalYn());
				
			}   else  {
				
				EserviceCommonDetails  eserCommonData = eserCommonRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() , request.getVehicleId()) ;
				Long commonCount =  eserCommonRepo.countByRequestReferenceNo(request.getRequestReferenceNo() ) ;
				//List<EserviceSectionDetails> sections = eserSecRepo.findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(request.getRequestReferenceNo() , request.getVehicleId(),request.getProductId() );
				home.setCompanyId(eserCommonData.getCompanyId());
				home.setCustomerName(eserCommonData.getCustomerName() );
				home.setBranchCode(eserCommonData.getBranchCode());
				home.setProductId(Integer.valueOf(eserCommonData.getProductId()));
				home.setSectionId(Integer.valueOf(0));
				home.setBrokerBranchCode(eserCommonData.getBrokerBranchCode());	
				home.setLoginId(eserCommonData.getLoginId());
				home.setApplicationId(eserCommonData.getApplicationId());
				home.setAgencyCode(Integer.valueOf(eserCommonData.getBrokerCode()));
				home.setAcExecutiveId(eserCommonData.getAcExecutiveId()==null?null : Long.valueOf(eserCommonData.getAcExecutiveId()));
				home.setBrokerCode(eserCommonData.getBrokerCode());
				home.setEffectiveDate(eserCommonData.getPolicyStartDate());
				home.setExpiryDate(eserCommonData.getPolicyEndDate());
				home.setAdminRemarks(eserCommonData.getAdminRemarks());
				home.setAdminReferralStatus(eserCommonData.getStatus());			
				home.setReferralDescription(eserCommonData.getReferalRemarks());
				home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? eserCommonData.getAdminLoginId() : request.getAdminLoginId() );
				home.setStatus(eserCommonData.getStatus());
				home.setQuoteCreatedDate(new Date());
				home.setEntryDate(new Date());
				home.setInceptionDate(eserCommonData.getPolicyStartDate());
				home.setExpiryDate(eserCommonData.getPolicyEndDate());
				home.setCurrency(eserCommonData.getCurrency());
				home.setExchangeRate(eserCommonData.getExchangeRate());
				home.setNoOfVehicles(Integer.valueOf(commonCount.toString()));
				home.setHavepromoYn(eserCommonData.getHavepromocode());
				home.setPromocode(eserCommonData.getPromocode());
				home.setManualReferalYn(eserCommonData.getManualReferalYn());
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
			String decimalDigits = currencyDecimalFormat(home.getCompanyId() , home.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			home.setPremiumFc(new BigDecimal(df.format(premiumFc)) );
			home.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			home.setVatPremiumFc(new BigDecimal(df.format(vatPremiumFc)));
			home.setVatPercent(new BigDecimal(df.format(vatPercent)));
			home.setPremiumLc(new BigDecimal(df.format(premiumLc)) );
			home.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			home.setVatPremiumLc(new BigDecimal(df.format(vatPremiumLc)));
			home.setFinalizeYn("N");
			home.setTax1(new BigDecimal(df.format(tax1)));
			home.setTax2(new BigDecimal(df.format(tax2)));
			home.setTax3(new BigDecimal(df.format(tax3)));
			
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

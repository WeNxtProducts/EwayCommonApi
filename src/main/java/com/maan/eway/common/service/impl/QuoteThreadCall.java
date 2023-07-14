package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.FrameOldDocSaveReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteThreadRes;
import com.maan.eway.error.Error;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
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
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.res.SuccessRes;


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
	private BuildingRiskDetailsRepository buildRepo  ;
	private EServiceSectionDetailsRepository eserSecRepo  ;
	private SectionDataDetailsRepository secRepo ;
	private BuildingDetailsRepository locRepo ;
	private ContentAndRiskRepository  contentRepo ;
	private ProductEmployeesDetailsRepository pacRepo ;   
	
	//Common
	private EserviceCommonDetailsRepository eserCommonRepo;
	private CommonDataDetailsRepository commonDataRepo;
	private DocumentTransactionDetailsRepository docRepo ;
	
	// productId
	private String travelProductId;
	
	// Document
	private DocumentUniqueDetailsRepository docUniqueRepo ;
	private DocumentTransactionDetailsRepository docTranRepo ;

	
	public QuoteThreadCall(String type , QuoteThreadReq request , EntityManager em ,EserviceCustomerDetailsRepository eserCustRepo ,
			EServiceMotorDetailsRepository eserMotRepo  ,FactorRateRequestDetailsRepository facRateRepo  ,PersonalInfoRepository perInfoRepo  , MotorDataDetailsRepository motorRepo , MotorDriverDetailsRepository driverRepo ,
			 CoverDetailsRepository coverRepo  , HomePositionMasterRepository homeRepo  ,EserviceTravelDetailsRepository eserTraRepo ,EserviceTravelGroupDetailsRepository eserGroupRepo ,
			 TravelPassengerDetailsRepository    traPassRepo ,TravelPassengerHistoryRepository traPassHisRepo  ,String travelProductId
			 , EserviceBuildingDetailsRepository eserBuildRepo , EServiceSectionDetailsRepository eserSecRepo,EserviceCommonDetailsRepository eserCommonRepo,CommonDataDetailsRepository commonDataRepo ,
			 SectionDataDetailsRepository secRepo,BuildingRiskDetailsRepository buildRepo , DocumentTransactionDetailsRepository docRepo, BuildingDetailsRepository locRepo ,ContentAndRiskRepository  contentRepo  ,ProductEmployeesDetailsRepository pacRepo
			 , DocumentUniqueDetailsRepository docUniqueRepo ,DocumentTransactionDetailsRepository docTranRepo   ) {
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
		this.travelProductId = travelProductId ;
		this.traPassHisRepo = traPassHisRepo ;
		this.eserBuildRepo = eserBuildRepo ;
		this.eserSecRepo = eserSecRepo ;
		this.eserCommonRepo=eserCommonRepo;
		this.commonDataRepo=commonDataRepo;
		this.secRepo = secRepo ;
		this.buildRepo = buildRepo ;
		this.docRepo = docRepo ;
		this.locRepo = locRepo ;
		this.contentRepo = contentRepo ;
		this.pacRepo = pacRepo ;
		this.docUniqueRepo = docUniqueRepo ;
		this.docTranRepo = docTranRepo ;
		
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

			} else if (type.equalsIgnoreCase("SectionSave")) {

				map.put("SectionSave", call_SectionSave(request));

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
			
			
			
			
			// Save Details
			CommonDataDetails commonData= new CommonDataDetails();
			//MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserCommonData, commonData);
			commonData.setEntryDate(new Date());	
			commonData.setCreatedBy(request.getCreatedBy());
			commonData.setQuoteNo(request.getQuoteNo());
			commonData.setCustomerId(request.getCustomerId());
			commonData.setRiskId(eserCommonData.getRiskId());
			commonData.setStatus(eserCommonData.getStatus());
			commonData.setSectionDesc(eserCommonData.getSectionName());		
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( eserCommonData.getRiskId())).collect(Collectors.toList());
		//	commonData.setVdRefno(filterCover.get(0).getVdRefno());	
		//	commonData.setMsRefno(filterCover.get(0).getMsRefno());		
		//	commonData.setCdRefno(filterCover.get(0).getCdRefno());	
			commonData.setActualPremiumFc(BigDecimal.valueOf(premiumFc));
			commonData.setActualPremiumLc(BigDecimal.valueOf(premiumLc));
			commonData.setOverallPremiumFc(BigDecimal.valueOf(overAllPremiumFc));
			commonData.setOverallPremiumLc(BigDecimal.valueOf(overAllPremiumLc)); 
			
			BigDecimal endtPremium=null;
			if(eserCommonData.getEndorsementType()!=null) {
				String prevQuoteNo=eserCommonData.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				endtPremium = updateEndtPremium(request.getQuoteNo(),eserCommonData.getEndorsementEffdate(),prevQuoteNo, eserCommonData.getRiskId(),Endtcovers);				
				eserCommonData.setEndtPremium(endtPremium.doubleValue());
				commonData.setEndtPremium(endtPremium.doubleValue());
			} 
			
			eserCommonRepo.saveAndFlush(eserCommonData);
			commonDataRepo.saveAndFlush(commonData);
			log.error("Save Common Info is ---> " + json.toJson(commonData));
			
			// Update Eservice Motor
			
			
	
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			// Copy Old Quote Additional Details
			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
				
		//		res =  copyQuoteDocumentDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Common Id : " + request.getVehicleId() + " Details" ) ;
		}
	
		return res;
	}
	
	private BigDecimal updateEndtPremium(String quoteNo,Date effDate,String prevQuoteNo,Integer riskId, List<PolicyCoverData> covers) {
		try {
			List<PolicyCoverData> newCovers=null;
			List<PolicyCoverData>  totalcovers =null;
			 List<PolicyCoverData>  oldcovers =null; 
			 
			 if(riskId.intValue()==0) {
				 newCovers=covers;
				 totalcovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				 oldcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(prevQuoteNo ,0, 0 ,"D");
			 }else {
				 newCovers=covers.stream().filter(i -> i.getVehicleId().doubleValue()==riskId.doubleValue()).collect(Collectors.toList());
				 totalcovers = coverRepo.findByQuoteNoAndVehicleIdOrderByVehicleIdAsc(quoteNo,riskId);
				 oldcovers = coverRepo.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(prevQuoteNo ,riskId,0, 0 ,"D");
			 }
			
			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxLc()!=null 
					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType()) 
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum());			 
			 Double endtChangePremium=totalcovers.stream().filter( o ->   
					   o.getPremiumIncludedTaxLc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum();
			 
			 List<PolicyCoverData>  oldcoversf=oldcovers;
			 newCovers.removeIf(p-> {
				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			 });
			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null 
					 && !"D".equals(o.getStatus())
					 && effDate.compareTo(o.getCoverPeriodFrom())>=0
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum();
				BigDecimal endtPremium= new  BigDecimal(removedCoverPremium+addedCoverPremium+endtChangePremium);
			String endtChargeOrRefund="REFUND";
			if(endtPremium.doubleValue()>=0) {
				endtChargeOrRefund="CHARGE";
			}		
			
			return endtPremium;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return BigDecimal.ZERO;
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
			if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId) ) {
				EserviceTravelDetails travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
				customerRefNo = travelData.getCustomerReferenceNo();
				
			} else if(request.getMotorYn().equalsIgnoreCase("M")) {
				EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo(),request.getVehicleIdsList().get(0).getVehicleId());
				customerRefNo = motorData.getCustomerReferenceNo();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A")) {
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
			
			
			
			
			// Save Motro Details
			MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserMotors, motorData);
			motorData.setEntryDate(new Date());	
			motorData.setCreatedBy(request.getCreatedBy());
			motorData.setQuoteNo(request.getQuoteNo());
			motorData.setCustomerId(request.getCustomerId());
			motorData.setVehicleId(eserMotors.getRiskId().toString());
			motorData.setStatus(eserMotors.getStatus());
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( eserMotors.getRiskId())).collect(Collectors.toList());
			motorData.setVdRefno(filterCover.get(0).getVdRefno());	
			motorData.setMsRefno(filterCover.get(0).getMsRefno());		
			motorData.setCdRefno(filterCover.get(0).getCdRefno());	
			motorData.setActualPremiumFc(premiumFc);
			motorData.setActualPremiumLc(premiumLc);
			motorData.setOverallPremiumFc(overAllPremiumFc);
			motorData.setOverallPremiumLc(overAllPremiumLc);
			
			//Vehiclewise EndtPRemium
			if(eserMotors.getEndorsementType()!=null) {
				String prevQuoteNo=eserMotors.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				BigDecimal endtPremium = updateEndtPremium(request.getQuoteNo(),eserMotors.getEndorsementEffdate(),prevQuoteNo, eserMotors.getRiskId(),Endtcovers);				
				eserMotors.setEndtPremium(endtPremium.doubleValue());
				motorData.setEndtPremium(endtPremium.doubleValue());
			}   
			eserMotRepo.saveAndFlush(eserMotors);			
			motorRepo.saveAndFlush(motorData);
			log.error("Save Motor Info is ---> " + json.toJson(motorData));
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			// Update Eservice Motor
			// Save Driver Details
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(eserMotors.getCustomerReferenceNo() );

			// Copy Old Quote Additional Details
			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
				
				res =   copyQuoteDriverDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()  );
				
			//	res =  copyQuoteDocumentDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
				
			} else {
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
			}
			
			
	
			
			
			
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
	
	
	public synchronized LoginBranchMaster getBranchDetails(String companyId ,String brokerBranchCode , String loginId ) {
		LoginBranchMaster brokerBranch = new LoginBranchMaster();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginBranchMaster> query=  cb.createQuery(LoginBranchMaster.class);
			// Find All
			Root<LoginBranchMaster> c = query.from(LoginBranchMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("entryDate")));
			
			
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("companyId"),companyId);
			Predicate n3 = cb.equal(c.get("brokerBranchCode"),brokerBranchCode);	
			Predicate n4 = cb.equal(c.get("loginId"),loginId);
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
			// Get Result
			TypedQuery<LoginBranchMaster> result = em.createQuery(query);
			List<LoginBranchMaster> list = result.getResultList();
			brokerBranch = list.size() > 0 ? list.get(0) : null ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return brokerBranch ;
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
	
	
	
	private synchronized  Map<String,Object>  copyQuoteDriverDetails(QuoteThreadReq  request , String oldQuoteNo , String newQuoteNo ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
	 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			
			// Motor Driver Details 
			List<MotorDriverDetails>   oldDriDetails = driverRepo.findByQuoteNoAndRiskId( oldQuoteNo ,  request.getVehicleId());
			
			Long driverInfo = driverRepo.countByQuoteNoAndRiskId(request.getQuoteNo() , request.getVehicleId());
			if( driverInfo <= 0  ) {
				if(oldDriDetails.size() > 0 ) {
					for ( MotorDriverDetails dri : oldDriDetails ) {
						MotorDriverDetails saveDri = new MotorDriverDetails(); 		
						dozerMapper.map(dri , saveDri);
						saveDri.setQuoteNo(request.getQuoteNo() );
						saveDri.setRequestReferenceNo(request.getRequestReferenceNo());
						driverRepo.saveAndFlush(saveDri);
					}
				}
				
			}
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Copy Vehicle Id : " + request.getVehicleId() + " Driver Details" ) ;
		}
	
		return res;
	}
	
//	private synchronized  Map<String,Object>  copyQuoteDocumentDetails(QuoteThreadReq  request , String oldQuoteNo , String newQuoteNo ) {
//		Map<String,Object> res= new HashMap<String,Object>() ;
//	 DozerBeanMapper dozerMapper = new DozerBeanMapper();
//		try {
//			
//			{
//				List<Integer> ids = new ArrayList<Integer>();
//				ids.add( request.getVehicleId());
//				
//				Long docInfo = docRepo.countByQuoteNoAndProductIdAndSectionId(request.getQuoteNo() , Integer.valueOf(request.getProductId()) , Integer.valueOf(request.getSectionId())  );
//				if( docInfo <= 0  ) {
//					// Other Doc
//					List<DocumentTransactionDetails>   oldDocDetails = docRepo.findByQuoteNoAndProductIdAndSectionId( oldQuoteNo , request.getVehicleId() ,
//							Integer.valueOf(request.getProductId()) , Integer.valueOf(request.getSectionId())   ) ; 
//					List<DocumentTransactionDetails> saveDocList = new ArrayList<DocumentTransactionDetails>(); 
//					if( oldDocDetails.size() > 0  ) {
//					
//						for ( DocumentTransactionDetails doc : oldDocDetails ) {
//							DocumentTransactionDetails saveDoc = new DocumentTransactionDetails(); 		
//							dozerMapper.map(doc , saveDoc);
//							saveDoc.setQuoteNo(request.getQuoteNo() );
//							saveDoc.setRequestReferenceNo(request.getRequestReferenceNo());
//							saveDocList.add(saveDoc) ;
//						}
//						docRepo.saveAllAndFlush(saveDocList);
//					}
//				}
//				
//			
//			}
//			
//			res.put("Response", "Success") ;
//			res.put("Errors", null) ;
//			
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//			log.error("Exception is ---> " + e.getMessage());
//			res.put("Response", "Failed") ;
//			res.put("Errors", "Failed To Copy Vehicle Id : " + request.getVehicleId() + " Document Details" ) ;
//		}
//	
//		return res;
//	}
	
	private synchronized  Map<String,Object>  copyQuoteLocationDetails(QuoteThreadReq  request , String oldQuoteNo , String newQuoteNo ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
	 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			
			// Locations
			Long locCount = locRepo.countByQuoteNo(newQuoteNo );
				
			if( locCount <= 0  ) {
				
				List<BuildingDetails> oldLocDetails = locRepo.findByQuoteNoOrderByRiskIdAsc(oldQuoteNo );
				List<BuildingDetails> saveLocList = new ArrayList<BuildingDetails>(); 
				if( oldLocDetails.size() > 0  ) {
					for ( BuildingDetails loc : oldLocDetails ) {
						BuildingDetails saveDoc = new BuildingDetails(); 		
						dozerMapper.map(loc , saveDoc);
						saveDoc.setQuoteNo(request.getQuoteNo() );
						saveDoc.setRequestReferenceNo(request.getRequestReferenceNo());
						saveLocList.add(saveDoc) ;
					}
					locRepo.saveAllAndFlush(saveLocList);
				}
			}
			
					
			// COntent And All Risk	
			Long contentCount = contentRepo.countByQuoteNo(newQuoteNo );
			
			if( contentCount <= 0  ) {
				
				List<ContentAndRisk> oldContentDetails = contentRepo.findByQuoteNoOrderByRiskIdAsc(oldQuoteNo );
				List<ContentAndRisk> saveConList = new ArrayList<ContentAndRisk>(); 
				if( oldContentDetails.size() > 0  ) {
					for ( ContentAndRisk con : oldContentDetails ) {
						ContentAndRisk saveCon = new ContentAndRisk(); 		
						dozerMapper.map(con , saveCon);
						saveCon.setQuoteNo(request.getQuoteNo() );
						saveCon.setRequestReferenceNo(request.getRequestReferenceNo());
						saveConList.add(saveCon) ;
					}
					contentRepo.saveAllAndFlush(saveConList);
				}
			}
			
			
			// Personal Accident
			Long pacCount = pacRepo.countByQuoteNo(newQuoteNo );
			
			if( pacCount <= 0  ) {
				
				List<ProductEmployeeDetails> oldPacDetails = pacRepo.findByQuoteNo(oldQuoteNo );
				List<ProductEmployeeDetails> savePacList = new ArrayList<ProductEmployeeDetails>(); 
				
				if( pacCount <= 0  ) {
					for ( ProductEmployeeDetails pac : oldPacDetails ) {
						ProductEmployeeDetails savePac = new ProductEmployeeDetails(); 		
						dozerMapper.map(pac , savePac);
						savePac.setQuoteNo(request.getQuoteNo() );
						savePac.setRequestReferenceNo(request.getRequestReferenceNo());
						savePacList.add(savePac) ;
					}
					pacRepo.saveAllAndFlush(savePacList);
				}
			}
		
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Copy Vehicle Id : " + request.getVehicleId() + " Document Details" ) ;
		}
	
		return res;
	}
	
	
	private synchronized  Map<String,Object>  call_BuildingSave(QuoteThreadReq  request  ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
	 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
	//		String SectionId = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId() ) ).collect(Collectors.toList()).get(0).getSectionId();
			
			// Cover Calc
		//	List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdNotOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getGroupId() ,Integer.valueOf(request.getProductId()) ,35);
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,"Y" );
			
			List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.size() > 0 ? VehicleList.get(0).getCoverIdList() : request.getVehicleIdsList().get(0).getCoverIdList() ; 		
			
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
			EserviceBuildingDetails eserBuild = eserBuildRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() ,1);
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
			
		
			
			BuildingRiskDetails bulildDetails = new BuildingRiskDetails();
			dozerMapper.map(eserBuild,bulildDetails);
			bulildDetails.setQuoteNo(request.getQuoteNo());
			bulildDetails.setUpdatedDate(new Date());;
		
			BigDecimal endtPremium = null;
			if(eserBuild.getEndorsementType()!=null) {
				String prevQuoteNo=eserBuild.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				endtPremium = updateEndtPremium(request.getQuoteNo(),eserBuild.getEndorsementEffdate(),prevQuoteNo, eserBuild.getRiskId(),Endtcovers);				
				eserBuild.setEndtPremium(endtPremium.doubleValue());
				bulildDetails.setEndtPremium(endtPremium.doubleValue());
			}   
			eserBuildRepo.saveAndFlush(eserBuild);
			buildRepo.saveAndFlush(bulildDetails);
			
			// Pacc
			List<EserviceCommonDetails> findPacc = eserCommonRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
			findPacc.forEach( o -> o.setQuoteNo(request.getQuoteNo())  );
		 	
			List<CommonDataDetails> savePacList = new ArrayList<CommonDataDetails>();
			for(EserviceCommonDetails pac : findPacc ) {
				CommonDataDetails savePac = new CommonDataDetails();
				dozerMapper.map(pac,savePac);
				savePac.setQuoteNo(request.getQuoteNo());
				savePac.setUpdatedDate(new Date());
				savePac.setSectionDesc(pac.getSectionName());	
				savePacList.add(savePac);
				
				if(eserBuild.getEndorsementType()!=null) {
					String prevQuoteNo=eserBuild.getEndtPrevQuoteNo();
					List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
					endtPremium = updateEndtPremium(request.getQuoteNo(),eserBuild.getEndorsementEffdate(),prevQuoteNo, eserBuild.getRiskId(),Endtcovers);				
					pac.setEndtPremium(endtPremium.doubleValue());
					savePac.setEndtPremium(endtPremium.doubleValue());
				} 
				
			}
			eserCommonRepo.saveAllAndFlush(findPacc);
			commonDataRepo.saveAllAndFlush(savePacList);
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			// Copy Old Quote Additional Details
			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
				
			//	res =  copyQuoteDocumentDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
				
				res =  copyQuoteLocationDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
			}
			
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
			Double premiumFc = 0D;					
			Double overAllPremiumFc = 0D;
			Double premiumLc = 0D;					
			Double overAllPremiumLc = 0D;

			Double groupPremiumFc = 0D ;					
			Double groupOverAllPremiumFc = 0D ;
			Double groupPremiumLc = 0D ;					
			Double groupOverAllPremiumLc = 0D ;
			
			
			
			// Update Eservice Travel
			EserviceTravelDetails eserTravel = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo() );
			EserviceTravelGroupDetails groupData = eserGroupRepo.findByRequestReferenceNoAndGroupId(request.getRequestReferenceNo() , request.getGroupId());
			List<FactorRateRequestDetails>  covers = new ArrayList<FactorRateRequestDetails>();
			
			if ( eserTravel.getPlanTypeId().equals(3) && request.getGroupId().equals(1) ) {
				covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,2 ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));
			} else {
				
				covers = facRateRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() , 0,0,request.getGroupId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));		
				
			//	List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
				
				// Insert Other Covers
				List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId())).collect(Collectors.toList());
				List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
				
				List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			//	premiumCovers.addAll(defaultCovers);
				
				for ( CoverIdsReq covReq :  coverReqList) {
					 
					List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0)).collect(Collectors.toList());				
					
					if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							premiumCovers.addAll(filterNonDefaultCovers);
							
						}else {
							List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
							premiumCovers.addAll(filterNonDefaultSubCovers);
						}
					}
				}
				premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
				overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
				premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
				overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();

				groupPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
				groupOverAllPremiumFc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
				groupPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
				groupOverAllPremiumLc = premiumCovers.stream().filter( o -> o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
				
			}
			String decimalDigits = currencyDecimalFormat(eserTravel.getCompanyId() , eserTravel.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			eserTravel.setActualPremiumFc(premiumFc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(premiumFc)) );
			eserTravel.setActualPremiumLc(premiumLc  == 0D ? new BigDecimal(0) :new BigDecimal(df.format(premiumLc)));
			eserTravel.setOverallPremiumFc(overAllPremiumFc  == 0D ? new BigDecimal(0) :new BigDecimal(df.format(overAllPremiumFc)));
			eserTravel.setOverallPremiumLc(overAllPremiumLc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(overAllPremiumLc)));
			eserTravel.setQuoteNo(request.getQuoteNo());
			eserTravel.setCustomerId(request.getCustomerId());
			
			groupData.setActualPremiumFc(groupPremiumFc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupPremiumFc)));
			groupData.setActualPremiumLc(groupPremiumLc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupPremiumLc)));
			groupData.setOverallPremiumFc(groupOverAllPremiumFc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupOverAllPremiumFc)));
			groupData.setOverallPremiumLc(groupOverAllPremiumLc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupOverAllPremiumLc)));
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
			travelData.setStatus(eserTravel.getStatus());
			List<FactorRateRequestDetails>  filterCover = covers.stream().filter( o -> o.getVehicleId().equals( request.getGroupId())).collect(Collectors.toList());
			filterCover = filterCover.size() > 0  ? filterCover : covers.stream().filter( o -> o.getVehicleId().equals(2)).collect(Collectors.toList());
					
			travelData.setVdRefno(filterCover.get(0).getVdRefno());	
			travelData.setMsRefno(filterCover.get(0).getMsRefno());		
			travelData.setCdRefno(filterCover.get(0).getCdRefno());	
			travelData.setActualPremiumFc(premiumFc);
			travelData.setActualPremiumLc(premiumLc);
			travelData.setOverallPremiumFc(overAllPremiumFc);
			travelData.setOverallPremiumLc(overAllPremiumLc);
			
			if(eserTravel.getEndorsementType()!=null) {
				String prevQuoteNo=eserTravel.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				BigDecimal endtPremium = updateEndtPremium(request.getQuoteNo(),eserTravel.getEndorsementEffdate(),prevQuoteNo, eserTravel.getRiskId(),Endtcovers);				
				eserTravel.setEndtPremium(endtPremium.doubleValue());
				travelData.setEndtPremium(endtPremium.doubleValue());
			}   
			
			eserTraRepo.saveAndFlush(eserTravel);
			traPassRepo.saveAndFlush(travelData);
			log.error("Save Motor Info is ---> " + json.toJson(travelData));
			
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
			// Copy Old Quote Additional Details
			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
				
			//	res =  copyQuoteDocumentDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
				
			}
			
			
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
			
			if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
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
				
				// Save Endt Covers
				if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
					res = EndtCoverSavePoint(request , devidedCovers );
					
				} else {
					res = CoverSavePoint(devidedCovers ) ;
					
				}
				
				
			} else {
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()) , request.getVehicleId());
				
				// Save Endt Covers
				if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
					res = EndtCoverSavePoint(request , covers );
				} else {
					res = CoverSavePoint(covers ) ;
				}
				
			}
			

			
				
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Vehicle Id : " + request.getVehicleId() + " Cover Details" ) ;
		}
	
		return res;
	}
		
	
	private synchronized Map<String,Object>  CoverSavePoint(List<FactorRateRequestDetails>  covers ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		try {
			// FindData 
		//	List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") ).collect(Collectors.toList() );
			
			// Insert Default Covers
		//	res = InsertCoverDetails(defaultCovers  );
			
			List<VehicleIdsReq> VehicleList = new ArrayList<VehicleIdsReq>();
			List<CoverIdsReq> coverReqList =new ArrayList<CoverIdsReq>();
			
			// Insert Other Covers
			if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if ( request.getMotorYn().equalsIgnoreCase("M") ) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) ). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A") ) {
				
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())   &&  o.getSectionId().equalsIgnoreCase(request.getSectionId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			
			} else   {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) ). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			}
			
			List<FactorRateRequestDetails> updateCovers = new ArrayList<FactorRateRequestDetails>(); 
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getCoverId().equals(covReq.getCoverId())).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						res = InsertCoverDetails(filterNonDefaultCovers );
						
						List<FactorRateRequestDetails> 	updateCovers1 = filterNonDefaultCovers.stream().filter( o ->o.getIsSelected()!=null &&  (o.getIsSelected().equalsIgnoreCase("N")) ).collect(Collectors.toList());
						updateCovers.addAll(updateCovers1);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId())) ).collect(Collectors.toList());
						res = InsertCoverDetails(filterNonDefaultSubCovers );
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
	
	
	private synchronized Map<String,Object>  EndtCoverSavePoint(QuoteThreadReq request , List<FactorRateRequestDetails>  covers ) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		try {
			// FindData 
		//	List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o -> o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") ).collect(Collectors.toList() );
			
			// Insert Default Covers
	//		res = InsertEndtCoverDetails(PrevQuoteNo , defaultCovers , policyStartDate ,policyEndDate ,  noOfDays);
						
			List<VehicleIdsReq> VehicleList = new ArrayList<VehicleIdsReq>();
			List<CoverIdsReq> coverReqList =new ArrayList<CoverIdsReq>();
			
			// Insert Other Covers
			 if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
					VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
					coverReqList = VehicleList.get(0).getCoverIdList();
					
			}else if ( request.getMotorYn().equalsIgnoreCase("M")) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A")) {
				
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())   &&  o.getSectionId().equalsIgnoreCase(request.getSectionId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			
			} else   {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
			}
			
			List<FactorRateRequestDetails> updateCovers = new ArrayList<FactorRateRequestDetails>();
			
			for ( CoverIdsReq covReq :  coverReqList) {
					covers.sort(Comparator.comparing(FactorRateRequestDetails :: getEndtCount).reversed()) ;
					BigDecimal endtCount = covers.get(0).getEndtCount() ;
					List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getCoverId().equals(covReq.getCoverId()) ).collect(Collectors.toList());
					// List<FactorRateRequestDetails> filterCovers = covers.stream().filter( o -> o.getCoverId().equals(covReq.getCoverId()) &&  o.getEndtCount().equals(endtCount) ).collect(Collectors.toList());
					
					if(filterCovers != null && filterCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							res = InsertEndtCoverDetails(request , filterCovers);
							
							List<FactorRateRequestDetails> 	updateCovers1 = filterCovers.stream().filter( o -> o.getIsSelected()!=null &&  (o.getIsSelected().equalsIgnoreCase("N")) ).collect(Collectors.toList());
							updateCovers.addAll(updateCovers1);
							
						}else {
							List<FactorRateRequestDetails> filterSubCovers = filterCovers.stream().filter( o ->  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId())) ).collect(Collectors.toList());
							res = InsertEndtCoverDetails(request , filterSubCovers );
							List<FactorRateRequestDetails> 	updateCovers2 = filterSubCovers.stream().filter( o -> o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("N") ).collect(Collectors.toList());
							updateCovers.addAll(updateCovers2);
						}
					}
				}
				
				
//			}
			
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
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	private synchronized BigDecimal getDevidedValue(BigDecimal inputValue ,Integer groupCount ) {
		BigDecimal devidedValue = BigDecimal.ZERO ;
		try {
			devidedValue = inputValue.divide(new BigDecimal(groupCount),2, RoundingMode.HALF_UP) ;
	
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return devidedValue;
	}

	
		private synchronized Map<String,Object>  InsertCoverDetails(List<FactorRateRequestDetails> covers ) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Save Cover Details
				List<PolicyCoverData>  saveCovers = new ArrayList<PolicyCoverData>();
				for ( FactorRateRequestDetails cov : covers) {
					PolicyCoverData coverData  = new PolicyCoverData();
					dozerMapper.map(cov, coverData);
					coverData.setEntryDate(new Date());
//					coverData.setCoverPeriodFrom(policyStartDate);
//					coverData.setCoverPeriodTo(policyEndDate);
//					coverData.setNoOfDays( noOfDays==null? null : new BigDecimal(noOfDays));
//					
					coverData.setQuoteNo(request.getQuoteNo());
					coverData.setIsSelected(cov.getIsSelected().equalsIgnoreCase("N") ? "Y" :cov.getIsSelected());
					coverData.setCreatedBy(request.getCreatedBy());
					coverData.setVehicleId(request.getVehicleId());
					coverData.setDiscountCoverId(cov.getDiscountCoverId()==null?0 :cov.getDiscountCoverId());
					saveCovers.add(coverData);
				//	log.error("Save Cover Info is ---> " + json.toJson(coverData));
					
				}
				coverRepo.saveAllAndFlush(saveCovers);	
				
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
		
		
		
		private synchronized Map<String,Object>  InsertEndtCoverDetails(QuoteThreadReq request , List<FactorRateRequestDetails> covers  ) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				List<PolicyCoverData>  OldPolicyCovers = coverRepo.findByQuoteNoAndStatusOrderByVehicleIdAsc(request.getEndtPrevQuoteNo() ,"Y" );
				
						
				// Save Cover Details
				List<PolicyCoverData> saveCovers = new ArrayList<PolicyCoverData>();
				for ( FactorRateRequestDetails cov : covers) {
					PolicyCoverData coverData  = new PolicyCoverData();
					
					dozerMapper.map(cov, coverData);
					coverData.setEntryDate(new Date());
					
					// Date Differents
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					Date periodStart =  cov.getCoverPeriodFrom();
					Date periodEnd   = cov.getCoverPeriodTo();
					Date effDate   = request.getEffetiveDate();
					
					// Endt Type
					boolean alreadyOptCover = false ;
					boolean endtCovModify = false ; 
					if( StringUtils.isNotBlank(request.getEndtFields())  &&  request.getEndtFields().equalsIgnoreCase("Covers") ) {
							endtCovModify = true  ;
					}
					
					// Filter Old Cover
					if( cov.getSubCoverYn() ==null || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
						List<PolicyCoverData> filterOldCover =  OldPolicyCovers.stream().filter(  o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId()) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
									&& o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());	            			
						
						if(filterOldCover.size() > 0 ) {
							PolicyCoverData oldCoverData = filterOldCover.get(0) ;
							periodStart = oldCoverData.getCoverPeriodFrom().before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : oldCoverData.getCoverPeriodFrom();
							periodEnd   = oldCoverData.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : oldCoverData.getCoverPeriodTo();
							alreadyOptCover = true ;
							
						} else {
							periodStart = effDate.before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : effDate;
							periodEnd   = cov.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : cov.getCoverPeriodTo();
						}
					
					} else {
        				List<PolicyCoverData> filterOldSubCover =  OldPolicyCovers.stream().filter(  o ->  o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId()) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
									&& o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))   ).collect(Collectors.toList());
						
        				if(filterOldSubCover.size() > 0 ) {
        					PolicyCoverData oldSubCoverData = filterOldSubCover.get(0) ;
							periodStart = oldSubCoverData.getCoverPeriodFrom().before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : oldSubCoverData.getCoverPeriodFrom();
							periodEnd   = oldSubCoverData.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : oldSubCoverData.getCoverPeriodTo();
							alreadyOptCover = true ;
							
						} else {
							periodStart = effDate.before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : effDate;
							periodEnd   = cov.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : cov.getCoverPeriodTo();
						}
						
        			}
					
					
					Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
					// Check Leap Year
					boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					System.out.println( "Policy Opted Cover :  "+ coverData.getCoverDesc() + " Difference in days: " + diff);
					
					coverData.setCoverPeriodFrom(periodStart);
					coverData.setCoverPeriodTo(periodEnd);
					coverData.setNoOfDays(new BigDecimal(diff));
					coverData.setStatus("Y");
					
					// Premium
					if(endtCovModify == true && alreadyOptCover==true && ( cov.getCoverageType().equalsIgnoreCase("E") || cov.getCoverageType().equalsIgnoreCase("T") && cov.getDiscLoadId() > 0 ) ) {
						
						coverData.setDiffPremiumIncludedTaxLc(BigDecimal.ZERO);
						coverData.setDiffPremiumIncludedTaxFc(BigDecimal.ZERO);
						coverData.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						coverData.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						coverData.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						coverData.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						coverData.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						coverData.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						coverData.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						coverData.setPremiumIncludedTaxLc(BigDecimal.ZERO);
						
					} else {
						
						coverData.setDiffPremiumIncludedTaxLc(cov.getDiffPremiumIncludedTaxLc() != null ? cov.getDiffPremiumIncludedTaxLc() : BigDecimal.ZERO );
						coverData.setDiffPremiumIncludedTaxFc(cov.getDiffPremiumIncludedTaxFc() != null ? cov.getDiffPremiumIncludedTaxLc() : BigDecimal.ZERO );
						coverData.setPremiumBeforeDiscountFc(cov.getPremiumBeforeDiscountFc() != null ? cov.getPremiumBeforeDiscountFc() : BigDecimal.ZERO );
						coverData.setPremiumBeforeDiscountLc(cov.getPremiumBeforeDiscountLc() != null ? cov.getPremiumBeforeDiscountLc() : BigDecimal.ZERO );
						coverData.setPremiumAfterDiscountFc(cov.getPremiumAfterDiscountFc() != null ? cov.getPremiumAfterDiscountFc() : BigDecimal.ZERO );
						coverData.setPremiumAfterDiscountLc(cov.getPremiumAfterDiscountLc() != null ? cov.getPremiumAfterDiscountLc() : BigDecimal.ZERO );
						coverData.setPremiumExcludedTaxFc(cov.getPremiumExcludedTaxFc() != null ? cov.getPremiumExcludedTaxFc() : BigDecimal.ZERO );
						coverData.setPremiumExcludedTaxLc(cov.getPremiumExcludedTaxLc() != null ? cov.getPremiumExcludedTaxLc() : BigDecimal.ZERO );
						coverData.setPremiumIncludedTaxFc(cov.getPremiumIncludedTaxFc()  != null ? cov.getPremiumIncludedTaxFc() : BigDecimal.ZERO );
						coverData.setPremiumIncludedTaxLc(cov.getPremiumIncludedTaxLc() != null ? cov.getPremiumIncludedTaxLc() : BigDecimal.ZERO );
						
					}
					
					
					coverData.setQuoteNo(request.getQuoteNo());
					coverData.setIsSelected(cov.getIsSelected().equalsIgnoreCase("N") ? "Y" :cov.getIsSelected());
					coverData.setCreatedBy(request.getCreatedBy());
					coverData.setVehicleId(request.getVehicleId());
					coverData.setDiscountCoverId(cov.getDiscountCoverId()==null?0 :cov.getDiscountCoverId());
					
					saveCovers.add(coverData);	
				//	log.error("Save Cover Info is ---> " + json.toJson(coverData));
					
				}
				coverRepo.saveAllAndFlush(saveCovers);
				
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

		
	
//-------------------------------------------------------------------Delete Method Start -------------------------------------------------------//
		
		public synchronized Map<String,Object>  deleteOldQuoteRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				
				// Delete Risk Tables
				 if( req.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId) ) {
						res = deleteTravelRecords(req);
						
				} else if(req.getMotorYn().equalsIgnoreCase("M") ) {
					res = deleteMotorRecords(req);
					
				} else if(req.getMotorYn().equalsIgnoreCase("A") ) {
					res = deleteBuildingRecords(req);
					
				} else  {
					res = deleteCommonRecords(req);
					
				}
				
				// Delete Cover Table
				res = deleteCoverRecords(req);
				
	 			// Section
				res = deleteSectionRecords(req);
	 			
				// Common Doc
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo()) ) {
					// Copy Quote Doc
					res = copyDocumentRecords(req);
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
		
		
		
		
		public synchronized Map<String,Object>  deleteMotorRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Long motorInfo =  motorRepo.countByQuoteNo(req.getQuoteNo());
				if (motorInfo > 0  ) {
					motorRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
				// update 
				
				// Find Motor
				// Deactivate Old Record
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo()) ) {
					List<MotorDataDetails> oldMotors = motorRepo.findByQuoteNo(request.getEndtPrevQuoteNo() );
					
					// Copy Quote Doc
					List<EserviceMotorDetails> eserMotors = eserMotRepo.findByRequestReferenceNoAndStatusOrderByRiskIdAsc(request.getRequestReferenceNo() ,"D");
					eserMotors.stream().forEach(i->i.setQuoteNo(request.getQuoteNo()));
					
					List<MotorDataDetails> motorDatas  = new ArrayList<MotorDataDetails>();
				
					eserMotors.forEach(ref ->  {
						// Save Motro Details
						MotorDataDetails motorData  = new MotorDataDetails();
						
						List<MotorDataDetails> filterOldMotors  = oldMotors.stream().filter( o -> o.getVehicleId().equals(ref.getRiskId().toString()) ).collect(Collectors.toList())	;			
						MotorDataDetails old = filterOldMotors.get(0);
						
						dozerMapper.map(ref , motorData);
						motorData.setEntryDate(new Date());	
						motorData.setCreatedBy(request.getCreatedBy());
						motorData.setQuoteNo(request.getQuoteNo());
						motorData.setCdRefno(old.getCdRefno());
						motorData.setVdRefno(old.getVdRefno());
						motorData.setMsRefno(old.getMsRefno());
						
						motorData.setCustomerId(request.getCustomerId());
						motorData.setVehicleId(ref.getRiskId().toString());
						motorData.setStatus(ref.getStatus());
								
						// Date Diffrence
						Date periodStart = ref.getPolicyStartDate();
						Date effDate =  request.getEffetiveDate();
						Date oldEndDate = null ;
						Long daysBetween = 0L ;
						String diff = "" ;
							
						if(periodStart.equals(effDate)  || periodStart.after(effDate) ) {
							oldEndDate = periodStart ;
							daysBetween = 0L ;
							diff = String.valueOf(daysBetween);
							
						} else {
							Long diffInMillies = Math.abs(effDate.getTime() - periodStart.getTime());
							daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
							oldEndDate  = effDate ;
							// Check Leap Year
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							boolean leapYear = LocalDate.parse(sdf.format(effDate) ).isLeapYear();
							diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
						}
						
						motorData.setPolicyEndDate(oldEndDate);
						motorData.setStatus("D");
						motorData.setPeriodOfInsurance(diff);
						
						List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);

						
			
						BigDecimal endtPremium = updateEndtPremium(request.getQuoteNo(),effDate,ref.getEndtPrevQuoteNo(),ref.getRiskId() ,Endtcovers);
						motorData.setEndtPremium(endtPremium.doubleValue());
						
						motorData.setActualPremiumFc(endtPremium.doubleValue());
						motorData.setActualPremiumLc(endtPremium.doubleValue());
						motorData.setOverallPremiumFc(endtPremium.doubleValue());
						motorData.setOverallPremiumLc(endtPremium.doubleValue());
						ref.setEndtPremium(endtPremium.doubleValue());
						motorDatas.add(motorData);
						
					}) ;

					motorRepo.saveAllAndFlush(motorDatas);
					eserMotRepo.saveAll(eserMotors);
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
	
		
		
		public synchronized Map<String,Object>  deleteTravelRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Long travelInfo =  traPassRepo.countByQuoteNo(req.getQuoteNo());
				if (travelInfo > 0  ) {
					//Delete data
//					List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getQuoteNo());
					traPassRepo.deleteByQuoteNo(req.getQuoteNo());
						
//					// Find History
//					for (TravelPassengerDetails passData :  oldPassDatas) {
//						Long travelHisInfo =  traPassHisRepo.countByQuoteNoAndPassengerId(req.getQuoteNo() ,passData.getPassengerId());
//						if (travelHisInfo > 0 ) {
//							//Delete data
//							traPassHisRepo.deleteByQuoteNoAndPassengerId(req.getQuoteNo(),passData.getPassengerId());
//							
//						}
//						// Save New 
//						TravelPassengerHistory traHistorySave = new TravelPassengerHistory(); 
//						dozerMapper.map(passData, traHistorySave);
//						traHistorySave.setEntryDate(new Date());
//						traPassHisRepo.saveAndFlush(traHistorySave);
//					}
					
					
				} else if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo())) {
					// Endorsement
					travelInfo =  traPassRepo.countByQuoteNo(req.getEndtPrevQuoteNo());
					if (travelInfo > 0  ) {
						//Delete data
//						List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getEndtPrevQuoteNo());
						traPassRepo.deleteByQuoteNo(req.getQuoteNo());
//							
//						// Find History
//						for (TravelPassengerDetails passData :  oldPassDatas) {
//							Long travelHisInfo =  traPassHisRepo.countByQuoteNoAndPassengerId(req.getQuoteNo(),passData.getPassengerId());
//							if (travelHisInfo > 0 ) {
//								//Delete data
//								traPassHisRepo.deleteByQuoteNoAndPassengerId(req.getQuoteNo(),passData.getPassengerId());
//								
//							}
//							// Save New 
//							TravelPassengerHistory traHistorySave = new TravelPassengerHistory(); 
//							dozerMapper.map(passData, traHistorySave);
//							traHistorySave.setRequestReferenceNo(req.getRequestReferenceNo());
//							traHistorySave.setQuoteNo(req.getQuoteNo());
//							traHistorySave.setCustomerId(req.getCustomerId());
//							traHistorySave.setEntryDate(new Date());
//							traPassHisRepo.saveAndFlush(traHistorySave);
//						}
					}
				
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		
		
		public synchronized Map<String,Object>  deleteBuildingRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Long buildInfo =  buildRepo.countByQuoteNo(req.getQuoteNo());
				if (buildInfo > 0  ) {
					buildRepo.deleteByQuoteNo(req.getQuoteNo());
				}

				Long pacInfo =  commonDataRepo.countByQuoteNo(req.getQuoteNo());
				if (pacInfo > 0  ) {
					commonDataRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		
		
		public synchronized Map<String,Object>  deleteCommonRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Long commonInfo =  commonDataRepo.countByQuoteNo(req.getQuoteNo());
				if (commonInfo > 0  ) {
					commonDataRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		
		public synchronized Map<String,Object>  deleteCoverRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Remove Covers
				Long coverInfo =  coverRepo.countByQuoteNo(req.getQuoteNo());
	 			if (coverInfo >0 ) {
	 				//Delete data
	 				coverRepo.deleteByQuoteNo(req.getQuoteNo() );
	 				
	 			}
		 		
	 			  // Deactivate Old Covers 
	 			if(StringUtils.isNotBlank(request.getEndtPrevQuoteNo()) ) {
	 				
	 				CommonRes commonRes = deactivateOldCovers(request); 
	 				
	 			}
	 			
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		public CommonRes deactivateOldCovers( QuoteThreadReq request ) {
			CommonRes commonRes = new CommonRes();
			List<Error> errors = new ArrayList<Error>();
			String res = "" ;
			try {
				
				// Deactivate Travel product covers
				if (request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
					
					res = deactivateTravelCovers(request);
				// Deactivate Other Covers
				} else {
					res = deactivateOtherProductCovers(request);
				}
				
				
	 			
		        commonRes.setCommonResponse(res);
				commonRes.setIsError(false);
				commonRes.setErrorMessage(Collections.emptyList());
			 	commonRes.setMessage("Success");
			 	
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " +  e.getMessage());
				errors.add(new Error("01","Common Error",e.getMessage()));
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");	
			}
			return commonRes ;
		}
		
		
		public String deactivateTravelCovers( QuoteThreadReq request ) {
			String res = "";
			DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
			try {
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(request.getRequestReferenceNo());
				List<EserviceTravelGroupDetails> groupData = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(request.getRequestReferenceNo() );
				List<PolicyCoverData>  OldPolicyCovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(request.getEndtPrevQuoteNo());
				List<PolicyCoverData> rePopulateRecords = new ArrayList<PolicyCoverData>();
				
//				// Deleted Records
//				List<PolicyCoverData>  fiterDeletedCovers = OldPolicyCovers.stream().filter( o ->  o.getStatus().equalsIgnoreCase("D") ).collect(Collectors.toList());
//				List<PolicyCoverData> rePopulateRecords = new ArrayList<PolicyCoverData>();
//				fiterDeletedCovers.forEach(ref ->  {
//					PolicyCoverData pc = new PolicyCoverData();
//					dozerMapper.map(ref , pc) ;
//					pc.setQuoteNo(request.getQuoteNo());
//					pc.setRequestReferenceNo(request.getRequestReferenceNo());
//					pc.setPolicyNo(null);
//					rePopulateRecords.add(pc) ;
//					
//				}) ;
				
				// Non Selected Records
				List<PolicyCoverData>  deactivateOldCovers = OldPolicyCovers ; 
	        	List<VehicleIdsReq>  filterAdult  = request.getVehicleIdsList().stream().filter( o ->  o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  filterOthers = request.getVehicleIdsList().stream().filter( o -> ! o.getVehicleId().equals(2) ).collect(Collectors.toList());
	        	List<VehicleIdsReq>  totalGroup  = new ArrayList<VehicleIdsReq>();
	        	totalGroup.addAll(filterAdult)	;
	        	totalGroup.addAll(filterOthers);
	        	List<Integer> groupIds = totalGroup.stream().map(VehicleIdsReq :: getVehicleId  ).collect(Collectors.toList());
//	        	// Filte Count
	        	for (Integer vehId :  groupIds ) {
	        		 Integer passCount = 0;
					 List<EserviceTravelGroupDetails> filterGroup = groupData.stream().filter( o -> o.getGroupId().equals(vehId) ).collect(Collectors.toList());				 
					 List<String> sectionId = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).map(VehicleIdsReq :: getSectionId   ).collect(Collectors.toList());	
					 List<CoverIdsReq> coverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(vehId)).collect(Collectors.toList()).get(0).getCoverIdList();
					 for (int i=0 ; i < filterGroup.get(0).getGrouppMembers() ; i++) {
						 Integer pass = passCount + 1 ;
						 passCount = passCount + 1 ;
						 
						 for (CoverIdsReq cov : coverList ) {
							  if( cov.getSubCoverYn() ==null && cov.getSubCoverYn().equalsIgnoreCase("N") ) {
			            			deactivateOldCovers.removeIf(  o -> o.getVehicleId().equals(pass) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
			            					&& o.getSectionId().equals(Integer.valueOf(sectionId.get(0) )) && o.getCoverId().equals(cov.getCoverId())   );	            			
			            		} else {
			            			
			            			deactivateOldCovers.removeIf(  o -> o.getVehicleId().equals(pass) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
			            					&& o.getSectionId().equals(Integer.valueOf(sectionId.get(0) )) && o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()) )  );	 
			            		}
						 }
		             }	
					 
		         } 
	        	
	        	 // Save Differents
				 deactivateOldCovers.forEach(ref ->  {
					 PolicyCoverData pc = new PolicyCoverData();
						dozerMapper.map(ref , pc) ;	
						pc.setQuoteNo(request.getQuoteNo());
						pc.setRequestReferenceNo(request.getRequestReferenceNo());
						pc.setPolicyNo(null);
						
						// Date Diffrence
						Date periodStart = ref.getCoverPeriodFrom();
						Date effDate =  request.getEffetiveDate();
						Date oldEndDate = null ;
						Long daysBetween = 0L ;
						String diff = "" ;
							
						if(periodStart.equals(effDate)  || periodStart.after(effDate) ) {
							oldEndDate = periodStart ;
							daysBetween = 0L ;
							diff = String.valueOf(daysBetween);
							
						} else {
							Long diffInMillies = Math.abs(effDate.getTime() - periodStart.getTime());
							daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
							oldEndDate  = effDate ;
							// Check Leap Year
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							boolean leapYear = LocalDate.parse(sdf.format(effDate) ).isLeapYear();
							System.out.println( "Deactivated Policy Cover :  "+ ref.getCoverDesc() + "  Difference in days: " + diff);
							diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
						}
						
						
						pc.setCoverPeriodTo(oldEndDate)  ;
						pc.setStatus("D");
						pc.setNoOfDays(new BigDecimal( diff));
						
						// Other fields
						List<FactorRateRequestDetails> filterFactor = covers.stream().filter( o -> o.getVehicleId().equals(ref.getVehicleId() ) && o.getProductId().equals(Integer.valueOf(ref.getProductId()))
		            					&& o.getSectionId().equals(Integer.valueOf(ref.getSectionId())) && o.getCoverId().equals(ref.getCoverId())   ).collect(Collectors.toList());
						
						if( filterFactor.size() > 0 ) {
							pc.setDiffPremiumIncludedTaxLc(filterFactor.get(0).getDiffPremiumIncludedTaxLc());
							pc.setDiffPremiumIncludedTaxFc(filterFactor.get(0).getDiffPremiumIncludedTaxFc());
						}
						rePopulateRecords.add(pc) ;
				}) ;
				 
				 if (rePopulateRecords.size() > 0 ) {
					 coverRepo.saveAllAndFlush(rePopulateRecords);
				 }
				 res = "Success" ;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " +  e.getMessage());
				return null ;
			}
			return res ;
		}
		
		@Transactional
		public String deactivateOtherProductCovers( QuoteThreadReq request ) {
			String res = "";
			DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
			try {			
				List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoOrderByVehicleIdAsc(request.getRequestReferenceNo());
				List<PolicyCoverData>  OldPolicyCovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(request.getEndtPrevQuoteNo());
				List<PolicyCoverData> rePopulateRecords = new ArrayList<PolicyCoverData>();
				
//				// Deleted Records
//				List<PolicyCoverData>  fiterDeletedCovers = OldPolicyCovers.stream().filter( o ->  o.getStatus().equalsIgnoreCase("D") ).collect(Collectors.toList());
//				List<PolicyCoverData> rePopulateRecords = new ArrayList<PolicyCoverData>();
//				fiterDeletedCovers.forEach(ref ->  {
//					PolicyCoverData pc = new PolicyCoverData();
//					dozerMapper.map(ref , pc) ;
//					pc.setQuoteNo(request.getQuoteNo());
//					pc.setRequestReferenceNo(request.getRequestReferenceNo());
//					pc.setPolicyNo(null);
//					rePopulateRecords.add(pc) ;
//					
//				}) ;
				
				// Non Selected Records
				List<PolicyCoverData>  deactivateOldCovers = OldPolicyCovers.stream().filter( o ->  ! o.getStatus().equalsIgnoreCase("D") ).collect(Collectors.toList());
				 for ( VehicleIdsReq vehId :  request.getVehicleIdsList() ) {
		            	for (CoverIdsReq cov : vehId.getCoverIdList() ) {
							
		            		if( cov.getSubCoverYn() ==null || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
		            				deactivateOldCovers.removeIf(  o -> o.getVehicleId().equals(vehId.getVehicleId() ) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
		            					&& o.getSectionId().equals(Integer.valueOf(vehId.getSectionId())) && o.getCoverId().equals(cov.getCoverId())   );	            			
		            		} else {
		            			
		            			deactivateOldCovers.removeIf(  o -> o.getVehicleId().equals(vehId.getVehicleId() ) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
		            					&& o.getSectionId().equals(Integer.valueOf(vehId.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))  );	 
		            			
		            		}
		            	}
			            
		           }
				 
				
				 // Save Differents
				 deactivateOldCovers.forEach(ref ->  {
						
						// Other fields
						List<FactorRateRequestDetails> filterFactor = covers.stream().filter( o -> o.getVehicleId().equals(ref.getVehicleId() ) && o.getProductId().equals(Integer.valueOf(ref.getProductId()))
		            					&& o.getSectionId().equals(Integer.valueOf(ref.getSectionId())) && o.getCoverId().equals(ref.getCoverId()) 
		            					  ).collect(Collectors.toList());
						
						if( filterFactor.size() > 0 ) {
							
							// Save with endorsement
							for ( FactorRateRequestDetails f :  filterFactor  ) {
								
								PolicyCoverData fc = new PolicyCoverData();
								dozerMapper.map(f , fc) ;
								fc.setQuoteNo(request.getQuoteNo());
								fc.setRequestReferenceNo(request.getRequestReferenceNo());
								fc.setPolicyNo(null);
								
								// Premium
								fc.setDiffPremiumIncludedTaxLc(f.getDiffPremiumIncludedTaxLc());
								fc.setDiffPremiumIncludedTaxFc(f.getDiffPremiumIncludedTaxFc());
								fc.setPremiumBeforeDiscountFc(f.getPremiumBeforeDiscountFc());
								fc.setPremiumBeforeDiscountLc(f.getPremiumBeforeDiscountLc());
								fc.setPremiumAfterDiscountFc(f.getPremiumAfterDiscountFc());
								fc.setPremiumAfterDiscountLc(f.getPremiumAfterDiscountLc());
								fc.setPremiumExcludedTaxFc(f.getPremiumExcludedTaxFc());
								fc.setPremiumExcludedTaxLc(f.getPremiumExcludedTaxLc());
								fc.setPremiumIncludedTaxFc(f.getPremiumIncludedTaxFc());
								fc.setPremiumIncludedTaxLc(f.getPremiumIncludedTaxLc());
								
								// Date Diffrence
								Date periodStart = ref.getCoverPeriodFrom();
								Date effDate =  request.getEffetiveDate();
								Date oldEndDate = null ;
								Long daysBetween = 0L ;
								String diff = "" ;
									
								if(periodStart.equals(effDate)  || periodStart.after(effDate) ) {
									oldEndDate = periodStart ;
									daysBetween = 0L ;
									diff = String.valueOf(daysBetween);
									
								} else {
									Long diffInMillies = Math.abs(effDate.getTime() - periodStart.getTime());
									daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
									oldEndDate  = effDate ;
									// Check Leap Year
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
									boolean leapYear = LocalDate.parse(sdf.format(effDate) ).isLeapYear();
									System.out.println( "Deactivated Policy Cover :  "+ ref.getCoverDesc() + "  Difference in days: " + diff);
									diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
								}
								
								
								fc.setCoverPeriodTo(oldEndDate)  ;
								fc.setStatus("D");
								fc.setNoOfDays(new BigDecimal( diff));
								rePopulateRecords.add(fc) ;
							} 	
						} 
						
				}) ;
				 
				 if (rePopulateRecords.size() > 0 ) {
					 coverRepo.saveAllAndFlush(rePopulateRecords);
				 }
				 res = "Success" ;
	        	
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --> " +  e.getMessage());
				return null ;
			}
			return res ;
		}
		
		public synchronized Map<String,Object>  deleteSectionRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Long secInfo =  secRepo.countByQuoteNo(req.getQuoteNo());
				if (secInfo > 0  ) {
					secRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		public synchronized Map<String,Object>  copyDocumentRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {

				List<Integer> ids = new ArrayList<Integer>();
				ids.add(0);
				ids.add(1);
				
				Long docInfo = docRepo.countByQuoteNo(req.getQuoteNo() );
				if( docInfo <= 0  ) {
					List<DocumentTransactionDetails>   oldDocDetails = docRepo.findByQuoteNo( req.getEndtPrevQuoteNo() ) ;
					
					List<DocumentTransactionDetails> saveDocList = new ArrayList<DocumentTransactionDetails>();
					if( oldDocDetails.size() > 0  ) {
						for ( DocumentTransactionDetails doc : oldDocDetails ) {
							DocumentTransactionDetails saveDoc = new DocumentTransactionDetails(); 		
							dozerMapper.map(doc , saveDoc);
							saveDoc.setQuoteNo(req.getQuoteNo() );
							saveDoc.setRequestReferenceNo(req.getRequestReferenceNo());
							saveDocList.add(saveDoc) ;
						}
						docRepo.saveAllAndFlush(saveDocList);
					}
				}
				
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
	
//------------------------------------------------------------Delete Method End ----------------------------------------------------------//		
		
		
	private synchronized QuoteThreadRes call_QuoteSave(QuoteThreadReq  request) {
		QuoteThreadRes res= new QuoteThreadRes() ;
		try {
			// Home Positiom Master Thread Call
			Long homeInfo =  homeRepo.countByQuoteNo(request.getQuoteNo());
			if (homeInfo > 0 ) {
				//Delete data
				homeRepo.deleteByQuoteNo(request.getQuoteNo());
 				
			}
			
			// Save Home Position Master
			HomePositionMaster home = new HomePositionMaster();
			
			// Set Product Details
			if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId) ) {
				
				home = setTravelDetails(request);
				
			} else if(request.getMotorYn().equalsIgnoreCase("M")) {
				
				home = setMotorDetails(request);
				
			}  else if(request.getMotorYn().equalsIgnoreCase("A")) {
				
				home = setBuildingDetails(request);
				
			}   else  {
				
				home = setCommonDetails(request);
				
			}
			
			// Set Branch Details 
			LoginBranchMaster loginBranch =  getBranchDetails(home.getCompanyId() ,home.getBrokerBranchCode() ,home.getLoginId() );
			home.setUserType(loginBranch.getUserType() );			
			home.setBranchName(loginBranch.getBranchName());
			home.setBrokerBranchName(loginBranch.getBrokerBranchName());
			home.setSubUserType(loginBranch.getSubUserType());		
			home.setAgencyCode(loginBranch.getAgencyCode());
			
			// Primary KEy
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
			
			
			  
			// Set Premium Details
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
		//	List<PolicyCoverData>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList() );
			List<PolicyCoverData>  premiumCovers = new  ArrayList<PolicyCoverData>();
			List<TravelPassengerDetails> passengers = new  ArrayList<TravelPassengerDetails>();
			if( request.getProductId().equalsIgnoreCase(travelProductId) ) 
				passengers =   traPassRepo.findByQuoteNoOrderByTravelIdAsc(request.getQuoteNo());
		//	premiumCovers.addAll(defaultCovers);
			
			for (VehicleIdsReq vehReq : request.getVehicleIdsList() ) {
				List<CoverIdsReq> coverReqList = vehReq.getCoverIdList();
				for ( CoverIdsReq covReq :  coverReqList) { 
					List<PolicyCoverData> filterNonDefaultCovers  = new ArrayList<PolicyCoverData>();
					
					 if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId) ) {
							
							List<TravelPassengerDetails> filterGroupPassengers = passengers.stream().filter( o -> o.getGroupId().equals(vehReq.getVehicleId())). collect(Collectors.toList());
							
							filterNonDefaultCovers = new ArrayList<PolicyCoverData>();
							for (TravelPassengerDetails tra :  filterGroupPassengers  ) {
								List<PolicyCoverData> passengerCover = covers.stream().filter( o ->   o.getVehicleId().equals(tra.getPassengerId()) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
								filterNonDefaultCovers.addAll(passengerCover);
							}
											
						
					} else if(request.getMotorYn().equalsIgnoreCase("A")) {
						 
						 filterNonDefaultCovers = covers.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(vehReq.getSectionId())) && o.getVehicleId().equals(vehReq.getVehicleId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
					
					} else  {
						
						 filterNonDefaultCovers = covers.stream().filter( o ->  o.getVehicleId().equals(vehReq.getVehicleId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
						
					}
					
					if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							premiumCovers.addAll(filterNonDefaultCovers);
							
						}else {
							List<PolicyCoverData> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null? vehReq.getVehicleId() : request.getGroupId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0)  && o.getTaxId().equals(0) ).collect(Collectors.toList());
							premiumCovers.addAll(filterNonDefaultSubCovers);
						}
					}
				}
				
				
				
				
			}
			
			Integer endtCount = home.getEndtCount() ;
			Double premiumFc = premiumCovers.stream().filter( o ->    o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o ->  o.getDiscLoadId().equals(0)  &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
			Double premiumLc = premiumCovers.stream().filter( o ->  o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()   ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o ->  o.getDiscLoadId().equals(0)  &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum();
			Double vatPremiumFc = overAllPremiumFc - premiumFc ;  
			Double vatPercent = vatPremiumFc<=0D ?0 : (vatPremiumFc*100) / premiumFc ;
			Double vatPremiumLc = overAllPremiumLc - premiumLc ;  
			System.out.println("Home Position PremiumFc --> "  + premiumFc );
			System.out.println("Home Position OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Home Position PremiumLc --> "  + premiumLc );
			System.out.println("Home Position OverAllPremiumLc --> "  + overAllPremiumLc );
			Double tax1 =  premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(1) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			Double tax2 = premiumCovers.stream().filter( o ->  o.getDiscLoadId().equals(0) && o.getTaxId().equals(2) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			Double tax3 = premiumCovers.stream().filter( o ->  o.getDiscLoadId().equals(0) && o.getTaxId().equals(3) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			
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
			
			List<Integer> vehicleIds = request.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId ).collect(Collectors.toList());
			home.setVehicleNo(vehicleIds.size());
			
			String endtChargeOrRefund="";
			
			//Overall Endt Premium
			if(StringUtils.isNotBlank(home.getEndtTypeId())) {
				 
				BigDecimal endtPremium = updateEndtPremium(request.getQuoteNo(),home.getEndorsementEffdate(),home.getEndtPrevQuoteNo(),0,covers);
					
				endtChargeOrRefund="REFUND";
				if(endtPremium.doubleValue()>=0) {
					endtChargeOrRefund="CHARGE";
				}
				
				Double endtPremiumTax = 0D;
				home.setEndtPremiumTax(new BigDecimal(endtPremiumTax));
				home.setEndtPremium(endtPremium);
				home.setIsChargRefund(endtChargeOrRefund);
	
			
			}
			
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
	
	
	 

	private synchronized Map<String,Object> call_SectionSave(QuoteThreadReq  request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		ModelMapper mapper = new ModelMapper();
		try {
			
			List<EserviceSectionDetails> eserSec = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(request.getRequestReferenceNo());
			eserSec.forEach( o -> o.setUserOpt("N")  ) ;
			eserSecRepo.saveAllAndFlush(eserSec);
			
			List<VehicleIdsReq> VehicleIdsList = request.getVehicleIdsList();
			
			
			List<SectionDataDetails> secList = new ArrayList<SectionDataDetails>();
			List<EserviceSectionDetails> updateEserSec =  new ArrayList<EserviceSectionDetails>();
			
			for (VehicleIdsReq veh : VehicleIdsList) {
				List<EserviceSectionDetails> filterSecId =  eserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
				if(filterSecId.size() > 0 ) {
					
					EserviceSectionDetails filterSec = eserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) &&  o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
					filterSec.setUserOpt("Y");
					filterSec.setQuoteNo(request.getQuoteNo());
					filterSec.setUpdatedDate(new Date());
					updateEserSec.add(filterSec);
					
					SectionDataDetails  saveSec = new  SectionDataDetails();
					mapper.map(filterSec, saveSec)	;
					saveSec.setQuoteNo(request.getQuoteNo());
					saveSec.setUpdatedDate(new Date());
					saveSec.setSectionDesc(filterSec.getSectionName());
					secList.add(saveSec);	
				} else {
					filterSecId =  eserSec.stream().filter( o ->   o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());

					if(filterSecId.size() > 0 ) {
						
						EserviceSectionDetails filterSec = eserSec.stream().filter( o ->   o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
						filterSec.setUserOpt("Y");
						filterSec.setQuoteNo(request.getQuoteNo());
						filterSec.setUpdatedDate(new Date());
						updateEserSec.add(filterSec);
						
						SectionDataDetails  saveSec = new  SectionDataDetails();
						mapper.map(filterSec, saveSec)	;
						saveSec.setQuoteNo(request.getQuoteNo());
						saveSec.setUpdatedDate(new Date());
						saveSec.setSectionDesc(filterSec.getSectionName());
						secList.add(saveSec);	
					}
				}
			}
						
//				if( veh.getSectionId().equalsIgnoreCase("35") && pacSec==false ) {
//					EserviceSectionDetails filterSec = eserSec.stream().filter( o ->    o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
//					filterSec.setUserOpt("Y");
//					filterSec.setQuoteNo(request.getQuoteNo());
//					filterSec.setUpdatedDate(new Date());
//					updateEserSec.add(filterSec);
//					
//					SectionDataDetails  saveSec = new  SectionDataDetails();
//					mapper.map(filterSec, saveSec)	;
//					saveSec.setQuoteNo(request.getQuoteNo());
//					saveSec.setUpdatedDate(new Date());
//					secList.add(saveSec);
//					pacSec= true ;
//					
//					
//					
//				} else if(!veh.getSectionId().equalsIgnoreCase("35") && request.getMotorYn().equalsIgnoreCase("M")  ) {
//					List<EserviceSectionDetails> filterSecId =  updateEserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
//					if(filterSecId.size() <=0 ) {
//						
//						EserviceSectionDetails filterSec = eserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) &&  o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
//						filterSec.setUserOpt("Y");
//						filterSec.setQuoteNo(request.getQuoteNo());
//						filterSec.setUpdatedDate(new Date());
//						updateEserSec.add(filterSec);
//						
//						SectionDataDetails  saveSec = new  SectionDataDetails();
//						mapper.map(filterSec, saveSec)	;
//						saveSec.setQuoteNo(request.getQuoteNo());
//						saveSec.setUpdatedDate(new Date());
//						secList.add(saveSec);	
//					}
//					
//				} else if(!veh.getSectionId().equalsIgnoreCase("35") ) {
//					List<EserviceSectionDetails> filterSecId =  updateEserSec.stream().filter( o ->  o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
//					if(filterSecId.size() <=0 ) {
//						
//						EserviceSectionDetails filterSec = eserSec.stream().filter( o -> o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
//						filterSec.setUserOpt("Y");
//						filterSec.setQuoteNo(request.getQuoteNo());
//						filterSec.setUpdatedDate(new Date());
//						updateEserSec.add(filterSec);
//						
//						SectionDataDetails  saveSec = new  SectionDataDetails();
//						mapper.map(filterSec, saveSec)	;
//						saveSec.setQuoteNo(request.getQuoteNo());
//						saveSec.setUpdatedDate(new Date());
//						secList.add(saveSec);	
//					}
//					
//				}
//			}
			secRepo.saveAllAndFlush(secList);
			eserSecRepo.saveAllAndFlush(updateEserSec);
			res.put("Response", "Success") ;
			res.put("Errors", null) ;
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save  Section Details" ) ;
		}
		return res;
	}

	private HomePositionMaster setMotorDetails(QuoteThreadReq  request) {
		HomePositionMaster home = new HomePositionMaster();
		try {
			
			
			EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskIdOrderByRiskIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(motorData.getCustomerReferenceNo());
			home.setCustomerName(custData.getClientName());
			
			home.setCompanyId(motorData.getCompanyId());
			home.setBranchCode(motorData.getBranchCode());
			home.setProductId(Integer.valueOf(motorData.getProductId()));
			home.setSectionId(Integer.valueOf(motorData.getSectionId()));
			home.setBrokerBranchCode(motorData.getBrokerBranchCode());	
			home.setLoginId(motorData.getLoginId());
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
			
			home.setCustomerCode(motorData.getCustomerCode());
			home.setProductName(motorData.getProductName());
			home.setCompanyName(motorData.getCompanyName());
			home.setCommissionType(motorData.getCommissionType());
			home.setCommissionTypeDesc(motorData.getCommissionTypeDesc());
			home.setSubUserType(motorData.getSubUserType());		
			home.setBdmCode(motorData.getBdmCode());
			home.setSourceType(motorData.getSourceType());
			home.setApplicationId(motorData.getApplicationId());
			home.setEndtTypeId(motorData.getEndorsementType()==null?null:String.valueOf(motorData.getEndorsementType()));
			home.setEndtStatus(StringUtils.isBlank(motorData.getEndtStatus())?"":motorData.getEndtStatus());
			home.setEndtDate(motorData.getEndorsementDate()==null?null:motorData.getEndorsementDate());
			home.setEndtBy(StringUtils.isBlank(request.getCreatedBy())?"":request.getCreatedBy());
			home.setPolicyNo(motorData.getEndorsementType()==null?null:motorData.getPolicyNo());
			home.setEndtCategDesc(motorData.getEndtCategDesc()==null?null:motorData.getEndtCategDesc());
			home.setEndorsementRemarks(motorData.getEndorsementRemarks()==null?null:motorData.getEndorsementRemarks());
			home.setEndorsementEffdate(motorData.getEndorsementEffdate()==null?null:motorData.getEndorsementEffdate());
			home.setEndtPrevPolicyNo(motorData.getEndtPrevPolicyNo()==null?null:motorData.getEndtPrevPolicyNo());
			home.setEndtPrevQuoteNo(motorData.getEndtPrevQuoteNo()==null?null:motorData.getEndtPrevQuoteNo());
			home.setEndtCount(motorData.getEndtCount()==null?0:motorData.getEndtCount().intValue());	
			home.setEndtTypeDesc(motorData.getEndorsementTypeDesc()==null?"":motorData.getEndorsementTypeDesc());
			home.setOriginalPolicyNo(motorData.getOriginalPolicyNo()==null?"":motorData.getOriginalPolicyNo());
			home.setQuoteNo(request.getQuoteNo());
			home.setRequestReferenceNo(request.getRequestReferenceNo());
			
			if(StringUtils.isNotBlank(motorData.getEndorsementType()==null?null:String.valueOf(motorData.getEndorsementType()))) {
				HomePositionMaster oldPosition = homeRepo.findByQuoteNo(motorData.getEndtPrevQuoteNo()==null?null:motorData.getEndtPrevQuoteNo());
				home.setCoverNoteReferenceNo(oldPosition.getCoverNoteReferenceNo());
				home.setPrevCoverNoteRefNo(oldPosition.getCoverNoteReferenceNo());
				home.setCancelledDate(oldPosition.getExpiryDate());
			}
			
			// Copy Old Motor Doc
			List<FrameOldDocSaveReq> frameDocReqList = frameMotorDocRequest( home  ) ;
			List<ListItemValue> docTypeList = getListItem( home.getCompanyId() , home.getBranchCode() , "DOC_ID_TYPE" , "M");
			String idType = docTypeList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("M") ).collect(Collectors.toList()).get(0).getItemValue() ;	
			saveDocumentsNewQuote(home  , idType , frameDocReqList ) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return home;
	}

	
	
	private HomePositionMaster setTravelDetails(QuoteThreadReq  request) {
		HomePositionMaster home = new HomePositionMaster();
		try {
			
			
			EserviceTravelDetails  travelData = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo()) ;
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(travelData.getCustomerReferenceNo());
			home.setCustomerName(custData.getClientName());
			
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
			
			home.setCustomerCode(travelData.getCustomerCode());
			home.setProductName(travelData.getProductName());
			home.setCompanyName(travelData.getCompanyName());
			home.setCommissionType(travelData.getCommissionType());
			home.setCommissionTypeDesc(travelData.getCommissionTypeDesc());
			home.setSubUserType(travelData.getSubUserType());		
			home.setBdmCode(travelData.getBdmCode());
			home.setSourceType(travelData.getSourceType());
			home.setApplicationId(travelData.getApplicationId());		
			home.setEndtTypeId(travelData.getEndorsementType()==null?null:String.valueOf(travelData.getEndorsementType()));
			home.setEndtStatus(StringUtils.isBlank(travelData.getEndtStatus())?"":travelData.getEndtStatus());
			home.setEndtDate(travelData.getEndorsementDate()==null?null:travelData.getEndorsementDate());
			home.setEndtBy(StringUtils.isBlank(request.getCreatedBy())?"":request.getCreatedBy());
			home.setPolicyNo(travelData.getEndorsementType()==null?null:travelData.getPolicyNo());
			home.setEndtCategDesc(travelData.getEndtCategDesc()==null?null:travelData.getEndtCategDesc());
			home.setEndorsementRemarks(travelData.getEndorsementRemarks()==null?null:travelData.getEndorsementRemarks());
			home.setEndorsementEffdate(travelData.getEndorsementEffdate()==null?null:travelData.getEndorsementEffdate());
			home.setEndtPrevPolicyNo(travelData.getEndtPrevPolicyNo()==null?null:travelData.getEndtPrevPolicyNo());
			home.setEndtPrevQuoteNo(travelData.getEndtPrevQuoteNo()==null?null:travelData.getEndtPrevQuoteNo());
			home.setEndtCount(travelData.getEndtCount()==null?0:travelData.getEndtCount().intValue());	
			home.setEndtTypeDesc(travelData.getEndorsementTypeDesc()==null?"":travelData.getEndorsementTypeDesc());
			home.setOriginalPolicyNo(travelData.getOriginalPolicyNo()==null?"":travelData.getOriginalPolicyNo());
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return home;
	}
	
	private HomePositionMaster setBuildingDetails(QuoteThreadReq  request) {
		HomePositionMaster home = new HomePositionMaster();
		try {
			
			
			EserviceBuildingDetails  buildingData = eserBuildRepo.findByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() , 1) ;
			Long builCount =  eserBuildRepo.countByRequestReferenceNo(request.getRequestReferenceNo() ) ;
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(buildingData.getCustomerReferenceNo());
			home.setCustomerName(custData.getClientName());
			
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
			
			home.setCustomerCode(buildingData.getCustomerCode());
			home.setProductName(buildingData.getProductDesc());
			home.setCompanyName(buildingData.getCompanyName());
			home.setCommissionType(buildingData.getCommissionType());
			home.setCommissionTypeDesc(buildingData.getCommissionTypeDesc());
			home.setSubUserType(buildingData.getSubUserType());		
			home.setBdmCode(buildingData.getBdmCode());
			home.setSourceType(buildingData.getSourceType());
			home.setApplicationId(buildingData.getApplicationId());		
			home.setEndtTypeId(buildingData.getEndorsementType()==null?null:String.valueOf(buildingData.getEndorsementType()));
			home.setEndtStatus(StringUtils.isBlank(buildingData.getEndtStatus())?"":buildingData.getEndtStatus());
			home.setEndtDate(buildingData.getEndorsementDate()==null?null:buildingData.getEndorsementDate());
			home.setEndtBy(StringUtils.isBlank(request.getCreatedBy())?"":request.getCreatedBy());
			home.setPolicyNo(buildingData.getEndorsementType()==null?null:buildingData.getPolicyNo());
			home.setEndtCategDesc(buildingData.getEndtCategDesc()==null?null:buildingData.getEndtCategDesc());
			home.setEndorsementRemarks(buildingData.getEndorsementRemarks()==null?null:buildingData.getEndorsementRemarks());
			home.setEndorsementEffdate(buildingData.getEndorsementEffdate()==null?null:buildingData.getEndorsementEffdate());
			home.setEndtPrevPolicyNo(buildingData.getEndtPrevPolicyNo()==null?null:buildingData.getEndtPrevPolicyNo());
			home.setEndtPrevQuoteNo(buildingData.getEndtPrevQuoteNo()==null?null:buildingData.getEndtPrevQuoteNo());
			home.setEndtCount(buildingData.getEndtCount()==null?0:buildingData.getEndtCount().intValue());	
			home.setEndtTypeDesc(buildingData.getEndorsementTypeDesc()==null?"":buildingData.getEndorsementTypeDesc());
			home.setOriginalPolicyNo(buildingData.getOriginalPolicyNo()==null?"":buildingData.getOriginalPolicyNo());
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return home;
	}
	
	private HomePositionMaster setCommonDetails(QuoteThreadReq  request) {
		HomePositionMaster home = new HomePositionMaster();
		try {
			
			
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
			
			home.setCustomerCode(eserCommonData.getCustomerCode());
			home.setProductName(eserCommonData.getProductDesc());
			home.setCompanyName(eserCommonData.getCompanyName());
		//	home.setCommissionType(eserCommonData.getCommissionType());
		//	home.setCommissionTypeDesc(eserCommonData.getCommissionTypeDesc());
			home.setBdmCode(eserCommonData.getBdmCode());
			home.setSourceType(eserCommonData.getSourceType());
			home.setApplicationId(eserCommonData.getApplicationId());				
			home.setEndtTypeId(eserCommonData.getEndorsementType()==null?null:String.valueOf(eserCommonData.getEndorsementType()));
			home.setEndtStatus(StringUtils.isBlank(eserCommonData.getEndtStatus())?"":eserCommonData.getEndtStatus());
			home.setEndtDate(eserCommonData.getEndorsementDate()==null?null:eserCommonData.getEndorsementDate());
			home.setEndtBy(StringUtils.isBlank(request.getCreatedBy())?"":request.getCreatedBy());
			home.setPolicyNo(eserCommonData.getEndorsementType()==null?null:eserCommonData.getPolicyNo());
			home.setEndtCategDesc(eserCommonData.getEndtCategDesc()==null?null:eserCommonData.getEndtCategDesc());
			home.setEndorsementRemarks(eserCommonData.getEndorsementRemarks()==null?null:eserCommonData.getEndorsementRemarks());
			home.setEndorsementEffdate(eserCommonData.getEndorsementEffdate()==null?null:eserCommonData.getEndorsementEffdate());
			home.setEndtPrevPolicyNo(eserCommonData.getEndtPrevPolicyNo()==null?null:eserCommonData.getEndtPrevPolicyNo());
			home.setEndtPrevQuoteNo(eserCommonData.getEndtPrevQuoteNo()==null?null:eserCommonData.getEndtPrevQuoteNo());
			home.setEndtCount(eserCommonData.getEndtCount()==null?0:eserCommonData.getEndtCount().intValue());	
			home.setEndtTypeDesc(eserCommonData.getEndorsementTypeDesc()==null?"":eserCommonData.getEndorsementTypeDesc());
			home.setOriginalPolicyNo(eserCommonData.getOriginalPolicyNo()==null?"":eserCommonData.getOriginalPolicyNo());
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			return null ;
		}
	
		return home;
	}
	
	
	public List<FrameOldDocSaveReq> frameMotorDocRequest( HomePositionMaster homeData  ) {
		List<FrameOldDocSaveReq> reqList = new ArrayList<FrameOldDocSaveReq>();
		try {
			List<MotorDataDetails> motList = motorRepo.findByQuoteNo(homeData.getQuoteNo());
			List<SectionDataDetails> secDatas =  secRepo.findByQuoteNoOrderByRiskIdAsc(homeData.getQuoteNo());
			
			// Frame Req 
			motList.forEach(  mot -> { 
				FrameOldDocSaveReq saveReq  = new FrameOldDocSaveReq();
				List<SectionDataDetails> filterSec = secDatas.stream().filter(  o -> o.getSectionId().equalsIgnoreCase(mot.getSectionId().toString())  ).collect(Collectors.toList());
				SectionDataDetails section = filterSec.get(0) ;
				 
				saveReq.setRiskId(mot.getVehicleId());
				saveReq.setId(mot.getChassisNumber());
				saveReq.setLocationId("1");
				saveReq.setLocationName(homeData.getProductName());
				saveReq.setSectionId(section.getSectionId()) ;
				saveReq.setSectionName(section.getSectionDesc());
				reqList.add(saveReq);
				
			} ) ;
						
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	
		return reqList ;
	}
	
	
	public SuccessRes saveDocumentsNewQuote(HomePositionMaster homeData , String idType , List<FrameOldDocSaveReq> framendReqList  ) {
		// TODO Auto-generated method stub
		SuccessRes res = new SuccessRes();
		try {
			Long  docTranCount = docTranRepo.countByQuoteNo(homeData.getQuoteNo());
			
			if(docTranCount <= 0 ) {
				int targetSize = 500;
				List<FrameOldDocSaveReq> largeList = framendReqList ;
				List<List<FrameOldDocSaveReq>> partitionList = ListUtils.partition(largeList, targetSize);
				
				for (List<FrameOldDocSaveReq> partitionIds :  partitionList ) {
					
					List<String> ids = partitionIds.stream().map( FrameOldDocSaveReq :: getId ).collect(Collectors.toList());
					List<DocumentUniqueDetails> uniqueDatas = docUniqueRepo.findByIdTypeAndIdInOrderByEntryDateDesc(idType , ids);
					uniqueDatas = uniqueDatas.stream().filter(distinctByKey(o -> Arrays.asList(o.getId()))).collect(Collectors.toList());
					
					List<DocumentTransactionDetails> saveDocList = new ArrayList<DocumentTransactionDetails>();
					uniqueDatas.forEach ( uniq ->  {  
						
						DocumentTransactionDetails docTran = new DocumentTransactionDetails();
						docTran.setUniqueId( uniq.getUniqueId());
						docTran.setId(uniq.getId());
						docTran.setIdType(idType);
						docTran.setRequestReferenceNo(homeData.getRequestReferenceNo());
						docTran.setQuoteNo(homeData.getQuoteNo());
						docTran.setCompanyId(homeData.getCompanyId());
						docTran.setCompanyName(homeData.getCompanyName());
						docTran.setProductId(homeData.getProductId());
						docTran.setProductName(homeData.getProductName());
						docTran.setEntryDate(new Date());
						docTran.setCreatedBy(homeData.getLoginId());
						docTran.setStatus("Y");
						
						List<FrameOldDocSaveReq> filterPartitions = partitionIds.stream().filter(  o -> o.getId().equalsIgnoreCase(uniq.getId())  ).collect(Collectors.toList());
						if ( filterPartitions.size() > 0 ) {
							FrameOldDocSaveReq partition = filterPartitions.get(0);
							docTran.setSectionId(Integer.valueOf(partition.getSectionId()));
							docTran.setSectionName(partition.getSectionName());
							docTran.setProductType(uniq.getProductType());
							docTran.setLocationId(Integer.valueOf(partition.getLocationId()) );
							docTran.setLocationName(partition.getLocationName());
							docTran.setRiskId(Integer.valueOf(partition.getRiskId()));
						
						}
						
						if (StringUtils.isNotBlank(homeData.getEndtTypeId())) {
								docTran.setEndorsementDate(homeData.getEndtDate() == null ? null : new Date());
								docTran.setEndorsementEffdate(homeData.getEndorsementEffdate() == null ? null : homeData.getEndorsementEffdate());
								docTran.setEndorsementRemarks(homeData.getEndorsementRemarks() == null ? "" : homeData.getEndorsementRemarks());
								docTran.setEndorsementTypeDesc(homeData.getEndtTypeDesc());
								docTran.setIsFinaceYn(homeData.getIsFinacialEndt());
								docTran.setEndtCategDesc(homeData.getEndtCategDesc());
								docTran.setEndtStatus(homeData.getEndtStatus());
								docTran.setEndtCount(new BigDecimal(homeData.getEndtCount()));
								docTran.setEndtPrevPolicyNo(homeData.getEndtPrevPolicyNo());
								docTran.setEndtPrevQuoteNo(homeData.getEndtPrevQuoteNo());
							
						}
						
						saveDocList.add(docTran);
						
					} );
					
					docTranRepo.saveAllAndFlush(saveDocList);
				}
				
			} 	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	
		return res;
	}

	
	
}

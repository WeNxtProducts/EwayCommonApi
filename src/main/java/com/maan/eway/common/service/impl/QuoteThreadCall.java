package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
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

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.maan.eway.bean.LoginMaster;
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
import com.maan.eway.common.req.VehicleNeedToRemove;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndtUpdatePremiumRes;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.Data;


@Data
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
	private ProductEmployeesDetailsRepository empRepo;

	
	public QuoteThreadCall(String type , QuoteThreadReq request , EntityManager em ,EserviceCustomerDetailsRepository eserCustRepo ,
			EServiceMotorDetailsRepository eserMotRepo  ,FactorRateRequestDetailsRepository facRateRepo  ,PersonalInfoRepository perInfoRepo  , MotorDataDetailsRepository motorRepo , MotorDriverDetailsRepository driverRepo ,
			 CoverDetailsRepository coverRepo  , HomePositionMasterRepository homeRepo  ,EserviceTravelDetailsRepository eserTraRepo ,EserviceTravelGroupDetailsRepository eserGroupRepo ,
			 TravelPassengerDetailsRepository    traPassRepo ,TravelPassengerHistoryRepository traPassHisRepo  ,String travelProductId
			 , EserviceBuildingDetailsRepository eserBuildRepo , EServiceSectionDetailsRepository eserSecRepo,EserviceCommonDetailsRepository eserCommonRepo,CommonDataDetailsRepository commonDataRepo ,
			 SectionDataDetailsRepository secRepo,BuildingRiskDetailsRepository buildRepo , DocumentTransactionDetailsRepository docRepo, BuildingDetailsRepository locRepo ,ContentAndRiskRepository  contentRepo  ,ProductEmployeesDetailsRepository pacRepo
			 , DocumentUniqueDetailsRepository docUniqueRepo ,DocumentTransactionDetailsRepository docTranRepo,ProductEmployeesDetailsRepository empRepo   ) {
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
		this.empRepo = empRepo;
		
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
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdAndLocationIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getVehicleId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()),request.getLocationId());
		//	List<FactorRateRequestDetails>  defaultCovers = covers.stream().filter( o ->o.getIsSelected()!=null &&  o.getIsSelected().equalsIgnoreCase("D") && o.getDiscLoadId().equals(0)).collect(Collectors.toList() );
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) && o.getLocationId().equals(request.getLocationId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			List<FactorRateRequestDetails>  coverTaxes = new  ArrayList<FactorRateRequestDetails>();
			
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o ->  o.getVehicleId().equals(request.getVehicleId()) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
				List<FactorRateRequestDetails> filterCoverTaxes = covers.stream().filter( o ->   o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						if(filterCoverTaxes.size() > 0)coverTaxes.addAll(filterCoverTaxes);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) &&   o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
						
						List<FactorRateRequestDetails> filtersubCoverTaxes = filterNonDefaultCovers.stream().filter( o ->    o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());
						if(filtersubCoverTaxes.size() > 0)coverTaxes.addAll(filtersubCoverTaxes);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			Double taxPremium = coverTaxes.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()  ).sum();

			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumFc --> "  + premiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Vehicle :" + request.getVehicleId() + " PremiumLc --> "  + premiumLc );
			System.out.println("Vehicle :" + request.getVehicleId() + " OverAllPremiumLc --> "  + overAllPremiumLc );
			
			
			// Find Motor
			EserviceCommonDetails  eserCommonData = eserCommonRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(request.getRequestReferenceNo() ,request.getVehicleId() ,request.getSectionId(),request.getLocationId());
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
			eserCommonData.setVatPremium(new BigDecimal(df.format(taxPremium)));
			eserCommonData.setQuoteNo(request.getQuoteNo());
			eserCommonData.setCustomerId(request.getCustomerId());
			
			//Update Eservice Section Details
			EserviceSectionDetails  eSecUpdate = eserSecRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(request.getRequestReferenceNo() ,request.getVehicleId() ,request.getSectionId(),request.getLocationId());
			if(eSecUpdate!=null) {
			eSecUpdate.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			eSecUpdate.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			eserSecRepo.saveAndFlush(eSecUpdate);
			}
			// Save Details
			CommonDataDetails commonData= new CommonDataDetails();
			//MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserCommonData, commonData);
			commonData.setEntryDate(new Date());	
			commonData.setCreatedBy(request.getCreatedBy());
			commonData.setQuoteNo(request.getQuoteNo());
			commonData.setCustomerId(request.getCustomerId());
//			commonData.setRiskId(eserCommonData.getOriginalRiskId());
			commonData.setRiskId(eserCommonData.getRiskId());
			commonData.setStatus(eserCommonData.getStatus());
			commonData.setSectionDesc(eserCommonData.getSectionName());		
			commonData.setLocationId(eserCommonData.getLocationId());
			EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();
			if(eserCommonData.getEndorsementType()!=null) {
				String prevQuoteNo=eserCommonData.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				endtRes = updateEndtPremium2(request.getQuoteNo(),eserCommonData.getEndorsementEffdate(),prevQuoteNo, eserCommonData.getRiskId(),Endtcovers,Integer.valueOf(eserCommonData.getProductId()) , Integer.valueOf(eserCommonData.getSectionId()) );				
				eserCommonData.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue());
				eserCommonData.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null : endtRes.getEndtVatPremium());
				commonData.setEndtPremium(eserCommonData.getEndtPremium());
				commonData.setEndtVatPremium(eserCommonData.getEndtVatPremium());
			} 
			List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
					o.getVehicleId().equals(eserCommonData.getRiskId()) && o.getSectionId().equalsIgnoreCase(eserCommonData.getSectionId()) ).collect(Collectors.toList());
		
			if(filterCoverList.size()== 0 || filterCoverList.get(0).getCoverIdList()==null  || filterCoverList.get(0).getCoverIdList().size() == 0 ) {
				commonData.setStatus("D");
				commonData.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue() >0 ? -endtRes.getEndtPremium().doubleValue() : endtRes.getEndtPremium().doubleValue() );
				commonData.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null :  endtRes.getEndtVatPremium().doubleValue() >0 ? new BigDecimal(-endtRes.getEndtVatPremium().doubleValue()) : endtRes.getEndtVatPremium());	
				eserCommonData.setEndtPremium(commonData.getEndtPremium());
				eserCommonData.setEndtVatPremium(commonData.getEndtVatPremium());
				commonData.setActualPremiumFc(BigDecimal.ZERO);
				commonData.setActualPremiumLc(BigDecimal.ZERO);
				commonData.setOverallPremiumFc(BigDecimal.ZERO);
				commonData.setOverallPremiumLc(BigDecimal.ZERO);
				commonData.setVatPremium(BigDecimal.ZERO);
				eserCommonData.setVatPremium(BigDecimal.ZERO);
				eserCommonData.setActualPremiumFc(BigDecimal.ZERO);
				eserCommonData.setActualPremiumLc(BigDecimal.ZERO);
				eserCommonData.setOverallPremiumFc(BigDecimal.ZERO);
				eserCommonData.setOverallPremiumLc(BigDecimal.ZERO);
				
				CommonDataDetails oldRisk = commonDataRepo.findByQuoteNoAndRiskIdAndSectionIdAndLocationId(request.getEndtPrevQuoteNo() ,1 ,request.getSectionId(),request.getLocationId());
				if(oldRisk!=null ) {
					eserCommonData.setVatPremium(oldRisk.getVatPremium());
					eserCommonData.setActualPremiumFc(oldRisk.getActualPremiumFc());
					eserCommonData.setActualPremiumLc(oldRisk.getActualPremiumLc() );
					eserCommonData.setOverallPremiumFc(oldRisk.getOverallPremiumFc());
					eserCommonData.setOverallPremiumLc(oldRisk.getOverallPremiumLc());
					commonData.setVatPremium(oldRisk.getVatPremium());
					commonData.setActualPremiumFc(oldRisk.getActualPremiumFc());
					commonData.setActualPremiumLc(oldRisk.getActualPremiumLc() );
					commonData.setOverallPremiumFc(oldRisk.getOverallPremiumFc());
					commonData.setOverallPremiumLc(oldRisk.getOverallPremiumLc());
				}
				
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
				res =  copyQuoteLocationDetails( request , request.getEndtPrevQuoteNo() , request.getQuoteNo()) ;
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			res.put("Response", "Failed") ;
			res.put("Errors", "Failed To Save Common Id : " + request.getVehicleId() + " Details" ) ;
		}
	
		return res;
	}
	
	
	private BigDecimal updateEndtPremium(String quoteNo,Date effDate,String prevQuoteNo,Integer riskId, List<PolicyCoverData> covers, Integer productId , Integer sectionId ) {
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
				 totalcovers = coverRepo.findByQuoteNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(quoteNo,riskId,productId,sectionId);
				 oldcovers = coverRepo.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotAndProductIdAndSectionIdOrderByVehicleIdAsc(prevQuoteNo ,riskId,0, 0 ,"D",productId,sectionId);
			 }
			
			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxFc()!=null 
					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType()) 
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum());			 
			 Double endtChangePremium=totalcovers.stream().filter( o ->   
					   o.getPremiumIncludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
			 
			 List<PolicyCoverData>  oldcoversf=oldcovers;
			 newCovers.removeIf(p-> {
				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			 });
			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null 
					 && !"D".equals(o.getStatus())
					 && effDate.compareTo(o.getCoverPeriodFrom())>=0
					  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
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
	
	private EndtUpdatePremiumRes updateEndtPremium2(String quoteNo,Date effDate,String prevQuoteNo,Integer riskId, List<PolicyCoverData> covers, Integer productId , Integer sectionId ) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();  
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
				 totalcovers = coverRepo.findByQuoteNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(quoteNo,riskId,productId,sectionId);
				 oldcovers = coverRepo.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotAndProductIdAndSectionIdOrderByVehicleIdAsc(prevQuoteNo ,riskId,0, 0 ,"D",productId,sectionId);
			 }
			 
			 List<PolicyCoverData>  oldcoversf=oldcovers;
			
			// Premium With Tax 
			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxFc()!=null 
					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum());			 
			 
			Double endtChangePremium=totalcovers.stream().filter( o ->  o.getPremiumIncludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
			 
			 newCovers.removeIf(p-> {
				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			 });
			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null 
					 && !"D".equals(o.getStatus())
					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
				BigDecimal endtPremium= new  BigDecimal(removedCoverPremium+addedCoverPremium+endtChangePremium);
				
				
			// Premium Without Tax 
			Double removedCoverPremiumWithoutTax =  (totalcovers.stream().filter( o ->   o.getPremiumExcludedTaxFc()!=null 
					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum());			 
			
			Double endtChangePremiumWithoutTax=totalcovers.stream().filter( o ->  o.getPremiumExcludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
			 .mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			 
			 newCovers.removeIf(p-> {
				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			 });
			 Double addedCoverPremiumWithoutTax =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
					 o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null 
					 && !"D".equals(o.getStatus())
					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
				BigDecimal endtPremiumWithoutTax = new  BigDecimal(removedCoverPremiumWithoutTax+addedCoverPremiumWithoutTax+endtChangePremiumWithoutTax);
					
			
			
			// Premium Condition 
			String endtChargeOrRefund="REFUND";
		    if (endtPremium.doubleValue()<0 && endtPremiumWithoutTax.doubleValue() >0 ) {
		    	endtPremiumWithoutTax = new BigDecimal(- endtPremiumWithoutTax.doubleValue());
			} else if (endtPremium.doubleValue()==0  ) {
				endtPremiumWithoutTax = new BigDecimal("0");
			}
		 // Tax Amount 
		    List<PolicyCoverData>  endt0Covers  = totalcovers.stream().filter( o ->  o.getCoverageType().equalsIgnoreCase("E") && 
		    		(o.getPremiumIncludedTaxFc()==null || o.getPremiumIncludedTaxFc().compareTo(new BigDecimal(0)) == 0 ) ).collect(Collectors.toList());
		    List<PolicyCoverData>  endtTaxCovers  =  totalcovers.stream().filter( o -> o.getCoverageType().equalsIgnoreCase("T") && 
					o.getDiscLoadId().equals(Integer.valueOf(request.getEndtType()))  ).collect(Collectors.toList());
		    // old cover taxes
		     endtTaxCovers.removeIf(p-> {
		    	return endt0Covers.stream().anyMatch(x->  (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
		    });
		    // new cover taxes
		     List<PolicyCoverData>  newCoverTax  =  totalcovers.stream().filter( o -> o.getCoverageType().equalsIgnoreCase("T") && 
						o.getDiscLoadId().equals(0) && !o.getTaxId().equals(0) ).collect(Collectors.toList());
		     newCoverTax.removeIf(p-> {
			    	return oldcoversf.stream().anyMatch(x->  (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			  });
		    
		    
//					endt0Covers.stream().filter( o -> o.getCoverageType().equalsIgnoreCase("T") && 
//					o.getDiscLoadId().equals(Integer.valueOf(request.getEndtType())) ).collect(Collectors.toList());
			 Double endtVatPremium = endtTaxCovers.stream().filter( o -> !o.getDiscLoadId().equals(0)  &&  
					 !o.getTaxId().equals(0) && o.getTaxAmount()!=null && o.getCoverageType().equalsIgnoreCase("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()   ).sum();
			 endtVatPremium = endtVatPremium + (newCoverTax.size() >0 ? newCoverTax.stream().mapToDouble( o ->   o.getTaxAmount().doubleValue()   ).sum() :0D  ) ;
	
			// Vat Condition
			if(endtPremiumWithoutTax.doubleValue()>0 ) {
				endtChargeOrRefund="CHARGE";
			} else if (endtPremiumWithoutTax.doubleValue()<0 && endtVatPremium >0 ) {
				endtVatPremium = - endtVatPremium;
			} else if (endtPremiumWithoutTax.doubleValue()==0  ) {
				endtVatPremium = 0D;
			}
			
			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(endtPremiumWithoutTax);
			endtRes.setEndtVatPremium(new  BigDecimal(endtVatPremium));
			
			
			return endtRes;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}

	private EndtUpdatePremiumRes mainTableEndtPremium(QuoteThreadReq  request , String companyId , String currency ) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();  
		try {
			Double endtPremiumWithoutTax = 0D ;
			Double endtVatPremium = 0D ;
			if (request.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteNo());
				for (MotorDataDetails mot : motors) {
					if (StringUtils.isNotBlank(request.getEndtType())) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  mot.getEndtPremium() ==null ? 0D : mot.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  mot.getEndtVatPremium() ==null ? 0D : mot.getEndtVatPremium().doubleValue()) ;
					}
				}
				
			// Travel Product
			} else if (request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
					//List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>) v1.getRiskDetails();
				List<TravelPassengerDetails> tras = traPassRepo.findByQuoteNo(request.getQuoteNo() );
				for (TravelPassengerDetails tra : tras) {
					if (StringUtils.isNotBlank(request.getEndtType())) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  tra.getEndtPremium() ==null ? 0D : tra.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  tra.getEndtVatPremium() ==null ? 0D : tra.getEndtVatPremium().doubleValue()) ;
					}
				}
				
				
			} else if (request.getMotorYn().equalsIgnoreCase("A")) {
				List<BuildingRiskDetails> BuildingRisk = buildRepo.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(request.getQuoteNo() ,"0");
	
					// Asset
					for (BuildingRiskDetails build : BuildingRisk) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  build.getEndtPremium() ==null ? 0D : build.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  build.getEndtVatPremium() ==null ? 0D : build.getEndtVatPremium().doubleValue()) ;
					
					}
					
					// Human Included
					List<CommonDataDetails> humans = commonDataRepo.findByQuoteNo(request.getQuoteNo());
	
					for (CommonDataDetails hum : humans) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  hum.getEndtPremium() ==null ? 0D : hum.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  hum.getEndtVatPremium() ==null ? 0D : hum.getEndtVatPremium().doubleValue()) ;
					}
					
			  // Human Products		
			} else {
				List<CommonDataDetails> humans = commonDataRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteNo());
	
					for (CommonDataDetails hum : humans) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  hum.getEndtPremium() ==null ? 0D : hum.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  hum.getEndtVatPremium() ==null ? 0D : hum.getEndtVatPremium().doubleValue()) ;
					}
				
			}
			
			String endtChargeOrRefund="REFUND";
			if(endtPremiumWithoutTax.doubleValue()>=0) {
				endtChargeOrRefund="CHARGE";
			}
			
			String decimalDigits = currencyDecimalFormat(companyId , currency ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			// Update Eservice Motor
			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(new  BigDecimal(df.format(endtPremiumWithoutTax)));
			endtRes.setEndtVatPremium(new  BigDecimal(df.format(endtVatPremium)));
			
			
			return endtRes;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}
	
	private synchronized Map<String,Object> call_CustomerSave(QuoteThreadReq request) {
		Map<String,Object> res= new HashMap<String,Object>() ;
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Long findInfo =  perInfoRepo.countByCustomerId(request.getCustomerId());
	         String sectionid=request.getVehicleIdsList().get(0).getSectionId();
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
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,request.getVehicleId() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));
			
			// Insert Other Covers
			List<VehicleIdsReq> VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())).collect(Collectors.toList());
			List<CoverIdsReq> coverReqList = VehicleList.get(0).getCoverIdList();
			
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			List<FactorRateRequestDetails>  coverTaxes = new  ArrayList<FactorRateRequestDetails>();
			for ( CoverIdsReq covReq :  coverReqList) {
				 
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o ->   o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());				
				List<FactorRateRequestDetails> filterCoverTaxes = covers.stream().filter( o ->   o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());				
				
				if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
					if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
						
						premiumCovers.addAll(filterNonDefaultCovers);
						if(filterCoverTaxes.size() > 0)coverTaxes.addAll(filterCoverTaxes);
						
					}else {
						List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o ->    o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
						premiumCovers.addAll(filterNonDefaultSubCovers);
						
						List<FactorRateRequestDetails> filtersubCoverTaxes = filterNonDefaultCovers.stream().filter( o ->    o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());
						if(filtersubCoverTaxes.size() > 0)coverTaxes.addAll(filtersubCoverTaxes);
					}
				}
			}
			Double premiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			Double taxPremium = coverTaxes.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()  ).sum();
			
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
			eserMotors.setVatPremium(new BigDecimal(df.format(taxPremium)));
			eserMotors.setQuoteNo(request.getQuoteNo());
			eserMotors.setCustomerId(request.getCustomerId());
			
			
			
			
			// Save Motor Data Detais Details
			System.out.println("*************Buy policy Motor Data Details************ ");
			MotorDataDetails motorData  = new MotorDataDetails();
			dozerMapper.map(eserMotors, motorData);
			motorData.setEntryDate(new Date());	
			motorData.setCreatedBy(request.getCreatedBy());
			motorData.setQuoteNo(request.getQuoteNo());
			motorData.setCustomerId(request.getCustomerId());
			motorData.setVehicleId(eserMotors.getRiskId().toString());
			motorData.setStatus(eserMotors.getStatus());
			motorData.setLocationId(eserMotors.getLocationId()==null?1:eserMotors.getLocationId());
			motorData.setActualPremiumFc(premiumFc);
			motorData.setActualPremiumLc(premiumLc);
			motorData.setOverallPremiumFc(overAllPremiumFc);
			motorData.setOverallPremiumLc(overAllPremiumLc);
			motorData.setCdRefno(eserMotors.getCdRefno()==null?null : eserMotors.getCdRefno().toString());
			motorData.setVdRefno(eserMotors.getVdRefNo()==null?null : eserMotors.getVdRefNo().toString());
			motorData.setMsRefno(eserMotors.getMsRefno()==null?null : eserMotors.getMsRefno().toString());
			
			// Map
			ObjectMapper m = new ObjectMapper();
			Map<String,String> motorKeyValue = m.convertValue(motorData , Map.class);
			List<FactorRateRequestDetails>  overAllCovers = covers.stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) && 
					o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getProductId().equals(Integer.valueOf(request.getProductId())) && o.getTaxId().equals(0)
					&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
					
					
			for (FactorRateRequestDetails cov : overAllCovers) {
		    	List<FactorRateRequestDetails>  filterCovers = premiumCovers.stream().filter( o -> "N".equalsIgnoreCase(o.getDependentCoverYn()) && o.getVehicleId().equals(cov.getVehicleId()) &&  o.getSectionId().equals(Integer.valueOf(cov.getSectionId())) 
		    			&&  o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(cov.getSubCoverId()) &&   o.getTaxId().equals(0) &&  o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
		    	
	    		Object motorKey =  motorKeyValue.get(cov.getCoverBasedOn());
	    		
	    		if(motorKey!=null && filterCovers.size() > 0 &&  !cov.getCoverName().contains("Minimum Premium") ) {
	    			if(!cov.getCoverBasedOn().equalsIgnoreCase("suminsured") && (cov.getFreeCoverLimit()==null || cov.getFreeCoverLimit().compareTo(BigDecimal.ZERO)==0) ) {
//	    				if(cov.getCoverId().equals(42)) {// || cov.getCoverId().equals(55) ) {
//		    				// skip
//		    			} else {
	    				String keyvalue = (cov.getSumInsured() == null) ? null : 
	    	                  (cov.getSumInsured().compareTo(BigDecimal.ONE) < 0) ? "0" : 
	    	                  cov.getSumInsured().toPlainString();
                	motorKeyValue.put(cov.getCoverBasedOn(), keyvalue );
		    			   
		    	//		}
	    			}
	    			
	    		} else if (motorKey!=null && "N".equalsIgnoreCase(cov.getDependentCoverYn())  && ! cov.getCoverName().contains("Minimum Premium")   ) {
	    			if(!cov.getCoverBasedOn().equalsIgnoreCase("suminsured") && (cov.getFreeCoverLimit()==null || cov.getFreeCoverLimit().compareTo(BigDecimal.ZERO)==0) ) {
	    				motorKeyValue.put(cov.getCoverBasedOn(), null );
	    			}
	    			
		    	}
		    	
		    }
		    
		    MotorDataDetails refinedMotor = m.convertValue(motorKeyValue, MotorDataDetails.class);
		    BigDecimal exchangeRate = refinedMotor.getExchangeRate()!=null ? new BigDecimal(refinedMotor.getExchangeRate()) : BigDecimal.ZERO ;
		    refinedMotor.setSumInsuredLc(refinedMotor.getSumInsured()==null ? null : new BigDecimal(refinedMotor.getSumInsured()).multiply(exchangeRate) );
		    refinedMotor.setAcccessoriesSumInsuredLc(refinedMotor.getAcccessoriesSumInsured()==null ? null : new BigDecimal(refinedMotor.getAcccessoriesSumInsured()).multiply(exchangeRate) );
		    refinedMotor.setWindScreenSumInsuredLc(refinedMotor.getWindScreenSumInsured()==null ? null : new BigDecimal(refinedMotor.getWindScreenSumInsured()).multiply(exchangeRate) );
		    refinedMotor.setTppdIncreaeLimitLc(refinedMotor.getTppdIncreaeLimit()==null ? null : new BigDecimal(refinedMotor.getTppdIncreaeLimit()).multiply(exchangeRate) );
		    refinedMotor.setTppdFreeLimitLc(refinedMotor.getTppdFreeLimit()==null ? null : new BigDecimal(refinedMotor.getTppdFreeLimit()).multiply(exchangeRate) );
		    
			//Vehiclewise EndtPRemium
			if(eserMotors.getEndorsementType()!=null) {
				String prevQuoteNo=eserMotors.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				EndtUpdatePremiumRes endtRes = updateEndtPremium2(request.getQuoteNo(),eserMotors.getEndorsementEffdate(),prevQuoteNo, eserMotors.getRiskId(),Endtcovers,Integer.valueOf(eserMotors.getProductId()) , Integer.valueOf(eserMotors.getSectionId()));				
				eserMotors.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue());
				eserMotors.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null : endtRes.getEndtVatPremium());
				refinedMotor.setEndtPremium(eserMotors.getEndtPremium());
				refinedMotor.setEndtVatPremium(eserMotors.getEndtVatPremium());
				
				List<ContentAndRisk> con = contentRepo.findByQuoteNo(request.getQuoteNo());
				if(con.size() <= 0 ) {
					List<ContentAndRisk> con1 = contentRepo.findByQuoteNo(prevQuoteNo);
					List<ContentAndRisk> confilter = con1.stream().filter(o -> request.getVehicleId().equals(o.getRiskId())).collect(Collectors.toList());
					
					List<ContentAndRisk> saveContList  = new ArrayList<ContentAndRisk>(); 
					for(ContentAndRisk cont : confilter) {
						ContentAndRisk content = new ContentAndRisk();
						dozerMapper.map(cont, content);
						content.setCreatedBy(request.getCreatedBy());
						content.setQuoteNo(request.getQuoteNo());
						content.setRequestReferenceNo(request.getRequestReferenceNo());
						saveContList.add(content);
					}
					contentRepo.saveAllAndFlush(saveContList);
					System.out.println("*************Buy policy Content And Risk Saved************ ");
					
					
				}
				
			} 
			eserMotRepo.saveAndFlush(eserMotors);			
			motorRepo.saveAndFlush(refinedMotor);
			System.out.println("*************Buy policy Motor Data Details Saved************ ");
			   
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
				Long driverInfo = driverRepo.countByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() , request.getVehicleId());
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
					System.out.println("*************Buy policy MotorDriverDetails Saved************ ");
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);
						
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
			
			if(itemType.equalsIgnoreCase("DOC_ID_TYPE")) {
				query.where(n1,n2,n3,n8,n9,n10,n11).orderBy(orderList);
			}else {
			
				query.where(n1,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			}
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
	
	public synchronized LoginMaster getLoginDetails(String loginId ) {
		LoginMaster login = new LoginMaster();
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginMaster> query=  cb.createQuery(LoginMaster.class);
			// Find All
			Root<LoginMaster> c = query.from(LoginMaster.class);
			
			//Select
			query.select(c);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("loginId"),loginId);
			query.where(n1,n2);
			// Get Result
			TypedQuery<LoginMaster> result = em.createQuery(query);
			List<LoginMaster> list = result.getResultList();
			login = list.size() > 0 ? list.get(0) : null ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return login ;
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CurrencyMaster> ocpm1 = effectiveDate.from(CurrencyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a11 = cb.equal(c.get("currencyId"),ocpm1.get("currencyId") );
			Predicate a12 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a18 = cb.equal(c.get("status"),ocpm1.get("status") );
			Predicate a22 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			
			effectiveDate.where(a11,a12,a18,a22);
			
			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CurrencyMaster> ocpm2 = effectiveDate2.from(CurrencyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
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
			//Predicate n5 = cb.equal(c.get("companyId"),"99999");
			//Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(c.get("currencyId"),currencyId);
			query.where(n1,n2,n3,n4,n7).orderBy(orderList);
			
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
			
			Long driverInfo = driverRepo.countByRequestReferenceNoAndRiskId(request.getRequestReferenceNo() , request.getVehicleId());
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
			
			List<String> status = new ArrayList<String>();
			status.add("Y");
			status.add("E");
			
			List<EserviceSectionDetails> secs = eserSecRepo.findByRequestReferenceNoAndStatusNot(request.getRequestReferenceNo(),"D");
			List<String> secIds = new ArrayList<String>(); // secs.stream().map(EserviceSectionDetails :: getSectionId).collect(Collectors.toList());
			
			for (VehicleIdsReq veh : request.getVehicleIdsList()) {
				List<EserviceSectionDetails> filterSecId =  secs.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
				if(filterSecId.size() > 0 ) {
					List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
	    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
					if(filterCoverList.size() > 0 && filterCoverList.get(0).getCoverIdList()!=null  &&  filterCoverList.get(0).getCoverIdList().size() > 0 ) {
						secIds.add(veh.getSectionId());
					}
				}else  {
					filterSecId =  secs.stream().filter( o ->  o.getRiskId().equals(1) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
					List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
	    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
					if(filterCoverList.size() > 0 && filterCoverList.get(0).getCoverIdList()!=null  &&  filterCoverList.get(0).getCoverIdList().size() > 0 ) {
						secIds.add(veh.getSectionId());
					}
				}
				
			}
					
			// COntent And All Risk	
			Long contentCount = contentRepo.countByQuoteNo(newQuoteNo );
			
			if( contentCount <= 0  ) {
				
				
				List<ContentAndRisk> oldContentDetails = contentRepo.findByQuoteNoOrderByRiskIdAsc(oldQuoteNo );
				List<ContentAndRisk> oldfilter = oldContentDetails.stream().filter(o -> secIds.contains(o.getSectionId())).collect(Collectors.toList());	
				
				List<ContentAndRisk> saveConList = new ArrayList<ContentAndRisk>(); 
				if( oldfilter.size() > 0  ) {
					for ( ContentAndRisk con : oldfilter ) {
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
				List<ProductEmployeeDetails> oldfilter = oldPacDetails.stream().filter(o -> secIds.contains(o.getSectionId())).collect(Collectors.toList());	
				
				List<ProductEmployeeDetails> savePacList = new ArrayList<ProductEmployeeDetails>(); 
				
				if( pacCount <= 0  ) {
					for ( ProductEmployeeDetails pac : oldfilter ) {
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
			List<FactorRateRequestDetails>  covers = facRateRepo.findByRequestReferenceNoAndSectionIdAndLocationIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getSectionId()),request.getLocationId());
			List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
			List<FactorRateRequestDetails>  coverTaxes = new  ArrayList<FactorRateRequestDetails>();

			for (VehicleIdsReq veh :  request.getVehicleIdsList() ) {
				List<CoverIdsReq> coverReqList = veh.getCoverIdList().size() > 0 ? veh.getCoverIdList() : new ArrayList<CoverIdsReq>();
				for ( CoverIdsReq covReq :  coverReqList) {
					 
					List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getVehicleId().equals(veh.getVehicleId()) && 
					o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) &&  o.getIsSelected()!=null && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());				
					List<FactorRateRequestDetails> filterCoverTaxes = covers.stream().filter( o ->   o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());				
					
					if(filterNonDefaultCovers != null && filterNonDefaultCovers.size()>0 ) {
						if (covReq.getSubCoverYn().equalsIgnoreCase("N") ) {
							
							premiumCovers.addAll(filterNonDefaultCovers);
							if(filterCoverTaxes.size() > 0)coverTaxes.addAll(filterCoverTaxes);
							
						}else {
							List<FactorRateRequestDetails> filterNonDefaultSubCovers = filterNonDefaultCovers.stream().filter( o -> o.getVehicleId().equals(veh.getVehicleId()) && 
									o.getSectionId().equals(Integer.valueOf(veh.getSectionId())) &&  o.getIsSelected()!=null &&    o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
							premiumCovers.addAll(filterNonDefaultSubCovers);
							List<FactorRateRequestDetails> filtersubCoverTaxes = filterNonDefaultCovers.stream().filter( o ->    o.getCoverId().equals(covReq.getCoverId()) && o.getSubCoverId().equals(Integer.valueOf(covReq.getSubCoverId()))&& o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());
							if(filtersubCoverTaxes.size() > 0)coverTaxes.addAll(filtersubCoverTaxes);
						}
					}
				}
			}
			
			
			Double premiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0)  && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
			Double overAllPremiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0)  && o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0)  && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
			Double overAllPremiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0)  && o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
			Double taxPremium = coverTaxes.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()  ).sum();
			
			
			// Find Building
			List<EserviceBuildingDetails> eserBuild1 = eserBuildRepo.findByRequestReferenceNoAndSectionId(request.getRequestReferenceNo() ,request.getSectionId());
			for(EserviceBuildingDetails section:eserBuild1) { 
				EserviceSectionDetails  eSecUpdate = eserSecRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(request.getRequestReferenceNo() ,section.getRiskId() ,request.getSectionId(),request.getLocationId());
				EserviceBuildingDetails eserBuild = eserBuildRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(request.getRequestReferenceNo() , section.getRiskId(),request.getSectionId(),request.getLocationId());
				eserBuild.setQuoteNo(request.getQuoteNo());
				eserBuild.setCustomerId(request.getCustomerId());
			
			BuildingRiskDetails bulildDetails = new BuildingRiskDetails();
			dozerMapper.map(eserBuild,bulildDetails);
			bulildDetails.setQuoteNo(request.getQuoteNo());
			bulildDetails.setUpdatedDate(new Date());
		
			BigDecimal endtPremium = null;
			EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes(); 
			if(eserBuild.getEndorsementType()!=null) {
				String prevQuoteNo=eserBuild.getEndtPrevQuoteNo();
				List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
				if(! "0".equalsIgnoreCase(bulildDetails.getSectionId()) ) {
					endtRes = updateEndtPremium2(request.getQuoteNo(),eserBuild.getEndorsementEffdate(),prevQuoteNo, eserBuild.getRiskId(),Endtcovers,Integer.valueOf(bulildDetails.getProductId()) , Integer.valueOf(bulildDetails.getSectionId()));				
					eserBuild.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue());
					eserBuild.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null : endtRes.getEndtVatPremium());
					bulildDetails.setEndtPremium(eserBuild.getEndtPremium());
					bulildDetails.setEndtVatPremium(eserBuild.getEndtVatPremium());	
				}
				
			}
			// Map
			if(premiumCovers.size()> 0 ) {
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
				eserBuild.setVatPremium(new BigDecimal(df.format(taxPremium)));
				
				// Main Table
				bulildDetails.setActualPremiumFc(new BigDecimal(df.format(premiumFc)));
				bulildDetails.setActualPremiumLc(new BigDecimal(df.format(premiumLc)));
				bulildDetails.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
				bulildDetails.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
				bulildDetails.setVatPremium(new BigDecimal(df.format(taxPremium)));
				
				//Update Eservice Section Details
				if(eSecUpdate!=null) {
				eSecUpdate.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
				eSecUpdate.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
				eserSecRepo.saveAndFlush(eSecUpdate);
				}
				ObjectMapper m = new ObjectMapper();
				// Set Empty Un opted Covers
				Map<String,String> assetKeyValue = m.convertValue(bulildDetails , Map.class);
				List<FactorRateRequestDetails>  overAllCovers = covers.stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) && 
						o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getProductId().equals(Integer.valueOf(request.getProductId())) && o.getTaxId().equals(0)
						&& o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
				
				for (FactorRateRequestDetails cov : overAllCovers ) {
			    	List<FactorRateRequestDetails>  filterCovers = premiumCovers.stream().filter( o -> "N".equalsIgnoreCase(o.getDependentCoverYn()) && o.getVehicleId().equals(cov.getVehicleId()) &&  o.getSectionId().equals(Integer.valueOf(cov.getSectionId())) 
			    			&&  o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(cov.getSubCoverId()) &&   o.getTaxId().equals(0) &&  o.getDiscLoadId().equals(0) ).collect(Collectors.toList());
			    	
		    		Object assetKey =  assetKeyValue.get(cov.getCoverBasedOn());
		    		if(assetKey!=null && filterCovers.size() > 0 && ! cov.getCoverName().contains("Minimum Premium") && (cov.getFreeCoverLimit()==null || cov.getFreeCoverLimit().compareTo(BigDecimal.ZERO)==0) ) {
		    			assetKeyValue.put(cov.getCoverBasedOn(),  cov.getSumInsured()==null ?  null : cov.getSumInsured().toPlainString());	
				    
		    		}	else if (assetKey!=null && "N".equalsIgnoreCase(cov.getDependentCoverYn())  && ! cov.getCoverName().contains("Minimum Premium") && (cov.getFreeCoverLimit()==null || cov.getFreeCoverLimit().compareTo(BigDecimal.ZERO)==0)  ) {
		    			assetKeyValue.put(cov.getCoverBasedOn(),  null );
			    	}
			    	
			    }
			    
				BuildingRiskDetails refinedBuilding = m.convertValue(assetKeyValue, BuildingRiskDetails.class);
			    BigDecimal exchangeRate = refinedBuilding.getExchangeRate()!=null ? refinedBuilding.getExchangeRate() : BigDecimal.ONE ;
			    refinedBuilding.setAllRiskSumInsuredLC(refinedBuilding.getAllriskSuminsured()==null ? null : refinedBuilding.getAllriskSuminsured().multiply(exchangeRate) );
				refinedBuilding.setMiningPlantSiLC(refinedBuilding.getMiningPlantSi()==null ? null : refinedBuilding.getMiningPlantSi().multiply(exchangeRate) );
				refinedBuilding.setGensetsSiLC(refinedBuilding.getGensetsSi()==null ? null : refinedBuilding.getGensetsSi().multiply(exchangeRate) );
				refinedBuilding.setNonMiningPlantSiLC(refinedBuilding.getNonminingPlantSi()==null ? null : refinedBuilding.getNonminingPlantSi().multiply(exchangeRate) );
				refinedBuilding.setEquipmentSiLC(refinedBuilding.getEquipmentSi()==null ? null : refinedBuilding.getEquipmentSi().multiply(exchangeRate) );
				refinedBuilding.setStockInTradeSiLC(refinedBuilding.getStockInTradeSi()==null ? null : refinedBuilding.getStockInTradeSi().multiply(exchangeRate) );
				refinedBuilding.setGoodsSiLC(refinedBuilding.getGoodsSi()==null ? null : refinedBuilding.getGoodsSi().multiply(exchangeRate) );
				refinedBuilding.setFurnitureSiLC(refinedBuilding.getFurnitureSi()==null ? null : refinedBuilding.getFurnitureSi().multiply(exchangeRate) );
				refinedBuilding.setApplianceSiLC(refinedBuilding.getApplianceSi()==null ? null : refinedBuilding.getApplianceSi().multiply(exchangeRate) );
				refinedBuilding.setCashValuablesSiLC(refinedBuilding.getCashValueablesSi()==null ? null : refinedBuilding.getCashValueablesSi().multiply(exchangeRate) );	
				refinedBuilding.setBuildingSumInsuredLC(refinedBuilding.getBuildingSumInsuredLC()==null ? null : refinedBuilding.getBuildingSumInsuredLC().multiply(exchangeRate) );
				refinedBuilding.setFirePlantSiLc(refinedBuilding.getFirePlantSi()==null ? null : refinedBuilding.getFirePlantSi().multiply(exchangeRate) );
				refinedBuilding.setStockInTradeSiLC(refinedBuilding.getStockInTradeSi()==null ? null : refinedBuilding.getStockInTradeSi().multiply(exchangeRate) );
				refinedBuilding.setBuildingSuminsured(refinedBuilding.getBuildingSumInsuredLC()==null ? null : refinedBuilding.getBuildingSumInsuredLC().multiply(exchangeRate) );
				refinedBuilding.setEquipmentSiLC(refinedBuilding.getEquipmentSi()==null ? null : refinedBuilding.getEquipmentSi().multiply(exchangeRate) );
				refinedBuilding.setBoilerPlantsSiLC(refinedBuilding.getBoilerPlantsSi()==null ? null : refinedBuilding.getBoilerPlantsSi().multiply(exchangeRate) );
				refinedBuilding.setElecMachinesSiLC(refinedBuilding.getElecMachinesSi()==null ? null : refinedBuilding.getElecMachinesSi().multiply(exchangeRate) );
				refinedBuilding.setEquipmentSiLC(refinedBuilding.getEquipmentSi()==null ? null : refinedBuilding.getEquipmentSi().multiply(exchangeRate) );
				refinedBuilding.setGeneralMachineSiLC(refinedBuilding.getGeneralMachineSi()==null ? null : refinedBuilding.getGeneralMachineSi().multiply(exchangeRate) );
				refinedBuilding.setMachineEquipSiLC(refinedBuilding.getMachineEquipSi()==null ? null : refinedBuilding.getMachineEquipSi().multiply(exchangeRate) );
				refinedBuilding.setManuUnitsSiLC(refinedBuilding.getManuUnitsSi()==null ? null : refinedBuilding.getManuUnitsSi().multiply(exchangeRate) );
				refinedBuilding.setPowerPlantSiLC(refinedBuilding.getPowerPlantSi()==null ? null : refinedBuilding.getPowerPlantSi().multiply(exchangeRate) );
				refinedBuilding.setPlateGlassSiLC(refinedBuilding.getPlateGlassSi()==null ? null : refinedBuilding.getPlateGlassSi().multiply(exchangeRate) );
				refinedBuilding.setContentSumInsuredLC(refinedBuilding.getContentSumInsuredLC()==null ? null : refinedBuilding.getContentSumInsuredLC().multiply(exchangeRate) );
				refinedBuilding.setElecEquipSumInsuredLC(refinedBuilding.getElecEquipSuminsured()==null ? null : refinedBuilding.getElecEquipSuminsured().multiply(exchangeRate) );
	
				// New Input
				refinedBuilding.setEquipmentSiLC(refinedBuilding.getEquipmentSi()==null ? null : refinedBuilding.getEquipmentSi().multiply(exchangeRate) );
				refinedBuilding.setJewellerySiLc(refinedBuilding.getJewellerySi()==null ? null : refinedBuilding.getJewellerySi().multiply(exchangeRate) );
				refinedBuilding.setPaitingsSiLc(refinedBuilding.getPaitingsSi()==null ? null : refinedBuilding.getPaitingsSi().multiply(exchangeRate) );
				refinedBuilding.setCarpetsSiLc(refinedBuilding.getCarpetsSi()==null ? null : refinedBuilding.getCarpetsSi().multiply(exchangeRate) );
				refinedBuilding.setWaterTankSiLc(refinedBuilding.getWaterTankSi()==null ? null : refinedBuilding.getWaterTankSi().multiply(exchangeRate) );
				refinedBuilding.setLossOfRentSiLc(refinedBuilding.getLossOfRentSi()==null ? null : refinedBuilding.getLossOfRentSi().multiply(exchangeRate) );
				refinedBuilding.setArchitectsSiLc(refinedBuilding.getArchitectsSi()==null ? null : refinedBuilding.getArchitectsSi().multiply(exchangeRate) );
				
				
				// Burglary First Loss Percent
				refinedBuilding.setApplianceLossPercent(refinedBuilding.getApplianceSi()!=null ? refinedBuilding.getApplianceLossPercent() : null ) ;
				refinedBuilding.setCashValueablesLossPercent(refinedBuilding.getCashValueablesSi()!=null ? refinedBuilding.getCashValueablesLossPercent() : null );
				refinedBuilding.setFurnitureLossPercent(refinedBuilding.getFurnitureSi()!=null ? refinedBuilding.getFurnitureLossPercent() : null );
				refinedBuilding.setGoodsLossPercent(refinedBuilding.getGoodsSi()!=null ? refinedBuilding.getGoodsLossPercent() : null );
				refinedBuilding.setStockLossPercent(refinedBuilding.getStockInTradeSi()!=null ? refinedBuilding.getStockLossPercent() : null );;
				
				buildRepo.saveAndFlush(refinedBuilding);
				
			} else {
				bulildDetails.setStatus(bulildDetails.getSectionId().equalsIgnoreCase("0") ?  bulildDetails.getStatus() : "D" );
				if( bulildDetails.getStatus().equalsIgnoreCase("D") ) {
					bulildDetails.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue() >0 ? -endtRes.getEndtPremium().doubleValue() : endtRes.getEndtPremium().doubleValue() );
					bulildDetails.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null :  endtRes.getEndtVatPremium().doubleValue() >0 ? new BigDecimal(-endtRes.getEndtVatPremium().doubleValue()) : endtRes.getEndtVatPremium());	
					eserBuild.setEndtPremium(bulildDetails.getEndtPremium());
					eserBuild.setEndtVatPremium(bulildDetails.getEndtVatPremium());
					BuildingRiskDetails oldRisk = buildRepo.findByQuoteNoAndRiskIdAndSectionId(request.getEndtPrevQuoteNo() ,1 ,request.getSectionId());
					if(oldRisk!=null ) {
						bulildDetails.setVatPremium(oldRisk.getVatPremium());
						bulildDetails.setActualPremiumFc(oldRisk.getActualPremiumFc());
						bulildDetails.setActualPremiumLc(oldRisk.getActualPremiumLc() );
						bulildDetails.setOverallPremiumFc(oldRisk.getOverallPremiumFc());
						bulildDetails.setOverallPremiumLc(oldRisk.getOverallPremiumLc());
					} else {
						bulildDetails.setActualPremiumFc(BigDecimal.ZERO);
						bulildDetails.setActualPremiumLc(BigDecimal.ZERO);
						bulildDetails.setOverallPremiumFc(BigDecimal.ZERO);
						bulildDetails.setOverallPremiumLc(BigDecimal.ZERO);
						bulildDetails.setVatPremium(BigDecimal.ZERO);
						
					}
					
					
				}
			
				buildRepo.saveAndFlush(bulildDetails);
			}
			
		    eserBuildRepo.saveAndFlush(eserBuild);
		    
		}
			
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
			res.put("Errors", "Failed To Save Section Id : " + request.getSectionId() + " Details" ) ;
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
			Double taxPremium = 0D ;
			
			Double groupPremiumFc = 0D ;					
			Double groupOverAllPremiumFc = 0D ;
			Double groupPremiumLc = 0D ;					
			Double groupOverAllPremiumLc = 0D ;
			
			
			
			// Update Eservice Travel
			EserviceTravelDetails eserTravel = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo() );
			eserTravel.setQuoteNo(request.getQuoteNo());
			eserTravel.setPolicyNo(eserTravel.getPolicyNo());
			eserTraRepo.saveAndFlush(eserTravel);
			
			EserviceTravelGroupDetails groupData = eserGroupRepo.findByRequestReferenceNoAndGroupId(request.getRequestReferenceNo() , request.getGroupId());
			List<FactorRateRequestDetails>  covers = new ArrayList<FactorRateRequestDetails>();
			
			if ( eserTravel.getPlanTypeId().equals(3) && request.getGroupId().equals(1) ) {
				covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));
			} else {
				
				covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdOrderByVehicleIdAsc(request.getRequestReferenceNo()  ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()));		
				
				List<FactorRateRequestDetails>  premiumCovers = new  ArrayList<FactorRateRequestDetails>();
				List<FactorRateRequestDetails> filterNonDefaultCovers = covers.stream().filter( o -> o.getUserOpt()!=null && o.getUserOpt().equals("Y") && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
				premiumCovers.addAll(filterNonDefaultCovers);
				
				List<FactorRateRequestDetails>  coverTaxes = new  ArrayList<FactorRateRequestDetails>();
				List<FactorRateRequestDetails> filterCoverTaxes = covers.stream().filter( o ->   o.getDiscLoadId().equals(0) && o.getCoverageType().equalsIgnoreCase("T") ).collect(Collectors.toList());					
				if(filterCoverTaxes.size() > 0)coverTaxes.addAll(filterCoverTaxes);
				
				premiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
				overAllPremiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
				premiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
				overAllPremiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
				taxPremium = coverTaxes.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()  ).sum();

				groupPremiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getVehicleId().equals(groupData.getGroupId()) && o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()  ).sum();					
				groupOverAllPremiumFc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()  ).sum();
				groupPremiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()  ).sum();					
				groupOverAllPremiumLc = premiumCovers.stream().filter( o -> o.getTaxId().equals(0) && o.getVehicleId().equals(groupData.getGroupId()) &&  o.getDiscLoadId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D ).mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()  ).sum();
				
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
			eserTravel.setVatPremium(new BigDecimal(df.format(taxPremium)));
			eserTravel.setQuoteNo(request.getQuoteNo());
			eserTravel.setCustomerId(request.getCustomerId());
			
			groupData.setActualPremiumFc(groupPremiumFc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupPremiumFc)));
			groupData.setActualPremiumLc(groupPremiumLc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupPremiumLc)));
			groupData.setOverallPremiumFc(groupOverAllPremiumFc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupOverAllPremiumFc)));
			groupData.setOverallPremiumLc(groupOverAllPremiumLc  == 0D ? new BigDecimal(0) : new BigDecimal(df.format(groupOverAllPremiumLc)));
			groupData.setQuoteNo(request.getQuoteNo());
			groupData.setCustomerId(request.getCustomerId());
			groupData.setSectionId(	Integer.valueOf(request.getSectionId()));
			
			eserGroupRepo.saveAndFlush(groupData);
			List<EserviceTravelGroupDetails> groupDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(request.getRequestReferenceNo() );
			groupDatas.forEach( o -> o.setQuoteNo(request.getQuoteNo()));
			eserGroupRepo.saveAllAndFlush(groupDatas);
			
//			// Save Travel Details
			Long passengerCount = traPassRepo.countByQuoteNo(request.getQuoteNo());
			List<TravelPassengerDetails> saveList = new ArrayList<TravelPassengerDetails>(); 

				if(eserTravel.getEndorsementType()!=null && passengerCount <= 0) {
					String prevQuoteNo=eserTravel.getEndtPrevQuoteNo();
					List<PolicyCoverData>  Endtcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdOrderByVehicleIdAsc(request.getQuoteNo() ,0, 0);
					EndtUpdatePremiumRes endtRes = updateEndtPremium2(request.getQuoteNo(),eserTravel.getEndorsementEffdate(),prevQuoteNo, 0,Endtcovers,Integer.valueOf(eserTravel.getProductId()) , Integer.valueOf(eserTravel.getSectionId()));				
					eserTravel.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue());
					eserTravel.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null : endtRes.getEndtVatPremium());
					
					// Copy Previuos Data 
					if( passengerCount <= 0) {
						List<TravelPassengerDetails> passengerDatas = traPassRepo.findByQuoteNoAndStatusNotAndSectionIdAndProductId(prevQuoteNo,"D" , Integer.valueOf(eserTravel.getSectionId()), Integer.valueOf(eserTravel.getProductId()) );
						passengerDatas.forEach( o ->  {
							TravelPassengerDetails saveNew = new TravelPassengerDetails();
							dozerMapper.map(o, saveNew);
							o.setQuoteNo(request.getQuoteNo());
							o.setSectionId(null);
							o.setSectionName(prevQuoteNo);
							o.setPobox(prevQuoteNo);
							o.setOriginalPolicyNo(eserTravel.getOriginalPolicyNo());
							 o.setEndorsementDate(eserTravel.getEndorsementDate());
							 o.setEndorsementRemarks(eserTravel.getEndorsementRemarks());
							 o.setEndorsementEffdate(eserTravel.getEndorsementEffdate());
							 o.setEndtPrevPolicyNo(eserTravel.getEndtPrevPolicyNo());
							 o.setEndtPrevQuoteNo(eserTravel.getEndtPrevQuoteNo());
							 o.setEndtCount(eserTravel.getEndtCount());
							 o.setEndtStatus(eserTravel.getEndtStatus());
							 o.setIsFinaceYn(eserTravel.getIsFinaceYn());
							 o.setEndtCategDesc(eserTravel.getEndtCategDesc());
							 o.setEndorsementType(eserTravel.getEndorsementType());
							 o.setEndorsementTypeDesc(eserTravel.getEndorsementTypeDesc());  
							 o.setPolicyNo(eserTravel.getPolicyNo());
							 //o.setEndtPremium(eserTravel.getEndtPremium());
							 saveList.add(saveNew);
						});
						
					}
			}
			traPassRepo.saveAllAndFlush(saveList);
			eserTraRepo.saveAndFlush(eserTravel);
			
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
			

			List<FactorRateRequestDetails> covers = facRateRepo.findByRequestReferenceNoAndProductIdAndSectionIdAndVehicleIdAndLocationIdOrderByVehicleIdAsc(request.getRequestReferenceNo() ,Integer.valueOf(request.getProductId()) ,Integer.valueOf(request.getSectionId()) , request.getVehicleId(),request.getLocationId());
				if(covers.size() > 0 ) {
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
			if(request.getVehicleId()!= null && request.getVehicleId().equals(99999) ) {
				
				List<FactorRateRequestDetails> fleetCovers = covers.stream().filter( o -> o.getVehicleId().equals(99999)  && o.getSectionId().equals(99999)).collect(Collectors.toList());
				
				for (FactorRateRequestDetails f:fleetCovers) {
				Integer coverId = f.getCoverId()!=null ? f.getCoverId() : 99999 ;   
				Integer subCoverId = f.getSubCoverId()!=null ? f.getSubCoverId() : 0;
				
				CoverIdsReq coverReq = new CoverIdsReq();
				coverReq.setCoverId(coverId);
				coverReq.setSubCoverYn("N");
				coverReq.setSubCoverId(subCoverId.toString() );
				
				coverReqList.add(coverReq);
				}
				
			} else if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList =  VehicleList.get(0).getCoverIdList();
				
			} else if ( request.getMotorYn().equalsIgnoreCase("M") ) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId()) ). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A") ) {
				
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getVehicleId())   &&  o.getSectionId().equalsIgnoreCase(request.getSectionId()) &&  o.getLocationId().equals(request.getLocationId())). collect(Collectors.toList());
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
						
			List<VehicleIdsReq> VehicleList = new ArrayList<VehicleIdsReq>();
			List<CoverIdsReq> coverReqList =new ArrayList<CoverIdsReq>();
			
			// Insert Other Covers
			if(request.getVehicleId()!= null && request.getVehicleId().equals(99999) ) {
				
				List<FactorRateRequestDetails> fleetCovers = covers.stream().filter( o -> o.getVehicleId().equals(99999)  && o.getSectionId().equals(99999)).collect(Collectors.toList());
				Integer coverId = fleetCovers.size() > 0 ? fleetCovers.get(0).getCoverId() : 99999 ;   
				Integer subCoverId = fleetCovers.size() > 0 ? fleetCovers.get(0).getSubCoverId() : 0;
				
				CoverIdsReq coverReq = new CoverIdsReq();
				coverReq.setCoverId(coverId);
				coverReq.setSubCoverYn("N");
				coverReq.setSubCoverId(subCoverId.toString() );
				
				coverReqList.add(coverReq);
				
			} else if(request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
					VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
					coverReqList = VehicleList.get(0).getCoverIdList();
					
			}else if ( request.getMotorYn().equalsIgnoreCase("M")) {
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())). collect(Collectors.toList());
				coverReqList = VehicleList.get(0).getCoverIdList();
				
			} else if(request.getMotorYn().equalsIgnoreCase("A")) {
				
				VehicleList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId())   &&  o.getSectionId().equalsIgnoreCase(request.getSectionId())&&  o.getLocationId().equals(request.getLocationId())). collect(Collectors.toList());
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
					coverData.setLocationId(request.getLocationId()==null?1:request.getLocationId());					
					coverData.setQuoteNo(request.getQuoteNo());
					coverData.setIsSelected(cov.getIsSelected().equalsIgnoreCase("N") ? "Y" :cov.getIsSelected());
					coverData.setCreatedBy(request.getCreatedBy());
					coverData.setVehicleId(request.getVehicleId());
					coverData.setDiscountCoverId(cov.getDiscountCoverId()==null?0 :cov.getDiscountCoverId());
					coverData.setIndividualId(request.getVehicleId());
					coverData.setOriginalPolicyNo(request.getOriginalPolicyNo());
					// Period End Condition
					if(cov.getCoverPeriodTo()!=null ) {
						Calendar cal = new GregorianCalendar(); 
						cal.setTime(cov.getCoverPeriodTo());
						cal.set(Calendar.HOUR_OF_DAY, 23);
						cal.set(Calendar.MINUTE, 59);
						cal.set(Calendar.SECOND,59);
						Date endDate = cal.getTime();
						coverData.setCoverPeriodTo(endDate);
					}
					saveCovers.add(coverData);
					
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
					if( StringUtils.isNotBlank(request.getEndtFields())  && ( request.getEndtFields ().equalsIgnoreCase("Y") ) ) {
							endtCovModify = true  ;
					}
					
					// Filter Old Cover
					if( cov.getSubCoverYn() ==null || cov.getSubCoverYn().equalsIgnoreCase("N") ) {
						List<PolicyCoverData> filterOldCover =  OldPolicyCovers.stream().filter(  o -> o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId()) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
									&& o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) ).collect(Collectors.toList());	            			
						
						if(filterOldCover.size() > 0 ) {
							PolicyCoverData oldCoverData = filterOldCover.get(0) ;
							periodStart =  effDate ;//oldCoverData.getCoverPeriodFrom().before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : oldCoverData.getCoverPeriodFrom();
							//periodEnd   = oldCoverData.getCoverPeriodTo().before(request.getPolicyEndDate()) ? oldCoverData.getCoverPeriodTo() : request.getPolicyEndDate()  ;
							SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy"); 
							String end1 = oldCoverData.getCoverPeriodTo() !=null ? sdf2.format(oldCoverData.getCoverPeriodTo()) : "";
							String end2 = request.getPolicyEndDate() !=null ?  sdf2.format(request.getPolicyEndDate()) : "";
							
							if( end1.equalsIgnoreCase(end2) ) {
								alreadyOptCover = true ;
							} else {
								alreadyOptCover = false ;
							}
							
							
						} else {
							periodStart = effDate;// effDate.before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : effDate;
							//periodEnd   = cov.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : cov.getCoverPeriodTo();
						}
					
					} else {
        				List<PolicyCoverData> filterOldSubCover =  OldPolicyCovers.stream().filter(  o ->  o.getVehicleId().equals(request.getGroupId()==null?request.getVehicleId() :request.getGroupId()) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
									&& o.getSectionId().equals(Integer.valueOf(request.getSectionId())) && o.getCoverId().equals(cov.getCoverId()) &&  o.getSubCoverId().equals(Integer.valueOf(cov.getSubCoverId()))   ).collect(Collectors.toList());
						
        				if(filterOldSubCover.size() > 0 ) {
        					PolicyCoverData oldSubCoverData = filterOldSubCover.get(0) ;
							periodStart = effDate ;//oldSubCoverData.getCoverPeriodFrom().before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : oldSubCoverData.getCoverPeriodFrom();
							//periodEnd   = oldSubCoverData.getCoverPeriodTo().before(request.getPolicyEndDate()) ? oldSubCoverData.getCoverPeriodTo() : request.getPolicyEndDate()  ;
							SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy"); 
							String end1 = oldSubCoverData.getCoverPeriodTo() !=null ? sdf2.format(oldSubCoverData.getCoverPeriodTo()) : "";
							String end2 = request.getPolicyEndDate() !=null ?  sdf2.format(request.getPolicyEndDate()) : "";
							
							if( end1.equalsIgnoreCase(end2) ) {
								alreadyOptCover = true ;
							} else {
								alreadyOptCover = false ;
							}
							
						} else {
							periodStart = effDate ;// effDate.before(request.getPolicyStartDate()) ? request.getPolicyStartDate() : effDate;
							//periodEnd   = cov.getCoverPeriodTo().before(request.getPolicyEndDate()) ? request.getPolicyEndDate() : cov.getCoverPeriodTo();
						}
						
        			}
					
					
					Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)  + 1 ;
					// Check Leap Year
					boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					System.out.println( "Policy Opted Cover :  "+ coverData.getCoverDesc() + " Difference in days: " + diff);
					
					coverData.setCoverPeriodFrom(periodStart);
					
					// Period End Condition
					Calendar cal = new GregorianCalendar(); 
					cal.setTime(periodEnd);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 59);
					cal.set(Calendar.SECOND, 59);
					periodEnd = cal.getTime();
					coverData.setCoverPeriodTo(periodEnd);
					coverData.setNoOfDays(new BigDecimal(diff));
					coverData.setStatus("Y");
					
					boolean isFinYn = request.getIsFinYn().equalsIgnoreCase("Y") ? true : false ;
					// Premium
					if( (isFinYn==false || endtCovModify == true)  && alreadyOptCover==true && ( cov.getCoverageType().equalsIgnoreCase("E") || cov.getCoverageType().equalsIgnoreCase("T") && cov.getDiscLoadId() > 0 ) ) {
				//	if(endtCovModify == true && alreadyOptCover==true && ( cov.getCoverageType().equalsIgnoreCase("E") || cov.getCoverageType().equalsIgnoreCase("T") && cov.getDiscLoadId() > 0 ) ) {
					
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
					coverData.setLocationId(request.getLocationId()==null?1:request.getLocationId());
					coverData.setDiscountCoverId(cov.getDiscountCoverId()==null?0 :cov.getDiscountCoverId());
					coverData.setIndividualId(request.getVehicleId());
					coverData.setCoverPeriodFrom(effDate);
					
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
				req.setVehicleNeedberemove(new ArrayList<VehicleNeedToRemove>());
				
				// Delete Cover Table
				 res = deleteCoverRecords(req);
				
	 			// Section
				res = deleteSectionRecords(req);
	 			
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
				
				
				// Common Doc
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo()) ) {
					// Copy Quote Doc
					res = copyDocumentRecords(req);
					
					// Find Traces
					Long dupQuoteCount = homeRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , Integer.valueOf(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						homeRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , Integer.valueOf(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
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
					List<EserviceMotorDetails> eserMotors = eserMotRepo.findByRequestReferenceNoAndStatusOrderByRiskIdAsc(request.getRequestReferenceNo() ,"D");
						
					// Copy Quote Doc
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

						
			
						EndtUpdatePremiumRes endtRes = updateEndtPremium2(request.getQuoteNo(),effDate,ref.getEndtPrevQuoteNo(),ref.getRiskId() ,Endtcovers,Integer.valueOf(motorData.getProductId()) , Integer.valueOf(motorData.getSectionId()));
						motorData.setEndtPremium(endtRes.getEndtPremium()==null ? null : endtRes.getEndtPremium().doubleValue() >0 ? -endtRes.getEndtPremium().doubleValue() : endtRes.getEndtPremium().doubleValue() );
						motorData.setEndtVatPremium(endtRes.getEndtVatPremium()==null ? null :  endtRes.getEndtVatPremium().doubleValue() >0 ? new BigDecimal(-endtRes.getEndtVatPremium().doubleValue()) : endtRes.getEndtVatPremium());	
						
						ref.setEndtPremium(motorData.getEndtPremium());
						ref.setEndtVatPremium(motorData.getEndtVatPremium());
						motorData.setActualPremiumFc(old.getActualPremiumFc()!=null  ? old.getActualPremiumFc() : 0D);
						motorData.setActualPremiumLc(old.getActualPremiumLc()!=null  ?old.getActualPremiumLc(): 0D);
						motorData.setOverallPremiumFc(old.getOverallPremiumFc()!=null  ?old.getOverallPremiumFc(): 0D);
						motorData.setOverallPremiumLc(old.getOverallPremiumLc()!=null  ?old.getOverallPremiumLc(): 0D);
						motorData.setVatPremium(old.getVatPremium()!=null  ? old.getVatPremium():  new BigDecimal(0));
//						ref.setVatPremium(BigDecimal.ZERO);
//						ref.setActualPremiumFc(BigDecimal.ZERO);
//						ref.setActualPremiumLc(BigDecimal.ZERO);
//						ref.setOverallPremiumFc(BigDecimal.ZERO);
//						ref.setOverallPremiumLc(BigDecimal.ZERO);
						ref.setVatPremium(motorData.getVatPremium()!=null  ? motorData.getVatPremium() : new BigDecimal(0) );
						ref.setActualPremiumFc(motorData.getActualPremiumFc()!=null  ? new BigDecimal(motorData.getActualPremiumFc()) : new BigDecimal(0) );
						ref.setActualPremiumLc(motorData.getActualPremiumLc()!=null  ? new BigDecimal(motorData.getActualPremiumLc()) : new BigDecimal(0) );
						ref.setOverallPremiumFc(motorData.getOverallPremiumFc()!=null? new BigDecimal( motorData.getOverallPremiumFc()) : new BigDecimal(0) );
						ref.setOverallPremiumLc(motorData.getOverallPremiumLc()!=null ? new BigDecimal(motorData.getOverallPremiumLc()) : new BigDecimal(0) );
						motorDatas.add(motorData);
						
					}) ;
					
				
					motorRepo.saveAllAndFlush(motorDatas);
					eserMotRepo.saveAll(eserMotors);
					List<VehicleNeedToRemove> vehicleNeedberemove = new ArrayList<VehicleNeedToRemove>();
					eserMotors.forEach( o -> {
						VehicleNeedToRemove removeVehicle = new VehicleNeedToRemove(); 
						removeVehicle.setSectionId(o.getSectionId());
						removeVehicle.setVehicleId(Integer.valueOf(o.getRiskId()));
						vehicleNeedberemove.add(removeVehicle);
					});
					
					req.setVehicleNeedberemove(vehicleNeedberemove);
					
					// Find Traces 
					Long dupQuoteCount = motorRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						motorRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
					
					dupQuoteCount = driverRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						driverRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
					
					// Remove Dup Traces
					dupQuoteCount = contentRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						contentRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
					
				}
				//Doc traces delete 
				List<EserviceSectionDetails> secs = eserSecRepo.findByRequestReferenceNoAndStatus(req.getRequestReferenceNo(),"Y");
				
				List<String> secIds = secs.stream().map(EserviceSectionDetails :: getSectionId).collect(Collectors.toList());
				
			
				List<DocumentTransactionDetails> doc = docRepo.findByQuoteNo(req.getQuoteNo());
				
				if (secIds.size()>0) {
					
					//unmatched based on sectionid
				
					List<DocumentTransactionDetails> docfilter = doc.stream().filter(o -> o.getLocationId()!=99999 && ! secIds.contains(o.getSectionId().toString())).collect(Collectors.toList());	
					docRepo.deleteAll(docfilter);
				}
				List<EserviceMotorDetails> eserMotors = eserMotRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(request.getRequestReferenceNo() ,"D");
				List<String> riskIds = new ArrayList<>();
				List<String> accRiskIds = new ArrayList<>();
				eserMotors.forEach(  o -> {
					riskIds.add(String.valueOf(o.getRiskId()));
					if(o.getAcccessoriesSumInsured() !=null && o.getAcccessoriesSumInsured().compareTo(BigDecimal.ZERO) > 0 ) {
						accRiskIds.add(String.valueOf(o.getRiskId()));
					}
							 
					} ) ;
			
				// COntent
				List<ContentAndRisk> con = contentRepo.findByQuoteNo(req.getQuoteNo());
				List<ContentAndRisk> confilter = con.stream().filter(o -> ! accRiskIds.contains(String.valueOf(o.getRiskId()))).collect(Collectors.toList());	
				contentRepo.deleteAll(confilter);
				
				// Driver
				List<MotorDriverDetails>  driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
				List<MotorDriverDetails> drifilter = driverList.stream().filter(o -> ! riskIds.contains(String.valueOf(o.getRiskId()))).collect(Collectors.toList());
				driverRepo.deleteAll(drifilter);
					
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
				List<VehicleNeedToRemove> vehicleNeedberemove = new ArrayList<VehicleNeedToRemove>();
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo())){
				List<EserviceTravelGroupDetails> oldGroupDatas = 	eserGroupRepo.findByQuoteNo(req.getEndtPrevQuoteNo() );
				oldGroupDatas.forEach( o -> {
					VehicleNeedToRemove removeVehicle = new VehicleNeedToRemove(); 
					removeVehicle.setSectionId(o.getSectionId().toString());
					removeVehicle.setVehicleId(Integer.valueOf(o.getRiskId()));
					vehicleNeedberemove.add(removeVehicle);
				});
				}
				
				req.setVehicleNeedberemove(vehicleNeedberemove);
				
			
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo()) && req.getEndtType().equalsIgnoreCase("842") ) {
//					// Endorsement
					Long travelInfo =  traPassRepo.countByQuoteNo(req.getEndtPrevQuoteNo());
					if (travelInfo > 0  ) {
					
						List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getEndtPrevQuoteNo());
						List<TravelPassengerDetails> saveEndtDatas = new ArrayList<TravelPassengerDetails>();
						for (TravelPassengerDetails passData :  oldPassDatas) {
							TravelPassengerDetails endtData = new TravelPassengerDetails(); 
							// Save New 
							dozerMapper.map(passData, endtData);
							
							// Date Diffrence
							Date periodStart = request.getPolicyStartDate();
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
								daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1 ;
								oldEndDate  = effDate ;
								// Check Leap Year
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
								boolean leapYear = LocalDate.parse(sdf.format(effDate) ).isLeapYear();
								System.out.println( "Deactivated Policy Cover :  "+ request.getPolicyStartDate() + "  Difference in days: " + diff);
								diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
							}
							
							endtData.setQuoteNo(request.getQuoteNo());
							endtData.setRequestReferenceNo(request.getRequestReferenceNo());
							endtData.setTravelEndDate(oldEndDate);
							endtData.setStatus("D");
							endtData.setTravelCoverDuration(Integer.valueOf(diff));
							//EndtUpdatePremiumRes endtRes = updateEndtPremium2(request.getQuoteNo(),effDate,ref.getEndtPrevQuoteNo(),ref.getRiskId() ,Endtcovers,Integer.valueOf(motorData.getProductId()) , Integer.valueOf(motorData.getSectionId()));
							endtData.setEndtPremium(endtData.getActualPremiumFc()==null ? null : endtData.getActualPremiumFc().doubleValue() >0 ? -endtData.getActualPremiumFc().doubleValue() : endtData.getActualPremiumFc().doubleValue() );
							endtData.setEndtVatPremium(endtData.getVatPremium()==null ? null :  endtData.getVatPremium().doubleValue() >0 ? new BigDecimal(-endtData.getVatPremium().doubleValue()) : endtData.getVatPremium());	
							saveEndtDatas.add(endtData);
						}
						traPassRepo.saveAllAndFlush(saveEndtDatas)	;
					}
				}
				
				if(StringUtils.isNotBlank(req.getEndtPrevQuoteNo()) ) {
					// Find Traces 
					Long dupQuoteCount = traPassRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						traPassRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
				}

				// Update Eservice Travel
				EserviceTravelDetails eserTravel = eserTraRepo.findByRequestReferenceNo(request.getRequestReferenceNo() );
				eserTravel.setQuoteNo(request.getQuoteNo());
				eserTraRepo.saveAndFlush(eserTravel);
				
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
			
			try {
				Long buildInfo =  buildRepo.countByQuoteNo(req.getQuoteNo());
				if (buildInfo > 0  ) {
					buildRepo.deleteByQuoteNo(req.getQuoteNo());
				}

				Long pacInfo =  commonDataRepo.countByQuoteNo(req.getQuoteNo());
				if (pacInfo > 0  ) {
					commonDataRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
				// Find Traces 
				Long dupQuoteCount = buildRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				if (dupQuoteCount> 0) {
					buildRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				}
				
				dupQuoteCount = commonDataRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				if (dupQuoteCount> 0) {
					commonDataRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				}
				
				
				//Additional info traces delete for Domestic & corporate plus
				List<EserviceSectionDetails> secs = eserSecRepo.findByRequestReferenceNoAndStatusNot(req.getRequestReferenceNo(),"D");
				
				List<String> secIds = new ArrayList<String>(); // secs.stream().map(EserviceSectionDetails :: getSectionId).collect(Collectors.toList());
				
				for (VehicleIdsReq veh : req.getVehicleIdsList()) {
					List<EserviceSectionDetails> filterSecId =  secs.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
					if(filterSecId.size() > 0 ) {
						List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
		    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
						if(filterCoverList.size() > 0 && filterCoverList.get(0).getCoverIdList()!=null  &&  filterCoverList.get(0).getCoverIdList().size() > 0 ) {
							secIds.add(veh.getSectionId());
						}
					} else  {
						filterSecId =  secs.stream().filter( o ->  o.getRiskId().equals(1) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());
						List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
		    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
						if(filterCoverList.size() > 0 && filterCoverList.get(0).getCoverIdList()!=null  &&  filterCoverList.get(0).getCoverIdList().size() > 0 ) {
							secIds.add(veh.getSectionId());
						}
					}
					
				}
				
				List<ContentAndRisk> con = contentRepo.findByQuoteNo(req.getQuoteNo());
				List<ProductEmployeeDetails> emp = empRepo.findByQuoteNo(req.getQuoteNo());
				List<DocumentTransactionDetails> doc = docRepo.findByQuoteNo(req.getQuoteNo());
				
				if (secIds.size()>0) {
					
					//unmatched based on sectionid
					
					List<ContentAndRisk> confilter = con.stream().filter(o -> ! secIds.contains(o.getSectionId())).collect(Collectors.toList());	
					contentRepo.deleteAll(confilter);
					
					List<ProductEmployeeDetails> empfilter = emp.stream().filter(o -> ! secIds.contains(o.getSectionId())).collect(Collectors.toList());	
					empRepo.deleteAll(empfilter);
					
					List<DocumentTransactionDetails> docfilter = doc.stream().filter(o -> o.getLocationId()!=99999 &&  ! secIds.contains(o.getSectionId().toString())).collect(Collectors.toList());	
					docRepo.deleteAll(docfilter);
					
					// Remove Dup Traces
					dupQuoteCount = contentRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						contentRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
					
					dupQuoteCount = empRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						empRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					}
					
				}
				List<VehicleNeedToRemove> vehicleNeedberemove = new ArrayList<VehicleNeedToRemove>();
				List<EserviceCommonDetails> eserHumans = eserCommonRepo.findByRequestReferenceNoAndStatus(request.getRequestReferenceNo(),"D");
				eserHumans.forEach( o -> {
					VehicleNeedToRemove removeVehicle = new VehicleNeedToRemove(); 
					removeVehicle.setSectionId(o.getSectionId());
					removeVehicle.setVehicleId(Integer.valueOf(o.getRiskId()));
					vehicleNeedberemove.add(removeVehicle);
				});
				List<EserviceSectionDetails> eserSections = eserSecRepo.findByRequestReferenceNoAndStatus(req.getRequestReferenceNo() ,"D");
				eserSections.forEach( o -> {
					VehicleNeedToRemove removeVehicle = new VehicleNeedToRemove();
					if(! "H".equalsIgnoreCase(o.getProductType())) {
						removeVehicle.setSectionId(o.getSectionId());
						removeVehicle.setVehicleId(Integer.valueOf(o.getRiskId()));
						vehicleNeedberemove.add(removeVehicle);
					}
					
				});
				
				req.setVehicleNeedberemove(vehicleNeedberemove);
				
				
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
				List<VehicleNeedToRemove> vehicleNeedberemove = new ArrayList<VehicleNeedToRemove>();
				List<EserviceCommonDetails> eserHumans = eserCommonRepo.findByRequestReferenceNoAndStatus(request.getRequestReferenceNo(),"D");
				eserHumans.forEach( o -> {
					VehicleNeedToRemove removeVehicle = new VehicleNeedToRemove(); 
					removeVehicle.setSectionId(o.getSectionId());
					removeVehicle.setVehicleId(Integer.valueOf(o.getRiskId()));
					vehicleNeedberemove.add(removeVehicle);
					
					o.setQuoteNo(req.getQuoteNo());
					
				});
				eserCommonRepo.saveAllAndFlush(eserHumans);
				
				// Find Traces 
				Long dupQuoteCount = commonDataRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				if (dupQuoteCount> 0) {
					commonDataRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				}
				
				req.setVehicleNeedberemove(vehicleNeedberemove);
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
	 				
	 				CommonRes commonRes = deactivateOldCovers(req); 
	 				
		 			// Find Traces
					Long dupQuoteCount = coverRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
					if (dupQuoteCount> 0) {
						coverRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
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
		
		public CommonRes deactivateOldCovers( QuoteThreadReq request ) {
			CommonRes commonRes = new CommonRes();
			List<Error> errors = new ArrayList<Error>();
			String res = "" ;
			try {
				
				// Deactivate Travel product covers
//				if (request.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
//					
//					res = deactivateTravelCovers(request);
//				// Deactivate Other Covers
//				} else {
//					res = deactivateOtherProductCovers(request);
//				}
				res = deactivateOtherProductCovers(request);
				
	 			
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
				

				// Filter Deactivated Covers
				List<VehicleIdsReq> collect = new ArrayList<VehicleIdsReq>();
				for(VehicleIdsReq o : request.getVehicleIdsList() ) {
					List<VehicleNeedToRemove> filterVehicle =  request.getVehicleNeedberemove().stream().filter( e -> 
					e.getVehicleId().equals(o.getVehicleId()) && e.getSectionId().equals(o.getSectionId()) ).collect(Collectors.toList());
					
					if(filterVehicle.size()<= 0 ) {
						collect.add(o);
					}
				}
				// Non Selected Records
				 List<PolicyCoverData>  deactivateOldCovers = OldPolicyCovers.stream().filter( o ->  ! o.getStatus().equalsIgnoreCase("D") ).collect(Collectors.toList());
				 for ( VehicleIdsReq vehId :  collect ) {
		            	for (CoverIdsReq cov : vehId.getCoverIdList() ) {
							
		            		if(( cov.getSubCoverYn() ==null || cov.getSubCoverYn().equalsIgnoreCase("N")) && ! request.getEndtType().equalsIgnoreCase("842")) {
		            				deactivateOldCovers.removeIf(  o -> o.getVehicleId().equals(vehId.getVehicleId() ) && o.getProductId().equals(Integer.valueOf(request.getProductId()))
		            					&& o.getSectionId().equals(Integer.valueOf(vehId.getSectionId())) && o.getCoverId().equals(cov.getCoverId())   );	            			
		            		
		            		} else if (! request.getEndtType().equalsIgnoreCase("842")) {
		            			
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
								fc.setIndividualId(f.getVehicleId());
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
									daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1 ;
									oldEndDate  = effDate ;
									// Check Leap Year
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
									boolean leapYear = LocalDate.parse(sdf.format(effDate) ).isLeapYear();
									System.out.println( "Deactivated Policy Cover :  "+ ref.getCoverDesc() + "  Difference in days: " + diff);
									diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
								}
								
								// Period End Condition
								Calendar cal = new GregorianCalendar(); 
								cal.setTime(oldEndDate);
								cal.set(Calendar.HOUR_OF_DAY, 23);
								cal.set(Calendar.MINUTE, 59);
								cal.set(Calendar.SECOND, 59);
								oldEndDate = cal.getTime();
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
				
				// Find Traces
				Long dupQuoteCount = secRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				if (dupQuoteCount> 0) {
					secRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				}
				
	 			res.put("Response", "Success") ;
				res.put("Errors", null) ;
				
			} catch ( Exception e) {
				e.printStackTrace();
				log.error("Exception is ---> " + e.getMessage());
			}
			return res;
		}
		
		@SuppressWarnings("unlikely-arg-type")
		public synchronized Map<String,Object>  copyDocumentRecords(QuoteThreadReq req) {
			Map<String,Object> res= new HashMap<String,Object>() ;
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Find Traces
				Long dupQuoteCount = docRepo.countByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				if (dupQuoteCount> 0) {
					docRepo.deleteByQuoteNoNotAndEndtCountAndOriginalPolicyNo(req.getQuoteNo() , new BigDecimal(req.getEndtCount()) ,req.getOriginalPolicyNo() );
				}
				
				List<EserviceSectionDetails> secs = eserSecRepo.findByRequestReferenceNoAndStatusNot(request.getRequestReferenceNo(),"D");
				List<String> secIds = secs.stream().map(EserviceSectionDetails :: getSectionId).collect(Collectors.toList());
				
				Long docInfo = docRepo.countByQuoteNo(req.getQuoteNo() );
				if( docInfo <= 0  ) {
					List<DocumentTransactionDetails>   oldDocDetails = docRepo.findByQuoteNo( req.getEndtPrevQuoteNo() ) ;
					List<DocumentTransactionDetails>   oldfilter = oldDocDetails.stream().filter(o -> secIds.contains(o.getSectionId().toString())).collect(Collectors.toList());	
					List<DocumentTransactionDetails>   commonfilter = oldDocDetails.stream().filter(o ->  o.getLocationId()==99999 ).collect(Collectors.toList());
					oldfilter.addAll(commonfilter);
					List<DocumentTransactionDetails> saveDocList = new ArrayList<DocumentTransactionDetails>();
					if( oldfilter.size() > 0  ) {
						for ( DocumentTransactionDetails doc : oldfilter ) {
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
			HomePositionMaster previousData = new HomePositionMaster();
			Long homeInfo =  homeRepo.countByQuoteNo(request.getQuoteNo());
			if (homeInfo > 0 ) {
				previousData = homeRepo.findByQuoteNo(request.getQuoteNo());
				
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
			
			 LoginMaster loginData = getLoginDetails(home.getLoginId());
			 home.setUserType(loginData!=null ? loginData.getUserType() : "Broker" );
			 
			 // Set Branch Details 
//			LoginBranchMaster loginBranch =  getBranchDetails(home.getCompanyId() ,home.getBrokerBranchCode() ,home.getLoginId() );
//			if( loginBranch !=null) {
//				home.setBranchName(loginBranch.getBranchName());
//				home.setBrokerBranchName(loginBranch.getBrokerBranchName());
//					
//			}
			
			// Primary KEy
			home.setQuoteNo(request.getQuoteNo());
			home.setRequestReferenceNo(request.getRequestReferenceNo());
			home.setCustomerId(request.getCustomerId());
			//	home.setProposalNo("");
			home.setAmendId(0);
			home.setApplicationNo(0L);
			
			// Commsion Setup
			if(StringUtils.isNotBlank(request.getCommissionModifyYn()) && "Y".equalsIgnoreCase(request.getCommissionModifyYn()) ) {
				home.setCommissionModifyYn( request.getCommissionModifyYn()) ;
				home.setCommissionPercentage(StringUtils.isNotBlank(request.getCommissionPercent()) ? new BigDecimal(request.getCommissionPercent())  : home.getCommissionPercentage()) ;
			} else {
				home.setCommissionModifyYn(previousData!=null && previousData.getCommissionModifyYn()!=null? previousData.getCommissionModifyYn() : "N") ;
				home.setCommissionPercentage(previousData!=null && previousData.getCommissionPercentage()!=null ? previousData.getCommissionPercentage() : home.getCommissionPercentage());
			
			}
			
			
			String loginId = "" ;
			if(! "1".equalsIgnoreCase(home.getApplicationId()  )) {
				loginId = home.getApplicationId();
			} else {
				loginId = home.getLoginId()  ;
			}
			
			{
				// Login Data
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<LoginMaster> query = cb.createQuery(LoginMaster.class);

				Root<LoginMaster> lm = query.from(LoginMaster.class);
				
				query.select(lm);

				Predicate m1 = cb.equal(lm.get("loginId"), loginId);
				
				query.where(m1);

				TypedQuery<LoginMaster> result1 = em.createQuery(query);
				List<LoginMaster> list1 = result1.getResultList();
				if( list1.size()> 0 ) {
					home.setSubUserType(list1.get(0).getSubUserType());
					home.setUserType(list1.get(0).getUserType());
				} else {
					home.setUserType("broker");
				}
			}
		
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
						 filterNonDefaultCovers = covers.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(vehReq.getSectionId())) && o.getVehicleId().equals(vehReq.getVehicleId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
//							List<TravelPassengerDetails> filterGroupPassengers = passengers.stream().filter( o -> o.getGroupId().equals(vehReq.getVehicleId())). collect(Collectors.toList());
//							
//							filterNonDefaultCovers = new ArrayList<PolicyCoverData>();
//							for (TravelPassengerDetails tra :  filterGroupPassengers  ) {
//								List<PolicyCoverData> passengerCover = covers.stream().filter( o ->   o.getVehicleId().equals(tra.getGroupId()) && o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
//								filterNonDefaultCovers.addAll(passengerCover);
//							}
											
						
					} else if(request.getMotorYn().equalsIgnoreCase("A")) {
						 
//						 filterNonDefaultCovers = covers.stream().filter( o -> o.getSectionId().equals(99999) && o.getVehicleId().equals(99999)  && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
//						 if(filterNonDefaultCovers==null) {
						 filterNonDefaultCovers = covers.stream().filter( o -> o.getLocationId().equals(Integer.valueOf(vehReq.getLocationId())) && o.getSectionId().equals(Integer.valueOf(vehReq.getSectionId())) && o.getVehicleId().equals(vehReq.getVehicleId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
//						 }
					
						 
					} else  {
						
						 filterNonDefaultCovers = covers.stream().filter( o -> o.getLocationId().equals(Integer.valueOf(vehReq.getLocationId())) && o.getSectionId().equals(Integer.valueOf(vehReq.getSectionId())) && o.getVehicleId().equals(vehReq.getVehicleId()) &&  o.getCoverId().equals(covReq.getCoverId()) && o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());				
						
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
			Double premiumFc = premiumCovers.stream().filter( o ->  
					( (o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && !o.getSectionId().equals(99999)) ||
					(o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && o.getSectionId().equals(99999) /*&& o.getCoverId().equals(945) */)) )
					.mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();	
			
			Double overAllPremiumFc = premiumCovers.stream().filter( o ->  ( (o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D && !o.getSectionId().equals(99999)) ||
					(o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null && o.getPremiumIncludedTaxFc().doubleValue() > 0D && o.getSectionId().equals(99999) /*&& o.getCoverId().equals(945) */)) ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
			
			Double premiumLc = premiumCovers.stream().filter( o ->  ( (o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D && !o.getSectionId().equals(99999)) ||
					(o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumExcludedTaxLc()!=null && o.getPremiumExcludedTaxLc().doubleValue() > 0D && o.getSectionId().equals(99999)/* && o.getCoverId().equals(945) */)) )
					.mapToDouble( o ->   o.getPremiumExcludedTaxLc().doubleValue()   ).sum();
			
			Double overAllPremiumLc = premiumCovers.stream().filter( o ->  ( (o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D && !o.getSectionId().equals(99999)) ||
					(o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0) && o.getPremiumIncludedTaxLc()!=null && o.getPremiumIncludedTaxLc().doubleValue() > 0D && o.getSectionId().equals(99999) /*&& o.getCoverId().equals(945)*/ )) )
					.mapToDouble( o ->   o.getPremiumIncludedTaxLc().doubleValue()   ).sum();
			
			List<PolicyCoverData>  covers2 = coverRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(request.getQuoteNo() ,"D");
			List<PolicyCoverData>  taxCovers =  covers2.stream().filter( o ->  o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).collect(Collectors.toList());
		//	Double taxPremium = taxCovers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getCoverageType().equals("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()  ).sum();
			// tax Percent 
			BigDecimal totalTaxPercent = new BigDecimal(0) ;
			Double totalTaxAmount = 0D ;
			boolean taxCondtion =  StringUtils.isNotBlank(request.getEndtType()) && request.getEndtType().equalsIgnoreCase("842") ? false : true ; 
			if(taxCondtion==true ) {
				totalTaxAmount = overAllPremiumFc - premiumFc ;  
				totalTaxPercent = new BigDecimal( totalTaxAmount<=0D ?0 : (totalTaxAmount*100) / premiumFc );
//				for(PolicyCoverData o : taxCovers ) {
//					if(o.getTaxAmount()!=null && o.getTaxAmount().compareTo(new BigDecimal(0)) > 0 && taxCondtion==true  ) {
//						totalTaxAmount =totalTaxAmount + ( o.getTaxAmount()==null ? 0D : o.getTaxAmount().doubleValue());
//						if(o.getTaxCalcType()!=null && o.getTaxCalcType().equalsIgnoreCase("P") ) {
//							totalTaxPercent =   totalTaxPercent.add( o.getTaxRate()==null ?  new BigDecimal(0) : o.getTaxRate());
//						}
//						
//					} 
//				}	
			} else if(taxCondtion == false) {
				totalTaxAmount = overAllPremiumFc - premiumFc ;  
				totalTaxPercent = new BigDecimal( totalTaxAmount<=0D ?0 : (totalTaxAmount*100) / premiumFc );
			}
			
			
			//List<PolicyCoverData>  taxCoversFilter =  covers2.stream().filter(  distinctByKey(o -> Arrays.asList(o.getVehicleId() ,o.getSectionId(),o.getCoverId() )) ).collect(Collectors.toList());
			//BigDecimal withCoverCount = new BigDecimal(taxCount.doubleValue()/taxCoversFilter.size() ) ;
			BigDecimal TaxPercent = totalTaxPercent.setScale(2, RoundingMode.HALF_UP); // totalTaxPercent.divide(new BigDecimal(home.getNoOfVehicles()) , 2, RoundingMode.HALF_UP); 
//			BigDecimal TaxPercent = totalTaxPercent.divide(new BigDecimal(taxCovers.size()<=0?1:taxCovers.stream().filter(o -> o.getTaxCalcType()!=null && o.getTaxCalcType().equalsIgnoreCase("P"))
//					.filter(distinctByKey(o -> Arrays.asList(o.getVehicleId()   ,o.getSectionId() ,  o.getCoverId()  ))) 
//					.collect(Collectors.toList()).size() ) , 2, RoundingMode.HALF_UP);
			// commPercentage.divide(commCount).setScale(new MathContext(2, RoundingMode.HALF_UP).getPrecision(),RoundingMode.HALF_UP)
			
//			Double vatPremiumFc = overAllPremiumFc - premiumFc ;  
//			Double vatPercent = vatPremiumFc<=0D ?0 : (vatPremiumFc*100) / premiumFc ;
//			Double vatPremiumLc = overAllPremiumLc - premiumLc ;  
			System.out.println("Home Position PremiumFc --> "  + premiumFc );
			System.out.println("Home Position OverAllPremiumFc --> "  + overAllPremiumFc );
			System.out.println("Home Position PremiumLc --> "  + premiumLc );
			System.out.println("Home Position OverAllPremiumLc --> "  + overAllPremiumLc );
			
			Double tax1 =  premiumCovers.stream().filter( o -> ((o.getDiscLoadId().equals(0) && o.getTaxId().equals(1) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && !o.getSectionId().equals(99999) )
					|| (o.getDiscLoadId().equals(0) && o.getTaxId().equals(1) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && o.getSectionId().equals(99999) && o.getCoverId().equals(945) ) ) )
					.mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			
			Double tax2 = premiumCovers.stream().filter( o ->  ((o.getDiscLoadId().equals(0) && o.getTaxId().equals(2) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && !o.getSectionId().equals(99999) )
					|| (o.getDiscLoadId().equals(0) && o.getTaxId().equals(2) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && o.getSectionId().equals(99999) && o.getCoverId().equals(945) ) ) ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			
			Double tax3 = premiumCovers.stream().filter( o ->  (o.getDiscLoadId().equals(0) && o.getTaxId().equals(3) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && !o.getSectionId().equals(99999) )
					|| (o.getDiscLoadId().equals(0) && o.getTaxId().equals(3) && o.getPremiumExcludedTaxFc() !=null && o.getPremiumExcludedTaxFc().doubleValue() > 0D && o.getSectionId().equals(99999) && o.getCoverId().equals(945) ) ).mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			
			// No OF Vehicles
			String decimalDigits = currencyDecimalFormat(home.getCompanyId() , home.getCurrency() ).toString();
			String stringFormat = "%0"+decimalDigits+"d" ;
			String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
			String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
			DecimalFormat df = new DecimalFormat(pattern);
			
			home.setPremiumFc(new BigDecimal(df.format(premiumFc)) );
			home.setOverallPremiumFc(new BigDecimal(df.format(overAllPremiumFc)));
			home.setVatPremiumFc(new BigDecimal(df.format(totalTaxAmount)));
			home.setVatPercent(TaxPercent);
			home.setPremiumLc(new BigDecimal(df.format(premiumLc)));
			home.setOverallPremiumLc(new BigDecimal(df.format(overAllPremiumLc)));
			home.setVatPremiumLc(  home.getVatPremiumFc().multiply(home.getExchangeRate(),MathContext.DECIMAL32));
			home.setFinalizeYn("N");
			home.setTax1(new BigDecimal(df.format(tax1)));
			home.setTax2(new BigDecimal(df.format(tax2)));
			home.setTax3(new BigDecimal(df.format(tax3)));
			home.setEffectiveDate(home.getInceptionDate());
			
			List<Integer> vehicleIds = request.getVehicleIdsList().stream().map(VehicleIdsReq :: getVehicleId ).collect(Collectors.toList());
			home.setVehicleNo(vehicleIds.size());
			
			String endtChargeOrRefund="";
			
			//Overall Endt Premium
			if(StringUtils.isNotBlank(home.getEndtTypeId())) {
				 
			//	BigDecimal endtPremium = updateEndtPremium(request.getQuoteNo(),home.getEndorsementEffdate(),home.getEndtPrevQuoteNo(),0,covers,null,null);
				EndtUpdatePremiumRes endtValues = mainTableEndtPremium(request ,home.getCompanyId() , home.getCurrency() );	
				endtChargeOrRefund="REFUND";
				if(endtValues.getEndtPremium().doubleValue()>=0) {
					endtChargeOrRefund="CHARGE";
				}
				
				home.setEndtPremium(endtValues.getEndtPremium());
				home.setEndtPremiumLc(home.getEndtPremium().multiply(home.getExchangeRate(),MathContext.DECIMAL32));
				home.setEndtPremiumTax(endtValues.getEndtVatPremium());
				home.setIsChargRefund(endtChargeOrRefund);
			}
			
			// 99999 fleet covers
//			List<PolicyCoverData>  filterFleetCovers = covers.stream().filter( o -> o.getVehicleId().equals(99999) && o.getSectionId().equals(99999)	 )
//					.collect(Collectors.toList());
//			if(filterFleetCovers.size() > 0 ) {
//				PolicyCoverData fleetCover = filterFleetCovers.get(0);
//				
//				home.setPremiumFc(new BigDecimal(df.format(fleetCover.getPremiumExcludedTaxFc())) );
//				home.setOverallPremiumFc(new BigDecimal(df.format(fleetCover.getPremiumIncludedTaxFc())));
//				home.setPremiumLc(new BigDecimal(df.format(fleetCover.getPremiumExcludedTaxLc())));
//				home.setOverallPremiumLc(new BigDecimal(df.format(fleetCover.getPremiumIncludedTaxLc())));
//				home.setVatPremiumFc(new BigDecimal(df.format(fleetCover.getPremiumIncludedTaxFc().subtract(fleetCover.getPremiumExcludedTaxFc()))));
//				home.setVatPremiumLc(  home.getVatPremiumFc().multiply(home.getExchangeRate(),MathContext.DECIMAL32));
//				
//				// Endt COvers
//				if(StringUtils.isNotBlank(home.getEndtTypeId())) {
//					List<PolicyCoverData>  covers3 = coverRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteNo() );
//					Integer endtType = Integer.valueOf(home.getEndtTypeId()) ; 
//					List<PolicyCoverData>  filterFleetEndtCovers = covers3.stream().filter( o -> o.getVehicleId().equals(99999) && o.getSectionId().equals(99999) &&
//							o.getDiscLoadId().equals(endtType) && o.getTaxId().equals(0) && o.getCoverageType().equalsIgnoreCase("E") )
//							.collect(Collectors.toList());
//					if(filterFleetEndtCovers.size() > 0 ) {
//						PolicyCoverData fleetEndtCover = filterFleetEndtCovers.get(0);
//						home.setEndtPremium(new BigDecimal(df.format(fleetEndtCover.getPremiumExcludedTaxFc())));
//						home.setEndtPremiumLc(home.getEndtPremium().multiply(home.getExchangeRate(),MathContext.DECIMAL32));
//						home.setEndtPremiumTax(new BigDecimal(df.format(fleetEndtCover.getPremiumIncludedTaxFc().subtract(fleetEndtCover.getPremiumExcludedTaxFc()))));
//					}	
//				}
//				
//						
//			}
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(home.getExpiryDate() );
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			home.setExpiryDate(cal.getTime());
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
		String quoteno="";
		String product_id="";
		try {
			
			List<EserviceSectionDetails> eserSec = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(request.getRequestReferenceNo());
			eserSec.forEach( o -> o.setUserOpt("N")  ) ;
			eserSecRepo.saveAllAndFlush(eserSec);
			
			List<VehicleIdsReq> VehicleIdsList = request.getVehicleIdsList();
			
			
			List<SectionDataDetails> secList = new ArrayList<SectionDataDetails>();
			List<EserviceSectionDetails> updateEserSec =  new ArrayList<EserviceSectionDetails>();
			
			for (VehicleIdsReq veh : VehicleIdsList) {
				List<EserviceSectionDetails> filterSecId =  eserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) && o.getSectionId().equalsIgnoreCase( veh.getSectionId()) && o.getLocationId().equals( veh.getLocationId())).collect(Collectors.toList());
				if(filterSecId.size() > 0 ) {
					
					EserviceSectionDetails filterSec = eserSec.stream().filter( o ->  o.getRiskId().equals(veh.getVehicleId() ) &&  o.getSectionId().equalsIgnoreCase( veh.getSectionId()) && o.getLocationId().equals( veh.getLocationId())).collect(Collectors.toList()).get(0);	
					filterSec.setUserOpt("Y");
					filterSec.setQuoteNo(request.getQuoteNo());
					filterSec.setUpdatedDate(new Date());
					updateEserSec.add(filterSec);
					
					SectionDataDetails  saveSec = new  SectionDataDetails();
					mapper.map(filterSec, saveSec)	;
					saveSec.setQuoteNo(request.getQuoteNo());
					quoteno=request.getQuoteNo();
					product_id=request.getProductId();
					saveSec.setUpdatedDate(new Date());
					saveSec.setSectionDesc(filterSec.getSectionName());
					List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
	    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
					if(filterCoverList.size() == 0 || filterCoverList.get(0).getCoverIdList()==null  || filterCoverList.get(0).getCoverIdList().size() == 0 ) {
						saveSec.setStatus("D");
					}
					secList.add(saveSec);	
				} else {
					filterSecId =  eserSec.stream().filter( o ->   o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList());

					if(filterSecId.size() > 0 ) {
						
						EserviceSectionDetails filterSec = eserSec.stream().filter( o ->   o.getSectionId().equalsIgnoreCase( veh.getSectionId()) ).collect(Collectors.toList()).get(0);	
						filterSec.setUserOpt("Y");
						quoteno=request.getQuoteNo();
						product_id=request.getProductId();
						filterSec.setQuoteNo(request.getQuoteNo());
						filterSec.setUpdatedDate(new Date());
						updateEserSec.add(filterSec);
						
						SectionDataDetails  saveSec = new  SectionDataDetails();
						mapper.map(filterSec, saveSec)	;
						saveSec.setQuoteNo(request.getQuoteNo());
						saveSec.setUpdatedDate(new Date());
						saveSec.setSectionDesc(filterSec.getSectionName());
						List<VehicleIdsReq> filterCoverList = request.getVehicleIdsList().stream().filter( o -> o.getVehicleId()!=null && StringUtils.isNotBlank(o.getSectionId())	&&	            					
		    					o.getVehicleId().equals(veh.getVehicleId()) && o.getSectionId().equalsIgnoreCase(veh.getSectionId()) ).collect(Collectors.toList());
						if(filterCoverList.size() == 0 || filterCoverList.get(0).getCoverIdList()==null  || filterCoverList.get(0).getCoverIdList().size() == 0 ) {
							saveSec.setStatus("D");
						}
						secList.add(saveSec);	
					}
				}
			}
						
			secRepo.saveAllAndFlush(secList);
			eserSecRepo.saveAllAndFlush(updateEserSec);
			updateAdditionalInfo(quoteno,product_id);
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
	public void updateAdditionalInfo(String quoteno,String productid) 
	{
		
	String requestrefno="";
	
	List<ContentAndRisk> content =null;
    List<BuildingDetails>  building = null;
    List<ProductEmployeeDetails> personalaccident =null; 
	

	try {
	
	if(productid.equals("59")) {
		List<EserviceSectionDetails> selecteddata = eserSecRepo.findByQuoteNoAndUserOpt(quoteno,"Y");
		List<String> sectionIds = selecteddata.stream().map(EserviceSectionDetails::getSectionId).collect(Collectors.toList());
		requestrefno=selecteddata.get(0).getRequestReferenceNo();
          content=contentRepo.findByRequestReferenceNo(requestrefno);
         
         building = locRepo.findByRequestReferenceNo(requestrefno);
 		
	      personalaccident=pacRepo.findByRequestReferenceNo(requestrefno);
	    if(content!=null && (content.size()>0)) {
	    List<ContentAndRisk> matchedContent = content.stream().filter(cc -> sectionIds.contains(cc.getSectionId())).collect(Collectors.toList());
	    List<ContentAndRisk> unmatchedContent = content.stream() .filter(cc -> !sectionIds.contains(cc.getSectionId())) .collect(Collectors.toList());
	    matchedContent.forEach(cc -> { cc.setQuoteNo(quoteno);  contentRepo.save(cc);});
	    unmatchedContent.forEach(cc -> contentRepo.delete(cc));
		}
		
		if(building!=null && (building.size()>0)) {
	   List<BuildingDetails> matchedContent = building.stream().filter(cc ->sectionIds.contains(cc.getSectionId())).collect(Collectors.toList());
       List<BuildingDetails> unmatchedContent = building.stream() .filter(cc ->!sectionIds.contains(cc.getSectionId())) .collect(Collectors.toList());
	   matchedContent.forEach(cc -> { cc.setQuoteNo(quoteno); locRepo.save(cc);});
	   if(content==null || content.size()<0) {
	   unmatchedContent.forEach(cc -> locRepo.delete(cc)); 
	   }
	   }
		 
	    if(personalaccident!=null && (personalaccident.size()>0)) {
		   List<ProductEmployeeDetails> matchedContent = personalaccident.stream().filter(cc -> sectionIds.contains(cc.getSectionId())).collect(Collectors.toList());
		    List<ProductEmployeeDetails> unmatchedContent = personalaccident.stream() .filter(cc -> !sectionIds.contains(cc.getSectionId())) .collect(Collectors.toList());
		    matchedContent.forEach(cc -> { cc.setQuoteNo(quoteno);  pacRepo.save(cc);});
		    unmatchedContent.forEach(cc -> pacRepo.delete(cc));
	    }
	    
	
	}
	    
	
	}catch(Exception ex) {
		System.out.println("Exception  in additional info ");
		
		ex.printStackTrace();
	}
	
	}

	
	
	private HomePositionMaster setMotorDetails(QuoteThreadReq  request) {
		HomePositionMaster home = new HomePositionMaster();
		try {
			
			
			EserviceMotorDetails motorData = eserMotRepo.findByRequestReferenceNoAndRiskIdOrderByRiskIdAsc(request.getRequestReferenceNo() ,request.getVehicleId());
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(motorData.getCustomerReferenceNo());
			home.setCustomerName(custData.getClientName());
			
			home.setCompanyId(motorData.getCompanyId());
			home.setProductId(Integer.valueOf(motorData.getProductId()));
			home.setSectionId(Integer.valueOf(motorData.getSectionId()));
		//	home.setEffectiveDate(motorData.getPolicyStartDate());
			home.setExpiryDate(motorData.getPolicyEndDate());
			home.setAdminRemarks(motorData.getAdminRemarks());
			if(motorData.getStatus().equalsIgnoreCase("RP") || motorData.getStatus().equalsIgnoreCase("RA") ||motorData.getStatus().equalsIgnoreCase("RR") ||
					motorData.getStatus().equalsIgnoreCase("RE") ||motorData.getStatus().equalsIgnoreCase("REV") ) {		
			home.setAdminReferralStatus(motorData.getStatus());	
		
			home.setReferralDescription(motorData.getReferalRemarks());
//			home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? motorData.getAdminLoginId() : request.getAdminLoginId() );
			home.setAdminLoginId(StringUtils.isBlank(motorData.getAdminLoginId()) ? request.getAdminLoginId() : motorData.getAdminLoginId() );
			home.setApprCanDt(motorData.getUpdatedDate());
			}
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
			home.setApprCanDt(motorData.getUpdatedDate());
			home.setProductName(motorData.getProductName());
			home.setCompanyName(motorData.getCompanyName());
			home.setCommissionType(motorData.getCommissionType());
			home.setCommissionTypeDesc(motorData.getCommissionTypeDesc());
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
			home.setCommissionPercentage(motorData.getCommissionPercentage());
			home.setVatCommission(motorData.getVatCommission());
			
			// Source Type Details 
			home.setBranchCode(motorData.getBranchCode()) ;
		    home.setBranchName(motorData.getBranchName());
		    home.setSourceType(motorData.getSourceType());
			home.setSourceTypeId(motorData.getSourceTypeId());
		    home.setSubUserType(motorData.getSubUserType());
		    home.setApplicationId(motorData.getApplicationId());
		    home.setBrokerCode(motorData.getBrokerCode());
		    home.setAgencyCode(Integer.valueOf(motorData.getAgencyCode().replaceAll("\\r", "" ).replaceAll("\\n", "" ) ));
		    home.setCustomerCode(motorData.getCustomerCode());
//		    home.setCustomerName(motorData.getCustomerName() );
		    home.setBdmCode(motorData.getBdmCode());
			home.setBrokerBranchCode(motorData.getBrokerBranchCode() );
			home.setBrokerBranchName(motorData.getBrokerBranchName() );
			home.setUserType(motorData.getApplicationId().equalsIgnoreCase("1") ? motorData.getSourceType() : "Issuer" ) ;
			home.setLoginId(motorData.getLoginId());
			home.setPolicyPeriod(motorData.getPeriodOfInsurance()==null?"" :motorData.getPeriodOfInsurance().toString());
			home.setPolicyTerm(motorData.getPeriodOfInsurance()==null?"" :motorData.getPeriodOfInsurance().toString());
			home.setSalePointCode(motorData.getSalePointCode());
			home.setBrokerTiraCode(motorData.getBrokerTiraCode());
			home.setTiraCoverNoteNo(motorData.getTiraCoverNoteNo());
			
//			List<EserviceMotorDetails> motList = eserMotRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
//			
//			
//			List<EserviceMotorDetails> filterComActive = motList.stream().filter( o ->  (! o.getStatus().equalsIgnoreCase("D")) &&  o.getCommissionPercentage() !=null ).collect(Collectors.toList());
//			BigDecimal commPercentage = new BigDecimal(filterComActive.stream().mapToDouble( o ->   o.getCommissionPercentage().doubleValue()   ).sum() ); 					  
//			BigDecimal commCount = new BigDecimal(filterComActive.size());
//			BigDecimal overAllcommPercent  = new BigDecimal(0);
//			if(commPercentage.compareTo(new BigDecimal(0)) > 0 && commCount.compareTo(new BigDecimal(0)) >0 )
//			overAllcommPercent = commPercentage.divide(commCount).setScale(new MathContext(2, RoundingMode.HALF_UP).getPrecision(),RoundingMode.HALF_UP) ;
//			
//			home.setCommissionPercentage(overAllcommPercent);
//			
//			List<EserviceMotorDetails> filterComVatActive = motList.stream().filter( o ->  (! o.getStatus().equalsIgnoreCase("D")) &&  o.getVatCommission() !=null ).collect(Collectors.toList());
//			BigDecimal commVatPercentage = new BigDecimal(filterComVatActive.stream().mapToDouble( o ->   o.getVatCommission().doubleValue()   ).sum() ); 					  
//			BigDecimal commVatCount = new BigDecimal(filterComVatActive.size());
//			
//			BigDecimal overAllVatcommPercent = new BigDecimal(0);
//			if(commVatPercentage.compareTo(new BigDecimal(0)) > 0 && commVatCount.compareTo(new BigDecimal(0)) >0 )
//			overAllVatcommPercent = commVatPercentage.divide(commVatCount).setScale(new MathContext(2, RoundingMode.HALF_UP).getPrecision(),RoundingMode.HALF_UP) ;
//			
//			home.setVatCommission(overAllVatcommPercent);
			
			if(StringUtils.isNotBlank(motorData.getEndorsementType()==null?null:String.valueOf(motorData.getEndorsementType()))) {
				HomePositionMaster oldPosition = homeRepo.findByQuoteNo(motorData.getEndtPrevQuoteNo()==null?null:motorData.getEndtPrevQuoteNo());
				home.setCoverNoteReferenceNo(oldPosition.getCoverNoteReferenceNo());
				home.setPrevCoverNoteRefNo(oldPosition.getCoverNoteReferenceNo());
				home.setCancelledDate(oldPosition.getExpiryDate());
			}
			
			// Copy Old Motor Doc
			List<FrameOldDocSaveReq> frameDocReqList = frameMotorDocRequest( home  ) ;
			List<ListItemValue> docTypeList = getListItem( "99999" , home.getBranchCode() , "DOC_ID_TYPE" , "M");
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
			home.setProductId(Integer.valueOf(travelData.getProductId()));
			home.setSectionId(Integer.valueOf(travelData.getSectionId()));
			home.setAcExecutiveId(travelData.getAcExecutiveId()==null?null : Long.valueOf(travelData.getAcExecutiveId()));
		//	home.setEffectiveDate(travelData.getTravelStartDate());
			home.setExpiryDate(travelData.getTravelEndDate());
			home.setAdminRemarks(travelData.getAdminRemarks());
			if(travelData.getStatus().equalsIgnoreCase("RP") || travelData.getStatus().equalsIgnoreCase("RA") ||travelData.getStatus().equalsIgnoreCase("RR") ||
					travelData.getStatus().equalsIgnoreCase("RE") ||travelData.getStatus().equalsIgnoreCase("REV") )	{		
				home.setAdminReferralStatus(travelData.getStatus());	
				
				home.setReferralDescription(travelData.getReferalRemarks());
//				home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? motorData.getAdminLoginId() : request.getAdminLoginId() );
				home.setAdminLoginId(StringUtils.isBlank(travelData.getAdminLoginId()) ? request.getAdminLoginId() : travelData.getAdminLoginId() );
				home.setApprCanDt(travelData.getUpdatedDate());
				}
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
			
			home.setProductName(travelData.getProductName());
			home.setCompanyName(travelData.getCompanyName());
			home.setCommissionType(travelData.getCommissionType());
			home.setCommissionTypeDesc(travelData.getCommissionTypeDesc());
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
			home.setCommissionPercentage(travelData.getCommissionPercentage());
			home.setVatCommission(travelData.getVatCommission());
			home.setTiraCoverNoteNo(travelData.getTiraCoverNoteNo());
			
			// Source Type Details 
			home.setBranchCode(travelData.getBranchCode()) ;
		    home.setBranchName(travelData.getBranchName());
			home.setSourceType(travelData.getSourceType());
			home.setSourceTypeId(travelData.getSourceTypeId());
		    home.setSubUserType(travelData.getSubUserType());
		    home.setApplicationId(travelData.getApplicationId());
		    home.setBrokerCode(travelData.getBrokerCode());
		    home.setAgencyCode(Integer.valueOf(travelData.getAgencyCode().replaceAll("\\r", "" ).replaceAll("\\n", "" ) ));
		    home.setCustomerCode(travelData.getCustomerCode());
		    home.setCustomerName( travelData.getCustomerName() );
		    home.setBdmCode(travelData.getBdmCode());
			home.setBrokerBranchCode(travelData.getBrokerBranchCode() );
			home.setBrokerBranchName(travelData.getBrokerBranchName() );
			home.setUserType(travelData.getApplicationId().equalsIgnoreCase("1") ? travelData.getSourceType() : "Issuer" ) ;
			home.setLoginId(travelData.getLoginId());
			home.setPolicyPeriod(travelData.getTravelCoverDuration()==null?"" :travelData.getTravelCoverDuration().toString());
			home.setPolicyTerm(travelData.getTravelCoverDuration()==null?"" :travelData.getTravelCoverDuration().toString());
			home.setSalePointCode(travelData.getSalePointCode());
			home.setBrokerTiraCode(travelData.getBrokerTiraCode());
			
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
			
			
//			EserviceBuildingDetails  buildingData = eserBuildRepo.findByRequestReferenceNoAndRiskIdAndSectionId(request.getRequestReferenceNo() , 1,"0") ;
			List<EserviceBuildingDetails>  buildingData1 = eserBuildRepo.findByRequestReferenceNo(request.getRequestReferenceNo()) ;
			EserviceBuildingDetails buildingData=buildingData1.get(0);
			Long builCount =  eserBuildRepo.countByRequestReferenceNo(request.getRequestReferenceNo() ) ;
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(buildingData.getCustomerReferenceNo());
			home.setCustomerName(custData.getClientName());
			
			//List<EserviceSectionDetails> sections = eserSecRepo.findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(request.getRequestReferenceNo() , request.getVehicleId(),request.getProductId() );
			home.setCompanyId(buildingData.getCompanyId());
			home.setProductId(Integer.valueOf(buildingData.getProductId()));
			home.setSectionId(Integer.valueOf(0));
			home.setAcExecutiveId(buildingData.getAcExecutiveId()==null?null : Long.valueOf(buildingData.getAcExecutiveId()));
		//	home.setEffectiveDate(buildingData.getPolicyStartDate());
			home.setExpiryDate(buildingData.getPolicyEndDate());
			home.setAdminRemarks(buildingData.getAdminRemarks());
			if(buildingData.getStatus().equalsIgnoreCase("RP") || buildingData.getStatus().equalsIgnoreCase("RA") ||buildingData.getStatus().equalsIgnoreCase("RR") ||
					buildingData.getStatus().equalsIgnoreCase("RE") ||buildingData.getStatus().equalsIgnoreCase("REV") ) {		
			home.setAdminReferralStatus(buildingData.getStatus());	
		
			home.setReferralDescription(buildingData.getReferalRemarks());
//			home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? motorData.getAdminLoginId() : request.getAdminLoginId() );
			home.setAdminLoginId(StringUtils.isBlank(buildingData.getAdminLoginId()) ? request.getAdminLoginId() : buildingData.getAdminLoginId() );
			home.setApprCanDt(buildingData.getUpdatedDate());
			}
			home.setStatus(buildingData.getStatus());
			home.setQuoteCreatedDate(new Date());
			home.setEntryDate(new Date());
			home.setInceptionDate(buildingData.getPolicyStartDate());
			home.setExpiryDate(buildingData.getPolicyEndDate());
			home.setCurrency(buildingData.getCurrency());
			home.setExchangeRate(buildingData.getExchangeRate());
			home.setNoOfVehicles(Integer.valueOf(builCount.toString() ) - 1 );
			home.setHavepromoYn(buildingData.getHavepromocode());
			home.setPromocode(buildingData.getPromocode());
			home.setManualReferalYn(buildingData.getManualReferalYn());
			home.setTiraCoverNoteNo(buildingData.getTiraCoverNoteNo());
			
			home.setProductName(buildingData.getProductDesc());
			home.setCompanyName(buildingData.getCompanyName());
			home.setCommissionType(buildingData.getCommissionType());
			home.setCommissionTypeDesc(buildingData.getCommissionTypeDesc());
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
			home.setCommissionPercentage(buildingData.getCommissionPercentage());
			home.setVatCommission(buildingData.getVatCommission());
			
			// Source Type Details 
			home.setBranchCode(buildingData.getBranchCode()) ;
		    home.setBranchName(buildingData.getBranchName());
		    home.setSourceType(buildingData.getSourceType());
			home.setSourceTypeId(buildingData.getSourceTypeId());
		    home.setSubUserType(buildingData.getSubUserType());
		    home.setApplicationId(buildingData.getApplicationId());
		    home.setBrokerCode(buildingData.getBrokerCode());
		    home.setAgencyCode(Integer.valueOf(buildingData.getAgencyCode().replaceAll("\\r", "" ).replaceAll("\\n", "" ) ));
		    home.setCustomerCode(buildingData.getCustomerCode());
		    home.setCustomerName( buildingData.getCustomerName() );
		    home.setBdmCode(buildingData.getBdmCode());
			home.setBrokerBranchCode(buildingData.getBrokerBranchCode() );
			home.setBrokerBranchName(buildingData.getBrokerBranchName() );
			home.setUserType(buildingData.getApplicationId().equalsIgnoreCase("1") ? buildingData.getSourceType() : "Issuer" ) ;
			home.setLoginId(buildingData.getLoginId());
			home.setPolicyPeriod(buildingData.getPolicyPeriord()==null?"" :buildingData.getPolicyPeriord().toString());
			home.setPolicyTerm(buildingData.getPolicyPeriord()==null?"" :buildingData.getPolicyPeriord().toString());
			home.setSalePointCode(buildingData.getSalePointCode());
			home.setBrokerTiraCode(buildingData.getBrokerTiraCode());
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
			String sectionid=request.getVehicleIdsList().get(0).getSectionId();
			Integer locationid=request.getVehicleIdsList().get(0).getLocationId();
			EserviceCommonDetails  eserCommonData = eserCommonRepo.findByRequestReferenceNoAndRiskIdAndSectionIdAndLocationId(request.getRequestReferenceNo(), request.getVehicleId(), sectionid, locationid);
		
//			EserviceCommonDetails  eserCommonData = eserCommonRepo.findByRequestReferenceNoAndOriginalRiskIdAndSectionId(request.getRequestReferenceNo() , request.getVehicleId(),sectionid) ;
			Long commonCount =  eserCommonRepo.countByRequestReferenceNo(request.getRequestReferenceNo() ) ;
			EserviceCustomerDetails custData = eserCustRepo.findByCustomerReferenceNo(eserCommonData.getCustomerReferenceNo());
			
			//List<EserviceSectionDetails> sections = eserSecRepo.findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(request.getRequestReferenceNo() , request.getVehicleId(),request.getProductId() );
			home.setSalePointCode(eserCommonData.getSalePointCode());
			home.setCompanyId(eserCommonData.getCompanyId());
			home.setCustomerName(eserCommonData.getCustomerName() );
			home.setProductId(Integer.valueOf(eserCommonData.getProductId()));
			home.setSectionId(Integer.valueOf(0));

			home.setAcExecutiveId(eserCommonData.getAcExecutiveId()==null?null : Long.valueOf(eserCommonData.getAcExecutiveId()));
		//	home.setEffectiveDate(eserCommonData.getPolicyStartDate());
			home.setExpiryDate(eserCommonData.getPolicyEndDate());
			home.setAdminRemarks(eserCommonData.getAdminRemarks());
			if(eserCommonData.getStatus().equalsIgnoreCase("RP") || eserCommonData.getStatus().equalsIgnoreCase("RA") ||eserCommonData.getStatus().equalsIgnoreCase("RR") ||
					eserCommonData.getStatus().equalsIgnoreCase("RE") ||eserCommonData.getStatus().equalsIgnoreCase("REV") ) {		
			home.setAdminReferralStatus(eserCommonData.getStatus());	
		
			home.setReferralDescription(eserCommonData.getReferalRemarks());
//			home.setAdminLoginId(StringUtils.isBlank(request.getAdminLoginId() ) ? motorData.getAdminLoginId() : request.getAdminLoginId() );
			home.setAdminLoginId(StringUtils.isBlank(eserCommonData.getAdminLoginId()) ? request.getAdminLoginId() : eserCommonData.getAdminLoginId() );
			home.setApprCanDt(eserCommonData.getUpdatedDate());
			}
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
			home.setTiraCoverNoteNo(eserCommonData.getTiraCoverNoteNo());
			
			home.setProductName(eserCommonData.getProductDesc());
			home.setCompanyName(eserCommonData.getCompanyName());
		//	home.setCommissionType(eserCommonData.getCommissionType());
		//	home.setCommissionTypeDesc(eserCommonData.getCommissionTypeDesc());
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
			home.setCommissionPercentage(eserCommonData.getCommissionPercentage());
			home.setVatCommission(eserCommonData.getVatCommission());
			
			// Source Type Details 
			home.setBranchCode(eserCommonData.getBranchCode()) ;
		    home.setBranchName(eserCommonData.getBranchName());
		    home.setSourceType(eserCommonData.getSourceType());
			home.setSourceTypeId(eserCommonData.getSourceTypeId());
		    home.setSubUserType(eserCommonData.getSubUserType());
		    home.setApplicationId(eserCommonData.getApplicationId());
		    home.setBrokerCode(eserCommonData.getBrokerCode());
		    home.setAgencyCode(Integer.valueOf(eserCommonData.getAgencyCode().replaceAll("\\r", "" ).replaceAll("\\n", "" ) ));
		    home.setCustomerCode(eserCommonData.getCustomerCode());
		    home.setCustomerName( eserCommonData.getCustomerName() );
		    home.setBdmCode(eserCommonData.getBdmCode());
			home.setBrokerBranchCode(eserCommonData.getBrokerBranchCode() );
			home.setBrokerBranchName(eserCommonData.getBrokerBranchName() );
			home.setUserType(eserCommonData.getApplicationId().equalsIgnoreCase("1") ? eserCommonData.getSourceType() : "Issuer" ) ;
			home.setLoginId(eserCommonData.getLoginId());
			home.setPolicyPeriod(eserCommonData.getPolicyPeriod()==null?"" :eserCommonData.getPolicyPeriod().toString());
			home.setPolicyTerm(eserCommonData.getPolicyPeriod()==null?"" :eserCommonData.getPolicyPeriod().toString());
			home.setSalePointCode(eserCommonData.getSalePointCode());
			home.setBrokerTiraCode(eserCommonData.getBrokerTiraCode());
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
				saveReq.setId(mot.getRegistrationNumber());
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

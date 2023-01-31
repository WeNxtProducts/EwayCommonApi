package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EservicePersonalAccidentDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPassengerHistory;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.DeleteOldQuoteReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.SectionSumInsuredGetReq;
import com.maan.eway.common.req.UpdateQuoteStatusReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.BuildingProductDetailsRes;
import com.maan.eway.common.res.CommonDetailsRes;
import com.maan.eway.common.res.CommonProductDetailsRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.MotorProductDetailsRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.TravelPassDetailsRes;
import com.maan.eway.common.res.TravelProductDetailsRes;
import com.maan.eway.common.res.VehicleDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EservicePersonalAccidentDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.res.BuildingSumInsuredDetails;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.OccupationReqClass;
import com.maan.eway.res.SectionWiseSumInsuredRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;


@Service
@Transactional
public class QuoteServiceImpl implements QuoteService {

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private QuoteThreadService otSer ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository custRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired
	private FactorRateRequestDetailsRepository eserCovRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private CoverDetailsRepository coverRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private EserviceTravelGroupDetailsRepository eserGroupRepo;
	
	@Autowired
	private TravelPassengerDetailsRepository traPassRepo  ;
	
	@Autowired
	private TravelPassengerHistoryRepository traPassHisRepo  ;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuildRepo  ;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;

	@Autowired
	private PolicyCoverDataRepository polCoverRepo  ;
	
	@Autowired
	private EservicePersonalAccidentDetailsRepository eserPaccRepo  ;
	
	@Autowired
	private GenerateSeqNoServiceImpl generateSeqService ;
	
	@Autowired
	private PaymentService paymentService ;
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo ;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	private Logger log = LogManager.getLogger(QuoteServiceImpl.class);
	
	@Override
	public CommonRes generateNewQuote(NewQuoteReq req) {
			CommonRes	res = otSer.call_OT_Insert(req);
			return res ;
			
	}

	@Override
	public ViewQuoteRes viewQuoteDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMappper = new DozerBeanMapper();
		try {
			// Quote Details
			HomePositionMaster homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());
			QuoteDetailsRes quoteRes = new QuoteDetailsRes();
			
			quoteRes = dozerMappper.map(homeData, QuoteDetailsRes.class);
			quoteRes.setOverAllPremiumFc(homeData.getOverallPremiumFc()==null?"":homeData.getOverallPremiumFc().toPlainString() );
			quoteRes.setOverAllPremiumLc(homeData.getOverallPremiumLc()==null?"":homeData.getOverallPremiumLc().toPlainString());
			quoteRes.setPremiumFc(homeData.getPremiumFc()==null?"":homeData.getPremiumFc().toPlainString() );
			quoteRes.setPremiumLc(homeData.getPremiumLc()==null?"":homeData.getPremiumLc().toPlainString());
			quoteRes.setAdminRemarks(homeData.getAdminRemarks());
			quoteRes.setReferalRemarks(homeData.getReferralDescription());
			
			quoteRes.setEmiYn("N");
			
			// Emi Details 
			List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNoAndCompanyIdAndProductId(homeData.getQuoteNo() ,homeData.getCompanyId() , homeData.getProductId().toString());
			if (emiDetails.size()>0 ) {
				List<EmiTransactionDetails> filterEmi =  emiDetails.stream().filter( o -> (!o.getPaymentStatus().equalsIgnoreCase("Accepted")) &&  ( o.getInstalment().equalsIgnoreCase("0") || o.getInstalment()!=null ) ).collect(Collectors.toList());
				if(filterEmi.size()>0   ) {
					quoteRes.setEmiYn("Y");
					quoteRes.setInstallmentPeriod(filterEmi.get(0).getInstallmentPeriod());
					quoteRes.setInstallmentMonth(filterEmi.get(0).getInstalment() );
					quoteRes.setDueAmount(filterEmi.get(0).getDueAmount()==null?"":filterEmi.get(0).getDueAmount().toString());
				}
			}
						
			// Customer Details
			PersonalInfo custData = custRepo.findByCustomerId(homeData.getCustomerId());
			CustomerDetailsRes  custRes = new CustomerDetailsRes();
			custRes  = dozerMappper.map(custData, CustomerDetailsRes.class);
			
			// Motor Product Details
			if( homeData.getProductId().equals(Integer.valueOf(motorProductId))) {
				viewRes =  getMotorProductDetails( req);
				
				viewRes.setCustomerDetails(custRes);
				viewRes.setQuoteDetails(quoteRes);
			} else if( homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
				// Travel Product Details
				viewRes =	getTravelProductDetails( req);
				viewRes.setCustomerDetails(custRes);
				viewRes.setQuoteDetails(quoteRes);
			} else if( homeData.getProductId().equals(Integer.valueOf(buildingProductId))) {
				// Travel Product Details
				viewRes =	getBuildingProductDetails( req);
				viewRes.setCustomerDetails(custRes);
				viewRes.setQuoteDetails(quoteRes);
			} else {
				// Travel Product Details
				viewRes =	getCommonProductDetails( req);
				viewRes.setCustomerDetails(custRes);
				viewRes.setQuoteDetails(quoteRes);
			}
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	
	public ViewQuoteRes getMotorProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<MotorDataDetails> motorDatas =  motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<MotorProductDetailsRes>   motorResList = new ArrayList<MotorProductDetailsRes>();
			for (MotorDataDetails mot :  motorDatas) {
				
				// Mot
				VehicleDetailsRes vehicleDetails = new  VehicleDetailsRes()  ;
				dozerMapper.map(mot, vehicleDetails);
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
						coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc() );	
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setIsselected(filterCover.get(0).getIsSelected());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
						coverRes.setSubCoverId(null);
						coverRes.setSubCoverDesc(null);
						coverRes.setSubCoverName(null);
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
						coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
						coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
						coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
						coverRes.setExchangeRate(filterCover.get(0).getExchangeRate());	
						
						// Discount Covers Or Promo Covers
						List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 coverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 coverRes.setLoadings(lodings);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate( filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( PolicyCoverData subCovers : filterSubCover) {
							Cover subCoverRes = new Cover();
							subCoverRes = dozerMapper.map(subCovers, Cover.class);
							subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
							subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
							subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc() );	
							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
							subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate());	
							

							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
							subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
							subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
							subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
							
							
							// Discount Covers Or Promo Covers
							List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
							}
							
							// Tax Covers
							List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
							
							if( filterTaxCover.size() > 0 ) {
								 List<Tax> taxes = getTaxRates(filterTaxCover) ;
								 subCoverRes.setTaxes(taxes);	
							}
							
							// Loginds Covers
							List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
							
							if( filterLodingCover.size() > 0 ) {
								 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
								 subCoverRes.setLoadings(lodings);	
							}
							subCoverListRes.add(subCoverRes);
						}
						coverRes.setSubcovers(subCoverListRes);
					}
					coverListRes.add(coverRes);
				}
				coverListRes.sort(Comparator.comparing(Cover :: getCoverId));;
				// Response
				MotorProductDetailsRes motorRes = new MotorProductDetailsRes();
				motorRes.setVehicleDetails(vehicleDetails);		
				motorRes.setCovers(coverListRes);
				motorResList.add(motorRes);				
			}
			viewRes.setProductDetails(motorResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	

	public ViewQuoteRes getBuildingProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<EserviceBuildingDetails> buildDatas = eserBuildRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<EserviceSectionDetails> secDatas =  eserSecRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<BuildingProductDetailsRes>   motorResList = new ArrayList<BuildingProductDetailsRes>();
			for (EserviceSectionDetails sec :  secDatas) {
				EserviceBuildingDetails buildData = buildDatas.stream().filter( o -> o.getRiskId().equals(sec.getRiskId()) ).collect(Collectors.toList()).get(0);
				// Build
				EserviceBuildingsDetailsRes buildingRes = new  EserviceBuildingsDetailsRes()  ;
				dozerMapper.map(buildData, buildingRes);
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(sec.getRiskId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
						coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
						coverRes.setPremiumExcluedTax( filterCover.get(0).getPremiumExcludedTaxFc() );	
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setIsselected(filterCover.get(0).getIsSelected());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
						coverRes.setSubCoverId(null);
						coverRes.setSubCoverDesc(null);
						coverRes.setSubCoverName(null);
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
						coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
						coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
						coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
						coverRes.setExchangeRate(filterCover.get(0).getExchangeRate());	
						
						// Discount Covers Or Promo Covers
						List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 coverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 coverRes.setLoadings(lodings);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( PolicyCoverData subCovers : filterSubCover) {
							Cover subCoverRes = new Cover();
							subCoverRes = dozerMapper.map(subCovers, Cover.class);
							subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
							subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
							subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
							subCoverRes.setPremiumExcluedTax( filterSubCover.get(0).getPremiumExcludedTaxFc() );	
							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
							subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate());	
							

							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
							subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
							subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
							subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
							
							
							// Discount Covers Or Promo Covers
							List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
							}
							
							// Tax Covers
							List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
							
							if( filterTaxCover.size() > 0 ) {
								 List<Tax> taxes = getTaxRates(filterTaxCover) ;
								 subCoverRes.setTaxes(taxes);	
							}
							
							// Loginds Covers
							List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
							
							if( filterLodingCover.size() > 0 ) {
								 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
								 subCoverRes.setLoadings(lodings);	
							}
							subCoverListRes.add(subCoverRes);
						}
						coverRes.setSubcovers(subCoverListRes);
					}
					coverListRes.add(coverRes);
				}
				coverListRes.sort(Comparator.comparing(Cover :: getCoverId));;
				// Response
				BuildingProductDetailsRes buildingProductRes = new BuildingProductDetailsRes();
				buildingProductRes.setBuildingDetails(buildingRes);		
				buildingProductRes.setCovers(coverListRes);
				motorResList.add(buildingProductRes);				
			}
			viewRes.setProductDetails(motorResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	
	public ViewQuoteRes getTravelProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Travel Data
			List<TravelPassengerDetails> travelDatas =  traPassRepo.findByQuoteNo(req.getQuoteNo());
			List<TravelPassengerDetails> adultDatas  = travelDatas.stream().filter( o -> o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> otherDatas  =  travelDatas.stream().filter( o -> ! o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> totalDatas  = new ArrayList<TravelPassengerDetails>();	
			totalDatas.addAll(adultDatas);
			totalDatas.addAll(otherDatas);
			
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<TravelProductDetailsRes>   travelResList = new ArrayList<TravelProductDetailsRes>();
			for (TravelPassengerDetails tra :  totalDatas) {
				
				// Mot
				TravelPassDetailsRes travelDetails = new  TravelPassDetailsRes()  ;
				dozerMapper.map(tra, travelDetails);
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(tra.getPassengerId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
						coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
						coverRes.setPremiumExcluedTax( filterCover.get(0).getPremiumExcludedTaxFc());	
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setIsselected(filterCover.get(0).getIsSelected());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
						coverRes.setSubCoverId(null);
						coverRes.setSubCoverDesc(null);
						coverRes.setSubCoverName(null);
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
						coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
						coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
						coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
						coverRes.setExchangeRate(filterCover.get(0).getExchangeRate());	
						
						// Discount Covers Or Promo Covers
						List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 coverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 coverRes.setLoadings(lodings);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( PolicyCoverData subCovers : filterSubCover) {
							Cover subCoverRes = new Cover();
							subCoverRes = dozerMapper.map(subCovers, Cover.class);
							subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
							subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
							subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
							subCoverRes.setPremiumExcluedTax( filterSubCover.get(0).getPremiumExcludedTaxFc() );	
							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
							subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate());	
							

							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
							subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
							subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
							subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
							
							
							// Discount Covers Or Promo Covers
							List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
							}
							
							// Tax Covers
							List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
							
							if( filterTaxCover.size() > 0 ) {
								 List<Tax> taxes = getTaxRates(filterTaxCover) ;
								 subCoverRes.setTaxes(taxes);	
							}
							
							// Loginds Covers
							List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
							
							if( filterLodingCover.size() > 0 ) {
								 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
								 subCoverRes.setLoadings(lodings);	
							}
							subCoverListRes.add(subCoverRes);
						}
						coverRes.setSubcovers(subCoverListRes);
					}
					coverListRes.add(coverRes);
				}
				coverListRes.sort(Comparator.comparing(Cover :: getCoverId));;
				// Response
				TravelProductDetailsRes traRes = new TravelProductDetailsRes();
				traRes.setTravelPassengerDetails(travelDetails);		
				traRes.setCovers(coverListRes);
				travelResList.add(traRes);				
			}
			viewRes.setProductDetails(travelResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public ViewQuoteRes getCommonProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<CommonDataDetails> commonDatas =  commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<CommonProductDetailsRes>   commonResList = new ArrayList<CommonProductDetailsRes>();
			for (CommonDataDetails com :  commonDatas) {
				
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(com.getRiskId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
						coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc() );	
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setIsselected(filterCover.get(0).getIsSelected());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
						coverRes.setSubCoverId(null);
						coverRes.setSubCoverDesc(null);
						coverRes.setSubCoverName(null);
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
						coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
						coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
						coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
						coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
						coverRes.setExchangeRate(filterCover.get(0).getExchangeRate());	
						
						// Discount Covers Or Promo Covers
						List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 coverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 coverRes.setLoadings(lodings);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate( filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( PolicyCoverData subCovers : filterSubCover) {
							Cover subCoverRes = new Cover();
							subCoverRes = dozerMapper.map(subCovers, Cover.class);
							subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
							subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
							subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc() );	
							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
							subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate());	
							

							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
							subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
							subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
							subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
							subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
							
							
							// Discount Covers Or Promo Covers
							List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
							}
							
							// Tax Covers
							List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
							
							if( filterTaxCover.size() > 0 ) {
								 List<Tax> taxes = getTaxRates(filterTaxCover) ;
								 subCoverRes.setTaxes(taxes);	
							}
							
							// Loginds Covers
							List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
							
							if( filterLodingCover.size() > 0 ) {
								 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
								 subCoverRes.setLoadings(lodings);	
							}
							subCoverListRes.add(subCoverRes);
						}
						coverRes.setSubcovers(subCoverListRes);
					}
					coverListRes.add(coverRes);
				}
				coverListRes.sort(Comparator.comparing(Cover :: getCoverId));;
				// Response
				CommonProductDetailsRes commonRes = new CommonProductDetailsRes();
				// Mot
				CommonDetailsRes commonDetails = new  CommonDetailsRes()  ;
				dozerMapper.map(com, commonDetails);
				
				commonRes.setCommonDetails(commonDetails);		
				commonRes.setCovers(coverListRes);
				commonResList.add(commonRes);				
			}
			viewRes.setProductDetails(commonResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public List<Discount> getDiscountRates(List<PolicyCoverData> filterDiscountCover) {
		List<Discount> DiscountList = new  ArrayList<Discount>();
		try {
			for (PolicyCoverData disc :  filterDiscountCover ) {
				Discount discount = new Discount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc());
				discount.setDiscountCalcType(disc.getCalcType());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				discount.setDiscountDesc(disc.getCoverName());	
				discount.setDiscountRate(disc.getRate()==null?"0.0" :disc.getRate().toString());
				discount.setFactorTypeId(disc.getFactorTypeId()==null?"" : disc.getFactorTypeId().toString());
				discount.setMaxAmount(disc.getMinimumPremium());
				discount.setSubCoverId(disc.getSubCoverId().toString());
				discount.setDiscountforId(disc.getDependentCoverId()==null?null:disc.getDependentCoverId().toString());
				
				DiscountList.add(discount);
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return DiscountList;
	}
	
	
	public List<Loading> getLodingCovers(List<PolicyCoverData> filterLodingCover) {
		List<Loading> LodingList = new  ArrayList<Loading>();
		try {
			for (PolicyCoverData lod :  filterLodingCover ) {
				Loading loding = new Loading();
				loding.setFactorTypeId(lod.getFactorTypeId()==null?null:lod.getFactorTypeId().toString());
				loding.setLoadingAmount(lod.getMinimumPremium());
				loding.setLoadingCalcType(lod.getCalcType());
				loding.setLoadingDesc(lod.getCoverName());
				loding.setLoadingforId(lod.getDependentCoverId()==null?null:lod.getDependentCoverId().toString());
				loding.setLoadingId(lod.getDiscLoadId()==null?null:lod.getDiscLoadId().toString());
				loding.setLoadingRate(lod.getRate()==null?null:lod.getRate().toString());
				loding.setMaxAmount(lod.getPremiumIncludedTaxFc());
				//loding.setSubCoverId(lod.getLodingSubcoverId()==null?null:lod.getLodingSubcoverId().toString());	
				LodingList.add(loding);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return LodingList;
	}
	
	public List<Tax> getTaxRates(List<PolicyCoverData> filterTaxCover) {
		List<Tax> TaxList = new  ArrayList<Tax>();
		try {
			for (PolicyCoverData tax :  filterTaxCover ) {
				Tax taxes = new Tax();
				taxes.setCalcType(tax.getCalcType());
				taxes.setIsTaxExempted(tax.getIsTaxExtempted());
				taxes.setTaxAmount(tax.getTaxAmount());
				taxes.setTaxDesc(tax.getTaxDesc());
				taxes.setTaxExemptCode(tax.getTaxExemptCode());
				taxes.setTaxExemptType(tax.getTaxExemptType());
				taxes.setTaxId(tax.getTaxId()==null?null:tax.getTaxId().toString()) ;
				taxes.setTaxRate(tax.getTaxRate()==null?null : Double.valueOf(tax.getTaxRate().toString()));
				TaxList.add(taxes);
			
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return TaxList;
	}


	@Override
	public List<Error> validateReferralStatus(AdminReferalStatusReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getAdminLoginId())) {
				errors.add(new Error("01","AdminLoginID","Please Enter Admin LoginId"));
			}
			
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("02","ReferralStatus","Please Select Referral Status"));
			} else if ( !(req.getStatus().equalsIgnoreCase("RP") || req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR")) ) {
				errors.add(new Error("02","ReferralStatus","Please Select Valid Referral Status Accept/Reject/Pending"));
			} else if ( req.getStatus().equalsIgnoreCase("RR")  ) {
				if(StringUtils.isBlank(req.getRejectReason())) {
					errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
				}
				
			} 
			
			if(StringUtils.isNotBlank(req.getStatus()) && (req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR") ) &&  StringUtils.isBlank(req.getAdminRemarks())) {
				errors.add(new Error("03","Admin Remarks","Please Enter Admin Remarks"));
			}
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return errors;
	}

	@Override
	public QuoteUpdateRes updateReferralStatus(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			
			if( req.getProductId().equalsIgnoreCase(motorProductId)) {
				updateRes = motorReferalUpdate(req);
				
			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = travelReferalUpdate(req);
				
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId)) {
				updateRes = buildingReferalUpdate(req);
			}  else {
				updateRes = commonReferalUpdate(req);
			} 
			
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	
	
//----------------------------------------MOTOR REFFERAL UPDATE ------------------------------------------------------------------//	
	public QuoteUpdateRes motorReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceMotorDetails mot : motorDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getVehicleId().equals(mot.getRiskId()) ).collect(Collectors.toList());
					
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(mot.getRiskId());
					vehDeh.setSectionId(mot.getSectionId());
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
			// Update Mot Status 
			for ( EserviceMotorDetails mot : motorDatas ) {
				mot.setStatus(req.getStatus());
				mot.setAdminLoginId(req.getAdminLoginId());
				mot.setAdminRemarks(req.getAdminRemarks());
				mot.setRejectReason(req.getRejectReason());
				mot.setUpdatedDate(new Date());
				eserMotRepo.saveAndFlush(mot);
				
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

// Common Referal Update
	public QuoteUpdateRes commonReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceCommonDetails> commonDatas = eserCommonRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceCommonDetails com : commonDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getVehicleId().equals(com.getRiskId()) ).collect(Collectors.toList());
					
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(com.getRiskId());
					vehDeh.setSectionId(com.getSectionId());
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
			// Update Mot Status 
			for ( EserviceCommonDetails com : commonDatas ) {
				com.setStatus(req.getStatus());
				com.setAdminLoginId(req.getAdminLoginId());
				com.setAdminRemarks(req.getAdminRemarks());
				com.setRejectReason(req.getRejectReason());
				com.setUpdatedDate(new Date());
				eserCommonRepo.saveAndFlush(com);
				
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	
//----------------------------------------- TRAVEL REFERRAL FLOW ------------------------------------------------------------------//
	public QuoteUpdateRes travelReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			EserviceTravelDetails travelData  = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<EserviceTravelGroupDetails> groupDatas = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(req.getRequestReferenceNo());
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceTravelGroupDetails tra : groupDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getVehicleId().equals(tra.getGroupId())
							&& o.getProductId().toString().equals(travelData.getProductId())  && o.getSectionId().toString().equals(travelData.getSectionId())
							).collect(Collectors.toList());
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(tra.getGroupId());
					vehDeh.setSectionId(travelData.getSectionId() );
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
			// Update Travel Status 
			
			travelData.setStatus(req.getStatus());
			travelData.setAdminLoginId(req.getAdminLoginId());
			travelData.setAdminRemarks(req.getAdminRemarks());
			travelData.setRejectReason(req.getRejectReason());
			travelData.setUpdatedDate(new Date());
			eserTraRepo.saveAndFlush(travelData);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

	public QuoteUpdateRes buildingReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceBuildingDetails> buildingDatas = eserBuildRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			List<EserviceSectionDetails>  secDatas = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo()); 
		// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceSectionDetails sec : secDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					List<FactorRateRequestDetails> filterCover = coverDatas.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(sec.getSectionId())) && o.getVehicleId().equals(sec.getRiskId()) ).collect(Collectors.toList());
					
					for (FactorRateRequestDetails cov :  filterCover ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(sec.getRiskId());
					vehDeh.setSectionId(sec.getSectionId());	
					vehicleIdsList.add(vehDeh);
				}
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				req2.setManualReferralYn("N");
				req2.setReferralRemarks("");
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
			// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
			// Update Mot Status 
			for ( EserviceBuildingDetails build : buildingDatas ) {
				build.setStatus(req.getStatus());
				build.setAdminLoginId(req.getAdminLoginId());
				build.setAdminRemarks(req.getAdminRemarks());
				build.setRejectReason(req.getRejectReason());
				build.setUpdatedDate(new Date());
				eserBuildRepo.saveAndFlush(build);
				
			}
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}

	
	@Override
	public SuccessRes deleteOldQuoteRecord(DeleteOldQuoteReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String subUserType = "" ;
			if( req.getProductId().equalsIgnoreCase(motorProductId) ) {
				
				Long motorInfo =  motorRepo.countByQuoteNo(req.getQuoteNo());
				if (motorInfo > 0  ) {
					motorRepo.deleteByQuoteNo(req.getQuoteNo());
				}
				
			} else if( req.getProductId().equalsIgnoreCase(travelProductId) ) {
			//  // Delete Old Record
				Long travelInfo =  traPassRepo.countByQuoteNo(req.getQuoteNo());
				if (travelInfo > 0  ) {
					//Delete data
					List<TravelPassengerDetails> oldPassDatas = 	traPassRepo.findByQuoteNo(req.getQuoteNo());
					subUserType = oldPassDatas.get(0).getSubUserType();
					if (! subUserType.equalsIgnoreCase("b2c") ) {
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
				
				}	
			}
			
			// Remove Covers
			if (! subUserType.equalsIgnoreCase("b2c") ) { 
				Long coverInfo =  coverRepo.countByQuoteNo(req.getQuoteNo());
	 			if (coverInfo >0 ) {
	 				//Delete data
	 				coverRepo.deleteByQuoteNo(req.getQuoteNo() );
	 				
	 			}
			}
 			
			res.setResponse("Old Record Removed ");
			res.setSuccessId(req.getQuoteNo());
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SectionWiseSumInsuredRes sectionWiseSuminsuredDetails(SectionSumInsuredGetReq req) {
		SectionWiseSumInsuredRes res = new SectionWiseSumInsuredRes();
		try {
			 if(req.getProductId().equalsIgnoreCase(buildingProductId )) {
				 BuildingSumInsuredDetails builSum  = buildingSuminsuredDetails(req);
				 res.setProductSuminsuredDetails(builSum);	
			}
			res.setQuoteNo(req.getQuoteNo());
			
			res.setRequestReferenceNo(req.getRequestReferenceNo());
			res.setProductId(req.getProductId());
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public BuildingSumInsuredDetails buildingSuminsuredDetails(SectionSumInsuredGetReq req) {
		BuildingSumInsuredDetails res = new BuildingSumInsuredDetails();
		try {
			EserviceBuildingDetails build  = eserBuildRepo.findByQuoteNo(req.getQuoteNo());
			List<EserviceSectionDetails>   buildSections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(build.getRequestReferenceNo());	
			List<EservicePersonalAccidentDetails> paccDatas = eserPaccRepo.findByRequestReferenceNo(build.getRequestReferenceNo());

			List<String> sectionIds = buildSections.stream().filter( o -> o.getRiskId().equals(build.getRiskId() )).map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
			
					
			List<SectionCoverMaster> sectionCovers  = getSectionCovers(build.getCompanyId() ,build.getProductId() , sectionIds );
			List<PolicyCoverData>   covers = polCoverRepo.findByQuoteNo(req.getQuoteNo())	;
			
			List<EserviceSectionDetails>   filterSections = buildSections.stream().filter( o -> o.getRiskId().equals(build.getRiskId()) ).collect(Collectors.toList());
			BigDecimal buildingSuminsured = null;
			BigDecimal allriskSuminsured = null;
			 List<OccupationReqClass> occupation = new ArrayList<OccupationReqClass>(); 
	//		BigDecimal paPermanentdisablementSuminsured = null;
	//		BigDecimal paTotaldisabilitySumInsured = null;
	//		BigDecimal PaMedicalSuminsured = null;
			BigDecimal personalIntSuminsured = null;
			BigDecimal contentSuminsured = null;
			
			for (EserviceSectionDetails sec : filterSections) {
				List<SectionCoverMaster> filterCovers =  sectionCovers.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(sec.getSectionId()))  
							&& o.getProductId().equals(Integer.valueOf(sec.getProductId()))	).collect(Collectors.toList());
				
				List<PolicyCoverData>   filterPolCovers = covers.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(sec.getSectionId()))  
						&& o.getProductId().equals(Integer.valueOf(sec.getProductId())) &&  o.getTaxId().equals(0) &&  o.getDiscLoadId().equals(0)  ).collect(Collectors.toList());
				
				for(PolicyCoverData cover :  filterPolCovers) {
					String sumInsuredColumn = filterCovers.stream().filter( o -> o.getCoverId().equals(cover.getCoverId() )
							&& o.getSubCoverId().equals(cover.getSubCoverId() )).collect(Collectors.toList()).get(0).getCoverBasedOn();
					if(sumInsuredColumn.equalsIgnoreCase("buildingSuminsured")) {
						buildingSuminsured =cover.getSumInsured();
						
					} else if(sumInsuredColumn.equalsIgnoreCase("allriskSuminsured")) {
						allriskSuminsured =cover.getSumInsured();
						
					}  else if(sumInsuredColumn.equalsIgnoreCase("paDeathSuminsured")) {
						OccupationReqClass  occ = new OccupationReqClass(); 
						List<EservicePersonalAccidentDetails> filterPacc = paccDatas.stream().filter( o -> o.getRiskId().equals(cover.getVehicleId())	).collect(Collectors.toList());				
						occ.setOccupationType(cover.getVehicleId().toString());
						occ.setSumInsuredTotal( cover.getSumInsured()==null?"" : cover.getSumInsured().toString());
						occ.setCount(filterPacc.get(0).getCount().toString());
						occupation.add(occ);
						
					} else if(sumInsuredColumn.equalsIgnoreCase("contentSuminsured")) {
						contentSuminsured =cover.getSumInsured();
					}  else if(sumInsuredColumn.equalsIgnoreCase("personalIntSuminsured")) {
						personalIntSuminsured =cover.getSumInsured();
						
					} 
//						else if(sumInsuredColumn.equalsIgnoreCase("paPermanentdisablementSuminsured")) {
//							paPermanentdisablementSuminsured =cover.getSumInsured()==null?null : new BigDecimal(cover.getSumInsured());
//							
//						}  else if(sumInsuredColumn.equalsIgnoreCase("paTotaldisabilitySumInsured")) {
//							paTotaldisabilitySumInsured =cover.getSumInsured()==null?null : new BigDecimal(cover.getSumInsured());
//							
//						}  else if(sumInsuredColumn.equalsIgnoreCase("PaMedicalSuminsured")) {
//							PaMedicalSuminsured =cover.getSumInsured()==null?null : new BigDecimal(cover.getSumInsured());
//							
//						} 
				}
				
			}
			res.setBuildingSuminsured(buildingSuminsured == null?"" :buildingSuminsured.toString());
			res.setAllriskSuminsured(allriskSuminsured == null?"" :allriskSuminsured.toString());
		//	res.setPaDeathSuminsured(paDeathSuminsured == null?"" :paDeathSuminsured.toString());
		//	res.setPaPermanentdisablementSuminsured(paPermanentdisablementSuminsured == null?"" :paPermanentdisablementSuminsured.toString());
		//	res.setPaTotaldisabilitySumInsured(paTotaldisabilitySumInsured == null?"" :paTotaldisabilitySumInsured.toString());
		//	res.setPaMedicalSuminsured(PaMedicalSuminsured == null?"" :PaMedicalSuminsured.toString());
			res.setPersonalIntermediarySuminsured(personalIntSuminsured == null?"" :personalIntSuminsured.toString());
			res.setContentSuminsured(contentSuminsured == null?"" :contentSuminsured.toString());
			res.setOccupationDetails(occupation);
			res.setRiskId(build.getRiskId().toString());
			res.setSectionId(sectionIds);		
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public List<SectionCoverMaster>  getSectionCovers(String companyId , String productId ,List<String> sectionIds) {
		List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
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
			CriteriaQuery<SectionCoverMaster> query = cb.createQuery(SectionCoverMaster.class);
			
			// Find All
			Root<SectionCoverMaster> c = query.from(SectionCoverMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("coverName")));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<SectionCoverMaster> ocpm1 = effectiveDate.from(SectionCoverMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			javax.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			javax.persistence.criteria.Predicate a4 = cb.equal(c.get("coverId"), ocpm1.get("coverId"));
			javax.persistence.criteria.Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a5);
			// Effective Date End
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<SectionCoverMaster> ocpm2 = effectiveDate2.from(SectionCoverMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("coverId"), ocpm2.get("coverId"));
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId") );
			Predicate a9 = cb.equal(c.get("productId"), ocpm2.get("productId") );
			Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6,a7,a8,a9,a10);
					
			//In 
			Expression<String>e0=c.get("sectionId");
			// Where
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), companyId);
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			javax.persistence.criteria.Predicate n5 =e0.in(sectionIds)	;
			javax.persistence.criteria.Predicate n6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			query.where(n1, n2, n3, n4, n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<SectionCoverMaster> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public List<Error> validateQuoteStatus(UpdateQuoteStatusReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getLoginId())) {
				errors.add(new Error("01","LoginID","Please Enter LoginId"));
			}
			
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("02","QuoteStatus","Please Select Quote Status"));
			}
			
			if(StringUtils.isNotBlank(req.getStatus()) && req.getStatus().equalsIgnoreCase("R") && StringUtils.isBlank(req.getRejectReason())  )  {
				errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
			} else if(StringUtils.isNotBlank(req.getRequestReferenceNo()) )  {
				List<HomePositionMaster> homeDatas = homeRepo.findByRequestReferenceNo(req.getRequestReferenceNo());  
				if(homeDatas.size() >0 ) {
					List<HomePositionMaster> filterDatas = homeDatas.stream().filter( o ->   StringUtils.isNotBlank(o.getPolicyNo())).collect(Collectors.toList());
					if(filterDatas.size() >0 ) {
						errors.add(new Error("03","Status Update","Can not  Update Status. Already Policy Converted For This Quote. PolicyNo : " + filterDatas.get(0).getPolicyNo() )) ;	
					}
				}
			}
			
			if(StringUtils.isBlank(req.getRequestReferenceNo()) )  {
				errors.add(new Error("03","RequestReferenceNo","Please Enter RequestReferenceNo"));
			} 
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return errors;
	}

	@Override
	@Transactional
	public QuoteUpdateRes updateQuoteStatus(UpdateQuoteStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			
			if( req.getProductId().equalsIgnoreCase(motorProductId)) {
				updateRes = updateMotorPorductStatus(req) ;
				
			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = updateTravelPorductStatus(req);
				
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId)) {
				updateRes = updateBuildingPorductStatus(req);
			} else {
				updateRes = updateCommonPorductStatus(req);
			}
			
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes ;
	}
	
	
	 public  QuoteUpdateRes updateMotorPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Motor Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceMotorDetails> update = cb.createCriteriaUpdate(EserviceMotorDetails.class);
				// set the root class
				Root<EserviceMotorDetails> m = update.from(EserviceMotorDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Motor Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
					// set the root class
					Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
    			    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		 
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
    		   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 
	 
	 public  QuoteUpdateRes updateTravelPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Travel Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceTravelDetails> update = cb.createCriteriaUpdate(EserviceTravelDetails.class);
				// set the root class
				Root<EserviceTravelDetails> m = update.from(EserviceTravelDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Travel Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<TravelPassengerDetails> update = cb.createCriteriaUpdate(TravelPassengerDetails.class);
					// set the root class
					Root<TravelPassengerDetails> m = update.from(TravelPassengerDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 public  QuoteUpdateRes updateBuildingPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Building Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceBuildingDetails> update = cb.createCriteriaUpdate(EserviceBuildingDetails.class);
				// set the root class
				Root<EserviceBuildingDetails> m = update.from(EserviceBuildingDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
//    		   // Motor Data Details Update
//    		   {
//	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
//					// create update
//					CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
//					// set the root class
//					Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
//					// set update and where clause
//					update.set("rejectReason", req.getRejectReason());
//					update.set("status", req.getStatus());
//					update.set("updatedDate", req.getLoginId());
//					update.set("updatedBy", new Date() );
//					
//					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
//					update.where(n1);
//					// perform update
//					em.createQuery(update).executeUpdate();
//					
//    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
	    	   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	 
	 public  QuoteUpdateRes updateCommonPorductStatus(UpdateQuoteStatusReq req  ) {
		 QuoteUpdateRes res = new QuoteUpdateRes() ;
	       try {
	    	    // Eservice Common Update
    		   {
    		    CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<EserviceCommonDetails> update = cb.createCriteriaUpdate(EserviceCommonDetails.class);
				// set the root class
				Root<EserviceCommonDetails> m = update.from(EserviceCommonDetails.class);
				// set update and where clause
				update.set("rejectReason", req.getRejectReason());
				update.set("status", req.getStatus());
				update.set("updatedDate",  new Date());
				update.set("updatedBy",req.getLoginId() );
				
				Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
				update.where(n1);
				// perform update
				em.createQuery(update).executeUpdate();
				
    		   }
    		   // Common Data Details Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<CommonDataDetails> update = cb.createCriteriaUpdate(CommonDataDetails.class);
					// set the root class
					Root<CommonDataDetails> m = update.from(CommonDataDetails.class);
					// set update and where clause
					update.set("rejectReason", req.getRejectReason());
					update.set("status", req.getStatus());
					update.set("updatedDate",  new Date());
					update.set("updatedBy",req.getLoginId() );
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		  // Home Position Master Update
    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<HomePositionMaster> update = cb.createCriteriaUpdate(HomePositionMaster.class);
					// set the root class
					Root<HomePositionMaster> m = update.from(HomePositionMaster.class);
					// set update and where clause
					update.set("status", req.getStatus());
					
					Predicate n1 = cb.equal(m.get("requestReferenceNo"), req.getRequestReferenceNo());
					update.where(n1);
					// perform update
					em.createQuery(update).executeUpdate();
					
    		   }
    		   
    		   // Resposne 
    		   res.setResponse("Status Updated Successfully");
    		   res.setRequestReferenceNo(req.getRequestReferenceNo());
    		   
	         } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }

	
}

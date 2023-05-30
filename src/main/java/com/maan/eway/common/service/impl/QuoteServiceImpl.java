package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
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

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductMaster;
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
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.DocumentDetails;
import com.maan.eway.common.res.DriverDetailsRes;
import com.maan.eway.common.res.EserviceCommonGetRes;
import com.maan.eway.common.res.EserviceMotorDetailsRes;
import com.maan.eway.common.res.EserviceTravelGetRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.PaccGetRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.master.controller.ProductGroupDropDownReq;
import com.maan.eway.master.req.TrackingDetailsSaveReq;
import com.maan.eway.master.res.ProductGroupMasterDropDownRes;
import com.maan.eway.master.service.ProductGroupMasterService;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalAccidentRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
import com.maan.eway.res.BuildingLocationDetails;
import com.maan.eway.res.BuildingSumInsuredDetails;
import com.maan.eway.res.CommonSumInsuredDetails;
import com.maan.eway.res.CoverRes;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.OccupationReqClass;
import com.maan.eway.res.PassengerSectionDetails;
import com.maan.eway.res.SectionDetails;
import com.maan.eway.res.SectionWiseSumInsuredRes;
import com.maan.eway.res.SubCoverRes;
import com.maan.eway.res.SuccessRes;
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
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	
	@Value(value = "${burglary.productId}")
	private String burglaryProductId;
	
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private QuoteThreadService otSer ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository custRepo ;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo ;
	
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
	private BuildingRiskDetailsRepository buildRiskRepo  ;
	
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
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepo;
	
	@Autowired
private BuildingDetailsRepository BuildingRepo;
	
	@Autowired
	private ProductGroupMasterService groupService;
		
	
	@Autowired
	private PersonalAccidentRepository personalRepo;


	private ProductMasterRepository productRepo;
	
	@Autowired
	private TrackingDetailsService trackingService;
	
	
	private Logger log = LogManager.getLogger(QuoteServiceImpl.class);
	
	@Override
	public CommonRes generateNewQuote(NewQuoteReq req) {
			CommonRes	res = otSer.call_OT_Insert(req);
			return res ;
			
	}

	@Override
	public ViewQuoteRes viewQuoteDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		
		try {
			// Quote Details
			HomePositionMaster homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());
			QuoteDetailsRes quoteRes = new QuoteDetailsRes();
			DozerBeanMapper dozerMappper = new DozerBeanMapper();
			quoteRes = dozerMappper.map(homeData, QuoteDetailsRes.class);
			quoteRes.setOverAllPremiumFc(homeData.getOverallPremiumFc()==null?"":homeData.getOverallPremiumFc().toPlainString() );
			quoteRes.setOverAllPremiumLc(homeData.getOverallPremiumLc()==null?"":homeData.getOverallPremiumLc().toPlainString());
			quoteRes.setPremiumFc(homeData.getPremiumFc()==null?"":homeData.getPremiumFc().toPlainString() );
			quoteRes.setPremiumLc(homeData.getPremiumLc()==null?"":homeData.getPremiumLc().toPlainString());
			quoteRes.setAdminRemarks(homeData.getAdminRemarks());
			quoteRes.setReferalRemarks(homeData.getReferralDescription());
			quoteRes.setBrokerBranchCode(homeData.getBrokerBranchCode());
			quoteRes.setBrokerCode(homeData.getBrokerCode());
			quoteRes.setHavepromocode(homeData.getHavepromoYn());
			quoteRes.setPromocode(homeData.getPromocode());
			quoteRes.setBdmCode(homeData.getBdmCode());
			quoteRes.setSourceType(homeData.getSourceType());
			quoteRes.setUserType(homeData.getUserType());
			quoteRes.setSubUserType(homeData.getSubUserType());	
			quoteRes.setProductName(homeData.getProductName());
			quoteRes.setCompanyName(homeData.getCompanyName());
			quoteRes.setCustomerCode(homeData.getCustomerCode());
			quoteRes.setBranchName(homeData.getBranchName());
			quoteRes.setBrokerBranchName(homeData.getBrokerBranchName());		
			quoteRes.setEmiYn("N");
			quoteRes.setEndtTypeId(homeData.getEndtTypeId());
			quoteRes.setEndtTypeDesc(homeData.getEndtTypeDesc()==null?"":homeData.getEndtTypeDesc());
			quoteRes.setEndtCategDesc(homeData.getEndtCategDesc()==null?null:homeData.getEndtCategDesc());
			quoteRes.setEndorsementRemarks(homeData.getEndorsementRemarks()==null?null:homeData.getEndorsementRemarks());
			quoteRes.setEndorsementEffdate(homeData.getEndorsementEffdate()==null?null:homeData.getEndorsementEffdate());
			quoteRes.setEndtPrevPolicyNo(homeData.getEndtPrevPolicyNo()==null?null:homeData.getEndtPrevPolicyNo());
			quoteRes.setEndtPrevQuoteNo(homeData.getEndtPrevQuoteNo()==null?null:homeData.getEndtPrevQuoteNo());
			quoteRes.setEndtCount(homeData.getEndtCount()==null?0:homeData.getEndtCount().intValue());
			quoteRes.setIsChargeOrRefund(homeData.getIsChargRefund()==null?"":homeData.getIsChargRefund());
			quoteRes.setPolicyNo(homeData.getPolicyNo()==null?"":homeData.getPolicyNo());
			quoteRes.setOriginalPolicyNo(homeData.getOriginalPolicyNo()==null?"":homeData.getOriginalPolicyNo());
			quoteRes.setEndtPremium(homeData.getEndtPremium()==null?BigDecimal.ZERO:homeData.getEndtPremium());
			req.setEndtTypeId(homeData.getEndtTypeId());
			quoteRes.setEndtPremiumTax(homeData.getEndtPremiumTax()==null?BigDecimal.ZERO:homeData.getEndtPremiumTax());
			quoteRes.setTotalEndtPremium(quoteRes.getEndtPremium().add(quoteRes.getEndtPremiumTax()));
			// Emi Details 
			List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNoAndCompanyIdAndProductId(homeData.getQuoteNo() ,homeData.getCompanyId() , homeData.getProductId().toString());
			if (emiDetails.size()>0 ) {
				List<EmiTransactionDetails> filterEmi =  emiDetails.stream().filter( o -> (!o.getPaymentStatus().equalsIgnoreCase("Accepted")) &&  ( o.getInstalment().equalsIgnoreCase("0") || o.getInstalment()!=null ) ).collect(Collectors.toList());
				if(filterEmi.size()>0   ) {
					quoteRes.setEmiYn("Y");
					quoteRes.setInstallmentPeriod(filterEmi.get(0).getInstallmentPeriod());
					quoteRes.setInstallmentMonth(filterEmi.get(0).getInstalment() );
					quoteRes.setDueAmount(filterEmi.get(0).getDueAmount()==null?"":new BigDecimal(filterEmi.get(0).getDueAmount()).toPlainString());
				}
			}
				
			
			// Customer Details
			PersonalInfo custData = custRepo.findByCustomerId(homeData.getCustomerId());
			CustomerDetailsRes  custRes = new CustomerDetailsRes();
			custRes  = dozerMappper.map(custData, CustomerDetailsRes.class);
			
			// Motor Product Details
			if( homeData.getProductId().equals(Integer.valueOf(motorProductId))) {
				viewRes =  getMotorProductDetails( req);
				
			} else if( homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
				// Travel Product Details
				viewRes =	getTravelProductDetails( req);
				
			} else if( homeData.getProductId().equals(Integer.valueOf(buildingProductId)) ||
					homeData.getProductId().equals(Integer.valueOf(smeProductId)) || homeData.getProductId().equals(Integer.valueOf(burglaryProductId)) ) {
				// Travel Product Details
				viewRes =	getBuildingProductDetails( req);
				
			} else {
				// Travel Product Details
				viewRes =	getCommonProductDetails( req);
				
			}
			
			viewRes.setCustomerDetails(custRes);
			viewRes.setQuoteDetails(quoteRes);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}

	public ViewQuoteRes getMotorProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<MotorDataDetails> motorDatas =  motorRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(),"D");
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(),"D");
			
			List<MotorDriverDetails> driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
			List<EserviceMotorDetailsRes>   motorResList = new ArrayList<EserviceMotorDetailsRes>();
			
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();			
			for (MotorDataDetails mot :  motorDatas) {
				EserviceMotorDetailsRes vehicleDetails = new  EserviceMotorDetailsRes()  ;
				 List<BrokerCommissionDetails> policylist = getPolicyName(mot.getCompanyId() , mot.getProductId().toString(), mot.getCreatedBy(),mot.getAgencyCode(), mot.getPolicyType());
				 Double commissionPercent =0.0;
			
				 if(policylist.size()>0 && policylist!=null) {

				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
				 }
				 else {
					 commissionPercent =5.0;
				 }
				 String premiumFc = mot.getOverallPremiumFc().toString();
				 String vatPremiumFc =	mot.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(new BigDecimal(commissionPercent))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);
	
				// Mot
				dozerMapper.map(mot, vehicleDetails);
				vehicleDetails.setOverAllPremiumFc(mot.getOverallPremiumFc()==null?0: mot.getOverallPremiumFc() );
				vehicleDetails.setOverAllPremiumLc(mot.getOverallPremiumLc()==null?0:mot.getOverallPremiumLc());
				vehicleDetails.setPremiumFc(mot.getActualPremiumFc()==null?0:mot.getActualPremiumFc() );
				vehicleDetails.setPremiumLc(mot.getActualPremiumLc()==null?0:mot.getActualPremiumLc());
				vehicleDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				vehicleDetails.setCommissionPercentage(commissionPercent.toString()==null?"":commissionPercent.toString());
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
				BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscount()!=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscountLC()!=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscount()!=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscountLC()!=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTax = (coverListRes.stream().filter(o -> o.getPremiumExcluedTax()!=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumExcluedTaxLC()!=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTax = (coverListRes.stream().filter(o -> o.getPremiumIncludedTax()!=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumIncludedTaxLC()!=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
				
				// Driver Details
				List<DriverDetailsRes>   driverResList = new ArrayList<DriverDetailsRes>();
				List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				for (MotorDriverDetails dri :  filterDriverList) {
					DriverDetailsRes driverRes  = new DriverDetailsRes();  
					dozerMapper.map(dri, driverRes);
					driverRes.setLicenseNo(dri.getIdNumber());
					
					driverResList.add(driverRes);
					
				}
				vehicleDetails.setRiskId(mot.getVehicleId());
				driverResList.sort(Comparator.comparing(DriverDetailsRes :: getDriverId  ));
				vehicleDetails.setDriverDetails(driverResList);
				vehicleDetails.setDocumentsTitle(mot.getSectionName());			
				vehicleDetails.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
			
				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
				sec.setSectionName( mot.getSectionName());
				sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
				sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
				sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
				sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
				sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
				sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
				sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
				sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());

				sec.setCovers(coverListRes);
				
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				vehicleDetails.setSectionDetails(sectionList);
				
				// Document 
				DocumentDetails  document = new DocumentDetails();
				document.setDocumentTitle(mot.getChassisNumber() + "~" + mot.getVehicleMakeDesc() + "~" + mot.getVehcileModelDesc()) ;
				document.setRiskId(mot.getVehicleId());
				document.setSectionId(mot.getSectionId().toString());
				documentDetails.add(document);
				
				// Response
				motorResList.add(vehicleDetails);		
			}
			viewRes.setRiskDetails(motorResList);
			viewRes.setDocumentDetails(documentDetails);
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
			
			BuildingRiskDetails buildData = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
			List<EserviceSectionDetails> secDatas =  eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(buildData.getRequestReferenceNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			
			// Building Details 
			// Build
			// Section Details
			// Document
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();			
			
			List<PaccGetRes> paccGetResList = new ArrayList<PaccGetRes>(); 
			List<EserviceBuildingsDetailsRes>   buildList = new ArrayList<EserviceBuildingsDetailsRes>();
			EserviceBuildingsDetailsRes buildingRes = new  EserviceBuildingsDetailsRes()  ;
			dozerMapper.map(buildData, buildingRes);
			buildingRes.setDocumentsTitle(buildData.getProductDesc());	
			
			
			//Broker Commission 
			List<BrokerCommissionDetails> policylist = getPolicyName(buildData.getCompanyId() , buildData.getProductId().toString(), buildData.getCreatedBy(),buildData.getAgencyCode(),"99999");
			 Double commissionPercent = 0.0;
			if(policylist.size()>0 && policylist!=null) {
			commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
			 	}
			else {
			 commissionPercent = 5.0; 
			}
			 String premiumFc = buildData.getOverallPremiumFc().toString();
			 String vatPremiumFc =	buildData.getOverallPremiumFc().toString();
			 BigDecimal commission=	new BigDecimal(premiumFc)
		 				.multiply(new BigDecimal(commissionPercent))
 						.divide(BigDecimal.valueOf(100D))
 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
 						.getPrecision(),RoundingMode.HALF_UP);
			 buildingRes.setOverAllPremiumFc(buildData.getOverallPremiumFc()==null?0D: Double.valueOf(buildData.getOverallPremiumFc().toString()));
			 buildingRes.setOverAllPremiumLc(buildData.getOverallPremiumLc()==null?0D:Double.valueOf(buildData.getOverallPremiumLc().toString()));
			 buildingRes.setPremiumFc(buildData.getActualPremiumFc()==null?0:Double.valueOf(buildData.getActualPremiumFc().toString()));
			 buildingRes.setPremiumLc(buildData.getActualPremiumLc()==null?0:Double.valueOf(buildData.getActualPremiumLc().toString()));
			 buildingRes.setCommissionAmount(commission==null?"":commission.toString());
			 buildingRes.setCommissionPercentage(commissionPercent==null?"":commissionPercent.toString());
			 buildingRes.setInsuranceForId(buildData.getInsuranceForId()!=null ? Arrays.asList(buildData.getInsuranceForId().split(",")) : null )  ;
			
			
			List<SectionDetails>  buildingSectionList = new ArrayList<SectionDetails>();
			for (EserviceSectionDetails sec :  secDatas) {
//				if( sec.getSectionId().equalsIgnoreCase("35") ) {
//					List<CommonDataDetails> accData =  	commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
//					for (CommonDataDetails	 acc : accData ) {
//						
//						List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(acc.getRiskId())) &&
//								 o.getSectionId().toString().equals(acc.getSectionId()) ).collect(Collectors.toList());
//					
//						Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
//						
//						List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
//
//						BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscount()!=null ) .map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumAfterDiscountLC()!=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscount()!=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter(o -> o.getPremiumBeforeDiscountLC()!=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumExcluedTax = (coverListRes.stream().filter(o -> o.getPremiumExcluedTax()!=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumExcluedTaxLC()!=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumIncludedTax = (coverListRes.stream().filter(o -> o.getPremiumIncludedTax()!=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
//						BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter(o -> o.getPremiumIncludedTaxLC()!=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
//
//						// Accident
////						PaccGetRes pacRes = new  PaccGetRes()  ;
////						dozerMapper.map(acc, pacRes);
////						pacRes.setOccupationType(acc.getRiskId().toString() );
////						pacRes.setOccupationTypeDesc(acc.getOccupationDesc());
////						pacRes.setSuminsured(acc.getSumInsured()==null?"":acc.getSumInsured().toPlainString());
////						pacRes.setRiskId(acc.getRiskId().toString());
////						pacRes.setDocumentsTitle(acc.getSectionDesc() + "-" + acc.getOccupationDesc());
////						pacRes.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
////						List<SectionDetails>  paSectionList = new ArrayList<SectionDetails>();
////						SectionDetails secData = new SectionDetails(); 
////						secData.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
////						secData.setSectionName( acc.getSectionDesc());
////						secData.setCovers(coverListRes);
////						paSectionList.add(secData);
////						pacRes.setSectionDetails(paSectionList);
////						paccGetResList.add(pacRes);
//						SectionDetails buildSec = new SectionDetails(); 
//						buildSec.setSectionId(acc.getSectionId()==null?"":acc.getSectionId().toString());
//						buildSec.setSectionName( acc.getSectionDesc());
//
//						buildSec.setPremiumAfterDiscount(PremiumAfterDiscount==null?"":PremiumAfterDiscount.toString());
//						buildSec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc==null?"":PremiumAfterDiscountLc.toString());
//						buildSec.setPremiumBeforeDiscount(PremiumBeforeDiscount==null?"":PremiumBeforeDiscount.toString());
//						buildSec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc==null?"":PremiumBeforeDiscountLc.toString());
//						buildSec.setPremiumExcluedTax(PremiumExcluedTax==null?"":PremiumExcluedTax.toString());
//						buildSec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc==null?"":PremiumExcluedTaxLc.toString());
//						buildSec.setPremiumIncludedTax(PremiumIncludedTax==null?"":PremiumIncludedTax.toString());
//						buildSec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc==null?"":PremiumIncludedTaxLc.toString());
//
//						buildSec.setCovers(coverListRes);
//						buildingSectionList.add(buildSec);
//						
//					}
					
			//	} else {
//					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(sec.getRiskId())) &&
//							o.getCompanyId().equals(sec.getCompanyId()) && o.getProductId().toString().equals(sec.getProductId()) && o.getSectionId().toString().equals(sec.getSectionId()) ).collect(Collectors.toList());
//				
//					Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
//					
//					List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
//					// Build
//					SectionDetails buildSec = new SectionDetails(); 
//					BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumExcluedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumIncludedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
//					BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
//					
//					buildSec.setSectionId(sec.getSectionId()==null?"":sec.getSectionId().toString());
//					buildingRes.setSectionId(StringUtils.isBlank(buildingRes.getSectionId() ) ? sec.getSectionId()==null?"":sec.getSectionId().toString() :buildingRes.getSectionId()  );
//					buildSec.setSectionName( sec.getSectionDesc());
//					buildSec.setCovers(coverListRes);
//					buildSec.setPremiumAfterDiscount(PremiumAfterDiscount==null?"":PremiumAfterDiscount.toString());
//					buildSec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc==null?"":PremiumAfterDiscountLc.toString());
//					buildSec.setPremiumBeforeDiscount(PremiumBeforeDiscount==null?"":PremiumBeforeDiscount.toString());
//					buildSec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc==null?"":PremiumBeforeDiscountLc.toString());
//					buildSec.setPremiumExcluedTax(PremiumExcluedTax==null?"":PremiumExcluedTax.toString());
//					buildSec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc==null?"":PremiumExcluedTaxLc.toString());
//					buildSec.setPremiumIncludedTax(PremiumIncludedTax==null?"":PremiumIncludedTax.toString());
//					buildSec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc==null?"":PremiumIncludedTaxLc.toString());
//					buildingSectionList.add(buildSec);
					
				else {
					List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(sec.getRiskId())) &&
							o.getCompanyId().equals(sec.getCompanyId()) && o.getProductId().toString().equals(sec.getProductId()) && o.getSectionId().toString().equals(sec.getSectionId()) ).collect(Collectors.toList());
				
					Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
					
					List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
					// Build
					SectionDetails buildSec = new SectionDetails(); 
					BigDecimal PremiumAfterDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscount = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTax = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
					BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().filter( o -> o.getPremiumAfterDiscount() !=null ).map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
					
					buildSec.setSectionId(sec.getSectionId()==null?"":sec.getSectionId().toString());
					buildingRes.setSectionId(StringUtils.isBlank(buildingRes.getSectionId() ) ? sec.getSectionId()==null?"":sec.getSectionId().toString() :buildingRes.getSectionId()  );
					buildSec.setSectionName( sec.getSectionName());
					buildSec.setCovers(coverListRes);
					buildSec.setPremiumAfterDiscount(PremiumAfterDiscount==null?"":PremiumAfterDiscount.toString());
					buildSec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc==null?"":PremiumAfterDiscountLc.toString());
					buildSec.setPremiumBeforeDiscount(PremiumBeforeDiscount==null?"":PremiumBeforeDiscount.toString());
					buildSec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc==null?"":PremiumBeforeDiscountLc.toString());
					buildSec.setPremiumExcluedTax(PremiumExcluedTax==null?"":PremiumExcluedTax.toString());
					buildSec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc==null?"":PremiumExcluedTaxLc.toString());
					buildSec.setPremiumIncludedTax(PremiumIncludedTax==null?"":PremiumIncludedTax.toString());
					buildSec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc==null?"":PremiumIncludedTaxLc.toString());
					buildingSectionList.add(buildSec);
					
				}
				
			} 
			buildingRes.setSectionDetails(buildingSectionList);
			
			buildList.add(buildingRes);
			List<Object> totalList = new ArrayList<Object>(); 
			totalList.addAll(buildList);
			totalList.addAll(paccGetResList);
			
			// Location Wise Details
			List<BuildingDetails> buildingRiskDatas = BuildingRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			List<BuildingLocationDetails> buildLocList = new ArrayList<BuildingLocationDetails>();
			for(BuildingDetails data : buildingRiskDatas) {
//				BuildingLocationDetails loc = new BuildingLocationDetails();
//				loc.setDocumentsTitle( "Location - " +  data.getLocationName());
//				loc.setLocationId(data.getRiskId().toString());
//				loc.setLocationName(data.getLocationName());
//				loc.setRiskId(data.getRiskId().toString());
//				loc.setSuminsured(data.getBuildingSuminsured()==null?"" : data.getBuildingSuminsured().toPlainString());
//				loc.setSectionId("99999");
//				buildLocList.add(loc);
				
				// Document 
				DocumentDetails  document = new DocumentDetails();
				document.setDocumentTitle( "Location - " +  data.getLocationName());
				document.setRiskId(data.getRiskId().toString());
				document.setSectionId("99999");
				documentDetails.add(document);
				
				
			}
			totalList.addAll(buildLocList);
			
			viewRes.setRiskDetails(totalList);
			viewRes.setDocumentDetails(documentDetails);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public List<CoverRes> getCoverDetails(Map<Integer,List<PolicyCoverData>> groupByCover  ) {
		List<CoverRes>  coverListRes = new ArrayList<CoverRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for ( Integer coverId : groupByCover.keySet() ) {
				List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
				CoverRes coverRes = new CoverRes();
				
				if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), CoverRes.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
					coverRes.setIsselected(filterCover.get(0).getIsSelected());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
					coverRes.setCoverageType(filterCover.get(0).getCoverageType());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
									
//					// Discount Covers Or Promo Covers
//					List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
//					
//					if ( filterDiscountCover.size() > 0 ) {
//						 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//						 coverRes.setDiscounts(discounts);	
//					}
//					
//					// Tax Covers
//					List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//					
//					if( filterTaxCover.size() > 0 ) {
//						 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//						 coverRes.setTaxes(taxes);	
//					}
//					
//					// Loginds Covers
//					List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//					
//					if( filterLodingCover.size() > 0 ) {
//						 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//						 coverRes.setLoadings(lodings);	
//					}
										
				} else {
					
					// Get Sub Covers
			
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());
					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					
					List<SubCoverRes>  subCoverListRes = new ArrayList<SubCoverRes>();
					List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
					for ( PolicyCoverData subCovers : filterSubCover) {
						SubCoverRes subCoverRes = new SubCoverRes();
						subCoverRes = dozerMapper.map(subCovers, SubCoverRes.class);
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
						coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
						coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
						coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
//						// Discount Covers Or Promo Covers
//						List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
//						
//						if ( filterDiscountCover.size() > 0 ) {
//							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//							 subCoverRes.setDiscounts(discounts);	
//						}
//						
//						// Tax Covers
//						List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//						
//						if( filterTaxCover.size() > 0 ) {
//							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//							 subCoverRes.setTaxes(taxes);	
//						}
//						
//						// Loginds Covers
//						List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//						
//						if( filterLodingCover.size() > 0 ) {
//							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//							 subCoverRes.setLoadings(lodings);	
//						}
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
	
			coverListRes.sort(Comparator.comparing(CoverRes :: getCoverId));;
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return coverListRes;
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
			
			ProductGroupDropDownReq groupReq = new ProductGroupDropDownReq();
			groupReq.setBranchCode(travelDatas.get(0).getBranchCode());
			groupReq.setInsuranceId(travelDatas.get(0).getCompanyId());		
			groupReq.setProductId(travelDatas.get(0).getProductId().toString());	
			
			List<ProductGroupMasterDropDownRes> groupRes =	groupService.getProductGroupMasterDropdown(groupReq);
			
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<EserviceTravelGetRes>   travelResList = new ArrayList<EserviceTravelGetRes>();
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();
			
			
			for (TravelPassengerDetails tra :  totalDatas) {
				EserviceTravelGetRes travelDetails = new  EserviceTravelGetRes()  ;
				dozerMapper.map(tra, travelDetails);
				travelDetails.setRiskId(tra.getPassengerId().toString());
				travelDetails.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
				travelDetails.setPassengerId(tra.getPassengerId().toString());
				travelDetails.setPassengerName(tra.getPassengerName());
				
				List<PassengerSectionDetails>  SectionList = new ArrayList<PassengerSectionDetails>();	
				 List<BrokerCommissionDetails> policylist = getPolicyName(tra.getCompanyId() , tra.getProductId().toString(), tra.getCreatedBy(),tra.getBrokerCode(), tra.getSectionId().toString());
				 Double commissionPercent =0.0;
				 if(policylist.size()>0 && policylist!=null) {
				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
				 }
				 else {
				 commissionPercent =5.0;
				 }
				 String premiumFc = tra.getOverallPremiumFc().toString();
				 String vatPremiumFc =	tra.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(new BigDecimal(commissionPercent))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);
	
				 travelDetails.setOverAllPremiumFc(tra.getOverallPremiumFc()==null?0: tra.getOverallPremiumFc() );
				 travelDetails.setOverAllPremiumLc(tra.getOverallPremiumLc()==null?0:tra.getOverallPremiumLc());
				 travelDetails.setPremiumFc(tra.getActualPremiumFc()==null?0:tra.getActualPremiumFc() );
				 travelDetails.setPremiumLc(tra.getActualPremiumLc()==null?0:tra.getActualPremiumLc());
				 travelDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				 travelDetails.setCommissionPercentage(commissionPercent.toString()==null?"":commissionPercent.toString());

				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(tra.getPassengerId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
				BigDecimal PremiumAfterDiscount = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTax = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTax = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());
				
//				// Response
//				List<PassengerSectionDetails> secList = new ArrayList<PassengerSectionDetails>();
//				// Passenger
//				PassengerSectionDetails traSec = new PassengerSectionDetails(); 
//				
//				traSec.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
//				traSec.setSectionName( tra.getSectionName());
//				traSec.setCovers(coverListRes);
//				traSec.setPassengerId(tra.getPassengerId().toString() );
//				traSec.setPassengerName(tra.getPassengerName());
//				traSec.setGroupDesc(groupRes.stream().filter( o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()) ).collect(Collectors.toList()).get(0).getCodeDesc()) ;		
//				traSec.setGroupId(tra.getGroupId().toString());
//				secList.add(traSec);
//				travelDetails.setSectionDetails(secList);
				
				// Document 
				DocumentDetails  document = new DocumentDetails();
				document.setDocumentTitle(tra.getPassengerName());
				document.setRiskId(tra.getPassengerId().toString());
				document.setSectionId(tra.getSectionId().toString());
				documentDetails.add(document);
				
				
				PassengerSectionDetails sec = new PassengerSectionDetails();
				sec.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
				sec.setSectionName( tra.getSectionName());
				sec.setCovers(coverListRes);
				sec.setPassengerId(tra.getPassengerId().toString() );
				sec.setPassengerName(tra.getPassengerName());
				sec.setGroupDesc(groupRes.stream().filter( o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()) ).collect(Collectors.toList()).get(0).getCodeDesc()) ;		
				sec.setGroupId(tra.getGroupId().toString());
				sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
				sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
				sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
				sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
				sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
				sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
				sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
				sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());
			
				SectionList.add(sec);
				travelDetails.setSectionDetails(SectionList);	
				travelResList.add(travelDetails);
			}
		
			viewRes.setRiskDetails(travelResList);	
			viewRes.setDocumentDetails(documentDetails);
			
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
			
			List<EserviceCommonGetRes>   commonResList = new ArrayList<EserviceCommonGetRes>();
			List<DocumentDetails> documentDetails = new ArrayList<DocumentDetails>();
			for (CommonDataDetails com :  commonDatas) {
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(com.getRiskId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
				BigDecimal PremiumAfterDiscount = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumAfterDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumAfterDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscount = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscount ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumBeforeDiscountLc = (coverListRes.stream().map(CoverRes:: getPremiumBeforeDiscountLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTax = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumExcluedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumExcluedTaxLC ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTax = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTax ).reduce((x, y) -> x.add(y)).get());
				BigDecimal PremiumIncludedTaxLc = (coverListRes.stream().map(CoverRes:: getPremiumIncludedTaxLC ).reduce((x, y) -> x.add(y)).get());

				// Response
				// Mot
				 List<BrokerCommissionDetails> policylist = getPolicyName(com.getCompanyId() , com.getProductId().toString(), com.getCreatedBy(),com.getAgencyCode(),"99999");
			
				 Double commissionPercent = 0.0;
					if(policylist.size()>0 && policylist!=null) {
					
				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
					}
					else {
						commissionPercent =5.0;
					}
				 String premiumFc = com.getOverallPremiumFc().toString();
				 String vatPremiumFc =	com.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(new BigDecimal(commissionPercent))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);

				
				EserviceCommonGetRes commonDetails = new  EserviceCommonGetRes()  ;
				dozerMapper.map(com, commonDetails);
				commonDetails.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				commonDetails.setOverAllPremiumFc(com.getOverallPremiumFc()==null?0D:Double.valueOf(com.getOverallPremiumFc().toString()));
				commonDetails.setOverAllPremiumLc(com.getOverallPremiumLc()==null?0D:Double.valueOf(com.getOverallPremiumLc().toString()));
				commonDetails.setPremiumFc(com.getActualPremiumFc()==null?0D:Double.valueOf(com.getActualPremiumFc().toString()));
				commonDetails.setPremiumLc(com.getActualPremiumLc()==null?0D:Double.valueOf(com.getActualPremiumLc().toString()));
				commonDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				commonDetails.setCommissionPercentage(commissionPercent.toString()==null?"":commissionPercent.toString());

				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				sec.setSectionName( com.getSectionDesc());
				sec.setPremiumAfterDiscount(PremiumAfterDiscount.toString()==null?"":PremiumAfterDiscount.toString());
				sec.setPremiumAfterDiscountLc(PremiumAfterDiscountLc.toString()==null?"":PremiumAfterDiscountLc.toString());
				sec.setPremiumBeforeDiscount(PremiumBeforeDiscount.toString()==null?"":PremiumBeforeDiscount.toString());
				sec.setPremiumBeforeDiscountLc(PremiumBeforeDiscountLc.toString()==null?"":PremiumBeforeDiscountLc.toString());
				sec.setPremiumExcluedTax(PremiumExcluedTax.toString()==null?"":PremiumExcluedTax.toString());
				sec.setPremiumExcluedTaxLc(PremiumExcluedTaxLc.toString()==null?"":PremiumExcluedTaxLc.toString());
				sec.setPremiumIncludedTax(PremiumIncludedTax.toString()==null?"":PremiumIncludedTax.toString());
				sec.setPremiumIncludedTaxLc(PremiumIncludedTaxLc.toString()==null?"":PremiumIncludedTaxLc.toString());

				sec.setCovers(coverListRes);
				
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				commonDetails.setSectionDetails(sectionList);
				commonResList.add(commonDetails);
				
				// Document 
				DocumentDetails  document = new DocumentDetails();
				document.setDocumentTitle(com.getCustomerName());
				document.setRiskId(com.getRiskId().toString());
				document.setSectionId(com.getSectionId());
				documentDetails.add(document);
			}
			viewRes.setRiskDetails(commonResList);	
			viewRes.setDocumentDetails(documentDetails);
			
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
			} else if ( !(req.getStatus().equalsIgnoreCase("RP") || req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR") ||  req.getStatus().equalsIgnoreCase("RE"))) {
				errors.add(new Error("02","ReferralStatus","Please Select Valid Referral Status Accept/Reject/Pending/Re-Quote"));
			} else if ( req.getStatus().equalsIgnoreCase("RR")  ) {
				if(StringUtils.isBlank(req.getRejectReason())) {
					errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
				}
				
			} 
			
			if(StringUtils.isNotBlank(req.getStatus()) && (req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR") || req.getStatus().equalsIgnoreCase("RE")) &&  StringUtils.isBlank(req.getAdminRemarks())) {
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
				//Mail Push Notification
					motorPushNotification(req);
				//Tracking Details
					trackingDetails(req);
					
			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = travelReferalUpdate(req);
				//Mail Push Notification
				 travelPushNotification(req);
				//Tracking Details
					trackingDetails(req);
				
			} else if( (req.getProductId().equalsIgnoreCase(buildingProductId)) || (req.getProductId().equalsIgnoreCase(smeProductId))
					|| (req.getProductId().equalsIgnoreCase(burglaryProductId))) {
				updateRes = buildingReferalUpdate(req);
				//Mail Push Notification
				 buildingPushNotification(req);
				//Tracking Details
					trackingDetails(req);
			}  else {
				updateRes = commonReferalUpdate(req);
				commonPushNotification(req);
				//Tracking Details
				trackingDetails(req);
			} 
			
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	//Tracking Details
	private QuoteUpdateRes trackingDetails(AdminReferalStatusReq req) {
		QuoteUpdateRes res=new QuoteUpdateRes();
	try {
	
		List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
		if( req.getProductId().equalsIgnoreCase(motorProductId)) {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			
			for(EserviceMotorDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
				
			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
			List<EserviceTravelDetails> cusRefNo = eserTraRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());
			for(EserviceTravelDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		} else if( req.getProductId().equalsIgnoreCase(buildingProductId)) {
			List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			for(EserviceBuildingDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		} else {
			List<EserviceCommonDetails> cusRefNo = eserCommonRepo
					.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

			for(EserviceCommonDetails motor:cusRefNo ) {
				if(! motor.getStatus().equalsIgnoreCase("D")  ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(req.getProductId());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus( req.getStatus());
					trackingReq.setBranchCode(motor.getBranchCode());
					trackingReq.setCompanyId(motor.getCompanyId());
					trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
					trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
					trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
					trackingReq.setCreatedby(req.getAdminLoginId());
					trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
					trackingReq.setRemarks(motor.getAdminRemarks());
					trackingReq1.add(trackingReq);
				}
				}
		}			
		
		trackingService.insertTrackingDetails(trackingReq1);
	} catch ( Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private QuoteUpdateRes motorPushNotification(AdminReferalStatusReq req) {

		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();
			//Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
			Customer cusReq = new Customer();
			if(customerData!=null) {
			cusReq.setCustomerMailid(customerData.getEmail1());
			cusReq.setCustomerName(customerData.getClientName());
			cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
			cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
			cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
			cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
			cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());
			}

			// UnderWriter Info
			List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
					underWriterReq.setUwLoginId(underWriterData.get("uwloginId")==null?"":underWriterData.get("uwloginId").toString());
					underWriterReq.setUwuserType(underWriterData.get("uwuserType")==null?"":underWriterData.get("uwuserType").toString());
					underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType")==null?"":underWriterData.get("uwsubuserType").toString());
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			//Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());
	
			
			if("RA".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Approved");
				n.setStatusMessage(req.getAdminRemarks());
			}else if("RP".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Pending");
				n.setStatusMessage(req.getAdminRemarks());				
			}else if("RR".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Rejected");
				n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
			}else if("RE".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("ReQuote");
				n.setStatusMessage(req.getAdminRemarks());
			}
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName("Motor");
			n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			
			n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
 
			// Calling pushNotification
			CommonRes res=notiService.pushNotification(n);
			if (res.getIsError()==null) {
				updateRes.setResponse("Pushed Successfuly");
				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	
	}
	

	// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//
	private QuoteUpdateRes travelPushNotification(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			
			List<EserviceTravelDetails> cusRefNo = eserviceTravelRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();
			// Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
			Customer cusReq = new Customer();
			if(customerData!=null) {
			cusReq.setCustomerMailid(customerData.getEmail1());
			cusReq.setCustomerName(customerData.getClientName());
			cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
			cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
			cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
			cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
			cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

			}
			// UnderWriter Info
			List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
					cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "" : underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO: new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO: new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(underWriterData.get("userName") == null ? "" : underWriterData.get("userName").toString());
					underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());
					underWriterReq.setUwLoginId(underWriterData.get("uwloginId")==null?"":underWriterData.get("uwloginId").toString());
					underWriterReq.setUwuserType(underWriterData.get("uwuserType")==null?"":underWriterData.get("uwuserType").toString());
					underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType")==null?"":underWriterData.get("uwsubuserType").toString());

					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			if("RA".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Approved");
				n.setStatusMessage(req.getAdminRemarks());
			}else if("RP".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Pending");
				n.setStatusMessage(req.getAdminRemarks());				
			}else if("RR".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Rejected");
				n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
			}else if("RE".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("ReQuote");
				n.setStatusMessage(req.getAdminRemarks());
			}
			// Common Info
			
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(5);
			n.setProductName("Travel");
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setStatusMessage("");
			n.getTinyUrl();
			n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

			// Calling pushNotification
			CommonRes res = notiService.pushNotification(n);
			if (res.getIsError() == null) {
				updateRes.setResponse("Pushed Successfuly");
				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	//----------------------------------------BUILDING UPDATE REFERRAL STATUS ------------------------------------------------------------------//
	private QuoteUpdateRes buildingPushNotification(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
			cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
					.collect(Collectors.toList());

			String loginId = "";
			if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
				loginId = cusRefNo.get(0).getLoginId();
			} else {
				loginId = cusRefNo.get(0).getApplicationId();
			}
			Notification n = new Notification();

			// Broker Info
			LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
			Broker brokerReq = new Broker();
			if(loginInfo!=null) {
			brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
			brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
			brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
			brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
			brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
			brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
			brokerReq.setBrokerName(loginInfo.getUserName());
			}
			// Customer Info
			EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
			Customer cusReq = new Customer();
			if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

			}

			// UnderWriter Info
			List<Tuple> underWriterList = getUnderWriterDetails(cusRefNo.get(0).getProductId(),
					cusRefNo.get(0).getCompanyId(), cusRefNo.get(0).getBranchCode(), cusRefNo.get(0).getLoginId());
			List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
			if (underWriterList != null) {
				for (Tuple underWriterData : underWriterList) {
					UnderWriter underWriterReq = new UnderWriter();
					underWriterReq.setUwMailid(
							underWriterData.get("userMail") == null ? "" : underWriterData.get("userMail").toString());
					underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc") == null ? null
							: Integer.valueOf(underWriterData.get("whatsappCodeDesc").toString()));
					underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo") == null ? BigDecimal.ZERO
							: new BigDecimal(underWriterData.get("whatsappNo").toString()));
					underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc") == null ? null
							: Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
					underWriterReq.setUwPhoneNo(underWriterData.get("userMobile") == null ? BigDecimal.ZERO
							: new BigDecimal(underWriterData.get("userMobile").toString()));
					underWriterReq.setUwName(
							underWriterData.get("userName") == null ? "" : underWriterData.get("userName").toString());
					underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());
					underWriterReq.setUwLoginId(underWriterData.get("uwloginId")==null?"":underWriterData.get("uwloginId").toString());
					underWriterReq.setUwuserType(underWriterData.get("uwuserType")==null?"":underWriterData.get("uwuserType").toString());
					underWriterReq.setUwsubuserType(underWriterData.get("uwsubuserType")==null?"":underWriterData.get("uwsubuserType").toString());

					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			if("RA".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Approved");
				n.setStatusMessage(req.getAdminRemarks());
			}else if("RP".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Pending");
				n.setStatusMessage(req.getAdminRemarks());				
			}else if("RR".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("Referral Rejected");
				n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
			}else if("RE".equalsIgnoreCase(req.getStatus())){
				n.setNotifTemplatename("ReQuote");
				n.setStatusMessage(req.getAdminRemarks());
			}
			
			// Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.PENDING);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(cusRefNo.get(0).getProductId()));
			n.setProductName(cusRefNo.get(0).getProductDesc());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionDesc());
			n.setStatusMessage("");
			n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			
			n.getTinyUrl();

			// Calling pushNotification
			CommonRes res = notiService.pushNotification(n);
			if (res.getIsError() == null) {
				updateRes.setResponse("Pushed Successfuly");
				updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
				updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
	// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
			private QuoteUpdateRes commonPushNotification(AdminReferalStatusReq req) {
				QuoteUpdateRes updateRes = new QuoteUpdateRes();
				try {
					List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
					
					cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
							.collect(Collectors.toList());

					String loginId = "";
					if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
						loginId = cusRefNo.get(0).getLoginId();
					} else {
						loginId = cusRefNo.get(0).getApplicationId();
					}
					Notification n = new Notification();
					//Broker Info
					LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
					Broker brokerReq = new Broker();
					if(loginInfo!=null) {
					brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?loginInfo.getUserName(): loginInfo.getCompanyName());
					brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
					brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
					brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
					brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
					brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
					brokerReq.setBrokerName(loginInfo.getUserName());
					}
					// Customer Info
					EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
					Customer cusReq = new Customer();
					if(customerData!=null) {
						cusReq.setCustomerMailid(customerData.getEmail1());
						cusReq.setCustomerName(customerData.getClientName());
						cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
						cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
						cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
						cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
						cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

					}

					
					// UnderWriter Info
					List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
					List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
					if (underWriterList != null) {
						for (Tuple underWriterData : underWriterList) {
							UnderWriter underWriterReq = new UnderWriter();
							underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
							underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
							underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
							underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
							underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
							underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
							underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
							underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
							underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());

							underWrite.add(underWriterReq);
						}
					}
					n.setUnderwriters(underWrite);
					//Company Info
					n.setCompanyid(cusRefNo.get(0).getCompanyId());
					n.setCompanyName(cusRefNo.get(0).getCompanyName());
					
					if("RA".equalsIgnoreCase(req.getStatus())){
						n.setNotifTemplatename("Referral Approved");
						n.setStatusMessage(req.getAdminRemarks());
					}else if("RP".equalsIgnoreCase(req.getStatus())){
						n.setNotifTemplatename("Referral Pending");
						n.setStatusMessage(req.getAdminRemarks());				
					}else if("RR".equalsIgnoreCase(req.getStatus())){
						n.setNotifTemplatename("Referral Rejected");
						n.setStatusMessage(StringUtils.isBlank(req.getAdminRemarks())?req.getRejectReason():req.getAdminRemarks());
					}else if("RE".equalsIgnoreCase(req.getStatus())){
						n.setNotifTemplatename("ReQuote");
						n.setStatusMessage(req.getAdminRemarks());
					}
					
					//Common Info
					n.setBroker(brokerReq);
					n.setCustomer(cusReq);
					n.setNotifcationDate(new Date());
					n.setNotifDescription("");
					n.setNotifPriority(0);
					n.setNotifPushedStatus(NotificationStatus.PENDING);
					n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
					n.setProductid(Integer.valueOf(req.getProductId()));
				//	ProductMaster productData= getByProductCode(Integer.valueOf(req.getProductId())) ;
					n.setProductName(cusRefNo.get(0).getProductDesc());
					n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
					n.setSectionName(cusRefNo.get(0).getSectionName());
				// Referral Noti , referral app,recj
					n.setRefNo(req.getRequestReferenceNo());
					n.setBranchCode(cusRefNo.get(0).getBranchCode());
					n.setCompanyid(cusRefNo.get(0).getCompanyId());

					n.getTinyUrl();

					// Calling pushNotification
					CommonRes res=notiService.pushNotification(n);
//					if (res.getIsError()==null) {
//						updateRes.setResponse("Pushed Successfuly");
//						updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
//						updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
//						updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());
	//
//					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return updateRes;
			}

	
	private List<Tuple> getUnderWriterDetails(String productId,String companyId,String branchCode,String loginId) {
		List<Tuple> list = new ArrayList<Tuple>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginBranchMaster> b = query.from(LoginBranchMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<LoginProductMaster> p = query.from(LoginProductMaster.class);
			query.multiselect( u.get("loginId").alias("loginId"), u.get("oaCode").alias("oaCode"), u.get("acExecutiveId").alias("acExecutiveId"),u.get("address1").alias("address1"), 
					   u.get("address2").alias("address2"),u.get("address3").alias("address3"), 
					   u.get("agencyCode").alias("agencyCode"),u.get("approvedPreparedBy").alias("approvedPreparedBy"), 
					   u.get("branchCode").alias("branchCode"),u.get("checkerYn").alias("checker"), 
					   u.get("cityCode").alias("cityCode"),u.get("cityName").alias("cityName"), 
					   u.get("commissionVatYn").alias("commissionVatYn"),u.get("companyName").alias("companyName"), 
					   u.get("contactPersonName").alias("contactPersonName"),u.get("coreAppBrokerCode").alias("coreAppBrokerCode"), 
					   u.get("countryCode").alias("countryCode"),u.get("countryName").alias("countryName"), 
					   u.get("createdBy").alias("createdBy"),u.get("custConfirmYn").alias("custConfirmYn"), 
					   u.get("customerId").alias("customerId"),u.get("designation").alias("designation"), 
					   u.get("effectiveDateStart").alias("effectiveDateStart"), 
					   u.get("entryDate").alias("entryDate"),u.get("fax").alias("fax"), 
					   u.get("makerYn").alias("makerYn"),u.get("missippiId").alias("missippiId"), 
					   u.get("mobileCode").alias("mobileCode"),u.get("mobileCodeDesc").alias("mobileCodeDesc"), 
					   u.get("pobox").alias("pobox"),u.get("remarks").alias("remarks"), 
					   u.get("stateCode").alias("stateCode"),u.get("stateName").alias("stateName"), 
					   u.get("status").alias("status"),u.get("updatedBy").alias("updatedBy"), 
					   u.get("updatedDate").alias("updatedDate"),u.get("userMail").alias("userMail"), 
					   u.get("userMobile").alias("userMobile"),u.get("userName").alias("userName"), 
					   u.get("vatRegNo").alias("vatRegNo"),u.get("whatsappCode").alias("whatsappCode"),
					   u.get("whatsappCodeDesc").alias("whatsappCodeDesc"),u.get("whatsappNo").alias("whatsappNo"),
					   l.get("userType").alias("userType"),l.get("subUserType").alias("subUserType"));
					   List<String> subUserType = new ArrayList<String>(); 
			subUserType.add("high");
			subUserType.add("both");
			//In 
			Expression<String>e0=cb.lower(l.get("subUserType"));
			//Where
			Predicate n1 = cb.equal(cb.lower(l.get("userType")), "issuer");
			Predicate n2 = e0.in(subUserType);
			Predicate n3 = cb.equal(l.get("companyId"),companyId);
			Predicate n4 = cb.equal(l.get("loginId"),u.get("loginId"));
			Predicate n5 = cb.equal(b.get("loginId"),(l.get("loginId")));
			Predicate n6 = cb.equal(p.get("loginId"),(l.get("loginId")));
			Predicate n7 = cb.equal(b.get("branchCode"),branchCode);
			Predicate n8 = cb.equal(p.get("productId"),productId);
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			Predicate n9 = cb.between(cb.literal(today),p.get("effectiveDateStart"), p.get("effectiveDateEnd"));
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9);
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
}
	//----------------------------------------MOTOR REFFERAL UPDATE ------------------------------------------------------------------//	
	public QuoteUpdateRes motorReferalUpdate(AdminReferalStatusReq req) {
		QuoteUpdateRes  updateRes = new QuoteUpdateRes(); 
		try {
			List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNoAndStatusNotOrderByRiskIdAsc(req.getRequestReferenceNo() , "D");
			
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
			}  else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
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
			}   else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
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
			}  else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
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
			}   else if (req.getStatus().equalsIgnoreCase("RE") ) {
				updateRes.setResponse("Referal Re-Quote");
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
			 if(req.getProductId().equalsIgnoreCase(buildingProductId ) || req.getProductId().equalsIgnoreCase(smeProductId )) {
				 BuildingSumInsuredDetails builSum  = buildingSuminsuredDetails(req);
				 res.setProductSuminsuredDetails(builSum);	
			} else {
				
				CommonSumInsuredDetails Sum  = commonSuminsuredDetails(req);
				res.setProductSuminsuredDetails(Sum);
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
			BuildingRiskDetails build  = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
			List<EserviceSectionDetails>   buildSections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(build.getRequestReferenceNo());	
			List<CommonDataDetails> paccDatas = commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());

			List<String> sectionIds = buildSections.stream().filter( o -> o.getRiskId().equals(build.getRiskId() )).map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
			
			 List<OccupationReqClass> occupation = new ArrayList<OccupationReqClass>(); 
//			 for (CommonDataDetails pac :  paccDatas) {
//				 OccupationReqClass occu = new OccupationReqClass(); 
//				 occu.setCount(pac.getCount()==null?"":pac.getCount().toString());		 
//				 occu.setOccupationType(pac.getOccupationType() );
//				 occu.setSumInsuredTotal(pac.getSumInsured()==null?"":pac.getSumInsured().toString());
//				 occupation.add(occu);
//				 
//			}
			res.setOccupationType(paccDatas.size()> 0 ? paccDatas.get(0).getOccupationType().toString() : "");
			res.setOccupationTypeDesc(build.getOccupationTypeDesc());
			res.setLiabilityOccupationId(build.getLiabilityOccupationId());
			res.setLiabilityOccupationDesc(build.getLiabilityOccupationDesc());
			res.setPersonalAccSuminsured(paccDatas.size()> 0 ? paccDatas.get(0).getSumInsured().toString() : "");
			res.setCount(paccDatas.size()> 0 ? paccDatas.get(0).getCount().toString() : "");
			
			res.setBuildingSuminsured(build.getBuildingSuminsured() == null?"0" :build.getBuildingSuminsured().toPlainString());
			res.setAllriskSuminsured(build.getAllriskSuminsured() == null?"0" :build.getAllriskSuminsured().toPlainString());
			res.setPersonalIntermediarySuminsured(build.getPersonalIntSuminsured() == null?"0" :build.getPersonalIntSuminsured().toPlainString());
			res.setContentSuminsured(build.getContentSuminsured() == null?"0" :build.getContentSuminsured().toPlainString());
		//	res.setOccupationDetails(occupation);
			res.setMoneySinglecarrySuminsured(build.getMoneySinglecarrySuminsured() == null?"0" :build.getMoneySinglecarrySuminsured().toPlainString());
			res.setMoneyAnnualcarrySuminsured(build.getMoneyAnnualcarrySuminsured() == null?"0" :build.getMoneyAnnualcarrySuminsured().toPlainString());
			res.setMoneyInsafeSuminsured(build.getMoneyInsafeSuminsured() == null?"0" :build.getMoneyInsafeSuminsured().toPlainString());
			res.setFidelityAnyoccuSuminsured(build.getFidelityAnyoccuSuminsured() == null?"0" :build.getFidelityAnyoccuSuminsured().toPlainString());
			res.setFidelityAnnualSuminsured(build.getFidelityAnnualSuminsured() == null?"0" :build.getFidelityAnnualSuminsured().toPlainString());
			res.setTpliabilityAnyoccuSuminsured(build.getTpliabilityAnyoccuSuminsured() == null?"0" :build.getTpliabilityAnyoccuSuminsured().toPlainString());
			res.setEmpliabilityAnnualSuminsured(build.getEmpliabilityAnnualSuminsured() == null?"0" :build.getEmpliabilityAnnualSuminsured().toPlainString());
			res.setEmpliabilityExcessSuminsured(build.getEmpliabilityExcessSuminsured() == null?"0" :build.getEmpliabilityExcessSuminsured().toPlainString());
			res.setElecEquipSuminsured(build.getElecEquipSuminsured() == null?"0" :build.getElecEquipSuminsured().toPlainString());
			res.setGoodsSinglecarrySuminsured(build.getGoodsSinglecarrySuminsured() == null?"0" :build.getGoodsSinglecarrySuminsured().toPlainString());
			res.setGoodsTurnoverSuminsured(build.getGoodsTurnoverSuminsured() == null?"0" :build.getGoodsTurnoverSuminsured().toPlainString());
			res.setCashInHandDirectors(build.getCashInHandDirectors() == null?"0" :build.getCashInHandDirectors().toPlainString());
			res.setCashInHandEmployees(build.getCashInHandEmployees() == null?"0" :build.getCashInHandEmployees().toPlainString());
			res.setCashInPremises(build.getCashInPremises() == null?"0" :build.getGoodsTurnoverSuminsured().toPlainString());
			res.setCashInSafe(build.getCashInSafe() == null?"0" :build.getCashInSafe().toPlainString());
			res.setCashInTransit(build.getCashInTransit() == null?"0" :build.getCashInTransit().toPlainString());
			res.setCashValueablesSi(build.getCashValueablesSi() == null?"0" :build.getCashValueablesSi().toPlainString());
			res.setRevenueFromStamps(build.getRevenueFromStamps() == null?"0" :build.getRevenueFromStamps().toPlainString());
			res.setMoneyInLocker(build.getMoneyInLocker() == null?"0" :build.getMoneyInLocker().toPlainString());
			res.setMoneyInPremises(build.getMoneyInPremises() == null?"0" :build.getMoneyInPremises().toPlainString());
			res.setMoneyInSafeBusiness(build.getMoneyInSafeBusiness() == null?"0" :build.getMoneyInSafeBusiness().toPlainString());
			res.setMoneyOutSafeBusiness(build.getMoneyOutSafeBusiness() == null?"0" :build.getMoneyOutSafeBusiness().toPlainString());
			res.setMoneyInPremises(build.getMoneyInPremises() == null?"0" :build.getMoneyInPremises().toPlainString());
			res.setMoneyAnnualcarrySuminsured(build.getMoneyAnnualcarrySuminsured() == null?"0" :build.getMoneyAnnualcarrySuminsured().toPlainString());
			res.setMoneySinglecarrySuminsured(build.getMoneySinglecarrySuminsured() == null?"0" :build.getMoneySinglecarrySuminsured().toPlainString());;
			
			res.setCurrencyId(build.getCurrency());
			res.setRiskId(build.getRiskId().toString());
			res.setSectionId(sectionIds);		
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	public CommonSumInsuredDetails commonSuminsuredDetails(SectionSumInsuredGetReq req) {
		CommonSumInsuredDetails res = new CommonSumInsuredDetails();
		try {
			List<CommonDataDetails> paccDatas = commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			CommonDataDetails pacc = paccDatas.get(0) ;
			List<EserviceSectionDetails>   sections = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(pacc.getRequestReferenceNo());	
			List<String> sectionIds = sections.stream().filter( o -> o.getRiskId().equals(pacc.getRiskId() )).map(EserviceSectionDetails :: getSectionId ).collect(Collectors.toList());
			
			res.setCurrencyId(pacc.getCurrency());
			res.setRiskId(pacc.getRiskId().toString());
			res.setSumInsured(pacc.getSumInsured() == null?"0" :pacc.getSumInsured().toPlainString());
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
				// Notification Trigger
				motorNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req);
				
			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				updateRes = updateTravelPorductStatus(req);
				// Notification Trigger
				travelNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req);
				
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId)) {
				updateRes = updateBuildingPorductStatus(req);
				// Notification Trigger
				buildingNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req);
			} else {
				updateRes = updateCommonPorductStatus(req);
				// Notification Trigger
				commonNotiReferralStatus(req);
				//Tracking Details
				trackingDetailsupdateQuoteStatus(req);
			}
			
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes ;
	}
	//Tracking Details
		private QuoteUpdateRes trackingDetailsupdateQuoteStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes res=new QuoteUpdateRes();
		try {
			
			List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
			if( req.getProductId().equalsIgnoreCase(motorProductId)) {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				for(EserviceMotorDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
					
				} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceTravelDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId)) {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceBuildingDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			} else {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo
						.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());

				for(EserviceCommonDetails motor:cusRefNo ) {
						TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
						trackingReq.setProductId(req.getProductId());
						trackingReq.setRiskId(motor.getRiskId().toString());
						trackingReq.setStatus( req.getStatus());
						trackingReq.setBranchCode(motor.getBranchCode());
						trackingReq.setCompanyId(motor.getCompanyId());
						trackingReq.setQuoteNo(motor.getQuoteNo()==null?"":motor.getQuoteNo().toString());
						trackingReq.setPolicyNo(motor.getPolicyNo()==null?"":motor.getPolicyNo().toString());
						trackingReq.setOriginalPolicyNo(motor.getOriginalPolicyNo()==null?"":motor.getOriginalPolicyNo().toString());
						trackingReq.setCreatedby(req.getLoginId());
						trackingReq.setRequestReferenceNo(req.getRequestReferenceNo());
						trackingReq.setRemarks(motor.getReferalRemarks());
						trackingReq1.add(trackingReq);
					
					}
			}			
			
			trackingService.insertTrackingDetails(trackingReq1);

		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
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
	 
	 private QuoteUpdateRes motorNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());

						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Motor");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.setStatusMessage("");
				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}

	 private QuoteUpdateRes travelNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceTravelDetails> cusRefNo = eserTraRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());

				}
				// Customer Info
				EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Travel");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	 private QuoteUpdateRes buildingNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceBuildingDetails> cusRefNo = eserviceBuildingRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName("Building");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionDesc());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	 private QuoteUpdateRes commonNotiReferralStatus(UpdateQuoteStatusReq req) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceCommonDetails> cusRefNo = eserCommonRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(), req.getProductId());
				
				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());

				String loginId = "";
				if (cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")||cusRefNo.get(0).getApplicationId().equalsIgnoreCase("01")) {
					loginId = cusRefNo.get(0).getLoginId();
				} else {
					loginId = cusRefNo.get(0).getApplicationId();
				}
				Notification n = new Notification();
				//Broker Info
				LoginUserInfo loginInfo = loginUserRepo.findByLoginId(loginId);
				Broker brokerReq = new Broker();
				if(loginInfo!=null) {
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = customerDetailsRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
				cusReq.setCustomerMailid(customerData.getEmail1());
				cusReq.setCustomerName(customerData.getClientName());
				cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
				cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
				cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
				cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
				cusReq.setCustomerRefno(cusRefNo.get(0).getCustomerReferenceNo());

				}

				// UnderWriter Info
				List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
				List<UnderWriter> underWrite = new ArrayList<UnderWriter>();
				if (underWriterList != null) {
					for (Tuple underWriterData : underWriterList) {
						UnderWriter underWriterReq = new UnderWriter();
						underWriterReq.setUwMailid(underWriterData.get("userMail") == null ? "": underWriterData.get("userMail").toString());
						underWriterReq.setUwMessengerCode(underWriterData.get("whatsappCodeDesc")==null?null :Integer.valueOf( underWriterData.get("whatsappCodeDesc").toString()));
						underWriterReq.setUwMessengerPhone(underWriterData.get("whatsappNo")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("whatsappNo").toString()));
						underWriterReq.setUwPhonecode(underWriterData.get("mobileCodeDesc")== null ? null:Integer.valueOf(underWriterData.get("mobileCodeDesc").toString()));
						underWriterReq.setUwPhoneNo(underWriterData.get("userMobile")== null ? BigDecimal.ZERO :new BigDecimal(underWriterData.get("userMobile").toString()));
						underWriterReq.setUwName(underWriterData.get("userName")==null ? "": underWriterData.get("userName").toString());
						underWriterReq.setUwLoginId(underWriterData.get("loginId")==null?"":underWriterData.get("loginId").toString());							
						underWriterReq.setUwuserType(underWriterData.get("userType")==null?"":underWriterData.get("userType").toString());
						underWriterReq.setUwsubuserType(underWriterData.get("subUserType")==null?"":underWriterData.get("subUserType").toString());
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
		
				
				if("RA".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Approved");
				}else if("RP".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Pending");
				}else if("RR".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("Referal Reject");
				}else if("RE".equalsIgnoreCase(req.getStatus())){
					n.setNotifTemplatename("ReQuote");
				}
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(req.getProductId()));
				n.setProductName(cusRefNo.get(0).getProductDesc());
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
				n.setStatusMessage("");
				n.setRefNo(req.getRequestReferenceNo());
			n.setBranchCode(cusRefNo.get(0).getBranchCode());
			n.setCompanyid(cusRefNo.get(0).getCompanyId());

				n.getTinyUrl();

				// Calling pushNotification
				CommonRes res=notiService.pushNotification(n);
				if (res.getIsError()==null) {
					updateRes.setResponse("Pushed Successfuly");
					updateRes.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
					updateRes.setCustomerId(cusRefNo.get(0).getCustomerReferenceNo());
					updateRes.setRequestReferenceNo(cusRefNo.get(0).getRequestReferenceNo().toString());

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
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
	 public ProductMaster getByProductCode(Integer productId) {
			ProductMaster res = new ProductMaster();
			try {
				Date today  =new Date();
				Calendar cal = new GregorianCalendar(); 
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today   = cal.getTime();
				
				List<ProductMaster> list = new ArrayList<ProductMaster>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ProductMaster> query = cb.createQuery(ProductMaster.class);
		
				// Find All
				Root<ProductMaster> b = query.from(ProductMaster.class);
		
				// Select
				query.select(b);
		
				// Amend ID Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<ProductMaster> ocpm1 = amendId.from(ProductMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				amendId.where(a1,a2);
		
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
		
				// Where
				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("productId"), productId);
		
				query.where(n1,n2).orderBy(orderList);
		
				// Get Result
				TypedQuery<ProductMaster> result = em.createQuery(query);
				list = result.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getProductId()))).collect(Collectors.toList());
				list.sort(Comparator.comparing(ProductMaster :: getProductName ));
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
	
}

package com.maan.eway.common.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CoverDocumentMaster;
import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentRefno;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SeqPaymentid;
import com.maan.eway.bean.TinyurlMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsHistoryReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.req.PaymentResUrlReq;
import com.maan.eway.common.req.SendSmsReq;
import com.maan.eway.common.req.TinyUrlGenerateReq;
import com.maan.eway.common.req.TinyUrlGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.LoginEncryptResponse;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.TinyUrlGetRes;
import com.maan.eway.common.res.TravelPassDetailsRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.error.Error;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentRefnoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.SeqPaymentidRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.service.CalculatorEngine;


@Service
@Transactional

public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private PaymentDetailRepository paymentdetailrepo;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;
	
	@Autowired
	private HomePositionMasterRepository homerepo;
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo;
	
	
	@Autowired
	private ListItemValueRepository listrepo;
	
	@Autowired
	private PersonalInfoRepository personalrepo;
	
	@Autowired
	private SeqPaymentidRepository seqPayIdrepo;
	
	@Autowired
	private PaymentRefnoRepository seqRefNorepo;
	
	@Autowired
	private CalculatorEngine calcService;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	
	@Autowired
	private LoginBranchMasterRepository lbranchRepo ;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
	@Autowired
	private CoverDocumentUploadDetailsRepository docUploadRepo ;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo ;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo ;
	
	@Autowired
	private TravelPassengerDetailsRepository passengerRepo ;
	
	@Autowired
	private CommonDataDetailsRepository commonRepo ;
	
	@Autowired
	private PaymentService paymentService ;
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private EserviceCustomerDetailsRepository eserCustRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired
	private SmsDetailsImpl smsRepo ;
	
	@Autowired
	private NotifTemplateMasterRepository notifRepo;
	
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

	Gson json = new Gson();
	@Override
	public List<Error> validatemakepayment(MakePaymentSaveReq req) {
		List<Error> error = new ArrayList<Error>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			if(StringUtils.isBlank(req.getEmiYn())) {
				error.add(new Error("01","EmiYn","Please select Emi Yes/No"));
			} else if(req.getEmiYn().equalsIgnoreCase("Y") ) {
				
				if(StringUtils.isBlank(req.getInstallmentMonth())) {
					error.add(new Error("01","InstallmentMonth","Please Enter InstallmentMonth"));
				}
				if(StringUtils.isBlank(req.getInstallmentPeriod())) {
					error.add(new Error("01","InstallmentPeriod","Please Enter InstallmentPeriod"));
				}
			}
			
			// Premium Validation
			if(StringUtils.isBlank(req.getPremium())) {
				error.add(new Error("01","Premium","Please Enter Premium"));
			} else if (! req.getPremium().matches("[0-9.]+") )  {
				error.add(new Error("01","Premium","Please Enter Valid Premium"));
				
			} else if (StringUtils.isNotBlank(req.getEmiYn()) && req.getEmiYn().equalsIgnoreCase("Y") && StringUtils.isNotBlank(req.getInstallmentMonth()) 
					&& StringUtils.isNotBlank(req.getInstallmentPeriod())  )  {
				EmiTransactionDetails  emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,req.getInstallmentMonth() , req.getInstallmentPeriod());
				String pattern = "#####0";
			 	DecimalFormat decimalFormat = new DecimalFormat(pattern);
			 	Double premium =  Double.valueOf (decimalFormat.format(Double.valueOf (req.getPremium())));
			 	Double overall =  Double.valueOf (decimalFormat.format(emiDetails.getDueAmount()));
				if(! premium.equals(overall) ) {
					error.add(new Error("01","Premium","Premium Mismatched. Given Premium : " + req.getPremium() + " Policy Premium :" + overall));
				}
			} else  {
				HomePositionMaster  findQuote = homerepo.findByQuoteNo(req.getQuoteNo());
				String pattern = "#####0";
			 	DecimalFormat decimalFormat = new DecimalFormat(pattern);
			 	Double premium =  Double.valueOf (decimalFormat.format( Double.valueOf (req.getPremium())));
			 	Double overall =  Double.valueOf (decimalFormat.format(findQuote.getOverallPremiumLc()));
			 	if(! premium.equals(overall)  ) {
					error.add(new Error("01","Premium","Premium Mismatched. Given Premium : " + premium + " Policy Premium :" +  overall));
				}
				
			}
			
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
			if(StringUtils.isBlank(req.getRemarks())) {
				error.add(new Error("01","Remarks","Please Enter Remarks"));
			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
		
			List<PaymentInfo> filterAccepted = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") ).collect(Collectors.toList());		
			
		
			if(filterAccepted.size()> 0) {
				if ( req.getEmiYn().equalsIgnoreCase("Y" ) && StringUtils.isNotBlank(req.getInstallmentMonth()) && StringUtils.isNotBlank(req.getInstallmentPeriod()) )  {
					List<PaymentInfo> filterEmi = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && o.getInstallmentMonth().equalsIgnoreCase(req.getInstallmentMonth()) && 
	  						o.getInstallmentPeriod().equalsIgnoreCase(req.getInstallmentPeriod()) ).collect(Collectors.toList());
					
					if(filterEmi.size()>0 ) {
						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
					}
				
				} else {
					error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
				}
				
				
			}
			
			// Doc Validation
			if ( StringUtils.isNotBlank( req.getQuoteNo())) {
				HomePositionMaster homeData = homerepo.findByQuoteNo(req.getQuoteNo());
				String companyId = homeData.getCompanyId() ;
				Integer productId =  homeData.getProductId() ;
				List<String> sectionIds = new ArrayList<String>(); 
				
				List<DocValidationReq> docValidateReqs = new ArrayList<DocValidationReq>() ;
				
				// Motor Product Specific Doc Valdiation
				if(homeData.getProductId().equals(Integer.valueOf(motorProductId)) ) {
					List<MotorDataDetails>  motorDatas = motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());	
					List<Integer> sectionList = motorDatas.stream().map(MotorDataDetails :: getSectionId ) .collect(Collectors.toList());
					sectionIds.addAll(Lists.transform(sectionList, Functions.toStringFunction()));
					sectionIds.add("99999");
					// Common Docs 
					
					
					// Other Docs
					for (MotorDataDetails mot : motorDatas) {
						DocValidationReq doc = new DocValidationReq();
						doc.setQuoteNo(mot.getQuoteNo() );
						doc.setProductId(String.valueOf(mot.getProductId()));
						doc.setProductDesc("Vehicle Id");
						doc.setRiskId(mot.getVehicleId() );
						doc.setSectionId(String.valueOf(mot.getSectionId()));
						doc.setSectionDesc(mot.getSectionName());
						docValidateReqs.add(doc);
						
					}
					
				} else if(homeData.getProductId().equals(Integer.valueOf(buildingProductId)) ) {
					List<EserviceSectionDetails>  buidingDatas = sectionRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());	
					sectionIds = buidingDatas.stream().map(EserviceSectionDetails ::  getSectionId ) .collect(Collectors.toList());
					sectionIds.add("99999");
					// Common Docs 
					
					
					// Other Docs
					for (EserviceSectionDetails mot : buidingDatas) {
						DocValidationReq doc = new DocValidationReq();
						doc.setQuoteNo(mot.getQuoteNo() );
						doc.setProductId(String.valueOf(mot.getProductId()));
						doc.setProductDesc("Risk Id");
						doc.setRiskId(mot.getRiskId().toString());
						doc.setSectionId(String.valueOf(mot.getSectionId()));
						doc.setSectionDesc(mot.getSectionDesc());
						docValidateReqs.add(doc);
						
					}
					
				} else if(homeData.getProductId().equals(Integer.valueOf(travelProductId)) ) {
					List<TravelPassengerDetails>  passDatas = passengerRepo.findByQuoteNoOrderByTravelIdAsc(req.getQuoteNo());	
					List<Integer> sectionList =passDatas.stream().map(TravelPassengerDetails :: getSectionId ) .collect(Collectors.toList());
					sectionIds.addAll(Lists.transform(sectionList, Functions.toStringFunction()));
					sectionIds.add("99999");
					// Common Docs 
					
					
					// Other Docs
					for (TravelPassengerDetails mot : passDatas) {
						DocValidationReq doc = new DocValidationReq();
						doc.setQuoteNo(mot.getQuoteNo() );
						doc.setProductId(String.valueOf(mot.getProductId()));
						doc.setProductDesc("Passenger Id");
						doc.setRiskId(mot.getPassengerId().toString() );
						doc.setSectionId(String.valueOf(mot.getSectionId()));
						doc.setSectionDesc(mot.getSectionName());
						docValidateReqs.add(doc);
						
					}
					
				} else  {
					List<CommonDataDetails>  commonDatas = commonRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());	
					sectionIds = commonDatas.stream().map(CommonDataDetails :: getSectionId ) .collect(Collectors.toList());
					sectionIds.add("99999");
					// Common Docs 
					
					
					// Other Docs
					for (CommonDataDetails mot : commonDatas) {
						DocValidationReq doc = new DocValidationReq();
						doc.setQuoteNo(mot.getQuoteNo() );
						doc.setProductId(String.valueOf(mot.getProductId()));
						doc.setProductDesc("Risk Id");
						doc.setRiskId(mot.getRiskId().toString() );
						doc.setSectionId(String.valueOf(mot.getSectionId()));
						doc.setSectionDesc(mot.getSectionDesc());
						docValidateReqs.add(doc);
						
					}
					
				}
				
				
				// Madatory Doc
				List<CoverDocumentMaster> mandatoryDocs = getCoverDocumentMasterMandatoryDocs( companyId, productId , sectionIds);
				mandatoryDocs =  mandatoryDocs.stream().filter( o ->    !( o.getDocumentId().equals(16) || o.getDocumentId().equals(17)  || o.getDocumentId().equals(18) || o.getDocumentId().equals(19) )
						).collect(Collectors.toList());
				//Uploaded Docs
				List<CoverDocumentUploadDetails> uploadedDocs = docUploadRepo.findByQuoteNo(req.getQuoteNo());
				
				for (CoverDocumentMaster mdoc :  mandatoryDocs) {
					// Common Docs
					if ( mdoc.getSectionId().equals(99999) ) {
						
						// Filter Common Docs 
						List<CoverDocumentUploadDetails> filterDocs = uploadedDocs.stream().filter( o ->  o.getDocumentId().equals(mdoc.getDocumentId()) && o.getId().equals(0) && o.getSectionId().equals(99999) 
								).collect(Collectors.toList());
						if(filterDocs.size()<=0 ) {
							error.add(new Error("01","Common Doc", mdoc.getDocumentName() + " is Mandatory In Common Document"));
						}
					} else {
						// Filter Other Docs 
						for (DocValidationReq doc :  docValidateReqs) {
							List<CoverDocumentUploadDetails> filterDocs = uploadedDocs.stream().filter( o -> o.getDocumentId().equals(mdoc.getDocumentId()) && o.getId().equals(Integer.valueOf(doc.getRiskId())) && o.getSectionId().equals(Integer.valueOf(doc.getSectionId()))
									).collect(Collectors.toList());
							if(filterDocs.size()<=0 && doc.getSectionId().equals(mdoc.getSectionId().toString() ) ) {
								error.add(new Error("01","Iniduvidual Doc", mdoc.getDocumentName() + " Document Mandatory In " + doc.getProductDesc() + " : " + doc.getRiskId() ));
							}
							
						}
						
					}
				}
				
				
				
			}
			
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			error.add(new Error("01","Common Error",  e.getMessage()));
		}
		return error;
	}
	
	
	public List<CoverDocumentMaster> getCoverDocumentMasterMandatoryDocs(String companyId , Integer productId , List<String> sectionIds  ) {
		List<CoverDocumentMaster> list = new ArrayList<CoverDocumentMaster>();
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
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);
			
			// Find All
			Root<CoverDocumentMaster> c = query.from(CoverDocumentMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("sectionId")));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CoverDocumentMaster> ocpm1 = effectiveDate.from(CoverDocumentMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			javax.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			javax.persistence.criteria.Predicate a4 = cb.equal(c.get("coverId"), ocpm1.get("coverId"));
			Predicate a11 = cb.equal(c.get("documentId"), ocpm1.get("documentId"));
			javax.persistence.criteria.Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a5,a11);
			// Effective Date End
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CoverDocumentMaster> ocpm2 = effectiveDate2.from(CoverDocumentMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("coverId"), ocpm2.get("coverId"));
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId") );
			Predicate a9 = cb.equal(c.get("productId"), ocpm2.get("productId") );
			Predicate a12 = cb.equal(c.get("documentId"), ocpm2.get("documentId"));
			Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6,a7,a8,a9,a10,a12);
					
			//In 
			Expression<String>e0=c.get("sectionId");
			
			// Where
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"),companyId);
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			javax.persistence.criteria.Predicate n5 = e0.in(sectionIds);
			javax.persistence.criteria.Predicate n6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			javax.persistence.criteria.Predicate n7 = cb.equal(c.get("mandatoryStatus"), "Y");
			query.where(n1, n2, n3, n4, n5,n6,n7).orderBy(orderList);

			// Get Result
			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getDocumentId() , o.getSectionId()))).collect(Collectors.toList());
			
			
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
	
	@Override
	public synchronized MakePaymentRes savemakepayment(MakePaymentSaveReq req) {
		// TODO Auto-generated method stub
		MakePaymentRes res = new MakePaymentRes();
		try {
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			List<PaymentInfo> filterPendings = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Pending") ).collect(Collectors.toList());
			
			String paymentId = "";
			
			if (filterPendings.size() <= 0 ) {
				//Find data from home Position Master
				HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
				String productName =   getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString()); //productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
				String companyName =  getInscompanyMasterDropdown(data.getCompanyId()) ; // companyRepo.findByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
				String branchName = getCompanyBranchMasterDropdown(data.getCompanyId() , data.getBranchCode());
				
				
				paymentId = generatePaymentid();
				
				// Save Paymetn Info
				PaymentInfo paymentinfo = new PaymentInfo();
				paymentinfo.setAddress1(personaldata.getAddress1());
				paymentinfo.setAmentId(0);
				paymentinfo.setBranchCode(data.getBranchCode());
				paymentinfo.setBranchName(branchName);
				paymentinfo.setCompanyId(data.getCompanyId());
				paymentinfo.setCompanyName(companyName);
				paymentinfo.setCreatedBy(req.getCreatedBy());
				paymentinfo.setCustomerCity(personaldata.getCityName());
				paymentinfo.setCustomerName(personaldata.getClientName() );
				paymentinfo.setEmailId(personaldata.getEmail1());
				paymentinfo.setEmiYn(req.getEmiYn());
				paymentinfo.setEntryDate(new Date());
				paymentinfo.setLoginId(req.getCreatedBy()); 
				paymentinfo.setMerchantReference("");
				paymentinfo.setMobileNo(personaldata.getMobileNo1());
				paymentinfo.setPaymentId(paymentId);
				paymentinfo.setPaymentStatus("PENDING");			
				paymentinfo.setPolicyEndDate(data.getExpiryDate());
				paymentinfo.setPolicyStartDate(data.getInceptionDate() );
				
				String pattern = "#####0.00" ;
				DecimalFormat df = new DecimalFormat(pattern);
				
				 if (StringUtils.isNotBlank(req.getEmiYn()) && req.getEmiYn().equalsIgnoreCase("Y") && StringUtils.isNotBlank(req.getInstallmentMonth()) 
							&& StringUtils.isNotBlank(req.getInstallmentPeriod())  )  {
					
					// Emi Premium
					EmiTransactionDetails  emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,req.getInstallmentMonth() , req.getInstallmentPeriod());
					paymentinfo.setPremium(new BigDecimal( emiDetails.getPremiumWithTax()));
					paymentinfo.setPremiumLc(new BigDecimal( emiDetails.getPremiumWithTax() ));
					
					BigDecimal premiumFc = paymentinfo.getPremiumLc().multiply(data.getExchangeRate(), MathContext.DECIMAL128 );
					paymentinfo.setPremiumFc( new BigDecimal(df.format(premiumFc)));
						
				 } else {
					 
					// Overall Premium
					paymentinfo.setPremium(data.getOverallPremiumLc());
					paymentinfo.setPremiumLc(data.getOverallPremiumLc() );
					paymentinfo.setPremiumFc( new BigDecimal(df.format(data.getOverallPremiumFc())));
						 
				 }
				 
				if(StringUtils.isNotBlank(data.getEndtTypeId())) {
					
					// Endorsment Premium
					paymentinfo.setPremium(data.getEndtPremium().add(data.getEndtPremiumTax()));
					paymentinfo.setPremiumLc(data.getEndtPremium().add(data.getEndtPremiumTax()));
					
					BigDecimal premiumFc = paymentinfo.getPremiumLc().multiply(data.getExchangeRate(), MathContext.DECIMAL128 );
					paymentinfo.setPremiumFc( new BigDecimal(df.format(premiumFc)));
					
				}
				
				
				
				//BigDecimal premium = new BigDecimal(req.getPremium()) ;
				//BigDecimal premiumFc = premium.divide(data.getExchangeRate(), MathContext.DECIMAL128 );
				
				paymentinfo.setCurrencyId(data.getCurrency());
				paymentinfo.setExchangeRate(data.getExchangeRate() );
				paymentinfo.setProductId(data.getProductId());
				paymentinfo.setProductDesc(productName);
				paymentinfo.setQuoteNo(req.getQuoteNo());
				paymentinfo.setRemarks(req.getRemarks());
				paymentinfo.setShorternUrl("");
				paymentinfo.setStatus("Y");			;
				paymentinfo.setSubUserType(req.getSubUserType());
				paymentinfo.setUpdatedBy(req.getCreatedBy());
				paymentinfo.setUpdatedDate(new Date());
				paymentinfo.setUserType(req.getUserType());
				paymentinfo.setInstallmentMonth(req.getInstallmentMonth());
				paymentinfo.setInstallmentPeriod(req.getInstallmentPeriod());
				
	//			Integer validateHour = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_HOUR"));
	//			Integer validateMinutes = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_MINUTES"));
	//			Date today  = new Date();
	//			Calendar cal = new GregorianCalendar(); 
	//			cal.setTime(today);
	//			cal.set(Calendar.HOUR_OF_DAY, +validateHour);
	//			cal.set(Calendar.MINUTE, +validateMinutes);
	//			Date validateDate = cal.getTime();
	//			
	//			paymentinfo.setValidityDate(validateDate);
				
				
				/*
				//SMS Calling
				
				SendSmsReq smsreq = new SendSmsReq();
				smsreq.setCompanyId(data.getCompanyId());
				smsreq.setBranchCode(data.getBranchCode());
				smsreq.setCustomerId(data.getCustomerId());
				smsreq.setQuoteNo(req.getQuoteNo());
				smsreq.setProductId(data.getProductId().toString());
				smsreq.setLoginId(req.getCreatedBy()); 
				smsreq.setRequestReferenceNo(data.getRequestReferenceNo());
				smsreq.setSectionId(data.getSectionId().toString());
				smsreq.setNotifTemplateName("Payment Message");
				smsreq.setMobileNo(personaldata.getMobileNo1());
				smsreq.setMobileNoDesc(personaldata.getMobileCodeDesc1());

				List<NotifTemplateMaster> notiftemplate = notifRepo.findByCompanyIdAndProductIdOrderByAmendIdDesc(data.getCompanyId(),Long.valueOf(data.getProductId()));
				smsreq.setSmsSubject(notiftemplate.get(0).getSmsSubject());
				smsreq.setSmsBody(notiftemplate.get(0).getSmsBodyEn());
				smsRepo.sendSms(smsreq);
				*/
				
				paymentinforepo.save(paymentinfo);
				log.info("Saved Details " + json.toJson(paymentinfo));
				
			} else {
				paymentId = filterPendings.get(0).getPaymentId() ;
			}
			
			res.setPaymentId(paymentId);
			res.setQuoteNo(req.getQuoteNo());
			res.setResponse("Saved Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
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

	 public synchronized String generatePaymentid() {
	       try {
	    	   SeqPaymentid entity;
	            entity = seqPayIdrepo.save(new SeqPaymentid());          
	            return String.format("%05d",entity.getPaymentId()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }
	 
	 public synchronized String getListItem(String insuranceId , String branchCode, String itemType) {
			String itemDesc = "" ;
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
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
			//	Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
				query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return itemDesc ;
		}
	 
	 public String getCompanyBranchMasterDropdown(String companyId , String branchCode ) {
			String branchName = "" ;
			try {
				Date today  = new Date();
				Calendar cal = new GregorianCalendar(); 
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today   = cal.getTime();
				
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);
				List<BranchMaster> list = new ArrayList<BranchMaster>();
				
				// Find All
				Root<BranchMaster>    c = query.from(BranchMaster.class);		
				
				// Select
				query.select(c );
				
			
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchName")));
				
				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode") );
				Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,a3);
				
			    // Where	
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("companyId"),companyId );
				Predicate n4 = cb.equal(c.get("branchCode"),branchCode );
				
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
				// Get Result
				TypedQuery<BranchMaster> result = em.createQuery(query);
				list = result.getResultList();
				branchName  = list.size()> 0 ? list.get(0).getBranchName() : "";	
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return branchName;
		}
	 
	public String getInscompanyMasterDropdown(String companyId ) {
		String companyName = "" ;
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<InsuranceCompanyMaster> query = cb.createQuery(InsuranceCompanyMaster.class);
			List<InsuranceCompanyMaster> list = new ArrayList<InsuranceCompanyMaster>();
			
			// Find All
			Root<InsuranceCompanyMaster>    c = query.from(InsuranceCompanyMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("companyName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			javax.persistence.criteria.Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
	
			// Get Result
			TypedQuery<InsuranceCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			companyName  = list.size()> 0 ? list.get(0).getCompanyName() : "";	
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return companyName;
	}
	
	public String getCompanyProductMasterDropdown(String companyId , String productId) {
		String productName = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query=  cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4,a5,a6);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			Predicate n5 = cb.equal(c.get("productId"),productId);
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			productName  = list.size()> 0 ? list.get(0).getProductName() : "";	
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return productName;
		}
	
	@Override
	public SuccessRes updatemakepayment(MakePaymentUpdateReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddmmssSSS");
		PaymentDetail paymentdetail = new PaymentDetail();
		PaymentInfo paymentinfo = new PaymentInfo();
		String refNo = "";

		try {
			Date today = new Date();

			
			//Reference No Generation
			Random rand = new Random();
			int random = rand.nextInt(90) + 10;
			refNo = "EWAY" +"-" + idf.format(new Date()) + random ; 
			
			//Payment Id Count
			Long count  = paymentdetailrepo.count();
			Long paymentid = 200001+count;
			
			//Find data from home Position Master
			List<PaymentDetail> data = paymentdetailrepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());

			//For Status Expired
			if(req.getStatus().equalsIgnoreCase("EXPIRED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(data.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Rejected
			else if(req.getStatus().equalsIgnoreCase("REJECTED"))
			{
			//Payment Detail Save
			paymentdetail=dozermappper.map(data.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(data.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(refNo);
			paymentdetail.setEntryDate(data.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(data.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(data.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(refNo);
			paymentinfo.setEntryDate(data.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(data.get(0).getPaymentTypeDesc());

			paymentinforepo.save(paymentinfo);
			}

			
			//For Status Accept
			if(req.getStatus().equalsIgnoreCase("ACCEPTED"))
			{
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNoAndPaymentStatusOrderByEntryDateDesc(req.getQuoteNo(),"PENDING");
					
			if(datas.size()>0) {
			//Payment Detail Save
			paymentdetail=dozermappper.map(datas.get(0), PaymentDetail.class);
			paymentdetail.setPaymentStatus(req.getStatus());
			paymentdetail.setPaymentId(datas.get(0).getPaymentId());
		//	paymentdetail.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentdetail.setEntryDate(datas.get(0).getEntryDate());
			paymentdetail.setUpdatedDate(today);

			paymentdetailrepo.save(paymentdetail);

			//Payment Info Save
			paymentinfo = dozermappper.map(datas.get(0), PaymentInfo.class);			
			paymentinfo.setPaymentStatus(req.getStatus());
		//	paymentinfo.setPaymentId(datas.get(0).getPaymentId());
		//	paymentinfo.setPaymentReferenceNo(datas.get(0).getPaymentReferenceNo());
			paymentinfo.setEntryDate(datas.get(0).getEntryDate());
			paymentinfo.setUpdatedDate(today);
		//	paymentinfo.setOthPaymentMode(datas.get(0).getPaymentTypeDesc());
			
			paymentinforepo.save(paymentinfo);
			}
			
			}
			else {
				return res;
			}
			
			res.setSuccessId(req.getQuoteNo());
			res.setResponse("Updated Successful");
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public PaymentDetailGetRes getpaymentdetails(PaymentDetailsGetReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		PaymentDetailGetRes res = new PaymentDetailGetRes();
		try {
		
			PaymentDetail data = paymentdetailrepo.findByQuoteNoAndPaymentIdAndMerchantReference(req.getQuoteNo(),Double.valueOf(req.getPaymentId()),req.getMerchantReference());
			
			res = dozermappper.map(data, PaymentDetailGetRes.class);
			res.setPaymentId(data.getPaymentId());				

		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}
	

	@Override
	public List<PaymentDetailGetRes> getallpaymentdetails(PaymentDetailsGetallReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		List<PaymentDetailGetRes> resList = new ArrayList<PaymentDetailGetRes>();
		try {
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNo(req.getQuoteNo());
			for(PaymentDetail data : datas) {
				PaymentDetailGetRes res = new PaymentDetailGetRes();
				res = dozermappper.map(data, PaymentDetailGetRes.class);
				res.setPaymentId(String.valueOf(data.getPaymentId()));				
				resList.add(res);
				}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public PaymentInfoGetRes getPaymentInfo(PaymentInfoGetReq req) {
		PaymentInfoGetRes res = new PaymentInfoGetRes();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		try {
			PaymentInfo data = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo() , req.getPaymentId() );
			res = dozermappper.map(data, PaymentInfoGetRes.class);
			
			
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}


	@Override
	public List<PaymentInfoGetRes> viewPaymentInfo(PaymentInfoGetAllReq req) {
		List<PaymentInfoGetRes> resList = new ArrayList<PaymentInfoGetRes>();
		DozerBeanMapper dozermappper = new DozerBeanMapper();
		try {
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			for(PaymentInfo data : datas) {
				PaymentInfoGetRes res = new PaymentInfoGetRes();
				res = dozermappper.map(data, PaymentInfoGetRes.class);
				resList.add(res);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return resList;
	}


	@Override
	public List<Error> validatePaymentInsert(PaymentDetailsSaveReq req) {
		List<Error> error = new ArrayList<Error>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
//			if(StringUtils.isBlank(req.getRemarks())) {
//				error.add(new Error("01","Remarks","Please Enter Remarks"));
//			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			if(StringUtils.isBlank(req.getPaymentType())) {
				error.add(new Error("01","PaymentType","Please Select PaymentType"));
			}
			
			if(StringUtils.isNotBlank(req.getPayments()) && req.getPayments().equalsIgnoreCase("Refund") ){
				if(StringUtils.isBlank(req.getAccountNumber())) {
					error.add(new Error("01","AccountNumber","Please Enter AccountNumber "));
				}
				if(StringUtils.isBlank(req.getIbanNumber())) {
					error.add(new Error("01","IbanNumber","Please Enter IbanNumber"));
				}
			}
			
			if("2".equals(req.getPaymentType())) {
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if(StringUtils.isBlank(req.getBankName())) {
					error.add(new Error("01","BankName","Please Enter BankName"));
				}
				if (StringUtils.isBlank(req.getPayments())  || ( StringUtils.isNotBlank(req.getPayments()) && ! req.getPayments().equalsIgnoreCase("Refund") ) ){
					if(StringUtils.isBlank(req.getChequeNo())) {
						error.add(new Error("01","ChequeNo","Please Enter ChequeNo"));
					}else if (req.getChequeDate() == null) {
						error.add(new Error("04", "ChequeDate", "Please Enter ChequeDate "));
					} else if (req.getChequeDate().before(today)) {
						error.add(new Error("04", "ChequeDate", "Please Enter ChequeDate as Future Date"));
					}
				}
					
			
			}
			
			// Check Paymetn Info
			if (StringUtils.isNotBlank(req.getQuoteNo()) && StringUtils.isNotBlank(req.getPaymentId()) ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
				
				if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Accepted") ) {
					error.add(new Error("01","Accepted","This Payment Already Accepted "));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Rejected") ) {
					error.add(new Error("01","Rejected","This Payment Already Rejected "));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Cancelled") ) {
					error.add(new Error("01","Cancelled","This Payment Already Cancelled"));
					
				} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Pending") && StringUtils.isNotBlank(paymentInfo.getMerchantReference())  )  {
					error.add(new Error("01","Cancelled","This Payment Already Pending"));
				}
				
			}
			
			
			
			// Other Payment Id Validation
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			
			if (datas.size() > 0 ) {
				List<PaymentInfo> filterPendings = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Pending") && ! o.getPaymentId().equalsIgnoreCase(req.getPaymentId()) ) .collect(Collectors.toList());		
				List<PaymentInfo> filterAccepted = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && ! o.getPaymentId().equalsIgnoreCase(req.getPaymentId())  ).collect(Collectors.toList());		
				
				if(filterPendings.size()> 0) {
					error.add(new Error("01","PaymentId","Already One Payment Id Pending Against This Quote No"));
				}
				
				if(filterAccepted.size()> 0) {
					PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
					if ( paymentInfo.getEmiYn().equalsIgnoreCase("Y" ) && StringUtils.isNotBlank(paymentInfo.getInstallmentMonth()) && StringUtils.isNotBlank(paymentInfo.getInstallmentPeriod()) )  {
						
						List<PaymentInfo> filterEmi = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && o.getInstallmentMonth().equalsIgnoreCase(paymentInfo.getInstallmentMonth()) && 
								  						o.getInstallmentPeriod().equalsIgnoreCase(paymentInfo.getInstallmentPeriod()) ).collect(Collectors.toList());
						if(filterEmi.size()>0 ) {
							error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
						}
					
					} else {
						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
					}
					
					
				}
			}
			
			List<PaymentDetail> pays = paymentdetailrepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			if ( pays.size() > 0 ) {
				List<PaymentDetail> filterAccepted = pays.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") &&  o.getPaymentId().equalsIgnoreCase(req.getPaymentId())  ).collect(Collectors.toList());		
				
				if(filterAccepted.size()> 0) {
					PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
					if ( paymentInfo.getEmiYn().equalsIgnoreCase("Y" ) && StringUtils.isNotBlank(paymentInfo.getInstallmentMonth()) && StringUtils.isNotBlank(paymentInfo.getInstallmentPeriod()) )  {
						
						List<PaymentDetail> filterEmi = pays.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && o.getInstallmentMonth().equalsIgnoreCase(paymentInfo.getInstallmentMonth()) && 
								  						o.getInstallmentPeriod().equalsIgnoreCase(paymentInfo.getInstallmentPeriod()) ).collect(Collectors.toList());
						if(filterEmi.size()>0 ) {
							error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
						}
					
					} else {
						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
					}
				}
				
				
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return error;
	}


	@Override
	@Transactional
	public PaymentDetailsSaveRes savePaymentDetails(PaymentDetailsSaveReq req) {
		PaymentDetailsSaveRes res = new PaymentDetailsSaveRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper ();
		try {
			//Find data from home Position Master
			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
			String productName =   getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString()); //productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
			String companyName =  getInscompanyMasterDropdown(data.getCompanyId()) ; // companyRepo.findByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
			String branchName = getCompanyBranchMasterDropdown(data.getCompanyId() , data.getBranchCode());
			String paymentMode = getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_MODE",req.getPaymentType());
			String refShortCode = getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_REF_SHORTCODE","1");
			PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
			String refno = refShortCode +"-"+ generateMerchantReferenceNo();
			
			// Tiny Url
			String tinyUrl = "" ; 
			if(StringUtils.isBlank(req.getShortenUrl())) {
				TinyUrlGetReq urlReq = new TinyUrlGetReq();
				urlReq.setQuoteNo(req.getQuoteNo());
				urlReq.setType("DO_NEW_QUOTE");
				
				CommonRes common =  paymentService.getTinyUrl(urlReq);
				TinyUrlGetRes tinyRes = (TinyUrlGetRes) common.getCommonResponse();
				tinyUrl = tinyRes.getTinyUrl();
			} else {
				tinyUrl = req.getShortenUrl();
			}
			
				
			
			String paymentStatus = "";
			
			// Save Paymetn Info
			PaymentDetail paymentDetail = new PaymentDetail();
			dozermapper.map(data,PaymentDetail.class);
			paymentDetail.setBranchCode(data.getBranchCode());
			paymentDetail.setBranchName(branchName);
			paymentDetail.setCreatedBy(req.getCreatedBy());
			paymentDetail.setPaymentType(req.getPaymentType());
			paymentDetail.setPaymentTypedesc(paymentMode);
			paymentDetail.setCustomerName(personaldata.getClientName() );
			paymentDetail.setEntryDate(new Date());
			paymentDetail.setMerchantReference(refno);
			paymentDetail.setPaymentStatus(paymentStatus);			
			paymentDetail.setQuoteNo(req.getQuoteNo());
			paymentDetail.setUpdatedBy(req.getCreatedBy());
			paymentDetail.setUpdatedDate(new Date());
			paymentDetail.setPaymentId(req.getPaymentId());
			paymentDetail.setCustomerEmail(personaldata.getEmail1());
			paymentDetail.setCustomerId(personaldata.getCustomerId());
			paymentDetail.setEmiYn(StringUtils.isBlank(paymentInfo.getEmiYn())?"N": paymentInfo.getEmiYn());
			paymentDetail.setInstallmentMonth(paymentInfo.getInstallmentMonth());
			paymentDetail.setInstallmentPeriod(paymentInfo.getInstallmentPeriod());
			paymentDetail.setPaymentType(req.getPaymentType());
			paymentDetail.setReqBillToAddressCity(personaldata.getCityName());
			paymentDetail.setReqBillToAddressLine1(personaldata.getAddress1());
			paymentDetail.setReqBillToAddressLine2(personaldata.getAddress2());
			paymentDetail.setReqBillToAddrPostalCode(null);
			paymentDetail.setReqBillToEmail(personaldata.getEmail1());;
			paymentDetail.setReqBillToForename(personaldata.getClientName());
			paymentDetail.setReqBillToPhone(personaldata.getMobileNo1());
			paymentDetail.setReqBillToSurname(personaldata.getClientName());
			paymentDetail.setReqCardExpiryDate(null);
			paymentDetail.setReqBillToCompanyName(companyName);
			paymentDetail.setShorternUrl(tinyUrl);
			paymentDetail.setPremium(paymentInfo.getPremium());
			paymentDetail.setPremiumFc(paymentInfo.getPremiumFc());
			paymentDetail.setPremiumLc(paymentInfo.getPremiumLc());
			paymentDetail.setCurrencyId(paymentInfo.getCurrencyId());
			paymentDetail.setExchangeRate(paymentInfo.getExchangeRate() );
			paymentDetail.setAccountNumber( req.getAccountNumber()  ); 
			paymentDetail.setIbanNumber(req.getIbanNumber()  ); 
			paymentDetail.setPayments( StringUtils.isBlank(req.getPayments() ) ? "Charge" : req.getPayments()  ); 
			
			if("2".equals(req.getPaymentType())) {
				paymentDetail.setBankName(req.getBankName());
				paymentDetail.setChequeNo(req.getChequeNo());
				paymentDetail.setChequeDate(req.getChequeDate());
			}
			Integer validateHour = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_HOUR"));
			Integer validateMinutes = Integer.valueOf(getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_VALIDATE_MINUTES"));
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, +validateHour);
			cal.set(Calendar.MINUTE, +validateMinutes);
			Date validateDate = cal.getTime();
			
			paymentDetail.setValidityDate(validateDate);
			
			if( req.getPaymentType().equalsIgnoreCase("1") || req.getPaymentType().equalsIgnoreCase("2")) {
				paymentStatus = "ACCEPTED" ;
				paymentDetail.setPaymentStatus(paymentStatus);
			} else {
				paymentStatus = "PENDING" ;
				paymentDetail.setPaymentStatus(paymentStatus);
			}
			
			
			
			paymentdetailrepo.saveAndFlush(paymentDetail);
			log.info("Saved Details " + json.toJson(paymentDetail));
			
			// Notification Trigger
			notificationTrigger(data.getProductId(),req.getQuoteNo(),paymentStatus);
			
			
			// Update Payment Info
			paymentInfo.setValidityDate(validateDate);
			paymentInfo.setShorternUrl(tinyUrl);
			paymentInfo.setPaymentStatus(paymentStatus);
			paymentInfo.setMerchantReference(refno);
			paymentInfo.setPayments( StringUtils.isBlank(req.getPayments() ) ? "Charge" : req.getPayments()  ); 			
			paymentinforepo.saveAndFlush(paymentInfo);
			
			// Update Emi 
			if (  paymentInfo.getEmiYn().equalsIgnoreCase("Y" )) {
				EmiTransactionDetails  emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,paymentInfo.getInstallmentMonth() , paymentInfo.getInstallmentPeriod());
				emiDetails.setPaymentStatus(paymentStatus);
				emiRepo.saveAndFlush(emiDetails);
				
			}
			res.setResponse("Payment Success");
			
			
			
			// Policy Convertion
			if(paymentStatus.equalsIgnoreCase("ACCEPTED") && ( paymentInfo.getEmiYn().equalsIgnoreCase("N") || paymentInfo.getInstallmentMonth().equalsIgnoreCase("0") )  ) {
				List<DebitAndCredit> policyDetails = new ArrayList<DebitAndCredit>();
				CalcCommission  policyReq = new CalcCommission();
				policyReq.setAgencyCode("");
				policyReq.setBranchCode(paymentInfo.getBranchCode());
				policyReq.setCreatedBy(req.getCreatedBy());
				policyReq.setInsuranceId(paymentInfo.getCompanyId());
				policyReq.setPolicyNo("");
				policyReq.setProductId(paymentInfo.getProductId().toString());
				policyReq.setQuoteno(req.getQuoteNo());
				policyReq.setSectionId("");
				
				policyDetails = calcService.commissionCalc(policyReq);
				
				List<DebitAndCredit> filterDebit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("DR")).collect(Collectors.toList());
				List<DebitAndCredit> filterCredit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")).collect(Collectors.toList());
				
				String policyNo = policyDetails.get(0).getPolicyNo();
				// Debit
				String debitNo = filterDebit.get(0).getDocNo() ;
				Date debitDate = filterDebit.get(0).getEntryDate();
				String debitTo = filterDebit.get(0).getDocType();
				// Credit
				String creditNo =  filterCredit.get(0).getDocNo();
				Date creditDate = filterCredit.get(0).getEntryDate();
				String creditTo = filterCredit.get(0).getDocType();
				// Commision
				BigDecimal commission =  policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") && o.getChargeCode().equals(new BigDecimal(1005)) ).collect(Collectors.toList()).get(0).getAmountFc();
				BigDecimal commissionPercent = 		policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") && o.getChargeCode().equals(new BigDecimal(1007)) ).collect(Collectors.toList()).get(0).getAmountFc();
				List<DebitAndCredit> filtercommissionVat = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")&& o.getChargeCode().equals(new BigDecimal(1012))).collect(Collectors.toList());
				BigDecimal commissionVat = BigDecimal.ZERO;
				if (filtercommissionVat.size()>0 ) {
					commissionVat =  filtercommissionVat.get(0).getAmountFc();
				}
				
				
				// Update Home Posion Master
				data.setDebitNoteNo(debitNo);
				data.setDebitNoteDate(debitDate);
				data.setDebitTo(debitTo);
				
				data.setCreditNo(creditNo);
				data.setCreditDate(creditDate);	
				data.setCreditTo(creditTo);
				
				data.setCommission(commission);
				data.setCommissionPercentage(commissionPercent);
				data.setVatCommission(commissionVat);
				data.setPaymentMode(req.getPaymentType());
				data.setPaymentType(  paymentMode);
				data.setPaymentStatus(paymentInfo.getEmiYn().equalsIgnoreCase("N") ? paymentStatus :"Pending");
				data.setPolicyNo(policyNo);
				
				data.setStatus("P");
				data.setIntegrationStatus("S");
				data.setEmiYn(paymentInfo.getEmiYn());
				data.setInstallmentPeriod(paymentInfo.getInstallmentPeriod());
				if(StringUtils.isNotBlank(data.getEndtTypeId())) {
					data.setEndtStatus("C");
					
				} else {
					data.setOriginalPolicyNo(policyNo);
				}
					
				
				homerepo.saveAndFlush(data);
				
				// Update ProductWise
				String msg = updateProductWisePolicyNo(paymentInfo.getProductId().toString() ,policyNo ,req.getQuoteNo(),data.getEndtTypeId() ); 
						
				res.setPolicyNo(policyNo);
				res.setDebitNoteNo(debitNo);
				res.setCreditNoteNo(creditNo);
				res.setResponse("Policy Converted");
				
				
				
			}
			
			res.setPaymentId(paymentDetail.getPaymentId().toString());
			res.setQuoteNo(req.getQuoteNo());
			res.setMerchantReference(refno);
			}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}
	
	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
		String itemDesc = "" ;
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
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n1,n2,n3,n8,n9,n10,n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}
	
	 public  String updateProductWisePolicyNo(String productId , String policyNo , String quoteNo,String endttypeId ) {
		 String res = "" ;
	       try {
	    	   if(productId.equalsIgnoreCase(motorProductId) ) {
	    		   // Eservice Motor Update
	    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<EserviceMotorDetails> update = cb.createCriteriaUpdate(EserviceMotorDetails.class);
					// set the root class
					Root<EserviceMotorDetails> m = update.from(EserviceMotorDetails.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					update.set("status", "P");
					if(StringUtils.isNotBlank(endttypeId))
						update.set("endtStatus","C");
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					Predicate n2 = cb.notEqual(m.get("status"),"D" );
					update.where(n1,n2);
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
						update.set("policyNo", policyNo);
						update.set("status", "P");
						
						Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
						// perform update
						em.createQuery(update).executeUpdate();
						
	    		   }
	    	   } else  if(productId.equalsIgnoreCase(travelProductId) ) {
	    		   // Eservice Travel Update
	    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<EserviceTravelDetails> update = cb.createCriteriaUpdate(EserviceTravelDetails.class);
					// set the root class
					Root<EserviceTravelDetails> m = update.from(EserviceTravelDetails.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					update.set("status", "P");
					if(StringUtils.isNotBlank(endttypeId))
						update.set("endtStatus","C");
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					Predicate n2 = cb.notEqual(m.get("status"),"D" );
					update.where(n1,n2);
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
						update.set("policyNo", policyNo);
						update.set("status", "P");
						
						Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
						// perform update
						em.createQuery(update).executeUpdate();
						
	    		   }
	    	   } else  if(productId.equalsIgnoreCase(buildingProductId) || productId.equalsIgnoreCase(smeProductId)) {
	    		   // Eservice Building Update
	    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<EserviceBuildingDetails> update = cb.createCriteriaUpdate(EserviceBuildingDetails.class);
					// set the root class
					Root<EserviceBuildingDetails> m = update.from(EserviceBuildingDetails.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					update.set("status", "P");
					if(StringUtils.isNotBlank(endttypeId))
						update.set("endtStatus","C");
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					Predicate n2 = cb.notEqual(m.get("status"),"D" );
					update.where(n1,n2);
					// perform update
					em.createQuery(update).executeUpdate();
					
	    		   }
	    		   // Building Data Details Update
//	    		   {
//		    		    CriteriaBuilder cb = em.getCriteriaBuilder();
//						// create update
//						CriteriaUpdate<MotorDataDetails> update = cb.createCriteriaUpdate(MotorDataDetails.class);
//						// set the root class
//						Root<MotorDataDetails> m = update.from(MotorDataDetails.class);
//						// set update and where clause
//						update.set("policyNo", policyNo);
//						update.set("status", "P");
//						
//						Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
//						update.where(n1);
//						// perform update
//						em.createQuery(update).executeUpdate();
//						
//	    		   }
	    	   } else {
	    		// Eservice Common Update
	    		   {
	    		    CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<EserviceCommonDetails> update = cb.createCriteriaUpdate(EserviceCommonDetails.class);
					// set the root class
					Root<EserviceCommonDetails> m = update.from(EserviceCommonDetails.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					update.set("status", "P");
					if(StringUtils.isNotBlank(endttypeId))
						update.set("endtStatus","C");
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					Predicate n2 = cb.notEqual(m.get("status"),"D" );
					update.where(n1,n2);
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
						update.set("policyNo", policyNo);
						update.set("status", "P");
						
						Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
						// perform update
						em.createQuery(update).executeUpdate();
						
	    		   }
	    	   }
	    	   
	    	   
	    	   // Policy Cover Data 
	    	   CriteriaBuilder cb = em.getCriteriaBuilder();
				// create update
				CriteriaUpdate<PolicyCoverData> update = cb.createCriteriaUpdate(PolicyCoverData.class);
				// set the root class
				Root<PolicyCoverData> m = update.from(PolicyCoverData.class);
				// set update and where clause
				update.set("policyNo", policyNo);
				
				Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
				Predicate n2 = cb.notEqual(m.get("status"),"D" );
				update.where(n1,n2);
				// perform update
				em.createQuery(update).executeUpdate();
	    	   
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       return res ;
	 }
	
	

	 public synchronized String generateMerchantReferenceNo() {
	       try {
	    	   PaymentRefno entity;
	            entity = seqRefNorepo.save(new PaymentRefno());          
	            return String.format("%05d",entity.getPaymentReferenceNo()) ;
	        } catch (Exception e) {
				e.printStackTrace();
				log.info( "Exception is ---> " + e.getMessage());
	            return null;
	        }
	       
	 }


	@Override
	public List<PaymentDetailGetRes> paymentdetailshistory(PaymentDetailsHistoryReq req) {
		List<PaymentDetailGetRes> resList = new ArrayList<PaymentDetailGetRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper ();
		try {
	
			List<PaymentDetail> datas = paymentdetailrepo.findByQuoteNo(req.getQuoteNo());
			for(PaymentDetail data : datas) {
				PaymentDetailGetRes res = new PaymentDetailGetRes();
				dozermapper.map(data, PaymentDetailGetRes.class);
				resList.add(res);
			}
			
		}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Log Details"+e.getMessage());
		return null;
	}
	return resList;
}
	
	@Override
	public CommonRes getTinyUrl(TinyUrlGetReq req) {
		CommonRes commonRes = new CommonRes();
		TinyUrlGetRes res = new TinyUrlGetRes();
		List<Error> errors = new ArrayList<Error>();
		try {
	
			// Quote No 
			if( StringUtils.isBlank(req.getQuoteNo())) {
				errors.add(new Error("01","Quote","We can not Get Tiny Url without QuoteNo"));
				commonRes.setCommonResponse(null);
				commonRes.setIsError(true);
				commonRes.setErrorMessage(errors);
				commonRes.setMessage("Failed");
				return commonRes ; 
			} 
			
			HomePositionMaster homeData = homerepo.findByQuoteNo(req.getQuoteNo())	;
			
			System.out .println("gettinyurl ---> QuoteNo: " + homeData.getQuoteNo());
			System.out .println("gettinyurl ---> OverallPremium: " + homeData.getOverallPremiumFc());
			
			
			String quoteNo = homeData.getQuoteNo() ;
			String productId = homeData.getProductId().toString();
			String overAllPremiumFc = homeData.getOverallPremiumFc().toString();
			String companyId = homeData.getCompanyId();
			String branchCode = homeData.getBranchCode();
			String type = req.getType();			
			TinyUrlGenerateReq urlReq = TinyUrlGenerateReq.builder()
					
				//	.param("QuoteNo=" + quoteNo)
					.param("QuoteNo=" + quoteNo+ "~"+"ProductId=" + req.getProductId() )
					.productId(productId)
					.companyId(companyId)
					.branchCode(branchCode)
					.type(type).build();
			
			String tinyUrl = generateTinyUrl (urlReq ) ;
			System.out .println("TinyUrl --> " + tinyUrl );
			
			// Response 
			res.setTinyUrl(tinyUrl);
			res.setOverAllPremiumFc(overAllPremiumFc);	
			res.setProductId(productId);
			res.setQuoteNo(quoteNo);
			res.setCompanyId(companyId);	
			res.setBranchCode(branchCode);
			
			commonRes.setCommonResponse(res);
			commonRes.setIsError(false);
			commonRes.setErrorMessage(null);
			commonRes.setMessage("Success");
			return commonRes ; 
			
			
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
	
	public String generateTinyUrl(TinyUrlGenerateReq req) {
		String encrData = "", tinyURL = "";
		try {
			String type = req.getType();
			log.info("gettinyurl--> type: " + type);
			passwordEnc passEnc = new passwordEnc();
			encrData = EncryDecryService.encrypt(req.getParam());
			String url = getAppUrl(type,req.getCompanyId() , req.getProductId() , req.getBranchCode() );
			url = url == null ? "" : url;
			log.info("gettinyurl--> URL: " + url);
			String encryptedURL = url + encrData;
			log.info("gettinyurl--> EncryptedURL: " + encryptedURL);
			tinyURL = getShorternURL(encryptedURL);
		} catch (Exception e) {
			log.error(e);
		}
		return tinyURL;
	}

	private String getShorternURL(String encryptedURL) {
		try {
			final String tinyUrl = "http://tinyurl.com/api-create.php?url=";
			String tinyUrlLookup = tinyUrl + encryptedURL;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(tinyUrlLookup).openStream()));
			String result = reader.readLine();
			log.info("Encrypted URL result: " + result + " Encrypted URL " + encryptedURL);
			reader.close();
			return result;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}
	
	
	public synchronized String getAppUrl(String type , String companyId , String productId , String branchCode ) {
		String url = "";
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TinyurlMaster> query=  cb.createQuery(TinyurlMaster.class);
			// Find All
			Root<TinyurlMaster> c = query.from(TinyurlMaster.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<TinyurlMaster> ocpm1 = effectiveDate.from(TinyurlMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("sno"),ocpm1.get("sno"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a11 = cb.equal(ocpm1.get("type") ,c.get("type"));
			effectiveDate.where(a1,a2,a3,a4,a5,a11);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<TinyurlMaster> ocpm2 = effectiveDate2.from(TinyurlMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("sno"),ocpm2.get("sno"));
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a9 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a12 = cb.equal(ocpm2.get("type") ,c.get("type"));
			effectiveDate2.where(a6,a7,a8,a9,a10,a12);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("productId"), productId);
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n6,n7);
			Predicate n9 = cb.equal(c.get("type"), type);
			query.where(n1,n2,n3,n4,n5,n8,n9).orderBy(orderList);
			// Get Result
			TypedQuery<TinyurlMaster> result = em.createQuery(query);
			List<TinyurlMaster> list = result.getResultList();
			url = list.get(0).getAppUrl();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return url ;
	}


	@Override
	public LoginEncryptResponse decryptTinyUrl(PaymentResUrlReq req) {
		LoginEncryptResponse resp = new LoginEncryptResponse();
		try {
			log.info("Req==>" + req.getEncryptValue());
			String decrypt = EncryDecryService.decrypt(URLDecoder.decode(req.getEncryptValue(), "UTF-8"));
			if (StringUtils.isNotBlank(decrypt) && decrypt.indexOf("~") != -1) {
				log.info("Encrypt==>" + decrypt);
				String[] split = decrypt.split("~");
				if (split.length > 0) {
					String[] quoteNo = split[0].split("=");
					String[] productId = split[1].split("=");
//					String[] loginType = split[2].split("=");
//					String[] branchcode = split[3].split("=");
					resp.setQuoteNo(quoteNo[1]);
					resp.setProductId(productId[1]);
					//resp.setLoginType(loginType[1]);
					//resp.setBranchcode(branchcode[1]);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}

	 public QuoteUpdateRes notificationTrigger(Integer productId,String quoteNo,String paymentStatus) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				
				if( productId.equals(motorProductId)) {
					//Mail Push Notification
					updateRes= motorPushNotification(productId,quoteNo,paymentStatus);
					
				} else if(productId.equals(travelProductId)) {
			
					//Mail Push Notification
					//updateRes= travelPushNotification(req);
					
				} else if( productId.equals(buildingProductId)) {
					//Mail Push Notification
					//updateRes= buildingPushNotification(req);
				}  
				
			
			} catch ( Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return updateRes;
		}
	 //Notification Trigger
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
		private QuoteUpdateRes motorPushNotification(Integer productId,String quoteNo,String paymentStatus) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				List<EserviceMotorDetails> cusRefNo = eserMotRepo
						.findByRequestReferenceNoAndProductId(quoteNo, productId.toString());

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
				brokerReq.setBrokerCompanyName(loginInfo.getCompanyName()==null?null: loginInfo.getCompanyName());
				brokerReq.setBrokerMailId(loginInfo.getUserMail()==null?"":loginInfo.getUserMail());
				brokerReq.setBrokerMessengerCode(loginInfo.getWhatsappCodeDesc()==null?null:Integer.valueOf(loginInfo.getWhatsappCodeDesc()));
				brokerReq.setBrokerMessengerPhone(loginInfo.getWhatsappNo()==null? BigDecimal.ZERO: new BigDecimal(loginInfo.getWhatsappNo().toString()));
				brokerReq.setBrokerPhoneCode(loginInfo.getMobileCodeDesc()==null?null:Integer.valueOf((loginInfo.getMobileCodeDesc())));
				brokerReq.setBrokerPhoneNo(loginInfo.getUserMobile()==null?BigDecimal.ZERO:new BigDecimal(loginInfo.getUserMobile()));
				brokerReq.setBrokerName(loginInfo.getUserName());
				}
				// Customer Info
				EserviceCustomerDetails customerData = eserCustRepo.findByCustomerReferenceNo(cusRefNo.get(0).getCustomerReferenceNo());
				Customer cusReq = new Customer();
				if(customerData!=null) {
					cusReq.setCustomerMailid(customerData.getEmail1());
					cusReq.setCustomerName(customerData.getClientName());
					cusReq.setCustomerPhoneCode(Integer.valueOf(customerData.getMobileCodeDesc1()));
					cusReq.setCustomerPhoneNo(new BigDecimal(customerData.getMobileNo1()));
					cusReq.setCustomerMessengerCode(Integer.valueOf(customerData.getWhatsappCodeDesc()));
					cusReq.setCustomerMessengerPhone(new BigDecimal(customerData.getWhatsappNo()));
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
						underWrite.add(underWriterReq);
					}
				}
				n.setUnderwriters(underWrite);
				//Company Info
				n.setCompanyid(cusRefNo.get(0).getCompanyId());
				n.setCompanyName(cusRefNo.get(0).getCompanyName());
				
				//Common Info
				n.setBroker(brokerReq);
				n.setCustomer(cusReq);
				n.setNotifcationDate(new Date());
				n.setNotifDescription("");
				n.setNotifPriority(0);
				n.setNotifPushedStatus(NotificationStatus.PENDING);
				n.setNotifTemplatename("Referral Pending");
				n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
				n.setProductid(Integer.valueOf(productId));
				n.setProductName("Motor");
				n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
				n.setSectionName(cusRefNo.get(0).getSectionName());
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
						   u.get("whatsappCodeDesc").alias("whatsappCodeDesc"),u.get("whatsappNo").alias("whatsappNo"));			
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
		
}
package com.maan.eway.common.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CoverDocumentMaster;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.DepositcbcMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.DocumentUniqueDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentRefno;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataIndividuals;
import com.maan.eway.bean.PolicyDrcrDetail;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqPaymentid;
import com.maan.eway.bean.TinyurlMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.chartaccount.ChartAccountRequest;
import com.maan.eway.chartaccount.ChartAccountServiceImpl;
import com.maan.eway.common.req.MakePaymentRes;
import com.maan.eway.common.req.MakePaymentSaveReq;
import com.maan.eway.common.req.MakePaymentUpdateReq;
import com.maan.eway.common.req.PaymentDetailsGetReq;
import com.maan.eway.common.req.PaymentDetailsGetallReq;
import com.maan.eway.common.req.PaymentDetailsHistoryReq;
import com.maan.eway.common.req.PaymentDetailsSaveReq;
import com.maan.eway.common.req.PaymentDetailsSaveRes;
import com.maan.eway.common.req.PaymentInfoGetAllReq;
import com.maan.eway.common.req.PaymentInfoGetReq;
import com.maan.eway.common.req.PaymentResUrlReq;
import com.maan.eway.common.req.SavePremiumDepositReq;
import com.maan.eway.common.req.TinyUrlGenerateReq;
import com.maan.eway.common.req.TinyUrlGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.LoginEncryptResponse;
import com.maan.eway.common.res.PaymentDetailGetRes;
import com.maan.eway.common.res.PaymentInfoGetRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.res.TinyUrlGetRes;
import com.maan.eway.common.service.DepositService;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.document.req.DocTypeDropDownReq;
import com.maan.eway.document.res.DocumentDropdownRes;
import com.maan.eway.document.res.DocumentSectionList;
import com.maan.eway.document.res.DocumentTypeDetails;
import com.maan.eway.document.res.LocationWiseSections;
import com.maan.eway.document.service.impl.DocumentServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.TrackingDetailsSaveReq;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.master.service.impl.ClausesMasterServiceImpl;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDocumentMasterRepository;
import com.maan.eway.repository.DepositcbcMasterRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.DocumentUniqueDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.NotifTemplateMasterRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentRefnoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.repository.SeqPaymentidRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.service.CalculatorEngine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Service
@Transactional

public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
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
	
	@Lazy
	@Autowired
	private CalculatorEngine calcService;
	
	@Autowired
	private EserviceTravelDetailsRepository eserTraRepo;
	
	@Autowired
	private TrackingDetailsService trackingService;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRiskRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
//	@Value(value ="${madison.auth}")
//	private String madisonAuth;
	
	@Autowired
	private LoginBranchMasterRepository lbranchRepo ;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo ;
	
	@Autowired
	private DocumentUniqueDetailsRepository docUploadRepo ;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo ;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo ;
	
	@Autowired
	private TravelPassengerDetailsRepository passengerRepo ;
	
	@Autowired
	private CommonDataDetailsRepository commonRepo ;
	
	@Lazy
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
	private EserviceBuildingDetailsRepository eserBuildingRepo;
	
	@Autowired
	private SmsDetailsImpl smsRepo ;
	
	@Autowired
	private NotifTemplateMasterRepository notifRepo;
	
//	@Autowired
//	private TiraIntegerationServiceImpl tiraIntegService ;

	@Autowired
	private ProductEmployeesDetailsRepository empDetailsRepo;
	
	@Autowired
	private SectionMasterRepository smRepo;
	

	@Autowired
	private  SectionDataDetailsRepository sddRepo;
	
	@Autowired
	private  DocumentTransactionDetailsRepository docTransDetails;
	@Autowired
	private SelcomPaymentService selcomService;
	
	@Autowired
	private DocumentServiceImpl docServ;
	
	@Autowired
	private	CoverDocumentMasterRepository docRepo;

	@Autowired
	private DepositService depoService;

	@Autowired
	private DepositcbcMasterRepository depositcbcRepo;

	@Autowired
	private LoginProductMasterRepository loginProductRepo;

	@Autowired
	private EserviceTravelGroupDetailsRepository groupRepo;
	
	@Autowired
	private ChartAccountServiceImpl accountServiceImpl;

	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);

	Gson json = new Gson();
	@SuppressWarnings("unlikely-arg-type")
	@Override
	public List<String> validatemakepayment(MakePaymentSaveReq req) {
		List<String> error = new ArrayList<String>();

		try {
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add("1094");
//				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			if(StringUtils.isBlank(req.getEmiYn())) {
				error.add("1095");
//				error.add(new Error("01","Emi Yn","Please select Emi Yes/No"));
			} else if(req.getEmiYn().equalsIgnoreCase("Y") ) {
				
				if(StringUtils.isBlank(req.getInstallmentMonth())) {
					error.add("1096");
//					error.add(new Error("01","InstallmentMonth","Please Enter InstallmentMonth"));
				}
				if(StringUtils.isBlank(req.getInstallmentPeriod())) {
					error.add("1097");
//					error.add(new Error("01","InstallmentPeriod","Please Enter InstallmentPeriod"));
				}
			}
			
			// Premium Validation
			if(StringUtils.isBlank(req.getPremium())) {
				error.add("1098");
//				error.add(new Error("01","Premium","Please Enter Premium"));
			} else if (!req.getPremium().matches("^-?[0-9]\\d*(\\.\\d+)?$") )  {
				error.add("1098");
//				error.add(new Error("01","Premium","Please Enter Valid Premium"));
				
			} else if (StringUtils.isNotBlank(req.getEmiYn()) && req.getEmiYn().equalsIgnoreCase("Y") && StringUtils.isNotBlank(req.getInstallmentMonth()) 
					&& StringUtils.isNotBlank(req.getInstallmentPeriod())  )  {
				Double premium =0d;
				Double overall=0d;
				List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNoAndSelectYn(req.getQuoteNo(),"Y");
				if(emiDetails.size()>0) {
				Double getData = emiDetails.stream()
						.filter(o -> o.getSelectYn().equalsIgnoreCase("Y"))
						.mapToDouble( o ->   o.getDueAmount().doubleValue()).sum();
				String pattern = "#####0";
			 	DecimalFormat decimalFormat = new DecimalFormat(pattern);
			 	premium =  Double.valueOf (decimalFormat.format(Double.valueOf (req.getPremium())));
			 	overall =  Double.valueOf (decimalFormat.format(getData));
			 	}else {
				EmiTransactionDetails  emiDetails1 = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,req.getInstallmentMonth() , req.getInstallmentPeriod());
				String pattern = "#####0";
			 	DecimalFormat decimalFormat = new DecimalFormat(pattern);
			 	premium =  Double.valueOf (decimalFormat.format(Double.valueOf (req.getPremium())));
			 	overall =  Double.valueOf (decimalFormat.format(emiDetails1.getDueAmount()));
			 	}
				if(! premium.equals(overall) ) {
					error.add("1106");
//					error.add(new Error("01","Premium","Premium Mismatched. Given Premium : " + req.getPremium() + " Policy Premium :" + overall));
				}
			} else  {
				HomePositionMaster  findQuote = homerepo.findByQuoteNo(req.getQuoteNo());
				String pattern = "#####0";
			 	DecimalFormat decimalFormat = new DecimalFormat(pattern);
			 	Double premium =  Double.valueOf (decimalFormat.format( Double.valueOf (req.getPremium())));
			 	Double overall =  Double.valueOf (decimalFormat.format(findQuote.getOverallPremiumLc()));
			 	if(! premium.equals(overall) &&  StringUtils.isBlank(findQuote.getEndtTypeId())) {
			 		error.add("1106");
//			 		error.add(new Error("01","Premium","Premium Mismatched. Given Premium : " + premium + " Policy Premium :" +  overall));
				}
				
			}
			
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add("1100");
//				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add("1101	");
//				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add("1102");
//				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
			if(StringUtils.isBlank(req.getRemarks())) {
				error.add("1103");
//				error.add(new Error("01","Remarks","Please Enter Remarks"));
			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add("1104");
//				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
		
			List<PaymentInfo> filterAccepted = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") ).collect(Collectors.toList());		
			
		
			if(filterAccepted.size()> 0) {
				if ( req.getEmiYn().equalsIgnoreCase("Y" ) && StringUtils.isNotBlank(req.getInstallmentMonth()) && StringUtils.isNotBlank(req.getInstallmentPeriod()) )  {
					List<PaymentInfo> filterEmi = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && o.getInstallmentMonth().equalsIgnoreCase(req.getInstallmentMonth()) && 
	  						o.getInstallmentPeriod().equalsIgnoreCase(req.getInstallmentPeriod()) ).collect(Collectors.toList());
					
					if(filterEmi.size()>0 ) {
						error.add("1105");
//						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
					}
				
				} else {
					error.add("1105");
//					error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
				}
				
				
			}
			
			//Doc validation
			
			//get upload document details
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Integer> query1 = cb.createQuery(Integer.class);

			Root<DocumentTransactionDetails> td = query1.from(DocumentTransactionDetails.class);
			Root<DocumentUniqueDetails> ud = query1.from(DocumentUniqueDetails.class);

			query1.select(ud.get("documentId"));

			Predicate m1 = cb.equal(td.get("quoteNo"), req.getQuoteNo());
			Predicate m2 = cb.equal(ud.get("uniqueId"), td.get("uniqueId"));
		
			query1.where(m1, m2);

			TypedQuery<Integer> result1 = em.createQuery(query1);
			List<Integer> list1 = result1.getResultList();
					
		
			String companyId = "";
			String productId = "";
			String sectionId = "";
			
			List<SectionDataDetails> sec =  sddRepo.findByQuoteNo(req.getQuoteNo());
			if(sec.size()>0) {
				companyId = sec.get(0).getCompanyId();
				productId = sec.get(0).getProductId();	
				sectionId = sec.get(0).getSectionId();
				}
			
			//Mandatory Y details get
			List<CoverDocumentMaster> list = new ArrayList<CoverDocumentMaster>();
			CriteriaQuery<CoverDocumentMaster> query = cb.createQuery(CoverDocumentMaster.class);
			Root<CoverDocumentMaster> b = query.from(CoverDocumentMaster.class);
			query.select(b);

			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CoverDocumentMaster> ocpm1 = amendId.from(CoverDocumentMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("documentId"), b.get("documentId"));
			Predicate a2 = cb.equal(b.get("coverId"), ocpm1.get("coverId"));
			Predicate a3 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			Predicate a5 = cb.equal(b.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a6 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			
			amendId.where(a1, a2, a3, a5, a6);

			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("documentId")));

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
	//		Predicate n2 = cb.equal(b.get("sectionId"), "99999");
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"), productId);
			Predicate n5 = cb.equal(b.get("coverId"), "99999");
			Predicate n6 = cb.equal(b.get("status"), "Y");
			Predicate n7 = cb.equal(b.get("mandatoryStatus"), "Y");

			query.where(n1, n3, n4, n5,n6,n7).orderBy(orderList);

			TypedQuery<CoverDocumentMaster> result = em.createQuery(query);
			list = result.getResultList();
		//	list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getDocumentId()))).collect(Collectors.toList());
			
			List<CoverDocumentMaster> filtercomm = list.stream().filter(o->o.getSectionId().equals(99999)).collect(Collectors.toList());
			filtercomm = filtercomm.stream().filter(distinctByKey(o -> Arrays.asList(o.getDocumentId()))).collect(Collectors.toList());	
			
			if(filtercomm.size()>0) {
				for(CoverDocumentMaster coverDoc : filtercomm) { 
					
					if(list1.size()>0) {
						
						if( ! list1.contains(coverDoc.getDocumentId()))	{
							if(coverDoc.getSectionId()==99999)
								error.add("1108");
//								error.add(new Error("01","Common Doc", coverDoc.getDocumentName() + " is Mandatory In Common Document"));
					}}else {
						if(coverDoc.getSectionId()==99999) {
								error.add("1108");
//								error.add(new Error("01","Common Doc", coverDoc.getDocumentName() + " is Mandatory In Common Document"));
						}
						
					}
					
				
				}
			}
			
		
			//
			CompanyProductMaster product =  getCompanyProductMasterDropdown(companyId , productId.toString());
			

			List<DocumentTransactionDetails> upload = docTransDetails.findByQuoteNoAndProductId(
					req.getQuoteNo(),Integer.valueOf(productId) );
			
			DocTypeDropDownReq req1 = new DocTypeDropDownReq();
			req1.setQuoteNo(req.getQuoteNo());
			req1.setCompanyId(req.getInsuranceId());
			
			DocumentTypeDetails res = docServ.getLocationWiseSections(req1);	
			List<LocationWiseSections>  indiDocs = res.getInduvidualDocuments();	
			if(indiDocs.size()>0) {
				for(LocationWiseSections loca : indiDocs) {
					Integer locId = Integer.valueOf(loca.getLocationId());
					String locName = loca.getLocationName();
					List<DocumentSectionList> secList = loca.getSectionList();
					
					for(DocumentSectionList section : secList) {
						
						Integer secId = Integer.valueOf( section.getSectionId());
						String secName = section.getSectionName();
						List<CoverDocumentMaster> docmandatoryfilter = list.stream().filter(o->o.getSectionId().equals(secId)).collect(Collectors.toList());						
						docmandatoryfilter = docmandatoryfilter.stream().filter(distinctByKey(o -> Arrays.asList(o.getDocumentId()))).collect(Collectors.toList());	
						
						List<DocumentDropdownRes> docList = section.getIdList();	
						for(DocumentDropdownRes doc : docList) {
							Integer riskId = Integer.valueOf(doc.getRiskId());
								for(CoverDocumentMaster mandatorydoc : docmandatoryfilter) {
									
									List<DocumentTransactionDetails> uploadfilter =upload.stream().filter(o-> 
									
									(o.getId().equals(doc.getId())) && (o.getIdType().equals(doc.getIdType())) && (o.getRiskId().equals(riskId))
									&& (o.getSectionId().equals(secId)) && (o.getLocationId().equals(locId))).collect(Collectors.toList());	
							
									if(!(uploadfilter.size()>0)) {
										if((product.getMotorYn().equalsIgnoreCase("H") &&  productId.equals("4")) || product.getMotorYn().equalsIgnoreCase("M")){
											error.add("1107");
//											error.add(new Error("01","Individual Doc", mandatorydoc.getDocumentName() + " is Mandatory In Individual Document" 
//											+ " for section: "+secName+", "+ doc.getIdType()+": "+doc.getId())); 
											}
										else
											error.add("1107");
//											error.add(new Error("01","Individual Doc", mandatorydoc.getDocumentName() + " is Mandatory In Individual Document" 
//										+ " for Location: "+locName+", section: "+secName+", "+ doc.getIdType()+": "+doc.getId()));	
									
								}				
								}
						}
						
					}
				}
				
			}	
			//Product employee validation
			if(product.getMotorYn().equalsIgnoreCase("H") && productId.equalsIgnoreCase("4") ) {

				error.addAll(checkGroupValidation(req.getQuoteNo()));
					
					
			} else if (product.getMotorYn().equalsIgnoreCase("H")) {
				if (!(productId.equalsIgnoreCase("43") || productId.equalsIgnoreCase("27"))) {
					error.addAll(employeeCountAndSIValid(req.getQuoteNo(), sectionId));
				}
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			error.add("1112");
//			error.add(new Error("01","Common Error",  e.getMessage()));
		}
		return error;
	}

	
	


	public Integer getBackDays(String companyId , String productId , String loginId ) {
		Integer backDays = 0 ;
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

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			jakarta.persistence.criteria.Predicate a3 = cb.equal(b.get("loginId"), ocpm1.get("loginId"));
			jakarta.persistence.criteria.Predicate a4 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a11 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
			jakarta.persistence.criteria.Predicate a12 = cb.equal(b.get("id"), ocpm1.get("id"));
			effectiveDate.where(a1, a2, a3,a4,a11,a12);
			
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			jakarta.persistence.criteria.Predicate a8 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			jakarta.persistence.criteria.Predicate a9 = cb.equal(b.get("loginId"), ocpm2.get("loginId"));
			jakarta.persistence.criteria.Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			jakarta.persistence.criteria.Predicate a13 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
			jakarta.persistence.criteria.Predicate a14 = cb.equal(b.get("id"), ocpm2.get("id"));
			effectiveDate2.where(a6,  a8, a9, a10,a13,a14);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);
			Predicate n6 = cb.equal(b.get("policyType"),"99999");
			Predicate n7 = cb.equal(b.get("id"),"99999");
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			list = result.getResultList();
			backDays = list.size() > 0 ? (list.get(0).getBackDays() !=null ? list.get(0).getBackDays() : 0)  : 0 ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return backDays;
	}
	


	private List<String> checkGroupValidation(String quoteNo  ) {
		List<String> errors = new ArrayList<String>();
		try {
			
			List<EserviceTravelGroupDetails> groupDetails = groupRepo.findByQuoteNoOrderByGroupIdAsc(quoteNo);
			List<TravelPassengerDetails>     passengerList = passengerRepo.findByQuoteNoAndStatusNot(quoteNo, "D");
			if(groupDetails.size() > 0 && (groupDetails.get(0).getEndorsementType()==null ||  groupDetails.get(0).getEndorsementType() != 842)  ) {
				// Group Validation 
				for ( EserviceTravelGroupDetails group : groupDetails ) {
					List<TravelPassengerDetails> filterList = passengerList.stream().filter( o -> o.getGroupId()!=null && Integer.valueOf(o.getGroupId()).equals(group.getGroupId())  )
							.collect(Collectors.toList()) ;
					if( group.getGrouppMembers() < filterList.size() ) {
						errors.add("1109");
//						errors.add(new Error("12", "Group", "Group : " + group.getGroupDesc() + " Number Of Passengers Greater Than " + group.getGrouppMembers() + " Passengers Not Allowed" ));
					} else if( group.getGrouppMembers() > filterList.size() ) {
						errors.add("1110");
//						errors.add(new Error("12", "Group", "Group : " + group.getGroupDesc() + " Number Of Passengers Lesser Than  " + group.getGrouppMembers() + " Passengers Not Allowed" ));
					}
					
				}
			
				List<TravelPassengerDetails> filterSelf1 = passengerList.stream().filter( o -> o.getRelationId()!=null && Integer.valueOf(o.getRelationId()).equals(9)  )
						.collect(Collectors.toList()) ;
				List<TravelPassengerDetails> filterSelf2 =passengerList.stream().filter( o -> o.getRelationId()!=null && Integer.valueOf(o.getRelationId()).equals(10)  )
						.collect(Collectors.toList()) ;
				if(filterSelf1.size()<=0 && filterSelf2.size()<=0 ) {
					errors.add("1111");
//					errors.add(new Error("12", "SelfRelation", " Self Relation is missing in Passenger Details" ));
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errors.add("1112");
			System.out.println("Common Error"+ e.getMessage());
//			errors.add(new Error("01","Common Error",  e.getMessage()));
		}
		return errors;
	}
	private List<String> employeeCountAndSIValid(String quoteNo , String sectionId ) {
		List<String> error = new ArrayList<String>();
		try {
			int checkCount = 0;
			int empCount = 0;
			double totalSi = 0.0;
			double empSi = 0.0;
			boolean temp1 = true;
			int indivcount = 0;
			String sectionname = "";
			List<SectionDataDetails> sm = sddRepo.findBySectionId(sectionId);
			sectionname = sm.get(0).getSectionDesc();
			
			
			List<CommonDataDetails> commonDatas = new ArrayList<CommonDataDetails>();
			List<ProductEmployeeDetails> reqList = 	empDetailsRepo.findByQuoteNoAndSectionIdAndStatusNot(quoteNo,sectionId,"D")	;
			
			if(reqList!=null && reqList.size()> 0 && StringUtils.isNotBlank(quoteNo)  ) {
				commonDatas = commonRepo.findByQuoteNoAndSectionId(quoteNo,sectionId);
				commonDatas = commonDatas.stream().filter( o ->  ! o.getStatus().equalsIgnoreCase("D") ).collect(Collectors.toList());
			}  /*else {
				error.add("1113");
//				error.add(new Error("01", "QuoteNo", "Please Enter Atleat one Employee Details "));
			}*/
			
			
			empCount = commonDatas.stream().mapToInt(o ->  o.getCount().intValue()).sum(); 
			
			//count
			if(reqList.size()>empCount || reqList.size()<empCount) {
				if(commonDatas.get(0).getProductId().equalsIgnoreCase("19"))
					error.add("1114");
//					error.add(new Error("01", "Employees Count", "Employee's Details Count Should be "+empCount+" for section "+ " '"+sectionname+"'"));
				else
					error.add("1114");
//					error.add(new Error("01", "Employees Count", "Employee's Details Count Should be "+empCount));
			} 
			
			if(error.size()<1) {
			//Total si & Individual occupation count
			for(CommonDataDetails cdata: commonDatas) {
				indivcount = 0;
				checkCount = 0;
				empSi = 0.0;
				indivcount = indivcount + cdata.getCount().intValue(); 
				
				totalSi = cdata.getSumInsured()==null?0.0:cdata.getSumInsured().doubleValue();

				checkCount  = (int) reqList.stream().filter(o-> o.getOccupationId()!=null&& o.getOccupationId().equalsIgnoreCase(cdata.getOccupationType())).count();
				empSi = reqList.stream().filter(o->o.getOccupationId()!=null&&o.getOccupationId().equalsIgnoreCase(cdata.getOccupationType()))
						.mapToDouble(o -> Double.valueOf(o.getSalary().toString())).sum() ;
				
				if(indivcount!=checkCount) {
					if(cdata.getProductId().equalsIgnoreCase("19") )
						error.add("1115");
//						error.add(new Error("01", "Occupation Count", "Employee Details count should be "+indivcount+" for Occupation "+"'"+cdata.getOccupationDesc()+"' "+" for section "+ " '"+sectionname+"'"));
					else if(!cdata.getProductId().equalsIgnoreCase("56"))
						error.add("1115");
//						error.add(new Error("01", "Occupation Count", "Employee Details count should be "+indivcount+" for Occupation "+"'"+cdata.getOccupationDesc()+"'"));
					temp1 = false;
				}
				if(error.size()<1) {
				if(totalSi!=empSi) {
					if(cdata.getProductId().equalsIgnoreCase("19"))
						error.add("1116");
//						error.add(new Error("01", "Sum Insured", "Total SumInsured not equal to the Actual SumInsured for occupation "+"'"+cdata.getOccupationDesc()+"'"+" for section "+ " '"+sectionname+"'"));
					else
						error.add("1116");
//						error.add(new Error("01", "Sum Insured", "Total SumInsured not equal to the Actual SumInsured for occupation "+"'"+cdata.getOccupationDesc()+"'" ));
					temp1 = false;
				}
				}
				if(!temp1) 
					break;
			}
			}	

		
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			error.add("1112");
//			error.add(new Error("01","Common Error",  e.getMessage()));
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CoverDocumentMaster> ocpm1 = effectiveDate.from(CoverDocumentMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			jakarta.persistence.criteria.Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			jakarta.persistence.criteria.Predicate a4 = cb.equal(c.get("coverId"), ocpm1.get("coverId"));
			Predicate a11 = cb.equal(c.get("documentId"), ocpm1.get("documentId"));
			jakarta.persistence.criteria.Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4, a5,a11);
			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CoverDocumentMaster> ocpm2 = effectiveDate2.from(CoverDocumentMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
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
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"),companyId);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), productId);
			jakarta.persistence.criteria.Predicate n5 = e0.in(sectionIds);
			jakarta.persistence.criteria.Predicate n6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n7 = cb.equal(c.get("mandatoryStatus"), "Y");
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
				String productName =   getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString()).getProductName();//productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
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
//					paymentinfo.setPremium(new BigDecimal( emiDetails.getPremiumWithTax()));
//					paymentinfo.setPremiumLc(new BigDecimal( emiDetails.getPremiumWithTax() ));
//					
//					BigDecimal premiumFc = paymentinfo.getPremiumLc().multiply(data.getExchangeRate(), MathContext.DECIMAL128 );
//					paymentinfo.setPremiumFc( new BigDecimal(df.format(premiumFc)));
					paymentinfo.setPremium(new BigDecimal(req.getPremium()));
					paymentinfo.setPremiumLc(new BigDecimal( req.getPremium() ));
					
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
				HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
				PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
				String productName =   getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString()).getProductName();//productRepo.findByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
				String companyName =  getInscompanyMasterDropdown(data.getCompanyId()) ; // companyRepo.findByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
				String branchName = getCompanyBranchMasterDropdown(data.getCompanyId() , data.getBranchCode());
				
				// Save Paymetn Info
				PaymentInfo paymentinfo = filterPendings.get(0) ;
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
//					paymentinfo.setPremium(new BigDecimal( emiDetails.getPremiumWithTax()));
//					paymentinfo.setPremiumLc(new BigDecimal( emiDetails.getPremiumWithTax() ));
//					
//					BigDecimal premiumFc = paymentinfo.getPremiumLc().multiply(data.getExchangeRate(), MathContext.DECIMAL128 );
//					paymentinfo.setPremiumFc( new BigDecimal(df.format(premiumFc)));
					
					paymentinfo.setPremium(new BigDecimal(req.getPremium()));
					paymentinfo.setPremiumLc(new BigDecimal( req.getPremium() ));
					
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
				Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2);
				// Effective Date End Max Filter
				Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				//Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				//Predicate n8 = cb.or(n4,n5);
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
			//	Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
				query.where(n1,n2,n3,n4,n9,n10).orderBy(orderList);
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
				Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
				Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<InsuranceCompanyMaster> ocpm1 = effectiveDate.from(InsuranceCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			jakarta.persistence.criteria.Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			
		    // Where	
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
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
		
			PaymentDetail data = paymentdetailrepo.findByQuoteNoAndPaymentIdAndMerchantReference(req.getQuoteNo(),req.getPaymentId(),req.getMerchantReference());
			
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
	public List<String> validatePaymentInsert(PaymentDetailsSaveReq req) {
		List<String> error = new ArrayList<String>();

		try {
			
			HomePositionMaster hp = homerepo.findByQuoteNo(req.getQuoteNo());
			if(hp!=null) { 
				
				if (hp.getInceptionDate() == null) {
					error.add("1118");
//					error.add(new Error("13", "PolicyStartDate", "Please Enter PolicyStartDate"));
				} else if( ( hp.getEndtTypeId()==null || hp.getEndtTypeId().equalsIgnoreCase("0"))) {
//						int before = getBackDays(hp.getCompanyId() , String.valueOf( hp.getProductId()) , req.getCreatedBy()) ;
//						int days = before ==0 ? -1 : - before ;
//						long MILLS_IN_A_DAY = 1000*60*60*24;
//						long backDays = MILLS_IN_A_DAY * days ;
//						Date today = new Date() ;
//						Date resticDate = new Date(today.getTime() + backDays);
//						long days90 = MILLS_IN_A_DAY * 90 ;
//						Date after90 = new Date(today.getTime() + days90);
					// Date Validation
					Calendar cal = new GregorianCalendar();
					Date today = new Date();
					cal.setTime(today);
					cal.add(Calendar.DAY_OF_MONTH, -1);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 50);
					today = cal.getTime();
					
						if( hp.getInceptionDate().before(today) ) {
							error.add("1119");
//							error.add(new Error("14", "PolicyStartDate", "Policy Start Date Back Days Not Allowed "));
						} 
//						else if( hp.getInceptionDate().after(after90) ) {
//							error.add(new Error("14", "PolicyStartDate", "PolicyStartDate  even after 90 days Not Allowed"));
//						}
					
				}
				
			}	
			
			if(StringUtils.isBlank(req.getQuoteNo())){
				error.add("1120");
//				error.add(new Error("01","Quote No","Please Enter Quote No"));
			}
			
			if(StringUtils.isBlank(req.getCreatedBy())) {
				error.add("1121");
//				error.add(new Error("01","CreatedBy","Please Enter CreatedBy"));
			}
			if(StringUtils.isBlank(req.getUserType())) {
				error.add("1122");
//				error.add(new Error("01","UserType","Please Enter UserType"));
			}
			if(StringUtils.isBlank(req.getSubUserType())) {
				error.add("1123");
//				error.add(new Error("01","SubUserType","Please Enter SubUserType"));
			}
//			if(StringUtils.isBlank(req.getRemarks())) {
//				error.add(new Error("01","Remarks","Please Enter Remarks"));
//			}
			if(StringUtils.isBlank(req.getInsuranceId())) {
				error.add("1124");
//				error.add(new Error("01","InsuranceId","Please Enter InsuranceId"));
			}
			if(StringUtils.isBlank(req.getPaymentType())) {
				error.add("1125");
//				error.add(new Error("01","PaymentType","Please Select PaymentType"));
			}
			
			if(StringUtils.isNotBlank(req.getPayments()) && req.getPayments().equalsIgnoreCase("Refund") ){
				if(StringUtils.isBlank(req.getAccountNumber())) {
					error.add("1126");
//					error.add(new Error("01","AccountNumber","Please Enter AccountNumber "));
				}
				if(StringUtils.isBlank(req.getIbanNumber())) {
					error.add("1127");
//					error.add(new Error("01","IbanNumber","Please Enter IbanNumber"));
				}
			}
			
			if("2".equals(req.getPaymentType())) {
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
				today = cal.getTime();
				if(StringUtils.isBlank(req.getBankName())) {
					error.add("1128");
//					error.add(new Error("01","BankName","Please Enter BankName"));
				}
				if (StringUtils.isBlank(req.getPayments())  || ( StringUtils.isNotBlank(req.getPayments()) && ! req.getPayments().equalsIgnoreCase("Refund") ) ){
					if(StringUtils.isBlank(req.getChequeNo())) {
						error.add("1129");
//						error.add(new Error("01","ChequeNo","Please Enter ChequeNo"));
					}else if (! req.getChequeNo().matches("[0-9]+")) {
						error.add("1130");
//						error.add(new Error("01","ChequeNo","Please Enter valid Number in ChequeNo"));
					}else if (req.getChequeNo().length() != 6 ) {
						error.add("1131");
//						error.add(new Error("01","ChequeNo","ChequeNo Must Be 6 Digits only allowed "));
					}else if (req.getChequeDate() == null) {
						error.add("1132");
//						error.add(new Error("04", "ChequeDate", "Please Enter ChequeDate "));
					} else if (req.getChequeDate().before(today)) {
						error.add("1133");
//						error.add(new Error("04", "ChequeDate", "Please Enter ChequeDate as Future Date"));
					}
					
					if(StringUtils.isBlank(req.getMicrNo())) {
						error.add("1134");
//						error.add(new Error("01","MicrNo","Please Enter MicrNo"));
//					}else if (! req.getMicrNo().matches("^[a-zA-Z0-9 ]+")) {
//						error.add(new Error("01","MicrNo","Please Enter valid MicrNo"));
					}else if ( req.getMicrNo().length() < 6 || req.getMicrNo().length() > 8 ) {
						error.add("1135");
//						error.add(new Error("01","MicrNo","MicrNo  Must be Min 6 To Max 8 Charecter Only Allowed"));
					}
					
				}
				
					
			
			}else if("1".equals(req.getPaymentType())) {
				if(StringUtils.isBlank(req.getPayeeName())) {
					error.add("1136");
//					error.add(new Error("01","PayeeName","Please Enter PayeeName"));
				}else if(StringUtils.isNotBlank(req.getPayeeName()) && !req.getPayeeName().matches("[a-zA-Z ]*$") && !req.getPayeeName().matches("^[a-zA-ZÀ-ÿ\\s'-]+$")) {
					error.add("1137");
//					error.add(new Error("01","PayeeName","Please Enter Valid PayeeName"));
				}
			}
			if("3".equals(req.getPaymentType())) {
				
				HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
				
				Long cbcDatacount=depositcbcRepo.countByBrokerIdAndStatus(homeData.getAgencyCode().toString(),"Y");
				if(cbcDatacount==0){
					error.add("1138");
//					error.add(new Error("01","Credit","Credit Option is not Activated For This Broker"));
				}else {
				Long count=loginProductData(homeData.getLoginId(),homeData.getCompanyId(),homeData.getProductId());
				if(count==0) {
					error.add("1139");
//					error.add(new Error("01","Credit","Credit Option is not Available  For This Product"));
				}
				}
				
			}
			
			// Check Paymetn Info
			if (StringUtils.isNotBlank(req.getQuoteNo()) && StringUtils.isNotBlank(req.getPaymentId()) ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
				if(paymentInfo!=null) {
					if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Accepted") ) {
						error.add("1140");
//						error.add(new Error("01","Accepted","This Payment Already Accepted "));

					} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Rejected") ) {
						error.add("1141");
//						error.add(new Error("01","Rejected","This Payment Already Reject"
//								+ "ed "));

					} else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Cancelled") ) {
						error.add("1142");
//						error.add(new Error("01","Cancelled","This Payment Already Cancelled"));

					} /*else if(  paymentInfo.getPaymentStatus().equalsIgnoreCase("Pending") && StringUtils.isNotBlank(paymentInfo.getMerchantReference())  )  {
						error.add(new Error("01","Cancelled","This Payment Already Pending"));
					}*/
				}
			}
			
			
			
			// Other Payment Id Validation
			List<PaymentInfo> datas = paymentinforepo.findByQuoteNoOrderByEntryDateDesc(req.getQuoteNo());
			
			if (datas.size() > 0 ) {
				List<PaymentInfo> filterPendings = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Pending") && ! o.getPaymentId().equalsIgnoreCase(req.getPaymentId()) ) .collect(Collectors.toList());		
				List<PaymentInfo> filterAccepted = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && ! o.getPaymentId().equalsIgnoreCase(req.getPaymentId())  ).collect(Collectors.toList());		
				
				/*if(filterPendings.size()> 0) {
					error.add(new Error("01","PaymentId","Already One Payment Id Pending Against This Quote No"));
				}*/
				
				if(filterAccepted.size()> 0) {
					PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(req.getQuoteNo(), req.getPaymentId());
					if ( paymentInfo.getEmiYn().equalsIgnoreCase("Y" ) && StringUtils.isNotBlank(paymentInfo.getInstallmentMonth()) && StringUtils.isNotBlank(paymentInfo.getInstallmentPeriod()) )  {
						
						List<PaymentInfo> filterEmi = datas.stream().filter( o -> o.getPaymentStatus().equalsIgnoreCase("Accepted") && o.getInstallmentMonth().equalsIgnoreCase(paymentInfo.getInstallmentMonth()) && 
								  						o.getInstallmentPeriod().equalsIgnoreCase(paymentInfo.getInstallmentPeriod()) ).collect(Collectors.toList());
						if(filterEmi.size()>0 ) {
							error.add("1143");
//							error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
						}
					
					} else {
						error.add("1143");
//						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
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
							error.add("1143");
//							error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
						}
					
					} else {
						error.add("1143");
//						error.add(new Error("01","PaymentId","Already One Payment Id Accepted Against This Quote No"));
					}
				}
				
				
			}
			
			if(error.isEmpty())
			{
				HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
				if ( StringUtils.isBlank(data.getEndtTypeId()) ) {
					if( req.getPremium()==null ) {
						error.add("1144");
//						error.add(new Error("01","Premium","Please Enter Premium "));
					} else if ( req.getPremium().compareTo(new BigDecimal("0")) <= 0) {
						error.add("1145");
//						error.add(new Error("01", "Premium", "Please Enter Premium Above Zero"));
					}
					if (StringUtils.isNotBlank(req.getEmiYn())) {
						if (!"Y".equalsIgnoreCase(req.getEmiYn())) {
							if (data.getOverallPremiumFc().compareTo(req.getPremium()) > 0) {
								error.add("1146");
//								error.add(new Error("01", "Premium",
//										"Required Premium Should Not be Lesser than " + data.getOverallPremiumFc()));
							}
						}
					}
				}
 				
			}
			
		/*	// Credit Y/N Validation
			String companyId="";
			Integer productId=null;
			Integer agencyCode=null;
			String creditLimit="";
			HomePositionMaster homeData=homerepo.findByQuoteNo(req.getQuoteNo());
			if(homeData!=null) {
				companyId=homeData.getCompanyId();
				productId=homeData.getProductId();
				agencyCode=homeData.getAgencyCode();
				
			}
			if(StringUtils.isNotBlank(agencyCode.toString()) 
					&& StringUtils.isNotBlank(productId.toString())&& StringUtils.isNotBlank(companyId)) {
				List<BrokerCommissionDetails> loginList= getExistingBrokerById(agencyCode.toString() , companyId ,productId.toString());
				creditLimit=loginList.get(0).getCreditYn();
				if((!"Y".equalsIgnoreCase(creditLimit))||StringUtils.isBlank(creditLimit)) {
					error.add(new Error("500","BrokerId","Credit Option is not Available"));
				}
			}*/
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return error;
	}

	public Long loginProductData(String loginId,String companyId,Integer productId ) {
		Long data=null;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);
			List<LoginProductMaster> list= new ArrayList<LoginProductMaster>();
			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));

			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n3 = cb.equal(b.get("productId"), productId);
			Predicate n4 = cb.equal(b.get("companyId"), companyId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);

			query.where(n1,n3, n4, n5).orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			data=list.size() > 0 ? Long.valueOf(list.get(0).getCreditYn()) : 0 ;

		}catch(Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return data;
	}
	public List<BrokerCommissionDetails> getExistingBrokerById(String brokerId , String InsuranceId , String productId) {
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
			Predicate a1 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		//	Predicate a4 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal( b.get("agencyCode"), brokerId);
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("productId"), productId);
		//	Predicate n5 = cb.equal(b.get("policyTypeId"),policyTypeId);
			
			query.where(n1,n2,n3,n4);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}

	@Override
	@Transactional
	public PaymentDetailsSaveRes savePaymentDetails(PaymentDetailsSaveReq req , String token) {
		PaymentDetailsSaveRes res = new PaymentDetailsSaveRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper ();
		try {
			//Find data from home Position Master
			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			PersonalInfo personaldata = personalrepo.findByCustomerId(data.getCustomerId());
			String companyName =  getInscompanyMasterDropdown(data.getCompanyId()) ; // companyRepo.findByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
			String installment="";
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
			
			String personalInfoMobile=personaldata.getMobileCode1()+""+personaldata.getMobileNo1();
			String requestMobile=req.getMobileCode1()+""+req.getMobileNo1();
			
			paymentDetail.setReqBillToPhone(StringUtils.isBlank(requestMobile)?personalInfoMobile:requestMobile);
			
			paymentDetail.setWhatsappCode(StringUtils.isBlank(req.getWhatsappCode())?personaldata.getMobileCode1():req.getWhatsappCode());			
			paymentDetail.setWhatsappNo(StringUtils.isBlank(req.getWhatsappNo())?personaldata.getMobileNo1():req.getWhatsappNo());
			
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
			paymentDetail.setPayeeName(req.getPayeeName());
			paymentDetail.setMicrNo(req.getMicrNo());
			paymentDetail.setCompanyId(paymentInfo.getCompanyId());
			paymentDetail.setReqBillToAddressState(personaldata.getStateName());
			paymentDetail.setReqBillToAddrPostalCode(personaldata.getPinCode());
			paymentDetail.setReqBillToCountry(personaldata.getNationality());
			if("2".equals(req.getPaymentType())) {
				paymentDetail.setBankCode(req.getBankCode());
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
			JsonObject payment =null;
			if( req.getPaymentType().equalsIgnoreCase("1") || req.getPaymentType().equalsIgnoreCase("2")) {
				paymentStatus = "ACCEPTED" ;
				paymentDetail.setPaymentStatus(paymentStatus);
			}else if(req.getPaymentType().equalsIgnoreCase("4") || req.getPaymentType().equalsIgnoreCase("5")) {
				paymentStatus = "PENDING" ;
				
				
				payment = selcomService.createOrderForPayment(paymentDetail);
				if(payment !=null && "SUCCESS".equalsIgnoreCase(payment.get("result").getAsString())) {
					JsonArray asJsonArray = payment.get("data").getAsJsonArray();
					JsonObject asJsonObject = asJsonArray.get(0).getAsJsonObject();	
					String jsonStr = asJsonObject.get("payment_gateway_url").getAsString();					
					paymentDetail.setShorternUrl(new String(Base64.getDecoder().decode(jsonStr)));
					res.setPaymentUrl(paymentDetail.getShorternUrl());
				}else {
					paymentStatus = "FAILED" ;
				}
				
				paymentDetail.setPaymentStatus(paymentStatus);
			} else {
				paymentStatus = "PENDING" ;
				paymentDetail.setPaymentStatus(paymentStatus);
			}

			//Payment Type Credit
			CommonRes depores=null;
			if (req.getPaymentType().equalsIgnoreCase("3")) {
				List<DepositcbcMaster> cbcData=depositcbcRepo.findByBrokerId(data.getAgencyCode().toString());
				if(cbcData!=null && cbcData.size()>0) {
				//Framing Request  Save Payment Details
				/*SavePaymentDepositReq paymentSaveReq= new SavePaymentDepositReq();
				paymentSaveReq.setCbcNo(cbcData.get(0).getCbcNo());
				paymentSaveReq.setQuoteNo(req.getQuoteNo());
				paymentSaveReq.setPaymentType("1");
				paymentSaveReq.setLoginId(data.getLoginId());
				paymentSaveReq.setPremium(req.getPremium().toString());
				paymentSaveReq.setChequeNo(req.getChequeNo()==null?"":req.getChequeNo());
				paymentSaveReq.setChequeDate(req.getChequeDate()==null?null:req.getChequeDate().toString());
				paymentSaveReq.setAccountNo(req.getAccountNumber()==null?"":req.getAccountNumber());	
				paymentSaveReq.setIbanNumber(req.getIbanNumber()==null?null:req.getIbanNumber());
				paymentSaveReq.setMicrNo(req.getMicrNo()==null?"":req.getMicrNo());
				paymentSaveReq.setPayeeName(req.getPayeeName());
				paymentSaveReq.setReferenceNo(refno);
				paymentSaveReq.setDepositNo("");
				paymentSaveReq.setCompanyId(data.getCompanyId());
				depoService.savePaymentDeposit(paymentSaveReq);*/
				
				SavePremiumDepositReq savDepositePayment =new SavePremiumDepositReq();
				savDepositePayment.setBrokerId(data.getAgencyCode().toString());
				savDepositePayment.setCompanyId(data.getCompanyId());
				savDepositePayment.setCustomerId(data.getCustomerId());
				savDepositePayment.setPremium(req.getPremium().toString());
				savDepositePayment.setProductId(data.getProductId().toString());
				savDepositePayment.setQuoteNo(req.getQuoteNo());
				savDepositePayment.setPayeeName(req.getPayeeName());
				depores=depoService.savePremiumDeposit(savDepositePayment);
				depores.getCommonResponse();
				res.setResponse(depores.getCommonResponse().toString());

				if (depores != null && "SUCCESS".equalsIgnoreCase(depores.getMessage())) {
					if ("Y".equalsIgnoreCase(depores.getCommonResponse().toString())) {
						paymentStatus = "ACCEPTED";
						paymentDetail.setPaymentStatus(paymentStatus);
						paymentDetail.setCbcNo(cbcData.get(0).getCbcNo());
					}
//					else {
//						paymentStatus = "PENDING";
//						paymentDetail.setPaymentStatus(paymentStatus);
//					
//					}

				} 
//				else {
//					paymentStatus = "FAILED";
//					paymentDetail.setPaymentStatus(paymentStatus);
//				
//				}
			}

		}

			
			paymentdetailrepo.saveAndFlush(paymentDetail);
		//	if(req.getPaymentType().equalsIgnoreCase("4") || req.getPaymentType().equalsIgnoreCase("5")) 
				 
			log.info("Saved Details " + json.toJson(paymentDetail));
			
			try{// Notification Trigger
			notificationTrigger(data.getProductId(),req.getQuoteNo(),paymentStatus);
			}catch (Exception e) {
				e.printStackTrace();
			}
			// Update Emi Transaction Details
			if (paymentInfo.getEmiYn().equalsIgnoreCase("Y")) {
					//EmiTransactionDetails emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo(), paymentInfo.getInstallmentMonth(), paymentInfo.getInstallmentPeriod());
					List<EmiTransactionDetails> emiDetails = emiRepo.findByQuoteNoAndSelectYn(req.getQuoteNo(),"Y");
					EmiTransactionDetails saveDate=new EmiTransactionDetails();
					for(EmiTransactionDetails data1:emiDetails) {
						saveDate=dozermapper.map(data1, EmiTransactionDetails.class);
						saveDate.setPaymentStatus("Paid");
						saveDate.setPaymentDetails(paymentMode);
						saveDate.setPaymentDate(new Date());
						saveDate.setPaymentId(req.getPaymentId());
						emiRepo.saveAndFlush(saveDate);
					}
					
				
			}

			// Update Payment Info
			paymentInfo.setValidityDate(validateDate);
			paymentInfo.setShorternUrl(tinyUrl);
			paymentInfo.setPaymentStatus(paymentStatus);
			paymentInfo.setMerchantReference(refno);
			paymentInfo.setPayments( StringUtils.isBlank(req.getPayments() ) ? "Charge" : req.getPayments()  ); 
			if (req.getEmiYn().equalsIgnoreCase("Y" )) {
				//EmiTransactionDetails  emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,paymentInfo.getInstallmentMonth() , paymentInfo.getInstallmentPeriod());
				List<EmiTransactionDetails> emiDetails = emiRepo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateDesc(req.getQuoteNo(), "Paid");
				if(emiDetails!=null) {
					installment=emiDetails.get(0).getInstalment();
					paymentInfo.setEmiYn(req.getEmiYn());
					paymentInfo.setInstallmentMonth(emiDetails.get(0).getInstalment());
				}
			}
			paymentinforepo.saveAndFlush(paymentInfo);
			
			if (req.getEmiYn().equalsIgnoreCase("Y" )) {
				List<EmiTransactionDetails> emiDetails = emiRepo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateDesc(req.getQuoteNo(), "Paid");
				if(emiDetails!=null) {
					installment=emiDetails.get(0).getInstalment();
				
					paymentDetail.setInstallmentMonth(emiDetails.get(0).getInstalment());
				}
				paymentdetailrepo.saveAndFlush(paymentDetail);
			}
			
			
			if (req.getEmiYn().equalsIgnoreCase("Y" )) {
			//	EmiTransactionDetails  emiDetails = emiRepo.findByQuoteNoAndInstalmentAndInstallmentPeriod(req.getQuoteNo() ,paymentInfo.getInstallmentMonth() , paymentInfo.getInstallmentPeriod());
				List<EmiTransactionDetails> emiDetails = emiRepo.findTop1ByQuoteNoAndPaymentStatusOrderByDueDateDesc(req.getQuoteNo(), "Paid");

				if(emiDetails!=null) {
				data.setEmiYn(req.getEmiYn());
				data.setEmiPremium(req.getPremium());
				data.setInstallmentPeriod(emiDetails.get(0).getInstallmentPeriod());
				data.setNoOfInstallment(emiDetails.get(0).getInstalment());
				data.setEmiinstallYn(req.getEmiYn());
				
				}
			}
			data.setPaymentMode(req.getPaymentType());
			data.setPaymentType(paymentDetail.getPaymentTypedesc());
			data.setPaymentStatus(paymentInfo.getEmiYn().equalsIgnoreCase("Y") ? paymentInfo.getPaymentStatus() :"Pending");
			data.setEffectiveDate(data.getInceptionDate());
			data.setPolicyCovertedDate(new Date());			
			homerepo.saveAndFlush(data);
			
			res.setResponse("Payment Success");	
			
			// Policy Convertion
			if("Y".equalsIgnoreCase(req.getEmiYn())){
				if(paymentStatus.equalsIgnoreCase("ACCEPTED") && ( paymentInfo.getEmiYn().equalsIgnoreCase("Y") || paymentInfo.getInstallmentMonth().equalsIgnoreCase(installment) )  ) {
					//List<DebitAndCredit> policyDetails = generatePolicy(paymentInfo,req,paymentDetail,token);
					List<PolicyDrcrDetail> policyDetails =  generatePolicyNew(paymentInfo,req,paymentDetail,token);
					if(!CollectionUtils.isEmpty(policyDetails)) {
						String policyNo = policyDetails.get(0).getPolicyNo();
						List<PolicyDrcrDetail> filterDebit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("DR")).collect(Collectors.toList());
						List<PolicyDrcrDetail> filterCredit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")).collect(Collectors.toList());
						// Debit
						String debitNo = filterDebit.size() > 0 ? filterDebit.get(0).getDocNo() : "";
						// Credit
						String creditNo ="";
						if(filterCredit!=null && !filterCredit.isEmpty()){
							creditNo =filterCredit.get(0).getDocNo();
						}
						
						res.setPolicyNo(policyNo);
						res.setDebitNoteNo(debitNo);
						res.setCreditNoteNo(creditNo);
						res.setResponse("Policy Converted");
					}else {
						res.setResponse("CRDR Premium does not calculated or something went wrong");
					}
				
			}
			}else {
			
			if(paymentStatus.equalsIgnoreCase("ACCEPTED")&& paymentInfo.getEmiYn().equalsIgnoreCase("N")) {
				//List<DebitAndCredit> policyDetails = generatePolicy(paymentInfo,req,paymentDetail,token);
				 List<PolicyDrcrDetail> policyDetails =  generatePolicyNew(paymentInfo,req,paymentDetail,token);
				 if(!CollectionUtils.isEmpty(policyDetails)) {
					String policyNo = policyDetails.get(0).getPolicyNo();
					List<PolicyDrcrDetail> filterDebit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("DR")).collect(Collectors.toList());
					List<PolicyDrcrDetail> filterCredit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")).collect(Collectors.toList());
					// Debit
					String debitNo = filterDebit.size() > 0 ? filterDebit.get(0).getDocNo() : "";
					// Credit
					String creditNo ="";
					if(filterCredit!=null && !filterCredit.isEmpty()){
						creditNo =filterCredit.get(0).getDocNo();
					}
					
					res.setPolicyNo(policyNo);
					res.setDebitNoteNo(debitNo);
					res.setCreditNoteNo(creditNo);
					res.setResponse("Policy Converted");
				 }else {
					res.setResponse("CRDR Premium does not calculated or something went wrong");
				}

			}
			
			}
			res.setPaymentId(paymentDetail.getPaymentId().toString());
			res.setQuoteNo(req.getQuoteNo());
			res.setMerchantReference(refno);
			
			/*if(req.getPaymentType().equalsIgnoreCase("4") || req.getPaymentType().equalsIgnoreCase("5")) {
				res.setIserror(((JsonPrimitive) payment.get("result")).getAsString()); //;
				if(res.getIserror().equals("SUCCESS")) {
					JsonArray array=(JsonArray) payment.get("data");
					JsonElement jsonElement = array.get(0);
					JsonObject asJsonObject = jsonElement.getAsJsonObject();
					res.setPaymentUrl(((JsonPrimitive) asJsonObject.get("payment_gateway_url")).getAsString());
				}else {
					res.setResponse(((JsonPrimitive) payment.get("message")).getAsString());
				}
			}else if(req.getPaymentType().equalsIgnoreCase("3")) {
				res.setIserror(depores.getMessage().toString()); //;
				if(res.getIserror().equals("SUCCESS")) {
					res.setDepositResponse(depores.getCommonResponse().toString());
					}else {
					res.setResponse(depores.getMessage().toString());
				}
				
			}*/
			//Tracking Details
			
			trackingDetailsPayment(data, req.getCreatedBy());
	
		/*if(paymentStatus.equalsIgnoreCase("ACCEPTED") && ( paymentInfo.getEmiYn().equalsIgnoreCase("N") || paymentInfo.getInstallmentMonth().equalsIgnoreCase("0") )  ) {
			//// Call Tira Insert				
						TiraFrameReqCall tiraReq = new TiraFrameReqCall();
						tiraReq.setQuoteNo(data.getQuoteNo());					
						tiraIntegService.callTiraIntegeration(tiraReq , token );
		}*/
	}catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
		}
		return res;
	}
	
	public List<DebitAndCredit>  generatePolicy(PaymentInfo paymentInfo, PaymentDetailsSaveReq req, PaymentDetail paymentDetail, String token) {
		try {

			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			//String paymentMode = getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_MODE",req.getPaymentType());
			
							
								


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
			
			
			String policyNo = calcService.getPolicyNo(policyReq);
			//policyDetails = calcService.commissionCalc(policyReq);

			ChartAccountRequest request = new ChartAccountRequest();
			request.setQuoteNo(req.getQuoteNo());
			request .setPolicyNo(policyNo);
			request.setDiscountYn("N");
			CommonRes res =accountServiceImpl.drcrEntry(request);
			
			HomePositionMaster hpm =homerepo.findByQuoteNo(req.getQuoteNo());
			
			List<PolicyDrcrDetail> policydrcr =(List<PolicyDrcrDetail>)res.getCommonResponse();
			
			List<PolicyDrcrDetail> filterDebit=policydrcr.stream().filter(p->"DR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			List<PolicyDrcrDetail> filterCredit=policydrcr.stream().filter(p->"CR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			//List<DebitAndCredit> filterDebit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("DR")).collect(Collectors.toList());
			//List<DebitAndCredit> filterCredit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")).collect(Collectors.toList());

			//String policyNo = policyDetails.get(0).getPolicyNo();
			// Debit
			String debitNo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocNo() :"" ;
			Date debitDate = filterDebit.size() > 0 ? filterDebit.get(0).getEntryDate() : null;
			String debitTo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocType()  : "";
			
			String creditNo ="";
			Date creditDate =null;
			String creditTo = "";
			//BigDecimal commission = new BigDecimal(0);
			//BigDecimal commissionPercent = new BigDecimal(0);
			//BigDecimal commissionVat = new BigDecimal(0);
			if(filterCredit!=null && !filterCredit.isEmpty()) {
			// Credit
			 creditNo =  filterCredit.get(0).getDocNo();
			 creditDate = filterCredit.get(0).getEntryDate();
			 creditTo = filterCredit.get(0).getDocType();
			 
			 
			// Commision
//			 List<DebitAndCredit> commissionList = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") &&
//					 (o.getChargeCode().equals(new BigDecimal(1005)) || o.getChargeCode().equals(new BigDecimal(1001)) )
//						).collect(Collectors.toList())	;
		//	String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1005" ;
			// List<DebitAndCredit> commissionList = policyDetails.stream().filter( o ->(o.getChargeCode().equals(new BigDecimal(1005))||o.getChargeCode().equals(new BigDecimal(1006)) )).collect(Collectors.toList())	;
			/* for ( DebitAndCredit o : commissionList) {
				 commission= commission.add(o.getAmountFc());
				 
			 };
			 // Commission Vat
			 String brokerDrFlag = commission.compareTo(new BigDecimal("0") ) < 0 ? "DR" :"CR"  ;
			List<DebitAndCredit> commissionVatList = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase(brokerDrFlag) &&
			 (o.getChargeCode().equals(new BigDecimal(1009)) )).collect(Collectors.toList())	;
			
			for ( DebitAndCredit o : commissionVatList) {
				commissionVat= commissionVat.add(o.getAmountFc());
				 
			 };*/
//			 commission= policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") 
//						&& (o.getChargeCode().equals(new BigDecimal(1005)) || o.getChargeCode().equals(new BigDecimal(1001)) )
//					 	).collect(Collectors.toList()).get(0).getAmountFc();
//			 commissionPercent=	policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")
//						&& (o.getChargeCode().equals(new BigDecimal(1007))
//								||
//								o.getChargeCode().equals(new BigDecimal(1012))
//								)
//						).collect(Collectors.toList()).get(0).getAmountFc();
//			 commissionPercent=policyDetails.stream().filter( o ->  (o.getChargeCode().equals(new BigDecimal(1007)))).collect(Collectors.toList()).size() 
//					 >0 ?	policyDetails.stream().filter( o ->  (o.getChargeCode().equals(new BigDecimal(1007)))).collect(Collectors.toList()).get(0).getAmountFc() : new BigDecimal(0);
//			 
			}
			
//			List<DebitAndCredit> filtercommissionVat = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")
//					&& o.getChargeCode().equals(new BigDecimal(1012))).collect(Collectors.toList());

//			if (filtercommissionVat.size()>0 ) {
//				commissionVat =  filtercommissionVat.get(0).getAmountFc();
//			}


			// Update Home Posion Master
			data.setDebitNoteNo(debitNo);
			data.setDebitNoteDate(debitDate);
			data.setDebitTo(debitTo);

			data.setCreditNo(creditNo);
			data.setCreditDate(creditDate);	
			data.setCreditTo(creditTo);

//			data.setCommission(commission);
//			data.setCommissionPercentage(commissionPercent);
//			data.setVatCommission(commissionVat);
			data.setPaymentMode(req.getPaymentType());
			data.setPaymentType(paymentDetail.getPaymentTypedesc());
			//data.setPaymentStatus(paymentInfo.getEmiYn().equalsIgnoreCase("N") ? paymentInfo.getPaymentStatus() :"Pending");
			data.setPaymentStatus(paymentInfo.getPaymentStatus());
			data.setPolicyNo(policyNo);
			
			data.setStatus(StringUtils.isNotBlank(data.getEndtTypeId()) && "842".equalsIgnoreCase(data.getEndtTypeId()) ? "D" : "P");
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
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString());
			String msg = updateProductWisePolicyNo(paymentInfo.getProductId().toString() ,policyNo ,req.getQuoteNo(),data.getEndtTypeId() , product.getMotorYn(),hpm.getCommissionPercentage()); 
  
			return policyDetails;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<PolicyDrcrDetail>  generatePolicyNew(PaymentInfo paymentInfo, PaymentDetailsSaveReq req, PaymentDetail paymentDetail, String token) {
		List<PolicyDrcrDetail> policydrcr  = new ArrayList<PolicyDrcrDetail>();
		try {

			HomePositionMaster data = homerepo.findByQuoteNo(req.getQuoteNo());
			//String paymentMode = getListItem (data.getCompanyId() , data.getBranchCode() ,"PAYMENT_MODE",req.getPaymentType());
			
							
								


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
			
			
			String policyNo = calcService.getPolicyNo(policyReq);
			//policyDetails = calcService.commissionCalc(policyReq);

			ChartAccountRequest request = new ChartAccountRequest();
			request.setQuoteNo(req.getQuoteNo());
			request .setPolicyNo(policyNo);
			request.setDiscountYn("N");
			CommonRes res =accountServiceImpl.drcrEntry(request);
			
			HomePositionMaster hpm =homerepo.findByQuoteNo(req.getQuoteNo());
			
			policydrcr =(List<PolicyDrcrDetail>)res.getCommonResponse();
			
			List<PolicyDrcrDetail> filterDebit=policydrcr.stream().filter(p->"DR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			List<PolicyDrcrDetail> filterCredit=policydrcr.stream().filter(p->"CR".equalsIgnoreCase(p.getDrcrFlag())).collect(Collectors.toList());
			//List<DebitAndCredit> filterDebit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("DR")).collect(Collectors.toList());
			//List<DebitAndCredit> filterCredit = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")).collect(Collectors.toList());

			//String policyNo = policyDetails.get(0).getPolicyNo();
			// Debit
			String debitNo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocNo() :"" ;
			Date debitDate = filterDebit.size() > 0 ? filterDebit.get(0).getEntryDate() : null;
			String debitTo = filterDebit.size() > 0 ?  filterDebit.get(0).getDocType()  : "";
			
			String creditNo ="";
			Date creditDate =null;
			String creditTo = "";
			//BigDecimal commission = new BigDecimal(0);
			//BigDecimal commissionPercent = new BigDecimal(0);
			//BigDecimal commissionVat = new BigDecimal(0);
			if(filterCredit!=null && !filterCredit.isEmpty()) {
			// Credit
			 creditNo =  filterCredit.get(0).getDocNo();
			 creditDate = filterCredit.get(0).getEntryDate();
			 creditTo = filterCredit.get(0).getDocType();
			 
			 
			// Commision
//			 List<DebitAndCredit> commissionList = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") &&
//					 (o.getChargeCode().equals(new BigDecimal(1005)) || o.getChargeCode().equals(new BigDecimal(1001)) )
//						).collect(Collectors.toList())	;
		//	String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1005" ;
			// List<DebitAndCredit> commissionList = policyDetails.stream().filter( o ->(o.getChargeCode().equals(new BigDecimal(1005))||o.getChargeCode().equals(new BigDecimal(1006)) )).collect(Collectors.toList())	;
			/* for ( DebitAndCredit o : commissionList) {
				 commission= commission.add(o.getAmountFc());
				 
			 };
			 // Commission Vat
			 String brokerDrFlag = commission.compareTo(new BigDecimal("0") ) < 0 ? "DR" :"CR"  ;
			List<DebitAndCredit> commissionVatList = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase(brokerDrFlag) &&
			 (o.getChargeCode().equals(new BigDecimal(1009)) )).collect(Collectors.toList())	;
			
			for ( DebitAndCredit o : commissionVatList) {
				commissionVat= commissionVat.add(o.getAmountFc());
				 
			 };*/
//			 commission= policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR") 
//						&& (o.getChargeCode().equals(new BigDecimal(1005)) || o.getChargeCode().equals(new BigDecimal(1001)) )
//					 	).collect(Collectors.toList()).get(0).getAmountFc();
//			 commissionPercent=	policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")
//						&& (o.getChargeCode().equals(new BigDecimal(1007))
//								||
//								o.getChargeCode().equals(new BigDecimal(1012))
//								)
//						).collect(Collectors.toList()).get(0).getAmountFc();
//			 commissionPercent=policyDetails.stream().filter( o ->  (o.getChargeCode().equals(new BigDecimal(1007)))).collect(Collectors.toList()).size() 
//					 >0 ?	policyDetails.stream().filter( o ->  (o.getChargeCode().equals(new BigDecimal(1007)))).collect(Collectors.toList()).get(0).getAmountFc() : new BigDecimal(0);
//			 
			}
			
//			List<DebitAndCredit> filtercommissionVat = policyDetails.stream().filter( o -> o.getDrcrFlag().equalsIgnoreCase("CR")
//					&& o.getChargeCode().equals(new BigDecimal(1012))).collect(Collectors.toList());

//			if (filtercommissionVat.size()>0 ) {
//				commissionVat =  filtercommissionVat.get(0).getAmountFc();
//			}


			// Update Home Posion Master
			data.setDebitNoteNo(debitNo);
			data.setDebitNoteDate(debitDate);
			data.setDebitTo(debitTo);

			data.setCreditNo(creditNo);
			data.setCreditDate(creditDate);	
			data.setCreditTo(creditTo);

//			data.setCommission(commission);
//			data.setCommissionPercentage(commissionPercent);
//			data.setVatCommission(commissionVat);
			data.setPaymentMode(req.getPaymentType());
			data.setPaymentType(paymentDetail.getPaymentTypedesc());
			//data.setPaymentStatus(paymentInfo.getEmiYn().equalsIgnoreCase("N") ? paymentInfo.getPaymentStatus() :"Pending");
			data.setPaymentStatus(paymentInfo.getPaymentStatus());
			data.setPolicyNo(policyNo);
			
			data.setStatus(StringUtils.isNotBlank(data.getEndtTypeId()) && "842".equalsIgnoreCase(data.getEndtTypeId()) ? "D" : "P");
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
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString());
			String msg = updateProductWisePolicyNo(paymentInfo.getProductId().toString() ,policyNo ,req.getQuoteNo(),data.getEndtTypeId() , product.getMotorYn(),hpm.getCommissionPercentage()); 
  
			return policydrcr;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	//Tracking Details
	private QuoteUpdateRes trackingDetailsPayment(HomePositionMaster data,String createdBy) {
		QuoteUpdateRes res=new QuoteUpdateRes();
	try {
		CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId(), data.getProductId().toString());

		List<TrackingDetailsSaveReq> trackingReq1 = new ArrayList<TrackingDetailsSaveReq>();
		if (!data.getStatus().equalsIgnoreCase("D")) {
		if(product.getMotorYn().equalsIgnoreCase("M") ) {
			List<EserviceMotorDetails> cusRefNo = eserMotRepo
					.findByRequestReferenceNoAndProductId(data.getRequestReferenceNo().toString(), data.getProductId().toString());

		
			for(EserviceMotorDetails motor:cusRefNo ) {
				
				if (! motor.getStatus().equalsIgnoreCase("D") ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(data.getProductId().toString());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus("P");
					trackingReq.setBranchCode(data.getBranchCode().toString());
					trackingReq.setQuoteNo(data.getQuoteNo().toString());
					trackingReq.setCompanyId(data.getCompanyId());
					trackingReq.setPolicyNo(data.getPolicyNo()==null?"":data.getPolicyNo().toString());
					trackingReq.setCreatedby(createdBy);
					trackingReq.setRequestReferenceNo(data.getRequestReferenceNo());
					trackingReq1.add(trackingReq);
				}
				
			}
			} else if( product.getMotorYn().equalsIgnoreCase("H")  && data.getProductId().toString().equalsIgnoreCase(travelProductId)) {
			List<EserviceTravelDetails> cusRefNo = eserTraRepo
					.findByRequestReferenceNoAndProductId(data.getRequestReferenceNo().toString(), data.getProductId().toString());

			for (EserviceTravelDetails motor : cusRefNo) {
				if (! motor.getStatus().equalsIgnoreCase("D") ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(data.getProductId().toString());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus("P");
					trackingReq.setBranchCode(data.getBranchCode().toString());
					trackingReq.setQuoteNo(data.getQuoteNo().toString());
					trackingReq.setCompanyId(data.getCompanyId());
					trackingReq.setPolicyNo(data.getPolicyNo().toString());
					trackingReq.setCreatedby(createdBy);
					trackingReq.setRequestReferenceNo(data.getRequestReferenceNo());
					trackingReq1.add(trackingReq);
				}
			}
		} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
			List<EserviceBuildingDetails> cusRefNo = eserBuildingRepo
					.findByRequestReferenceNoAndProductId(data.getRequestReferenceNo().toString(), data.getProductId().toString());
			for (EserviceBuildingDetails motor : cusRefNo) {
				if (! motor.getStatus().equalsIgnoreCase("D") ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(data.getProductId().toString());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus("P");
					trackingReq.setBranchCode(data.getBranchCode().toString());
					trackingReq.setQuoteNo(data.getQuoteNo().toString());
					trackingReq.setCompanyId(data.getCompanyId());
					trackingReq.setPolicyNo(data.getPolicyNo().toString());
					trackingReq.setCreatedby(createdBy);
					trackingReq.setRequestReferenceNo(data.getRequestReferenceNo());
					trackingReq1.add(trackingReq);
				}
			}
		} else {
			List<EserviceCommonDetails> cusRefNo = eserCommonRepo
					.findByRequestReferenceNoAndProductId(data.getRequestReferenceNo().toString(), data.getProductId().toString());


			for (EserviceCommonDetails motor : cusRefNo) {
				if (! motor.getStatus().equalsIgnoreCase("D") ) {
					TrackingDetailsSaveReq trackingReq = new TrackingDetailsSaveReq();
					trackingReq.setProductId(data.getProductId().toString());
					trackingReq.setRiskId(motor.getRiskId().toString());
					trackingReq.setStatus("P");
					trackingReq.setBranchCode(data.getBranchCode().toString());
					trackingReq.setQuoteNo(data.getQuoteNo().toString());
					trackingReq.setCompanyId(data.getCompanyId());
					trackingReq.setPolicyNo(data.getPolicyNo().toString());
					trackingReq.setCreatedby(createdBy);
					trackingReq.setRequestReferenceNo(data.getRequestReferenceNo());
					trackingReq1.add(trackingReq);
				}
			}
		}			
		
		trackingService.insertTrackingDetails(trackingReq1);
		}
	} catch ( Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
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
	
	 public  String updateProductWisePolicyNo(String productId , String policyNo , String quoteNo,String endttypeId, String motorYn ,BigDecimal commissionPercent ) {
		 String res = "" ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper();
		 try {
			 
	    	   if(motorYn.equalsIgnoreCase("M") ) {
	    		   
	    		   // Update Main Motor
	    		   List<MotorDataDetails> motorList =  motorRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   motorList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				 //  o.setCommissionPercentage(commissionPercent);
	    				   
	    			   });
					} else {
					   motorList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							  // o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
			    			  
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			  // o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   motorRepo.saveAllAndFlush(motorList);
	    		  
	    		// Update Eservice Motor
	    		  List<EserviceMotorDetails> eserMotorsList =  eserMotRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		  List<EserviceMotorDetails> updateEserList = new ArrayList<EserviceMotorDetails>(); 
	    		  eserMotorsList.forEach( o -> {
	    			  
    			  List<MotorDataDetails> filterMotor = motorList.stream().filter( e -> e.getVehicleId().equals(o.getRiskId().toString())
    					  && e.getSectionId().equals(Integer.valueOf(o.getSectionId())) ).collect(Collectors.toList());
    			  
    			  if( filterMotor.size()> 0 ) {
    				  EserviceMotorDetails updateEser = o ; 
    				  dozerMapper.map(filterMotor.get(0) , updateEser);
    				  updateEserList.add(updateEser);
    			   }
	    		 
	    		  });	    		   
	    		  eserMotRepo.saveAllAndFlush(updateEserList);
	    		  
	    	   } else  if(motorYn.equalsIgnoreCase("H")  && productId.equalsIgnoreCase(travelProductId) ) {
	    		   
	    		   // Update Main Travel
	    		   List<TravelPassengerDetails> passengerList =  passengerRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   passengerList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				//   o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						passengerList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							 //  o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			 //  o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   passengerRepo.saveAllAndFlush(passengerList);
	    		  
	    		// Update Eservice Travel
	    		  EserviceTravelDetails eserTravel =  eserTraRepo.findByQuoteNo(quoteNo);
	    		  eserTravel.setPolicyNo(policyNo);
	    		  eserTravel.setStatus(StringUtils.isNotBlank(endttypeId) && "842".equalsIgnoreCase(endttypeId) ? "D" : "P");
	    		  eserTravel.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
	    		//  eserTravel.setCommissionPercentage(commissionPercent);
	    		  eserTraRepo.saveAndFlush(eserTravel);
	    		  
	    	   } else  if(motorYn.equalsIgnoreCase("A") ) {
	    		   
	    		// Update Main Asset
	    		   List<BuildingRiskDetails> buildingList =  buildingRiskRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   buildingList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    			   });
					} else {
						buildingList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							//   o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
			    			   
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			 //  o.setCommissionPercentage(commissionPercent);
			    		    
		    		   });
					}
	    		   buildingRiskRepo.saveAllAndFlush(buildingList);
	    		  
	    		// Update Eservice Asset
	    		   List<EserviceBuildingDetails> updateEserList = new ArrayList<EserviceBuildingDetails>();
	    		   List<EserviceBuildingDetails> eserBuildingList =  eserBuildingRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
		    	   eserBuildingList.forEach( o -> {
		    			  
	    			  List<BuildingRiskDetails> filterAsset = buildingList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId())&& e.getLocationId().equals(o.getLocationId()) ).collect(Collectors.toList());
	    			  
	    			  if( filterAsset.size()> 0 ) {
	    				  EserviceBuildingDetails updateEser = o; 
	    				  dozerMapper.map(filterAsset.get(0) , updateEser);
	    				  updateEserList.add(updateEser);
	    			   }
		    		 
		    		  });	    		   
	    		   eserBuildingRepo.saveAllAndFlush(updateEserList);
		    		  
	    		
	    		 
	    		// Update Main Human
	    		   List<CommonDataDetails> humanList =  commonRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   humanList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				//   o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						humanList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
							 //  o.setOriginalPolicyNo(StringUtils.isNotBlank(endttypeId) ? o.getOriginalPolicyNo() : policyNo );
			    			   o.setStatus("P");
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			//   o.setCommissionPercentage(commissionPercent);
		    			    
		    		   });
					}
	    		   commonRepo.saveAllAndFlush(humanList);
	    		  
	    		   
	    		// Update Eservice Asset
	    		   List<EserviceCommonDetails> updateHumanEserList = new ArrayList<EserviceCommonDetails>();
	    		   List<EserviceCommonDetails> eserHumanList =  eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		   eserHumanList.forEach( o -> {
		    			  
	    			  List<CommonDataDetails> filterHuman = humanList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId())&& e.getLocationId().equals(o.getLocationId()) ).collect(Collectors.toList());
	    			  
	    			  if( filterHuman.size()> 0 ) {
	    				  EserviceCommonDetails updateEser = o ; 
	    				  dozerMapper.map(filterHuman.get(0) , updateEser);
	    				  updateHumanEserList.add(updateEser);
	    			   }
		    		 
		    		  });	    		   
	    		   eserCommonRepo.saveAllAndFlush(updateHumanEserList);
		    		  
	    		
	    	
	    		  
	    	   } else {
	    		// Update Main Human
	    		   List<CommonDataDetails> humanList =  commonRepo.findByQuoteNo(quoteNo);
	    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
	    			   humanList.forEach( o -> {
	    				   o.setPolicyNo(policyNo);
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    				  // o.setCommissionPercentage(commissionPercent);
	    			   });
					} else {
						humanList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(policyNo);
			    			   o.setStatus("P");
						   }   
			    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
			    			//   o.setCommissionPercentage(commissionPercent);
			    		   
		    			    
		    		   });
					}
	    		   commonRepo.saveAllAndFlush(humanList);
	    		  
	    		// Update Eservice Asset
	    		   List<EserviceCommonDetails> updateHumanEserList = new ArrayList<EserviceCommonDetails>();
	    		   List<EserviceCommonDetails> eserHumanList =  eserCommonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    		   eserHumanList.forEach( o -> {
		    			  
	    			  List<CommonDataDetails> filterHuman = humanList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId()) && e.getLocationId().equals(o.getLocationId())).collect(Collectors.toList());
	    			  
	    			  if( filterHuman.size()> 0 ) {
	    				  EserviceCommonDetails updateEser = o; 
	    				  dozerMapper.map(filterHuman.get(0) , updateEser);
	    				  updateHumanEserList.add(updateEser);
	    			   }
		    		 
		    		  });	    		   
	    		   eserCommonRepo.saveAllAndFlush(updateHumanEserList);
	    	   }
	    	   
	    	   // Policy Cover Data
	    	   {
	    		   CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<PolicyCoverData> update = cb.createCriteriaUpdate(PolicyCoverData.class);
					// set the root class
					Root<PolicyCoverData> m = update.from(PolicyCoverData.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					// Cancellation Condition
					if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
						update.where(n1);
					} else {
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
					}
					// perform update
					em.createQuery(update).executeUpdate();
	    	   }
	    	   
	    	   // Policy Cover Data Induviduals
	    	   {
	    		   CriteriaBuilder cb = em.getCriteriaBuilder();
					// create update
					CriteriaUpdate<PolicyCoverDataIndividuals> update = cb.createCriteriaUpdate(PolicyCoverDataIndividuals.class);
					// set the root class
					Root<PolicyCoverDataIndividuals> m = update.from(PolicyCoverDataIndividuals.class);
					// set update and where clause
					update.set("policyNo", policyNo);
					
					Predicate n1 = cb.equal(m.get("quoteNo"),quoteNo );
					// Cancellation Condition
					if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
						update.where(n1);
					} else {
						Predicate n2 = cb.notEqual(m.get("status"),"D" );
						update.where(n1,n2);
					}
					// perform update
					em.createQuery(update).executeUpdate();  
	    	   }
	    	   
	    	   // Section Update
	    	   List<SectionDataDetails> secList =  sddRepo.findByQuoteNo(quoteNo);
    		   if(StringUtils.isNotBlank(endttypeId) && endttypeId.equalsIgnoreCase("842")) {
    			   secList.forEach( o -> {
    				   o.setPolicyNo(policyNo);
    				   o.setStatus("D");
    				   o.setEndtStatus("C");
    			   });
				} else {
					secList.forEach( o -> {
					   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
						   o.setPolicyNo(policyNo);
		    			   o.setStatus("P");
					   }
		    			   o.setEndtStatus(StringUtils.isNotBlank(endttypeId) ? "C" : "");
		    		    
	    		   });
				}
    		   sddRepo.saveAllAndFlush(secList);
    		  
    		// Update Eservice Asset
    		   List<EserviceSectionDetails> updateEserList = new ArrayList<EserviceSectionDetails>();
    		   List<EserviceSectionDetails> eserBuildingList =  sectionRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	    	   eserBuildingList.forEach( o -> {
	    			  
    			  List<SectionDataDetails> filterAsset = secList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
    					  && e.getSectionId().equals(o.getSectionId()) && e.getLocationId().equals(o.getLocationId())).collect(Collectors.toList());
    			  
    			  if( filterAsset.size()> 0 ) {
    				  EserviceSectionDetails updateEser = o; 
    				  dozerMapper.map(filterAsset.get(0) , updateEser);
    				  updateEserList.add(updateEser);
    			   }
	    		 
	    		  });	    		   
	    	   sectionRepo.saveAllAndFlush(updateEserList);
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
			tinyURL = "";// getShorternURL(encryptedURL);
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<TinyurlMaster> ocpm1 = effectiveDate.from(TinyurlMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("sno"),ocpm1.get("sno"));
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a11 = cb.equal(ocpm1.get("type") ,c.get("type"));
			effectiveDate.where(a1,a2,a3,a4,a5,a11);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<TinyurlMaster> ocpm2 = effectiveDate2.from(TinyurlMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
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
			url =list.size() > 0 ?  list.get(0).getAppUrl() : "";
			
			
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

	@Autowired
	private LoginMasterRepository loginmasterRepo;
	
	 public QuoteUpdateRes notificationTrigger(Integer productId,String quoteNo,String paymentStatus) {
			QuoteUpdateRes updateRes = new QuoteUpdateRes();
			try {
				HomePositionMaster data = homerepo.findByQuoteNo(quoteNo);
				CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , data.getProductId().toString());

				if(product.getMotorYn().equalsIgnoreCase("M") ) {
					//Mail Push Notification
					updateRes= motorPushNotification(productId,quoteNo,paymentStatus);
					
				} else if(product.getMotorYn().equalsIgnoreCase("H")  && productId.equals(travelProductId)) {
			
					//Mail Push Notification
					//updateRes= travelPushNotification(req);
					
				} else if(product.getMotorYn().equalsIgnoreCase("A") ) {
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
						.findByQuoteNoOrderByRiskIdAsc(quoteNo);

				cusRefNo = cusRefNo.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
						.collect(Collectors.toList());
				String loginId = "";
				if (cusRefNo!=null && cusRefNo.size()>0 &&  cusRefNo.get(0).getApplicationId().equalsIgnoreCase("1")) {
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
				/*List<Tuple> underWriterList=getUnderWriterDetails(cusRefNo.get(0).getProductId(),cusRefNo.get(0).getCompanyId(),cusRefNo.get(0).getBranchCode(),cusRefNo.get(0).getLoginId());
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
				n.setUnderwriters(underWrite);*/
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
				LoginMaster logid = loginmasterRepo.findByCompanyIdAndLoginId(cusRefNo.get(0).getCompanyId(), loginId);
				if(logid.getSubUserType().equalsIgnoreCase("b2c")) {
					n.setNotifTemplatename("RISK MESSAGE");
				}else
					n.setNotifTemplatename("POLICY MESSAGE");
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
		
		
		public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
			CompanyProductMaster product = new CompanyProductMaster();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				;
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 1);
				Date todayEnd = cal.getTime();

				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
				List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
				// Find All
				Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
				// Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("productName")));

				// Effective Date Start Max Filter
				Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
				Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
				Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
				Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1, a2, a3);
				// Effective Date End Max Filter
				Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
				Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
				Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
				Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
				Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a4, a5, a6);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), companyId);
				Predicate n5 = cb.equal(c.get("productId"), productId);
				query.where(n1, n2, n3, n4, n5).orderBy(orderList);
				// Get Result
				TypedQuery<CompanyProductMaster> result = em.createQuery(query);
				list = result.getResultList();
				product = list.size() > 0 ? list.get(0) :null;
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return product;
		}


		@Override
		public CommonRes getCreditLimit(String brokerId) {
			log.info("Enter in getCreditLimit\nArgument ==> "+brokerId);
			CommonRes res = new CommonRes();
			Map<String,Object> map = new HashMap<>();
			List<Error> error = new ArrayList<>();
			String result=null;
			try {
				String ApiURL= paymentdetailrepo.getCreditLimitApiURL();
				if(StringUtils.isNotBlank(ApiURL)) {
					JSONObject json = new JSONObject();
					JSONObject json1 = new JSONObject();
					JSONParser parser = new JSONParser();
					CloseableHttpClient httpClient = HttpClients.createDefault();
					HttpGet httpPost = new HttpGet(ApiURL+brokerId);
					httpPost.setHeader("content-type", "application/json; charset=utf8");
					httpPost.setHeader("Authorization", "Basic "+"");
					CloseableHttpResponse response = httpClient.execute(httpPost);
					if(response.getStatusLine().getStatusCode()<=400) {
						BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
						StringBuffer responseAsString = new StringBuffer();
						String line = "";
						while((line = rd.readLine()) != null) {
							responseAsString.append(line);
						}
						if(StringUtils.isNotBlank(responseAsString))
							result = responseAsString.toString();
						if(StringUtils.isNotBlank(result)) {
							json = (JSONObject) parser.parse(result);
							if("SUCCESS".equals(json.get("Message"))) {
								json1 = (JSONObject) json.get("Response");
								map.put("CustomerName", json1.get("CustomerName"));
								map.put("CreditLimit", json1.get("CreditLimit"));
								map.put("UpdateDate", json1.get("UpdateDate"));
								res.setCommonResponse(map);
								res.setMessage("SUCCESS");
								res.setErrorMessage(error);
							}else {
								error.add(new Error("No Data Found","BrokerId","500"));
								res.setCommonResponse(null);
								res.setMessage("FAILED");
								res.setErrorMessage(error);
							}
						}
					}
				}
				log.info("Exit into getCreditLimit");
			}catch(Exception e) {
				log.info("Error in getCreditLimit ==> "+e.getMessage());
				e.printStackTrace();
			}
			return res;
		}
		
		
}
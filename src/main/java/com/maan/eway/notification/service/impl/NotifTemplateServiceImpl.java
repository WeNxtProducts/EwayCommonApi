package com.maan.eway.notification.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.service.PaymentService;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.service.ProductGroupMasterService;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.JobCredentials;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.Messenger;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.NotificationFrameReq;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.res.MailTemplateRes;
import com.maan.eway.notification.res.SmsTemplateRes;
import com.maan.eway.notification.service.NotifTemplateService;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.notification.service.NotificationValidation;
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
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
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
import com.maan.eway.res.DropDownRes;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

@Service
@Transactional
public class NotifTemplateServiceImpl implements  NotifTemplateService {
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(NotifTemplateServiceImpl.class);

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	
	@Autowired
	protected CriteriaService crservice;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Autowired
	private NotificationService notiService;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository eserviceTravelRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eserviceBuildingRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerDetailsRepo;
	
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private NotificationValidation vad;
	
	
	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Override
	public List<DropDownRes> getTemplatesDropDown(TemplatesDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
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
			CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
			List<NotifTemplateMaster> list = new ArrayList<NotifTemplateMaster>();

			// Find All
			Root<NotifTemplateMaster> c = query.from(NotifTemplateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("notifTemplatename")));
    
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			effectiveDate.where(a1, a2, a3, a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a9 = cb.greaterThanOrEqualTo(c.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6, a7, a8 ,a9 );

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = null ;
			if (req.getNotifApplicable().equalsIgnoreCase("Mail")  ) {
				n6 = cb.equal(c.get("mailRequired"), "Y");
				
			} else if(req.getNotifApplicable().equalsIgnoreCase("Sms")  ) {
				n6 = cb.equal(c.get("smsRequired"), "Y");
				
			}
			query.where(n1, n2, n3,n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (NotifTemplateMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getNotifTemplateCode().toString());
				res.setCodeDesc(data.getNotifTemplatename());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}
	
	public NotifTemplateMaster getTemplateDetails(NotifTemplateGetReq req) {
		NotifTemplateMaster data = new NotifTemplateMaster();
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
			CriteriaQuery<NotifTemplateMaster> query = cb.createQuery(NotifTemplateMaster.class);
			

			// Find All
			Root<NotifTemplateMaster> c = query.from(NotifTemplateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("notifTemplatename")));
    
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			effectiveDate.where(a1, a2, a3, a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a9 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), todayEnd);
			effectiveDate2.where(a6, a7, a8 ,a9);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = cb.equal(c.get("notifTemplateCode"), req.getNotifTemplateCode() );
			
			query.where(n1, n2, n3,n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
			data = result.getResultList().get(0) ;
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return data;
	}

	@Override
	public CommonRes getMailTemplate(NotifTemplateGetReq req) {
		CommonRes res = new CommonRes();
		MailTemplateRes mailTemplateRes = new MailTemplateRes();
		try {
			NotifTemplateMaster template = getTemplateDetails(req) ;
			
			NotificationFrameReq nf = new NotificationFrameReq();
			nf.setInsuranceId(req.getInsuranceId());
			nf.setNotifTemplateCode(template.getNotifTemplateCode().toString());
			nf.setNotifTemplateName(template.getNotifTemplatename());
			nf.setProductId(req.getProductId() );
			nf.setRequestReferenceNo(req.getRequestReferenceNo());;
			nf.setRemarks(req.getRemarks());
			nf.setCreatedBy(req.getCreatedBy());
			
			if( req.getProductId().equalsIgnoreCase(motorProductId)) {
				res =	motorPushNotification(nf);

			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				res =  travelPushNotification(nf);
				
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId) || req.getProductId().equalsIgnoreCase(smeProductId)) {
				res = buildingPushNotification(nf);
			}  else {
			
				res = commonPushNotification(nf);
			} 
			if(res.getIsError()!=null && res.getIsError() == true ) {
				return res ;
			}
			
			
			NotifTransactionDetails ne = (NotifTransactionDetails) res.getCommonResponse() ;
		    Tuple t =  loadNotificationPending(ne.getNotifNo()).get(0);
			
			String mailBody=(String) getContentFrame(t, template.getMailBody());
		    String mailSubject=(String) getContentFrame(t, template.getMailSubject());
			String mailRegards=(String) getContentFrame(t, template.getMailRegards());
			
			mailTemplateRes.setMailBody(mailBody);
			mailTemplateRes.setMailSubject(mailSubject);
			mailTemplateRes.setMailRegards(mailRegards);
			res.setCommonResponse(mailTemplateRes);
			res.setIsError(false);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			
			return res;
		}
		return res;
	}
	

	

	@Override
	public CommonRes getSmsTemplate(NotifTemplateGetReq req) {
		CommonRes res = new CommonRes();
		SmsTemplateRes smsTemplateRes = new SmsTemplateRes();
		try {
			NotifTemplateMaster template = getTemplateDetails(req) ;
			
			NotificationFrameReq nf = new NotificationFrameReq();
			nf.setInsuranceId(req.getInsuranceId());
			nf.setNotifTemplateCode(template.getNotifTemplateCode().toString());
			nf.setNotifTemplateName(template.getNotifTemplatename());
			nf.setProductId(req.getProductId() );
			nf.setRequestReferenceNo(req.getRequestReferenceNo());
			nf.setRemarks(req.getRemarks());
			
			if( req.getProductId().equalsIgnoreCase(motorProductId)) {
				res = 	motorPushNotification(nf);

			} else if( req.getProductId().equalsIgnoreCase(travelProductId)) {
				res = travelPushNotification(nf);
				
			} else if( req.getProductId().equalsIgnoreCase(buildingProductId) || req.getProductId().equalsIgnoreCase(smeProductId)) {
				res = buildingPushNotification(nf);
			}  else {
			
				res = commonPushNotification(nf);
			} 
			NotifTransactionDetails ne = (NotifTransactionDetails) res.getCommonResponse() ;
			Tuple t =  loadNotificationPending(ne.getNotifNo()).get(0);
			String smsBody=(String) getContentFrame(t, template.getSmsBodyEn());
		    String smsSubject=(String) getContentFrame(t, template.getSmsSubject());
			String smsRegards=(String) getContentFrame(t, template.getSmsRegards());
			
			smsTemplateRes.setSmsBody(smsBody);
			smsTemplateRes.setSmsSubject(smsSubject);
			smsTemplateRes.setSmsRegards(smsRegards);
			res.setCommonResponse(smsTemplateRes);
			res.setIsError(false);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			List<Error> errors = new ArrayList<Error>();
			errors.add(new Error("01" ,"Common Error" ,e.getMessage() ));
			res.setCommonResponse(null);
			res.setIsError(false);
			res.setErrorMessage(errors);
			return res ;
		}
		return res;
	}
	
	public List<Tuple> loadNotificationPending(Integer notifNo) {
		 List<Tuple> list = new ArrayList<Tuple>();
		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			

			// Find All
			Root<NotifTransactionDetails> c = query.from(NotifTransactionDetails.class);

			// Select
			//query.select(c.get("").alias(""));
//			 private Integer    notifNo ;
//
//			    //--- ENTITY DATA FIELDS 
//			    @Column(name="Customer_Name", length=20)
//			    private String     customerName ;
//
//			    @Column(name="Customer_Mail_Id", length=20)
//			    private String     customerMailid ;
//
//			    @Column(name="Customer_Phone_No")
//			    private BigDecimal customerPhoneNo ;
//
//			    @Column(name="Customer_Phone_Code")
//			    private Integer    customerPhoneCode ;
//
//			    @Column(name="Customer_Messenger_Code")
//			    private Integer    customerMessengerCode ;
//
//			    @Column(name="Customer_Messenger_Phone")
//			    private BigDecimal customerMessengerPhone ;
//
//			    @Column(name="Broker_Name", length=20)
//			    private String     brokerName ;
//
//			    @Column(name="Broker_Company_Name", length=20)
//			    private String     brokerCompanyName ;
//
//			    @Column(name="Broker_Mail_Id", length=20)
//			    private String     brokerMailId ;
//
//			    @Column(name="Broker_Phone_No")
//			    private BigDecimal brokerPhoneNo ;
//
//			    @Column(name="Broker_Phone_Code")
//			    private Integer    brokerPhoneCode ;
//
//			    @Column(name="Broker_Messenger_Code")
//			    private Integer    brokerMessengerCode ;
//
//			    @Column(name="Broker_Messenger_Phone")
//			    private BigDecimal brokerMessengerPhone ;
//
//			    @Column(name="UW_name", length=20)
//			    private String     uwName ;
//
//			    @Column(name="uw_mail_id", length=20)
//			    private String     uwMailid ;
//
//			    @Column(name="UW_Phone_Code")
//			    private Integer    uwPhonecode ;
//
//			    @Column(name="UW_Phone_No")
//			    private BigDecimal uwPhoneNo ;
//
//			    @Column(name="UW_messenger_code")
//			    private Integer    uwMessengerCode ;
//
//			    @Column(name="UW_messenger_phone")
//			    private BigDecimal uwMessengerPhone ;
//
//			    @Column(name="Company_Name", length=20)
//			    private String     companyName ;
//
//			    @Column(name="Product_Name", length=20)
//			    private String     productName ;
//
//			    @Column(name="Section_Name", length=20)
//			    private String     sectionName ;
//
//			    @Column(name="Status_message", length=20)
//			    private String     statusMessage ;
//
//			    @Column(name="OTP")
//			    private Integer    otp ;
//
//			    @Column(name="Policy_No")
//			    private String policyNo ;
//
//			    @Column(name="Quote_No")
//			    private String quoteNo ;
//
//			    @Column(name="Notif_Description", length=100)
//			    private String     notifDescription ;
//
//			    @Column(name="notif_template_name", length=20)
//			    private String     notifTemplatename ;
//
//			    @Temporal(TemporalType.TIMESTAMP)
//			    @Column(name="Entry_Date")
//			    private Date       entryDate ;
//
//			    @Temporal(TemporalType.TIMESTAMP)
//			    @Column(name="Notifcation_Push_date")
//			    private Date       notifcationPushDate ;
//			    
//			    @Temporal(TemporalType.TIMESTAMP)
//			    @Column(name="Notifcation_End_date")
//			    private Date       notifcationEndDate ;
//
//			    @Column(name="Notif_pushed_status", length=20)
//			    private String     notifPushedStatus ;
//
//			    @Column(name="Notif_Priority", nullable=false)
//			    private Integer    notifPriority ;
//
//			    @Column(name="Tiny_URL", length=15)
//			    private String     tinyUrl ;
//
//			    @Column(name="company_id", nullable=false, length=15)
//			    private String     companyid ;
//
//			    @Column(name="product_id", nullable=false)
//			    private Integer    productid ;
//
//			    @Column(name="company_Address")
//			    private String companyAddress;
//			    @Column(name="company_Logo")
//			    private String companyLogo;
//			    //--- ENTITY LINKS ( RELATIONSHIP )
//			    @Column(name="attach_file_path")
//			    private String attachFilePath;
//			    
//			    @Column(name="PUSHED_BY")
//			    private String pushedBy;
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("notifTemplatename")));
    
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm1 = effectiveDate.from(NotifTemplateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("notifTemplateCode"), ocpm1.get("notifTemplateCode"));
		//	Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			effectiveDate.where(a1);// a2, a3, a4);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<NotifTemplateMaster> ocpm2 = effectiveDate2.from(NotifTemplateMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("notifTemplateCode"), ocpm2.get("notifTemplateCode"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			//Predicate a9 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), todayEnd);
		//	effectiveDate2.where(a6, a7, a8 ,a9);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		//	Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
		//	Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		//	Predicate n6 = cb.equal(c.get("notifTemplateCode"), req.getNotifTemplateCode() );
			
		//	query.where(n1, n2, n3,n4, n5, n6).orderBy(orderList);

			// Get Result
		///	TypedQuery<NotifTemplateMaster> result = em.createQuery(query);
		//	data = result.getResultList().get(0) ;
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return null;
		
	}
	
	// --------------------------------------MOTOR UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private CommonRes motorPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
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
	
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
		
			//Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductName());
			n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.setPushedBy(req.getCreatedBy());
			n.getTinyUrl();

			// Calling pushNotification
			res= pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	
	}
	

	// --------------------------------------TRAVEL UPDATE REFERRAL STATUS----------------------------------------------------------------------//
	private CommonRes travelPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
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
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			// Common Info
			
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductName());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionName());
			n.getTinyUrl();
			
			n.setPushedBy(req.getCreatedBy());
			// Calling pushNotification
			res = pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	//----------------------------------------BUILDING UPDATE REFERRAL STATUS ------------------------------------------------------------------//
	private CommonRes buildingPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
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
					underWrite.add(underWriterReq);
				}
			}
			n.setUnderwriters(underWrite);
			// Company Info
			n.setCompanyid(cusRefNo.get(0).getCompanyId());
			n.setCompanyName(cusRefNo.get(0).getCompanyName());

			// Common Info
			n.setBroker(brokerReq);
			n.setCustomer(cusReq);
			n.setNotifcationDate(new Date());
			n.setNotifDescription("");
			n.setNotifPriority(0);
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductDesc());
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionDesc());
			n.setPushedBy(req.getCreatedBy());
			n.getTinyUrl();

			// Calling pushNotification
			res = pushNotification(n);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	// -------------------------------------- COMMON UPDATE REFERRAL STATUS----------------------------------------------------------------------//	
	private CommonRes commonPushNotification(NotificationFrameReq req) {
		CommonRes res = new CommonRes();
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
			n.setNotifPushedStatus(NotificationStatus.COMPLETED);
			n.setNotifTemplatename(req.getNotifTemplateName());
			n.setStatusMessage(req.getRemarks());
			n.setPolicyNo(cusRefNo.get(0).getPolicyNo());
			n.setProductid(Integer.valueOf(req.getProductId()));
			n.setProductName(cusRefNo.get(0).getProductDesc());
			n.setQuoteNo(StringUtils.isBlank(cusRefNo.get(0).getQuoteNo().toString())?cusRefNo.get(0).getRequestReferenceNo():cusRefNo.get(0).getQuoteNo().toString());
			n.setSectionName(cusRefNo.get(0).getSectionDesc());
			n.setPushedBy(req.getCreatedBy());
			// Referral Noti , referral app,recj
			n.getTinyUrl();

			// Calling pushNotification
			res= pushNotification(n);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
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

	public CommonRes pushNotification(Notification n) {
		
		
		List<Error> validation = vad.pushValidation(n);
		CommonRes c=new CommonRes();
		
		if(validation.isEmpty()) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(n.getNotifcationDate()); 
			calend.add(Calendar.DATE, 1); 
			List<InsuranceCompanyMaster> coms = companyRepo.findByCompanyIdOrderByAmendIdDesc(n.getCompanyid());
			String filesTobeAttch=null;
			if(n.getAttachments()!=null && n.getAttachments().size()>0) {
				filesTobeAttch = n.getAttachments().stream().collect(Collectors.joining(";"));
			}
				
			
					
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(n.getBroker().getBrokerCompanyName())
					.brokerMailId(n.getBroker().getBrokerMailId())
					.brokerMessengerCode(n.getBroker().getBrokerMessengerCode())
					.brokerMessengerPhone(n.getBroker().getBrokerMessengerPhone())
					.brokerPhoneCode(n.getBroker().getBrokerPhoneCode())
					.brokerPhoneNo(n.getBroker().getBrokerPhoneNo())
					.brokerName(n.getBroker().getBrokerName())
					.companyName(n.getCompanyName())
					.customerMailid(n.getCustomer().getCustomerMailid())
					.customerPhoneCode(n.getCustomer().getCustomerPhoneCode())
					.customerPhoneNo(n.getCustomer().getCustomerPhoneNo())
					.customerMessengerCode(n.getCustomer().getCustomerMessengerCode())
					.customerMessengerPhone(n.getCustomer().getCustomerMessengerPhone())
					.customerName(n.getCustomer().getCustomerName())
					.entryDate(new Date())
					.notifcationPushDate(n.getNotifcationDate())
					.notifcationEndDate(calend.getTime())
					.notifDescription(n.getNotifDescription())
					//.notifNo(null)
					.notifPriority(n.getNotifPriority())
					.notifPushedStatus("C")
					.notifTemplatename(n.getNotifTemplatename())
					.otp(n.getOtp())
					.policyNo(n.getPolicyNo())
					.quoteNo(n.getQuoteNo()) 
					.uwMailid((n.getUnderwriters().size()>5)?n.getUnderwriters().subList(0, 5).stream().map(a -> a.getUwMailid()).collect(Collectors.joining(",")):
						n.getUnderwriters().stream().map(a -> a.getUwMailid()).collect(Collectors.joining(",")))
					.uwMessengerCode(n.getUnderwriters().get(0).getUwMessengerCode())
					.uwMessengerPhone(n.getUnderwriters().get(0).getUwMessengerPhone())
					.uwName(n.getUnderwriters().get(0).getUwName())
					.uwPhonecode(n.getUnderwriters().get(0).getUwPhonecode())
					.uwPhoneNo(n.getUnderwriters().get(0).getUwPhoneNo())
					.productName(n.getProductName())
					.sectionName(n.getSectionName())
					.statusMessage(n.getStatusMessage())
					.tinyUrl(n.getTinyUrl())
					.notifPushedStatus(n.getNotifPushedStatus().toString())
					.companyid(n.getCompanyid())
					.productid(n.getProductid())
					.companyLogo(coms.get(0).getCompanyLogo())
					.companyAddress(coms.get(0).getCompanyAddress())
					.attachFilePath(filesTobeAttch)
					.pushedBy(n.getPushedBy())
			.build();	
			NotifTransactionDetails sv = notifTrans.save(nt);
			c.setIsError(Boolean.FALSE);
			c.setErroCode(100);
			c.setIsError(null);
			c.setMessage("Pushed Successfuly");
			c.setCommonResponse(sv);
			
			//jobScheduler.enqueue(()->jobProcess(n,nt));
			
			
		}else {
			c.setErroCode(101);
			c.setErrorMessage(validation);
			c.setIsError(Boolean.TRUE);
			c.setMessage("Have Validation");
		}
		
		return  c;
		
		
 	}
	
	private Object getContentFrame(Tuple t ,String messageTemplate ) {
		try {
			 
		  
			StringBuffer b=new StringBuffer(messageTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(t.get(tx)));
			} 
			return b.toString();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	
	private String getTemplateFrame(Tuple t ,NotifTemplateMaster m) {
		try {
			 String baseTemplate="<div style=\"margin: 0px auto;width: 700px;max-width: 90%;padding-top: 20px;background-color: rgb(255,255,255);\">\r\n"
			 		+ "        <div style=\"text-align: center; margin-bottom: 20px;\"> <img height=\"20px\"> </div>\r\n"
			 		+ "<div class=\"adM\" style=\"text-align: center;\"><img src=\"{xCompanyLogox}\">\r\n"
			 		+ "        \r\n"
			 		+ "        </div>"
			 		+ "        <div style=\"margin: 0px auto; width: 100%; line-height: 1.3;\"> <img width=\"100%\">\r\n"
			 		+ "          <div style=\"padding: 20px 30px;\">\r\n"
			 		+ "            <p style=\"text-transform: capitalize;\">Hi {xCustomerx},</p>\r\n"
			 		+ "            <p style=\"\r\n"
			 		+ "    font-size: 17px;\r\n"
			 		+ "\">{xsubjectx}</p>\r\n"
			 		+ "            <div style=\"border: 1px solid rgb(221, 221, 221); padding: 20px;\">\r\n"
			 		+ "              {xmailBodyx}"
			 		+ "                \r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">Why {companyName}?</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text -1.</li>\r\n"
			 		+ "                <li>Sample Text -2.</li>\r\n"
			 		+ "                <li>Sample Text -3.</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-weight: bold;\">What’s next?</p>\r\n"
			 		+ "              <p>Paragraph Text</p>\r\n"
			 		+ "              <ul>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "                <li>Sample Text</li>\r\n"
			 		+ "              </ul>\r\n"
			 		+ "              <p style=\"font-size: 1.3em; text-align: center; font-weight: bold;\">That's all, it’s that simple.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "             \r\n"
			 		+ "             \r\n"
			 		+ "            <p style=\"margin-top: 30px;\">We care,</p>\r\n"
			 		+ "            <p>{xregardsx} Team</p>\r\n"
			 		+ "            <div style=\"font-size: 0.8em; color: rgba(0, 0, 0, 0.4); margin-top: 30px;\">\r\n"
			 		+ "              <p>Your premium may need to be adjusted if the provided\r\n"
			 		+ "                information is incorrect.</p>\r\n"
			 		+ "            </div>\r\n"
			 		+ "            <div style=\"color: rgba(0, 0, 0, 0.4); font-size: 0.8em; border-top: 1px solid; margin-top: 30px; line-height: 0.9em; text-align: center; padding: 30px 0px;\">\r\n"
			 		+ "              <p><a href='#'/>\r\n"
			 		+ "               {xCompanyAddressx}</p>\r\n"
			 		+ "              <div>\r\n"
			 		+ "                <p style=\"font-weight: bold; color: rgb(0, 0, 0); margin-top: 20px;\">Connect with us</p>\r\n"
			 		+ "                </div>\r\n"
			 		+ "            </div>\r\n"
			 		+ "          </div>\r\n"
			 		+ "        </div>\r\n"
			 		+ "      </div>";
		  
			     String mailBody=(String) getContentFrame(t, m.getMailBody());
			     String mailSubject=(String) getContentFrame(t, m.getMailSubject());
				String mailRegards=(String) getContentFrame(t, m.getMailRegards());
				String xCustomerx="Team";
				if("customerMailid".equals(m.getToEmail())) {
					xCustomerx=(t.get("customerName")==null || StringUtils.isBlank(t.get("customerName").toString()))?"Team":t.get("customerName").toString();
				}else if("brokerMailId".equals(m.getToEmail())) {
					xCustomerx=(t.get("brokerName")==null || StringUtils.isBlank(t.get("brokerName").toString()))?"Team":t.get("brokerName").toString();
				}else if("uwMailid".equals(m.getToEmail())) {
					xCustomerx=(t.get("uwName")==null || StringUtils.isBlank(t.get("uwName").toString()))?"Team":t.get("uwName").toString();
				}
				
				Map<String,String> hmap=new HashMap<String,String>();
				hmap.put("xregardsx", mailRegards);
				hmap.put("xmailBodyx", mailBody);
				hmap.put("xCustomerx", xCustomerx);
				hmap.put("xsubjectx", mailSubject);
				hmap.put("xCompanyLogox", String.valueOf(t.get("companyLogo")));
				hmap.put("xCompanyAddressx", String.valueOf(t.get("companyAddress")));
				
			StringBuffer b=new StringBuffer(baseTemplate);
			while (b.indexOf("{")!=-1 && b.indexOf("}")!=-1) {
				 String tx = b.substring(b.indexOf("{")+1, b.indexOf("}"));
				 b.replace(b.indexOf("{"), b.indexOf("}")+1, String.valueOf(hmap.get(tx)==null?t.get(tx):hmap.get(tx)));
			} 
			return b.toString(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
}

package com.maan.eway.notification.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
import com.maan.eway.bean.MailMaster;
import com.maan.eway.bean.NotifTemplateMaster;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.bean.SmsDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.bean.MailDataDetails;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.MailDataDetailsRepository;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.req.Broker;
import com.maan.eway.notification.req.Customer;
import com.maan.eway.notification.req.DirectMailSentReq;
import com.maan.eway.notification.req.DirectSmsSentReq;
import com.maan.eway.notification.req.JobCredentials;
import com.maan.eway.notification.req.Mail;
import com.maan.eway.notification.req.NotifGetByIdReq;
import com.maan.eway.notification.req.NotifGetReq;
import com.maan.eway.notification.req.NotifTemplateGetReq;
import com.maan.eway.notification.req.Notification;
import com.maan.eway.notification.req.NotificationFrameReq;
import com.maan.eway.notification.req.Sms;
import com.maan.eway.notification.req.TemplatesDropDownReq;
import com.maan.eway.notification.req.UnderWriter;
import com.maan.eway.notification.req.statealgo.NotificationStatus;
import com.maan.eway.notification.res.MailNotifGetRes;
import com.maan.eway.notification.res.MailTemplateRes;
import com.maan.eway.notification.res.SmsNofiGetRes;
import com.maan.eway.notification.res.SmsTemplateRes;
import com.maan.eway.notification.service.NotifTemplateService;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.notification.service.NotificationValidation;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MailMasterRepository;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.repository.SmsDataDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.upgrade.criteria.CriteriaService;

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
	private MailMasterRepository mailRepo;
	
	@Autowired
	private SmsConfigMasterRepository smsRepo;
	
	@Autowired 
	private MailDataDetailsRepository mailDataRepo;
	
	@Autowired
	private SmsDataDetailsRepository smsDataRepo;
	
	@Autowired
	private NotifTransactionDetailsRepository notifTrans ; 
	
	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Autowired
	private NotificationValidation vad;
	
	
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
		//	nf.setRemarks(req.getRemarks());
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
			mailTemplateRes.setNotificationNo(ne.getNotifNo()==null?"":String.valueOf(ne.getNotifNo()));
			mailTemplateRes.setNotifTemplateCode(req.getNotifTemplateCode());
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
			nf.setCreatedBy(req.getCreatedBy());
	//		nf.setRemarks(req.getRemarks());
			
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
			smsTemplateRes.setNotificationNo(ne.getNotifNo()==null?"":String.valueOf(ne.getNotifNo()));
			smsTemplateRes.setNotifTemplateCode(req.getNotifTemplateCode());
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
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			// Find All
			Root<NotifTransactionDetails> b = query.from(NotifTransactionDetails.class);
			query.multiselect( b.get("notifNo").alias("notifNo") ,
   			    b.get("customerName").alias("customerName") ,
			    b.get("customerMailid").alias("customerMailid"),
			    b.get("customerPhoneNo").alias("customerPhoneNo"),
			    b.get("customerPhoneCode").alias("customerPhoneCode"),
			    b.get("customerMessengerCode").alias("customerMessengerCode"),
			    b.get("customerMessengerPhone").alias("customerMessengerPhone"),
			    b.get("brokerName").alias("brokerName"),
			    b.get("brokerCompanyName").alias("brokerCompanyName"),
			    b.get("brokerMailId").alias("brokerMailId"),
			    b.get("brokerPhoneNo").alias("brokerPhoneNo"),
			    b.get("brokerPhoneCode").alias("brokerPhoneCode"),
			    b.get("brokerMessengerCode").alias("brokerMessengerCode"),
			    b.get("brokerMessengerPhone").alias("brokerMessengerPhone"),
			    b.get("uwName").alias("uwName"),
			    b.get("uwMailid").alias("uwMailid"),
			    b.get("uwPhonecode").alias("uwPhonecode"),
			    b.get("uwPhoneNo").alias("uwPhoneNo"),
			    b.get("uwMessengerCode").alias("uwMessengerCode"),
			    b.get("uwMessengerPhone").alias("uwMessengerPhone"),
			    b.get("companyName").alias("companyName"),
			    b.get("productName").alias("productName"),
			    b.get("sectionName").alias("sectionName"),
			    b.get("statusMessage").alias("statusMessage"),
			    b.get("otp").alias("otp"),
			    b.get("policyNo").alias("policyNo"),
			    b.get("quoteNo").alias("quoteNo"),
			    b.get("notifDescription").alias("notifDescription"),
			    b.get("notifTemplatename").alias("notifTemplatename"),
			    b.get("entryDate").alias("entryDate"),
			    b.get("notifcationPushDate").alias("notifcationPushDate"),
			    b.get("notifcationEndDate").alias("notifcationEndDate"),
			    b.get("notifPushedStatus").alias("notifPushedStatus"),
			    b.get("notifPriority").alias("notifPriority"),
			    b.get("tinyUrl").alias("tinyUrl"),
			    b.get("companyid").alias("companyid"),
			    b.get("productid").alias("productid"),
			    b.get("companyAddress").alias("companyAddress"),
			    b.get("companyLogo").alias("companyLogo"),
			    b.get("attachFilePath").alias("attachFilePath"),
			    b.get("pushedBy").alias("pushedBy") );
			
			// Where
			Predicate n1 = cb.equal(b.get("notifNo"), notifNo);
			
			query.where(n1);

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return list;
		
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
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
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
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
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
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
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
			n.setQuoteNo(cusRefNo.get(0).getQuoteNo()!=null? cusRefNo.get(0).getQuoteNo().toString() : "");
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
	
	
	private String getTemplateFrame(Tuple t ,NotifTemplateMaster m , String mailBody , 	String mailSubject,	String mailRegards) {
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

	@Override
	public CommonRes sentDirectMail(DirectMailSentReq req) {
		CommonRes res = new CommonRes();
		SuccessRes response = new SuccessRes();
		try {
			
			String mailBody= req.getMailBody();
			String mailSubject= req.getMailSubject();
			String mailRegards=req.getMailRegards();
			
			NotifTemplateGetReq ntr = new NotifTemplateGetReq();
			ntr.setNotifTemplateCode(req.getNotifTemplateCode());
			ntr.setInsuranceId(req.getInsuranceId());
			ntr.setProductId(req.getProductId());
			
			NotifTemplateMaster template = getTemplateDetails(ntr) ;
			
			Tuple t =  loadNotificationPending(Integer.valueOf( req.getNotificationNo())).get(0);
			MailMaster mailc = mailRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(req.getInsuranceId(),"99999","Y").get(0);													

			// Mail Credentials 
			String tomailds=(String) getValue(t,template.getToEmail());
			String tomailid=tomailds;
			List<String> mailcc=null;
			if(tomailds.indexOf(",")!=1) {
				tomailid=tomailds.split(",")[0];
			    String[] mailcsc = tomailid.split(",");
			    List<String> asList = Arrays.asList(mailcsc);
			    mailcc= (asList.size()>5)?asList.subList(0, 5):asList;
			}
			
			String templatebody=getTemplateFrame(t, template ,  mailSubject ,mailBody , mailRegards);
			
			Mail m=Mail.builder()
					.mailBody(templatebody)
					.mailRegards(mailRegards)
					.mailSubject(mailSubject)
					.mailTo(tomailid)
					.mailcc(mailcc)
					.credential(JobCredentials.builder().host(mailc.getSmtpHost()).port(mailc.getSmtpPort()).isSSL(true).password(mailc.getSmtpPwd()).username(mailc.getSmtpUser()).build())
					.attachments(t.get("attachFilePath")==null?"":t.get("attachFilePath").toString())
					.notifNo(Integer.parseInt(t.get("notifNo").toString()))
					.build();
			
			// save Mail
			MailDataDetails mdd=MailDataDetails.builder()
					.fromEmail(m.getCredential().getUsername())
					.mailBody(m.getMailBody())
					.mailRegards(m.getMailRegards())
					.mailResponse("Pending")
					.mailSubject(m.getMailSubject())
					.mailTranId(null)
					.pushedEntryDate(new Date())
					.status("P")
					.toEmail(m.getMailTo())
					.notifNo(m.getNotifNo())
					.pushedBy(req.getCreatedBy())
					.build();
			mailDataRepo.saveAndFlush(mdd);
			
			// Push Mail
			ExecutorService service = Executors.newFixedThreadPool(4);
		    service.submit(new Runnable() {
		        public void run() {
		        	pushMail(m , mdd );
		        }
		    });
			
		 	response.setResponse("Mail Sent Successfully");	
			response.setSuccessId(req.getNotificationNo());
			res.setCommonResponse(response);
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
	
	private Object getValue(Tuple t, String fieldNameString) {
		 try {
			Object o=(Object) t.get(fieldNameString);
			if (o instanceof BigDecimal) {
				return o.toString();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public CommonRes sentDirectSms(DirectSmsSentReq req) {
		CommonRes res = new CommonRes();
		SuccessRes response = new SuccessRes();
		try {
			String smsBody= req.getSmsBody();
			String smsSubject= req.getSmsSubject();
			String smsRegards=req.getSmsRegards();
		
			NotifTemplateGetReq ntr = new NotifTemplateGetReq();
			ntr.setNotifTemplateCode(req.getNotifTemplateCode());
			ntr.setInsuranceId(req.getInsuranceId());
			ntr.setProductId(req.getProductId());
			
			NotifTemplateMaster template = getTemplateDetails(ntr) ;
			Tuple t =  loadNotificationPending(Integer.valueOf( req.getNotificationNo())).get(0);
			SmsConfigMaster smsc = smsRepo.findByCompanyIdAndBranchCodeAndStatusOrderByAmendIdDesc(req.getInsuranceId(),"99999","Y").get(0);													
			
			Sms m=Sms.builder()
					.smsBody(smsBody)
					.smsRegards(smsRegards)
					.smsSubject(smsSubject)
					.smsTo(t.get("customerPhoneNo").toString() )	
					.smsFrom(smsc.getSenderId())
					.credential(JobCredentials.builder().host(smsc.getSmsPartyUrl()).isSSL(true).password(smsc.getSmsUserPass()).username(smsc.getSmsUserName()).build())
					.smsToCode(t.get("customerPhoneCode")==null?"255" : t.get("customerPhoneCode").toString())
					.notifNo(Integer.parseInt(t.get("notifNo").toString()))
					.build();
			
			// Save Sms Data Details
			SmsDataDetails savedata = new SmsDataDetails();

			Long sno = smsDataRepo.count();
			sno=sno+1;
			savedata.setMobileNo(m.getSmsTo());
			savedata.setSmsFrom(m.getSmsFrom());		
			savedata.setSmsType(smsSubject);
			savedata.setSmsContent(smsBody);
			savedata.setEntryDate(new Date());
			savedata.setSNo(sno.toString());
			savedata.setResMessage("Pending");
			savedata.setResStatus("P");
			savedata.setReqTime(new Date());
			savedata.setResTime(new Date());
			savedata.setNotifNo(m.getNotifNo());
			savedata.setPushedBy(req.getCreatedBy());
			savedata.setSmsRegards(smsRegards);
			smsDataRepo.saveAndFlush(savedata);
			
			ExecutorService service = Executors.newFixedThreadPool(4);
		    service.submit(new Runnable() {
		        public void run() {
		        	pushSms(m , savedata);
		        }
		    });
			
			
			response.setResponse("Sms Sent Successfully");	
			response.setSuccessId(req.getNotificationNo());
			res.setCommonResponse(response);
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
	
	
	public String pushMail(Mail m , MailDataDetails mdd) {
		   
		String statusResponse=null;
		try {
			Properties prop = new Properties();
			prop.put("mail.smtp.host", m.getCredential().getHost());
			prop.put("mail.smtp.port", m.getCredential().getPort());
			if(m.getCredential().getIsSSL()) {
				prop.put("mail.smtp.auth", "true");
				prop.put("mail.smtp.starttls.enable", "true"); // TLS
			}else {
				prop.put("mail.smtp.auth", "false");
				prop.put("mail.smtp.starttls.enable", "false"); // TLS
			}
			
			Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(m.getCredential().getUsername(), m.getCredential().getPassword());
				}
			});
			MimeMessage mimeMessage = new MimeMessage(session);

			mimeMessage.setFrom(new InternetAddress(m.getCredential().getUsername()));
			
			InternetAddress	to = new InternetAddress(m.getMailTo());
			mimeMessage.addRecipient(Message.RecipientType.TO, to);
			// Mail Cc
			InternetAddress[] addressCc=null;
			if (m.getMailcc() != null && m.getMailcc().size()>0 ) {
				 addressCc = new InternetAddress[m.getMailcc().size()];
				for (int i = 0; i < m.getMailcc().size(); i++) {
					if (StringUtils.isNotBlank( m.getMailcc().get(i))) {
						addressCc[i] = new InternetAddress( m.getMailcc().get(i)); 
						mimeMessage.addRecipient(Message.RecipientType.CC, addressCc[i]); 
					}
				} 
			}
			 
			mimeMessage.setSubject(m.getMailSubject());
			mimeMessage.setContent(m.getMailBody(), "text/html");
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setSubject(m.getMailSubject());
			helper.setText(m.getMailBody(), true);
			if(m.getAttachments()!=null && StringUtils.isNotBlank(m.getAttachments())) {
				for (String attachPath : m.getAttachments().split(";")) {
					File file=loadFilesFromPath(attachPath);
					if (file != null && file.exists())
						helper.addAttachment(file.getName(), file);
				}
			}
			
			
			
			Transport.send(mimeMessage);
		}catch (Exception e) {
			e.printStackTrace();
			statusResponse=e.getLocalizedMessage();
			return statusResponse ;
		}
		
		statusResponse = "Success" ;
		mdd.setMailResponse("Success");
		mdd.setStatus("C");
		mailDataRepo.save(mdd);
		return statusResponse ;
		 
	}
	
	
private File loadFilesFromPath(String attachPath) {
		
		try {
			return new File(attachPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

public String pushSms(Sms m , SmsDataDetails savedata) {

	String statusResponse = null;
	String type="0";	
	String dlr="1";
	String statuscode="";
	Integer statusvalue =0;
	try {
		/*
		Properties prop = new Properties();
		prop.put("MobileNo", m.getSmsTo());
		prop.put("SmsContent", m.getSmsBody());
		prop.put("SmsRegards", m.getSmsRegards()==null?m.getWhatsappRegards():m.getSmsRegards());
		prop.put("SmsSubject", m.getSmsSubject());
		*/	
		String mobileCode="";
		RestTemplate restTemplate = new RestTemplate();
		String fooResourceUrl = m.getCredential().getHost();
		if(StringUtils.isNotBlank( m.getSmsToCode())) {
		mobileCode = m.getSmsToCode().replace("+", "");
		}
		String content="username="
				+ URLEncoder.encode(m.getCredential().getUsername(), "UTF-8") + "&password="
				+ m.getCredential().getPassword() + "&type="
				+ URLEncoder.encode(type, "UTF-8") + "&dlr="
				+ URLEncoder.encode(dlr, "UTF-8") + "&destination="
				 + URLEncoder.encode(m.getSmsBody(), "UTF-8") + "&source="
						+ URLEncoder.encode(mobileCode+m.getSmsFrom(), "UTF-8") + "&message="
						+m.getSmsBody()+m.getSmsRegards()==null?"":m.getSmsRegards();
		System.out.println("SMS request  ---> "+fooResourceUrl + "?"+content);
		
		ResponseEntity<String> response	  = restTemplate.getForEntity(fooResourceUrl + "?"+content, String.class);
		
		System.out.println("SMS Response"+response.getBody());
		statuscode =response.getStatusCode()!=null? response.getStatusCode().toString() : "";
		statusvalue = response.getStatusCodeValue() ;		
	} catch (Exception e) {
		e.printStackTrace();
		statusResponse = e.getLocalizedMessage();
		return statusResponse ;
	}

	if(statuscode.equalsIgnoreCase("200OK")) {
	savedata.setResStatus("OK");
	savedata.setResMessage("SMS Sent Successful");		
	}
	else {
		savedata.setResStatus("Not OK");
		savedata.setResMessage("SMS Sent Failed");					
	}
	savedata.setResTime(new Date());
	smsDataRepo.save(savedata);
	statusResponse = "Success" ;
	return statusResponse ;

}

@Override
public List<MailNotifGetRes> getSentMailList(NotifGetReq req) {
	List<MailNotifGetRes> resList = new ArrayList<MailNotifGetRes>();
	try {
		// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();

			// Find All
			Root<NotifTransactionDetails> n = query.from(NotifTransactionDetails.class);
			Root<MailDataDetails> m = query.from(MailDataDetails.class);

			// Select
			query.multiselect( m.alias("mail") , n.alias("notif")   );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("pushedEntryDate")));

			// Where
			Predicate n1 = cb.equal(m.get("pushedBy"), req.getCreatedBy() );
			Predicate n2 = cb.equal(n.get("pushedBy"), req.getCreatedBy());
			Predicate n3 = cb.equal(n.get("companyid"), req.getInsuranceId());
			Predicate n4 = cb.equal(n.get("productid"), req.getProductId());
			Predicate n5 = cb.equal(n.get("notifNo"), m.get("notifNo"));
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			int limit =StringUtils.isBlank(req.getLimit())? 0 :Integer.valueOf(req.getLimit()) ;
			int offset =StringUtils.isBlank(req.getOffset())? 100 :Integer.valueOf(req.getOffset()) ;
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
		for (Tuple t : list) {
			MailDataDetails data = (MailDataDetails) t.get("mail") ;
			NotifTransactionDetails notif = (NotifTransactionDetails) t.get("notif") ;
			// Response
			MailNotifGetRes res = new MailNotifGetRes();
			res.setFromMail(data.getFromEmail());
			res.setMailBody(data.getMailBody());
			res.setMailRegards(data.getMailRegards());
			res.setMailResponse(data.getMailResponse());
			res.setMailSubject(data.getMailSubject());		
			res.setMailTranId(data.getMailTranId()==null?"" : data.getMailTranId().toString() );
			res.setNotificationNo(data.getNotifNo()==null?"" : data.getNotifNo().toString() );
			res.setPushedBy(data.getPushedBy());
			res.setPushedEntryDate(data.getPushedEntryDate());
			res.setStatus(data.getStatus());
			res.setToMail(data.getToEmail());
			res.setCustomerName(notif.getCustomerName());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}

@Override
public List<SmsNofiGetRes> getSmsSentList(NotifGetReq req) {
	List<SmsNofiGetRes> resList = new ArrayList<SmsNofiGetRes>();
	try {
		// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			List<Tuple> list = new ArrayList<Tuple>();

			// Find All
			Root<NotifTransactionDetails> n = query.from(NotifTransactionDetails.class);
			Root<SmsDataDetails> s = query.from(SmsDataDetails.class);

			// Select
			query.multiselect( s.alias("sms") , n.alias("notif")   );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(s.get("entryDate")));

			// Where
			Predicate n1 = cb.equal(s.get("pushedBy"), req.getCreatedBy() );
			Predicate n2 = cb.equal(n.get("pushedBy"), req.getCreatedBy());
			Predicate n3 = cb.equal(n.get("companyid"), req.getInsuranceId());
			Predicate n4 = cb.equal(n.get("productid"), req.getProductId());
			Predicate n5 = cb.equal(n.get("notifNo"), s.get("notifNo"));
			query.where(n1,n2,n3,n4 , n5).orderBy(orderList);
			int limit =StringUtils.isBlank(req.getLimit())? 0 :Integer.valueOf(req.getLimit()) ;
			int offset =StringUtils.isBlank(req.getOffset())? 100 :Integer.valueOf(req.getOffset()) ;
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
		for (Tuple t : list) {
			SmsDataDetails data = (SmsDataDetails) t.get("sms") ;
			NotifTransactionDetails notif = (NotifTransactionDetails) t.get("notif") ;
			// Response
			SmsNofiGetRes res = new SmsNofiGetRes();
			res.setEntryDate(data.getEntryDate());
			res.setMobileNo(data.getMobileNo());
			res.setNotificationNo(data.getNotifNo()==null?"":data.getNotifNo().toString());		
			res.setPushedBy(data.getPushedBy());
			res.setReqTime(data.getReqTime());
			res.setResTime(data.getResTime());
			res.setResMessage(data.getResMessage());		
			res.setResStatus(data.getResStatus());
			res.setSmsContent(data.getSmsContent());		
			res.setSmsFrom(data.getSmsFrom());
			res.setSmsType(data.getSmsType());
			res.setSno(data.getSNo());
			res.setSmsRegards(data.getSmsRegards());
			res.setCustomerName(notif.getCustomerName());
			resList.add(res);
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return resList;
}

@Override
public MailNotifGetRes viewSentMail(NotifGetByIdReq req) {
	MailNotifGetRes res = new MailNotifGetRes();
	try {
		List<MailDataDetails> list = mailDataRepo.findByNotifNoOrderByPushedEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
		List<NotifTransactionDetails> notiflist = notifTrans.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));

		MailDataDetails data = list.get(0) ;
		// Response
		res.setFromMail(data.getFromEmail());
		res.setMailBody(data.getMailBody());
		res.setMailRegards(data.getMailRegards());
		res.setMailResponse(data.getMailResponse());
		res.setMailSubject(data.getMailSubject());		
		res.setMailTranId(data.getMailTranId()==null?"" : data.getMailTranId().toString() );
		res.setNotificationNo(data.getNotifNo()==null?"" : data.getNotifNo().toString() );
		res.setPushedBy(data.getPushedBy());
		res.setPushedEntryDate(data.getPushedEntryDate());
		res.setStatus(data.getStatus());
		res.setToMail(data.getToEmail());
		res.setCustomerName(notiflist.get(0).getCustomerName());
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res ;
}
	

@Override
public SmsNofiGetRes viewSmsSent(NotifGetByIdReq req) {
	SmsNofiGetRes res = new SmsNofiGetRes();
	try {
		List<SmsDataDetails> list = smsDataRepo.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
		List<NotifTransactionDetails> notiflist = notifTrans.findByNotifNoOrderByEntryDateDesc(Integer.valueOf(req.getNotificationNo()));
			
		SmsDataDetails data = list.get(0);
		// Response
		res.setEntryDate(data.getEntryDate());
		res.setMobileNo(data.getMobileNo());
		res.setNotificationNo(data.getNotifNo()==null?"":data.getNotifNo().toString());		
		res.setPushedBy(data.getPushedBy());
		res.setReqTime(data.getReqTime());
		res.setResTime(data.getResTime());
		res.setResMessage(data.getResMessage());		
		res.setResStatus(data.getResStatus());
		res.setSmsContent(data.getSmsContent());		
		res.setSmsFrom(data.getSmsFrom());
		res.setSmsType(data.getSmsType());
		res.setSno(data.getSNo());
		res.setSmsRegards(data.getSmsRegards());
		res.setCustomerName(notiflist.get(0).getCustomerName());;
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
}

package com.maan.eway.renewal.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.RenewDriverDetails;
import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.bean.RenewVehicleDetails;
import com.maan.eway.bean.RenewalNotificationMaster;
import com.maan.eway.bean.RenewalTransactionDetails;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewDataRequest;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.renewal.req.RenewalPendingRequest;
import com.maan.eway.renewal.res.RenewQuotePolicyResponse;
import com.maan.eway.renewal.res.RenewalDetailRes;
import com.maan.eway.renewal.res.RenewalPendingResponse;
import com.maan.eway.renewal.res.RenewalPolicyDetailsRes;
import com.maan.eway.renewal.res.RenewalTransactionDetailsRes;
import com.maan.eway.renewal.service.RenewalService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.RenewDriverDetailsRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;
import com.maan.eway.repository.RenewVehicleDetailsRepository;
import com.maan.eway.repository.RenewalNotificationMasterRepository;
import com.maan.eway.repository.RenewalTransactionDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class RenewalServiceImpl implements RenewalService{
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepo;
	
	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepository;
	
	@Autowired
	private RenewalTransactionDetailsRepository renewalTransactionDetailsRepository;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetailsRepository;
	
	@Autowired
	private RenewVehicleDetailsRepository renewVehicleDetailsRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private RenewalNotificationMasterRepository renewalNotificationMasterRepository;
	
	@Autowired
	private GenerateSeqNoServiceImpl genSeqNoService ; 
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EServiceSectionDetailsRepository sectionRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository customerRepo;
	
	@Autowired
	private RenewDriverDetailsRepository renewalDriverRepo;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo;
	
	
	private Logger log=LogManager.getLogger(RenewalServiceImpl.class);
	@Value(value = "${spring.jpa.database}")
	private String dataBaseType;
	
	@Override
	public CommonRes pullrenewal(PullrenewalReq req) {
		CommonRes res=new CommonRes();
		try {
		String tranId=InsertTransactionDetails(req);
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<RenewalPolicyDetailsRes>list= getRenewalPolicyDetail(req);
		if(!CollectionUtils.isEmpty(list)) {
			for (RenewalPolicyDetailsRes rdata : list) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(rdata.getOldendDate());
				cal.set(Calendar.DATE, 1);
				Date startDate = cal.getTime();
				
				Calendar cal1 = new GregorianCalendar();
				cal1.setTime(rdata.getOldendDate());
				cal1.set(Calendar.DATE, 365);
				Date endDate = cal1.getTime();
				
				RenewQuotePolicy data=new RenewQuotePolicy();
				data = dozerMapper.map(rdata, RenewQuotePolicy.class);
				data.setTranId(tranId);
				data.setRequestTime(new Date());
				data.setResponseTime(new Date());
				data.setStatus("Y");
				data.setNewstartDate(startDate);
				data.setNewendDate(endDate);
				data.setCurrentStatus("RENEW_PENDING");
				data.setCurrentStageCode("R");
				data.setCurrentStatusCode("RP");
				
				renewQuotePolicyRepository.saveAndFlush(data);
				InsertVehicleDetails(rdata.getOldquoteNo());
				InsertDriverDetails(rdata.getOldquoteNo());
			}
			res.setMessage("Renewal Pull Success for "+tranId);
			InsertRenewalNotification(list,tranId);
			res.setCommonResponse(tranId);
		}else {
			res.setMessage("Renewal Pull failed for "+tranId);
		}
		return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void InsertDriverDetails(String quoteNo) {
		List<MotorDriverDetails>mddList=driverRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
		if(!CollectionUtils.isEmpty(mddList)) {
			for (MotorDriverDetails mdata : mddList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				RenewDriverDetails rvd=new RenewDriverDetails();
				rvd = dozerMapper.map(mdata, RenewDriverDetails.class);
				rvd.setRiskId(mdata.getRiskId());
				rvd.setOldrequestreferenceNo(mdata.getRequestReferenceNo());
				rvd.setOldquoteNo(quoteNo);
				rvd.setEntryDate(new Date());
				renewalDriverRepo.saveAndFlush(rvd);
			}
		}
	}


	private void InsertVehicleDetails(String quoteNo) {
		List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
		if(!CollectionUtils.isEmpty(mddList)) {
			for (MotorDataDetails mdata : mddList) {
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				RenewVehicleDetails rvd=new RenewVehicleDetails();
				rvd = dozerMapper.map(mdata, RenewVehicleDetails.class);
				rvd.setVehicleId(mdata.getVehicleId());
				rvd.setOldrequestReferenceNo(mdata.getRequestReferenceNo());
				rvd.setOldquoteNo(quoteNo);
				rvd.setEntryDate(new Date());
				renewVehicleDetailsRepository.saveAndFlush(rvd);
			}
		}
		
	}

	private String InsertTransactionDetails(PullrenewalReq req) {
		String tranId="";
		try {
			SequenceGenerateReq seqReq=new SequenceGenerateReq();
			seqReq.setInsuranceId(req.getInsuranceId());
			seqReq.setType("9");
			seqReq.setTypeDesc("RENEWAL_TRAN_ID");
			tranId=genSeqNoService.generateSeqCall(seqReq);
			
			RenewalTransactionDetails renewTrans=new RenewalTransactionDetails();
			renewTrans.setTranId(tranId);
			renewTrans.setRequestTime(new Date());
			renewTrans.setResponseTime(new Date());
			renewTrans.setStatus("Y");
			renewalTransactionDetailsRepository.saveAndFlush(renewTrans);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tranId;
	}

	private void InsertRenewalNotification(List<RenewalPolicyDetailsRes> list, String tranId) {
		try {
		if (null != list && list.size() > 0) {
			Long sno=1l;
			List<RenewalNotificationMaster>rlist=renewalNotificationMasterRepository.findAllByOrderBySnoDesc();
			if(!CollectionUtils.isEmpty(rlist)) {
				sno=rlist.get(0).getSno()+1;
			}
			RenewalNotificationMaster sms = new RenewalNotificationMaster();
			sms.setTranId(tranId);
			sms.setNotificationId(1L);
			sms.setSmssentdate(new Date());
			sms.setPolicyexpiryDate(list.get(0).getOldendDate());
			ListItemValue data=listRepo.findByItemTypeAndItemCodeAndStatus("RENEWAL_NOTIFICATION","1", "Y");
			if(data!=null) {
				sms.setNextsmsdate(getNotificationDate(sms.getPolicyexpiryDate(),Integer.parseInt(data.getParam1())));
				sms.setSmsCount(String.valueOf(list.size()));
				sms.setSno(sno);
				renewalNotificationMasterRepository.save(sms);
			}
		}
		}catch (Exception e) {
			e.printStackTrace();;
		}
	}

	public  List<RenewalPolicyDetailsRes> getRenewalPolicyDetail(PullrenewalReq req){
		
		List<RenewalPolicyDetailsRes> portfolio = new ArrayList<RenewalPolicyDetailsRes>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalPolicyDetailsRes> query = cb.createQuery(RenewalPolicyDetailsRes.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);

			// Select
			query.multiselect(
					// Customer Info
					c.get("title").alias("title"),c.get("clientName").alias("customerName"), c.get("gender").alias("gender"),
					c.get("occupation").alias("occupation"),c.get("mobileCode1").alias("mobileCode"),c.get("mobileNo1").alias("mobileNo"),
					c.get("email1").alias("emailId"), c.get("idType").alias("identityType"),c.get("idNumber").alias("identityNumber"),
					c.get("preferredNotification").alias("preferedNotification"), c.get("isTaxExempted").alias("taxExcemption"),
					c.get("street").alias("street"),c.get("nationality").alias("country"),c.get("regionCode").alias("region"),
					c.get("stateCode").alias("district"),
					
					// Vehicle Info c.get("stateCode").alias("pobox"),
					m.get("companyId").alias("companyId"), m.get("productId").alias("productCode"),m.get("sectionId").alias("sectionCode"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("oldrequestreferenceNo"), m.get("quoteNo").alias("oldquoteNo"),
					m.get("policyNo").alias("oldpolicyNo"),cb.literal("Portal").alias("serviceType"),
					m.get("inceptionDate").alias("oldstartDate"),m.get("expiryDate").alias("oldendDate"),
					m.get("noOfVehicles").alias("noofVehicle"),
					m.get("loginId").alias("loginId"),m.get("applicationId").alias("applicationId"),m.get("branchName").alias("branchName")

			);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
			
			Subquery<String> policyNo = query.subquery(String.class);
			Root<RenewQuotePolicy> ocpm1 = policyNo.from(RenewQuotePolicy.class);
			policyNo.select(ocpm1.get("oldpolicyNo"));
			policyNo.where(cb.equal(ocpm1.get("oldpolicyNo"), m.get("policyNo")));
			
			
			Predicate n5=null;
			if("mysql".equalsIgnoreCase(dataBaseType)) {
				Expression<Integer> dateDiffExpression = cb.function("DATEDIFF",Integer.class, m.get("expiryDate"),cb.currentDate());
				n5 = cb.equal(dateDiffExpression,req.getDays());
			}
			else {
				Expression<Long> dateDiffExpression = cb.diff(
				    cb.function("TRUNC", Date.class, m.get("expiryDate")).as(Long.class),
				    cb.function("TRUNC", Date.class, cb.currentDate()).as(Long.class)
				);
				n5 = cb.equal(dateDiffExpression,req.getDays());
			}
			Predicate n1 = cb.equal(m.get("status"), "P");
			Predicate n2 = cb.equal(m.get("integrationStatus"), "S"); 
			Predicate n3 = cb.equal(m.get("companyId"), req.getInsuranceId()); 
			Predicate n4 = cb.equal(m.get("customerId"), c.get("customerId")); 
			Predicate n7 = cb.isNull(m.get("endtTypeId"));
			Predicate n8 = cb.equal(m.get("productId"), "5");
			Predicate n9 = cb.exists(policyNo).not();
			query.where( n1,n2,n3,n4,n5,n7,n8,n9);
			
			// Get Result
			TypedQuery<RenewalPolicyDetailsRes> result = em.createQuery(query);
//			result.setFirstResult(limit * offset);
//			result.setMaxResults(offset);
			portfolio = result.getResultList();

		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return portfolio;
		
		
	}
	@Transactional
	public void sendSmsEmail(RenewDataRequest req) {
		boolean sms=true,mail=true;
		String remarks="",currenctStatus="",currentStatusCode="";
		try {
			
		if(StringUtils.isBlank(req.getMobileno())) {
			remarks="Mobile Not Available";
			currentStatusCode="ACV";
			currenctStatus="ADMIN-CALL-ALLIANCE";
			sms=false;
		}else if(StringUtils.isBlank(req.getMobileno())) {
			remarks="Email Not Available";
			mail=false;
		}
		if(sms || mail) {
			Calendar calend = Calendar.getInstance();
			calend.setTime(new Date()); 
			calend.add(Calendar.DATE, 1); 
			NotifTransactionDetails nt = NotifTransactionDetails.builder()
					.brokerCompanyName(req.getCustomerName())
					.brokerMailId(req.getEmail())					
					.companyName(req.getCompanyName())
					.customerPhoneCode(Integer.parseInt(req.getMobileCode()))
					.customerPhoneNo(req.getMobileno()==null?null:new BigDecimal(req.getMobileno()))
					.customerMailid(req.getEmail())					
					.customerName(req.getCustomerName())
					.entryDate(new Date())
					.notifcationPushDate(new Date())
					.notifcationEndDate(calend.getTime())
					.regNo(req.getRegistrationNo())
					.expiryDate(req.getExpiryDate())
					//.notifDescription(tempPassword)
					.notifNo(Instant.now().toEpochMilli())
					//.notifNo(null)
					.notifPriority(1)
					.notifPushedStatus("P")
					.notifTemplatename("RENEWAL_NOTIFICATION1")											
					.productName("Common")					
					//.tinyUrl(n.getTinyUrl())
					.companyid(req.getCompanyId())
					.productid(99999)
					//.companyLogo(cm.getCompanyLogo())
					//.companyAddress(cm.getCompanyAddress())											
					.tinyUrlActive("N")
					//.tinyGroupId(tinyGroupId)
					.build();
			NotifTransactionDetails sv = notifTrans.save(nt);
			List<NotifTransactionDetails> text=new LinkedList<NotifTransactionDetails>();
			text.add(sv);
			notificationService.jobProcess(text);
			currentStatusCode="ASS";
			currenctStatus="ALLIANCE-SMS-SENT";
		}
		}catch (Exception e) {
			e.printStackTrace();
			currentStatusCode="ASF";
			currenctStatus="ALLIANCE-SMS-FAILED";
		}
		if(sms) {
			updateRenewalStatusAndStage("V",currentStatusCode,currenctStatus,req.getPolicyNo());
			if("Y".equalsIgnoreCase(req.getLastNotifyYN()))
			updateNotifyStatus(req.getLastNotifyYN(),req.getPolicyNo());
		}
		updateCurrentStatus(remarks,req.getPolicyNo());
	}
	@Transactional
	private void updateNotifyStatus(String lastNotifyYN, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("lastNotifyYN", lastNotifyYN);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate();  
		
	}

	@Transactional
	private void updateCurrentStatus(String remarks, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("remarks", remarks);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate();  
		
	}
	@Transactional
	private void updateRenewalStatusAndStage(String stageCode, String currentStatusCode, String currenctStatus, String policyNo) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
		Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
		
		update.set("currentStatus", currenctStatus);
		update.set("currentStageCode", stageCode);
		update.set("currentStatusCode", currentStatusCode);
		update.where(cb.equal(rqp.get("oldpolicyNo"), policyNo) );
		em.createQuery(update).executeUpdate(); 
	}

	@Override
	public boolean getDbStatus() {
		List<ListItemValue> mobileCodes = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("RENEW_SCHEDULER" , "Y");
		if(!CollectionUtils.isEmpty(mobileCodes)) {
			return true;
		}
		return false;
	}

	@Override
	public List<RenewDataRequest> getRenewPolicyList(String tranId) {
		List<RenewDataRequest>res=new ArrayList<>();
		List<RenewQuotePolicy> rqplist=renewQuotePolicyRepository.findByCurrentStatusCodeAndTranId("RP",tranId);
			if(!CollectionUtils.isEmpty(rqplist)) {
				for (RenewQuotePolicy rdata : rqplist) {
				String quoteNo=rdata.getOldquoteNo();
				List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				if(!CollectionUtils.isEmpty(mddList)) {
					for (MotorDataDetails mdata : mddList) {
						RenewDataRequest  rdr=new RenewDataRequest();
						rdr.setCompanyId(rdata.getCompanyId());		
						rdr.setCompanyName(null);
						rdr.setCustomerName(rdata.getCustomerName());
						rdr.setEmail(rdata.getEmailId());
						rdr.setMobileCode(rdata.getMobileCode());
						rdr.setMobileno(rdata.getMobileNo());
						rdr.setPolicyNo(rdata.getOldpolicyNo());
						rdr.setTransId(rdata.getTranId());	
						rdr.setRegistrationNo(mdata.getRegistrationNumber());
						rdr.setExpiryDate(rdata.getOldendDate());
						res.add(rdr);
					}
				}
			}
		}
		
		return res;
	}

	@Override
	public List<RenewDataRequest> getNotificationRequestList() {
		List<RenewDataRequest>res=new ArrayList<>();
		
		List<String>status=new ArrayList<>();
		status.add("ACV");status.add("APG");status.add("ASS");status.add("ASF");status.add("ASC");
		List<String>tranId=getNotificationPendingTrans();
		
		List<RenewQuotePolicy> rqplist=renewQuotePolicyRepository.findByCurrentStatusCodeInAndTranIdIn(status,tranId);
			if(!CollectionUtils.isEmpty(rqplist)) {
				for (RenewQuotePolicy rdata : rqplist) {
				String quoteNo=rdata.getOldquoteNo();
				List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				if(!CollectionUtils.isEmpty(mddList)) {
					for (MotorDataDetails mdata : mddList) {
						RenewDataRequest  rdr=new RenewDataRequest();
						rdr.setCompanyId(rdata.getCompanyId());		
						rdr.setCompanyName(null);
						rdr.setCustomerName(rdata.getCustomerName());
						rdr.setEmail(rdata.getEmailId());
						rdr.setMobileCode(rdata.getMobileCode());
						rdr.setMobileno(rdata.getMobileNo());
						rdr.setPolicyNo(rdata.getOldpolicyNo());
						rdr.setTransId(rdata.getTranId());	
						rdr.setRegistrationNo(mdata.getRegistrationNumber());
						rdr.setExpiryDate(rdata.getOldendDate());
						res.add(rdr);
					}
				}
			}
		}
		
		return res;
	}

	private List<String> getNotificationPendingTrans() {
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> query = cb.createQuery(String.class);

			Root<RenewalNotificationMaster> m = query.from(RenewalNotificationMaster.class);

			// Select
			query.multiselect(m.get("tranId").alias("tranId"));

			Predicate n1=null;
			if("mysql".equalsIgnoreCase(dataBaseType)) {
				Expression<Integer> dateDiffExpression = cb.function("DATEDIFF",Integer.class, m.get("nextsmsdate"),cb.currentDate());
				n1 = cb.equal(dateDiffExpression,0);
			}
			else {
				Expression<Long> dateDiffExpression = cb.diff(
				    cb.function("TRUNC", Date.class, m.get("nextsmsdate")).as(Long.class),
				    cb.function("TRUNC", Date.class, cb.currentDate()).as(Long.class)
				);
				n1 = cb.equal(dateDiffExpression,0);
			}
			
			query.where(n1);
			
			// Get Result
			TypedQuery<String> result = em.createQuery(query);
			return result.getResultList();

		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	}

	@Override
	public void InsertNotificationSmsNext(List<RenewDataRequest> list) {
		try {
			
			List<String> uniqueTranIdList = list.stream().map(RenewDataRequest::getTransId).distinct().collect(Collectors.toList());
			
			for(String tranId:uniqueTranIdList) {
				try {
					List<RenewalNotificationMaster> nlist = renewalNotificationMasterRepository.findByTranIdOrderByNotificationIdDesc(tranId);
					if(nlist!=null) {
						RenewalNotificationMaster sms=nlist.get(0);
						ListItemValue data=listRepo.findByItemTypeAndItemCodeAndStatus("RENEWAL_NOTIFICATION",String.valueOf(sms.getNotificationId()+1) , "Y");
						if(data!=null) {
							Long sno=1l;
							List<RenewalNotificationMaster>rlist=renewalNotificationMasterRepository.findAllByOrderBySnoDesc();
							if(!CollectionUtils.isEmpty(rlist)) {
								sno=rlist.get(0).getSno()+1;
							}
							sms.setNotificationId(sms.getNotificationId()+1);
							sms.setSmssentdate(sms.getNextsmsdate());
							System.out.println("Notification Id"+sms.getNotificationId());
							if(!"5".equals(String.valueOf(sms.getNotificationId()))) {
								sms.setNextsmsdate(getNotificationDate(sms.getPolicyexpiryDate(),Integer.parseInt(data.getParam1())));
							}
							sms.setSmsCount(String.valueOf(list.size()));
							sms.setSno(sno);
							renewalNotificationMasterRepository.save(sms);
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("******Exception occurs while inserting new Notification next SMS Date******");
		}
	}
	public Date getNotificationDate(Date dateInstance,int noofdays ) {
		Date result=null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateInstance);
			cal.add(Calendar.DATE, -noofdays);
			result = cal.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	@Transactional
	public void startExpiredPolicyUpdateData() {
	try {
		updateExpiredPolicyUpdateData();
		List<RenewQuotePolicy>list=getExpiredPolicyList();
		if(!CollectionUtils.isEmpty(list)) {
			for (RenewQuotePolicy rdata : list) {
			String quoteNo=rdata.getOldquoteNo();
			List<MotorDataDetails>mddList=motorDataDetailsRepository.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
			if(!CollectionUtils.isEmpty(mddList)) {
				for (MotorDataDetails mdata : mddList) {
					RenewDataRequest  rdr=new RenewDataRequest();
					rdr.setCompanyId(rdata.getCompanyId());		
					rdr.setCompanyName(null);
					rdr.setCustomerName(rdata.getCustomerName());
					rdr.setEmail(rdata.getEmailId());
					rdr.setMobileCode(rdata.getMobileCode());
					rdr.setMobileno(rdata.getMobileNo());
					rdr.setPolicyNo(rdata.getOldpolicyNo());
					rdr.setTransId(rdata.getTranId());	
					rdr.setRegistrationNo(mdata.getRegistrationNumber());
					rdr.setExpiryDate(rdata.getOldendDate());
					sendSmsEmail(rdr);
					//res.add(rdr);
				}
			}
			}
		}
	}catch (Exception e) {
		e.printStackTrace();;
	}
	}
	private List<RenewQuotePolicy> getExpiredPolicyList() {
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewQuotePolicy> query = cb.createQuery(RenewQuotePolicy.class);

			Root<RenewQuotePolicy> rqp = query.from(RenewQuotePolicy.class);

			// Select
			query.select(rqp);

			query.where(cb.equal(rqp.get("currentStageCode"), "ASE"),rqp.get("lastNotifyYN").isNull(),
					cb.lessThan(rqp.get("oldendDate"), cb.currentDate()));
			
			// Get Result
			TypedQuery<RenewQuotePolicy> result = em.createQuery(query);
			return result.getResultList();

		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
	}

	@Transactional
	private void updateExpiredPolicyUpdateData() {
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaUpdate<RenewQuotePolicy>update=cb.createCriteriaUpdate(RenewQuotePolicy.class);
			Root<RenewQuotePolicy>rqp=update.from(RenewQuotePolicy.class);
			
			update.set("currentStatus", "V");
			update.set("currentStageCode", "ASE");
			update.set("currentStatusCode", "ALLIANCE-SMS-EXPIRED");
			update.set("remarks", "Quotation Expired");
			List<String>status=new ArrayList<>();
			status.add("PS");status.add("PF");status.add("CS");status.add("CF");status.add("RF");status.add("ASE");
			update.where(rqp.get("currentStageCode").in(status).not(),cb.lessThan(rqp.get("oldendDate"), cb.currentDate()));
			em.createQuery(update).executeUpdate(); 
		}catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	@Override
	public CopyQuoteSuccessRes renewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		res=MotorRenewalCopyQuote(req);
		return res;
	}

	private CopyQuoteSuccessRes MotorRenewalCopyQuote(RenewalCopyQuoteReq req) {
		CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceMotorDetails savedata = new EserviceMotorDetails();
		String refNo="",custRefNo="";
		try {
			
			List<RenewQuotePolicy>list=renewQuotePolicyRepository.findByOldpolicyNo(req.getPolicyNo());
			if (list.size() > 0) {

				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(req.getInsuranceId());  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	refNo =  genSeqNoService.generateSeqCall(generateSeqReq);
	 		 	
	 		 	generateSeqReq.setType("1");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
	 		 	custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
				for (RenewQuotePolicy data : list) {
					String requestNo=data.getOldrequestreferenceNo();
					String quoteNo=data.getOldquoteNo();
					List<RenewVehicleDetails>mddList=renewVehicleDetailsRepository.findByOldquoteNoOrderByVehicleIdAsc(quoteNo);
					if(!CollectionUtils.isEmpty(mddList)) {
							for (RenewVehicleDetails mdata : mddList) {
								savedata=dozerMapper.map(mdata, EserviceMotorDetails.class);
						
								savedata.setEntryDate(new Date());
								savedata.setCreatedBy(req.getLoginId());
								savedata.setUpdatedBy(req.getLoginId());
								savedata.setUpdatedDate(new Date());
								savedata.setRequestReferenceNo(refNo);
								savedata.setCustomerReferenceNo(custRefNo);
								savedata.setPolicyStartDate(data.getNewstartDate());		
								savedata.setPolicyEndDate(data.getNewendDate());
								savedata.setActualPremiumFc(BigDecimal.ZERO);
								savedata.setActualPremiumLc(BigDecimal.ZERO);
								savedata.setOverallPremiumFc(BigDecimal.ZERO);
								savedata.setOverallPremiumLc(BigDecimal.ZERO);
								savedata.setQuoteNo("");
								savedata.setStatus("Y");
								
								savedata.setAgencyCode("1111");
								
								savedata.setProductId(data.getProductCode());
								savedata.setCompanyId(data.getCompanyId());
								savedata.setSectionId(data.getSectionCode());
								savedata.setBranchCode(data.getBranchCode());
								savedata.setIdNumber(data.getIdentityNumber());
								savedata.setRiskId(Integer.parseInt(mdata.getVehicleId()));
								savedata.setEndorsementYn("N");
								savedata.setEndorsementDate(null);
								savedata.setEndorsementEffdate(null);
								savedata.setEndorsementRemarks(null);
								savedata.setEndorsementType(null);
								savedata.setEndorsementTypeDesc(null);
								savedata.setEndtCategDesc(null);
								savedata.setEndtCount(null);
								savedata.setEndtPremium(null);
								savedata.setEndtPrevPolicyNo(null);
								savedata.setEndtPrevQuoteNo(null);
								savedata.setEndtStatus(null);
								savedata.setFinalizeYn("N");
								savedata.setEmiYn("N");
								savedata.setEmiPremium(null);
								savedata.setInstallmentPeriod(null);
								savedata.setNoOfInstallment(null);
								savedata.setApplicationId(req.getApplicationId());
								if(StringUtils.isNotBlank(req.getLoginId())) {
								savedata.setLoginId(req.getLoginId());
								}
								savedata.setSubUserType(req.getSubUserType());
								List<ListItemValue> sourcerTypes =genSeqNoService.getSourceTypeDropdown(req.getInsuranceId() , req.getBranchCode() ,"SOURCE_TYPE");
								if("broker".equalsIgnoreCase(req.getUserType())){
									List<ListItemValue> filterSource =  sourcerTypes.stream().filter(o-> o.getItemValue().equalsIgnoreCase(req.getUserType())).collect(Collectors.toList());
									savedata.setSourceType(req.getUserType());
									savedata.setSourceTypeId(filterSource.get(0).getItemCode());
								}
								savedata.setPolicyNo(null);
								savedata.setVatPremium(null);
								savedata.setCdRefno(null);
								savedata.setMsRefno(null);
								savedata.setVdRefNo(null);
								savedata.setSumInsuredLc(null);
								
								// Source Type Condtion
								String sourceType = savedata.getSourceType() ;
								if(StringUtils.isNotBlank(savedata.getApplicationId()) && ! "1".equalsIgnoreCase(savedata.getApplicationId()) ) {
									List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
											&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
									savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	savedata.getSourceType());			
									savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	savedata.getSourceTypeId());
								}
								
								repo.saveAndFlush(savedata);
								
								
								//Section Insert
								EserviceSectionDetails savedata3 = new EserviceSectionDetails();
								
								savedata3.setRequestReferenceNo(refNo);
								savedata3.setCustomerReferenceNo(custRefNo);;
								savedata3.setRiskId(Integer.parseInt(mdata.getVehicleId()));
								savedata3.setLocationId(mdata.getLocationId());
								savedata3.setProductId(data.getProductCode());
								savedata3.setSectionId(String.valueOf(mdata.getSectionId()));
								savedata3.setSectionName(mdata.getSectionName());
								savedata3.setCompanyId(String.valueOf(mdata.getCompanyId()));
								savedata3.setCompanyName(mdata.getCompanyName());
								savedata3.setEntryDate(new Date());
								savedata3.setCreatedBy(req.getLoginId());
								savedata3.setUpdatedBy(req.getLoginId());
								savedata3.setUpdatedDate(new Date());
								savedata3.setQuoteNo("");
								savedata3.setStatus("Y");
								savedata3.setEndorsementDate(null);
								savedata3.setEndorsementEffdate(null);
								savedata3.setEndorsementRemarks(null);
								savedata3.setEndorsementType(null);
								savedata3.setEndorsementTypeDesc(null);
								savedata3.setEndtCategDesc(null);
								savedata3.setEndtCount(null);
								savedata3.setEndtPremium(null);
								savedata3.setEndtPrevPolicyNo(null);
								savedata3.setEndtPrevQuoteNo(null);
								savedata3.setEndtStatus(null);
								savedata3.setPolicyNo(null);
								sectionRepo.saveAndFlush(savedata3);
								
					}
				}
					
					EserviceCustomerDetails customer=new EserviceCustomerDetails();
					customer.setCustomerReferenceNo(custRefNo);
					customer.setClientName(data.getCustomerName());
					customer.setTitle(data.getTitle());
					customer.setGender(data.getGender());
					customer.setOccupation(data.getOccupation());
					customer.setMobileCode1(data.getMobileCode());
					customer.setMobileNo1(data.getMobileNo());
					customer.setEmail1(data.getEmailId());
					customer.setIdType(data.getIdentityType());
					customer.setIdNumber(data.getIdentityNumber());
					customer.setPreferredNotification(data.getPreferedNotification());	
					customer.setIsTaxExempted(data.getTaxExcemption());
					customer.setStreet(data.getStreet());
					customer.setNationality(data.getCountry());
					customer.setRegionCode(data.getRegion());
					customer.setStateCode(Integer.parseInt(data.getDistrict()));
					customer.setPinCode(data.getPobox());
					customer.setAge(9);
					customer.setPolicyHolderTypeid("1");
					customer.setCompanyId(data.getCompanyId());
					customer.setProductId(Integer.parseInt(data.getProductCode()));
					customerRepo.saveAndFlush(customer);
					
					//Driver Save
					List<RenewDriverDetails> motorDriverData = renewalDriverRepo.findByOldrequestreferenceNoAndStatusNot(requestNo,"D");
					if (motorDriverData.size() > 0) {
						for (RenewDriverDetails ddata : motorDriverData) {
							MotorDriverDetails savedriver =new MotorDriverDetails();
							savedriver = dozerMapper.map(ddata, MotorDriverDetails.class);
							savedriver.setRequestReferenceNo(refNo);
							savedriver.setQuoteNo("");
							savedriver.setEntryDate(new Date());
							savedriver.setCreatedBy(req.getLoginId());
							savedriver.setEndorsementDate(null);
							savedriver.setEndorsementEffdate(null);
							savedriver.setEndorsementRemarks(null);
							savedriver.setEndorsementType(null);
							savedriver.setEndorsementTypeDesc(null);
							savedriver.setEndtCategDesc(null);
							savedriver.setEndtCount(null);
							savedriver.setEndtPrevPolicyNo(null);
							savedriver.setEndtPrevQuoteNo(null);
							savedriver.setEndtStatus(null);
							savedriver.setIsFinaceYn("N");
							driverRepo.saveAndFlush(savedriver);
						}
					}
					
				}
				
				
				
				
				
				
			}
			res.setRequestReferenceNo(refNo);

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
		return null;
	}
		return res;
	}

	

	@Override
	public CommonRes getRenewalPending(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllPendingRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalDetailRes> getAllPendingRenewalDetails(RenewalPendingRequest req) {
		List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
		
		try {

	        Date today = new Date();
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(today);
	        calendar.add(Calendar.DAY_OF_MONTH, 30);
	        Date dateAfter30Days = calendar.getTime();
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

			// Find All
			Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
						
			// Select
			query.multiselect(
					r.get("oldpolicyNo").alias("oldpolicyNo"),
					r.get("oldquoteNo").alias("oldquoteNo"),
					r.get("customerName").alias("customerName"),
					r.get("oldstartDate").alias("inceptionDate"),
					r.get("oldendDate").alias("expiryDate"),
					r.get("newpolicyNumber").alias("newpolicyNumber"),
					r.get("currentStatus").alias("currentStatus")

					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(r.get("newendDate")));
					
			// Where
			Predicate n1 = cb.equal(r.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(r.get("productCode"), req.getProductId());
			Predicate n3 = cb.between(r.get("newendDate"), today, dateAfter30Days);
			Predicate n4 = cb.equal(r.get("loginId"), req.getLoginId());
			Predicate n5 = cb.equal(r.get("branchCode"), req.getBranchCode());

			query.where(n1, n2, n3, n4, n5).orderBy(orderList);


			// Get Result
			TypedQuery<RenewalDetailRes> result = em.createQuery(query);
			res = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public CommonRes getRenewalExpired(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllExpiredRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	private List<RenewalDetailRes> getAllExpiredRenewalDetails(RenewalPendingRequest req) {
      List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
		
		try {

	        Date today = new Date();
	        
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

			// Find All
			Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
						
			// Select
			query.multiselect(
					r.get("oldpolicyNo").alias("oldpolicyNo"),
					r.get("oldquoteNo").alias("oldquoteNo"),
					r.get("customerName").alias("customerName"),
					r.get("oldstartDate").alias("inceptionDate"),
					r.get("oldendDate").alias("expiryDate"),
					r.get("newpolicyNumber").alias("newpolicyNumber"),
					r.get("currentStatus").alias("currentStatus")

					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(r.get("newendDate")));
					
			// Where
			Predicate n1 = cb.equal(r.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(r.get("productCode"), req.getProductId());
			Predicate n3 = cb.lessThan(r.get("newendDate"), today);
			Predicate n4 = cb.equal(r.get("loginId"), req.getLoginId());
			Predicate n5 = cb.equal(r.get("branchCode"), req.getBranchCode());

			query.where(n1, n2, n3, n4, n5).orderBy(orderList);


			// Get Result
			TypedQuery<RenewalDetailRes> result = em.createQuery(query);
			res = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	public CommonRes getRenewalCompleted(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			RenewalPendingResponse res = new RenewalPendingResponse();
			List<RenewalDetailRes> renewal = getAllCompletedRenewalDetails(req);
			res.setRenewalDetailRes(renewal);
			if(renewal != null ) {
				int count = renewal.size();
				res.setTotalCount(count);
			}
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalDetailRes> getAllCompletedRenewalDetails(RenewalPendingRequest req) {
		 List<RenewalDetailRes> res = new ArrayList<RenewalDetailRes>();
			
			try {
		        
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<RenewalDetailRes> query = cb.createQuery(RenewalDetailRes.class);

				// Find All
				Root<RenewQuotePolicy> r = query.from(RenewQuotePolicy.class);
							
				// Select
				query.multiselect(
						r.get("oldpolicyNo").alias("oldpolicyNo"),
						r.get("oldquoteNo").alias("oldquoteNo"),
						r.get("customerName").alias("customerName"),
						r.get("oldstartDate").alias("inceptionDate"),
						r.get("oldendDate").alias("expiryDate"),
						r.get("newpolicyNumber").alias("newpolicyNumber"),
						r.get("currentStatus").alias("currentStatus")

						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(r.get("newendDate")));
						
				// Where
				Predicate n1 = cb.equal(r.get("companyId"), req.getInsuranceId());
				Predicate n2 = cb.equal(r.get("productCode"), req.getProductId());
				Predicate n3 = cb.equal(r.get("currentStatusCode"), "RS");
				Predicate n4 = cb.equal(r.get("loginId"), req.getLoginId());
				Predicate n5 = cb.equal(r.get("branchCode"), req.getBranchCode());

				query.where(n1, n2, n3, n4, n5).orderBy(orderList);


				// Get Result
				TypedQuery<RenewalDetailRes> result = em.createQuery(query);
				res = result.getResultList();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return res;
	}

	@Override
	public CommonRes getRenewalTransaction(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			List<RenewalTransactionDetailsRes> res = new ArrayList<RenewalTransactionDetailsRes>();
			res = getAllRenewalTransactionDetails(req);

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewalTransactionDetailsRes> getAllRenewalTransactionDetails(RenewalPendingRequest req) {
		List<RenewalTransactionDetailsRes> res = new ArrayList<RenewalTransactionDetailsRes>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalTransactionDetailsRes> cq = cb.createQuery(RenewalTransactionDetailsRes.class);

			// Root for RenewalTransactionDetails table
			Root<RenewalTransactionDetails> renewalTransactionDetailsRoot = cq.from(RenewalTransactionDetails.class);

			// Subquery for success count
			Subquery<String> successCountSubquery = cq.subquery(String.class);
			Root<RenewQuotePolicy> successSubqueryRoot = successCountSubquery.from(RenewQuotePolicy.class);
			successCountSubquery.select(cb.count(successSubqueryRoot).as(String.class))
			    .where(
			        cb.equal(successSubqueryRoot.get("tranId"), renewalTransactionDetailsRoot.get("tranId")),
			        cb.equal(successSubqueryRoot.get("currentStatusCode"), "RS")
			    );

			// Subquery for Converted count
			Subquery<String> convertedCountSubquery = cq.subquery(String.class);
			Root<RenewQuotePolicy> convertedSubqueryRoot = convertedCountSubquery.from(RenewQuotePolicy.class);
			convertedCountSubquery.select(cb.count(convertedSubqueryRoot).as(String.class))
			    .where(
			        cb.equal(convertedSubqueryRoot.get("tranId"), renewalTransactionDetailsRoot.get("tranId")),
			        cb.equal(convertedSubqueryRoot.get("currentStatusCode"), "CS")
			    );

			// Subquery for pending count
			Subquery<String> pendingCountSubquery = cq.subquery(String.class);
			Root<RenewQuotePolicy> pendingSubqueryRoot = pendingCountSubquery.from(RenewQuotePolicy.class);
			pendingCountSubquery.select(cb.count(pendingSubqueryRoot).as(String.class))
			    .where(
			        cb.equal(pendingSubqueryRoot.get("tranId"), renewalTransactionDetailsRoot.get("tranId")),
			        cb.notEqual(pendingSubqueryRoot.get("currentStatusCode"), "RS"),
			        cb.notEqual(pendingSubqueryRoot.get("currentStatusCode"), "CS")
			    );
			
			// Subquery for total count
	        Subquery<String> totalCountSubquery = cq.subquery(String.class);
	        Root<RenewQuotePolicy> totalCountSubqueryRoot = totalCountSubquery.from(RenewQuotePolicy.class);
	        totalCountSubquery.select(cb.count(totalCountSubqueryRoot).as(String.class))
	            .where(
	                cb.equal(totalCountSubqueryRoot.get("tranId"), renewalTransactionDetailsRoot.get("tranId"))
	            );
	        
			// Define predicates for the query
			//Predicate tranIdPredicate = cb.equal(renewalTransactionDetailsRoot.get("tranId"), req.getTranId());

			// Select fields and subqueries in multiselect
			cq.multiselect(
			    renewalTransactionDetailsRoot.get("tranId").alias("tranId"),
			    renewalTransactionDetailsRoot.get("requestTime").alias("requestTime"),
			    renewalTransactionDetailsRoot.get("responseTime").alias("responseTime"),
			    totalCountSubquery.getSelection().alias("totalCount"),
			    successCountSubquery.getSelection().alias("successCount"),
			    convertedCountSubquery.getSelection().alias("convertedCount"),
			    pendingCountSubquery.getSelection().alias("pendingCount")
			);

			// Apply the predicates
			//cq.where(tranIdPredicate);
			
			// Apply the order by clause
	        cq.orderBy(cb.asc(renewalTransactionDetailsRoot.get("responseTime")));
	        
			// Execute the query
			res =  em.createQuery(cq).getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return res;
	}

	@Override
	public CommonRes getRenewalTransactionSuccess(RenewalPendingRequest req) {
	    CommonRes data = new CommonRes();
	    try {
	        // Fetch the list of RenewQuotePolicy where status is "RS" (Success)
	        List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCurrentStatusCode(req.getTranId(), "RS");

	        // Map the entities to response objects
	        List<RenewQuotePolicyResponse> res = mapRenewQuotePolicyResponse(list);

	        // Set the response data
	        data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        data.setIsError(true);
	        data.setMessage("Failed");
	    }
	    return data;
	}

	@Override
	public CommonRes getRenewalTransactionConverted(RenewalPendingRequest req) {
		CommonRes data = new CommonRes();
		try {
			List<RenewQuotePolicyResponse> res = new ArrayList<RenewQuotePolicyResponse>();
			
			List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCurrentStatusCode(req.getTranId(),"CS");
			
			res = mapRenewQuotePolicyResponse(list);

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private List<RenewQuotePolicyResponse> mapRenewQuotePolicyResponse(List<RenewQuotePolicy> list) {
	    List<RenewQuotePolicyResponse> responseList = new ArrayList<>();
	    try {
			for (RenewQuotePolicy policy : list) {
			    RenewQuotePolicyResponse response = new RenewQuotePolicyResponse();
			    
			 // Mapping fields from RenewQuotePolicy to RenewQuotePolicyResponse
		        response.setTranId(policy.getTranId());
		        response.setOldRequestRefNo(policy.getOldrequestreferenceNo());
		        response.setServiceType(policy.getServiceType());
		        response.setRequestTime(policy.getRequestTime());
		        response.setResponseTime(policy.getResponseTime());
		        response.setStatus(policy.getStatus());
		        response.setOldPolicyNo(policy.getOldpolicyNo());
		        response.setOldQuoteNo(policy.getOldquoteNo());
		        response.setOldStartDate(policy.getOldstartDate());
		        response.setOldEndDate(policy.getOldendDate());
		        response.setTitle(policy.getTitle());
		        response.setCustomerName(policy.getCustomerName());
		        response.setGender(policy.getGender());
		        response.setOccupation(policy.getOccupation());
		        response.setMobileCode(policy.getMobileCode());
		        response.setMobileNo(policy.getMobileNo());
		        response.setEmailId(policy.getEmailId());
		        response.setIdentityType(policy.getIdentityType());
		        response.setIdentityNumber(policy.getIdentityNumber());
		        response.setPreferredNotification(policy.getPreferedNotification());
		        response.setTaxExcemption(policy.getTaxExcemption());
		        response.setStreet(policy.getStreet());
		        response.setCountry(policy.getCountry());
		        response.setRegion(policy.getRegion());
		        response.setDistrict(policy.getDistrict());
		        response.setPobox(policy.getPobox());
		        response.setNoOfVehicle(policy.getNoofVehicle());
		        response.setNewStartDate(policy.getNewstartDate());
		        response.setNewEndDate(policy.getNewendDate());
		        response.setNewPremium(policy.getNewPremium() != null ? policy.getNewPremium().toString() : null);
		        response.setNewPolicyNumber(policy.getNewpolicyNumber());
		        response.setCurrentStatus(policy.getCurrentStatus());
		        response.setCurrentStageCode(policy.getCurrentStageCode());
		        response.setCurrentStatusCode(policy.getCurrentStatusCode());
		        response.setLoginId(policy.getLoginId());
		        response.setApplicationId(policy.getApplicationId());
		        response.setCompanyId(policy.getCompanyId());
		        response.setProductCode(policy.getProductCode());
		        response.setSectionCode(policy.getSectionCode());
		        response.setBranchCode(policy.getBranchCode());
		        response.setBranchName(policy.getBranchName());
		        response.setViewedDate(policy.getViewDate());
		        response.setRemarks(policy.getRemarks());

			    responseList.add(response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return responseList;
	}

	@Override
	public CommonRes getRenewalTransactionPending(RenewalPendingRequest req) {
	    CommonRes data = new CommonRes();
	    try {
	        // Fetch the list of RenewQuotePolicy where status is neither "RS" nor "CS" (Pending)
	        List<RenewQuotePolicy> list = renewQuotePolicyRepo.findByTranIdAndCurrentStatusCodeNotIn(req.getTranId(), Arrays.asList("RS", "CS"));

	        // Map the entities to response objects
	        List<RenewQuotePolicyResponse> res = mapRenewQuotePolicyResponse(list);

	        // Set the response data
	        data.setCommonResponse(res);
	        data.setIsError(false);
	        data.setErrorMessage(Collections.emptyList());
	        data.setMessage("Success");
	    } catch (Exception e) {
	        e.printStackTrace();
	        data.setIsError(true);
	        data.setMessage("Failed");
	    }
	    return data;
	}
	
}

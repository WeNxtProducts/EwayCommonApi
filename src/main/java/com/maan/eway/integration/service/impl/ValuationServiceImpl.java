package com.maan.eway.integration.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.ValuationIntegration;
import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationListReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.ValuationListRes;
import com.maan.eway.integration.service.ValuationService;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.notification.service.NotificationService;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.ValuationIntegrationRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ValuationServiceImpl implements ValuationService {
	@PersistenceContext
	private EntityManager em;
	@Autowired
	private MotorDataDetailsRepository repo;
	@Autowired
	private SolvitValuation solvit;
	@Autowired
	private RegentValuation regent;
	@Autowired
	private ValuationIntegrationRepository valuationIntegrationRepository;
	@Autowired
	private NotificationService notificationService;
	@Autowired 
	private NotifTransactionDetailsRepository notifTrans;
	
	@Autowired 
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Override
	public PremiaResponse pushValuation(ValuationReq req) {
		PremiaResponse resp=new PremiaResponse();
		String valCompanyId="";
		try {
			List<MotorDataDetails>list=repo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			valCompanyId=list.get(0).getValCompanyId();
			if(!CollectionUtils.isEmpty(list)) {
			if("1".equals(valCompanyId))
				resp=solvit.pushValuation(req);
			else if("2".equals(valCompanyId))
				resp=regent.pushValuation(req);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return resp;
	}

	@Override
	public PremiaResponse getStatus(ValuationStatusReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			if("1".equals(req.getValCompanyId()))
				resp=solvit.getStatus(req);
			else if("2".equals(req.getValCompanyId()))
				resp=regent.getStatus(req);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public PremiaResponse getDetails(ValuationDetailsReq req) {
		PremiaResponse resp=new PremiaResponse();
		try {
			if("1".equals(req.getValCompanyId()))
				resp=solvit.getDetails(req);
			else if("2".equals(req.getValCompanyId()))
				resp=regent.getDetails(req);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public List<ValuationListRes> getValuationList(ValuationListReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<ValuationListRes>list=null;
		try {
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ValuationListRes> query = cb.createQuery(ValuationListRes.class);

		// Find All
		Root<ValuationIntegration> a = query.from(ValuationIntegration.class);

		// Select
		query.multiselect(a.get("quoteNo").alias("quoteNo"),
				a.get("vehicleId").alias("vehicleId"),a.get("vehicleRegNo").alias("vehicleRegNo"),a.get("firstName").alias("firstName"),
				a.get("email").alias("email"),a.get("customerMobile").alias("customerMobile"),a.get("policyNo").alias("policyNo"),
				a.get("createRequest").alias("createRequest"),a.get("createResponse").alias("createResponse"),a.get("idrequest").alias("idrequest"),
				a.get("idresponse").alias("statusrequest"),a.get("statusrequest").alias("statusresponse"),a.get("statusresponse").alias("idresponse"),
				a.get("recordId").alias("recordId"),a.get("status").alias("status"),a.get("valCompanyId").alias("valCompanyId"),
				a.get("exceptionSumInsured").as(String.class).alias("exceptionSumInusred"),a.get("exceptionStatus").alias("exceptionStatus"),a.get("exceptionRemarks").alias("exceptionRemarks"));

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(a.get("vehicleId")));

		
		List<Predicate> predics1 = new ArrayList<Predicate>();
		predics1.add(cb.equal(a.get("companyId"), req.getCompanyId()));
		if("quoteNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("quoteNo"), req.getSearchValue()));
		}else if("policyNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("policyNo"), req.getSearchValue()));
		}else if("vehicleRegNo".equals(req.getSearchBy())) {
			predics1.add(cb.equal(a.get("vehicleRegNo"), req.getSearchValue()));
		}else {
			predics1.add(cb.between(a.get("entryDate"), sdf.parse(req.getStartDate()), sdf.parse(req.getEndDate())));
		}
		
		

		query.where(predics1.toArray(new Predicate[0])).orderBy(orderList);

		// Get Result
		TypedQuery<ValuationListRes> result = em.createQuery(query);
		list = result.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ValuationStatusReq> getValuationStatusPendingList() {
		List<ValuationStatusReq>list=new ArrayList<>();
		List<ValuationIntegration>vlist=valuationIntegrationRepository.findByStatusOrderByQuoteNo("Pending");
		if(!CollectionUtils.isEmpty(vlist)) {
			for (ValuationIntegration data : vlist) {
				ValuationStatusReq vs=new ValuationStatusReq();
				vs.setBranchCode(data.getBranchCode());
				vs.setCompanyId(data.getCompanyId());
				vs.setValCompanyId(data.getValCompanyId());
				vs.setVehicleRegNo(data.getVehicleRegNo());
				list.add(vs);
			}
		}
		return list;
	}

	public void sendSMSMail(ValuationIntegration vdata) {
		boolean sms=true,mail=true;
		try {
			 String mobileNo=vdata.getCustomerMobile();
			 String email=vdata.getEmail();
			 if(StringUtils.isBlank(email)) {
				 mail=false;
			 }
			 if(StringUtils.isBlank(mobileNo)) {
				 sms=false;
			 }
			if(sms || mail) {
				List<InsuranceCompanyMaster>cm= companyRepo.findTopByCompanyIdOrderByAmendIdDesc(vdata.getCompanyId());
				if(!CollectionUtils.isEmpty(cm)) {
				Calendar calend = Calendar.getInstance();
				calend.setTime(new Date()); 
				calend.add(Calendar.DATE, 1); 
				NotifTransactionDetails nt = NotifTransactionDetails.builder()
						.brokerCompanyName(vdata.getFirstName())
						.brokerMailId(vdata.getEmail())					
						.companyName("First Insurance")
						.customerPhoneCode(Integer.parseInt("254"))
						.customerPhoneNo(vdata.getCustomerMobile()==null?null:new BigDecimal(vdata.getCustomerMobile()))
						.customerMailid(vdata.getEmail())					
						.customerName(vdata.getFirstName())
						.entryDate(new Date())
						.notifcationPushDate(new Date())
						.notifcationEndDate(calend.getTime())
						.regNo(vdata.getVehicleRegNo())
						//.notifDescription(tempPassword)
						.notifNo(Instant.now().toEpochMilli())
						//.notifNo(null)
						.notifPriority(1)
						.notifPushedStatus("P")
						.notifTemplatename("VALUATION_NOTIFICATION")											
						.productName("Common")					
						//.tinyUrl(n.getTinyUrl())
						.companyid(vdata.getCompanyId())
						.productid(99999)
						.companyLogo(cm.get(0).getCompanyLogo())
						.companyAddress(cm.get(0).getCompanyAddress())											
						.tinyUrlActive("N").pushedBy(vdata.getValCompanyName())
						//.tinyGroupId(tinyGroupId)
						.build();
				NotifTransactionDetails sv = notifTrans.save(nt);
				List<NotifTransactionDetails> text=new LinkedList<NotifTransactionDetails>();
				text.add(sv);
				notificationService.jobProcess(text);
				}
			}
			 
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
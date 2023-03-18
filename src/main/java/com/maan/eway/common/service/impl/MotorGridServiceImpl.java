package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.SeqRefno;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MotorGridServiceImpl implements MotorGridService {
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetepo;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;

	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private MotorDriverDetailsRepository motordrivDetepo;
	
	@Autowired
	private CoverDocumentUploadDetailsRepository coverDocUploadDetails;

	
	// Exiting Motor Details
	@Override
	public List<QuoteCriteriaRes> getMotorExistingQuoteDetails(ExistingQuoteReq req, List<String> branches,
			Date startDate, Date endDate, Integer limit, Integer offset) {
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		try {

			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

			// Select
			query.multiselect(cb.count(m).alias("idsCount"),
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
							.alias("quoteNo"),
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
							.otherwise(m.get("customerId")).alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate")
					
					);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("policyStartDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n7 = cb.equal(m.get("loginId"), req.getLoginId());
			} else {
				n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				Expression<String> e0 = m.get("brokerBranchCode");
				n8 = e0.in(branches);
			} else {
				Expression<String> e0 = m.get("branchCode");
				n8 = e0.in(branches);
			}

			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
			existingQuotes = existingQuotes.stream().filter(o -> !o.getIdsCount().equals(0L))
					.collect(Collectors.toList());
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return existingQuotes;
	}

	@Override
	public List<QuoteCriteriaRes> getMotorLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches, Date before30,
			int limit, int offset) {
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.multiselect(cb.count(m).alias("idsCount"),
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
							.alias("quoteNo"),
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
							.otherwise(m.get("customerId")).alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("policyStartDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);

			Predicate n6 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n6 = cb.equal(m.get("loginId"), req.getLoginId());
			} else {
				n6 = cb.equal(m.get("applicationId"), req.getApplicationId());
			}
			Predicate n7 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				Expression<String> e0 = m.get("brokerBranchCode");
				n7 = e0.in(branches);
			} else {
				Expression<String> e0 = m.get("branchCode");
				n7 = e0.in(branches);
			}
			Predicate n8 = cb.isNull(m.get("endtTypeId"));
			query.where(n1, n2, n3, n4, n5, n6, n7,n8)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
			lapsedQuotes = lapsedQuotes.stream().filter(o -> !o.getIdsCount().equals(0L)).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return lapsedQuotes;
	}

	@Override
	public List<RejectCriteriaRes> getMotorRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			// Find All
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.multiselect(cb.count(m).alias("idsCount"),
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
							.alias("quoteNo"),
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
							.otherwise(m.get("customerId")).alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("policyStartDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "R");

			Predicate n5 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				Expression<String> e0 = m.get("brokerBranchCode");
				n6 = e0.in(branches);
			} else {
				Expression<String> e0 = m.get("branchCode");
				n6 = e0.in(branches);
			}
			Predicate n7 = cb.isNull(m.get("endtTypeId"));
			query.where(n1, n2, n3, n4, n5, n6,n7)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			rejectedQuotes = rejectedQuotes.stream().filter(o -> !o.getIdsCount().equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return rejectedQuotes;
	}


	@Override
	public synchronized List<MotorGridCriteriaRes> getMotorReferalDetails(ExistingQuoteReq req, List<String> branches,
			int limit, int offset, String status) {
		List<MotorGridCriteriaRes> referrals = new ArrayList<MotorGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.multiselect(cb.count(m).as(Long.class).alias("idsCount"),
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
							.alias("quoteNo"),
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
							.otherwise(m.get("customerId")).alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks")
			
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("policyStartDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);

			Predicate n5 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				Expression<String> e0 = m.get("brokerBranchCode");
				n6 = e0.in(branches);
			} else {
				Expression<String> e0 = m.get("branchCode");
				n6 = e0.in(branches);
			}
			Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1, n2, n3, n4, n5, n6,n7)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			List<Tuple> referralsList = result.getResultList();
			for (  Tuple r :referralsList   ) {
				MotorGridCriteriaRes res = new MotorGridCriteriaRes();
				res.setIdsCount(r.get("idsCount")==null ? null : (Long) r.get("idsCount"));
				res.setAdminRemarks(r.get("adminRemarks")==null ? "" : (String) r.get("adminRemarks"));
				res.setBranchCode(r.get("branchCode")==null ? "" : (String) r.get("branchCode"));
				res.setClientName(r.get("clientName")==null ? "" : (String) r.get("clientName"));
				res.setCompanyId(r.get("companyId")==null ? "" : (String) r.get("companyId"));
				res.setCustomerId(r.get("customerId")==null ? "" : (String) r.get("customerId"));
				res.setCustomerReferenceNo(r.get("customerReferenceNo")==null ? "" : (String) r.get("customerReferenceNo"));
				res.setIdNumber(r.get("idNumber")==null ? "" : (String) r.get("idNumber"));
				res.setPolicyEndDate(r.get("policyEndDate")==null ? null : (Date) r.get("policyEndDate"));
				res.setPolicyStartDate(r.get("policyStartDate")==null ? null : (Date) r.get("policyStartDate"));
				res.setProductId(r.get("productId")==null ? "" : (String) r.get("productId"));
				res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
				res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
				res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
				referrals.add(res);
			}
			
			referrals = referrals.stream().filter(o -> !o.getIdsCount().equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referrals;
	}

	@Override
	public synchronized List<MotorGridCriteriaRes> getMotorAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset ,String status) {
		List<MotorGridCriteriaRes> referrals = new ArrayList<MotorGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.multiselect(cb.count(m).as(Long.class).alias("idsCount"),
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
							.alias("quoteNo"),
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
							.otherwise(m.get("customerId")).alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks")
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("policyStartDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status);

			Expression<String> e0 = c.get("branchCode");
			Predicate n6 = e0.in(branches);
			Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1, n2, n3, n4, n6,n7)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			List<Tuple> referralsList = result.getResultList();
			for (  Tuple r :referralsList   ) {
				MotorGridCriteriaRes res = new MotorGridCriteriaRes();
				res.setIdsCount(r.get("idsCount")==null ? null : (Long) r.get("idsCount"));
				res.setAdminRemarks(r.get("adminRemarks")==null ? "" : (String) r.get("adminRemarks"));
				res.setBranchCode(r.get("branchCode")==null ? "" : (String) r.get("branchCode"));
				res.setClientName(r.get("clientName")==null ? "" : (String) r.get("clientName"));
				res.setCompanyId(r.get("companyId")==null ? "" : (String) r.get("companyId"));
				res.setCustomerId(r.get("customerId")==null ? "" : (String) r.get("customerId"));
				res.setCustomerReferenceNo(r.get("customerReferenceNo")==null ? "" : (String) r.get("customerReferenceNo"));
				res.setIdNumber(r.get("idNumber")==null ? "" : (String) r.get("idNumber"));
				res.setPolicyEndDate(r.get("policyEndDate")==null ? null : (Date) r.get("policyEndDate"));
				res.setPolicyStartDate(r.get("policyStartDate")==null ? null : (Date) r.get("policyStartDate"));
				res.setProductId(r.get("productId")==null ? "" : (String) r.get("productId"));
				res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
				res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
				res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
				referrals.add(res);
			}
			referrals = referrals.stream().filter(o -> !o.getIdsCount().equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referrals;
	}

	//SearchMotorQuote
		@Override
		public List<Tuple> searchMotorQuote(CopyQuoteReq req, List<String> branches) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<Tuple> searchQuote = new ArrayList<Tuple>();
			try {
				// Search
				String searchKey = req.getSearchKey();
				String searchValue = req.getSearchValue();
				String companyId = req.getInsuranceId();
				String loginId = req.getLoginId();
				String userType = req.getUserType();

				if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("ClientName".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("RegistrationNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("EntryDate".equalsIgnoreCase(searchKey)) {
					//Date entryDate = sdf.parse(searchValue);
					//searchValue = sdf.format(entryDate);
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return searchQuote;
		}

		public List<Tuple> searchDetails(String searchKey, String searchValue, String companyId, String loginId,
				String userType, List<String> branches) {
			List<Tuple> customerDetailsList = new ArrayList<Tuple>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceMotorDetails> c = query.from(EserviceMotorDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(c.alias("c"),
						cus.get("clientName").alias("clientName"),cb.count(c).alias("idsCount"));


				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("customerReferenceNo")));

				Predicate n1 = null;
				Predicate n3 = null;
				Predicate n4 = null;
				Predicate n5 = null;
		

				// Where
				if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
				} else if (searchKey.equalsIgnoreCase("CustomerReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("customerReferenceNo")), searchValue);
				} else if (searchKey.equalsIgnoreCase("RegistrationNumber")) {
					n1 = cb.equal(cb.lower(c.get("registrationNumber")), searchValue);
				} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
					n1 = cb.equal(cb.lower(c.get("quoteNo")), searchValue);
				} else if (searchKey.equalsIgnoreCase("EntryDate")) {
					Date entryDate = sdf.parse(searchValue);
					Calendar cal = new GregorianCalendar();
					cal.setTime(entryDate);
					//cal.add(Calendar.HOUR , -1);
					cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
					Date startDate = cal.getTime() ;
					cal.setTime(entryDate);
				//	cal.add(Calendar.HOUR , +23);
					cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
					Date endDate = cal.getTime() ;
					n1=cb.between(c.get("entryDate"), startDate, endDate);
					
				} else if (searchKey.equalsIgnoreCase("ChassisNumber")) {
					n1 = cb.equal(cb.lower(c.get("chassisNumber")), searchValue);
				} else if (searchKey.equalsIgnoreCase("ClientName")) {
					n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
					n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
				}

				Predicate n2 = cb.equal(c.get("companyId"), companyId);

				if ("issuer".equalsIgnoreCase(userType)) {
					n3 = cb.equal(c.get("applicationId"), loginId);
					Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
					n3 = cb.equal(c.get("loginId"), loginId);
					Expression<String> e0 = c.get("brokerBranchCode");
					n4 = e0.in(branches);
				}
				if (searchKey.equalsIgnoreCase("ClientName")) {
					if ("issuer".equalsIgnoreCase(userType)) {

						Expression<String> e0 = cus.get("branchCode");
						n4 = e0.in(branches);
					} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

						Expression<String> e0 = cus.get("brokerBranchCode");
						n4 = e0.in(branches);
					}
				}
				n5 = cb.equal(cus.get("customerReferenceNo"), c.get("customerReferenceNo"));
				Predicate n6 = cb.isNull(c.get("endtTypeId"));
				query.where(n1,n2,n3,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
						c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
				.orderBy(orderList);
				if (searchKey.equalsIgnoreCase("ClientName")) {
					query.where(n1, n2,n4,n5,n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
							c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
					.orderBy(orderList);
				}
				if (searchKey.equalsIgnoreCase("EntryDate")) {
					query.where(n1,n2,n3,n4,n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
							c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
					.orderBy(orderList);
				}

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				customerDetailsList = result.getResultList();
				customerDetailsList = customerDetailsList.stream().filter(o -> !o.get("idsCount").equals(0L))
						.collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return customerDetailsList;
		}

		//CopyQuote
		@Override
		public CopyQuoteSuccessRes motorCopyQuote(CopyQuoteReq req, List<String> branches,String loginId) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		//	SimpleDateFormat idf = new SimpleDateFormat("yyMMddmmssSSS");
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceMotorDetails savedata = new EserviceMotorDetails();
			
			try {
				String searchValue = req.getRequestReferenceNo();
				String searchKey = "RequestReferenceNo";
				String companyId = req.getInsuranceId();
				String userType = req.getUserType();
				String branchCode = "";
				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);

				String refNo = req.getRequestReferenceNo();

			//	Random rand = new Random();
			//	int random = rand.nextInt(90) + 10;
			//	refNo = "Mot-" + idf.format(new Date()) + random;
			String refShortCode = getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
			refNo = refShortCode + seqNo.generateRefNo();
			if (list.size() > 0) {
				for (Tuple data : list) {

					savedata = dozerMapper.map(data.get(0), EserviceMotorDetails.class);

					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getLoginId());
					savedata.setUpdatedBy(req.getLoginId());
					savedata.setUpdatedDate(new Date());
					savedata.setRequestReferenceNo(refNo);
					savedata.setOldReqRefNo(req.getRequestReferenceNo());
					if (req.getUserType().equalsIgnoreCase("Broker") || (req.getUserType().equalsIgnoreCase("User"))) {
						branchCode = req.getBranchCode();
						savedata.setApplicationId("1");
						savedata.setBrokerBranchCode(branchCode);

					} else if ("issuer".equalsIgnoreCase(userType)) {
						savedata.setApplicationId(req.getLoginId());
						branchCode = req.getBranchCode();
						savedata.setBranchCode(branchCode);
					}

					savedata.setActualPremiumFc(BigDecimal.ZERO);
					savedata.setActualPremiumLc(BigDecimal.ZERO);
					savedata.setOverallPremiumFc(BigDecimal.ZERO);
					savedata.setOverallPremiumLc(BigDecimal.ZERO);
					savedata.setQuoteNo("");
					repo.saveAndFlush(savedata);
				}
			
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
		
		public synchronized String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
			String itemDesc = "";
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();

				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);

				// Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));

				// Effective Date Start Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1, a2);
				// Effective Date End Max Filter
				Subquery<Long> effectiveDate2 = query.subquery(Long.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3, a4);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4, n5);
				Predicate n9 = cb.or(n6, n7);
				Predicate n10 = cb.equal(c.get("itemType"), itemType);
				Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
				query.where(n1, n2, n3, n8, n9, n10, n11).orderBy(orderList);
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();

				itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return itemDesc;
		}

	public List<Tuple> copyQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceMotorDetails> c = query.from(EserviceMotorDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(c,
						cus.get("clientName").alias("clientName"));

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("customerReferenceNo")));

				Predicate n1 = null;
				Predicate n3 = null;
				Predicate n4 = null;
				Predicate n5 = null;

				// Where
				if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
				}

				Predicate n2 = cb.equal(c.get("companyId"), companyId);

				if ("issuer".equalsIgnoreCase(userType)) {
					n3 = cb.equal(c.get("applicationId"), loginId);
					Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
					n3 = cb.equal(c.get("loginId"), loginId);
				//	Expression<String> e0 = c.get("brokerBranchCode");
				Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				}
				
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
				query.where(n1,n2,n3,n4,n5).orderBy(orderList);
		

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				customerDetailsList = result.getResultList();
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return customerDetailsList;
		}
		
		//Endrosment
		@Transactional
		@Override
		public CopyQuoteSuccessRes motorEndt(CopyQuoteReq req, List<String> branches,String loginId) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DozerBeanMapper mapper = new DozerBeanMapper();
			try {
				String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
				String refNo=refShortCode +seqNo.generateRefNo();
				String quoteNo  = "Q"+ generateQuoteNo();
				String customerId = "C-" + generateCustId();
	            
	            //Copy Quote E service Motor Details
	            res=eserviceMotorCopyquote(req,refNo,branches,loginId,customerId,quoteNo);
	            
	            //Response Get All Motor Details
	            List<GetAllMotorDetailsRes> reslist = new ArrayList<GetAllMotorDetailsRes>();
	            List<EserviceMotorDetails> datas = repo.findByRequestReferenceNoOrderByRiskIdAsc(res.getRequestReferenceNo());
				for (EserviceMotorDetails data : datas) {
					GetAllMotorDetailsRes motorRes = new GetAllMotorDetailsRes();
					motorRes = mapper.map(data, GetAllMotorDetailsRes.class);
					motorRes.setVehicleId(data.getRiskId());
					motorRes.setQuoteNo(data.getQuoteNo()!=null?data.getQuoteNo() :"" );
					motorRes.setCustomerId(data.getCustomerId()!=null ? data.getCustomerId() : "" );
					motorRes.setPolicyTypeDesc(data.getPolicyTypeDesc());
					motorRes.setReferalRemarks(data.getReferalRemarks());	
					motorRes.setActualPremiumLc(data.getActualPremiumLc()==null ? "" : data.getActualPremiumLc().toPlainString());	
					motorRes.setActualPremiumFc(data.getActualPremiumFc()==null ? "" :data.getActualPremiumFc().toPlainString());	
					motorRes.setOverallPremiumFc(data.getOverallPremiumFc()==null ? "" :data.getOverallPremiumFc().toPlainString());	
					motorRes.setOverallPremiumLc(data.getOverallPremiumLc()==null ? "" :data.getOverallPremiumLc().toPlainString());	
					reslist.add(motorRes);
				}
				res.setMotorRes(reslist);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}

		public synchronized String generateQuoteNo() {
		       try {
		    	   SeqQuoteno entity;
		            entity = quoteNoRepo.save(new SeqQuoteno());          
		            return String.format("%05d",entity.getQuoteNo()) ;
		        } catch (Exception e) {
					e.printStackTrace();
					log.info( "Exception is ---> " + e.getMessage());
		            return null;
		        }
		       
		 }
		
		 public synchronized String generateCustId() {
		       try {
		    	   SeqCustid entity;
		            entity = custIdRepo.save(new SeqCustid());          
		            return String.format("%05d",entity.getCustId()) ;
		        } catch (Exception e) {
					e.printStackTrace();
					log.info( "Exception is ---> " + e.getMessage());
		            return null;
		        }
		       
		 }
		
		//Eservice Motor Copy Quote
		public CopyQuoteSuccessRes eserviceMotorCopyquote(CopyQuoteReq req, String refNo, List<String> branches,String loginId,String customerId,String quoteNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceMotorDetails savedata = new EserviceMotorDetails();
			try {
//				String searchValue = req.getRequestReferenceNo();
//				String searchKey = "RequestReferenceNo";
				String userType = req.getUserType();
				String branchCode = "";

				List<EserviceMotorDetails> motor=null;
				Integer count=repo.countByOriginalPolicyNoAndRiskId(req.getPolicyNo(),1);
				String prevPolicyNo=null;
				String prevQuoteNo=null;
				String newRequestNo =null;
				long pendingcount =0;
				if(count>0) {
					List<EserviceMotorDetails> motors=repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(),1);
					//Compare
					motors.sort(new Comparator<EserviceMotorDetails>() {

						@Override
						public int compare(EserviceMotorDetails o1, EserviceMotorDetails o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}
					}.reversed());
					
					pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
					if(pendingcount>0) {
						 List<EserviceMotorDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
						 motor= pendingData;
						 prevPolicyNo=motor.get(0).getEndtPrevPolicyNo();
						 prevQuoteNo=motor.get(0).getEndtPrevQuoteNo();
						 newRequestNo=motor.get(0).getRequestReferenceNo();
						 count--;
					}else {
						motor=motors;
						
						if(motors.size()>1) {
							prevPolicyNo=motors.get(1).getPolicyNo();
							prevQuoteNo =motors.get(1).getQuoteNo();
						}else {
							prevPolicyNo=req.getPolicyNo();
							prevQuoteNo =motor.get(0).getEndtPrevQuoteNo();
						}
					}
					
				}else {
					motor=repo.findByPolicyNoAndStatus(req.getPolicyNo(),"P");
					prevPolicyNo=req.getPolicyNo();
					prevQuoteNo =motor.get(0).getQuoteNo();
				}
				if(pendingcount==0)
					newRequestNo=refNo;
				
				
//				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//						branches);
				List<EserviceMotorDetails> motors=repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				
				if (motors.size() > 0) {
					for (EserviceMotorDetails data : motors) {
						EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()));
						savedata = dozerMapper.map(data, EserviceMotorDetails.class);

						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(req.getLoginId());
						savedata.setUpdatedBy(req.getLoginId());
						savedata.setUpdatedDate(new Date());
						savedata.setRequestReferenceNo(newRequestNo);
						savedata.setOldReqRefNo(req.getRequestReferenceNo());
						if (req.getUserType().equalsIgnoreCase("Broker")|| (req.getUserType().equalsIgnoreCase("User"))) {
							branchCode = req.getBranchCode();
							savedata.setApplicationId("1");
							savedata.setBrokerBranchCode(branchCode);

						} else if ("issuer".equalsIgnoreCase(userType)) {
							savedata.setApplicationId(req.getLoginId());
							branchCode = req.getBranchCode();
							savedata.setBranchCode(branchCode);
						}

						savedata.setActualPremiumFc(BigDecimal.ZERO);
						savedata.setActualPremiumLc(BigDecimal.ZERO);
						savedata.setOverallPremiumFc(BigDecimal.ZERO);
						savedata.setOverallPremiumLc(BigDecimal.ZERO);
						savedata.setQuoteNo(quoteNo);
						
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						//savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevPolicyNo(req.getPolicyNo()+"-"+count);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo()+"-"+count);
						repo.saveAndFlush(savedata);
					}
		
				}
		
				if (pendingcount == 0) {
					// Copy Quote Home Position Master
					res = homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId, prevQuoteNo, prevPolicyNo, count);

					// Copy Quote Personal Info
					res = personolInfoEndoCopyQuote(req, customerId, prevQuoteNo, prevPolicyNo, count);

					// Copy Quote Policy Cover Data
					res = policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevQuoteNo, prevPolicyNo, count);

					// Copy Quote Motor Data Details
					res = motorDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevQuoteNo,
							prevPolicyNo, count);

					// Copy Quote Motor Driver Details
					res = motorDriverDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevQuoteNo,
							prevPolicyNo, count);

					// Copy COVER_DOCUMENT_UPLOAD_DETAILS
					res = coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevQuoteNo,
							prevPolicyNo, count);
				
				}
				res.setRequestReferenceNo(newRequestNo);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
		//Home Position Master Endt Copy Quote
		public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
		String prevQuoteNo,Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			HomePositionMaster savedata = new HomePositionMaster();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			
		try {
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()));
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			savedata = dozerMapper.map(homeData, HomePositionMaster.class);
			savedata.setRequestReferenceNo(refNo);
			savedata.setCustomerId(customerId);
			savedata.setQuoteNo(quoteNo);
			savedata.setEndtTypeId(req.getEndtTypeId());
			savedata.setEndtDate(new Date());
			savedata.setEndtBy(loginId);
			savedata.setEndtStatus("P");
			savedata.setEndtPremium(BigDecimal.ZERO);
			savedata.setEndtCommission(BigDecimal.valueOf(0));
		//	savedata.setOriginalPolicyNo(homeData.getPolicyNo());
			savedata.setQuoteCreatedDate(new Date());
			savedata.setEntryDate(new Date());
			
			savedata.setRequestReferenceNo(refNo);
			savedata.setOriginalPolicyNo(req.getPolicyNo());
			savedata.setEndorsementRemarks(req.getEndtRemarks());
			savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
			savedata.setEndtPrevPolicyNo(prevPolicyNo);
			savedata.setEndtPrevQuoteNo(prevQuoteNo);
			savedata.setEndtCount(count);
			savedata.setEndtStatus("P");
			savedata.setIsFinacialEndt("N");
			savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
			savedata.setEndtTypeDesc(entMaster.getEndtTypeDesc());
			savedata.setStatus("E");
			savedata.setPolicyNo(req.getPolicyNo()+"-"+count);

			homePosistionRepo.saveAndFlush(savedata);
		
			System.out.println("QUOTE NO:"+quoteNo);
			System.out.println("Customer Id:"+customerId);
			System.out.println("Reference No:"+refNo);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
		}
		
		//Personal Info Endt Copy Quote
		public CopyQuoteSuccessRes personolInfoEndoCopyQuote(CopyQuoteReq req,String customerId,String prevPolicyNo,
				String prevQuoteNo,Integer count) {
			CopyQuoteSuccessRes res =new CopyQuoteSuccessRes();
			PersonalInfo savedata = new PersonalInfo();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()));
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				String olsCustomerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
				savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
				savedata.setCustomerId(customerId);
				savedata.setEntryDate(new Date());
				savedata.setCreatedBy(req.getLoginId());
				savedata.setUpdatedBy(req.getLoginId());
				savedata.setUpdatedDate(new Date());
				
				//Endo
				savedata.setOriginalPolicyNo(req.getPolicyNo());
				savedata.setEndorsementDate(new Date());
				savedata.setEndorsementRemarks(req.getEndtRemarks());
				savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
				savedata.setEndtPrevPolicyNo(prevPolicyNo);
				savedata.setEndtPrevQuoteNo(prevQuoteNo);
				savedata.setEndtCount(new BigDecimal(count));
				savedata.setEndtStatus("P");
				savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
				savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
				savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
				savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				savedata.setStatus("E");
				personalInforepo.saveAndFlush(savedata);
	
				//res.setSuccessId(customerId);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}
		//Policy Cover Data Enst Copy Quote
		public CopyQuoteSuccessRes policyCoverDataEndocopyQuote(CopyQuoteReq req,String refNo,String quoteNo,String loginId,String prevPolicyNo,
				String prevQuoteNo,Integer count) {
			CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
			PolicyCoverData savedata = new PolicyCoverData();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				List<PolicyCoverData> policyCoverData=policyCoverDataRepo.findByQuoteNo(req.getQuoteNo());
				if (policyCoverData.size() > 0) {
					for (PolicyCoverData data : policyCoverData) {
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setStatus("E");
						policyCoverDataRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
		//Motor Data Details Enst Copy Quote
		public CopyQuoteSuccessRes motorDataDetailsEndoCopyquote(CopyQuoteReq req,String refNo,String quoteNo,String customerId,String loginId,String prevPolicyNo,
				String prevQuoteNo,Integer count ) {
			CopyQuoteSuccessRes res=new CopyQuoteSuccessRes();
			MotorDataDetails savedata = new MotorDataDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()));
				List<MotorDataDetails> motorData=motorDataDetepo.findByQuoteNo(req.getQuoteNo());
				if (motorData.size() > 0) {
					for (MotorDataDetails data : motorData) {
						savedata = dozerMapper.map(data, MotorDataDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo()+"-"+count);
						motorDataDetepo.saveAndFlush(savedata);
					}
				}
		
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

		// Motor Data Details Enst Copy Quote
		public CopyQuoteSuccessRes motorDriverDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
				String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			MotorDriverDetails savedata = new MotorDriverDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()));
				List<MotorDriverDetails> motorDriverData = motordrivDetepo.findByQuoteNo(req.getQuoteNo());
				if (motorDriverData.size() > 0) {
					for (MotorDriverDetails data : motorDriverData) {
						savedata = dozerMapper.map(data, MotorDriverDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
//						savedata.setUpdatedBy(loginId);
//						savedata.setUpdatedDate(new Date());

						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						motordrivDetepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

		// Motor Data Details Enst Copy Quote
		public CopyQuoteSuccessRes coverDocumentUploadDetailsEndoCopyquote(CopyQuoteReq req, String refNo,
				String quoteNo, String customerId, String loginId, String prevPolicyNo, String prevQuoteNo,
				Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			CoverDocumentUploadDetails savedata = new CoverDocumentUploadDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeId(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()));
				List<CoverDocumentUploadDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getQuoteNo());
				if (motorData.size() > 0) {
					for (CoverDocumentUploadDetails data : motorData) {
						savedata = dozerMapper.map(data, CoverDocumentUploadDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
//						savedata.setUpdatedBy(loginId);
//						savedata.setUpdatedDate(new Date());

						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						coverDocUploadDetails.saveAndFlush(savedata);
					}
				}
			
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

		// Validaton
		@Override
		public List<Tuple> validateMotorEndt(String quoteNo) {
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> c = query.from(HomePositionMaster.class);
				// Customer Id
				Subquery<Long> cusId = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = cusId.from(HomePositionMaster.class);
				cusId.select(ocpm1.get("customerId"));
				Predicate a1 = cb.equal(ocpm1.get("quoteNo"), quoteNo);
				cusId.where(a1);

				// PersonalInfo Count SubQuery
				Subquery<Long> perInfo = query.subquery(Long.class);
				Root<PersonalInfo> e = perInfo.from(PersonalInfo.class);
				perInfo.select(cb.count(e));
				Predicate n2 = cb.equal(e.get("customerId"), c.get("customerId"));
				perInfo.where(n2);

				// Policy Cover Data Count SubQuery
				Subquery<Long> policyCover = query.subquery(Long.class);
				Root<PolicyCoverData> q = policyCover.from(PolicyCoverData.class);
				policyCover.select(cb.count(q));
				Predicate n3 = cb.equal(q.get("quoteNo"), quoteNo);
				policyCover.where(n3);

				// Motor Data Details Count SubQuery
				Subquery<Long> motorData = query.subquery(Long.class);
				Root<MotorDataDetails> m = motorData.from(MotorDataDetails.class);
				motorData.select(cb.count(m));
				Predicate n4 = cb.equal(m.get("quoteNo"), quoteNo);
				motorData.where(n4);

				query.multiselect(cb.count(c).alias("homeCount"),c.get("policyNo").alias("policyNo"),
						c.get("status").alias("status"),perInfo.alias("perCount"),
						policyCover.alias("policyCount"), motorData.alias("motorCount"));

				// Where
				Predicate n1 = cb.equal(c.get("quoteNo"), quoteNo);
				query.where(n1);

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is --->" + e.getMessage());
					return null;
				}
				return list;
			}
		
		//CopyQuote Dropdown 
		@Override
		public List<ListItemValue> geMotorCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType) {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				today = cal.getTime();
				Date todayEnd = cal.getTime();

				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);

				// Select
				query.select(c);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));

				// Effective Date Start Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1, a2);
				// Effective Date End Max Filter
				Subquery<Long> effectiveDate2 = query.subquery(Long.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3, a4);

				// Where
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
				Predicate n5 = cb.equal(c.get("companyId"), "99999");
				Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n8 = cb.or(n4, n5);
				Predicate n9 = cb.or(n6, n7);
				Predicate n10 = cb.equal(c.get("itemType"), itemType);
				query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();

				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode())))
						.collect(Collectors.toList());
				list.sort(Comparator.comparing(ListItemValue::getItemValue));
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return list;
		}

		private static <T> java.util.function.Predicate<T> distinctByKey(
				java.util.function.Function<? super T, ?> keyExtractor) {
			Map<Object, Boolean> seen = new ConcurrentHashMap<>();
			return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}

//		@Override
//		public List<ReferalGridCriteriaRes> getMotorProtfolioActive(ExistingQuoteReq req, List<String> branches,
//				int limit, int offset, String string) {
//			// TODO Auto-generated method stub
//			return null;
//		}

		@Override
		public synchronized List<PortfolioGridCriteriaRes> getMotorProtfolioActive(ExistingQuoteReq req, List<String> branches,
				Date startDate,int limit, int offset, String status) {
			List<PortfolioGridCriteriaRes> portfolio = new ArrayList<PortfolioGridCriteriaRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioGridCriteriaRes> query = cb.createQuery(PortfolioGridCriteriaRes.class);

				// Find All
				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<PersonalInfo> c = query.from(PersonalInfo.class);

				// Select
				query.multiselect(
						cb.count(m).as(Long.class).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("mobileNo1").alias("mobileNo1"),
						c.get("isTaxExempted").alias("isTaxExempted"),
						c.get("taxExemptedId").alias("taxExemptedId"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), 
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), 
						m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("inceptionDate").alias("inceptionDate"),
						m.get("expiryDate").alias("expiryDate"),
						m.get("overallPremiumLc").alias("overallPremiumLc"),
						m.get("overallPremiumFc").alias("overallPremiumFc"),
						m.get("policyNo").alias("policyNo"),
						m.get("debitAcNo").alias("debitAcNo"),
						m.get("debitTo").alias("debitTo"),
						m.get("debitToId").alias("debitToId"),
						m.get("debitNoteNo").alias("debitNoteNo"),
						m.get("debitNoteDate").alias("debitNoteDate"),
						m.get("creditTo").alias("creditTo"),
						m.get("creditToId").alias("creditToId"),
						m.get("creditNo").alias("creditNo"),
						m.get("creditDate").alias("creditDate"),
						m.get("emiYn").alias("emiYn"),
						m.get("installmentPeriod").alias("installmentPeriod"),
						m.get("effectiveDate").alias("effectiveDate"),
						m.get("currency").alias("currency")

						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.isNull(m.get("endtTypeId"));

				Predicate n5 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}
				Predicate n6 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
				}

				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10)
				.groupBy(
						c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
						m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
						m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
						m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
						m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),m.get("effectiveDate"),m.get("currency")
						)
						.orderBy(orderList);

				// Get Result
				TypedQuery<PortfolioGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return portfolio;
		}

		@Override
		public List<PortfolioGridCriteriaRes> getMotorProtfolioPending(ExistingQuoteReq req, List<String> branches,
				Date startDate, int limit, int offset, String status) {
			List<PortfolioGridCriteriaRes> portfolio = new ArrayList<PortfolioGridCriteriaRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioGridCriteriaRes> query = cb.createQuery(PortfolioGridCriteriaRes.class);

				// Find All
				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<PersonalInfo> c = query.from(PersonalInfo.class);
				

				// Select
				query.multiselect(
						cb.count(m).as(Long.class).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("mobileNo1").alias("mobileNo1"),
						c.get("isTaxExempted").alias("isTaxExempted"),
						c.get("taxExemptedId").alias("taxExemptedId"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), 
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), 
						m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("inceptionDate").alias("inceptionDate"),
						m.get("expiryDate").alias("expiryDate"),
						m.get("overallPremiumLc").alias("overallPremiumLc"),
						m.get("overallPremiumFc").alias("overallPremiumFc"),
						m.get("policyNo").alias("policyNo"),
						m.get("debitAcNo").alias("debitAcNo"),
						m.get("debitTo").alias("debitTo"),
						m.get("debitToId").alias("debitToId"),
						m.get("debitNoteNo").alias("debitNoteNo"),
						m.get("debitNoteDate").alias("debitNoteDate"),
						m.get("creditTo").alias("creditTo"),
						m.get("creditToId").alias("creditToId"),
						m.get("creditNo").alias("creditNo"),
						m.get("creditDate").alias("creditDate"),
						m.get("emiYn").alias("emiYn"),
						m.get("installmentPeriod").alias("installmentPeriod"),
						m.get("effectiveDate").alias("effectiveDate"),
						m.get("currency").alias("currency")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n9 = cb.notEqual(m.get("integrationStatus"),"S");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.isNull(m.get("endtTypeId"));

				Predicate n5 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}
				Predicate n6 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
				}

				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10)
				.groupBy(
						c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
						m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
						m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
						m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
						m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),m.get("effectiveDate")
						)
						.orderBy(orderList);

				// Get Result
				TypedQuery<PortfolioGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return portfolio;
		}

		@Override
		public List<PortfolioGridCriteriaRes> getMotorPortfolioCancelled(ExistingQuoteReq req, List<String> branches,
				Date startDate,int limit, int offset, String status) {
			List<PortfolioGridCriteriaRes> portfolio = new ArrayList<PortfolioGridCriteriaRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioGridCriteriaRes> query = cb.createQuery(PortfolioGridCriteriaRes.class);

				// Find All
				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<PersonalInfo> c = query.from(PersonalInfo.class);
				

				// Select
				query.multiselect(
						cb.count(m).as(Long.class).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("mobileNo1").alias("mobileNo1"),
						c.get("isTaxExempted").alias("isTaxExempted"),
						c.get("taxExemptedId").alias("taxExemptedId"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), 
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), 
						m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("inceptionDate").alias("inceptionDate"),
						m.get("expiryDate").alias("expiryDate"),
						m.get("overallPremiumLc").alias("overallPremiumLc"),
						m.get("overallPremiumFc").alias("overallPremiumFc"),
						m.get("policyNo").alias("policyNo"),
						m.get("debitAcNo").alias("debitAcNo"),
						m.get("debitTo").alias("debitTo"),
						m.get("debitToId").alias("debitToId"),
						m.get("debitNoteNo").alias("debitNoteNo"),
						m.get("debitNoteDate").alias("debitNoteDate"),
						m.get("creditTo").alias("creditTo"),
						m.get("creditToId").alias("creditToId"),
						m.get("creditNo").alias("creditNo"),
						m.get("creditDate").alias("creditDate"),
						m.get("emiYn").alias("emiYn"),
						m.get("installmentPeriod").alias("installmentPeriod"),
						m.get("effectiveDate").alias("effectiveDate"),
						m.get("currency").alias("currency")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.isNull(m.get("endtTypeId"));

				Predicate n5 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}
				Predicate n6 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
				}

				query.where(n1,n2, n3, n4, n5, n6,n7,n8,n10)
						.groupBy(
								c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
								m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
								m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
								m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),m.get("effectiveDate")
								)
						.orderBy(orderList);

				// Get Result
				TypedQuery<PortfolioGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return portfolio;
		}

		@Override
		public List<Tuple> getMotorIssuerQuoteDetails(IssuerQuoteReq req, Date startDate, Date endDate) {
			List<Tuple> unionAll = new ArrayList<Tuple>();
			try {
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				// Find All
				Root<LoginUserInfo> c = query.from(LoginUserInfo.class);
				List<Tuple> result1 = new ArrayList<Tuple>();
				Subquery<Long> loginId = query.subquery(Long.class);
				Root<EserviceMotorDetails> ocpm1 = loginId.from(EserviceMotorDetails.class);
				loginId.select(ocpm1.get("loginId"));
				Predicate a1 = cb.equal(ocpm1.get("applicationId"),req.getApplicationId());
				Predicate a2 = cb.equal(ocpm1.get("branchCode"),req.getBranchCode());
				Predicate a3 = cb.equal(ocpm1.get("status"),req.getStatus());
				Predicate a4 = ocpm1.get("bdmCode").isNull();
				loginId.where(a1,a2,a3,a4);
				
				// Select
				query.multiselect(cb.count(c).alias("idsCount"),
						c.get("userName").alias("userName"), c.get("loginId").alias("loginId"),
						c.get("agencyCode").alias("agencyCode")
				);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(c.get("userName")));
				
				// Where
				Predicate n1 = cb.equal(c.get("loginId"), loginId);
				query.where(n1).orderBy(orderList);

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				result1 = result.getResultList();
				
				// Result 2 Query
				Root<PremiaCustomerDetails> p  =  query.from(PremiaCustomerDetails.class);
				Root<EserviceMotorDetails> e = query.from(EserviceMotorDetails.class);

				Predicate p1 = cb.equal(p.get("customerType"), "009");
				Predicate p2 = e.get("bdmCode").isNotNull();
				Predicate p3 = cb.equal(p.get("status"),req.getStatus());
				Predicate p4 = cb.equal(e.get("bdmCode"),p.get("customerCode"));
				Predicate p5 = cb.equal(e.get("applicationId"),req.getApplicationId());
				Predicate p6 = cb.equal(e.get("branchCode"),req.getBranchCode());

				query.multiselect( p.get("customerName").alias("customerUserName"),
						p.get("customerName").alias("customerLoginName"),
						e.get("bdmCode").alias("agencyCode")) ;	
				
				query.where(p1,p2,p3,p4,p5,p6);
				TypedQuery<Tuple> res2 = em.createQuery(query);
				List<Tuple> result2 = res2.getResultList();

				// Union All (Combining Two Result )
				 result1.addAll(result2);			
				 unionAll = result1 ;
				

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return unionAll;
		}
		
		public String generateRequestNo(String companyId,String branchcode,String productId) {
			String refShortCode = getListItem(companyId, branchcode, "PRODUCT_SHORT_CODE",productId);
			String refNo = refShortCode + seqNo.generateRefNo();
			return refNo;
		} 

}

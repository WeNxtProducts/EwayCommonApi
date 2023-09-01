package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.MotorGridCriteriaAdminRes;
import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioAdminSearchRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.res.AdminPendingGridListRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.res.PortFolioSearchRes;
import com.maan.eway.common.res.PortfolioAdminGridRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.PortfolioSearchDataRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.CoverMasterRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;


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
	private SeqCustrefnoRepository custRefRepo  ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private MotorDriverDetailsRepository motordrivDetepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private CoverMasterRepository coverMasterRepo;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;

	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	 @Autowired
	 private DataSource dataSource;
	 
	 private boolean isOracle;
	 private boolean isMySQL;
	 
	 
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
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), 
					cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
					m.get("currency").alias("currency")
					);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

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
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocpm1 = riskId.from(EserviceMotorDetails.class);
			riskId.select(cb.max(ocpm1.get("riskId")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			
			riskId.where(a1);
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),m.get("updatedDate"),	m.get("currency"))
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
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), 
					cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
					m.get("currency").alias("currency"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

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
			Predicate n8 = cb.isNull(m.get("endorsementType"));
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocpm1 = riskId.from(EserviceMotorDetails.class);
			riskId.select(cb.max(ocpm1.get("riskId")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			
			riskId.where(a1);
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			
			query.where(n1, n2, n3, n4, n5, n6, n7,n8,n10)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),m.get("updatedDate"),	m.get("currency"))
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
					m.get("rejectReason").alias("rejectReason"),
					cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), 
					cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
					m.get("currency").alias("currency")); 

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

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
			Predicate n7 = cb.isNull(m.get("endorsementType"));
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocpm1 = riskId.from(EserviceMotorDetails.class);
			riskId.select(cb.max(ocpm1.get("riskId")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			
			riskId.where(a1);
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			
			query.where(n1, n2, n3, n4, n5, n6,n7,n10)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("updatedDate"),	m.get("currency"))
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
					cb.max(m.get("policyStartDate")).alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					cb.max(m.get("rejectReason")).alias("rejectReason"),
					cb.max(m.get("adminRemarks")).alias("adminRemarks"),
					cb.max(m.get("endorsementType")).alias("endorsementType"),
					cb.max(m.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(m.get("endorsementDate")).alias("endorsementDate"),
					cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
					cb.max(m.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(m.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(m.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(m.get("endtCount")).alias("endtCount"),
					cb.max(m.get("endtStatus")).alias("endtStatus"),
					cb.max(m.get("endtCategDesc")).alias("endtCategDesc"),
					//cb.max(m.get("endorsementYn")).alias("endorsementYn"),
					cb.max(m.get("endtPremium")).alias("endtPremium")
					
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

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
		//	Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1, n2, n3, n4, n5, n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate"))
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
				res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
				res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
				res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
				res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
				res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());
//				res.setEndorsementYn(r.get("endorsementYn")==null ? "" : r.get("endorsementYn").toString());
				res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
				res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
				res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
				res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
				res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
				res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
				res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
				
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
					m.get("adminRemarks").alias("adminRemarks"),
					cb.max(m.get("endorsementType")).alias("endorsementType"),
					cb.max(m.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(m.get("endorsementDate")).alias("endorsementDate"),
					cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
					cb.max(m.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(m.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(m.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(m.get("endtCount")).alias("endtCount"),
					cb.max(m.get("endtStatus")).alias("endtStatus"),
					cb.max(m.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(m.get("endtPremium")).alias("endtPremium")
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			//Predicate n4 = cb.equal(m.get("status"),status);
			Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));

			Expression<String> e0 = c.get("branchCode");
			Predicate n6 = e0.in(branches);
		//	Predicate n7 = cb.isNull(m.get("endorsementType"));
			
			// Uw Condition 
			if("RP".equalsIgnoreCase(status)) {
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("status"), "Y"); 
				
				query.where(n1, n2, n3, n4, n6,n8,n9,n10)
						.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
								m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
								)
						.orderBy(orderList);
			} else {
				query.where(n1, n2, n3, n4, n6)
						.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
								m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
								)
						.orderBy(orderList);
			}
			

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
				res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
				res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
				res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
				res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
				res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());
//				res.setEndorsementYn(r.get("endorsementYn")==null ? "" : r.get("endorsementYn").toString());
				res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
				res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
				res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
				res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
				res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
				res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
				res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
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
				} else if ("PolicyNo".equalsIgnoreCase(searchKey)) {
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
				
				query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),
						
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
						cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"), 
						cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("branchCode")).alias("branchCode"),
						cb.max(c.get("riskId")).alias("riskId"),
						cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
								.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

						cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
								.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
						cb.max(c.get("insuranceType")).alias("insuranceType"),
						cb.max(c.get("accident")).alias("accident"), 
						cb.max(c.get("gpsTrackingInstalled")).alias("gpsTrackingInstalled"),
						cb.max(c.get("manufactureYear")).alias("manufactureYear"),
						
						cb.max(c.get("insuranceTypeDesc")).alias("insuranceTypeDesc"),
						cb.max(c.get("policyNo")).alias("policyNo"),
						cb.max(c.get("motorCategory")).alias("motorCategory"),
						cb.max(c.get("motorCategoryDesc")).alias("motorCategoryDesc"), 
						cb.max(c.get("motorUsage")).alias("motorUsage"),
						cb.max(c.get("registrationNumber")).alias("registrationNumber"),
						cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
						cb.max(c.get("chassisNumber")).alias("chassisNumber"),
						cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
						cb.max(c.get("vehicleMake")).alias("vehicleMake"),
						cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
						cb.max(c.get("vehicleMakeDesc")).alias("vehicleMakeDesc"),
						cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.max(c.get("vehcileModel")).alias("vehcileModel"),
						cb.max(c.get("vehcileModelDesc")).alias("vehcileModelDesc"),
						cb.max(c.get("vehicleType")).alias("vehicleType"),
						cb.max(c.get("vehicleTypeDesc")).alias("vehicleTypeDesc"), 
						cb.max(c.get("modelNumber")).alias("modelNumber"),
						cb.max(c.get("engineNumber")).alias("engineNumber"),
						cb.max(c.get("fuelType")).alias("fuelType"), 
						cb.max(c.get("fuelTypeDesc")).alias("fuelTypeDesc"),
						cb.max(c.get("overridePercentage")).alias("overridePercentage"),
						cb.max(c.get("registrationYear")).alias("registrationYear"), 
						cb.max(c.get("seatingCapacity")).alias("seatingCapacity"),
						cb.max(c.get("cubicCapacity")).alias("cubicCapacity"),
						cb.max(c.get("color")).alias("color"),
						cb.max(c.get("colorDesc")).alias("colorDesc"),
						cb.max(c.get("grossWeight")).alias("grossWeight"),
						cb.max(c.get("tareWeight")).alias("tareWeight"),
						cb.max(c.get("covernoteNo")).alias("covernoteNo"),
						cb.max(c.get("stickerNo")).alias("stickerNo"),
						cb.max(c.get("periodOfInsurance")).alias("periodOfInsurance"),
						cb.max(c.get("windScreenSumInsured")).alias("windScreenSumInsured"),
						cb.max(c.get("acccessoriesSumInsured")).alias("acccessoriesSumInsured"),
						cb.max(c.get("numberOfAxels")).alias("numberOfAxels"),
						cb.max(c.get("axelDistance")).alias("axelDistance"),
						cb.max(c.get("sumInsured")).alias("sumInsured"),
						cb.max(c.get("endorsementType")).alias("endorsementType"),
						cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
						cb.max(c.get("tppdFreeLimit")).alias("tppdFreeLimit"),
						cb.max(c.get("tppdIncreaeLimit")).alias("tppdIncreaeLimit"),
						cb.max(c.get("specialTermsOfPremium")).alias("specialTermsOfPremium"),
						cb.max(c.get("agencyCode")).alias("agencyCode"),
						cb.max(c.get("insuranceClass")).alias("insuranceClass"),
						cb.max(c.get("sectionId")).alias("sectionId"),
						cb.max(c.get("sectionName")).alias("sectionName"),
						cb.max(c.get("productName")).alias("productName"),
						cb.max(c.get("productId")).alias("productId"),
						cb.max(c.get("insuranceClassDesc")).alias("insuranceClassDesc"),
						cb.max(c.get("ownerCategory")).alias("ownerCategory"),
						cb.max(c.get("companyId")).alias("companyId"),
						cb.max(c.get("companyName")).alias("companyName"),
						cb.max(c.get("manufactureAge")).alias("manufactureAge"),
						cb.max(c.get("insurerSettlement")).alias("insurerSettlement"),
						cb.max(c.get("registrationAge")).alias("registrationAge"),
						cb.max(c.get("ncdYears")).alias("ncdYears"),
						cb.max(c.get("ncdYn")).alias("ncdYn"),
						cb.max(c.get("policyType")).alias("policyType"),
						cb.max(c.get("policyTypeDesc")).alias("policyTypeDesc"),
						cb.max(c.get("radioorcasseteplayer")).alias("radioorcasseteplayer"),
						cb.max(c.get("status")).alias("status"),
						cb.max(c.get("roofRack")).alias("roofRack"),
						cb.max(c.get("entryDate")).alias("entryDate"),
						cb.max(c.get("createdBy")).alias("createdBy"), 
						cb.max(c.get("trailerDetails")).alias("trailerDetails"),
						cb.max(c.get("updatedDate")).alias("updatedDate"),
						cb.max(c.get("updatedBy")).alias("updatedBy"),
						cb.max(c.get("drivenBy")).alias("drivenBy"),
						cb.max(c.get("policyStartDate")).alias("policyStartDate"),
						
						cb.max(c.get("drivenByDesc")).alias("drivenByDesc"),
						cb.max(c.get("policyEndDate")).alias("policyEndDate"),
						cb.max(c.get("currency")).alias("currency"),
						cb.max(c.get("defectiveVisionOrHearing")).alias("defectiveVisionOrHearing"),
						cb.max(c.get("exchangeRate")).alias("exchangeRate"),
						cb.max(c.get("motoringOffence")).alias("motoringOffence"),
						cb.max(c.get("fleetOwnerYn")).alias("fleetOwnerYn"),
						cb.max(c.get("suspensionOfLicense")).alias("suspensionOfLicense"),
						cb.max(c.get("noOfVehicles")).alias("noOfVehicles"),
						cb.max(c.get("irrespectiveOfBlame")).alias("irrespectiveOfBlame"),
						cb.max(c.get("noOfCompehensives")).alias("noOfCompehensives"),
						cb.max(c.get("vehicleInterestedCompany")).alias("vehicleInterestedCompany"),
						cb.max(c.get("claimRatio")).alias("claimRatio"), 
						cb.max(c.get("interestedCompanyDetails")).alias("interestedCompanyDetails"),
						cb.max(c.get("collateralYn")).alias("collateralYn"),
						cb.max(c.get("borrowerType")).alias("borrowerType"),
						cb.max(c.get("otherVehicle")).alias("otherVehicle"),
						cb.max(c.get("borrowerTypeDesc")).alias("borrowerTypeDesc"),

						cb.max(c.get("otherVehicleDetails")).alias("otherVehicleDetails"),
						cb.max(c.get("collateralName")).alias("collateralName"),
						cb.max(c.get("otherInsurance")).alias("otherInsurance"),
						cb.max(c.get("firstLossPayee")).alias("firstLossPayee"),
						cb.max(c.get("otherInsuranceDetails")).alias("otherInsuranceDetails"),
						cb.max(c.get("holdInsurancePolicy")).alias("holdInsurancePolicy"),
						cb.max(c.get("noOfClaims")).alias("noOfClaims"),
						cb.max(c.get("cityLimit")).alias("cityLimit"),
						cb.max(c.get("additionalCircumstances")).alias("additionalCircumstances"),
						cb.max(c.get("savedFrom")).alias("savedFrom"),
						cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
						cb.max(c.get("applicationId")).alias("applicationId"), 
						cb.max(c.get("brokerCode")).alias("brokerCode"),
						cb.max(c.get("subUserType")).alias("subUserType"),
						cb.max(c.get("loginId")).alias("loginId"),
						cb.max(c.get("adminLoginId")).alias("adminLoginId"),
						cb.max(c.get("adminRemarks")).alias("adminRemarks"),
						cb.max(c.get("referalRemarks")).alias("referalRemarks"),
						cb.max(c.get("bdmCode")).alias("bdmCode"),
						cb.max(c.get("sourceType")).alias("sourceType"),
						cb.max(c.get("customerCode")).alias("customerCode"),
						cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
						cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
						
						cb.max(c.get("commissionType")).alias("commissionType"),
						cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
						cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
						cb.max(c.get("havepromocode")).alias("havepromocode"),
						cb.max(c.get("promocode")).alias("promocode"),
						cb.max(c.get("driverYn")).alias("driverYn"),
						cb.max(c.get("bankCode")).alias("bankCode"),
						cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
						cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
						
						cb.max(c.get("endorsementDate")).alias("endorsementDate"),
						cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
						cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
						cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
						cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
						cb.max(c.get("endtStatus")).alias("endtStatus"),
						cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
						cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
						cb.max(c.get("endtPremium")).alias("endtPremium"),

						cb.max(c.get("endtCount")).alias("endtCount"),
						cb.max(c.get("endorsementYn")).alias("endorsementYn")

				);
						

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
					n1 = cb.equal(c.get("quoteNo"), searchValue);
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
				} else if (searchKey.equalsIgnoreCase("PolicyNo")) {
					n1 = cb.like(cb.lower(c.get("policyNo")), searchValue );
				
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
			//	Predicate n6 = cb.isNull(c.get("endtTypeId"));
				query.where(n1,n2,n3,n4,n5)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
							c.get("rejectReason")/*,c.get("riskId"),c.get("insuranceType")*/)
				.orderBy(orderList);
				if (searchKey.equalsIgnoreCase("ClientName")) {
					query.where(n1, n2,n4,n5)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
							c.get("rejectReason")/*,c.get("riskId"),c.get("insuranceType")*/)
					.orderBy(orderList);
				}
				if (searchKey.equalsIgnoreCase("EntryDate")) {
					query.where(n1,n2,n3,n4)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), cus.get("clientName"), c.get("companyId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),
							c.get("rejectReason")/*,c.get("riskId"),c.get("insuranceType")*/)
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
				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
				String refNo = req.getRequestReferenceNo();
				if (list.size() > 0) {
					String refShortCode = getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",
							req.getProductId());
					refNo = refShortCode +"-"+ seqNo.generateRefNo();
					for (Tuple data : list) {

						savedata = dozerMapper.map(data.get(0), EserviceMotorDetails.class);

						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(req.getLoginId());
						savedata.setUpdatedBy(req.getLoginId());
						savedata.setUpdatedDate(new Date());
						savedata.setRequestReferenceNo(refNo);
						savedata.setOldReqRefNo(req.getRequestReferenceNo());
						if (req.getUserType().equalsIgnoreCase("Broker")
								|| (req.getUserType().equalsIgnoreCase("User"))) {
							branchCode = req.getBranchCode();
							savedata.setApplicationId("1");
						//	savedata.setBrokerBranchCode(branchCode);

						} else if ("issuer".equalsIgnoreCase(userType)) {
							savedata.setApplicationId(req.getLoginId());
							branchCode = req.getBranchCode();
						//	savedata.setBranchCode(branchCode);
						}
						savedata.setPolicyStartDate(null);		
						savedata.setPolicyEndDate(null);
						savedata.setActualPremiumFc(BigDecimal.ZERO);
						savedata.setActualPremiumLc(BigDecimal.ZERO);
						savedata.setOverallPremiumFc(BigDecimal.ZERO);
						savedata.setOverallPremiumLc(BigDecimal.ZERO);
						savedata.setQuoteNo("");
						savedata.setStatus("Y");
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
						repo.saveAndFlush(savedata);
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
//				Predicate n3 = null;
//				Predicate n4 = null;
				Predicate n5 = null;

				// Where
				if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
				}

//				Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//				if ("issuer".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("applicationId"), loginId);
//					Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("loginId"), loginId);
//				Expression<String> e0 = c.get("brokerBranchCode");
//			//	Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				}
//				
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
//				query.where(n1,n2,n3,n5).orderBy(orderList);
				query.where(n1,n5).orderBy(orderList);

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
		public  CopyQuoteSuccessRes motorEndt(CopyQuoteReq req, List<String> branches,String loginId) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DozerBeanMapper mapper = new DozerBeanMapper();
			try {
				String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
				String refNo=refShortCode +"-" +seqNo.generateRefNo();
				String quoteNo  = "Q"+ generateQuoteNo();
				String customerId = "C-" + generateCustId();
				String custRefNo = "Cust-" +   generateCustRefNo() ; 
	            //Copy Quote E service Motor Details
				EserviceMotorDetails savedata=eserviceMotorCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
				res.setCommonResponse(savedata);
				res.setQuoteNo(quoteNo);
			
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
		public EserviceMotorDetails eserviceMotorCopyquote(CopyQuoteReq req, String refNo, List<String> branches,String loginId,String customerId,String quoteNo,String custRefNo) {
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceMotorDetails savedata = new EserviceMotorDetails();
			try {
				String userType = req.getUserType();
				String branchCode = "";

				List<EserviceMotorDetails> motor=null;
				Integer count=0;
				List<Object> list=getMasterTableCount(req.getPolicyNo());
				if (list.size() > 0) {
					count = list.size();
				}
				String prevPolicyNo=null; 
				String prevQuoteNo=null;
				String newRequestNo =null;
				String newQuoteNo =null;
				String newCustRefNo=null;
				String newCustId=null;
				long pendingcount =0;
				if (count > 0) {
					List<EserviceMotorDetails> motors = repo.findByOriginalPolicyNo(req.getPolicyNo());
					pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
					if (pendingcount > 0) {
						 List<EserviceMotorDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
						if (!pendingData.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
							deletePreviousEndo(req,pendingData);
						
							count--;
							pendingcount=0;
						}
					}
				}
				if(count>0) {
					List<EserviceMotorDetails> motors=repo.findByOriginalPolicyNo(req.getPolicyNo());
					//motors=motors.stream().filter(distinctByKey(m ->m.getPolicyNo())).collect(Collectors.toList());
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
						 newQuoteNo=motor.get(0).getQuoteNo();
						 newCustRefNo=motor.get(0).getCustomerReferenceNo();
						 newCustId=motor.get(0).getCustomerId();
						 count--;
					}else {
						motor=motors;
						
						if(motors.size()>1) {
							prevPolicyNo=motors.get(0).getPolicyNo();
							prevQuoteNo =motors.get(0).getQuoteNo();
						}else {
//						
//							prevPolicyNo =motor.get(0).getEndtPrevQuoteNo();
//							prevQuoteNo = motor.get(0).getQuoteNo();
							prevPolicyNo =motor.get(0).getPolicyNo();
							prevQuoteNo = motor.get(0).getQuoteNo();
						}
					}
					
				}else {
					motor=repo.findByPolicyNoAndStatus(req.getPolicyNo(),"P");
					prevPolicyNo=req.getPolicyNo();
					prevQuoteNo =motor.get(0).getQuoteNo();
				}
				
				if(pendingcount==0) 
				{
					newRequestNo=refNo;
					newCustRefNo=custRefNo;
					newCustId=customerId;
				}	
				
//				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//						branches);
				List<EserviceMotorDetails> motors=repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				++count;
				if (motors.size() > 0) {
					for (EserviceMotorDetails data : motors) {
						EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
								/*
								 * endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());
								 */
						savedata = dozerMapper.map(data, EserviceMotorDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(req.getLoginId());
						savedata.setUpdatedBy(req.getLoginId());
						savedata.setUpdatedDate(new Date());
						savedata.setRequestReferenceNo(newRequestNo);
						//savedata.setCustomerReferenceNo(newCustRefNo);
						savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
						savedata.setCustomerId(newCustId);
						savedata.setOldReqRefNo(req.getRequestReferenceNo());
//						if (req.getUserType().equalsIgnoreCase("Broker")|| (req.getUserType().equalsIgnoreCase("User"))) {
//							branchCode = req.getBranchCode();
//							savedata.setApplicationId("1");
//							savedata.setBrokerBranchCode(branchCode);
//
//						} else if ("issuer".equalsIgnoreCase(userType)) {
//							savedata.setApplicationId(req.getLoginId());
//							branchCode = req.getBranchCode();
//							savedata.setBranchCode(branchCode);
//						}
						savedata.setApplicationId(data.getApplicationId());
						savedata.setBrokerBranchCode(data.getBrokerBranchCode());
						savedata.setActualPremiumFc(BigDecimal.ZERO);
						savedata.setActualPremiumLc(BigDecimal.ZERO);
						savedata.setOverallPremiumFc(BigDecimal.ZERO);
						savedata.setOverallPremiumLc(BigDecimal.ZERO);
						if(pendingcount==0) {
						savedata.setQuoteNo(quoteNo);
						}else {
						savedata.setQuoteNo(newQuoteNo);
						}
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						//savedata.setEndtPrevPolicyNo(req.getPolicyNo()+"-"+count);
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
		
				System.out.println("*************EserviceMotorDetails************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
				System.out.println("New Customer Id:"+newCustId);
				System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
				if (pendingcount == 0) {
					// Copy Quote Home Position Master
					 homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

					// Copy Quote Personal Info
					 personolInfoEndoCopyQuote(req, customerId, prevPolicyNo,prevQuoteNo, count,custRefNo);

					// Copy Quote Policy Cover Data
					 policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo,prevQuoteNo, count);

					// Copy Quote Motor Data Details
					motorDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo, count);

					// Copy Quote Motor Driver Details
					 motorDriverDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo, count);

					// Copy COVER_DOCUMENT_UPLOAD_DETAILS
					coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count);
				
					// Copy ESERVICE_SECTION_DETAILS
					eserviceSectionDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

					// Copy Section Data Details
					sectionDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					
//					// Copy ESERVICE_CUSTOMER_DETAILS
//					eserviceCustDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				}
				//res.setRequestReferenceNo(newRequestNo);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return savedata;
		}

//Count
		public List<Object> getMasterTableCount(String policyNo) {
			List<Object> list = new ArrayList<Object>();
			try {
				// List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Object> query = cb.createQuery(Object.class);
				// Find all
				Root<EserviceMotorDetails> b = query.from(EserviceMotorDetails.class);
				// Select
				query.multiselect(b.get("policyNo").alias("policyNo"));

				Predicate n1 = cb.equal(b.get("originalPolicyNo"), policyNo);
				query.where(n1).groupBy(b.get("policyNo"));

				// Get Result
				TypedQuery<Object> result = em.createQuery(query);
				list = result.getResultList();

			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
			return list;
		}

		//Delete Previous Endo
		private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceMotorDetails> motorsPending) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			try {
				String quoteNo=motorsPending.get(0).getQuoteNo();
				// Delete Old Record
				
				//E service Motor Details
				if(motorsPending.size()>0) {
					repo.deleteAll(motorsPending);
				}
				//E service Customer Details 
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(quoteNo);
				String customerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
//				EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
//				if (custData!=null) {
//					custRepo.delete(custData);
//				}
				//Personal Info
				if(personalInfoData!=null) {
					personalInforepo.delete(personalInfoData);
				}
				//Home Position master
				if(homeData!=null) {
					homePosistionRepo.delete(homeData);				}
				//Policy Cover Data
				List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(quoteNo);
				if (policyCoverData.size() > 0) {
					policyCoverDataRepo.deleteAll(policyCoverData);
				}
				//Motor Driver Details
				List<MotorDriverDetails> motorDriverData = motordrivDetepo.findByQuoteNo(quoteNo);
				if (motorDriverData.size() > 0) {
					motordrivDetepo.deleteAll(motorDriverData);
				}
				//Cover Document Upload Details
				List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
				if (coverDocList.size() > 0) {
					coverDocUploadDetails.deleteAll(coverDocList);
				}
				//Motor Data Details
				List<MotorDataDetails> motorData = motorDataDetepo.findByQuoteNo(quoteNo);
				if (motorData.size() > 0) {
					motorDataDetepo.deleteAll(motorData);
				}
				//Eservice SectionDetails
				List<EserviceSectionDetails> eseSecList = eserSecRepo.findByQuoteNo(quoteNo);
				if (eseSecList.size() > 0) {
					eserSecRepo.deleteAll(eseSecList);
				}
				// Section Data Details
				List<SectionDataDetails> secDataList = sectionDataRepo.findByQuoteNo(quoteNo);
				if (secDataList.size() > 0) {
					sectionDataRepo.deleteAll(secDataList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}

	/*	private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			EserviceCustomerDetails savedata = new EserviceCustomerDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				String olsCustomerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
				EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
				if (custData!=null) 
						savedata = dozerMapper.map(custData, EserviceCustomerDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
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
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						custRepo.saveAndFlush(savedata);
			
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

			
		}*/

		//Home Position Master Endt Copy Quote
		@Transactional
		public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
		String prevQuoteNo,Integer count,String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			HomePositionMaster savedata = new HomePositionMaster();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			
		try {
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
					
			String endtFeeYn=entMaster.getEndtFeeYn();
			BigDecimal endtPre=BigDecimal.ZERO;
			BigDecimal endtPremiumtax=BigDecimal.ZERO;
			Double endtPercent=0d;
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
			Double tax=Double.valueOf(homeData.getVatPercent().toString());
			BigDecimal exchangeRate=homeData.getExchangeRate();
			BigDecimal overAllPremiumFc=new BigDecimal(homeData.getOverallPremiumFc().toString());
			if ("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)) {
				endtPercent = Double.valueOf(entMaster.getEndtFeePercent());
				endtPre = domath(entMaster.getCalcTypeId(), endtPercent, overAllPremiumFc,exchangeRate);
				endtPremiumtax=endtPre.multiply(new BigDecimal(tax/100));
			}
			
			
			String txt="";
			if(endtPre.intValue()>0) {
				txt="CHARGE";
			}else {
				txt="REFUND";
			}
			savedata = dozerMapper.map(homeData, HomePositionMaster.class);
			savedata.setRequestReferenceNo(refNo);
			savedata.setCustomerId(customerId);
			savedata.setQuoteNo(quoteNo);
			savedata.setEndtTypeId(req.getEndtTypeId());
			savedata.setEndtDate(new Date());
			savedata.setEndtBy(loginId);
			savedata.setEndtStatus("P");
			savedata.setEndtCommission(BigDecimal.valueOf(0));
			savedata.setQuoteCreatedDate(new Date());
			savedata.setEntryDate(new Date());
			if("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)){
				savedata.setEndtPremium(endtPre);
				savedata.setIsChargRefund(txt);
				}else {
				savedata.setEndtPremium(endtPre);
				savedata.setIsChargRefund("");
			}
			savedata.setEndtPremiumTax(endtPremiumtax);
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
		
			System.out.println("*************HomePositionMaster************");
			System.out.println("QUOTE NO:"+quoteNo);
			System.out.println("Customer Id:"+customerId);
			System.out.println("Reference No:"+refNo);
			System.out.println("PreQuoteNo:"+prevPolicyNo);
			System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
			System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
		}
		
		protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) {
			BigDecimal d=BigDecimal.ZERO;
			if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);	
			 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate/*,round*/));// for foreign currency calculation we have to divide by exchange rate	
			 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);	
			 }
			return d;
			}

		//Personal Info Endt Copy Quote
		@Transactional
		public CopyQuoteSuccessRes personolInfoEndoCopyQuote(CopyQuoteReq req,String customerId,String prevPolicyNo,
				String prevQuoteNo,Integer count,String custRefNo) {
			CopyQuoteSuccessRes res =new CopyQuoteSuccessRes();
			PersonalInfo savedata = new PersonalInfo();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
				String olsCustomerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
				savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
				savedata.setCustomerId(customerId);
				//savedata.setCustomerReferenceNo(custRefNo);
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
				System.out.println("*************PersonalInfo************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				System.out.println("Old Customer Id:"+olsCustomerId);
				System.out.println("New Customer Id:"+customerId);
//				System.out.println("Old Reference No:"+refNo);
//				System.out.println("QUOTE NO:"+quoteNo);
//				System.out.println("New Customer Id:"+newCustId);
//				System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
				//res.setSuccessId(customerId);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}

		// Policy Cover Data Enst Copy Quote
		public CopyQuoteSuccessRes policyCoverDataEndocopyQuote(CopyQuoteReq req, String refNo, String quoteNo,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			PolicyCoverData savedata = new PolicyCoverData();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				CoverMaster coverdata = null;
				EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				String endTypeDesc = entMaster.getEndtTypeDesc();
				String endtFeeYn = entMaster.getEndtFeeYn();
				String coverDesc = "";
				BigDecimal endtFee = BigDecimal.ZERO;
				if(StringUtils.isBlank(entMaster.getEndtFeePercent())|entMaster.getEndtFeePercent()==null) {
					endtFee = BigDecimal.ZERO;
				}else {
					endtFee =new BigDecimal(entMaster.getEndtFeePercent());
				}
				BigDecimal endtAmt = BigDecimal.ZERO;
				List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(prevQuoteNo);
				if (policyCoverData.size() > 0) {
					for (PolicyCoverData data : policyCoverData) {
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setEndtCount(new BigDecimal(count));
					//	savedata.setStatus("E");
						policyCoverDataRepo.saveAndFlush(savedata);
					}

					List<PolicyCoverData> basecovers1 = policyCoverData.stream()
							.filter(d -> ("B".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers1) {
						coverDesc = basecovers1.get(0).getCoverDesc();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverageType("E");
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);

						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);

						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);

						savedata.setMinimumPremium(BigDecimal.ZERO);
						savedata.setIsTaxExtempted("N");
						savedata.setTaxId(0);
						savedata.setTaxRate(BigDecimal.ZERO);
						savedata.setTaxDesc("");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					List<PolicyCoverData> basecovers2 = policyCoverData.stream()
							.filter(d -> ("T".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers2) {
						BigDecimal divisor = new BigDecimal(100);
						Double taxRate = Double.valueOf(data.getTaxRate().toString());
						endtAmt = endtFee.multiply(new BigDecimal(taxRate / 100));
						coverDesc = data.getCoverName();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc);
						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);
						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);

						savedata.setMinimumPremium(BigDecimal.ZERO);
						savedata.setTaxId(data.getTaxId());
						savedata.setTaxRate(data.getTaxRate());
						savedata.setTaxCalcType(data.getTaxCalcType());
						savedata.setTaxDesc(endTypeDesc + " VAT");

						savedata.setTaxAmount(endtAmt);
						savedata.setIsTaxExtempted("N");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					if ("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)) {
						for (PolicyCoverData data : basecovers2) {
							coverDesc = data.getCoverName();
							savedata = dozerMapper.map(data, PolicyCoverData.class);
							savedata.setRequestReferenceNo(refNo);
							savedata.setQuoteNo(quoteNo);
							savedata.setEntryDate(new Date());
							savedata.setCreatedBy(loginId);
							savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
							savedata.setCoverName(coverDesc + " " + endTypeDesc + " Endorsement Fee" + count);
							savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " Endorsement Fee" + count);
							savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
							savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
							savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
							savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
							savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
							savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
							savedata.setPremiumIncludedTaxFc(endtAmt.add(endtFee));
							savedata.setPremiumIncludedTaxLc(endtAmt.add(endtFee));
							savedata.setDependentCoverYn("N");
							savedata.setDependentCoverId(null);
							savedata.setTaxId(Integer.valueOf(req.getEndtTypeId()));
							savedata.setTaxRate(endtFee);
							savedata.setTaxAmount(endtFee);
							savedata.setTaxCalcType(data.getTaxCalcType());
							savedata.setTaxDesc(endTypeDesc + " Endorsement Fee");
							savedata.setEndtCount(new BigDecimal(count));
							savedata.setDiscountCoverId(data.getCoverId());
							savedata.setCoverageType("T");
							savedata.setIsTaxExtempted("N");
							policyCoverDataRepo.saveAndFlush(savedata);
						}
					}
					List<PolicyCoverData> basecovers3 = policyCoverData.stream()
							.filter(d -> ("O".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers3) {
						coverDesc = data.getCoverDesc();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setCoverageType("T");
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);
						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);
						savedata.setTaxId(0);
						savedata.setTaxRate(BigDecimal.ZERO);
						savedata.setTaxCalcType(data.getTaxCalcType());
						savedata.setTaxDesc(" ");
						savedata.setIsTaxExtempted("N");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					

				}
				System.out.println("*************PolicyCoverData************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				//System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
				//System.out.println("New Customer Id:"+newCustId);
				//System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
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
				EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				List<MotorDataDetails> motorData=motorDataDetepo.findByQuoteNo(prevQuoteNo);
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
				System.out.println("*************PolicyCoverData************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
//				System.out.println("New Customer Id:"+newCustId);
//				System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

		// Motor Data Details Endt Copy Quote
		public CopyQuoteSuccessRes motorDriverDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
				String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			MotorDriverDetails savedata = new MotorDriverDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				List<MotorDriverDetails> motorDriverData = motordrivDetepo.findByQuoteNo(prevQuoteNo);
				if (motorDriverData.size() > 0) {
					for (MotorDriverDetails data : motorDriverData) {
						savedata = dozerMapper.map(data, MotorDriverDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
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
					System.out.println("*************DriverDetails************");
					System.out.println("Old QUOTE NO:"+prevQuoteNo);
					System.out.println("Old Customer Id:"+customerId);
					System.out.println("Old Reference No:"+refNo);
					System.out.println("QUOTE NO:"+quoteNo);
//					System.out.println("New Customer Id:"+newCustId);
//					System.out.println("Reference No:"+newRequestNo);
					System.out.println("PreQuoteNo:"+prevPolicyNo);
					System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
					System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
					System.out.println("**********************************************");
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
			DocumentTransactionDetails savedata = new DocumentTransactionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(prevQuoteNo);
				if (motorData.size() > 0) {
					for (DocumentTransactionDetails data : motorData) {
						savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
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
				System.out.println("*************DriverDataDetails************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
//				System.out.println("New Customer Id:"+newCustId);
//				System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
		//Eservice Section Details
		private CopyQuoteSuccessRes eserviceSectionDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			EserviceSectionDetails savedata = new EserviceSectionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

				List<EserviceSectionDetails> eserSec = eserSecRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				if (eserSec != null && eserSec.size()>0 ) {
					for (EserviceSectionDetails data : eserSec) {
						savedata = dozerMapper.map(data, EserviceSectionDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
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
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						eserSecRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
		// Section Data Details
		private CopyQuoteSuccessRes sectionDataDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			SectionDataDetails savedata = new SectionDataDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

//				List<SectionDataDetails> eserSec = sectionDataRepo.findByQuoteNoAndStatusOrderByRiskIdAsc(prevQuoteNo,"Y");
				List<SectionDataDetails> eserSec = sectionDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
				if (eserSec != null && eserSec.size()>0 ) {
					for (SectionDataDetails data : eserSec) {
						savedata = dozerMapper.map(data, SectionDataDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
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
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						sectionDataRepo.saveAndFlush(savedata);
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
						m.get("currency").alias("currency"),
						m.get("originalPolicyNo").alias("originalPolicyNo")

						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));
					
				// Endt Count Max Filter
				Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
				endtCount.where(a1,a2);
				
				 
				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
				Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");
				Predicate n12 = cb.isNull(m.get("endtTypeId"));
				Predicate n13 = cb.or(n11,n12);
			Predicate n5 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				if ("broker".equalsIgnoreCase(req.getUserType())) {
					n5 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				} else {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
				}
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
				
				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10,n13)
				.groupBy(
						c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
						m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
						m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
						m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
						m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),m.get("effectiveDate")
						,m.get("currency"),m.get("originalPolicyNo")
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
		public List<PortfolioPendingGridCriteriaRes> getMotorProtfolioPending(ExistingQuoteReq req, List<String> branches,
				Date startDate, int limit, int offset, String status) {
			List<PortfolioPendingGridCriteriaRes> portfolio = new ArrayList<PortfolioPendingGridCriteriaRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioPendingGridCriteriaRes> query = cb.createQuery(PortfolioPendingGridCriteriaRes.class);
/*
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
						m.get("currency").alias("currency"),
						m.get("originalPolicyNo").alias("originalPolicyNo")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				// Endt Count Max Filter
				Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
				endtCount.where(a1,a2);
				
				 
				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.equal(m.get("endtCount"), endtCount);

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

				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n10)
				.groupBy(
						c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
						m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
						m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
						m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
						m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),m.get("effectiveDate"),
						m.get("currency"),m.get("originalPolicyNo")
						)
						.orderBy(orderList);

				// Get Result
				TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());*/
				

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
				Subquery<Long> endtPre = query.subquery(Long.class);
				Root<HomePositionMaster> h1 = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h1.get("endtPremium"))) ;
				Predicate pm1 = cb.equal(h1.get("endtStatus"), m.get("endtStatus"));
				Predicate pm2   = cb.like(h1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				endtPre.where(pm1,pm2);
		
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						cb.count(m).as(Long.class).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), 
						cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						
						cb.max(c.get("mobileNo1")).alias("mobileNo1"),
						cb.max(c.get("isTaxExempted")).alias("isTaxExempted"),
						cb.max(c.get("taxExemptedId")).alias("taxExemptedId"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), 
						cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"),
						cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("inceptionDate"),
						cb.max(m.get("policyEndDate")).alias("expiryDate"),
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), 
						cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						
						//Home Position Master
						
						cb.max(h.get("debitAcNo")).alias("debitAcNo"),
						cb.max(h.get("debitTo")).alias("debitTo"),
						cb.max(h.get("debitToId")).alias("debitToId"),
						cb.max(h.get("debitNoteNo")).alias("debitNoteNo"),
						cb.max(h.get("debitNoteDate")).alias("debitNoteDate"),
						cb.max(h.get("creditTo")).alias("creditTo"),
						cb.max(h.get("creditToId")).alias("creditToId"),
						cb.max(h.get("creditNo")).alias("creditNo"),
						cb.max(h.get("creditDate")).alias("creditDate"),
						cb.max(h.get("emiYn")).alias("emiYn"),
						cb.max(h.get("installmentPeriod")).alias("installmentPeriod"),
						cb.max(h.get("effectiveDate")).alias("effectiveDate"),
						cb.max(m.get("currency")).alias("currency"),
						cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
						
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						//cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						endtPre.alias("endtPremium")
						);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

			
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("endtStatus"), status);
			//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","D"));  
				Predicate n7 = cb.greaterThanOrEqualTo(h.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(h.get("entryDate"), startDate);
				

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

				query.where(n1, n2, n3, n4, n5, n6,n7,n8).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				// Get Result
				TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return portfolio;
		}

		@Override
		public List<PortfolioGridCriteriaRes> getMotorPortfolioCancelled(ExistingQuoteReq req, List<String> branches,
				Date startDate,int limit, int offset, String endtId) {
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
					//	m.get("entryDate").alias("entryDate"), //now add
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
						m.get("currency").alias("currency"),
						m.get("originalPolicyNo").alias("originalPolicyNo")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				// Endt Count Max Filter
				Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
				endtCount.where(a1,a2);
				
				 
				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), "P");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
				Predicate n12 = cb.equal(m.get("endtTypeId"), endtId);
			//	Predicate n10 = cb.isNull(m.get("endtTypeId"));
				
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

				query.where(n1,n2, n3, n4, n5, n6,n7,n8,n10,n12)
						.groupBy(
								c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"),c.get("mobileNo1"), c.get("isTaxExempted"), c.get("taxExemptedId"),
								m.get("companyId"),m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("entryDate"), m.get("expiryDate"),m.get("inceptionDate"), m.get("overallPremiumLc"), m.get("overallPremiumFc"),
								m.get("policyNo"), m.get("debitAcNo"), m.get("debitTo"),m.get("debitToId"), m.get("debitNoteNo"), m.get("debitNoteDate"),
								m.get("creditTo"), m.get("creditToId"), m.get("creditNo"),m.get("creditDate"), m.get("emiYn"), m.get("installmentPeriod"),
								m.get("effectiveDate")
								,	m.get("currency"),m.get("originalPolicyNo")
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
			String refNo = refShortCode  +"-" + seqNo.generateRefNo();
			return refNo;
		}
		 public synchronized String generateCustRefNo() {
		       try {
		    	   SeqCustrefno entity;
		            entity = custRefRepo.save(new SeqCustrefno());          
		            return String.format("%05d",entity.getCustReferenceNo()) ;
		        } catch (Exception e) {
					e.printStackTrace();
					log.info( "Exception is ---> " + e.getMessage());
		            return null;
		        }
		       
		 }
		 
		 @Override
		 public List<Tuple> getMotorReportDetails(GetallPolicyReportsReq req) {
				List<Tuple> list = new ArrayList<Tuple>();
				
				try {
					
					 DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
			         String databaseProductName = metaData.getDatabaseProductName();
			         // Check if the database product name contains the word "Oracle"
			            isOracle = databaseProductName.contains("Oracle");

			            // Check if the database product name contains the word "MySQL"
			            isMySQL = databaseProductName.contains("MySQL");
			    
			            String dateFormat = "";
			            if(isOracle) {
			            	
			            	dateFormat = "dd-MM-yy";
			        	 
			         	} else if (isMySQL) {
			        	 
			         		dateFormat = "yyyy-MM-dd";
			         	}
			            DateFormat dateForm = new SimpleDateFormat(dateFormat);  
			            
					
					CriteriaBuilder cb = em.getCriteriaBuilder(); 
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class); 
					
					Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
					Root<PersonalInfo> pif = query.from(PersonalInfo.class);
					Root<MotorDataDetails> mdd = query.from(MotorDataDetails.class);
				
					//broker name
					Subquery<String> brokerName = query.subquery(String.class); 
					Root<LoginUserInfo> lui = brokerName.from(LoginUserInfo.class);
					brokerName.select(lui.get("userName"));
					Predicate b1 = cb.equal( lui.get("loginId"), hpm.get("loginId"));
					brokerName.where(b1);
					
					//User type
					Subquery<String> userType = query.subquery(String.class); 
					Root<LoginMaster> lm = userType.from(LoginMaster.class);
					userType.select(lm.get("userType"));
					Predicate a1 = cb.equal( lm.get("loginId"), req.getLoginId());
					userType.where(a1);
					
					//SubUser type
					Subquery<String> subUserType = query.subquery(String.class); 
					Root<LoginMaster> lms = subUserType.from(LoginMaster.class);
					subUserType.select(lms.get("subUserType"));
					Predicate c1 = cb.equal( lms.get("loginId"), req.getLoginId());
					subUserType.where(c1);
					
					//paymentId
					Subquery<String> paymentId = query.subquery(String.class); 
					Root<PaymentInfo> pi = paymentId.from(PaymentInfo.class);
					paymentId.select(pi.get("paymentId"));
					Predicate d1 = cb.equal( pi.get("quoteNo"), hpm.get("quoteNo"));
					Predicate d2 = cb.equal( pi.get("paymentStatus"), "ACCEPTED");
					paymentId.where(d1,d2);
					
					//CurrencyId
					Subquery<String> currencyId = query.subquery(String.class); 
					Root<InsuranceCompanyMaster> icm = currencyId.from(InsuranceCompanyMaster.class);
					currencyId.select(icm.get("currencyId"));
					Predicate e1 = cb.equal( icm.get("companyId"), hpm.get("companyId"));
					currencyId.where(e1);
					
					Expression<Object> premium = cb.selectCase().when(hpm.get("currency").in(currencyId==null?null:currencyId), mdd.get("overallPremiumLc"))
							.otherwise(mdd.get("overallPremiumFc"));
					
					query.multiselect(
							hpm.get("loginId").alias("loginId"),
							hpm.get("quoteNo").alias("quoteNo"),	
							hpm.get("policyNo").alias("policyNo"),	
							cb.upper(cb.concat(cb.concat(pif.get("titleDesc"),  "."), pif.get("clientName"))).alias("customerName"),
							hpm.get("inceptionDate").alias("startDate"),
							hpm.get("expiryDate").alias("endDate"),
							hpm.get("effectiveDate").alias("issueDate"),
							cb.upper(hpm.get("branchName")).alias("branchName"),
							cb.upper(brokerName).alias("brokerName"),
							userType.alias("userType"),
							subUserType.alias("subUserType"),
							hpm.get("currency").alias("currency"),
							hpm.get("paymentType").alias("paymentType"),
							paymentId.alias("paymentId"),
							mdd.get("policyTypeDesc").alias("policyTypeDesc"),
							hpm.get("debitNoteNo").alias("debitNoteNo"),
							mdd.get("sumInsured").alias("sumInsured"), //motor only
							cb.function("ROUND", Double.class, premium, cb.literal(2)).alias("premium"),   //round 2
							cb.function("ROUND", BigDecimal.class, hpm.get("commissionPercentage"), cb.literal(1)).alias("commissionPercentage"), 
							cb.function("ROUND", BigDecimal.class, hpm.get("commission"), cb.literal(2)).alias("commissionAmount")
							
							); 
					//agencyCode
					Subquery<String> agencyCode = query.subquery(String.class); 
					Root<LoginMaster> log = agencyCode.from(LoginMaster.class);
					agencyCode.select(log.get("agencyCode")).distinct(true);
					
					Subquery<String> agency = query.subquery(String.class); 
					Root<LoginMaster> ag1 = agency.from(LoginMaster.class);
					agency.select(ag1.get("agencyCode"));
					Predicate h2 =  cb.equal(ag1.get("loginId"), req.getLoginId());
					agency.where(h2);
					
					Predicate f1 =  log.get("oaCode").in(agency==null?null:agency);
					agencyCode.where(f1);
					
					//agencyCode
					Subquery<String> agencyCode1 = query.subquery(String.class); 
					Root<LoginMaster> ag = agencyCode1.from(LoginMaster.class);
					agencyCode1.select(ag.get("agencyCode"));
					Predicate g2 =  cb.equal(ag.get("loginId"), req.getLoginId());
					agencyCode1.where(g2);
					
					List<Predicate> predicates = new ArrayList<Predicate>();
					predicates.add(cb.equal(hpm.get("customerId"), pif.get("customerId")));
					predicates.add(cb.equal(hpm.get("quoteNo"), mdd.get("quoteNo")));
					predicates.add(cb.equal(hpm.get("productId"), req.getProductId()));
					predicates.add(cb.equal(hpm.get("branchCode"), req.getBranchCode()));
					predicates.add(cb.equal(hpm.get("status"), "P"));
					predicates.add(cb.isNotNull(hpm.get("policyNo")));
					predicates.add(cb.equal(hpm.get("companyId"), req.getInsuranceId()));
					predicates.add(cb.between(hpm.get("inceptionDate"),dateForm.parse(dateForm.format(req.getStartDate())), dateForm.parse(dateForm.format(req.getEndDate()))));		
					
					Predicate ex1 = cb.equal(cb.selectCase().when(subUserType.in("both"), "1"), "1") ;
					Predicate ex2 = cb.equal(cb.selectCase().when(subUserType.in("low","high"), hpm.get("applicationId")), req.getLoginId());
					
					Predicate ex3 = cb.selectCase().when(cb.equal(userType, "Broker"), hpm.get("agencyCode")).in(agencyCode)	;		
					Predicate ex4 = hpm.get("agencyCode").in(agencyCode1);

					predicates.add(cb.or(ex1,ex2,ex3,ex4));
					
					
					query.where(predicates.toArray(new Predicate[0]));
				
					TypedQuery<Tuple> result = em.createQuery(query);
					list = result.getResultList();
					
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return list;
			}
			public boolean isOracle() {
		        return isOracle;
		    }

		    public boolean isMySQL() {
		        return isMySQL;
		    }
		    
		    @Override
			public synchronized List<MotorGridCriteriaAdminRes> getMotorAdminReferalPendingDetails(RevertGridReq req , int limit,int offset ,String status) {
				List<MotorGridCriteriaAdminRes> referrals = new ArrayList<MotorGridCriteriaAdminRes>();
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
							m.get("branchCode").alias("branchCode"),
							m.get("requestReferenceNo").alias("requestReferenceNo"),
							cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
									.alias("quoteNo"),
							cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
									.otherwise(m.get("customerId")).alias("customerId"),
							m.get("policyStartDate").alias("policyStartDate"),
							m.get("policyEndDate").alias("policyEndDate"),
							m.get("rejectReason").alias("rejectReason"),
							m.get("adminRemarks").alias("adminRemarks"),
							cb.max(m.get("status")).alias("status"),
							cb.max(m.get("entryDate")).alias("entryDate"),
							cb.max(m.get("endorsementType")).alias("endorsementType"),
							cb.max(m.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
							cb.max(m.get("endorsementDate")).alias("endorsementDate"),
							cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
							cb.max(m.get("endorsementEffdate")).alias("endorsementEffdate"),
							cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
							cb.max(m.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
							cb.max(m.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
							cb.max(m.get("endtCount")).alias("endtCount"),
							cb.max(m.get("endtStatus")).alias("endtStatus"),
							cb.max(m.get("endtCategDesc")).alias("endtCategDesc"),
							cb.max(m.get("endtPremium")).alias("endtPremium")
							);

					// Order By
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(m.get("updatedDate")));

					// Where
					Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
					Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
					Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n4 = cb.equal(m.get("status"),status);
					//Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));

					String branchCode ="";
					if (StringUtils.isNotBlank(req.getBranchCode()) && !"99999".equals(req.getBranchCode())) {
						branchCode = req.getBranchCode();
						Predicate n6 =cb.equal(m.get("branchCode"),branchCode);
						query.where(n1, n2, n3, n4, n6)
						.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
								m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
								)
						.orderBy(orderList);
					}else {
						query.where(n1, n2, n3, n4)
						.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
								m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
								)
						.orderBy(orderList);
						
					}
				
						
					

					// Get Result
					TypedQuery<Tuple> result = em.createQuery(query);
					result.setFirstResult(limit * offset);
					result.setMaxResults(offset);
					List<Tuple> referralsList = result.getResultList();
					for (  Tuple r :referralsList   ) {
						MotorGridCriteriaAdminRes res = new MotorGridCriteriaAdminRes();
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
						res.setStatus(r.get("status")==null ? "" : (String) r.get("status"));
						res.setEntryDate(r.get("entryDate")==null ? null : (Date) r.get("entryDate"));
						res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
						res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
						res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
						res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
						res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
						res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
						res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
						res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());
//						res.setEndorsementYn(r.get("endorsementYn")==null ? "" : r.get("endorsementYn").toString());
						res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
						res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
						res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
						res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
						res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
						res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
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
			public synchronized List<MotorGridCriteriaAdminRes> getMotorAdminReferalPendingDetailsCount(RevertGridReq req, String status) {
				List<MotorGridCriteriaAdminRes> referrals = new ArrayList<MotorGridCriteriaAdminRes>();
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
									m.get("branchCode").alias("branchCode"),
									m.get("requestReferenceNo").alias("requestReferenceNo"),
									cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
											.alias("quoteNo"),
									cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
											.otherwise(m.get("customerId")).alias("customerId"),
									m.get("policyStartDate").alias("policyStartDate"),
									m.get("policyEndDate").alias("policyEndDate"),
									m.get("rejectReason").alias("rejectReason"),
									m.get("adminRemarks").alias("adminRemarks"),
									cb.max(m.get("status")).alias("status"),
									cb.max(m.get("entryDate")).alias("entryDate"),
									cb.max(m.get("endorsementType")).alias("endorsementType"),
									cb.max(m.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
									cb.max(m.get("endorsementDate")).alias("endorsementDate"),
									cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
									cb.max(m.get("endorsementEffdate")).alias("endorsementEffdate"),
									cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
									cb.max(m.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
									cb.max(m.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
									cb.max(m.get("endtCount")).alias("endtCount"),
									cb.max(m.get("endtStatus")).alias("endtStatus"),
									cb.max(m.get("endtCategDesc")).alias("endtCategDesc"),
									cb.max(m.get("endtPremium")).alias("endtPremium")
									);

							// Order By
							List<Order> orderList = new ArrayList<Order>();
							orderList.add(cb.desc(m.get("updatedDate")));

							// Where
							Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
							Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
							Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
							Predicate n4 = cb.equal(m.get("status"),status);
							//Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));

							String branchCode ="";
							if (StringUtils.isNotBlank(req.getBranchCode()) && !"99999".equals(req.getBranchCode())) {
								branchCode = req.getBranchCode();
								Predicate n6 =cb.equal(m.get("branchCode"),branchCode);
								query.where(n1, n2, n3, n4, n6)
								.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
										m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
										m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
										m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
										)
								.orderBy(orderList);
							}else {
								query.where(n1, n2, n3, n4)
								.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
										m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
										m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
										m.get("rejectReason"),m.get("adminRemarks"),m.get("updatedDate")
										)
								.orderBy(orderList);
								
							}
						
								
							

							// Get Result
							TypedQuery<Tuple> result = em.createQuery(query);

							List<Tuple> referralsList = result.getResultList();
							for (  Tuple r :referralsList   ) {
								MotorGridCriteriaAdminRes res = new MotorGridCriteriaAdminRes();
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
								res.setStatus(r.get("status")==null ? "" : (String) r.get("status"));
								res.setEntryDate(r.get("entryDate")==null ? null : (Date) r.get("entryDate"));
								res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
								res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
								res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
								res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
								res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
								res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
								res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
								res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());
//								res.setEndorsementYn(r.get("endorsementYn")==null ? "" : r.get("endorsementYn").toString());
								res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
								res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
								res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
								res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
								res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
								res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
								res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
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
			public List<PortfolioSearchDataRes> getProtfolioSearchData(SearchBrokerPolicyReq req) {
				List<PortfolioSearchDataRes> portfolioResList = new ArrayList<PortfolioSearchDataRes>();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					Date startDate = new Date();
					Calendar cal = new GregorianCalendar();
					cal.setTime(startDate);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 1);
					startDate = cal.getTime();

					int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
					int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
					String policyNo = req.getPolicyNo();
//					String loginId = "";
//					if (req.getApplicationId().equalsIgnoreCase("1")) {
//						loginId = req.getLoginId();
//					} else {
//						loginId = req.getApplicationId();
//					}
					// Branch Res
					List<String> branches = new ArrayList<String>();
					branches.add(req.getBranchCode());
					
					if( StringUtils.isNotBlank(policyNo) && policyNo.length() > 4 ) {
						List<PortfolioAdminSearchRes> portFolioList = new ArrayList<PortfolioAdminSearchRes>();
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<PortfolioAdminSearchRes> query = cb.createQuery(PortfolioAdminSearchRes.class);

						// Find All
						Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
						Root<PersonalInfo> c = query.from(PersonalInfo.class);
						
						// Select
						query.multiselect(
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
								m.get("quoteNo").alias("quoteNo"),
								m.get("customerId").alias("customerId"),
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
								m.get("currency").alias("currency"),
								m.get("originalPolicyNo").alias("originalPolicyNo")

								);

						// Order By
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(cb.desc(m.get("entryDate")));
							
						// Endt Count Max Filter
						Subquery<Long> endtCount = query.subquery(Long.class);
						Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
						endtCount.select(cb.max(ocpm1.get("endtCount")));
						Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
						Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
						endtCount.where(a1,a2);
						
						 
						// Where
						Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
						Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
						Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n4 = cb.equal(m.get("status"), "P");
						Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
						Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
						Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
						Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
						Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");
						Predicate n12 = cb.isNull(m.get("endtTypeId"));
						Predicate n13 = cb.or(n11,n12);
						Predicate n5 = cb.equal(m.get("applicationId"), "1");
						Expression<String> e0 = m.get("branchCode");
						Predicate n6 = e0.in(branches);
						Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
						
						query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10,n13,n14).orderBy(orderList);

						// Get Result
						TypedQuery<PortfolioAdminSearchRes> result = em.createQuery(query);
						result.setFirstResult(limit * offset);
						result.setMaxResults(offset);
						portFolioList = result.getResultList();
						
						for ( PortfolioAdminSearchRes portfolio : portFolioList ) {
							PortfolioSearchDataRes portfolioRes = new PortfolioSearchDataRes();
							portfolioRes = dozerMapper.map(portfolio, PortfolioSearchDataRes.class);
							
							portfolioResList.add(portfolioRes);
						}
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return portfolioResList;
			}

			@Override
			public Long getProtfolioSearchDataCount(SearchBrokerPolicyReq req) {
				Long portfolioCount = 0L ;
				try {
					Date startDate = new Date();
					Calendar cal = new GregorianCalendar();
					cal.setTime(startDate);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 1);
					startDate = cal.getTime();

					String policyNo = req.getPolicyNo();
//					String loginId = "";
//					if (req.getApplicationId().equalsIgnoreCase("1")) {
//						loginId = req.getLoginId();
//					} else {
//						loginId = req.getApplicationId();
//					}
					// Branch Res
					List<String> branches = new ArrayList<String>();
					branches.add(req.getBranchCode());
					
					if( StringUtils.isNotBlank(policyNo) && policyNo.length() > 4 ) {
						
						List<Long> portFolioList = new ArrayList<Long>();
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Long> query = cb.createQuery(Long.class);

						// Find All
						Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
						Root<PersonalInfo> c = query.from(PersonalInfo.class);

						// Select
						query.multiselect(cb.count(m) );

						// Order By
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(cb.desc(m.get("entryDate")));
							
						// Endt Count Max Filter
						Subquery<Long> endtCount = query.subquery(Long.class);
						Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
						endtCount.select(cb.max(ocpm1.get("endtCount")));
						Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
						Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
						endtCount.where(a1,a2);
						
						 
						// Where
						Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
						Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
						Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n4 = cb.equal(m.get("status"), "P");
						Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
						Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
						Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
						Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
						Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");
						Predicate n12 = cb.isNull(m.get("endtTypeId"));
						Predicate n13 = cb.or(n11,n12);
						Predicate n5 = cb.equal(m.get("applicationId"), "1");
						Expression<String> e0 = m.get("branchCode");
						Predicate n6 = e0.in(branches);
						Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
						
						query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10,n13,n14).orderBy(orderList);

						// Get Result
						TypedQuery<Long> result = em.createQuery(query);
						portFolioList = result.getResultList();
						portfolioCount = portFolioList.size() > 0 ? portFolioList.get(0) : 0L ;			
					
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return portfolioCount;
			}	    

		
	}

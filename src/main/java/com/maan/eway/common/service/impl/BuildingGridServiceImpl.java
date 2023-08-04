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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.ContentAndRisk;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

@Service
@Transactional
public class BuildingGridServiceImpl implements BuildingGridService {

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(BuildingGridServiceImpl.class);

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired
	private EserviceBuildingDetailsRepository repo;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private MotorGridServiceImpl motorService ;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;

	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;
	
	@Autowired
	private ProductEmployeesDetailsRepository pARepo;
	
	@Autowired
	private ContentAndRiskRepository contentRiskRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo;
	

	@Autowired 
	private RatingFactorsUtil ratingutil;
	// Exiting Motor Details

	@Override
	public List<QuoteCriteriaRes> getBuildingExistingQuoteDetails(ExistingQuoteReq req, List<String> branches,
			Date startDate, Date endDate, Integer limit, Integer offset) {
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		try {

			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

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

			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),m.get("updatedDate"))
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
	public List<QuoteCriteriaRes> getBuildingLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches, Date before30,
			int limit, int offset) {
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
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

			query.where(n1, n2, n3, n4, n5, n6, n7)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),m.get("updatedDate"))
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
	public List<RejectCriteriaRes> getBuildingRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
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

			query.where(n1, n2, n3, n4, n5, n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("updatedDate"))
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
	public synchronized List<ReferalGridCriteriaRes> getBuildingReferalDetails(ExistingQuoteReq req, List<String> branches,
			int limit, int offset , String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
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
					m.get("referalRemarks").alias("referalRemarks"));

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
							m.get("rejectReason"),m.get("adminRemarks"),m.get("referalRemarks"),m.get("updatedDate"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
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
	public synchronized List<ReferalGridCriteriaRes> getBuildingAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset, String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
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
					m.get("referalRemarks").alias("referalRemarks"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);

			Expression<String> e0 = c.get("branchCode");
			Predicate n6 = e0.in(branches);
		//	Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1, n2, n3, n4, n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"),m.get("referalRemarks"),m.get("updatedDate"))
					.orderBy(orderList);

			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
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
	public List<Tuple> searchBuildingQuote(CopyQuoteReq req, List<String> branches) {
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
			//	Date entryDate = sdf.parse(searchValue);
			//	searchValue = sdf.format(entryDate);
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
			}else if ("PolicyNo".equalsIgnoreCase(searchKey)) {
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

			Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),

					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.max(c.get("riskId")).alias("riskId"), 
					cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
					cb.max(c.get("productId")).alias("productId"),
					cb.max(c.get("companyId")).alias("companyId"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
					.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

					cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
					.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("branchCode")).alias("branchCode"), 
					cb.max(c.get("inbuildConstructType")).alias("inbuildConstructType"),
					cb.max(c.get("buildingFloors")).alias("buildingFloors"),
					cb.max(c.get("outbuildConstructType")).alias("outbuildConstructType"),

					cb.max(c.get("buildingUsageYn")).alias("buildingUsageYn"),
					cb.max(c.get("buildingPurpose")).alias("buildingPurpose"),
					cb.max(c.get("buildingPurposeId")).alias("buildingPurposeId"),
					cb.max(c.get("buildingUsageId")).alias("buildingUsageId"), 
					cb.max(c.get("buildingUsageDesc")).alias("buildingUsageDesc"),
					cb.max(c.get("buildingType")).alias("buildingType"),
					cb.max(c.get("buildingOwnerYn")).alias("buildingOwnerYn"),
					cb.max(c.get("buildingOccupationType")).alias("buildingOccupationType"),
					cb.max(c.get("apartmentOrBorder")).alias("apartmentOrBorder"),
					cb.max(c.get("withoutInhabitantDays")).alias("withoutInhabitantDays"),
					cb.max(c.get("buildingCondition")).alias("buildingCondition"),
					cb.max(c.get("buildingBuildYear")).alias("buildingBuildYear"),
					cb.max(c.get("buildingAge")).alias("buildingAge"),
					cb.max(c.get("buildingAreaSqm")).alias("buildingAreaSqm"),
					cb.max(c.get("buildingSuminsured")).alias("buildingSuminsured"),
					cb.max(c.get("allriskSuminsured")).alias("allriskSuminsured"), 
					cb.max(c.get("paDeathSuminsured")).alias("paDeathSuminsured"),
					cb.max(c.get("paPermanentdisablementSuminsured")).alias("paPermanentdisablementSuminsured"),
					cb.max(c.get("paTotaldisabilitySumInsured")).alias("paTotaldisabilitySumInsured"), 
					cb.max(c.get("PaMedicalSuminsured")).alias("PaMedicalSuminsured"),
					cb.max(c.get("personalIntSuminsured")).alias("personalIntSuminsured"),
					cb.max(c.get("contentSuminsured")).alias("contentSuminsured"), 
					cb.max(c.get("workmenCompSuminsured")).alias("workmenCompSuminsured"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("createdBy")).alias("createdBy"),
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("updatedDate")).alias("updatedDate"),
					cb.max(c.get("updatedBy")).alias("updatedBy"),

					cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
					cb.max(c.get("applicationId")).alias("applicationId"),
					cb.max(c.get("brokerCode")).alias("brokerCode"),
					cb.max(c.get("subUserType")).alias("subUserType"),
					cb.max(c.get("loginId")).alias("loginId"),
					cb.max(c.get("agencyCode")).alias("agencyCode"),
					cb.max(c.get("policyStartDate")).alias("policyStartDate"),
					cb.max(c.get("policyEndDate")).alias("policyEndDate"),
					cb.max(c.get("policyPeriord")).alias("policyPeriord"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("adminLoginId")).alias("adminLoginId"),
					cb.max(c.get("adminRemarks")).alias("adminRemarks"),
					cb.max(c.get("rejectReason")).alias("rejectReason"),
					cb.max(c.get("referalRemarks")).alias("referalRemarks"),
					cb.max(c.get("productDesc")).alias("productDesc"),
					cb.max(c.get("sectionId")).alias("sectionId"),
					cb.max(c.get("sectionDesc")).alias("sectionDesc"),
					cb.max(c.get("branchName")).alias("branchName"),
					cb.max(c.get("companyName")).alias("companyName"),
					cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
					cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
					cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
					cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
					cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
					cb.max(c.get("insuranceType")).alias("insuranceType"),
					cb.max(c.get("commissionType")).alias("commissionType"),
					cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
					cb.max(c.get("havepromocode")).alias("havepromocode"),
					cb.max(c.get("promocode")).alias("promocode"),
					cb.max(c.get("occupationType")).alias("occupationType"),
					cb.max(c.get("occupationTypeDesc")).alias("occupationTypeDesc"),
					cb.max(c.get("domesticPackageYn")).alias("domesticPackageYn"), 
					cb.max(c.get("categoryId")).alias("categoryId"),
					cb.max(c.get("categoryDesc")).alias("categoryDesc"),
					cb.max(c.get("bankCode")).alias("bankCode"),
					cb.max(c.get("sourceType")).alias("sourceType"),
					cb.max(c.get("customerCode")).alias("customerCode"),

					cb.max(c.get("bdmCode")).alias("bdmCode"),
					cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
					cb.max(c.get("elecEquipSuminsured")).alias("elecEquipSuminsured"),
					cb.max(c.get("moneySinglecarrySuminsured")).alias("moneySinglecarrySuminsured"),
					cb.max(c.get("moneyAnnualcarrySuminsured")).alias("moneyAnnualcarrySuminsured"),
					cb.max(c.get("moneyInsafeSuminsured")).alias("moneyInsafeSuminsured"),
					cb.max(c.get("fidelityAnyoccuSuminsured")).alias("fidelityAnyoccuSuminsured"),
					cb.max(c.get("fidelityAnnualSuminsured")).alias("fidelityAnnualSuminsured"),
					cb.max(c.get("tpliabilityAnyoccuSuminsured")).alias("tpliabilityAnyoccuSuminsured"),
					cb.max(c.get("empliabilityAnnualSuminsured")).alias("empliabilityAnnualSuminsured"),
					cb.max(c.get("empliabilityExcessSuminsured")).alias("empliabilityExcessSuminsured"),
					cb.max(c.get("goodsSinglecarrySuminsured")).alias("goodsSinglecarrySuminsured"),
					cb.max(c.get("goodsTurnoverSuminsured")).alias("goodsTurnoverSuminsured"), 
					cb.max(c.get("industryId")).alias("industryId"),
					cb.max(c.get("industryDesc")).alias("industryDesc"),
					cb.max(c.get("endorsementType")).alias("endorsementType"),
					cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(c.get("endorsementDate")).alias("endorsementDate"),

					cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
				
					cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(c.get("endtCount")).alias("endtCount"),
					cb.max(c.get("endtStatus")).alias("endtStatus"),
					cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
					cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(c.get("endtPremium")).alias("endtPremium"),
					cb.max(c.get("liabilityOccupationId")).alias("liabilityOccupationId"), 
					cb.max(c.get("liabilityOccupationDesc")).alias("liabilityOccupationDesc"),
					cb.max(c.get("wallType")).alias("wallType"),
					cb.max(c.get("wallTypeDesc")).alias("wallTypeDesc"),
					cb.max(c.get("roofType")).alias("roofType"),
					cb.max(c.get("roofTypeDesc")).alias("roofTypeDesc"),
					cb.max(c.get("natureOfTradeId")).alias("natureOfTradeId"),
					cb.max(c.get("natureOfTradeDesc")).alias("natureOfTradeDesc"),
					cb.max(c.get("insuranceForId")).alias("insuranceForId"),
					cb.max(c.get("insuranceForDesc")).alias("insuranceForDesc"),
					cb.max(c.get("internalWallType")).alias("internalWallType"),
					cb.max(c.get("internalWallDesc")).alias("internalWallDesc"),

					cb.max(c.get("ceilingType")).alias("ceilingType"),
					cb.max(c.get("ceilingTypeDesc")).alias("ceilingTypeDesc"),
					cb.max(c.get("stockInTradeSi")).alias("stockInTradeSi"),
					cb.max(c.get("goodsSi")).alias("goodsSi"),
					cb.max(c.get("furnitureSi")).alias("furnitureSi"),
					cb.max(c.get("applianceSi")).alias("applianceSi"),
					cb.max(c.get("cashValueablesSi")).alias("cashValueablesSi"),
					cb.max(c.get("stockLossPercent")).alias("stockLossPercent"),
					cb.max(c.get("goodsLossPercent")).alias("goodsLossPercent"),

					cb.max(c.get("furnitureLossPercent")).alias("furnitureLossPercent"),
					cb.max(c.get("applianceLossPercent")).alias("applianceLossPercent"),
					cb.max(c.get("cashValueablesLossPercent")).alias("cashValueablesLossPercent"),
					cb.max(c.get("address")).alias("address"),
					cb.max(c.get("regionCode")).alias("regionCode"),
					cb.max(c.get("regionDesc")).alias("regionDesc"),
					cb.max(c.get("districtCode")).alias("districtCode"),
					cb.max(c.get("districtDesc")).alias("districtDesc"),
					cb.max(c.get("occupiedYear")).alias("occupiedYear"),
					cb.max(c.get("showWindow")).alias("showWindow"),

					cb.max(c.get("frontDoors")).alias("frontDoors"),
					cb.max(c.get("backDoors")).alias("backDoors"),
					cb.max(c.get("windowsMaterialId")).alias("windowsMaterialId"),
					cb.max(c.get("windowsMaterialDesc")).alias("windowsMaterialDesc"),
					cb.max(c.get("doorsMaterialId")).alias("doorsMaterialId"),
					cb.max(c.get("doorsMaterialDesc")).alias("doorsMaterialDesc"),
					cb.max(c.get("nightLeftDoor")).alias("nightLeftDoor"),
					cb.max(c.get("nightLeftDoorDesc")).alias("nightLeftDoorDesc"),
					cb.max(c.get("buildingOccupied")).alias("buildingOccupied"),
					cb.max(c.get("buildingOccupiedDesc")).alias("buildingOccupiedDesc"),
					cb.max(c.get("watchmanGuardHours")).alias("watchmanGuardHours"),
					cb.max(c.get("accessibleWindows")).alias("accessibleWindows"),
					cb.max(c.get("trapDoors")).alias("trapDoors"),
					cb.max(c.get("cashInHandDirectors")).alias("cashInHandDirectors"),
					cb.max(c.get("cashInTransit")).alias("cashInTransit"),
					cb.max(c.get("cashInHandEmployees")).alias("cashInHandEmployees"),
					cb.max(c.get("cashInSafe")).alias("cashInSafe"),
					cb.max(c.get("cashInPremises")).alias("cashInPremises"),
					cb.max(c.get("revenueFromStamps")).alias("revenueFromStamps"),
					cb.max(c.get("moneyInSafeBusiness")).alias("moneyInSafeBusiness"),
					cb.max(c.get("moneyOutSafeBusiness")).alias("moneyOutSafeBusiness"),
					cb.max(c.get("moneyInPremises")).alias("moneyInPremises"),
					cb.max(c.get("moneyInLocker")).alias("moneyInLocker"),
					cb.max(c.get("machineEquipSi")).alias("machineEquipSi"),
					cb.max(c.get("plateGlassSi")).alias("plateGlassSi"),
					cb.max(c.get("firstLossPercentId")).alias("firstLossPercentId"),
					cb.max(c.get("firstLossPercent")).alias("firstLossPercent"),
					cb.max(c.get("accDamageSi")).alias("accDamageSi"),
					cb.max(c.get("burglarySi")).alias("burglarySi"),
					cb.max(c.get("powerPlantSi")).alias("powerPlantSi"),
					cb.max(c.get("elecMachinesSi")).alias("elecMachinesSi"),
					cb.max(c.get("equipmentSi")).alias("equipmentSi"),
					cb.max(c.get("generalMachineSi")).alias("generalMachineSi"),
					cb.max(c.get("manuUnitsSi")).alias("manuUnitsSi"),
					cb.max(c.get("boilerPlantsSi")).alias("boilerPlantsSi"),
					cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
					cb.max(c.get("indemityPeriod")).alias("indemityPeriod"),
					cb.max(c.get("indemityPeriodDesc")).alias("indemityPeriodDesc"),
					cb.max(c.get("makutiYn")).alias("makutiYn"),
					cb.max(c.get("plateGlassType")).alias("plateGlassType"),
					cb.max(c.get("plateGlassDesc")).alias("plateGlassDesc")

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
			}else if (searchKey.equalsIgnoreCase("PolicyNo")) {
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
				//Expression<String> e0 = c.get("branchCode");
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
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			query.where(n1,n2,n3,n4,n5)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
			.orderBy(orderList);
			if (searchKey.equalsIgnoreCase("ClientName")) {
				query.where(n1, n2,n4,n5)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
				.orderBy(orderList);
			}
			if (searchKey.equalsIgnoreCase("EntryDate")) {
				query.where(n1,n2,n3,n4)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
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

	@Override
	public CopyQuoteSuccessRes buildingCopyQuote(CopyQuoteReq req, List<String> branches,String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceBuildingDetails savedata = new EserviceBuildingDetails();
		List<Tuple> copyQuote = new ArrayList<Tuple>();
		try {
			String searchValue = req.getRequestReferenceNo();
			String searchKey = "RequestReferenceNo";
			String companyId = req.getInsuranceId();
			String userType = req.getUserType();
			String branchCode = "";
			List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);

			String refNo = req.getRequestReferenceNo();

			if (list.size() > 0) {
				String refShortCode = motorService.getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
		        refNo = refShortCode + "-"  + seqNo.generateRefNo() ; 
				for (Tuple data : list) {
	
						savedata = dozerMapper.map(data.get(0), EserviceBuildingDetails.class);

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
				
				// Save Human 
				List<Tuple> list2 = copyCommonQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				for (Tuple data : list2) {
					EserviceCommonDetails savedata2 = new EserviceCommonDetails();
					savedata2 = dozerMapper.map(data.get(0), EserviceCommonDetails.class);

					savedata2.setEntryDate(new Date());
					savedata2.setCreatedBy(req.getLoginId());
					savedata2.setUpdatedBy(req.getLoginId());
					savedata2.setUpdatedDate(new Date());
					savedata2.setRequestReferenceNo(refNo);
					savedata2.setOldReqRefNo(req.getRequestReferenceNo());
					if (req.getUserType().equalsIgnoreCase("Broker")
							|| (req.getUserType().equalsIgnoreCase("User"))) {
						branchCode = req.getBranchCode();
						savedata2.setApplicationId("1");
						// savedata.setBrokerBranchCode(branchCode);

					} else if ("issuer".equalsIgnoreCase(userType)) {
						savedata2.setApplicationId(req.getLoginId());
						branchCode = req.getBranchCode();
						// savedata.setBranchCode(branchCode);
					}
					savedata2.setPolicyStartDate(null);		
					savedata2.setPolicyEndDate(null);
					savedata2.setActualPremiumFc(BigDecimal.ZERO);
					savedata2.setActualPremiumLc(BigDecimal.ZERO);
					savedata2.setOverallPremiumFc(BigDecimal.ZERO);
					savedata2.setOverallPremiumLc(BigDecimal.ZERO);
					savedata2.setQuoteNo("");
					savedata2.setStatus("Y");
					savedata2.setEndorsementDate(null);
					savedata2.setEndorsementEffdate(null);
					savedata2.setEndorsementRemarks(null);
					savedata2.setEndorsementType(null);
					savedata2.setEndorsementTypeDesc(null);
					savedata2.setEndtCategDesc(null);
					savedata2.setEndtCount(null);
					savedata2.setEndtPremium(null);
					savedata2.setEndtPrevPolicyNo(null);
					savedata2.setEndtPrevQuoteNo(null);
					savedata2.setEndtStatus(null);
					eserCommonRepo.saveAndFlush(savedata2);
				}
				
				// Save Section
				List<Tuple> list3 = copySectionQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
				if (list3 != null && list3.size() > 0) {
					for (Tuple data : list3) {
						EserviceSectionDetails savedata3 = new EserviceSectionDetails();
						savedata3 = dozerMapper.map(data.get(0), EserviceSectionDetails.class);

						savedata3.setEntryDate(new Date());
						savedata3.setCreatedBy(req.getLoginId());
						savedata3.setUpdatedBy(req.getLoginId());
						savedata3.setUpdatedDate(new Date());
						savedata3.setRequestReferenceNo(refNo);
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
						eserSecRepo.saveAndFlush(savedata3);
					}

				}
				}
		
			
		//	res.setResponse("Successfully Updated");
			res.setRequestReferenceNo(refNo);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<ListItemValue> geBuildingCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType) {
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
	public List<Tuple> copyQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(c,
						cus.get("clientName").alias("clientName"));

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("customerReferenceNo")));

				Predicate n1 = null;
	//			Predicate n3 = null;
	//			Predicate n4 = null;
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
//				//	Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				}
//				
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
		//		query.where(n1,n2,n3,n4,n5).orderBy(orderList);
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
	@Override
	public CopyQuoteSuccessRes buildingEndt(CopyQuoteReq req, List<String> branches, String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
			String refNo=refShortCode +"-"  +seqNo.generateRefNo();
			String quoteNo  = "Q"+ generateQuoteNo();
			String customerId = "C-" + generateCustId();
			String custRefNo = "Cust-" +   generateCustRefNo() ; 
            //Copy Quote E service Building Details
			EserviceBuildingDetails savedata=eserviceBuildingCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
			res.setCommonResponse(savedata);
			res.setQuoteNo(quoteNo);
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public List<Tuple> copyCommonQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceCommonDetails> c = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c,
					cus.get("clientName").alias("clientName"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
		//	Predicate n3 = null;
		//	Predicate n4 = null;
			Predicate n5 = null;

			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			}
//
//			Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), loginId);
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("loginId"), loginId);
//				//Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			}
			
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			//query.where(n1,n2,n3,n4,n5).orderBy(orderList);
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

	public List<Tuple> copySectionQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceSectionDetails> c = query.from(EserviceSectionDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c,
					cus.get("clientName").alias("clientName"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
		//	Predicate n3 = null;
		//	Predicate n4 = null;
			Predicate n5 = null;

			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			}
//
//			Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), loginId);
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("loginId"), loginId);
//				//Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			}
			
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			//query.where(n1,n2,n3,n4,n5).orderBy(orderList);
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

public synchronized String generateQuoteNo() {
	try {
		SeqQuoteno entity;
		entity = quoteNoRepo.save(new SeqQuoteno());
		return String.format("%05d", entity.getQuoteNo());
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}

}

public synchronized String generateCustId() {
	try {
		SeqCustid entity;
		entity = custIdRepo.save(new SeqCustid());
		return String.format("%05d", entity.getCustId());
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}

}
			
// Eservice Motor Copy Quote
public EserviceBuildingDetails eserviceBuildingCopyquote(CopyQuoteReq req, String refNo, List<String> branches,
		String loginId, String customerId, String quoteNo, String custRefNo) {
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	EserviceBuildingDetails savedata = new EserviceBuildingDetails();
	try {
		String userType = req.getUserType();
		String branchCode = "";

		List<EserviceBuildingDetails> motor = null;
		Integer count=0;
		List<Object> list=getMasterTableCount(req.getPolicyNo());
		if (list.size() > 0) {
			count = list.size();
		}
		String prevPolicyNo = null;
		String prevQuoteNo = null;
		String newRequestNo = null;
		String newQuoteNo = null;
		String newCustRefNo = null;
		String newCustId = null;
		long pendingcount = 0;
		if (count > 0) {
			List<EserviceBuildingDetails> motors = repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(), 1);
			pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
			if (pendingcount > 0) {
				 List<EserviceBuildingDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
				if (!pendingData.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
					deletePreviousEndo(req, pendingData);
					count--;
					pendingcount = 0;
				}
			}
		}
		if (count > 0) {
			List<EserviceBuildingDetails> motors = repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(), 1);
			// Compare
			motors.sort(new Comparator<EserviceBuildingDetails>() {
				@Override
				public int compare(EserviceBuildingDetails o1, EserviceBuildingDetails o2) {
					// TODO Auto-generated method stub
					return o1.getEndtCount().compareTo(o2.getEndtCount());
				}
			}.reversed());

			pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
			if (pendingcount > 0) {
				List<EserviceBuildingDetails> pendingData = motors.stream().filter(m -> m.getEndtStatus().equals("P"))
						.collect(Collectors.toList());
				motor = pendingData;
				prevPolicyNo = motor.get(0).getEndtPrevPolicyNo();
				prevQuoteNo = motor.get(0).getEndtPrevQuoteNo();
				newRequestNo = motor.get(0).getRequestReferenceNo();
				newQuoteNo = motor.get(0).getQuoteNo();
				newCustRefNo = motor.get(0).getCustomerReferenceNo();
				newCustId = motor.get(0).getCustomerId();
				count--;
			} else {
				motor = motors;

				if (motors.size() > 1) {
					prevPolicyNo = motors.get(0).getPolicyNo();
					prevQuoteNo = motors.get(0).getQuoteNo();
				} else {
					//prevPolicyNo = req.getPolicyNo();
					prevPolicyNo =motor.get(0).getPolicyNo();
					prevQuoteNo = motor.get(0).getQuoteNo();
				}
			}

		} else {
			motor = repo.findByPolicyNoAndStatus(req.getPolicyNo(), "P");
			prevPolicyNo = req.getPolicyNo();
			prevQuoteNo = motor.get(0).getQuoteNo();
		}

		if (pendingcount == 0) {
			newRequestNo = refNo;
			newCustRefNo = custRefNo;
			newCustId = customerId;
		}

//					List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//							branches);
		List<EserviceBuildingDetails> motors = repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
		++count;
		if (motors.size() > 0) {
			for (EserviceBuildingDetails data : motors) {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); 
						/*endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
				savedata = dozerMapper.map(data, EserviceBuildingDetails.class);
				savedata.setEntryDate(new Date());
				savedata.setCreatedBy(req.getLoginId());
				savedata.setUpdatedBy(req.getLoginId());
				savedata.setUpdatedDate(new Date());
				savedata.setRequestReferenceNo(newRequestNo);
				//savedata.setCustomerReferenceNo(newCustRefNo);
				savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
				savedata.setCustomerId(newCustId);
				savedata.setOldReqRefNo(req.getRequestReferenceNo());
//				if (req.getUserType().equalsIgnoreCase("Broker") || (req.getUserType().equalsIgnoreCase("User"))) {
//					branchCode = req.getBranchCode();
//					savedata.setApplicationId("1");
//					savedata.setBrokerBranchCode(branchCode);
//
//				} else if ("issuer".equalsIgnoreCase(userType)) {
//					savedata.setApplicationId(req.getLoginId());
//					branchCode = req.getBranchCode();
//					savedata.setBranchCode(branchCode);
//				}
				savedata.setApplicationId(data.getApplicationId());
				savedata.setBrokerBranchCode(data.getBrokerBranchCode());
				
				savedata.setActualPremiumFc(BigDecimal.ZERO);
				savedata.setActualPremiumLc(BigDecimal.ZERO);
				savedata.setOverallPremiumFc(BigDecimal.ZERO);
				savedata.setOverallPremiumLc(BigDecimal.ZERO);
				if (pendingcount == 0) {
					savedata.setQuoteNo(quoteNo);
				} else {
					savedata.setQuoteNo(newQuoteNo);
				}
				savedata.setOriginalPolicyNo(req.getPolicyNo());
				savedata.setEndorsementDate(new Date());
				savedata.setEndorsementRemarks(req.getEndtRemarks());
				savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
				savedata.setEndtPrevPolicyNo(prevPolicyNo);
				//savedata.setEndtPrevPolicyNo(req.getPolicyNo()+"-"+ count);
				savedata.setEndtPrevQuoteNo(prevQuoteNo);
				savedata.setEndtCount(new BigDecimal(count));
				savedata.setEndtStatus("P");
				savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
				savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
				savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
				savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				savedata.setStatus("E");
				savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
				repo.saveAndFlush(savedata);
			}

		}
		System.out.println("*************EserviceBuildingDetails************");
		System.out.println("Old QUOTE NO:"+prevQuoteNo);
		System.out.println("Old Customer Id:"+customerId);
		System.out.println("Old Reference No:"+refNo);
		System.out.println("QUOTE NO:"+quoteNo);
		System.out.println("New Customer Id:"+newCustId);
		System.out.println("Reference No:"+newRequestNo);
		System.out.println("prevPolicyNo:"+prevPolicyNo);
		System.out.println("PreQuoteNo:"+prevQuoteNo);
		System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
		System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
		System.out.println("**********************************************");
		if (pendingcount == 0) {
			// Copy Quote Home Position Master
			homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId, prevPolicyNo, prevQuoteNo, count, custRefNo);

			// Copy Quote Personal Info
			personolInfoEndoCopyQuote(req, customerId, prevPolicyNo, prevQuoteNo, count, custRefNo);

			// Copy Quote Policy Cover Data
			policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo, prevQuoteNo, count);

			// Copy COVER_DOCUMENT_UPLOAD_DETAILS
			coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo,
					count);

//			// Copy ESERVICE_CUSTOMER_DETAILS
//			eserviceCustDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo, count,
//					custRefNo);

			// Copy ESERVICE_COMMON_DETAILS
			eserviceCommonDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo,
					count, custRefNo);

			// Copy ESERVICE_SECTION_DETAILS
			eserviceSectionDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			
			// Copy Section Data Details
			sectionDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy BUILDING_DETAILS
			buildingDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy Common Data Details
			commonDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			
			// Copy PERSONAL_ACCIDENT/ProductEmployeeDetails
			productEmpDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
		
			// Copy CONDENT_AND_ALLRISK
			contentAndRiskEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy BUILDING_RISK_DETAILS
			buildingRiskDetailsCopyQuote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

		}
		// res.setRequestReferenceNo(newRequestNo);
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
				//List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Object> query = cb.createQuery(Object.class);
				//Find all
				Root<EserviceBuildingDetails> b = query.from(EserviceBuildingDetails.class);
				// Select
				query.multiselect(b.get("policyNo").alias("policyNo"));
							
				Predicate n1 = cb.equal(b.get("originalPolicyNo"),policyNo);
				query.where(n1).groupBy(b.get("policyNo"));
				
				// Get Result
				TypedQuery<Object> result = em.createQuery(query);
				list = result.getResultList();
				
			}
			catch(Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
			return list;
		}

//Building Risk Details
private CopyQuoteSuccessRes buildingRiskDetailsCopyQuote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	BuildingRiskDetails savedata = new BuildingRiskDetails();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); 
				/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
				req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
				Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
		List<BuildingRiskDetails> buildingRiskData=buildRiskRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
		if (buildingRiskData!=null) {
			for(BuildingRiskDetails data :buildingRiskData) {
				savedata = dozerMapper.map(data, BuildingRiskDetails.class);
				savedata.setEntryDate(new Date());
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
				buildRiskRepo.saveAndFlush(savedata);
			}
		}
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
	
}


//Building Details
private CopyQuoteSuccessRes buildingDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	BuildingDetails savedata = new BuildingDetails();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());  /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
				req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
				Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
		List<BuildingDetails> buildingData=buildingRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
		if (buildingData!=null) {
			for(BuildingDetails data :buildingData) {
				savedata = dozerMapper.map(data, BuildingDetails.class);
				savedata.setEntryDate(new Date());
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
				buildingRepo.saveAndFlush(savedata);
			}
		}
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
	
}

//Content and Risk
private CopyQuoteSuccessRes contentAndRiskEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	ContentAndRisk savedata = new ContentAndRisk();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
				req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
				Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
		
		List<ContentAndRisk> content = contentRiskRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
		if (content != null && content.size() > 0) {
			for (ContentAndRisk data : content) {
				savedata = dozerMapper.map(data, ContentAndRisk.class);
				savedata.setEntryDate(new Date());
				savedata.setRequestReferenceNo(refNo);
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
				contentRiskRepo.saveAndFlush(savedata);
			}
		}
	
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
// Common Data Details
private CopyQuoteSuccessRes commonDataDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
		String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,
		String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	CommonDataDetails savedata = new CommonDataDetails();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(), req.getProductId(),
				req.getEndtTypeId()); /*
										 * endtTypeRepo
										 * .findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
										 * req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
										 * Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
										 */

		List<CommonDataDetails> eserSec = commonDataRepo.findByQuoteNo(prevQuoteNo);
		if (eserSec != null && eserSec.size() > 0) {
			for (CommonDataDetails data : eserSec) {
				savedata = dozerMapper.map(data, CommonDataDetails.class);
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
				commonDataRepo.saveAndFlush(savedata);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;

}
//Personal Accident And Personal Indem
private CopyQuoteSuccessRes productEmpDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	ProductEmployeeDetails savedata = new ProductEmployeeDetails();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/* endtTypeRepo
				.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

		List<ProductEmployeeDetails> PA = pARepo.findByQuoteNo(prevQuoteNo);
		if (PA != null && PA.size() > 0) {
			for (ProductEmployeeDetails data : PA) {
				savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
				savedata.setEntryDate(new Date());
				savedata.setRequestReferenceNo(refNo);
				savedata.setQuoteNo(quoteNo);
				savedata.setCreatedBy(loginId);
			//	savedata.setUpdatedBy(loginId);
		//		savedata.setUpdatedDate(new Date());
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
				pARepo.saveAndFlush(savedata);
			}
		}
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

			private CopyQuoteSuccessRes eserviceCommonDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				EserviceCommonDetails savedata = new EserviceCommonDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
							req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
							Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
					
					List<EserviceCommonDetails> commData = eserCommonRepo.findByQuoteNo(prevQuoteNo);
					if (commData!=null && commData.size()>0 ) 
						for(EserviceCommonDetails commData1:commData) {
							savedata = dozerMapper.map(commData1, EserviceCommonDetails.class);
							savedata.setEntryDate(new Date());
							savedata.setQuoteNo(quoteNo);
							savedata.setCustomerId(customerId);
							savedata.setRequestReferenceNo(refNo);
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
							savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
							eserCommonRepo.saveAndFlush(savedata);
				
						}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

				
			}


			//Delete Previous Endo
			private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceBuildingDetails> motors) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				try {
					String quoteNo=motors.get(0).getQuoteNo();
					// Delete Old Record
					
					//E service Motor Details
					if(motors.size()>0) {
						repo.deleteAll(motors);
					}
					//E service Customer Details 
					HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(quoteNo);
					String customerId=homeData.getCustomerId();
					
					PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
//					EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
//					if (custData!=null) {
//						custRepo.delete(custData);
//					}
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
					
					//Cover Document Upload Details
					List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
					if (coverDocList.size() > 0) {
						coverDocUploadDetails.deleteAll(coverDocList);
					}
					
					//Eservice Section Details
					List<EserviceSectionDetails> sectionList = eserSecRepo.findByQuoteNo(quoteNo);
					if (sectionList.size() > 0) {
						eserSecRepo.deleteAll(sectionList);
					}
					
					// Section Data Details
					List<SectionDataDetails> secDataList = sectionDataRepo.findByQuoteNo(quoteNo);
					if (secDataList.size() > 0) {
						sectionDataRepo.deleteAll(secDataList);
					}
					
					//Eservice Common Details
					List<EserviceCommonDetails> commonList = eserCommonRepo.findByQuoteNo(quoteNo);
					if (commonList!=null&& commonList .size()>0) {
						eserCommonRepo.deleteAll(commonList);
					}
					//Building Details
					List<BuildingDetails> buildingList = buildingRepo.findByQuoteNo(quoteNo);
					if (buildingList.size() > 0) {
						buildingRepo.deleteAll(buildingList);
					}
					//Personal Accident
					List<ProductEmployeeDetails> perList = pARepo.findByQuoteNo(quoteNo);
					if (perList.size() > 0) {
						pARepo.deleteAll(perList);
					}
					//Content And Risk
					List<ContentAndRisk> contentandrisk = contentRiskRepo.findByQuoteNo(quoteNo);
					if (contentandrisk.size() > 0) {
						contentRiskRepo.deleteAll(contentandrisk);
					}
					//Building And Risk
					BuildingRiskDetails buildingRiskDetails = buildRiskRepo.findByQuoteNo(quoteNo);
					if (buildingRiskDetails!= null) {
						buildRiskRepo.delete(buildingRiskDetails);
					}
					//CommonDataDetals
					List<CommonDataDetails> commonData = commonDataRepo.findByQuoteNo(quoteNo);
					if (commonData.size() > 0) {
						commonDataRepo.deleteAll(commonData);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

			}

	/*	private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
					String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,
					String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				EserviceCustomerDetails savedata = new EserviceCustomerDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = endtTypeRepo
							.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
									req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
									Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
					HomePositionMaster homeData = homePosistionRepo.findByQuoteNo(prevQuoteNo);
					String olsCustomerId = homeData.getCustomerId();

					PersonalInfo personalInfoData = personalInforepo.findByCustomerId(olsCustomerId);
					EserviceCustomerDetails custData = custRepo
							.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
					if (custData != null)
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
					// savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
					custRepo.saveAndFlush(savedata);

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

			}
*/
			@Transactional
			public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
			String prevQuoteNo,Integer count,String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				HomePositionMaster savedata = new HomePositionMaster();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				
				try {
					EndtTypeMaster entMaster= ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());*/
					String endtFeeYn=entMaster.getEndtFeeYn()	;
					BigDecimal endtPre=BigDecimal.ZERO;
					BigDecimal endtPremiumtax=BigDecimal.ZERO;
					Double endtPercent=0d;
					HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
					Double tax=Double.valueOf(homeData.getVatPercent().toString());
					BigDecimal exchangeRate=homeData.getExchangeRate();
					BigDecimal overAllPremiumFc=new BigDecimal(homeData.getOverallPremiumFc().toString());
					if ("Y".equalsIgnoreCase(endtFeeYn)) {
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
					if("Y".equalsIgnoreCase(endtFeeYn)){
						savedata.setEndtPremium(endtPre);
						savedata.setIsChargRefund(txt);
						savedata.setEndtPremiumTax(endtPremiumtax);
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
				//	savedata.setCustomerReferenceNo(custRefNo);
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

			// Policy Cover Data Enst Copy Quote
			public CopyQuoteSuccessRes policyCoverDataEndocopyQuote(CopyQuoteReq req, String refNo, String quoteNo,
					String loginId, String prevPolicyNo, String prevQuoteNo, Integer count) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				PolicyCoverData savedata = new PolicyCoverData();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					CoverMaster coverdata = null;
					EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/* endtTypeRepo
							.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
									req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
									Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
					String endTypeDesc = entMaster.getEndtTypeDesc();
					String endtFeeYn = entMaster.getEndtFeeYn();
					String coverDesc = "";
					BigDecimal endtFee=BigDecimal.ZERO;
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
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;
			}

			// Cover Document Upload Details Enst Copy Quote
			public CopyQuoteSuccessRes coverDocumentUploadDetailsEndoCopyquote(CopyQuoteReq req, String refNo,
					String quoteNo, String customerId, String loginId, String prevPolicyNo, String prevQuoteNo,
					Integer count) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				DocumentTransactionDetails savedata = new DocumentTransactionDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
							req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
							Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
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

}

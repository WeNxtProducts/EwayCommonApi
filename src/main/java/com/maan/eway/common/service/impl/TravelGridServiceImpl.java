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

import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.TravelPassengerDetails;

import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.CopyQuoteReq;

import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.SuccessRes;

import io.swagger.v3.oas.annotations.servers.Server;

@Transactional
@Service
public class TravelGridServiceImpl implements  TravelGridService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EserviceTravelDetailsRepository repo;
	
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
	private CoverDocumentUploadDetailsRepository coverDocUploadDetails;

	

	
	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	@Override
	public List<QuoteCriteriaRes> getTravelExistingQuoteDetails(ExistingQuoteReq req, List<String> branches, Date startDate, Date endDate, Integer limit, Integer offset) {
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			
			
		
			// Select
			query.multiselect( m.get("totalPassengers").as(Long.class).alias("idsCount"),
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				   m.get("requestReferenceNo").alias("requestReferenceNo") , 
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate"));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("travelStartDate")));
			
			
		    // Where	
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 =  null ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				n7 = cb.equal(  m.get("loginId"),  req.getLoginId());
			} else {
				n7 = cb.equal(  m.get("applicationId"),  req.getApplicationId());
			}
			
			Predicate n8 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				Expression<String>e0=m.get("brokerBranchCode");
				 n8 = e0.in(branches ) ;
			} else {
				Expression<String>e0=m.get("branchCode");
				n8 = e0.in(branches ) ;
			}
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9)
			.orderBy(orderList) ;
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
			existingQuotes = existingQuotes.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return existingQuotes;
	}

	@Override
	public List<QuoteCriteriaRes> getTravelLapsedQuoteDetails(ExistingQuoteReq req, List<String> branches, Date before30, int limit, int offset) {
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			
			// Select
			query.multiselect(  m.get("totalPassengers").as(Long.class).as(Long.class).alias("idsCount"),
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				   m.get("requestReferenceNo").alias("requestReferenceNo") , 
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate")
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("travelStartDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			
			Predicate n6 =  null ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				n6 = cb.equal(  m.get("loginId"),  req.getLoginId());
			} else {
				n6 = cb.equal(  m.get("applicationId"),  req.getApplicationId());
			}
			Predicate n7 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				Expression<String>e0=m.get("brokerBranchCode");
				 n7 = e0.in(branches ) ;
			} else {
				Expression<String>e0=m.get("branchCode");
				n7 = e0.in(branches ) ;
			}
			
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
			lapsedQuotes = lapsedQuotes.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return lapsedQuotes;
	}

	@Override
	public List<RejectCriteriaRes> getTravelRejectedQuoteDetails(ExistingQuoteReq req, List<String> branches, int limit,	int offset) {
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
		
			// Select
			query.multiselect(  m.get("totalPassengers").as(Long.class).alias("idsCount"),
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				   m.get("requestReferenceNo").alias("requestReferenceNo") , 
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate"), m.get("rejectReason").alias("rejectReason")
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("travelStartDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"R" );
			
			Predicate n5 =  null ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				n5 = cb.equal(  m.get("loginId"),  req.getLoginId());
			} else {
				n5 = cb.equal(  m.get("applicationId"),  req.getApplicationId());
			}
			Predicate n6 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				Expression<String>e0=m.get("brokerBranchCode");
				 n6 = e0.in(branches ) ;
			} else {
				Expression<String>e0=m.get("branchCode");
				n6 = e0.in(branches ) ;
			}
			
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			rejectedQuotes = rejectedQuotes.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return rejectedQuotes;
	}

	
	@Override
	public synchronized  List<ReferalGridCriteriaRes> getTravelReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,	int offset, String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			
			// Select
			query.multiselect(  m.get("totalPassengers").as(Long.class).alias("idsCount"),
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				   m.get("requestReferenceNo").alias("requestReferenceNo") , 
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate") , m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks"),
					m.get("referalRemarks").alias("referalRemarks")
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("travelStartDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			
			Predicate n5 =  null ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				n5 = cb.equal(  m.get("loginId"),  req.getLoginId());
			} else {
				n5 = cb.equal(  m.get("applicationId"),  req.getApplicationId());
			}
			Predicate n6 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				Expression<String>e0=m.get("brokerBranchCode");
				 n6 = e0.in(branches ) ;
			} else {
				Expression<String>e0=m.get("branchCode");
				n6 = e0.in(branches ) ;
			}
			Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
			referrals = referrals.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referrals;
	}




	@Override
	public synchronized List<ReferalGridCriteriaRes> getTravelAdminReferalDetails(ExistingQuoteReq req, List<String> branches, int limit,
			int offset , String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			
			// Select
			query.multiselect(  m.get("totalPassengers").as(Long.class).alias("idsCount"),
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				   m.get("requestReferenceNo").alias("requestReferenceNo") , 
					cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
					cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate") , m.get("rejectReason").alias("rejectReason")
					,
					m.get("adminRemarks").alias("adminRemarks"),
					m.get("referalRemarks").alias("referalRemarks"));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("travelStartDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			
			Expression<String>e0=c.get("branchCode");
			Predicate n6 = e0.in(branches ) ;
			Predicate n7 = cb.isNull(m.get("endorsementType"));
			query.where(n1,n2,n3,n4,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
			referrals = referrals.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referrals;
	}
	@Override
	public CopyQuoteSuccessRes travelCopyQuote(CopyQuoteReq req, List<String> branches,String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		EserviceTravelDetails savedata = new EserviceTravelDetails();
		
		try {
			String searchValue = req.getRequestReferenceNo();
			String searchKey = "RequestReferenceNo";
			String companyId = req.getInsuranceId();
			String userType=req.getUserType();
			String branchCode="";
			List<Tuple> list = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
	
			String refNo=req.getRequestReferenceNo();
			String refShortCode = motorService.getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
	        refNo = refShortCode + seqNo.generateRefNo() ; 
            
			if(list.size()>0) {
				for (Tuple data : list) {
				savedata=dozerMapper.map(data.get(0),EserviceTravelDetails.class);
				
				savedata.setEntryDate( new Date());
				savedata.setCreatedBy(req.getLoginId());
				savedata.setUpdatedBy(req.getLoginId());
				savedata.setUpdatedDate(new Date());
				savedata.setRequestReferenceNo(refNo);
				savedata.setOldReqRefNo(req.getRequestReferenceNo());
				if (req.getUserType().equalsIgnoreCase("Broker") || ( req.getUserType().equalsIgnoreCase("User"))) {  
					branchCode=req.getBranchCode();
					savedata.setApplicationId("1");
					savedata.setBrokerBranchCode(branchCode);
					
				}else if ("issuer".equalsIgnoreCase(userType)) {
					savedata.setApplicationId(req.getLoginId());
					 branchCode=req.getBranchCode();
					 savedata.setBranchCode(branchCode);
				}
				
				savedata.setActualPremiumFc(BigDecimal.ZERO);
				savedata.setActualPremiumLc(BigDecimal.ZERO);
				savedata.setOverallPremiumFc(BigDecimal.ZERO);
				savedata.setOverallPremiumLc(BigDecimal.ZERO);
				repo.saveAndFlush(savedata);
				}	
	//			res.setResponse("Successfully Updated");
				res.setRequestReferenceNo(refNo);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	@Override
	public List<Tuple> searchTravelQuote(CopyQuoteReq req, List<String> branches) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
	try {
		// Search 
		String searchKey = req.getSearchKey();
		String searchValue = req.getSearchValue();
		String companyId = req.getInsuranceId();
		String loginId=req.getLoginId();
		String userType=req.getUserType();
		
			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {	
			searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {	
				searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("ClientName".equalsIgnoreCase(searchKey)) {	
				searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {	
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("RegistrationNumber".equalsIgnoreCase(searchKey)) {
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("EntryDate".equalsIgnoreCase(searchKey)) {
				Date entryDate=sdf.parse(searchValue);
				searchValue=sdf.format(entryDate);
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			} 
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return searchQuote;
}
	public List<Tuple> searchDetails(String searchKey,String searchValue,String companyId, String loginId,String userType,List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			 
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceTravelDetails> c = query.from(EserviceTravelDetails.class);
		
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			query.multiselect(c.alias("c") ,cus.get("clientName").alias("clientName"),cb.count(c).alias("idsCount"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));


			Predicate n1 = null;
			Predicate n3 = null;
			Predicate n4 = null;
			Predicate n5 = null;
			
			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.like(cb.lower(c.get("requestReferenceNo")), searchValue );
			} else if (searchKey.equalsIgnoreCase("CustomerReferenceNo")) {
				n1 = cb.like(cb.lower(c.get("customerReferenceNo")), searchValue );
			} else if (searchKey.equalsIgnoreCase("RegistrationNumber")) {
				n1 = cb.like(cb.lower(c.get("registrationNumber")),searchValue );
			} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
				n1 = cb.like(cb.lower(c.get("quoteNo")),  searchValue  );
			} else if (searchKey.equalsIgnoreCase("EntryDate")) {
				n1 = cb.like(cb.lower(c.get("entryDate").as(String.class)),"%"+ searchValue+"%" );
			} else if (searchKey.equalsIgnoreCase("ChassisNumber")) {
				n1 = cb.like(cb.lower(c.get("chassisNumber")),  searchValue );
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
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			if (searchKey.equalsIgnoreCase("ClientName")) {
				query.where(n1, n2,n4,n5).orderBy(orderList);
			}

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
	public  List<ListItemValue> getTravelCoptyQuotetListItem(CopyQuoteDropDownReq req ,String itemType) {
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
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public CopyQuoteSuccessRes travelEndt(CopyQuoteReq req, List<String> branches, String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
			String refNo=refShortCode +seqNo.generateRefNo();
			String quoteNo  = "Q"+ generateQuoteNo();
			String customerId = "C-" + generateCustId();
			String custRefNo = "Cust-" +   generateCustRefNo() ; 
            //Copy Quote E service Travel Details
			EserviceTravelDetails savedata=eserviceTravelCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
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
	public EserviceTravelDetails eserviceTravelCopyquote(CopyQuoteReq req, String refNo, List<String> branches,String loginId,String customerId,String quoteNo,String custRefNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceTravelDetails savedata = new EserviceTravelDetails();
		try {
			String userType = req.getUserType();
			String branchCode = "";

			List<EserviceTravelDetails> motor=null;
			Integer count=0;
			count=repo.countByOriginalPolicyNoAndRiskId(req.getPolicyNo(),1);
			String prevPolicyNo=null;
			String prevQuoteNo=null;
			String newRequestNo =null;
			String newQuoteNo =null;
			String newCustRefNo=null;
			String newCustId=null;
			long pendingcount =0;
			if (count > 0) {
				List<EserviceTravelDetails> motors = repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(), 1);
				pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
				if (pendingcount > 0) {
					if (!motors.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
						deletePreviousEndo(req,motors);
						count--;
						pendingcount=0;
					}
				}
			}
			if(count>0) {
				List<EserviceTravelDetails> motors=repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(),1);
				//Compare
				motors.sort(new Comparator<EserviceTravelDetails>() {
					@Override
					public int compare(EserviceTravelDetails o1, EserviceTravelDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceTravelDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
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
			{
				newRequestNo=refNo;
				newCustRefNo=custRefNo;
				newCustId=customerId;
			}	
			
//			List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//					branches);
			List<EserviceTravelDetails> motors=repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			++count;
			if (motors.size() > 0) {
				for (EserviceTravelDetails data : motors) {
					EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
					savedata = dozerMapper.map(data, EserviceTravelDetails.class);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getLoginId());
					savedata.setUpdatedBy(req.getLoginId());
					savedata.setUpdatedDate(new Date());
					savedata.setRequestReferenceNo(newRequestNo);
					savedata.setCustomerReferenceNo(newCustRefNo);
					savedata.setCustomerId(newCustId);
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
	
			if (pendingcount == 0) {
				// Copy Quote Home Position Master
				 homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

				// Copy Quote Personal Info
				 personolInfoEndoCopyQuote(req, customerId, prevPolicyNo,prevQuoteNo, count,custRefNo);

				// Copy Quote Policy Cover Data
				 policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo,prevQuoteNo, count);

				// Copy COVER_DOCUMENT_UPLOAD_DETAILS
				coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count);
			
				// Copy ESERVICE_CUSTOMER_DETAILS
				eserviceCustDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			}
			//res.setRequestReferenceNo(newRequestNo);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return savedata;
	}

	//Delete Previous Endo
	private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceTravelDetails> motors) {
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
			EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
			if (custData!=null) {
				custRepo.delete(custData);
			}
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
			List<CoverDocumentUploadDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
			if (coverDocList.size() > 0) {
				coverDocUploadDetails.deleteAll(coverDocList);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}

	private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
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

		
	}

	//Home Position Master Endt Copy Quote
	public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
	String prevQuoteNo,Integer count,String custRefNo) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		HomePositionMaster savedata = new HomePositionMaster();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
	try {
		EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
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
			String prevQuoteNo,Integer count,String custRefNo) {
		CopyQuoteSuccessRes res =new CopyQuoteSuccessRes();
		PersonalInfo savedata = new PersonalInfo();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster=endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
			String olsCustomerId=homeData.getCustomerId();
			
			PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
			savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
			savedata.setCustomerId(customerId);
			savedata.setCustomerReferenceNo(custRefNo);
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
	//
	// Motor Data Details Enst Copy Quote
	public CopyQuoteSuccessRes coverDocumentUploadDetailsEndoCopyquote(CopyQuoteReq req, String refNo,
			String quoteNo, String customerId, String loginId, String prevPolicyNo, String prevQuoteNo,
			Integer count) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		CoverDocumentUploadDetails savedata = new CoverDocumentUploadDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
					req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
					Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
			List<CoverDocumentUploadDetails> motorData = coverDocUploadDetails.findByQuoteNo(req.getQuoteNo());
			if (motorData.size() > 0) {
				for (CoverDocumentUploadDetails data : motorData) {
					savedata = dozerMapper.map(data, CoverDocumentUploadDetails.class);
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

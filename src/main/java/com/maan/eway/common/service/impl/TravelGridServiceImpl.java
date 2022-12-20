package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.TravelGridService;

import io.swagger.v3.oas.annotations.servers.Server;

@Transactional
@Service
public class TravelGridServiceImpl implements  TravelGridService {

	@PersistenceContext
	private EntityManager em;
	
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
			
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
			
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
			
		    // Where	
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			
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
			query.where(n1,n2,n3,n4,n5,n6,n7,n8)
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
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
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
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
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
	public List<QuoteCriteriaRes> getTravelReferalPendingDetails(ExistingQuoteReq req, List<String> branches, int limit,	int offset) {
		List<QuoteCriteriaRes> referralPendings = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RP" );
			
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
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referralPendings = result.getResultList();
			referralPendings = referralPendings.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralPendings;
	}

	@Override
	public List<QuoteCriteriaRes> getTravelReferalApprovedDetails(ExistingQuoteReq req, List<String> branches, int limit,	int offset) {
		List<QuoteCriteriaRes> referralApproved = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RA" );
			
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
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referralApproved = result.getResultList();
			referralApproved = referralApproved.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralApproved;
	}

	@Override
	public List<RejectCriteriaRes> getTravelReferalRejectedDetails(ExistingQuoteReq req, List<String> branches, int limit,	int offset) {
		List<RejectCriteriaRes> referralApproved = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
					m.get("travelEndDate").alias("policyEndDate") , m.get("rejectReason").alias("rejectReason") 
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RR" );
			
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
			referralApproved = result.getResultList();
			referralApproved = referralApproved.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralApproved;
	}

	@Override
	public List<QuoteCriteriaRes> getTravelAdminReferalPendings(ExistingQuoteReq req, List<String> branches, int limit,	int offset) {
		List<QuoteCriteriaRes> referralApproved = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
					m.get("travelEndDate").alias("policyEndDate")
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RP" );
			
			Expression<String>e0=c.get("branchCode");
			Predicate n6 = e0.in(branches ) ;
			
			query.where(n1,n2,n3,n4,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referralApproved = result.getResultList();
			referralApproved = referralApproved.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralApproved;
	}

	@Override
	public List<QuoteCriteriaRes> getTravelAdminReferalApproved(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		List<QuoteCriteriaRes> referralApproved = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RA" );
			
			Expression<String>e0=c.get("branchCode");
			Predicate n6 = e0.in(branches ) ;
			
			query.where(n1,n2,n3,n4,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referralApproved = result.getResultList();
			referralApproved = referralApproved.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralApproved;
	}

	@Override
	public List<RejectCriteriaRes> getTravelAdminReferalRejected(ExistingQuoteReq req, List<String> branches, int limit,
			int offset) {
		List<RejectCriteriaRes> referralApproved = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Total Count Filter
			Subquery<Long> totalCount = query.subquery(Long.class);
			Root<TravelPassengerDetails> t = totalCount.from(TravelPassengerDetails.class);
			totalCount.select(cb.count(t.get("quoteNo")));
			Predicate a1 = cb.equal(t.get("quoteNo"),m.get("quoteNo") );
			totalCount.where(a1);
					
			// Select
			query.multiselect(  totalCount.alias("idsCount"),
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
					);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"RR" );
			
			Expression<String>e0=c.get("branchCode");
			Predicate n6 = e0.in(branches ) ;
			
			query.where(n1,n2,n3,n4,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referralApproved = result.getResultList();
			referralApproved = referralApproved.stream().filter( o -> ! o.getIdsCount().equals(0L) ).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return referralApproved;
	}

}

package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceLifeDetails;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.LifeGridService;

@Service
@Transactional
public class LifeGridServiceImple implements LifeGridService {
	
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(LifeGridServiceImple.class);

	@Override
	public QuoteCriteriaResponse getLifeExistingQuoteDetails(ExistingQuoteReq req, Date startDate, Date endDate, int limit,
			int offset) {
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		
		try {
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		

			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					 m.get("productDesc").alias("productName"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
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
			Predicate n11 = null;
			 
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
		
			query.where(n1, n2, n3, n4, n5, n6, n7,n11, n8,n9,n10).orderBy(orderList);	
		
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
		
			resp.setQuoteRes(existingQuotes);
			resp.setTotalCount(totalcountexisting(req, startDate,endDate, "Y"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountexisting(ExistingQuoteReq req, Date startDate, Date endDate, String string) {
		Long count = 0l;
		try {
			
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
	
			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			query.multiselect(cb.count(m));			
	
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
			Predicate n11 = null;
			 
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}
	
			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
		
			query.where(n1, n2, n3, n4, n5, n6, n7,n11, n8,n9,n10).orderBy(orderList);	
			
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			
			if(list.size()>0)
				count = list.get(0);
			
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return count;
	}

	@Override
	public List<GetExistingBrokerListRes> getLifeExistingDropdown(ExistingBrokerUserListReq req, Date today,
			Date before30) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
		if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
			query.multiselect(m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m.get("sourceType").alias("type"));

			// Find All
			Subquery<Long> agencyCode = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
			agencyCode.select(ocpm1.get("agencyCode"));
			Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
			Predicate a3 = cb.equal(ocpm1.get("status"), "Y");
			agencyCode.where(a1, a3);

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
			predics1.add(cb.equal(m.get("status"), "Y"));
			predics1.add(cb.equal(m.get("productId"), req.getProductId()));
			predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			predics1.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
			predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
			predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
			if ("Broker".equalsIgnoreCase(req.getUserType())) {
				predics1.add(cb.equal(m.get("brokerCode"), agencyCode));
			} else if ("User".equalsIgnoreCase(req.getUserType())) {
				predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
			}
			predics1.add(cb.isNotNull(m.get("sourceType")));
			predics1.add(cb.isNotNull(m.get("loginId")));
			query.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
			list = typedQuery1.getResultList();
			if (list != null && list.size() > 0) {
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
				
			for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);
				
						}
			}
			}else {
				
				resList = getExistingIssuerLife(req,today,before30); //Issuer
			}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return resList;

	}
	private List<GetExistingBrokerListRes> getExistingIssuerLife(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

			query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
					m.get("sourceType").alias("type"));
			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
			predics.add(cb.equal(m.get("status"), "Y"));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			predics.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
			predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
			predics.add(cb.isNotNull(m.get("bdmCode")));
			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
		
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}
			}
			{CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
			Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
			query1.multiselect (m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m1.get("sourceType").alias("type"));

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getApplicationId()));
			predics1.add(cb1.equal(m1.get("status"), "Y"));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
			predics1.add(cb1.greaterThanOrEqualTo(m1.get("updatedDate"), before30));
			predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), today));
			predics1.add(cb1.isNull(m1.get("bdmCode")));
			predics1.add(cb1.equal(us.get("loginId"),m1.get("loginId")));
			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<GetExistingBrokerListRes> getBrokerUserListLapsedLife(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(
						m.get("loginId").alias("code"),
						us.get("userName").alias("codeDesc"),
						m.get("sourceType").alias("type")).distinct(true);

				// Find All
				Subquery<Long> agencyCode = query.subquery(Long.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

				List<Predicate> predics1 = new ArrayList<Predicate>();
				predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
				predics1.add(cb.equal(m.get("status"), "Y"));
				predics1.add(cb.equal(m.get("productId"), req.getProductId()));
				predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
				predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
				predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), before30));
		//		predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("brokerCode"), agencyCode));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
				}
				predics1.add(cb.isNotNull(m.get("sourceType")));
				predics1.add(cb.isNotNull(m.get("loginId")));
				predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
				query.where(predics1.toArray(new Predicate[0]));

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
			}else {
				resList = getBrokerListLapsedLifeIssuer(req, today,  before30) ;  //Issuer
				
				
			}
			
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();

					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	private List<GetExistingBrokerListRes> getBrokerListLapsedLifeIssuer(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{ CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class); 
			 
			 query.multiselect(
					 m.get("bdmCode").alias("code"),
					 m.get("customerName").alias("codeDesc"),
					 m.get("sourceType").alias("type")
					 ).distinct(true) ;
			 
			 List<Predicate> predics = new ArrayList<Predicate>();
			 predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
			 predics.add(cb.equal(m.get("status"), "Y"));
			 predics.add(cb.equal(m.get("productId"), req.getProductId()));
			 predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			 predics.add(cb.isNotNull(m.get("bdmCode")));
			 
			predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), before30)); //lapsed
			predics.add(cb.isNotNull(m.get("sourceType")));
			 
			 query.where(predics.toArray(new Predicate[0]));
			 
			 TypedQuery<Tuple> typedQuery = em.createQuery(query);
			 list=  typedQuery.getResultList();
			 list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			 
			 if(list!=null && list.size()>0) {
				 
				 for(Tuple data : list) {
					 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					 res.setCode(data.get("code")==null?"":	data.get("code").toString());
					 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
					 res.setType(data.get("type")==null?"":	data.get("type").toString());
					 resList.add(res);
				
				 }
			 }	
			}
			{
			 CriteriaBuilder cb1 = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class); 
			 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
			 
			 query1.multiselect(
					
					 m1.get("loginId").alias("code"),
					 us.get("userName").alias("codeDesc"),
					 m1.get("sourceType").alias("type")
					 ).distinct(true) ;
			 
			 List<Predicate> predics1 = new ArrayList<Predicate>();
			 predics1.add(cb1.equal(m1.get("applicationId"),req.getApplicationId()));
			 predics1.add(cb1.equal(m1.get("status"), "Y"));
			 predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			 predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			 predics1.add(cb1.isNull(m1.get("bdmCode")));
			 
			predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
			predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), before30));
			predics1.add(cb1.isNotNull(m1.get("sourceType")));
			predics1.add(cb1.isNotNull(m1.get("loginId")));
			predics1 .add(cb1.equal(us.get("loginId"), m1.get("loginId")));
			 query1.where(predics1.toArray(new Predicate[0]));
			 
			 TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			 list1=  typedQuery1.getResultList();
			 list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			
			 if(list1!=null && list1.size()>0) {
				 
				 for(Tuple data : list1) {
					 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					 res.setCode(data.get("code")==null?"":	data.get("code").toString());
					 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
					 res.setType(data.get("type")==null?"":	data.get("type").toString());
					 resList.add(res);
				
				 }
			 }	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList ;
	}

	@Override
	public QuoteCriteriaResponse getLifeLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit, int offset) {
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), 
					m.get("productId").alias("productId"),
					 m.get("productDesc").alias("productName"),
					 
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
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
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if (StringUtils.isNotBlank(req.getBdmCode())) {

				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());

			} else {

				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );

			query.where(n1, n2, n3, n4, n5,  n7, n8,n9,n10,n11).orderBy(orderList);
			

			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
		
			resp.setQuoteRes(lapsedQuotes);
			resp.setTotalCount(totalcountlapsedQuotes(req, before30));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountlapsedQuotes(ExistingQuoteReq req, Date before30) {

		Long count = 0l;
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

			query.multiselect(cb.count(m));

			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n7 = cb.equal(m.get("loginId"), req.getLoginId());
				n11 = cb.equal(m.get("applicationId"), req.getApplicationId());
				
			} else {
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n11 = cb.equal(m.get("loginId"), req.getLoginId());
				}
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );

			query.where(n1, n2, n3, n4, n5,  n7, n8,n9,n10,n11);

			TypedQuery<Long> result = em.createQuery(query);
			List<Long> val = result.getResultList();
				
					if(val.size()>0)
						count = val.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	
	}

	@Override
	public GetRejectedQuoteDetailsRes getLifeRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate , int limit,int offset) {

		GetRejectedQuoteDetailsRes resp = new GetRejectedQuoteDetailsRes();
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		

			// Select
			query.multiselect(
					
					c.get("customerReferenceNo").alias("customerReferenceNo"), 
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
					m.get("currency").alias("currency"));




			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "R");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			

			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10,n11).orderBy(orderList);

			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset); 
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			
			resp.setQuoteRes(rejectedQuotes);
			resp.setTotalCount(totalcountexisting(req, startDate,endDate, "R"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	
	}

	@Override
	public List<GetExistingBrokerListRes> getBrokerUserListLifeRejected(ExistingBrokerUserListReq req, Date today,
			Date before30) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(
						m.get("loginId").alias("code"),
						us.get("userName").alias("codeDesc"),
						m.get("sourceType").alias("type")).distinct(true);

				// Find All
				Subquery<Long> agencyCode = query.subquery(Long.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

				List<Predicate> predics1 = new ArrayList<Predicate>();
				predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
				predics1.add(cb.equal(m.get("status"), "R"));
				predics1.add(cb.equal(m.get("productId"), req.getProductId()));
				predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
				predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
				predics1.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
				predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("brokerCode"), agencyCode));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
				}
				predics1.add(cb.isNotNull(m.get("sourceType")));
				predics1.add(cb.isNotNull(m.get("loginId")));
				predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
				query.where(predics1.toArray(new Predicate[0]));

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
			} else {

				resList = getBrokerListRejectedLifeIssuer(req, today, before30); // Issuer

			}
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();

					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}

				
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	
	}

	private List<GetExistingBrokerListRes> getBrokerListRejectedLifeIssuer(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			 CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class); 
			 
			 query.multiselect(
					 m.get("bdmCode").alias("code"),
					 m.get("customerName").alias("codeDesc"),
					 m.get("sourceType").alias("type")
					 ).distinct(true) ;
			 
			 List<Predicate> predics = new ArrayList<Predicate>();
			 predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
			 predics.add(cb.equal(m.get("status"), "R"));
			 predics.add(cb.equal(m.get("productId"), req.getProductId()));
			 predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			 predics.add(cb.isNotNull(m.get("bdmCode")));
			 
			predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			predics.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
			predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
			predics.add(cb.isNotNull(m.get("sourceType")));
			 
			 query.where(predics.toArray(new Predicate[0]));
			 
			 TypedQuery<Tuple> typedQuery = em.createQuery(query);
			 list=  typedQuery.getResultList();
			 list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			
			 if(list!=null && list.size()>0) {
				 
				 for(Tuple data : list) {
					 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					 res.setCode(data.get("code")==null?"":	data.get("code").toString());
					 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
					 res.setType(data.get("type")==null?"":	data.get("type").toString());
					 resList.add(res);
				
				 }
			 }	
			 
			 CriteriaBuilder cb1 = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class); 
			 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class); 
			 query1.multiselect(
					 m1.get("loginId").alias("code"),
					 us.get("userName").alias("codeDesc"),
					 m1.get("sourceType").alias("type")
					 ).distinct(true) ;
			 
			 List<Predicate> predics1 = new ArrayList<Predicate>();
			 predics1.add(cb1.equal(m1.get("applicationId"),req.getApplicationId()));
			 predics1.add(cb1.equal(m1.get("status"), "R"));
			 predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			 predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			 predics1.add(cb1.isNull(m1.get("bdmCode")));
			 
			predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
			predics1.add(cb1.greaterThanOrEqualTo(m1.get("updatedDate"), before30));
			predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), today));
			predics1.add(cb1.isNotNull(m1.get("sourceType")));
			predics1.add(cb1.isNotNull(m1.get("loginId")));
			predics1.add(cb1.equal(us.get("loginId"), m1.get("loginId")));
			 query1.where(predics1.toArray(new Predicate[0]));
			 
			 TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			 list1=  typedQuery1.getResultList();
			 list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			 
			 if(list1!=null && list1.size()>0) {
				 
				 for(Tuple data : list1) {
					 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					 res.setCode(data.get("code")==null?"":	data.get("code").toString());
					 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
					 res.setType(data.get("type")==null?"":	data.get("type").toString());
					 resList.add(res);
				
				 }
			 }	
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList ;
	}


}

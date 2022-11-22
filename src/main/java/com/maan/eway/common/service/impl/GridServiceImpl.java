package com.maan.eway.common.service.impl;

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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.CriteriaCustomerRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.service.GridService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;

@Service
@Transactional
public class GridServiceImpl implements GridService {

	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(GridServiceImpl.class);

	
	@Override
	public List<EserviceCustomerDetailsRes> getallExistingQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			Date today = new Date() ;
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1); cal.set(Calendar.MINUTE, 1); cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();
			
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Quote Customer Details
			List<CriteriaCustomerRes> customerDetails = getallQuoteCustomerDetails(req , "Y" , before30 , today , limit , offset ) ;
			
			for(CriteriaCustomerRes data : customerDetails  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	 
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	

	public List<CriteriaCustomerRes> getallQuoteCustomerDetails(ExistingQuoteReq req , String status , Date startDate ,Date  endDate , Integer limit , Integer offset ) {
		List<CriteriaCustomerRes> customerDetails = new ArrayList<CriteriaCustomerRes>();
		try {
			// Get Request Ref No
			List<String> reqRefNos = new ArrayList<String>(); 
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> query = cb.createQuery(String.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

				query.select(m.get("requestReferenceNo")).distinct(true);
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));
				
				// Where
				Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(  m.get("branchCode"), req.getBranchCode()) ;
				Predicate n3 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
				Predicate n4 = cb.equal(  m.get("productId"),  req.getProductId());
				Predicate n5 = cb.equal(  m.get("createdBy"),  req.getCreatedBy());
				Predicate n6 = cb.equal(m.get("status"),status );
				Predicate n7 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				Predicate n8 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
				query.where(n1,n2,n3,n4,n5,n6,n7,n8).orderBy(orderList);
				
				// Get Result
				TypedQuery<String> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				reqRefNos = result.getResultList();
			
			}
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CriteriaCustomerRes> query = cb.createQuery(CriteriaCustomerRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

			// Select
			query.multiselect(
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("policyHolderTypeid").alias("policyHolderTypeid"),
					c.get("idType").alias("idType"),
					c.get("idNumber").alias("idNumber"),
					c.get("age").alias("age"),
					c.get("clientName").alias("clientName"),
					c.get("titleDesc").alias("titleDesc"),
					c.get("policyHolderType").alias("policyHolderType"),
					c.get("idTypeDesc").alias("idTypeDesc"),
					c.get("dobOrRegDate").alias("dobOrRegDate"),
					c.get("genderDesc").alias("genderDesc"),
					c.get("occupationDesc").alias("occupationDesc"),
					c.get("businessTypeDesc").alias("businessTypeDesc"),
					c.get("telephoneNo1").alias("telephoneNo1"),
					c.get("telephoneNo2").alias("telephoneNo2"),
					c.get("telephoneNo3").alias("telephoneNo3"),
					c.get("mobileNo1").alias("mobileNo1"),
					c.get("mobileNo2").alias("mobileNo2"),
					c.get("mobileNo3").alias("mobileNo3"),
					c.get("email1").alias("email1"),
					c.get("email2").alias("email2"),
					c.get("email3").alias("email3"),
					
					c.get("vrnGst").alias("vrnGst") ,
				
					// Vehicle Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
				 m.get("requestReferenceNo").alias("requestReferenceNo") , 
					m.get("createdBy").alias("createdBy") , 
					m.get("status").alias("status") ,
					m.get("quoteNo").alias("quoteNo") ,
					m.get("customerId").alias("customerId") ,
					m.get("entryDate").alias("entryDate") ,
					m.get("updatedDate").alias("updatedDate") ,
					m.get("updatedBy").alias("updatedBy")
					).distinct(true);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("quoteNo")));
			
			//In 
			Expression<String>e0= m.get("requestReferenceNo");
			
			// Where
			Predicate n1 = e0.in(reqRefNos);
			Predicate n2 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			query.where(n1,n2).orderBy(orderList);
			
			// Get Result
			TypedQuery<CriteriaCustomerRes> result = em.createQuery(query);
			customerDetails = result.getResultList();
			
			customerDetails = customerDetails.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());	
			customerDetails.sort(Comparator.comparing(CriteriaCustomerRes :: getUpdatedDate).reversed());	
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return customerDetails;
	}
	

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}

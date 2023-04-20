package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.cmc.GetCert;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.service.TravelSearchService;


@Service
public class TravelSearchServiceImpl implements TravelSearchService {
	@PersistenceContext
	private EntityManager em;
	
	
	private Logger log = LogManager.getLogger(TravelSearchServiceImpl.class);


	@Override
	public List<Tuple> searchTravel(SearchReq req, List<String> branches) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
		try {
			// Search
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();
			String productId=req.getProductId();

			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchTravelDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchTravelDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerName".equalsIgnoreCase(searchKey)) {
				searchQuote = searchTravelDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchTravelDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} 
			
			else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchTravelDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			}
							
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;

	}

	@Override
	public List<Tuple> searchTravelDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) {
		// TODO Auto-generated method stub
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceTravelDetails> c = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c.alias("c"),
					cus.get("clientName").alias("clientName"),cb.count(c).alias("idsCount"),
					cus.get("mobileNo1").alias("mobileNumber"));

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
			
			} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
				n1 = cb.equal(cb.lower(c.get("quoteNo")), searchValue);
					
			}
			else if (searchKey.equalsIgnoreCase("MobileNumber")) {
				n1 = cb.equal(cb.lower(cus.get("mobileNo1")), searchValue);
		 }
			else if (searchKey.equalsIgnoreCase("CustomerName")) {
				n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			}

			Predicate n2 = cb.equal(c.get("companyId"), companyId);
			Predicate n6 = cb.equal(c.get("productId"), productId);

			if ("issuer".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("applicationId"), loginId);
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				n4 = e0.in(branches);
			}
			if (searchKey.equalsIgnoreCase("CustomerName")) {
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
			query.where(n1,n2,n3,n4,n5,n6)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"),c.get("travelCoverId"),
					c.get("rejectReason"),c.get("riskId"))
			.orderBy(orderList);
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				query.where(n1, n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"),c.get("travelCoverId"),
						c.get("rejectReason"),c.get("riskId"))
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
}

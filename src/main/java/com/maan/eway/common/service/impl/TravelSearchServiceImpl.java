package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.EserviceTravelGetRes;
import com.maan.eway.common.service.TravelSearchService;
import com.maan.eway.master.controller.ProductGroupDropDownReq;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.master.res.ProductGroupMasterDropDownRes;
import com.maan.eway.master.service.ProductGroupMasterService;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.PassengerSectionDetails;


@Service
public class TravelSearchServiceImpl implements TravelSearchService {
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ProductGroupMasterService groupService;
	@Autowired
	private TravelPassengerDetailsRepository traPassRepo  ;
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
			}else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
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
			else if (searchKey.equalsIgnoreCase("PolicyNumber")) {
				n1 = cb.equal(cb.lower(c.get("policyNo")), searchValue);
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

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	
	
	@Override
	public List<ListItemValue> searchDropdownTravel(CopyQuoteDropDownReq req) {
		// TODO Auto-generated method stub
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			String itemType = "ADMIN_SEARCH_TRAVEL";
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
	
	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}

	@Override
	public AdminViewQuoteRes getTravelProductDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Travel Data
			List<TravelPassengerDetails> travelDatas =  traPassRepo.findByQuoteNo(req.getQuoteNo());
			List<TravelPassengerDetails> adultDatas  = travelDatas.stream().filter( o -> o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> otherDatas  =  travelDatas.stream().filter( o -> ! o.getGroupId().equals(2)  ).collect(Collectors.toList());
			List<TravelPassengerDetails> totalDatas  = new ArrayList<TravelPassengerDetails>();	
			totalDatas.addAll(adultDatas);
			totalDatas.addAll(otherDatas);
			
			ProductGroupDropDownReq groupReq = new ProductGroupDropDownReq();
			groupReq.setBranchCode(travelDatas.get(0).getBranchCode());
			groupReq.setInsuranceId(travelDatas.get(0).getCompanyId());		
			groupReq.setProductId(travelDatas.get(0).getProductId().toString());	
			
			List<ProductGroupMasterDropDownRes> groupRes =	groupService.getProductGroupMasterDropdown(groupReq);
			
			
			List<EserviceTravelGetRes>   travelResList = new ArrayList<EserviceTravelGetRes>();
			
			
			for (TravelPassengerDetails tra :  totalDatas) {
				EserviceTravelGetRes travelDetails = new  EserviceTravelGetRes()  ;
				dozerMapper.map(tra, travelDetails);
				travelDetails.setRiskId(tra.getPassengerId().toString());
				travelDetails.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
				travelDetails.setPassengerId(tra.getPassengerId().toString());
				travelDetails.setPassengerName(tra.getPassengerName());
				
				List<PassengerSectionDetails>  SectionList = new ArrayList<PassengerSectionDetails>();	
				 List<BrokerCommissionDetails> policylist = getPolicyName(tra.getCompanyId() , tra.getProductId().toString(), tra.getCreatedBy(),tra.getBrokerCode(), tra.getSectionId().toString());
				 Double commissionPercent =0.0;
				 if(policylist.size()>0 && policylist!=null) {
				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
				 }
				 else {
				 commissionPercent =5.0;
				 }
				 String premiumFc = tra.getOverallPremiumFc().toString();
				 String vatPremiumFc =	tra.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(new BigDecimal(commissionPercent))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);

				 travelDetails.setOverAllPremiumFc(tra.getOverallPremiumFc()==null?0: tra.getOverallPremiumFc() );
				 travelDetails.setOverAllPremiumLc(tra.getOverallPremiumLc()==null?0:tra.getOverallPremiumLc());
				 travelDetails.setPremiumFc(tra.getActualPremiumFc()==null?0:tra.getActualPremiumFc() );
				 travelDetails.setPremiumLc(tra.getActualPremiumLc()==null?0:tra.getActualPremiumLc());
				 travelDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				 travelDetails.setCommissionPercentage(commissionPercent.toString()==null?"":commissionPercent.toString());
				
				PassengerSectionDetails sec = new PassengerSectionDetails();
				sec.setSectionId(tra.getSectionId()==null?"":tra.getSectionId().toString());
				sec.setSectionName( tra.getSectionName());
				sec.setPassengerId(tra.getPassengerId().toString() );
				sec.setPassengerName(tra.getPassengerName());
				sec.setGroupDesc(groupRes.stream().filter( o -> o.getCode().equalsIgnoreCase(tra.getGroupId().toString()) ).collect(Collectors.toList()).get(0).getCodeDesc()) ;		
				sec.setGroupId(tra.getGroupId().toString());
				
				SectionList.add(sec);
				travelDetails.setSectionDetails(SectionList);	
				travelResList.add(travelDetails);
			}
		
			viewRes.setRiskDetails(travelResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		
	}
		return viewRes;
	}

	//Rating
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> travelRating(SearchReq req) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

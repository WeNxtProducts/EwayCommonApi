package com.maan.eway.common.controller;


import java.awt.image.RescaleOp;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DasboardCountRes;
import com.maan.eway.req.calcengine.CalcEngine;

@Service
public class DashBoardServiceV1 {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	
	
	//SELECT TYPE,SUM(COUNT) AS COUNT,SUM(PREMIUM) AS PREMIUM,CURRENCY_CODE FROM( SELECT (CASE WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN IS NULL) THEN 'QUOTE' WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN='Y') THEN 'RENEWAL QUOTE' END) AS TYPE, COUNT(*) AS COUNT, ROUND(SUM(OVERALL_PREMIUM_LC),0) AS PREMIUM, MAX(CPM.CURRENCY_ID) AS CURRENCY_CODE FROM ESERVICE_BUILDING_DETAILS HPM,EWAY_INSURANCE_COMPANY_MASTER CPM WHERE HPM.COMPANY_ID=? AND HPM.PRODUCT_ID=? AND HPM.STATUS IN ('Y') AND (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN () AND HPM.entry_date >= ? AND HPM.ENTRY_DATE <=? AND CPM.COMPANY_ID=HPM.COMPANY_ID AND CPM.AMEND_ID=(SELECT MAX(AMEND_ID) FROM EWAY_INSURANCE_COMPANY_MASTER WHERE CPM.COMPANY_ID=COMPANY_ID) GROUP BY HPM.STATUS,HPM.RENEWAL_DATE_YN UNION ALL SELECT (CASE WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN IS NULL) THEN 'QUOTE' WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN='Y') THEN 'RENEWAL QUOTE' END) AS TYPE, COUNT(*) AS COUNT, ROUND(SUM(OVERALL_PREMIUM_LC),0) AS PREMIUM, MAX(CPM.CURRENCY_ID) AS CURRENCY_CODE FROM ESERVICE_COMMON_DETAILS HPM,EWAY_INSURANCE_COMPANY_MASTER CPM WHERE HPM.COMPANY_ID=? AND HPM.PRODUCT_ID=? AND HPM.STATUS IN ('Y') AND CPM.COMPANY_ID=HPM.COMPANY_ID and (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN () AND HPM.entry_date >= ? AND HPM.ENTRY_DATE <=? AND CPM.AMEND_ID=(SELECT MAX(AMEND_ID) FROM EWAY_INSURANCE_COMPANY_MASTER WHERE CPM.COMPANY_ID=COMPANY_ID) GROUP BY HPM.STATUS,HPM.RENEWAL_DATE_YN)X GROUP BY TYPE,CURRENCY_CODE
	
	public CommonRes getallCount(DashBoardGetReq req) {
		if("19".equals(req.getProductId())) {
			return getDashBoardCorporatePlus(req);
		}else {
			CalcEngine engine=new CalcEngine();
			engine.setInsuranceId(req.getInsuranceId());
			engine.setProductId(req.getProductId());
			List<Tuple> product = ratingutil.collectProductType(engine);
			String oneProduct=product.get(0).get("motorYn")==null?"M":product.get(0).get("motorYn").toString();
			if("M".equals(oneProduct)) {
				return getDashBoard(req,EserviceMotorDetails.class);	
			} else if (oneProduct.equals("H")) {
				return getDashBoard(req,EserviceTravelDetails.class);
			}else if (oneProduct.equalsIgnoreCase("A")) {
				return getDashBoard(req,EserviceBuildingDetails.class);
			}else if (oneProduct.equalsIgnoreCase("L")) {
				return getDashBoard(req,EserviceCommonDetails.class);
			}
		}
		return null;
		
	}

	private CommonRes getDashBoardCorporatePlus(DashBoardGetReq req) {

		List<DasboardCountRes> total=new ArrayList<DasboardCountRes>();
		Date date1 = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date1);
		cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
		Date startDate = cal.getTime();
		
		
		List<String> loginIds=new ArrayList<String>();
		if("Broker".equals(req.getUserType())) {
			loginIds.addAll(findByCompanyAndLoginId(req.getInsuranceId(),req.getLoginId()));
		}else {
			loginIds.add(req.getLoginId());
		}
		try { 
			List<Map<String, Object>> results = ratingutil.executeCorporatePlusQuery(req,loginIds,startDate,new Date());
			for (Map<String,Object> objects : results) {
				DasboardCountRes res=new DasboardCountRes();
			  
				res.setType(objects.get("TYPE").toString());
				res.setCount(objects.get("COUNT").toString());
				res.setPremium(new BigDecimal(objects.get("PREMIUM")==null?"0":objects.get("PREMIUM").toString()));
				res.setCurrencyCode(objects.get("CURRENCY_CODE").toString());
				total.add(res);
			}
			
			List<String> list=new ArrayList<String>();
			list.add("QUOTE");
			list.add("RENEWAL QUOTE");
			list.add("POLICY");
			list.add("RENEWAL POLICY");
			for(String type:list) {
				long count = total.stream().filter(t-> type.equals(t.getType())).count();
				if(count==0) {
					DasboardCountRes res=new DasboardCountRes();				
					res.setType(type);
					res.setCount("0".toString());
					res.setPremium(new BigDecimal("0"));
					res.setCurrencyCode("None");
					total.add(res);
				}
			}
			
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		 
		return null;
		
	
	}

	private CommonRes getDashBoard(DashBoardGetReq req,Class tablename) {
		List<DasboardCountRes> total=new ArrayList<DasboardCountRes>();
		Date date1 = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date1);
		cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
		Date startDate = cal.getTime();
		
		
		List<String> loginIds=new ArrayList<String>();
		if("Broker".equals(req.getUserType())) {
			loginIds.addAll(findByCompanyAndLoginId(req.getInsuranceId(),req.getLoginId()));
		}else {
			loginIds.add(req.getLoginId());
		}
		try {
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<?> hpm = cq.from(tablename);
			Root<InsuranceCompanyMaster> cpm = cq.from(InsuranceCompanyMaster.class);

			// Create expressions for case statement
			Expression<Object> quoteCase = cb.selectCase().when(cb.and(cb.equal(hpm.get("status"), "Y"), cb.isNull(hpm.get("renewalDateYn"))), "QUOTE")
					.when(cb.and(cb.equal(hpm.get("status"), "Y"), cb.equal(hpm.get("renewalDateYn"), "Y")), "RENEWAL QUOTE")
					.otherwise("");
			Expression<Object> caseExpression = cb.selectCase()
				    .when(cb.equal(cb.literal("Issuer"), req.getUserType()), hpm.get("applicationId"))
				    .otherwise(hpm.get("loginId"));
			
			 
			
			// Create subquery for MAX(AMEND_ID)
			Subquery<Integer> subquery = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> cpmSub = subquery.from(InsuranceCompanyMaster.class);
			subquery.select(cb.max(cpmSub.get("amendId")))
			.where(cb.equal(cpmSub.get("companyId"), hpm.get("companyId")));

			// Construct the main query
			cq.multiselect(quoteCase.alias("Desc"), cb.count(hpm).alias("Count"), cb.sum(hpm.get("overallPremiumLc")).alias("TotalPremium"), cb.max(cpm.get("currencyId")).alias("Currency"))
			.where(cb.equal(hpm.get("companyId"), req.getInsuranceId()),
					cb.equal(hpm.get("productId"), req.getProductId()),
					cb.equal(hpm.get("status"), "Y"),
					cb.equal(cpm.get("companyId"), hpm.get("companyId")),
					cb.greaterThanOrEqualTo(hpm.get("entryDate"), startDate),
			        cb.lessThanOrEqualTo(hpm.get("entryDate"), new Date()),
			        cb.in(caseExpression).value(loginIds),
					cb.equal(cpm.get("amendId"), subquery))
			.groupBy(hpm.get("status"), hpm.get("renewalDateYn"))
			.orderBy(cb.asc(cb.count(hpm)));
				
			//  AND (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN ()

			// Execute the query
			List<Object[]> results = entityManager.createQuery(cq).getResultList();
			for (int i = 0; i < results.size(); i++) {
				DasboardCountRes res=new DasboardCountRes();
				Object[] objects = results.get(i);
				res.setType(objects[0].toString());
				res.setCount(objects[1].toString());
				res.setPremium(new BigDecimal(objects[2]==null?"0":objects[2].toString()));
				res.setCurrencyCode(objects[3].toString());
				total.add(res);
			}
			
			 
			//return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			Root<InsuranceCompanyMaster> cpm = cq.from(InsuranceCompanyMaster.class);

			// Create expressions for case statement
			Expression<Object> quoteCase = cb.selectCase().when(cb.and(cb.equal(hpm.get("status"), "P"), cb.isNull(hpm.get("renewalDateYn"))), "POLICY")
					.when(cb.and(cb.equal(hpm.get("status"), "P"), cb.equal(hpm.get("renewalDateYn"), "Y")), "RENEWAL POLICY")
					.otherwise("");

			Expression<Object> caseExpression = cb.selectCase()
				    .when(cb.equal(cb.literal("Issuer"), req.getUserType()), hpm.get("applicationId"))
				    .otherwise(hpm.get("loginId"));
			
			// Create subquery for MAX(AMEND_ID)
			Subquery<Integer> subquery = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> cpmSub = subquery.from(InsuranceCompanyMaster.class);
			subquery.select(cb.max(cpmSub.get("amendId")))
			.where(cb.equal(cpmSub.get("companyId"), hpm.get("companyId")));

			// Construct the main query
			
			
			
			cq.multiselect(quoteCase.alias("Desc"), cb.count(hpm).alias("Count"), cb.sum(hpm.get("overallPremiumLc")).alias("TotalPremium"), cb.max(cpm.get("currencyId")).alias("Currency"))
			.where(cb.equal(hpm.get("companyId"), req.getInsuranceId()),
					cb.equal(hpm.get("productId"),  req.getProductId()),
					cb.equal(hpm.get("status"), "P"),
					cb.equal(cpm.get("companyId"), hpm.get("companyId")),
					cb.greaterThanOrEqualTo(hpm.get("entryDate"), startDate),
			         cb.lessThanOrEqualTo(hpm.get("entryDate"), new Date()),
			         cb.in(caseExpression).value(loginIds),
					cb.equal(cpm.get("amendId"), subquery))
			.groupBy(hpm.get("status"), hpm.get("renewalDateYn"))
			.orderBy(cb.asc(cb.count(hpm)));

			// Execute the query
			List<Object[]> results = entityManager.createQuery(cq).getResultList();
			for (int i = 0; i < results.size(); i++) {
				DasboardCountRes res=new DasboardCountRes();
				Object[] objects = results.get(i);
				res.setType(objects[0].toString());
				res.setCount(objects[1].toString());
				res.setPremium(new BigDecimal(objects[2]==null?"0":objects[2].toString()));
				res.setCurrencyCode(objects[3].toString());
				total.add(res);
			}
			
			List<String> list=new ArrayList<String>();
			list.add("QUOTE");
			list.add("RENEWAL QUOTE");
			list.add("POLICY");
			list.add("RENEWAL POLICY");
			for(String type:list) {
				long count = total.stream().filter(t-> type.equals(t.getType())).count();
				if(count==0) {
					DasboardCountRes res=new DasboardCountRes();				
					res.setType(type);
					res.setCount("0".toString());
					res.setPremium(new BigDecimal("0"));
					res.setCurrencyCode("None");
					total.add(res);
				}
			}
			
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	private List<String> findByCompanyAndLoginId(String insuranceId, String loginId) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<LoginMaster> root = cq.from(LoginMaster.class);
			
            Subquery<String> subquery = cq.subquery(String.class);
            Root<LoginMaster> subRoot = subquery.from(LoginMaster.class);
            subquery.select(subRoot.get("oaCode"))
                    .where(cb.equal(subRoot.get("loginId"), loginId),
                    		cb.equal(subRoot.get("companyId"), insuranceId));

            cq.multiselect(root.get("loginId")).where( cb.and(
            		cb.equal(root.get("companyId"), insuranceId),
            		cb.equal(root.get("oaCode"), subquery)));

            List<String> results = entityManager.createQuery(cq).getResultList();
            
            List<String> result=new ArrayList<String>();
            
            for (String objects : results) {
            	 
				result.add((String) objects);				
			}
            return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}

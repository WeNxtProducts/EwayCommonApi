package com.maan.eway.admin.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.maan.eway.admin.req.AttachCompnayProductRequest;
import com.maan.eway.admin.req.AttachEndtIdsReq;
import com.maan.eway.admin.req.AttachIssuerProductRequest;
import com.maan.eway.admin.req.BrokerCompanyProductGetReq;
import com.maan.eway.admin.req.BrokerCompanyProductsGetRes;
import com.maan.eway.admin.req.BrokerProductGetReq;
import com.maan.eway.admin.req.IssuerProductGetReq;
import com.maan.eway.admin.req.IssuerProductListReq;
import com.maan.eway.admin.req.UserCompanyProductGetReq;
import com.maan.eway.admin.res.BrokerCommssionDetailsRes;
import com.maan.eway.admin.res.BrokerProductGetRes;
import com.maan.eway.admin.res.IssuerProductGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.res.ProductCriteriaRes;
import com.maan.eway.admin.service.LoginProductService;
import com.maan.eway.auth.dto.LoginProductCriteriaRes;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterGetReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterSaveReq;
import com.maan.eway.master.req.BrokerCommissionDetailsReq;
import com.maan.eway.master.req.BrokerCompanyProductReq;
import com.maan.eway.master.req.BrokerProductChangeReq;
import com.maan.eway.master.req.BrokerProductReq;
import com.maan.eway.master.res.BrokerCommissionDetailsMasterGetRes;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.repository.BrokerCommissionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
public class LoginProductServiceImpl  implements LoginProductService {
	 
	@Autowired
	private LoginProductMasterRepository loginProductRepo ;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ListItemValueRepository listRepo ;

	@Autowired
	private EndtTypeMasterRepository endtRepo;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private BrokerCommissionDetailsRepository commissionRepo ;

	Gson json = new Gson();


	private Logger log=LogManager.getLogger(LoginProductServiceImpl.class);
	
//*************************************** Add Products Apis Methods **********************************************************//

	@Transactional
	@Override
	public LoginCreationRes saveBrokerProductDetails(AttachCompnayProductRequest req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try { 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(new Date() );  cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes()) ;
			cal.set(Calendar.SECOND, today.getSeconds());
			Date effDate = cal.getTime();
			Date endDate = sdformat.parse("12/12/2050") ;
			cal.setTime(sdformat.parse("12/12/2050"));  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 50) ;
			endDate = cal.getTime() ;
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			
			// Find All
			Root<CompanyProductMaster>    c = query.from(CompanyProductMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			effectiveDate.where(a1,a2,a3);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId") );
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			effectiveDate2.where(a4,a5,a6);
			
			//In 
			Expression<String>e0=c.get("productId");
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 =e0.in( req.getProductIds());
			Predicate n5 =cb.equal(c.get("companyId"), req.getInsuranceId());
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			
			for ( CompanyProductMaster data : list  ) {
				
			
				LoginProductMaster save = new LoginProductMaster();
				dozerMapper.map(data, save);
				save.setCompanyId(req.getInsuranceId());
				save.setCreatedBy(req.getCreatedBy());
				save.setEffectiveDateStart(effDate);
				save.setEffectiveDateEnd(endDate);
				save.setEntryDate(new Date());
				save.setAmendId(0);
				save.setLoginId(req.getLoginId());
				save.setBackDays(0);
				save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));
				save.setOaCode(loginData.getOaCode());
				save.setCommissionPercent(15);
				String financeid = "";
				String nonfinanceid = "";
				List<EndtTypeMaster> endtids = getEndtId(req.getInsuranceId(), data.getProductId()); 								
				for(EndtTypeMaster endtid :endtids) {				
					if(endtid.getEndtTypeCategoryId().toString().equalsIgnoreCase("2")) {						
						financeid = financeid+","+endtid.getEndtTypeId().toString();
					}
					else if(endtid.getEndtTypeCategoryId().toString().equalsIgnoreCase("1")){
						nonfinanceid = nonfinanceid+","+endtid.getEndtTypeId().toString();						
					}					
				}
				if(StringUtils.isNotBlank(financeid)) {
				financeid=financeid.substring(1);
				}
				if(StringUtils.isNotBlank(nonfinanceid)) {
				nonfinanceid=nonfinanceid.substring(1);
				}
				save.setFinancialEndtIds(financeid);
				save.setNonFinancialEndtIds(nonfinanceid);
				
				loginProductRepo.saveAndFlush(save);
				log.info("Saved Details is ---> " + json.toJson(save));
				
			}		
			
			res.setResponse("Products Added Successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	
	
//*************************************** Get Products Apis Methods **********************************************************//
	
	private List<EndtTypeMaster> getEndtId(String insuranceId, Integer productId) {
		// TODO Auto-generated method stub
		List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			//Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = effectiveDate.from(EndtTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a2 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a3 = cb.equal(b.get("productId"),ocpm1.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
			
			effectiveDate.where(a1,a2,a3,a4);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm2 = effectiveDate2.from(EndtTypeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(b.get("endtTypeId"),ocpm2.get("endtTypeId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(b.get("companyId"),ocpm2.get("companyId"));
			Predicate a9 = cb.equal(b.get("productId"),ocpm2.get("productId"));
			
			effectiveDate2.where(a6,a7,a8,a9);
			Predicate n1 = cb.equal(b.get("companyId"),insuranceId);
			Predicate n2 = cb.equal(b.get("productId"),productId);
			Predicate n3 = cb.equal(b.get("status"),"Y");
			Predicate n4 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n5 = cb.equal(b.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3,n4,n5);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}

	



	@Override
	public BrokerProductGetRes getBrokerProducts(BrokerProductGetReq req) {
		BrokerProductGetRes res = new BrokerProductGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
			
			
			// Update
			// Get Less than Equal Today Record 
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			effectiveDate.where(a1,a2,a3);

			// Order By
		//	List<Order> orderList = new ArrayList<Order>();
		//	orderList.add(cb.asc(b.get("branchName")));
			
			// Where
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =  cb.equal(b.get("productId"), req.getProductId() );
			Predicate n4 =  cb.equal(b.get("companyId"), req.getInsuranceId() );
			Predicate n5 =  cb.equal(b.get("loginId"), req.getLoginId() );

			query.where( n2, n3,n4,n5);//.orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			LoginProductMaster data = list.get(0);
			if( data !=null ) {
				String financeid = data.getFinancialEndtIds();
				String nonFinanceid = data.getNonFinancialEndtIds();

		        ArrayList<String> financeids = new ArrayList<String>(Arrays.asList(financeid));
		        ArrayList<String> nonfinanceids = new ArrayList<String>(Arrays.asList(nonFinanceid));
		  	
				dozerMapper.map(list.get(0), res);
			//	res.setBackDays(list.get(0).getBackDays().toString());
				
				res.setFinanceIds(financeids);
				res.setNonFinanceIds(nonfinanceids);
			}
			
			
			
			// Policy Type List 
			List<PolicyTypeMaster> policytypeList = policyTypeList(req.getInsuranceId(),req.getProductId() );	
			
			// Get Commission Details 
			List<BrokerCommissionDetails>  commissionList = getBrokerCommissionDetails(req.getInsuranceId() , req.getProductId() , req.getLoginId() ) ;
			
			List<BrokerCommssionDetailsRes> commList = new ArrayList<BrokerCommssionDetailsRes>();
			for ( PolicyTypeMaster policyType : policytypeList) {
				BrokerCommssionDetailsRes comm = new BrokerCommssionDetailsRes();
				
				comm.setPolicyTypeId(policyType.getPolicyTypeId()==null?"" : policyType.getPolicyTypeId().toString());
				comm.setPolicyTypeDesc(policyType.getPolicyTypeName());
				
				List<BrokerCommissionDetails>  filterCommission = commissionList.stream().filter( o -> o.getPolicyType().equalsIgnoreCase(policyType.getPolicyTypeId().toString()) ).collect(Collectors.toList());
				
				if(filterCommission.size() > 0 && filterCommission.get(0).getStatus().equalsIgnoreCase("Y")  ) {
					BrokerCommissionDetails commData = filterCommission.get(0);
					comm.setBackDays(commData.getBackDays()==null?"" : commData.getBackDays().toString());
					comm.setCommissionPercent(commData.getCommissionPercentage()==null?"" : commData.getCommissionPercentage().toString());
					comm.setCommissionVatPercent(commData.getCommissionVatPercent()==null?"" : commData.getCommissionVatPercent().toString());
					comm.setCommissionVatYn(commData.getCommissionVatYn()==null?"" : commData.getCommissionVatYn());
					comm.setCoreAppCode(commData.getCoreAppCode());
					comm.setRegulatoryCode(commData.getRegulatoryCode());
					comm.setStatus(commData.getStatus());
					comm.setSumInsuredEnd(commData.getSuminsuredEnd()==null?"" : commData.getSuminsuredEnd().toPlainString());
					comm.setSumInsuredStart(commData.getSuminsuredStart()==null?"" : commData.getSuminsuredStart().toPlainString());
					comm.setSelectedYn("Y");
					
				} else {
					comm.setSelectedYn("N");
				}
				
				
				commList.add(comm);
			}
			res.setBrokerCommssionDetails(commList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	
	public List<BrokerCommissionDetails> getBrokerCommissionDetails(String companyId , String productId ,String loginId  ) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("oaCode"), b.get("oaCode"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a7 = cb.equal(ocpm1.get("id"), b.get("id"));

			amendId.where(a1, a2,a3,a4,a5,a6,a7);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId );
			Predicate n5 = cb.equal(b.get("loginId"), loginId );
			
			query.where(n1,n2,n3,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			list = result.getResultList();
			//list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPolicyType()))).collect(Collectors.toList());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	@Transactional
	@Override
	public LoginCreationRes saveIssuerProductDetails(AttachCompnayProductRequest req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try { 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(new Date() );  cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes()) ;
			cal.set(Calendar.SECOND, today.getSeconds());
			Date effDate = cal.getTime();
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(effDate.getTime() - MILLIS_IN_A_DAY);
			Date endDate = sdformat.parse("12/12/2050") ;
			cal.setTime(sdformat.parse("12/12/2050"));  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 50) ;
			endDate = cal.getTime() ;
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		{
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			
			// Find All
			Root<CompanyProductMaster>    c = query.from(CompanyProductMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			effectiveDate.where(a1,a2,a3);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId") );
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			effectiveDate2.where(a4,a5,a6);
			
			//In 
			Expression<String>e0=c.get("productId");
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 =e0.in( req.getProductIds());
			Predicate n5 =cb.equal(c.get("companyId"), req.getInsuranceId());
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
		}
		 List<LoginProductMaster> oldlist = new ArrayList<LoginProductMaster>();
			{// Find Old 
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
			
				
				// Find All
				Root<LoginProductMaster>    c = query.from(LoginProductMaster.class);		
				
				// Select
				query.select(c );
				
			
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(c.get("amendId")));
				orderList.add(cb.desc(c.get("productId")));
				
				//In 
		//		Expression<String>e0=c.get("productId");
				
			    // Where	
			//	Predicate n4 =e0.in( req.getProductIds());
				Predicate n5 =cb.equal(c.get("companyId"), req.getInsuranceId());
				Predicate n6 = cb.equal(c.get("loginId"),req.getLoginId() );
				query.where(n5,n6).orderBy(orderList);
				
				// Get Result
				TypedQuery<LoginProductMaster> result = em.createQuery(query);	
				int limit = 0 , offset = 100 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				oldlist =  result.getResultList();  	
				
			}
			 List<LoginProductMaster> nonSelectedFromList = oldlist ;
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			
			for ( CompanyProductMaster data : list  ) {
				LoginProductMaster save = new LoginProductMaster();
				
				Integer amendId = 0;
				Date entryDate  = new Date();
				String createdBy = req.getCreatedBy();
				
				
				 List<LoginProductMaster> filterOldData = oldlist.stream().filter( o -> o.getCompanyId().equalsIgnoreCase(data.getCompanyId()) &&
						 o.getProductId().equals(data.getProductId()) ).collect(Collectors.toList());  
				 filterOldData.sort(Comparator.comparing(LoginProductMaster :: getAmendId).reversed() );
				 
				  
				if (filterOldData.size() > 0) {
					nonSelectedFromList.removeIf(o -> o.getCompanyId().equalsIgnoreCase(data.getCompanyId()) &&  o.getProductId().equals(data.getProductId()) ) ;
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						LoginProductMaster lastRecord = filterOldData.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							loginProductRepo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						save = filterOldData.get(0) ;
						if (list.size()>1 ) {
							LoginProductMaster lastRecord = filterOldData.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							loginProductRepo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				
				dozerMapper.map(data, save);
				save.setCompanyId(req.getInsuranceId());
				save.setCreatedBy(createdBy);
				save.setEffectiveDateStart(effDate);
				save.setEffectiveDateEnd(endDate);
				save.setEntryDate(entryDate);
				save.setAmendId(amendId);
				save.setLoginId(req.getLoginId());
				save.setBackDays(0);
				save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));
				save.setOaCode(loginData.getOaCode());
				save.setCommissionPercent(15);
				String financeid = "";
				String nonfinanceid = "";
				List<EndtTypeMaster> endtids = getEndtId(req.getInsuranceId(), data.getProductId()); 								
				for(EndtTypeMaster endtid :endtids) {				
					if(endtid.getEndtTypeCategoryId().toString().equalsIgnoreCase("1")) {						
						financeid = financeid+","+endtid.getEndtTypeId().toString();
					}
					else {
						nonfinanceid = nonfinanceid+","+endtid.getEndtTypeId().toString();						
					}					
				}
				if(StringUtils.isNotBlank(financeid)) {
				financeid=financeid.substring(1);
				}
				if(StringUtils.isNotBlank(nonfinanceid)) {
				nonfinanceid=nonfinanceid.substring(1);
				}
				save.setFinancialEndtIds(financeid);
				save.setNonFinancialEndtIds(nonfinanceid);

				String referralid = "";				
				List<String> referralIds = req.getReferralIds();
				for (int i = 0; i < referralIds.size(); i++) {
					referralid = referralid + "," + referralIds.get(i);
				}
				referralid=referralid.substring(1);
				save.setReferralId(referralid);
				loginProductRepo.saveAndFlush(save);
				log.info("Saved Details is ---> " + json.toJson(save));
				
			}	
			
			nonSelectedFromList.sort(Comparator.comparing(LoginProductMaster :: getAmendId ).reversed());
			nonSelectedFromList = nonSelectedFromList.stream().filter(distinctByKey(o -> Arrays.asList(o.getProductId() , o.getCompanyId()))).collect(Collectors.toList());
			// Deactive old Records 
			for ( LoginProductMaster data : nonSelectedFromList  ) { 
				LoginProductMaster lastRecord = data;
				lastRecord.setEffectiveDateEnd(today);
				lastRecord.setStatus("N");
				loginProductRepo.saveAndFlush(lastRecord);
			}
			
			res.setResponse("Products Added Successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	public List<ProductCriteriaRes> getProductDetails(List<String> companyIds , Date today ) {
		List<ProductCriteriaRes> list = new ArrayList<ProductCriteriaRes>();  
		try {
			// Product Query 	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductCriteriaRes> query = cb.createQuery(ProductCriteriaRes.class);
			
			Root<ProductMaster> pm  = query.from(ProductMaster.class);
			
			// Select Company Name SubQuery for Effective Date Max Filter 
			Subquery<Long> insEff = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> i = insEff.from(InsuranceCompanyMaster.class);
			Subquery<Long> company = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ins = company.from(InsuranceCompanyMaster.class);
			
			insEff.select( cb.max(i.get("effectiveDateStart")) );
			Predicate i1 = cb.equal(ins.get("companyId"), i.get("companyId"));
			Predicate i2 = cb.lessThanOrEqualTo(i.get("effectiveDateStart") , today);
			insEff.where(i1,i2);
			
			company.select( ins.get("companyName")) ;
			Predicate ins1 = cb.equal(ins.get("companyId"), pm.get("companyId"));
			Predicate ins2  = cb.equal(ins.get("effectiveDateStart"),insEff);
			Predicate ins3  = cb.equal(ins.get("status"),"Y");
			company.where(ins1,ins2,ins3);
			
			// Select
			query.multiselect( pm.get("productId").alias("productId") , pm.get("productName").alias("productName") ,pm.get("companyId").alias("companyId") ,
					company.alias("companyName"));

			// Product Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ProductMaster> ocpm1 = effectiveDate.from(ProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), pm.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), pm.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2,a3); 
					
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(pm.get("entryDate")));
			
			//In 
			Expression<String>e0=pm.get("companyId");
			
			// Where
			Predicate n1 = cb.equal(pm.get("status"), "Y");
			Predicate n2 = cb.equal(pm.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(companyIds);
			
			query.where(n1, n2, n3).orderBy(orderList);
		
			// Get Result
			TypedQuery<ProductCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
				
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}
	
	
	public List<LoginProductMaster> getBrokerProducts(String loginId , List<String> companyIds , Date today ) {
		List<LoginProductMaster> list = new ArrayList<LoginProductMaster>(); 
		try {
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
		
			// Find All
			Root<LoginProductMaster>    c = query.from(LoginProductMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			
			
			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = amendId.from(LoginProductMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			Predicate a3 = cb.equal(c.get("loginId"),ocpm1.get("loginId") );
			amendId.where(a1,a2,a3);
			
			// Filer Product IDs
			Subquery<Long> productIds = query.subquery(Long.class);
			Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);
			
			
			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
			effectiveDate3.select(cb.max(ocpm4.get("effectiveDateStart")));
			Predicate a9 = cb.equal(cm.get("productId"),ocpm4.get("productId") );
			Predicate a10 = cb.equal(cm.get("companyId"),ocpm4.get("companyId") );
			Predicate a11 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
			effectiveDate3.where(a9,a10,a11);
			
			Subquery<Long> effectiveDate4 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
			effectiveDate4.select(cb.max(ocpm5.get("effectiveDateEnd")));
			Predicate a12 = cb.equal(cm.get("productId"),ocpm5.get("productId") );
			Predicate a13 = cb.equal(cm.get("companyId"),ocpm5.get("companyId") );
			Predicate a14 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			effectiveDate4.where(a12,a13,a14);
			
			
			productIds.select(cm.get("productId"));
			Predicate a15 = cb.equal(cm.get("companyId"),companyIds.get(0));
			Predicate a16 = cb.equal(cm.get("status"),"Y" );
			Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
			productIds.where(a15,a16,a17,a18);
			
			//In 
			Expression<String>e0=c.get("productId");
			
		    // Where	
			Predicate n2 = cb.equal(c.get("amendId"), amendId);
			Predicate n4 = cb.equal(c.get("companyId"), companyIds.get(0));
			Predicate n5 = cb.equal(c.get("loginId"), loginId);
			Predicate n6 = e0.in(productIds);
			query.where(n2,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);			
			list =  result.getResultList(); 
			
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}

	@Override
	public List<LoginProductCriteriaRes> getBrokerProductDetails(String loginId , List<String> companyIds , Date today ) {
		List<LoginProductCriteriaRes> list = new ArrayList<LoginProductCriteriaRes>(); 
		try {
			
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Login Product Query	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductCriteriaRes> query = cb.createQuery(LoginProductCriteriaRes.class);

			Root<LoginProductMaster> lm  = query.from(LoginProductMaster.class);
			
			
			// Company Effective Date Max Filter
			Subquery<Long> company = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ins = company.from(InsuranceCompanyMaster.class);
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm2 = effectiveDate2.from(InsuranceCompanyMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateStart")));
			Predicate ceff1 = cb.equal(ocpm2.get("companyId"), ins.get("companyId"));
			Predicate ceff2 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"), today);
			effectiveDate2.where(ceff1,ceff2);
			
			// Company Name
			company.select(ins.get("companyName"));
			Predicate ins1 = cb.equal(ins.get("companyId"), lm.get("companyId"));
			Predicate ins2 = cb.equal(ins.get("effectiveDateStart"), effectiveDate2);
			company.where(ins1,ins2);
			
			
			// Select Product Name SubQuery for Effective Date Max Filter 
			Subquery<Long> pmEff = query.subquery(Long.class);
			Root<ProductMaster> pm = pmEff.from(ProductMaster.class);
			Subquery<Long> product = query.subquery(Long.class);
			Root<ProductMaster> p = product.from(ProductMaster.class);
			
			pmEff.select( cb.max(pm.get("effectiveDateStart")) );
			Predicate i2 = cb.equal(p.get("productId"), pm.get("productId"));
			Predicate i3 = cb.lessThanOrEqualTo(pm.get("effectiveDateStart") , today);
			pmEff.where(i2,i3);
			
			product.select( p.get("productName")) ;
			Predicate pm2 = cb.equal(p.get("productId"), lm.get("productId"));
			Predicate pm3   = cb.equal(p.get("effectiveDateStart"),pmEff);
			Predicate pm4  = cb.equal(p.get("status"),"Y");
			product.where(pm2,pm3,pm4);
			
			// Select
			query.multiselect( lm.get("productId").alias("productId") ,  lm.get("companyId").alias("companyId") , product.alias("productName") ,
					lm.get("productName").alias("oldProductName") ,  lm.get("sumInsuredStart").alias("sumInsuredStart") , lm.get("sumInsuredEnd").alias("sumInsuredEnd") ,
					 lm.get("status").alias("status")  ,  lm.get("remarks").alias("remarks")   , company.alias("companyName")
					,lm.get("effectiveDateStart").alias("effectiveDateStart") ,lm.get("effectiveDateEnd").alias("effectiveDateEnd"));

			// Product Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("loginId"), lm.get("loginId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), lm.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), lm.get("companyId"));
			effectiveDate.where(a1,a2,a3); 
			
			/*

			// Company Products

			Subquery<Long> productids = query.subquery(Long.class);
			Root<CompanyProductMaster> pids = productids.from(CompanyProductMaster.class);

			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm3 = effectiveDate3.from(CompanyProductMaster.class);
			effectiveDate3.select(cb.max(ocpm3.get("effectiveDateStart")));
			Predicate ceff5 = cb.equal(ocpm3.get("companyId"), pids.get("companyId"));
			Predicate ceff6 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			Predicate ceff7 = cb.equal(ocpm3.get("status"),"Y");			
			Predicate ceff8 = cb.equal(ocpm3.get("productId"),pids.get("productId"));			
			effectiveDate3.where(ceff5,ceff6,ceff7,ceff8);
			
			Subquery<Long> effectiveDate4 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm4 = effectiveDate4.from(CompanyProductMaster.class);
			effectiveDate4.select(cb.max(ocpm4.get("effectiveDateEnd")));
			Predicate ceff9 = cb.equal(ocpm4.get("companyId"), pids.get("companyId"));
			Predicate ceff10 = cb.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
			Predicate ceff11 = cb.equal(ocpm4.get("status"),"Y");			
			Predicate ceff12 = cb.equal(ocpm4.get("productId"),pids.get("productId"));			
			effectiveDate4.where(ceff9,ceff10,ceff11,ceff12);
			
			productids.select(pids.get("productId"));
			Predicate ceff13 = cb.equal(pids.get("companyId"), lm.get("companyId"));
			Predicate ceff14 = cb.equal(pids.get("effectiveDateEnd"),effectiveDate4);
			Predicate ceff15 = cb.equal(pids.get("status"),"Y");			
			Predicate ceff16 = cb.equal(pids.get("productId"),lm.get("productId"));			
			Predicate ceff17 = cb.equal(pids.get("effectiveDateStart"),effectiveDate3);

			productids.where(ceff13,ceff14,ceff15,ceff16,ceff17);
			
			*/
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(lm.get("entryDate")));
			
			//In 
			Expression<String>e0=lm.get("companyId");
			
			// Where
			Predicate n1 = cb.equal(lm.get("loginId"), loginId );
			Predicate n2 = cb.equal(lm.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(companyIds);
			query.where(n1, n2, n3).orderBy(orderList);
			
			//Predicate n4 = e0.in(productids);
			//query.where(n1, n2, n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<LoginProductCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
				
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}

//*************************************** Get Login Company Products Apis Methods **********************************************************//
	
	@Override
	public List<BrokerCompanyProductsGetRes> getBrokerCompanyProducts(BrokerCompanyProductGetReq req) {
		List<BrokerCompanyProductsGetRes> productList = new ArrayList<BrokerCompanyProductsGetRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today  =  new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			
			String loginId = req.getLoginId() ;
			List<String> companyIds = new ArrayList<String>() ;
			companyIds.add(req.getInsuranceId());
			
			List<LoginProductMaster> loginProducts = getBrokerProducts (loginId , companyIds , today ) ;
				
			for(LoginProductMaster data :  loginProducts) {
				BrokerCompanyProductsGetRes productRes = new BrokerCompanyProductsGetRes();
				
				String pattern = "#####0.00";
				DecimalFormat df = new DecimalFormat(pattern);
				productRes.setProductId(data.getProductId()==null?"" :data.getProductId().toString() );
				productRes.setProductName(data.getProductName());
				productRes.setSumInsuredStart(data.getSumInsuredStart()==null?"" : df.format(data.getSumInsuredStart()) );
				productRes.setSumInsuredEnd(data.getSumInsuredEnd()==null?"" :df.format(data.getSumInsuredEnd()) );
				productRes.setStatus(data.getStatus());
				productRes.setEffectiveDateStart(data.getEffectiveDateStart() );
				productRes.setEffectiveDateEnd(data.getEffectiveDateEnd() );
				productList.add(productRes);
			
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return productList;
	}

	@Override
	public SuccessRes updateBrokerCompanyProductDetails(BrokerCompanyProductReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
		
			LoginMaster login =loginRepo.findByLoginId(req.getLoginId());
			LoginProductMaster saveData = new LoginProductMaster();
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			String financeId = "";
			String nonFinanceId = "";
			
			String productId="";
			Date entryDate = null ;
			String createdBy = "" ;
			
			// Update
			// Get Less than Equal Today Record 
			// Criteria
			productId=req.getProductId().toString();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			

			// Order By
		//	List<Order> orderList = new ArrayList<Order>();
		//	orderList.add(cb.asc(b.get("branchName")));
			
			// Where
			//Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n3 =  cb.equal(b.get("productId"), req.getProductId() );
			Predicate n4 =  cb.equal(b.get("companyId"), req.getCompanyId() );
			Predicate n5 =  cb.equal(b.get("loginId"), req.getLoginId() );

			query.where( n3,n4,n5);//.orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			int limit = 0 , offset = 2 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				financeId = list.get(0).getFinancialEndtIds();
				nonFinanceId= list.get(0).getFinancialEndtIds();
				
				if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
					amendId = list.get(0).getAmendId() + 1 ;
					entryDate = new Date() ;
					createdBy = req.getCreatedBy();
					LoginProductMaster lastRecord = list.get(0);
					
						lastRecord.setEffectiveDateEnd(oldEndDate);
						loginProductRepo.saveAndFlush(lastRecord);
					
				} else {
					amendId = list.get(0).getAmendId() ;
					entryDate = list.get(0).getEntryDate() ;
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0) ;
					if (list.size()>1 ) {
						LoginProductMaster lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						loginProductRepo.saveAndFlush(lastRecord);
					}
				
			    }
			}
		
			res.setResponse("Updated Successfully ");
			res.setSuccessId(productId);
				
			
		    dozerMapper.map(req, saveData );
			saveData.setProductId(Integer.valueOf(productId));
			saveData.setProductName(req.getProductName());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(new Date());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setEntryDate(entryDate);
			saveData.setAmendId(amendId);
			saveData.setCoreAppCode(req.getCoreAppCode());
			saveData.setAgencyCode(Integer.valueOf(login.getAgencyCode()));
			saveData.setOaCode(login.getOaCode());
			saveData.setUserType(login.getUserType());
			saveData.setSubUserType(login.getSubUserType());
			//saveData.setBackDays(Integer.valueOf(req.getBackDays()));
			
			saveData.setFinancialEndtIds(financeId);
			saveData.setNonFinancialEndtIds(nonFinanceId);
			loginProductRepo.saveAndFlush(saveData);
							
			log.info("Saved Details is ---> " + json.toJson(saveData));
			
			
			// Save Broker Commission Details 
			for ( BrokerCommissionDetailsReq comm :  req.getBrokerCommissionDetails() ) {
				
				res = 	saveBrokerCommission(comm , req , login ) ;
					
				
			}
			
			List<String> policyTypeIds =  req.getBrokerCommissionDetails().stream().map( BrokerCommissionDetailsReq :: getPolicyTypeId ) .collect(Collectors.toList());					
			List<BrokerCommissionDetails>   oldCommList = commissionRepo.findByProductIdAndPolicyTypeNotInAndLoginIdAndStatus( req.getProductId() ,
					policyTypeIds , req.getLoginId() , "Y" ) ;
			
			oldCommList.forEach ( o -> {  o.setStatus("N");  }   );
			commissionRepo.saveAll(oldCommList);
			
				
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public SuccessRes saveBrokerCommission(BrokerCommissionDetailsReq comm ,  BrokerCompanyProductReq req ,LoginMaster login  ) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerCommissionDetails saveData = new BrokerCommissionDetails();
		List<BrokerCommissionDetails> list  = new ArrayList<BrokerCommissionDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer id = 1;
			
			
			
			id = StringUtils.isBlank(comm.getPolicyTypeId()) ? 1 : Integer.valueOf(comm.getPolicyTypeId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			//Findall
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),login.getOaCode());
			Predicate n5 = cb.equal(b.get("agencyCode"),login.getAgencyCode());
			Predicate n6 = cb.equal(b.get("policyType"),comm.getPolicyTypeId());

			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			int limit=0, offset=2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId()+1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					BrokerCommissionDetails lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					commissionRepo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						BrokerCommissionDetails lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						commissionRepo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(comm.getPolicyTypeId());
	
		String policytype = policyName(req.getCompanyId(),req.getProductId(),comm.getPolicyTypeId());	
			
		dozerMapper.map(req, saveData);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(createdBy);
		saveData.setEntryDate(entryDate);
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setSuminsuredStart(StringUtils.isBlank(comm.getSumInsuredStart()) ?  new BigDecimal("0") : new BigDecimal(comm.getSumInsuredStart()));
		saveData.setSuminsuredEnd(StringUtils.isBlank(comm.getSumInsuredEnd()) ?  new BigDecimal("0") :  new BigDecimal(comm.getSumInsuredEnd()));
		saveData.setCommissionPercentage(StringUtils.isBlank(comm.getCommissionPercent()) ?  0D : Double.valueOf(comm.getCommissionPercent()));
		saveData.setCommissionVatYn(comm.getCommissionVatYn());
		saveData.setCommissionVatPercent(StringUtils.isBlank(comm.getCommissionVatPercent()) ?  0D : Double.valueOf(comm.getCommissionVatPercent()));
		saveData.setBackDays(StringUtils.isBlank(comm.getCommissionVatPercent()) ?  0 : Integer.valueOf(comm.getBackDays()) ) ;
		saveData.setAgencyCode(login.getAgencyCode());
		saveData.setCheckerYn("N");
		saveData.setCompanyId(req.getCompanyId());
		saveData.setFmvSiEnd("");
		saveData.setFmvSiStart("");
		saveData.setFmvStatus("");
		saveData.setLoginId(login.getLoginId());
		saveData.setOaCode(login.getOaCode().toString()) ;
		saveData.setPolicyType(comm.getPolicyTypeId());
		saveData.setPolicyTypeDesc(policytype);
		saveData.setProductId(req.getProductId());
		saveData.setRemarks(req.getRemarks());
		saveData.setCoreAppCode(StringUtils.isBlank(comm.getCoreAppCode()) ?  req.getCoreAppCode() : comm.getCoreAppCode());
		saveData.setRegulatoryCode(StringUtils.isBlank(comm.getRegulatoryCode()) ?  req.getRegulatoryCode() : comm.getRegulatoryCode());
		saveData.setStatus(StringUtils.isBlank(comm.getStatus()) ?  req.getStatus() : comm.getStatus());
		
		
		saveData.setId(id);
		saveData.setPolicyTypeDesc(policytype);
		commissionRepo.save(saveData);
		
		
		res.setResponse("Saved Successfully");
		res.setSuccessId(login.getLoginId());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
		return res;

	}

	private String policyName(String companyId, String productId, String policyTypeId) {
		// TODO Auto-generated method stub
		String data="";
		try {
		List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);
		//Find all
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Long> effectiveDate = query.subquery(Long.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

		effectiveDate.where(a1,a2,a3);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("amendId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("productId"),productId);
		Predicate n4 = cb.equal(b.get("policyTypeId"),policyTypeId);
		
		query.where(n1,n2,n3,n4).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getPolicyTypeName() : "" ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}
	
	private List<PolicyTypeMaster> policyTypeList(String companyId, String productId ) {
		// TODO Auto-generated method stub
		List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
		try {
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);
		//Find all
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Long> effectiveDate = query.subquery(Long.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

		effectiveDate.where(a1,a2,a3);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("amendId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("productId"),productId);
		
		query.where(n1,n2,n3).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
		int limit = 0 , offset = 100 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return list;
}
	
	private Integer getMasterTableCount(String companyId, String productId) {
		// TODO Auto-generated method stub
		Integer data =0;
		try {
			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			//Find all
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("id"),b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			effectiveDate.where(a1,a2,a3);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("id")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}

	@Override
	public List<Error> validateUpdateBrokerCompanyProductDetails(BrokerCompanyProductReq req) {
List<Error> errorList = new ArrayList<Error>();
		
		try {
		
			
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("01", "ProductId", "Please Select Product  Id" ));
			}else if (req.getProductId().length() > 3){
				errorList.add(new Error("01","ProductId", "Please Enter Product  Id within 100 Characters ")); 
			}else if (! req.getProductId().matches("[0-9]+") ){
				errorList.add(new Error("01","ProductId", "Please Enter Valid Number in Product  Id ")); 
			}
			
			if (StringUtils.isBlank(req.getProductName())) {
				errorList.add(new Error("01", "ProductName", "Please Select Product  Name  "));
			}else if (req.getProductName().length() > 100){
				errorList.add(new Error("01","ProductName", "Please Enter Product  Name within 100 Characters  ")); 
			}
			

			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("01", "InsuranceId", "Please Select InsuranceId"));
			}
	
			if (StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("01", "LoginId", "Please Select LoginId"));
			}
			
			
			if (StringUtils.isBlank(req.getRemarks()) ) {
				errorList.add(new Error("03", "Remark", "Please Select Remark  "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters  ")); 
			}

			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Select Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}
			// Effective Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date  "));
			}
			//	else if (req.getEffectiveDateEnd() == null ) {
//				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End  in Row No :"));
//
//			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart()) || req.getEffectiveDateEnd().equalsIgnoreCase(req.getEffectiveDateStart())) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date End  is After Effective Date Start  "));
		//	}
		else if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("08", "InsuranceId", "Please Enter InsuranceId  "));
			} else if (req.getCompanyId().length() > 20) {
				errorList.add(new Error("11", "InsuranceId", "Please Enter InsuranceId within 20 Characters  "));
			} else if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("02", "CoreAppCode", "Please Enter CoreAppCode"));
			} else if (req.getCoreAppCode().length() > 20) {
				errorList.add(new Error("02", "CoreAppCode", "CoreAppCode under 20 Characters only allowed"));
			} else if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("09", "ProductId", "Please Enter ProductId  "));
			} else if (! req.getProductId().matches("[0-9]+") ) {
				errorList.add(new Error("09", "ProductId", "Please Enter Valid Number ProductId "));
			} 
			
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Enter Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Enter Status 1 Character Only "));
			}else if(!("P".equalsIgnoreCase(req.getStatus()) || "Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Enter Status Y or N or P Only   "));
			}
			
			if (StringUtils.isBlank(req.getPaymentYn())) {
				errorList.add(new Error("06", "Payment", "Please Select Payment Type  "));
			} else if (req.getPaymentYn().length() > 1) {
				errorList.add(new Error("06", "Payment", "Enter Payment Type 1 Character Only  "));
			}else if(StringUtils.isBlank(req.getPaymentYn())) {
				errorList.add(new Error("06", "Payment", "Enter Payment Type"));
			} else if ( "Y".equalsIgnoreCase(req.getPaymentYn()) && StringUtils.isBlank(req.getPaymentRedirUrl())) {
				errorList.add(new Error("08", "PaymentRedirUrl", "Please Select PaymentRedirUrl  Category  "));
			}else if ("Y".equalsIgnoreCase(req.getPaymentYn()) && req.getPaymentRedirUrl().length() > 500) {
				errorList.add(new Error("10", "PaymentRedirUrl", "Please Enter PaymentRedirUrl within 500 Characters  "));
			}
			
			
			if (StringUtils.isBlank(req.getCheckerYn())) {
				errorList.add(new Error("05", "Checker", "Please Select Checker  "));
			} else if (req.getCheckerYn().length() > 1) {
				errorList.add(new Error("05", "Checker", "Enter Checker 1 Character Only  "));
			}else if(!("Y".equalsIgnoreCase(req.getCheckerYn())||"N".equalsIgnoreCase(req.getCheckerYn()))) {
				errorList.add(new Error("05", "Checker", "Enter Checker Y or N Only  "));
			}
			
			if (StringUtils.isBlank(req.getMakerYn())) {
				errorList.add(new Error("05", "Maker", "Please Select Maker "));
			} else if (req.getMakerYn().length() > 1) {
				errorList.add(new Error("05", "Maker", "Enter Maker 1 Character Only  "));
			}else if(!("Y".equalsIgnoreCase(req.getMakerYn())||"N".equalsIgnoreCase(req.getMakerYn()))) {
				errorList.add(new Error("05", "Maker", "Enter Maker Y or N Only  "));
			}
			
			if (StringUtils.isBlank(req.getCustConfirmYn())) {
				errorList.add(new Error("05", "CustomerConfirmation", "Please Select CustomerConfirmation  "));
			} else if (req.getCustConfirmYn().length() > 1) {
				errorList.add(new Error("05", "CustomerConfirmation", "Enter CustomerConfirmation 1 Character Only  "));
			}else if(!("Y".equalsIgnoreCase(req.getCustConfirmYn())||"N".equalsIgnoreCase(req.getCustConfirmYn()))) {
				errorList.add(new Error("05", "CustomerConfirmation", "Enter CustomerConfirmation Y or N Only  "));
			}

			
			if (StringUtils.isBlank(req.getProductDesc())) {
				errorList.add(new Error("08", "ProductDesc", "Please Select Product  Desc "));
			}else if (req.getProductDesc().length() > 500) {
				errorList.add(new Error("08", "ProductDesc", "Please Enter Product Desc within 500 Characters  "));
			}
			
		
	/*		if (StringUtils.isBlank(req.getAppLoginUrl())) {
				errorList.add(new Error("08", "AppLoginUrl", "Please Select AppLoginUrl  "));
			}else if (req.getAppLoginUrl().length() > 100) {
				errorList.add(new Error("11", "AppLoginUrl", "Please Enter AppLoginUrl within 100 Characters  "));
			} */
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("08", "CreatedBy", "Please Enter CreatedBy  "));
			}else if (req.getCreatedBy().length() > 50) {
				errorList.add(new Error("11", "CreatedBy", "Please Enter CreatedBy within 100 Characters  "));
			}
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode  "));
			}else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters  "));
			}

			if (req.getBrokerCommissionDetails() ==null || req.getBrokerCommissionDetails().size() <= 0 ) {
				errorList.add(new Error("10", "BrokerCommissionDetails", "Please Enter Atleast One Broker Commission Details "));
			} else {
				Long row = 0L ;
				for (BrokerCommissionDetailsReq data : req.getBrokerCommissionDetails()) {
					row = row + 1 ;
				if (StringUtils.isBlank(data.getCommissionPercent())) {
					errorList.add(new Error("01", "CommissionPercent", "Please Enter Commission Percent In Row No : " + row));
				}if (!data.getCommissionPercent().matches("[0-9.]+")){
					errorList.add(new Error("01","CommissionPercent", "Please Enter Valid Commission Percent In Row No : " + row)); 
				}else if (Double.valueOf(data.getCommissionPercent()) >= 100){
					errorList.add(new Error("01","CommissionPercent", "Please Enter Valid Commission Percent In Row No : " + row)); 
				}
					
				if (StringUtils.isBlank(data.getRegulatoryCode())) {
					errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode  In Row No : " + row ));
				}else if (data.getRegulatoryCode().length() > 20) {
					errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters  In Row No : " + row ));
				}
				
				if (StringUtils.isBlank(data.getCoreAppCode())) {
						errorList.add(new Error("02", "CoreAppCode", "Please Enter CoreAppCode In Row No : " + row ));
				} else if (data.getCoreAppCode().length() > 20) {
						errorList.add(new Error("02", "CoreAppCode", "CoreAppCode under 20 Characters only allowed In Row No : " + row ));
				} 
				
				if (StringUtils.isBlank(data.getCommissionVatYn())) {
					errorList.add(new Error("05", "CommissionVat", "Please Select Commission Vat Type In Row No : " + row));
				} else if (data.getCommissionVatYn().length() > 1) {
					errorList.add(new Error("05", "CommissionVat", "Enter CommissionVat Type 1 Character OnlyIn Row No : " + row));
				}else if(!("Y".equalsIgnoreCase(data.getCommissionVatYn())||"N".equalsIgnoreCase(data.getCommissionVatYn()))) {
					errorList.add(new Error("05", "CommissionVat", "Enter Commission Vat Y or N Only In Row No : " + row));
				} else if ("Y".equalsIgnoreCase(data.getCommissionVatYn()) ) {
					if (StringUtils.isBlank(data.getCommissionVatPercent())) {
						errorList.add(new Error("01", "CommissionVatPercent", "Please Enter Commission Vat Percent In Row No : " + row));
					}else if (!data.getCommissionVatPercent().matches("[0-9.]+")){
						errorList.add(new Error("01","CommissionVatPercent", "Please Enter Valid Commission Vat Percent In Row No : " + row)); 
					}else if (Double.valueOf(data.getCommissionVatPercent()) >= 100){
						errorList.add(new Error("01","CommissionVatPercent", "Please Enter Valid Commission Vat Percent In Row No : " + row)); 
					}
				}
					
				if(StringUtils.isBlank(data.getSumInsuredStart())) {
					errorList.add(new Error("02", "Sum Insured Start", "Plese Enter Sum Insured Start in In Row No : " + row));
				} else if (! data.getSumInsuredStart().matches("[0-9.]+") ) {
					errorList.add(new Error("02", "Sum Insured Start", "Plese Enter Valid Number Sum Insured Start In Row No : " + row  ));
				}
					
				if(StringUtils.isBlank(data.getSumInsuredEnd())) {
					errorList.add(new Error("02", "Sum Insured End", "Plese Enter Sum Insured End in In Row No : " + row ));
				} else if (! data.getSumInsuredEnd().matches("[0-9.]+") ) {
					errorList.add(new Error("02", "Sum Insured End", "Plese Enter Valid Number Sum Insured End In Row No : " + row ));
				} else if (StringUtils.isNotBlank(data.getSumInsuredStart()) && StringUtils.isBlank(data.getSumInsuredEnd())  ) {
					if (Long.valueOf(data.getSumInsuredStart()) > Long.valueOf(data.getSumInsuredEnd()) ) {
						errorList.add(new Error("02", "Sum Insured End", "Sum Insured Start Greater Than Sum Insured End In Row No : " + row  ));
					}
				}
					
				if (StringUtils.isBlank(data.getBackDays())) {
					errorList.add(new Error("10", "BackDays", "Please Enter BackDays In Row No : " + row ));
				}	
				else if (StringUtils.isNotBlank(data.getBackDays())&& ! data.getBackDays().matches("[0-9]+") ) {
					errorList.add(new Error("10", "BackDays", "Plese Enter Valid Number BackDays In Row No : " + row   ));
				}
				
				}
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public List<CompanyProductMasterRes> getallNonSelectedBrokerCompanyProducts(BrokerCompanyProductGetReq req) {
		List<CompanyProductMasterRes> resList = new ArrayList<CompanyProductMasterRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
	
			// Find All
			Root<CompanyProductMaster> b = query.from(CompanyProductMaster.class);
	
			// Select
			query.select(b);
	
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
			effectiveDate.where(a1,a2,a3);
	
			// Effective Date End
			Subquery<Long> effectiveDate5 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm5 = effectiveDate5.from(CompanyProductMaster.class);
			effectiveDate5.select(cb.max(ocpm5.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(b.get("productId"),ocpm5.get("productId") );
			Predicate a5 = cb.equal(ocpm5.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			effectiveDate5.where(a4,a5,a6);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("productName")));
			
			// Company Product Effective Date Max Filter
			Subquery<Long> product = query.subquery(Long.class);
			Root<LoginProductMaster> ps = product.from(LoginProductMaster.class);
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateStart")));
			Predicate eff1 = cb.equal(ocpm2.get("productId"), ps.get("productId"));
			Predicate eff2 = cb.equal(ocpm2.get("companyId"), ps.get("companyId"));
			Predicate eff3 = cb.equal(ocpm2.get("loginId"), ps.get("loginId"));
			Predicate eff4 = cb.lessThanOrEqualTo(ocpm2.get("effectiveDateStart"),today);
			effectiveDate2.where(eff1,eff2,eff3,eff4);
			
			// Product Section Filter
			product.select(ps.get("productId"));
			Predicate ps1 = cb.equal(ps.get("companyId"), req.getInsuranceId());
			Predicate ps3 = cb.equal(ps.get("loginId"), req.getLoginId());
			Predicate ps4 = cb.equal(ps.get("effectiveDateStart"),effectiveDate2);
			Predicate ps5 = cb.equal(ps.get("status"),"Y");
			product.where(ps1,ps3,ps4,ps5);
			
			// Where
			Expression<String>e0= b.get("productId");
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n4 = e0.in(product).not();
			Predicate n5 = cb.equal(b.get("effectiveDateEnd"), effectiveDate5);
			Predicate n6 = cb.equal(b.get("status"), "Y");
			Predicate n7 = cb.equal(b.get("companyId"), req.getInsuranceId());
			query.where(n1,n4,n5,n6,n7).orderBy(orderList);
	
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			// Map
			for (CompanyProductMaster data : list ) {
				CompanyProductMasterRes res = new CompanyProductMasterRes();
	
				res = dozerMapper.map(data, CompanyProductMasterRes.class);
				res.setProductId(data.getProductId().toString());
				resList.add(res);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}

	@Override
	public SuccessRes changeStatusOfCompanyProduct(BrokerProductChangeReq req) {
		SuccessRes res = new SuccessRes();
		try {
			Date today  = req.getEffectiveDateStart()!=null ?req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar(); 
			
			LoginProductMaster updateRecord  = new LoginProductMaster();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2,a3,a4);

			// Order By
		//	List<Order> orderList = new ArrayList<Order>();
		//	orderList.add(cb.asc(b.get("branchName")));
			
			// Where
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =  cb.equal(b.get("productId"), req.getProductId() );
			Predicate n4 =  cb.equal(b.get("companyId"), req.getCompanyId() );
			Predicate n5 =  cb.equal(b.get("loginId"), req.getLoginId() );

			query.where( n2, n3,n4,n5);//.orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			updateRecord = list.get(0) ;
				
			if (req.getStatus().equalsIgnoreCase("N") )	{
					// Delete Old Records
					cal.setTime(today);
					cal.set(Calendar.HOUR_OF_DAY, 23);
					cal.set(Calendar.MINUTE, 30);
					today   = cal.getTime();
					
					// create update
					CriteriaDelete<LoginProductMaster> delete = cb.createCriteriaDelete(LoginProductMaster.class);
					Root<LoginProductMaster> pm = delete.from(LoginProductMaster.class);
					
					 // Where	
					Predicate n6 = cb.equal(pm.get("companyId"), req.getCompanyId());
					Predicate n7 = cb.greaterThanOrEqualTo(pm.get("effectiveDateStart"), today);
					Predicate n8 = cb.equal(pm.get("productId"), req.getProductId() );
					Predicate n9 = cb.equal(pm.get("loginId"), req.getLoginId() );
					delete.where(n6,n7,n8,n9);	
					em.createQuery(delete).executeUpdate();
					

					// Insert Updated Record
					updateRecord.setStatus(req.getStatus());
					loginProductRepo.save(updateRecord);
					
				
			} else if (req.getStatus().equalsIgnoreCase("Y") ) {
				// Insert Updated Record
				updateRecord.setStatus(req.getStatus());
				loginProductRepo.save(updateRecord);
			}
			
			res.setResponse("Status Changed");
			res.setSuccessId(req.getCompanyId());
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<DropDownRes> getBrokerProductDropdown(BrokerProductReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
			
			// Find All
			Root<LoginProductMaster>    c = query.from(LoginProductMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a4 = cb.equal(c.get("loginId"),ocpm1.get("loginId") );
			effectiveDate.where(a1,a2,a3,a4);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("productId"),ocpm2.get("productId") );
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("loginId"),ocpm2.get("loginId") );
			effectiveDate2.where(a5,a6,a7,a8);
			
			// Filer Product IDs
			Subquery<Long> productIds = query.subquery(Long.class);
			Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);
			
			
			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
			effectiveDate3.select(cb.max(ocpm4.get("effectiveDateStart")));
			Predicate a9 = cb.equal(cm.get("productId"),ocpm4.get("productId") );
			Predicate a10 = cb.equal(cm.get("companyId"),ocpm4.get("companyId") );
			Predicate a11 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
			effectiveDate3.where(a9,a10,a11);
			
			Subquery<Long> effectiveDate4 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
			effectiveDate4.select(cb.max(ocpm5.get("effectiveDateEnd")));
			Predicate a12 = cb.equal(cm.get("productId"),ocpm5.get("productId") );
			Predicate a13 = cb.equal(cm.get("companyId"),ocpm5.get("companyId") );
			Predicate a14 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			effectiveDate4.where(a12,a13,a14);
			
			
			productIds.select(cm.get("productId"));
			Predicate a15 = cb.equal(cm.get("companyId"),c.get("companyId"));
			Predicate a16 = cb.equal(cm.get("status"),"Y" );
			Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
			productIds.where(a15,a16,a17,a18);
			
			//In 
			Expression<String>e0=c.get("productId");
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("loginId"), req.getLoginId());
			Predicate n6 = e0.in(productIds);
			query.where(n12,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);			
			list =  result.getResultList(); 
		
			for ( LoginProductMaster data : list ) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getProductId().toString());
				res.setCodeDesc(data.getProductName());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<CompanyProductMasterRes> getallNonSelectedUserCompanyProducts(UserCompanyProductGetReq req) {
		List<CompanyProductMasterRes> resList = new ArrayList<CompanyProductMasterRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			Date today  =  new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			LoginMaster brokerData = loginRepo.findByAgencyCodeAndOaCode(req.getOaCode(),Integer.valueOf(req.getOaCode()));
			
			List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);
	
			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);
	
			// Select
			query.select(b);
	
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
			Predicate a7 = cb.equal(ocpm1.get("loginId"),b.get("loginId"));
			effectiveDate.where(a1,a2,a3,a7);
	
			// Effective Date End
			Subquery<Long> effectiveDate5 = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm5 = effectiveDate5.from(LoginProductMaster.class);
			effectiveDate5.select(cb.max(ocpm5.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(b.get("productId"),ocpm5.get("productId") );
			Predicate a5 = cb.equal(ocpm5.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(ocpm5.get("loginId"),b.get("loginId"));
			effectiveDate5.where(a4,a5,a6,a8);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("productName")));
			
			// Company Product Effective Date Max Filter
			Subquery<Long> product = query.subquery(Long.class);
			Root<LoginProductMaster> ps = product.from(LoginProductMaster.class);
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm2 = amendId.from(LoginProductMaster.class);
			amendId.select(cb.max(ocpm2.get("amendId")));
			Predicate eff1 = cb.equal(ocpm2.get("productId"), ps.get("productId"));
			Predicate eff2 = cb.equal(ocpm2.get("companyId"), ps.get("companyId"));
			Predicate eff3 = cb.equal(ocpm2.get("loginId"), ps.get("loginId"));
			amendId.where(eff1,eff2,eff3);
			
			// Product Section Filter
			product.select(ps.get("productId"));
			Predicate ps1 = cb.equal(ps.get("companyId"), req.getInsuranceId());
			Predicate ps3 = cb.equal(ps.get("loginId"), req.getLoginId());
			Predicate ps4 = cb.equal(ps.get("amendId"),amendId);
			product.where(ps1,ps3,ps4);
			
			// Where
			Expression<String>e0= b.get("productId");
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n4 = e0.in(product).not();
			Predicate n5 = cb.equal(b.get("effectiveDateEnd"), effectiveDate5);
			Predicate n6 = cb.equal(b.get("status"), "Y");
			Predicate n7 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n8 = cb.equal(b.get("loginId"), brokerData.getLoginId());
			query.where(n1,n4,n5,n6,n7,n8).orderBy(orderList);
	
			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			// Map
			for (LoginProductMaster data : list ) {
				CompanyProductMasterRes res = new CompanyProductMasterRes();
	
				res = dozerMapper.map(data, CompanyProductMasterRes.class);
				res.setProductId(data.getProductId().toString());
				resList.add(res);
			}
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}



	@Override
	public LoginCreationRes saveIssuerProducts(AttachIssuerProductRequest req1) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try { 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(new Date() );  cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes()) ;
			cal.set(Calendar.SECOND, today.getSeconds());
			Date effDate = cal.getTime();
			Date endDate = sdformat.parse("12/12/2050") ;
			cal.setTime(sdformat.parse("12/12/2050"));  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 50) ;
			endDate = cal.getTime() ;
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			// Changing Added Products Date not in Req
			
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query2 = cb2.createQuery(LoginProductMaster.class);
			List<LoginProductMaster> list2 = new ArrayList<LoginProductMaster>();
			
			// Find All
			Root<LoginProductMaster>    c2 = query2.from(LoginProductMaster.class);		
			
			// Select
			query2.select(c2);
			
		
			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb2.asc(c2.get("productName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate4 = query2.subquery(Long.class);
			Root<LoginProductMaster> ocpm4 = effectiveDate4.from(LoginProductMaster.class);
			effectiveDate4.select(cb2.max(ocpm4.get("effectiveDateStart")));
			Predicate a11 = cb2.equal(c2.get("productId"),ocpm4.get("productId") );
			Predicate a12 = cb2.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
			Predicate a13 = cb2.equal(c2.get("companyId"),ocpm4.get("companyId") );
			Predicate a14 = cb2.equal(c2.get("loginId"),ocpm4.get("loginId") );
			
			effectiveDate4.where(a11,a12,a13,a14);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate5 = query2.subquery(Long.class);
			Root<LoginProductMaster> ocpm5 = effectiveDate5.from(LoginProductMaster.class);
			effectiveDate5.select(cb2.max(ocpm5.get("effectiveDateEnd")));
			Predicate a15 = cb2.equal(c2.get("productId"),ocpm5.get("productId") );
			Predicate a16 = cb2.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			Predicate a17 = cb2.equal(c2.get("companyId"),ocpm5.get("companyId") );
			Predicate a18 = cb2.equal(c2.get("loginId"),ocpm5.get("loginId") );

			effectiveDate5.where(a15,a16,a17,a18);
			
			
		    // Where	
			Predicate n11 = cb2.equal(c2.get("status"), "Y");
			Predicate n12 = cb2.equal(c2.get("effectiveDateStart"), effectiveDate4);
			Predicate n13 = cb2.equal(c2.get("effectiveDateEnd"), effectiveDate5);
			Predicate n14 =cb2.equal(c2.get("loginId"), req1.getLoginId());
			Predicate n15 =cb2.equal(c2.get("companyId"), req1.getInsuranceId());
			query2.where(n11,n12,n13,n14,n15).orderBy(orderList2);
			
			// Get Result
			TypedQuery<LoginProductMaster> result2 = em.createQuery(query2);			
			list2 =  result2.getResultList();  

				List<IssuerProductListReq> productlist = req1.getIssuerProductReq();
		for (LoginProductMaster data : list2 ) {

		List<IssuerProductListReq> filterProduct = productlist.stream().filter( o ->  o.getProductId().equalsIgnoreCase(data.getProductId().toString())).collect(Collectors.toList());

		if( filterProduct.size()<=0	) {
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(effDate.getTime()- MILLS_IN_A_DAY);
			data.setEffectiveDateEnd(oldEndDate);				
			Date oldEffDate = new Date(oldEndDate.getTime()- MILLS_IN_A_DAY);
			data.setEffectiveDateStart(oldEffDate);
			
			loginProductRepo.saveAndFlush(data);
			
			
		}
		
		}
			
			// Adding New Products as per Req
			
			for(IssuerProductListReq req : req1.getIssuerProductReq()) {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
			
			// Find All
			Root<CompanyProductMaster>    c = query.from(CompanyProductMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"),ocpm1.get("productId") );
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"),ocpm1.get("companyId") );
			effectiveDate.where(a1,a2,a3);
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"),ocpm2.get("productId") );
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a6 = cb.equal(c.get("companyId"),ocpm2.get("companyId") );
			effectiveDate2.where(a4,a5,a6);
			
			//In 
			Expression<String>e0=c.get("productId");
			
		    // Where	
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 =e0.in( req.getProductId());
			Predicate n5 =cb.equal(c.get("companyId"), req1.getInsuranceId());
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			LoginMaster loginData = loginRepo.findByLoginId(req1.getLoginId());

			LoginProductMaster save = new LoginProductMaster();
			
						
			for ( CompanyProductMaster data : list  ) {
		
				dozerMapper.map(data,save);
				save.setCompanyId(req1.getInsuranceId());
				save.setCreatedBy(req1.getCreatedBy());
				save.setLoginId(req1.getLoginId());
				save.setBackDays(0);
				save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));
				save.setOaCode(loginData.getOaCode());
				save.setSumInsuredStart(new BigDecimal(req.getSuminsuredStart()));
				save.setSumInsuredEnd(new BigDecimal(req.getSuminsuredEnd()));
				String financeId = data.getFinancialEndtIds() ;
				String nonFinanceId = data.getNonFinancialEndtIds();
			
				List<LoginProductMaster> loginproduct = loginProductRepo.findByLoginIdAndCompanyIdAndProductIdOrderByAmendIdDesc(req1.getLoginId(),req1.getInsuranceId(),Integer.valueOf(data.getProductId()));
				if(loginproduct.size()>0 && loginproduct!=null) {
					
					LoginProductMaster lastRecord = loginproduct.get(0);
					financeId = lastRecord.getFinancialEndtIds() ;
					nonFinanceId = lastRecord.getNonFinancialEndtIds();
					
					if(lastRecord.getEffectiveDateStart().equals(effDate)) {
						save.setAmendId(loginproduct.get(0).getAmendId());											
					}
					else {
					save.setAmendId(loginproduct.get(0).getAmendId()+1);
					long MILLS_IN_A_DAY = 1000*60*60*24;
					Date oldEndDate = new Date(effDate.getTime()- MILLS_IN_A_DAY);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					}
					loginProductRepo.saveAndFlush(lastRecord);

				}
				else {
					save.setAmendId(0);

				}
				save.setFinancialEndtIds(financeId);
				save.setNonFinancialEndtIds(nonFinanceId);
				save.setEffectiveDateStart(effDate);
				save.setEffectiveDateEnd(endDate);
				save.setEntryDate(new Date());

				loginProductRepo.saveAndFlush(save);
				log.info("Saved Details is ---> " + json.toJson(save));
				
			}	
			
			res.setResponse("Products Added Successfully");
			}
			
			
			
			//change status "N"
			List<IssuerProductListReq> prodlist = req1.getIssuerProductReq();
			List<String> prodIdsReq = prodlist.stream().map(o -> o.getProductId()).collect(Collectors.toList());
			
			List<LoginProductMaster> lp = loginProductRepo.findByLoginIdAndCompanyId(req1.getLoginId(), req1.getInsuranceId());
			List<LoginProductMaster> lpdist = lp.stream().filter(distinctByKey(o -> Arrays.asList(o.getProductId()))).collect(Collectors.toList());
			
			for(LoginProductMaster prodId: lpdist) {
				
				if(! prodIdsReq.contains(prodId.getProductId().toString())) {
					
					List<LoginProductMaster> lpfilter = lp.stream().filter(o -> o.getProductId().equals(prodId.getProductId())).collect(Collectors.toList());
					
						int maxValue =  lpfilter.stream().max(Comparator.comparingInt(LoginProductMaster::getAmendId)).map(LoginProductMaster::getAmendId).orElse(0);
						
						List<LoginProductMaster> lpf = lpfilter.stream().filter(o -> o.getAmendId().equals(maxValue)).collect(Collectors.toList());
						
							lpf.get(0).setStatus("N");
							loginProductRepo.saveAndFlush(lpf.get(0));
						}
				
				
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}



	@Override
	public List<IssuerProductGetRes> getIssuerProducts(IssuerProductGetReq req) {
		// TODO Auto-generated method stub
		List<IssuerProductGetRes> resList = new ArrayList<IssuerProductGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			
			if(req.getUserType().equalsIgnoreCase("User"))  {
				
			LoginMaster login = loginRepo.findByLoginId(req.getLoginId());
			Integer oaCode = login.getOaCode();
			
			LoginMaster loginid =  loginRepo.findByAgencyCodeAndOaCode(oaCode.toString(),oaCode);
			List<CompanyProductMaster> companylist = new ArrayList<CompanyProductMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);

			// Find All
			Root<CompanyProductMaster> b = query.from(CompanyProductMaster.class);

			// Select
			query.select(b);
	
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));

			effectiveDate.where(a1,a2);

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 =  cb.equal(b.get("companyId"), req.getInsuranceId() );
		
			query.where(n1, n2);

			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			companylist = result.getResultList();
		
		
//			List<LoginProductMaster> loginlist = new ArrayList<LoginProductMaster>();
//
//			CriteriaBuilder cb2 = em.getCriteriaBuilder();
//			CriteriaQuery<LoginProductMaster> query2 = cb2.createQuery(LoginProductMaster.class);
//
//			// Find All
//			Root<LoginProductMaster> b2 = query2.from(LoginProductMaster.class);
//
//			// Select
//			query2.select(b2);
//
//			// Effective Date Max Filter
//			Subquery<Long> effectiveDate2 = query2.subquery(Long.class);
//			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
//			effectiveDate2.select(cb2.max(ocpm2.get("effectiveDateStart")));
//			Predicate a11 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
//			Predicate a12 = cb2.equal(ocpm2.get("loginId"), b2.get("loginId"));
//			Predicate a13 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
//			
//			effectiveDate2.where(a11,a12,a13);
//
//			
//			// Where
//			Predicate n11 = cb2.equal(b2.get("effectiveDateStart"), effectiveDate2);
//			Predicate n12 = cb2.equal(b2.get("companyId"), req.getInsuranceId() );
//			Predicate n13 = cb2.equal(b2.get("loginId"), loginid.getLoginId());
//
//			query2.where( n11,n12,n13);
//
//			// Get Result
//			TypedQuery<LoginProductMaster> result2 = em.createQuery(query2);
//			loginlist = result2.getResultList();


			List<LoginProductMaster> loginlist = loginProductRepo.findByLoginIdAndCompanyId(req.getLoginId(), req.getInsuranceId());	
			
			for(CompanyProductMaster data : companylist) {
	        
				List<LoginProductMaster> filterUse = loginlist.stream().
						filter( o ->  o.getProductId().toString().
								equalsIgnoreCase(data.getProductId().toString()))
						.collect(Collectors.toList());
				
				int maxValue =  filterUse.stream().max(Comparator.comparingInt(LoginProductMaster::getAmendId)).map(LoginProductMaster::getAmendId).orElse(0);
				
				List<LoginProductMaster> filterUser = filterUse.stream().filter(o -> o.getAmendId().equals(maxValue)).collect(Collectors.toList());

		        IssuerProductGetRes res = new IssuerProductGetRes();
		        String endorsementid ="";
		        String referralid = "";
		        if(filterUser.size()>0) {
		        	dozerMapper.map(filterUser.get(0), res);
		        	endorsementid = filterUser.get(0).getFinancialEndtIds()==null?"":filterUser.get(0).getFinancialEndtIds();
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?  filterUser.get(0).getNonFinancialEndtIds() :endorsementid + "," +  filterUser.get(0).getNonFinancialEndtIds() ;
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?   "" :  endorsementid ;
					referralid = filterUser.get(0).getReferralId()==null?"":filterUser.get(0).getReferralId();
					ArrayList<String> endorsementids = new ArrayList<String>(Arrays.asList(endorsementid));
			        ArrayList<String> referralids = new ArrayList<String>(Arrays.asList(referralid));
			        res.setEndorsementIds(endorsementids);
					res.setReferralIds(referralids);
					
					
					if(filterUser.get(0).getStatus().equalsIgnoreCase("N"))
						res.setIsOptedYn("N");
					else
						res.setIsOptedYn("Y");
						
					res.setColumnName( loginlist.size() > 0 ?loginlist.get(0).getColumnName() : "");
					CalcEngine engine = new CalcEngine();					
					engine.setProductId(data.getProductId().toString());
					engine.setInsuranceId(data.getCompanyId());
					engine.setSectionId("");					
					String oneProduct = ratingutil.collectProductType(engine);
					if (oneProduct.equals("M")) {
						res.setTableName("MsVehicleDetails");
					}
					else if (oneProduct.equals("H")) {
						res.setTableName("MsHumanDetails");
					}
					else if (oneProduct.equals("A")) {
						res.setTableName("MsAssetDetails");
					}
					res.setColumnName( loginlist.size() > 0 ?loginlist.get(0).getColumnName() : "");
					resList.add(res);
				}
		        else {
		        	dozerMapper.map(data, res);
		        	endorsementid = data.getFinancialEndtIds()==null?"":data.getFinancialEndtIds();
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?  data.getNonFinancialEndtIds() :endorsementid + "," +  data.getNonFinancialEndtIds() ;
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?   "" :  endorsementid ;
					referralid = data.getReferralId()==null?"":data.getReferralId();
					ArrayList<String> endorsementids = new ArrayList<String>(Arrays.asList(endorsementid));
			        ArrayList<String> referralids = new ArrayList<String>(Arrays.asList(referralid));
			        res.setEndorsementIds(endorsementids);
					res.setReferralIds(referralids);
				
					
//					if(filterUser.get(0).getStatus().equalsIgnoreCase("N"))
//						res.setIsOptedYn("N");
//					else
//						res.setIsOptedYn("Y");
					
					
					CalcEngine engine = new CalcEngine();					
					engine.setProductId(data.getProductId().toString());
					engine.setInsuranceId(data.getCompanyId());
					engine.setSectionId("");					
					String oneProduct = ratingutil.collectProductType(engine);
					if (oneProduct.equals("M")) {
						res.setTableName("MsVehicleDetails");
					}
					else if (oneProduct.equals("H")) {
						res.setTableName("MsHumanDetails");
					}
					else if (oneProduct.equals("A")) {
						res.setTableName("MsAssetDetails");
					}
					res.setColumnName( loginlist.size() > 0 ?loginlist.get(0).getColumnName() : "");
					resList.add(res);
		        }
	        }
			}
			
			
			else {
			List<CompanyProductMaster> companylist = new ArrayList<CompanyProductMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);

			// Find All
			Root<CompanyProductMaster> b = query.from(CompanyProductMaster.class);

			// Select
			query.select(b);
	
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
					
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));

			effectiveDate.where(a1,a2);

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 =  cb.equal(b.get("companyId"), req.getInsuranceId() );
			query.where(n1, n2);

			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			companylist = result.getResultList();
		
		
			
			
//			List<LoginProductMaster> loginlist = new ArrayList<LoginProductMaster>();
//
//			CriteriaBuilder cb2 = em.getCriteriaBuilder();
//			CriteriaQuery<LoginProductMaster> query2 = cb2.createQuery(LoginProductMaster.class);
//
//			// Find All
//			Root<LoginProductMaster> b2 = query2.from(LoginProductMaster.class);
//
//			// Select
//			query2.select(b2);
//
//			// Effective Date Max Filter
//			Subquery<Long> effectiveDate2 = query2.subquery(Long.class);
//			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
//			effectiveDate2.select(cb2.max(ocpm2.get("effectiveDateStart")));
//			Predicate a11 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
//			Predicate a12 = cb2.equal(ocpm2.get("loginId"), b2.get("loginId"));
//			Predicate a13 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
//			
//			effectiveDate2.where(a11,a12,a13);
//
//			
//			// Where
//			Predicate n11 = cb2.equal(b2.get("effectiveDateStart"), effectiveDate2);
//			Predicate n12 = cb2.equal(b2.get("companyId"), req.getInsuranceId() );
//			Predicate n13 = cb2.equal(b2.get("loginId"), req.getLoginId() );
//
//			query2.where( n11,n12,n13);
//
//			// Get Result
//			TypedQuery<LoginProductMaster> result2 = em.createQuery(query2);
//			loginlist = result2.getResultList();
			
			List<LoginProductMaster> loginlist = loginProductRepo.findByLoginIdAndCompanyId(req.getLoginId(), req.getInsuranceId());	
			
			for(CompanyProductMaster data : companylist) {
	        
				
				
				List<LoginProductMaster> filterUse = loginlist.stream().
						filter( o ->  o.getProductId().toString().
								equalsIgnoreCase(data.getProductId().toString()))
						.collect(Collectors.toList());
				
				int maxValue =  filterUse.stream().max(Comparator.comparingInt(LoginProductMaster::getAmendId)).map(LoginProductMaster::getAmendId).orElse(0);
				
				List<LoginProductMaster> filterUser = filterUse.stream().filter(o -> o.getAmendId().equals(maxValue)).collect(Collectors.toList());

		        IssuerProductGetRes res = new IssuerProductGetRes();
		        String endorsementid ="";
		        String referralid = "";
		        if(filterUser.size()>0) {
		        	dozerMapper.map(filterUser.get(0), res);
		        	endorsementid = filterUser.get(0).getFinancialEndtIds()==null?"":filterUser.get(0).getFinancialEndtIds();
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?  filterUser.get(0).getNonFinancialEndtIds() :endorsementid + "," +  filterUser.get(0).getNonFinancialEndtIds() ;
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?   "" :  endorsementid ;
		        	referralid = filterUser.get(0).getReferralId()==null?"":filterUser.get(0).getReferralId();
					//referralid=referralid.substring(1);
					
					ArrayList<String> endorsementids = new ArrayList<String>(Arrays.asList(endorsementid));
			        ArrayList<String> referralids = new ArrayList<String>(Arrays.asList(referralid));
			
			        res.setEndorsementIds(endorsementids);
					res.setReferralIds(referralids);
					
					if(filterUser.get(0).getStatus().equalsIgnoreCase("N"))
						res.setIsOptedYn("N");
					else
						res.setIsOptedYn("Y");
					
					CalcEngine engine = new CalcEngine();					
					engine.setProductId(data.getProductId().toString());
					engine.setInsuranceId(data.getCompanyId());
					engine.setSectionId("");
					String oneProduct ="" ;
					try { 
					oneProduct =  ratingutil.collectProductType(engine);
					}  catch (Exception e) {
						e.printStackTrace();
						log.info("Exception is --->" + e.getMessage());
						
					}
					if (oneProduct.equals("M")) {
						res.setTableName("MsVehicleDetails");
					}
					else if (oneProduct.equals("H")) {
						res.setTableName("MsHumanDetails");
					}
					else if (oneProduct.equals("A")) {
						res.setTableName("MsAssetDetails");
					} else {
						res.setTableName("MsAssetDetails");
					}
					res.setColumnName( loginlist.size() > 0 ?loginlist.get(0).getColumnName() : "");

					resList.add(res);
				}
		        else {
		        	dozerMapper.map(data, res);
		        	endorsementid = data.getFinancialEndtIds()==null?"":data.getFinancialEndtIds();
		         	endorsementid =  StringUtils.isBlank(endorsementid) ?  data.getNonFinancialEndtIds() :endorsementid + "," +  data.getNonFinancialEndtIds() ;
		        	endorsementid =  StringUtils.isBlank(endorsementid) ?   "" :  endorsementid ;
		        	referralid = data.getReferralId()==null?"":data.getReferralId();
					//referralid=referralid.substring(1);

					ArrayList<String> endorsementids = new ArrayList<String>(Arrays.asList(endorsementid));
			        ArrayList<String> referralids = new ArrayList<String>(Arrays.asList(referralid));
			        res.setEndorsementIds(endorsementids);
					res.setReferralIds(referralids);
					
//					if(filterUser.get(0).getStatus().equalsIgnoreCase("N"))
//						res.setIsOptedYn("N");
//					else
//						res.setIsOptedYn("Y");
					
					CalcEngine engine = new CalcEngine();					
					engine.setProductId(data.getProductId().toString());
					engine.setInsuranceId(data.getCompanyId());
					engine.setSectionId("");
					String oneProduct ="" ;
					try { 
					oneProduct =  ratingutil.collectProductType(engine);
					}  catch (Exception e) {
						e.printStackTrace();
						log.info("Exception is --->" + e.getMessage());
						
					}
					
					if (oneProduct.equals("M")) {
						res.setTableName("MsVehicleDetails");
					}
					else if (oneProduct.equals("H")) {
						res.setTableName("MsHumanDetails");
					}
					else if (oneProduct.equals("A")) {
						res.setTableName("MsAssetDetails");
					} else {
						res.setTableName("MsAssetDetails");
					}
					res.setColumnName( loginlist.size() > 0 ?loginlist.get(0).getColumnName() : "");
					resList.add(res);
		        }
	        }
			}
			
			
			
			
		}
		
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}



	@Override
	public LoginCreationRes saveProductsEndtIds(AttachEndtIdsReq req) {
		// TODO Auto-generated method stub
				SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
				LoginCreationRes res = new LoginCreationRes();
				DozerBeanMapper dozerMapper = new  DozerBeanMapper();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
				LoginProductMaster saveData = new LoginProductMaster();
				try { 
					Integer amendId = 0 ;
					String branchCode = "";
					Date startDate = new Date() ;
					String end = "31/12/2050";
					Date endDate = sdformat.parse(end);
					long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
					Date oldEndDate = new Date(startDate.getTime() - MILLIS_IN_A_DAY);
					Date entryDate = null ;
					String createdBy = "" ;

					// Changing Added Products Date not in Req
					
					CriteriaBuilder cb2 = em.getCriteriaBuilder();
					CriteriaQuery<LoginProductMaster> query2 = cb2.createQuery(LoginProductMaster.class);
					List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
					
					// Find All
					Root<LoginProductMaster>    c2 = query2.from(LoginProductMaster.class);		
					
					// Select
					query2.select(c2);
					
				
					// Order By
					List<Order> orderList2 = new ArrayList<Order>();
					orderList2.add(cb2.desc(c2.get("amendId")));	
					
					
				    // Where	
					Predicate n14 =cb2.equal(c2.get("loginId"), req.getLoginId());
					Predicate n15 =cb2.equal(c2.get("companyId"), req.getInsuranceId());
					Predicate n16 =cb2.equal(c2.get("productId"), req.getProductId());
					query2.where(n14,n15,n16).orderBy(orderList2);
					
					// Get Result
					TypedQuery<LoginProductMaster> result2 = em.createQuery(query2);			
					list =  result2.getResultList();  

					if (list.size() > 0) {
						Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
						
						if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
							dozerMapper.map( list.get(0), saveData )  ;
							amendId = list.get(0).getAmendId() + 1 ;
							entryDate = new Date() ;
							createdBy = req.getCreatedBy();
							LoginProductMaster lastRecord = list.get(0);
								lastRecord.setEffectiveDateEnd(oldEndDate);
								loginProductRepo.saveAndFlush(lastRecord);
							
						} else {
							amendId = list.get(0).getAmendId() ;
							entryDate = list.get(0).getEntryDate() ;
							createdBy = list.get(0).getCreatedBy();
							dozerMapper.map( list.get(0), saveData )  ;
							if (list.size()>1 ) {
								LoginProductMaster lastRecord = list.get(1);
								lastRecord.setEffectiveDateEnd(oldEndDate);
								loginProductRepo.saveAndFlush(lastRecord);
							}
						
					    }
					}
					
					if( "R".equalsIgnoreCase(req.getIdType())  ) {
						String referralids="";
						List<String> keys = req.getIds();
						for (int i = 0; i < keys.size(); i++) {
						referralids = referralids + "," + keys.get(i);				
						}
						referralids=referralids.substring(1);
						saveData.setReferralId(referralids);
					} else if( "F".equalsIgnoreCase(req.getIdType())  ) {
						String endorsementids="";
						List<String> keys1 = req.getIds();
						for (int i = 0; i < keys1.size(); i++) {
							endorsementids = endorsementids + "," + keys1.get(i);				
						}
						saveData.setFinancialEndtIds(endorsementids);
					} else if( "NF".equalsIgnoreCase(req.getIdType())  ) {
						String endorsementids="";
						List<String> keys1 = req.getIds();
						for (int i = 0; i < keys1.size(); i++) {
							endorsementids = endorsementids + "," + keys1.get(i);				
						}
						saveData.setNonFinancialEndtIds(endorsementids);
					}
				
					saveData.setEffectiveDateStart(startDate);
					saveData.setEffectiveDateEnd(endDate);
					saveData.setEntryDate(new Date());
					saveData.setAmendId(amendId);
					saveData.setEntryDate(entryDate);
					saveData.setCreatedBy(createdBy);
				
					loginProductRepo.saveAndFlush(saveData);
					
					res.setResponse("Updated Successfully");
					res.setAgencyCode(saveData.getAgencyCode().toString());
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is --->" + e.getMessage());
					return null;
				}
				return res;
			}


	
}

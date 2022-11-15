package com.maan.eway.admin.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
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
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.admin.req.AttachIssuerReferalReq;
import com.maan.eway.admin.req.AttachReferalReq;
import com.maan.eway.admin.req.IssuerCompanyReferalGetReq;
import com.maan.eway.admin.req.IssuerReferalGetReq;
import com.maan.eway.admin.res.IssuerReferalCompanyGetRes;
import com.maan.eway.admin.res.IssuerReferalCompniesRes;
import com.maan.eway.admin.res.IssuerReferalCriteriaRes;
import com.maan.eway.admin.res.IssuerReferalGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.service.LoginReferalService;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginReferalMaster;
import com.maan.eway.bean.ReferalMaster;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginReferalMasterRepository;

@Service
public class LoginReferalServiceImpl implements LoginReferalService {

	@Autowired
	private LoginReferalMasterRepository loginReferalRepo ;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();


	private Logger log=LogManager.getLogger(LoginReferalServiceImpl.class);

//*************************************** Add Referal Apis Methods **********************************************************//	


	@Transactional
	@Override
	public LoginCreationRes attachIssuerReferal(AttachIssuerReferalReq req) {
		LoginCreationRes res = new LoginCreationRes();
		try { 
			
			 String  successRes = "" ;
			if(req.getBranchCode().equalsIgnoreCase("All") ) {
				LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());				
				List<String> branches = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(","))) ;
				
				 List<BranchMaster>  branchList = branchCompanyIds(branches);
				 
				for (BranchMaster branch :  branchList) {
					successRes = attachIssuerReferal(req , branch.getBranchCode() ,branch.getCompanyId()  );	
				}
						
			} else {
					successRes = attachIssuerReferal(req , req.getBranchCode() , req.getInsuranceId());
			}
			
			res.setResponse(successRes);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	public List<BranchMaster> branchCompanyIds(List<String> branchCodes) {
		List<BranchMaster> branchList = new ArrayList<BranchMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);

			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

			// Select
			query.select(b);
			
			//In 
			Expression<String>e0=b.get("branchCode");
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("entryDate")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("status"), "Y");
			Predicate n3 = e0.in(branchCodes);

			query.where(n1, n2,n3).orderBy(orderList);

			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);
			branchList = result.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return branchList;
	}
	
	
	public String attachIssuerReferal(AttachIssuerReferalReq req ,String branchCode , String insCompanyId) {
		String res = "";
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			for ( AttachReferalReq data : req.getAttachedReferals() ) {
				
				Calendar cal = new GregorianCalendar();
				cal.setTime(data.getEffectiveDate());  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
				Date startDate = cal.getTime() ;
				cal.setTime(data.getEffectiveDate());  cal.set(Calendar.DAY_OF_MONTH, -1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 10);
				Date oldEndDate = cal.getTime() ;
				Date today = new Date();
				cal.setTime(data.getEffectiveDate());  cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes() );
				Date effDate = cal.getTime();
				
				// Find Old Record 
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<LoginReferalMaster> query = cb.createQuery(LoginReferalMaster.class);
				List<LoginReferalMaster> list = new ArrayList<LoginReferalMaster>();
				
				// Find All
				Root<LoginReferalMaster> lr = query.from(LoginReferalMaster.class);
	
				// Select
				query.select(lr);
	
				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<LoginReferalMaster> ocpm1 = effectiveDate.from(LoginReferalMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("referalId"), lr.get("referalId"));
				Predicate a2 = cb.equal(ocpm1.get("loginId"), lr.get("loginId"));
				Predicate a3 = cb.equal(ocpm1.get("companyId"), lr.get("companyId"));
				Predicate a4 = cb.equal(ocpm1.get("branchCode"), lr.get("branchCode"));
				Predicate a5 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , startDate);
				effectiveDate.where(a1,a2,a3,a4,a5);
	
				// Order By
				//List<Order> orderList = new ArrayList<Order>();
				//orderList.add(cb.asc(lp.get("effectiveDateStart")));
				
				// Where
				Predicate n1 = cb.equal(lr.get("referalId"), data.getReferalId());
				Predicate n2 = cb.equal(lr.get("loginId"), req.getLoginId());
				Predicate n3 = cb.equal(lr.get("companyId"),insCompanyId );
				Predicate n4 = cb.equal(lr.get("branchCode"),branchCode );
				Predicate n5 = cb.equal(lr.get("effectiveDateStart") , effectiveDate);
	
				query.where(n1, n2, n3,n4,n5);//.orderBy(orderList);
	
				// Get Result
				TypedQuery<LoginReferalMaster> result = em.createQuery(query);
				list = result.getResultList();
				
				if(list.size() > 0 ) {
					loginReferalRepo.delete(list.get(0));
				}
				Date endDate = sdf.parse("12/12/2050");
				
				LoginReferalMaster save = new LoginReferalMaster();
				dozerMapper.map(data, save);
				save.setCompanyId(insCompanyId);
				save.setBranchCode(branchCode);
				save.setEffectiveDateStart(effDate);	
				save.setEffectiveDateEnd(endDate);
				save.setEntryDate(new Date());
				save.setLoginId(req.getLoginId());
				loginReferalRepo.saveAndFlush(save);
				log.info("Saved Details is ---> " + json.toJson(save));
				if(list.size() > 0 ) {
					// Update Old Record
					LoginReferalMaster lastRecord = list.get(0) ;
					lastRecord.setEffectiveDateEnd(oldEndDate);
					loginReferalRepo.saveAndFlush(lastRecord);
				}
			}
			
			res = "Referal Added Successfully" ;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
//*************************************** Get Referal Apis Methods **********************************************************//	

	@Override
	public List<IssuerReferalCompniesRes> getIssuerReferals(IssuerReferalGetReq req) {
		List<IssuerReferalCompniesRes> companyList = new ArrayList<IssuerReferalCompniesRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			
			String loginId = req.getLoginId() ;
			LoginMaster loginData = loginRepo.findByLoginId(loginId);
			List<String> branchIds = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(","))) ;
			
			if(branchIds.size()>0 ) {
				
				List<ReferalCriteriaRes> referals = getReferalDetails( branchIds , today  );
				List<IssuerReferalCriteriaRes> loginReferals = getIssuerReferalDetails (loginId , branchIds , today ) ;
				
				// Grouping
				Map<String ,List<ReferalCriteriaRes>> groupByBranch = referals.stream().collect(Collectors.groupingBy(ReferalCriteriaRes :: getBranchCode )) ;
				for (String branch : groupByBranch.keySet()) { 
					IssuerReferalCompniesRes companyRes = new IssuerReferalCompniesRes();
					List<IssuerReferalGetRes> attachedReferals = new ArrayList<IssuerReferalGetRes>();
					
					List<ReferalCriteriaRes> filterReferals = groupByBranch.get(branch);
					
					for(ReferalCriteriaRes data :  filterReferals) {
						IssuerReferalGetRes referalRes = new IssuerReferalGetRes();
						
						// Filter Login Referals
						List<IssuerReferalCriteriaRes> filterLoginReferals = loginReferals.stream().filter( o -> o.getBranchCode().equalsIgnoreCase(data.getBranchCode()) && o.getReferalId().equals(data.getReferalId()) ).collect(Collectors.toList());
						
						referalRes.setStatus("N");
						if (filterLoginReferals.size() > 0 ) {
							String pattern = "#####0.00";
							DecimalFormat df = new DecimalFormat(pattern);
							referalRes.setOldReferalName(filterLoginReferals.get(0).getOldReferalName() );
							referalRes.setSumInsuredStart(filterLoginReferals.get(0).getSumInsuredStart()==null?"" : df.format(filterLoginReferals.get(0).getSumInsuredStart()) );
							referalRes.setSumInsuredEnd(filterLoginReferals.get(0).getSumInsuredEnd()==null?"" :df.format(filterLoginReferals.get(0).getSumInsuredEnd()) );
							referalRes.setStatus(filterLoginReferals.get(0).getStatus());
							referalRes.setRemarks(filterLoginReferals.get(0).getRemarks());	
						;
						}
						
						referalRes.setReferalId(data.getReferalId().toString());
						referalRes.setReferalName(data.getReferalName());
						referalRes.setReferalDesc(data.getReferalDesc() );
						attachedReferals.add(referalRes);
					}
					
					// Response 
					companyRes.setBranchCode(filterReferals.get(0).getBranchCode() );
					companyRes.setInsuranceId(filterReferals.get(0).getCompanyId() );
					companyRes.setBranchName(filterReferals.get(0).getBranchName() );
					companyRes.setAttachedReferals(attachedReferals);
					companyList.add(companyRes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}

	

	public List<ReferalCriteriaRes> getReferalDetails(List<String> branchIds , Date today ) {
		List<ReferalCriteriaRes> list = new ArrayList<ReferalCriteriaRes>();  
		try {
			// Product Query 	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalCriteriaRes> query = cb.createQuery(ReferalCriteriaRes.class);
			
			Root<ReferalMaster> rm  = query.from(ReferalMaster.class);
			
			// Select Company Name SubQuery for Effective Date Max Filter 
			Subquery<Long> bmEff = query.subquery(Long.class);
			Root<BranchMaster> b = bmEff.from(BranchMaster.class);
			Subquery<Long> branch = query.subquery(Long.class);
			Root<BranchMaster> bm = branch.from(BranchMaster.class);
			
			bmEff.select( cb.max(b.get("effectiveDateStart")) );
			Predicate b1 = cb.equal(bm.get("branchCode"), b.get("branchCode"));
			Predicate b2 = cb.lessThanOrEqualTo(b.get("effectiveDateStart") , today);
			bmEff.where(b1,b2);
			
			branch.select( bm.get("branchName")) ;
			Predicate bm1 = cb.equal(bm.get("branchCode"), rm.get("branchCode"));
			Predicate bm2  = cb.equal(bm.get("effectiveDateStart"),bmEff);
			Predicate bm3  = cb.equal(bm.get("status"),"Y");
			branch.where(bm1,bm2,bm3);
			
			// Select
			query.multiselect( rm  .get("referalId").alias("referalId") , rm  .get("referalName").alias("referalName") , rm.get("companyId").alias("companyId") ,
					rm.get("branchCode").alias("branchCode") , branch.alias("branchName") , rm.get("referalDesc").alias("referalDesc")  );

			// Product Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ReferalMaster> ocpm1 = effectiveDate.from(ReferalMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("referalId"), rm.get("referalId"));
			Predicate a2 = cb.equal(ocpm1.get("branchCode"), rm.get("branchCode"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2,a3); 
					
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(rm.get("entryDate")));
			
			//In 
			Expression<String>e0=rm.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(rm.get("status"), "Y");
			Predicate n2 = cb.equal(rm.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(branchIds);
			
			query.where(n1, n2, n3).orderBy(orderList);
		
			// Get Result
			TypedQuery<ReferalCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
				
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}

	public List<IssuerReferalCriteriaRes> getIssuerReferalDetails(String loginId , List<String> branchIds , Date today ) {
		List<IssuerReferalCriteriaRes> list = new ArrayList<IssuerReferalCriteriaRes>(); 
		try {
			// Login Product Query	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<IssuerReferalCriteriaRes> query = cb.createQuery(IssuerReferalCriteriaRes.class);

			Root<LoginReferalMaster> lm  = query.from(LoginReferalMaster.class);
			
			// Select Referal Name SubQuery for Effective Date Max Filter 
			Subquery<Long> rmEff = query.subquery(Long.class);
			Root<ReferalMaster> rm = rmEff .from(ReferalMaster.class);
			Subquery<Long> referal = query.subquery(Long.class);
			Root<ReferalMaster> r = referal.from(ReferalMaster.class);
			
			rmEff .select( cb.max(rm.get("effectiveDateStart")) );
			Predicate i1 = cb.equal(r.get("branchCode"), rm.get("branchCode"));
			Predicate i2 = cb.equal(r.get("referalId"), rm.get("referalId"));
			Predicate i3 = cb.lessThanOrEqualTo(rm.get("effectiveDateStart") , today);
			rmEff .where(i1,i2,i3);
			
			referal.select( r.get("referalName")) ;
			Predicate rm1 = cb.equal(r.get("branchCode"), lm.get("branchCode"));
			Predicate rm2 = cb.equal(r.get("referalId"), lm.get("referalId"));
			Predicate rm3   = cb.equal(r.get("effectiveDateStart"),rmEff);
			Predicate rm4  = cb.equal(r.get("status"),"Y");
			referal.where(rm1,rm2,rm3,rm4);
			
			// Select
			query.multiselect( lm.get("referalId").alias("referalId"), lm.get("companyId").alias("companyId") ,lm.get("branchCode").alias("branchCode"),
					referal.alias("referalName")  , lm.get("referalName").alias("oldReferalName") , 
					lm.get("sumInsuredStart").alias("sumInsuredStart")  , lm.get("sumInsuredEnd").alias("sumInsuredEnd")  ,
					 lm.get("status").alias("status") , lm.get("remarks").alias("remarks"));

			// Product Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginReferalMaster> ocpm1 = effectiveDate.from(LoginReferalMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("loginId"), lm.get("loginId"));
			Predicate a2 = cb.equal(ocpm1.get("referalId"), lm.get("referalId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), lm.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2,a3,a4); 
					
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(lm.get("entryDate")));
			
			//In 
			Expression<String>e0=lm.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(lm.get("loginId"), loginId );
			Predicate n2 = cb.equal(lm.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(branchIds);
			
			query.where(n1, n2, n3).orderBy(orderList);
		
			// Get Result
			TypedQuery<IssuerReferalCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
				
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}

	@Override
	public List<IssuerReferalCompanyGetRes> getIssuerCompanyReferal(IssuerCompanyReferalGetReq req) {
		List<IssuerReferalCompanyGetRes> referalList = new ArrayList<IssuerReferalCompanyGetRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			
			String loginId = req.getLoginId() ;
			List<String> branchIds = new ArrayList<String>() ;
			branchIds.add(req.getBranchCode());
			
			List<IssuerReferalCriteriaRes> loginReferals = getIssuerReferalDetails (loginId , branchIds , today ) ;
				
			for(IssuerReferalCriteriaRes data :  loginReferals) {
				IssuerReferalCompanyGetRes referalRes = new IssuerReferalCompanyGetRes();
				
				String pattern = "#####0.00";
				DecimalFormat df = new DecimalFormat(pattern);
				referalRes.setOldReferalName(data.getOldReferalName());
				referalRes.setSumInsuredStart(data.getSumInsuredStart()==null?"" : df.format(data.getSumInsuredStart()) );
				referalRes.setSumInsuredEnd(data.getSumInsuredEnd()==null?"" :df.format(data.getSumInsuredEnd()) );
				referalRes.setReferalId(data.getReferalId().toString());
				referalRes.setReferalName(data.getReferalName());
				referalRes.setStatus(data.getStatus());
				referalList.add(referalRes);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return referalList ;
	}
}

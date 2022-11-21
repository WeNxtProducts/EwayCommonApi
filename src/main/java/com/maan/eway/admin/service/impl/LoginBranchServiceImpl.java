package com.maan.eway.admin.service.impl;

import java.lang.reflect.Type;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.admin.req.AttachBrokerBranchReq;
import com.maan.eway.admin.req.AttachCompaniesReq;
import com.maan.eway.admin.req.AttachIssuerBrannchReq;
import com.maan.eway.admin.req.AttacheIssuerBranchReq;
import com.maan.eway.admin.req.AttachedBranchesReq;
import com.maan.eway.admin.req.BrokerBranchGetReq;
import com.maan.eway.admin.req.BrokerBranchesReq;
import com.maan.eway.admin.req.GetAllBrokerBranchReq;
import com.maan.eway.admin.req.GetBrokerBranchReq;
import com.maan.eway.admin.req.IssuerBranchGetReq;
import com.maan.eway.admin.res.BranchCriteriaRes;
import com.maan.eway.admin.res.BrokerBranchGetRes;
import com.maan.eway.admin.res.BrokerCompanyGetRes;
import com.maan.eway.admin.res.GetBrokerBranchRes;
import com.maan.eway.admin.res.IssuerBranchGetRes;
import com.maan.eway.admin.res.IssuerCompanyGetRes;
import com.maan.eway.admin.res.LoginCreationRes;
import com.maan.eway.admin.service.LoginBranchService;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginBranchMasterArch;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginMasterArch;
import com.maan.eway.bean.RegionMaster;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.LoginBranchMasterArchRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterArchRepository;
import com.maan.eway.repository.LoginMasterRepository;

@Service
public class LoginBranchServiceImpl implements LoginBranchService {

	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private LoginMasterArchRepository loginArchRepo;
	

	@Autowired
	private LoginBranchMasterRepository loginBrokerRepo;
	
	@Autowired
	private LoginBranchMasterArchRepository loginBrokerArchRepo;
	
	@Autowired
	private BranchMasterRepository branchRepo ;
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();


	private Logger log=LogManager.getLogger(LoginBranchServiceImpl.class);

//*************************************** Add Branch Methods **********************************************************//	

	@Override
	public LoginCreationRes attachBrokerBranches(AttachCompaniesReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Find Data 
			LoginMaster findLogin = loginRepo.findByLoginId(req.getLoginId());
			
			// Save in Arch tables
			String archId = "AI-" + idf.format(new Date());
			LoginMasterArch  loginArch = dozerMapper.map(findLogin, LoginMasterArch.class )  ;
			loginArch.setArchId(archId);
			loginArchRepo.saveAndFlush(loginArch);
			
			// Branch Setup
			String totalBranches  = "" ;
			String companies = "" ;
			List<String> branchIds = new ArrayList<String>();
			
			for(AttachedBranchesReq  data : req.getAttachedCompanies() ) {
				String branches  = "" ;
				for(BrokerBranchesReq data2 :  data.getAttachedBranches() ) {
					branches =  StringUtils.isBlank(branches) ?  data2.getBranchCode() : branches + "," + data2.getBranchCode();
					branchIds.add( data2.getBranchCode());
				}
				
				companies = StringUtils.isBlank(companies) ? data.getInsuranceId() : "," + data.getInsuranceId() ;
				totalBranches  = StringUtils.isBlank(branches) ? totalBranches :totalBranches + "," + branches ; 
			}
			List<BranchMaster> branchList = getBranchList(branchIds);
			List<String> rigionList = branchList.stream().map( o -> o.getRegionCode()  ).collect(Collectors.toList());
			
			// Remove Duplicate
			rigionList = rigionList.stream().distinct().collect(Collectors.toList());
			
			String regions   = rigionList==null   || rigionList.size()==0 ?"" : String.join(",",rigionList);
			
			// Update Login Master
			findLogin.setAttachedBranches(totalBranches);
			findLogin.setAttachedRegions(regions);
			findLogin.setAttachedCompanies(companies);
		
			loginRepo.saveAndFlush(findLogin);
			
			log.info( "Login Master Updated Details ---> " + json.toJson(findLogin) );
			
			res.setResponse("Updated Successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public LoginCreationRes attachIssuerBranches(AttachIssuerBrannchReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Find Data 
			LoginMaster findLogin = loginRepo.findByLoginId(req.getLoginId());
			
			// Save in Arch tables
			String archId = "AI-" + idf.format(new Date());
			LoginMasterArch  loginArch = dozerMapper.map(findLogin, LoginMasterArch.class )  ;
			loginArch.setArchId(archId);
			loginArchRepo.saveAndFlush(loginArch);
			
		/*	//Login Broker Branch Master
			LoginBrokerBranchMaster loginBroker =new LoginBrokerBranchMaster();
			loginBroker=loginBrokerRepo.findByLoginId(req.getLoginId());
			// Save in Arch tables
			String brokerArchId = "AI-" + idf.format(new Date());
			LoginBrokerBranchMasterArch loginBrokerArch = dozerMapper.map(loginBroker, LoginBrokerBranchMasterArch.class);
			loginBrokerArch.setArchId(brokerArchId);
			loginBrokerArchRepo.saveAndFlush(loginBrokerArch);*/
			
			// Branch Setup
			String totalBranches  = "" ;
			String companies = "" ;
			List<String> branchIds = new ArrayList<String>();
			
			for(AttacheIssuerBranchReq  data : req.getAttachedCompanies() ) {
				 
				String branches  = "" ;
				for(String data2 :  data.getAttachedBranches() ) {
					branches =  StringUtils.isBlank(branches) ?  data2 : branches + "," + data2;
					branchIds.add( data2);
				}
				companies = StringUtils.isBlank(companies) ? data.getInsuranceId() : "," + data.getInsuranceId() ;
				totalBranches  = StringUtils.isBlank(branches) ? totalBranches :totalBranches + "," + branches ; 
			}
			List<BranchMaster> branchList = getBranchList(branchIds);
			List<String> rigionList = branchList.stream().map( o -> o.getRegionCode()  ).collect(Collectors.toList());
			
			// Remove Duplicate
			rigionList = rigionList.stream().distinct().collect(Collectors.toList());
			
			String regions   = rigionList==null   || rigionList.size()==0 ?"" : String.join(",",rigionList);
			
			// Update Login Master
			findLogin.setAttachedBranches(totalBranches);
			findLogin.setAttachedRegions(regions);
			findLogin.setAttachedCompanies(companies);
			loginRepo.saveAndFlush(findLogin);
			
			log.info( "Login Master Updated Details ---> " + json.toJson(findLogin) );
			
			res.setResponse("Updated Successfully");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public List<BranchMaster> getBranchList(List<String> branchIds ) {
		List<BranchMaster> branchList = new ArrayList<BranchMaster>(); 
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);

			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

			// Branch Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2);
			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("entryDate")));
			
			//In 
			Expression<String>e0= b.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =   e0.in(branchIds) ;

			query.where(n1, n2, n3).orderBy(orderList);
			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);
			branchList = result.getResultList();
			
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return branchList  ; 
	}
	
	@Override
	public List<BrokerCompanyGetRes> getBrokerBranches(BrokerBranchGetReq req) {
		List<BrokerCompanyGetRes> companyList = new ArrayList<BrokerCompanyGetRes>();
		try {
			// Find Data 
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			
			List<String> branchIds = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(","))) ; 
			// Criteria Query
			List<BranchCriteriaRes> list = getCompanyAndBranchDetails(branchIds);
			
			Map<String , List<BranchCriteriaRes>> groupByCompany = list.stream().collect(Collectors.groupingBy(BranchCriteriaRes :: getCompanyId ) ) ; 
			
			for (String companyId : groupByCompany.keySet()  ) {
				BrokerCompanyGetRes  companyRes = new BrokerCompanyGetRes();
				
				List<BranchCriteriaRes>  getDatas = groupByCompany.get(companyId) ;
				List<BrokerBranchGetRes> attachedBranches = new ArrayList<BrokerBranchGetRes>();
				
				for (BranchCriteriaRes data :  getDatas  ) {
					BrokerBranchGetRes branchRes = new BrokerBranchGetRes();
					
					// Branch Res
					branchRes.setBranchCode(data.getBranchCode());
					branchRes.setBranchName(data.getBranchName());
					branchRes.setRegionCode(data.getRegionCode());
					branchRes.setRegionName(data.getRegionName());
					attachedBranches.add(branchRes);
				}
				
				// Company Res 
				companyRes.setInsuranceId(getDatas.get(0).getCompanyId());
				companyRes.setCompanyName(getDatas.get(0).getCompanyName());
				companyRes.setAttachedBranches(attachedBranches);		
				companyList.add(companyRes);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}
	
	@Override
	public List<IssuerCompanyGetRes> getIssuerBranches(IssuerBranchGetReq req) {
		List<IssuerCompanyGetRes> companyList = new ArrayList<IssuerCompanyGetRes>();
		try {
			// Find Data 
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			
			List<String> branchIds = new ArrayList<>(Arrays.asList(loginData.getAttachedBranches().split(","))) ; 
			// Criteria Query
			List<BranchCriteriaRes> list = getCompanyAndBranchDetails(branchIds);
			
			Map<String , List<BranchCriteriaRes>> groupByCompany = list.stream().collect(Collectors.groupingBy(BranchCriteriaRes :: getCompanyId ) ) ; 
			
			for (String companyId : groupByCompany.keySet()  ) {
				IssuerCompanyGetRes  companyRes = new IssuerCompanyGetRes();
				
				List<BranchCriteriaRes>  getDatas = groupByCompany.get(companyId) ;
				List<IssuerBranchGetRes> attachedBranches = new ArrayList<IssuerBranchGetRes>();
				
				for (BranchCriteriaRes data :  getDatas  ) {
					IssuerBranchGetRes branchRes = new IssuerBranchGetRes();
					
					// Branch Res
					branchRes.setBranchCode(data.getBranchCode());
					branchRes.setBranchName(data.getBranchName());
					branchRes.setRegionCode(data.getRegionCode());
					branchRes.setRegionName(data.getRegionName());
					attachedBranches.add(branchRes);
				}
				
				// Company Res 
				companyRes.setInsuranceId(getDatas.get(0).getCompanyId());
				companyRes.setCompanyName(getDatas.get(0).getCompanyName());
				companyRes.setAttachedBranches(attachedBranches);		
				companyList.add(companyRes);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}
	
	public List<BranchCriteriaRes> getCompanyAndBranchDetails(List<String> branchIds ) {
		List<BranchCriteriaRes> list = new ArrayList<BranchCriteriaRes>(); 
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchCriteriaRes> query = cb.createQuery(BranchCriteriaRes.class);

			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);
	
			// Select Region Name SubQuery for Effective Date Max Filter
			Subquery<Long> regEff = query.subquery(Long.class);
			Root<RegionMaster> r = regEff.from(RegionMaster.class);
			Subquery<Long> region = query.subquery(Long.class);
			Root<RegionMaster> rn = region.from(RegionMaster.class);
			
			regEff.select( cb.max(r.get("effectiveDateStart")) );
			Predicate e1 = cb.equal(rn.get("regionCode"), r.get("regionCode"));
			Predicate e2 = cb.lessThanOrEqualTo(r.get("effectiveDateStart") , today);
			regEff.where(e1,e2);
			
			region.select( rn.get("regionName")) ;
			Predicate r1 = cb.equal(rn.get("regionCode"), b.get("regionCode"));
			Predicate r2 = cb.equal(rn.get("effectiveDateStart"),regEff);
			region.where(r1,r2);
			
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
			Predicate ins1 = cb.equal(ins.get("companyId"), b.get("companyId"));
			Predicate ins2  = cb.equal(ins.get("effectiveDateStart"),insEff);
			company.where(ins1,ins2);
			
			// Select
			query.multiselect( b.get("branchCode").alias("branchCode") ,b.get("branchName").alias("branchName") ,
					           b.get("regionCode").alias("regionCode") , region.alias("regionName") ,
					           b.get("companyId").alias("companyId")   , company.alias("companyName")  );

			// Branch Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart") , today);
			effectiveDate.where(a1,a2);
					
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("entryDate")));
			
			//In 
			Expression<String>e0= b.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =   e0.in(branchIds) ;

			query.where(n1, n2, n3).orderBy(orderList);
		
			// Get Result
			TypedQuery<BranchCriteriaRes> result = em.createQuery(query);
			//System.out.println(result.unwrap(org.hibernate.query.Query.class).getQueryString() );
			list = result.getResultList();
			
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list  ; 
	}

	@Override
	public LoginCreationRes attachBrokerCompanyBranch(AttachBrokerBranchReq req) {
		LoginCreationRes res = new LoginCreationRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Login Data 
			LoginMaster loginData = loginRepo.findByLoginId(req.getLoginId());
			
			// Find Data 
			LoginBranchMaster findBranch = loginBrokerRepo.findByLoginIdAndBranchCodeAndCompanyId(req.getLoginId() , req.getBranchCode() , req.getCompanyId());
			
			LoginBranchMaster save = dozerMapper.map(req, LoginBranchMaster.class )  ;
			if(findBranch !=null  ) {
				//Delete Old Record
				loginBrokerRepo.delete(findBranch);
				// Save in Arch tables
				String archId = "AI-" + idf.format(new Date());
				LoginBranchMasterArch  loginArch = dozerMapper.map(findBranch, LoginBranchMasterArch.class )  ;
				loginArch.setArchId(archId);
				loginBrokerArchRepo.saveAndFlush(loginArch);
				
				save.setEntryDate(findBranch.getEntryDate() );
				save.setCreatedBy(findBranch.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			} else {
				save.setEntryDate(new Date());
				save.setCreatedBy(req.getCreatedBy());
				save.setUpdatedBy(req.getCreatedBy());
				save.setUpdatedDate(new Date());
			}
			
			save.setOaCode(Integer.valueOf(loginData.getOaCode()));
			save.setAgencyCode(Integer.valueOf(loginData.getAgencyCode()));	
			save.setAttachedBranch(StringUtils.isBlank(req.getAttachedBranch())? req.getBranchCode() : req.getAttachedBranch() );
			save.setAttachedCompany(StringUtils.isBlank(req.getAttachedCompany())? req.getCompanyId() : req.getAttachedCompany() );
			
			loginBrokerRepo.save(save);
			
			
			log.info( "Login Master Updated Details ---> " + json.toJson(save) );
			res.setResponse("Branch Added Successfully");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public GetBrokerBranchRes getBrokerCompanyBranch(GetBrokerBranchReq req) {
		GetBrokerBranchRes res = new GetBrokerBranchRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Find Data 
			LoginBranchMaster findBranch = loginBrokerRepo.findByLoginIdAndBranchCodeAndCompanyId(req.getLoginId() , req.getBranchCode() , req.getInsuranceId());
			res = dozerMapper.map(findBranch, GetBrokerBranchRes.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<GetBrokerBranchRes> getallBrokerCompanyBranch(GetAllBrokerBranchReq req) {
		List<GetBrokerBranchRes> resList = new ArrayList<GetBrokerBranchRes>();
		ModelMapper mapper = new  ModelMapper();
		SimpleDateFormat idf = new SimpleDateFormat("yyMMddhhssmmss"); 
		try {
			// Find Data 
			List<LoginBranchMaster> findBranches = loginBrokerRepo.findByLoginIdOrderByUpdatedDateDesc(req.getLoginId() );
			Type listType = new TypeToken<List<GetBrokerBranchRes>>(){}.getType();
			resList = mapper.map(findBranches ,listType);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	
}

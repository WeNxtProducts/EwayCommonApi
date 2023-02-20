package com.maan.eway.auth.service.impl;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.maan.eway.auth.dto.BrokerProductCompaniesRes;
import com.maan.eway.auth.dto.BrokerProductsGetRes;
import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ClaimLoginResponse;
import com.maan.eway.auth.dto.ClaimLogoutResponse;
import com.maan.eway.auth.dto.CommonLoginRes;
import com.maan.eway.auth.dto.LoginBranchCriteriaRes;
import com.maan.eway.auth.dto.LoginBranchDetailsRes;
import com.maan.eway.auth.dto.LoginProductCriteriaRes;
import com.maan.eway.auth.dto.LoginRequest;
import com.maan.eway.auth.dto.LogoutRequest;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.auth.service.AuthendicationService;
import com.maan.eway.auth.token.EncryDecryService;
import com.maan.eway.auth.token.JwtTokenUtil;
import com.maan.eway.auth.token.passwordEnc;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginMasterId;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SessionMaster;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SessionMasterRepository;


@Lazy
@Service
public class AuthendicationServiceImpl implements AuthendicationService, UserDetailsService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private LoginMasterRepository loginRepo;
	@Autowired
	private SessionMasterRepository sessionRep;
	@Autowired
	private EncryDecryService endecryService;
	@Autowired
	private BranchMasterRepository branchRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserRepo ;

	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@Autowired
	private LoginProductMasterRepository loginProductRepo;
	
	@Autowired
	private ProductMasterRepository productRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(AuthendicationServiceImpl.class);
	
	@Override
	public CommonLoginRes checkUserLogin(LoginRequest mslogin, HttpServletRequest http) {
		CommonLoginRes res = new CommonLoginRes();
		try {
			LoginMaster login = new LoginMaster();
			
			if(mslogin.getLoginId().equalsIgnoreCase("guest") ) {
				login =loginRepo.findByLoginId(mslogin.getLoginId());
			} else {
				passwordEnc passEnc = new passwordEnc();
				String epass = passEnc.crypt(mslogin.getPassword().trim());
				log.info("Encrpted password "+epass);
				 login =loginRepo.findByLoginIdAndPassword(mslogin.getLoginId(),epass);
					
			}
			
			if (login != null ) {
				http.getSession().removeAttribute(mslogin.getLoginId());
				String token = jwtTokenUtil.doGenerateToken(mslogin.getLoginId());
				log.info("-----token------" + token);
				SessionMaster session = new SessionMaster();
				session.setLoginId(mslogin.getLoginId());
				session.setTokenId(token);
				session.setStatus("ACTIVE");
				String temptoken = bCryptPasswordEncoder.encode("CommercialClaim");
				session.setTempTokenid(temptoken);
				session.setUserType(login.getUserType());
				session.setSubUserType(login.getSubUserType());
				Date today = new Date(); 
				session.setEntryDate(today);
				session.setStartTime(today);
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MINUTE, 20);
				Date endTime = cal.getTime();
				session.setEndTime(endTime );
				session =sessionRep.save(session);
				ClaimLoginResponse loginRes = new ClaimLoginResponse(); 
				
				if (login.getLoginId().equalsIgnoreCase("guest") ) {
					loginRes.setToken(session.getTempTokenid());
					loginRes.setLoginId(login.getLoginId());
					loginRes.setUserName("guest");
					loginRes.setUserMail("");
					loginRes.setUserMobile("");
					loginRes.setUserType(login.getUserType());
					loginRes.setSubUserType(login.getSubUserType());
					loginRes.setOaCode(login.getOaCode().toString());
					loginRes.setBankCode(login.getBankCode());
					
					
					
				} else {
					loginRes = setTokenResponse(session,login,mslogin);
				}
				
				
				//Response 
				res.setCommonResponse(loginRes);
				res.setIsError(false);
				res.setErrorMessage(Collections.emptyList());
				res.setMessage("Success");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private ClaimLoginResponse setTokenResponse(SessionMaster session, LoginMaster login, LoginRequest mslogin) {
		ClaimLoginResponse r = new ClaimLoginResponse();
		try {
			LoginUserInfo userInfo = loginUserRepo.findByLoginId(login.getLoginId());
			
			r.setToken(session.getTempTokenid());
			r.setLoginId(login.getLoginId());
			r.setUserName(userInfo.getUserName());
			r.setUserMail(StringUtils.isBlank(userInfo.getUserMail())?"":userInfo.getUserMail());
			r.setUserMobile(StringUtils.isBlank(userInfo.getUserMobile())?"":userInfo.getUserMobile());
			r.setUserType(login.getUserType());
			r.setSubUserType(login.getSubUserType());
			r.setOaCode(login.getOaCode().toString());
			r.setBankCode(login.getBankCode());
			
			// Branch Res	
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginIdAndStatus(login.getLoginId() , "Y");
		
			List<String> branchCode =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			List<String> attachedBranchCode = loginBranch.stream().map(LoginBranchMaster ::getAttachedBranch ).collect(Collectors.toList()) ;
			List<String> totalList = new ArrayList<>();
			totalList.addAll(branchCode);
			totalList.addAll(attachedBranchCode);
			List<String> companies =loginBranch.stream().map(LoginBranchMaster ::getCompanyId ).collect(Collectors.toList()) ;
			Set<String> removeDuplicateCompany = new HashSet<>(companies);
			Set<String> removeDuplicateBranch = new HashSet<>(totalList);
			List<LoginBranchCriteriaRes> loginCriteriaRes = getBranchDetails(removeDuplicateBranch);
			
			List<LoginBranchDetailsRes> loginBranchRes = new ArrayList<LoginBranchDetailsRes>();
			
			for ( LoginBranchMaster data :  loginBranch  ) {
				LoginBranchDetailsRes branchRes = new LoginBranchDetailsRes();
				
				List<LoginBranchCriteriaRes>  filterBranchCriteria = loginCriteriaRes.stream().filter( o ->  o.getBranchCode().equalsIgnoreCase(data.getBranchCode()) ).collect(Collectors.toList());
				branchRes.setBranchCode( data.getBranchCode() );
				branchRes.setBranchName(data.getBranchName());
				// Normal Branch
				if(filterBranchCriteria.size()>0 ) {
					LoginBranchCriteriaRes getBranch = filterBranchCriteria.get(0);
					branchRes.setBranchName(getBranch.getBranchName());
					branchRes.setBrokerBranchCode(data.getBrokerBranchCode());
					branchRes.setBrokerBranchName(data.getBrokerBranchName());
					branchRes.setRegionCode(getBranch.getRegionCode() );
				//	branchRes.setRegionName(getBranch.getRegionName() );
					branchRes.setInsuranceId(getBranch.getCompanyId() );
					branchRes.setCompanyName(getBranch.getCompanyName() );
			//		branchRes.setCompanyLogo(getBranch.getCompanyLogo() );
					branchRes.setCurrencyId(getBranch.getCurrencyId() );;
					branchRes.setSourceType(data.getSourceType());
					branchRes.setDepartmentCode(data.getDepartmentCode());
					branchRes.setCustomerCode(data.getCustomerCode());
				}
				
				// Attached Branch
				if(! data.getBranchCode().equalsIgnoreCase(data.getAttachedBranch())  ) {
					List<LoginBranchCriteriaRes>  filterAttachedBranch = loginCriteriaRes.stream().filter( o ->  o.getBranchCode().equalsIgnoreCase(data.getBranchCode()) ).collect(Collectors.toList());
					branchRes.setAttachedBranchCode(data.getAttachedBranch());
					if(filterAttachedBranch.size()>0 ) {
						LoginBranchCriteriaRes getAttachedBranch = filterAttachedBranch.get(0);
						branchRes.setAttachedBranchName(getAttachedBranch.getBranchName()  );
						branchRes.setAttachedRegionCode(getAttachedBranch.getRegionCode() );
				//		branchRes.setAttachedRegionName(getAttachedBranch.getRegionName() );
						branchRes.setAttachedCompanyId(getAttachedBranch.getCompanyId() );
						branchRes.setAttachedCompanyName(getAttachedBranch.getCompanyName() );
				//		branchRes.setAttachedCompanyLogo(getAttachedBranch.getCompanyLogo() );
						branchRes.setCurrencyId(getAttachedBranch.getCurrencyId() );
						branchRes.setSourceType(data.getSourceType());
						branchRes.setDepartmentCode(data.getDepartmentCode());
						branchRes.setCustomerCode(data.getCustomerCode());
					
					}
				}
				loginBranchRes.add(branchRes);
			}
			
			
			r.setLoginBranchDetails(loginBranchRes);

			List<String> companyIds = new ArrayList<>(removeDuplicateCompany);
			// Products

			List<LoginProductMaster> loginproduct = new ArrayList<LoginProductMaster>();//loginProductRepo.findByLoginId(login.getLoginId());
			{
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
				Predicate a15 = cb.equal(cm.get("companyId"),companyIds.get(0));
				Predicate a16 = cb.equal(cm.get("status"),"Y" );
				Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
				Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
				productIds.where(a15,a16,a17,a18);
				
				//In 
				Expression<String>e0=c.get("productId");
				
			    // Where	
				Predicate n1 = cb.equal(c.get("status"), "Y");
				Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), companyIds.get(0));
				Predicate n5 = cb.equal(c.get("loginId"), login.getLoginId());
				Predicate n6 = e0.in(productIds);
				query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
				
				// Get Result
				TypedQuery<LoginProductMaster> result = em.createQuery(query);			
				loginproduct =  result.getResultList(); 
			}
			
			Integer productId;
			List<ProductDropDownRes> resList = new ArrayList<ProductDropDownRes>();

			for (LoginProductMaster products : loginproduct) {
				productId = products.getProductId();

				List<ProductMaster> product = productRepo.findByProductIdOrderByEffectiveDateStartDesc(productId);
				ProductDropDownRes res = new ProductDropDownRes();
				res.setOldProductName(products.getProductName());
				res.setNewProductName(product.get(0).getProductName());
				res.setProductIconId(product.get(0).getProductIconId().toString());
				res.setProductIconName(product.get(0).getProductIconName());
				res.setProductId(productId.toString());
				res.setPackageYn(product.get(0).getPackageYn());
				resList.add(res);
			}

			r.setCompanyProducts(resList);
			

			// Menu Ids
		  if(login.getMenuIds()!=null && login.getMenuIds().indexOf(",")!=-1) {
			  String[] split = login.getMenuIds().split(",");
			  List<String> asList = Arrays.asList(split);
			//  r.setMenuList(getMenuList( asList));
		  }				
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return r;
		
	}
	
	

	public List<BrokerProductCompaniesRes> getBrokerProducts(String loginId , List<String> companyIds  ) {
		List<BrokerProductCompaniesRes> companyList = new ArrayList<BrokerProductCompaniesRes>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime() ;
			
			List<LoginProductCriteriaRes> loginProducts = null ;// loginProductsService.getBrokerProductDetails(loginId , companyIds , today ) ;
				
			// Grouping
			Map<String ,List<LoginProductCriteriaRes>> groupByCompany = loginProducts.stream().collect(Collectors.groupingBy(LoginProductCriteriaRes :: getCompanyId )) ;
			for (String company : groupByCompany.keySet()) { 
				BrokerProductCompaniesRes companyRes = new BrokerProductCompaniesRes();
				List<BrokerProductsGetRes> attachedProducts = new ArrayList<BrokerProductsGetRes>();
				
				List<LoginProductCriteriaRes> filterProduct = groupByCompany.get(company);
				
				for(LoginProductCriteriaRes data :  filterProduct) {
					BrokerProductsGetRes productRes = new BrokerProductsGetRes();
					
					if(StringUtils.isNotBlank(data.getStatus()) && data.getStatus().equalsIgnoreCase("Y")  ) {
						String pattern = "#####0.00";
						DecimalFormat df = new DecimalFormat(pattern);
						productRes.setOldProductName(data.getOldProductName() );
						productRes.setSumInsuredStart(data.getSumInsuredStart()==null?"" : df.format(data.getSumInsuredStart()) );
						productRes.setSumInsuredEnd(data.getSumInsuredEnd()==null?"" :df.format(data.getSumInsuredEnd()) );
						productRes.setStatus(data.getStatus());
						productRes.setRemarks(data.getRemarks());

						productRes.setProductId(data.getProductId().toString());
						productRes.setProductName(data.getProductName());
						attachedProducts.add(productRes);
					}
				}
				
				// Response 
				companyRes.setInsuranceId(filterProduct.get(0).getCompanyId() );
				companyRes.setCompanyName(filterProduct.get(0).getCompanyName() );
				companyRes.setAttachedProducts(attachedProducts);
				companyList.add(companyRes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return companyList;
	}
	
	private List<LoginBranchCriteriaRes> getBranchDetails(Set<String> removeDuplicateBranch) {
		List<LoginBranchCriteriaRes> list = new ArrayList<LoginBranchCriteriaRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			today = cal.getTime();

			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginBranchCriteriaRes> query = cb.createQuery(LoginBranchCriteriaRes.class);
			


			// Find All
			Root<BranchMaster> b = query.from(BranchMaster.class);

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
			Predicate ins1 = cb.equal(ins.get("companyId"), b.get("companyId"));
			Predicate ins2 = cb.equal(ins.get("effectiveDateStart"), effectiveDate2);
			company.where(ins1,ins2);
//			
//			// Company Logo Effective Date Max Filter
//			Subquery<Long> companyLogo = query.subquery(Long.class);
//			Root<InsuranceCompanyMaster> logo = companyLogo.from(InsuranceCompanyMaster.class);
//			Subquery<Long> effectiveDate5 = query.subquery(Long.class);
//			Root<InsuranceCompanyMaster> ocpm5 = effectiveDate5.from(InsuranceCompanyMaster.class);
//			effectiveDate5.select(cb.max(ocpm5.get("effectiveDateStart")));
//			Predicate iceff1 = cb.equal(ocpm5.get("companyId"), logo.get("companyId"));
//			Predicate iceff2 = cb.lessThanOrEqualTo(ocpm5.get("effectiveDateStart"), today);
//			effectiveDate5.where(iceff1,iceff2);
////			
//			// Company Logo
//			companyLogo.select(logo.get("companyLogo"));
//			Predicate in1 = cb.equal(logo.get("companyId"), b.get("companyId"));
//			Predicate in2 = cb.equal(logo.get("effectiveDateStart"), effectiveDate5);
//			companyLogo.where(in1,in2);
			
			// Company Currency Effective Date Max Filter
			Subquery<Long> currency = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> currencyId = currency.from(InsuranceCompanyMaster.class);
			Subquery<Long> effectiveDate6 = query.subquery(Long.class);
			Root<InsuranceCompanyMaster> ocpm6 = effectiveDate6.from(InsuranceCompanyMaster.class);
			effectiveDate6.select(cb.max(ocpm6.get("effectiveDateStart")));
			Predicate iceff3 = cb.equal(ocpm6.get("companyId"), currencyId.get("companyId"));
			Predicate iceff4 = cb.lessThanOrEqualTo(ocpm6.get("effectiveDateStart"), today);
			effectiveDate6.where(iceff3,iceff4);
			
			// Currency Id
			currency.select(currencyId.get("currencyId"));
			Predicate in3 = cb.equal(currencyId.get("companyId"), b.get("companyId"));
			Predicate in4 = cb.equal(currencyId.get("effectiveDateStart"), effectiveDate6);
			currency.where(in3,in4);
			
						
			
//			// Region Effective Date Filter
//			Subquery<Long> region = query.subquery(Long.class);
//			Root<RegionMaster> rm = region.from(RegionMaster.class);
//			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
//			Root<RegionMaster> ocpm3 = effectiveDate3.from(RegionMaster.class);
//			effectiveDate3.select(cb.max(ocpm3.get("effectiveDateStart")));
//			Predicate reff2 = cb.equal(ocpm3.get("regionCode"), rm.get("regionCode"));
//			Predicate reff3 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
//			effectiveDate3.where(reff2,reff3);
//			
//			//Region Name
//			region.select(rm.get("regionName"));
//			Predicate rm2 = cb.equal(rm.get("regionCode"),  b.get("regionCode") );
//			Predicate rm3 = cb.equal(rm.get("effectiveDateStart"), effectiveDate3);
//			region.where(rm2,rm3);
//			
			// Select
			query.multiselect(b.get("branchCode").alias("branchCode") , b.get("regionCode").alias("regionCode") ,
					b.get("companyId").alias("companyId") , b.get("branchName").alias("branchName") ,
					company.alias("companyName") ,
					//region.alias("regionName") , 
				//	companyLogo.alias("companyLogo") ,
					currency.alias("currencyId") );

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = effectiveDate.from(BranchMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate eff1 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate eff2 = cb.equal(ocpm1.get("regionCode"), b.get("regionCode"));
			Predicate eff3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate eff4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(eff1, eff2, eff3, eff4 );
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			//In 
			Expression<String>e0=b.get("branchCode");
			
			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = e0.in(removeDuplicateBranch) ;

			query.where(n1, n2, n3).orderBy(orderList);

			// Get Result
			TypedQuery<LoginBranchCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
		}
		return list;
		
	}
	
	@SuppressWarnings("static-access")
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LoginMaster userList = new LoginMaster ();
		try {
			log.info("loadUserByUsername==>" + username);
			
			String[] split = username.split(":");
			
			LoginMasterId id = new LoginMasterId();
			id.setLoginId(split[0]);
			
			LoginMaster  userListopt = loginRepo.findByLoginId(split[0]);
			 if(userListopt!=null) {
				 userList = userListopt;
			 }
			if (userList!=null) {
				//user = userList.get(0);
				String pass = bCryptPasswordEncoder.encode(endecryService.decrypt("zQYgCo7GMZeX1tBQyzAi8Q=="));
				userList.setPassword(pass);
				Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
				grantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
				log.info("loadUserByTokenEncrypt==>" + userList.getPassword());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new org.springframework.security.core.userdetails.User(userList.getLoginId(), userList.getPassword(),getAuthority());
	}
	
	private List<SimpleGrantedAuthority> getAuthority() {
		return Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	
	
	// Change Passowrd
	@Override
	public String LoginChangePassword(ChangePasswordReq req) {
		String res = new String();
		try {
		passwordEnc passEnc = new passwordEnc();
		String epass = passEnc.crypt(req.getOldpassword().trim());
		String newpass = passEnc.crypt(req.getNewPassword().trim());
		LoginMaster master = new LoginMaster();  
		log.info("EncryptPassword-->" + epass);
		LoginMaster model = loginRepo.findByLoginId(req.getLoginId());
		if(model !=null ) {
			master = model ;
			
			String pass1 = master.getPassword();
			String pass2 = master.getLpass1();
			String pass3 = master.getLpass2();
			String pass4 = master.getLpass4();
			String pass5 = master.getLpass5();
			
			master.setLpass1(pass1);
			master.setLpass2(pass2);
			master.setLpass3(pass3);
			master.setLpass4(pass4);
			master.setLpass5(pass5);
			master.setPassword(newpass);
			master.setPwdCount(master.getPwdCount()+1);
			
			Instant now = Instant.now();
			Instant after = now.plus(Duration.ofDays(45));
			Date dateAfter = Date.from(after);
			master.setLpassDate(dateAfter);
			LoginMaster table = loginRepo.save(master);
			
			if(table!=null) {
				res  = "Password Changed Successfully";
			}
			else {
				res  = "FAILED" ;
				
			}
		}
		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Error-->" + e.getMessage());
		}
		return res;

	}

	@Override
	public CommonLoginRes logout(LogoutRequest mslogin) {
		CommonLoginRes res = new CommonLoginRes();
		ClaimLogoutResponse r = new ClaimLogoutResponse();
		try {
		
			LoginMaster login = loginRepo.findByLoginId(mslogin.getUserId());
			if (login!=null) {

				SessionMaster session = sessionRep.findByTempTokenid(mslogin.getToken());
				session.setLogoutDate(new Date());
				session.setStatus("DE-ACTIVE");
				session = sessionRep.save(session);
				r.setStatus("Log Out Sucessfully");
			}else {
				r.setStatus("Log Out Failed");
			}
		} catch (Exception e) {
			r.setStatus("Log Out Failed");
			e.printStackTrace();
		}
		res.setCommonResponse(r);
		res.setErrorMessage(Collections.emptyList());
		res.setIsError(false);
		res.setMessage("Success");
		return res;
	}

	
}



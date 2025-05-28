package com.maan.eway.crm.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.auth.dto.ChangePasswordReq;
import com.maan.eway.auth.dto.ProductDropDownRes;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.SessionMaster;
import com.maan.eway.crm.bean.UserLoginResponseData;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.SessionMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class CrmServiceImpl implements CrmService {
	private Logger log = LogManager.getLogger(CrmServiceImpl.class);

	@PersistenceContext
	private EntityManager em;
	@Value(value = "${crm.changePassword}")
	private String changePassword;

	@Autowired
	private LoginBranchMasterRepository loginBranchMasterRepo;

	@Autowired
	private InsuranceCompanyMasterRepository companyRepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	@Autowired
	private SessionMasterRepository sessionRep;
	@Autowired
	private LoginMasterRepository loginRepo;

	
	@Value(value = "${crm.enquiry}")
	private String enquiry;

	@Autowired
	private EserviceCustomerDetailsRepository eserviceCustomerDetailsRepo;

	@Override
	public UserLoginResponseData validateTokenForCRM(String token) {
		UserLoginResponseData resp = new UserLoginResponseData();
		try {
			SessionMaster session = sessionRep.findByTempTokenid(token);

			if (session == null || "DE-ACTIVE".equalsIgnoreCase(session.getStatus())) {
				resp.setValidateToken(false);
				return resp;
			}

			String loginId = session.getLoginId();
			String userType = session.getUserType();
			String subUserType = session.getSubUserType();

			LoginMaster loginData = loginRepo.findByLoginId(loginId);
			if (loginData == null) {
				resp.setValidateToken(false);
				return resp;
			}

			String companyId = loginData.getCompanyId();

			resp.setValidateToken(true);
			resp.setUserType(userType);
			resp.setSubUserType(subUserType);
			resp.setCompanyId(companyId);
			resp.setLoginId(loginId);
			List<LoginBranchMaster> loginBranchData = loginBranchMasterRepo.findByLoginId(loginId);
			String branchCode = loginBranchData.stream().map(a -> a.getBranchCode()).collect(Collectors.joining(","));
			resp.setBranchCode(branchCode);
		} catch (Exception e) {
			e.printStackTrace();
			resp.setValidateToken(false);
		}
		return resp;
	}

	@Override
	public List<ProductDropDownRes> getProductDetailByLoginId(String loginId, String companyId) {
		List<LoginProductMaster> loginproduct = new ArrayList<LoginProductMaster>();// loginProductRepo.findByLoginId(login.getLoginId());
		{
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> c = query.from(LoginProductMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a4 = cb.equal(c.get("loginId"), ocpm1.get("loginId"));
			effectiveDate.where(a1, a2, a3, a4);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a5 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a6 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("loginId"), ocpm2.get("loginId"));
			effectiveDate2.where(a5, a6, a7, a8);

			// Filer Product IDs
			Subquery<Long> productIds = query.subquery(Long.class);
			Root<CompanyProductMaster> cm = productIds.from(CompanyProductMaster.class);

			Subquery<Long> effectiveDate3 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm4 = effectiveDate3.from(CompanyProductMaster.class);
			effectiveDate3.select(cb.max(ocpm4.get("effectiveDateStart")));
			Predicate a9 = cb.equal(cm.get("productId"), ocpm4.get("productId"));
			Predicate a10 = cb.equal(cm.get("companyId"), ocpm4.get("companyId"));
			Predicate a11 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateStart"), today);
			effectiveDate3.where(a9, a10, a11);

			Subquery<Long> effectiveDate4 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm5 = effectiveDate4.from(CompanyProductMaster.class);
			effectiveDate4.select(cb.max(ocpm5.get("effectiveDateEnd")));
			Predicate a12 = cb.equal(cm.get("productId"), ocpm5.get("productId"));
			Predicate a13 = cb.equal(cm.get("companyId"), ocpm5.get("companyId"));
			Predicate a14 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			effectiveDate4.where(a12, a13, a14);

			productIds.select(cm.get("productId"));
			Predicate a15 = cb.equal(cm.get("companyId"), companyId);
			Predicate a16 = cb.equal(cm.get("status"), "Y");
			Predicate a17 = cb.equal(cm.get("effectiveDateStart"), effectiveDate3);
			Predicate a18 = cb.equal(cm.get("effectiveDateEnd"), effectiveDate4);
			productIds.where(a15, a16, a17, a18);

			// In
			Expression<String> e0 = c.get("productId");

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n5 = cb.equal(c.get("loginId"), loginId);
			Predicate n6 = e0.in(productIds);
			query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			loginproduct = result.getResultList();
		}

		Integer productId;
		List<ProductDropDownRes> resList = new ArrayList<ProductDropDownRes>();

		for (LoginProductMaster products : loginproduct) {
			productId = products.getProductId();

			List<CompanyProductMaster> product = getCompanyProductMaster(products.getCompanyId(), productId);
			ProductDropDownRes res = new ProductDropDownRes();
			res.setOldProductName(products.getProductName());
			res.setNewProductName(product.get(0).getProductName());
			res.setProductIconId(product.get(0).getProductIconId().toString());
			res.setProductIconName(product.get(0).getProductIconName());
			res.setProductId(productId.toString());
			res.setPackageYn(product.get(0).getPackageYn());
			res.setDisplayOrder(product.get(0).getDisplayOrder() == null ? 999 : product.get(0).getDisplayOrder());
			resList.add(res);
		}
		resList.sort(Comparator.comparing(ProductDropDownRes::getDisplayOrder));
		return resList;
	}

	public List<CompanyProductMaster> getCompanyProductMaster(String companyId, Integer productId) {
		List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);

			// Find All
			Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productName")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
			Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a4, a5, a6);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), companyId);
			Predicate n7 = cb.equal(c.get("productId"), productId);
			Predicate n5 = cb.equal(c.get("status"), "R");
			Predicate n6 = cb.or(n1, n5);
			query.where(n6, n2, n3, n4, n7).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return list;
	}
	@Override
	public void updatePassword(ChangePasswordReq req,String url) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// headers.set("X-AUTH-TOKEN", actualToken);
		headers.set("Authorization", "");
		HttpEntity<ChangePasswordReq> requestEntity = new HttpEntity<>(req, headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity,
				String.class);
		System.out.println("Response: " + response.getBody());
	}
	
	@Override
	public String getEnqiryDetail(Long enquiryId, String token) {
		String rcmApi = enquiry + "/" + enquiryId;

		RestTemplate restTemplate = new RestTemplate();

		// Clean up token
		String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
		actualToken = actualToken.split(",")[0];

		// Set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("X-AUTH-TOKEN", actualToken);
		headers.set("Authorization", "Bearer " + actualToken);

		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(rcmApi, HttpMethod.GET, requestEntity, String.class);

		return response.getBody();
	}

	@Override
	public List<EserviceCustomerDetails> getCustomerDetailByLeadseqNo(Long leadSeqNo,String companyId, String token) {
		return  eserviceCustomerDetailsRepo
				.findByCompanyIdAndLeadSeqNo(companyId, leadSeqNo);
	}

}

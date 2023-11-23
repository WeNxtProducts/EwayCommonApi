package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Tuple;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.auth.dto.BrokerProductCompaniesRes;
import com.maan.eway.auth.dto.BrokerProductsGetRes;
import com.maan.eway.auth.dto.LoginProductCriteriaRes;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.TiraTrackingDetails;
import com.maan.eway.common.req.AdminTiraIntegrationGridReq;
import com.maan.eway.common.res.AdminTiraIntegrationGirdRes;
import com.maan.eway.common.res.TiraRes;
import com.maan.eway.common.service.AdminTiraIntegrationService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.TiraTrackingDetailsRepository;
import com.maan.eway.thread.MyTaskList;

@Service
@Transactional
public class AdminTiraInterationGridServiceImpl implements AdminTiraIntegrationService {

	@Autowired
	private TiraTrackingDetailsRepository tiraRepo;
	

	@Autowired
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private EserviceCustomerDetailsRepository custRepo;

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	
	@Autowired
	private EserviceTravelDetailsRepository travelRepo;

	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@PersistenceContext
	private EntityManager em;
	
	

	private Logger log = LogManager.getLogger(AdminTiraInterationGridServiceImpl.class);

	@Override
	public List<AdminTiraIntegrationGirdRes>  getallTiraSuccess(AdminTiraIntegrationGridReq req) {
		List<AdminTiraIntegrationGirdRes> reslist = new ArrayList<AdminTiraIntegrationGirdRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date entryDate1 = sdf.parse(req.getPushStartDate());
			Date entryDate2 = sdf.parse(req.getPushEndDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
			
			List<Tuple> list = new ArrayList<Tuple>();
			// Select
			query.multiselect(
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),m.get("noOfVehicles").alias("noOfVehicles"),
					m.get("agencyCode").alias("agencyCode"),m.get("branchCode").alias("branchCode"),
					m.get("productId").alias("productId"),m.get("companyId").alias("companyId"),
					m.get("loginId").alias("loginId"),m.get("applicationId").alias("applicationId"),
					m.get("customerName").alias("customerName"),m.get("sectionId").alias("sectionId"),
					m.get("overallPremiumLc").alias("overallPremiumLc"),m.get("overallPremiumFc").alias("overallPremiumFc"),
					m.get("status").alias("status"),m.get("policyNo").alias("policyNo"),
					m.get("debitNoteNo").alias("debitNoteNo"),m.get("creditNo").alias("creditNo"),
					m.get("tiraCoverNoteNo").alias("tiraCoverNoteNo"),m.get("tiraRequestId").alias("tiraRequestId"),
					m.get("tiraResponseId").alias("tiraResponseId"),m.get("stickerNumber").alias("stickerNumber"),
					m.get("responseStatusCode").alias("responseStatusCode"),m.get("responseStatusDesc").alias("responseStatusDesc"),
					m.get("bdmCode").alias("bdmCode"),m.get("branchName").alias("branchName"),
					m.get("productName").alias("productName"),m.get("brokerCode").alias("brokerCode"),
					m.get("customerCode").alias("customerCode"),m.get("customerId").alias("customerId"),
					m.get("sourceType").alias("sourceType"),m.get("userType").alias("userType"));
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerId"),  m.get("customerId"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "P");
			Predicate n5 = cb.equal(m.get("responseStatusCode"), "TIRA001");
			Predicate n6 = cb.equal(m.get("responseStatusCode"), "TIRA214");
			Predicate n7 = cb.or(n5,n6);
		
			Predicate n8=cb.between(m.get("entryDate"), startDate, endDate);
			query.where(n1,n2,n3,n4,n7,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			
			
			for(Tuple data:list) {
				AdminTiraIntegrationGirdRes	res= new AdminTiraIntegrationGirdRes();
				
				res.setAgencyCode(data.get("agencyCode")==null?null:data.get("agencyCode").toString());				res.setApplicationId(null);
				res.setBdmCode(data.get("bdmCode")==null?null:data.get("bdmCode").toString());
				res.setBranchCode(data.get("branchCode")==null?null:data.get("branchCode").toString());
				res.setBranchName(data.get("branchName")==null?null:data.get("branchName").toString());
				res.setBrokerCode(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
				res.setCompanyId(data.get("companyId")==null?null:data.get("companyId").toString());
				res.setCreditNo(data.get("creditNo")==null?null:data.get("creditNo").toString());
				res.setCustomerCode(data.get("customerCode")==null?null:data.get("customerCode").toString());
				res.setCustomerId(data.get("customerId")==null?null:data.get("customerId").toString());
				res.setDebitNoteNo(data.get("debitNoteNo")==null?null:data.get("debitNoteNo").toString());
				res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
				res.setNoOfVehicles(data.get("noOfVehicles")==null?null:data.get("noOfVehicles").toString());
				res.setOverAllPremiumFc(data.get("overallPremiumFc")==null?null:data.get("overallPremiumFc").toString());
				res.setOverAllPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
				res.setProductId(data.get("productId")==null?null:data.get("productId").toString());
				res.setProductName(data.get("productName")==null?null:data.get("productName").toString());
				res.setQuoteNo(data.get("quoteNo")==null?null:data.get("quoteNo").toString());
				res.setRequestReferenceNo(data.get("requestReferenceNo")==null?null:data.get("requestReferenceNo").toString());
				res.setResponseStatusCode(data.get("responseStatusCode")==null?null:data.get("responseStatusCode").toString());
				res.setResponseStatusDesc(data.get("responseStatusDesc")==null?null:data.get("responseStatusDesc").toString());
				res.setSourceType(data.get("sourceType")==null?null:data.get("sourceType").toString());
				res.setStatus(data.get("status")==null?null:data.get("status").toString());
				res.setStickerNumber(data.get("stickerNumber")==null?null:data.get("stickerNumber").toString());
				res.setTiraCoverNoteNo(data.get("tiraCoverNoteNo")==null?null:data.get("tiraCoverNoteNo").toString());
				res.setTiraRequestId(data.get("tiraRequestId")==null?null:data.get("tiraRequestId").toString());
				res.setTiraResponseId(data.get("tiraResponseId")==null?null:data.get("tiraResponseId").toString());
				res.setUserType(data.get("userType")==null?null:data.get("userType").toString());
//				res.setInceptionDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
//				res.setExpiryDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				reslist.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	
	

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<AdminTiraIntegrationGirdRes> getallTiraFailure(AdminTiraIntegrationGridReq req) {
		List<AdminTiraIntegrationGirdRes> reslist = new ArrayList<AdminTiraIntegrationGirdRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date entryDate1 = sdf.parse(req.getPushStartDate());
			Date entryDate2 = sdf.parse(req.getPushEndDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
			
			List<Tuple> list = new ArrayList<Tuple>();
			// Select
			query.multiselect(
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),m.get("noOfVehicles").alias("noOfVehicles"),
					m.get("agencyCode").alias("agencyCode"),m.get("branchCode").alias("branchCode"),
					m.get("productId").alias("productId"),m.get("companyId").alias("companyId"),
					m.get("loginId").alias("loginId"),m.get("applicationId").alias("applicationId"),
					m.get("customerName").alias("customerName"),m.get("sectionId").alias("sectionId"),
					m.get("overallPremiumLc").alias("overallPremiumLc"),m.get("overallPremiumFc").alias("overallPremiumFc"),
					m.get("status").alias("status"),m.get("policyNo").alias("policyNo"),
					m.get("debitNoteNo").alias("debitNoteNo"),m.get("creditNo").alias("creditNo"),
					m.get("tiraCoverNoteNo").alias("tiraCoverNoteNo"),m.get("tiraRequestId").alias("tiraRequestId"),
					m.get("tiraResponseId").alias("tiraResponseId"),m.get("stickerNumber").alias("stickerNumber"),
					m.get("responseStatusCode").alias("responseStatusCode"),m.get("responseStatusDesc").alias("responseStatusDesc"),
					m.get("bdmCode").alias("bdmCode"),m.get("branchName").alias("branchName"),
					m.get("productName").alias("productName"),m.get("brokerCode").alias("brokerCode"),
					m.get("customerCode").alias("customerCode"),m.get("customerId").alias("customerId"),
					m.get("sourceType").alias("sourceType"),m.get("userType").alias("userType"));
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerId"),  m.get("customerId"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "P");
			Predicate n5 = cb.notEqual(m.get("responseStatusCode"), "TIRA001");
			Predicate n6 = cb.notEqual(m.get("responseStatusCode"), "TIRA214");
			Predicate n7 = cb.and(n5,n6);
			Predicate n8 = cb.isNotNull(m.get("responseStatusCode"));
			Predicate n9 = cb.isNotNull(m.get("tiraResponseId"));
			Predicate n10=cb.between(m.get("entryDate"), startDate, endDate);
			query.where(n1,n2,n3,n4,n7,n8,n9,n10).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			
			
			for(Tuple data:list) {
				AdminTiraIntegrationGirdRes	res= new AdminTiraIntegrationGirdRes();
				
				res.setAgencyCode(data.get("agencyCode")==null?null:data.get("agencyCode").toString());				res.setApplicationId(null);
				res.setBdmCode(data.get("bdmCode")==null?null:data.get("bdmCode").toString());
				res.setBranchCode(data.get("branchCode")==null?null:data.get("branchCode").toString());
				res.setBranchName(data.get("branchName")==null?null:data.get("branchName").toString());
				res.setBrokerCode(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
				res.setCompanyId(data.get("companyId")==null?null:data.get("companyId").toString());
				res.setCreditNo(data.get("creditNo")==null?null:data.get("creditNo").toString());
				res.setCustomerCode(data.get("customerCode")==null?null:data.get("customerCode").toString());
				res.setCustomerId(data.get("customerId")==null?null:data.get("customerId").toString());
				res.setDebitNoteNo(data.get("debitNoteNo")==null?null:data.get("debitNoteNo").toString());
				res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
				res.setNoOfVehicles(data.get("noOfVehicles")==null?null:data.get("noOfVehicles").toString());
				res.setOverAllPremiumFc(data.get("overallPremiumFc")==null?null:data.get("overallPremiumFc").toString());
				res.setOverAllPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
				res.setProductId(data.get("productId")==null?null:data.get("productId").toString());
				res.setProductName(data.get("productName")==null?null:data.get("productName").toString());
				res.setQuoteNo(data.get("quoteNo")==null?null:data.get("quoteNo").toString());
				res.setRequestReferenceNo(data.get("requestReferenceNo")==null?null:data.get("requestReferenceNo").toString());
				res.setResponseStatusCode(data.get("responseStatusCode")==null?null:data.get("responseStatusCode").toString());
				res.setResponseStatusDesc(data.get("responseStatusDesc")==null?null:data.get("responseStatusDesc").toString());
				res.setSourceType(data.get("sourceType")==null?null:data.get("sourceType").toString());
				res.setStatus(data.get("status")==null?null:data.get("status").toString());
				res.setStickerNumber(data.get("stickerNumber")==null?null:data.get("stickerNumber").toString());
				res.setTiraCoverNoteNo(data.get("tiraCoverNoteNo")==null?null:data.get("tiraCoverNoteNo").toString());
				res.setTiraRequestId(data.get("tiraRequestId")==null?null:data.get("tiraRequestId").toString());
				res.setTiraResponseId(data.get("tiraResponseId")==null?null:data.get("tiraResponseId").toString());
				res.setUserType(data.get("userType")==null?null:data.get("userType").toString());
//				res.setInceptionDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
//				res.setExpiryDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				reslist.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	@Override
	public List<AdminTiraIntegrationGirdRes> getallTiraPending(AdminTiraIntegrationGridReq req) {
		List<AdminTiraIntegrationGirdRes> reslist = new ArrayList<AdminTiraIntegrationGirdRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date entryDate1 = sdf.parse(req.getPushStartDate());
			Date entryDate2 = sdf.parse(req.getPushEndDate());
			Calendar cal = new GregorianCalendar();
			cal.setTime(entryDate1);
			cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			cal.setTime(entryDate2);
			cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
			Date endDate = cal.getTime() ;
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
			
			List<Tuple> list = new ArrayList<Tuple>();
			// Select
			query.multiselect(
					// Customer Info
				    c.get("customerReferenceNo").alias("customerReferenceNo"),
				    c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),m.get("noOfVehicles").alias("noOfVehicles"),
					m.get("agencyCode").alias("agencyCode"),m.get("branchCode").alias("branchCode"),
					m.get("productId").alias("productId"),m.get("companyId").alias("companyId"),
					m.get("loginId").alias("loginId"),m.get("applicationId").alias("applicationId"),
					m.get("customerName").alias("customerName"),m.get("sectionId").alias("sectionId"),
					m.get("overallPremiumLc").alias("overallPremiumLc"),m.get("overallPremiumFc").alias("overallPremiumFc"),
					m.get("status").alias("status"),m.get("policyNo").alias("policyNo"),
					m.get("debitNoteNo").alias("debitNoteNo"),m.get("creditNo").alias("creditNo"),
					m.get("tiraCoverNoteNo").alias("tiraCoverNoteNo"),m.get("tiraRequestId").alias("tiraRequestId"),
					m.get("tiraResponseId").alias("tiraResponseId"),m.get("stickerNumber").alias("stickerNumber"),
					m.get("responseStatusCode").alias("responseStatusCode"),m.get("responseStatusDesc").alias("responseStatusDesc"),
					m.get("bdmCode").alias("bdmCode"),m.get("branchName").alias("branchName"),
					m.get("productName").alias("productName"),m.get("brokerCode").alias("brokerCode"),
					m.get("customerCode").alias("customerCode"),m.get("customerId").alias("customerId"),
					m.get("sourceType").alias("sourceType"),m.get("userType").alias("userType"));
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerId"),  m.get("customerId"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "P");
//			Predicate n5 = cb.notEqual(m.get("responseStatusCode"), "TIRA001");
//			Predicate n6 = cb.notEqual(m.get("responseStatusCode"), "TIRA214");
//			Predicate n7 = cb.and(n5,n6);
			Predicate n8 = cb.isNull(m.get("responseStatusCode"));
			Predicate n9 = cb.isNull(m.get("tiraResponseId"));
			Predicate n10 = cb.or(n8,n9);
			Predicate n11=cb.between(m.get("entryDate"), startDate, endDate);
			query.where(n1,n2,n3,n4,n10,n11).orderBy(orderList);
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			
			
			for(Tuple data:list) {
				AdminTiraIntegrationGirdRes	res= new AdminTiraIntegrationGirdRes();
				
				res.setAgencyCode(data.get("agencyCode")==null?null:data.get("agencyCode").toString());				res.setApplicationId(null);
				res.setBdmCode(data.get("bdmCode")==null?null:data.get("bdmCode").toString());
				res.setBranchCode(data.get("branchCode")==null?null:data.get("branchCode").toString());
				res.setBranchName(data.get("branchName")==null?null:data.get("branchName").toString());
				res.setBrokerCode(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				res.setClientName(data.get("clientName")==null?null:data.get("clientName").toString());
				res.setCompanyId(data.get("companyId")==null?null:data.get("companyId").toString());
				res.setCreditNo(data.get("creditNo")==null?null:data.get("creditNo").toString());
				res.setCustomerCode(data.get("customerCode")==null?null:data.get("customerCode").toString());
				res.setCustomerId(data.get("customerId")==null?null:data.get("customerId").toString());
				res.setDebitNoteNo(data.get("debitNoteNo")==null?null:data.get("debitNoteNo").toString());
				res.setLoginId(data.get("loginId")==null?null:data.get("loginId").toString());
				res.setNoOfVehicles(data.get("noOfVehicles")==null?null:data.get("noOfVehicles").toString());
				res.setOverAllPremiumFc(data.get("overallPremiumFc")==null?null:data.get("overallPremiumFc").toString());
				res.setOverAllPremiumLc(data.get("overallPremiumLc")==null?null:data.get("overallPremiumLc").toString());
				res.setProductId(data.get("productId")==null?null:data.get("productId").toString());
				res.setProductName(data.get("productName")==null?null:data.get("productName").toString());
				res.setQuoteNo(data.get("quoteNo")==null?null:data.get("quoteNo").toString());
				res.setRequestReferenceNo(data.get("requestReferenceNo")==null?null:data.get("requestReferenceNo").toString());
				res.setResponseStatusCode(data.get("responseStatusCode")==null?null:data.get("responseStatusCode").toString());
				res.setResponseStatusDesc(data.get("responseStatusDesc")==null?null:data.get("responseStatusDesc").toString());
				res.setSourceType(data.get("sourceType")==null?null:data.get("sourceType").toString());
				res.setStatus(data.get("status")==null?null:data.get("status").toString());
				res.setStickerNumber(data.get("stickerNumber")==null?null:data.get("stickerNumber").toString());
				res.setTiraCoverNoteNo(data.get("tiraCoverNoteNo")==null?null:data.get("tiraCoverNoteNo").toString());
				res.setTiraRequestId(data.get("tiraRequestId")==null?null:data.get("tiraRequestId").toString());
				res.setTiraResponseId(data.get("tiraResponseId")==null?null:data.get("tiraResponseId").toString());
				res.setUserType(data.get("userType")==null?null:data.get("userType").toString());
//				res.setInceptionDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
//				res.setExpiryDate(data.get("brokerCode")==null?null:data.get("brokerCode").toString());
				reslist.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}




	@Override
	public List<TiraRes> getallTiraDetails(AdminTiraIntegrationGridReq req) {
		List<TiraRes> resList=new ArrayList<TiraRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<TiraTrackingDetails> tiraList=tiraRepo.findByPolicyNoOrderByRequestIdAsc(req.getQuoteNo());
			for(TiraTrackingDetails data:tiraList) {
				TiraRes res=new TiraRes();
				res=dozerMapper.map(data, TiraRes.class);
				res.setEntryDate(data.getEntryDate()==null?null:data.getEntryDate());
				resList.add(res);
				}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}
}

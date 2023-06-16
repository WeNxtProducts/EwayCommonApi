package com.maan.eway.common.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CityMaster;
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

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.CriteriaCustomerRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class GridServiceImpl implements GridService {

	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private EserviceCustomerDetailsRepository custRepo;

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	@Autowired
	private MotorGridService motService;

	@Autowired
	private TravelGridService traService;

	@Autowired
	private BuildingGridService buiService;

	@Autowired
	private CommonGridService commonService;

	@Autowired
	private EserviceTravelDetailsRepository travelRepo;

	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(GridServiceImpl.class);

	@Override
	public List<EserviceCustomerDetailsRes> getallExistingQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();

			if (req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				extingQuoteList = motService.getMotorExistingQuoteDetails(req, branches, before30, today, limit,
						offset);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				extingQuoteList = traService.getTravelExistingQuoteDetails(req, branches, before30, today, limit,
						offset);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				extingQuoteList = buiService.getBuildingExistingQuoteDetails(req, branches, before30, today, limit,
						offset);
				// Common
			} else { // (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				extingQuoteList = commonService.getCommonExistingQuoteDetails(req, branches, before30, today, limit,
						offset);
			}

			for (QuoteCriteriaRes data : extingQuoteList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
		CompanyProductMaster product = new CompanyProductMaster();
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
			List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
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
			Predicate n5 = cb.equal(c.get("productId"), productId);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);
			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			product = list.size() > 0 ? list.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return product;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallLapsedQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			List<QuoteCriteriaRes> lapsedQuoteList = new ArrayList<QuoteCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				lapsedQuoteList = motService.getMotorLapsedQuoteDetails(req, branches, before30, limit, offset);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				lapsedQuoteList = traService.getTravelLapsedQuoteDetails(req, branches, before30, limit, offset);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				lapsedQuoteList = buiService.getBuildingLapsedQuoteDetails(req, branches, before30, limit, offset);
			} else {
				lapsedQuoteList = commonService.getCommonLapsedQuoteDetails(req, branches, before30, limit, offset);
			}

			for (QuoteCriteriaRes data : lapsedQuoteList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallRejectedQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<RejectCriteriaRes> rejectedQuoteList = new ArrayList<RejectCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				rejectedQuoteList = motService.getMotorRejectedQuoteDetails(req, branches, limit, offset);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				rejectedQuoteList = traService.getTravelRejectedQuoteDetails(req, branches, limit, offset);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				rejectedQuoteList = buiService.getBuildingRejectedQuoteDetails(req, branches, limit, offset);
			} else {
				rejectedQuoteList = commonService.getCommonRejectedQuoteDetails(req, branches, limit, offset);
			}
			for (RejectCriteriaRes data : rejectedQuoteList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	// Referral Grids

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralPendingDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> referralPendingList2 = motService.getMotorReferalDetails(req, branches,
						limit, offset, "RP");
				for (MotorGridCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				referralPendingList = traService.getTravelReferalDetails(req, branches, limit, offset, "RP");

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				referralPendingList = buiService.getBuildingReferalDetails(req, branches, limit, offset, "RP");
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req,
						branches, limit, offset, "RP");
				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}

			for (ReferalGridCriteriaRes data : referralPendingList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralApprovedDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralApprovedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorReferalDetails(req, branches, limit, offset,
						"RA");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				referralApprovedList = traService.getTravelReferalDetails(req, branches, limit, offset, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				referralApprovedList = buiService.getBuildingReferalDetails(req, branches, limit, offset, "RA");
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req,
						branches, limit, offset, "RA");
				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : referralApprovedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralRejectedDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorReferalDetails(req, branches, limit, offset,
						"RR");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				referralRejectedList = traService.getTravelReferalDetails(req, branches, limit, offset, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				referralRejectedList = buiService.getBuildingReferalDetails(req, branches, limit, offset, "RR");
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req,
						branches, limit, offset, "RR");
				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : referralRejectedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralPendings(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> adminReferralPendingList2 = motService.getMotorAdminReferalDetails(req,
						branches, limit, offset, "RP");
				for (MotorGridCriteriaRes data : adminReferralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				adminReferralPendingList = traService.getTravelAdminReferalDetails(req, branches, limit, offset, "RP");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				adminReferralPendingList = buiService.getBuildingAdminReferalDetails(req, branches, limit, offset,
						"RP");
			} else {
				List<ReferalCommonCriteriaRes> adminReferralPendingList2 = commonService
						.getCommonAdminReferalDetails(req, branches, limit, offset, "RP");
				for (ReferalCommonCriteriaRes data : adminReferralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : adminReferralPendingList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralApproved(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralApprovedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorAdminReferalDetails(req, branches, limit, offset,
						"RA");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				adminReferralApprovedList = traService.getTravelAdminReferalDetails(req, branches, limit, offset, "RA");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				adminReferralApprovedList = buiService.getBuildingAdminReferalDetails(req, branches, limit, offset,
						"RA");
			} else {
				List<ReferalCommonCriteriaRes> adminReferralApprovedList2 = commonService
						.getCommonAdminReferalDetails(req, branches, limit, offset, "RA");
				for (ReferalCommonCriteriaRes data : adminReferralApprovedList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : adminReferralApprovedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralRejected(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorAdminReferalDetails(req, branches, limit, offset,
						"RR");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				adminReferralRejectedList = traService.getTravelAdminReferalDetails(req, branches, limit, offset, "RR");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				adminReferralRejectedList = buiService.getBuildingAdminReferalDetails(req, branches, limit, offset,
						"RR");
			} else {
				List<ReferalCommonCriteriaRes> adminReferralRejectedList2 = commonService
						.getCommonAdminReferalDetails(req, branches, limit, offset, "RR");
				for (ReferalCommonCriteriaRes data : adminReferralRejectedList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Transactional
	@Override
	public CopyQuoteSuccessRes copyQuote(CopyQuoteReq req) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		try {
			String appId = "";
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<EserviceMotorDetails> motorList = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
				appId = motorList.get(0).getApplicationId();
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				List<EserviceBuildingDetails> buildingList = buildingRepo
						.findByRequestReferenceNo(req.getRequestReferenceNo());
				appId = buildingList.get(0).getApplicationId();
			}
			String loginId = "";
			List<String> branches = new ArrayList<String>();
			if (appId.equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}

			branches.add(req.getBranchCode());

			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				// Product Wise Get
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = motService.motorEndt(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("H")
						/*&& req.getProductId().equalsIgnoreCase(travelProductId)*/) {
					res = traService.travelEndt(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					res = buiService.buildingEndt(req, branches, loginId);
				} else {
					res = commonService.commonEndt(req, branches, loginId);

				}
			} else if (StringUtils.isBlank(req.getTypeId()) || req.getTypeId().equalsIgnoreCase("Normal")) {
				// Product Wise Get
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					res = motService.motorCopyQuote(req, branches, loginId);

				} else if (product.getMotorYn().equalsIgnoreCase("H")
						/*&& req.getProductId().equalsIgnoreCase(travelProductId)*/) {
					res = traService.travelCopyQuote(req, branches, loginId);
				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
					res = buiService.buildingCopyQuote(req, branches, loginId);

				} else {
					res = commonService.commonCopyQuote(req, branches);

				}
				res.setRequestReferenceNo(res.getRequestReferenceNo());
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	// Validation
	@Override
	public List<Error> validateQuotoNo(CopyQuoteReq req) {
		List<Error> error = new ArrayList<Error>();

		try {

			List<Tuple> quoteExist = null;
			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				if (StringUtils.isBlank(req.getQuoteNo())) {
					error.add(new Error("01", "QuoteNo", "Please Enter QuoteNo "));
				} else if (StringUtils.isBlank(req.getEndtTypeId())) {
					error.add(new Error("01", "EndtTypeId", "Please Select EndtTypeId "));
				}
				CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
						req.getProductId().toString());

				if (product.getMotorYn().equalsIgnoreCase("M")) {
					quoteExist = motService.validateMotorEndt(req.getQuoteNo());
				} else {
					quoteExist = commonService.validateCommonEndt(req.getQuoteNo());
				}

				if (quoteExist.size() > 0) {
					for (Tuple data : quoteExist) {
						if (Integer.valueOf(data.get("homeCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (data.get("policyNo") == " " || data.get("policyNo") == null) {
							error.add(new Error("02", "PolicyNo", "Policy No is Null "));
						} else if (!data.get("status").equals("P")) {
							error.add(new Error("02", "Status", "Status is Null "));
						} else if (Integer.valueOf(data.get("perCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (Integer.valueOf(data.get("policyCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						} else if (Integer.valueOf(data.get("motorCount").toString()) < 0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}
					}

				} else {
					error.add(new Error("02", "List", "No Data Found "));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return error;
	}

	@Override
	public List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		List<GetAllMotorDetailsRes> reslist = new ArrayList<GetAllMotorDetailsRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			/*
			 * String loginId = req.getLoginId();
			 * 
			 * // Branch Res List<String> branches = new ArrayList<String>();
			 * 
			 * if (req.getBranchCode().equalsIgnoreCase("99999")) {
			 * 
			 * List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);
			 * 
			 * branches = loginBranch.stream().filter(o ->
			 * !o.getBrokerBranchCode().equalsIgnoreCase("None"))
			 * .map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList()); if
			 * (branches.size() <= 0) { branches =
			 * loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors
			 * .toList());
			 * 
			 * }
			 * 
			 * } else if (req.getUserType().equalsIgnoreCase("Broker") ||
			 * req.getUserType().equalsIgnoreCase("User")) {
			 * branches.add(req.getBranchCode()); } else {
			 * branches.add(req.getBranchCode()); }
			 */
			String loginId = "";
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res

			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

			branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
					.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
			if (branches.size() <= 0) {
				branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

			}

			branches.add(req.getBranchCode());
			List<Tuple> list = null;
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				list = motService.searchMotorQuote(req, branches);

			} else if (product.getMotorYn().equalsIgnoreCase("H")
					/*&& req.getProductId().equalsIgnoreCase(travelProductId)*/) {
				list = traService.searchTravelQuote(req, branches);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				list = buiService.searchBuildingQuote(req, branches);

			} else {
				list = commonService.searchCommonQuote(req, branches);
			}

			for (Tuple data : list) {
				GetAllMotorDetailsRes res = new GetAllMotorDetailsRes();

				// Risk
				if (product.getMotorYn().equalsIgnoreCase("M")) {
					if (data != null) {
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setIdNumber(data.get("idNumber") == null ? null : data.get("idNumber").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						// res.setRiskId(data.get("riskId")==null?null:data.get("riskId").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setInsuranceType(
								data.get("insuranceType") == null ? null : data.get("insuranceType").toString());
						res.setAccident(data.get("accident") == null ? null : data.get("accident").toString());
						// res.setGpsTrackingInstalled(data.get("gpsTrackingInstalled")==null?null:data.get("gpsTrackingInstalled").toString());
						// res.setGpsTrackingInstalled(data.get("gpsTrackingInstalled")==null?null:data.get("gpsTrackingInstalled").toString());
						// res.setManufactureYear(data.get("manufactureYear")==null?null:data.get("manufactureYear").toString());

						res.setInsuranceTypeDesc(data.get("insuranceTypeDesc") == null ? null
								: data.get("insuranceTypeDesc").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						// res.setMotorCategory(data.get("motorCategory")==null?null:data.get("motorCategory").toString());
						// res.setMotorCategoryDesc(data.get("motorCategoryDesc")==null?null:data.get("motorCategoryDesc").toString());
						// res.setMotorUsage(data.get("motorUsage")==null?null:data.get("motorUsage").toString());
						res.setRegistrationNumber(data.get("registrationNumber") == null ? null
								: data.get("registrationNumber").toString());
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setChassisNumber(
								data.get("chassisNumber") == null ? null : data.get("chassisNumber").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setVehicleMake(data.get("vehicleMake") == null ? null : data.get("vehicleMake").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setVehicleMakeDesc(
								data.get("vehicleMakeDesc") == null ? null : data.get("vehicleMakeDesc").toString());
						res.setVehicleMakeDesc(
								data.get("vehicleMakeDesc") == null ? null : data.get("overallPremiumFc").toString());
						res.setVehcileModel(
								data.get("vehcileModel") == null ? null : data.get("vehcileModel").toString());
						res.setVehcileModelDesc(
								data.get("vehcileModelDesc") == null ? null : data.get("vehcileModelDesc").toString());
						res.setVehicleType(data.get("vehicleType") == null ? null : data.get("vehicleType").toString());
						res.setVehicleTypeDesc(
								data.get("vehicleTypeDesc") == null ? null : data.get("vehicleTypeDesc").toString());
						res.setModelNumber(data.get("modelNumber") == null ? null : data.get("modelNumber").toString());
						// res.setEngineNumber(data.get("engineNumber")==null?null:data.get("engineNumber").toString());
//						res.setFuelType(data.get("fuelType")==null?null:data.get("fuelType").toString()); 
//						res.setFuelTypeDesc(data.get("fuelTypeDesc")==null?null:data.get("fuelTypeDesc").toString());
//						res.setOverridePercentage(data.get("overridePercentage")==null?null:data.get("overridePercentage").toString());
//						res.setRegistrationYear(data.get("registrationYear")==null?null:data.get("registrationYear").toString()); 
//						res.setSeatingCapacity(data.get("seatingCapacity")==null?null:data.get("seatingCapacity").toString());
//						res.setCubicCapacity(data.get("cubicCapacity")==null?null:data.get("cubicCapacity").toString());
//						res.setColor(data.get("color")==null?null:data.get("color").toString());
						// res.setCo(data.get("colorDesc")==null?null:data.get("colorDesc").toString());
						// res.setg//(data.get("grossWeight")==null?null:data.get("grossWeight").toString());
						// res.setT//(data.get("tareWeight")==null?null:data.get("tareWeight").toString());
						// res.setCovernoteNo(data.get("covernoteNo")==null?null:data.get("covernoteNo").toString());
//						res.set(data.get("stickerNo")==null?null:data.get("stickerNo").toString());
//						res.set(data.get("periodOfInsurance")==null?null:data.get("periodOfInsurance").toString());
//						res.setWindScreenSumInsured(data.get("windScreenSumInsured")==null?null:data.get("windScreenSumInsured").toString());
//						res.set(data.get("acccessoriesSumInsured")==null?null:data.get("acccessoriesSumInsured").toString());
//						res.set(data.get("accessoriesInformation")==null?null:data.get("accessoriesInformation").toString());
//						res.set(data.get("numberOfAxels")==null?null:data.get("numberOfAxels").toString());
//						res.set(data.get("axelDistance")==null?null:data.get("axelDistance").toString());
//						res.setSumInsured(data.get("sumInsured")==null?null:data.get("sumInsured").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());
//						res.setTppdFreeLimit(data.get("tppdFreeLimit")==null?null:data.get("tppdFreeLimit").toString());
//						res.setTppdIncreaeLimit(data.get("tppdIncreaeLimit")==null?null:data.get("tppdIncreaeLimit").toString());
//						res.setSpecialTermsOfPremium(data.get("specialTermsOfPremium")==null?null:data.get("specialTermsOfPremium").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());
						res.setInsuranceClass(
								data.get("insuranceClass") == null ? null : data.get("insuranceClass").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						// res.setCompanyName(data.get("companyName")==null?null:data.get("companyName").toString());
//						res.setPolicyType(data.get("policyType")==null?null:data.get("policyType").toString());
//						res.sePolicyTypeDesc(data.get("policyTypeDesc")==null?null:data.get("policyTypeDesc").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						// res.setRoofRack(data.get("roofRack")==null?null:data.get("roofRack").toString());

						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						// res.set(data.get("trailerDetails")==null?null:data.get("trailerDetails").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setUpdatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
						// res.setDrivenBy(data.get("drivenBy")==null?null:data.get("drivenBy").toString());

						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						res.setDrivenByDesc(
								data.get("drivenByDesc") == null ? null : data.get("drivenByDesc").toString());
						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						// res.setDefectiveVisionOrHearing(data.get("defectiveVisionOrHearing")==null?null:data.get("defectiveVisionOrHearing").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
//						res.setMotoringOffence(data.get("motoringOffence")==null?null:data.get("motoringOffence").toString());
//						res.setFleetOwnerYn(data.get("fleetOwnerYn")==null?null:data.get("fleetOwnerYn").toString());
//						res.setSuspensionOfLicense(data.get("suspensionOfLicense")==null?null:data.get("suspensionOfLicense").toString());
//						res.setNoOfVehicles(data.get("noOfVehicles")==null?null:data.get("noOfVehicles").toString());
//						res.setIrrespectiveOfBlame(data.get("irrespectiveOfBlame")==null?null:data.get("irrespectiveOfBlame").toString());
//						res.setNoOfCompehensives(data.get("noOfCompehensives")==null?null:data.get("noOfCompehensives").toString());
//						res.setVehicleInterestedCompany(data.get("vehicleInterestedCompany")==null?null:data.get("vehicleInterestedCompany").toString());
//						res.setClaimRatio(data.get("claimRatio")==null?null:data.get("claimRatio").toString()); 
//						res.setInterestedCompanyDetails(data.get("interestedCompanyDetails")==null?null:data.get("interestedCompanyDetails").toString());
//						res.setCollateralYn(data.get("collateralYn")==null?null:data.get("collateralYn").toString());
//						res.setBorrowerType(data.get("borrowerType")==null?null:data.get("borrowerType").toString());
//						res.setOtherVehicle(data.get("otherVehicle")==null?null:data.get("otherVehicle").toString());
//						res.setBorrowerTypeDesc(data.get("borrowerTypeDesc")==null?null:data.get("borrowerTypeDesc").toString());

//						res.setOtherVehicleDetails(data.get("otherVehicleDetails")==null?null:data.get("otherVehicleDetails").toString());
//						res.setOtherInsurance(data.get("collateralName")==null?null:data.get("collateralName").toString());
//						res.setOtherInsurance(data.get("otherInsurance")==null?null:data.get("otherInsurance").toString());
//						res.setFirstLossPayee(data.get("firstLossPayee")==null?null:data.get("firstLossPayee").toString());
//						res.setOtherInsuranceDetails(data.get("otherInsuranceDetails")==null?null:data.get("otherInsuranceDetails").toString());
//						res.setHoldInsurancePolicy(data.get("holdInsurancePolicy")==null?null:data.get("holdInsurancePolicy").toString());
//						res.setNoOfClaims(data.get("noOfClaims")==null?null:data.get("noOfClaims").toString());
//						res.setCityLimit(data.get("cityLimit")==null?null:data.get("cityLimit").toString());
//						res.setAdditionalCircumstances(data.get("additionalCircumstances")==null?null:data.get("additionalCircumstances").toString());
						res.setSavedFrom(data.get("savedFrom") == null ? null : data.get("savedFrom").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setSubUserType(data.get("subUserType") == null ? null : data.get("subUserType").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						// res.setAdminLoginId(data.get("adminLoginId")==null?null:data.get("adminLoginId").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						// res.setBrokerBranchName(data.get("brokerBranchName")==null?null:data.get("brokerBranchName").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());

						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
//						res.setCommissionTypeDesc(data.get("commissionTypeDesc")==null?null:data.get("commissionTypeDesc").toString());
//						res.setOldReqRefNo(data.get("oldReqRefNo")==null?null:data.get("oldReqRefNo").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
//						res.setDriverYn(data.get("driverYn")==null?null:data.get("driverYn").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
//						res.setManualReferalYn(data.get("manualReferalYn")==null?null:data.get("manualReferalYn").toString());
//						res.setTiraCoverNoteNo(data.get("tiraCoverNoteNo")==null?null:data.get("tiraCoverNoteNo").toString());

						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						res.setIsFinaceYn(data.get("isFinaceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setEndtCount(data.get("endtCount") == null ? null : data.get("endtCount").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					EserviceMotorDetails riskData =data.get("c") ==null?null: (EserviceMotorDetails) data.get("c")   ;
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getSectionName());	
//					}

				} else if (product.getMotorYn().equalsIgnoreCase("H")
						&& req.getProductId().equalsIgnoreCase(travelProductId)) {
					EserviceTravelDetails riskData = new EserviceTravelDetails();
					if (data != null) {
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("quoteNo").toString());
						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);

						// res.setTravelStartDate(data.get("travelStartDate")==null?null:data.get("travelStartDate").toString());
						// res.setTravelEndDat(data.get("travelEndDate")==null?null:data.get("travelEndDate").toString());
						// res.setRejectReason(data.get("rejectReason")==null?null:data.get("rejectReason").toString());
						// res.setSdminRemarks(data.get("adminRemarks")==null?null:data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
//						res.setRiskId(data.get("riskId")==null?null:data.get("riskId").toString());
//						res.setTravelCoverId(data.get("travelCoverId")==null?null:data.get("travelCoverId").toString());
//						res.setTravelCoverDesc(data.get("travelCoverDesc")==null?null:data.get("travelCoverDesc").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
//						res.setSourceCountry(data.get("sourceCountry")==null?null:data.get("sourceCountry").toString());
//						res.setDestinationCountry(data.get("destinationCountry")==null?null:data.get("destinationCountry").toString());
//						res.setSportsCoverYn(data.get("sportsCoverYn")==null?null:data.get("sportsCoverYn").toString());
//						res.setTerrorismCoverYn(data.get("terrorismCoverYn")==null?null:data.get("terrorismCoverYn").toString());
//						res.setPlanTypeId(data.get("planTypeId")==null?null:data.get("planTypeId").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
//						res.setPlanTypeDesc(data.get("planTypeDesc")==null?null:data.get("planTypeDesc").toString());
//						res.setTravelCoverDuration(data.get("travelCoverDuration")==null?null:data.get("travelCoverDuration").toString());
//						res.setTotalPassengers(data.get("totalPassengers")==null?null:data.get("totalPassengers").toString());
//						res.setTotalPremium(data.get("totalPremium")==null?null:data.get("totalPremium").toString());
//						res.setAge(data.get("age")==null?null:data.get("age").toString()); 
//						res.setEffectiveDate(data.get("effectiveDate")==null?null:data.get("effectiveDate").toString());
						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setUpdatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
//						res.setRemarks(data.get("remarks")==null?null:data.get("remarks").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
//						res.setCovidCoverYn(data.get("covidCoverYn")==null?null:data.get("covidCoverYn").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setSubUserType(data.get("subUserType") == null ? null : data.get("subUserType").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
//						res.setAdminLoginId(data.get("adminLoginId")==null?null:data.get("adminLoginId").toString());
//						res.setBdmCode.set(data.get("bdmCode")==null?null:data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
//						res.setcustomerCode(data.get("customerCode")==null?null:data.get("customerCode").toString());
//						res.setBrokerBranchName(data.get("brokerBranchName")==null?null:data.get("brokerBranchName").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());
//						res.setCompanyName(data.get("companyName")==null?null:data.get("companyName").toString());
//						res.setProductName(data.get("productName")==null?null:data.get("productName").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
//						res.setCommissionTypeDesc(data.get("commissionTypeDesc")==null?null:data.get("commissionTypeDesc").toString());
//						res.setSourceCountryDesc(data.get("sourceCountryDesc")==null?null:data.get("sourceCountryDesc").toString());
//						res.setDestinationCountryDesc(data.get("destinationCountryDesc")==null?null:data.get("destinationCountryDesc").toString());
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
//						res.setactualPremiumFc(data.get("actualPremiumFc")==null?null:data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
//						res.setOldReqRefNo(data.get("oldReqRefNo")==null?null:data.get("oldReqRefNo").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
//						res.setManualReferalYn(data.get("manualReferalYn")==null?null:data.get("manualReferalYn").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());
						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						res.setIsFinaceYn(data.get("isFinaceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
//						res.setEndtPremium(data.get("endtPremium")==null?null:data.get("endtPremium").toString());

						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					//EserviceTravelDetails riskData =data==null?null: (EserviceTravelDetails) data;
//					if( riskData !=null ) {
//						dozermapper.map(riskData==null?null:data.get("quoteNo").toString()); res);
//						res.setSectionName(riskData.getSectionName());	
//					}

				} else if (product.getMotorYn().equalsIgnoreCase("A")) {
//					EserviceBuildingDetails riskData =data.get("c") ==null?null: (EserviceBuildingDetails) data.get("c")   ;
//					
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getProductDesc());	
//					}
					if (data != null) {
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setBrokerBranchCode(
								data.get("brokerBranchCode") == null ? null : data.get("brokerBranchCode").toString());
						res.setRequestReferenceNo(data.get("requestReferenceNo") == null ? null
								: data.get("requestReferenceNo").toString());
						res.setInsuranceType(
								data.get("insuranceType") == null ? null : data.get("insuranceType").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());
						res.setCreatedBy(data.get("createBy") == null ? null : data.get("createdBy").toString());

						String entryDate = data.get("entryDate") == null ? null
								: dateFormat.format(data.get("entryDate"));
						res.setEntryDate(entryDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						// res.set(data.get("trailerDetails")==null?null:data.get("trailerDetails").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));
						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);
						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setSubUserType(data.get("subUserId") == null ? null : data.get("subUserId").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						res.setCommissionType(
								data.get("commissionType") == null ? null : data.get("commissionType").toString());
						res.setEndorsementType(
								data.get("endorsement") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endoresmentTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());

						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);
						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						String endorsementEffdate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffdate);

						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtpolicyNo") == null ? null : data.get("endtPrevPolicYNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtCount(data.get("endCount") == null ? null : data.get("endCount").toString());
						res.setEndtStatus(data.get("endStatus") == null ? null : data.get("endStatus").toString());
						res.setIsFinaceYn(data.get("isFinanceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
				} else {

					if (data != null) {
						res.setCustomerReferenceNo(data.get("customerReferenceNo") == null ? null
								: data.get("customerReferenceNo").toString());
						res.setSectionId(data.get("sectionId") == null ? null : data.get("sectionId").toString());
						res.setProductId(data.get("productId") == null ? null : data.get("productId").toString());
						res.setCompanyId(data.get("companyId") == null ? null : data.get("companyId").toString());
						res.setBranchCode(data.get("branchCode") == null ? null : data.get("branchCode").toString());
						res.setAgencyCode(data.get("agencyCode") == null ? null : data.get("agencyCode").toString());
						res.setAgencyCode(data.get("sumInsured") == null ? null : data.get("sumInsured").toString());
						res.setStatus(data.get("status") == null ? null : data.get("status").toString());

						String updatedDate = data.get("updatedDate") == null ? null
								: dateFormat.format(data.get("updatedDate"));
						res.setUpdatedDate(updatedDate);
						res.setCreatedBy(data.get("createdBy") == null ? null : data.get("createdBy").toString());
						res.setCreatedBy(data.get("updatedBy") == null ? null : data.get("updatedBy").toString());
						res.setAdminRemarks(
								data.get("adminRemarks") == null ? null : data.get("adminRemarks").toString());
						res.setReferalRemarks(
								data.get("referalRemarks") == null ? null : data.get("referalRemarks").toString());
						res.setBrokerCode(data.get("brokerCode") == null ? null : data.get("brokerCode").toString());
						res.setLoginId(data.get("loginId") == null ? null : data.get("loginId").toString());
						res.setAcExecutiveId(
								data.get("acExecutiveId") == null ? null : data.get("acExecutiveId").toString());
						res.setQuoteNo(data.get("quoteNo") == null ? null : data.get("quoteNo").toString());
						res.setApplicationId(
								data.get("applicationId") == null ? null : data.get("applicationId").toString());
						res.setCustomerId(data.get("customerId") == null ? null : data.get("customerId").toString());
						res.setCurrency(data.get("currency") == null ? null : data.get("currency").toString());
						res.setExchangeRate(
								data.get("exchangeRate") == null ? null : data.get("exchangeRate").toString());

						String policyStartDate = data.get("policyStartDate") == null ? null
								: dateFormat.format(data.get("policyStartDate"));

						res.setPolicyStartDate(policyStartDate);

						String policyEndDate = data.get("policyEndDate") == null ? null
								: dateFormat.format(data.get("policyEndDate"));
						res.setPolicyEndDate(policyEndDate);

						res.setActualPremiumLc(
								data.get("actualPremiumLc") == null ? null : data.get("actualPremiumLc").toString());
						res.setActualPremiumFc(
								data.get("actualPremiumFc") == null ? null : data.get("actualPremiumFc").toString());
						res.setOverallPremiumLc(
								data.get("overallPremiumLc") == null ? null : data.get("overallPremiumLc").toString());
						res.setOverallPremiumFc(
								data.get("overallPremiumFc") == null ? null : data.get("overallPremiumFc").toString());
						res.setHavepromocode(
								data.get("havepromocode") == null ? null : data.get("havepromocode").toString());
						res.setPromocode(data.get("promocode") == null ? null : data.get("promocode").toString());
						res.setBankCode(data.get("bankCode") == null ? null : data.get("bankCode").toString());
						res.setSourceType(data.get("sourceType") == null ? null : data.get("sourceType").toString());
						res.setCustomerCode(
								data.get("customerCode") == null ? null : data.get("customerCode").toString());
						res.setBdmCode(data.get("bdmCode") == null ? null : data.get("bdmCode").toString());
						res.setEndorsementType(
								data.get("endorsementType") == null ? null : data.get("endorsementType").toString());
						res.setEndorsementTypeDesc(data.get("endorsementTypeDesc") == null ? null
								: data.get("endorsementTypeDesc").toString());

						String endorsementDate = data.get("endorsementDate") == null ? null
								: dateFormat.format(data.get("endorsementDate"));
						res.setEndorsementDate(endorsementDate);

						res.setEndorsementRemarks(data.get("endorsementRemarks") == null ? null
								: data.get("endorsementRemarks").toString());

						String endorsementEffDate = data.get("endorsementEffdate") == null ? null
								: dateFormat.format(data.get("endorsementEffdate"));
						res.setEndorsementEffdate(endorsementEffDate);
						res.setPolicyNo(data.get("policyNo") == null ? null : data.get("policyNo").toString());
						res.setOriginalPolicyNo(
								data.get("originalPolicyNo") == null ? null : data.get("originalPolicyNo").toString());
						res.setEndtPrevPolicyNo(
								data.get("endtPrevPolicyNo") == null ? null : data.get("endtPrevPolicyNo").toString());
						res.setEndtPrevQuoteNo(
								data.get("endtPrevQuoteNo") == null ? null : data.get("endtPrevQuoteNo").toString());
						res.setEndtCount(data.get("endtCount") == null ? null : data.get("endtCount").toString());
						res.setEndtStatus(data.get("endtStatus") == null ? null : data.get("endtStatus").toString());
						res.setIsFinaceYn(data.get("isFinaceYn") == null ? null : data.get("isFinaceYn").toString());
						res.setEndtCategDesc(
								data.get("endtCategDesc") == null ? null : data.get("endtCategDesc").toString());
						res.setSectionName(data.get("sectionName") == null ? null : data.get("sectionName").toString());
					}
//					EserviceCommonDetails riskData =data.get("c") ==null?null: (EserviceCommonDetails) data.get("c")   ;
//					if( riskData !=null ) {
//						dozermapper.map(riskData, res);
//						res.setSectionName(riskData.getSectionName());	
//					}
				}
				res.setClientName((data.get("clientName").toString()));
				res.setIdsCount(data.get("idsCount") == null ? "" : data.get("idsCount").toString());
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
	public List<DropDownRes> copyQuoteByDropdown(CopyQuoteDropDownReq req) {

		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {

			List<ListItemValue> getList = new ArrayList<ListItemValue>();
			String itemType = "";
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				itemType = "COPY_QUOTE_BY_MOTOR";
				getList = motService.geMotorCoptyQuotetListItem(req, itemType);
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				itemType = "COPY_QUOTE_BY_TRAVEL";
				getList = traService.getTravelCoptyQuotetListItem(req, itemType);
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				itemType = "COPY_QUOTE_BY_BUILDING";
				getList = buiService.geBuildingCoptyQuotetListItem(req, itemType);

			} else {
				itemType = "COPY_QUOTE_BY_COMMON";
				getList = buiService.geBuildingCoptyQuotetListItem(req, itemType);
			}
			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralRequoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}

			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorReferalDetails(req, branches, limit, offset,
						"RE");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				referralRejectedList = traService.getTravelReferalDetails(req, branches, limit, offset, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				referralRejectedList = buiService.getBuildingReferalDetails(req, branches, limit, offset, "RE");
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req,
						branches, limit, offset, "RE");
				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : referralRejectedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralRequote(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(req.getApplicationId());
			branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorGridCriteriaRes> List2 = motService.getMotorAdminReferalDetails(req, branches, limit, offset,
						"RE");
				for (MotorGridCriteriaRes data : List2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				adminReferralRejectedList = traService.getTravelAdminReferalDetails(req, branches, limit, offset, "RE");
			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				adminReferralRejectedList = buiService.getBuildingAdminReferalDetails(req, branches, limit, offset,
						"RE");
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonAdminReferalDetails(req,
						branches, limit, offset, "RE");
				for (ReferalCommonCriteriaRes data : referralPendingList2) {
					EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					custRes.add(res);
				}
				return custRes;
			}
			for (ReferalGridCriteriaRes data : adminReferralRejectedList) {
				EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				res = dozerMapper.map(data, EserviceCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public UpdateLapsedQuoteRes updateLapsedQuoteDetails(UpdateLapsedQuoteReq req) {
		UpdateLapsedQuoteRes res = new UpdateLapsedQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			EserviceMotorDetails motordata = new EserviceMotorDetails();
			EserviceTravelDetails traveldata = new EserviceTravelDetails();
			EserviceBuildingDetails buildingdata = new EserviceBuildingDetails();
			EserviceCommonDetails commondata = new EserviceCommonDetails();
			HomePositionMaster homeData = homeRepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product = getCompanyProductMasterDropdown(homeData.getCompanyId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				motordata = repo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),
						req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(motordata, EserviceMotorDetails.class);
				motordata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(motordata.getRequestReferenceNo());
				res.setQuoteNo(motordata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			}

			else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				traveldata = travelRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(traveldata, EserviceTravelDetails.class);
				traveldata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(traveldata.getRequestReferenceNo());
				res.setQuoteNo(traveldata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			} else if (product.getMotorYn().equalsIgnoreCase("A")) {
				buildingdata = buildingRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(buildingdata, EserviceBuildingDetails.class);
				buildingdata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(buildingdata.getRequestReferenceNo());
				res.setQuoteNo(buildingdata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			} else {
				commondata = commonRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(
						req.getRequestReferenceNo(), req.getQuoteNo(), req.getProductId(), req.getCompanyId());
				dozerMapper.map(commondata, EserviceCommonDetails.class);
				commondata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(commondata.getRequestReferenceNo());
				res.setQuoteNo(buildingdata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	// Portfolio

	@Override
	public List<PortfolioCustomerDetailsRes> getallPortfolioActive(ExistingQuoteReq req) {
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}

			List<PortfolioGridCriteriaRes> portfolioActiveList = new ArrayList<PortfolioGridCriteriaRes>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				portfolioActiveList = motService.getMotorProtfolioActive(req, branches, today, limit, offset, "P");
			} else {
				portfolioActiveList = commonService.getCommonProtfolioActive(req, branches, today, limit, offset, "P");
			}
//			 if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelProtfolioActive(req  , branches, limit , offset, "P" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingProtfolioActive(req  , branches, limit , offset, "P" );
//				}
			for (PortfolioGridCriteriaRes data : portfolioActiveList) {
				PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
				res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
				res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
				res.setClientName(data.getClientName());
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<PortfolioCustomerDetailsRes> getallPortfolioPending(ExistingQuoteReq req) {
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, +365);
			Date before365 = cal.getTime();
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			List<PortfolioGridCriteriaRes> list = new ArrayList<PortfolioGridCriteriaRes>();
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				list = motService.getMotorProtfolioPending(req, branches, today, limit, offset, "P");
			} else {
				list = commonService.getCommonProtfolioPending(req, branches, today, limit, offset, "P");

			}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelProtfolioPending(req  , branches, limit , offset, "P" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingProtfolioPending(req  , branches, limit , offset, "P" );
//				}
			for (PortfolioGridCriteriaRes data : list) {
				PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
				res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
				// res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<PortfolioCustomerDetailsRes> getallPortfolioCancelled(ExistingQuoteReq req) {
		List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before365 = cal.getTime();
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "";
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}

			List<PortfolioGridCriteriaRes> list = new ArrayList<PortfolioGridCriteriaRes>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				list = motService.getMotorPortfolioCancelled(req, branches, today, limit, offset, "D");
			} else {
				list = commonService.getCommonPortfolioCancelled(req, branches, today, limit, offset, "D");
			}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelPortfolioCancelled(req  , branches, limit , offset, "D" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingPortfolioCancelled(req  , branches, limit , offset, "D" );
//				}
			for (PortfolioGridCriteriaRes data : list) {
				PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
				res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
				// res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				custRes.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<DropDownRes> getallIssuerQuoteDetails(IssuerQuoteReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			List<Tuple> List = new ArrayList<Tuple>();
			CompanyProductMaster product = getCompanyProductMasterDropdown(req.getInsuranceId(),
					req.getProductId().toString());

			// Product Wise Get
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List = motService.getMotorIssuerQuoteDetails(req, before30, today);
			}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
//					extingQuoteList = traService.getTravelExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
//					extingQuoteList = buiService.getBuildingExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//					// Common
//				} else { // (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					extingQuoteList = commonService.getCommonExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				}

			for (Tuple data : List) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.get("loginId").toString());
				res.setCodeDesc(data.get("agencyCode").toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

}

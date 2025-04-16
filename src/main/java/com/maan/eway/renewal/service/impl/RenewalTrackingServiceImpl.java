package com.maan.eway.renewal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.req.GetBrokerListDropDownReq;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.renewal.req.GetCustomersByBrokerReq;
import com.maan.eway.renewal.req.RenewalTrackingInReq;
import com.maan.eway.renewal.req.RtGetProductsReq;
import com.maan.eway.renewal.req.RtProductReq;
import com.maan.eway.renewal.res.GetBrokerListRes;
import com.maan.eway.renewal.res.RenewalTrackBranchByProductRes;
import com.maan.eway.renewal.res.RenewalTrackBranchRes;
import com.maan.eway.renewal.res.RenewalTrackBrokerRes;
import com.maan.eway.renewal.res.RenewalTrackByProductRes;
import com.maan.eway.renewal.res.RenewalTrackProductRes;
import com.maan.eway.renewal.res.RenewalTrackingDetails;
import com.maan.eway.renewal.res.RenewalTrackingInRes;
import com.maan.eway.renewal.res.RtProductRes;
import com.maan.eway.renewal.service.RenewalTrackingService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.RenewPremiaPolicyRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class RenewalTrackingServiceImpl implements RenewalTrackingService {
	@Autowired
	private HomePositionMasterRepository homePositionMasterRepo;

	@Autowired
	private RenewQuotePolicyRepository renewQuotePolicyRepo;

	@Autowired
	private LoginBranchMasterRepository branchRepo;

	@Autowired
	private LoginUserInfoRepository userInfo;

	@Autowired
	private RenewPremiaPolicyRepository rppRepo;
	
	@Autowired
	private LoginBranchMasterRepository lbmRepo;

	@Autowired
	private EntityManager em;

	/*
	 * public RenewalTrackingRes trackRenewal(RenewalTrackingReq req) {
	 * RenewalTrackingRes res = new RenewalTrackingRes(); List<HomePositionMaster>
	 * hpm = new ArrayList<HomePositionMaster>(); List<RenewalTrackingDetails> rtd =
	 * new ArrayList<RenewalTrackingDetails>(); try { String loginId =
	 * req.getLoginId(); hpm = getPolicyDetails(loginId);
	 * 
	 * Map<String, List<RenewQuotePolicy>> groupByStatus =
	 * groupByStatusOfRenewPolicy(hpm);
	 * 
	 * Integer completedRenewal = groupByStatus.get("RS").size(); Integer
	 * pendingRenewal = groupByStatus.get("RP").size(); Integer lostRenewal =
	 * groupByStatus.get("RF").size(); Integer totalRenewalCount =
	 * groupByStatus.get("ALL").size(); BigDecimal completionRate
	 * =rate(completedRenewal,totalRenewalCount); BigDecimal pendingRate
	 * =rate(pendingRenewal,totalRenewalCount); BigDecimal lostRate
	 * =rate(lostRenewal,totalRenewalCount); List<RenewQuotePolicy> allRenewals =
	 * groupByStatus.get("ALL"); // Step 1: Extract all oldPolicyNos from "ALL" list
	 * Set<String> allOldPolicyNos =
	 * allRenewals.stream().map(RenewQuotePolicy::getOldpolicyNo)
	 * .collect(Collectors.toSet());
	 * 
	 * // Step 2: Build the occurs map Map<HomePositionMaster, Boolean> occurs =
	 * hpm.stream() .collect(Collectors.toMap(h -> h, h ->
	 * allOldPolicyNos.contains(h.getPolicyNo()))); System.out.println(occurs); for
	 * (Map.Entry<HomePositionMaster, Boolean> o : occurs.entrySet()) {
	 * HomePositionMaster h = o.getKey(); if (o.getValue()) {
	 * rtd.add(saveRenewalTrackingDetails(h)); } }
	 * res.setTotalPolicyCount(totalRenewalCount);
	 * res.setCompletedRenewal(completedRenewal);
	 * res.setPendingRenewal(pendingRenewal); res.setLost(lostRenewal);
	 * res.setRenewalDetails(rtd);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return res; }
	 * 
	 * private RenewalTrackingDetails saveRenewalTrackingDetails(HomePositionMaster
	 * h) { RenewalTrackingDetails rtd = new RenewalTrackingDetails(); String
	 * customerName = h.getCustomerName(); String productName = h.getProductName();
	 * LocalDate policyEndDate =
	 * h.getExpiryDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	 * BigDecimal premium = h.getPremiumFc(); List<RenewQuotePolicy> rqp =
	 * renewQuotePolicyRepo.findByOldpolicyNo(h.getPolicyNo()); String Status =
	 * rqp.get(0).getStatus();
	 * 
	 * rtd.setCustomerName(customerName); rtd.setProductName(productName);
	 * rtd.setPremium(premium); rtd.setPolicyEndDate(policyEndDate);
	 * rtd.setStatus(Status); rtd.setBranchName(h.getBranchName());
	 * rtd.setLoginId(h.getLoginId()); return rtd; }
	 * 
	 * public List<HomePositionMaster> getPolicyDetails(String loginId) {
	 * List<HomePositionMaster> hpm = new ArrayList<HomePositionMaster>(); try {
	 * CriteriaBuilder cb = em.getCriteriaBuilder();
	 * CriteriaQuery<HomePositionMaster> q =
	 * cb.createQuery(HomePositionMaster.class); Root<HomePositionMaster> r =
	 * q.from(HomePositionMaster.class);
	 * 
	 * Date today = new Date(); Calendar cal = Calendar.getInstance();
	 * cal.setTime(today); cal.add(Calendar.MONTH, -1); Date oneMonthAgo =
	 * cal.getTime();
	 * 
	 * Predicate n1 = cb.equal(r.get("loginId"), loginId); Predicate n2 =
	 * cb.between(r.get("expiryDate"), oneMonthAgo, today);
	 * 
	 * q.select(r); q.where(n1, n2); q.orderBy(cb.desc(r.get("expiryDate")));
	 * TypedQuery<HomePositionMaster> typedQuery = em.createQuery(q); hpm =
	 * typedQuery.getResultList(); } catch (Exception e) { e.printStackTrace(); }
	 * return hpm; }
	 * 
	 * public Map<String, List<RenewQuotePolicy>>
	 * groupByStatusOfRenewPolicy(List<HomePositionMaster> hpm) { Map<String,
	 * List<RenewQuotePolicy>> map = new HashMap<String, List<RenewQuotePolicy>>();
	 * List<RenewQuotePolicy> renewalSuccess = new ArrayList<RenewQuotePolicy>();
	 * List<RenewQuotePolicy> renewaFailure = new ArrayList<RenewQuotePolicy>();
	 * List<RenewQuotePolicy> renewalPending = new ArrayList<RenewQuotePolicy>();
	 * List<RenewQuotePolicy> allRenewalList = new ArrayList<RenewQuotePolicy>();
	 * 
	 * try { for (HomePositionMaster hpm1 : hpm) { List<RenewQuotePolicy> rqp =
	 * renewQuotePolicyRepo.findByOldpolicyNo(hpm1.getPolicyNo()); if (rqp == null
	 * || rqp.isEmpty()) { continue; } for (RenewQuotePolicy rqp1 : rqp) { String
	 * statusCode = rqp1.getCurrentStatusCode(); if
	 * ("RS".equalsIgnoreCase(statusCode)) { renewalSuccess.add(rqp1); } else if
	 * ("RP".equalsIgnoreCase(statusCode)) { renewalPending.add(rqp1); } else if
	 * ("RF".equalsIgnoreCase(statusCode)) { renewaFailure.add(rqp1); }
	 * allRenewalList.add(rqp1); } } map.put("RS", renewalSuccess); map.put("RP",
	 * renewalPending); map.put("RF", renewaFailure); map.put("ALL",
	 * allRenewalList); } catch (Exception e) { e.printStackTrace(); } return map; }
	 */

	public List<RenewalTrackBrokerRes> getAllBrokersRenewPolicyDetailsCount() {
		List<RenewalTrackBrokerRes> resultList = new ArrayList<RenewalTrackBrokerRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalTrackBrokerRes> q = cb.createQuery(RenewalTrackBrokerRes.class);
			Root<RenewPremiaPolicy> r = q.from(RenewPremiaPolicy.class);

			LocalDate curDate = LocalDate.now();
			LocalDate beforeDate = curDate.minusMonths(5);

			Predicate n1 = cb.between(r.get("polExpDt"), beforeDate, curDate);

			q.multiselect(cb.count(r), r.get("sourceCode"), r.get("sourceName"));
			q.where(n1);
			q.groupBy(r.get("sourceCode"));

			TypedQuery<RenewalTrackBrokerRes> tq = em.createQuery(q);
			resultList = tq.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public List<Object[]> getAllBrokersRenewPolicyDetailsCountByProductWise() {
		List<Object[]> resultList = new ArrayList<Object[]>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> q = cb.createQuery(Object[].class);
			Root<RenewPremiaPolicy> r = q.from(RenewPremiaPolicy.class);

			LocalDate curDate = LocalDate.now();
			LocalDate beforeDate = curDate.minusMonths(5);

			Predicate n1 = cb.between(r.get("polExpDt"), beforeDate, curDate);

			q.multiselect(cb.count(r), r.get("sourceCode"), r.get("polProdCode"));
			q.where(n1);
			q.groupBy(r.get("sourceCode"), r.get("polProdCode"));

			TypedQuery<Object[]> tq = em.createQuery(q);
			resultList = tq.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	public RenewalTrackingInRes renewTrackByApprover2(RenewalTrackingInReq req) {
		RenewalTrackingInRes res = new RenewalTrackingInRes();
		try {
			List<LoginBranchMaster> branches = branchRepo.findByLoginId(req.getApproverLoginId());

			Map<String, List<GetBrokerListRes>> brokerMap = branches.stream()
					.collect(Collectors.toMap(LoginBranchMaster::getBranchCode, // key: branchCode
							b -> {
								GetBrokerListDropDownReq brokerReq = new GetBrokerListDropDownReq();
								brokerReq.setCompanyId(b.getCompanyId());
								brokerReq.setBranchCode(b.getBranchCode());
								brokerReq.setUserType("Broker");
								brokerReq.setSubUserType("broker");
								return getBrokerList(brokerReq); // value: List<GetBrokerListDropDownRes>
							}));

			res.setApproverLoginId(req.getApproverLoginId());
			List<RenewalTrackBranchRes> branchResList = new ArrayList<RenewalTrackBranchRes>();

			for (Map.Entry<String, List<GetBrokerListRes>> broker : brokerMap.entrySet()) {
				RenewalTrackBranchRes branchRes = new RenewalTrackBranchRes();
				List<RenewalTrackBrokerRes> brokerResList = new ArrayList<RenewalTrackBrokerRes>();
				String branchName = "";
				for (GetBrokerListRes b : broker.getValue()) {
					RenewalTrackBrokerRes brokerRes = new RenewalTrackBrokerRes();

					LoginUserInfo brokerInfo = userInfo.findByLoginId(b.getLoginId());
					List<RenewPremiaPolicy> rrp = rppRepo.findBySourceCode(brokerInfo.getCoreAppBrokerCode());
					branchName = StringUtils.isNotBlank(b.getBranchName()) ? b.getBranchName() : null;
					if (rrp.isEmpty()) {
						continue;
					}
					RenewalTrackBrokerRes br = getBrokerDetails(b, brokerInfo.getCoreAppBrokerCode());
					if (br != null) {
						brokerRes.setSourceCode(StringUtils.isNotBlank(brokerInfo.getCoreAppBrokerCode())
								? brokerInfo.getCoreAppBrokerCode()
								: "");
						Long total = br.getTotalRenewalNoOfPolicies();
						brokerRes.setTotalRenewalNoOfPolicies(total);
						branchName = b.getBranchName();
						brokerResList.add(brokerRes);
						if (StringUtils.isNotBlank(brokerInfo.getCoreAppBrokerCode())) {
							List<RenewPremiaPolicy> rpp = rppRepo.findBySourceCode(brokerInfo.getCoreAppBrokerCode());
							brokerRes.setSourceName(
									StringUtils.isNotBlank(rrp.get(0).getSourceName()) ? rrp.get(0).getSourceName()
											: "");

							// Extract all polNo
							List<String> policyNos = rpp.stream().map(RenewPremiaPolicy::getPolNo)
									.collect(Collectors.toList());

							List<RenewQuotePolicy> quotePolicies = renewQuotePolicyRepo.findByOldpolicyNoIn(policyNos);
							Map<String, Long> statusCountMap = quotePolicies.stream().collect(Collectors
									.groupingBy(RenewQuotePolicy::getCurrentStatusCode, Collectors.counting()));

							Long completion = statusCountMap.get("RS");
							Long pending = statusCountMap.get("RP");
							Long lost = statusCountMap.get("RF");

							brokerRes.setCompletedRenewal(statusCountMap.get("RS"));
							brokerRes.setPendingRenewal(statusCountMap.get("RP"));
							brokerRes.setCompletionRate(completion != null ? rate(completion, total) : null);
							brokerRes.setPendingRate(pending != null ? rate(pending, total) : null);
							brokerRes.setLostRate(lost != null ? rate(lost, total) : null);
							;

						}
					}

				}
				branchRes.setBranchCode(broker.getKey());
				branchRes.setBranchName(branchName);
				branchRes.setBrokerList(brokerResList);

				branchResList.add(branchRes);
			}
			;
			res.setBranchList(branchResList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	// new trial
	@Override
	public RenewalTrackingInRes renewTrackByApprover(RenewalTrackingInReq req) {
		RenewalTrackingInRes res = new RenewalTrackingInRes();
		try {
			List<LoginBranchMaster> branches = branchRepo.findByLoginId(req.getApproverLoginId());

			Map<String, List<GetBrokerListRes>> brokerMap = branches.stream()
					.collect(Collectors.toMap(LoginBranchMaster::getBranchCode, // key: branchCode
							b -> {
								GetBrokerListDropDownReq brokerReq = new GetBrokerListDropDownReq();
								brokerReq.setCompanyId(b.getCompanyId());
								brokerReq.setBranchCode(b.getBranchCode());
								brokerReq.setUserType("Broker");
								brokerReq.setSubUserType("broker");
								return getBrokerList(brokerReq); // value: List<GetBrokerListDropDownRes>
							}));

			res.setApproverLoginId(req.getApproverLoginId());
			List<RenewalTrackBranchRes> branchResList = new ArrayList<RenewalTrackBranchRes>();

			for (Map.Entry<String, List<GetBrokerListRes>> broker : brokerMap.entrySet()) {
				RenewalTrackBranchRes branchRes = new RenewalTrackBranchRes();
				List<RenewalTrackBrokerRes> brokerResList = new ArrayList<RenewalTrackBrokerRes>();
				String branchName = "";
				for (GetBrokerListRes b : broker.getValue()) {
					RenewalTrackBrokerRes brokerRes = new RenewalTrackBrokerRes();

					LoginUserInfo brokerInfo = userInfo.findByLoginId(b.getLoginId());
					List<RenewPremiaPolicy> rrp = rppRepo.findBySourceCode(brokerInfo.getCoreAppBrokerCode());
					branchName = StringUtils.isNotBlank(b.getBranchName()) ? b.getBranchName() : null;
					if (rrp.isEmpty()) {
						continue;
					}
					RenewalTrackBrokerRes br = getBrokerDetails(b, brokerInfo.getCoreAppBrokerCode());
					if (br != null) {
						brokerRes.setSourceCode(StringUtils.isNotBlank(brokerInfo.getCoreAppBrokerCode())
								? brokerInfo.getCoreAppBrokerCode()
								: "");
						Long total = br.getTotalRenewalNoOfPolicies();
						brokerRes.setTotalRenewalNoOfPolicies(total);
						branchName = b.getBranchName();
						brokerResList.add(brokerRes);
						if (StringUtils.isNotBlank(brokerInfo.getCoreAppBrokerCode())) {
							List<RenewQuotePolicy> rqp = renewQuotePolicyRepo
									.findBySourceCode(brokerInfo.getCoreAppBrokerCode());
							brokerRes.setSourceName(
									StringUtils.isNotBlank(rrp.get(0).getSourceName()) ? rrp.get(0).getSourceName()
											: "");

							Map<String, Long> statusCountMap = rqp.stream().collect(Collectors
									.groupingBy(RenewQuotePolicy::getCurrentStatusCode, Collectors.counting()));

							Long completion = statusCountMap.get("RS");
							Long pending = statusCountMap.get("RP");
							Long lost = statusCountMap.get("RF");

							brokerRes.setCompletedRenewal(statusCountMap.get("RS"));
							brokerRes.setPendingRenewal(statusCountMap.get("RP"));
							brokerRes.setCompletionRate(completion != null ? rate(completion, total) : null);
							brokerRes.setPendingRate(pending != null ? rate(pending, total) : null);
							brokerRes.setLostRate(lost != null ? rate(lost, total) : null);

						}
					}

				}
				branchRes.setBranchCode(broker.getKey());
				branchRes.setBranchName(branchName);
				branchRes.setBrokerList(brokerResList);

				branchResList.add(branchRes);
			}
			;
			res.setBranchList(branchResList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public RenewalTrackBrokerRes getBrokerDetails(GetBrokerListRes req, String coreAppBrokercode) {
		RenewalTrackBrokerRes res = new RenewalTrackBrokerRes();
		String sourceName = "";
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RenewalTrackBrokerRes> q = cb.createQuery(RenewalTrackBrokerRes.class);
			Root<RenewQuotePolicy> r = q.from(RenewQuotePolicy.class);

			LocalDate curDate = LocalDate.now();
			LocalDate beforeDate = curDate.minusMonths(5);

			ZoneId defaultZoneId = ZoneId.systemDefault();

// Convert LocalDate to java.util.Date
			Date curUtilDate = Date.from(curDate.atStartOfDay(defaultZoneId).toInstant());
			Date beforeUtilDate = Date.from(beforeDate.atStartOfDay(defaultZoneId).toInstant());

			Predicate n1 = cb.between(r.get("oldendDate"), beforeUtilDate, curUtilDate);
			Predicate n2 = cb.equal(r.get("sourceCode"), coreAppBrokercode);

			q.multiselect(r.get("sourceCode").alias("sourceCode"), cb.count(r).alias("totalRenewalNoOfPolicies"));
			q.where(n1, n2);
			q.groupBy(r.get("sourceCode"));

			TypedQuery<RenewalTrackBrokerRes> tq = em.createQuery(q);
			if (tq != null) {
				res = tq.getSingleResult();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public List<GetBrokerListRes> getBrokerList(GetBrokerListDropDownReq req) {

		List<GetBrokerListRes> resList = new ArrayList<GetBrokerListRes>();
		try {

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<GetBrokerListRes> query = cb.createQuery(GetBrokerListRes.class);

			// Find All
			Root<LoginMaster> c = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			Root<LoginBranchMaster> l = query.from(LoginBranchMaster.class);

			// Select
			query.multiselect(c.get("loginId").alias("loginId"), c.get("status").alias("status"),
					c.get("oaCode").alias("oaCode"), c.get("agencyCode").alias("agencyCode"),
					u.get("userName").alias("userName"), u.get("customerCode").alias("customerCode"),
					u.get("customerName").alias("customerName"), l.get("branchCode").alias("branchCode"),
					l.get("branchName").alias("branchName"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("loginId")));

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("userType"), req.getUserType());
			Predicate n3 = cb.equal(c.get("subUserType"), req.getSubUserType());
			Predicate n4 = cb.equal(c.get("companyId"), req.getCompanyId());

			Predicate n9 = cb.equal(u.get("loginId"), c.get("loginId"));
			Predicate n10 = cb.equal(u.get("loginId"), l.get("loginId"));
			Predicate n11 = cb.equal(l.get("branchCode"), req.getBranchCode());
			Predicate n12 = cb.equal(l.get("branchCode"), "99999");
			Predicate n13 = cb.or(n11, n12);

			query.where(n1, n2, n3, n4, n9, n10, n13).orderBy(orderList);

			// Get Result
			TypedQuery<GetBrokerListRes> result = em.createQuery(query);
			resList = result.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resList;
	}

	public BigDecimal rate(Long count, Long totalCount) {
		if (count == null || totalCount == null || totalCount == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal countBD = BigDecimal.valueOf(count);
		BigDecimal totalCountBD = BigDecimal.valueOf(totalCount);

		return countBD.multiply(BigDecimal.valueOf(100)).divide(totalCountBD, 2, RoundingMode.HALF_UP); // returns
																										// percentage

	}

	@Override
	public List<RenewalTrackingDetails> getBrokersCustomerList(GetCustomersByBrokerReq req) {
		List<RenewalTrackingDetails> resList = new ArrayList<RenewalTrackingDetails>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<RenewPremiaPolicy> rpp = rppRepo.findBySourceCode(req.getSourceCode());
			if (!rpp.isEmpty()) {
				for (RenewPremiaPolicy rp : rpp) {
					RenewalTrackingDetails res = mapper.map(rp, RenewalTrackingDetails.class);
					List<RenewQuotePolicy> rq = renewQuotePolicyRepo.findByOldpolicyNo(rp.getPolNo());
					res.setStatus(rq.get(0).getCurrentStatus());
					res.setPolExpDt(formatDateOnly(rp.getPolExpDt()));;
					resList.add(res);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}
	
	
	public Date formatDateOnly(Date inputDate) {
	    try {
	        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	        String formatted = sdf.format(inputDate); // format date to string
	        return sdf.parse(formatted);              // parse string back to Date
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}


	@Override
	public RenewalTrackByProductRes renewTrackForProductPerf(RenewalTrackingInReq req) {
		RenewalTrackByProductRes res = new RenewalTrackByProductRes();
		try {
			List<LoginBranchMaster> branches = branchRepo.findByLoginId(req.getApproverLoginId());
             String companyId = branches.get(0).getCompanyId();
			Map<String, List<RtProductRes>> productMap = branches.stream()
					.collect(Collectors.toMap(LoginBranchMaster::getBranchCode, b -> {
						RtProductReq productReq = new RtProductReq();
						productReq.setBranchCode(b.getBranchCode());
						productReq.setCompanyId(b.getCompanyId());
						return getProductList(productReq);
					}));

			res.setApproverLoginId(req.getApproverLoginId());
			List<RenewalTrackBranchByProductRes> branchResList = new ArrayList<RenewalTrackBranchByProductRes>();

			for (Entry<String, List<RtProductRes>> product : productMap.entrySet()) {
				RenewalTrackBranchByProductRes branchRes = new RenewalTrackBranchByProductRes();
				List<RenewalTrackProductRes> productResList = new ArrayList<RenewalTrackProductRes>();
				String branchName ="";
				for (RtProductRes b : product.getValue()) {
					branchName= b.getBranchName();
					RenewalTrackProductRes productRes = new RenewalTrackProductRes();
					
					List<RenewQuotePolicy> rqp=renewQuotePolicyRepo.findByBranchCodeAndCompanyIdAndPolProductCode(product.getKey(),companyId,b.getPolProductCode());
					
					Long total = (long)rqp.size();
					productRes.setProdcode(b.getPolProductCode());
					productRes.setProdName(b.getProductName());					
					productRes.setTotalRenewalNoOfPolicies(total);
					
					Map<String , Long> statusCountMap= rqp.stream().collect(
							Collectors.groupingBy(RenewQuotePolicy::getCurrentStatusCode,Collectors.counting()));
					
					Long completion = statusCountMap.get("RS");
					Long pending = statusCountMap.get("RP");
					Long lost = statusCountMap.get("RF");
					
					productRes.setCompletedRenewal(completion);
					productRes.setPendingRenewal(pending);
					productRes.setLost(lost);
					productRes.setCompletionRate(completion != null ? rate(completion, total) : null);
					productRes.setPendingRate(pending != null ? rate(pending, total) : null);
					productRes.setLostRate(lost != null ? rate(lost, total) : null);;

					productResList.add(productRes);
				}
				branchRes.setBranchCode(product.getKey());
				branchRes.setBranchName(branchName);
				branchRes.setProductList(productResList);

				branchResList.add(branchRes);
			}
			;
			res.setBranchList(branchResList);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public List<RtProductRes> getProductList(RtProductReq req) {
		List<RtProductRes> resList = new ArrayList<RtProductRes>();
		try {
			List<RenewQuotePolicy> rq = renewQuotePolicyRepo.findByBranchCodeAndCompanyId(req.getBranchCode(),
					req.getCompanyId());
			Set<String> set= new HashSet<String>();
			for (RenewQuotePolicy rqp : rq) {
				RtProductRes res = new RtProductRes();
				if(set.contains(rqp.getPolProductCode())) {
					continue;
				}
				set.add(rqp.getPolProductCode());
				res.setPolProductCode(rqp.getPolProductCode());
				res.setProductName(rqp.getProductName());
				res.setBranchName(rqp.getBranchName());
				if(StringUtils.isBlank(rqp.getBranchName())) {
					res.setBranchName(getDistinctBranchNamesByCode(rqp.getBranchCode()).get(0));
				}
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}
	
	public List<String> getDistinctBranchNamesByCode(String branchCode) {
	    List<String> branchNames = new ArrayList<>();
	    try {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<String> cq = cb.createQuery(String.class);
	        Root<LoginBranchMaster> root = cq.from(LoginBranchMaster.class);

	        // Select only branchName
	        cq.select(root.get("branchName")).distinct(true);

	        // WHERE branch_code = :branchCode
	        Predicate n1 = cb.equal(root.get("branchCode"), branchCode);
	        cq.where(n1);

	        branchNames = em.createQuery(cq).getResultList();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return branchNames;
	}

	
	@Override
	public List<RenewalTrackingDetails> getAllByProductCode(RtGetProductsReq req) {
		List<RenewalTrackingDetails> resList = new ArrayList<RenewalTrackingDetails>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<RenewQuotePolicy> rqp = renewQuotePolicyRepo.findByBranchCodeAndCompanyIdAndPolProductCode(req.getBranchCode(),req.getCompanyId(),req.getProductCode());	
			if (!rqp.isEmpty()) {
				for (RenewQuotePolicy rq : rqp) {
					RenewalTrackingDetails res = new RenewalTrackingDetails();
					res.setCustomerName(rq.getCustomerName());
					res.setProdName(rq.getProductName());
					res.setPolPrem(rq.getNewPremium()!=null?rq.getNewPremium().doubleValue():null);
					res.setStatus(rq.getCurrentStatus());
					res.setPolExpDt(rq.getOldendDate());
					resList.add(res);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

}

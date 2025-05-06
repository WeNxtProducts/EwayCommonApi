package com.maan.eway.renewal.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.renewal.req.RenewalTrackAgentResByProduct2;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.DivisionDetails;
import com.maan.eway.renewal.res.PolicyDet;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.res.ProductDetails;
import com.maan.eway.renewal.res.ProductsBySourceRes;
import com.maan.eway.renewal.service.RenewalTrackingService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.RenewPremiaPolicyRepository;
import com.maan.eway.repository.RenewQuotePolicyRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
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

	public BigDecimal rate(Long count, Long totalCount) {
		if (count == null || totalCount == null || totalCount == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal countBD = BigDecimal.valueOf(count);
		BigDecimal totalCountBD = BigDecimal.valueOf(totalCount);

		return countBD.multiply(BigDecimal.valueOf(100)).divide(totalCountBD, 2, RoundingMode.HALF_UP); // returns
																										// percentage

	}	


	private Integer policyCountBySourceAndDiv(List<ProductDetails> list) {
		int totalPolicyCount = list.stream().map(ProductDetails::getProductCount) // get the string
				.filter(Objects::nonNull) // avoid nulls
				.map(String::trim) // clean strings
				.filter(s -> !s.isEmpty()) // avoid empty strings
				.mapToInt(Integer::parseInt) // convert to int
				.filter(i -> i >= 0) // only non-negative
				.sum();
		return totalPolicyCount;
	}

	
//            From division <--> products <--> Agents <--> policy details 

//  Get Divisions From companyId
	@Override
	public BranchForRenewalTrack RenewalTrackGetBranch(RenewalTrackReq req) {
		BranchForRenewalTrack res = new BranchForRenewalTrack();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DivisionDetails> cq = cb.createQuery(DivisionDetails.class);
			Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);
			
			Expression<Long> successCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RS"), 1L)
				        .otherwise(0L)
				);
				Expression<Long> pendingCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RP"), 1L)
				        .otherwise(0L)
				);
				Expression<Long> lostCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RF"), 1L)
				        .otherwise(0L)
				);

			// Select DISTINCT division_code
			cq.multiselect(root.get("divisionCode").alias("divisionCode"),
					root.get("divisionName").alias("divisionName"),
					cb.count(root).as(String.class).alias("totalPolicycount"),
					cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"),
					lostCount.as(String.class).alias("lostCount")
					);

			// WHERE company_id = '100020'
			cq.where(cb.equal(root.get("companyId"), req.getCompanyId()));
			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			cq.where(cb.between(root.get("expiryDate"), startTimestamp, endTimestamp));


			cq.groupBy(root.get("divisionCode"), root.get("divisionName"));

			// Execute query
			List<DivisionDetails> result = em.createQuery(cq).getResultList();
			res.setCompanyId(req.getCompanyId());
			if (!CollectionUtils.isEmpty(result)) {
				result.parallelStream().forEach( division-> {
					Long success=Long.parseLong(division.getSuccessCount());
					Long totCount=Long.parseLong(division.getTotalPolicycount());
					BigDecimal rate = rate(success,totCount);
					division.setSuccessRate(rate.toPlainString());
				});
				res.setDivisionDetails(result);
				res.setNoOfDivisions(String.valueOf(result.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

// get product details from division
	public List<ProductByBranch> GetRenewalDetailsByDivsion2(RenewalTrackReq req) {
		List<ProductByBranch> list = getBranchByProduct(req);
		return list;
	}

	private List<ProductByBranch> getBranchByProduct(RenewalTrackReq req) {
		List<ProductByBranch> res=new ArrayList<ProductByBranch>();
		try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductByBranch> cq = cb.createQuery(ProductByBranch.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);
		
		Expression<Long> successCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RS"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> pendingCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RP"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> lostCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RF"), 1L)
			        .otherwise(0L)
			);


		cq.select(cb.construct(ProductByBranch.class, root.get("productCode").alias("productCode"),
				root.get("productName").alias("productName"), cb.count(root).as(String.class).alias("productCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
				successCount.as(String.class).alias("successCount"),
				pendingCount.as(String.class).alias("pendingCount"),
				lostCount.as(String.class).alias("lostCount")
				));

		cq.where(cb.equal(root.get("companyId"), req.getCompanyId()), cb.equal(root.get("divisionCode"), req.getDivisionCode()));
		// Convert String to Timestamp
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
		LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

		// Convert LocalDate to Timestamp (start of day and end of day)
		Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
		Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

		// Add condition to criteria
		cq.where(cb.between(root.get("expiryDate"), startTimestamp, endTimestamp));
		cq.groupBy(root.get("productCode"), root.get("productName"));
		
		res=em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(res)) {
		res.parallelStream().forEach(product-> { 
			Long success=Long.parseLong(product.getSuccessCount());
			Long totCount=Long.parseLong(product.getProductCount());
			BigDecimal rate = rate(success,totCount);
			product.setSuccessRate(rate.toPlainString());

		});
		}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return res;
	}

// get brokers from product
	@Override
	public List<RenewalTrackAgentResByProduct2> RenewalTrackAgentRes2(RenewalTrackReq req) {
		List<RenewalTrackAgentResByProduct2> res = new ArrayList<RenewalTrackAgentResByProduct2>();
		try {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RenewalTrackAgentResByProduct2> cq = cb.createQuery(RenewalTrackAgentResByProduct2.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);
		
		Expression<Long> successCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RS"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> pendingCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RP"), 1L)
			        .otherwise(0L)
			);
			Expression<Long> lostCount = cb.sum(
			    cb.<Long>selectCase()
			        .when(cb.equal(root.get("currentStatus"), "RF"), 1L)
			        .otherwise(0L)
			);


		cq.select(cb.construct(RenewalTrackAgentResByProduct2.class, root.get("polSrcCode").alias("sourceCode"),
				root.get("polSrcName").alias("sourceName"), cb.count(root).as(String.class).alias("sourceCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
				successCount.as(String.class).alias("successCount"),
				pendingCount.as(String.class).alias("pendingCount"),
				lostCount.as(String.class).alias("lostCount")
				));

		cq.where(cb.equal(root.get("companyId"), req.getCompanyId()), cb.equal(root.get("divisionCode"), req.getDivisionCode()),
				cb.equal(root.get("productCode"), req.getProductCode()));
		// Convert String to Timestamp
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
		LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

		// Convert LocalDate to Timestamp (start of day and end of day)
		Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
		Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

		// Add condition to criteria
		cq.where(cb.between(root.get("expiryDate"), startTimestamp, endTimestamp));
		cq.groupBy(root.get("polSrcCode"), root.get("polSrcName"));
		
		res=em.createQuery(cq).getResultList();
		if(!CollectionUtils.isEmpty(res)) {
	     res.parallelStream().forEach(agent-> {
			Long success=Long.parseLong(agent.getSuccessCount());
			Long totCount=Long.parseLong(agent.getSourceCount());
			BigDecimal rate = rate(success,totCount);
			agent.setSuccessRate(rate.toPlainString());
		});
		}
		}catch(Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	
	//Get product list by a source in a division & company
	@Override
	public ProductsBySourceRes getProductsBySource(RenewalTrackReq req) {
		ProductsBySourceRes res = new ProductsBySourceRes();
		try {
			List<ProductDetails> productList = getProductsBySourceIn(req);
			res.setSourceCode(req.getSourceCode());
			res.setTotalPolicyCount(policyCountBySourceAndDiv(productList).toString());
			if (!CollectionUtils.isEmpty(productList)) {
				
				productList.parallelStream().forEach(product-> {			
					Long success=Long.parseLong(product.getSuccessCount());
					Long totCount=Long.parseLong(product.getProductCount());
					BigDecimal rate = rate(success,totCount);
					product.setSuccessRate(rate.toPlainString());
				});
				res.setProductDetails(productList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private List<ProductDetails> getProductsBySourceIn(RenewalTrackReq req) {
		List<ProductDetails> resList = new ArrayList<ProductDetails>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductDetails> cq = cb.createQuery(ProductDetails.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			Expression<Long> successCount = cb.sum(
					cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RS"), 1L).otherwise(0L)
					);
			Expression<Long> pendingCount = cb.sum(
					cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RP"), 1L).otherwise(0L)
					);
			Expression<Long> lostCount = cb.sum(
					cb.<Long>selectCase().when(cb.equal(r.get("currentStatus"), "RF"), 1L).otherwise(0L)
					);
			cq.select(cb.construct(ProductDetails.class, r.get("productCode").alias("productCode"),
					r.get("productName").alias("productName"), cb.count(r).as(String.class).alias("productCount"),
					cb.sum(r.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"),
					lostCount.as(String.class).alias("lostCount")
					));
			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()), cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					cb.equal(r.get("polSrcCode"), req.getSourceCode()));
			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			cq.where(cb.between(r.get("expiryDate"), startTimestamp, endTimestamp));

			cq.groupBy(r.get("productCode"), r.get("productName"));
			resList = em.createQuery(cq).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}


	@Override
	public List<PolicyDet> RenewalTrackPolicyDetailsBySource(RenewalTrackReq req) {
		List<PolicyDet> policyDetails = new ArrayList<PolicyDet>();
		
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(
				    PolicyDet.class,
				    r.get("transactionId").alias("transactionId"),
				    r.get("policyNumber").alias("policyNumber"),
				    r.get("expiryDate").as(String.class).alias("expiryDate"),

				    r.get("companyId").alias("companyId"),
				    r.get("companyCode").alias("companyCode"),
				    r.get("companyName").alias("companyName"),

				    r.get("classCode").alias("classCode"),
				    r.get("className").alias("className"),

				    r.get("productCode").alias("productCode"),
				    r.get("productName").alias("productName"),

				    r.get("divisionCode").alias("divisionCode"),
				    r.get("divisionName").alias("divisionName"),

				    r.get("departmentCode").alias("departmentCode"),
				    r.get("departmentName").alias("departmentName"),

				    r.get("businessType").alias("businessType"),
				    r.get("businessName").alias("businessName"),

				    r.get("endorsementNumber").alias("endorsementNumber"),
				    r.get("fromDate").as(String.class).alias("fromDate"),
				    r.get("renewalDate").as(String.class).alias("renewalDate"),

				    r.get("customerCode").alias("customerCode"),
				    r.get("customerName").alias("customerName"),
				    r.get("insuredCivilId").alias("insuredCivilId"),
				    r.get("insuredMobile").alias("insuredMobile"),
				    r.get("insuredEmailId").alias("insuredEmailId"),

				    r.get("polAssrCode").alias("polAssrCode"),
				    r.get("polAssrName").alias("polAssrName"),

				    r.get("polSrcType").alias("polSrcType"),
				    r.get("polSrcCode").alias("polSrcCode"),
				    r.get("polSrcName").alias("polSrcName"),

				    r.get("policySi").alias("policySi"),
				    r.get("grossPremium").alias("grossPremium"),
				    r.get("coverPremium").alias("coverPremium"),
				    r.get("discountPremium").alias("discountPremium"),
				    r.get("loadingPremium").alias("loadingPremium"),

				    r.get("pvtCoverYn").alias("pvtCoverYn"),
				    r.get("pvtCoverSi").alias("pvtCoverSi"),
				    r.get("pvtCoverPremium").alias("pvtCoverPremium"),

				    r.get("chargeAmount").alias("chargeAmount"),
				    r.get("totalPremium").alias("totalPremium"),
				    r.get("agBrokCommission").alias("agBrokCommission"),

				    r.get("currentStatus").alias("currentStatus"),
				    r.get("newPolicyNumber").alias("newPolicyNumber"),
				    r.get("lossReason").alias("lossReason"),
				    r.get("lossRemarks").alias("lossRemarks"),
				    r.get("competitor").alias("competitor"),

				    r.get("entryDate").as(String.class).alias("entryDate")
				));

			
			cq.where(cb.equal(r.get("companyId"), req.getCompanyId()), cb.equal(r.get("divisionCode"), req.getDivisionCode()),
					cb.equal(r.get("polSrcCode"), req.getSourceCode()));
			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			cq.where(cb.between(r.get("expiryDate"), startTimestamp, endTimestamp));

			policyDetails = em.createQuery(cq).getResultList();

		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return policyDetails;
	}
	
	// Get Approver's Division code
	
	public BranchForRenewalTrack RenewalTrackGetBranchByApprover(RenewalTrackReq req) {
		BranchForRenewalTrack res = new BranchForRenewalTrack();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DivisionDetails> cq = cb.createQuery(DivisionDetails.class);
			Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);
			
			Expression<Long> successCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RS"), 1L)
				        .otherwise(0L)
				);
				Expression<Long> pendingCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RP"), 1L)
				        .otherwise(0L)
				);
				Expression<Long> lostCount = cb.sum(
				    cb.<Long>selectCase()
				        .when(cb.equal(root.get("currentStatus"), "RF"), 1L)
				        .otherwise(0L)
				);

			// Select DISTINCT division_code
			cq.multiselect(root.get("divisionCode").alias("divisionCode"),
					root.get("divisionName").alias("divisionName"),
					cb.count(root).as(String.class).alias("totalPolicycount"),
					cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"),
					successCount.as(String.class).alias("successCount"),
					pendingCount.as(String.class).alias("pendingCount"),
					lostCount.as(String.class).alias("lostCount")
					);

			// WHERE company_id = '100020'
			cq.where(cb.equal(root.get("companyId"), req.getCompanyId()));
			// Convert String to Timestamp
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate start = LocalDate.parse(req.getStartDate(), formatter);
			LocalDate end = LocalDate.parse(req.getEndDate(), formatter);

			// Convert LocalDate to Timestamp (start of day and end of day)
			Timestamp startTimestamp = Timestamp.valueOf(start.atStartOfDay());
			Timestamp endTimestamp = Timestamp.valueOf(end.atTime(LocalTime.MAX));

			// Add condition to criteria
			cq.where(cb.between(root.get("expiryDate"), startTimestamp, endTimestamp));


			cq.groupBy(root.get("divisionCode"), root.get("divisionName"));

			// Execute query
			List<DivisionDetails> result = em.createQuery(cq).getResultList();
			res.setCompanyId(req.getCompanyId());
			if (!CollectionUtils.isEmpty(result)) {
				result.parallelStream().forEach( division-> {
					Long success=Long.parseLong(division.getSuccessCount());
					Long totCount=Long.parseLong(division.getTotalPolicycount());
					BigDecimal rate = rate(success,totCount);
					division.setSuccessRate(rate.toPlainString());
				});
				res.setDivisionDetails(result);
				res.setNoOfDivisions(String.valueOf(result.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}


}

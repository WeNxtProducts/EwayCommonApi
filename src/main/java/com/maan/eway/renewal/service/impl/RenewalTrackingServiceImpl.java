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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.maan.eway.admin.req.GetBrokerListDropDownReq;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.RenewPremiaPolicy;
import com.maan.eway.bean.RenewQuotePolicy;
import com.maan.eway.renewal.req.GetCustomersByBrokerReq;
import com.maan.eway.renewal.req.GetPolicyBySourceReq;
import com.maan.eway.renewal.req.RenewalTrackAgentResByProduct2;
import com.maan.eway.renewal.req.RenewalTrackReq;
import com.maan.eway.renewal.req.RenewalTrackingInReq;
import com.maan.eway.renewal.req.RtGetProductsReq;
import com.maan.eway.renewal.req.RtProductReq;
import com.maan.eway.renewal.res.BranchForRenewalTrack;
import com.maan.eway.renewal.res.DivisionDetails;
import com.maan.eway.renewal.res.GetBrokerListRes;
import com.maan.eway.renewal.res.GetPolicyBySourceRes;
import com.maan.eway.renewal.res.GetProductBySource;
import com.maan.eway.renewal.res.PolicyDet;
import com.maan.eway.renewal.res.ProductByBranch;
import com.maan.eway.renewal.res.ProductDetails;
import com.maan.eway.renewal.res.ProductsBySourceRes;
import com.maan.eway.renewal.res.RenewalTrackAgentResByProduct;
import com.maan.eway.renewal.res.RenewalTrackAgentResByProduct.PolicyDetail;
import com.maan.eway.renewal.res.RenewalTrackBranchByProductRes;
import com.maan.eway.renewal.res.RenewalTrackBranchRes;
import com.maan.eway.renewal.res.RenewalTrackBrokerRes;
import com.maan.eway.renewal.res.RenewalTrackByProductRes;
import com.maan.eway.renewal.res.RenewalTrackByProductResByDivision;
import com.maan.eway.renewal.res.RenewalTrackProductRes;
import com.maan.eway.renewal.res.RenewalTrackProductResByDivision;
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


	public BigDecimal rate(Long count, Long totalCount) {
		if (count == null || totalCount == null || totalCount == 0) {
			return BigDecimal.ZERO;
		}

		BigDecimal countBD = BigDecimal.valueOf(count);
		BigDecimal totalCountBD = BigDecimal.valueOf(totalCount);

		return countBD.multiply(BigDecimal.valueOf(100)).divide(totalCountBD, 2, RoundingMode.HALF_UP); // returns
																										// percentage

	}
	
// new
	@Override
	public RenewalTrackByProductResByDivision GetRenewalDetailsByDivsion(String divisionCode, String companyId) {
		RenewalTrackByProductResByDivision res = new RenewalTrackByProductResByDivision();
		List<RenewalTrackProductResByDivision> list = RenewalTrackProductRes(divisionCode, companyId);
		if (!CollectionUtils.isEmpty(list)) {
			res.setDivisionCode(divisionCode);
			res.setTotalProductCount(String.valueOf(list.size()));
			res.setTotalPolicyCount(policyCountByDivision(list).toString());
			// res.setProductList(list);
			List<RenewalTrackProductResByDivision> productList = new ArrayList<>();
			for (RenewalTrackProductResByDivision data : list) {
				RenewalTrackProductResByDivision proRes = new RenewalTrackProductResByDivision();

				proRes.setProductCode(data.getProductCode());
				proRes.setProductName(data.getProductName());
				proRes.setProductCount(data.getProductCount());
				proRes.setTotalPremium(data.getTotalPremium());
				List<RenewalTrackAgentResByProduct> agentRes = RenewalTrackAgentRes(divisionCode, companyId,
						data.getProductCode());
				if (!CollectionUtils.isEmpty(agentRes))

					for (RenewalTrackAgentResByProduct agent : agentRes) {

						List<PolicyDetail> policyDetails = RenewalTrackPolicyDetailsBySource(divisionCode, companyId,
								data.getProductCode(), agent.getSourceCode());
						if (!CollectionUtils.isEmpty(policyDetails))
							agent.setPolicyDetails(policyDetails);
					}
				proRes.setAgentList(agentRes);
				productList.add(proRes);
			}
			res.setProductList(productList);
		}

		return res;
	}

	private List<RenewalTrackProductResByDivision> RenewalTrackProductRes(String divisionCode, String companyId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RenewalTrackProductResByDivision> cq = cb.createQuery(RenewalTrackProductResByDivision.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(RenewalTrackProductResByDivision.class, root.get("productCode").alias("productCode"),
				root.get("productName").alias("productName"), cb.count(root).as(String.class).alias("productCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode));

		cq.groupBy(root.get("productCode"), root.get("productName"));

		return em.createQuery(cq).getResultList();
	}

	@Override
	public List<RenewalTrackAgentResByProduct> RenewalTrackAgentRes(String divisionCode, String companyId,
			String productCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RenewalTrackAgentResByProduct> cq = cb.createQuery(RenewalTrackAgentResByProduct.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(RenewalTrackAgentResByProduct.class, root.get("polSrcCode").alias("sourceCode"),
				root.get("polSrcName").alias("sourceName"), cb.count(root).as(String.class).alias("sourceCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode),
				cb.equal(root.get("productCode"), productCode));

		cq.groupBy(root.get("polSrcCode"), root.get("polSrcName"));

		return em.createQuery(cq).getResultList();
	}
	
	
@Override
	public List<PolicyDetail> RenewalTrackPolicyDetailsBySource(String divisionCode, String companyId,
			String productCode, String brokerCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyDetail> cq = cb.createQuery(PolicyDetail.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(PolicyDetail.class, root.get("polSrcCode").alias("sourceCode"),
				root.get("polSrcName").alias("sourceName"), root.get("productCode").alias("productCode"),
				root.get("productName").alias("productName"), root.get("divisionCode").alias("branchCode"),
				root.get("divisionName").alias("branchName"), root.get("customerCode").alias("customerCode"),
				root.get("customerName").alias("customerName"), root.get("expiryDate").alias("policyEndDate"),
				root.get("currentStatus").alias("status"), root.get("totalPremium").alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode),
				cb.equal(root.get("productCode"), productCode), cb.equal(root.get("polSrcCode"), brokerCode));

		return em.createQuery(cq).getResultList();
	}

	private Integer policyCountByDivision(List<RenewalTrackProductResByDivision> list) {
		int totalPolicyCount = list.stream().map(RenewalTrackProductResByDivision::getProductCount) // get the string
				.filter(Objects::nonNull) // avoid nulls
				.map(String::trim) // clean strings
				.filter(s -> !s.isEmpty()) // avoid empty strings
				.mapToInt(Integer::parseInt) // convert to int
				.filter(i -> i >= 0) // only non-negative
				.sum();
		return totalPolicyCount;
	}

	@Override
	public GetPolicyBySourceRes getPolicyBySource(GetPolicyBySourceReq req) {
		GetPolicyBySourceRes res = new GetPolicyBySourceRes();
		try {
			List<GetProductBySource> productList = getProductListBySource(req.getCompanyId(), req.getDivisionCode(),
					req.getSourceCode());
			res.setSourceCode(req.getSourceCode());
			res.setTotalPolicyCount(policyCountBySource(productList).toString());
			if (!CollectionUtils.isEmpty(productList)) {
				res.setNoOfProducts(String.valueOf(productList.size()));
				for (GetProductBySource product : productList) {
					List<PolicyDet> policyDetails = getPolicyListBySourceAndProduct(req.getCompanyId(),
							req.getDivisionCode(), product.getProductCode(), req.getSourceCode());
					if (!CollectionUtils.isEmpty(productList))
						product.setPolicyList(policyDetails);
				}
				res.setProdList(productList);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private List<GetProductBySource> getProductListBySource(String companyId, String divisionCode, String sourceCode) {
		List<GetProductBySource> resList = new ArrayList<GetProductBySource>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<GetProductBySource> cq = cb.createQuery(GetProductBySource.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(GetProductBySource.class, r.get("productCode").alias("productCode"),
					r.get("productName").alias("productName"), cb.count(r).as(String.class).alias("productCount"),
					cb.sum(r.get("totalPremium")).as(String.class).alias("totalPremium")));
			cq.where(cb.equal(r.get("companyId"), companyId), cb.equal(r.get("divisionCode"), divisionCode),
					cb.equal(r.get("polSrcCode"), sourceCode));
			cq.groupBy(r.get("productCode"), r.get("productName"));
			resList = em.createQuery(cq).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	private List<PolicyDet> getPolicyListBySourceAndProduct(String companyId, String divisionCode, String productCode,
			String brokerCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyDet> cq = cb.createQuery(PolicyDet.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(PolicyDet.class, root.get("polSrcCode").alias("sourceCode"),
				root.get("polSrcName").alias("sourceName"), root.get("productCode").alias("productCode"),
				root.get("productName").alias("productName"), root.get("divisionCode").alias("branchCode"),
				root.get("divisionName").alias("branchName"), root.get("customerCode").alias("customerCode"),
				root.get("customerName").alias("customerName"), root.get("expiryDate").alias("policyEndDate"),
				root.get("currentStatus").alias("status"), root.get("totalPremium").alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode),
				cb.equal(root.get("productCode"), productCode), cb.equal(root.get("polSrcCode"), brokerCode));

		return em.createQuery(cq).getResultList();
	}

	private Integer policyCountBySource(List<GetProductBySource> list) {
		int totalPolicyCount = list.stream().map(GetProductBySource::getProductCount) // get the string
				.filter(Objects::nonNull) // avoid nulls
				.map(String::trim) // clean strings
				.filter(s -> !s.isEmpty()) // avoid empty strings
				.mapToInt(Integer::parseInt) // convert to int
				.filter(i -> i >= 0) // only non-negative
				.sum();
		return totalPolicyCount;
	}

	/*
	public GetAllPolicyBySourceRes getAllPolicyBySource(GetPolicyBySourceReq req) {
		GetAllPolicyBySourceRes res = new GetAllPolicyBySourceRes();
		try {
			List<GetProductBySource> productList = getProductListBySource(req.getCompanyId(), req.getDivisionCode(),
					req.getSourceCode());
			res.setSourceCode(req.getSourceCode());
			res.setTotalPolicyCount(policyCountBySource(productList).toString());
			if (!CollectionUtils.isEmpty(productList)) {
				res.setNoOfProducts(String.valueOf(productList.size()));
				for (GetProductBySource product : productList) {
					List<PolicyDet> policyDetails = getPolicyListBySourceAndProduct(req.getCompanyId(),
							req.getDivisionCode(), product.getProductCode(), req.getSourceCode());
					if (!CollectionUtils.isEmpty(productList))
						product.setPolicyList(policyDetails);
				}
				res.setProdList(productList);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	*/
	
	public List<ProductByBranch> GetRenewalDetailsByDivsion2(String divisionCode, String companyId) {
		List<ProductByBranch> list = getBranchByProduct(divisionCode, companyId);
		return list;
	}
	
	private List<ProductByBranch> getBranchByProduct(String divisionCode, String companyId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductByBranch> cq = cb.createQuery(ProductByBranch.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(ProductByBranch.class, root.get("productCode").alias("productCode"),
				root.get("productName").alias("productName"), cb.count(root).as(String.class).alias("productCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode));

		cq.groupBy(root.get("productCode"), root.get("productName"));

		return em.createQuery(cq).getResultList();
	}


	@Override
	 public BranchForRenewalTrack RenewalTrackGetBranch(String companyId) {
		 BranchForRenewalTrack res = new BranchForRenewalTrack();
		 try {
			 CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<DivisionDetails> cq = cb.createQuery(DivisionDetails.class);
			 Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

			 // Select DISTINCT division_code
			 cq.multiselect(root.get("divisionCode").alias("divisionCode"),
					 root.get("divisionName").alias("divisionName"),
					 cb.count(root).as(String.class).alias("totalPolicycount"),
					 cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium"));

			 // WHERE company_id = '100020'
			 cq.where(cb.equal(root.get("companyId"), companyId));
			 
			 cq.groupBy(root.get("divisionCode"),root.get("divisionName"));

			 // Execute query
			 List<DivisionDetails> result = em.createQuery(cq).getResultList();
             res.setCompanyId(companyId);
             if(!CollectionUtils.isEmpty(result)) {
             res.setDivisionDetails(result);
             res.setNoOfDivisions(String.valueOf(result.size()));             };
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
		 return res;
	 }
	
	@Override
	public List<RenewalTrackAgentResByProduct2> RenewalTrackAgentRes2(String divisionCode, String companyId,
			String productCode) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RenewalTrackAgentResByProduct2> cq = cb.createQuery(RenewalTrackAgentResByProduct2.class);
		Root<RenewPremiaPolicy> root = cq.from(RenewPremiaPolicy.class);

		cq.select(cb.construct(RenewalTrackAgentResByProduct2.class, root.get("polSrcCode").alias("sourceCode"),
				root.get("polSrcName").alias("sourceName"), cb.count(root).as(String.class).alias("sourceCount"),
				cb.sum(root.get("totalPremium")).as(String.class).alias("totalPremium")));

		cq.where(cb.equal(root.get("companyId"), companyId), cb.equal(root.get("divisionCode"), divisionCode),
				cb.equal(root.get("productCode"), productCode));

		cq.groupBy(root.get("polSrcCode"), root.get("polSrcName"));

		return em.createQuery(cq).getResultList();
	}
	
@Override
	public ProductsBySourceRes getProductsBySource(RenewalTrackReq req) {
		ProductsBySourceRes res = new ProductsBySourceRes();
		try {
			List<ProductDetails> productList = getProductsBySource(req.getCompanyId(), req.getDivisionCode(),
					req.getSourceCode());
			res.setSourceCode(req.getSourceCode());
			res.setTotalPolicyCount(policyCountBySourceAndDiv(productList).toString());
			if (!CollectionUtils.isEmpty(productList)) {
				res.setProductDetails(productList);		
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private List<ProductDetails> getProductsBySource(String companyId, String divisionCode, String sourceCode) {
		List<ProductDetails> resList = new ArrayList<ProductDetails>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductDetails> cq = cb.createQuery(ProductDetails.class);
			Root<RenewPremiaPolicy> r = cq.from(RenewPremiaPolicy.class);

			cq.select(cb.construct(ProductDetails.class, r.get("productCode").alias("productCode"),
					r.get("productName").alias("productName"), cb.count(r).as(String.class).alias("productCount"),
					cb.sum(r.get("totalPremium")).as(String.class).alias("totalPremium")));
			cq.where(cb.equal(r.get("companyId"), companyId), cb.equal(r.get("divisionCode"), divisionCode),
					cb.equal(r.get("polSrcCode"), sourceCode));
			cq.groupBy(r.get("productCode"), r.get("productName"));
			resList = em.createQuery(cq).getResultList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
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

}

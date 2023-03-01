package com.maan.eway.endorsment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.service.impl.GridServiceImpl;
import com.maan.eway.endorsment.request.EndorsementType;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.EndtMaster;
import com.maan.eway.endorsment.util.QuoteInfoUtil;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

@Service
public class EndorsementService {

	@Autowired
	private HomePositionMasterRepository hpmrepo;
	@Autowired
	private GridServiceImpl copyquoteService;
	
	@Autowired
	private QuoteInfoUtil quoteutil;
	
	@Autowired
	private PolicyCoverDataRepository pcdRepo;
	
	public CommonRes cancelPolicy(Endorsment request) {
		try {

			HomePositionMaster hp=hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P",request.getCompanyId(),Integer.valueOf(request.getProductId().intValue()));

			CopyQuoteReq c= new CopyQuoteReq();
					c.setRequestReferenceNo(hp.getRequestReferenceNo());
					c.setLoginId(hp.getLoginId());
					c.setInsuranceId(hp.getCompanyId());
					c.setBranchCode(hp.getBranchCode());
					c.setProductId(String.valueOf(hp.getProductId()));
					c.setUserType("Broker");
					c.setEndtTypeId("42");
					c.setTypeId("Endt");
					c.setQuoteNo(hp.getQuoteNo());
					

			CopyQuoteSuccessRes copyQuote = copyquoteService.copyQuote(c);
			;

			List<PolicyCoverData> covers = quoteutil.getFromPolicyCoverData(copyQuote.getQuoteNo());

			List<PolicyCoverData> distinctsVehicle = covers.stream()
					.filter(distinctByKey(cust -> cust.getVehicleId()))
					.collect(Collectors.toList());
			for(PolicyCoverData v  :distinctsVehicle) {

				List<PolicyCoverData> distinctsCovers = covers.stream()
						.filter(p-> p.getVehicleId().equals(v.getVehicleId()) )
						.filter(distinctByKey(cust -> cust.getCoverId() ))
						.collect(Collectors.toList());
				for(PolicyCoverData dc:distinctsCovers) {
					List<PolicyCoverData> cover = covers.stream()
							.filter(p-> p.getVehicleId().equals(v.getVehicleId()) )
							.filter(cust -> cust.getCoverId().equals(dc.getCoverId()) )
							.collect(Collectors.toList());
					
					List<PolicyCoverData> basecov = cover.stream().filter(cc-> cc.getDiscLoadId()==0 && !cc.getCoverageType().equals("T")).collect(Collectors.toList());
					//mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
					basecov.get(0).setPremiumAfterDiscountFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumAfterDiscountFc().doubleValue()).sum()));
					basecov.get(0).setPremiumAfterDiscountLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumAfterDiscountLc().doubleValue()).sum()));
					basecov.get(0).setPremiumBeforeDiscountFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumBeforeDiscountFc().doubleValue()).sum()));
					basecov.get(0).setPremiumBeforeDiscountLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumBeforeDiscountLc().doubleValue()).sum()));
					basecov.get(0).setPremiumExcludedTaxFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumExcludedTaxFc().doubleValue()).sum()));
					basecov.get(0).setPremiumExcludedTaxLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumExcludedTaxLc().doubleValue()).sum()));
					basecov.get(0).setPremiumIncludedTaxFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumIncludedTaxFc().doubleValue()).sum()));
					basecov.get(0).setPremiumIncludedTaxLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumIncludedTaxLc().doubleValue()).sum()));
					covers.add(basecov.get(0));
				}
			}
			pcdRepo.saveAllAndFlush(covers);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public  <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	public EndtMaster getEndorsementTypes(Endorsment request) {
		try {
			
			List<EndtTypeMaster> m = endtTypeRepo.findByCompanyIdAndProductIdAndStatusOrderByPriorityAsc(request.getCompanyId(),Integer.valueOf(request.getProductId().intValue()),"Y");
			
			List<EndorsementType> ets=new ArrayList<EndorsementType>(); 
			for(EndtTypeMaster ent:m) {
				
				String fieldAsString = ent.getEndtDependantFields();
				List<String> fields=null;
				if(StringUtils.isNotBlank(fieldAsString)) {
					 fields=new ArrayList<String>();
					 if(fieldAsString.indexOf(",")!=-1) {
						 String[] split = fieldAsString.split(",");
						 fields= Arrays.asList(split);
					 }else {
						 fields.add(fieldAsString);
					 }
				}
				
				EndorsementType e = EndorsementType.builder()
						.endorsementCategory(new BigDecimal(ent.getEndtTypeCategoryId()))
						.endorsementCategoryDesc(ent.getEndtTypeCategory())
						.endorsementDesc(ent.getEndtTypeDesc())
						.endtType(new BigDecimal(ent.getEndtTypeId()))
						.fieldsAllowed(fields)
						.build();
				
				ets.add(e);
			}
			EndtMaster endt=EndtMaster.builder().endorsementTypes(ets).build();
			return endt;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;
	
	
	public List<QuoteCriteriaRes> endorsementPendingData(Endorsment request) {

		 try {


				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

				// Select
				query.multiselect(cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate")
						
						);
				

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("endorsementDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.equal(m.get("originalPolicyNo"), request.getPolicyNo());
				//Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				//Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

			/*	Predicate n7 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n7 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}*/

				/*Predicate n8 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n8 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n8 = e0.in(branches);
				}*/

				query.where(n1, n2, n3, n4, n5 )
						/*.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))*/
						.orderBy(orderList);

				// Get Result
				TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<QuoteCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	 
}


package com.maan.eway.endorsment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.GridServiceImpl;
import com.maan.eway.common.service.impl.PaymentServiceImpl;
import com.maan.eway.endorsment.request.EndorsementType;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.EndtMaster;
import com.maan.eway.endorsment.util.CopyBuildingRaw;
import com.maan.eway.endorsment.util.CopyCommonRaw;
import com.maan.eway.endorsment.util.CopyRawTable;
import com.maan.eway.endorsment.util.CopyTravelRaw;
import com.maan.eway.endorsment.util.QuoteInfoUtil;
import com.maan.eway.repository.EndtDependantFieldsMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.service.FactorRateRequestDetailsService;

@Service
public class EndorsementService {

	@Autowired
	private HomePositionMasterRepository hpmrepo;
	@Autowired
	private GridServiceImpl copyquoteService;
	@Autowired
	private PaymentServiceImpl paymentServiceImpl;
	
	@Autowired
	private QuoteInfoUtil quoteutil;
	
	@Autowired
	private PolicyCoverDataRepository pcdRepo;
	
	@Autowired
	private CopyRawTable copyraw;
	
	@Autowired
	private CopyBuildingRaw copyBuildingraw;
	
	@Autowired
	private CopyTravelRaw copyTravelraw;
	
	@Autowired
	private CopyCommonRaw copyCommonraw;


	@Autowired
	private EndtDependantFieldsMasterRepository dependantRepo;
	
	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	
	@Autowired
	private  FactorRateRequestDetailsService factorService;
	@Autowired
	private  QuoteService entityService ;
	public CommonRes cancelPolicy(Endorsment request) {
		try {

			/*HomePositionMaster hp=hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(
			 * request.getPolicyNo(),
			 * "P",
			 * request.getCompanyId(),
			 * Integer.valueOf(request.getProductId().intValue())
			 * );

			CopyQuoteReq c= new CopyQuoteReq();
					c.setRequestReferenceNo(hp.getRequestReferenceNo());
					c.setLoginId(hp.getLoginId());
					c.setApplicationId(hp.getApplicationId());
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
			pcdRepo.saveAllAndFlush(covers);*/
			FactorRateDetailsGetReq viewCalcReq=new FactorRateDetailsGetReq();
			viewCalcReq.setProductId(request.getProductId().toPlainString());
			viewCalcReq.setRequestReferenceNo(request.getRequestReferenceNo());
			List<EservieMotorDetailsViewRes> viewCalc = factorService.getFactorRateRequestDetails(viewCalcReq, "");
			
			List<VehicleIdsReq> vehicles=new ArrayList<VehicleIdsReq>();
			for (EservieMotorDetailsViewRes motors : viewCalc) {
				
				VehicleIdsReq v=new VehicleIdsReq();
				
				v.setVehicleId(Integer.parseInt(motors.getVehicleId()));
				
				
				List<Cover> coverList = motors.getCoverList();
				List<Cover> distinctSections = coverList.stream().filter(distinctByKey(c->c.getSectionId())).collect(Collectors.toList());
				
				for (Cover ds : distinctSections) {
					v.setSectionId(ds.getSectionId());
					List<CoverIdsReq> covers=new ArrayList<CoverIdsReq>();				
					for (Cover cover : coverList) {

						if("Y".equals(cover.getUserOpt()) && ds.getSectionId().equals(cover.getSectionId())) {
							String isSubCover = cover.getIsSubCover();
							
							if("Y".equals(isSubCover)) {
								List<Cover> subcovers = cover.getSubcovers().stream().filter(f-> "Y".equals(f.getUserOpt())).collect(Collectors.toList());
								for (Cover c : subcovers) {
									CoverIdsReq r=new CoverIdsReq();
									r.setSubCoverYn(isSubCover);
									r.setCoverId(Integer.parseInt(c.getCoverId()));
									r.setSubCoverId(c.getSubCoverId());
									covers.add(r);
								}
							}else {
								CoverIdsReq r=new CoverIdsReq();						
								
								r.setSubCoverYn(isSubCover);
								r.setCoverId(Integer.parseInt(cover.getCoverId()));
								r.setSubCoverId(null);
								covers.add(r);
							}
							
						}
					}
					v.setCoverIdList(covers);
				}
				vehicles.add(v);
				
				
			}  
			NewQuoteReq newq=new NewQuoteReq();
			 
			newq.setCreatedBy(request.getCreatedBy());
			newq.setManualReferralYn("N");
			newq.setProductId(request.getProductId().toPlainString());
			newq.setReferralRemarks("");
			newq.setRequestReferenceNo(request.getRequestReferenceNo());
			newq.setSectionId(null);
			newq.setVehicleIdsList(vehicles);
			CommonRes generateNewQuote = entityService.generateNewQuote(newq);
			
			if(!generateNewQuote.getIsError()) {
				NewQuoteRes view=(NewQuoteRes) generateNewQuote.getCommonResponse();
				ViewQuoteReq requestView=new ViewQuoteReq();
				requestView.setQuoteNo(view.getQuoteNo());
				ViewQuoteRes viewQuoteDetails = entityService.viewQuoteDetails(requestView);
				generateNewQuote.setCommonResponse(viewQuoteDetails);
			}
			return generateNewQuote;
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
			
			
			List<EndtTypeMaster> m = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualOrderByPriorityAsc(request.getCompanyId(),Integer.valueOf(request.getProductId().intValue()),"Y",new Date(),new Date());
			
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
	private EndtTypeMaster entMaster;
	
	
	public List<EndorsementCriteriaRes> endorsementPendingData(Endorsment request) {

		 try {

			 
				  List<EndorsementCriteriaRes> grids = new ArrayList<EndorsementCriteriaRes>();
				  
				if( request.getProductId().equals(new BigDecimal(motorProductId))  ) {
					 grids = endorsementMotorGrid(request);
					
					
				} else if ( request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					grids = copyTravelraw.endorsementTravelGrid(request);
				
					
				} else if ( request.getProductId().equals(new BigDecimal(buildingProductId)) || request.getProductId().equals(new BigDecimal(smeProductId))  ) {
					grids = copyBuildingraw.endorsementBuildingGrid(request);
				
					
				} else {
					
					grids = copyCommonraw.endorsementCommonGrid(request);
				
				
				}
					
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	
	public List<EndorsementCriteriaRes> endorsementMotorGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
				//Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						
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
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("endorsementType").alias("endorsementTypeId"),
						m.get("endorsementTypeDesc").alias("endorsementDesc"),
						m.get("endtCategDesc").alias("endorsementCategoryDesc"),
						m.get("endorsementEffdate").alias("effectiveDate"),
						m.get("endtStatus").alias("endorsementStatus"),
						m.get("policyNo").alias("policyNo"),
						m.get("endorsementRemarks").alias("endorsementRemarks"),
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						/*m.get("overallPremiumFc").alias("endtPremium"),*/  m.get("currency").alias("currency")
							);
			 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("endorsementDate")));

			
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),cb.like(m.get("policyNo"), request.getPolicyNo()));
				//Predicate n6 = cb.equal(m.get("riskId"), "1");
				//Predicate n7 = cb.like(h.get("quoteNo"), m.get("quoteNo"));
				//Predicate n8 = cb.like(m.get("PolicyNo"), request.getPolicyNo());
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

				query.where(n1, n2, n3, n4, n5)
						/*.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))*/
				
				.groupBy(/*m.get("overallPremiumLc"),m.get("overallPremiumFc"),*/m.get("policyNo"))
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	public List<EndorsementCriteriaRes> endorsementGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
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
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("endorsementType").alias("endorsementTypeId"),
						m.get("endorsementTypeDesc").alias("endorsementDesc"),
						m.get("endtCategDesc").alias("endorsementCategoryDesc"),
						m.get("endorsementEffdate").alias("effectiveDate"),
						m.get("endtStatus").alias("endorsementStatus"),
						m.get("policyNo").alias("policyNo"),
						m.get("endorsementRemarks").alias("endorsementRemarks")
						
						);
			 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("endorsementDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.like(m.get("originalPolicyNo"), request.getPolicyNo());
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
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	public CommonRes createEndorsment(Endorsment request) {
		try {
			//EndtTypeMaster entTypeMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(request.getCompanyId(), request.getProductId().intValue(), "Y",Integer.parseInt(request.getEndtType()),new Date(), new Date());
			EndtTypeMaster entTypeMaster =ratingutil.getEndtMasterData(request.getCompanyId(),request.getProductId().toPlainString(), request.getEndtType());
			if("42".equals(request.getEndtType())) {
				CommonRes cancelPolicy = cancelPolicy(request);	
				return cancelPolicy;
			}else if ("1".equals(entTypeMaster.getEndtTypeCategoryId().toString()) ) {
				Object response = null ;
				HomePositionMaster hp = hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P", request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
				if(hp!=null) {
				CopyQuoteReq c = new CopyQuoteReq();
				c.setRequestReferenceNo(hp.getRequestReferenceNo());
				c.setLoginId(hp.getLoginId());
				c.setApplicationId(hp.getApplicationId());
				c.setInsuranceId(hp.getCompanyId());
				c.setBranchCode(hp.getBranchCode());
				c.setProductId(String.valueOf(hp.getProductId()));
				c.setUserType("Broker");
				c.setEndtTypeId(request.getEndtType());
				c.setTypeId("Endt");
				c.setQuoteNo(hp.getQuoteNo());
				c.setPolicyNo(request.getPolicyNo());
				c.setEndtRemarks(request.getEndtRemarks());
				c.setEndtEffectiveDate(request.getEndtEffectiveDate());

				if (request.getProductId().equals(new BigDecimal(motorProductId))) {
					List<EserviceMotorDetails> copyQuote = new ArrayList<EserviceMotorDetails>();
					copyQuote.add((EserviceMotorDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = copyQuote;
				} else if ( request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelCopyQuote = new ArrayList<EserviceTravelDetails>(); 
					travelCopyQuote.add(copyTravelraw.copyTravelRaw(request));
					response = travelCopyQuote ;
					
				}else if ( request.getProductId().equals(new BigDecimal(buildingProductId)) || request.getProductId().equals(new BigDecimal(smeProductId))  ) {
					List<EserviceBuildingDetails> buildcopyquote = new ArrayList<EserviceBuildingDetails>(); 
					buildcopyquote.add((EserviceBuildingDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = buildcopyquote ;
					
				} /*else {
					List<EserviceCommonDetails> commonCopyQuote = new ArrayList<EserviceCommonDetails>();
					commonCopyQuote.add((EserviceCommonDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = commonCopyQuote ;
				
				}*/
				
				CommonRes com=new CommonRes();
				com.setCommonResponse(response);
				com.setErroCode(0);
				com.setIsError(false);
				com.setMessage("Success");
				return com;
				}
			}else {
				Object response = null ;
				
				if( request.getProductId().equals(new BigDecimal(motorProductId))  ) {
					List<EserviceMotorDetails> motorRaw = copyraw.copyMotorRaw(request,entTypeMaster);
					response = motorRaw ;
					
				} else if ( request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelRaw = new ArrayList<EserviceTravelDetails>(); 
					travelRaw.add(copyTravelraw.copyTravelRaw(request));
					response = travelRaw ;
					
				} else if ( request.getProductId().equals(new BigDecimal(buildingProductId)) || request.getProductId().equals(new BigDecimal(smeProductId))  ) {
					List<EserviceBuildingDetails> buildRaw = new ArrayList<EserviceBuildingDetails>(); 
					buildRaw.add( copyBuildingraw.copyBuildingRaw(request));
					response = buildRaw ;
					
				} else {
					List<EserviceCommonDetails> commonRaw = new ArrayList<EserviceCommonDetails>();
					 commonRaw.add(copyCommonraw.copyCommonRaw(request));
					response = commonRaw ;
				
				}
				
				
				CommonRes c=new CommonRes();
				c.setCommonResponse(response);
				c.setErroCode(0);
				c.setIsError(false);
				c.setMessage("Success");
				return c;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public CommonRes changeEndtStatus(ChangeEndoStatusReq req) {

		try {
			HomePositionMaster data=hpmrepo.findByQuoteNo(req.getQuoteNo());
			if ("Financial".equalsIgnoreCase(data.getEndtCategDesc())) {
				// Update Home Posion Master
				if (StringUtils.isNotBlank(data.getEndtTypeId()))
					data.setEndtStatus("C");
					hpmrepo.saveAndFlush(data);
					// Update ProductWise
					paymentServiceImpl.updateProductWisePolicyNo(req.getProductId().toString(), data.getPolicyNo(),
						req.getQuoteNo(), data.getEndtTypeId());
			}
			Object res = null ;
			if (req.getProductId().equals(motorProductId)) {
				EserviceMotorDetails motorEndtStatus = copyraw.eserviceMotorEndtStatus(req);
				res = motorEndtStatus ;
			} else if (req.getProductId().equals(buildingProductId)|| req.getProductId().equals(smeProductId)) {
				List<EserviceBuildingDetails> buildEndtStatus = new ArrayList<EserviceBuildingDetails>();
				buildEndtStatus.add( copyBuildingraw.buildingRawEndtStatus(req));
				res = buildEndtStatus ;
			}
			CommonRes c=new CommonRes();
			c.setCommonResponse(res);
			c.setErroCode(0);
			c.setIsError(false);
			c.setMessage("Success");
			return c;
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}


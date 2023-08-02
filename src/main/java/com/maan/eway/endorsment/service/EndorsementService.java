
package com.maan.eway.endorsment.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javax.persistence.criteria.CriteriaUpdate;
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

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.EndtSectionListReq;
import com.maan.eway.common.req.EndtSectionSaveReq;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.QuoteThreadReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.res.EndtSectionListRes;
import com.maan.eway.common.res.EndtSectionsRes;
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
import com.maan.eway.error.Error;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtDependantFieldsMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.res.SuccessRes;
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
	private EServiceSectionDetailsRepository eserSecRepo ; 

	@Autowired
	private HomePositionMasterRepository homeRepo ; 

	@Autowired
	private EndtDependantFieldsMasterRepository dependantRepo;
	
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuldingRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private  FactorRateRequestDetailsService factorService;
	@Autowired
	private  QuoteService entityService ;
	
	private Logger log = LogManager.getLogger(EndorsementService.class);
	
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
			
			
			List<EndtTypeMaster> m =ratingutil.getEndtMasterDatas(request.getCompanyId(),request.getProductId().toPlainString());

					// endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualOrderByPriorityAsc(request.getCompanyId(),Integer.valueOf(request.getProductId().intValue()),"Y",new Date(),new Date());
			
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
						.sectionModificationYn(ent.getSectionModificationYn())
						.sectionModificationType(ent.getSectionModificationType())
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
				  CompanyProductMaster product =  getCompanyProductMasterDropdown(request.getCompanyId() , request.getProductId().toString());

				if(product.getMotorYn().equalsIgnoreCase("M") ) {
					 grids = endorsementMotorGrid(request);
					
					
				} else if (product.getMotorYn().equalsIgnoreCase("H")  && request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					grids = copyTravelraw.endorsementTravelGrid(request);
				
					
				} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
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
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"), cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("policyStartDate"), cb.max(m.get("policyEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						//Home Position Master
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.sum(h.get("endtPremium")).alias("endtPremium"),cb.max( m.get("currency")).alias("currency")
						
						);
			 
				// Order By
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc((m.get("policyNo"))));

			
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
		//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D"));  // m.get("status").in("E","P"));
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
				Predicate n6 = cb.equal(h.get("quoteNo"), m.get("quoteNo"));
				query.where(n1, n2, n3,/* n4,*/ n5,n6)
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
			CompanyProductMaster product =  getCompanyProductMasterDropdown(request.getCompanyId() , request.getProductId().toString());
			HomePositionMaster hp = hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P", request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
			
			
			if(!((hp.getInceptionDate().compareTo(request.getEndtEffectiveDate()) * request.getEndtEffectiveDate().compareTo(hp.getExpiryDate()) ) >=0) ) {
				CommonRes com=new CommonRes();
				//com.setCommonResponse();
				com.setErroCode(0);
				com.setIsError(true);
				com.setMessage("invalid Data ,Unable todo Endorsement");
				return com;
			}
				
			//EndtTypeMaster entTypeMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(request.getCompanyId(), request.getProductId().intValue(), "Y",Integer.parseInt(request.getEndtType()),new Date(), new Date());
			EndtTypeMaster entTypeMaster =ratingutil.getEndtMasterData(request.getCompanyId(),request.getProductId().toPlainString(), request.getEndtType());
			if("42".equals(request.getEndtType())) {
				CommonRes cancelPolicy = cancelPolicy(request);	
				return cancelPolicy;
			}else if ("1".equals(entTypeMaster.getEndtTypeCategoryId().toString()) ) {
				Object response = null ;
				
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

				if (product.getMotorYn().equalsIgnoreCase("M") ) {
					List<EserviceMotorDetails> copyQuote = new ArrayList<EserviceMotorDetails>();
					copyQuote.add((EserviceMotorDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = copyQuote;
					updateTermsAndCondition(hp , (copyQuote.size() > 0 ?  copyQuote.get(0).getRequestReferenceNo() : "" ));
					
				} else if (product.getMotorYn().equalsIgnoreCase("H")  &&  request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelCopyQuote = new ArrayList<EserviceTravelDetails>(); 
					travelCopyQuote.add((EserviceTravelDetails)copyquoteService.copyQuote(c).getCommonResponse());
					response = travelCopyQuote ;
					updateTermsAndCondition(hp , (travelCopyQuote.size() > 0 ?  travelCopyQuote.get(0).getRequestReferenceNo() : "" ));
					
				}else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					List<EserviceBuildingDetails> buildcopyquote = new ArrayList<EserviceBuildingDetails>(); 
					buildcopyquote.add((EserviceBuildingDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = buildcopyquote ;
					updateTermsAndCondition(hp , (buildcopyquote.size() > 0 ?  buildcopyquote.get(0).getRequestReferenceNo() : "" ));
					
				} else {
					List<EserviceCommonDetails> commonCopyQuote = new ArrayList<EserviceCommonDetails>();
					commonCopyQuote.add((EserviceCommonDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = commonCopyQuote ;
					updateTermsAndCondition(hp , (commonCopyQuote.size() > 0 ?  commonCopyQuote.get(0).getRequestReferenceNo() : "" ));
				
				}
				
				
				
				CommonRes com=new CommonRes();
				com.setCommonResponse(response);
				com.setErroCode(0);
				com.setIsError(false);
				com.setMessage("Success");
				return com;
				}
			}else {
				Object response = null ;
				
				if(product.getMotorYn().equalsIgnoreCase("M") ) {
					List<EserviceMotorDetails> motorRaw = copyraw.copyMotorRaw(request,entTypeMaster);
					response = motorRaw ;
					updateTermsAndCondition(hp , (motorRaw.size() > 0 ?  motorRaw.get(0).getRequestReferenceNo() : "" ));
				} else if (product.getMotorYn().equalsIgnoreCase("H")  && request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelRaw = new ArrayList<EserviceTravelDetails>(); 
					travelRaw.add(copyTravelraw.copyTravelRaw(request));
					response = travelRaw ;
					updateTermsAndCondition(hp , (travelRaw.size() > 0 ?  travelRaw.get(0).getRequestReferenceNo() : "" ));
					
				} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					List<EserviceBuildingDetails> buildRaw = new ArrayList<EserviceBuildingDetails>(); 
					buildRaw.add( copyBuildingraw.copyBuildingRaw(request));
					response = buildRaw ;
					updateTermsAndCondition(hp , (buildRaw.size() > 0 ?  buildRaw.get(0).getRequestReferenceNo() : "" ));
					
				} else {
					List<EserviceCommonDetails> commonRaw = new ArrayList<EserviceCommonDetails>();
					 commonRaw.add(copyCommonraw.copyCommonRaw(request));
					response = commonRaw ;
					updateTermsAndCondition(hp , (commonRaw.size() > 0 ?  commonRaw.get(0).getRequestReferenceNo() : "" ) );
				
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
	
	private void updateTermsAndCondition(HomePositionMaster hp , String newRefNo ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();  
		try {
			//Insert Terms and Condtiond
			// Find Latest Record
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<TermsAndCondition> query2 = cb2.createQuery(TermsAndCondition.class);

			// Find All
			Root<TermsAndCondition> b2 = query2.from(TermsAndCondition.class);

			// Select
			query2.select(b2);
			
			// Where
			Predicate n4 = cb2.equal(b2.get("quoteNo"), hp.getQuoteNo());
			Predicate n5 = cb2.equal(b2.get("companyId"),   hp.getCompanyId());
			Predicate n6 = cb2.equal(b2.get("productId"), hp.getProductId());
			
			query2.where(n4,n5,n6);

			// Get Result
			TypedQuery<TermsAndCondition> result2 = em.createQuery(query2);
			List<TermsAndCondition> list2 = result2.getResultList();
			if(list2.size() > 0 ){
				list2.forEach( o -> {
					TermsAndCondition newTerm = new TermsAndCondition(); 
					dozerMapper.map(o, newTerm)			;
					newTerm.setRequestReferenceNo(newRefNo);
					
				});	
		  }
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			
		}
	}

	public CommonRes changeEndtStatus(ChangeEndoStatusReq req) {

		try {
			HomePositionMaster data=hpmrepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , req.getProductId().toString());

			if ("Financial".equalsIgnoreCase(data.getEndtCategDesc())) {
				// Update Home Posion Master
				if (StringUtils.isNotBlank(data.getEndtTypeId()))
					data.setEndtStatus("C");
					
					hpmrepo.saveAndFlush(data);
					// Update ProductWise
					paymentServiceImpl.updateProductWisePolicyNo(req.getProductId().toString(), data.getPolicyNo(),
						req.getQuoteNo(), data.getEndtTypeId(),product.getMotorYn());
			}
			Object res = null ;
			if (product.getMotorYn().equalsIgnoreCase("M") ) {
				EserviceMotorDetails motorEndtStatus = copyraw.eserviceMotorEndtStatus(req);
				res = motorEndtStatus ;
			} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
				List<EserviceBuildingDetails> buildEndtStatus = new ArrayList<EserviceBuildingDetails>();
				buildEndtStatus.add( copyBuildingraw.buildingRawEndtStatus(req));
				res = buildEndtStatus ;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				List<EserviceTravelDetails> travelEndtStatus = new ArrayList<EserviceTravelDetails>();
				travelEndtStatus.add( copyTravelraw.travelRawEndtStatus(req));
				res=travelEndtStatus;
			}else { 
				List<EserviceCommonDetails> comonEndtStatus = new ArrayList<EserviceCommonDetails>();
				comonEndtStatus.add( copyCommonraw.commonRawEndtStatus(req));
				res = comonEndtStatus ;
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
			product = list.size() > 0 ? list.get(0) :null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return product;
	}
	
	
	
	
	public CommonRes getSectionList(EndtSectionListReq req) {
		CommonRes commonRes = new CommonRes();
		try {
			// Find Data
			List<ProductSectionMaster> secList = getProductSectionDropdown(req.getInsuranceId() , req.getProductId() ) ;
			List<EserviceSectionDetails> sectionDatas = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			
			// Opted Sections
			List<EndtSectionsRes> optedResList = new ArrayList<EndtSectionsRes>() ;
	
			sectionDatas.forEach( sec ->  {
				EndtSectionsRes secRes = new EndtSectionsRes();
				secRes.setSectionId(sec.getSectionId());
				secRes.setSectionName(sec.getSectionName());
				secRes.setProductType(sec.getProductType());
				String viewOrEdit = "";
				viewOrEdit = StringUtils.isBlank(sec.getSectionEndtModification()) || sec.getSectionEndtModification().equalsIgnoreCase("None") || sec.getSectionEndtModification().equalsIgnoreCase("Removed")
						? "View"  : "Edit" ;
				secRes.setViewOrEdit(viewOrEdit);
				secRes.setModificationType(sec.getSectionEndtModification());
				optedResList.add(secRes);
				
			} );
			
			
			// NonOpted Sections Fiter Based on Opted Section
			List<EndtSectionsRes> nonoptedResList = new ArrayList<EndtSectionsRes>();
			List<ProductSectionMaster> filterNonOptedSections = secList.stream().filter( sec -> sectionDatas.stream().map( EserviceSectionDetails :: getSectionId  ).anyMatch(  
					eser ->  eser.equalsIgnoreCase( sec.getSectionId().toString()) )).collect(Collectors.toList());
			List<ProductSectionMaster> fiterNonOptedSecList = secList ; 
			fiterNonOptedSecList.removeAll(filterNonOptedSections) ; 
			 
			fiterNonOptedSecList.forEach( sec ->  {
				EndtSectionsRes secRes = new EndtSectionsRes();
				secRes.setSectionId(sec.getSectionId().toString());
				secRes.setSectionName(sec.getSectionName());
				secRes.setProductType(sec.getMotorYn());
				nonoptedResList.add(secRes);
			} );
			
			
//			// Endt Sections
//			List<EndtSectionsRes> endtSecResList = new  ArrayList<EndtSectionsRes>();
//			List<EserviceSectionDetails> filterEndtSections = sectionDatas.stream().filter( o -> o.getSectionEndtModification() != null 
//					&& ! o.getSectionEndtModification().equalsIgnoreCase("None") ).collect( Collectors.toList());
//			
//			filterEndtSections.forEach( sec ->  {
//				EndtSectionsRes secRes = new EndtSectionsRes();
//				secRes.setSectionId(sec.getSectionId());
//				secRes.setSectionName(sec.getSectionName());
//				secRes.setProductType(sec.getProductType());
//				endtSecResList.add(secRes);
//			} );
			
			// Response 
			EndtSectionListRes  sectionRes = new EndtSectionListRes(); 
			sectionRes.setOptedSections(optedResList);
			sectionRes.setNonoptedSections(nonoptedResList);
		//	sectionRes.setEndtSections(endtSecResList);
			
			commonRes.setCommonResponse(sectionRes);
			commonRes.setErroCode(0);
			commonRes.setIsError(false);
			commonRes.setMessage("Success");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return commonRes;
	}
	
	
	public List<ProductSectionMaster> getProductSectionDropdown(String companyId, String productId) {
		List<ProductSectionMaster> sectionList = new ArrayList<ProductSectionMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
		
			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("sectionName")));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ProductSectionMaster> ocpm1 = effectiveDate.from(ProductSectionMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4);

			// Effective Date End
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ProductSectionMaster> ocpm2 = effectiveDate2.from(ProductSectionMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			javax.persistence.criteria.Predicate a5 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));

			javax.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8);

			// Where
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"), companyId);
			javax.persistence.criteria.Predicate n5 = cb.equal(c.get("productId"), productId);
		//	Predicate n6 = cb.equal(c.get("sectionId"), sectionId);
		//	query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);

			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			sectionList = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return sectionList;
	}
	
	
	public CommonRes saveEndtSection(EndtSectionSaveReq req) {
		CommonRes commonRes = new CommonRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			// Section Master
			List<ProductSectionMaster> sectionList = getProductSectionDropdown(req.getInsuranceId() , req.getProductId() ) ;
			
			// Transaction Tables
			List<EserviceSectionDetails>  secDatas =  eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			EserviceSectionDetails mapSec = secDatas.get(0);
			
			String secEndtModify = req.getSectionEndtModification() ;
			String humanProductType = getListItem(req.getInsuranceId(), req.getBranchCode(), "PRODUCT_CATEGORY", "H");
			String assetProductType = getListItem(req.getInsuranceId(), req.getBranchCode(), "PRODUCT_CATEGORY", "A");
			
			
			List<EserviceSectionDetails>  saveSecList = new ArrayList<EserviceSectionDetails>(); 
			
			// Modify Section 
			for ( String sec :  req.getEndtSectionIds() ) {
				List<EserviceSectionDetails>  filterSecs =  secDatas.stream().filter(  o -> o.getSectionId().equalsIgnoreCase(sec) ).collect(Collectors.toList() ) ;
				if(filterSecs.size() > 0 ) {
					EserviceSectionDetails secData =   filterSecs.get(0);
					secData.setSectionEndtModification(secEndtModify);
					saveSecList.add(secData);
				} else {
					EserviceSectionDetails secData = new EserviceSectionDetails();
					List<ProductSectionMaster> filterSection = sectionList.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(sec ) ) ).collect(Collectors.toList());		
					if( filterSection.size() > 0 ) {
						ProductSectionMaster section =  filterSection.get(0) ;
						
						dozerMapper.map(mapSec, secData);	
						
						secData.setSectionId(sec);
						secData.setSectionName( section.getSectionName() );
						secData.setRiskId(1);
						secData.setProductType(section.getMotorYn());
						secData.setProductTypeDesc(section.getMotorYn().equalsIgnoreCase("H") ? humanProductType : assetProductType  );
						secData.setUserOpt("N");
						secData.setSectionEndtModification(secEndtModify);
						if(!(req.getEndorsementType()==null || req.getEndorsementType()==0)) {
							secData.setOriginalPolicyNo(req.getOriginalPolicyNo());
							secData.setEndorsementDate(req.getEndorsementDate());
							secData.setEndorsementRemarks(req.getEndorsementRemarks());
							secData.setEndorsementEffdate(req.getEndorsementEffdate());
							secData.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
							secData.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
							secData.setEndtCount(req.getEndtCount());
							secData.setEndtStatus(req.getEndtStatus());
							secData.setIsFinaceYn(req.getIsFinaceYn());
							secData.setEndtCategDesc(req.getEndtCategDesc());
							secData.setEndorsementType(req.getEndorsementType());
							secData.setEndorsementTypeDesc(req.getEndorsementTypeDesc()); 
								
						} 
						saveSecList.add(secData);
						
					}
				}
			}
			
			  
			// Save Sections
			eserSecRepo.saveAllAndFlush(saveSecList);
				
			// Response 
			SuccessRes  res = new SuccessRes(); 
			res.setResponse("Saved Successfully");
			res.setSuccessId("1");
			
			commonRes.setCommonResponse(res);
			commonRes.setErroCode(0);
			commonRes.setIsError(false);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return commonRes;
	}
	
	
	
	public synchronized String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
		String itemDesc = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n13, n2, n3, n8, n9, n10, n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc;
	}
	
	public List<Error> validateEndtDetails(Endorsment request) {
		List<Error> error = new ArrayList<Error>();

		try {
			if(request.getEndtEffectiveDate() ==null ) {
				error.add(new Error("01", "EndtEffectiveDate", "Please Select Endoresment Effective Date"));
				
			}else if ( travelProductId.equalsIgnoreCase(request.getProductId().toPlainString())  ) {
				HomePositionMaster homeData = homeRepo.findByPolicyNo(request.getPolicyNo() );	
				if(homeData !=null ) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy") ;
					Date effDate = request.getEndtEffectiveDate();
					Date travelStartDate = homeData.getInceptionDate() ;
					if (travelStartDate.before(effDate) ) {
						error.add(new Error("01", "TravelStartDate", "Policy Start Date - " + sdf.format(homeData.getInceptionDate()) 
								+ " is Less Than Effective Date -" + sdf.format(request.getEndtEffectiveDate())  + " Not Allowed . Future  Policy only we can cancel in travel" ));
					}
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			error.add(new Error("01", "CommonError", e.getMessage() ));
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return error;
	}

}


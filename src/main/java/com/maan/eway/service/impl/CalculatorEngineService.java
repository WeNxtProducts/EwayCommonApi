package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.ChartOfAccount;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsDriverDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsLifeDetails;
import com.maan.eway.bean.MsPolicyDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataEndt;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TaxRemover;
import com.maan.eway.calculator.util.AdminCoverCalculator;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.CoverFromFactor;
import com.maan.eway.calculator.util.CreateMinimumPremium;
import com.maan.eway.calculator.util.CreatePolicyPremium;
import com.maan.eway.calculator.util.DiscountFromFactor;
import com.maan.eway.calculator.util.EndtCoverCalculator;
import com.maan.eway.calculator.util.EndtFromFactor;
import com.maan.eway.calculator.util.LoadingFromFactor;
import com.maan.eway.calculator.util.PolicyCoverCalculator;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxFromFactor;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.EndtUpdatePremiumRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.endorsment.util.CoverFromPolicy;
import com.maan.eway.endorsment.util.CreateEndorsment;
import com.maan.eway.endorsment.util.DiscountFromPolicy;
import com.maan.eway.endorsment.util.LoadingFromPolicy;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataEndtRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.FactorRateRequestDetailsService;
import com.maan.eway.service.PolicyDrcrDetailService;
import com.maan.eway.service.impl.referal.ReferalServiceImpl;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.JoinCriteria;
import com.maan.eway.upgrade.criteria.SpecCriteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class CalculatorEngineService implements CalculatorEngine {

	// 1.Section
	// 2.Cover

	@Autowired
	private CriteriaService crservice;

	@Autowired
	private RatingFactorsUtil ratingutil;
	
	
	@Autowired
	private CoverDetailsRepository coverRepo ;
	/*
	 * 
	  @Autowired 
	  private CoverCalculator calc;
	 */
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	
	protected List<Tuple> commontbl = null;
	protected List<Tuple> vehicles = null;
	protected List<Tuple> customers = null;
	protected List<Cover> calculatedcover = null;
	protected List<Tuple> prorata = null;
	protected BigDecimal minimumPremium=BigDecimal.ZERO;
	protected List<Tuple> policytbl = null;
	protected List<Tuple> drivers = null;
	

	@Autowired
	private FactorRateRequestDetailsService fservice;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FactorRateRequestDetailsRepository repository;

	@Autowired
	private ReferalServiceImpl referal;

	@Autowired
	private LoginUserInfoRepository loginUserRepo ;
	
	@Autowired
	private QuoteService quoteservice;

	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");

	@Autowired
	private LoginProductMasterRepository loginProductrepo;

	@Autowired
	private PolicyDrcrDetailService crdrservice;

	@Autowired
	private GenerateSeqNoServiceImpl genNo;

	DecimalFormat decimalFormat = null;
	@Autowired
	private PolicyCoverDataRepository coverDataRepo;
	
	@Autowired
	private TravelPassengerDetailsRepository travelRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildingRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository piRepo ;
	
	@Autowired
	private EServiceSectionDetailsRepository esSecRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eservicebuildingRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eservicecommonRepo;
	
	private Boolean isPolicyPeriod=Boolean.FALSE;
	@Autowired
	private MsVehicleDetailsRepository msVehicleRepo;
	/*
	 * public void LoadSection(CalcEngine engine) {
	 * 
	 * try { String todayInString = DD_MM_YYYY.format(new Date());
	 * 
	 * String search="companyId:"+ engine.getInsuranceId()
	 * +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+
	 * ";status=Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;";
	 * List<Tuple> result=null; SpecCriteria criteria =
	 * crservice.createCriteria(ProductSectionMaster.class, search, "coverId");
	 * result=crservice.getResult(criteria, 0, 50);
	 * 
	 * System.out.println("result"+result.size()); }catch(Exception e) {
	 * e.printStackTrace(); } }
	 */
	private final List<String> NORMAL_TAX_LIST = Arrays.asList("NB");
	private final List<String> ENDT_TAX_LIST = Arrays.asList("EC", "ER");
	@Autowired
	private PolicyCoverDataEndtRepository policyCoverEndtRepo;
 
	public List<Tuple> LoadCover(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			/*
			  String search1 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode() + ";branchCode:"
					+ engine.getBranchCode() + ";";
			 */
			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";
/*
			String search3 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";*/

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;";

			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);
			
			
			SpecCriteria criteria = null;		
			
			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for(Tuple t:specific) {
						commonResult.removeIf(c-> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}
					
			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		} 		 	
		return null;
	}
	private Map<String,BigDecimal> loadFixedValue(CalcEngine engine){
		try {
			List<Tuple> customerChoiceTaxes	=ratingutil.customerTaxList(engine);
			
			List<Tuple> totalcoverstuple = LoadCoverFixedValue(engine);
			if(totalcoverstuple!=null && totalcoverstuple.size()>0) {
			List<Tuple> covers = totalcoverstuple.parallelStream()
					.filter(t -> "N".equals(t.get("dependentCoverYn").toString()))
					.collect(Collectors.toList());
			List<Discount> discounts = null;
			List<Loading> loadings = null;
			if (covers != null && covers.size() > 0) {
				SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
						engine.getPolicyEndDate() ,"");
				discounts = covers.parallelStream().map(discountUtil).filter(d -> d != null).collect(Collectors.toList());
				discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
				SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
						engine.getPolicyEndDate());
				loadings = covers.parallelStream().map(loadingtuils).filter(d -> d != null).collect(Collectors.toList());
			}

			SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
					engine.getPolicyEndDate());
			Map<String, List<Cover>> nonSubcovers = covers.parallelStream().map(splitsub).filter(d -> d != null)
					.collect(Collectors.groupingBy(Cover::getIsSubCover));
			if (!nonSubcovers.isEmpty()) {
				List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
				if (!discounts.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Discount> ds = discounts.stream()
								.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
								//.collect(Collectors.toUnmodifiableList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						
						c.setDiscounts(ds);
						
					}
				}

				if (!loadings.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
								//.collect(Collectors.toUnmodifiableList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						c.setLoadings(ds);
						
					}
				}
				
			}
			
			List<Cover> totalcovers = new ArrayList<Cover>();
			if (!nonSubcovers.isEmpty() ) {
				totalcovers.addAll(nonSubcovers.get("N"));
			}
			CoverCalculator calc = new CoverCalculator();
			calc.setEngine(engine, totalcovers, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers,customerChoiceTaxes);
			totalcovers.parallelStream().forEach(calc);
			//286,319,287,324
			BigDecimal premiumLLD = totalcovers.stream().sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed()).filter(c-> "324".equals(c.getCoverId())).map(x-> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
			BigDecimal premiumTPL = totalcovers.stream().sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed()).map(x-> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
			
			MsVehicleDetails details = msVehicleRepo.findByVdRefno(Long.parseLong(engine.getVdRefNo()));	
			details.setPremiumLLD(premiumLLD);
			details.setPremiumTPL(premiumTPL);
			msVehicleRepo.save(details);
			Map<String,BigDecimal> hap=new HashMap<String,BigDecimal>();
			hap.put("PremiumLLD",premiumLLD);
			hap.put("PremiumTPL",premiumTPL);
			return hap;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	private List<Tuple> LoadCoverFixedValue(CalcEngine engine) {

		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			
			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;coverId:324";

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;coverId:324";

			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);
			
			
			SpecCriteria criteria = null;		
			//286,319,287,324
			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for(Tuple t:specific) {
						commonResult.removeIf(c-> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}
					
			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		} 		 	
		return null;
		}
	public synchronized EserviceMotorDetailsSaveRes calculator(CalcEngine engine, String token) {
		// Referal Checking.
		BigDecimal endtCount = BigDecimal.ZERO;
		List<UWReferrals> referr = referal.underwriterReferral(engine);

		List<MasterReferal> masterreferral = null;
		try {
			masterreferral = referal.masterreferral(engine, token);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String isEndt=null;
		
		
		List<Cover> retc = new ArrayList<Cover>();
		try {
			
			loadOnetimetable(engine);
			if("100040".equals(engine.getInsuranceId())){
				Map<String, BigDecimal> fixedValue = loadFixedValue(engine);
				loadOnetimetable(engine);	 
			}
			if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
					|| (customers == null || customers.size() == 0)) {
				System.out.println("::: Exception :: ");
				throw new Exception();

				/*
				 * throw
				 * CoverException.builder().message("Exception :: onetime table not inserted")
				 * .isError(true).build();
				 */
			}
			
			
				
			String promocode=vehicles.get(0).get("promocode")==null?"":vehicles.get(0).get("promocode").toString();
			List<Tuple> taxes = ratingutil.LoadTax(engine,NORMAL_TAX_LIST);
			
			List<Tuple> customerChoiceTaxes	=ratingutil.customerTaxList(engine);
			
			TaxUtils tzx = new TaxUtils(endtCount,"");
			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine,NORMAL_TAX_LIST);
			
			TaxRemover taxRemov=new TaxRemover(excludedTaxes,null);
			
			List<String> dependedcovers = new ArrayList<String>();
			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<Tuple> totalcoverstuple = LoadCover(engine);
			// effectiveDateStart&effectiveDateEnd

			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				//CopyOnWriteArrayList<Cover> totalcovers=new  CopyOnWriteArrayList<Cover>();
				List<Tuple> covers = totalcoverstuple.parallelStream()
						.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString()))
						.collect(Collectors.toList());
				List<Discount> discounts = null;
				List<Loading> loadings = null;
				if (covers != null && covers.size() > 0) {
					SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate() ,promocode);
					discounts = covers.parallelStream().map(discountUtil).filter(d -> d != null).collect(Collectors.toList());
					discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
					SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate());
					loadings = covers.parallelStream().map(loadingtuils).filter(d -> d != null).collect(Collectors.toList());
				}

				SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
						engine.getPolicyEndDate());
				Map<String, List<Cover>> nonSubcovers = covers.parallelStream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
									//.collect(Collectors.toUnmodifiableList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
									//.collect(Collectors.toUnmodifiableList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setLoadings(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
										//.collect(Collectors.toUnmodifiableList());
								c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new SplitSubCoverUtil("Y", engine.getEffectiveDate(), engine.getPolicyEndDate());
				Map<String, List<Cover>> subcovers = covers.parallelStream().map(splitsub)
						
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
								//	.collect(Collectors.toUnmodifiableList())	;
							// List<Tax> taxez =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(dss);
							// c.setTaxes(taxez);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
									//.collect(Collectors.toUnmodifiableList());
							c.setLoadings(dss);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
							List<Tax> taxey = taxes.parallelStream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
									//.collect(Collectors.toUnmodifiableList());
							c.setTaxes(taxey);
							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					//CopyOnWriteArrayList<Cover> subcov=new  CopyOnWriteArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						subcover.stream().forEach(taxRemov);
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					//totalcovers = 
					List<Cover> list = subcovers.get("Y");
					totalcovers.addAll(list);
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers.addAll(subcovers.get("Y"));
				}
				
				
				totalcovers.stream().forEach(taxRemov);
				/*
				 * if(StringUtils.isNotBlank(engine.getVdRefNo()) &&
				 * StringUtils.isNotBlank(engine.getCdRefNo())) { //calc.setEngine(engine,
				 * retc);
				 * 
				 * 
				 * }
				 */

				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers,customerChoiceTaxes);

				totalcovers.parallelStream().forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
			}
			//if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
			BigDecimal totalPremium=retc.stream().filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId()) && x.getPremiumExcluedTaxLC()!=null )).map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO,BigDecimal::add);
			if(totalPremium.compareTo(minimumPremium)<0) {
				List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
						.collect(Collectors.toList());
				BigDecimal difference=minimumPremium.subtract(totalPremium,MathContext.DECIMAL32);
				CreateMinimumPremium min=new CreateMinimumPremium(difference, engine, endtCount, taxey);
				Cover mini = min.create();
				List<Cover> minies=new ArrayList<Cover>(1);
				minies.add(mini);
				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers,customerChoiceTaxes);
				minies.stream().forEach(calc);
				retc.add(mini);
				
			}else {
				retc.removeIf(t -> "945".equals(t.getCoverId()));//.stream().filter(t-> "945".equals(t.getCoverId()).de
			}
				
			try {

				String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? ""
						: vehicles.get(0).get("endtTypeId").toString();
				if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
					String requestRefercenNo = engine.getRequestReferenceNo();
					String rawtable = ratingutil.getProductIdBasedRawTable(engine);
					String search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
							+ ";sectionId:" + engine.getSectionId() + ";riskId:" + engine.getVehicleId()
							+ ";status:{E,D,RP};requestReferenceNo:" + requestRefercenNo + ";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
					SpecCriteria criteria = crservice.createCriteria(Class.forName(rawtable), search,
							"requestReferenceNo");
					List<Long> count = crservice.getCount(criteria,0,2);
					if (!count.isEmpty() && count.get(0)<=0) {

						String riskid = engine.getVehicleId();
						search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
								+ ";riskId:" + riskid + ";status:{E,D,RP};requestReferenceNo:" + requestRefercenNo +
								";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
					}

					List<Tuple> result = null;
					 criteria = crservice.createCriteria(Class.forName(rawtable), search,
							"requestReferenceNo");
					result = crservice.getResult(criteria, 0, 50);
					endtCount = new BigDecimal(result.get(0).get("endtCount").toString());
					isEndt="admin";
					loadAndRemoveCoversForEndt(engine, retc, result);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} /*
			 * catch(CoverException e) { e.printStackTrace(); }
			 */catch (Exception e) {
			e.printStackTrace();
		}

		try {
			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
			response.setCoverList(retc);
			response.setResponse("Saved Successfully");
			response.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			response.setVehicleId(engine.getVehicleId());
			response.setVdRefNo(engine.getVdRefNo());
			response.setCdRefNo(engine.getCdRefNo());
			response.setInsuranceId(engine.getInsuranceId());
			response.setSectionId(engine.getSectionId());
			response.setCreatedBy(engine.getCreatedBy());
			response.setProductId(engine.getProductId());
			response.setLocationId(engine.getLocationId());
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(isEndt);
			response.setUwList(referr);
			response.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(response);

			// Update Premium,referral

			/// Endoresment calculation
			try {
				String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? "" :  vehicles.get(0).get("endtTypeId").toString() ;
				if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
					// referalCalculator = referalCalculator(engine);					
					return endorsementCalculator(engine, endtCount,endtTypeId,isPolicyPeriod);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * 
	 * @Autowired private EndtTypeMasterRepository endtTypeRepo;
	 * 
	 */
	private void loadAndRemoveCoversForEndt(CalcEngine engine, List<Cover> retc, List<Tuple> result) {
		try {

			if (!result.isEmpty()) {

				// String endtPrevPolicyNo=result.get(0).get("endtPrevPolicyNo").toString();
				String endtPrevQuoteNo = result.get(0).get("endtPrevQuoteNo").toString();

				String endtDesc = result.get(0).get("endorsementTypeDesc").toString();
				String endtTypeId = result.get(0).get("endorsementType").toString();
				BigDecimal endtCount = new BigDecimal(result.get(0).get("endtCount").toString());

				String originalPolicyNo = result.get(0).get("originalPolicyNo").toString();
				List<PolicyCoverDataEndt> oldPolicyData = policyCoverEndtRepo.findByPolicyNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdOrderByCoverIdAsc(originalPolicyNo,
						Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
						Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()));
				
				EndtTypeMaster endtmaster = ratingutil.getEndtMasterData(engine.getInsuranceId(), engine.getProductId(),
						endtTypeId);

				retc.stream().forEach(i -> i.setEndtCount(endtCount));
			 	// find Prev Quote Data
				List<PolicyCoverData> oldPolicyCovers = coverDataRepo
						.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
								endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
								Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()), "Y");
				List<Tuple> taxes = ratingutil.LoadTax(engine,NORMAL_TAX_LIST);
				TaxUtils tzx = new TaxUtils(endtCount,"");
				TaxUtils tzxEndt = new TaxUtils(endtCount,endtTypeId);
				List<Tax> taxey = taxes.stream().map(tzx).filter(t -> t != null).collect(Collectors.toList());
				
				List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine,NORMAL_TAX_LIST);
				TaxRemover taxRemov=new TaxRemover(excludedTaxes,null);

				
				
				List<Tax> tzxeyEndt = taxes.stream().map(tzxEndt).filter(t -> t != null).collect(Collectors.toList());
				
				// CoverFromPolicy
				List<PolicyCoverData> basecovers = oldPolicyCovers.stream()
						.filter(d -> !("T".equals(d.getCoverageType()) || "D".equals(d.getCoverageType())
								|| "L".equals(d.getCoverageType()) || "E".equals(d.getCoverageType())
								|| "P".equals(d.getCoverageType()) || d.getCoverId().compareTo(Integer.valueOf("945"))==0))
						.collect(Collectors.toList());

				List<PolicyCoverData> countPolicy = oldPolicyCovers.stream().filter(d -> (d.getCoverId().compareTo(Integer.valueOf("945"))==0)).collect(Collectors.toList());
				List<Cover> countCover = retc.stream().filter(t -> "945".equals(t.getCoverId())).collect(Collectors.toList());
				if(countCover.size()>0 && countPolicy.size()>0 && countCover.get(0).getPremiumExcluedTaxLC().compareTo(countPolicy.get(0).getPremiumExcludedTaxLc())==0) {
					
					retc.removeIf(t -> "945".equals(t.getCoverId()));
				}
				
				for (PolicyCoverData d : basecovers) {
					List<Cover> operatedList = new ArrayList<Cover>();
					//try {
						isPolicyPeriod=	getZeroTimeDate(engine.getPolicyEndDate()).compareTo(getZeroTimeDate(d.getCoverPeriodTo()))>=0?true:false;
					/*}catch (Exception e) {
						e.printStackTrace();
					}*/
					DiscountFromPolicy discountUtil = new DiscountFromPolicy();
					List<Discount> discounts = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(discountUtil).filter(dx -> dx != null).collect(Collectors.toList());

					LoadingFromPolicy loadingUtil = new LoadingFromPolicy();
					List<Loading> loadings = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(loadingUtil).filter(dx -> dx != null).collect(Collectors.toList());

					
					
					
					List<Endorsement> endorsements = new ArrayList<Endorsement>();
					List<PolicyCoverDataEndt> coverData = oldPolicyData.stream().filter(i -> i.getCoverId().compareTo(d.getCoverId())==0 ).collect(Collectors.toList()) ;
					
					CreateEndorsment createEndt=new CreateEndorsment(endtmaster,endtCount,tzxeyEndt,coverData,d);
					Endorsement currentEndt =createEndt.create();
					endorsements.add(currentEndt);
 
					CoverFromPolicy coverUtil = new CoverFromPolicy("");
					List<Cover> covers = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(coverUtil).filter(dx -> dx != null).collect(Collectors.toList());
					/*
					 * List<Cover> oldTax = covers.stream().filter(c ->
					 * "T".equals(c.getCoverageType())).collect(Collectors.toList());
					 * covers.removeAll(oldTax);
					 */
					
					covers.stream().filter(c -> (!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y"))).forEach(c -> c.setTaxes(taxey));
					covers.forEach(c -> c.setEndtCount(endtCount));
					covers.forEach(c -> c.setEndorsements(endorsements));// Existing Endorsement
					covers.forEach(c -> c.setDiscounts(discounts));
					covers.forEach(c -> c.setLoadings(loadings));
					covers.stream().forEach(taxRemov);
					
					retc.stream().filter(r -> d.getCoverId() == Integer.parseInt(r.getCoverId())).forEach(item -> {
						operatedList.add(item);
						covers.stream().forEach( c -> {
							c.setCoverageLimit(item.getCoverageLimit());
							c.setEffectiveDate(engine.getEffectiveDate());
							c.setPolicyEndDate(engine.getPolicyEndDate());
							});
					});
					retc.removeAll(operatedList);
					retc.addAll(covers);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request, BigDecimal endtCount, String endtTypeId, Boolean isPolicyPeriod) {
		try {
			List<Cover> retc = new ArrayList<Cover>();

			List<String> dependedcovers = new ArrayList<String>();

			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<FactorRateRequestDetails> factors = repository
					.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
							request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),
							Integer.valueOf(request.getProductId()), Integer.valueOf(request.getSectionId()));

			// TaxFromFactor tzx=new TaxFromFactor();
			List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST);
			List<Tuple> taxesEndt = ratingutil.LoadTax(request,ENDT_TAX_LIST);
			
			List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(request,NORMAL_TAX_LIST);
			List<Tuple> excludedTaxesEndt = ratingutil.LoadExcludedTax(request,ENDT_TAX_LIST);
			
			TaxRemover taxRemov=new TaxRemover(excludedTaxes,excludedTaxesEndt);
			TaxUtils tzx = new TaxUtils(endtCount,"");
			TaxUtils tzxsa = new TaxUtils(endtCount,endtTypeId);

			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<FactorRateRequestDetails> covers = factors.stream()
						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());

				DiscountFromFactor discountUtil = new DiscountFromFactor();
				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
						.collect(Collectors.toList());
				LoadingFromFactor loadingtuils = new LoadingFromFactor();
				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
						.collect(Collectors.toList());
				EndtFromFactor endtUtil = new EndtFromFactor();
				List<Endorsement> endorsements = covers.stream().map(endtUtil).filter(d -> d != null)
						.collect(Collectors.toList());

				CoverFromFactor splitsub = new CoverFromFactor("N");
				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setLoadings(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Endorsement> ds = endorsements.stream()
									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());

							//TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (ds != null && ds.size() > 0) {
								
								
								for (Endorsement e : ds) {

									// only for endrose we cannt use cover objs tax cover wontbe list.
									/*List<Tax> txx = factors.stream()
											.filter(r -> (r.getDiscLoadId() == Integer.parseInt(e.getEndorsementId())
													&& r.getCoverId() == Integer.parseInt(e.getEndorsementforId())
													&& r.getEndtCount().intValue() == e.getEndtCount().intValue()))
											.map(endttaxUtil).filter(dx -> (dx != null && !"0".equals(dx.getTaxId())))
											.collect(Collectors.toList());*/
									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
											.collect(Collectors.toList());
									e.setTaxes(taxey);
								}
							}

							c.setEndorsements(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) { 
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new CoverFromFactor("Y");
				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							// List<Tax> taxez =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(dss);
							// c.setTaxes(taxez);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setLoadings(dss);
						}
					}

					if (!endorsements.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Endorsement> ds = endorsements.stream()
									.filter(d -> d.getEndorsementforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							List<Endorsement> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());

							//TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (dss != null && dss.size() > 0) {
								
								for (Endorsement e : dss) {
								/*	List<Tax> txx = covers.stream()
											.filter(r -> (r.getDiscLoadId() == Integer.parseInt(e.getEndorsementId())
													&& r.getCoverId() == Integer.parseInt(e.getEndorsementforId())
													&& r.getEndtCount().intValue() == e.getEndtCount().intValue()))
											.map(endttaxUtil).filter(dx -> dx != null).collect(Collectors.toList());*/
									List<Tax> taxey = taxesEndt.stream().map(tzxsa).filter(d -> d != null)
											.collect(Collectors.toList());
									e.setTaxes(taxey);
								}
							}

							c.setEndorsements(dss);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) { 
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers = nonSubcovers.get("N");
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
				}

				totalcovers.stream().forEach(taxRemov);
				EndtCoverCalculator calc = new EndtCoverCalculator(isPolicyPeriod);
				
				if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
						|| (customers == null || customers.size() == 0)) {
					loadOnetimetable(request);
				}
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil,
						request.getEffectiveDate(), decimalFormat,drivers);

				totalcovers.stream().filter(t -> "Y".equals(t.getStatus())).forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);

			}
			
			try {
				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
				response.setCoverList(retc);
				response.setResponse("Saved Successfully");
				response.setRequestReferenceNo(request.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				response.setVehicleId(request.getVehicleId());
				response.setVdRefNo(request.getVdRefNo());
				response.setCdRefNo(request.getCdRefNo());
				response.setInsuranceId(request.getInsuranceId());
				response.setSectionId(request.getSectionId());
				response.setCreatedBy(request.getCreatedBy());
				response.setProductId(request.getProductId());
				
				response.setLocationId(request.getLocationId());
				response.setMsrefno(request.getMsrefno());
				response.setUpdateas("admin");
				// response.setUwList(referr);

				fservice.saveFactorRateRequestDetails(response);

				// Update Premium,referral

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void loadOnetimetable(CalcEngine engine) {
		/// One time table record
		try {
			SpecCriteria criteria = null;
			/*
			 * MsVehicleDetails findByVdRefno =
			 * msvech.findByVdRefno(Long.parseLong(engine.getVdRefNo()));
			 * System.out.println("findByVdRefno"+findByVdRefno.getChassisNumber());
			 */
			 
			List<Tuple> product = ratingutil.collectProductType(engine);
			String oneProduct=product.get(0).get("motorYn")==null?"M":product.get(0).get("motorYn").toString();
			

			/*
			 * vehicles=null; while(vehicles==null) {
			 * 
			 * 
			 * if(oneProduct.equalsIgnoreCase("M")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";vehicleId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsVehicleDetails.class, search,
			 * "vdRefno"); vehicles = crservice.getResult(criteria, 0, 50); }else
			 * if(oneProduct.equalsIgnoreCase("H")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";humanId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");
			 * vehicles = crservice.getResult(criteria, 0, 50); }else
			 * if(oneProduct.equalsIgnoreCase("A")){ String
			 * search="vdRefno:"+engine.getVdRefNo()+";locationId:"+engine.getVehicleId();
			 * criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");
			 * vehicles = crservice.getResult(criteria, 0, 50); }
			 * 
			 * System.out.println("Vehicle record "+engine.getVdRefNo()+", vehicles is "+((
			 * vehicles==null || vehicles.isEmpty())?"empty":"Not an empty")); }
			 * 
			 */

			String search = "msRefno:" + engine.getMsrefno() + ";";

			// if(result==null) {
			criteria = crservice.createCriteria(MsCommonDetails.class, search, "msRefno");
			commontbl = crservice.getResult(criteria, 0, 50);
			// }
			if (commontbl != null && commontbl.size() > 0) {
				Tuple tuple = commontbl.get(0);
				String vdRefno = tuple.get("vdRefno").toString();
				String cdRefno = tuple.get("cdRefno").toString();
				vehicles = null;
				int counter = 0;
				while (vehicles == null || vehicles.size() == 0 && counter < 6) {

					if (oneProduct.equals("M")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";vehicleId:" + engine.getVehicleId()+";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
						criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
						
						if(StringUtils.isNotBlank(engine.getDdRefno()) && !"0".equals(engine.getDdRefno())) {
							search = "ddRefno:" + engine.getDdRefno() + ";riskId:" + engine.getVehicleId()+";driverId:1;locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
							criteria = crservice.createCriteria(MsDriverDetails.class, search, "ddRefno");
							drivers = crservice.getResult(criteria, 0, 50);
						}
						
					} else if (oneProduct.equals("H")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";humanId:" + engine.getVehicleId()+";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
						criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					} else if (oneProduct.equalsIgnoreCase("A")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";riskId:" + engine.getVehicleId()+";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
						criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					}else if (oneProduct.equalsIgnoreCase("L")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";riskId:" + engine.getVehicleId()+";locationId:"+(StringUtils.isBlank(engine.getLocationId())?"1":engine.getLocationId());
						criteria = crservice.createCriteria(MsLifeDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					}

					counter++;

					System.out.println("Vehicle record " + vdRefno + ", vehicles is "
							+ ((vehicles == null || vehicles.isEmpty()) ? "empty" : "Not an empty"));
				}

				// if(customers==null) {
				search = "cdRefno:" + cdRefno + ";";
				criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
				customers = crservice.getResult(criteria, 0, 50);
				// }

				if (vehicles != null && vehicles.size() > 0) {
					String periodOfInsurance = (vehicles.get(0).get("periodOfInsurance") == null ? "365"
							: vehicles.get(0).get("periodOfInsurance").toString());
					String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
							: vehicles.get(0).get("insuranceClass").toString());
					
					prorata = ratingutil.loadProRataData(engine, periodOfInsurance,policyTypeId);

					String currencyId = vehicles.get(0).get("currency") == null ? "TTT"
							: vehicles.get(0).get("currency").toString();
					String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
					String stringFormat = "%0" + decimalDigits + "d";
					String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
					String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
					decimalFormat = new DecimalFormat(pattern);
										
					minimumPremium =product.get(0).get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(product.get(0).get("minPremium").toString());
					
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public synchronized EserviceMotorDetailsSaveRes referalCalculator(CalcEngine request) {
		try {
			List<Cover> retc = new ArrayList<Cover>();

			loadOnetimetable(request);
			if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
					|| (customers == null || customers.size() == 0)) {
				System.out.println("::: Exception :: ");
				throw new Exception();

				/*
				 * throw
				 * CoverException.builder().message("Exception :: onetime table not inserted")
				 * .isError(true).build();
				 */
			}
			List<Tuple> customerChoiceTaxes	=ratingutil.customerTaxList(request);
			List<String> dependedcovers = new ArrayList<String>();

			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<FactorRateRequestDetails> factors = repository
					.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
							request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),
							Integer.valueOf(request.getProductId()), Integer.valueOf(request.getSectionId()));

			// TaxFromFactor tzx=new TaxFromFactor();
			/*List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST);
			TaxUtils tzx = new TaxUtils(BigDecimal.ZERO	,"");
		*/
			TaxFromFactor tzx=new TaxFromFactor();
			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<FactorRateRequestDetails> covers = factors.stream()
						.filter(f -> dependcover.equals(f.getDependentCoverYn())).collect(Collectors.toList());

				DiscountFromFactor discountUtil = new DiscountFromFactor();
				List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
						.collect(Collectors.toList());
				LoadingFromFactor loadingtuils = new LoadingFromFactor();
				List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
						.collect(Collectors.toList());

				CoverFromFactor splitsub = new CoverFromFactor("N");
				Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!nonSubcovers.isEmpty()) {
					List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
							// List<Tax> taxey =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setLoadings(ds);
							// c.setTaxes(taxey);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) { 
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
							List<Tax> taxey = factors.stream().filter(d ->( d.getCoverId().toString().equals(c.getCoverId()) && d.getCoverageType().equalsIgnoreCase("T"))).map(tzx)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
							}
						}
					}
				}

				splitsub = new CoverFromFactor("Y");
				Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
						.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
						.collect(Collectors.groupingBy(Cover::getIsSubCover));
				if (!subcovers.isEmpty()) {
					List<Cover> noncovers = subcovers.get("Y"); // noncovers
					if (!discounts.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Discount> ds = discounts.stream()
									.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							// List<Tax> taxez =
							// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
							c.setDiscounts(dss);
							// c.setTaxes(taxez);
						}
					}

					if (!loadings.isEmpty() && !noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
									.collect(Collectors.toList());
							ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

							List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
									.collect(Collectors.toList());
							c.setLoadings(dss);
						}
					}
					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) { 
							if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
								/*List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
								*/
								 
								List<Tax> taxey = factors.stream().filter(d ->( d.getCoverId().toString().equals(c.getCoverId()) && d.getCoverageType().equalsIgnoreCase("T"))).map(tzx)
										.collect(Collectors.toList());
								c.setTaxes(taxey);
								
							
							}
						}
					}

					List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
							.collect(Collectors.toList());
					List<Cover> subcov = new ArrayList<Cover>();
					for (Cover cover : d) {
						List<Cover> subcover = noncovers.stream()
								.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
						subcover.stream().forEach(s -> s.setIsSubCover("N"));
						// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
						Cover newcover = SerializationUtils.clone(cover);
						newcover.setSubcovers(subcover);
						newcover.setIsSubCover("Y");
						newcover.setSubCoverId(null);
						newcover.setSubCoverDesc(null);
						newcover.setSubCoverName(null);
						newcover.setDiscounts(null);
						newcover.setLoadings(null);
						newcover.setTaxes(null);
						subcov.add(newcover);
					}
					subcovers.put("Y", subcov);
				}

				if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
					totalcovers.addAll(nonSubcovers.get("N"));
				} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
					totalcovers = nonSubcovers.get("N");
				} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
					totalcovers = subcovers.get("Y");
				}

				// CoverCalculator calc=new CoverCalculator();
				
				
				AdminCoverCalculator calc = new AdminCoverCalculator();
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers,customerChoiceTaxes);

				totalcovers.stream().forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
			}
			//if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
			BigDecimal totalPremium=retc.stream().filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId()) && x.getPremiumExcluedTaxLC()!=null  )).map(x-> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO,BigDecimal::add);
			if(totalPremium.compareTo(minimumPremium)<0) {
				
				List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST);
				TaxUtils tzxx = new TaxUtils(BigDecimal.ZERO,"");
				List<Tax> taxey = taxes.stream().map(tzxx).filter(d->d!=null).collect(Collectors.toList());
				BigDecimal difference=minimumPremium.subtract(totalPremium,MathContext.DECIMAL32);
				CreateMinimumPremium min=new CreateMinimumPremium(difference, request,factors.get(0).getEndtCount() , taxey);
				Cover mini = min.create();
				List<Cover> minies=new ArrayList<Cover>(1);
				minies.add(mini);
				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers,customerChoiceTaxes);
				minies.stream().forEach(calc);
				retc.add(mini);
				
			}else {
				retc.removeIf(t -> "945".equals(t.getCoverId()));//.stream().filter(t-> "945".equals(t.getCoverId()).de
						}
			try {
				EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
				response.setCoverList(retc);
				response.setResponse("Saved Successfully");
				response.setRequestReferenceNo(request.getRequestReferenceNo());
				// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
				response.setVehicleId(request.getVehicleId());
				response.setVdRefNo(request.getVdRefNo());
				response.setCdRefNo(request.getCdRefNo());
				response.setInsuranceId(request.getInsuranceId());
				response.setSectionId(request.getSectionId());
				response.setCreatedBy(request.getCreatedBy());
				response.setProductId(request.getProductId());
				response.setMsrefno(request.getMsrefno());
				response.setLocationId(request.getLocationId());
				response.setUpdateas("admin");
				// response.setUwList(referr);

				fservice.saveFactorRateRequestDetails(response);

				// Update Premium,referral

				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Autowired
	private MotorDataDetailsRepository motorRepo;
	@Autowired
	private SectionDataDetailsRepository sectionRepo;
	@Override
	public List<DebitAndCredit> commissionCalc(CalcCommission request) {
		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
		String policyNo = "";
		try {
			
			resList = getOverAllcommissionCalc(request );
			//resList =  getRiskWisecommissionCalc( )
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}
	
	public List<DebitAndCredit> getOverAllcommissionCalc(CalcCommission request ) {
		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
		try {
			ViewQuoteReq q = new ViewQuoteReq();
			q.setQuoteNo(request.getQuoteno());
			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
			CompanyProductMaster product =  getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId() , v1.getQuoteDetails().getProductId().toString());
			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
			String emiYn=v1.getQuoteDetails().getEmiYn();
			String instalment=v1.getQuoteDetails().getInstallmentMonth();
			 List<BranchMaster> branchCode=ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getBranchCode());
			 
			 //Not endt
			if (StringUtils.isBlank(endttypeid)&& ( emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {			 
				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
		 	List<ProductSectionMaster> coreappcode=ratingutil.collectSectionMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getProductId().toString(),sections.get(0).getSectionId());
		 
		 	List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());
		 	HomePositionMaster hpm = homeRepo.findByQuoteNo(request.getQuoteno())	 ;	
		 	PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId()) 	;
			String vehUsageCoreappcode = "";
			String policyNo ="";
			
			
		 	if(request.getProductId().equalsIgnoreCase("5"))		 	
		 		vehUsageCoreappcode = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());	 	
		 	
		 	  if(request.getInsuranceId().equalsIgnoreCase("100004")) {
		 		  
		 		 String itemvalue = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "POLICY_NO");
		 		  
		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode, request.getProductId(), itemvalue);
		 		 
		 	  }else if(request.getInsuranceId().equalsIgnoreCase("100019")) {
		 		  
			 		 policyNo = genNo.generateUgandaPolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
		 	  } else {
		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
		 	  }
		 	
				request.setPolicyNo(policyNo);
			} else { //endt
				
				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
			}
			
		/*	HomePositionMaster homeData = homeRepo.findByQuoteNo(v1.getQuoteDetails().getQuoteNo());
			List<ChartOfAccount>  getChartList = getChartList(homeData.getCompanyId());
			// Source Type Search Condition
			List<String> directSource = new ArrayList<String>();
			directSource.add("1");
			directSource.add("2");
			directSource.add("3");
			boolean directSourceAvailable = homeData.getSourceTypeId()!=null && directSource.contains(homeData.getSourceTypeId()) ? true : false ;  
			
			String policyType = "99999";
			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno());
				policyType = motors.size() > 0 ? motors.get(0).getPolicyType() : "99999" ;					
			}
				HomePositionMaster v = homeData ;
				
				Double commissionPercent = 0.0;
				//commissionPercent=v.getCommissionPercentage().doubleValue();
				String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
				List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
						v.getProductId().toString(), loginId, v.getBrokerCode(), policyType);
				 
					
				// Premia Broker , Agent Condition
				 if(directSourceAvailable == true ) {
					//commissionPercent=12.5;
					String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
					commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
					
				} else if(policylist.size()>0 && policylist!=null) {
						if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
							commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
						} else {
							commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
									: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
						}
						
				}
				else {
					commissionPercent=0D;
				}
				
				String premiumFc = v.getPremiumFc().toString();
				String vatPremiumFc = v.getVatPremiumFc()==null  ?"0" : v.getVatPremiumFc().toPlainString();
			
					
				if(StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())  ) {
					EndtUpdatePremiumRes endtRes = mainTableEndtPremium(v.getQuoteNo() , product.getProductId().toString() , product.getMotorYn() );				
					premiumFc = endtRes.getEndtPremium()==null ? "0" : String.valueOf(endtRes.getEndtPremium().toPlainString());
					vatPremiumFc  = endtRes.getEndtVatPremium()==null ? "0" : String.valueOf(endtRes.getEndtVatPremium().toPlainString()) ;
					
				}

				BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
						.divide(BigDecimal.valueOf(100D))
						.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
				//totalcommission = totalcommission.add(commission);
				
				
				List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();

				// Setup
				Map<String, Object> setup = new HashMap<String, Object>();

				List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
				{
					Map<String, Object> subset = new HashMap<String, Object>();
					String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
					
					List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equals(Integer.valueOf(chargeCode))	 ).collect(Collectors.toList());
					ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
							
					subset.put("CHARGE_CODE", chargeCode);
					subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
					subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
					subset.put("CHARGE_CODE_VALUE", premiumFc);
					subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
					csubsets.add(subset);
				}
				{
					Map<String, Object> subset = new HashMap<String, Object>();
					List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equals(1009)	 ).collect(Collectors.toList());
					ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
					subset.put("CHARGE_CODE", "1009");
					subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
					subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " " + Double.valueOf(homeData.getVatPercent()==null?"0":homeData.getVatPercent().toPlainString()) +"%" :  "VAT");
					subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
					subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
					csubsets.add(subset);
				}
				String crnumber ="";
				if(commissionPercent.doubleValue()>0D) {

					List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1005" ;
						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equals(Integer.valueOf(chargeCode))	 ).collect(Collectors.toList());
						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
						subset.put("CHARGE_CODE", chargeCode);
						subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()  : "Commission");
						subset.put("CHARGE_CODE_VALUE", commission);
						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
						bsubsets.add(subset);
					}

					{
						Map<String, Object> subset = new HashMap<String, Object>();
						String chargeCode =  "1007" ;
						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equals(Integer.valueOf(chargeCode))	 ).collect(Collectors.toList());
						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
						subset.put("CHARGE_CODE", chargeCode);
						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
						subset.put("CHARGE_CODE_VALUE", commissionPercent);
						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
						bsubsets.add(subset);
					}
					{// Broker Commmission Vat
						String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
						LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
						String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
						if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
							String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
							BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
									.divide(BigDecimal.valueOf(100D))
									.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
									
							Map<String, Object> subset = new HashMap<String, Object>();
							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equals(1009)	 ).collect(Collectors.toList());
							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
							subset.put("CHARGE_CODE", "1009");
							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " " + Double.valueOf(brokerVatPercent) +"%" : "Vat");
							subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
							bsubsets.add(subset);
						}
					}
					setup.put("<BROKER>", bsubsets);
					crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
				}
				/*
				 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
				 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
				 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
				 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
				 * 
				 * 
				 * Map<String,Object> subset=new HashMap<String, Object>();
				 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
				 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
				 * bsubsets.add(subset); }
				 * 
				 */
				/*setup.put("<CUSTOMER>", csubsets);
				

				// Rule
				Map<String, Object> rule1 = new HashMap<String, Object>();
				
				if( v.getEndtPremium()!=null && v.getEndtPremium().compareTo(new BigDecimal(0))<=0  ){
					rule1.put("DEBIT", "<BROKER>");
					rule1.put("CREDIT", "<CUSTOMER>");
				}else {
						rule1.put("DEBIT", "<CUSTOMER>");
						rule1.put("CREDIT", "<BROKER>");
				}
				rules.add(rule1);

				 // ThreadLocalRandom.current().ints(1001,
																	// 4999).distinct().limit(5).findAny().toString();
				String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
																	// 9999).distinct().limit(5).findAny().toString();

				
				int rownum = 1;

				for (Map<String, Object> map : rules) {
					for (Entry<String, Object> m : map.entrySet()) {
						List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
						if(dd!=null){
							for (Map<String, Object> s : dd) {
								DebitAndCredit res =new  DebitAndCredit();
								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";

								res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								if(s.get("CHARGE_CODE").toString().equalsIgnoreCase("1007")  ) {
									res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								} else {
									String pattern =  "#####0" ;
									DecimalFormat df = new DecimalFormat(pattern);
									res.setAmountLc( new BigDecimal(df.format(res.getAmountFc().multiply(v.getExchangeRate()))) );
									
								}
							
								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
								res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
								res.setNarration(s.get("NARATION").toString());
								res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
								res.setBranchCode(request.getBranchCode());
								res.setChgId(new BigDecimal(rownum++));
								res.setCompanyId(request.getInsuranceId());
								res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
										: v1.getQuoteDetails().getLoginId());
								res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
								res.setDocType(doctype);
								res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
								res.setEntryDate(new Date());
								res.setPolicyNo(request.getPolicyNo());
								res.setProductId(request.getProductId());
								res.setQuoteNo(request.getQuoteno());
								res.setStatus("Y");
								res.setQuoteInfo(v1);
								resList.add(res);
							}
						}
					}
				}
				crdrservice.insertDRCR(resList, request.getQuoteno()); */
			} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return resList ;
	}
	
	
	@Override
	public String getPolicyNo(CalcCommission request ) {
		String policyNo = "" ;
		try {
			ViewQuoteReq q = new ViewQuoteReq();
			q.setQuoteNo(request.getQuoteno());
			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
			CompanyProductMaster product =  getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId() , v1.getQuoteDetails().getProductId().toString());
			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
			String emiYn=v1.getQuoteDetails().getEmiYn();
			String instalment=v1.getQuoteDetails().getInstallmentMonth();
			 List<BranchMaster> branchCode=ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getBranchCode());
			 
			 //Not endt
			if (StringUtils.isBlank(endttypeid)&& ( emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {			 
				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
		 	List<ProductSectionMaster> coreappcode=ratingutil.collectSectionMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getProductId().toString(),sections.get(0).getSectionId());
		 
		 	List<MotorDataDetails> list = motorRepo.findByQuoteNo(request.getQuoteno());
		 	HomePositionMaster hpm = homeRepo.findByQuoteNo(request.getQuoteno())	 ;	
		 	PersonalInfo pi = piRepo.findByCustomerId(hpm.getCustomerId()) 	;
			String vehUsageCoreappcode = "";
			
			
//		 	if(request.getProductId().equalsIgnoreCase("5"))		 	
//		 		vehUsageCoreappcode = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "MADISON_MOTOR", list.get(0).getMotorUsage(), pi.getPolicyHolderType());	 	
//		 	
//		 	  if(request.getInsuranceId().equalsIgnoreCase("100004")) {
//		 		  
//		 		 String itemvalue = getListItemvalue(request.getInsuranceId() , request.getBranchCode(), "POLICY_NO");
//		 		  
//		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode(), request.getInsuranceId(), vehUsageCoreappcode, request.getProductId(), itemvalue);
//		 		 
//		 	  }else {
//		 		 policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
//		 	  }
				// Generate Policy Seq
				SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
			 	generateSeqReq.setInsuranceId(hpm.getCompanyId());  
			 	generateSeqReq.setProductId(hpm.getProductId().toString());
			 	generateSeqReq.setType("5"); 
			 	generateSeqReq.setTypeDesc("POLICY_NO");
			 	List<String> params = new ArrayList<String>();
			 	params.add(hpm.getQuoteNo());
			 	generateSeqReq.setParams(params);
			 	policyNo =  genNo.generateSeqCall(generateSeqReq);
			 	policyNo = policyNo.replaceAll(" ", "");
			 	request.setPolicyNo(policyNo);
			} else { //endt
				
				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
				policyNo = v1.getQuoteDetails().getPolicyNo();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return policyNo;
	}
	
	private EndtUpdatePremiumRes updateEndtPremium2(String quoteNo,Date effDate,String prevQuoteNo,Integer riskId, List<PolicyCoverData> covers, Integer productId , Integer sectionId , String endtType ) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();  
		try {
			List<PolicyCoverData> newCovers=null;
			List<PolicyCoverData>  totalcovers =null;
			 List<PolicyCoverData>  oldcovers =null; 
			 
			 if(riskId.intValue()==0) {
				 newCovers=covers;
				 totalcovers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				 oldcovers = coverRepo.findByQuoteNoAndDiscLoadIdAndTaxIdAndStatusNotOrderByVehicleIdAsc(prevQuoteNo ,0, 0 ,"D");
			 }else {
				 newCovers=covers.stream().filter(i -> i.getVehicleId().doubleValue()==riskId.doubleValue()).collect(Collectors.toList());
				 totalcovers = coverRepo.findByQuoteNoAndVehicleIdAndProductIdAndSectionIdOrderByVehicleIdAsc(quoteNo,riskId,productId,sectionId);
				 oldcovers = coverRepo.findByQuoteNoAndVehicleIdAndDiscLoadIdAndTaxIdAndStatusNotAndProductIdAndSectionIdOrderByVehicleIdAsc(prevQuoteNo ,riskId,0, 0 ,"D",productId,sectionId);
			 }
			 
			 List<PolicyCoverData>  oldcoversf=oldcovers;
			
			// Premium With Tax 
//			Double removedCoverPremium =  (totalcovers.stream().filter( o ->   o.getPremiumIncludedTaxFc()!=null 
//					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum());			 
//			 
//			Double endtChangePremium=totalcovers.stream().filter( o ->  o.getPremiumIncludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
//			 .mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
//			 
//			 newCovers.removeIf(p-> {
//				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
//			 });
//			 Double addedCoverPremium =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
//					 o.getTaxId().equals(0) && o.getPremiumIncludedTaxFc()!=null 
//					 && !"D".equals(o.getStatus())
//					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
//				BigDecimal endtPremium= new  BigDecimal(removedCoverPremium+addedCoverPremium+endtChangePremium);
//				
				
			// Premium Without Tax 
			Double removedCoverPremiumWithoutTax =  (totalcovers.stream().filter( o ->   o.getPremiumExcludedTaxFc()!=null 
					 && "D".equals(o.getStatus())   && "E".equals(o.getCoverageType())  ) .mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum());			 
			
			Double endtChangePremiumWithoutTax=totalcovers.stream().filter( o ->  o.getPremiumExcludedTaxFc()!=null && "E".equals(o.getCoverageType()) && !"D".equals(o.getStatus())  )
			 .mapToDouble( o ->   o.getPremiumExcludedTaxFc().doubleValue()   ).sum();
			 
			 newCovers.removeIf(p-> {
				 return oldcoversf.stream().anyMatch(x-> (x.getVehicleId()==p.getVehicleId() && x.getSectionId() ==p.getSectionId() && x.getProductId()==p.getProductId() && x.getCoverId()==p.getCoverId()));
			 });
			 Double addedCoverPremiumWithoutTax =newCovers.stream().filter( o -> o.getDiscLoadId().equals(0)  &&  
					 o.getTaxId().equals(0) && o.getPremiumExcludedTaxFc()!=null 
					 && !"D".equals(o.getStatus())
					 && effDate.compareTo(o.getCoverPeriodFrom())>=0  ).mapToDouble( o ->   o.getPremiumIncludedTaxFc().doubleValue()   ).sum();
				BigDecimal endtPremiumWithoutTax = new  BigDecimal(removedCoverPremiumWithoutTax+addedCoverPremiumWithoutTax+endtChangePremiumWithoutTax);
					
			// Tax Amount 
			List<PolicyCoverData>  endtTaxCovers  = totalcovers.stream().filter( o -> o.getCoverageType().equalsIgnoreCase("T") && 
					o.getDiscLoadId().equals(Integer.valueOf(endtType)) ).collect(Collectors.toList());
			 Double endtVatPremium = endtTaxCovers.stream().filter( o -> !o.getDiscLoadId().equals(0)  &&  
					 !o.getTaxId().equals(0) && o.getTaxAmount()!=null && o.getCoverageType().equalsIgnoreCase("T") ).mapToDouble( o ->   o.getTaxAmount().doubleValue()   ).sum();
			 
			String endtChargeOrRefund="REFUND";
			if(endtPremiumWithoutTax.doubleValue()>=0) {
				endtChargeOrRefund="CHARGE";
			} else if (endtPremiumWithoutTax.doubleValue()<0 && endtVatPremium >=0 ) {
				endtVatPremium = - endtVatPremium;
			}
			
			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(endtPremiumWithoutTax);
			endtRes.setEndtVatPremium(new  BigDecimal(endtVatPremium));
			
			
			return endtRes;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}
	
	private EndtUpdatePremiumRes mainTableEndtPremium(String quoteNo ,String productId , String productType) {
		EndtUpdatePremiumRes endtRes = new EndtUpdatePremiumRes();  
		try {
			Double endtPremiumWithoutTax = 0D ;
			Double endtVatPremium = 0D ;
			if (productType.equalsIgnoreCase("M")) {
				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(quoteNo);
				for (MotorDataDetails mot : motors) {
					endtPremiumWithoutTax = endtPremiumWithoutTax + (  mot.getEndtPremium() ==null ? 0D : mot.getEndtPremium().doubleValue()) ;
					endtVatPremium =  endtVatPremium + (  mot.getEndtVatPremium() ==null ? 0D : mot.getEndtVatPremium().doubleValue()) ;
					
				}
				
			// Travel Product
			} else if (productType.equalsIgnoreCase("H") && productId.equalsIgnoreCase(travelProductId)) {
					//List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>) v1.getRiskDetails();
					//EserviceTravelDetails tra = eserTraRepo.findByRequestReferenceNo(request.getQuoteNo() );
				    HomePositionMaster homeData = homeRepo.findByQuoteNo(quoteNo) ;
					endtPremiumWithoutTax = endtPremiumWithoutTax + (  homeData.getEndtPremium() ==null ? 0D : homeData.getEndtPremium().doubleValue()) ;
					endtVatPremium =  endtVatPremium + (  homeData.getEndtPremiumTax() ==null ? 0D : homeData.getEndtPremiumTax().doubleValue()) ;
				
				
			} else if (productType.equalsIgnoreCase("A")) {
				List<BuildingRiskDetails> BuildingRisk = buildingRepo.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(quoteNo ,"0");
	
					// Asset
					for (BuildingRiskDetails build : BuildingRisk) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  build.getEndtPremium() ==null ? 0D : build.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  build.getEndtVatPremium() ==null ? 0D : build.getEndtVatPremium().doubleValue()) ;
					
					}
					
					// Human Included
					List<CommonDataDetails> humans = commonRepo.findByQuoteNo(quoteNo);
	
					for (CommonDataDetails hum : humans) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  hum.getEndtPremium() ==null ? 0D : hum.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  hum.getEndtVatPremium() ==null ? 0D : hum.getEndtVatPremium().doubleValue()) ;
					}
					
			  // Human Products		
			} else {
				List<CommonDataDetails> humans = commonRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
	
					for (CommonDataDetails hum : humans) {
						endtPremiumWithoutTax = endtPremiumWithoutTax + (  hum.getEndtPremium() ==null ? 0D : hum.getEndtPremium().doubleValue()) ;
						endtVatPremium =  endtVatPremium + (  hum.getEndtVatPremium() ==null ? 0D : hum.getEndtVatPremium().doubleValue()) ;
					}
				
			}
			
			String endtChargeOrRefund="REFUND";
			if(endtPremiumWithoutTax.doubleValue()>=0) {
				endtChargeOrRefund="CHARGE";
			}
			
			endtRes.setChargeOrRefund(endtChargeOrRefund);
			endtRes.setEndtPremium(new  BigDecimal(endtPremiumWithoutTax));
			endtRes.setEndtVatPremium(new  BigDecimal(endtVatPremium));
			
			
			return endtRes;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return endtRes;
	}
	
//	public List<DebitAndCredit> getRiskWisecommissionCalc(CalcCommission request ) {
//		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();
//		try {
//			ViewQuoteReq q = new ViewQuoteReq();
//			q.setQuoteNo(request.getQuoteno());
//			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
//			CompanyProductMaster product =  getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId() , v1.getQuoteDetails().getProductId().toString());
//			String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//			String emiYn=v1.getQuoteDetails().getEmiYn();
//			String instalment=v1.getQuoteDetails().getInstallmentMonth();
//			 List<BranchMaster> branchCode=ratingutil.collectBranchMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getBranchCode());
//			if (StringUtils.isBlank(endttypeid)&& ( emiYn.equalsIgnoreCase("N") || instalment.equalsIgnoreCase("0"))) {			 
//				List<SectionDataDetails> sections = sectionRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//		 	List<ProductSectionMaster> coreappcode=ratingutil.collectSectionMaster(v1.getQuoteDetails().getCompanyId(),v1.getQuoteDetails().getProductId().toString(),sections.get(0).getSectionId());
//		 	String policyNo = genNo.generatePolicyNo(coreappcode.get(0).getCoreAppCode(),branchCode.get(0).getCoreAppCode());
//				request.setPolicyNo(policyNo);
//			} else {
//				request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//			}
//			
//			HomePositionMaster homeData = homeRepo.findByQuoteNo(v1.getQuoteDetails().getQuoteNo());
//			List<ChartOfAccount>  getChartList = getChartList(homeData.getCompanyId());
//			// Source Type Search Condition
//			List<String> directSource = new ArrayList<String>();
//			directSource.add("1");
//			directSource.add("2");
//			directSource.add("3");
//			boolean directSourceAvailable = homeData.getSourceTypeId()!=null && directSource.contains(homeData.getSourceTypeId()) ? true : false ;  
//			
//			
//			if (product.getMotorYn().equalsIgnoreCase("M")) {
//				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno());
//					//List<EserviceMotorDetailsRes> motors = (List<EserviceMotorDetailsRes>) v1.getRiskDetails();
//				for (MotorDataDetails v : motors) {
//					Double commissionPercent = 0.0;
//					//commissionPercent=v.getCommissionPercentage().doubleValue();
//					String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v.getBrokerCode(), v.getPolicyType());
//					 
//						
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true ) {
//						//commissionPercent=12.5;
//						String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//						commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//							
//					}
//					else {
//						commissionPercent=0D;
//					}
//					
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = v.getVatPremium()==null  ?"0" : v.getVatPremium().toPlainString();
//				
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//						
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					//totalcommission = totalcommission.add(commission);
//					
//					
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//					
//					if("D".equals(v.getStatus()) || ( v.getEndtPremium()!=null && v.getEndtPremium() <0 ) ){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//					
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null){
//								for (Map<String, Object> s : dd) {
//									DebitAndCredit res =new  DebitAndCredit();
//									String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//	
//									res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//									res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//									res.setNarration(s.get("NARATION").toString());
//									res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//									res.setRiskDesc("Vehicle Id -" + v.getVehicleId() ) ;
//									res.setBranchCode(request.getBranchCode());
//									res.setChgId(new BigDecimal(rownum++));
//									res.setCompanyId(request.getInsuranceId());
//									res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//											: v1.getQuoteDetails().getLoginId());
//									res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//									res.setDocType(doctype);
//									res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//									res.setEntryDate(new Date());
//									res.setPolicyNo(request.getPolicyNo());
//									res.setProductId(request.getProductId());
//									res.setQuoteNo(request.getQuoteno());
//									res.setStatus("Y");
//									res.setRiskId(v.getVehicleId());
//									res.setQuoteInfo(v1);
//									res.setSectionId(v.getInsuranceClass());
//									resList.add(res);
//								}
//							}
//						}
//					}
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//				}
//				
//			}
//	
//			// Travel Product
//			else if (product.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
//					//List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>) v1.getRiskDetails();
//				List<TravelPassengerDetails> motors = travelRepo.findByQuoteNoOrderByTravelIdAsc(request.getQuoteno());
//	
//					for (TravelPassengerDetails v : motors) {
//						Double commissionPercent = 0.0;
//						// Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v1.getQuoteDetails().getBrokerCode(), "99999");
//							
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true) {
//						//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//						
//						if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//							commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//						} else {
//							commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//									: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//						}
//					}
//					else {
//						commissionPercent=0D;
//					}
//					 String premiumFc ="";
//					 String vatPremiumFc = "";
//					 
//					if(! v.getStatus().equalsIgnoreCase("D")) {
//						premiumFc = v.getActualPremiumFc().toString();
//						vatPremiumFc = String.valueOf(  v.getVatPremium());
//						
//					}
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId()) && v.getStatus().equalsIgnoreCase("D") ) {
//						premiumFc = v.getEndtPremium() ==null ? "" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"" :  v.getEndtVatPremium().toPlainString();
//					}
//	
//					
//					
//				//	String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					if(StringUtils.isNotBlank(premiumFc) ) {
//						BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//								.divide(BigDecimal.valueOf(100D))
//								.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//						
//						Map<String, Object> setup = new HashMap<String, Object>();
//	
//						List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//							
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//									
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//							subset.put("CHARGE_CODE_VALUE", premiumFc);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//							csubsets.add(subset);
//						}
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1009");
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//							subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//							csubsets.add(subset);
//						}
//						String crnumber ="";
//						if(commissionPercent.doubleValue()>0D) {
//	
//							List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//							{
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1005");
//								subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", commission);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//								bsubsets.add(subset);
//							}
//	
//							{
//								Map<String, Object> subset = new HashMap<String, Object>();
//								String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", chargeCode);
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//								subset.put("CHARGE_CODE_VALUE", commissionPercent);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//								bsubsets.add(subset);
//							}
//							{// Broker Commmission Vat
//								String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//								LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//								String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//								if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//									String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//									BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//											.divide(BigDecimal.valueOf(100D))
//											.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//											
//									Map<String, Object> subset = new HashMap<String, Object>();
//									List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//									ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//									subset.put("CHARGE_CODE", "1009");
//									subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//									subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//									subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//									subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//									bsubsets.add(subset);
//								}
//							}
//							setup.put("<BROKER>", bsubsets);
//							crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//						}
//						
//						/*
//						 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//						 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//						 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//						 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//						 * 
//						 * 
//						 * Map<String,Object> subset=new HashMap<String, Object>();
//						 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//						 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//						 * bsubsets.add(subset); }
//						 * 
//						 */
//						setup.put("<CUSTOMER>", csubsets);
//						
//	
//						// Rule
//						Map<String, Object> rule1 = new HashMap<String, Object>();
//						
//						if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//							rule1.put("DEBIT", "<BROKER>");
//							rule1.put("CREDIT", "<CUSTOMER>");
//						}else {
//								rule1.put("DEBIT", "<CUSTOMER>");
//								rule1.put("CREDIT", "<BROKER>");
//						}
//						
//						rules.add(rule1);
//	
//						 // ThreadLocalRandom.current().ints(1001,
//																			// 4999).distinct().limit(5).findAny().toString();
//						String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																			// 9999).distinct().limit(5).findAny().toString();
//	
//						/*if (StringUtils.isBlank(endttypeid)) {
//							String policyNo = genNo.generatePolicyNo();
//							request.setPolicyNo(policyNo);
//						} else {
//							request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//						}*/
//						int rownum = 1;
//						
//						for (Map<String, Object> map : rules) {
//							for (Entry<String, Object> m : map.entrySet()) {
//	
//								List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//								if(dd!=null) {
//									for (Map<String, Object> s : dd) {
//										DebitAndCredit res =new  DebitAndCredit();
//										String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//	
//										res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//										res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//										res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//										res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//										res.setNarration(s.get("NARATION").toString());
//										res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//										res.setRiskDesc("Passenger Id -" + v.getPassengerId() ) ;
//										res.setBranchCode(request.getBranchCode());
//										res.setChgId(new BigDecimal(rownum++));
//										res.setCompanyId(request.getInsuranceId());
//										res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//												: v1.getQuoteDetails().getLoginId());
//										res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//										res.setDocType(doctype);
//										res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//										res.setEntryDate(new Date());
//										res.setPolicyNo(request.getPolicyNo());
//										res.setProductId(request.getProductId());
//										res.setQuoteNo(request.getQuoteno());
//										res.setStatus("Y");
//										res.setQuoteInfo(v1);
//										res.setSectionId(v.getSectionId().toString());
//										res.setRiskId(v.getPassengerId().toString());
//										resList.add(res);
//									}
//								}
//							}
//						}
//						crdrservice.insertDRCR(resList, request.getQuoteno());
//					}
//					
//					
//	
//				
//				}
//				
//			}
//	
//			// Building and SME Product
//			else if (product.getMotorYn().equalsIgnoreCase("A")) {
//	//				List<EserviceBuildingsDetailsRes> motors = (List<EserviceBuildingsDetailsRes>) v1.getRiskDetails();
//				List<BuildingRiskDetails> motors = buildingRepo.findByQuoteNoAndSectionIdNotOrderByRiskIdAsc(request.getQuoteno() ,"0");
//	
//					for (BuildingRiskDetails v : motors) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//					List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//							v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//					 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//					
//					// Premia Broker , Agent Condition
//					 if(directSourceAvailable == true) {
//						//commissionPercent=12.5;
//						String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//						commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//						
//					} else if(policylist.size()>0 && policylist!=null) {
//						
//						if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//							commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//						} else {
//							commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//									: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//						}
//					}
//					else {
//						commissionPercent=0D;
//					}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				 //  String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//				 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber =  genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//					/*if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//								for (Map<String, Object> s : dd) {
//									DebitAndCredit res =new  DebitAndCredit();
//									String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//									res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//									res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//									res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//									res.setNarration(s.get("NARATION").toString());
//									res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//									res.setRiskDesc( v.getSectionDesc() ) ;
//									res.setBranchCode(request.getBranchCode());
//									res.setChgId(new BigDecimal(rownum++));
//									res.setCompanyId(request.getInsuranceId());
//									res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//											: v1.getQuoteDetails().getLoginId());
//									res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//									res.setDocType(doctype);
//									res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//									res.setEntryDate(new Date());
//									res.setPolicyNo(request.getPolicyNo());
//									res.setProductId(request.getProductId());
//									res.setQuoteNo(request.getQuoteno());
//									res.setStatus("Y");
//									res.setQuoteInfo(v1);
//									res.setSectionId(v.getSectionId());
//									res.setRiskId(v.getRiskId().toString());
//									resList.add(res);
//								}
//							}
//						}
//					}
//					
//				}
//					
//					// Human Icluded
//					List<CommonDataDetails> humans = commonRepo.findByQuoteNo(request.getQuoteno());
//	
//					for (CommonDataDetails v : humans) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//						List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//								v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//						 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						
//						// Premia Broker , Agent Condition
//						 if(directSourceAvailable == true) {
//							//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//							
//						} else if(policylist.size()>0 && policylist!=null) {
//							
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//						}
//						else {
//							commissionPercent=0D;
//						}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc =String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//	
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				
//					//String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber = genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//				/*	if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//	
//							for (Map<String, Object> s : dd) {
//							 	 DebitAndCredit res =new  DebitAndCredit();
//								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//															res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//								res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//								res.setNarration(s.get("NARATION").toString());
//								res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//								res.setRiskDesc( v.getSectionDesc() +"-" + v.getOccupationDesc() ) ;
//								res.setBranchCode(request.getBranchCode());
//								res.setChgId(new BigDecimal(rownum++));
//								res.setCompanyId(request.getInsuranceId());
//								res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//										: v1.getQuoteDetails().getLoginId());
//								res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//								res.setDocType(doctype);
//								res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//								res.setEntryDate(new Date());
//								res.setPolicyNo(request.getPolicyNo());
//								res.setProductId(request.getProductId());
//								res.setQuoteNo(request.getQuoteno());
//								res.setStatus("Y");
//								res.setQuoteInfo(v1);
//								res.setSectionId(v.getSectionId());
//								res.setRiskId(v.getRiskId().toString());
//								resList.add(res);
//							}
//							}
//						}
//					}
//				}
//					
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//			}
//			
//			// Common Product
//			else {
//	//				List<EserviceCommonGetRes> motors = (List<EserviceCommonGetRes>) v1.getRiskDetails();
//					List<CommonDataDetails> motors = commonRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());
//	
//					for (CommonDataDetails v : motors) {
//						 Double commissionPercent = 0.0;
//						String loginId = "b2c".equalsIgnoreCase(v.getSourceType()) ? "guest" : v.getLoginId() ;
//						List<BrokerCommissionDetails> policylist = getPolicyName(v.getCompanyId(),
//								v.getProductId().toString(), loginId, v.getBrokerCode(),"99999");
//						 // Double commissionPercent = v.getCommissionPercentage().doubleValue();
//						
//						// Premia Broker , Agent Condition
//						 if(directSourceAvailable == true) {
//							//commissionPercent=12.5;
//							String  commission = getListItem (homeData.getCompanyId() , homeData.getBranchCode() ,"COMMISSION_PERCENT",homeData.getSourceType() );
//							commissionPercent = StringUtils.isNotBlank(commission) ? Double.valueOf(commission ) : 0D;
//							
//						} else if(policylist.size()>0 && policylist!=null) {
//							
//							if(StringUtils.isNotBlank(homeData.getCommissionModifyYn() ) && "Y".equalsIgnoreCase(homeData.getCommissionModifyYn()) ) {
//								commissionPercent  =  homeData.getCommissionPercentage()==null ? 0D : Double.valueOf(homeData.getCommissionPercentage().toPlainString()) ;
//							} else {
//								commissionPercent =   policylist.get(0).getCommissionPercentage().toString() == null ? 0
//										: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
//							}
//						}
//						else {
//							commissionPercent=0D;
//						}
//					String premiumFc = v.getActualPremiumFc().toString();
//					String vatPremiumFc = String.valueOf( v.getOverallPremiumFc().subtract( v.getActualPremiumFc()));
//	
//					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
//						premiumFc = v.getEndtPremium() ==null ? "0" : v.getEndtPremium().toString();
//						vatPremiumFc = v.getEndtVatPremium()==null  ?"0" :  v.getEndtVatPremium().toPlainString();
//	
//					}
//	
//					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
//							.divide(BigDecimal.valueOf(100D))
//							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
//					v1.getQuoteDetails().getVatPercent();
//				
//					//String endttypeid = v1.getQuoteDetails().getEndtTypeId();
//					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();
//	
//					// Setup
//					Map<String, Object> setup = new HashMap<String, Object>();
//	
//					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						String chargeCode = Double.valueOf(premiumFc)<0 ? "1002" : "1001" ;
//						
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								
//						subset.put("CHARGE_CODE", chargeCode);
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "Premium");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() : "Premium");
//						subset.put("CHARGE_CODE_VALUE", premiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "1");
//						csubsets.add(subset);
//					}
//					{
//						Map<String, Object> subset = new HashMap<String, Object>();
//						List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//						ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//						subset.put("CHARGE_CODE", "1009");
//						subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "VAT");
//						subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" +  homeData.getVatPercent() +"%)" :  "VAT");
//						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
//						subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "2");
//						csubsets.add(subset);
//					}
//					String crnumber ="";
//					if(commissionPercent.doubleValue()>0D) {
//	
//						List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1005")	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", "1005");
//							subset.put("CHARGE_CODE_DESC",  filteredCharge!=null ? filteredCharge.getChartAccountDesc() :  "Commission");
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + commissionPercent +"%)" : "Commission"+ " (" + commissionPercent +"%)");
//							subset.put("CHARGE_CODE_VALUE", commission);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "3");
//							bsubsets.add(subset);
//						}
//	
//						{
//							Map<String, Object> subset = new HashMap<String, Object>();
//							String chargeCode = Double.valueOf(premiumFc)<0 ? "1006" : "1007" ;
//							List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase(chargeCode)	 ).collect(Collectors.toList());
//							ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//							subset.put("CHARGE_CODE", chargeCode);
//							subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() :"Commission%" );
//							subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration() :  "Commission%");
//							subset.put("CHARGE_CODE_VALUE", commissionPercent);
//							subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "4");
//							bsubsets.add(subset);
//						}
//						{// Broker Commmission Vat
//							String brokerLoginId = policylist.size() > 0 ? policylist.get(0).getLoginId() : "";
//							LoginUserInfo loginuser = loginUserRepo.findByLoginId(brokerLoginId);
//							String brokerVatYn = loginuser !=null && loginuser.getTaxExemptedYn()!=null && loginuser.getTaxExemptedYn().equalsIgnoreCase("Y") ? "N" : "Y" ;
//							if(brokerVatYn!=null && brokerVatYn.equalsIgnoreCase("Y") ) {
//								String brokerVatPercent = homeData.getVatPercent()==null ? "0" : homeData.getVatPercent().toPlainString();
//								BigDecimal brokerVatAmount =  commission.multiply(new BigDecimal(brokerVatPercent))
//										.divide(BigDecimal.valueOf(100D))
//										.setScale(new MathContext(0, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);;
//										
//								Map<String, Object> subset = new HashMap<String, Object>();
//								List<ChartOfAccount>  filterChargeCode = getChartList.stream().filter( o -> o.getChartAccountCode().equalsIgnoreCase("1009")	 ).collect(Collectors.toList());
//								ChartOfAccount filteredCharge =   filterChargeCode.size() > 0 ? filterChargeCode.get(0):null ; 
//								subset.put("CHARGE_CODE", "1009");
//								subset.put("CHARGE_CODE_DESC", filteredCharge!=null ? filteredCharge.getChartAccountDesc() : "BrokerCommissionVat");
//								subset.put("NARATION", filteredCharge!=null ? filteredCharge.getNaration()+ " (" + brokerVatPercent +"%)" : "BrokerCommissionVat"+ " (" + brokerVatPercent +"%)");
//								subset.put("CHARGE_CODE_VALUE", brokerVatAmount);
//								subset.put("DISPLAY_ORDER",  filteredCharge!=null ? filteredCharge.getDisplayOrder() :  "5");
//								bsubsets.add(subset);
//							}
//						}
//						setup.put("<BROKER>", bsubsets);
//						crnumber =  genNo.generateCreditNo(branchCode.get(0).getCoreAppCode());
//					}
//					/*
//					 * if(commissionVatYn.equals("Y")) { commissionVat=commission .multiply(new
//					 * BigDecimal(v1.getQuoteDetails().getVatPercent()))
//					 * .divide(BigDecimal.valueOf(100D)) .setScale(new MathContext(3,
//					 * RoundingMode.HALF_UP) .getPrecision(),RoundingMode.HALF_UP);
//					 * 
//					 * 
//					 * Map<String,Object> subset=new HashMap<String, Object>();
//					 * subset.put("CHARGE_CODE", "1012"); subset.put("CHARGE_CODE_DESC",
//					 * "COMMISSON_VAT"); subset.put("CHARGE_CODE_VALUE",commissionVat);
//					 * bsubsets.add(subset); }
//					 * 
//					 */
//					setup.put("<CUSTOMER>", csubsets);
//					
//	
//					// Rule
//					Map<String, Object> rule1 = new HashMap<String, Object>();
//	
//					if("D".equals(v.getStatus())|| ( v.getEndtPremium()!=null && v.getEndtPremium() <0)){
//						rule1.put("DEBIT", "<BROKER>");
//						rule1.put("CREDIT", "<CUSTOMER>");
//					}else {
//							rule1.put("DEBIT", "<CUSTOMER>");
//							rule1.put("CREDIT", "<BROKER>");
//					}
//					
//					rules.add(rule1);
//	
//					 // ThreadLocalRandom.current().ints(1001,
//																		// 4999).distinct().limit(5).findAny().toString();
//					String drnumber = genNo.generateDebitNo(branchCode.get(0).getCoreAppCode()); // ThreadLocalRandom.current().ints(4999,
//																		// 9999).distinct().limit(5).findAny().toString();
//	
//				/*	if (StringUtils.isBlank(endttypeid)) {
//						String policyNo = genNo.generatePolicyNo();
//						request.setPolicyNo(policyNo);
//					} else {
//						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
//					}*/
//					int rownum = 1;
//	
//					for (Map<String, Object> map : rules) {
//						for (Entry<String, Object> m : map.entrySet()) {
//	
//							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
//							if(dd!=null) {
//	
//							for (Map<String, Object> s : dd) {
//							 	 DebitAndCredit res =new  DebitAndCredit();
//								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
//															res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
//								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
//								res.setChargeAccountDesc(s.get("CHARGE_CODE_DESC").toString());
//								res.setNarration(s.get("NARATION").toString());
//								res.setDisplayOrder(s.get("DISPLAY_ORDER").toString());
//								res.setRiskDesc(  v.getSectionDesc() +"-" + v.getOccupationDesc() ) ;
//								res.setBranchCode(request.getBranchCode());
//								res.setChgId(new BigDecimal(rownum++));
//								res.setCompanyId(request.getInsuranceId());
//								res.setDocId(doctype.equals("C") ? v1.getCustomerDetails().getCustomerId()
//										: v1.getQuoteDetails().getLoginId());
//								res.setDocNo(m.getKey().equals("DEBIT") ? drnumber : crnumber);
//								res.setDocType(doctype);
//								res.setDrcrFlag(m.getKey().equals("DEBIT") ? "DR" : "CR");
//								res.setEntryDate(new Date());
//								res.setPolicyNo(request.getPolicyNo());
//								res.setProductId(request.getProductId());
//								res.setQuoteNo(request.getQuoteno());
//								res.setStatus("Y");
//								res.setQuoteInfo(v1);
//								res.setSectionId(v.getSectionId());
//								res.setRiskId(v.getRiskId().toString());
//								resList.add(res);
//							}
//							}
//						}
//					}
//					crdrservice.insertDRCR(resList, request.getQuoteno());
//				}
//				
//			}
//		} catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	 public synchronized String getListItemvalue(String insuranceId , String branchCode, String itemType) {
			String itemvalue = "" ;
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
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
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
			
				
				// Effective Date Start Max Filter
				Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate b3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				Predicate b4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,b3,b4);
				// Effective Date End Max Filter
				Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate b1 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				Predicate b2 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4,b1,b2);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n12 = cb.equal(c.get("status"),"R");
				Predicate n13 = cb.or(n1,n12);
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
				
				query.where(n13,n2,n3,n4,n9,n10);
				
			
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				itemvalue = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return itemvalue ;
	
	}

	 

		public synchronized String getListItemvalue(String insuranceId , String branchCode, String itemType, String vehUsageId, String cusTypeId) {
			String coreappcode = "" ;
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			try {
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
				CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
				// Find All
				Root<ListItemValue> c = query.from(ListItemValue.class);
				
				//Select
				query.select(c);
			
				
				// Effective Date Start Max Filter
				Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
				Predicate b3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				Predicate b4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				effectiveDate.where(a1,a2,b3,b4);
				// Effective Date End Max Filter
				Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
				Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
				Predicate b1 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				Predicate b2 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3,a4,b1,b2);
							
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n12 = cb.equal(c.get("status"),"R");
				Predicate n13 = cb.or(n1,n12);
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
				Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
				Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
				Predicate n7 = cb.equal(c.get("branchCode"), "99999");
				Predicate n9 = cb.or(n6,n7);
				Predicate n10 = cb.equal(c.get("itemType"),itemType );
				Predicate n11 = cb.equal(c.get("itemCode"), vehUsageId);  //Veh USAGE Id  (private, commercial, special)
				Predicate n14 = cb.equal(c.get("param1"), cusTypeId); //Customer TYPE Id (corporate/Indidual)
				
				query.where(n13,n2,n3,n4,n9,n10,n11, n14);
				
			
				// Get Result
				TypedQuery<ListItemValue> result = em.createQuery(query);
				list = result.getResultList();
				
				coreappcode = list.size() > 0 ? list.get(0).getCoreAppCode() : "" ; 
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return coreappcode ;
	
	}
		
	private List<BrokerCommissionDetails> getPolicyName(String companyId, String productId, String loginId,
			String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
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

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a2 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a6 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a7 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			effectiveDate.where(a1,a2,  a3,a4, a5,a6,a7);
			
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("id"), b.get("id"));
			Predicate a10 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a11 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a12 = cb.equal(ocpm2.get("policyType"), b.get("policyType"));
			Predicate a13 = cb.equal(ocpm2.get("loginId"), b.get("loginId"));
			Predicate a14 = cb.equal(ocpm2.get("agencyCode"), b.get("agencyCode"));

			effectiveDate2.where(a8,  a9, a10, a11 ,a12 ,a13, a14);
	
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"), productId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);
		//	Predicate n6 = cb.like(b.get("agencyCode"), "%" + agencyCode + "%");
			Predicate n7 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n8 = cb.equal(b.get("status"), "Y");
			
			query.where(n1, n2, n3, n4, n5,n7,n8);
		
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}
	@Override
	public List<AdminReferral> getReferalList(ReferralApi request) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			  List<SpecCriteria> criterias;
			  List<String> columns=new ArrayList<String>();
			  columns.add("loginId");
			  columns.add("userName");
			  columns.add("userMobile");
			  columns.add("mobileCodeDesc");
			  columns.add("companyName");
			  columns.add("userMail");
			  columns.add("userName");
			  columns.add("whatsappCodeDesc");
			  columns.add("whatsappNo");
			  columns.add("userType");
			  columns.add("subUserType");
			  
			  
			  String s1="userType:Issuer;subUserType:{high,both};companyId:"+request.getInsuranceId()+";attachedBranches%"+request.getBranchCode()+";status:Y;";
			  SpecCriteria c1 = crservice.createCriteria(LoginMaster.class, s1, "loginId",columns);
			  JoinCriteria j1=new JoinCriteria();
			  j1.setColumnName("loginId");
			  j1.setToColumnName("loginId");
			  j1.setToTableName(LoginProductMaster.class);
			  List<JoinCriteria> j1s=new ArrayList<JoinCriteria>();
			  j1s.add(j1);
			  c1.setJoins(j1s);
			  
			  String s2="productId:"+request.getProductId()+";status:Y;"+todayInString+"~effectiveDateStart&effectiveDateEnd;"+request.getSuminsured()+"~sumInsuredStart&sumInsuredEnd";
			  SpecCriteria c2 = crservice.createCriteria(LoginProductMaster.class, s2, "loginId",columns);
			  
			  JoinCriteria j2=new JoinCriteria();
			  j2.setColumnName("loginId");
			  j2.setToColumnName("loginId");
			  j2.setToTableName(LoginMaster.class);
			  
			  JoinCriteria j2_1=new JoinCriteria();
			  j2_1.setColumnName("loginId");
			  j2_1.setToColumnName("loginId");
			  j2_1.setToTableName(LoginUserInfo.class);
			  
			  List<JoinCriteria> j2s=new ArrayList<JoinCriteria>();
			  j2s.add(j2);
			  j2s.add(j2_1);
			  c2.setJoins(j2s);
			  
			  
			  String s3="status:Y;";
			  SpecCriteria c3 = crservice.createCriteria(LoginUserInfo.class, s3, "loginId",columns);
			  
			  JoinCriteria j3=new JoinCriteria();
			  j3.setColumnName("loginId");
			  j3.setToColumnName("loginId");
			  j3.setToTableName(LoginMaster.class);
			  List<JoinCriteria> j3s=new ArrayList<JoinCriteria>();
			  j3s.add(j3);
			  c3.setJoins(j3s);
			  
			  
			  criterias=new ArrayList<SpecCriteria>();
			  criterias.add(c1);
			  criterias.add(c2);
			  criterias.add(c3);
			  
			List<Tuple> joinResult = crservice.getJoinResult(criterias, 0, 0);
			List<AdminReferral> list=new ArrayList<AdminReferral>();
			for (Tuple tuple : joinResult) {
				
				AdminReferral a=AdminReferral.builder()
						.insuranceId((String) tuple.get("companyName"))
						.loginId((String) tuple.get("loginId"))
						.mailId((String) tuple.get("userMail"))
						.mobileCode((String) tuple.get("mobileCodeDesc"))
						.mobileNo((String) tuple.get("userMobile"))
						.userName((String) tuple.get("userName"))
						.whatsappcode((String) tuple.get("whatsappCodeDesc"))
						.whatsAppNo((String) tuple.get("whatsappNo"))
						.uwuserType((String) tuple.get("userType"))
						.uwsubuserType((String) tuple.get("subUserType"))
						.build();
				list.add(a); 
			}
			return list;
		}catch (Exception e) {
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
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
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
	
	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
		String itemDesc = "" ;
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate b3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,b3,b4);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate b1 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4,b1,b2);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			
			query.where(n13,n2,n3,n4,n9,n10,n11).orderBy(orderList);
			
		
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
		} catch (Exception e) {
			e.printStackTrace();
		//	log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}
	
	public List<ChartOfAccount> getChartList(String companyId) {
		List<ChartOfAccount>  list = new ArrayList<ChartOfAccount>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 59);
//			Date todayEnd = cal.getTime();
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ChartOfAccount> query=  cb.createQuery(ChartOfAccount.class);
			
			// Find All
			Root<ChartOfAccount> c = query.from(ChartOfAccount.class);
			//Select
			query.select(c);
			
			
			// Effective Date Start Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<ChartOfAccount> ocpm1 = effectiveDate.from(ChartOfAccount.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("chartAccountCode"),ocpm1.get("chartAccountCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a4= cb.equal(c.get("chartAccountCode"),ocpm1.get("chartAccountCode"));
			effectiveDate.where(a1,a2,a3,a4);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<ChartOfAccount> ocpm2 = effectiveDate2.from(ChartOfAccount.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			Predicate a7 = cb.equal(c.get("chartAccountCode"),ocpm2.get("chartAccountCode"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a10= cb.equal(c.get("chartAccountCode"),ocpm2.get("chartAccountCode"));
			effectiveDate2.where(a7,a8,a9,a10);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("chartAccountCode")));
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n8 = cb.equal(c.get("status"),"R");
			Predicate n9 = cb.or(n1,n8);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"),companyId);
			query.where(n9,n2,n3,n4).orderBy(orderList);
				
			// Get Result
			
			TypedQuery<ChartOfAccount> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
		return list ;
	}
	

	@Override
	public EserviceMotorDetailsSaveRes policyCalculator(CalcEngine engine, String tokens) {
		String isEndt=null;
		List<UWReferrals> referr=null;
		List<MasterReferal> masterreferral = null;
		List<Cover> retc = new ArrayList<Cover>();
		String promocode="";
		loadOnetimetablePolicy(engine);
		String currencyId = policytbl.get(0).get("currency") == null ? "TTT"
				: policytbl.get(0).get("currency").toString();
		
		String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
		String stringFormat = "%0" + decimalDigits + "d";
		String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
		String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
		decimalFormat = new DecimalFormat(pattern);
	 	
		List<Tuple> totalcoverstuple = LoadCoverPolicy(engine);
		
		List<Tuple> taxes = ratingutil.LoadTax(engine,NORMAL_TAX_LIST);
		TaxUtils tzx = new TaxUtils(BigDecimal.ZERO	,"");
		List<Tuple> excludedTaxes = ratingutil.LoadExcludedTax(engine,NORMAL_TAX_LIST);
		TaxRemover taxRemov=new TaxRemover(excludedTaxes,null);
		
		List<String> dependedcovers = new ArrayList<String>();
		dependedcovers.add("N");
		dependedcovers.add("Y");
		for (String dependcover : dependedcovers) {
			List<Cover> totalcovers = new ArrayList<Cover>();
			List<Tuple> covers = totalcoverstuple.stream()
					.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString()))
					.collect(Collectors.toList());
			List<Discount> discounts = null;
			List<Loading> loadings = null;
			if (totalcoverstuple != null && totalcoverstuple.size() > 0) {
				SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
						engine.getPolicyEndDate() ,promocode);
				discounts = totalcoverstuple.stream().map(discountUtil).filter(d -> d != null).collect(Collectors.toList());
				discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
				SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
						engine.getPolicyEndDate());
				loadings = totalcoverstuple.stream().map(loadingtuils).filter(d -> d != null).collect(Collectors.toList());
			}

			SplitSubCoverUtil splitsub = new SplitSubCoverUtil("N", engine.getEffectiveDate(),
					engine.getPolicyEndDate());
			Map<String, List<Cover>> nonSubcovers = covers.stream().map(splitsub).filter(d -> d != null)
					.collect(Collectors.groupingBy(Cover::getIsSubCover));
			if (!nonSubcovers.isEmpty()) {
				List<Cover> noncovers = nonSubcovers.get("N"); // noncovers
				if (!discounts.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Discount> ds = discounts.stream()
								.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						// List<Tax> taxey =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						c.setDiscounts(ds);
						// c.setTaxes(taxey);
					}
				}

				if (!loadings.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));
						// List<Tax> taxey =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						c.setLoadings(ds);
						// c.setTaxes(taxey);
					}
				}

				if (!noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
						}
					}
				}
			}

			splitsub = new SplitSubCoverUtil("Y", engine.getEffectiveDate(), engine.getPolicyEndDate());
			Map<String, List<Cover>> subcovers = covers.stream().map(splitsub)
					.filter(d -> (d != null && !"0".equals(d.getSubCoverId())))
					.collect(Collectors.groupingBy(Cover::getIsSubCover));
			if (!subcovers.isEmpty()) {
				List<Cover> noncovers = subcovers.get("Y"); // noncovers
				if (!discounts.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Discount> ds = discounts.stream()
								.filter(d -> d.getDiscountforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

						List<Discount> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
								.collect(Collectors.toList());
						// List<Tax> taxez =
						// taxes.stream().map(tzx).filter(d->d!=null).collect(Collectors.toList());
						c.setDiscounts(dss);
						// c.setTaxes(taxez);
					}
				}

				if (!loadings.isEmpty() && !noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						List<Loading> ds = loadings.stream().filter(d -> d.getLoadingforId().equals(c.getCoverId()))
								.collect(Collectors.toList());
						ds.stream().forEach(dss -> dss.setSubCoverId(c.getSubCoverId()));

						List<Loading> dss = ds.stream().map(dx -> SerializationUtils.clone(dx))
								.collect(Collectors.toList());
						c.setLoadings(dss);
					}
				}
				if (!noncovers.isEmpty()) {
					for (Cover c : noncovers) {
						if(!c.getCoverageType().equals("A") && !c.getIsTaxExcempted().equals("Y")) {
						List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
								.collect(Collectors.toList());
						c.setTaxes(taxey);
						}
					}
				}

				List<Cover> d = noncovers.stream().filter(SubCoverCreationUtil.distinctByKey(Cover::getCoverId))
						.collect(Collectors.toList());
				List<Cover> subcov = new ArrayList<Cover>();
				for (Cover cover : d) {
					List<Cover> subcover = noncovers.stream()
							.filter(cv -> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
					subcover.stream().forEach(s -> s.setIsSubCover("N"));
					// subcover.stream().forEach(s->s.setTaxes(new ArrayList<Tax>(taxez)));
					Cover newcover = SerializationUtils.clone(cover);
					newcover.setSubcovers(subcover);
					newcover.setIsSubCover("Y");
					newcover.setSubCoverId(null);
					newcover.setSubCoverDesc(null);
					newcover.setSubCoverName(null);
					newcover.setDiscounts(null);
					newcover.setLoadings(null);
					newcover.setTaxes(null);
					subcov.add(newcover);
				}
				subcovers.put("Y", subcov);
			}

			if (!nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
				totalcovers = subcovers.get("Y");
				totalcovers.addAll(nonSubcovers.get("N"));
			} else if (!nonSubcovers.isEmpty() && subcovers.isEmpty()) {
				totalcovers = nonSubcovers.get("N");
			} else if (nonSubcovers.isEmpty() && !subcovers.isEmpty()) {
				totalcovers = subcovers.get("Y");
			}
			
			
			totalcovers.stream().forEach(taxRemov);
			/*
			 * if(StringUtils.isNotBlank(engine.getVdRefNo()) &&
			 * StringUtils.isNotBlank(engine.getCdRefNo())) { //calc.setEngine(engine,
			 * retc);
			 * 
			 * 
			 * }
			 */

			/*CoverCalculator calc = new CoverCalculator();
			calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat);*/
			PolicyCoverCalculator calc=new PolicyCoverCalculator(policytbl,ratingutil,engine,decimalFormat,customers,false);
			retc.stream().forEach(calc);

			totalcovers.stream().forEach(calc);
			// remove error records
			totalcovers.removeIf(ll -> (ll.isNotsutable()));
			retc.addAll(totalcovers);
			Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
			retc.sort(comp);
		}
		//if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
		BigDecimal totalPremium=retc.stream().filter(x -> (!"N".equals(x.getIsselected()) && !"945".equals(x.getCoverId()) && x.getPremiumExcluedTaxLC()!=null)).map(x -> x.getPremiumExcluedTaxLC()).reduce(BigDecimal.ZERO,BigDecimal::add);
		if(totalPremium.compareTo(minimumPremium)<0) {
			List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
					.collect(Collectors.toList());
			BigDecimal difference=minimumPremium.subtract(totalPremium,MathContext.DECIMAL32);
			CreateMinimumPremium min=new CreateMinimumPremium(difference, engine, BigDecimal.ZERO, taxey);
			Cover mini = min.create();
			List<Cover> minies=new ArrayList<Cover>(1);
			minies.add(mini);
			PolicyCoverCalculator calc=new PolicyCoverCalculator(policytbl,ratingutil,engine,decimalFormat,customers,false);
			//calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat,drivers);
			minies.stream().forEach(calc);
			retc.add(mini);
			
		}else {
			retc.removeIf(t -> "945".equals(t.getCoverId()));//.stream().filter(t-> "945".equals(t.getCoverId()).de
		}
		
		 
		
		try {

			String endtTypeId = policytbl.get(0).get("endtTypeId") == null ? "": policytbl.get(0).get("endtTypeId").toString();
			if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
			  			//	loadAndRemoveCoversForEndt(engine, retc, result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	  
		
		
		try {
			EserviceMotorDetailsSaveRes response = new EserviceMotorDetailsSaveRes();
			response.setCoverList(retc);
			response.setResponse("Saved Successfully");
			response.setRequestReferenceNo(engine.getRequestReferenceNo());
			// response.setCustomerReferenceNo(req.getCustomerReferenceNo());
			response.setVehicleId(engine.getVehicleId());
			response.setVdRefNo(engine.getPdrefno());
			response.setCdRefNo(engine.getCdRefNo());
			response.setInsuranceId(engine.getInsuranceId());
			response.setSectionId(engine.getSectionId());
			response.setCreatedBy(engine.getCreatedBy());
			response.setProductId(engine.getProductId());
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(isEndt);
			response.setUwList(referr);
			response.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(response); 
			return response;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<Tuple> LoadCoverPolicy(CalcEngine engine) {

		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			
			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:99999;status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:99999;status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;agencyCode:99999;branchCode:99999;";

			SpecCriteria commonCriteria = crservice.createCriteria(SectionCoverMaster.class, search4, "coverId");
			List<Tuple> commonResult = crservice.getResult(commonCriteria, 0, 50);
			
			
			SpecCriteria criteria = null;		
			
			criteria = crservice.createCriteria(SectionCoverMaster.class, search2, "coverId");
			List<Long> count = crservice.getCount(criteria, 0, 50);
			if (!count.isEmpty()) {
				Long countrec = count.get(0);
				if (countrec > 0) {
					List<Tuple> specific = crservice.getResult(criteria, 0, 50);
					for(Tuple t:specific) {
						commonResult.removeIf(c-> c.get("coverId").toString().equals(t.get("coverId").toString()));
						commonResult.add(t);
					}
				}
					
			}

			return commonResult;
		} catch (Exception e) {
			e.printStackTrace();
		} 		 	
		return null;  
	}
	
	
	public void loadOnetimetablePolicy(CalcEngine engine) {
 
		try {
			SpecCriteria criteria = null;
		 	String search = "pdRefno:" + engine.getPdrefno() + ";";
 			criteria = crservice.createCriteria(MsPolicyDetails.class, search, "pdRefno");
			policytbl = crservice.getResult(criteria, 0, 50); 
			String cdRefno=policytbl.get(0).get("cdRefno").toString();
			search = "cdRefno:" + cdRefno + ";";
			criteria = crservice.createCriteria(MsCustomerDetails.class, search, "cdRefno");
			customers = crservice.getResult(criteria, 0, 50);
			
			String todayInString = DD_MM_YYYY.format(new Date());
			
			search="companyId:"+engine.getInsuranceId()+";status:Y;"+ todayInString+ "~effectiveDateStart&effectiveDateEnd;productId:"+engine.getProductId()+";";
			criteria = crservice.createCriteria(CompanyProductMaster.class, search, "productId");
			List<Tuple> result = crservice.getResult(criteria, 0, 50);
			if(result!=null && result.size()>0) {
				minimumPremium=result.get(0).get("minimumPremium")==null?BigDecimal.ZERO:new BigDecimal(result.get(0).get("minimumPremium").toString());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void policyReferralCalc(CalcEngine engine) {
		try {
			
			List<FactorRateRequestDetails> factors = repository.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc
					(engine.getRequestReferenceNo(), Integer.valueOf(99999),Integer.valueOf(engine.getProductId()), Integer.valueOf(99999));
			engine.setPdrefno(factors.get(0).getVdRefno());
			loadOnetimetablePolicy(engine);
			List<FactorRateRequestDetails> covers = factors.stream().collect(Collectors.toList());

			DiscountFromFactor discountUtil = new DiscountFromFactor();
			List<Discount> discounts = covers.stream().map(discountUtil).filter(d -> d != null)
					.collect(Collectors.toList());
			LoadingFromFactor loadingtuils = new LoadingFromFactor();
			List<Loading> loadings = covers.stream().map(loadingtuils).filter(d -> d != null)
					.collect(Collectors.toList());
			CreatePolicyPremium c=new CreatePolicyPremium(engine,null);
			Cover create = c.create();
			create.setDiscounts(discounts);
			create.setLoadings(loadings);
			
			List<Tuple> taxes = ratingutil.LoadTax(engine,Arrays.asList("NB"));	
			TaxUtils tzx = new TaxUtils(BigDecimal.ZERO,"");
			List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null).collect(Collectors.toList());
			create.setTaxes(taxey);
			List<Cover> policyCovers=new ArrayList<Cover>();
			policyCovers.add(create);
			PolicyCoverCalculator calc=new PolicyCoverCalculator(policytbl,ratingutil,engine,decimalFormat,customers,true);
			policyCovers.stream().forEach(calc);

			
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}	
	private Date getZeroTimeDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		return date;
	}

	@Override
	public List<EserviceMotorDetailsSaveRes> getCalc(CalcEngine request, String token) {
		 List<EserviceMotorDetailsSaveRes> resList=new ArrayList<EserviceMotorDetailsSaveRes>();
		try {
			Integer locationId=0;
			String riskId="";
			String sectionId="";
			
			CalcEngine engine=new CalcEngine();
			System.out.println("Calculator Calling Api");
			List<EserviceSectionDetails> secList= esSecRepo.findByRequestReferenceNo(request.getRequestReferenceNo());
			if(secList!=null) {
			Set<Integer> findlocationid = secList.stream().map(EserviceSectionDetails::getLocationId)
					.distinct().collect(Collectors.toSet());
			System.out.println("Total Location Ids :"+findlocationid);
			if(findlocationid!=null ) {
			for (Integer data : findlocationid) {
				locationId=data;
				System.out.println("Location Id "+data);
				List<EserviceSectionDetails> secFilter = secList.stream()
					.filter(o -> o.getLocationId().equals(data))
					.collect(Collectors.toList());
				for(EserviceSectionDetails s:secFilter) {
					 if("A".equalsIgnoreCase(s.getProductType())) {
							List<EserviceBuildingDetails> buildingdata =eservicebuildingRepo
									.findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(),data);
							List<EserviceBuildingDetails> building=buildingdata.stream()
									.filter(o -> o.getLocationId().equals(data) && o.getSectionId().equals(s.getSectionId())
											&& o.getRiskId().equals(s.getRiskId()))
									.collect(Collectors.toList());
							
							for (EserviceBuildingDetails bd : building) {
								{
									engine.setLocationId(bd.getLocationId().toString());
									engine.setBranchCode(bd.getBranchCode());
									engine.setInsuranceId(bd.getCompanyId());
									engine.setSectionId(bd.getSectionId());
									engine.setProductId(bd.getProductId());
									engine.setMsrefno(bd.getMsRefno().toString());
									engine.setCdRefNo(bd.getCdRefno().toString());
									engine.setVdRefNo(bd.getVdRefNo().toString());
									engine.setCreatedBy(bd.getCreatedBy());
									engine.setRequestReferenceNo(bd.getRequestReferenceNo());
									engine.setEffectiveDate(bd.getPolicyStartDate());
									engine.setPolicyEndDate(bd.getPolicyEndDate());
									engine.setCoverModification("N");
									engine.setVehicleId(bd.getRiskId().toString());		
									EserviceMotorDetailsSaveRes res= calculator( engine,  token) ;
									resList.add(res);
									}
							}
					 }else  if("H".equalsIgnoreCase(s.getProductType())) {
						 List<EserviceCommonDetails> comdata = eservicecommonRepo.findByRequestReferenceNoAndLocationId(request.getRequestReferenceNo(),data);
						 List<EserviceCommonDetails> common=comdata.stream()
									.filter(o -> o.getLocationId().equals(data) && o.getSectionId().equals(s.getSectionId())
											&& o.getRiskId().equals(s.getRiskId()))
									.collect(Collectors.toList());
						 for (EserviceCommonDetails cd : common) {
								engine.setLocationId(cd.getLocationId().toString());
								engine.setBranchCode(cd.getBranchCode());
								engine.setInsuranceId(cd.getCompanyId());
								engine.setSectionId(cd.getSectionId());
								engine.setProductId(cd.getProductId());
								engine.setMsrefno(cd.getMsRefno().toString());
								engine.setCdRefNo(cd.getCdRefno().toString());
								engine.setVdRefNo(cd.getVdRefNo().toString());
								engine.setCreatedBy(cd.getCreatedBy());
								engine.setRequestReferenceNo(cd.getRequestReferenceNo());
								engine.setEffectiveDate(cd.getPolicyStartDate());
								engine.setPolicyEndDate(cd.getPolicyEndDate());
								engine.setCoverModification("N");
								engine.setVehicleId(cd.getRiskId().toString());		
								EserviceMotorDetailsSaveRes res= calculator( engine,  token) ;
								resList.add(res);
						 }
						 
					 }
				}
			}
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resList;
	}
}

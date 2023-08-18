package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsCustomerDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.calculator.util.AdminCoverCalculator;
import com.maan.eway.calculator.util.CoverCalculator;
import com.maan.eway.calculator.util.CoverFromFactor;
import com.maan.eway.calculator.util.DiscountFromFactor;
import com.maan.eway.calculator.util.EndtCoverCalculator;
import com.maan.eway.calculator.util.EndtFromFactor;
import com.maan.eway.calculator.util.LoadingFromFactor;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.SplitDiscountUtils;
import com.maan.eway.calculator.util.SplitLoadingUtils;
import com.maan.eway.calculator.util.SplitSubCoverUtil;
import com.maan.eway.calculator.util.SubCoverCreationUtil;
import com.maan.eway.calculator.util.TaxFromFactor;
import com.maan.eway.calculator.util.TaxUtils;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.endorsment.util.CoverFromPolicy;
import com.maan.eway.endorsment.util.DiscountFromPolicy;
import com.maan.eway.endorsment.util.LoadingFromPolicy;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
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

@Service
public class CalculatorEngineService implements CalculatorEngine {

	// 1.Section
	// 2.Cover

	@Autowired
	private CriteriaService crservice;

	@Autowired
	private RatingFactorsUtil ratingutil;
	/*
	 * @Autowired private CoverCalculator calc;
	 */
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	
	protected List<Tuple> commontbl = null;
	protected List<Tuple> vehicles = null;
	protected List<Tuple> customers = null;
	protected List<Cover> calculatedcover = null;
	protected List<Tuple> prorata = null;

	@Autowired
	private FactorRateRequestDetailsService fservice;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private FactorRateRequestDetailsRepository repository;

	@Autowired
	private ReferalServiceImpl referal;

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
 
	public List<Tuple> LoadCover(CalcEngine engine) {
		try {
			String todayInString = DD_MM_YYYY.format(new Date());
			String search1 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode() + ";branchCode:"
					+ engine.getBranchCode() + ";";

			String search2 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";

			String search3 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:" + engine.getAgencyCode()
					+ ";branchCode:99999;";

			String search4 = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
					+ ";sectionId:" + engine.getSectionId() + ";status:{Y,R};" + todayInString
					+ "~effectiveDateStart&effectiveDateEnd;" + "agencyCode:99999;branchCode:99999;";

			Map<Integer, String> hsmap = new TreeMap<Integer, String>();
			hsmap.put(1, search1);
			hsmap.put(2, search2);
			hsmap.put(3, search3);
			hsmap.put(4, search4);

			SpecCriteria criteria = null;

			for (int i = 1; i <= hsmap.size(); i++) {
				String dataquery = hsmap.get(i);

				criteria = crservice.createCriteria(SectionCoverMaster.class, dataquery, "coverId");

				List<Long> count = crservice.getCount(criteria, 0, 50);
				if (!count.isEmpty()) {
					Long countrec = count.get(0);
					if (countrec > 0)
						break;
				}

			}

			if (criteria != null) {
				List<Tuple> result = null;
				result = crservice.getResult(criteria, 0, 50);
				return result;
			}
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
			TaxUtils tzx = new TaxUtils(endtCount);

			List<String> dependedcovers = new ArrayList<String>();
			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<Tuple> totalcoverstuple = LoadCover(engine);
			// effectiveDateStart&effectiveDateEnd

			for (String dependcover : dependedcovers) {
				List<Cover> totalcovers = new ArrayList<Cover>();
				List<Tuple> covers = totalcoverstuple.stream()
						.filter(t -> dependcover.equals(t.get("dependentCoverYn").toString()))
						.collect(Collectors.toList());
				List<Discount> discounts = null;
				List<Loading> loadings = null;
				if (covers != null && covers.size() > 0) {
					SplitDiscountUtils discountUtil = new SplitDiscountUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate() ,promocode);
					discounts = covers.stream().map(discountUtil).filter(d -> d != null).collect(Collectors.toList());
					discounts.stream().forEach(t -> t.setEffectiveDate(engine.getEffectiveDate()));
					SplitLoadingUtils loadingtuils = new SplitLoadingUtils(engine.getEffectiveDate(),
							engine.getPolicyEndDate());
					loadings = covers.stream().map(loadingtuils).filter(d -> d != null).collect(Collectors.toList());
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
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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

				/*
				 * if(StringUtils.isNotBlank(engine.getVdRefNo()) &&
				 * StringUtils.isNotBlank(engine.getCdRefNo())) { //calc.setEngine(engine,
				 * retc);
				 * 
				 * 
				 * }
				 */

				CoverCalculator calc = new CoverCalculator();
				calc.setEngine(engine, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat);

				totalcovers.stream().forEach(calc);
				// remove error records
				totalcovers.removeIf(ll -> (ll.isNotsutable()));
				retc.addAll(totalcovers);
				Comparator<Cover> comp = Comparator.comparing(Cover::getCoverageType);
				retc.sort(comp);
			}
			
			try {
				String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? ""
						: vehicles.get(0).get("endtTypeId").toString();
				if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
					String requestRefercenNo = engine.getRequestReferenceNo();
					String rawtable = ratingutil.getProductIdBasedRawTable(engine);
					String search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
							+ ";sectionId:" + engine.getSectionId() + ";riskId:" + engine.getVehicleId()
							+ ";status:{E,D};requestReferenceNo:" + requestRefercenNo + ";";
					SpecCriteria criteria = crservice.createCriteria(Class.forName(rawtable), search,
							"requestReferenceNo");
					List<Long> count = crservice.getCount(criteria,0,2);
					if (!count.isEmpty() && count.get(0)<=0) {

						String riskid = engine.getVehicleId();
						search = "companyId:" + engine.getInsuranceId() + ";productId:" + engine.getProductId()
								+ ";riskId:" + riskid + ";status:{E,D};requestReferenceNo:" + requestRefercenNo + ";";
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
			response.setMsrefno(engine.getMsrefno());
			response.setUpdateas(isEndt);
			response.setUwList(referr);
			response.setReferals(masterreferral);
			fservice.saveFactorRateRequestDetails(response);

			// Update Premium,referral

			/// Endoresment calculation
			try {
				String endtTypeId = vehicles.get(0).get("endtTypeId") == null ? ""
						: vehicles.get(0).get("endtTypeId").toString();
				if (StringUtils.isNotBlank(endtTypeId) && !"0".equals(endtTypeId)) {
					// referalCalculator = referalCalculator(engine);
					return endorsementCalculator(engine, endtCount);

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

				EndtTypeMaster endtmaster = ratingutil.getEndtMasterData(engine.getInsuranceId(), engine.getProductId(),
						endtTypeId);

				retc.stream().forEach(i -> i.setEndtCount(endtCount));
			/*	List<PolicyCoverData> oldPolicyCovers =ratingutil.findDataForTravel(
						endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
						Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()), "Y");*/
				// find Prev Quote Data
				List<PolicyCoverData> oldPolicyCovers = coverDataRepo
						.findByQuoteNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndStatusOrderByCoverIdAsc(
								endtPrevQuoteNo, Integer.parseInt(engine.getVehicleId()), engine.getInsuranceId(),
								Integer.parseInt(engine.getProductId()), Integer.parseInt(engine.getSectionId()), "Y");
				List<Tuple> taxes = ratingutil.LoadTax(engine,NORMAL_TAX_LIST);
				TaxUtils tzx = new TaxUtils(endtCount);

				// CoverFromPolicy
				List<PolicyCoverData> basecovers = oldPolicyCovers.stream()
						.filter(d -> !("T".equals(d.getCoverageType()) || "D".equals(d.getCoverageType())
								|| "L".equals(d.getCoverageType()) || "E".equals(d.getCoverageType())
								|| "P".equals(d.getCoverageType())))
						.collect(Collectors.toList());
				for (PolicyCoverData d : basecovers) {
					List<Cover> operatedList = new ArrayList<Cover>();

					DiscountFromPolicy discountUtil = new DiscountFromPolicy();
					List<Discount> discounts = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(discountUtil).filter(dx -> dx != null).collect(Collectors.toList());

					LoadingFromPolicy loadingUtil = new LoadingFromPolicy();
					List<Loading> loadings = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(loadingUtil).filter(dx -> dx != null).collect(Collectors.toList());

					/*
					 * EndtFromPolicy endtUtils=new EndtFromPolicy(); List<Endorsement> endorsements
					 * = oldPolicyCovers.stream().filter(r ->
					 * d.getCoverId()==r.getCoverId()).map(endtUtils).filter(dx->dx!=null).collect(
					 * Collectors.toList());
					 * 
					 * TaxFromPolicy endttaxUtil=new TaxFromPolicy(); if(endorsements!=null &&
					 * endorsements.size()>0) { for (Endorsement e : endorsements) { List<Tax> txx =
					 * oldPolicyCovers.stream().filter(r -> (d.getCoverId() ==r.getCoverId() &&
					 * "T".equals(r.getCoverageType()) &&
					 * r.getEndtCount().intValue()==e.getEndtCount().intValue() && r.getCoverId()
					 * ==Integer.parseInt(e.getEndorsementforId()) )
					 * ).map(endttaxUtil).filter(dx->dx!=null).collect(Collectors.toList());
					 * e.setTaxes(txx); } }
					 */
					// CurrentEndorsement

					List<Endorsement> endorsements = new ArrayList<Endorsement>();

					Endorsement currentEndt = Endorsement.builder()
							.endorsementDesc(d.getCoverDesc() + " " + endtDesc/* +" "+endtCount.intValue() */)
							.endorsementId(endtTypeId).endorsementRate(d.getRate().toPlainString())
							.endorsementCalcType("A").endorsementforId(String.valueOf(d.getCoverId()))
							.maxAmount(BigDecimal.ZERO).factorTypeId(null).regulatoryCode("N/A").endtCount(endtCount)
							.premiumAfterDiscount(d.getPremiumAfterDiscountFc())
							.premiumAfterDiscountLC(d.getPremiumAfterDiscountLc())
							.premiumBeforeDiscount(d.getPremiumBeforeDiscountFc())
							.premiumBeforeDiscountLC(d.getPremiumBeforeDiscountLc())
							.premiumExcluedTax(d.getPremiumExcludedTaxFc())
							.premiumExcluedTaxLC(d.getPremiumExcludedTaxLc())
							.premiumIncludedTax(d.getPremiumIncludedTaxFc())
							.premiumIncludedTaxLC(d.getPremiumIncludedTaxLc())
							.proRata(BigDecimal.ZERO).proRataYn("N")
							.build();

					{
						List<Tax> taxey = taxes.stream().map(tzx).filter(t -> t != null).collect(Collectors.toList());
						taxey.stream().forEach(t -> t.setEndtTypeId(endtTypeId + ""));
						taxey.stream().forEach(t -> t.setEndtTypeCount(endtCount));
						taxey.stream().forEach(t -> t.setTaxDesc(/* endtDesc +" "+ */t.getTaxDesc()));
						if ("Y".equals(endtmaster.getEndtFeeYn())) {
							Tax tax = Tax.builder().calcType(endtmaster.getCalcTypeId()).isTaxExempted("N")
									.regulatoryCode("N/A").taxAmount(BigDecimal.ZERO)
									.taxDesc(/* endtDesc+ */" Endorsement Fee"/* +" "+endtCount.intValue() */)
									.taxExemptCode(null).taxRate(Double.parseDouble(endtmaster.getEndtFeePercent()))
									.taxId(endtTypeId + "").endtTypeId(endtTypeId + "").endtTypeCount(endtCount)
									.build();
							taxey.add(tax);
						}

						currentEndt.setTaxes(taxey);
					}
					endorsements.add(currentEndt);

					CoverFromPolicy coverUtil = new CoverFromPolicy("");
					List<Cover> covers = oldPolicyCovers.stream().filter(r -> d.getCoverId() == r.getCoverId())
							.map(coverUtil).filter(dx -> dx != null).collect(Collectors.toList());
					/*
					 * List<Cover> oldTax = covers.stream().filter(c ->
					 * "T".equals(c.getCoverageType())).collect(Collectors.toList());
					 * covers.removeAll(oldTax);
					 */
					List<Tax> taxey = taxes.stream().map(tzx).filter(t -> t != null).collect(Collectors.toList());
					covers.forEach(c -> c.setTaxes(taxey));
					covers.forEach(c -> c.setEndtCount(endtCount));
					covers.forEach(c -> c.setEndorsements(endorsements));// Existing Endorsement
					covers.forEach(c -> c.setDiscounts(discounts));
					covers.forEach(c -> c.setLoadings(loadings));

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

	public EserviceMotorDetailsSaveRes endorsementCalculator(CalcEngine request, BigDecimal endtCount) {
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
			List<Tuple> taxes = ratingutil.LoadTax(request,ENDT_TAX_LIST);
			TaxUtils tzx = new TaxUtils(endtCount);

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

							TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (ds != null && ds.size() > 0) {
								for (Endorsement e : ds) {

									// only for endrose we cannt use cover objs tax cover wontbe list.
									List<Tax> txx = factors.stream()
											.filter(r -> (r.getDiscLoadId() == Integer.parseInt(e.getEndorsementId())
													&& r.getCoverId() == Integer.parseInt(e.getEndorsementforId())
													&& r.getEndtCount().intValue() == e.getEndtCount().intValue()))
											.map(endttaxUtil).filter(dx -> (dx != null && !"0".equals(dx.getTaxId())))
											.collect(Collectors.toList());
									e.setTaxes(txx);
								}
							}

							c.setEndorsements(ds);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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

							TaxFromFactor endttaxUtil = new TaxFromFactor();
							if (dss != null && dss.size() > 0) {
								for (Endorsement e : dss) {
									List<Tax> txx = covers.stream()
											.filter(r -> (r.getDiscLoadId() == Integer.parseInt(e.getEndorsementId())
													&& r.getCoverId() == Integer.parseInt(e.getEndorsementforId())
													&& r.getEndtCount().intValue() == e.getEndtCount().intValue()))
											.map(endttaxUtil).filter(dx -> dx != null).collect(Collectors.toList());
									e.setTaxes(txx);
								}
							}

							c.setEndorsements(dss);
							// c.setTaxes(taxey);
						}
					}

					if (!noncovers.isEmpty()) {
						for (Cover c : noncovers) {
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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

				EndtCoverCalculator calc = new EndtCoverCalculator();
				
				if ((commontbl == null || commontbl.size() == 0) || (vehicles == null || vehicles.size() == 0)
						|| (customers == null || customers.size() == 0)) {
					loadOnetimetable(request);
				}
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil,
						request.getEffectiveDate(), decimalFormat);

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
			String oneProduct = ratingutil.collectProductType(engine);

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
						search = "vdRefno:" + engine.getVdRefNo() + ";vehicleId:" + engine.getVehicleId();
						criteria = crservice.createCriteria(MsVehicleDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					} else if (oneProduct.equals("H")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";humanId:" + engine.getVehicleId();
						criteria = crservice.createCriteria(MsHumanDetails.class, search, "vdRefno");
						vehicles = crservice.getResult(criteria, 0, 50);
					} else if (oneProduct.equalsIgnoreCase("A")) {
						search = "vdRefno:" + engine.getVdRefNo() + ";locationId:" + engine.getVehicleId();
						criteria = crservice.createCriteria(MsAssetDetails.class, search, "vdRefno");
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
					prorata = ratingutil.loadProRataData(engine, periodOfInsurance);

					String currencyId = vehicles.get(0).get("currency") == null ? "TTT"
							: vehicles.get(0).get("currency").toString();
					String decimalDigits = ratingutil.currencyDecimalFormat(engine.getInsuranceId(), currencyId);
					String stringFormat = "%0" + decimalDigits + "d";
					String decimalLength = decimalDigits.equals("0") ? "" : String.format(stringFormat, 0L);
					String pattern = StringUtils.isBlank(decimalLength) ? "#####0" : "#####0." + decimalLength;
					decimalFormat = new DecimalFormat(pattern);
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

			List<String> dependedcovers = new ArrayList<String>();

			dependedcovers.add("N");
			dependedcovers.add("Y");

			List<FactorRateRequestDetails> factors = repository
					.findByRequestReferenceNoAndVehicleIdAndProductIdAndSectionIdOrderByCoverIdAsc(
							request.getRequestReferenceNo(), Integer.valueOf(request.getVehicleId()),
							Integer.valueOf(request.getProductId()), Integer.valueOf(request.getSectionId()));

			// TaxFromFactor tzx=new TaxFromFactor();
			List<Tuple> taxes = ratingutil.LoadTax(request,NORMAL_TAX_LIST);
			TaxUtils tzx = new TaxUtils(BigDecimal.ZERO);

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
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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
							List<Tax> taxey = taxes.stream().map(tzx).filter(d -> d != null)
									.collect(Collectors.toList());
							c.setTaxes(taxey);
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
				calc.setEngine(request, retc, commontbl, vehicles, customers, prorata, ratingutil, decimalFormat);

				totalcovers.stream().forEach(calc);
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

	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Override
	public List<DebitAndCredit> commissionCalc(CalcCommission request) {
		List<DebitAndCredit> resList = new ArrayList<DebitAndCredit>();

		try {
			ViewQuoteReq q = new ViewQuoteReq();
			q.setQuoteNo(request.getQuoteno());
			ViewQuoteRes v1 = quoteservice.viewQuoteDetails(q);
			CompanyProductMaster product =  getCompanyProductMasterDropdown(v1.getQuoteDetails().getCompanyId() , v1.getQuoteDetails().getProductId().toString());

			if (product.getMotorYn().equalsIgnoreCase("M")) {
				List<MotorDataDetails> motors = motorRepo.findByQuoteNoOrderByVehicleIdAsc(request.getQuoteno());
 				//List<EserviceMotorDetailsRes> motors = (List<EserviceMotorDetailsRes>) v1.getRiskDetails();
				for (MotorDataDetails v : motors) {
					Double commissionPercent = 0.0;
					/*List<BrokerCommissionDetails> policylist = getPolicyName(request.getInsuranceId(),
							request.getProductId(), request.getCreatedBy(), request.getAgencyCode(), v.getPolicyType());
					 
						if(policylist.size()>0 && policylist!=null) {
						
					commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
							: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
						}
						else {
							commissionPercent=5.0;
						}*/
					commissionPercent=v.getCommissionPercentage().doubleValue();
					
					String premiumFc = v.getActualPremiumFc().toString();
					String vatPremiumFc = v.getVatPremium().toPlainString();

					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
						premiumFc = v.getEndtPremium().toString();
						vatPremiumFc = v.getEndVatPremium().toPlainString();
					}

					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
							.divide(BigDecimal.valueOf(100D))
							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
					//totalcommission = totalcommission.add(commission);

					String endttypeid = v1.getQuoteDetails().getEndtTypeId();
					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();

					// Setup
					Map<String, Object> setup = new HashMap<String, Object>();

					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1001");
						subset.put("CHARGE_CODE_DESC", "Premium");
						subset.put("CHARGE_CODE_VALUE", premiumFc);
						csubsets.add(subset);
					}
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1012");
						subset.put("CHARGE_CODE_DESC", "VAT");
						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
						csubsets.add(subset);
					}

					List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1005");
						subset.put("CHARGE_CODE_DESC", "Commission");
						subset.put("CHARGE_CODE_VALUE", commission);
						bsubsets.add(subset);
					}

					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1007");
						subset.put("CHARGE_CODE_DESC", "Commission%");
						subset.put("CHARGE_CODE_VALUE", commissionPercent);
						bsubsets.add(subset);
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
					setup.put("<CUSTOMER>", csubsets);
					setup.put("<BROKER>", bsubsets);

					// Rule
					Map<String, Object> rule1 = new HashMap<String, Object>();
					
					if("D".equals(v.getStatus())){
						rule1.put("DEBIT", "<BROKER>");
						rule1.put("CREDIT", "<CUSTOMER>");
					}else {
							rule1.put("DEBIT", "<CUSTOMER>");
							rule1.put("CREDIT", "<BROKER>");
					}
					rules.add(rule1);

					String crnumber = "CN-" + genNo.generateCreditNo(); // ThreadLocalRandom.current().ints(1001,
																		// 4999).distinct().limit(5).findAny().toString();
					String drnumber = "DN-" + genNo.generateDebitNo(); // ThreadLocalRandom.current().ints(4999,
																		// 9999).distinct().limit(5).findAny().toString();

					if (StringUtils.isBlank(endttypeid)) {
						String policyNo = genNo.generatePolicyNo();
						request.setPolicyNo(policyNo);
					} else {
						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
					}
					int rownum = 1;

					for (Map<String, Object> map : rules) {
						for (Entry<String, Object> m : map.entrySet()) {

							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
							for (Map<String, Object> s : dd) {
							 	 DebitAndCredit res =new  DebitAndCredit();
								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
								
								res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
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
								res.setRiskId(v.getVehicleId());
								res.setQuoteInfo(v1);
								res.setSectionId(request.getSectionId());
								resList.add(res);
							}
						}
					}
					crdrservice.insertDRCR(resList, request.getQuoteno());
				}
				
			}

			// Travel Product
			else if (product.getMotorYn().equalsIgnoreCase("H") && request.getProductId().equalsIgnoreCase(travelProductId)) {
 				//List<EserviceTravelGetRes> motors = (List<EserviceTravelGetRes>) v1.getRiskDetails();
				List<TravelPassengerDetails> motors = travelRepo.findByQuoteNoOrderByTravelIdAsc(request.getQuoteno());

 				for (TravelPassengerDetails v : motors) {

					/*List<BrokerCommissionDetails> policylist = getPolicyName(request.getInsuranceId(),
							request.getProductId(), request.getCreatedBy(), request.getAgencyCode(), v.getSectionId().toString());*/
					 Double commissionPercent = v.getCommissionPercentage().doubleValue();
					/*	if(policylist.size()>0 && policylist!=null) {
						
					commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
							: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
						}
						else {
							commissionPercent=5.0;
						}*/
					String premiumFc = v.getActualPremiumFc().toString();
					String vatPremiumFc = v.getActualPremiumFc().toString();

					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
						premiumFc = v1.getQuoteDetails().getEndtPremium().toPlainString();
						vatPremiumFc = v1.getQuoteDetails().getEndtPremiumTax().toPlainString();
					}

					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
							.divide(BigDecimal.valueOf(100D))
							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
					v1.getQuoteDetails().getVatPercent();
					String endttypeid = v1.getQuoteDetails().getEndtTypeId();
					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();

					// Setup
					Map<String, Object> setup = new HashMap<String, Object>();

					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1001");
						subset.put("CHARGE_CODE_DESC", "Premium");
						subset.put("CHARGE_CODE_VALUE", premiumFc);
						csubsets.add(subset);
					}
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1012");
						subset.put("CHARGE_CODE_DESC", "VAT");
						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
						csubsets.add(subset);
					}

					List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1005");
						subset.put("CHARGE_CODE_DESC", "Commission");
						subset.put("CHARGE_CODE_VALUE", commission);
						bsubsets.add(subset);
					}

					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1007");
						subset.put("CHARGE_CODE_DESC", "Commission%");
						subset.put("CHARGE_CODE_VALUE", commissionPercent);
						bsubsets.add(subset);
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
					setup.put("<CUSTOMER>", csubsets);
					setup.put("<BROKER>", bsubsets);

					// Rule
					Map<String, Object> rule1 = new HashMap<String, Object>();
					rule1.put("DEBIT", "<CUSTOMER>");
					rule1.put("CREDIT", "<BROKER>");
					rules.add(rule1);

					String crnumber = "CN-" + genNo.generateCreditNo(); // ThreadLocalRandom.current().ints(1001,
																		// 4999).distinct().limit(5).findAny().toString();
					String drnumber = "DN-" + genNo.generateDebitNo(); // ThreadLocalRandom.current().ints(4999,
																		// 9999).distinct().limit(5).findAny().toString();

					if (StringUtils.isBlank(endttypeid)) {
						String policyNo = genNo.generatePolicyNo();
						request.setPolicyNo(policyNo);
					} else {
						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
					}
					int rownum = 1;

					for (Map<String, Object> map : rules) {
						for (Entry<String, Object> m : map.entrySet()) {

							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
							for (Map<String, Object> s : dd) {
							 	 DebitAndCredit res =new  DebitAndCredit();
								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
								
								res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
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
								res.setSectionId(request.getSectionId());
								res.setRiskId(v.getTravelId().toString());
								resList.add(res);
							}
						}
					}
					crdrservice.insertDRCR(resList, request.getQuoteno());
				}
				
			}

			// Building and SME Product
			else if (product.getMotorYn().equalsIgnoreCase("A")) {
// 				List<EserviceBuildingsDetailsRes> motors = (List<EserviceBuildingsDetailsRes>) v1.getRiskDetails();
				List<BuildingRiskDetails> motors = buildingRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());

 				for (BuildingRiskDetails v : motors) {

					/*List<BrokerCommissionDetails> policylist = getPolicyName(request.getInsuranceId(),
							request.getProductId(), request.getCreatedBy(), request.getAgencyCode(),"99999");*/
					 Double commissionPercent = v.getCommissionPercentage().doubleValue();
						/*if(policylist.size()>0 && policylist!=null) {
						
					commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
							: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
						}
						else {
							commissionPercent=5.0;
						}*/
					String premiumFc = v.getActualPremiumFc().toString();
					String vatPremiumFc = v.getActualPremiumFc().toString();

					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
						premiumFc = v1.getQuoteDetails().getEndtPremium().toPlainString();
						vatPremiumFc = v1.getQuoteDetails().getEndtPremiumTax().toPlainString();
					}

					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
							.divide(BigDecimal.valueOf(100D))
							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
					v1.getQuoteDetails().getVatPercent();
				   String endttypeid = v1.getQuoteDetails().getEndtTypeId();
					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();

					// Setup
					Map<String, Object> setup = new HashMap<String, Object>();

					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1001");
						subset.put("CHARGE_CODE_DESC", "Premium");
						subset.put("CHARGE_CODE_VALUE", premiumFc);
						csubsets.add(subset);
					}
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1012");
						subset.put("CHARGE_CODE_DESC", "VAT");
						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
						csubsets.add(subset);
					}

					List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1005");
						subset.put("CHARGE_CODE_DESC", "Commission");
						subset.put("CHARGE_CODE_VALUE", commission);
						bsubsets.add(subset);
					}

					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1007");
						subset.put("CHARGE_CODE_DESC", "Commission%");
						subset.put("CHARGE_CODE_VALUE", commissionPercent);
						bsubsets.add(subset);
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
					setup.put("<CUSTOMER>", csubsets);
					setup.put("<BROKER>", bsubsets);

					// Rule
					Map<String, Object> rule1 = new HashMap<String, Object>();
					rule1.put("DEBIT", "<CUSTOMER>");
					rule1.put("CREDIT", "<BROKER>");
					rules.add(rule1);

					String crnumber = "CN-" + genNo.generateCreditNo(); // ThreadLocalRandom.current().ints(1001,
																		// 4999).distinct().limit(5).findAny().toString();
					String drnumber = "DN-" + genNo.generateDebitNo(); // ThreadLocalRandom.current().ints(4999,
																		// 9999).distinct().limit(5).findAny().toString();

					if (StringUtils.isBlank(endttypeid)) {
						String policyNo = genNo.generatePolicyNo();
						request.setPolicyNo(policyNo);
					} else {
						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
					}
					int rownum = 1;

					for (Map<String, Object> map : rules) {
						for (Entry<String, Object> m : map.entrySet()) {

							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
							for (Map<String, Object> s : dd) {
							 	 DebitAndCredit res =new  DebitAndCredit();
								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
								res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
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
								res.setSectionId(request.getSectionId());
								res.setRiskId(v.getRiskId().toString());
								resList.add(res);
							}
						}
					}
					crdrservice.insertDRCR(resList, request.getQuoteno());
				}
				
			}
			
			// Common Product
			else {
// 				List<EserviceCommonGetRes> motors = (List<EserviceCommonGetRes>) v1.getRiskDetails();
 				List<CommonDataDetails> motors = commonRepo.findByQuoteNoOrderByRiskIdAsc(request.getQuoteno());

 				for (CommonDataDetails v : motors) {

					/*List<BrokerCommissionDetails> policylist = getPolicyName(request.getInsuranceId(),
							request.getProductId(), request.getCreatedBy(), request.getAgencyCode(),"99999");*/
					 Double commissionPercent =v.getCommissionPercentage().doubleValue();
					/*	if(policylist.size()>0 && policylist!=null) {
						
					commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
							: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
						}
						else {
							commissionPercent=5.0;
						}*/
					String premiumFc = v.getActualPremiumFc().toString();
					String vatPremiumFc = v.getActualPremiumFc().toString();

					if (StringUtils.isNotBlank(v1.getQuoteDetails().getEndtTypeId())) {
						premiumFc = v1.getQuoteDetails().getEndtPremium().toPlainString();
						vatPremiumFc = v1.getQuoteDetails().getEndtPremiumTax().toPlainString();
					}

					BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
							.divide(BigDecimal.valueOf(100D))
							.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
					v1.getQuoteDetails().getVatPercent();
				
					String endttypeid = v1.getQuoteDetails().getEndtTypeId();
					List<Map<String, Object>> rules = new ArrayList<Map<String, Object>>();

					// Setup
					Map<String, Object> setup = new HashMap<String, Object>();

					List<Map<String, Object>> csubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1001");
						subset.put("CHARGE_CODE_DESC", "Premium");
						subset.put("CHARGE_CODE_VALUE", premiumFc);
						csubsets.add(subset);
					}
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1012");
						subset.put("CHARGE_CODE_DESC", "VAT");
						subset.put("CHARGE_CODE_VALUE", vatPremiumFc);
						csubsets.add(subset);
					}

					List<Map<String, Object>> bsubsets = new ArrayList<Map<String, Object>>();
					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1005");
						subset.put("CHARGE_CODE_DESC", "Commission");
						subset.put("CHARGE_CODE_VALUE", commission);
						bsubsets.add(subset);
					}

					{
						Map<String, Object> subset = new HashMap<String, Object>();
						subset.put("CHARGE_CODE", "1007");
						subset.put("CHARGE_CODE_DESC", "Commission%");
						subset.put("CHARGE_CODE_VALUE", commissionPercent);
						bsubsets.add(subset);
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
					setup.put("<CUSTOMER>", csubsets);
					setup.put("<BROKER>", bsubsets);

					// Rule
					Map<String, Object> rule1 = new HashMap<String, Object>();
					rule1.put("DEBIT", "<CUSTOMER>");
					rule1.put("CREDIT", "<BROKER>");
					rules.add(rule1);

					String crnumber = "CN-" + genNo.generateCreditNo(); // ThreadLocalRandom.current().ints(1001,
																		// 4999).distinct().limit(5).findAny().toString();
					String drnumber = "DN-" + genNo.generateDebitNo(); // ThreadLocalRandom.current().ints(4999,
																		// 9999).distinct().limit(5).findAny().toString();

					if (StringUtils.isBlank(endttypeid)) {
						String policyNo = genNo.generatePolicyNo();
						request.setPolicyNo(policyNo);
					} else {
						request.setPolicyNo(v1.getQuoteDetails().getPolicyNo());
					}
					int rownum = 1;

					for (Map<String, Object> map : rules) {
						for (Entry<String, Object> m : map.entrySet()) {

							List<Map<String, Object>> dd = (List<Map<String, Object>>) setup.get(m.getValue());
							for (Map<String, Object> s : dd) {
							 	 DebitAndCredit res =new  DebitAndCredit();
								String doctype = m.getValue().equals("<CUSTOMER>") ? "C" : "B";
															res.setAmountFc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setAmountLc(new BigDecimal(s.get("CHARGE_CODE_VALUE").toString()));
								res.setChargeCode(new BigDecimal(s.get("CHARGE_CODE").toString()));
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
								res.setSectionId(request.getSectionId());
								res.setRiskId(v.getRiskId().toString());
								resList.add(res);
							}
						}
					}
					crdrservice.insertDRCR(resList, request.getQuoteno());
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resList;
	}

	private List<BrokerCommissionDetails> getPolicyName(String companyId, String productId, String loginId,
			String agencyCode, String policyType) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));

			amendId.where(a1, a2, a3, a4, a5, a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("productId"), productId);
			Predicate n5 = cb.equal(b.get("loginId"), loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"), agencyCode);

			query.where(n1, n2, n3, n4, n5, n6);

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
			  
			List<Tuple> joinResult = crservice.getJoinResult(criterias, 0, 50);
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
	
}

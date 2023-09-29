package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.service.BuildingSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.EserviceBuildingsDetailsRes;
import com.maan.eway.res.SectionDetails;


@Service
@Transactional
public class BuildingSearchServiceImpl implements BuildingSearchService {
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(BuildingSearchServiceImpl.class);

	
	@Autowired
	private EserviceBuildingDetailsRepository repo;

	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private PersonalInfoRepository perRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo  ;
	
	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo  ;

	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo ;
	@Override
	public List<Tuple> searchBuilding(SearchReq req, List<String> branches) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
		try {
			// Search
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();
			String productId=req.getProductId();
			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerName".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchBuildingDetails(searchKey, searchValue, companyId, loginId, userType, branches,productId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;
	}

	@Override
	public List<Tuple> searchBuildingDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) {
		// TODO Auto-generated method stub
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
//			query.multiselect(c.alias("c"),
//					cus.get("clientName").alias("clientName"),
//					cb.count(c).alias("idsCount"),cus.get("mobileNo1").alias("mobileNumber"));

			query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),

				//	cus.get("customerReferenceNo").alias("customerReferenceNo"),
					cus.get("mobileNo1").alias("mobileNumber"),
					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.max(c.get("riskId")).alias("riskId"), 
					cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
					cb.max(c.get("productId")).alias("productId"),
					cb.max(c.get("companyId")).alias("companyId"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
					.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

					cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
					.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("branchCode")).alias("branchCode"), 
					cb.max(c.get("inbuildConstructType")).alias("inbuildConstructType"),
					cb.max(c.get("buildingFloors")).alias("buildingFloors"),
					cb.max(c.get("outbuildConstructType")).alias("outbuildConstructType"),

					cb.max(c.get("buildingUsageYn")).alias("buildingUsageYn"),
					//cb.max(c.get("buildingPurpose")).alias("buildingPurpose"),
					//cb.max(c.get("buildingPurposeId")).alias("buildingPurposeId"),
					cb.max(c.get("buildingUsageId")).alias("buildingUsageId"), 
					cb.max(c.get("buildingUsageDesc")).alias("buildingUsageDesc"),
					cb.max(c.get("buildingType")).alias("buildingType"),
					cb.max(c.get("buildingOwnerYn")).alias("buildingOwnerYn"),
					cb.max(c.get("buildingOccupationType")).alias("buildingOccupationType"),
					cb.max(c.get("apartmentOrBorder")).alias("apartmentOrBorder"),
					cb.max(c.get("withoutInhabitantDays")).alias("withoutInhabitantDays"),
					cb.max(c.get("buildingCondition")).alias("buildingCondition"),
					cb.max(c.get("buildingBuildYear")).alias("buildingBuildYear"),
					cb.max(c.get("buildingAge")).alias("buildingAge"),
					cb.max(c.get("buildingAreaSqm")).alias("buildingAreaSqm"),
					cb.max(c.get("buildingSuminsured")).alias("buildingSuminsured"),
					cb.max(c.get("allriskSuminsured")).alias("allriskSuminsured"), 
					//cb.max(c.get("paDeathSuminsured")).alias("paDeathSuminsured"),
					//cb.max(c.get("paPermanentdisablementSuminsured")).alias("paPermanentdisablementSuminsured"),
					//cb.max(c.get("paTotaldisabilitySumInsured")).alias("paTotaldisabilitySumInsured"), 
					//cb.max(c.get("PaMedicalSuminsured")).alias("PaMedicalSuminsured"),
					//cb.max(c.get("personalIntSuminsured")).alias("personalIntSuminsured"),
					cb.max(c.get("contentSuminsured")).alias("contentSuminsured"), 
					//cb.max(c.get("workmenCompSuminsured")).alias("workmenCompSuminsured"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("createdBy")).alias("createdBy"),
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("updatedDate")).alias("updatedDate"),
					cb.max(c.get("updatedBy")).alias("updatedBy"),

					cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
					cb.max(c.get("applicationId")).alias("applicationId"),
					cb.max(c.get("brokerCode")).alias("brokerCode"),
					cb.max(c.get("subUserType")).alias("subUserType"),
					cb.max(c.get("loginId")).alias("loginId"),
					cb.max(c.get("agencyCode")).alias("agencyCode"),
					cb.max(c.get("policyStartDate")).alias("policyStartDate"),
					cb.max(c.get("policyEndDate")).alias("policyEndDate"),
					cb.max(c.get("policyPeriord")).alias("policyPeriord"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("adminLoginId")).alias("adminLoginId"),
					cb.max(c.get("adminRemarks")).alias("adminRemarks"),
					cb.max(c.get("rejectReason")).alias("rejectReason"),
					cb.max(c.get("referalRemarks")).alias("referalRemarks"),
					cb.max(c.get("productDesc")).alias("productDesc"),
					cb.max(c.get("sectionId")).alias("sectionId"),
					cb.max(c.get("sectionDesc")).alias("sectionDesc"),
					cb.max(c.get("branchName")).alias("branchName"),
					cb.max(c.get("companyName")).alias("companyName"),
					cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
					cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
					cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
					cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
					cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
					cb.max(c.get("insuranceType")).alias("insuranceType"),
					cb.max(c.get("commissionType")).alias("commissionType"),
					cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
					cb.max(c.get("havepromocode")).alias("havepromocode"),
					cb.max(c.get("promocode")).alias("promocode"),
					cb.max(c.get("occupationType")).alias("occupationType"),
					cb.max(c.get("occupationTypeDesc")).alias("occupationTypeDesc"),
					cb.max(c.get("domesticPackageYn")).alias("domesticPackageYn"), 
					cb.max(c.get("categoryId")).alias("categoryId"),
					cb.max(c.get("categoryDesc")).alias("categoryDesc"),
					cb.max(c.get("bankCode")).alias("bankCode"),
					cb.max(c.get("sourceType")).alias("sourceType"),
					cb.max(c.get("customerCode")).alias("customerCode"),

					cb.max(c.get("bdmCode")).alias("bdmCode"),
					cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
					cb.max(c.get("elecEquipSuminsured")).alias("elecEquipSuminsured"),
					//cb.max(c.get("moneySinglecarrySuminsured")).alias("moneySinglecarrySuminsured"),
					//cb.max(c.get("moneyAnnualcarrySuminsured")).alias("moneyAnnualcarrySuminsured"),
					////cb.max(c.get("moneyInsafeSuminsured")).alias("moneyInsafeSuminsured"),
					//cb.max(c.get("fidelityAnyoccuSuminsured")).alias("fidelityAnyoccuSuminsured"),
					//cb.max(c.get("fidelityAnnualSuminsured")).alias("fidelityAnnualSuminsured"),
					//cb.max(c.get("tpliabilityAnyoccuSuminsured")).alias("tpliabilityAnyoccuSuminsured"),
					//cb.max(c.get("empliabilityAnnualSuminsured")).alias("empliabilityAnnualSuminsured"),
					//cb.max(c.get("empliabilityExcessSuminsured")).alias("empliabilityExcessSuminsured"),
					cb.max(c.get("goodsSinglecarrySuminsured")).alias("goodsSinglecarrySuminsured"),
					cb.max(c.get("goodsTurnoverSuminsured")).alias("goodsTurnoverSuminsured"), 
					cb.max(c.get("industryId")).alias("industryId"),
					cb.max(c.get("industryDesc")).alias("industryDesc"),
					cb.max(c.get("endorsementType")).alias("endorsementType"),
					cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(c.get("endorsementDate")).alias("endorsementDate"),

					cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
				
					cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(c.get("endtCount")).alias("endtCount"),
					cb.max(c.get("endtStatus")).alias("endtStatus"),
					cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
					cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(c.get("endtPremium")).alias("endtPremium"),
					//cb.max(c.get("liabilityOccupationId")).alias("liabilityOccupationId"), 
					//cb.max(c.get("liabilityOccupationDesc")).alias("liabilityOccupationDesc"),
					cb.max(c.get("wallType")).alias("wallType"),
					cb.max(c.get("wallTypeDesc")).alias("wallTypeDesc"),
					cb.max(c.get("roofType")).alias("roofType"),
					cb.max(c.get("roofTypeDesc")).alias("roofTypeDesc"),
					cb.max(c.get("natureOfTradeId")).alias("natureOfTradeId"),
					cb.max(c.get("natureOfTradeDesc")).alias("natureOfTradeDesc"),
					cb.max(c.get("insuranceForId")).alias("insuranceForId"),
					cb.max(c.get("insuranceForDesc")).alias("insuranceForDesc"),
					cb.max(c.get("internalWallType")).alias("internalWallType"),
					cb.max(c.get("internalWallDesc")).alias("internalWallDesc"),

					cb.max(c.get("ceilingType")).alias("ceilingType"),
					cb.max(c.get("ceilingTypeDesc")).alias("ceilingTypeDesc"),
					cb.max(c.get("stockInTradeSi")).alias("stockInTradeSi"),
					cb.max(c.get("goodsSi")).alias("goodsSi"),
					cb.max(c.get("furnitureSi")).alias("furnitureSi"),
					cb.max(c.get("applianceSi")).alias("applianceSi"),
					cb.max(c.get("cashValueablesSi")).alias("cashValueablesSi"),
					cb.max(c.get("stockLossPercent")).alias("stockLossPercent"),
					cb.max(c.get("goodsLossPercent")).alias("goodsLossPercent"),

					cb.max(c.get("furnitureLossPercent")).alias("furnitureLossPercent"),
					cb.max(c.get("applianceLossPercent")).alias("applianceLossPercent"),
					cb.max(c.get("cashValueablesLossPercent")).alias("cashValueablesLossPercent"),
					cb.max(c.get("address")).alias("address"),
					cb.max(c.get("regionCode")).alias("regionCode"),
					cb.max(c.get("regionDesc")).alias("regionDesc"),
					cb.max(c.get("districtCode")).alias("districtCode"),
					cb.max(c.get("districtDesc")).alias("districtDesc"),
					cb.max(c.get("occupiedYear")).alias("occupiedYear"),
					cb.max(c.get("showWindow")).alias("showWindow"),

					cb.max(c.get("frontDoors")).alias("frontDoors"),
					cb.max(c.get("backDoors")).alias("backDoors"),
					cb.max(c.get("windowsMaterialId")).alias("windowsMaterialId"),
					cb.max(c.get("windowsMaterialDesc")).alias("windowsMaterialDesc"),
					cb.max(c.get("doorsMaterialId")).alias("doorsMaterialId"),
					cb.max(c.get("doorsMaterialDesc")).alias("doorsMaterialDesc"),
					cb.max(c.get("nightLeftDoor")).alias("nightLeftDoor"),
					cb.max(c.get("nightLeftDoorDesc")).alias("nightLeftDoorDesc"),
					cb.max(c.get("buildingOccupied")).alias("buildingOccupied"),
					cb.max(c.get("buildingOccupiedDesc")).alias("buildingOccupiedDesc"),
					cb.max(c.get("watchmanGuardHours")).alias("watchmanGuardHours"),
					cb.max(c.get("accessibleWindows")).alias("accessibleWindows"),
					cb.max(c.get("trapDoors")).alias("trapDoors"),
					//cb.max(c.get("cashInHandDirectors")).alias("cashInHandDirectors"),
					//cb.max(c.get("cashInTransit")).alias("cashInTransit"),
					//cb.max(c.get("cashInHandEmployees")).alias("cashInHandEmployees"),
					//cb.max(c.get("cashInSafe")).alias("cashInSafe"),
					//cb.max(c.get("cashInPremises")).alias("cashInPremises"),
					cb.max(c.get("revenueFromStamps")).alias("revenueFromStamps"),
					//cb.max(c.get("moneyInSafeBusiness")).alias("moneyInSafeBusiness"),
					//cb.max(c.get("moneyOutSafeBusiness")).alias("moneyOutSafeBusiness"),
					//cb.max(c.get("moneyInPremises")).alias("moneyInPremises"),
					//cb.max(c.get("moneyInLocker")).alias("moneyInLocker"),
					cb.max(c.get("machineEquipSi")).alias("machineEquipSi"),
					cb.max(c.get("plateGlassSi")).alias("plateGlassSi"),
					cb.max(c.get("firstLossPercentId")).alias("firstLossPercentId"),
					cb.max(c.get("firstLossPercent")).alias("firstLossPercent"),
					//cb.max(c.get("accDamageSi")).alias("accDamageSi"),
					//cb.max(c.get("burglarySi")).alias("burglarySi"),
					cb.max(c.get("powerPlantSi")).alias("powerPlantSi"),
					cb.max(c.get("elecMachinesSi")).alias("elecMachinesSi"),
					cb.max(c.get("equipmentSi")).alias("equipmentSi"),
					cb.max(c.get("generalMachineSi")).alias("generalMachineSi"),
					cb.max(c.get("manuUnitsSi")).alias("manuUnitsSi"),
					cb.max(c.get("boilerPlantsSi")).alias("boilerPlantsSi"),
					cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
					cb.max(c.get("indemityPeriod")).alias("indemityPeriod"),
					cb.max(c.get("indemityPeriodDesc")).alias("indemityPeriodDesc"),
					cb.max(c.get("makutiYn")).alias("makutiYn"),
					cb.max(c.get("plateGlassType")).alias("plateGlassType"),
					cb.max(c.get("plateGlassDesc")).alias("plateGlassDesc")

					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
			Predicate n3 = null;
			Predicate n4 = null;
			Predicate n5 = null;
			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			} else if (searchKey.equalsIgnoreCase("CustomerReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("customerReferenceNo")), searchValue);
			
			} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
				n1 = cb.equal(c.get("quoteNo"), searchValue);
				
			}
			 else if (searchKey.equalsIgnoreCase("MobileNumber")) {
					n1 = cb.equal(cb.lower(cus.get("mobileNo1")), searchValue);
			 }
			
			else if (searchKey.equalsIgnoreCase("CustomerName")) {
				n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			}
			else if (searchKey.equalsIgnoreCase("PolicyNumber")) {
				n1 = cb.equal(cb.lower(c.get("policyNo")), searchValue);
			}

			Predicate n2 = cb.equal(c.get("companyId"), companyId);
			Predicate n6 = cb.equal(c.get("productId"), productId);

			

			if ("issuer".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("applicationId"), loginId);
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				n4 = e0.in(branches);
			}
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				if ("issuer".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("brokerBranchCode");
					n4 = e0.in(branches);
				}
			}
			n5 = cb.equal(cus.get("customerReferenceNo"), c.get("customerReferenceNo"));
		//	Predicate n6 = cb.isNull(c.get("endtTypeId"));
			query.where(n1,n2,n3,n4,n5,n6)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
//					c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
			.orderBy(orderList);
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				query.where(n1, n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),cus.get("mobileNo1"),
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
//						c.get("rejectReason"),c.get("riskId"),c.get("insuranceType"))
				.orderBy(orderList);
			}
			

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			customerDetailsList = result.getResultList();
			customerDetailsList = customerDetailsList.stream().filter(o -> !o.get("idsCount").equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return customerDetailsList;
	}

	
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	
	
	@Override
	public List<ListItemValue> searchDropdownBuilding(CopyQuoteDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		try {
            String itemType = "ADMIN_SEARCH_BUILDING";
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
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue::getItemValue));
			
//			for (ListItemValue data : list) {
//				DropDownRes res = new DropDownRes();
//				res.setCode(data.getItemCode());
//				res.setCodeDesc(data.getItemValue());
//				res.setStatus(data.getStatus());
//				resList.add(res);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
		}
	
		// Risk details
		@Override
		public AdminViewQuoteRes getBuildingProductDetails(SearchReq req) {
			AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				// Find Motor Data

				BuildingRiskDetails buildData = buildRiskRepo.findByQuoteNo(req.getQuoteNo());
				List<EserviceSectionDetails> secDatas = eserSecRepo
						.findByRequestReferenceNoOrderByRiskIdAsc(buildData.getRequestReferenceNo());

				// Building Details
				// Section Details

				List<EserviceBuildingsDetailsRes> buildList = new ArrayList<EserviceBuildingsDetailsRes>();
				EserviceBuildingsDetailsRes buildingRes = new EserviceBuildingsDetailsRes();
				dozerMapper.map(buildData, buildingRes);
				buildingRes.setDocumentsTitle(buildData.getProductDesc());

				// Broker Commission
				List<BrokerCommissionDetails> policylist = getPolicyName(buildData.getCompanyId(),
						buildData.getProductId().toString(), buildData.getCreatedBy(), buildData.getAgencyCode(),
						"99999");
				Double commissionPercent = 0.0;
				if (policylist.size() > 0 && policylist != null) {
					commissionPercent = policylist.get(0).getCommissionPercentage().toString() == null ? 0
							: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());
				} else {
					commissionPercent = 5.0;
				}
				String premiumFc = buildData.getOverallPremiumFc().toString();
				String vatPremiumFc = buildData.getOverallPremiumFc().toString();
				BigDecimal commission = new BigDecimal(premiumFc).multiply(new BigDecimal(commissionPercent))
						.divide(BigDecimal.valueOf(100D))
						.setScale(new MathContext(3, RoundingMode.HALF_UP).getPrecision(), RoundingMode.HALF_UP);
				buildingRes.setOverAllPremiumFc(buildData.getOverallPremiumFc().toString() == null ? 0D
						: Double.valueOf(buildData.getOverallPremiumFc().toString()));
				buildingRes.setOverAllPremiumLc(buildData.getOverallPremiumLc().toString() == null ? 0D
						: Double.valueOf(buildData.getOverallPremiumLc().toString()));
				buildingRes.setPremiumFc(buildData.getActualPremiumFc().toString() == null ? 0
						: Double.valueOf(buildData.getActualPremiumFc().toString()));
				buildingRes.setPremiumLc(buildData.getActualPremiumLc().toString() == null ? 0
						: Double.valueOf(buildData.getActualPremiumLc().toString()));
				buildingRes.setCommissionAmount(commission.toString() == null ? "" : commission.toString());
				buildingRes.setCommissionPercentage(
						commissionPercent.toString() == null ? "" : commissionPercent.toString());

				List<SectionDetails> buildingSectionList = new ArrayList<SectionDetails>();
				for (EserviceSectionDetails sec : secDatas) {
					if (sec.getSectionId().equalsIgnoreCase("35")) {
						List<CommonDataDetails> accData = commonDataRepo
								.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
						for (CommonDataDetails acc : accData) {
							SectionDetails buildSec = new SectionDetails();
							buildSec.setSectionId(acc.getSectionId() == null ? "" : acc.getSectionId().toString());
							buildSec.setSectionName(acc.getSectionDesc());
							buildingSectionList.add(buildSec);

						}

					} else {
						// Build
						SectionDetails buildSec = new SectionDetails();
						buildSec.setSectionId(sec.getSectionId() == null ? "" : sec.getSectionId().toString());
						buildingRes.setSectionId(StringUtils.isBlank(buildingRes.getSectionId())
								? sec.getSectionId() == null ? "" : sec.getSectionId().toString()
								: buildingRes.getSectionId());
						buildSec.setSectionName(sec.getSectionName());
						buildingSectionList.add(buildSec);

					}

				}
				buildingRes.setSectionDetails(buildingSectionList);

				buildList.add(buildingRes);
				List<Object> totalList = new ArrayList<Object>();
				totalList.addAll(buildList);

				viewRes.setRiskDetails(totalList);

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return viewRes;
		}

	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
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
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}
//Rating
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> buildingRating() {
		List<SearchEservieMotorDetailsViewRatingRes> reslist = new ArrayList<SearchEservieMotorDetailsViewRatingRes>();
		return reslist;
	}

	
	//Customer Search
	@Override
	public List<SearchCustomerDetailsRes> buildingCustSearch(SearchReq req,List<HomePositionMaster> homeData) {
			List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
			DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
			try {
				String customerId=homeData.get(0).getCustomerId();
				String loginId = "";
				String appId = "";
				String sourceType="";
				String coustomerCode="";
				String customerCodeName="";
				String source=""; 
				PersonalInfo list =new PersonalInfo();
				List<PremiaCustomerDetails> premiadata =null;
				List<EserviceBuildingDetails> motor = repo.findByCustomerId(customerId);
				if (motor.size() > 0) {
					sourceType = motor.get(0).getSourceType();
					coustomerCode = motor.get(0).getCustomerCode();
					loginId = motor.get(0).getLoginId();
					appId = motor.get(0).getApplicationId();
					source = motor.get(0).getLoginId();
					premiadata = premiaRepo.findByCustomerCode(coustomerCode);
					if (premiadata.size() > 0) {
						customerCodeName = premiadata.get(0).getCustomerName();
					}
				}
				list = perRepo.findByCustomerId(homeData.get(0).getCustomerId());
				
				SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
				res = dozerMapper.map(list,SearchCustomerDetailsRes.class);	
				res.setLoginId(req.getLoginId());
				res.setApplicationId(req.getApplicationId());
				res.setCustomerCode(coustomerCode);
				res.setCustomerName(customerCodeName);	
				res.setSourceType(sourceType);
				res.setBranchCode(list.getBranchCode().toString());
				res.setSource(source);
				reslist.add(res);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return reslist;
		}
	}
		

package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
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
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.MasterReferralDetails;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.calculator.util.TaxFromFactor;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.CriteriaCustomerRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.DriverDetailsRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.EserviceMotorDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchEserviceMotorDetailsRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.common.service.MotorSearchService;
import com.maan.eway.common.service.SearchService;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BranchMasterGetReq;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.master.res.BranchMasterRes;
import com.maan.eway.master.service.TrackingDetailsService;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.CoverRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SectionDetails;
import com.maan.eway.res.SubCoverRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.res.referal.MasterReferal;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	@Value(value = "${sme.productId}")
	private String smeProductId;
	
	@Autowired
	private FactorRateRequestDetailsRepository factorrepo;
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository commonRepo ;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo ;
	
	@Autowired
	private MotorSearchService motService ;
	
	@Autowired
	private TravelGridService traService ;
	
	@Autowired
	private BuildingGridService buiService ;
	
	@Autowired
	private CommonGridService commonService ;
	
	@Autowired
	private EserviceTravelDetailsRepository travelRepo;
	
	@Autowired
	private PersonalInfoRepository perRepo ;
	

	@Autowired
	private EmiTransactionDetailsRepository emiRepo ;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;
	

	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private MotorDriverDetailsRepository driverRepo ;

	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private CoverDetailsRepository coverRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(SearchServiceImpl.class);

	//Dropdown
	//CopyQuote Dropdown 
	@Override
	public List<DropDownRes> searchDropdown(CopyQuoteDropDownReq req) { 
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> list = new ArrayList<ListItemValue>();
			String itemType = "ADMIN_SEARCH";
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
			
			for (ListItemValue data : list) {
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
	public List<SearchRes> adminSearchOrderByEntryDate(SearchReq req) {

		List<SearchRes> reslist = new ArrayList<SearchRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
		
			 List<BranchMaster> branchlist= getByBranchCode(req.getBranchCode());
			 String branchName=branchlist.get(0).getBranchName();
			 String loginId = "" ;
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
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

			// Product Wise Get
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				list = motService.adminSearchMotorQuote(req, branches);

			}
//			else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//				list = traService.searchTravelQuote(req, branches);
//			}
//			else if (req.getProductId().equalsIgnoreCase(buildingProductId) || req.getProductId().equalsIgnoreCase(smeProductId)) {
//				list = buiService.searchBuildingQuote(req, branches);
//
//			} else {
//				list = commonService.searchCommonQuote(req, branches);
//			}

			for (Tuple data : list) {
				SearchRes res = new SearchRes();
				res = dozermapper.map(data.get(0) , SearchRes.class);	
//				res.setRequestReferenceNo(data.get(0).get("requestReferenceNo"));
//				res.setQuoteNo(data.get(0).get("requestReferenceNo"));
				res.setClientName((data.get("clientName").toString()));
				res.setMobileNumber((data.get("mobileNumber").toString()));
				res.setBranchName(branchName);		
				//res.setIdsCount(data.get("idsCount")==null?"":data.get("idsCount").toString() );
				 reslist.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	//BranchName
	public List<BranchMaster> getByBranchCode(String branchCode) {
		//BranchMaster res = new BranchMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<BranchMaster> list = new ArrayList<BranchMaster>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BranchMaster> query = cb.createQuery(BranchMaster.class);
			
			
			// Find All
			Root<BranchMaster>    c = query.from(BranchMaster.class);		
			
			// Select
			query.select(c );
			
			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BranchMaster> ocpm1 = amendId.from(BranchMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode") );
			
			amendId.where(a1);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Where
			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n4 = cb.equal(c.get("branchCode"), branchCode);
			query.where(n1,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<BranchMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBranchCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BranchMaster :: getBranchName ));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	


	@Override
	public AdminViewQuoteRes adminViewQuoteDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		DozerBeanMapper mapper =new DozerBeanMapper();
		try {
			HomePositionMaster homeData =null;
			if(StringUtils.isNotBlank(req.getQuoteNo())){
				homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());	
			}
			// Motor Product Details
			if( homeData.getProductId().equals(Integer.valueOf(motorProductId))) {
				viewRes =  getMotorProductDetails( req);
				
			}
//			else if( homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
//				// Travel Product Details
//				viewRes =	getTravelProductDetails( req);
//				
//			} else if( homeData.getProductId().equals(Integer.valueOf(buildingProductId)) || homeData.getProductId().equals(Integer.valueOf(smeProductId)) ) {
//				// Travel Product Details
//				viewRes =	getBuildingProductDetails( req);
//				
//			} else {
//				// Travel Product Details
//				viewRes =	getCommonProductDetails( req);
//				
//			}

			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}

	public AdminViewQuoteRes getMotorProductDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<MotorDataDetails> motorDatas =  motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<MotorDriverDetails> driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
			List<SearchEserviceMotorDetailsRes>   motorResList = new ArrayList<SearchEserviceMotorDetailsRes>();
			
						
			for (MotorDataDetails mot :  motorDatas) {
				SearchEserviceMotorDetailsRes vehicleDetails = new  SearchEserviceMotorDetailsRes()  ;
				
				// Mot
				dozerMapper.map(mot, vehicleDetails);
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<CoverRes>  coverListRes = getCoverDetails(groupByCover);
				
				// Driver Details
				List<DriverDetailsRes>   driverResList = new ArrayList<DriverDetailsRes>();
				List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				for (MotorDriverDetails dri :  filterDriverList) {
					DriverDetailsRes driverRes  = new DriverDetailsRes();  
					dozerMapper.map(dri, driverRes);
					driverRes.setLicenseNo(dri.getIdNumber());
					
					driverResList.add(driverRes);
					
				}
				vehicleDetails.setRiskId(mot.getVehicleId());
				driverResList.sort(Comparator.comparing(DriverDetailsRes :: getDriverId  ));
				vehicleDetails.setDriverDetails(driverResList);
				vehicleDetails.setDocumentsTitle(mot.getSectionName());			
				vehicleDetails.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
				
				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(mot.getSectionId()==null?"":mot.getSectionId().toString());
				sec.setSectionName( mot.getSectionName());
				sec.setCovers(coverListRes);
				
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				vehicleDetails.setSectionDetails(sectionList);
				
				// Response
				motorResList.add(vehicleDetails);		
			}
			viewRes.setRiskDetails(motorResList);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	public List<CoverRes> getCoverDetails(Map<Integer,List<PolicyCoverData>> groupByCover  ) {
		List<CoverRes>  coverListRes = new ArrayList<CoverRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for ( Integer coverId : groupByCover.keySet() ) {
				List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
				CoverRes coverRes = new CoverRes();
				
				if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), CoverRes.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
					coverRes.setIsselected(filterCover.get(0).getIsSelected());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
					coverRes.setCoverageType(filterCover.get(0).getCoverageType());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
									
//					// Discount Covers Or Promo Covers
//					List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
//					
//					if ( filterDiscountCover.size() > 0 ) {
//						 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//						 coverRes.setDiscounts(discounts);	
//					}
//					
//					// Tax Covers
//					List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//					
//					if( filterTaxCover.size() > 0 ) {
//						 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//						 coverRes.setTaxes(taxes);	
//					}
//					
//					// Loginds Covers
//					List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//					
//					if( filterLodingCover.size() > 0 ) {
//						 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//						 coverRes.setLoadings(lodings);	
//					}
										
				} else {
					
					// Get Sub Covers
			
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());
					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null : Double.valueOf(filterCover.get(0).getRate().toString()));
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					
					List<SubCoverRes>  subCoverListRes = new ArrayList<SubCoverRes>();
					List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
					for ( PolicyCoverData subCovers : filterSubCover) {
						SubCoverRes subCoverRes = new SubCoverRes();
						subCoverRes = dozerMapper.map(subCovers, SubCoverRes.class);
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setRegulatoryCode(filterCover.get(0).getRegulatoryCode());
						coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
						coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
						coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
//						// Discount Covers Or Promo Covers
//						List<PolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) && ( o.getCoverageType().equalsIgnoreCase("D") ||  o.getCoverageType().equalsIgnoreCase("P") )  ).collect(Collectors.toList());
//						
//						if ( filterDiscountCover.size() > 0 ) {
//							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
//							 subCoverRes.setDiscounts(discounts);	
//						}
//						
//						// Tax Covers
//						List<PolicyCoverData> filterTaxCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
//						
//						if( filterTaxCover.size() > 0 ) {
//							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
//							 subCoverRes.setTaxes(taxes);	
//						}
//						
//						// Loginds Covers
//						List<PolicyCoverData> filterLodingCover = coverGroups.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
//						
//						if( filterLodingCover.size() > 0 ) {
//							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
//							 subCoverRes.setLoadings(lodings);	
//						}
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
	
			coverListRes.sort(Comparator.comparing(CoverRes :: getCoverId));;
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return coverListRes;
	}
	
	public synchronized List<Cover> getCoversList(Map<Integer,List<FactorRateRequestDetails>> groupByCover) {
		List<Cover>  coverListRes = new ArrayList<Cover>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for ( Integer coverId : groupByCover.keySet() ) {
				List<FactorRateRequestDetails>  covers  = groupByCover.get(coverId);
				Cover coverRes = new Cover();
				
				if (covers.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<FactorRateRequestDetails> filterCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
					coverRes.setIsselected(filterCover.get(0).getIsSelected());
					coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
					coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
					coverRes.setSubCoverId(null);
					coverRes.setSubCoverDesc(null);
					coverRes.setSubCoverName(null);
					coverRes.setSectionId(filterCover.get(0).getSectionId()==null?"":filterCover.get(0).getSectionId().toString());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setExchangeRate(filterCover.get(0).getExchangeRate());	
					coverRes.setCdRefNo(filterCover.get(0).getCdRefno());
					coverRes.setCreatedBy(filterCover.get(0).getCreatedBy());
					coverRes.setInsuranceId(filterCover.get(0).getCompanyId());
					coverRes.setMsrefno(filterCover.get(0).getMsRefno());
					coverRes.setProductId(filterCover.get(0).getProductId().toString() );
					coverRes.setRequestReferenceNo(filterCover.get(0).getRequestReferenceNo());
					coverRes.setSectionId(filterCover.get(0).getSectionId()==null?"" :filterCover.get(0).getSectionId().toString());
					coverRes.setVdRefNo(filterCover.get(0).getVdRefno());
					coverRes.setVehicleId(filterCover.get(0).getVehicleId()==null?"" :filterCover.get(0).getVehicleId().toString());
					coverRes.setMultiSelectYn(filterCover.get(0).getMultiSelectYn()==null?"":filterCover.get(0).getMultiSelectYn());
					coverRes.setDiffPremiumIncludedTax(filterCover.get(0).getDiffPremiumIncludedTaxFc());
					coverRes.setEffectiveDate(filterCover.get(0).getCoverPeriodFrom());
					coverRes.setPolicyEndDate(filterCover.get(0).getCoverPeriodTo());
					coverRes.setProRata(filterCover.get(0).getProRataPercent());
					coverRes.setProRataYn(filterCover.get(0).getProRataYn());
					
					// Discount Covers Or Promo Covers
					List<FactorRateRequestDetails> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && (   o.getCoverageType().equalsIgnoreCase("D") || o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
					
					if ( filterDiscountCover.size() > 0 ) {
						 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
						 coverRes.setDiscounts(discounts);	
					}
					
					// Tax Covers
					List<FactorRateRequestDetails> filterTaxCover = covers.stream().filter( o -> 
					(! o.getTaxId().equals(0)) && o.getDiscLoadId()==0 &&   o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
					
					if( filterTaxCover.size() > 0 ) {
						 List<Tax> taxes = getTaxRates(filterTaxCover) ;
						 coverRes.setTaxes(taxes);	
					}

					//Endorsement
					List<FactorRateRequestDetails> filterEndtCover = covers.stream().filter(o -> (o.getDiscLoadId()!=0 &&   o.getCoverageType().equalsIgnoreCase("E"))).collect(Collectors.toList());
					
					if(filterEndtCover.size() > 0 ) {
						List<Endorsement> endorsment  =   getEndorsementRates(filterEndtCover,covers);
						coverRes.setEndorsements(endorsment);
					}
						
					
					
					// Loginds Covers
					List<FactorRateRequestDetails> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
					
					if( filterLodingCover.size() > 0 ) {
						 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
						 coverRes.setLoadings(lodings);	
					}
										
				} else {
					
					// Get Sub Covers
			
					List<FactorRateRequestDetails> filterCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
					 coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCalcType(filterCover.get(0).getCalcType());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());
					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? BigDecimal.ZERO : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null:Double.valueOf(filterCover.get(0).getRate().toString()));
					 coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setMultiSelectYn(filterCover.get(0).getMultiSelectYn()==null?"":filterCover.get(0).getMultiSelectYn());		
					coverRes.setSectionId(filterCover.get(0).getSectionId()==null?"":filterCover.get(0).getSectionId().toString());
					coverRes.setEffectiveDate(filterCover.get(0).getCoverPeriodFrom());
					coverRes.setPolicyEndDate(filterCover.get(0).getCoverPeriodTo());
					coverRes.setProRata(filterCover.get(0).getProRataPercent());
					coverRes.setProRataYn(filterCover.get(0).getProRataYn());
						
					List<Cover>  subCoverListRes = new ArrayList<Cover>();
					List<FactorRateRequestDetails> filterSubCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
					for ( FactorRateRequestDetails subCovers : filterSubCover) {
						Cover subCoverRes = new Cover();
						subCoverRes = dozerMapper.map(subCovers, Cover.class);
						subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
						subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
						subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
						subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate());	
						subCoverRes.setSectionId(filterSubCover.get(0).getSectionId()==null?"":filterSubCover.get(0).getSectionId().toString());

						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC( filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setCdRefNo(filterSubCover.get(0).getCdRefno());
						subCoverRes.setCreatedBy(filterSubCover.get(0).getCreatedBy());
						subCoverRes.setInsuranceId(filterSubCover.get(0).getCompanyId());
						subCoverRes.setMsrefno(filterSubCover.get(0).getMsRefno());
						subCoverRes.setProductId(filterSubCover.get(0).getProductId().toString() );
						subCoverRes.setRequestReferenceNo(filterSubCover.get(0).getRequestReferenceNo());
						subCoverRes.setSectionId(filterSubCover.get(0).getSectionId()==null?"" :filterSubCover.get(0).getSectionId().toString());
						subCoverRes.setVdRefNo(filterSubCover.get(0).getVdRefno());
						subCoverRes.setVehicleId(filterSubCover.get(0).getVehicleId()==null?"" :filterSubCover.get(0).getVehicleId().toString());
						subCoverRes.setDiffPremiumIncludedTax(filterSubCover.get(0).getDiffPremiumIncludedTaxFc());
						subCoverRes.setDiffPremiumIncludedTaxLC(filterSubCover.get(0).getDiffPremiumIncludedTaxLc());
						subCoverRes.setEffectiveDate(filterSubCover.get(0).getCoverPeriodFrom());
						subCoverRes.setPolicyEndDate(filterSubCover.get(0).getCoverPeriodTo());
						subCoverRes.setProRata(filterSubCover.get(0).getProRataPercent());
						subCoverRes.setProRataYn(filterSubCover.get(0).getProRataYn());
						
						
						// Discount Covers Or Promo Covers
						List<FactorRateRequestDetails> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) && ( ! o.getDiscLoadId().equals(0)) && (   o.getCoverageType().equalsIgnoreCase("D") || o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 subCoverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<FactorRateRequestDetails> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) && (! o.getTaxId().equals(0)) &&  o.getIsSelected().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 subCoverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<FactorRateRequestDetails> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getIsSelected().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 subCoverRes.setLoadings(lodings);	
						}
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
			
			System.out.print("cover sort");
			coverListRes.sort(Comparator.comparing(Cover ::    getSumInsured ).reversed() );
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return coverListRes;
	}
	public List<Discount> getDiscountRates(List<FactorRateRequestDetails> filterDiscountCover) {
		List<Discount> DiscountList = new  ArrayList<Discount>();
		try {
			for (FactorRateRequestDetails disc :  filterDiscountCover ) {
				Discount discount = new Discount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc());
				discount.setDiscountCalcType(disc.getCalcType());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				discount.setDiscountDesc(disc.getCoverName());	
				discount.setDiscountRate(disc.getRate()==null?"0.0" :disc.getRate().toString());
				discount.setFactorTypeId(disc.getFactorTypeId()==null?"" : disc.getFactorTypeId().toString());
				discount.setMaxAmount(disc.getMinimumPremium());
				discount.setSubCoverId(disc.getSubCoverId().toString());
				discount.setDiscountforId(disc.getDependentCoverId()==null?null:disc.getDependentCoverId().toString());
				
				DiscountList.add(discount);
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return DiscountList;
	}
	
	
	public List<Loading> getLodingCovers(List<FactorRateRequestDetails> filterLodingCover) {
		List<Loading> LodingList = new  ArrayList<Loading>();
		try {
			for (FactorRateRequestDetails lod :  filterLodingCover ) {
				Loading loding = new Loading();
				loding.setFactorTypeId(lod.getFactorTypeId()==null?null:lod.getFactorTypeId().toString());
				loding.setLoadingAmount(lod.getMinimumPremium());
				loding.setLoadingCalcType(lod.getCalcType());
				loding.setLoadingDesc(lod.getCoverName());
				loding.setLoadingforId(lod.getDependentCoverId()==null?null:lod.getDependentCoverId().toString());
				loding.setLoadingId(lod.getDiscLoadId()==null?null:lod.getDiscLoadId().toString());
				loding.setLoadingRate(lod.getRate()==null?null:lod.getRate().toString());
				loding.setMaxAmount(lod.getPremiumIncludedTaxFc());
				//loding.setSubCoverId(lod.getLodingSubcoverId()==null?null:lod.getLodingSubcoverId().toString());	
				LodingList.add(loding);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return LodingList;
	}
	
	public List<Tax> getTaxRates(List<FactorRateRequestDetails> filterTaxCover) {
		List<Tax> TaxList = new  ArrayList<Tax>();
		try {
			for (FactorRateRequestDetails tax :  filterTaxCover ) {
				Tax taxes = new Tax();
				taxes.setCalcType(tax.getCalcType());
				taxes.setIsTaxExempted(tax.getIsTaxExtempted());
				taxes.setTaxAmount(tax.getTaxAmount());
				taxes.setTaxDesc(tax.getTaxDesc());
				taxes.setTaxExemptCode(tax.getTaxExemptCode());
				taxes.setTaxExemptType(tax.getTaxExemptType());
				taxes.setTaxId(tax.getTaxId()==null?null:tax.getTaxId().toString()) ;
				taxes.setTaxRate( tax.getTaxRate()==null?null : Double.valueOf(tax.getTaxRate().toString()));
				TaxList.add(taxes);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return TaxList;
	}
	
	public List<Endorsement> getEndorsementRates(List<FactorRateRequestDetails> filterEndtCover ,List<FactorRateRequestDetails> totalCovers) {
		List<Endorsement> endtList = new  ArrayList<Endorsement>();
		try {
			for (FactorRateRequestDetails t :  filterEndtCover ) {
				 Endorsement d=Endorsement.builder()
						 	.endorsementDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.endorsementId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.endorsementRate("F".equals(t.getCalcType()==null?"A":t.getCalcType())?"0": t.getRate()==null?"0":t.getRate().toString())
						 	.endorsementCalcType(t.getCalcType()==null?"":t.getCalcType())
						 	.endorsementforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())	
						 	.premiumAfterDiscount(t.getPremiumAfterDiscountFc())
						    .premiumAfterDiscountLC(t.getPremiumAfterDiscountLc())
						     .premiumBeforeDiscount(t.getPremiumBeforeDiscountFc())
						    .premiumBeforeDiscountLC(t.getPremiumBeforeDiscountLc())
						    .premiumExcluedTax(t.getPremiumExcludedTaxFc())
						    .premiumExcluedTaxLC(t.getPremiumExcludedTaxLc())
						    .premiumIncludedTax(t.getPremiumIncludedTaxFc())
						    .premiumIncludedTaxLC(t.getPremiumIncludedTaxLc())	 
						    .endtCount(t.getEndtCount())
						     .proRata(t.getProRataPercent())
						     .proRataYn(t.getProRataYn())
						 	.build();
				 
				
					
					
				 endtList.add(d);
			}
			
			
			 TaxFromFactor endttaxUtil=new TaxFromFactor();
				if(endtList!=null && endtList.size()>0) {
					for (Endorsement e : endtList) {
						
						// only for endrose we cannt use cover objs tax cover wontbe list.
						 List<Tax> txx = totalCovers.stream().filter(r -> (r.getDiscLoadId()==Integer.parseInt(e.getEndorsementId())
								 && r.getCoverId()==Integer.parseInt(e.getEndorsementforId())
								 && r.getEndtCount().intValue()==e.getEndtCount().intValue()
								 && r.getTaxId() != Integer.parseInt(e.getEndorsementId())
								 )
								 
								  ).map(endttaxUtil).filter(dx->(dx!=null && !"0".equals(dx.getTaxId())) ).collect(Collectors.toList());
						 e.setTaxes(txx);
						 
						 List<Tax> endtfees = totalCovers.stream().filter(r -> (r.getDiscLoadId()==Integer.parseInt(e.getEndorsementId())
								 && r.getCoverId()==Integer.parseInt(e.getEndorsementforId())
								 && r.getEndtCount().intValue()==e.getEndtCount().intValue()
								 && r.getTaxId() == Integer.parseInt(e.getEndorsementId())
								 )
								 
								  ).map(endttaxUtil).filter(dx->(dx!=null && !"0".equals(dx.getTaxId())) ).collect(Collectors.toList());
						 	e.setEndtFees(endtfees);	
						 
					}
				}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return endtList;
	}


//Get Customer Details -Search
	@Override
	public List<SearchCustomerDetailsRes> adminCustomerSearch(SearchReq req) {
		List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
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

			String customerId="";
			// Product Wise Get
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
					List<HomePositionMaster> homelist = homeRepo.findByRequestReferenceNo(searchValue);
					customerId = homelist.get(0).getCustomerId();
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);
				} 
				else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
					HomePositionMaster homelist = homeRepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(searchValue,
							"Y", companyId, Integer.valueOf(req.getProductId()));
					customerId = homelist.getCustomerId();
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);
				} 
				else if ("CustomerName".equalsIgnoreCase(searchKey)) {
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);
				}
				else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
					HomePositionMaster homeData = homeRepo.findByQuoteNo(searchValue);
					customerId = homeData.getCustomerId();
//					PersonalInfo custData = perRepo.findByCustomerId(homeData.getCustomerId());
//					SearchCustomerDetailsRes custRes = new SearchCustomerDetailsRes();
//					custRes = dozerMapper.map(custData, SearchCustomerDetailsRes.class);
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);

				} 
				else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);

				} else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
					list = motService.searchCutomerDetails(searchKey, searchValue, companyId, loginId, userType,
							branches, customerId);

				}
			}

			for (Tuple data : list) {
				SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
				res = dozerMapper.map(data.get(0),SearchCustomerDetailsRes.class);	
				res.setLoginId(loginId);
				res.setApplicationId(req.getApplicationId());
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
	public List<SearchEservieMotorDetailsViewRatingRes> adminViewRatingDetails(SearchReq req) {
		List<SearchEservieMotorDetailsViewRatingRes>  resList = new ArrayList<SearchEservieMotorDetailsViewRatingRes>();
		try {
			if(StringUtils.isNotBlank(req.getRequestReferenceNo())){
			// Find Risk Datas
			resList = getRiskDetails(req);
			
			// Find Covers
			List<FactorRateRequestDetails> findCovers = factorrepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
			
			//Response 
			for (SearchEservieMotorDetailsViewRatingRes res : resList ) {
				
				// Set Covers
				List<FactorRateRequestDetails> filterVehicleCovers =  findCovers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(res.getVehicleId())) &&
						o.getCompanyId().equals(res.getInsuranceId()) && o.getProductId().toString().equals(res.getProductId()) && o.getSectionId().toString().equals(res.getSectionId()) ).collect(Collectors.toList());
				
				Map<Integer,List<FactorRateRequestDetails>> groupByCover = filterVehicleCovers.stream().collect(Collectors.groupingBy(FactorRateRequestDetails :: getCoverId));			
				List<Cover> coverListRes = 	getCoversList(groupByCover);
				coverListRes.forEach(cov ->  cov.setSectionName(res.getSectionName())) ;
				
				}
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return resList;
	}

	public  List<SearchEservieMotorDetailsViewRatingRes> getRiskDetails(SearchReq req) {
		 List<SearchEservieMotorDetailsViewRatingRes> viewDetailsList  = new ArrayList<SearchEservieMotorDetailsViewRatingRes>() ;
		try {
			if(req.getProductId().equalsIgnoreCase(motorProductId)) {
				viewDetailsList = getMotorDetails(req) ;
				
			}
//			else if(req.getProductId().equalsIgnoreCase(travelProductId)) {
//				viewDetailsList = getTravelDetails(req) ;
//				
//			} else if(req.getProductId().equalsIgnoreCase(buildingProductId) || req.getProductId().equalsIgnoreCase(smeProductId)) {
//				viewDetailsList = getBuildingDetails(req) ;
//				
//			} else {
//				viewDetailsList = getCommonDetails(req) ;
//			}
			
			
			
		} catch(Exception e) {
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return viewDetailsList;
	}
	public  List<SearchEservieMotorDetailsViewRatingRes> getMotorDetails(SearchReq req   ) {
		 List<SearchEservieMotorDetailsViewRatingRes> motorDetailsList  = new ArrayList<SearchEservieMotorDetailsViewRatingRes>() ;
		 DozerBeanMapper dozerMapper = new DozerBeanMapper() ;
		try {
			// Motor Product Details
			List<EserviceMotorDetails>    motorDatas = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
					
			for (EserviceMotorDetails mot :  motorDatas) {
				// Response 
				SearchEservieMotorDetailsViewRatingRes res = new SearchEservieMotorDetailsViewRatingRes();
				
				dozerMapper.map(mot,res);
				res.setInsuranceId(mot.getCompanyId());
				res.setOverallPremiumFc(mot.getOverallPremiumFc()==null?"0": mot.getOverallPremiumFc().toPlainString());
				res.setOverallPremiumLc(mot.getOverallPremiumLc()==null?"0":mot.getOverallPremiumLc().toPlainString());
				res.setActualPremiumFc(mot.getActualPremiumFc()==null?"0":mot.getActualPremiumFc().toPlainString());
				res.setActualPremiumLc(mot.getActualPremiumLc()==null?"0":mot.getActualPremiumLc().toPlainString());
				res.setVehicleId(mot.getRiskId().toString());
				res.setGroupId(1);
				res.setGroupMember(0);
				res.setSectionId(mot.getSectionId() );
				res.setSectionName(mot.getSectionName());	
				res.setEffectiveDate(mot.getEndorsementEffdate()==null?null:mot.getEndorsementEffdate() );
				
				Object riskDetails = new Object();
				EserviceMotorDetailsRes  motorRes = new EserviceMotorDetailsRes();
				dozerMapper.map(mot, motorRes);
				//motorRes.setSectionName(mot.getSectionName());
				riskDetails = motorRes ;
				res.setRiskDetails(riskDetails);
				motorDetailsList.add(res);
			}
			
		} catch(Exception e) {
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return motorDetailsList;
	}
}

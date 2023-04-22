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
import java.util.concurrent.ConcurrentHashMap;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.CoverDocumentUploadDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorDriverDetails;
import com.maan.eway.bean.MotorVehicleInfo;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalAccident;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionMaster;

import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.BuildingSearchRes;
import com.maan.eway.common.res.DocumentRes;
import com.maan.eway.common.res.PersonalAccidentRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchDriverDetailsRes;
import com.maan.eway.common.res.SearchPaymentInfoRes;
import com.maan.eway.common.res.SearchPremiumCoverDetailsRes;
import com.maan.eway.common.res.SearchPremiumDetailsRes;
import com.maan.eway.common.res.SearchROPDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleDetailsRes;
import com.maan.eway.common.res.SearchROPVehicleRes;
import com.maan.eway.common.res.SearchRes;
import com.maan.eway.common.service.BuildingSearchService;
import com.maan.eway.common.service.CommonSearchService;
import com.maan.eway.common.service.MotorSearchService;
import com.maan.eway.common.service.SearchService;
import com.maan.eway.common.service.TravelSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.notification.repository.CoverDocumentUploadDetailsRepository;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MotorDriverDetailsRepository;
import com.maan.eway.repository.MotorVehicleInfoRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PersonalAccidentRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SubCoverRes;

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
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;

	@Autowired
	private MotorSearchService motService;
	@Autowired
	private BuildingSearchService buiService;

	@Autowired
	private PaymentInfoRepository paymentrepo;

	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private PersonalInfoRepository perRepo;
	@Autowired
	CoverDocumentUploadDetailsRepository coverdocumentuploaddetailsrepository;

	@Autowired
	private MotorVehicleInfoRepository motVehInfoRepo;

	@Autowired
	private CommonSearchService commonSearch;
	@Autowired
	private TravelSearchService travelSearch;
	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private MotorDriverDetailsRepository driverRepo;

	@Autowired
	private MotorDataDetailsRepository motorRepo;

	@Autowired
	private CoverDetailsRepository coverRepo;

	@Autowired
	BuildingDetailsRepository buildingrepo;

	@Autowired
	PersonalAccidentRepository personalRepository;

	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(SearchServiceImpl.class);

	//Dropdown
	//CopyQuote Dropdown 
	

	@Override
	public List<BuildingSearchRes> adminSearchBuildingDeatails(SearchReq req) {
		List<BuildingSearchRes> builLisRes=new ArrayList<BuildingSearchRes>();
		try {
			
			
			BuildingSearchRes bulRes=new BuildingSearchRes();
			List<BuildingDetails> buldingListDt = new ArrayList<BuildingDetails>();
			
			if (req.getProductId().equalsIgnoreCase(buildingProductId)) 
			{
				
				buldingListDt=buildingrepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			}
			
            if(buldingListDt!=null && buldingListDt.size()>0)

            {
            	for(BuildingDetails data:buldingListDt)
            	{
            		bulRes = new DozerBeanMapper().map(data, BuildingSearchRes.class);
            		builLisRes.add(bulRes);

            	}
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		
		return builLisRes;
	}
	
	@Override
	public List<PersonalAccidentRes> viewPersonalAccidentDetails(SearchReq req) {
		
		List<PersonalAccidentRes> preslist = new ArrayList<PersonalAccidentRes>();

		try {

			PersonalAccidentRes pres = new PersonalAccidentRes();

			List<PersonalAccident> personalList = new ArrayList<PersonalAccident>();

			if (req.getProductId().equalsIgnoreCase(buildingProductId)) 
			{
			 if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 personalList = personalRepository.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			}
			}
            if(personalList!=null && personalList.size()>0)
            {
			for (PersonalAccident data : personalList) {

				pres = new DozerBeanMapper().map(data, PersonalAccidentRes.class);
				preslist.add(pres);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return preslist;
	}	

	
	@Override
	public List<DropDownRes> searchDropdown(CopyQuoteDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {

			List<ListItemValue> getList = new ArrayList<ListItemValue>();
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				getList = motService.searchDropdownMotor(req);
			} else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
				getList = travelSearch.searchDropdownTravel(req);
			} else if (req.getProductId().equalsIgnoreCase(buildingProductId)
					|| req.getProductId().equalsIgnoreCase(smeProductId)) {
				getList = buiService.searchDropdownBuilding(req);
			} else {
				getList = commonSearch.searchDropdownCommon(req);
			}

			for (ListItemValue data : getList) {
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
			Date effectiveDate = null;
			List<BranchMaster> branchlist = getByBranchCode(req.getBranchCode());
			String branchName = branchlist.get(0).getBranchName();
			String loginId = "";
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1")) {
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

			else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
				list = travelSearch.searchTravel(req, branches);
			} else if (req.getProductId().equalsIgnoreCase(buildingProductId)|| req.getProductId().equalsIgnoreCase(smeProductId)) {
				list = buiService.searchBuilding(req, branches);
			} else {
				list = commonSearch.searchCommon(req, branches);
			}

			for (Tuple data : list) {
				SearchRes res = new SearchRes();
				res = dozermapper.map(data.get(0), SearchRes.class);
				res.setClientName((data.get("clientName").toString()));
				res.setMobileNo1((data.get("mobileNumber").toString()));
				res.setBranchName(branchName);
				res.setLoginId(req.getLoginId());
				res.setEffectiveDate(effectiveDate);
				// res.setIdsCount(data.get("idsCount")==null?"":data.get("idsCount").toString()
				// );
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
		try {
			HomePositionMaster homeData =null;
			if(StringUtils.isNotBlank(req.getQuoteNo())){
				homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());	
			}
			if(homeData!=null) {
			// Motor Product Details
			if( homeData.getProductId().equals(Integer.valueOf(motorProductId))) {
				viewRes =motService.getMotorProductDetails( req);
				
			}
			else if( homeData.getProductId().equals(Integer.valueOf(travelProductId))) {
				// Travel Product Details
				viewRes =travelSearch.getTravelProductDetails( req);
				
			} else if( homeData.getProductId().equals(Integer.valueOf(buildingProductId)) || homeData.getProductId().equals(Integer.valueOf(smeProductId)) ) {
				// Building Product Details
				viewRes =buiService.getBuildingProductDetails( req);
				
			} else {
				// Travel Product Details
				viewRes =commonSearch.getCommonProductDetails( req);
				
			}
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}

	
	public List<SearchPremiumCoverDetailsRes> getCoverDetails(Map<Integer,List<PolicyCoverData>> groupByCover  ) {
		List<SearchPremiumCoverDetailsRes>  coverListRes = new ArrayList<SearchPremiumCoverDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			String sectionName="";
			for ( Integer coverId : groupByCover.keySet() ) {
				List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
				SearchPremiumCoverDetailsRes coverRes = new SearchPremiumCoverDetailsRes();
				
				if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), SearchPremiumCoverDetailsRes.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setCoverageType(filterCover.get(0).getCoverageType());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount()==null ? "" :filterCover.get(0).getExcessAmount().toPlainString() );
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent()==null ? "" :filterCover.get(0).getExcessPercent().toPlainString() );
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());	
					if(!StringUtils.isBlank(filterCover.get(0).getSectionId().toString())){
						List<SectionMaster> sectiondata=getBySectionId(filterCover.get(0).getSectionId().toString());
						sectionName=sectiondata.get(0).getSectionName();
					}
					coverRes.setSectionName(sectionName);
										
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
						if(!StringUtils.isBlank(filterCover.get(0).getSectionId().toString())){
							List<SectionMaster> sectiondata=getBySectionId(filterCover.get(0).getSectionId().toString());
							sectionName=sectiondata.get(0).getSectionName();
						}
						coverRes.setSectionName(sectionName);
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
	
			coverListRes.sort(Comparator.comparing(SearchPremiumCoverDetailsRes :: getCoverId));;
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return coverListRes;
	}
	
	public List<SectionMaster> getBySectionId(String sectionid) {
		List<SectionMaster> list = new ArrayList<SectionMaster>();
	
		try {
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SectionMaster> query = cb.createQuery(SectionMaster.class);
	
			
			// Find All
			Root<SectionMaster>    c = query.from(SectionMaster.class);		
			
			// Select
			query.select(c );
			
			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SectionMaster> ocpm1 = amendId.from(SectionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId") );
			amendId.where(a1);
			
			
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(c.get("effectiveDateStart")));
			
		    // Where	
		
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("amendId"), amendId);		
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("sectionId"),sectionid) ;
			query.where(n1 ,n2).orderBy(orderList);
			
			// Get Result
			TypedQuery<SectionMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	
	

// Customer Details -Search
	@Override
	public List<SearchCustomerDetailsRes> adminCustomerSearch(SearchReq req) {
		List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
		try {
			List<HomePositionMaster> homeData = null;
			// Product Wise Get
			if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				homeData = homeRepo.findByRequestReferenceNoAndProductId(req.getRequestReferenceNo(),Integer.valueOf(req.getProductId()));
			} else if (StringUtils.isNotBlank(req.getQuoteNo())) {
				homeData = homeRepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
			}
			if (homeData.size() > 0 && homeData!=null ) {
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					reslist = motService.motorCustSearch(req, homeData);
				} else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
					reslist = travelSearch.travelCustSearch(req, homeData);
				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)|| req.getProductId().equalsIgnoreCase(smeProductId)) {
					reslist = buiService.buildingCustSearch(req, homeData);
				} else {
					reslist = commonSearch.commonCustSearch(req, homeData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}



	//Rating Details
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> adminViewRatingDetails(SearchReq req) {
		List<SearchEservieMotorDetailsViewRatingRes>  resList = new ArrayList<SearchEservieMotorDetailsViewRatingRes>();
		try {
			
			// Product Wise Get
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				resList = motService.motorRating(req);
			} else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
				resList = travelSearch.travelRating(req);
			} else if (req.getProductId().equalsIgnoreCase(buildingProductId)|| req.getProductId().equalsIgnoreCase(smeProductId)) {
				resList = buiService.buildingRating();
			} else {
				resList = commonSearch.commonRating(req);
			}

		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return resList;
	}

	//Premium Details
	@Override
	public SearchPremiumDetailsRes adminPremiumSearch(SearchReq req) {
		SearchPremiumDetailsRes viewRes = new SearchPremiumDetailsRes();
		try {
			List<MotorDataDetails> motorDatas=null;
			List<PolicyCoverData> covers=null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				// Find Motor Data
				 motorDatas = motorRepo
						.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
				 covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 motorDatas = motorRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
					 covers = coverRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
			}
				for (MotorDataDetails mot : motorDatas) {
					// Cover Details
					List<PolicyCoverData> filterCovers = covers.stream()
							.filter(o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId())))
							.collect(Collectors.toList());

					Map<Integer, List<PolicyCoverData>> groupByCover = filterCovers.stream()
							.collect(Collectors.groupingBy(PolicyCoverData::getCoverId));

					List<SearchPremiumCoverDetailsRes> coverListRes = getCoverDetails(groupByCover);
					viewRes.setSearchPremiumCoverDetailsRes(coverListRes);

				}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	//ROP Driver Details
	@Override
	public SearchROPDetailsRes adminROPDriverSearch(SearchReq req) {
		SearchROPDetailsRes viewRes = new SearchROPDetailsRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorDataDetails> motorDatas=null;
			List<MotorDriverDetails> driverList =null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				// Find Motor Data
				 motorDatas = motorRepo.findByQuoteNoAndStatusNotOrderByVehicleIdAsc(req.getQuoteNo(), "D");
				 driverList = driverRepo.findByQuoteNo(req.getQuoteNo() );
			}else if (StringUtils.isNotBlank(req.getQuoteNo())) {
				 motorDatas = motorRepo.findByRequestReferenceNoAndStatusNotOrderByVehicleIdAsc(req.getRequestReferenceNo(), "D");
				 driverList = driverRepo.findByRequestReferenceNo(req.getRequestReferenceNo() );
			}
			for (MotorDataDetails mot : motorDatas) {
			List<SearchDriverDetailsRes>   driverResList = new ArrayList<SearchDriverDetailsRes>();
			List<MotorDriverDetails> filterDriverList = driverList.stream().filter( o -> o.getRiskId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
			for (MotorDriverDetails dri :  filterDriverList) {
				SearchDriverDetailsRes driverRes  = new SearchDriverDetailsRes();  
				dozerMapper.map(dri, driverRes);
				driverRes.setLicenseNo(dri.getIdNumber());
				
				driverResList.add(driverRes);
				
			}
			driverResList.sort(Comparator.comparing(SearchDriverDetailsRes :: getDriverId  ));
			viewRes.setDriverDetails(driverResList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	//ROP Vehicle Details
	@Override
	public SearchROPVehicleDetailsRes adminROPVehicleSearch(SearchReq req) {
		SearchROPVehicleDetailsRes viewRes = new SearchROPVehicleDetailsRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
		List<EserviceMotorDetails> motorDatas=null;
		String chassisNo="";
		if (StringUtils.isNotBlank(req.getQuoteNo())) {
			// Find Motor Data
			 motorDatas = repo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			
			 }else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
			 motorDatas = repo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
		}
		List<SearchROPVehicleRes> resList=new ArrayList<SearchROPVehicleRes>();
		for (EserviceMotorDetails data : motorDatas) {
			chassisNo=data.getChassisNumber();
			MotorVehicleInfo vehInfo = motVehInfoRepo.findByResChassisNumber(chassisNo);
			if(vehInfo!=null) {
			SearchROPVehicleRes res =new SearchROPVehicleRes();
			res.setResRegNumber(vehInfo.getResRegNumber());
			res.setResChassisNumber(vehInfo.getResChassisNumber());
			res.setResEngineNumber(vehInfo.getResEngineNumber());
			res.setResMake(vehInfo.getResMake());
			res.setResModel(vehInfo.getResModel());
			res.setResColor(vehInfo.getResColor());
			res.setResBodyType(vehInfo.getResBodyType());
			res.setResYearOfManufacture(vehInfo.getResYearOfManufacture());
			resList.add(res);
			}
		}
		viewRes.setVehDetails(resList);		
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return viewRes;
	}


//Payment Info
	@Override
	public List<SearchPaymentInfoRes> viewPaymentInfo(SearchReq req) {
		SearchPaymentInfoRes paymentgetres = new SearchPaymentInfoRes();
		List<SearchPaymentInfoRes> paylist = new ArrayList<SearchPaymentInfoRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();

		try {
			List<PaymentInfo> paymentinfo = null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				paymentinfo = paymentrepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
			}  

			for (PaymentInfo pi : paymentinfo) {

				paymentgetres = new DozerBeanMapper().map(pi, SearchPaymentInfoRes.class);
				paylist.add(paymentgetres);

			}

		} catch (Exception e) {

			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}

		return paylist;

	}



	@Override
	public List<DocumentRes> viewDocumentDetails(SearchReq req) {
		List<DocumentRes> reslist = new ArrayList<DocumentRes>();

		try {

			DocumentRes dres = new DocumentRes();

			List<CoverDocumentUploadDetails> getList = null;

			if (StringUtils.isNotBlank(req.getQuoteNo())) {

				getList = coverdocumentuploaddetailsrepository.findByQuoteNo(req.getQuoteNo());
			} else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				getList = coverdocumentuploaddetailsrepository.findByRequestReferenceNo(req.getRequestReferenceNo());
			}

			for (CoverDocumentUploadDetails cd : getList) {

				dres = new DozerBeanMapper().map(cd, DocumentRes.class);
				reslist.add(dres);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return reslist;
	}


	


}

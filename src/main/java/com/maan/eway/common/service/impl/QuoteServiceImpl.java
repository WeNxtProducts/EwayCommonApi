package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.AdminReferalStatusReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.MotorProductDetailsRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.VehicleDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.res.QuoteUpdateRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;


@Service
public class QuoteServiceImpl implements QuoteService {

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private QuoteThreadService otSer ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository custRepo ;
	
	@Autowired
	private EServiceMotorDetailsRepository eserMotRepo;
	
	@Autowired
	private FactorRateRequestDetailsRepository eserCovRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private CoverDetailsRepository coverRepo;
	
	private Logger log = LogManager.getLogger(QuoteServiceImpl.class);
	
	@Override
	public CommonRes generateNewQuote(NewQuoteReq req) {
			CommonRes	res = otSer.call_OT_Insert(req);
			return res ;
			
	}

	@Override
	public ViewQuoteRes viewQuoteDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMappper = new DozerBeanMapper();
		try {
			// Quote Details
			HomePositionMaster homeData  =  homeRepo.findByQuoteNo(req.getQuoteNo());
			QuoteDetailsRes quoteRes = new QuoteDetailsRes();
			quoteRes = dozerMappper.map(homeData, QuoteDetailsRes.class);
			
			// Customer Details
			PersonalInfo custData = custRepo.findByCustomerId(homeData.getCustomerId());
			CustomerDetailsRes  custRes = new CustomerDetailsRes();
			custRes  = dozerMappper.map(custData, CustomerDetailsRes.class);
			
			// Motor Product Details
			if( homeData.getProductId().equals(Integer.valueOf(motorProductId))) {
				viewRes =  getMotorProductDetails( req);
				viewRes.setCustomerDetails(custRes);
				viewRes.setQuoteDetails(quoteRes);
			}
			
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	
	public ViewQuoteRes getMotorProductDetails(ViewQuoteReq req) {
		ViewQuoteRes viewRes = new ViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<MotorDataDetails> motorDatas =  motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			List<PolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<MotorProductDetailsRes>   motorResList = new ArrayList<MotorProductDetailsRes>();
			for (MotorDataDetails mot :  motorDatas) {
				
				// Mot
				VehicleDetailsRes vehicleDetails = new  VehicleDetailsRes()  ;
				dozerMapper.map(mot, vehicleDetails);
				
				// Cover Details
				List<PolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<PolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(PolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<PolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes = dozerMapper.map(filterCover.get(0), Cover.class);
						coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"":filterCover.get(0).getDependentCoverId().toString());
						coverRes.setPremiumExcluedTax( filterCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal(filterCover.get(0).getPremiumExcludedTaxFc()) );	
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null ? null : new BigDecimal((filterCover.get(0).getPremiumAfterDiscountFc().toString())));
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null ? null : new BigDecimal((filterCover.get(0).getPremiumBeforeDiscountFc().toString())));
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal((filterCover.get(0).getPremiumExcludedTaxFc().toString())));
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null ? null : new BigDecimal((filterCover.get(0).getPremiumIncludedTaxFc().toString())));
						coverRes.setIsselected(filterCover.get(0).getIsSelected());
						coverRes.setDependentCoveryn(filterCover.get(0).getDependentCoverYn());
						coverRes.setDependentCoverId(filterCover.get(0).getDependentCoverId()==null?"": filterCover.get(0).getDependentCoverId().toString());
						coverRes.setSubCoverId(null);
						coverRes.setSubCoverDesc(null);
						coverRes.setSubCoverName(null);
						coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null ? null : new BigDecimal (filterCover.get(0).getPremiumAfterDiscountFc()));
						coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null ? null : new BigDecimal (filterCover.get(0).getPremiumBeforeDiscountFc()));
						coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal (filterCover.get(0).getPremiumExcludedTaxFc()));
						coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null ? null :new BigDecimal(filterCover.get(0).getPremiumIncludedTaxFc()));
						coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null ? null : new BigDecimal(filterCover.get(0).getPremiumAfterDiscountLc()));
						coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null ? null : new BigDecimal( filterCover.get(0).getPremiumBeforeDiscountLc()));
						coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc()==null ? null : new BigDecimal(filterCover.get(0).getPremiumExcludedTaxLc()));
						coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc()==null ? null :new BigDecimal (filterCover.get(0).getPremiumIncludedTaxLc()));
						coverRes.setExchangeRate(filterCover.get(0).getExchangeRate()==null?null:new BigDecimal(filterCover.get(0).getExchangeRate()));	
						
						// Discount Covers
						List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("d") ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("t")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<Tax> taxes = getTaxRates(filterTaxCover) ;
							 coverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("l") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<Loading> lodings =  getLodingCovers(filterLodingCover) ;
							 coverRes.setLoadings(lodings);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<PolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0) &&  o.getTaxId().equals(0)).collect(Collectors.toList());
						coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate(filterCover.get(0).getRate());
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<PolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( PolicyCoverData subCovers : filterSubCover) {
							Cover subCoverRes = new Cover();
							subCoverRes = dozerMapper.map(subCovers, Cover.class);
							subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
							subCoverRes.setDependentCoveryn(filterSubCover.get(0).getDependentCoverYn());
							subCoverRes.setDependentCoverId(filterSubCover.get(0).getDependentCoverId()==null?"":filterSubCover.get(0).getDependentCoverId().toString());
							subCoverRes.setPremiumExcluedTax( filterSubCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal(filterSubCover.get(0).getPremiumExcludedTaxFc()) );	
							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc()==null ? null : new BigDecimal((filterSubCover.get(0).getPremiumAfterDiscountFc().toString())));
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc()==null ? null : new BigDecimal((filterSubCover.get(0).getPremiumBeforeDiscountFc().toString())));
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal((filterSubCover.get(0).getPremiumExcludedTaxFc().toString())));
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc()==null ? null : new BigDecimal((filterCover.get(0).getPremiumIncludedTaxFc().toString())));
							subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());
							subCoverRes.setExchangeRate(filterSubCover.get(0).getExchangeRate()==null?null:new BigDecimal(filterSubCover.get(0).getExchangeRate()));	
							

							subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc()==null ? null : new BigDecimal (filterSubCover.get(0).getPremiumAfterDiscountFc()));
							subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc()==null ? null : new BigDecimal (filterSubCover.get(0).getPremiumBeforeDiscountFc()));
							subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc()==null ? null : new BigDecimal (filterSubCover.get(0).getPremiumExcludedTaxFc()));
							subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc()==null ? null :new BigDecimal(filterSubCover.get(0).getPremiumIncludedTaxFc()));
							subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc()==null ? null : new BigDecimal(filterSubCover.get(0).getPremiumAfterDiscountLc()));
							subCoverRes.setPremiumBeforeDiscountLC(filterSubCover.get(0).getPremiumBeforeDiscountLc()==null ? null : new BigDecimal( filterSubCover.get(0).getPremiumBeforeDiscountLc()));
							subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc()==null ? null : new BigDecimal(filterSubCover.get(0).getPremiumExcludedTaxLc()));
							subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc()==null ? null :new BigDecimal (filterSubCover.get(0).getPremiumIncludedTaxLc()));
							
							
							// Discount Covers
							List<PolicyCoverData> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("d") ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
							}
							
							// Tax Covers
							List<PolicyCoverData> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  (! o.getTaxId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("t")).collect(Collectors.toList());
							
							if( filterTaxCover.size() > 0 ) {
								 List<Tax> taxes = getTaxRates(filterTaxCover) ;
								 subCoverRes.setTaxes(taxes);	
							}
							
							// Loginds Covers
							List<PolicyCoverData> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("l") ).collect(Collectors.toList());
							
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
				coverListRes.sort(Comparator.comparing(Cover :: getCoverId));;
				// Response
				MotorProductDetailsRes motorRes = new MotorProductDetailsRes();
				motorRes.setVehicleDetails(vehicleDetails);		
				motorRes.setCovers(coverListRes);
				motorResList.add(motorRes);				
			}
			viewRes.setProductDetails(motorResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}
	
	
	public List<Discount> getDiscountRates(List<PolicyCoverData> filterDiscountCover) {
		List<Discount> DiscountList = new  ArrayList<Discount>();
		try {
			for (PolicyCoverData disc :  filterDiscountCover ) {
				Discount discount = new Discount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc()==null?new BigDecimal(0): new BigDecimal(disc.getPremiumIncludedTaxFc()));
				discount.setDiscountCalcType(disc.getCalcType());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				discount.setDiscountDesc(disc.getCoverName());	
				discount.setDiscountRate(disc.getRate()==null?"0.0" :disc.getRate().toString());
				discount.setFactorTypeId(disc.getFactorTypeId()==null?"" : disc.getFactorTypeId().toString());
				discount.setMaxAmount(disc.getMinimumPremium()==null?null :new BigDecimal(disc.getMinimumPremium()));
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
	
	
	public List<Loading> getLodingCovers(List<PolicyCoverData> filterLodingCover) {
		List<Loading> LodingList = new  ArrayList<Loading>();
		try {
			for (PolicyCoverData lod :  filterLodingCover ) {
				Loading loding = new Loading();
				loding.setFactorTypeId(lod.getFactorTypeId()==null?null:lod.getFactorTypeId().toString());
				loding.setLoadingAmount(lod.getMinimumPremium()==null?null:new BigDecimal(lod.getMinimumPremium()));
				loding.setLoadingCalcType(lod.getCalcType());
				loding.setLoadingDesc(lod.getCoverName());
				loding.setLoadingforId(lod.getDependentCoverId()==null?null:lod.getDependentCoverId().toString());
				loding.setLoadingId(lod.getDiscLoadId()==null?null:lod.getDiscLoadId().toString());
				loding.setLoadingRate(lod.getRate()==null?null:lod.getRate().toString());
				loding.setMaxAmount(lod.getPremiumIncludedTaxFc()==null?null:new BigDecimal(lod.getPremiumIncludedTaxFc()));
				//loding.setSubCoverId(lod.getLodingSubcoverId()==null?null:lod.getLodingSubcoverId().toString());	
				LodingList.add(loding);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return LodingList;
	}
	
	public List<Tax> getTaxRates(List<PolicyCoverData> filterTaxCover) {
		List<Tax> TaxList = new  ArrayList<Tax>();
		try {
			for (PolicyCoverData tax :  filterTaxCover ) {
				Tax taxes = new Tax();
				taxes.setCalcType(tax.getCalcType());
				taxes.setIsTaxExempted(tax.getIsTaxExtempted());
				taxes.setTaxAmount(tax.getTaxAmount()==null?null:new BigDecimal(tax.getTaxAmount()));
				taxes.setTaxDesc(tax.getTaxDesc());
				taxes.setTaxExemptCode(tax.getTaxExemptCode());
				taxes.setTaxExemptType(tax.getTaxExemptType());
				taxes.setTaxId(tax.getTaxId()==null?null:tax.getTaxId().toString()) ;
				taxes.setTaxRate(tax.getTaxRate());
				TaxList.add(taxes);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return TaxList;
	}


	@Override
	public List<Error> validateReferralStatus(AdminReferalStatusReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {
			if(StringUtils.isBlank(req.getAdminLoginId())) {
				errors.add(new Error("01","AdminLoginID","Please Enter Admin LoginId"));
			}
			
			if(StringUtils.isBlank(req.getStatus())) {
				errors.add(new Error("02","ReferralStatus","Please Select Referral Status"));
			} else if ( !(req.getStatus().equalsIgnoreCase("RP") || req.getStatus().equalsIgnoreCase("RA") || req.getStatus().equalsIgnoreCase("RR")) ) {
				errors.add(new Error("02","ReferralStatus","Please Select Valid Referral Status Accept/Reject/Pending"));
			} else if ( req.getStatus().equalsIgnoreCase("RR")  ) {
				if(StringUtils.isBlank(req.getRejectReason())) {
					errors.add(new Error("03","Reject Reason","Please Enter Reject Reason"));
				}
			} 
			
//			if(StringUtils.isBlank(req.getAdminRemarks())) {
//				errors.add(new Error("03","Admin Remarks","Please Enter Admin Remarks"));
//			}
			
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return errors;
	}

	@Override
	public QuoteUpdateRes updateReferralStatus(AdminReferalStatusReq req) {
		QuoteUpdateRes updateRes = new QuoteUpdateRes();
		try {
			List<EserviceMotorDetails> motorDatas = eserMotRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
			
			// Referal Approve & Create New Quote
			if (req.getStatus().equalsIgnoreCase("RA") ) {
				List<FactorRateRequestDetails> coverDatas = eserCovRepo.findByRequestReferenceNoAndDiscLoadIdAndTaxIdAndUserOptOrderByVehicleIdAsc(req.getRequestReferenceNo(), 0 ,0,"Y");
				
				
				NewQuoteReq req2 = new NewQuoteReq();
				List<VehicleIdsReq> vehicleIdsList = new ArrayList<VehicleIdsReq>();
				
				for(EserviceMotorDetails mot : motorDatas ) {
					VehicleIdsReq vehDeh = new VehicleIdsReq();
					List<CoverIdsReq>  coverList = new ArrayList<CoverIdsReq>();
					
					for (FactorRateRequestDetails cov :  coverDatas ) {
						CoverIdsReq coverReq = new CoverIdsReq();
						if (cov.getCoverId().equals(cov.getSubCoverId())) {
							coverReq.setSubCoverId(null);
						} else {
							coverReq.setSubCoverId(cov.getSubCoverId().toString());
						}
						coverReq.setIsReferal(cov.getIsReferral());
						coverReq.setCoverId(cov.getCoverId());
						coverReq.setSubCoverYn(cov.getSubCoverYn());
						coverList.add(coverReq);
						
					}
					vehDeh.setCoverIdList(coverList);
					vehDeh.setVehicleId(mot.getVehicleId());
					vehicleIdsList.add(vehDeh);
				}
				
				
				
				req2.setAdminLoginId(req.getAdminLoginId());
				req2.setCreatedBy(req.getAdminLoginId());	
				req2.setProductId(req.getProductId());
				req2.setRequestReferenceNo(req.getRequestReferenceNo());
				req2.setVehicleIdsList(vehicleIdsList);
				CommonRes	res = otSer.call_OT_Insert(req2);
				NewQuoteRes response = (NewQuoteRes) res.getCommonResponse();
				updateRes.setResponse("Referal Approved");
				updateRes.setQuoteNo(response.getQuoteNo());
				updateRes.setCustomerId(response.getCustomerId());
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				
				
				// Referal Pending
			} else if (req.getStatus().equalsIgnoreCase("RP")  ) {
				updateRes.setResponse("Referal Pending");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
				// Referal Reject
			} else if (req.getStatus().equalsIgnoreCase("RR") ) {
				updateRes.setResponse("Referal Rejected");
				updateRes.setQuoteNo("");
				updateRes.setCustomerId("");
				updateRes.setRequestReferenceNo(req.getRequestReferenceNo());
			} 
			
			// Update Mot Status 
			for ( EserviceMotorDetails mot : motorDatas ) {
				mot.setStatus(req.getStatus());
				mot.setAdminLoginId(req.getAdminLoginId());
				mot.setAdminRemarks(req.getAdminRemarks());
				mot.setRejectReason(req.getRejectReason());
				mot.setUpdatedDate(new Date());
				eserMotRepo.saveAndFlush(mot);
				
			}
		
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return updateRes;
	}
}

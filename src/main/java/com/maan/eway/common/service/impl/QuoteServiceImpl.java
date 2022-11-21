package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.MotorPolicyCoverData;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CustomerDetailsRes;
import com.maan.eway.common.res.MotorProductDetailsRes;
import com.maan.eway.common.res.QuoteDetailsRes;
import com.maan.eway.common.res.VehicleDetailsRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.repository.CoverDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.res.CommonRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;


@Service
public class QuoteServiceImpl implements QuoteService {


	@Autowired
	private QuoteThreadService otSer ;
	
	@Autowired
	private HomePositionMasterRepository homeRepo ;
	
	@Autowired
	private PersonalInfoRepository custRepo ;
	
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
			if( homeData.getProductId().equals(5)) {
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
			List<MotorPolicyCoverData>  covers = coverRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
			
			List<MotorProductDetailsRes>   motorResList = new ArrayList<MotorProductDetailsRes>();
			for (MotorDataDetails mot :  motorDatas) {
				
				// Mot
				VehicleDetailsRes vehicleDetails = new  VehicleDetailsRes()  ;
				dozerMapper.map(mot, vehicleDetails);
				
				// Cover Details
				List<MotorPolicyCoverData> filterCovers = covers.stream().filter( o -> o.getVehicleId().equals(Integer.valueOf(mot.getVehicleId()))).collect(Collectors.toList());
				
				Map<Integer,List<MotorPolicyCoverData>> groupByCover = filterCovers.stream().collect(Collectors.groupingBy(MotorPolicyCoverData :: getCoverId));			
				
				List<Cover>  coverListRes = new ArrayList<Cover>();
				
				for ( Integer coverId : groupByCover.keySet() ) {
					List<MotorPolicyCoverData>  coverGroups  = groupByCover.get(coverId);
					Cover coverRes = new Cover();
					
					if (coverGroups.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
						// Get Covers
						List<MotorPolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
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
						
						List<MotorPolicyCoverData> filterDiscountCover = covers.stream().filter( o -> ! o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
							 coverRes.setDiscounts(discounts);	
						}
											
					} else {
						
						// Get Sub Covers
				
						List<MotorPolicyCoverData> filterCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						 coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
						 coverRes.setCalcType(filterCover.get(0).getCalcType());
						 coverRes.setCoverName(filterCover.get(0).getCoverName());
						 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
						 coverRes.setMinimumPremium(filterCover.get(0).getMinimumPremium()==null ? null : new BigDecimal(filterCover.get(0).getMinimumPremium().toString()));
						 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
						 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? null : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
						 coverRes.setRate(filterCover.get(0).getRate());
						
						List<Cover>  subCoverListRes = new ArrayList<Cover>();
						List<MotorPolicyCoverData> filterSubCover = coverGroups.stream().filter( o -> o.getDiscLoadId().equals(0)).collect(Collectors.toList());
						for ( MotorPolicyCoverData subCovers : filterSubCover) {
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
							List<MotorPolicyCoverData> filterDiscountCover = coverGroups.stream().filter( o -> o.getSubCoverId().equals(subCovers.getSubCoverId()) && ( ! o.getDiscLoadId().equals(0)) ).collect(Collectors.toList());
							
							if ( filterDiscountCover.size() > 0 ) {
								 List<Discount> discounts =  getDiscountRates(filterDiscountCover);
								 subCoverRes.setDiscounts(discounts);	
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
	
	
	public List<Discount> getDiscountRates(List<MotorPolicyCoverData> filterDiscountCover) {
		List<Discount> DiscountList = new  ArrayList<Discount>();
		try {
			for (MotorPolicyCoverData disc :  filterDiscountCover ) {
				Discount discount = new Discount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc()==null?null : new BigDecimal(disc.getPremiumIncludedTaxFc()));
				discount.setDiscountCalcType(disc.getCalcType());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				
				discount.setDiscountRate(disc.getRate()==null?null :disc.getRate().toString());
				discount.setFactorTypeId(disc.getFactorTypeId()==null?"" : disc.getFactorTypeId().toString());
				discount.setMaxAmount(disc.getMinimumPremium()==null?null :new BigDecimal(disc.getMinimumPremium()));
				discount.setSubCoverId(disc.getSubCoverId().toString());
				discount.setDiscountforId(null);
				discount.setDiscountDesc("");	
				DiscountList.add(discount);
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return DiscountList;
	}
}

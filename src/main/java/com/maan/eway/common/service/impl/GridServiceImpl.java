package com.maan.eway.common.service.impl;

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

import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.IssuerQuoteReq;
import com.maan.eway.common.req.UpdateLapsedQuoteReq;
import com.maan.eway.common.res.CriteriaCustomerRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.PortfolioCustomerDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.res.UpdateLapsedQuoteRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class GridServiceImpl implements GridService {

	@Value(value = "${motor.productId}")
	private String motorProductId;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;
	
	@Value(value = "${building.productId}")
	private String buildingProductId;
	
	
	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository commonRepo ;
	
	@Autowired
	private LoginBranchMasterRepository loginBranchRepo ;
	
	@Autowired
	private MotorGridService motService ;
	
	@Autowired
	private TravelGridService traService ;
	
	@Autowired
	private BuildingGridService buiService ;
	
	@Autowired
	private CommonGridService commonService ;
	
	@Autowired
	private EserviceTravelDetailsRepository travelRepo;
	
	
	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;
	
	
	
	@PersistenceContext
	private EntityManager em;
	
	private Logger log = LogManager.getLogger(GridServiceImpl.class);

	
	@Override
	public List<EserviceCustomerDetailsRes> getallExistingQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			Date today = new Date() ;
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1); cal.set(Calendar.MINUTE, 1); cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();
			
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<QuoteCriteriaRes> extingQuoteList = new ArrayList<QuoteCriteriaRes>();
			
			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			
			
			if (req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add( req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			// Product Wise Get			
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				extingQuoteList = motService.getMotorExistingQuoteDetails(req  , branches , before30 , today , limit , offset );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				extingQuoteList = traService.getTravelExistingQuoteDetails(req  , branches , before30 , today , limit , offset );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				extingQuoteList = buiService.getBuildingExistingQuoteDetails(req  , branches , before30 , today , limit , offset );
				// Common
			}else { // (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				extingQuoteList = commonService.getCommonExistingQuoteDetails(req  , branches , before30 , today , limit , offset );
			}
			 
			for(QuoteCriteriaRes data : extingQuoteList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallLapsedQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			Date today = new Date() ;
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today); cal.set(Calendar.HOUR_OF_DAY, 1); cal.set(Calendar.MINUTE, 1); cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();
			
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
	
			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			// Product Wise Get	
			List<QuoteCriteriaRes> lapsedQuoteList = new ArrayList<QuoteCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				lapsedQuoteList = motService.getMotorLapsedQuoteDetails(req  , branches, before30 , limit , offset );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				lapsedQuoteList = traService.getTravelLapsedQuoteDetails(req  , branches, before30 , limit , offset );
			}
			else if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				lapsedQuoteList = buiService.getBuildingLapsedQuoteDetails(req  , branches, before30 , limit , offset );
			}else {
				lapsedQuoteList = commonService.getCommonLapsedQuoteDetails(req  , branches, before30 , limit , offset );
			}
			
			for(QuoteCriteriaRes data : lapsedQuoteList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallRejectedQuoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			List<RejectCriteriaRes> rejectedQuoteList = new ArrayList<RejectCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				rejectedQuoteList = motService.getMotorRejectedQuoteDetails(req  , branches, limit , offset );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				rejectedQuoteList = traService.getTravelRejectedQuoteDetails(req  , branches, limit , offset );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				rejectedQuoteList = buiService.getBuildingRejectedQuoteDetails(req  , branches, limit , offset );
			}else  {
				rejectedQuoteList = commonService.getCommonRejectedQuoteDetails(req  , branches, limit , offset );
			}
			for(RejectCriteriaRes data : rejectedQuoteList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}
	
	// Referral Grids

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralPendingDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode())  && req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			List<ReferalGridCriteriaRes> referralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				referralPendingList = motService.getMotorReferalDetails(req  , branches, limit , offset , "RP" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				referralPendingList = traService.getTravelReferalDetails(req  , branches, limit , offset, "RP" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				referralPendingList = buiService.getBuildingReferalDetails(req  , branches, limit , offset, "RP" );
			}else  {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req  , branches, limit , offset, "RP" );
				for(ReferalCommonCriteriaRes data : referralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			
			for(ReferalGridCriteriaRes data : referralPendingList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralApprovedDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode())  && req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			List<ReferalGridCriteriaRes> referralApprovedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				referralApprovedList = motService.getMotorReferalDetails(req  , branches, limit , offset, "RA" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				referralApprovedList = traService.getTravelReferalDetails(req  , branches, limit , offset, "RA" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				referralApprovedList = buiService.getBuildingReferalDetails(req  , branches, limit , offset, "RA" );
			} else  {
				List<ReferalCommonCriteriaRes> referralPendingList2 = commonService.getCommonReferalDetails(req  , branches, limit , offset, "RA" );
				for(ReferalCommonCriteriaRes data : referralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : referralApprovedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallReferralRejectedDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode())  && req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				referralRejectedList = motService.getMotorReferalDetails(req  , branches, limit , offset, "RR" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				referralRejectedList = traService.getTravelReferalDetails(req  , branches, limit , offset, "RR" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				referralRejectedList = buiService.getBuildingReferalDetails(req  , branches, limit , offset, "RR" );
			} else {
				List<ReferalCommonCriteriaRes> referralPendingList2  = commonService.getCommonReferalDetails(req  , branches, limit , offset, "RR" );
				for(ReferalCommonCriteriaRes data : referralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : referralRejectedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralPendings(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(req.getApplicationId());
			branches =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			
			List<ReferalGridCriteriaRes> adminReferralPendingList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				adminReferralPendingList = motService.getMotorAdminReferalDetails(req  , branches, limit , offset ,"RP" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				adminReferralPendingList = traService.getTravelAdminReferalDetails(req  , branches, limit , offset,"RP" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				adminReferralPendingList = buiService.getBuildingAdminReferalDetails(req  , branches, limit , offset,"RP" );
			} else  {
				List<ReferalCommonCriteriaRes> adminReferralPendingList2 = commonService.getCommonAdminReferalDetails(req  , branches, limit , offset,"RP" );
				for(ReferalCommonCriteriaRes data : adminReferralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : adminReferralPendingList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralApproved(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(req.getApplicationId());
			branches =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			
			List<ReferalGridCriteriaRes> adminReferralApprovedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				adminReferralApprovedList = motService.getMotorAdminReferalDetails(req  , branches, limit , offset,"RA" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				adminReferralApprovedList = traService.getTravelAdminReferalDetails(req  , branches, limit , offset,"RA" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				adminReferralApprovedList = buiService.getBuildingAdminReferalDetails(req  , branches, limit , offset,"RA" );
			}else  {
				List<ReferalCommonCriteriaRes> adminReferralApprovedList2 = commonService.getCommonAdminReferalDetails(req  , branches, limit , offset,"RA" );
				for(ReferalCommonCriteriaRes data : adminReferralApprovedList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : adminReferralApprovedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}

	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralRejected(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(req.getApplicationId());
			branches =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			
			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				adminReferralRejectedList = motService.getMotorAdminReferalDetails(req  , branches, limit , offset ,"RR" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				adminReferralRejectedList = traService.getTravelAdminReferalDetails(req  , branches, limit , offset,"RR" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				adminReferralRejectedList = buiService.getBuildingAdminReferalDetails(req  , branches, limit , offset,"RR" );
			}else {
				List<ReferalCommonCriteriaRes>	adminReferralRejectedList2 = commonService.getCommonAdminReferalDetails(req  , branches, limit , offset,"RR" );
				for(ReferalCommonCriteriaRes data : adminReferralRejectedList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : adminReferralRejectedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}



	@Override
	public CopyQuoteSuccessRes copyQuote(CopyQuoteReq req) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		try {
			// Branch Res
			

		/*	if (req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}*/
			String loginId = "" ;
			List<String> branches = new ArrayList<String>();
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}

			branches.add(req.getBranchCode());

			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				// Product Wise Get
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					res = motService.motorEndt(req, branches,loginId);
				}
//					else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
//					res = traService.travelCopyQuote(req, branches);
//				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
//					res = buiService.buildingCopyQuote(req, branches);
//
//				}
			}else if (req.getTypeId().equalsIgnoreCase("Normal") || StringUtils.isBlank(req.getTypeId())) {
				// Product Wise Get
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					res = motService.motorCopyQuote(req, branches,loginId);

				} else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
					res = traService.travelCopyQuote(req, branches,loginId);
				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
					res = buiService.buildingCopyQuote(req, branches,loginId);

				} else {
					res = commonService.commonCopyQuote(req, branches);

				}
			}

		}catch(

	Exception e)
	{
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}return res;
	}
	//Validation
	@Override
	public List<Error> validateQuotoNo(CopyQuoteReq req) {
		List<Error> error = new ArrayList<Error>();

		try {

			List<Tuple> quoteExist=null;
			if (req.getTypeId().equalsIgnoreCase("Endt")) {
				if (StringUtils.isBlank(req.getQuoteNo())) {
					error.add(new Error("01", "QuoteNo", "Please Enter QuoteNo "));
				}else if (StringUtils.isBlank(req.getEndtTypeId())) {
					error.add(new Error("01", "EndtTypeId", "Please Select EndtTypeId "));
				}
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					quoteExist = motService.validateMotorEndt(req.getQuoteNo());
				} else {
					quoteExist = commonService.validateCommonEndt(req.getQuoteNo());
				}
				
				if (quoteExist.size() > 0) {
					for (Tuple data : quoteExist) {
						if (Integer.valueOf(data.get("homeCount").toString()) <0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}else if ( data.get("policyNo")==" "||data.get("policyNo")==null) {
							error.add(new Error("02", "PolicyNo", "Policy No is Null "));
						}else if (!data.get("status").equals("P")) {
							error.add(new Error("02", "Status", "Status is Null "));
						}else if (Integer.valueOf(data.get("perCount").toString()) <0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}else if (Integer.valueOf(data.get("policyCount").toString()) <0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}else if (Integer.valueOf(data.get("motorCount").toString()) <0) {
							error.add(new Error("02", "QuoteNo", "No Data Found  "));
						}
					}
					
				}else {
					error.add(new Error("02", "List", "No Data Found "));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return error;
	}

	@Override
	public List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req) {

		List<GetAllMotorDetailsRes> reslist = new ArrayList<GetAllMotorDetailsRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
		/*	String loginId = req.getLoginId();

			// Branch Res
			List<String> branches = new ArrayList<String>();

			if (req.getBranchCode().equalsIgnoreCase("99999")) {

				List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

				branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
						.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
				if (branches.size() <= 0) {
					branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

				}

			} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				branches.add(req.getBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}*/
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
				list = motService.searchMotorQuote(req, branches);

			}
			else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				list = traService.searchTravelQuote(req, branches);
		}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
				list = buiService.searchBuildingQuote(req, branches);

			} else {
				list = commonService.searchCommonQuote(req, branches);
			}

			for (Tuple data : list) {
				GetAllMotorDetailsRes res = new GetAllMotorDetailsRes();
				dozermapper.map(data.get(0), res);
				res.setClientName((data.get("clientName").toString()));
				 res.setIdsCount(data.get("idsCount")==null?"":data.get("idsCount").toString() );
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
	public List<DropDownRes> copyQuoteByDropdown(CopyQuoteDropDownReq req) {

		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			
			List<ListItemValue> getList = new ArrayList<ListItemValue>();
			String itemType ="";
			
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				 itemType = "COPY_QUOTE_BY_MOTOR";
				 getList = motService.geMotorCoptyQuotetListItem(req, itemType);
			}
			else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				itemType = "COPY_QUOTE_BY_TRAVEL";
				getList = traService.getTravelCoptyQuotetListItem( req,itemType);
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
				 itemType = "COPY_QUOTE_BY_BUILDING";
				 getList = buiService.geBuildingCoptyQuotetListItem(req, itemType);
				 
			} else  {
				 itemType = "COPY_QUOTE_BY_COMMON";
				 getList = buiService.geBuildingCoptyQuotetListItem(req, itemType);
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
	public List<EserviceCustomerDetailsRes> getallReferralRequoteDetails(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			String loginId = "" ;
			if (req.getApplicationId().equalsIgnoreCase("1") ) {
				loginId = req.getLoginId();
			} else {
				loginId = req.getApplicationId();
			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			if (StringUtils.isNotBlank(req.getBranchCode())  && req.getBranchCode().equalsIgnoreCase("99999") ) {
				
				List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(loginId);
				
				 branches =loginBranch.stream().filter( o -> ! o.getBrokerBranchCode().equalsIgnoreCase("None") ) .map(LoginBranchMaster ::getBrokerBranchCode ).collect(Collectors.toList()) ;
				if(branches.size()<=0 ) {
					 branches =loginBranch.stream() .map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
							
				}
				 
			} else if( req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User") ) {
				branches.add(req.getBrokerBranchCode()) ;
			} else {
				branches.add(req.getBranchCode()) ;
			}
			
			List<ReferalGridCriteriaRes> referralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				referralRejectedList = motService.getMotorReferalDetails(req  , branches, limit , offset, "RE" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				referralRejectedList = traService.getTravelReferalDetails(req  , branches, limit , offset, "RE" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				referralRejectedList = buiService.getBuildingReferalDetails(req  , branches, limit , offset, "RE" );
			}else {
				List<ReferalCommonCriteriaRes> referralPendingList2  = commonService.getCommonReferalDetails(req  , branches, limit , offset, "RE" );
				for(ReferalCommonCriteriaRes data : referralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : referralRejectedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}



	@Override
	public List<EserviceCustomerDetailsRes> getallAdminReferralRequote(ExistingQuoteReq req) {
		List<EserviceCustomerDetailsRes> custRes = new ArrayList<EserviceCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			List<String> branches = new ArrayList<String>();
			List<LoginBranchMaster> loginBranch=loginBranchRepo.findByLoginId(req.getApplicationId());
			branches =loginBranch.stream().map(LoginBranchMaster ::getBranchCode ).collect(Collectors.toList()) ;
			
			List<ReferalGridCriteriaRes> adminReferralRejectedList = new ArrayList<ReferalGridCriteriaRes>();
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {
				adminReferralRejectedList = motService.getMotorAdminReferalDetails(req  , branches, limit , offset ,"RE" );
			} else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				adminReferralRejectedList = traService.getTravelAdminReferalDetails(req  , branches, limit , offset,"RE" );
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
				adminReferralRejectedList = buiService.getBuildingAdminReferalDetails(req  , branches, limit , offset,"RE" );
			}else  {
				List<ReferalCommonCriteriaRes> referralPendingList2  = commonService.getCommonAdminReferalDetails(req  , branches, limit , offset,"RE" );
				for(ReferalCommonCriteriaRes data : referralPendingList2  ) {
					 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
					 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
					 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					 custRes.add(res);	
				}
				return custRes;
			}
			for(ReferalGridCriteriaRes data : adminReferralRejectedList  ) {
				 EserviceCustomerDetailsRes res = new EserviceCustomerDetailsRes();
				 res = dozerMapper.map(data , EserviceCustomerDetailsRes.class);	
				 res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
				 custRes.add(res);	
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return custRes;
	}



	@Override
	public UpdateLapsedQuoteRes updateLapsedQuoteDetails(UpdateLapsedQuoteReq req) {
		UpdateLapsedQuoteRes res = new UpdateLapsedQuoteRes();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 

		try {
			EserviceMotorDetails motordata = new EserviceMotorDetails();
			EserviceTravelDetails traveldata = new EserviceTravelDetails();
			EserviceBuildingDetails buildingdata = new EserviceBuildingDetails();
			EserviceCommonDetails commondata = new EserviceCommonDetails();
			
			if (req.getProductId().equalsIgnoreCase(motorProductId) ) {			
				 motordata = repo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),req.getQuoteNo(),req.getProductId(),req.getCompanyId());			
					dozerMapper.map(motordata, EserviceMotorDetails.class);
					motordata.setUpdatedDate(new Date());
					res.setRequestReferenceNo(motordata.getRequestReferenceNo());
					res.setQuoteNo(motordata.getQuoteNo());
					res.setMessage("Lapsed Quote Updated Successful");

			}
			
			else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {			
				traveldata = travelRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),req.getQuoteNo(),req.getProductId(),req.getCompanyId());			
					dozerMapper.map(traveldata, EserviceTravelDetails.class);
					traveldata.setUpdatedDate(new Date());
					res.setRequestReferenceNo(traveldata.getRequestReferenceNo());
					res.setQuoteNo(traveldata.getQuoteNo());
					res.setMessage("Lapsed Quote Updated Successful");

			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {			
				buildingdata = buildingRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),req.getQuoteNo(),req.getProductId(),req.getCompanyId());			
					dozerMapper.map(buildingdata, EserviceBuildingDetails.class);
					buildingdata.setUpdatedDate(new Date());
					res.setRequestReferenceNo(buildingdata.getRequestReferenceNo());
					res.setQuoteNo(buildingdata.getQuoteNo());
					res.setMessage("Lapsed Quote Updated Successful");

			}	else  {			
				commondata = commonRepo.findByRequestReferenceNoAndQuoteNoAndProductIdAndCompanyId(req.getRequestReferenceNo(),req.getQuoteNo(),req.getProductId(),req.getCompanyId());			
				dozerMapper.map(commondata, EserviceCommonDetails.class);
				commondata.setUpdatedDate(new Date());
				res.setRequestReferenceNo(commondata.getRequestReferenceNo());
				res.setQuoteNo(buildingdata.getQuoteNo());
				res.setMessage("Lapsed Quote Updated Successful");

		}			
			
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	//Portfolio

		@Override
		public List<PortfolioCustomerDetailsRes> getallPortfolioActive(ExistingQuoteReq req) {
			List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();

				int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
				int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

				String loginId = "";
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					loginId = req.getLoginId();
				} else {
					loginId = req.getApplicationId();
				}
				// Branch Res
				List<String> branches = new ArrayList<String>();
				if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

					List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

					branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
							.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
					if (branches.size() <= 0) {
						branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

					}

				} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					branches.add(req.getBrokerBranchCode());
				} else {
					branches.add(req.getBranchCode());
				}

				List<PortfolioGridCriteriaRes> portfolioActiveList = new ArrayList<PortfolioGridCriteriaRes>();
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					portfolioActiveList = motService.getMotorProtfolioActive(req, branches, today, limit, offset, "P");
				} else {
					portfolioActiveList = commonService.getCommonProtfolioActive(req, branches, today, limit, offset, "P");
				}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelProtfolioActive(req  , branches, limit , offset, "P" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingProtfolioActive(req  , branches, limit , offset, "P" );
//				}
				for (PortfolioGridCriteriaRes data : portfolioActiveList) {
					PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
					res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
					res.setCount(data.getIdsCount() == null ? "" : data.getIdsCount().toString());
					res.setClientName(data.getClientName());
					custRes.add(res);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return custRes;
		}

		@Override
		public List<PortfolioCustomerDetailsRes> getallPortfolioPending(ExistingQuoteReq req) {
			List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 1);
				cal.add(Calendar.DAY_OF_MONTH, +365);
				Date before365 = cal.getTime();
				int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
				int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

				String loginId = "";
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					loginId = req.getLoginId();
				} else {
					loginId = req.getApplicationId();
				}
				// Branch Res
				List<String> branches = new ArrayList<String>();
				if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

					List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

					branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
							.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
					if (branches.size() <= 0) {
						branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

					}

				} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					branches.add(req.getBrokerBranchCode());
				} else {
					branches.add(req.getBranchCode());
				}

				List<PortfolioGridCriteriaRes> list = new ArrayList<PortfolioGridCriteriaRes>();
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					list = motService.getMotorProtfolioPending(req, branches, today, limit, offset, "P");
				} else {
					list = commonService.getCommonProtfolioPending(req, branches, today, limit, offset, "P");
					
				}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelProtfolioPending(req  , branches, limit , offset, "P" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingProtfolioPending(req  , branches, limit , offset, "P" );
//				}
				for (PortfolioGridCriteriaRes data : list) {
					PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
					res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					custRes.add(res);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return custRes;
		}

		@Override
		public List<PortfolioCustomerDetailsRes> getallPortfolioCancelled(ExistingQuoteReq req) {
			List<PortfolioCustomerDetailsRes> custRes = new ArrayList<PortfolioCustomerDetailsRes>();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				Date today = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 1);
				today = cal.getTime();
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 1);
				cal.add(Calendar.DAY_OF_MONTH, -30);
				Date before365 = cal.getTime();
				int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
				int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

				String loginId = "";
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					loginId = req.getLoginId();
				} else {
					loginId = req.getApplicationId();
				}
				// Branch Res
				List<String> branches = new ArrayList<String>();
				if (StringUtils.isNotBlank(req.getBranchCode()) && req.getBranchCode().equalsIgnoreCase("99999")) {

					List<LoginBranchMaster> loginBranch = loginBranchRepo.findByLoginId(loginId);

					branches = loginBranch.stream().filter(o -> !o.getBrokerBranchCode().equalsIgnoreCase("None"))
							.map(LoginBranchMaster::getBrokerBranchCode).collect(Collectors.toList());
					if (branches.size() <= 0) {
						branches = loginBranch.stream().map(LoginBranchMaster::getBranchCode).collect(Collectors.toList());

					}

				} else if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					branches.add(req.getBrokerBranchCode());
				} else {
					branches.add(req.getBranchCode());
				}

				List<PortfolioGridCriteriaRes> list = new ArrayList<PortfolioGridCriteriaRes>();
				if (req.getProductId().equalsIgnoreCase(motorProductId)) {
					list = motService.getMotorPortfolioCancelled(req, branches, today, limit, offset, "D");
				} else {
					list = commonService.getCommonPortfolioCancelled(req, branches, today, limit, offset, "D");
				}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
//					referralApprovedList = traService.getTravelPortfolioCancelled(req  , branches, limit , offset, "D" );
//				}
//				else if (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					referralApprovedList = buiService.getBuildingPortfolioCancelled(req  , branches, limit , offset, "D" );
//				}
				for (PortfolioGridCriteriaRes data : list) {
					PortfolioCustomerDetailsRes res = new PortfolioCustomerDetailsRes();
					res = dozerMapper.map(data, PortfolioCustomerDetailsRes.class);
					// res.setCount(data.getIdsCount()==null?"":data.getIdsCount().toString() );
					custRes.add(res);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return custRes;
		}



	@Override
	public List<DropDownRes> getallIssuerQuoteDetails(IssuerQuoteReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			cal.add(Calendar.DAY_OF_MONTH, -30);
			Date before30 = cal.getTime();

			List<Tuple> List = new ArrayList<Tuple>();

			// Product Wise Get
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				List = motService.getMotorIssuerQuoteDetails(req, before30, today);
			}
//				else if (req.getProductId().equalsIgnoreCase(travelProductId)) {
//					extingQuoteList = traService.getTravelExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				} else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
//					extingQuoteList = buiService.getBuildingExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//					// Common
//				} else { // (req.getProductId().equalsIgnoreCase(buildingProductId) ) {
//					extingQuoteList = commonService.getCommonExistingQuoteDetails(req, branches, before30, today, limit,
//							offset);
//				}

			for (Tuple data : List) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.get("loginId").toString());
				res.setCodeDesc(data.get("agencyCode").toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

}

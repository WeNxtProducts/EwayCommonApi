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

import com.maan.eway.admin.res.ReferalCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginBranchMaster;
import com.maan.eway.bean.LoginMaster;

import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.EserviceCustomerSearchVrtinReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.CriteriaCustomerRes;
import com.maan.eway.common.res.CustomerDetailsGetRes;
import com.maan.eway.common.res.EserviceCustomerDetailsRes;
import com.maan.eway.common.res.GetAllMotorDetailsRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.GridService;
import com.maan.eway.common.service.MotorGridService;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.master.req.LovDropDownReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
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
	private LoginBranchMasterRepository loginBranchRepo ;
	
	@Autowired
	private MotorGridService motService ;
	
	@Autowired
	private TravelGridService traService ;
	
	@Autowired
	private BuildingGridService buiService ;
	
	
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
	public SuccessRes copyQuote(CopyQuoteReq req) {
		SuccessRes res = new SuccessRes();
		try {

			String loginId = req.getLoginId();

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
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}

			// Product Wise Get
			if (req.getProductId().equalsIgnoreCase(motorProductId)) {
				res = motService.motorCopyQuote(req, branches);

			}else if (req.getProductId().equalsIgnoreCase(travelProductId) ) {
				res = traService.travelCopyQuote(req, branches);
			}
			else if (req.getProductId().equalsIgnoreCase(buildingProductId)) {
				res = buiService.buildingCopyQuote(req, branches);

			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}
	
	@Override
	public List<GetAllMotorDetailsRes> getbyReqRefNo(CopyQuoteReq req) {

		List<GetAllMotorDetailsRes> reslist = new ArrayList<GetAllMotorDetailsRes>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			String loginId = req.getLoginId();

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
				branches.add(req.getBrokerBranchCode());
			} else {
				branches.add(req.getBranchCode());
			}
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

	
}

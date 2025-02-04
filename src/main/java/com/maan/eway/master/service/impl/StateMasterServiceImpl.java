/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:26 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.service.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.StateMaster;
import com.maan.eway.common.res.CityDropdown;
import com.maan.eway.common.res.StateDropdown;
import com.maan.eway.common.res.SubUrbDropDown;
import com.maan.eway.master.req.StateMasterChangeStatusReq;
import com.maan.eway.master.req.StateMasterDropDownReq;
import com.maan.eway.master.req.StateMasterGetAllReq;
import com.maan.eway.master.req.StateMasterGetReq;
import com.maan.eway.master.req.StateMasterSaveReq;
import com.maan.eway.master.res.StateMasterRes;
import com.maan.eway.master.service.StateMasterService;
import com.maan.eway.repository.StateMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.impl.BasicValidationService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

/**
 * <h2>StateMasterServiceimpl</h2>
 */
@Service
@Transactional
public class StateMasterServiceImpl implements StateMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private StateMasterRepository repo;

	@Autowired
	private BasicValidationService basicvalidateService;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(StateMasterServiceImpl.class);

//************************************************INSERT/UPDATE STATE DETAILS******************************************************\\
	@Transactional
	@Override
	public SuccessRes insertState(StateMasterSaveReq req) {
		SuccessRes res = new SuccessRes();
		StateMaster saveData = new StateMaster();
		List<StateMaster> list = new ArrayList<StateMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");

		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;

			Integer stateId = 0;
			

			// Update
			stateId = Integer.valueOf(req.getStateShortCode());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			//Find all
			Root<StateMaster> b = query.from(StateMaster.class);
			//Select 
			query.select(b);
//						
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			
			// Where
			Predicate n1 = cb.equal(b.get("stateShortCode"), req.getStateShortCode());
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			
			
			query.where(n1,n2).orderBy(orderList);
			
			// Get Result 
			TypedQuery<StateMaster> result = em.createQuery(query);
			int limit = 0 , offset = 2 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
			entryDate = new Date() ;
			createdBy = req.getCreatedBy();
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
			
				if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
					amendId = list.get(0).getAmendId() + 1 ;
					entryDate = new Date() ;
					createdBy = req.getCreatedBy();
						StateMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					
				} else {
					amendId = list.get(0).getAmendId() ;
					entryDate = list.get(0).getEntryDate() ;
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0) ;
					if (list.size()>1 ) {
						StateMaster lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				
			    }
			}
			if (StringUtils.isBlank(req.getStateId())) {
				// Save
				res.setResponse("Saved Successfully ");
				res.setSuccessId(stateId.toString());

			} else {
				res.setResponse("Updated Successfully ");
				res.setSuccessId(stateId.toString());
			}

			dozerMapper.map(req, saveData);
			saveData.setStateId(stateId);
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setTiraCode(req.getRegulatoryCode());
			saveData.setAmendId(amendId);
			saveData.setCoreAppCode(req.getCoreAppCode());
			
			repo.saveAndFlush(saveData);
			log.info("Saved Details is ---> " + json.toJson(saveData));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<String> validateStateDetails(StateMasterSaveReq req) {

		List<String> errorList = new ArrayList<String>();

		try {

			if (StringUtils.isBlank(req.getStateName())) {
//				errorList.add(new Error("01", "StateName", "Please Select State  Name "));
				errorList.add("2126");
			}else if (req.getStateName().length() > 100){
//				errorList.add(new Error("01","StateName", "Please Enter State Name within 100 Characters")); 
				errorList.add("2127");
			}else if (StringUtils.isBlank(req.getStateId()) && StringUtils.isNotBlank(req.getCountryId())) {
				List<StateMaster> stateList = getStateNameExistDetails(req.getStateName() , req.getCountryId());
				if (stateList.size()>0 ) {
//					errorList.add(new Error("01", "State", "This State Name Already Exist "));
					errorList.add("2128");
				}
			}else if(  StringUtils.isNotBlank(req.getCountryId())) {
				List<StateMaster> stateList =  getStateNameExistDetails(req.getStateName() ,req.getCountryId() );
				if (stateList.size()>0 &&  (! req.getStateId().equalsIgnoreCase(stateList.get(0).getStateId().toString())) ) {
//					errorList.add(new Error("01", "State", "This State Name Already Exist "));
					errorList.add("2128");
				}
				
			}

			if (StringUtils.isBlank(req.getCountryId()) ) {
//				errorList.add(new Error("03", "CountryId", "Please Select Country Id "));
				errorList.add("2129");
			} 
			
			if (StringUtils.isBlank(req.getCoreAppCode()) ) {
//				errorList.add(new Error("03", "CoreAppCode", "Please Enter CoreAppCode"));
				errorList.add("2130");
			}

			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
				errorList.add("2034");
	
			} else if (req.getEffectiveDateStart().before(today)) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("2035");
			}
//			else if (req.getEffectiveDateEnd() == null ) {
//				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End "));
//	
//			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart()) || req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
//				errorList.add(new Error("04", "EffectiveDateEnd", "Please Enter Effective Date End  is After Effective Date End"));
//			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
//				errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("2036");
			} else if (req.getStatus().length() > 1) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("2037");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("2038");
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
//				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("2039");
			}else if (req.getCreatedBy().length() > 50) {
//				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
				errorList.add("2040");
			}
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				errorList.add(new Error("07", "RegulatoryCode", "Please Enter RegulatoryCode"));
				errorList.add("2041");
			}else if (req.getRegulatoryCode().length() > 20) {
//				errorList.add(new Error("07", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
				errorList.add("2042");
			}
			
			if (StringUtils.isBlank(req.getRemarks())) {
//				errorList.add(new Error("09", "Remarks", "Please Enter Remarks"));
				errorList.add("2032");
			}else if (req.getRemarks().length() > 100) {
//				errorList.add(new Error("09", "Remarks", "Please Enter Remarks within 100 Characters"));
				errorList.add("2033");
			}
			if (StringUtils.isBlank(req.getStateShortCode())) {
//				errorList.add(new Error("10", "StateShortCode", "Please Enter StateShortCode"));
				errorList.add("2131");
			}else if (req.getStateShortCode().length() > 20) {
//				errorList.add(new Error("10", "StateShortCode", "Please Enter StateShortCode within 20 Characters"));
				errorList.add("2132");
			}else if (! req.getStateShortCode().matches("[0-9]+") ) {
//				errorList.add(new Error("10", "StateShortCode", "Please Enter Valid number in StateShortCode"));
				errorList.add("2133");
			}

			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
//			errorList.add(new Error("10", "Common Error",e.getMessage()));
			errorList.add("2114");
		}
		return errorList;
	}

	// State Name Exist Details validation
	public List<StateMaster> getStateNameExistDetails(String sectionName , String countryId) {
		List<StateMaster> list = new ArrayList<StateMaster>();
		try {
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// Find All
			Root<StateMaster> b = query.from(StateMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = amendId.from(StateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			amendId.where(a1);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("stateName"), sectionName);
			Predicate n3 = cb.equal(b.get("countryId"), countryId);
			query.where(n1, n2,n3);
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}

	
///*********************************************************************GET ALL******************************************************\\
	@Override
	public List<StateMasterRes> getallStateDetails(StateMasterGetAllReq req) {
		List<StateMasterRes> resList = new ArrayList<StateMasterRes>();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			List<StateMaster> list = new ArrayList<StateMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// Find All
			Root<StateMaster> b = query.from(StateMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = amendId.from(StateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			
			amendId.where(a1 );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("stateName")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			query.where(n1,n2).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			// Map
			for (StateMaster data : list) {
				StateMasterRes res = new StateMasterRes();

				res = dozerMapper.map(data, StateMasterRes.class);
				res.setStateId(data.getStateId().toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

///*********************************************************************GET BY ID******************************************************\\
	@Override
	public StateMasterRes getByStateId(StateMasterGetReq req) {
		StateMasterRes res = new StateMasterRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();

		try {
			List<StateMaster> list = new ArrayList<StateMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// Find All
			Root<StateMaster> b = query.from(StateMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = amendId.from(StateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			
			amendId.where(a1 );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("stateName")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			Predicate n3 = cb.equal(b.get("stateId"), req.getStateId());
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			res = dozerMapper.map(list.get(0), StateMasterRes.class);
			res.setStateId(list.get(0).getStateId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

//**********************************************************DROPDOWN********************************************************************\\
	@Override
	public List<DropDownRes> getStateMasterDropdown(StateMasterDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			String countryId=null;
		
			if (StringUtils.isBlank(req.getCountryId())) {
				countryId="TZA";
			}else {
				countryId=req.getCountryId();
			}
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			List<StateMaster> list = new ArrayList<StateMaster>();

			// Find All
			Root<StateMaster> c = query.from(StateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("stateName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
			effectiveDate.where(a1, a2,a3);

			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"), ocpm2.get("stateId"));
			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a4 = cb.equal(c.get("countryId"), ocpm2.get("countryId"));
			effectiveDate2.where(a5,a6, a4);
			
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("countryId"), countryId);
	
			query.where(n12, n2,n3,n4).orderBy(orderList);

			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (StateMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getStateId().toString());
				res.setCodeDesc(data.getStateName());
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

//************************************************GET ACTIVE STATE******************************************\\
	@Override
	public List<StateMasterRes> getActiveStateDetails(StateMasterGetAllReq req) {
		List<StateMasterRes> resList = new ArrayList<StateMasterRes>();
		 DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		try {
			List<StateMaster> list = new ArrayList<StateMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);

			// Find All
			Root<StateMaster> b = query.from(StateMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = amendId.from(StateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			
			amendId.where(a1 );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("stateName")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			Predicate n3 = cb.equal(b.get("status"), "Y");
			query.where(n1,n2,n3).orderBy(orderList);
			
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			// Map
			for (StateMaster data : list) {
				StateMasterRes res = new StateMasterRes();

				res = dozerMapper.map(data, StateMasterRes.class);
				res.setStateId(data.getStateId().toString());
				resList.add(res);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	@Override
	public SuccessRes changeStatusOfStateMaster(StateMasterChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		try {
			List<StateMaster> list = new ArrayList<StateMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			// Find all
			Root<StateMaster> b = query.from(StateMaster.class);
			//Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<StateMaster> ocpm1 = amendId.from(StateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			
			amendId.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("stateId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			Predicate n3 = cb.equal(b.get("stateId"), req.getStateId());
			
			query.where(n1,n2,n3).orderBy(orderList);
			
			// Get Result 
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			StateMaster updateRecord = list.get(0);
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
			
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getStateId());
		} catch(Exception e ) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<DropDownRes> getRegionStateMasterDropdown(StateMasterDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			String countryId=null;
		
			if (StringUtils.isBlank(req.getCountryId())) {
				countryId="TZA";
			}else {
				countryId=req.getCountryId();
			}
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			List<StateMaster> list = new ArrayList<StateMaster>();

			// Find All
			Root<StateMaster> c = query.from(StateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("stateName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
			Predicate a7 = cb.equal(c.get("regionCode"), ocpm1.get("regionCode"));
			effectiveDate.where(a1, a2,a3,a7);

			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"), ocpm2.get("stateId"));
			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a4 = cb.equal(c.get("countryId"), ocpm2.get("countryId"));
			Predicate a8 = cb.equal(c.get("regionCode"), ocpm2.get("regionCode"));
			effectiveDate2.where(a5,a6, a4,a8);
			
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("countryId"), countryId);
			if(req.getRegionCode()!=null) {
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("regionCode"), req.getRegionCode());
	
			query.where(n12, n2,n3,n4,n5).orderBy(orderList);
			}
			else {
				query.where(n12, n2,n3,n4).orderBy(orderList);	
			}
			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (StateMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getStateId().toString());
				res.setCodeDesc(data.getStateName());
				res.setCodeDescLocal(data.getStateNameLocal());
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
	public List<StateDropdown> getStateGroupMasterDropdown(StateMasterDropDownReq req) {
		List<StateDropdown> resList = new ArrayList<StateDropdown>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			String countryId=null;
		
			if (StringUtils.isBlank(req.getCountryId())) {
				countryId="TZA";
			}else {
				countryId=req.getCountryId();
			}
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			List<StateMaster> list = new ArrayList<StateMaster>();

			// Find All
			Root<StateMaster> c = query.from(StateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("stateName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
			Predicate a7 = cb.equal(c.get("regionCode"), ocpm1.get("regionCode"));
			Predicate a9 = cb.equal(c.get("stateShortCode"), ocpm1.get("stateShortCode"));
			Predicate a11 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
			Predicate a13 = cb.equal(c.get("suburbId"), ocpm1.get("suburbId"));
			effectiveDate.where(a1, a2,a3,a7 ,a9,a11,a13);

			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"), ocpm2.get("stateId"));
			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a4 = cb.equal(c.get("countryId"), ocpm2.get("countryId"));
			Predicate a8 = cb.equal(c.get("regionCode"), ocpm2.get("regionCode"));
			Predicate a10 = cb.equal(c.get("stateShortCode"), ocpm2.get("stateShortCode"));
			Predicate a12 = cb.equal(c.get("cityId"), ocpm2.get("cityId"));
			Predicate a14 = cb.equal(c.get("suburbId"), ocpm2.get("suburbId"));
			effectiveDate2.where(a5,a6, a4,a8,a10,a12,a14);
			
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("countryId"), countryId);
			//jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("regionCode"), req.getRegionCode());
	
			query.where(n12, n2,n3,n4).orderBy(orderList);

			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();

			Map<Integer ,List<StateMaster>>  groupByStates = list.stream().collect(Collectors.groupingBy(StateMaster :: getStateId ));
					
			for (Integer state : groupByStates.keySet() ) {
				List<StateMaster> cityList = groupByStates.get(state);
				StateMaster  stateDetails = cityList.get(0); 
				
//				Map<Integer ,List<StateMaster>>  groupByCities = list.stream().collect(Collectors.groupingBy(StateMaster :: getCityId ));
//				
//				List<CityDropdown> cityDetailsList = new ArrayList<CityDropdown>();
//				
//				for (Integer city : groupByCities.keySet() ) {
//					List<StateMaster> subUrbList = groupByCities.get(city);
//					StateMaster  cityDetails = subUrbList.get(0); 
//					
//					List<SubUrbDropDown> subUrbDetails = new ArrayList<SubUrbDropDown>() ;
//					
//					for (StateMaster data : subUrbList) {
//						// Response
//						SubUrbDropDown res = new SubUrbDropDown();
//						res.setCode(data.getSuburbId().toString());
//						res.setCodeDesc(data.getSuburb());
//						res.setStatus(data.getStatus());
//						res.setAreaGroup(data.getAreaGroup()==null?"0":data.getAreaGroup().toString());
//						subUrbDetails.add(res);
//						
//						
//					}
//					subUrbDetails.sort(Comparator.comparing(SubUrbDropDown :: getCodeDesc) );
//					
//					CityDropdown cityRes = new CityDropdown();
//					cityRes.setCode(cityDetails.getCityId().toString());
//					cityRes.setCodeDesc(cityDetails.getCity());
//					cityRes.setStatus(cityDetails.getStatus());
//					cityRes.setSubUrbDetails(subUrbDetails);
//					cityDetailsList.add(cityRes);
//					
//				}
//				
//				cityDetailsList.sort(Comparator.comparing(CityDropdown :: getCodeDesc) );
				
				StateDropdown stateRes = new StateDropdown();
				stateRes.setCode(stateDetails.getStateId().toString());
				stateRes.setCodeDesc(stateDetails.getStateName());
				stateRes.setStatus(stateDetails.getStatus());
				//stateRes.setCityDetails(cityDetailsList);
				resList.add(stateRes);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<CityDropdown> getCityGroupMasterDropdown(StateMasterDropDownReq req) {
		List<CityDropdown> cityDetailsList = new ArrayList<CityDropdown>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			String countryId=null;
		
			if (StringUtils.isBlank(req.getCountryId())) {
				countryId="TZA";
			}else {
				countryId=req.getCountryId();
			}
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<StateMaster> query = cb.createQuery(StateMaster.class);
			List<StateMaster> list = new ArrayList<StateMaster>();

			// Find All
			Root<StateMaster> c = query.from(StateMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("stateName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm1 = effectiveDate.from(StateMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
			Predicate a7 = cb.equal(c.get("regionCode"), ocpm1.get("regionCode"));
			Predicate a9 = cb.equal(c.get("stateShortCode"), ocpm1.get("stateShortCode"));
			Predicate a11 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
			Predicate a13 = cb.equal(c.get("suburbId"), ocpm1.get("suburbId"));
			effectiveDate.where(a1, a2,a3,a7 ,a9,a11,a13);

			// Effective Date End
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<StateMaster> ocpm2 = effectiveDate2.from(StateMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"), ocpm2.get("stateId"));
			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a4 = cb.equal(c.get("countryId"), ocpm2.get("countryId"));
			Predicate a8 = cb.equal(c.get("regionCode"), ocpm2.get("regionCode"));
			Predicate a10 = cb.equal(c.get("stateShortCode"), ocpm2.get("stateShortCode"));
			Predicate a12 = cb.equal(c.get("cityId"), ocpm2.get("cityId"));
			Predicate a14 = cb.equal(c.get("suburbId"), ocpm2.get("suburbId"));
			effectiveDate2.where(a5,a6, a4,a8,a10,a12,a14);
			
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("countryId"), countryId);
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("stateId"), req.getStateId());
	
			query.where(n12, n2,n3,n4,n5).orderBy(orderList);

			// Get Result
			TypedQuery<StateMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			Map<Integer ,List<StateMaster>>  groupByCities = list.stream().collect(Collectors.groupingBy(StateMaster :: getCityId ));
			
			for (Integer city : groupByCities.keySet() ) {
				List<StateMaster> subUrbList = groupByCities.get(city);
				StateMaster  cityDetails = subUrbList.get(0); 
				
				List<SubUrbDropDown> subUrbDetails = new ArrayList<SubUrbDropDown>() ;
				
				for (StateMaster data : subUrbList) {
					// Response
					SubUrbDropDown res = new SubUrbDropDown();
					res.setCode(data.getSuburbId().toString());
					res.setCodeDesc(data.getSuburb());
					res.setStatus(data.getStatus());
					res.setAreaGroup(data.getAreaGroup()==null?"0":data.getAreaGroup().toString());
					subUrbDetails.add(res);
					
					
				}
				subUrbDetails.sort(Comparator.comparing(SubUrbDropDown :: getCodeDesc) );
				
				CityDropdown cityRes = new CityDropdown();
				cityRes.setCode(cityDetails.getCityId().toString());
				cityRes.setCodeDesc(cityDetails.getCity());
				cityRes.setStatus(cityDetails.getStatus());
				cityRes.setSubUrbDetails(subUrbDetails);
				cityDetailsList.add(cityRes);
				
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return cityDetailsList;
	}

}

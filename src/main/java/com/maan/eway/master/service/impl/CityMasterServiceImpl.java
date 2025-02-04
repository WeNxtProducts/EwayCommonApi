/*
*  Copyright (c) 2019. All right reserved
* Created on 2022-08-24 ( Date ISO 2022-08-24 - Time 12:58:26 )
* Generated by Telosys Tools Generator ( version 3.3.0 )
*/
package com.maan.eway.master.service.impl;

import java.sql.Timestamp;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.CityMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.CityChangeStatusReq;
import com.maan.eway.master.req.CityMasterDropDownReq;
import com.maan.eway.master.req.CityMasterGetAllReq;
import com.maan.eway.master.req.CityMasterGetReq;
import com.maan.eway.master.req.CityMasterSaveReq;
import com.maan.eway.master.res.CityMasterRes;
import com.maan.eway.master.service.CityMasterService;
import com.maan.eway.repository.CityMasterRepository;
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
 * <h2>CityMasterServiceimpl</h2>
 */
@Service
@Transactional
public class CityMasterServiceImpl implements CityMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private CityMasterRepository repo;

	@Autowired
	private BasicValidationService basicvalidateService;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(CityMasterServiceImpl.class);

//************************************************INSERT/UPDATE CITY DETAILS******************************************************\\
	@Transactional
	@Override
	public SuccessRes insertCity(CityMasterSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		CityMaster saveData = new CityMaster();
		List<CityMaster> list = new ArrayList<CityMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId = 0 ;
			String branchCode = "";
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			String cityId = "";

			if (StringUtils.isBlank(req.getCityId())) {
				// Save
				// Long totalCount = repo.count();
				Long totalCount = getMasterTableCount();
				cityId = Long.valueOf(totalCount + 1).toString();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(cityId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				cityId = req.getCityId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);

				// Find All
				Root<CityMaster> b = query.from(CityMaster.class);

				// Select
				query.select(b);

				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n3 = cb.equal(b.get("cityId"), req.getCityId());
				Predicate n4 = cb.equal(b.get("countryId"), req.getCountryId());
//				Predicate n5 = cb.equal(b.get("regionId"), req.getRegionId());
				Predicate n6 = cb.equal(b.get("stateId"), req.getStateId());

				query.where(n1, n3, n4,  n6);// .orderBy(orderList);

				// Get Result
				TypedQuery<CityMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();

				if (list.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						CityMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							CityMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully ");
				res.setSuccessId(cityId);

			}

			dozerMapper.map(req, saveData);
			saveData.setCityId(Integer.valueOf(cityId));
			saveData.setCityName(req.getCityName());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setTiraCode(req.getRegulatoryCode());
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
	public List<String> validateCityDetails(CityMasterSaveReq req) {

		List<String> errorList = new ArrayList<String>();

		try {
			
//			if(StringUtils.isBlank(req.getCityName())&&StringUtils.isBlank(req.getCountryId())
//				||req.getEffectiveDateStart() == null && StringUtils.isBlank(req.getStatus())
//				&& StringUtils.isBlank(req.getStateId())&&StringUtils.isBlank(req.getRegulatoryCode())
//				&& StringUtils.isBlank(req.getCreatedBy())&&StringUtils.isBlank(req.getCoreAppCode())
//				&&StringUtils.isBlank(req.getRegionId())&&StringUtils.isBlank(req.getRemarks()))
//			{
//				errorList.add(new Error("02", "", "every status is empty"));
//
//			}
//			if (StringUtils.isBlank(req.getCityName()) && req.getCityName()==null) {
//				errorList.add(new Error("02", "CityName", "Please Select City  Name "));
//			} else if (req.getCityName().length() > 100) {
//				errorList.add(new Error("02", "CityName", "Please Enter City  Name within 100 Characters"));
//			} else if (StringUtils.isBlank(req.getCityId().toString())) {
//				Long CityCount = repo.countByCityNameOrderByEntryDateDesc(req.getCityName());
//				if (CityCount > 0) {
//					errorList.add(new Error("01", "CityName", "This City Name Alrady Exist "));
//				}
//				else {
//					
//				}
//			}
			
			
			
			
//			else if (req.getCityName().length() > 100) {
//				errorList.add(new Error("02", "CityName", "Please Enter City  Name within 100 Characters"));
//			}
//
//				else if (StringUtils.isBlank(req.getCityId().toString())) {
//				Long CityCount = repo.countByCityNameOrderByEntryDateDesc(req.getCityName());
//					if (CityCount > 1) {
//					errorList.add(new Error("01", "CityName", "This City Name Alrady Exist "));
//				}
//				
//				
//				
			if (StringUtils.isBlank(req.getCountryId()) ) {
//				errorList.add(new Error("03", "CountryId", "Please Select Country Id "));
				errorList.add("2134");
			}
			else if(req.getCountryId().length()>20) {
//				errorList.add(new Error("03","CountryId","Pleaser enter the country between 20 characters"));
				errorList.add("2135");
				
			}
			
			if (StringUtils.isBlank(req.getStateId()) || req.getStateId() == null) {
//				errorList.add(new Error("06", "StateId", "Please Select State Id "));
				errorList.add("2136");
			}
			
			if(StringUtils.isBlank(req.getCoreAppCode())) {
//				errorList.add(new Error("11","CoreAppCode","Please Enter core app code"));
				errorList.add("2124");
			}
			else if(req.getCoreAppCode().length()>10) {
//				errorList.add(new Error("11","CoreAppCode","Please Enter CoreAppCode within 10 characters"));
				errorList.add("2125");
			}

			if (StringUtils.isBlank(req.getCreatedBy())) {
//				errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("2039");
			} else if (req.getCreatedBy().length() > 20) {
//				errorList.add(new Error("07", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
				errorList.add("2040");
			}


			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
				errorList.add("2034");

			} else if (req.getEffectiveDateStart().before(today)) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
						
				errorList.add("2035");
			}

			//Status Validation
//			if (StringUtils.isBlank(req.getStatus())) {
//				errorList.add(new Error("05", "Status", "Please Select Status  "));
//			} else if (req.getStatus().length() > 1) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
//			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
//			}
			
			
			if (StringUtils.isBlank(req.getStatus())) {
//					errorList.add(new Error("05", "Status", "Please Enter Status"));
					errorList.add("2036");
				} else if (req.getStatus().length() > 1) {
//					errorList.add(new Error("05", "Status", "Enter Status in One Character Only"));
					errorList.add("2037");
				} else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
//					errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
					errorList.add("2038");
				}

			
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode"));
				errorList.add("2041");
			} else if (req.getRegulatoryCode().length() > 20) {
//				errorList.add(new Error("09", "RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters"));
				errorList.add("2042");
			}
			
						
//			if(StringUtils.isBlank(req.getRegionId())) {
//				errorList.add(new Error("12","RegionId","Please select Region"));
//			}
//			else if(req.getRegionId().length()>9) {
//				errorList.add(new Error("12","RegionId","Please Enter RegionId within 10 characters"));
//			}
			
			if(StringUtils.isBlank(req.getRemarks())) {
//				errorList.add(new Error("13","Remarks","Please Enter the Remarks"));
				errorList.add("2032");
			}
			else if(req.getRemarks().length()>100) {
//				errorList.add(new Error("13","Remarks","Please Enter RegionId within 10 characters"));
				errorList.add("2033");
			}
			
			if (StringUtils.isBlank(req.getCityName())) {
//				errorList.add(new Error("01", "CityName", "Please City Name "));
				errorList.add("2137");
			}else if (req.getCityName().length() > 100){
//				errorList.add(new Error("01","CityName", "Please Enter City Name within 100 Characters")); 
				errorList.add("2138");
			}else if (StringUtils.isBlank(req.getCityId()) && StringUtils.isNotBlank(req.getCountryId())) {
				List<CityMaster> CityList = getCityNameExistDetails(req.getCityName() , req.getCountryId());
				if (CityList.size()>0 ) {
//					errorList.add(new Error("01", "CityName", "This City Name Already Exist "));
					errorList.add("2139");
				}
			}
			else if(  StringUtils.isNotBlank(req.getCountryId())) {
				List<CityMaster> CityList =  getCityNameExistDetails(req.getCityName() ,req.getCountryId() );
				if (CityList.size()>0 &&  (! req.getCityId().equalsIgnoreCase(CityList.get(0).getCityId().toString())) ) {
//					errorList.add(new Error("01", "CityName", "This City Name Already Exist "));
					errorList.add("2139");
				}

			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	private List<CityMaster> getCityNameExistDetails(String cityName, String countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Long getMasterTableCount() {

		Long data = 0L;
		try {

			List<CityMaster> list = new ArrayList<CityMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);

			// Find All
			Root<CityMaster> b = query.from(CityMaster.class);

			// Select
			query.select(b);
			
			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CityMaster> ocpm1 = effectiveDate.from(CityMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("cityId"), b.get("cityId"));
			effectiveDate.where(a1);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("cityId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			query.where(n1).orderBy(orderList);
			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? Long.valueOf(list.get(0).getCityId()) : 0 ;
			

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return data;
	}

///*********************************************************************GET ALL******************************************************\\
	@Override
	public List<CityMasterRes> getallCityDetails(CityMasterGetAllReq req) {
		List<CityMasterRes> resList = new ArrayList<CityMasterRes>();
		ModelMapper mapper = new ModelMapper();
		try {
			List<CityMaster> list = new ArrayList<CityMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);
			// Find All
			Root<CityMaster> b = query.from(CityMaster.class);
		
			// Select
			query.select(b);
		
			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CityMaster> ocpm1 = amendId.from(CityMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("cityId"), b.get("cityId"));
			Predicate a2 = cb.equal(ocpm1.get("countryId"), b.get("countryId"));
//			Predicate a3 = cb.equal(ocpm1.get("regionId"), b.get("regionId"));
			Predicate a4 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));

			amendId.where(a1, a2, a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("cityId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			Predicate n4 = cb.equal(b.get("stateId"), req.getStateId());

			query.where(n1, n2, n4).orderBy(orderList);

			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCityId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(CityMaster :: getCityName ));
			
			// Map
			for (CityMaster data : list) {
				CityMasterRes res = new CityMasterRes();

				res = mapper.map(data, CityMasterRes.class);
				res.setCityId(data.getCityId().toString());
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
	public CityMasterRes getByCityId(CityMasterGetReq req) {
		CityMasterRes res = new CityMasterRes();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);
			List<CityMaster> list = new ArrayList<CityMaster>();

			// Find All
			Root<CityMaster> c = query.from(CityMaster.class);

			// Select
			query.select(c);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CityMaster> ocpm1 = amendId.from(CityMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
			Predicate a2 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
//			Predicate a3 = cb.equal(c.get("regionId"), ocpm1.get("regionId"));
			Predicate a4 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));

			amendId.where(a1, a2, a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("cityId")));

			// Where

			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = cb.equal(c.get("cityId"), req.getCityId());
			Predicate n3 = cb.equal(c.get("countryId"), req.getCountryId());
			Predicate n5 = cb.equal(c.get("stateId"), req.getStateId());

			query.where(n1, n2, n3, n5).orderBy(orderList);

			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCityId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(CityMaster :: getCityName ));
			
			res = mapper.map(list.get(0), CityMasterRes.class);
			res.setCityId(list.get(0).getCityId().toString());
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
	public List<Error> validateDropdownGet(CityMasterDropDownReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {

			/*
			 * if (StringUtils.isBlank(req.getStateId()) || req.getStateId() == null &&
			 * req.getCountryId()!="IVY") { errorList.add(new Error("01", "StateId",
			 * "Please Enter State  Id ")); }
			 */
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	@Override
	public List<DropDownRes> getCityMasterDropdown(CityMasterDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
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

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);
			List<CityMaster> list = new ArrayList<CityMaster>();

			// Find All
			Root<CityMaster> c = query.from(CityMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("cityName")));

			// Effective Date Max Filter
			Subquery<Timestamp> effectiveDate = query.subquery(Timestamp.class);
			Root<CityMaster> ocpm1 = effectiveDate.from(CityMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart")));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(c.get("cityId"), ocpm1.get("cityId"));
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			jakarta.persistence.criteria.Predicate a3 = cb.equal(c.get("countryId"), ocpm1.get("countryId"));
//			jakarta.persistence.criteria.Predicate a4 = cb.equal(c.get("regionId"), ocpm1.get("regionId"));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"), ocpm1.get("stateId"));

			effectiveDate.where(a1, a2, a3, a5);
			// Effective Date End Max Filter
			Subquery<Timestamp> effectiveDate2 = query.subquery(Timestamp.class);
			Root<CityMaster> ocpm2 = effectiveDate2.from(CityMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd")));
			jakarta.persistence.criteria.Predicate a6 = cb.equal(c.get("countryId"), ocpm2.get("countryId"));
//			jakarta.persistence.criteria.Predicate a7 = cb.equal(c.get("regionId"), ocpm2.get("regionId"));
			jakarta.persistence.criteria.Predicate a8 = cb.equal(c.get("stateId"), ocpm2.get("stateId"));
			jakarta.persistence.criteria.Predicate a9 = cb.equal(c.get("cityId"), ocpm2.get("cityId"));
			jakarta.persistence.criteria.Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a6,  a8, a9, a10);

			// Where

			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n11 = cb.equal(c.get("status"),"R");
			Predicate n12 = cb.or(n1,n11);
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("countryId"), req.getCountryId());
			jakarta.persistence.criteria.Predicate n6 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);

			if(req.getStateId()!=null) {
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("stateId"), req.getStateId());
			
			query.where(n12, n2, n3, n5, n6).orderBy(orderList);
			}else {
				query.where(n12, n2, n3, n6).orderBy(orderList);	
			}

			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			list = result.getResultList();

			for (CityMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getCityId().toString());
				res.setCodeDesc(data.getCityName());
				res.setCodeDescLocal(data.getCityNameLocal());
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
//************************************************GET ACTIVE CITY******************************************\\
	@Override
	public List<CityMasterRes> getActiveCityDetails(CityMasterGetAllReq req) {
		List<CityMasterRes> resList = new ArrayList<CityMasterRes>();
		ModelMapper mapper = new ModelMapper();
		try {
			List<CityMaster> list = new ArrayList<CityMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);
			// Find All
			Root<CityMaster> b = query.from(CityMaster.class);
		
			// Select
			query.select(b);
		
			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CityMaster> ocpm1 = amendId.from(CityMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("cityId"), b.get("cityId"));
			Predicate a2 = cb.equal(ocpm1.get("countryId"), b.get("countryId"));
//			Predicate a3 = cb.equal(ocpm1.get("regionId"), b.get("regionId"));
			Predicate a4 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));

			amendId.where(a1, a2, a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("cityId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("countryId"), req.getCountryId());
			Predicate n4 = cb.equal(b.get("stateId"), req.getStateId());
			Predicate n3 = cb.equal(b.get("status"), "Y");

			query.where(n1, n2,n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCityId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(CityMaster :: getCityName ));
			
			// Map
			for (CityMaster data : list) {
				CityMasterRes res = new CityMasterRes();

				res = mapper.map(data, CityMasterRes.class);
				res.setCityId(data.getCityId().toString());
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
	public SuccessRes changeStatusOfCity(CityChangeStatusReq req) {
		DozerBeanMapper mapper = new DozerBeanMapper();
		SuccessRes res = new SuccessRes();
		try {
			
			List<CityMaster> list = new ArrayList<CityMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CityMaster> query = cb.createQuery(CityMaster.class);

			// Find All
			Root<CityMaster> b = query.from(CityMaster.class);

			// Select
			query.select(b);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CityMaster> ocpm1 = amendId.from(CityMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("cityId"), b.get("cityId"));
			Predicate a2 = cb.equal(ocpm1.get("stateId"), b.get("stateId"));
			Predicate a3 = cb.equal(ocpm1.get("countryId"), b.get("countryId"));
			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("cityId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("cityId"), req.getCityId());
			Predicate n3 = cb.equal(b.get("stateId"), req.getStateId());
			Predicate n4 = cb.equal(b.get("countryId"), req.getCountryId());
			
			query.where(n1, n2,n3,n4).orderBy(orderList);

			// Get Result
			TypedQuery<CityMaster> result = em.createQuery(query);
			list = result.getResultList();
			CityMaster updateRecord = list.get(0);

			if(  req.getCityId().equalsIgnoreCase(updateRecord.getCityId().toString())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				CityMaster saveNew = new CityMaster();
				mapper.map(updateRecord,saveNew);
				saveNew.setCityId(Integer.valueOf(req.getCityId()));
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
			// perform update

			res.setResponse("Status Changed");
			res.setSuccessId(req.getCityId());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

}

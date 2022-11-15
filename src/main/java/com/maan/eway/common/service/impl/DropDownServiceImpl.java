package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.CompanyCityMaster;
import com.maan.eway.bean.CompanyRegionMaster;
import com.maan.eway.bean.CompanyStateMaster;
import com.maan.eway.bean.CountryMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.req.NcdDetailsGetReq;
import com.maan.eway.common.service.DropDownService;
import com.maan.eway.master.dropdown.req.CityDropDownReq;
import com.maan.eway.master.dropdown.req.CountryChangeStatusReq;
import com.maan.eway.master.dropdown.req.RegionDropDownReq;
import com.maan.eway.master.dropdown.req.StateDropDownReq;
import com.maan.eway.master.dropdown.service.impl.PolicyTypeMasterServiceImpl;
import com.maan.eway.repository.CompanyCityMasterRepository;
import com.maan.eway.repository.CompanyRegionMasterRepository;
import com.maan.eway.repository.CompanyStateMasterRepository;
import com.maan.eway.repository.CountryMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.req.SubUserTypeReq;
import com.maan.eway.res.DropDownRes;

@Service
public class DropDownServiceImpl  implements DropDownService{


	private Logger log = LogManager.getLogger(DropDownServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private CountryMasterRepository countryRepo;
	
	@Autowired
	private CompanyRegionMasterRepository regionrepo;
	
	@Autowired
	private CompanyStateMasterRepository staterepo;
	
	@Autowired
	private CompanyCityMasterRepository cityrepo;
	
	@Autowired
	private PolicyTypeMasterServiceImpl service;
	
	// Cover Note Type Drop Down

	@Override
	public List<DropDownRes> coverNoteType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("COVER_NOTE_TYPE", "Y");

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
	public List<DropDownRes> paymentmode() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PAYMENT_MODE", "Y");

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
	public List<DropDownRes> endorsementtype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ENDROSEMENT_TYPE", "Y");

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
	public List<DropDownRes> discounttypeoffered() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("DISCOUNT_TYPE_OFFERED", "Y");

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
	public List<DropDownRes> taxexcempted() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("IS_TAX_EXEMPTED", "Y");

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
	public List<DropDownRes> taxexcemptiontype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TAX_EXEMPTION_TYPE", "Y");

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
	public List<DropDownRes> policyholdertype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_TYPE", "Y");

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
	public List<DropDownRes> policyholderidtype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_ID_TYPE", "Y");

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
	public List<DropDownRes> policyholdergender() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("POLICY_HOLDER_GENDER", "Y");

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
	public List<DropDownRes> nametitle() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NAME_TITLE", "Y");

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
	public List<DropDownRes> notificationtype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NOTIFICATION_TYPE", "Y");

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
	public List<DropDownRes> getCountryDropdown() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CountryMaster> query = cb.createQuery(CountryMaster.class);
			List<CountryMaster> list = new ArrayList<CountryMaster>();
			
			// Find All
			Root<CountryMaster>    c = query.from(CountryMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("countryName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CountryMaster> ocpm1 = effectiveDate.from(CountryMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("countryId"),ocpm1.get("countryId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			
			query.where(n1,n2).orderBy(orderList);
			
			// Get Result
			TypedQuery<CountryMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			for(CountryMaster data : list ) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getCountryId().toString());
				res.setCodeDesc(data.getCountryName());
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
	public List<DropDownRes> getRegionDropdown(RegionDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyRegionMaster> query = cb.createQuery(CompanyRegionMaster.class);
			List<CompanyRegionMaster> list = new ArrayList<CompanyRegionMaster>();
			
			// Find All
			Root<CompanyRegionMaster>    c = query.from(CompanyRegionMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("regionName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyRegionMaster> ocpm1 = effectiveDate.from(CompanyRegionMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("countryId"),ocpm1.get("countryId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("regionCode"),ocpm1.get("regionCode"));
			javax.persistence.criteria.Predicate a4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,a3,a4);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("countryId"),req.getCountryId());
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyRegionMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			for(CompanyRegionMaster data : list ) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getRegionCode().toString());
				res.setCodeDesc(data.getRegionName());
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
	public List<DropDownRes> getStateDropdown(StateDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyStateMaster> query = cb.createQuery(CompanyStateMaster.class);
			List<CompanyStateMaster> list = new ArrayList<CompanyStateMaster>();
			
			// Find All
			Root<CompanyStateMaster>    c = query.from(CompanyStateMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("stateName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyStateMaster> ocpm1 = effectiveDate.from(CompanyStateMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("countryId"),ocpm1.get("countryId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("regionCode"),ocpm1.get("regionCode"));
			javax.persistence.criteria.Predicate a4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			javax.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"),ocpm1.get("stateId")); 
			effectiveDate.where(a1,a2,a3,a4,a5);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("countryId"),req.getCountryId());
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
			javax.persistence.criteria.Predicate n5 = cb.equal(c.get("regionCode"),req.getRegionCode());
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyStateMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			for(CompanyStateMaster data : list ) {
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


	@Override
	public List<DropDownRes> getCityDropdown(CityDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyCityMaster> query = cb.createQuery(CompanyCityMaster.class);
			List<CompanyCityMaster> list = new ArrayList<CompanyCityMaster>();
			
			// Find All
			Root<CompanyCityMaster>    c = query.from(CompanyCityMaster.class);		
			
			// Select
			query.select(c );
			
		
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("cityName")));
			
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<CompanyCityMaster> ocpm1 = effectiveDate.from(CompanyCityMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("countryId"),ocpm1.get("countryId") );
			javax.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			javax.persistence.criteria.Predicate a3 = cb.equal(c.get("regionId"),ocpm1.get("regionId"));
			javax.persistence.criteria.Predicate a4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			javax.persistence.criteria.Predicate a5 = cb.equal(c.get("stateId"),ocpm1.get("stateId")); 
			javax.persistence.criteria.Predicate a6 = cb.equal(c.get("cityId"),ocpm1.get("cityId"));
			effectiveDate.where(a1,a2,a3,a4,a5,a6);
			
		    // Where	
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("countryId"),req.getCountryId());
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
			javax.persistence.criteria.Predicate n5 = cb.equal(c.get("regionId"),req.getRegionId());
			javax.persistence.criteria.Predicate n6 = cb.equal(c.get("stateId"),req.getStateId());
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<CompanyCityMaster> result = em.createQuery(query);			
			list =  result.getResultList();  
			
			for(CompanyCityMaster data : list ) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getCityId().toString());
				res.setCodeDesc(data.getCityName());
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
	public List<DropDownRes> getMotorCategory() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_CATEGORY", "Y");

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
	public List<DropDownRes> getMotorType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_TYPE", "Y");

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
	public List<DropDownRes> getMotorUsage() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("MOTOR_USAGE", "Y");

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
	public List<DropDownRes> ownerCategory() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("OWNER_CATEGORY", "Y");

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
	public List<DropDownRes> fleetType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("FLEET_TYPE", "Y");

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
	public List<DropDownRes> reinsuranceCategory() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_CATEGORY", "Y");

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
	public List<DropDownRes> participantType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("PARTICIPANT_TYPE", "Y");

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
	public List<DropDownRes> reinsuranceForm() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_FORM", "Y");

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
	public List<DropDownRes> reinsuranceType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("REINSURANCE_TYPE", "Y");

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
	public List<DropDownRes> claimformdullyfilled() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("CLAIM_FORM_DULLY_FILLED", "Y");

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
	public List<DropDownRes> lostassessmentoption() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("LOSS_ASSESSMENT_OPTION", "Y");

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
	public List<DropDownRes> assessoridtype() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("ASSESSOR_ID_TYPE", "Y");

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
	public List<DropDownRes> claimantCategory() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_CATEGORY", "Y");

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
	public List<DropDownRes> claimantType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_TYPE", "Y");

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
	public List<DropDownRes> claimantIdType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CLAIMANT_ID_TYPE", "Y");

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
	public List<DropDownRes> isreassessment() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("IS_REASSESSMENT", "Y");

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
	public List<DropDownRes> offerAccepted() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("OFFER_ACCEPTED", "Y");

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
	public List<DropDownRes> partiesNotified() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("PARTIES_NOTIFIED", "Y");

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
	public List<DropDownRes> claimResultedLitigation() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeDesc("CLAIM_RESULTED_LITIGATION", "Y");

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
	public List<DropDownRes> tonnage() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TONNAGE", "Y");

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
	public List<DropDownRes> getNcdDetails(NcdDetailsGetReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Integer year = Calendar.getInstance().get(Calendar.YEAR);
			Integer manuyear = Integer.valueOf(req.getManufactureYear());
			Integer ncdyear = year-manuyear;
				List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("NCD", "Y");
				
				for(ListItemValue data : getList) {				
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				res.setStatus(data.getStatus());
				resList.add(res);
				if(ncdyear+2==Integer.valueOf(data.getItemCode())) {
				break;			
				}	
				}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());
			return null;
			}
		return resList;
	}


	@Override
	public List<DropDownRes> insuranceType() {
			List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {

			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("INSURANCE_TYPE", "Y");

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
	public List<DropDownRes> insuranceClass() {
/*		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("INSURANCE_CLASS", "Y");

			for (ListItemValue data : getList) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getItemCode());
				res.setCodeDesc(data.getItemValue());
				resList.add(res);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	*/	
		return service.getPolicyTypeMasterDropdown();

	}


	@Override
	public List<DropDownRes> title() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("TITLE", "Y");

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
	public List<DropDownRes> borrowerType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("BORROWER_TYPE", "Y");

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
	public List<DropDownRes> cityLimit() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("CITY_LIMIT", "Y");

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
	public List<DropDownRes> getLanguage() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("LANGUAGE", "Y");

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
	public List<DropDownRes> getFuelType() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			List<ListItemValue> getList = listRepo.findByItemTypeAndStatusOrderByItemCodeAsc("FUEL_TYPE", "Y");

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

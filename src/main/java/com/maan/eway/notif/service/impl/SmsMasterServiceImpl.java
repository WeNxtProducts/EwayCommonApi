package com.maan.eway.notif.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.SmsConfigMaster;
import com.maan.eway.error.Error;
import com.maan.eway.notif.req.SmsGetReq;
import com.maan.eway.notif.req.SmsInsertReq;
import com.maan.eway.notif.res.SmsMasterGetRes;
import com.maan.eway.notif.service.SmsMasterService;
import com.maan.eway.repository.SmsConfigMasterRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class SmsMasterServiceImpl implements SmsMasterService{

	@Autowired
	private SmsConfigMasterRepository smsrepo; 
	
	@PersistenceContext
	private EntityManager em;
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(SmsMasterServiceImpl.class);
	
	
	@Override
	public List<Error> validatesmsmaster(SmsInsertReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("01", "Company Id", "Please Enter Company Id"));
			} else if (req.getCompanyId().length() > 20) {
				errorList.add(new Error("01", "Company Id", "Please Enter Company Id within 20 Characters"));
			} else if (StringUtils.isBlank(req.getSNo())) {
				List<SmsConfigMaster> smsList = getSnoDetails(req.getCompanyId(), req.getBranchCode());
				if (smsList.size() > 0) {
					errorList.add(new Error("01", "S No", "Please Enter Your Sno"));
				}
			} else {
				List<SmsConfigMaster> smsList = getCompanyIdExistDetails(req.getCompanyId() , req.getBranchCode());
				if (smsList.size() > 0 && (!req.getSNo().equalsIgnoreCase(smsList.get(0).getSNo().toString()))) {
					errorList.add(new Error("01", "Company Id", "This Company Id Already Exist "));
				}

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
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList
						.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} 
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("04", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("04", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("04", "Status", "Enter Status Y or N Only"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("05", "CreatedBy", "Please Enter CreatedBy"));
			} else if (req.getCreatedBy().length() > 100) {
				errorList.add(new Error("05", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("06", "Remarks", "Please Enter Remarks"));
			} else if (req.getRemarks().length() > 300) {
				errorList.add(new Error("06", "Remarks", "Please Enter Remarks within 100 Characters"));
			}
			if (StringUtils.isBlank(req.getSmsUserPass())) {
				errorList.add(new Error("07", "SmsUserPass", "Please Enter SmsUserPass"));
			} else if (req.getSmsUserPass().length() > 150) {
				errorList.add(new Error("07", "SmsUserPass", "Please Enter SmsUserPass within 150 Characters"));
			}
			if (StringUtils.isBlank(req.getSmsUserName())) {
				errorList.add(new Error("08", "SmsUserName", "Please Enter SmsUserName"));
			} else if (req.getSmsUserName().length() > 150) {
				errorList.add(new Error("08", "SmsUserName", "Please Enter SmsUserName within 150 Characters"));
			}
			if (StringUtils.isBlank(req.getSmsPartyUrl())) {
				errorList.add(new Error("09", "SmsPartyUrl", "Please Enter SmsPartyUrl"));
			} else if (req.getSmsPartyUrl().length() > 300) {
				errorList.add(new Error("09", "SmsPartyUrl", "Please Enter SmsPartyUrl within 300 Characters"));
			}
			if (StringUtils.isBlank(req.getSenderId())) {
				errorList.add(new Error("10", "SenderId", "Please Enter SenderId"));
			}
			 else if (req.getSenderId().length() > 60) {
				errorList.add(new Error("10", "Sender ID", "Please Enter Sender ID within 60 Characters"));
			}
			if (StringUtils.isBlank(req.getSecureYn())) {
				errorList.add(new Error("11", "SecureYn", "Please Enter SecureYn"));
			} else if (req.getSecureYn().length() > 60) {
				errorList.add(new Error("11", "SecureYn", "Please Enter SecureYn within 60 Characters"));
			}
			if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("12", "Core App Code", "Please Enter Core App Code"));
			} else if (req.getCoreAppCode().length() > 20) {
				errorList.add(new Error("12", "Core App Code", "Please Enter CreatedBy within 20 Characters"));
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("13", "Regulatory Code", "Please Enter Regulatory Code"));
			} else if (req.getRegulatoryCode().length() > 20) {
				errorList.add(new Error("13", "Regulatory Code", "Please Enter Regulatory Code within 20 Characters"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode().toString())) {
				errorList.add(new Error("14", "Branch Code", "Please Enter BranchCode"));
			}else if (req.getBranchCode().length() > 20) {
				errorList.add(new Error("14", "Branch Code", "Please Enter Branch Code within 20 Characters"));
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes insertsmsmaster(SmsInsertReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		SmsConfigMaster saveData = new SmsConfigMaster();
		List<SmsConfigMaster> list = new ArrayList<SmsConfigMaster>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
		
			String sno ="";
			if(StringUtils.isBlank(req.getSNo())) {
				// Save 
				Integer totalcount =getMasterTableCount ( req.getCompanyId() , req.getBranchCode());
				sno = Integer.valueOf(totalcount+1).toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(sno);
				}
			else {
				// Update
				sno= req.getSNo();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<SmsConfigMaster> query = cb.createQuery(SmsConfigMaster.class);
				// Find all
				Root<SmsConfigMaster> b = query.from(SmsConfigMaster.class);
				// Select
				query.select(b);
//				// Effective Date Max Filter
//				Subquery<Long> effectiveDate = query.subquery(Long.class);
//				Root<SmsConfigMaster> ocpm1= effectiveDate.from(SmsConfigMaster.class);
//				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
//				Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
//				effectiveDate.where(a1);
//				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				//where
				Predicate n1 = cb.equal(b.get("status"),"Y");
//				Predicate n2 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(b.get("sNo"),req.getSNo());
				query.where(n1,n3).orderBy(orderList);
				// Get Result
				TypedQuery<SmsConfigMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				
				list = result.getResultList();
				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						SmsConfigMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							smsrepo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							SmsConfigMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							smsrepo.saveAndFlush(lastRecord);
						}
					
				    }
				}
			
				
				res.setResponse("Updated Successfully");
				res.setSuccessId(sno);
			}
			dozermapper.map(req, saveData);
			saveData.setSNo(Integer.valueOf(sno));
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setAmendId(amendId);
			smsrepo.saveAndFlush(saveData);

			log.info("Saved Details is --> " + json.toJson(saveData));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is -->" + e.getMessage());
			return null;
		}
		return res;
	}

	
	public Integer getMasterTableCount(String companyId , String branchCode) {
		Integer data = 0;
		try {
			List<SmsConfigMaster> list = new ArrayList<SmsConfigMaster>();
			// Find Latest Record

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SmsConfigMaster> query = cb.createQuery(SmsConfigMaster.class);
			// Find All
			Root<SmsConfigMaster> b = query.from(SmsConfigMaster.class);
			// Select
			//query.multiselect(cb.count(b));
			
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<SmsConfigMaster> ocpm1 = effectiveDate.from(SmsConfigMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("sNo")));
			effectiveDate.where(a1);
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
		
			// Get Result
			TypedQuery<SmsConfigMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getSNo() : 0 ;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;

	}

	// Company Id Exist Details validation
	public List<SmsConfigMaster> getSnoDetails(String companyId, String branchCode) {
		List<SmsConfigMaster> list = new ArrayList<SmsConfigMaster>();
		try {
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SmsConfigMaster> query = cb.createQuery(SmsConfigMaster.class);

			// Find All
			Root<SmsConfigMaster> b = query.from(SmsConfigMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SmsConfigMaster> ocpm1 = amendId.from(SmsConfigMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1,a2);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			
			query.where(n1, n2,n6);
			// Get Result
			TypedQuery<SmsConfigMaster> result = em.createQuery(query);
			list = result.getResultList();

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}

	@Override
	public SmsMasterGetRes getbysmsid(SmsGetReq req) {
		SmsMasterGetRes res = new SmsMasterGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today  = req.getEffectiveDateStart()!=null ?req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<SmsConfigMaster> list = new ArrayList<SmsConfigMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<SmsConfigMaster> query = cb.createQuery(SmsConfigMaster.class);
			// Find all
			Root<SmsConfigMaster> b = query.from(SmsConfigMaster.class);
			// Select
			query.select(b);
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<SmsConfigMaster> ocpm1 = amendId.from(SmsConfigMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			//Predicate a1 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where( a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("sNo"), req.getSNo());
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			
			query.where(n1, n2,n3,n6).orderBy(orderList);

			// Get Result
			TypedQuery<SmsConfigMaster> result = em.createQuery(query);
			list = result.getResultList();
//			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getOccupationId()))).collect(Collectors.toList());
//			list.sort(Comparator.comparing(SmsConfigMaster :: get ));
			
			// Map
			for (SmsConfigMaster data : list) {

				res = mapper.map(data, SmsMasterGetRes.class);
				res.setSNo(data.getSNo().toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return res;
	}

	// Company Id Exist Details validation

		private List<SmsConfigMaster> getCompanyIdExistDetails(String companyId, String branchCode){
			List<SmsConfigMaster> list = new ArrayList<SmsConfigMaster>();
			try {
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<SmsConfigMaster> query = cb.createQuery(SmsConfigMaster.class);

				// Find All
				Root<SmsConfigMaster> b = query.from(SmsConfigMaster.class);

				// Select
				query.select(b);

				// AmendId Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<SmsConfigMaster> ocpm1 = amendId.from(SmsConfigMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("sNo"), b.get("sNo"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
				
				amendId.where(a1, a2,a3);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("companyId"), companyId);
				Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
				Predicate n5 = cb.equal(b.get("branchCode"), "99999");
				Predicate n6 = cb.or(n4,n5);
				
				query.where(n1, n2,n6);
				// Get Result
				TypedQuery<SmsConfigMaster> result = em.createQuery(query);
				list = result.getResultList();

			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());

			}
			return list;

		}
		private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
		    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}
}

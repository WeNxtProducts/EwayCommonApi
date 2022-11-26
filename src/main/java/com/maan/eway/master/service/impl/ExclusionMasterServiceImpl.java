package com.maan.eway.master.service.impl;

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

import com.google.gson.Gson;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.common.req.ExclusionMasterDropdownReq;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ExclusionChangeStatusReq;
import com.maan.eway.master.req.ExclusionMasterGetReq;
import com.maan.eway.master.req.ExclusionMasterGetallReq;
import com.maan.eway.master.req.ExclusionMasterSaveReq;
import com.maan.eway.master.res.ExclusionMasterRes;

import com.maan.eway.master.service.ExclusionMasterService;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
@Service
public class ExclusionMasterServiceImpl implements ExclusionMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ExclusionMasterRepository repo;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(ExclusionMasterServiceImpl.class);
	
	@Override
	public List<Error> validateExclusion(ExclusionMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
		
			if (StringUtils.isBlank(req.getExclusionDescription())) {
				errorList.add(new Error("02", "ExclusionDescription", "Please Select ExclusionDescription"));
			}else if (req.getExclusionDescription().length() > 100){
				errorList.add(new Error("02","ExclusionDescription", "Please Enter ExclusionDescription 100 Characters")); 
			}else if (StringUtils.isBlank(req.getExclusionId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<ExclusionMaster> ExclusionList = getExclusionDescriptionExistDetails(req.getExclusionDescription() , req.getCompanyId() , req.getBranchCode());
				if (ExclusionList.size()>0 ) {
					errorList.add(new Error("01", "ExclusionDescription", "This ExclusionDescription Already Exist "));
				}
			}else if (StringUtils.isNotBlank(req.getExclusionId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<ExclusionMaster> ExclusionList = getExclusionDescriptionExistDetails(req.getExclusionDescription() , req.getCompanyId() , req.getBranchCode());
				
				if (ExclusionList.size()>0 &&  (! req.getExclusionId().equalsIgnoreCase(ExclusionList.get(0).getExclusionId().toString())) ) {
					errorList.add(new Error("01", "ExclusionDescription", "This ExclusionDescription Already Exist "));
				}
				
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
	/*		if (StringUtils.isBlank(req.getOccupationNameAr())) {
				errorList.add(new Error("03", "OccupationNameAr", "Please Select OccupationNameAr"));
			}else if (req.getOccupationNameAr().length() > 100){
				errorList.add(new Error("03","OccupationNameAr", "Please Enter OccupationNameAr 100 Characters")); 
			} */
			
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("06", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("06", "Status", "Enter Status in 1 Character Only"));
			}else if(!("Y".equals(req.getStatus())||"N".equals(req.getStatus()) || "R".equals(req.getStatus()))) {
				errorList.add(new Error("06", "Status", "Enter Status in Y or N or R Only"));
			}

			if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("07", "CoreAppCode", "Please Select CoreAppCode"));
			}else if (req.getCoreAppCode().length() > 20){
				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("08", "RegulatoryCode", "Please Select RegulatoryCode"));
			}else if (req.getRegulatoryCode().length() > 20){
				errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}		
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<ExclusionMaster> getExclusionDescriptionExistDetails(String ExclusionDescription , String InsuranceId , String branchCode) {
		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);

			// Find All
			Root<ExclusionMaster> b = query.from(ExclusionMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ExclusionMaster> ocpm1 = amendId.from(ExclusionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("exclusionId"), b.get("exclusionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("exclusionDescription")), ExclusionDescription.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			query.where(n1,n2,n3,n6);
			
			// Get Result
			TypedQuery<ExclusionMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Override
	public SuccessRes saveExclusion(ExclusionMasterSaveReq req) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	ExclusionMaster saveData = new ExclusionMaster();
	List<ExclusionMaster> list  = new ArrayList<ExclusionMaster>();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		Integer amendId = 0;
		Date StartDate = req.getEffectiveDateStart();
		String end = "31/12/2050";
		Date endDate = sdf.parse(end);
		long MILLS_IN_A_DAY = 1000*60*60*24;
		Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
		Date entryDate = null;
		String createdBy ="";
		Integer exclusionId = 0;
		if(StringUtils.isBlank(req.getExclusionId())) {
			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode());
			exclusionId = totalCount+1;
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			res.setResponse("Saved Successfully");
			res.setSuccessId(exclusionId.toString());
		}
		else {
			exclusionId = Integer.valueOf(req.getExclusionId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);
			//Findall
			Root<ExclusionMaster> b = query.from(ExclusionMaster.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("exclusionId"),req.getExclusionId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			
			query.where(n1,n2,n3).orderBy(orderList);
			
			// Get Result
			TypedQuery<ExclusionMaster> result = em.createQuery(query);
			int limit=0, offset=2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId()+1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					ExclusionMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						ExclusionMaster lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(exclusionId.toString());
		}
		dozerMapper.map(req, saveData);
		saveData.setExclusionId(exclusionId);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(createdBy);
		saveData.setEntryDate(entryDate);
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		repo.saveAndFlush(saveData);	
		log.info("Saved Details is --> " + json.toJson(saveData));	
		}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return res;
	}
	
	
public Integer getMasterTableCount(String companyId, String branchCode)	{

	Integer data =0;
	try {
		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);
		//Find all
		Root<ExclusionMaster> b = query.from(ExclusionMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Long> effectiveDate = query.subquery(Long.class);
		Root<ExclusionMaster> ocpm1 = effectiveDate.from(ExclusionMaster.class);
		effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
		Predicate a1 = cb.equal(ocpm1.get("exclusionId"),b.get("exclusionId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		effectiveDate.where(a1,a2,a3);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("exclusionId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		query.where(n1,n2,n5).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<ExclusionMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getExclusionId() : 0 ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}

@Override
public List<ExclusionMasterRes> getallExclusion(ExclusionMasterGetallReq req) {
	List<ExclusionMasterRes> resList = new ArrayList<ExclusionMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);

		// Find All
		Root<ExclusionMaster> b = query.from(ExclusionMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ExclusionMaster> ocpm1 = amendId.from(ExclusionMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("exclusionId"), b.get("exclusionId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

		amendId.where(a1, a2,a3);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		query.where(n1,n2,n5).orderBy(orderList);
		
		// Get Result
		TypedQuery<ExclusionMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ExclusionMaster :: getExclusionDescription ));
		
		// Map
		for (ExclusionMaster data : list) {
			ExclusionMasterRes res = new ExclusionMasterRes();

			res = mapper.map(data, ExclusionMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());

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
@Override
public List<ExclusionMasterRes> getActiveExclusion(ExclusionMasterGetallReq req) {
	List<ExclusionMasterRes> resList = new ArrayList<ExclusionMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);

		// Find All
		Root<ExclusionMaster> b = query.from(ExclusionMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ExclusionMaster> ocpm1 = amendId.from(ExclusionMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("exclusionId"), b.get("exclusionId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

		amendId.where(a1, a2,a3);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("status"), "Y");
		Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		Predicate n6 = cb.or(n3,n5);
		query.where(n1,n2,n4,n6).orderBy(orderList);
		
		// Get Result
		TypedQuery<ExclusionMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ExclusionMaster :: getExclusionDescription ));
		
		// Map
		for (ExclusionMaster data : list) {
			ExclusionMasterRes res = new ExclusionMasterRes();

			res = mapper.map(data, ExclusionMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());

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
public ExclusionMasterRes getByExclusionId(ExclusionMasterGetReq req) {
	ExclusionMasterRes res = new ExclusionMasterRes();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();

		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);

		// Find All
		Root<ExclusionMaster> b = query.from(ExclusionMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ExclusionMaster> ocpm1 = amendId.from(ExclusionMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("exclusionId"), b.get("exclusionId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

		amendId.where(a1, a2,a3);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("exclusionId"), req.getExclusionId());
		Predicate n6 = cb.equal(b.get("branchCode"), "99999");
		Predicate n7 = cb.or(n3,n6);
		query.where(n1,n2,n4,n7).orderBy(orderList);
		
		// Get Result
		TypedQuery<ExclusionMaster> result = em.createQuery(query);

		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ExclusionMaster :: getExclusionDescription ));
		
		res = mapper.map(list.get(0), ExclusionMasterRes.class);
		res.setExclusionId(list.get(0).getExclusionId().toString());
		res.setEntryDate(list.get(0).getEntryDate());
		res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
		res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
		res.setCoreAppCode(list.get(0).getCoreAppCode());
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}

@Override
public SuccessRes changeStatusOfExclusion(ExclusionChangeStatusReq req) {
	SuccessRes res = new SuccessRes();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
		
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ExclusionMaster> query = cb.createQuery(ExclusionMaster.class);
		// Find all
		Root<ExclusionMaster> b = query.from(ExclusionMaster.class);
		//Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ExclusionMaster> ocpm1 = amendId.from(ExclusionMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("exclusionId"), b.get("exclusionId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

		amendId.where(a1, a2,a3);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("exclusionId"), req.getExclusionId());
		Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		Predicate n6 = cb.or(n3,n5);
		
		query.where(n1,n2,n4,n6).orderBy(orderList);
		
		// Get Result 
		TypedQuery<ExclusionMaster> result = em.createQuery(query);
		list = result.getResultList();
		ExclusionMaster updateRecord = list.get(0);
		if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
		} else {
			ExclusionMaster saveNew = new ExclusionMaster();
			dozerMapper.map(updateRecord,saveNew);
			saveNew.setBranchCode(req.getBranchCode());
			saveNew.setStatus(req.getStatus());
			repo.save(saveNew);
		}
	
		// Perform Update
		res.setResponse("Status Changed");
		res.setSuccessId(req.getExclusionId());
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
		}
	return res;
}



@Override
public List<DropDownRes> getExclusionMasterDropdown(ExclusionMasterDropdownReq req) {
List<DropDownRes> resList = new ArrayList<DropDownRes>();
try {
	Date today = new Date();
	Calendar cal = new GregorianCalendar();
	cal.setTime(today);
	today = cal.getTime();
	Date todayEnd = cal.getTime();
	
	// Criteria
	CriteriaBuilder cb = em.getCriteriaBuilder();
	CriteriaQuery<ExclusionMaster> query=  cb.createQuery(ExclusionMaster.class);
	List<ExclusionMaster> list = new ArrayList<ExclusionMaster>();
	// Find All
	Root<ExclusionMaster> c = query.from(ExclusionMaster.class);
	//Select
	query.select(c);
	// Order By
	List<Order> orderList = new ArrayList<Order>();
	orderList.add(cb.asc(c.get("exclusionDescription")));
	
	// Effective Date Start Max Filter
	Subquery<Long> effectiveDate = query.subquery(Long.class);
	Root<ExclusionMaster> ocpm1 = effectiveDate.from(ExclusionMaster.class);
	effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
	Predicate a1 = cb.equal(c.get("exclusionId"),ocpm1.get("exclusionId"));
	Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
	effectiveDate.where(a1,a2);
	// Effective Date End Max Filter
	Subquery<Long> effectiveDate2 = query.subquery(Long.class);
	Root<ExclusionMaster> ocpm2 = effectiveDate2.from(ExclusionMaster.class);
	effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
	Predicate a3 = cb.equal(c.get("exclusionId"),ocpm2.get("exclusionId"));
	Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
	effectiveDate2.where(a3,a4);
	// Where
	Predicate n1 = cb.equal(c.get("status"),"Y");
	Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
	Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
	Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
	Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
	Predicate n6 = cb.equal(c.get("branchCode"),"99999");
	Predicate n7 = cb.or(n5,n6);
	Predicate n8 = cb.equal(c.get("productId"),req.getProductId());

	query.where(n1,n2,n3,n4,n7).orderBy(orderList);
	// Get Result
	TypedQuery<ExclusionMaster> result = em.createQuery(query);
	list = result.getResultList();
	for (ExclusionMaster data : list) {
		// Response 
		DropDownRes res = new DropDownRes();
		res.setCode(data.getExclusionId().toString());
		res.setCodeDesc(data.getExclusionDescription());
		res.setStatus(data.getStatus());
		resList.add(res);
	}
}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is --->"+e.getMessage());
		return null;
		}
	return resList;
}


		
	

	
	
	
	
}

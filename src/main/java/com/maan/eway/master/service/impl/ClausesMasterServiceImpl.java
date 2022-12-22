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
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ClausesChangeStatusReq;
import com.maan.eway.master.req.ClausesMasterDropdownReq;
import com.maan.eway.master.req.ClausesMasterGetReq;
import com.maan.eway.master.req.ClausesMasterGetallReq;
import com.maan.eway.master.req.ClausesMasterSaveReq;
import com.maan.eway.master.res.ClausesMasterRes;
import com.maan.eway.master.service.ClausesMasterService;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
@Service
public class ClausesMasterServiceImpl implements ClausesMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private ClausesMasterRepository repo;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(ClausesMasterServiceImpl.class);
	
	@Override
	public List<Error> validateClauses(ClausesMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
		
			if (StringUtils.isBlank(req.getClausesDescription())) {
				errorList.add(new Error("02", "ClausesDescription", "Please Select ClausesDescription"));
			}else if (req.getClausesDescription().length() > 100){
				errorList.add(new Error("02","ClausesDescription", "Please Enter ClausesDescription 100 Characters")); 
			}else if (StringUtils.isBlank(req.getClausesId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())&& StringUtils.isNotBlank(req.getPolicyType())) {
				List<ClausesMaster> ClausesList = getClausesDescriptionExistDetails(req.getClausesDescription() , req.getCompanyId() , req.getBranchCode(), req.getProductId(),req.getSectionId(), req.getPolicyType());
				if (ClausesList.size()>0 ) {
					errorList.add(new Error("01", "ClausesDescription", "This ClausesDescription Already Exist "));
				}
			}else if (StringUtils.isNotBlank(req.getClausesId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())&& StringUtils.isNotBlank(req.getPolicyType())) {
				List<ClausesMaster> ClausesList = getClausesDescriptionExistDetails(req.getClausesDescription() , req.getCompanyId() , req.getBranchCode(), req.getProductId(),req.getSectionId(), req.getPolicyType());
				
				if (ClausesList.size()>0 &&  (! req.getClausesId().equalsIgnoreCase(ClausesList.get(0).getClausesId().toString())) ) {
					errorList.add(new Error("01", "ClausesDescription", "This ClausesDescription Already Exist "));
				}
				
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			
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
			
			if (StringUtils.isBlank(req.getCoverId())) {
				errorList.add(new Error("10","CoverId", "Please Enter CoverId ")); 
			}
			if (StringUtils.isBlank(req.getExtraCoverId())) {
				errorList.add(new Error("11","ExtraCoverId", "Please Enter ExtraCoverId ")); 
			}
			if (StringUtils.isBlank(req.getDisplayOrder())) {
				errorList.add(new Error("12","DisplayOrder", "Please Enter DisplayOrder ")); 
			}
			if (req.getPdfLocation().length() > 100){
				errorList.add(new Error("13","IntCode", "Please Enter IntCode  within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("14", "ProductId", "Please Enter ProductId"));
			}
			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("15", "SectionId", "Please Enter SectionId"));
			}
			if (StringUtils.isBlank(req.getPolicyType())) {
				errorList.add(new Error("16", "PolicyType", "Please Enter PolicyType"));
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<ClausesMaster> getClausesDescriptionExistDetails(String ClausesDescription , String InsuranceId , String branchCode, String  productId, String sectionId, String policyType) {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

			// Find All
			Root<ClausesMaster> b = query.from(ClausesMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ClausesMaster> ocpm1 = amendId.from(ClausesMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("clausesId"), b.get("clausesId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a8 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));

			
			amendId.where(a1,a2,a3,a4,a5,a6,a7,a8);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("clausesDescription")), ClausesDescription.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(b.get("productId"),productId);
			Predicate n8 = cb.equal(b.get("productId"), "99999");
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(b.get("sectionId"),sectionId);
			Predicate n11 = cb.equal(b.get("sectionId"), "99999");
			Predicate n12 = cb.or(n9,n10);
			Predicate n13 = cb.equal(b.get("policyType"),policyType);
			Predicate n14 = cb.equal(b.get("policyType"), "99999");
			Predicate n15 = cb.or(n12,n13);
			
			query.where(n1,n2,n3,n6,n9,n12,n15);
			
			// Get Result
			TypedQuery<ClausesMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Override
	public SuccessRes saveClauses(ClausesMasterSaveReq req) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	ClausesMaster saveData = new ClausesMaster();
	List<ClausesMaster> list  = new ArrayList<ClausesMaster>();
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
		Integer clausesId = 0;
		if(StringUtils.isBlank(req.getClausesId())) {
			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId(),req.getPolicyType());
			clausesId = totalCount+1;
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			res.setResponse("Saved Successfully");
			res.setSuccessId(clausesId.toString());
		}
		else {
			clausesId = Integer.valueOf(req.getClausesId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);
			//Findall
			Root<ClausesMaster> b = query.from(ClausesMaster.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("clausesId"),req.getClausesId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());
			
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<ClausesMaster> result = em.createQuery(query);
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
					ClausesMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						ClausesMaster lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(clausesId.toString());
		}
	
		dozerMapper.map(req, saveData);
		
		saveData.setClausesId(clausesId);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(createdBy);
		saveData.setEntryDate(entryDate);
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setBranchCode(req.getBranchCode()==null?"" : "99999");
		saveData.setProductId(req.getProductId()==null?"" : "99999");
		saveData.setSectionId(req.getSectionId()==null?"" : "99999");
		saveData.setPolicyType(req.getPolicyType()==null?"" : "99999");
		
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
	
	
public Integer getMasterTableCount(String companyId, String branchCode, String productId, String sectionId, String policyType)	{

	Integer data =0;
	try {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);
		//Find all
		Root<ClausesMaster> b = query.from(ClausesMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Long> effectiveDate = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = effectiveDate.from(ClausesMaster.class);
		effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
		Predicate a1 = cb.equal(ocpm1.get("clausesId"),b.get("clausesId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));
		
		
		effectiveDate.where(a1,a2,a3,a4,a5,a6);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("clausesId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		Predicate n6 = cb.equal(b.get("productId"),productId);
		Predicate n7 = cb.equal(b.get("productId"), "99999");
		Predicate n8 = cb.or(n6,n7);
		Predicate n9 = cb.equal(b.get("sectionId"),sectionId);
		Predicate n10 = cb.equal(b.get("sectionId"), "99999");
		Predicate n11 = cb.or(n9,n10);
		Predicate n12 = cb.equal(b.get("policyType"),policyType);
		Predicate n13 = cb.equal(b.get("policyType"), "99999");
		Predicate n14 = cb.or(n12,n13);
		
		
		query.where(n1,n2,n5,n8,n11,n14).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<ClausesMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getClausesId() : 0 ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}

@Override
public List<ClausesMasterRes> getallClauses(ClausesMasterGetallReq req) {
	List<ClausesMasterRes> resList = new ArrayList<ClausesMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

		// Find All
		Root<ClausesMaster> b = query.from(ClausesMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = amendId.from(ClausesMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("clausesId"), b.get("clausesId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));
		amendId.where(a1, a2,a3,a4,a5,a6);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		Predicate n6 = cb.equal(b.get("productId"),req.getProductId());
		Predicate n7 = cb.equal(b.get("productId"), "99999");
		Predicate n8 = cb.or(n6,n7);
		Predicate n9 = cb.equal(b.get("sectionId"),req.getSectionId());
		Predicate n10 = cb.equal(b.get("sectionId"), "99999");
		Predicate n11 = cb.or(n9,n10);
		Predicate n12 = cb.equal(b.get("policyType"),req.getPolicyType());
		Predicate n13 = cb.equal(b.get("policyType"), "99999");
		Predicate n14 = cb.or(n12,n13);

		
		query.where(n1,n2,n5,n8,n11,n14).orderBy(orderList);
		
		// Get Result
		TypedQuery<ClausesMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ClausesMaster :: getClausesDescription ));
		
		// Map
		for (ClausesMaster data : list) {
			ClausesMasterRes res = new ClausesMasterRes();

			res = mapper.map(data, ClausesMasterRes.class);
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
public List<ClausesMasterRes> getActiveClauses(ClausesMasterGetallReq req) {
	List<ClausesMasterRes> resList = new ArrayList<ClausesMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

		// Find All
		Root<ClausesMaster> b = query.from(ClausesMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = amendId.from(ClausesMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("clausesId"), b.get("clausesId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5,a6);

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
		Predicate n7 = cb.equal(b.get("productId"),req.getProductId());
		Predicate n8 = cb.equal(b.get("productId"), "99999");
		Predicate n9 = cb.or(n7,n8);
		Predicate n10 = cb.equal(b.get("sectionId"),req.getSectionId());
		Predicate n11 = cb.equal(b.get("sectionId"), "99999");
		Predicate n12 = cb.or(n10,n11);
		Predicate n13 = cb.equal(b.get("policyType"),req.getPolicyType());
		Predicate n14 = cb.equal(b.get("policyType"), "99999");
		Predicate n15 = cb.or(n13,n14);

		
		query.where(n1,n2,n4,n6,n9,n12,n15).orderBy(orderList);
		
		// Get Result
		TypedQuery<ClausesMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ClausesMaster :: getClausesDescription ));
		
		// Map
		for (ClausesMaster data : list) {
			ClausesMasterRes res = new ClausesMasterRes();

			res = mapper.map(data, ClausesMasterRes.class);
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
public ClausesMasterRes getByClausesId(ClausesMasterGetReq req) {
	ClausesMasterRes res = new ClausesMasterRes();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();

		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

		// Find All
		Root<ClausesMaster> b = query.from(ClausesMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = amendId.from(ClausesMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("clausesId"), b.get("clausesId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5,a6);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("clausesId"), req.getClausesId());
		Predicate n6 = cb.equal(b.get("branchCode"), "99999");
		Predicate n7 = cb.or(n3,n6);
		Predicate n8 = cb.equal(b.get("productId"),req.getProductId());
		Predicate n9 = cb.equal(b.get("productId"), "99999");
		Predicate n10 = cb.or(n8,n9);
		Predicate n11 = cb.equal(b.get("sectionId"),req.getSectionId());
		Predicate n12 = cb.equal(b.get("sectionId"), "99999");
		Predicate n13 = cb.or(n11,n12);
		Predicate n14 = cb.equal(b.get("policyType"),req.getPolicyType());
		Predicate n15 = cb.equal(b.get("policyType"), "99999");
		Predicate n16 = cb.or(n14,n15);

		query.where(n1,n2,n4,n7,n10,n13,n16).orderBy(orderList);
		
		// Get Result
		TypedQuery<ClausesMaster> result = em.createQuery(query);

		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(ClausesMaster :: getClausesDescription ));
		
		res = mapper.map(list.get(0), ClausesMasterRes.class);
		res.setClausesId(list.get(0).getClausesId().toString());
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
public SuccessRes changeStatusOfClauses(ClausesChangeStatusReq req) {
	SuccessRes res = new SuccessRes();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
		
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);
		// Find all
		Root<ClausesMaster> b = query.from(ClausesMaster.class);
		//Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = amendId.from(ClausesMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("clausesId"), b.get("clausesId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5,a6);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("clausesId"), req.getClausesId());
		Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		Predicate n6 = cb.or(n3,n5);
		Predicate n7 = cb.equal(b.get("productId"),req.getProductId());
		Predicate n8 = cb.equal(b.get("productId"), "99999");
		Predicate n9 = cb.or(n7,n8);
		Predicate n10 = cb.equal(b.get("sectionId"),req.getSectionId());
		Predicate n11 = cb.equal(b.get("sectionId"), "99999");
		Predicate n12 = cb.or(n10,n11);
		Predicate n13 = cb.equal(b.get("policyType"),req.getPolicyType());
		Predicate n14 = cb.equal(b.get("policyType"), "99999");
		Predicate n15 = cb.or(n13,n14);

		
		query.where(n1,n2,n4,n6,n9,n12,n15).orderBy(orderList);
		
		// Get Result 
		TypedQuery<ClausesMaster> result = em.createQuery(query);
		list = result.getResultList();
		ClausesMaster updateRecord = list.get(0);
		if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
		} else {
			ClausesMaster saveNew = new ClausesMaster();
			dozerMapper.map(updateRecord,saveNew);
			saveNew.setBranchCode(req.getBranchCode());
			saveNew.setStatus(req.getStatus());
			repo.save(saveNew);
		}
	
		// Perform Update
		res.setResponse("Status Changed");
		res.setSuccessId(req.getClausesId());
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
		}
	return res;
}
@Override
public List<DropDownRes> getClausesMasterDropdown(ClausesMasterDropdownReq req) {
	List<DropDownRes> resList = new ArrayList<DropDownRes>();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		today = cal.getTime();
		Date todayEnd = cal.getTime();
		
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClausesMaster> query=  cb.createQuery(ClausesMaster.class);
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();
		// Find All
		Root<ClausesMaster> c = query.from(ClausesMaster.class);
		//Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("clausesDescription")));
		
		// Effective Date Start Max Filter
		Subquery<Long> effectiveDate = query.subquery(Long.class);
		Root<ClausesMaster> ocpm1 = effectiveDate.from(ClausesMaster.class);
		effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
		Predicate a1 = cb.equal(c.get("clausesId"),ocpm1.get("clausesId"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		effectiveDate.where(a1,a2);
		// Effective Date End Max Filter
		Subquery<Long> effectiveDate2 = query.subquery(Long.class);
		Root<ClausesMaster> ocpm2 = effectiveDate2.from(ClausesMaster.class);
		effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
		Predicate a3 = cb.equal(c.get("clausesId"),ocpm2.get("clausesId"));
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
		Predicate n9 = cb.equal(c.get("productId"), "99999");
		Predicate n10 = cb.or(n8,n9);
		Predicate n11 = cb.equal(c.get("sectionId"),req.getSectionId());
		Predicate n12 = cb.equal(c.get("sectionId"), "99999");
		Predicate n13 = cb.or(n11,n12);
		Predicate n14 = cb.equal(c.get("policyType"),req.getPolicyType());
		Predicate n15 = cb.equal(c.get("policyType"), "99999");
		Predicate n16 = cb.or(n14,n15);


		query.where(n1,n2,n3,n4,n7,n10,n13,n16).orderBy(orderList);
		// Get Result
		TypedQuery<ClausesMaster> result = em.createQuery(query);
		list = result.getResultList();
		for (ClausesMaster data : list) {
			// Response 
			DropDownRes res = new DropDownRes();
			res.setCode(data.getClausesId().toString());
			res.setCodeDesc(data.getClausesDescription());
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

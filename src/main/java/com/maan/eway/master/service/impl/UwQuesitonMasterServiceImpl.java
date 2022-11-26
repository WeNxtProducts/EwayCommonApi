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
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.UWQuestionsMaster;
import com.maan.eway.common.req.UwMasterDropdownReq;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.UwQuestionChangeStatusReq;
import com.maan.eway.master.req.UwQuestionMasterGetReq;
import com.maan.eway.master.req.UwQuestionMasterSaveReq;
import com.maan.eway.master.req.UwQuestionsMasterGetAllReq;
import com.maan.eway.master.res.UwQuestionMasterRes;
import com.maan.eway.master.service.UwQuestionMasterService;
import com.maan.eway.repository.UwQuestionMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class UwQuesitonMasterServiceImpl implements UwQuestionMasterService {

	@Autowired
	private UwQuestionMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(UwQuesitonMasterServiceImpl.class);

	@Override
	public List<Error> validateUwQuestions(UwQuestionMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
		
			if (StringUtils.isBlank(req.getUwQuestionDesc())) {
				errorList.add(new Error("02", "UwQuestionDesc", "Please Select UwQuestionDesc"));
			}else if (req.getUwQuestionDesc().length() > 100){
				errorList.add(new Error("02","UwQuestionDesc", "Please Enter UwQuestionDesc 100 Characters")); 
			}
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			if (StringUtils.isBlank(req.getQuestionType())) {
				errorList.add(new Error("03", "QuestionType", "Please Select QuestionType"));
			}else if (req.getQuestionType().length() > 100){
				errorList.add(new Error("03","QuestionType", "Please Enter QuestionType 100 Characters")); 
			} 
			if(req.getQuestionType().equalsIgnoreCase("02")){
				if (StringUtils.isBlank(req.getDataType())) {
					errorList.add(new Error("03", "DataType", "Please Select DataType"));
				}	
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
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<UWQuestionsMaster> getUwQuestionDescExistDetails(String UWQuestionDesc , String InsuranceId , String branchCode, String productId, String Status) {
		List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));

			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("uwQuestionDesc")), UWQuestionDesc.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(b.get("productId"),productId);
			
			query.where(n1,n2,n3,n6,n7);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	@Override
	public SuccessRes insertUwQuestions(UwQuestionMasterSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		UWQuestionsMaster saveData = new UWQuestionsMaster();
		List<UWQuestionsMaster> list  = new ArrayList<UWQuestionsMaster>();
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
			Integer uwQuestionId = 0;
			if(StringUtils.isBlank(req.getUwQuestionId())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode(),req.getProductId());
				uwQuestionId = totalCount+1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(uwQuestionId.toString());
			}
			else {
				uwQuestionId = Integer.valueOf(req.getUwQuestionId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
				//Findall
				Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				//Where
				Predicate n1 = cb.equal(b.get("uwQuestionId"),req.getUwQuestionId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
				
				query.where(n1,n2,n3).orderBy(orderList);
				
				// Get Result
				TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
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
						UWQuestionsMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if(list.size()>1) {
							UWQuestionsMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(uwQuestionId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setUwQuestionId(uwQuestionId);
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
		
	public Integer getMasterTableCount(String companyId, String branchCode, String productId)	{

		Integer data =0;
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			//Find all
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = effectiveDate.from(UWQuestionsMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"),b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			effectiveDate.where(a1,a2,a3,a4);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("uwQuestionId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("productId"), productId);

			query.where(n1,n2,n5,n6).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getUwQuestionId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}

	@Override
	public List<UwQuestionMasterRes> getallUwQuestions(UwQuestionsMasterGetAllReq req) {
		List<UwQuestionMasterRes> resList = new ArrayList<UwQuestionMasterRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
			
			query.where(n1,n2,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			
			// Map
			for (UWQuestionsMaster data : list) {
				UwQuestionMasterRes res = new UwQuestionMasterRes();

				res = mapper.map(data, UwQuestionMasterRes.class);
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
	public List<UwQuestionMasterRes> getActiveUwQuestions(UwQuestionsMasterGetAllReq req) {
		List<UwQuestionMasterRes> resList = new ArrayList<UwQuestionMasterRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			amendId.where(a1, a2,a3,a4);

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
			Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
			
			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			
			// Map
			for (UWQuestionsMaster data : list) {
				UwQuestionMasterRes res = new UwQuestionMasterRes();

				res = mapper.map(data, UwQuestionMasterRes.class);
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
	public UwQuestionMasterRes getByUwQuestionId(UwQuestionMasterGetReq req) {
		UwQuestionMasterRes res = new UwQuestionMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);

			// Find All
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("uwQuestionId"),b.get("uwQuestionId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("uwQuestionId"), req.getUwQuestionId());
			Predicate n6 = cb.equal(b.get("branchCode"), "99999");
			Predicate n8 = cb.equal(b.get("productId"), req.getProductId());
			
			Predicate n7 = cb.or(n3,n6);
			query.where(n1,n2,n4,n7,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getUwQuestionId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(UWQuestionsMaster :: getUwQuestionDesc ));
			
			res = mapper.map(list.get(0), UwQuestionMasterRes.class);
			res.setUwQuestionId(list.get(0).getUwQuestionId().toString());
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
	public SuccessRes changeStatusOfUwQuestion(UwQuestionChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			// Find all
			Root<UWQuestionsMaster> b = query.from(UWQuestionsMaster.class);
			//Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = amendId.from(UWQuestionsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("uwQuestionId"), b.get("uwQuestionId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("uwQuestionId"), req.getUwQuestionId());

			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			
			// Get Result 
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			UWQuestionsMaster updateRecord = list.get(0);
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				UWQuestionsMaster saveNew = new UWQuestionsMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
		
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getUwQuestionId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getUwQuestionMasterDropdown(UwQuestionMasterGetReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UWQuestionsMaster> query = cb.createQuery(UWQuestionsMaster.class);
			List<UWQuestionsMaster> list = new ArrayList<UWQuestionsMaster>();
			// Find All
			Root<UWQuestionsMaster> c = query.from(UWQuestionsMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("uwQuestionDesc")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm1 = effectiveDate.from(UWQuestionsMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("uwQuestionId"), ocpm1.get("uwQuestionId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));

			effectiveDate.where(a1, a2, a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<UWQuestionsMaster> ocpm2 = effectiveDate2.from(UWQuestionsMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a6 = cb.equal(c.get("uwQuestionId"), ocpm2.get("uwQuestionId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));

			effectiveDate2.where(a6, a7, a8);
			// Where
			// Where
			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n3 = cb.equal(c.get("companyId"), req.getCompanyId());
			javax.persistence.criteria.Predicate n4 = cb.equal(c.get("productId"), req.getProductId());
			javax.persistence.criteria.Predicate n5 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);

			query.where(n1, n2, n3, n4, n5).orderBy(orderList);

			// Get Result
			TypedQuery<UWQuestionsMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (UWQuestionsMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getUwQuestionId().toString());
				res.setCodeDesc(data.getUwQuestionDesc());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	

}

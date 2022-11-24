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
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.MakeModelChangeStatusReq;
import com.maan.eway.master.req.MotorMakeModelGetAllReq;
import com.maan.eway.master.req.MotorMakeModelGetReq;
import com.maan.eway.master.req.MotorMakeModelSaveReq;
import com.maan.eway.master.res.MotorMakeModelGetRes;
import com.maan.eway.master.service.MotorMakeModelMasterService;
import com.maan.eway.repository.MotorMakeModelMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MotorMakeModelMasterServiceImpl implements MotorMakeModelMasterService {

	@Autowired
	private MotorMakeModelMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorMakeModelMasterServiceImpl.class);

	@Override
	public List<Error> validateMotorMakeModel(MotorMakeModelSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
		
			if (StringUtils.isBlank(req.getModelNameEn())) {
				errorList.add(new Error("02", "ModelName", "Please Select ModelName"));
			}else if (req.getModelNameEn().length() > 100){
				errorList.add(new Error("02","ModelName", "Please Enter ModelName 100 Characters")); 
			}else if (StringUtils.isBlank(req.getModelId()) && StringUtils.isNotBlank(req.getMakeId())  && StringUtils.isNotBlank(req.getBodyId()) 
					&& StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorMakeModelMaster> ModelList = getModelNameExistDetails(req.getMakeId()  ,req.getBodyId() , req.getMakeNameEn() ,  req.getInsuranceId() , req.getBranchCode());
				if (ModelList.size()>0 ) {
					errorList.add(new Error("01", "ModelName", "This ModelName Already Exist "));
				}
			}else if (StringUtils.isNotBlank(req.getModelId()) && StringUtils.isNotBlank(req.getMakeId())  && StringUtils.isNotBlank(req.getBodyId()) 
					&& StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode()) ) {
				List<MotorMakeModelMaster> ModelList = getModelNameExistDetails(req.getMakeId()  ,req.getBodyId() , req.getMakeNameEn() , req.getInsuranceId() , req.getBranchCode());
				
				if (ModelList.size()>0 &&  (! req.getModelId().equalsIgnoreCase(ModelList.get(0).getModelId().toString())) ) {
					errorList.add(new Error("01", "ModelName", "This ModelName Already Exist "));
				}
				
			}
			
			
			if (StringUtils.isBlank(req.getInsuranceId())) {
				errorList.add(new Error("02", "InsuranceId", "Please Enter InsuranceId"));
			}
			if (StringUtils.isBlank(req.getMakeId())) {
				errorList.add(new Error("02", "MakeId", "Please Enter MakeId"));
			}
			if (StringUtils.isBlank(req.getMakeNameEn())) {
				errorList.add(new Error("02", "MakeName", "Please Enter MakeName"));
			}
			
			if (StringUtils.isBlank(req.getBodyId())) {
				errorList.add(new Error("02", "BodyId", "Please Enter BodyId"));
			}
			if (StringUtils.isBlank(req.getBodyNameEn())) {
				errorList.add(new Error("02", "BodyName", "Please Enter BodyName"));
			}
			
			if (StringUtils.isBlank(req.getInsuranceId())) {
				errorList.add(new Error("02", "InsuranceId", "Please Enter InsuranceId"));
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
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus()) || "R".equalsIgnoreCase(req.getStatus()))) {
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

	public List<MotorMakeModelMaster> getModelNameExistDetails(String makeId , String bodyId ,  String modelName , String InsuranceId , String branchCode) {
		List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("modelNameEn")), modelName.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(b.get("makeId"), b.get("makeId"));
			Predicate n8 = cb.equal(b.get("bodyId"), b.get("bodyId"));
			query.where(n1,n2,n3,n6,n7,n8);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	
	@Override
	public SuccessRes saveMotorMakeModel(MotorMakeModelSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		MotorMakeModelMaster saveData = new MotorMakeModelMaster();
		List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			Integer modelId = 0 ;
			if(StringUtils.isBlank(req.getModelId())) {
				// Save
				Integer totalCount = getMasterTableCount( req.getInsuranceId() , req.getBranchCode());
				modelId =  totalCount+ 1 ;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(modelId.toString());
			}
			else {
				// Update
				modelId = Integer.valueOf(req.getModelId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
				//Find all
				Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
				//Select 
				query.select(b);
//				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				// Where
			//	Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n2 = cb.equal(b.get("modelId"), req.getModelId());
				Predicate n3 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n5 = cb.equal(b.get("makeId"), req.getMakeId());
				Predicate n6 = cb.equal(b.get("bodyId"), req.getBodyId());
				
				query.where(n2,n3,n4,n5,n6).orderBy(orderList);
				
				// Get Result 
				TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
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
						MotorMakeModelMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorMakeModelMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(modelId.toString());
			}
			dozerMapper.map(req, saveData);
			saveData.setModelId(modelId);
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setBranchCode(req.getBranchCode());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setAmendId(amendId);
			saveData.setCoreAppCode(req.getCoreAppCode());
			repo.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
			
			}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> "+ e.getMessage());
			return null;
		}
		return res;
		}

	@Override
	public MotorMakeModelGetRes getMotorMakeModel(MotorMakeModelGetReq req) {
		MotorMakeModelGetRes res = new MotorMakeModelGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"), req.getMakeId());
			Predicate n7 = cb.equal(b.get("modelId"), req.getModelId());
			Predicate n8 = cb.equal(b.get("bodyId"), req.getBodyId());
			query.where(n1,n2,n5,n6,n7,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			// Map
			res = mapper.map(list.get(0) , MotorMakeModelGetRes.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return res;
	}

	@Override
	public List<MotorMakeModelGetRes> getallMotorMakeModel(MotorMakeModelGetAllReq req) {
		List<MotorMakeModelGetRes> resList = new ArrayList<MotorMakeModelGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"), req.getMakeId());
			query.where(n1,n2,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getModelId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeModelMaster :: getModelNameEn ));
			// Map
			for (MotorMakeModelMaster data : list) {
				MotorMakeModelGetRes res = new MotorMakeModelGetRes();
				res = mapper.map(data, MotorMakeModelGetRes.class);
			
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
	
	public Integer getMasterTableCount(String companyId , String branchCode) {
		Integer data =0;
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
		// Find all
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
			//Select 
			query.select(b);

			//Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a2,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("modelId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getModelId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	@Override
	public List<MotorMakeModelGetRes> getactiveMakeModel(MotorMakeModelGetAllReq req) {
		List<MotorMakeModelGetRes> resList = new ArrayList<MotorMakeModelGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);

			// Find All
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = amendId.from(MotorMakeModelMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("modelId"), b.get("modelId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("makeId"),req.getMakeId());
			Predicate n7 = cb.equal(b.get("status"), "Y");
			query.where(n1,n2,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getModelId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeModelMaster :: getModelNameEn ));
			// Map
			for (MotorMakeModelMaster data : list) {
				MotorMakeModelGetRes res = new MotorMakeModelGetRes();
				res = mapper.map(data, MotorMakeModelGetRes.class);
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
	public List<DropDownRes> getMotorMakeModelDropdown(MotorMakeModelGetAllReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query=  cb.createQuery(MotorMakeModelMaster.class);
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			// Find All
			Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("modelNameEn")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("modelId"),ocpm1.get("modelId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
			effectiveDate.where(a1,a2,a3);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorMakeModelMaster> ocpm2 = effectiveDate2.from(MotorMakeModelMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a4 = cb.equal(c.get("modelId"),ocpm2.get("modelId"));
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a6 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
			effectiveDate2.where(a4,a5,a6);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"),req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"),"99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n8 = cb.equal(c.get("makeId"),req.getMakeId());
			query.where(n1,n2,n3,n4,n7,n8).orderBy(orderList);
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorMakeModelMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getModelId().toString());
				res.setCodeDesc(data.getModelNameEn());
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

	@Override
	public SuccessRes changeStatusOfMakeModel(MakeModelChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeModelMaster> query = cb.createQuery(MotorMakeModelMaster.class);
			// Find all
			Root<MotorMakeModelMaster> b = query.from(MotorMakeModelMaster.class);
			//Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<OccupationMaster> ocpm1 = amendId.from(OccupationMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("occupationId"), b.get("occupationId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n4 = cb.equal(b.get("makeId"), req.getMakeId());
			Predicate n7 = cb.equal(b.get("modelId"),req.getModelId());
			Predicate n8 = cb.equal(b.get("bodyId"), req.getBodyId());
			
			query.where(n1,n2,n4,n6,n7,n8).orderBy(orderList);
			
			// Get Result 
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			MotorMakeModelMaster updateRecord = list.get(0);
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				MotorMakeModelMaster saveNew = new MotorMakeModelMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
		
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getModelId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}


}

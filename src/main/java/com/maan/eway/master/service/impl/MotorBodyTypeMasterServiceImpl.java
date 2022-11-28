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
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BodyTypeChangeStatusReq;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.req.MotorBodySaveReq;
import com.maan.eway.master.req.MotorBodyTypeGetAllReq;
import com.maan.eway.master.req.MotorBodyTypeGetReq;
import com.maan.eway.master.res.MotorBodyTypeGetRes;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.master.service.MotorBodyTypeMasterService;
import com.maan.eway.repository.MotorBodyTypeMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MotorBodyTypeMasterServiceImpl implements MotorBodyTypeMasterService {

	@Autowired
	private MotorBodyTypeMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorBodyTypeMasterServiceImpl.class);

	@Override
	public List<Error> validateMakeMotor(MotorBodySaveReq req) {

		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getBodyNameEn())) {
				errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En "));
			}
			else if (req.getBodyNameEn().length()>100) {
				errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En within 100 Characters "));
			}else if (StringUtils.isBlank(req.getBodyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorBodyTypeMaster> motorBodyList = getBodyNameExistDetails(req.getBodyNameEn() , req.getCompanyId() , req.getBranchCode());
				if (motorBodyList.size()>0 ) {
					errorList.add(new Error("01", "BodyNameEn", "This Body Name Already Exist "));
				}
			}else if (StringUtils.isNotBlank(req.getBodyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorBodyTypeMaster> motorBodyList = getBodyNameExistDetails(req.getBodyNameEn() , req.getCompanyId() , req.getBranchCode());
				
				if (motorBodyList.size()>0 &&  (! req.getBodyId().equalsIgnoreCase(motorBodyList.get(0).getBodyId().toString())) ) {
					errorList.add(new Error("01", "BodyNameEn", "This Body Name Already Exist "));
				}
			}	
			
			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList
						.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			// Status Validation
			 if (req.getStatus().length() > 1) {
				errorList.add(new Error("03", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()) || "P".equals(req.getStatus()) || "R".equals(req.getStatus()))) {
				errorList.add(new Error("03", "Status", "Enter Status Y or N Only"));
			}
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("04", "CompanyId", "Please Enter CompanyId"));
			} else if (req.getCompanyId().length() > 20) {
				errorList.add(new Error("04", "CompanyId", "CompanyId 20 Character Only"));
			}
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

		public List<MotorBodyTypeMaster> getBodyNameExistDetails(String name , String InsuranceId , String branchCode) {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			try {
				Date today = new Date();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

				// Find All
				Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
				Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
				amendId.where(a1,a2,a3,a4,a5);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(cb.lower( b.get("bodyNameEn")), name.toLowerCase());
				Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
				Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
				Predicate n5 = cb.equal(b.get("branchCode"), "99999");
				Predicate n6 = cb.or(n4,n5);
				query.where(n1,n2,n3,n6);
				
				// Get Result
				TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
				list = result.getResultList();		
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());

			}
			return list;
		}

	
		
	@Override
	public SuccessRes saveMakeMotor(MotorBodySaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorBodyTypeMaster saveData = new MotorBodyTypeMaster();
		List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String  end = "31/12/2022";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			String bodyId = "";

			if (StringUtils.isBlank(req.getBodyId())) {
				// Save
				// Long totalCount = repo.count();
				Long totalCount =  getMasterTableCount( req.getCompanyId() , req.getBranchCode());
				bodyId = Long.valueOf(totalCount + 1).toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(bodyId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				bodyId = req.getBodyId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

				// Find All
				Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
		/*		Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
				effectiveDate.where(a1, a2,a3);
*/
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				
				// Where
			//	Predicate n1 = cb.equal(b.get("status"), "Y");
				//Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n2 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n3 = cb.equal(b.get("bodyId"), req.getBodyId());
				Predicate n4 = cb.equal(b.get("sectionId"), req.getSectionId());
				query.where(n1, n2, n3 , n4).orderBy(orderList);;
				
				// Get Result
				TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
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
						MotorBodyTypeMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorBodyTypeMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
			
				res.setResponse("Updated Successfully ");
				res.setSuccessId(bodyId);

			}

			dozerMapper.map(req, saveData);
			saveData.setBodyId(Integer.valueOf(bodyId));
			saveData.setBodyNameEn(req.getBodyNameEn());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setEntryDate(entryDate);
			saveData.setAmendId(amendId);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCyclinders(req.getCylinders());
			repo.saveAndFlush(saveData);

			if (list.size() > 0) {
				// Update Old Record
				MotorBodyTypeMaster lastRecord = list.get(0);
				lastRecord.setEffectiveDateEnd(oldEndDate);
				repo.saveAndFlush(lastRecord);
			}

			log.info("Saved Details is ---> " + json.toJson(saveData));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public Long getMasterTableCount(String companyId , String branchCode) {

		Long data = 0L;
		try {

			List<Long> list = new ArrayList<Long>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.multiselect(cb.count(b));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a2,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("bodyId")));
		

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			list = result.getResultList();
			data = list.get(0);

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return data;
	}
	

	@Override
	public MotorBodyTypeGetRes getMotorBody(MotorBodyTypeGetReq req) {
		MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
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
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();

			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(c);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),c.get("branchCode"));

			amendId.where(a1,a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(c.get("branchCode"), "99999");
			Predicate n5 = cb.equal(c.get("bodyId"), req.getBodyId());
			Predicate n6 = cb.or(n3,n4);
			query.where(n1,n2,n5,n6).orderBy(orderList);
		
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			res = mapper.map(list.get(0), MotorBodyTypeGetRes.class);
			res.setBodyId(list.get(0).getBodyId());
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
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public List<MotorBodyTypeGetRes> getallMotorBody(MotorBodyTypeGetAllReq req) {
		List<MotorBodyTypeGetRes> resList = new ArrayList<MotorBodyTypeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Pagination
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			amendId.where(a1,a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			query.where(n1,n2,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
		
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			// Map
			for (MotorBodyTypeMaster data : list) {
				MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();

				res = mapper.map(data, MotorBodyTypeGetRes.class);
				res.setCylinders(data.getCyclinders());
				res.setTonnage(data.getTonnage());
				res.setSeatingCapacity(data.getSeatingCapacity());
				res.setRemarks(data.getRemarks());;
				res.setBodyId(data.getBodyId());
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
	public List<MotorBodyTypeGetRes> getactiveMotorBody(MotorBodyTypeGetAllReq req) {
		List<MotorBodyTypeGetRes> resList = new ArrayList<MotorBodyTypeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Pagination
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("sectionId"), req.getSectionId());
			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			// Map
			for (MotorBodyTypeMaster data : list) {
				MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();

				res = mapper.map(data, MotorBodyTypeGetRes.class);
				res.setBodyId(data.getBodyId());
				res.setCylinders(data.getCyclinders());
				res.setTonnage(data.getTonnage());
				res.setSeatingCapacity(data.getSeatingCapacity());
				res.setRemarks(data.getRemarks());
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
	public SuccessRes changeStatusOfBodyType(BodyTypeChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			
			// Find all
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);
			
			//Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"),b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where(a1,a2,a3);
			
			//Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			//where 
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("bodyId"),req.getBodyId());
			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			// Get Result 
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			MotorBodyTypeMaster updateRecord = list.get(0);
			
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				MotorBodyTypeMaster saveNew = new MotorBodyTypeMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
		
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getBodyId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getBodyTypeMasterDropdown(BodyTypeDropDownReq req) {
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
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("bodyId"), ocpm2.get("bodyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("sectionId"), req.getSectionId());
			Predicate n8 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			query.where(n1,n2,n3,n4,n7,n8).orderBy(orderList);
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorBodyTypeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getBodyId().toString());
				res.setCodeDesc(data.getBodyNameEn());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getInduvidualBodyTypeMasterDropdown(BodyTypeDropDownReq req) {
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
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("bodyId"), ocpm2.get("bodyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3, a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			query.where(n1,n2,n3,n4,n7).orderBy(orderList);
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorBodyTypeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getBodyId().toString());
				res.setCodeDesc(data.getBodyNameEn());
				res.setStatus(data.getStatus());
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

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
import com.maan.eway.bean.MotorColorMaster;

import com.maan.eway.error.Error;
import com.maan.eway.master.req.ColorChangeStatusReq;
import com.maan.eway.master.req.MotorColorGetAllReq;
import com.maan.eway.master.req.MotorColorGetReq;
import com.maan.eway.master.req.MotorColorSaveReq;
import com.maan.eway.master.res.MotorColorGetRes;

import com.maan.eway.master.service.MotorColorMasterService;
import com.maan.eway.repository.MotorColorMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MotorColorMasterServiceImpl implements MotorColorMasterService {

	@Autowired
	private MotorColorMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorColorMasterServiceImpl.class);

	@Override
	public List<Error> validateColorMotor(MotorColorSaveReq req) {

		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getColorCode())) {
				errorList.add(new Error("01", "Color Code", "Please Enter Color Code "));
			}
			else if (req.getColorCode().length()>100) {
				errorList.add(new Error("01", "Color Code", "Please Enter Color Code within 100 Characters "));
			}
			if (StringUtils.isBlank(req.getColorDesc())) {
				errorList.add(new Error("02", "Color Desc", "Please Enter Color Desc "));
			}
			else if (req.getColorDesc().length()>100) {
				errorList.add(new Error("02", "Color Desc", "Please Enter Color Desc within 100 Characters "));
			}else if (StringUtils.isBlank(req.getColorId()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorColorMaster> colorList = getColorDescExistDetails(req.getColorDesc() , req.getInsuranceId() , req.getBranchCode());
				if (colorList.size()>0 ) {
					errorList.add(new Error("01", "ColorDesc", "This Color Name Already Exist "));
				}
			}else if (StringUtils.isNotBlank(req.getColorId()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorColorMaster> colorList = getColorDescExistDetails(req.getColorDesc() , req.getInsuranceId() , req.getBranchCode());
				
				if (colorList.size()>0 &&  (! req.getColorId().equalsIgnoreCase(colorList.get(0).getColorId().toString())) ) {
					errorList.add(new Error("01", "ColorDesc", "This Color Name Already Exist "));
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
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("03", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList
						.add(new Error("03", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			// Status Validation
			 if (req.getStatus().length() > 1) {
				errorList.add(new Error("04", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()) || "P".equals(req.getStatus()) || "R".equals(req.getStatus()))) {
				errorList.add(new Error("04", "Status", "Enter Status Y or N Only"));
			}
			if (StringUtils.isBlank(req.getInsuranceId())) {
				errorList.add(new Error("05", "InsuranceId", "Please Enter InsuranceId"));
			}

			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("06", "BranchCode", "Please Select BranchCode"));
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<MotorColorMaster> getColorDescExistDetails(String colorDesc , String InsuranceId , String branchCode) {
		List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);

			// Find All
			Root<MotorColorMaster> b = query.from(MotorColorMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = amendId.from(MotorColorMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"), b.get("colorId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("colorDesc")), colorDesc.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			query.where(n1,n2,n3,n6);
			
			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}


	@Override
	public SuccessRes saveColor(MotorColorSaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorColorMaster saveData = new MotorColorMaster();
		List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
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
			
			String colorId = "";

			if (StringUtils.isBlank(req.getColorId())) {
				// Save
				// Long totalCount = repo.count();
				Integer totalCount = getMasterTableCount( req.getInsuranceId() , req.getBranchCode());
				colorId = Long.valueOf(totalCount + 1).toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(colorId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				colorId = req.getColorId().toString();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);

				// Find All
				Root<MotorColorMaster> b = query.from(MotorColorMaster.class);

				// Select
				query.select(b);

				/*// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<MotorColorMaster> ocpm1 = effectiveDate.from(MotorColorMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("colorId"), b.get("colorId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);

				effectiveDate.where(a1, a2);
*/
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
			//	Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("colorId"), req.getColorId());

				query.where(n1, n3).orderBy(orderList);

				// Get Result
				TypedQuery<MotorColorMaster> result = em.createQuery(query);
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
						MotorColorMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorColorMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				res.setResponse("Updated Successfully ");
				res.setSuccessId(colorId);

			}

			dozerMapper.map(req, saveData);
			saveData.setColorId(Integer.valueOf(colorId));
			saveData.setColorDesc(req.getColorDesc());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdateBy(req.getCreatedBy());
			saveData.setAmendId(amendId);
			repo.saveAndFlush(saveData);

			log.info("Saved Details is ---> " + json.toJson(saveData));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public Integer getMasterTableCount(String companyId , String branchCode) {

		Integer data = 0;
		try {

			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);

			// Find All
			Root<MotorColorMaster> b = query.from(MotorColorMaster.class);

			// Select
			//query.multiselect(cb.count(b));
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = effectiveDate.from(MotorColorMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"), b.get("colorId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
		
			effectiveDate.where(a1,a2,a3);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("colorId")));
		
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getColorId() : 0 ;
				} 
		catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return data;
	}

	@Override
	public MotorColorGetRes getMotorColor(MotorColorGetReq req) {
		MotorColorGetRes res = new MotorColorGetRes();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();

			// Find All
			Root<MotorColorMaster> c = query.from(MotorColorMaster.class);

			// Select
			query.select(c);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = amendId.from(MotorColorMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"), c.get("colorId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),c.get("branchCode"));
			amendId.where(a1, a2,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Where

			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("amendId"), amendId);
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("colorId"), req.getColorId());
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(c.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			query.where(n1,n2,n3,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColorId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorColorMaster :: getColorDesc ));
			
			res = mapper.map(list.get(0), MotorColorGetRes.class);
			res.setColorId(list.get(0).getColorId());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setRemarks(list.get(0).getRemarks());
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
	public List<MotorColorGetRes> getallMotorColor(MotorColorGetAllReq req) {
		List<MotorColorGetRes> resList = new ArrayList<MotorColorGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);

			// Find All
			Root<MotorColorMaster> b = query.from(MotorColorMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = amendId.from(MotorColorMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"), b.get("colorId"));
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
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			query.where(n1).orderBy(orderList);

			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColorId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorColorMaster :: getColorDesc ));
			
			// Map
			for (MotorColorMaster data : list) {
				MotorColorGetRes res = new MotorColorGetRes();

				res = mapper.map(data, MotorColorGetRes.class);
				res.setColorId(data.getColorId());
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
	public List<MotorColorGetRes> getactiveMotorColor(MotorColorGetAllReq req) {
		List<MotorColorGetRes> resList = new ArrayList<MotorColorGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
			// Pagination
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);

			// Find All
			Root<MotorColorMaster> b = query.from(MotorColorMaster.class);

			// Select
			query.select(b);


			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = amendId.from(MotorColorMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"), b.get("colorId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where(a1, a2,a3);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			query.where(n1,n2,n4,n6).orderBy(orderList);			query.where(n1).orderBy(orderList);

			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getColorId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorColorMaster :: getColorDesc ));
			
			
			// Map
			for (MotorColorMaster data : list) {
				MotorColorGetRes res = new MotorColorGetRes();

				res = mapper.map(data, MotorColorGetRes.class);
				res.setColorId(data.getColorId());
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
	public List<DropDownRes> getColorMasterDropdown(MotorColorGetAllReq req) {
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
			CriteriaQuery<MotorColorMaster> query=  cb.createQuery(MotorColorMaster.class);
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
			// Find All
			Root<MotorColorMaster> c = query.from(MotorColorMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = effectiveDate.from(MotorColorMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("colorCode"),ocpm1.get("colorCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			effectiveDate.where(a1,a2,a5,a6);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm2 = effectiveDate2.from(MotorColorMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("colorCode"),ocpm2.get("colorCode"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,a7,a8);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			query.where(n1,n2,n3,n4,n7).orderBy(orderList);
			// Get Result
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorColorMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getColorId().toString());
				res.setCodeDesc(data.getColorCode());
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
	public SuccessRes changeStatusOfColor(ColorChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = req.getEffectiveDateStart()!=null ? req.getEffectiveDateStart(): new Date();
			Calendar cal = new GregorianCalendar();
			MotorColorMaster updateRecord = new MotorColorMaster();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<MotorColorMaster> list = new ArrayList<MotorColorMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorColorMaster> query = cb.createQuery(MotorColorMaster.class);
			// Find all
			Root<MotorColorMaster> b = query.from(MotorColorMaster.class);
			//Select
			query.select(b);
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorColorMaster> ocpm1 = amendId.from(MotorColorMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("colorId"),b.get("colorId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"),today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where(a1,a2,a3,a4);
			//Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));


			//where 
			Predicate n1 = cb.equal(b.get("amendId"),amendId);
		
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("colorId"),req.getColorId());
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			
			query.where(n1,n2,n4,n6).orderBy(orderList);
		
			// Get Result 
			TypedQuery<MotorColorMaster> result = em.createQuery(query);
			list = result.getResultList();
			updateRecord = list.get(0);
			if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else {
				MotorColorMaster saveNew = new MotorColorMaster();
				dozerMapper.map(updateRecord,saveNew);
				saveNew.setBranchCode(req.getBranchCode());
				saveNew.setStatus(req.getStatus());
				repo.save(saveNew);
			}
			
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getColorId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

}

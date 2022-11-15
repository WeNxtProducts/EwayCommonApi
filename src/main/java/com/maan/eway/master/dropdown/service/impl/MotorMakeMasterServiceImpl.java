package com.maan.eway.master.dropdown.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.dropdown.req.MotorMakeGetAllReq;
import com.maan.eway.master.dropdown.req.MotorMakeGetReq;
import com.maan.eway.master.dropdown.req.MotorMakeSaveReq;
import com.maan.eway.master.dropdown.res.MotorMakeGetRes;
import com.maan.eway.master.dropdown.service.MotorMakeMasterService;
import com.maan.eway.repository.MotorMakeMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class MotorMakeMasterServiceImpl implements MotorMakeMasterService {

	@Autowired
	private MotorMakeMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorMakeMasterServiceImpl.class);
	@Override
	public List<DropDownRes> getMotorMakeDropdown() {
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
			CriteriaQuery<MotorMakeMaster> query=  cb.createQuery(MotorMakeMaster.class);
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			// Find All
			Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("makeId")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm2 = effectiveDate2.from(MotorMakeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3).orderBy(orderList);
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorMakeMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getMakeId().toString());
				res.setCodeDesc(data.getMakeNameEn());
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

	/*
	@Override
	public List<Error> validateMakeMotor(MotorMakeSaveReq req) {

		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getMakeNameEn())) {
				errorList.add(new Error("01", "Make Name En", "Please Enter Make Name En "));
			}
			else if (req.getMakeNameEn().length()>100) {
				errorList.add(new Error("01", "Make Name En", "Please Enter Make Name En within 100 Characters "));
			}
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			// Status Validation
			 if (req.getStatus().length() > 1) {
				errorList.add(new Error("03", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("03", "Status", "Enter Status Y or N Only"));
			}
			 if (req.getColorDesc().length()>100) {
					errorList.add(new Error("04", "Color Desc", "Please Enter Color Desc within 100 Characters "));
				}
			 
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes saveMakeMotor(MotorMakeSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorMakeMaster saveData = new MotorMakeMaster();
		List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Calendar cal = new GregorianCalendar();
			cal.setTime(req.getEffectiveDateStart());  cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime() ;
			Date today = new Date();
			cal.setTime(req.getEffectiveDateStart());   cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes());
			Date oldEndDate = cal.getTime() ;
			cal.setTime(req.getEffectiveDateStart());  cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes()) ;
			Date effDate = cal.getTime();
			Date endDate = sdformat.parse("12/12/2050");
			
			String makeId = "";

			if (StringUtils.isBlank(req.getMakeId())) {
				// Save
				// Long totalCount = repo.count();
				Long totalCount = getMasterTableCount();
				makeId = Long.valueOf(totalCount + 1).toString();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(makeId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				makeId = req.getMakeId().toString();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

				// Find All
				Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				
				effectiveDate.where(a1, a2);


				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("makeId"), req.getMakeId());

				query.where(n1, n2, n3);
				
				// Get Result
				TypedQuery<MotorMakeMaster> result = em.createQuery(query);
				list = result.getResultList();

				if (list.size() > 0) {
					repo.delete(list.get(0));
				}
			
				res.setResponse("Updated Successfully ");
				res.setSuccessId(makeId);

			}

			dozerMapper.map(req, saveData);
			saveData.setMakeId(Integer.valueOf(makeId));
			saveData.setMakeNameEn(req.getMakeNameEn());
			saveData.setEffectiveDateStart(req.getEffectiveDateStart());
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus("Y");
			saveData.setEntryDate(new Date());
			saveData.setAmendId(0);
			repo.saveAndFlush(saveData);

			if (list.size() > 0) {
				// Update Old Record
				MotorMakeMaster lastRecord = list.get(0);
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

	public Long getMasterTableCount() {

		Long data = 0L;
		try {

			List<Long> list = new ArrayList<Long>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.multiselect(cb.count(b));

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			effectiveDate.where(a1);

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			query.where(n1);
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
	public MotorMakeGetRes getMakeId(MotorMakeGetReq req) {
		MotorMakeGetRes res = new MotorMakeGetRes();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();

			// Find All
			Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);

			// Select
			query.select(c);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("makeId"), ocpm1.get("makeId"));

			effectiveDate.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("effectiveDateStart")));

			// Where

			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("makeId"), req.getMakeId());

			query.where(n1, n2).orderBy(orderList);

			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = mapper.map(list.get(0), MotorMakeGetRes.class);
			res.setMakeId(list.get(0).getMakeId());
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

	@Override
	public List<MotorMakeGetRes> getallMotorMake(MotorMakeGetAllReq req) {
		List<MotorMakeGetRes> resList = new ArrayList<MotorMakeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));

			effectiveDate.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("makeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);

			query.where(n1).orderBy(orderList);

			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			for (MotorMakeMaster data : list) {
				MotorMakeGetRes res = new MotorMakeGetRes();

				res = mapper.map(data, MotorMakeGetRes.class);
				res.setMakeId(data.getMakeId());
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
	public List<MotorMakeGetRes> getactiveMotorMake(MotorMakeGetAllReq req) {
		List<MotorMakeGetRes> resList = new ArrayList<MotorMakeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));

			effectiveDate.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("makeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("status"), "Y");

			query.where(n1,n2).orderBy(orderList);

			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			for (MotorMakeMaster data : list) {
				MotorMakeGetRes res = new MotorMakeGetRes();

				res = mapper.map(data, MotorMakeGetRes.class);
				res.setMakeId(data.getMakeId());
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
	public SuccessRes changeStatusOfMotorMake(MotorMakeChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		try {
			Date today = req.getEffectiveDateStart()!=null ? req.getEffectiveDateStart(): new Date();
			Calendar cal = new GregorianCalendar();
			MotorMakeMaster updateRecord = new MotorMakeMaster();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
			// Find all
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);
			//Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"),b.get("makeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"),today);
			effectiveDate.where(a1,a2);
			//Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//where 
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("makeId"),req.getMakeId());
			query.where(n1,n2).orderBy(orderList);
			// Get Result 
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			updateRecord = list.get(0);
			
			if(req.getStatus().equalsIgnoreCase("N")) {
				// Delete Old Records
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 30);
				today = cal.getTime();
				// Create Update
				CriteriaDelete<MotorMakeMaster> delete = cb.createCriteriaDelete(MotorMakeMaster.class);
				Root<MotorMakeMaster> pm = delete.from(MotorMakeMaster.class);
				// Where
				
				Predicate n3 = cb.equal(pm.get("makeId"), req.getMakeId());
				Predicate n4 = cb.greaterThanOrEqualTo(pm.get("effectiveDateStart"),today);
				delete.where(n3,n4);
				em.createQuery(delete).executeUpdate();
				// Insert Update Record
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			}
			else if(req.getStatus().equalsIgnoreCase("Y")) {
				// Insert Update Record
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
				}
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getMakeId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}



	*/
}

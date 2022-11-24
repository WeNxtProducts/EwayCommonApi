package com.maan.eway.master.service.impl;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.service.MotorBodyTypeMasterService;
import com.maan.eway.repository.MotorBodyTypeMasterRepository;
import com.maan.eway.res.DropDownRes;

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
	public List<DropDownRes> getBodyTypeMasterDropdown(BodyTypeDropDownReq req) {
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
			CriteriaQuery<MotorBodyTypeMaster> query=  cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("bodyNameEn")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("bodyId"),ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("bodyId"),ocpm2.get("bodyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("sectionId"),req.getSectionId());	
			query.where(n1,n2,n3,n4).orderBy(orderList);
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
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return resList;
		}
	@Override
	public List<DropDownRes> getInduvidualBodyTypeMasterDropdown() {
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
			CriteriaQuery<MotorBodyTypeMaster> query=  cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("bodyNameEn")));
			
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("bodyId"),ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("bodyId"),ocpm2.get("bodyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3).orderBy(orderList);
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
	public List<Error> validateMakeMotor(MotorBodySaveReq req) {

		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getBodyNameEn())) {
				errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En "));
			}
			else if (req.getBodyNameEn().length()>100) {
				errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En within 100 Characters "));
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
				errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList
						.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			// Status Validation
			 if (req.getStatus().length() > 1) {
				errorList.add(new Error("03", "Status", "Status 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("03", "Status", "Enter Status Y or N Only"));
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes saveMakeMotor(MotorBodySaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorBodyTypeMaster saveData = new MotorBodyTypeMaster();
		List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Calendar cal = new GregorianCalendar();
			cal.setTime(req.getEffectiveDateStart());cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime();
			Date today = new Date();
			cal.setTime(req.getEffectiveDateStart());   cal.set(Calendar.HOUR_OF_DAY, today.getHours()); cal.set(Calendar.MINUTE, today.getMinutes());
			Date oldEndDate = cal.getTime();
			cal.setTime(req.getEffectiveDateStart());
			cal.set(Calendar.HOUR_OF_DAY, today.getHours());cal.set(Calendar.MINUTE, today.getMinutes());
			Date effDate = cal.getTime();
			Date endDate = req.getEffectiveDateEnd();

			String bodyId = "";

			if (StringUtils.isBlank(req.getBodyId())) {
				// Save
				// Long totalCount = repo.count();
				Long totalCount = getMasterTableCount();
				bodyId = Long.valueOf(totalCount + 1).toString();
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
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
				effectiveDate.where(a1, a2,a3);


				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("bodyId"), req.getBodyId());
				Predicate n4 = cb.equal(b.get("sectionId"), req.getSectionId());
				query.where(n1, n2, n3 , n4);
				
				// Get Result
				TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
				list = result.getResultList();

				if (list.size() > 0) {
					repo.delete(list.get(0));
				}
			
				res.setResponse("Updated Successfully ");
				res.setSuccessId(bodyId);

			}

			dozerMapper.map(req, saveData);
			saveData.setBodyId(Integer.valueOf(bodyId));
			saveData.setBodyNameEn(req.getBodyNameEn());
			saveData.setEffectiveDateStart(req.getEffectiveDateStart());
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus("Y");
			saveData.setEntryDate(new Date());
			saveData.setAmendId(0);
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

	public Long getMasterTableCount() {

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
	public MotorBodyTypeGetRes getMotorBody(MotorBodyTypeGetReq req) {
		MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();

			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(c);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			javax.persistence.criteria.Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));

			effectiveDate.where(a1);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("effectiveDateStart")));

			// Where

			javax.persistence.criteria.Predicate n1 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			javax.persistence.criteria.Predicate n2 = cb.equal(c.get("bodyId"), req.getBodyId());

			query.where(n1, n2).orderBy(orderList);

			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
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
	@Override
	public List<MotorBodyTypeGetRes> getallMotorBody(MotorBodyTypeGetAllReq req) {
		List<MotorBodyTypeGetRes> resList = new ArrayList<MotorBodyTypeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			effectiveDate.where(a1,a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("bodyId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("sectionId"), req.getSectionId());
			query.where(n1,n2).orderBy(orderList);

			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

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
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			effectiveDate.where(a1,a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("bodyId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("status"), "Y");
			Predicate n3 = cb.equal(b.get("sectionId"), req.getSectionId());
			query.where(n1,n2).orderBy(orderList);

			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

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
		try {
			Date today = req.getEffectiveDateStart()!=null ? req.getEffectiveDateStart(): new Date();
			Calendar cal = new GregorianCalendar();
			MotorBodyTypeMaster updateRecord = new MotorBodyTypeMaster();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			// Find all
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);
			//Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"),b.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"),today);
			effectiveDate.where(a1,a2);
			//Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//where 
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("bodyId"),req.getBodyId());
			query.where(n1,n2).orderBy(orderList);
			// Get Result 
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			updateRecord = list.get(0);
			
			if(req.getStatus().equalsIgnoreCase("N")) {
				// Delete Old Records
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 30);
				today = cal.getTime();
				// Create Update
				CriteriaDelete<MotorBodyTypeMaster> delete = cb.createCriteriaDelete(MotorBodyTypeMaster.class);
				Root<MotorBodyTypeMaster> pm = delete.from(MotorBodyTypeMaster.class);
				// Where
				
				Predicate n3 = cb.equal(pm.get("bodyId"), req.getBodyId());
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
			res.setSuccessId(req.getBodyId());
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

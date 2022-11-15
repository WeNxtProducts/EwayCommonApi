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
import com.maan.eway.bean.CityMaster;
import com.maan.eway.bean.ExchangeMaster;
import com.maan.eway.bean.OccupationMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.ExchangeChangeStatusReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterGetReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterGetallReq;
import com.maan.eway.master.dropdown.req.ExchangeMasterSaveReq;
import com.maan.eway.master.dropdown.res.CityMasterRes;
import com.maan.eway.master.dropdown.res.ExchangeMasterGetRes;
import com.maan.eway.master.dropdown.service.ExchangeMasterService;
import com.maan.eway.repository.ExchangeMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class ExchangeMasterServiceImpl implements ExchangeMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private ExchangeMasterRepository repo;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(ExchangeMasterServiceImpl.class);

	@Override
	public List<DropDownRes> getExchangeMasterDropdown() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find All
			Root<ExchangeMaster> c = query.from(ExchangeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("exchangeId")));

			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(c.get("exchangeId"),ocpm1.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm2 = effectiveDate2.from(ExchangeMaster.class);
			effectiveDate2.select(cb.max(ocpm2.get("effectiveDateEnd")));
			Predicate a3 = cb.equal(c.get("exchangeId"),ocpm2.get("exchangeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3).orderBy(orderList);
	// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (ExchangeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getExchangeId().toString());
				res.setCodeDesc(data.getCurrencyId());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details -->" + e.getMessage());
			return null;
		}
		return resList;
	}


/*	
	
	@Transactional
	@Override
	public List<Error> validateInsertExchangeMaster(ExchangeMasterSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("03", "Remark", "Please Select Remark "));
			} else if (req.getRemarks().length() > 100) {
				errorList.add(new Error("03", "Remark", "Please Enter Remark within 100 Characters"));
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
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList
						.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Enter Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Enter Status in 1 Character Only"));
			} else if (!("Y".equals(req.getStatus()) || "N".equals(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Enter Status in Y or N Only"));
			}
			if (StringUtils.isBlank(req.getExchangeRate())) {
				errorList.add(new Error("06", "ExchangeRate", "Please Enter ExchangeRate"));
			}
			if (StringUtils.isBlank(req.getCurrencyId())) {
				errorList.add(new Error("07", "CurrencyId", "Please Enter CurrencyId"));
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("08", "CompanyId", "Please Enter CompanyId"));
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Transactional
	@Override
	public SuccessRes insertExchangeMaster(ExchangeMasterSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ExchangeMaster saveData = new ExchangeMaster();
		List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Calendar cal = new GregorianCalendar();
			cal.setTime(req.getEffectiveDateStart());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime();
			Date today = new Date();
			cal.setTime(req.getEffectiveDateStart());
			cal.set(Calendar.HOUR_OF_DAY, today.getHours());
			cal.set(Calendar.MINUTE, today.getMinutes());
			Date oldEndDate = cal.getTime();
			cal.setTime(req.getEffectiveDateStart());
			cal.set(Calendar.HOUR_OF_DAY, today.getHours());
			cal.set(Calendar.MINUTE, today.getMinutes());
			Date effDate = cal.getTime();
			Date endDate = req.getEffectiveDateEnd();

			String exchangeId = "";
			if (StringUtils.isBlank(req.getExchangeId().toString())) {
				// Save
				Long totalCount = getMasterTableCount();
				exchangeId = Long.valueOf(totalCount + 1).toString();
				res.setResponse("Saved Successfully");
				res.setSuccessId(exchangeId);
			} else {
				// Update
				exchangeId = req.getExchangeId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
				// Find all
				Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
				// Select
				query.select(b);
				// Effective Date Max Filter
				Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
				effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
				Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));

				effectiveDate.where(a1, a2, a3);

				// Where
				Predicate n1 = cb.equal(b.get("status"), "Y");
				Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("exchangeId"), req.getExchangeId());
				Predicate n4 = cb.equal(b.get("companyId"), req.getCompanyId());

				query.where(n1, n2, n3, n4);

				// Get Result
				TypedQuery<ExchangeMaster> result = em.createQuery(query);
				list = result.getResultList();
				if (list.size() > 0) {
					repo.delete(list.get(0));
					// Amend Id

					if (list.get(0).getEffectiveDateStart().before(startDate)) {
						String startDatewithoutTime = sdformat.format(startDate);
						String oldDatewithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());

						if (startDatewithoutTime.equalsIgnoreCase(oldDatewithoutTime))
							;
						{
							amendId = list.get(0).getAmendId() + 1;
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(exchangeId);
			}
			dozerMapper.map(req, saveData);
			saveData.setExchangeId(Integer.valueOf(exchangeId));
			saveData.setEffectiveDateStart(effDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus(req.getStatus());
			saveData.setEntryDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setSNo(Integer.valueOf(exchangeId));
			repo.saveAndFlush(saveData);
			if (list.size() > 0) {
				// Update Old Record
				ExchangeMaster lastRecord = list.get(0);
				lastRecord.setEffectiveDateEnd(oldEndDate);
				String startDatewithoutTime = sdformat.format(startDate);
				String oldDatewithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());

				if (startDatewithoutTime.equalsIgnoreCase(oldDatewithoutTime)) {
					lastRecord.setStatus("N");
				}
				repo.saveAndFlush(lastRecord);

			}

			log.info("Saved Details is --> " + json.toJson(saveData));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
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
			// Find all
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
			// Select
			query.multiselect(cb.count(b));
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
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
	public ExchangeMasterGetRes getExchangeMaster(ExchangeMasterGetReq req) {
		ExchangeMasterGetRes res = new ExchangeMasterGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = req.getEffectiveDateStart() != null ? req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a2 = cb.equal(b.get("companyId"),b.get("companyId"));

			effectiveDate.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("exchangeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("exchangeId"), req.getExchangeId());

			query.where(n1, n2).orderBy(orderList);

			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);

			list = result.getResultList();

			res = mapper.map(list.get(0), ExchangeMasterGetRes.class);
			res.setExchangeId(list.get(0).getExchangeId().toString());
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
	public List<ExchangeMasterGetRes> getallExchangeMaster(ExchangeMasterGetallReq req) {
		List<ExchangeMasterGetRes> resList = new ArrayList<ExchangeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = req.getEffectiveDateStart() != null ? req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("exchangeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);

			query.where(n1).orderBy(orderList);

			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			for (ExchangeMaster data : list) {
				ExchangeMasterGetRes res = new ExchangeMasterGetRes();

				res = mapper.map(data, ExchangeMasterGetRes.class);
				res.setExchangeId(data.getExchangeId().toString());
				res.setCompanyId(data.getCompanyId());
				res.setCurrencyId(data.getCurrencyId());
				res.setExchangeRate(data.getExchangeRate().toString());
				;
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
	public List<ExchangeMasterGetRes> getActiveExchange(ExchangeMasterGetallReq req) {
		List<ExchangeMasterGetRes> resList = new ArrayList<ExchangeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = req.getEffectiveDateStart() != null ? req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);

			// Find All
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("exchangeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("status"), "Y");

			query.where(n1, n2).orderBy(orderList);

			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			// Map
			for (ExchangeMaster data : list) {
				ExchangeMasterGetRes res = new ExchangeMasterGetRes();

				res = mapper.map(data, ExchangeMasterGetRes.class);
				res.setExchangeId(data.getExchangeId().toString());
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
	public SuccessRes changeStatusOfExchange(ExchangeChangeStatusReq req) {
		SuccessRes res = new SuccessRes();
		try {
			Date today = req.getEffectiveDateStart() != null ? req.getEffectiveDateStart() : new Date();
			Calendar cal = new GregorianCalendar();
			ExchangeMaster updateRecord = new ExchangeMaster();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<ExchangeMaster> list = new ArrayList<ExchangeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ExchangeMaster> query = cb.createQuery(ExchangeMaster.class);
			// Find all
			Root<ExchangeMaster> b = query.from(ExchangeMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<ExchangeMaster> ocpm1 = effectiveDate.from(ExchangeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("exchangeId"), b.get("exchangeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("exchangeId"), Integer.valueOf(req.getExchangeId()));
			query.where(n1, n2).orderBy(orderList);
			// Get Result
			TypedQuery<ExchangeMaster> result = em.createQuery(query);
			list = result.getResultList();
			updateRecord = list.get(0);
			if (req.getStatus().equalsIgnoreCase("N")) {
				// Delete Old Records
				cal.setTime(today);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 30);
				today = cal.getTime();
				// Create Update
				CriteriaDelete<ExchangeMaster> delete = cb.createCriteriaDelete(ExchangeMaster.class);
				;
				Root<ExchangeMaster> pm = delete.from(ExchangeMaster.class);
				// Where
				Predicate n3 = cb.equal(pm.get("exchangeId"), req.getExchangeId());
				Predicate n4 = cb.greaterThanOrEqualTo(pm.get("effectiveDateStart"), today);
				delete.where(n3, n4);
				em.createQuery(delete).executeUpdate();
				// Insert Update Record
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			} else if (req.getStatus().equalsIgnoreCase("Y")) {

				// Insert Update Record
				updateRecord.setStatus(req.getStatus());
				repo.save(updateRecord);
			}
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getExchangeId());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details -->" + e.getMessage());
			return null;
		}
		return res;
	}
*/
}

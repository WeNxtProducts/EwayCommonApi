package com.maan.eway.master.dropdown.service.impl;

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
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.dropdown.req.MotorMakeModelGetAllReq;
import com.maan.eway.master.dropdown.req.MotorMakeModelGetReq;
import com.maan.eway.master.dropdown.req.MotorMakeModelSaveReq;
import com.maan.eway.master.dropdown.res.MotorColorGetRes;
import com.maan.eway.master.dropdown.res.MotorMakeModelGetRes;
import com.maan.eway.master.dropdown.service.MotorMakeModelMasterService;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SuccessRes saveMotorMakeModel(MotorMakeModelSaveReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MotorMakeModelGetRes getMotorMakeModel(MotorMakeModelGetReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MotorMakeModelGetRes> getallMotorMakeModel(MotorMakeModelGetAllReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MotorColorGetRes> getactiveMakeModel(MotorMakeModelGetAllReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DropDownRes> getMotorMakeModelDropdown( String makeId ) {
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
			Predicate n4 = cb.equal(c.get("makeId"),makeId);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			// Get Result
			TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (MotorMakeModelMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getModelId().toString());
				res.setCodeDesc(data.getModelNameEn());
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

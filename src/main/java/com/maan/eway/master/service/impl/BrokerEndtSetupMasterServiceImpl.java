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
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.BrokerEndtSetupMaster;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerEndtSetupMasterChangeStatusReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterGetallReq;
import com.maan.eway.master.req.BrokerEndtSetupMasterSaveReq;
import com.maan.eway.master.res.BrokerEndtSetupMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.master.service.BrokerEndtSetupMasterService;
import com.maan.eway.repository.BrokerEndtSetupMasterRepository;
import com.maan.eway.res.SuccessRes;

@Service
@Transactional
public class BrokerEndtSetupMasterServiceImpl  implements BrokerEndtSetupMasterService{

	@Autowired
	private BrokerEndtSetupMasterRepository repo;
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(EndorsementMasterServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	
	@Override
	public List<Error> validateBrokerEndtSetup(BrokerEndtSetupMasterSaveReq req) {
		// TODO Auto-generated method stub
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("01", "ProductId", "Please Enter ProductId"));
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("04", "Remarks", "Please Enter Remarks "));
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
				errorList.add(new Error("05", "Status", "Please Select Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}
		
			
			if (req.getEndtTypes().isEmpty()){
				errorList.add(new Error("14", "EndtTypes", "Please Select EndtTypes"));
				}
			
			
			if (StringUtils.isBlank(req.getAgencyCode())) {
				errorList.add(new Error("16", "AgencyCode", "Please Enter AgencyCode"));
			}
			if (StringUtils.isBlank(req.getOaCode())) {
				errorList.add(new Error("17", "OA Code", "Please Enter OA Code"));
			}
			if (StringUtils.isBlank(req.getUserType())) {
				errorList.add(new Error("18", "UserType", "Please Enter UserType"));
			}
			if (StringUtils.isBlank(req.getSubUserType())) {
				errorList.add(new Error("19", "SubUserType", "Please Enter SubUserType"));
			}
			
			if (StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("15", "LoginId", "Please Enter LoginId"));
			}
			else if (req.getLoginId().length() > 100){
				errorList.add(new Error("15","Login Id", "Please Enter LoginId  within 100 Characters")); 
			}else if (StringUtils.isBlank(req.getEndtSetupId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())) {
				List<BrokerEndtSetupMaster> loginList = getLoginIdExistDetails(req.getLoginId() , req.getCompanyId() ,  req.getProductId());
				if (loginList.size()>0 ) {
					errorList.add(new Error("01", "Login Id", "This Login Id Already Exist "));
				}
			}
			/*
			else if (StringUtils.isNotBlank(req.getEndtSetupId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())) {
				List<BrokerEndtSetupMaster> loginList = getLoginIdExistDetails(req.getLoginId() , req.getCompanyId() ,req.getProductId());
				
				if (loginList.size()>0 &&  (! req.getEndtSetupId().equalsIgnoreCase(loginList.get(0).getLoginId().toString())) ) {
					errorList.add(new Error("01", "Login Id", "This Login Id Already Exist "));
				}
				
			}
			*/
	} catch (Exception e) {
		log.error(e);
		e.printStackTrace();
	}
	return errorList;
}


	private List<BrokerEndtSetupMaster> getLoginIdExistDetails(String loginId, String companyId, String productId) {
		// TODO Auto-generated method stub
		List<BrokerEndtSetupMaster> list = new ArrayList<BrokerEndtSetupMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);

			// Find All
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerEndtSetupMaster> ocpm1 = amendId.from(BrokerEndtSetupMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("endtSetupId"), b.get("endtSetupId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			
			amendId.where(a1,a2,a3);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("loginId")), loginId.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			
			query.where(n1,n2,n3,n4);
			
			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}


	@Override
	public SuccessRes saveBrokerEndtSetup(BrokerEndtSetupMasterSaveReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerEndtSetupMaster saveData = new BrokerEndtSetupMaster();
		List<BrokerEndtSetupMaster> list  = new ArrayList<BrokerEndtSetupMaster>();
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
			Integer endtSetupId = 0;

			if(StringUtils.isBlank(req.getEndtSetupId())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId());
				endtSetupId = totalCount+1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(endtSetupId.toString());
			}

			else {
				endtSetupId = Integer.valueOf(req.getEndtSetupId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);
				//Findall
				Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				//Where
				Predicate n1 = cb.equal(b.get("endtSetupId"),req.getEndtSetupId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n4 = cb.equal(b.get("loginId"), req.getLoginId());	
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
				// Get Result
				TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);
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
						BrokerEndtSetupMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if(list.size()>1) {
							BrokerEndtSetupMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(endtSetupId.toString());
			}
		
			dozerMapper.map(req, saveData);
			saveData.setEndtSetupId(endtSetupId.toString());
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setProductId(req.getProductId());
			saveData.setAgencyCode(Integer.valueOf(req.getAgencyCode()));	
			saveData.setOaCode(Integer.valueOf(req.getOaCode()));	
			
			
			String id = "";
			List<String> ids = req.getEndtTypes();
			for (int i = 0; i < ids.size(); i++) {
				id = id + "," + ids.get(i);
			}

			
			id=id.substring(1);
			saveData.setEndtTypes(id);
			
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
	public Integer getMasterTableCount(String companyId,  String productId)	{

		Integer data =0;
		try {
			List<BrokerEndtSetupMaster> list = new ArrayList<BrokerEndtSetupMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);
			//Find all
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<BrokerEndtSetupMaster> ocpm1 = effectiveDate.from(BrokerEndtSetupMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("endtSetupId"),b.get("endtSetupId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			effectiveDate.where(a1,a2,a3);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("endtSetupId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? Integer.valueOf(list.get(0).getEndtSetupId()) : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}

			
	@Override
	public List<BrokerEndtSetupMasterGetallRes> getallBrokerEndtSetup(BrokerEndtSetupMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<BrokerEndtSetupMasterGetallRes> resList = new ArrayList<BrokerEndtSetupMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerEndtSetupMaster> list = new ArrayList<BrokerEndtSetupMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);

			// Find All
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerEndtSetupMaster> ocpm1 = amendId.from(BrokerEndtSetupMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("userType"), b.get("userType"));
			Predicate a4 = cb.equal(ocpm1.get("subUserType"), b.get("subUserType"));
			Predicate a5 = cb.equal(ocpm1.get("endtSetupId"), b.get("endtSetupId"));
			Predicate a6 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtSetupId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("userType"),req.getUserType());
			Predicate n5 = cb.equal(b.get("subUserType"),req.getSubUserType());
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtSetupId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerEndtSetupMaster :: getEndtSetupId ));

			if(list.size()>0 && list!=null) {
			for(BrokerEndtSetupMaster data : list) {
			BrokerEndtSetupMasterGetallRes res = new BrokerEndtSetupMasterGetallRes();

			String dependentid = data.getEndtTypes();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtTypes(dependentids);
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setEndtSetupId(data.getEndtSetupId());
			res.setLoginId(data.getLoginId());
			res.setCompanyId(data.getCompanyId());
			res.setProductId(data.getProductId());
			res.setAgencyCode(data.getAgencyCode().toString());
			res.setOaCode(data.getOaCode().toString());
			res.setStatus(data.getStatus());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRemarks(data.getRemarks());
			res.setUserType(data.getUserType());
			res.setSubUserType(data.getSubUserType());
			res.setUpdatedDate(data.getUpdatedDate());
			resList.add(res);
			}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return resList;
	}
	@Override
	public List<BrokerEndtSetupMasterGetallRes> getActiveBrokerEndtSetup(BrokerEndtSetupMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<BrokerEndtSetupMasterGetallRes> resList = new ArrayList<BrokerEndtSetupMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerEndtSetupMaster> list = new ArrayList<BrokerEndtSetupMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);

			// Find All
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerEndtSetupMaster> ocpm1 = amendId.from(BrokerEndtSetupMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("userType"), b.get("userType"));
			Predicate a4 = cb.equal(ocpm1.get("subUserType"), b.get("subUserType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("endtSetupId"), b.get("endtSetupId"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtSetupId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("userType"),req.getUserType());
			Predicate n5 = cb.equal(b.get("subUserType"),req.getSubUserType());
			Predicate n6 = cb.equal(b.get("status"),"Y");
			
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtSetupId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerEndtSetupMaster :: getEndtSetupId ));
			if(list.size()>0 && list!=null) {
			for(BrokerEndtSetupMaster data : list) {
			BrokerEndtSetupMasterGetallRes res = new BrokerEndtSetupMasterGetallRes();

			String dependentid = data.getEndtTypes();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtTypes(dependentids);
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setEndtSetupId(data.getEndtSetupId());
			res.setLoginId(data.getLoginId());
			res.setCompanyId(data.getCompanyId());
			res.setProductId(data.getProductId());
			res.setAgencyCode(data.getAgencyCode().toString());
			res.setOaCode(data.getOaCode().toString());
			res.setStatus(data.getStatus());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRemarks(data.getRemarks());
			res.setUserType(data.getUserType());
			res.setSubUserType(data.getSubUserType());
			res.setUpdatedDate(data.getUpdatedDate());

			resList.add(res);
			}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return resList;
	}

	@Override
	public BrokerEndtSetupMasterGetallRes getBybrokerEndtSetupid(BrokerEndtSetupMasterGetReq req) {
		// TODO Auto-generated method stub
		BrokerEndtSetupMasterGetallRes res = new BrokerEndtSetupMasterGetallRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerEndtSetupMaster> list = new ArrayList<BrokerEndtSetupMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);

			// Find All
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerEndtSetupMaster> ocpm1 = amendId.from(BrokerEndtSetupMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("endtSetupId"), b.get("endtSetupId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a5 = cb.equal(ocpm1.get("userType"), b.get("userType"));
			Predicate a6 = cb.equal(ocpm1.get("subUserType"), b.get("subUserType"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtSetupId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtSetupId"),req.getEndtSetupId());
			Predicate n5 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n6 = cb.equal(b.get("userType"),req.getUserType());
			Predicate n7 = cb.equal(b.get("subUserType"),req.getSubUserType());
			
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtSetupId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerEndtSetupMaster :: getEndtSetupId ));

			if(list!=null && list.size()>0) {
				String dependentid = list.get(0).getEndtTypes();
				List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
				res.setEndtTypes(dependentids);
			
				res.setAmendId(list.get(0).getAmendId().toString());
				res.setEntryDate(list.get(0).getEntryDate());
				res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
				res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
				res.setEndtSetupId(list.get(0).getEndtSetupId());
				res.setLoginId(list.get(0).getLoginId());
				res.setCompanyId(list.get(0).getCompanyId());
				res.setProductId(list.get(0).getProductId());
				res.setAgencyCode(list.get(0).getAgencyCode().toString());
				res.setOaCode(list.get(0).getOaCode().toString());
				res.setStatus(list.get(0).getStatus());
				res.setCreatedBy(list.get(0).getCreatedBy());
				res.setUpdatedBy(list.get(0).getUpdatedBy());
				res.setUpdatedDate(list.get(0).getUpdatedDate());
				res.setRemarks(list.get(0).getRemarks());
				res.setUserType(list.get(0).getUserType());
				res.setSubUserType(list.get(0).getSubUserType());
				
			}
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
			
	@Override
	public SuccessRes changeStatusOfBrokerEndtSetup(BrokerEndtSetupMasterChangeStatusReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerEndtSetupMaster saveData = new BrokerEndtSetupMaster();
		List<BrokerEndtSetupMaster> list  = new ArrayList<BrokerEndtSetupMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			Integer endtSetupId = 0;

			endtSetupId = Integer.valueOf(req.getEndtSetupId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerEndtSetupMaster> query = cb.createQuery(BrokerEndtSetupMaster.class);
			// Findall
			Root<BrokerEndtSetupMaster> b = query.from(BrokerEndtSetupMaster.class);
			// select
			query.select(b);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("endtSetupId"), req.getEndtSetupId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("loginId"), req.getLoginId());
			Predicate n5 = cb.equal(b.get("userType"), req.getUserType());
			Predicate n6 = cb.equal(b.get("subUserType"), req.getSubUserType());
			
			query.where(n1, n2, n3, n4,n5,n6).orderBy(orderList);

			// Get Result
			TypedQuery<BrokerEndtSetupMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					BrokerEndtSetupMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
			//			EndtTypeMaster lastRecord = list.get(1);
			//			lastRecord.setEffectiveDateEnd(oldEndDate);
			//			repo.saveAndFlush(lastRecord);
					
				}
			
			res.setResponse("Updated Successfully");
			res.setSuccessId(endtSetupId.toString());

			dozerMapper.map(list.get(0), saveData);
			
			saveData.setEndtSetupId(endtSetupId.toString());
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(list.get(0).getCompanyId());
			saveData.setProductId(req.getProductId());
			saveData.setAgencyCode(list.get(0).getAgencyCode());
			
			
			repo.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			res.setResponse("Status Changed");
			res.setSuccessId(req.getEndtSetupId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

}

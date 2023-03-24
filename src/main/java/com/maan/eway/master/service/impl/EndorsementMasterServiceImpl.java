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
import javax.persistence.criteria.Expression;
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
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ClausesChangeStatusReq;
import com.maan.eway.master.req.ClausesMasterDropdownReq;
import com.maan.eway.master.req.ClausesMasterGetReq;
import com.maan.eway.master.req.ClausesMasterGetallReq;
import com.maan.eway.master.req.ClausesMasterListSaveReq;
import com.maan.eway.master.req.ClausesMasterReq;
import com.maan.eway.master.req.ClausesMasterSaveReq;
import com.maan.eway.master.req.EndorsementChangeStatusReq;
import com.maan.eway.master.req.EndorsementMasterDropdownReq;
import com.maan.eway.master.req.EndorsementMasterGetReq;
import com.maan.eway.master.req.EndorsementMasterGetallReq;
import com.maan.eway.master.req.EndorsementMasterSaveReq;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.req.WarrantyMasterReq;
import com.maan.eway.master.res.ClausesMasterRes;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterListRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.master.service.ClausesMasterService;
import com.maan.eway.master.service.EndorsementMasterService;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
@Service
public class EndorsementMasterServiceImpl implements EndorsementMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EndtTypeMasterRepository repo;

	@Autowired
	private ListItemValueRepository listrepo;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(EndorsementMasterServiceImpl.class);

	@Override
	public List<Error> validateEndorsement(EndorsementMasterSaveReq req) {
		// TODO Auto-generated method stub
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("01", "ProductId", "Please Enter ProductId"));
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
						
		/*	if (StringUtils.isBlank(req.getEndtTypeId())) {
				errorList.add(new Error("03", "EndtTypeId", "Please Enter EndtTypeId"));
			}
			*/
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
			
			if (StringUtils.isBlank(req.getCoreAppCode())) {
				errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
			}else if (req.getCoreAppCode().length() > 20){
				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}

			if (StringUtils.isBlank(req.getEndtType())) {
				errorList.add(new Error("10", "EndtType", "Please Enter EndtType"));
			}else if (req.getEndtType().length() > 300){
				errorList.add(new Error("10","EndtType", "Please Enter EndtType within 300 Characters")); 
			}

			if (StringUtils.isBlank(req.getEndtTypeDesc())) {
				errorList.add(new Error("11", "EndtTypeDesc", "Please Enter EndtTypeDesc"));
			}else if (req.getEndtTypeDesc().length() > 300){
				errorList.add(new Error("11","EndtTypeDesc", "Please Enter EndtTypeDesc within 300 Characters")); 
			}

			if (StringUtils.isBlank(req.getEndtTypeCategoryId())) {
				errorList.add(new Error("12", "EndtTypeCategoryId", "Please Enter EndtTypeCategoryId"));
			}
			
			if (StringUtils.isBlank(req.getPriority())) {
				errorList.add(new Error("13", "Priority", "Please Enter Priority"));
			}

			for(String dependantid : req.getEndtDependantIds()) {
			if (StringUtils.isBlank(dependantid)) {
				errorList.add(new Error("14", "EndtDependantId", "Please Enter EndtDependantId"));
			}
			}
			if (StringUtils.isBlank(req.getCalcTypeId())) {
				errorList.add(new Error("15", "CalcTypeId", "Please Enter CalcTypeId"));
			}
			
			if((StringUtils.isNotBlank(req.getCalcTypeId())) && req.getCalcTypeId()=="A") {
				errorList.add(new Error("17", "EndtFeePercent", "Please Enter EndtFeePercent"));
							
			}
			else if((StringUtils.isNotBlank(req.getCalcTypeId())) && req.getCalcTypeId()=="P") {
				errorList.add(new Error("17", "EndtFeePercent", "Please Enter EndtFeePercent"));
				Double a =Double.valueOf(req.getEndtFeePercent());
				if(a>100) {
					errorList.add(new Error("17", "EndtFeePercent", "Please Enter EndtFeePercent below 100"));							
				}
			}
			if (StringUtils.isBlank(req.getEndtFeeYn())) {
				errorList.add(new Error("16", "EndtFeeYn", "Please Enter EndtFeeYn"));
			}
			
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("18", "Remarks", "Please Enter Remarks"));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("18","Remarks", "Please Enter Remarks within 100 Characters")); 
			}

			if ((StringUtils.isNotBlank(req.getEndtFeePercent()))
					&& !req.getEndtFeePercent().matches("[0-9]+")){
				errorList.add(new Error("19","EndtFeePercent", "Please Enter EndtFeePercent in correct format")); 
				
			}
			
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
				errorList.add(new Error("20", "RegulatoryCode", "Please Enter RegulatoryCode"));
			}else if (req.getRegulatoryCode().length() > 10){
				errorList.add(new Error("20","RegulatoryCode", "Please Enter RegulatoryCode within 10 Characters")); 
			}
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
			
	@Override
	public SuccessRes saveEndorsement(EndorsementMasterSaveReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		EndtTypeMaster saveData = new EndtTypeMaster();
		List<EndtTypeMaster> list  = new ArrayList<EndtTypeMaster>();
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
			Integer endtTypeId = 0;
			
			ListItemValue data = listrepo.findByItemTypeAndItemCode("ENDORSEMENT_TYPE",req.getEndtTypeCategoryId());
		//	ListItemValue calc = listrepo.findByItemTypeAndItemCode("CALCULATION_TYPE",req.getCalcTypeId());
			
			if(StringUtils.isBlank(req.getEndtTypeId())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId(),req.getEndtTypeCategoryId());
				endtTypeId = totalCount+1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(endtTypeId.toString());
			}
			else {
				endtTypeId = Integer.valueOf(req.getEndtTypeId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
				//Findall
				Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
				//select
				query.select(b);
				//Orderby
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				//Where
				Predicate n1 = cb.equal(b.get("endtTypeId"),req.getEndtTypeId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				
				query.where(n1,n2,n3).orderBy(orderList);
				
				// Get Result
				TypedQuery<EndtTypeMaster> result = em.createQuery(query);
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
						EndtTypeMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0);
						if(list.size()>1) {
							EndtTypeMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(endtTypeId.toString());
			}
		
			dozerMapper.map(req, saveData);
			saveData.setEndtTypeId(endtTypeId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setProductId(req.getProductId()==null?99999: Integer.valueOf(req.getProductId()));
			saveData.setEndtType(req.getEndtType());
			saveData.setEndtTypeDesc(req.getEndtTypeDesc());
			saveData.setEndtTypeCategoryId(Integer.valueOf(req.getEndtTypeCategoryId()));
			saveData.setEndtTypeCategory(data.getItemValue());
			saveData.setPriority(Integer.valueOf(req.getPriority()));
			saveData.setCalcTypeId(req.getCalcTypeId());
		//	saveData.setCalcType(calc.getItemValue());
			saveData.setEndtTypeId(endtTypeId);
			saveData.setRegulatoryCode(req.getRegulatoryCode());
			String id = "";
			String desc = "";
			List<String> ids = req.getEndtDependantIds();
			for (int i = 0; i < ids.size(); i++) {
			ListItemValue data1 = listrepo.findByItemTypeAndItemCode("ENDT_DEPENDANT_FIELDS",ids.get(i));				
				id = id + "," + ids.get(i);
				desc=desc+"," +data1.getItemValue();
			}
			id=id.substring(1);
			desc=desc.substring(1);
			saveData.setEndtDependantIds(id);
			saveData.setEndtDependantFields(desc);
			
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
	
	
	public Integer getMasterTableCount(String companyId,  String productId, String endtTypeCategoryId)	{

		Integer data =0;
		try {
			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
			//Find all
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = effectiveDate.from(EndtTypeMaster.class);
			effectiveDate.select(cb.max(ocpm1.get("effectiveDateStart")));
			Predicate a1 = cb.equal(ocpm1.get("endtTypeId"),b.get("endtTypeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeCategoryId"),b.get("endtTypeCategoryId"));

			effectiveDate.where(a1,a2,a3,a4);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("endtTypeId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),endtTypeCategoryId);
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getEndtTypeId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}
	
	
	@Override
	public List<EndorsementMasterGetallRes> getallEndorsement(EndorsementMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<EndorsementMasterGetallRes> resList = new ArrayList<EndorsementMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));

			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
			EndorsementMasterGetallRes res1 = new EndorsementMasterGetallRes();
			
			List<EndorsementMasterListRes> endtlist = new ArrayList<EndorsementMasterListRes>();
			for(EndtTypeMaster data : list) {
				EndorsementMasterListRes res = new EndorsementMasterListRes(); 	
			String dependentid = data.getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = data.getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setEndtTypeId(data.getEndtTypeId().toString());
			res1.setEndtTypeCategoryId(data.getEndtTypeCategoryId().toString());
			res.setPriority(data.getPriority().toString());
			res1.setProductId(data.getProductId().toString());
			res.setEndtFeePercent(data.getEndtFeePercent());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setEndtType(data.getEndtType());
			res.setEndtTypeDesc(data.getEndtTypeDesc());
			res1.setEndtTypeCategory(data.getEndtTypeCategory());
			res.setStatus(data.getStatus());
			res1.setCompanyId(data.getCompanyId());
			res.setCalcTypeId(data.getCalcTypeId());			
		//	res.setCalcType(data.getCalcType());
			res.setEndtFeeYn(data.getEndtFeeYn());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			endtlist.add(res);
			}
			res1.setEndorsementMasterListRes(endtlist);
			resList.add(res1);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<EndorsementMasterGetallRes> getActiveEndorsement(EndorsementMasterGetallReq req) {
		// TODO Auto-generated method stub
		List<EndorsementMasterGetallRes> resList = new ArrayList<EndorsementMasterGetallRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));

			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			Predicate n5 = cb.equal(b.get("status"),"Y");
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
			EndorsementMasterGetallRes res1 = new EndorsementMasterGetallRes();
			
			List<EndorsementMasterListRes> endtlist = new ArrayList<EndorsementMasterListRes>();
			for(EndtTypeMaster data : list) {
				EndorsementMasterListRes res = new EndorsementMasterListRes(); 	
			String dependentid = data.getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = data.getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setEndtTypeId(data.getEndtTypeId().toString());
			res1.setEndtTypeCategoryId(data.getEndtTypeCategoryId().toString());
			res.setPriority(data.getPriority().toString());
			res1.setProductId(data.getProductId().toString());
			res.setEndtFeePercent(data.getEndtFeePercent());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setEndtType(data.getEndtType());
			res.setEndtTypeDesc(data.getEndtTypeDesc());
			res1.setEndtTypeCategory(data.getEndtTypeCategory());
			res.setStatus(data.getStatus());
			res1.setCompanyId(data.getCompanyId());
			res.setCalcTypeId(data.getCalcTypeId());			
		//	res.setCalcType(data.getCalcType());
			res.setEndtFeeYn(data.getEndtFeeYn());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			
			endtlist.add(res);
			}
			res1.setEndorsementMasterListRes(endtlist);
			resList.add(res1);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public EndorsementMasterRes getByEndorsementId(EndorsementMasterGetReq req) {
		EndorsementMasterRes res = new EndorsementMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtTypeMaster> list = new ArrayList<EndtTypeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);

			// Find All
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtTypeMaster> ocpm1 = amendId.from(EndtTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("endtTypeId"), b.get("endtTypeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("endtTypeCategoryId"), b.get("endtTypeCategoryId"));

			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("endtTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"),req.getEndtTypeCategoryId());
			Predicate n5 = cb.equal(b.get("endtTypeId"),req.getEndtTypeId());
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEndtTypeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtTypeMaster :: getEndtType ));
			
	//		res = mapper.map(list.get(0), EndorsementMasterRes.class);
			if(list!=null && list.size()>0) {
			String dependentid = list.get(0).getEndtDependantIds();
			List<String> dependentids = new ArrayList<String>(Arrays.asList(dependentid.split(",")));
			res.setEndtDependantIds(dependentids);

			String dependentfield = list.get(0).getEndtDependantFields();
			List<String> dependentfields = new ArrayList<String>(Arrays.asList(dependentfield.split(",")));
			res.setEndtDependantFields(dependentfields);
			
			res.setAmendId(list.get(0).getAmendId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCoreAppCode(list.get(0).getCoreAppCode());
			res.setEndtTypeId(list.get(0).getEndtTypeId().toString());
			res.setEndtTypeCategoryId(list.get(0).getEndtTypeCategoryId().toString());
			res.setPriority(list.get(0).getPriority().toString());
			res.setProductId(list.get(0).getProductId().toString());
			res.setEndtFeePercent(list.get(0).getEndtFeePercent());
			res.setUpdatedDate(list.get(0).getUpdatedDate());
			res.setEndtType(list.get(0).getEndtType());
			res.setEndtTypeDesc(list.get(0).getEndtTypeDesc());
			res.setEndtTypeCategory(list.get(0).getEndtTypeCategory());
			res.setStatus(list.get(0).getStatus());
			res.setCompanyId(list.get(0).getCompanyId());
			res.setCalcTypeId(list.get(0).getCalcTypeId());			
		//	res.setCalcType(list.get(0).getCalcType());
			res.setEndtFeeYn(list.get(0).getEndtFeeYn());
			res.setRemarks(list.get(0).getRemarks());
			res.setCreatedBy(list.get(0).getCreatedBy());
			res.setUpdatedBy(list.get(0).getUpdatedBy());	
			res.setRegulatoryCode(list.get(0).getRegulatoryCode());
			
			}
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
	public SuccessRes changeStatusOfEndorsement(EndorsementChangeStatusReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		EndtTypeMaster saveData = new EndtTypeMaster();
		List<EndtTypeMaster> list  = new ArrayList<EndtTypeMaster>();
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
			Integer endtTypeId = 0;

			endtTypeId = Integer.valueOf(req.getEndtTypeId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtTypeMaster> query = cb.createQuery(EndtTypeMaster.class);
			// Findall
			Root<EndtTypeMaster> b = query.from(EndtTypeMaster.class);
			// select
			query.select(b);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("endtTypeId"), req.getEndtTypeId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("endtTypeCategoryId"), req.getEndtTypeCategoryId());
			query.where(n1, n2, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<EndtTypeMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					EndtTypeMaster lastRecord = list.get(0);
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
			res.setSuccessId(endtTypeId.toString());

			dozerMapper.map(list.get(0), saveData);
			
			saveData.setEndtTypeId(endtTypeId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(list.get(0).getCompanyId());
			saveData.setProductId(req.getProductId()==null?99999:Integer.valueOf(req.getProductId()));
			repo.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			res.setResponse("Status Changed");
			res.setSuccessId(req.getEndtTypeId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getEndorsementMasterDropdown(EndorsementMasterDropdownReq req) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
}
